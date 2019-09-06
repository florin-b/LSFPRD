package my.logon.screen;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import listeners.OperatiiClientPierderiListener;
import listeners.OperatiiPierdVanzListener;
import listeners.OperatiiPierderiListener;
import listeners.PierdDepartListener;
import listeners.PierdTotalListener;
import model.OperatiiPierdereVanz;
import model.UserInfo;
import utils.UtilsUser;
import adapters.PierdereDepartAdapter;
import adapters.PierdereNivel1Adapter;
import adapters.PierdereTipClientAdapter;
import adapters.PierdereTotalAdapter;
import adapters.PierdereVanzariAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import beans.PierdereDepart;
import beans.PierdereNivel1;
import beans.PierdereTipClient;
import beans.PierdereTotal;
import beans.PierderiVanzariAV;
import enums.EnumOperatiiPierdereVanz;
import filters.Nivel1PierderiFilter;
import filters.TipClientPierderiFilter;

public class PierderiVanzari extends Activity implements OperatiiPierdVanzListener, OperatiiPierderiListener, OperatiiClientPierderiListener,
		PierdDepartListener, PierdTotalListener {

	private ActionBar actionBar;
	private NumberFormat nf;
	private OperatiiPierdereVanz opVanzari;
	private ListView listPierdereGen;
	private PierderiVanzariAV pierderiVanzariAV;
	private String ulDV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.pierderi_vanzari);

		actionBar = getActionBar();
		actionBar.setTitle("Pierderi vanzari");
		actionBar.setDisplayHomeAsUpEnabled(true);

		listPierdereGen = (ListView) findViewById(R.id.listPierdereGen);

		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(2);

		opVanzari = new OperatiiPierdereVanz(PierderiVanzari.this);
		opVanzari.setOperatiiPierdListener(PierderiVanzari.this);

		if (UserInfo.getInstance().getTipUser().equals("AV"))
			getPierderiVanzariData(UserInfo.getInstance().getCod(), UserInfo.getInstance().getUnitLog(), UserInfo.getInstance().getCodDepart());
		else if (UserInfo.getInstance().getTipUser().equals("SD"))
			getPierderiVanzariDepart(UserInfo.getInstance().getUnitLog(), UserInfo.getInstance().getCodDepart());
		else if (UtilsUser.isDV())
			getPierderiVanzariTotal();

	}

	private void getPierderiVanzariData(String codAgent, String ul, String codDepart) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codAgent", codAgent);
		params.put("ul", ul);
		params.put("codDepart", codDepart);
		opVanzari.getPierdereVanzAg(params);
	}

	private void getPierderiVanzariDepart(String unitLog, String depart) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("ul", unitLog);
		params.put("codDepart", depart);
		opVanzari.getPierdereVanzDep(params);
	}

	private void getPierderiVanzariTotal() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codDepart", UserInfo.getInstance().getCodDepart());
		opVanzari.getPierdereVanzTotal(params);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	private void CreateMenu(Menu menu) {

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			returnToMainMenu();
			return true;

		}
		return false;
	}

	private void afisPierderiVanzari(String result) {

		((LinearLayout) findViewById(R.id.headerSalAgenti)).setVisibility(View.VISIBLE);
		pierderiVanzariAV = opVanzari.deserializePierdereVanz(result);

		PierdereVanzariAdapter adapter = new PierdereVanzariAdapter(pierderiVanzariAV.getPierderiHeader(), this);
		adapter.setPierderiVanzariListener(this);
		listPierdereGen.setVisibility(View.VISIBLE);
		listPierdereGen.setAdapter(adapter);

		listPierdereGen.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

	}

	private void afisPierderiVanzariDepart(String result) {

		((LinearLayout) findViewById(R.id.layoutPierdereDepart)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutDateDepart)).setVisibility(View.VISIBLE);

		ListView listViewDepart = (ListView) findViewById(R.id.listPierdereDepart);

		List<PierdereDepart> listPierderiDepart = opVanzari.deserializePierdereDepart(result);

		PierdereDepartAdapter departAdapter = new PierdereDepartAdapter(listPierderiDepart, this);
		departAdapter.setPierderiDepartListener(this);
		listViewDepart.setAdapter(departAdapter);
		
		listViewDepart.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

	}

	public void afisPierderiVanzariTotal(String result) {

		((LinearLayout) findViewById(R.id.layoutDateTotal)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutPierdereTotal)).setVisibility(View.VISIBLE);
		ListView listViewTotal = (ListView) findViewById(R.id.listPierdereTotal);

		List<PierdereTotal> listPierderiTotal = opVanzari.deserializePierdereTotal(result);
		PierdereTotalAdapter totalAdapter = new PierdereTotalAdapter(listPierderiTotal, this);
		totalAdapter.setPierderiTotalListener(this);
		listViewTotal.setAdapter(totalAdapter);

		listViewTotal.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

	}

	@Override
	public void onBackPressed() {
		returnToMainMenu();
		return;
	}

	private void returnToMainMenu() {
		UserInfo.getInstance().setParentScreen("");
		Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);
		startActivity(nextScreen);
		finish();
	}

	@Override
	public void operationPierduteComplete(EnumOperatiiPierdereVanz methodName, Object result) {

		switch (methodName) {
		case GET_VANZ_PIERD_AG:
			afisPierderiVanzari((String) result);
			break;
		case GET_VANZ_PIERD_DEP:
			afisPierderiVanzariDepart((String) result);
			break;
		case GET_VANZ_PIERD_TOTAL:
			afisPierderiVanzariTotal((String) result);
		default:
			break;
		}

	}

	@Override
	public void tipClientSelected(String codTipClient, String numeTipClient) {

		((TextView) findViewById(R.id.textTipClientSelectat)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.headerPierdTipClient)).setVisibility(View.VISIBLE);

		((TextView) findViewById(R.id.textNumeClientSelectat)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutPierdNivel1)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.listNivel1)).setVisibility(View.GONE);

		((TextView) findViewById(R.id.textTipClientSelectat)).setText("Detalii " + numeTipClient.toLowerCase());
		ListView listTipClient = (ListView) findViewById(R.id.listTipClient);

		List<PierdereTipClient> lPierderi = new TipClientPierderiFilter().getPierderiTipClient(pierderiVanzariAV.getListPierderiTipCl(), codTipClient);
		PierdereTipClientAdapter tipAdapter = new PierdereTipClientAdapter(lPierderi, this);
		tipAdapter.setPierderiVanzariListener(this);
		listTipClient.setAdapter(tipAdapter);

		listTipClient.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		listTipClient.setVisibility(View.VISIBLE);

	}

	@Override
	public void clientSelected(String numeClient) {
		((TextView) findViewById(R.id.textNumeClientSelectat)).setText("Detalii " + numeClient);
		((TextView) findViewById(R.id.textNumeClientSelectat)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutPierdNivel1)).setVisibility(View.VISIBLE);

		ListView listNivel1 = (ListView) findViewById(R.id.listNivel1);
		listNivel1.setVisibility(View.VISIBLE);
		List<PierdereNivel1> lPierderi = new Nivel1PierderiFilter().getPierderiNivel1(pierderiVanzariAV.getListPierderiNivel1(), numeClient);
		PierdereNivel1Adapter nivel1Adapter = new PierdereNivel1Adapter(lPierderi, this);
		listNivel1.setAdapter(nivel1Adapter);

		listNivel1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

	}

	@Override
	public void agentDepartSelected(String codAgent, String numeAgent) {
		((TextView) findViewById(R.id.textAgentSelectat)).setText(numeAgent);
		((TextView) findViewById(R.id.textAgentSelectat)).setVisibility(View.VISIBLE);

		((TextView) findViewById(R.id.textNumeClientSelectat)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutPierdNivel1)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.listNivel1)).setVisibility(View.GONE);

		((TextView) findViewById(R.id.textTipClientSelectat)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.headerPierdTipClient)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.listTipClient)).setVisibility(View.GONE);

		if (UtilsUser.isDV())
			getPierderiVanzariData(codAgent, ulDV, UserInfo.getInstance().getCodDepart());
		else
			getPierderiVanzariData(codAgent, UserInfo.getInstance().getUnitLog(), UserInfo.getInstance().getCodDepart());

	}

	@Override
	public void filialaSelected(String unitLog) {
		ulDV = unitLog;

		((TextView) findViewById(R.id.textUlSelect)).setText(unitLog);
		((TextView) findViewById(R.id.textUlSelect)).setVisibility(View.VISIBLE);

		((LinearLayout) findViewById(R.id.headerSalAgenti)).setVisibility(View.GONE);
		listPierdereGen.setVisibility(View.GONE);
		((TextView) findViewById(R.id.textAgentSelectat)).setVisibility(View.GONE);

		((TextView) findViewById(R.id.textTipClientSelectat)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.headerPierdTipClient)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.listTipClient)).setVisibility(View.GONE);

		((TextView) findViewById(R.id.textNumeClientSelectat)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutPierdNivel1)).setVisibility(View.GONE);
		((ListView) findViewById(R.id.listNivel1)).setVisibility(View.GONE);

		getPierderiVanzariDepart(unitLog, UserInfo.getInstance().getCodDepart());

	}

}
