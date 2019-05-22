package my.logon.screen;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import listeners.IntervalSalarizareListener;
import listeners.OperatiiSalarizareListener;
import listeners.SituatieSalarizareListener;
import model.OperatiiSalarizare;
import model.UserInfo;
import utils.UtilsUser;
import adapters.Detalii08Adapter;
import adapters.DetaliiBazaAdapter;
import adapters.SalarizareCvsAdapter;
import adapters.SalarizareDepartAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import beans.BeanSalarizareAgent;
import beans.BeanSalarizareAgentAfis;
import beans.BeanSalarizareSD;
import beans.SalarizareDetaliiBaza;
import beans.SalarizareDetaliiInc08;
import dialogs.SelectLunaSalarizareDialog;
import dialogs.SelectSituatiiSalarizareDialog;
import enums.EnumOperatiiSalarizare;
import enums.EnumSituatieSalarizare;

public class Salarizare extends Activity implements OperatiiSalarizareListener, IntervalSalarizareListener, SituatieSalarizareListener {

	private ActionBar actionBar;
	private OperatiiSalarizare operatiiSalarizare;
	private String[] listLuni = { "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie", "Iulie", "August", "Septembrie", "Octombrie",
			"Noiembrie", "Decembrie" };

	private String lunaSelect, anSelect;
	private boolean isDetaliiAgent;

	private Button btnDetaliiSalarizare;
	private boolean isDetaliiSalarizare;

