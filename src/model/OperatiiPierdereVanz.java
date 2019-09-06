package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.AsyncTaskListener;
import listeners.OperatiiPierdVanzListener;
import my.logon.screen.AsyncTaskWSCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.widget.Toast;
import beans.PierdereDepart;
import beans.PierdereNivel1;
import beans.PierdereTipClient;
import beans.PierdereTotal;
import beans.PierdereVanz;
import beans.PierderiVanzariAV;
import enums.EnumOperatiiPierdereVanz;

public class OperatiiPierdereVanz implements AsyncTaskListener {

	private EnumOperatiiPierdereVanz numeComanda;
	private Context context;
	private OperatiiPierdVanzListener opPierdListener;

	public OperatiiPierdereVanz(Context context) {
		this.context = context;
	}

	public void getPierdereVanzAg(HashMap<String, String> params) {
		numeComanda = EnumOperatiiPierdereVanz.GET_VANZ_PIERD_AG;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNume(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();

	}

	public void getPierdereVanzDep(HashMap<String, String> params) {
		numeComanda = EnumOperatiiPierdereVanz.GET_VANZ_PIERD_DEP;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNume(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();

	}

	public void getPierdereVanzTotal(HashMap<String, String> params) {
		numeComanda = EnumOperatiiPierdereVanz.GET_VANZ_PIERD_TOTAL;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNume(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();

	}

	public PierderiVanzariAV deserializePierdereVanz(String result) {

		PierderiVanzariAV pierderiAV = new PierderiVanzariAV();

		List<PierdereVanz> listPierderi = new ArrayList<PierdereVanz>();
		List<PierdereTipClient> listTip = new ArrayList<PierdereTipClient>();
		List<PierdereNivel1> listNivel1 = new ArrayList<PierdereNivel1>();

		try {

			JSONObject jsonObject = new JSONObject((String) result);

			JSONArray jsonArrayHeader = new JSONArray(jsonObject.getString("pierderiAvHeader"));

			for (int i = 0; i < jsonArrayHeader.length(); i++) {

				JSONObject jsonObj = jsonArrayHeader.getJSONObject(i);

				PierdereVanz p = new PierdereVanz();
				p.setCodTipClient(jsonObj.getString("codTipClient"));
				p.setNumeTipClient(jsonObj.getString("numeTipClient"));
				p.setNrClientiIstoric(Integer.parseInt(jsonObj.getString("nrClientiIstoric")));
				p.setNrClientiCurent(Integer.parseInt(jsonObj.getString("nrClientiPrezent")));
				p.setNrClientiRest(Integer.parseInt(jsonObj.getString("nrClientiRest")));
				listPierderi.add(p);

			}

			JSONArray jsonArrayTipClient = new JSONArray(jsonObject.getString("pierderiTipClient"));

			for (int i = 0; i < jsonArrayTipClient.length(); i++) {

				JSONObject jsonObj = jsonArrayTipClient.getJSONObject(i);

				PierdereTipClient pierdereTip = new PierdereTipClient();
				pierdereTip.setCodTipClient(jsonObj.getString("codTipClient"));
				pierdereTip.setNumeClient(jsonObj.getString("numeClient"));
				pierdereTip.setVenitLC(Double.valueOf(jsonObj.getString("venitLC")));
				pierdereTip.setVenitLC1(Double.valueOf(jsonObj.getString("venitLC1")));
				pierdereTip.setVenitLC2(Double.valueOf(jsonObj.getString("venitLC2")));
				listTip.add(pierdereTip);

			}

			JSONArray jsonArrayNivel1 = new JSONArray(jsonObject.getString("pierderiNivel1"));

			for (int i = 0; i < jsonArrayNivel1.length(); i++) {

				JSONObject jsonObj = jsonArrayNivel1.getJSONObject(i);
				PierdereNivel1 pNivel1 = new PierdereNivel1();

				pNivel1.setNumeClient(jsonObj.getString("numeClient"));
				pNivel1.setNumeNivel1(jsonObj.getString("numeNivel1"));
				pNivel1.setVenitLC(Double.valueOf(jsonObj.getString("venitLC")));
				pNivel1.setVenitLC1(Double.valueOf(jsonObj.getString("venitLC1")));
				pNivel1.setVenitLC2(Double.valueOf(jsonObj.getString("venitLC2")));
				listNivel1.add(pNivel1);

			}

		} catch (Exception ex) {
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
		}

		pierderiAV.setPierderiHeader(listPierderi);
		pierderiAV.setListPierderiTipCl(listTip);
		pierderiAV.setListPierderiNivel1(listNivel1);

		return pierderiAV;
	}

	public List<PierdereDepart> deserializePierdereDepart(String result) {

		List<PierdereDepart> listPierderi = new ArrayList<PierdereDepart>();

		try {
			Object json = new JSONTokener(result).nextValue();

			if (json instanceof JSONArray) {

				JSONArray jsonArray = new JSONArray(result);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					PierdereDepart pDepart = new PierdereDepart();
					pDepart.setCodAgent(object.getString("codAgent"));
					pDepart.setNumeAgent(object.getString("numeAgent"));
					pDepart.setNrClientiIstoric(Integer.valueOf(object.getString("nrClientiIstoric")));
					pDepart.setNrClientiCurent(Integer.valueOf(object.getString("nrClientiPrezent")));
					pDepart.setNrClientiRest(Integer.valueOf(object.getString("nrClientiRest")));
					listPierderi.add(pDepart);

				}
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return listPierderi;
	}

	
	public List<PierdereTotal> deserializePierdereTotal(String result) {

		List<PierdereTotal> listPierderi = new ArrayList<PierdereTotal>();

		try {
			Object json = new JSONTokener(result).nextValue();

			if (json instanceof JSONArray) {

				JSONArray jsonArray = new JSONArray(result);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					PierdereTotal pDepart = new PierdereTotal();
					pDepart.setUl(object.getString("ul"));
					pDepart.setNrClientiIstoric(Integer.valueOf(object.getString("nrClientiIstoric")));
					pDepart.setNrClientiCurent(Integer.valueOf(object.getString("nrClientiPrezent")));
					pDepart.setNrClientiRest(Integer.valueOf(object.getString("nrClientiRest")));
					listPierderi.add(pDepart);

				}
			}

		} catch (JSONException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}

		return listPierderi;
	}	
	
	
	public void setOperatiiPierdListener(OperatiiPierdVanzListener listener) {
		this.opPierdListener = listener;
	}

	@Override
	public void onTaskComplete(String methodName, Object result) {
		if (opPierdListener != null) {
			opPierdListener.operationPierduteComplete(numeComanda, result);
		}

	}

}
