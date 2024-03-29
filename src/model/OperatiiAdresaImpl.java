package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.AsyncTaskListener;
import listeners.OperatiiAdresaListener;
import my.logon.screen.AsyncTaskWSCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import utils.UtilsFormatting;
import android.content.Context;
import android.widget.Toast;
import beans.BeanAdreseJudet;
import beans.BeanDateLivrareClient;
import beans.BeanLocalitate;
import enums.EnumLocalitate;
import enums.EnumOperatiiAdresa;

public class OperatiiAdresaImpl implements OperatiiAdresa, AsyncTaskListener {

	private Context context;
	private EnumOperatiiAdresa numeComanda;
	private HashMap<String, String> params;
	private OperatiiAdresaListener listener;
	private EnumLocalitate tipLocalitate;

	public OperatiiAdresaImpl(Context context) {
		this.context = context;
	}

	public void getLocalitatiJudet(HashMap<String, String> params, EnumLocalitate tipLocalitate) {

		numeComanda = EnumOperatiiAdresa.GET_LOCALITATI_JUDET;
		this.params = params;
		this.tipLocalitate = tipLocalitate;
		performOperation();

	}

	public void getAdreseJudet(HashMap<String, String> params, EnumLocalitate tipLocalitate) {

		numeComanda = EnumOperatiiAdresa.GET_ADRESE_JUDET;
		this.params = params;
		this.tipLocalitate = tipLocalitate;
		performOperation();

	}

	public void isAdresaValida(HashMap<String, String> params, EnumLocalitate tipLocalitate) {
		numeComanda = EnumOperatiiAdresa.IS_ADRESA_VALIDA;
		this.params = params;
		this.tipLocalitate = tipLocalitate;
		performOperation();

	}

	@Override
	public void getDateLivrare(HashMap<String, String> params) {
		numeComanda = EnumOperatiiAdresa.GET_DATE_LIVRARE;
		this.params = params;
		performOperation();

	}

	@Override
	public void getAdreseLivrareClient(HashMap<String, String> params) {
		numeComanda = EnumOperatiiAdresa.GET_ADRESE_LIVR_CLIENT;
		this.params = params;
		performOperation();

	}

	@Override
	public void getLocalitatiLivrareRapida() {
		numeComanda = EnumOperatiiAdresa.GET_LOCALITATI_LIVRARE_RAPIDA;
		performOperation();

	}

	public void getDateLivrareClient(HashMap<String, String> params) {
		this.params = params;
		numeComanda = EnumOperatiiAdresa.GET_DATE_LIVRARE_CLIENT;
		performOperation();

	}	
	
	public void getFilialaLivrareMathaus(HashMap<String, String> params) {
		this.params = params;
		numeComanda = EnumOperatiiAdresa.GET_FILIALA_MATHAUS;
		performOperation();

	}
	
	public void getAdresaFiliala(HashMap<String, String> params) {
		this.params = params;
		numeComanda = EnumOperatiiAdresa.GET_ADRESA_FILIALA;
		performOperation();

	}
	
	public List<String> deserializeListLocalitati(Object resultList) {

		List<String> listLocalitati = new ArrayList<String>();

		try {
			Object json = new JSONTokener((String) resultList).nextValue();

			if (json instanceof JSONArray) {
				JSONArray jsonObject = new JSONArray((String) resultList);

				for (int i = 0; i < jsonObject.length(); i++) {
					listLocalitati.add(jsonObject.get(i).toString());
				}
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return listLocalitati;
	}

	public BeanAdreseJudet deserializeListAdrese(Object resultList) {

		BeanAdreseJudet adreseJudet = new BeanAdreseJudet();
		List<BeanLocalitate> listLocalitati = new ArrayList<BeanLocalitate>();
		List<String> listStrazi = new ArrayList<String>();

		try {
			JSONObject jsonObject = new JSONObject((String) resultList);
			JSONArray jsonArrayLoc = new JSONArray(jsonObject.getString("listLocalitati"));

			for (int i = 0; i < jsonArrayLoc.length(); i++) {
				JSONObject articolObject = jsonArrayLoc.getJSONObject(i);

				BeanLocalitate loc = new BeanLocalitate();
				loc.setLocalitate(UtilsFormatting.flattenToAscii(articolObject.getString("localitate")));
				loc.setOras(Boolean.parseBoolean(articolObject.getString("isOras")));
				loc.setRazaKm(Integer.parseInt(articolObject.getString("razaKm")));
				loc.setCoordonate(articolObject.getString("coordonate"));

				listLocalitati.add(loc);
			}

			JSONArray jsonArrayStrazi = new JSONArray(jsonObject.getString("listStrazi"));

			for (int i = 0; i < jsonArrayStrazi.length(); i++) {
				listStrazi.add(UtilsFormatting.flattenToAscii(jsonArrayStrazi.getString(i).toString()));
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		adreseJudet.setListLocalitati(listLocalitati);
		adreseJudet.setListStrazi(listStrazi);

		return adreseJudet;
	}

	public BeanDateLivrareClient deserializeDateLivrareClient(String result) {
		BeanDateLivrareClient dateLivrare = new BeanDateLivrareClient();

		try {

			JSONObject jsonObject = new JSONObject((String) result);

			if (jsonObject instanceof JSONObject) {

				dateLivrare.setCodJudet(jsonObject.getString("codJudet"));
				dateLivrare.setLocalitate(jsonObject.getString("localitate"));
				dateLivrare.setStrada(jsonObject.getString("strada"));
				dateLivrare.setNrStrada(jsonObject.getString("nrStrada"));
				dateLivrare.setNumePersContact(jsonObject.getString("numePersContact"));
				dateLivrare.setTelPersContact(jsonObject.getString("telPersContact"));
				dateLivrare.setTermenPlata(jsonObject.getString("termenPlata"));

			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}
		return dateLivrare;
	}	
	
	
	public void setOperatiiAdresaListener(OperatiiAdresaListener listener) {
		this.listener = listener;
	}

	private void performOperation() {
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNumeComanda(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void onTaskComplete(String methodName, Object result) {

		if (listener != null) {
			listener.operatiiAdresaComplete(numeComanda, result, tipLocalitate);
		}

	}

}
