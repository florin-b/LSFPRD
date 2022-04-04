package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import listeners.AsyncTaskListener;
import listeners.OperatiiReturListener;
import my.logon.screen.AsyncTaskWSCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.widget.Toast;
import beans.BeanAdresaLivrare;
import beans.BeanArticolRetur;

import beans.BeanComandaRetur;
import beans.BeanComandaReturAfis;
import beans.BeanDocumentRetur;
import beans.BeanPersoanaContact;
import beans.PozaArticol;
import enums.EnumRetur;

public class OperatiiReturMarfa implements AsyncTaskListener {

	private OperatiiReturListener opReturListener;
	private EnumRetur numeComanda;
	private Context context;
	private List<BeanAdresaLivrare> listAdrese;
	private List<BeanPersoanaContact> listPersoane;

	public OperatiiReturMarfa(Context context) {
		this.context = context;
	}

	public void getDocumenteClient(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_LISTA_DOCUMENTE;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void getArticoleDocument(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_ARTICOLE_DOCUMENT;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void saveComandaRetur(HashMap<String, String> params) {
		numeComanda = EnumRetur.SAVE_COMANDA_RETUR;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();

	}

	public void getListaComenziSalvate(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_COMENZI_SALVATE;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void getArticoleComandaSalvata(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_ARTICOLE_COMANDA_SALVATA;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void getArticoleComandaSalvataSap(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_ARTICOLE_COMANDA_SAP;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}
	
	public void getStocReturAvansat(HashMap<String, String> params) {
		numeComanda = EnumRetur.GET_STOC_RETUR_AVANSAT;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void opereazaComanda(HashMap<String, String> params) {
		numeComanda = EnumRetur.OPEREAZA_COMANDA;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void setOperatiiReturListener(OperatiiReturListener listener) {
		this.opReturListener = listener;
	}

	public List<BeanDocumentRetur> deserializeListDocumente(Object objList) {
		BeanDocumentRetur docRetur = null;
		List<BeanDocumentRetur> listDocumente = new ArrayList<BeanDocumentRetur>();

		BeanAdresaLivrare adresa = null;
		List<BeanAdresaLivrare> listAdrese = new ArrayList<BeanAdresaLivrare>();

		BeanPersoanaContact persoana = null;
		List<BeanPersoanaContact> listPersoane = new ArrayList<BeanPersoanaContact>();

		try {

			JSONObject object = new JSONObject((String) objList);
			JSONArray jsonDocumente = new JSONArray(object.get("listaDocumente").toString());
			JSONArray jsonAdrese = new JSONArray(object.get("adreseLivrare").toString());
			JSONArray jsonPersoana = new JSONArray(object.get("persoaneContact").toString());

			if (jsonDocumente instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(object.get("listaDocumente").toString());

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					docRetur = new BeanDocumentRetur();
					docRetur.setNumar(jsonObj.getString("numar"));
					docRetur.setData(jsonObj.getString("data"));
					docRetur.setTipTransport(jsonObj.getString("tipTransport"));
					docRetur.setDataLivrare(jsonObj.getString("dataLivrare"));
					
					
					JSONObject objectExtra = new JSONObject(jsonObj.getString("extraDate"));
					
					List<BeanAdresaLivrare> listAdreseDoc = new ArrayList<BeanAdresaLivrare>();
					BeanAdresaLivrare adresaDoc = new BeanAdresaLivrare();
					adresaDoc.setCodAdresa(objectExtra.getString("codAdresa"));
					adresaDoc.setCodJudet(objectExtra.getString("codJudet"));
					adresaDoc.setOras(objectExtra.getString("localitate"));
					adresaDoc.setStrada(objectExtra.getString("strada"));
					adresaDoc.setNrStrada("");
					listAdreseDoc.add(adresaDoc);
					docRetur.setListAdrese(listAdreseDoc);
					
					List<BeanPersoanaContact> listPersoaneDoc = new ArrayList<BeanPersoanaContact>();
					BeanPersoanaContact persoanaDoc = new BeanPersoanaContact();
					persoanaDoc.setNume(objectExtra.getString("numeContact"));
					persoanaDoc.setTelefon(objectExtra.getString("telContact"));
					listPersoaneDoc.add(persoanaDoc);
					docRetur.setListPersoane(listPersoaneDoc);
					
					
					listDocumente.add(docRetur);

				}

			}

			if (jsonAdrese instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(object.get("adreseLivrare").toString());

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					adresa = new BeanAdresaLivrare();
					adresa.setCodAdresa(jsonObj.getString("codAdresa"));
					adresa.setCodJudet(jsonObj.getString("codJudet"));
					adresa.setOras(jsonObj.getString("oras"));
					adresa.setStrada(jsonObj.getString("strada"));
					adresa.setNrStrada(jsonObj.getString("nrStrada"));
					listAdrese.add(adresa);
				}
			}

			if (jsonPersoana instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray(object.get("persoaneContact").toString());

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					persoana = new BeanPersoanaContact();
					persoana.setNume(jsonObj.getString("nume"));
					persoana.setTelefon(jsonObj.getString("telefon"));
					listPersoane.add(persoana);

				}

			}

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}

		this.listAdrese = listAdrese;
		this.listPersoane = listPersoane;

		return listDocumente;
	}

	public List<BeanAdresaLivrare> getListAdrese() {
		return listAdrese;
	}

	public List<BeanPersoanaContact> getListPersoane() {
		return listPersoane;
	}

	public List<BeanArticolRetur> deserializeListArticole(Object objList) {
		BeanArticolRetur artRetur = null;
		List<BeanArticolRetur> listArticole = new ArrayList<BeanArticolRetur>();
		try {
			Object json = new JSONTokener((String) objList).nextValue();

			if (json instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray((String) objList);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					artRetur = new BeanArticolRetur();
					artRetur.setNume(jsonObj.getString("nume"));
					artRetur.setCod(jsonObj.getString("cod"));
					artRetur.setCantitate(Double.valueOf(jsonObj.getString("cantitate")));
					artRetur.setUm(jsonObj.getString("um"));
					artRetur.setCantitateRetur(0);

					if (jsonObj.has("pretUnitPalet") && jsonObj.getString("pretUnitPalet") != "null")
						artRetur.setPretUnitPalet(Double.valueOf(jsonObj.getString("pretUnitPalet")));
					else
						artRetur.setPretUnitPalet(0);

					listArticole.add(artRetur);
				}
			}

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}
		return listArticole;
	}

	public String serializeListArticole(List<BeanArticolRetur> listArticole) {

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;

		Iterator<BeanArticolRetur> iterator = listArticole.iterator();
		BeanArticolRetur articol = null;
		while (iterator.hasNext()) {
			articol = iterator.next();
			try {
				if (articol.getCantitateRetur() > 0) {
					jsonObject = new JSONObject();
					jsonObject.put("nume", articol.getNume());
					jsonObject.put("cod", articol.getCod());
					jsonObject.put("cantitateRetur", String.valueOf(articol.getCantitateRetur()));
					jsonObject.put("um", articol.getUm());
					jsonObject.put("motivRespingere", articol.getMotivRespingere());
					jsonObject.put("inlocuire", articol.isInlocuire());
					jsonObject.put("pozeArticol", serializePozeArticol(articol.getPozeArticol()));

					jsonArray.put(jsonObject);
				}
			} catch (Exception ex) {
				Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		return jsonArray.toString();
	}

	private String serializePozeArticol(List<PozaArticol> listPoze) {
		JSONArray jsonPoze = new JSONArray();

		if (listPoze == null)
			return "";

		try {

			for (PozaArticol poza : listPoze) {
				JSONObject obj = new JSONObject();
				obj.put("nume", poza.getNume());
				obj.put("strData", poza.getStrData());
				jsonPoze.put(obj);
			}

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}

		return jsonPoze.toString();
	}

	public String serializeComandaRetur(BeanComandaRetur comandaRetur) {

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("nrDocument", comandaRetur.getNrDocument());
			jsonObject.put("dataLivrare", comandaRetur.getDataLivrare());
			jsonObject.put("tipTransport", comandaRetur.getTipTransport());
			jsonObject.put("codAgent", comandaRetur.getCodAgent());
			jsonObject.put("tipAgent", comandaRetur.getTipAgent());
			jsonObject.put("motivRetur", comandaRetur.getMotivRespingere());
			jsonObject.put("numePersContact", comandaRetur.getNumePersContact());
			jsonObject.put("telPersContact", comandaRetur.getTelPersContact());
			jsonObject.put("adresaCodJudet", comandaRetur.getAdresaCodJudet());
			jsonObject.put("adresaOras", comandaRetur.getAdresaOras());
			jsonObject.put("adresaStrada", comandaRetur.getAdresaStrada());
			jsonObject.put("adresaCodAdresa", comandaRetur.getAdresaCodAdresa());
			jsonObject.put("listaArticole", comandaRetur.getListArticole());
			jsonObject.put("observatii", comandaRetur.getObservatii());
			jsonObject.put("codclient", comandaRetur.getCodClient());
			jsonObject.put("numeclient", comandaRetur.getNumeClient());
			jsonObject.put("transpBack", comandaRetur.isTransBack());

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}

		return jsonObject.toString();
	}

	public BeanComandaRetur deserializeComandaSalvata(Object dateComanda) {
		BeanComandaRetur comandaRetur = new BeanComandaRetur();

		try {
			JSONObject jsonObject = new JSONObject((String) dateComanda);

			if (jsonObject instanceof JSONObject) {

				comandaRetur.setAdresaCodJudet(jsonObject.getString("adresaCodJudet"));
				comandaRetur.setAdresaOras(jsonObject.getString("adresaOras"));
				comandaRetur.setAdresaStrada(jsonObject.getString("adresaStrada"));
				comandaRetur.setDataLivrare(jsonObject.getString("dataLivrare"));
				comandaRetur.setTipTransport(jsonObject.getString("tipTransport"));
				comandaRetur.setNumePersContact(jsonObject.getString("numePersContact"));
				comandaRetur.setTelPersContact(jsonObject.getString("telPersContact"));

				comandaRetur.setArrayListArticole(deserializeListArticole(jsonObject.getString("listaArticole")));

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return comandaRetur;
	}

	public List<BeanComandaReturAfis> deserializeComenziSalvate(Object objList) {
		BeanComandaReturAfis comanda = null;
		List<BeanComandaReturAfis> listComenzi = new ArrayList<BeanComandaReturAfis>();
		try {
			Object json = new JSONTokener((String) objList).nextValue();

			if (json instanceof JSONArray) {
				JSONArray jsonArray = new JSONArray((String) objList);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					comanda = new BeanComandaReturAfis();
					comanda.setId(jsonObj.getString("id"));
					comanda.setNrDocument(jsonObj.getString("nrDocument"));
					comanda.setNumeClient(jsonObj.getString("numeClient"));
					comanda.setDataCreare(jsonObj.getString("dataCreare"));
					comanda.setStatus(jsonObj.getString("status"));
					comanda.setNumeAgent(jsonObj.getString("numeAgent"));
					listComenzi.add(comanda);
				}
			}

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}
		return listComenzi;
	}

	public void onTaskComplete(String methodName, Object result) {
		if (opReturListener != null) {
			opReturListener.operationReturComplete(numeComanda, result);
		}

	}

}
