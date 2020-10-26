package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import listeners.AsyncTaskListener;
import listeners.OperatiiArticolListener;
import my.logon.screen.AsyncTaskWSCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import utils.UtilsGeneral;
import android.content.Context;
import android.widget.Toast;
import beans.ArticolCant;
import beans.ArticolDB;
import beans.BeanArticolSimulat;
import beans.BeanArticolStoc;
import beans.BeanGreutateArticol;
import beans.BeanParametruPretGed;
import beans.PretArticolGed;
import enums.EnumArticoleDAO;
import enums.EnumUnitMas;

public class OperatiiArticolImpl implements OperatiiArticol, AsyncTaskListener {

	private Context context;
	private OperatiiArticolListener listener;
	private EnumArticoleDAO numeComanda;
	private HashMap<String, String> params;

	public OperatiiArticolImpl(Context context) {
		this.context = context;
	}

	public void getPret(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_PRET;
		this.params = params;
		performOperation();
	}

	public void getPretGed(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_PRET_GED;
		this.params = params;
		performOperation();
	}

	public void getPretGedJson(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_PRET_GED_JSON;
		this.params = params;
		performOperation();

	}

	public void getFactorConversie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_FACTOR_CONVERSIE;
		this.params = params;
		performOperation();
	}

	public void getStocDepozit(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_STOC_DEPOZIT;
		this.params = params;
		performOperation();
	}

	public void getArticoleDistributie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_DISTRIBUTIE;
		this.params = params;
		performOperation();
	}

	public void getArticoleFurnizor(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_FURNIZOR;
		this.params = params;
		performOperation();
	}

	public void getArticoleComplementare(List<ArticolComanda> listArticole) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_COMPLEMENTARE;

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("listaArticoleComanda", serializeListArticole(listArticole));
		this.params = params;

		performOperation();
	}

	public void getSinteticeDistributie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_SINTETICE_DISTRIBUTIE;
		this.params = params;
		performOperation();
	}

	public void getNivel1Distributie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_NIVEL1_DISTRIBUTIE;
		this.params = params;
		performOperation();
	}

	@Override
	public void getStocArticole(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_STOC_ARTICOLE;
		this.params = params;
		performOperation();
	}

	@Override
	public void getCodBare(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_COD_BARE;
		this.params = params;
		performOperation();
	}

	@Override
	public void getArticoleStatistic(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_STATISTIC;
		this.params = params;
		performOperation();

	}

	@Override
	public void getArticoleCustodie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_CUSTODIE;
		this.params = params;
		performOperation();

	}		
	
	public void getStocCustodie(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_STOC_CUSTODIE;
		this.params = params;
		performOperation();
	}

	public void getArticoleCant(HashMap<String, String> params) {
		numeComanda = EnumArticoleDAO.GET_ARTICOLE_CANT;
		this.params = params;
		performOperation();
	}	
	
	@Override
	public Object getDepartBV90(String codArticol) {
		numeComanda = EnumArticoleDAO.GET_DEP_BV90;

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codArticol", codArticol);

		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		Object obj = call.getCallResultsSync();
		return obj;
	}

	private void performOperation() {
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public ArrayList<ArticolDB> deserializeArticoleVanzare(String serializedListArticole) {
		ArticolDB articol = null;
		ArrayList<ArticolDB> listArticole = new ArrayList<ArticolDB>();

		try {
			Object json = new JSONTokener(serializedListArticole).nextValue();

			if (json instanceof JSONArray) {

				JSONArray jsonArray = new JSONArray(serializedListArticole);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject articolObject = jsonArray.getJSONObject(i);

					articol = new ArticolDB();
					articol.setCod(articolObject.getString("cod"));
					articol.setNume(articolObject.getString("nume"));
					articol.setSintetic(articolObject.getString("sintetic"));
					articol.setNivel1(articolObject.getString("nivel1"));
					articol.setUmVanz(articolObject.getString("umVanz"));
					articol.setUmVanz10(articolObject.getString("umVanz10"));
					articol.setTipAB(articolObject.getString("tipAB"));
					articol.setDepart(articolObject.getString("depart"));
					articol.setDepartAprob(articolObject.getString("departAprob"));
					articol.setUmPalet(articolObject.getString("umPalet").equals("1") ? true : false);
					articol.setStoc(articolObject.getString("stoc"));
					articol.setCategorie(articolObject.getString("categorie"));
					articol.setLungime(Double.valueOf(articolObject.getString("lungime")));
					listArticole.add(articol);

				}
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return listArticole;

	}

	public String serializeParamPretGed(BeanParametruPretGed parametru) {

		JSONObject jsonParametru = new JSONObject();

		try {
			jsonParametru.put("client", parametru.getClient());
			jsonParametru.put("articol", parametru.getArticol());
			jsonParametru.put("cantitate", parametru.getCantitate());
			jsonParametru.put("depart", parametru.getDepart());
			jsonParametru.put("um", parametru.getUm());
			jsonParametru.put("ul", parametru.getUl());
			jsonParametru.put("depoz", parametru.getDepoz());
			jsonParametru.put("codUser", parametru.getCodUser());
			jsonParametru.put("tipUser", parametru.getTipUser());
			jsonParametru.put("canalDistrib", parametru.getCanalDistrib());
			jsonParametru.put("metodaPlata", parametru.getMetodaPlata());
			jsonParametru.put("codJudet", parametru.getCodJudet());
			jsonParametru.put("localitate", parametru.getLocalitate());
			jsonParametru.put("filialaAlternativa", parametru.getFilialaAlternativa());
			jsonParametru.put("codClientParavan", parametru.getCodClientParavan());
			jsonParametru.put("filialaClp", parametru.getFilialaClp());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonParametru.toString();

	}

	private String serializeListArticole(List<ArticolComanda> listArticole) {
		String serializedResult = "";

		JSONArray myArray = new JSONArray();
		JSONObject obj = null;

		Iterator<ArticolComanda> iterator = listArticole.iterator();
		ArticolComanda articol;

		while (iterator.hasNext()) {
			articol = iterator.next();

			try {
				obj = new JSONObject();
				obj.put("cod", articol.getCodArticol());
				myArray.put(obj);
			} catch (Exception ex) {
				Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
			}

		}

		serializedResult = myArray.toString();

		return serializedResult;
	}

	public String serializeArticolePretTransport(List<ArticolComanda> listArticole) {
		String serializedResult = "";

		JSONArray myArray = new JSONArray();
		JSONObject obj = null;

		Iterator<ArticolComanda> iterator = listArticole.iterator();
		ArticolComanda articol;
		String localCodArticol = "";

		while (iterator.hasNext()) {
			articol = iterator.next();

			try {
				obj = new JSONObject();

				if (articol.getCodArticol().length() == 8)
					localCodArticol = "0000000000" + articol.getCodArticol();
				else
					localCodArticol = articol.getCodArticol();

				obj.put("cod", localCodArticol);
				obj.put("valoare", Double.valueOf(articol.getPret()));
				obj.put("promo", articol.getPromotie() <= 0 ? " " : "X");

				myArray.put(obj);
			} catch (Exception ex) {
				Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
			}

		}

		serializedResult = myArray.toString();

		return serializedResult;
	}

	@Override
	public String serializeListArtStoc(List<BeanArticolStoc> listArticole) {

		JSONArray jsonArray = new JSONArray();
		JSONObject object = null;

		Iterator<BeanArticolStoc> iterator = listArticole.iterator();

		while (iterator.hasNext()) {
			BeanArticolStoc articol = iterator.next();

			object = new JSONObject();
			try {
				object.put("cod", articol.getCod());
				object.put("depozit", articol.getDepozit());
				object.put("depart", articol.getDepart());
				object.put("unitLog", articol.getUnitLog());

				jsonArray.put(object);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}

		return jsonArray.toString();
	}

	@Override
	public String serializeListArtSim(List<BeanArticolSimulat> listArticole) {

		JSONArray jsonArray = new JSONArray();
		JSONObject object = null;

		Iterator<BeanArticolSimulat> iterator = listArticole.iterator();

		while (iterator.hasNext()) {
			BeanArticolSimulat articol = iterator.next();

			object = new JSONObject();
			try {
				object.put("cod", articol.getCod());
				object.put("depozit", articol.getDepozit());
				object.put("depart", articol.getDepart());
				object.put("unitLog", articol.getUnitLog());
				object.put("um", articol.getUm());

				jsonArray.put(object);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}

		return jsonArray.toString();
	}	
	
	
	
	public void deserializeListArtStoc(String listArticole) {
		// Object json = new JSONTokener(serializedListArticole).nextValue();

	}

	public PretArticolGed deserializePretGed(Object result) {
		PretArticolGed pretArticol = new PretArticolGed();

		try {

			JSONObject jsonObject = new JSONObject((String) result);

			if (jsonObject instanceof JSONObject) {
				pretArticol.setPret(jsonObject.getString("pret"));
				pretArticol.setUm(jsonObject.getString("um"));
				pretArticol.setFaraDiscount(jsonObject.getString("faraDiscount"));
				pretArticol.setCodArticolPromo(jsonObject.getString("codArticolPromo"));
				pretArticol.setCantitateArticolPromo(jsonObject.getString("cantitateArticolPromo"));
				pretArticol.setPretArticolPromo(jsonObject.getString("pretArticolPromo"));
				pretArticol.setUmArticolPromo(jsonObject.getString("umArticolPromo"));
				pretArticol.setPretLista(jsonObject.getString("pretLista"));
				pretArticol.setCantitate(jsonObject.getString("cantitate"));
				pretArticol.setConditiiPret(jsonObject.getString("conditiiPret"));
				pretArticol.setMultiplu(jsonObject.getString("multiplu"));
				pretArticol.setCantitateUmBaza(jsonObject.getString("cantitateUmBaza"));
				pretArticol.setUmBaza(jsonObject.getString("umBaza"));
				pretArticol.setCmp(jsonObject.getString("cmp"));
				pretArticol.setPretMediu(jsonObject.getString("pretMediu"));
				pretArticol.setAdaosMediu(jsonObject.getString("adaosMediu"));
				pretArticol.setUmPretMediu(jsonObject.getString("umPretMediu"));
				pretArticol.setCoefCorectie(Double.valueOf(jsonObject.getString("coefCorectie") != "null" ? jsonObject.getString("coefCorectie")
						: "0"));
				pretArticol.setProcTransport(Double.valueOf(jsonObject.getString("procTransport") != "null" ? jsonObject.getString("procTransport")
						: "0"));
				pretArticol.setDiscMaxAV(Double.valueOf(jsonObject.getString("discMaxAV") != "null" ? jsonObject.getString("discMaxAV") : "0"));
				pretArticol.setDiscMaxSD(Double.valueOf(jsonObject.getString("discMaxSD") != "null" ? jsonObject.getString("discMaxSD") : "0"));
				pretArticol.setDiscMaxDV(Double.valueOf(jsonObject.getString("discMaxDV") != "null" ? jsonObject.getString("discMaxDV") : "0"));
				pretArticol.setDiscMaxKA(Double.valueOf(jsonObject.getString("discMaxKA") != "null" ? jsonObject.getString("discMaxKA") : "0"));
				pretArticol.setImpachetare(jsonObject.getString("impachetare"));
				pretArticol.setIstoricPret(jsonObject.getString("istoricPret"));
				pretArticol.setValTrap(Double.valueOf(jsonObject.getString("valTrap")));
				pretArticol.setErrMsg(jsonObject.getString("errMsg"));
				pretArticol.setProcReducereCmp(Double.valueOf(jsonObject.getString("procReducereCmp")));
				pretArticol.setPretFaraTva(Double.valueOf(jsonObject.getString("pretFaraTva")));

			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return pretArticol;
	}

	public void onTaskComplete(String methodName, Object result) {
		if (listener != null) {
			listener.operationComplete(numeComanda, result);
		}
	}

	public void setListener(OperatiiArticolListener listener) {
		this.listener = listener;
	}

	public BeanGreutateArticol deserializeGreutateArticol(Object result) {

		BeanGreutateArticol greutateArticol = new BeanGreutateArticol();

		try {
			JSONObject jsonObject = new JSONObject((String) result);

			if (jsonObject instanceof JSONObject) {
				greutateArticol.setCodArticol(jsonObject.getString("codArticol"));
				greutateArticol.setGreutate(Double.valueOf(jsonObject.getString("greutate")));
				greutateArticol.setUnitMas(EnumUnitMas.valueOf(jsonObject.getString("um")));
				greutateArticol.setUnitMasCantiate(jsonObject.getString("umCantitate"));

			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return greutateArticol;
	}

	public List<BeanArticolStoc> derializeListArtStoc(String serializedResult) {

		List<BeanArticolStoc> listArticole = new ArrayList<BeanArticolStoc>();

		try {
			Object jsonObject = new JSONTokener(serializedResult).nextValue();

			if (jsonObject instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(serializedResult);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject articolObject = jsonArray.getJSONObject(i);

					BeanArticolStoc articol = new BeanArticolStoc();

					articol.setCod(articolObject.getString("cod"));

					articol.setDepozit(articolObject.getString("depozit"));
					articol.setDepart(articolObject.getString("depart"));
					articol.setUnitLog(articolObject.getString("unitLog"));
					articol.setStoc(Double.valueOf(articolObject.getString("stoc")));

					listArticole.add(articol);

				}

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return listArticole;
	}
	
	
	public ArrayList<ArticolCant> deserializeArticoleCant(String listArticoleSer) {
		ArticolCant articol = null;
		ArrayList<ArticolCant> listArticole = new ArrayList<ArticolCant>();

		try {
			Object json = new JSONTokener(listArticoleSer).nextValue();

			if (json instanceof JSONArray) {

				JSONArray jsonArray = new JSONArray(listArticoleSer);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject articolObject = jsonArray.getJSONObject(i);

					articol = new ArticolCant();
					
					articol.setCod(articolObject.getString("cod"));
					articol.setNume(articolObject.getString("denumire"));
					articol.setSintetic(articolObject.getString("sintetic"));
					articol.setDimensiuni(articolObject.getString("dimensiuni"));
					articol.setCaract(articolObject.getString("caract"));
					articol.setStoc(articolObject.getString("stoc"));
					articol.setUmVanz(articolObject.getString("um"));
					articol.setTipCant(articolObject.getString("tipCant"));
					articol.setUlStoc(articolObject.getString("ulStoc"));
					articol.setNivel1(articolObject.getString("nivel1"));
					articol.setUmVanz(articolObject.getString("umVanz"));
					articol.setUmVanz10(articolObject.getString("umVanz10"));
					articol.setDepart(articolObject.getString("depart"));
					articol.setDepartAprob(articolObject.getString("departAprob"));
					articol.setTipAB(articolObject.getString("tipAB"));
					articol.setUmPalet(articolObject.getString("umPalet").equals("1") ? true : false);
					articol.setCategorie(articolObject.getString("categorie"));
					articol.setLungime(Double.valueOf(articolObject.getString("lungime")));
					articol.setDepozit(articolObject.getString("depozit"));
					listArticole.add(articol);

				}
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return listArticole;

	}

}