	private String numeAgentSelectat;
	private NumberFormat nf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.salarizare);

		actionBar = getActionBar();
		actionBar.setTitle("Salarizare");
		actionBar.setDisplayHomeAsUpEnabled(true);

		operatiiSalarizare = new OperatiiSalarizare(this);
		operatiiSalarizare.setListener(Salarizare.this);

		checkAccess();

		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(2);

		btnDetaliiSalarizare = (Button) findViewById(R.id.btnDetaliiSalarizare);
		setListenerBtnDetalii();

		initData();

	}

	private void checkAccess() {

		if (UtilsUser.isAgentOrSD() && UserInfo.getInstance().getIsMeniuBlocat()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Acces blocat. Folositi modulul Utilizator pentru deblocare.").setCancelable(true)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							returnToMainMenu();
						}
					});
			AlertDialog alert = builder.create();
			alert.setCancelable(false);
			alert.show();

		}

	}

	private void initData() {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.MONTH, -1);

		NumberFormat nf3 = new DecimalFormat("00");

		lunaSelect = nf3.format(c.get(Calendar.MONTH) + 1);
		anSelect = String.valueOf(c.get(Calendar.YEAR));

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	private void CreateMenu(Menu menu) {

		MenuItem mnu1 = menu.add(0, 0, 0, "Luna");
		mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		if (UserInfo.getInstance().getTipUserSap().equals("SD")) {
			MenuItem mnu2 = menu.add(0, 1, 1, "Salarizare");
			mnu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 0:
			SelectLunaSalarizareDialog intervalDialog = new SelectLunaSalarizareDialog(this);
			intervalDialog.setIntervalDialogListener(this);
			intervalDialog.showDialog();
			return true;

		case 1:
			SelectSituatiiSalarizareDialog situatieDialog = new SelectSituatiiSalarizareDialog(this);
			situatieDialog.setSituatieListener(this);
			situatieDialog.showDialog();
			return true;

		case android.R.id.home:
			returnToMainMenu();
			return true;

		}
		return false;
	}

	private void setDetaliiT1Visibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.labelDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.headerDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.headerTotalDetaliiBaza)).setVisibility(View.VISIBLE);
		} else {
			((LinearLayout) findViewById(R.id.labelDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.headerDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.headerTotalDetaliiBaza)).setVisibility(View.GONE);
		}
	}

	private void setDetaliiTCFVisibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.labelDetaliiTCF)).setVisibility(View.VISIBLE);
			((TableLayout) findViewById(R.id.table_detalii_tcf)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.labelDetaliiTCF)).setVisibility(View.GONE);
			((TableLayout) findViewById(R.id.table_detalii_tcf)).setVisibility(View.GONE);

		}
	}

	private void setDetaliiCorectieVisibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.labelDetaliiCorectii)).setVisibility(View.VISIBLE);
			((TableLayout) findViewById(R.id.table_detalii_corectii)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.labelDetaliiCorectii)).setVisibility(View.GONE);
			((TableLayout) findViewById(R.id.table_detalii_corectii)).setVisibility(View.GONE);

		}
	}

	private void setDetalii08Visibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.labelDetaliiInc08)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layout_detalii_08)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutListDetalii0_8)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layout_total_detalii_08)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.labelDetaliiInc08)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layout_detalii_08)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutListDetalii0_8)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layout_total_detalii_08)).setVisibility(View.GONE);

		}
	}

	private void setDetaliiCSVVisibility(boolean isVisible) {
		if (isVisible) {
			((TextView) findViewById(R.id.labelDetaliiCVS)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layout_detalii_cvs)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutListDetaliiCVS)).setVisibility(View.VISIBLE);

		} else {
			((TextView) findViewById(R.id.labelDetaliiCVS)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layout_detalii_cvs)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutListDetaliiCVS)).setVisibility(View.GONE);
		}
	}

	private void setHeaderCVSVisibility(boolean isVisible) {
		if (isVisible) {
			((TextView) findViewById(R.id.labelHeaderCVS)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.venitCVS)).setVisibility(View.VISIBLE);
		} else {
			((TextView) findViewById(R.id.labelHeaderCVS)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.venitCVS)).setVisibility(View.GONE);
		}
	}

	private void setListenerBtnDetalii() {
		btnDetaliiSalarizare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isDetaliiSalarizare = !isDetaliiSalarizare;
				setDetaliiSalarizareVisibility(isDetaliiSalarizare);

			}
		});
	}

	private void setDetaliiSalarizareVisibility(boolean isVisible) {

		setDetaliiT1Visibility(isVisible);
		setDetaliiTCFVisibility(isVisible);
		setDetaliiCorectieVisibility(isVisible);
		setDetalii08Visibility(isVisible);

		if (UserInfo.getInstance().getTipUserSap().equals("SD"))
			setDetaliiCSVVisibility(isVisible);

	}

	private void setSalAgentiVisibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.layoutListAgenti)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.headerSalAgenti)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.layoutListAgenti)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.headerSalAgenti)).setVisibility(View.GONE);

		}
	}

	private void setDateGeneraleVisibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.layoutDateGenerale)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutDetaliiSalarizare)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.layoutDateGenerale)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutDetaliiSalarizare)).setVisibility(View.GONE);
		}
	}

	private void setDetaliiVisibility(boolean isVisible) {

		if (isVisible) {
			((LinearLayout) findViewById(R.id.labelDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.headerDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutDetaliiBaza)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.labelDetaliiTCF)).setVisibility(View.VISIBLE);
			((TableLayout) findViewById(R.id.table_detalii_tcf)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.labelDetaliiCorectii)).setVisibility(View.VISIBLE);
			((TableLayout) findViewById(R.id.table_detalii_corectii)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.labelDetaliiInc08)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layout_detalii_08)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutListDetalii0_8)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layout_total_detalii_08)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.headerTotalDetaliiBaza)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.labelDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.headerDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutDetaliiBaza)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.labelDetaliiTCF)).setVisibility(View.GONE);
			((TableLayout) findViewById(R.id.table_detalii_tcf)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.labelDetaliiCorectii)).setVisibility(View.GONE);
			((TableLayout) findViewById(R.id.table_detalii_corectii)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.labelDetaliiInc08)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layout_detalii_08)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutListDetalii0_8)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layout_total_detalii_08)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.headerTotalDetaliiBaza)).setVisibility(View.GONE);

		}

	}

	private void getDetaliiAgent(String codAgent, String luna, String an) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codAgent", codAgent);
		params.put("ul", UserInfo.getInstance().getUnitLog());
		params.put("divizie", UserInfo.getInstance().getCodDepart());
		params.put("an", an);
		params.put("luna", luna);

		operatiiSalarizare.getSalarizareAgent(params);

	}

	private void getSalarizareAgent(String luna, String an) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codAgent", UserInfo.getInstance().getCod());
		params.put("ul", UserInfo.getInstance().getUnitLog());
		params.put("divizie", UserInfo.getInstance().getCodDepart());
		params.put("an", an);
		params.put("luna", luna);

		operatiiSalarizare.getSalarizareAgent(params);

	}

	private void getSalarizareDepartament() {
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("ul", UserInfo.getInstance().getUnitLog());
		params.put("divizie", UserInfo.getInstance().getCodDepart());
		params.put("an", anSelect);
		params.put("luna", lunaSelect);

		operatiiSalarizare.getSalarizareDepartament(params);
	}

	private void getSalarizareSD() {
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("codAgent", UserInfo.getInstance().getCod());
		params.put("ul", UserInfo.getInstance().getUnitLog());
		params.put("divizie", UserInfo.getInstance().getCodDepart());
		params.put("an", anSelect);
		params.put("luna", lunaSelect);

		operatiiSalarizare.getSalarizareSD(params);
	}

	private void afisSalarizareAgent(String result, BeanSalarizareAgent salarizareAgent) {

		if (isDetaliiAgent) {

			((TextView) findViewById(R.id.textAgentSelectat)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.textAgentSelectat)).setText(numeAgentSelectat);

			setDateGeneraleVisibility(false);
			setDetaliiVisibility(true);
		} else {

			((TextView) findViewById(R.id.textAgentSelectat)).setVisibility(View.GONE);

			setSalAgentiVisibility(false);
			setDateGeneraleVisibility(true);
			setDetaliiVisibility(false);
		}

		setHeaderCVSVisibility(false);
		isDetaliiSalarizare = false;

		((TextView) findViewById(R.id.labelDateGenerale)).setText(listLuni[Integer.parseInt(lunaSelect) - 1] + "  " + anSelect);

		BeanSalarizareAgent salarizare;

		if (salarizareAgent == null)
			salarizare = operatiiSalarizare.deserializeSalarizareAgent(result);
		else
			salarizare = salarizareAgent;

		((TextView) findViewById(R.id.venitT1)).setText(nf.format(salarizare.getDatePrincipale().getVenitMJ_T1()));
		((TextView) findViewById(R.id.venitTCF)).setText(nf.format(salarizare.getDatePrincipale().getVenitTCF()));
		((TextView) findViewById(R.id.corectieInc)).setText(nf.format(salarizare.getDatePrincipale().getCorectieIncasare()));
		((TextView) findViewById(R.id.venitFinal)).setText(nf.format(salarizare.getDatePrincipale().getVenitFinal()));

		DetaliiBazaAdapter detaliiBazaAdapter = new DetaliiBazaAdapter(salarizare.getDetaliiBaza(), this);

		ListView listDetaliiBaza = (ListView) findViewById(R.id.listDetaliiBaza);
		listDetaliiBaza.setAdapter(detaliiBazaAdapter);

		listDetaliiBaza.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		((TextView) findViewById(R.id.textTotalVenitBaza)).setText(nf.format(getTotalDetaliiBaza(salarizare)));

		((TextView) findViewById(R.id.venitBazaTCF)).setText(nf.format(salarizare.getDetaliiTCF().getVenitBaza()));
		((TextView) findViewById(R.id.clientiAnterior)).setText(nf.format(Double.valueOf(salarizare.getDetaliiTCF().getClientiAnterior())));
		((TextView) findViewById(R.id.targetTCF)).setText(nf.format(Double.valueOf(salarizare.getDetaliiTCF().getTarget())));
		((TextView) findViewById(R.id.clientiCurent)).setText(nf.format(Double.valueOf(salarizare.getDetaliiTCF().getClientiCurent())));
		((TextView) findViewById(R.id.coeficientTCF)).setText(nf.format(salarizare.getDetaliiTCF().getCoeficient()));
		((TextView) findViewById(R.id.venitTotalTCF)).setText(nf.format(salarizare.getDetaliiTCF().getVenitTcf()));

		((TextView) findViewById(R.id.venitBazaCorectii)).setText(nf.format(salarizare.getDetaliiCorectie().getVenitBaza()));
		((TextView) findViewById(R.id.venitIncasari08)).setText(nf.format(salarizare.getDetaliiCorectie().getIncasari08()));
		((TextView) findViewById(R.id.valoareMalus)).setText(nf.format(salarizare.getDetaliiCorectie().getMalus()));
		((TextView) findViewById(R.id.venitCorectieIncasari)).setText(nf.format(salarizare.getDetaliiCorectie().getVenitCorectat()));

		((TextView) findViewById(R.id.text_total_val_08)).setText(nf.format(getTotalInc08(salarizare)));
		((TextView) findViewById(R.id.text_total_cor_08)).setText(nf.format(getTotalCor08(salarizare)));

		Detalii08Adapter detalii08Adapter = new Detalii08Adapter(salarizare.getDetaliiInc08(), this);
		ListView listDetalii08 = (ListView) findViewById(R.id.listDetalii0_8);
		listDetalii08.setAdapter(detalii08Adapter);

		listDetalii08.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

	}

	private double getTotalDetaliiBaza(BeanSalarizareAgent salarizare) {
		double totalBaza = 0;

		for (SalarizareDetaliiBaza detaliiBaza : salarizare.getDetaliiBaza()) {
			totalBaza += detaliiBaza.getVenitBaza();
		}

		return totalBaza;
	}

	private double getTotalInc08(BeanSalarizareAgent salarizare) {
		double totalInc = 0;

		for (SalarizareDetaliiInc08 detaliiInc : salarizare.getDetaliiInc08()) {
			totalInc += detaliiInc.getValoareIncasare();
		}

		return totalInc;
	}

	private double getTotalCor08(BeanSalarizareAgent salarizare) {
		double totalCor = 0;

		for (SalarizareDetaliiInc08 detaliiInc : salarizare.getDetaliiInc08()) {
			totalCor += detaliiInc.getVenitCorectat();
		}

		return totalCor;
	}

	private void afisSalarizareDepartament(String result) {

		setSalAgentiVisibility(true);
		setDateGeneraleVisibility(false);
		setDetaliiVisibility(false);
		setDetaliiCSVVisibility(false);

		((TextView) findViewById(R.id.labelDateGenerale)).setText(listLuni[Integer.parseInt(lunaSelect) - 1] + "  " + anSelect);

		List<BeanSalarizareAgentAfis> listAgenti = operatiiSalarizare.deserializeSalarizareDepartament(result);

		ListView listViewAgenti = (ListView) findViewById(R.id.listAgenti);

		((TextView) findViewById(R.id.textAgentSelectat)).setText("Detalii agent " + numeAgentSelectat);

		SalarizareDepartAdapter departAdapter = new SalarizareDepartAdapter(listAgenti, getApplicationContext());
		departAdapter.setSalarizareDepartListener(this);
		listViewAgenti.setAdapter(departAdapter);

	}

	private void afisSalarizareSD(String result) {
		BeanSalarizareSD salarizare = operatiiSalarizare.deserializeSalarizareSD(result);

		isDetaliiSalarizare = false;
		isDetaliiAgent = false;
		setDetaliiCSVVisibility(false);

		afisSalarizareAgent(null, salarizare);

		setHeaderCVSVisibility(true);

		((TextView) findViewById(R.id.labelDateGenerale)).setText(listLuni[Integer.parseInt(lunaSelect) - 1] + "  " + anSelect);
		((TextView) findViewById(R.id.venitCVS)).setText(String.valueOf(salarizare.getDatePrincipale().getVenitCVS()));

		ListView listViewDetaliiCVS = (ListView) findViewById(R.id.listDetaliiCVS);

		SalarizareCvsAdapter cvsAdapter = new SalarizareCvsAdapter(salarizare.getDetaliiCvs(), getApplicationContext());
		listViewDetaliiCVS.setAdapter(cvsAdapter);
		listViewDetaliiCVS.setOnTouchListener(new OnTouchListener() {
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
	public void operatiiSalarizareComplete(EnumOperatiiSalarizare numeComanda, Object result) {

		switch (numeComanda) {
		case GET_SALARIZARE_AGENT:
			afisSalarizareAgent((String) result, null);
			break;
		case GET_SALARIZARE_DEPART:
			afisSalarizareDepartament((String) result);
			break;
		case GET_SALARIZARE_SD:
			afisSalarizareSD((String) result);
			break;
		default:
			break;
		}

	}

	@Override
	public void intervalSalarizareSelected(String luna, String an) {
		lunaSelect = luna;
		anSelect = an;

		isDetaliiAgent = false;

		if (UserInfo.getInstance().getTipUserSap().equals("AV"))
			getSalarizareAgent(luna, an);

	}

	@Override
	public void tipSituatieSalSelected(EnumSituatieSalarizare tipSituatie) {

		switch (tipSituatie) {
		case AGENTI:
			getSalarizareDepartament();
			break;
		case SEF_DEP:
			getSalarizareSD();
			break;
		default:
			break;
		}

	}

	@Override
	public void detaliiAgentSelected(String codAgent, String numeAgent) {
		isDetaliiAgent = true;
		numeAgentSelectat = numeAgent;
		getDetaliiAgent(codAgent, lunaSelect, anSelect);

	}

}
