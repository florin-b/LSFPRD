/**
 * @author florinb
 *
 */
package my.logon.screen;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import listeners.Cablu05SelectedListener;
import listeners.MagazinMathausListener;
import listeners.OperatiiArticolListener;
import model.ArticolComandaGed;
import model.Constants;
import model.DateLivrare;
import model.HelperUserSite;
import model.ListaArticoleComandaGed;
import model.OperatiiArticol;
import model.OperatiiArticolFactory;
import model.UserInfo;
import utils.DepartamentAgent;
import utils.UtilsArticole;
import utils.UtilsComenzi;
import utils.UtilsFormatting;
import utils.UtilsGeneral;
import utils.UtilsUser;
import adapters.CautareArticoleAdapter;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import beans.ArticolDB;
import beans.BeanCablu05;
import beans.BeanParametruPretGed;
import beans.DepoziteUl;
import beans.PretArticolGed;
import dialogs.Cabluri05Dialog;
import enums.EnumArticoleDAO;
import enums.EnumDepartExtra;
import enums.EnumTipClientIP;
import enums.EnumTipComanda;
import enums.TipCmdGed;
import filters.DecimalDigitsInputFilter;

public class SelectArtCmdGed extends ListActivity implements OperatiiArticolListener, MagazinMathausListener, Cablu05SelectedListener {

	Button articoleBtn, saveArtBtn, pretBtn;
	String filiala = "", nume = "", cod = "", umStoc = "";
	String articolResponse = "";
	String pretResponse = "";
	String codArticol = "";
	String numeArticol = "", tipArticol = "";
	String depart = "";

	String numeClientVar = "";
	LinearLayout redBtnTable, layoutStocKA;
	EditText valRedIntText, valRedDecText;

	public String globalDepozSel = "", artPromoText = "", globalCodDepartSelectetItem = "";

	ToggleButton tglButton, tglTipArtBtn;

	private EditText txtNumeArticol, textProcRed;
	private TextView textCodArticol, txtPretArt;
	private TextView textNumeArticol;

	ToggleButton tglProc;

	private TextView textStoc;
	private TextView textCant, procDisc, textPretGED, textMultipluArt;

	private TextView textUM;
	private TextView labelCant, labelStoc;
	private Spinner spinnerDepoz, spinnerUnitMas;

	private TextView textPromo, textCondPret, labelFactConv;

	private boolean pretMod = false;

	private double initPrice = 0, cmpArt = 0;
	private double finalPrice = 0;
	private double listPrice = 0, procDiscClient = 0;

	private double procentAprob = 0, selectedCant = 0;
	private double pretMediuDistrib = 0, adaosMediuDistrib = 0;
	private double valoareUmrez = 1, valoareUmren = 1;

	private static ArrayList<HashMap<String, String>> listUmVanz = null;
	public SimpleAdapter adapterUmVanz;
	private double varProc = 0, valMultiplu = 0, pretVanzare = 0;

	String tipAlert = "", codPromo = "0", infoArticol = "", Umb = "", cantUmb = "", selectedUnitMas = "", selectedUnitMasPret = "";

	private HashMap<String, String> artMap = null;
	double procR = 0, globalCantArt = 0;

	Dialog dialogSelFilArt;

	private double discMaxAV = 0, discMaxSD = 0;

	OperatiiArticol opArticol;

	String selectedDepartamentAgent;
	LinearLayout resultLayout;

	private boolean totalNegociat;
	private String codClientVar;

	private String tipComanda;
	private boolean rezervStoc;
	private String filialaAlternativa;

	private double coefCorectie;
	private ArrayAdapter<String> adapterSpinnerDepozite;
	private LinearLayout layoutPretGEDFTva;
	private TextView textPretGEDFTva, textTransport;
	private double procentTVA, procentTransport, valoareTransport;
	private PretArticolGed selectedArticol;

	private ArticolDB articolDBSelected;
	private TextView txtImpachetare;

	private String unitLogUnic = "";
	private boolean isFilialaMavSite = false;
	private List<BeanCablu05> listCabluri;

	private enum EnumDepoz {
		MAV1;
	}

	private String tipPersClient;
	private Spinner spinnerDepart;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.selectartcmd_ged_header);

		Intent intent = getIntent();

		totalNegociat = Boolean.valueOf(intent.getStringExtra("totalNegociat"));
		codClientVar = intent.getStringExtra("codClientVar");

		tipComanda = intent.getStringExtra("tipComanda");
		rezervStoc = Boolean.valueOf(intent.getStringExtra("rezervStoc"));
		filialaAlternativa = intent.getStringExtra("filialaAlternativa");

		tipPersClient = intent.getStringExtra("tipPersClient");

		if (isCV() && tipPersClient != null && !tipPersClient.isEmpty())
			DateLivrare.getInstance().setTipPersClient(tipPersClient);

		opArticol = OperatiiArticolFactory.createObject("OperatiiArticolImpl", this);
		opArticol.setListener(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		initSelectionDepartament();

		if (!isCV())
			addSpinnerDepartamente();

		resultLayout = (LinearLayout) findViewById(R.id.resLayout);
		resultLayout.setVisibility(View.INVISIBLE);

		labelFactConv = (TextView) findViewById(R.id.labelFactConv);
		labelFactConv.setVisibility(View.INVISIBLE);

		layoutStocKA = (LinearLayout) findViewById(R.id.layoutStocKA);
		layoutStocKA.setVisibility(View.INVISIBLE);

		this.articoleBtn = (Button) findViewById(R.id.articoleBtn);
		addListenerBtnArticole();

		this.saveArtBtn = (Button) findViewById(R.id.saveArtBtn);
		addListenerBtnSaveArt();

		this.tglButton = (ToggleButton) findViewById(R.id.togglebutton);
		addListenerToggle();
		this.tglButton.setChecked(true);

		this.tglTipArtBtn = (ToggleButton) findViewById(R.id.tglTipArt);
		addListenerTglTipArtBtn();

		this.redBtnTable = (LinearLayout) findViewById(R.id.RedBtnTable);

		txtPretArt = (TextView) findViewById(R.id.txtPretArt);

		this.tglProc = (ToggleButton) findViewById(R.id.tglProc);
		addListenerTglProc();

		this.pretBtn = (Button) findViewById(R.id.pretBtn);
		addListenerPretBtn();

		textProcRed = (EditText) findViewById(R.id.textProcRed);
		textProcRed.setFocusableInTouchMode(true);
		addListenerProcArt();

		if (CreareComandaGed.tipClient.equals("IP"))
			textProcRed.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		else
			textProcRed.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(3) });

		procDisc = (TextView) findViewById(R.id.procDisc);

		textMultipluArt = (TextView) findViewById(R.id.txtMultipluArt);

		txtNumeArticol = (EditText) findViewById(R.id.txtNumeArt);
		textNumeArticol = (TextView) findViewById(R.id.textNumeArticol);
		textCodArticol = (TextView) findViewById(R.id.textCodArticol);
		textUM = (TextView) findViewById(R.id.textUm);
		textStoc = (TextView) findViewById(R.id.textStoc);
		textCant = (EditText) findViewById(R.id.txtCantArt);
		labelCant = (TextView) findViewById(R.id.labelCant);
		labelStoc = (TextView) findViewById(R.id.labelStoc);
		textCondPret = (TextView) findViewById(R.id.textCondPret);
		textPretGED = (TextView) findViewById(R.id.textPretGED);

		txtImpachetare = (TextView) findViewById(R.id.txtImpachetare);

		textPromo = (TextView) findViewById(R.id.textPromo);
		addListenerTextPromo();

		txtNumeArticol.setHint("Introduceti cod articol");

		spinnerDepoz = (Spinner) findViewById(R.id.spinnerDepoz);

		ArrayList<String> arrayListDepozite = new ArrayList<String>();

		if (isWood())
			arrayListDepozite.addAll(Arrays.asList(UtilsGeneral.getDepoziteWood()));
		else if (UtilsUser.isUserSite())
			arrayListDepozite.addAll(Arrays.asList(UtilsGeneral.getDepoziteSite()));
		else
			arrayListDepozite.addAll(Arrays.asList(UtilsGeneral.getDepoziteGed()));

		adapterSpinnerDepozite = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListDepozite);

		UtilsGeneral.trateazaExceptieMAV_BU(adapterSpinnerDepozite);
		adapterSpinnerDepozite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDepoz.setAdapter(adapterSpinnerDepozite);
		spinnerDepoz.setOnItemSelectedListener(new OnSelectDepozit());

		if (isWood() || UtilsUser.isCV())
			setDefaultDepoz(EnumDepoz.MAV1, arrayListDepozite);

		spinnerUnitMas = (Spinner) findViewById(R.id.spinnerUnitMas);

		listUmVanz = new ArrayList<HashMap<String, String>>();
		adapterUmVanz = new SimpleAdapter(this, listUmVanz, R.layout.simplerowlayout, new String[] { "rowText" }, new int[] { R.id.textRowName });
		spinnerUnitMas.setVisibility(View.GONE);
		spinnerUnitMas.setOnItemSelectedListener(new OnSelectUnitMas());

		textNumeArticol.setVisibility(View.INVISIBLE);
		textCodArticol.setVisibility(View.INVISIBLE);
		textUM.setVisibility(View.INVISIBLE);

		textStoc.setVisibility(View.INVISIBLE);
		textCant.setVisibility(View.INVISIBLE);

		labelCant.setVisibility(View.INVISIBLE);

		labelStoc.setVisibility(View.INVISIBLE);
		saveArtBtn.setVisibility(View.INVISIBLE);

		redBtnTable.setVisibility(View.INVISIBLE);
		textProcRed.setVisibility(View.INVISIBLE);
		pretBtn.setVisibility(View.INVISIBLE);
		textPromo.setVisibility(View.INVISIBLE);
		procDisc.setVisibility(View.INVISIBLE);
		textCondPret.setVisibility(View.INVISIBLE);
		textPretGED.setVisibility(View.INVISIBLE);
		textMultipluArt.setVisibility(View.INVISIBLE);

		layoutPretGEDFTva = (LinearLayout) findViewById(R.id.layoutPretGEDFTva);
		layoutPretGEDFTva.setVisibility(View.GONE);

		textPretGEDFTva = (TextView) findViewById(R.id.textPretGEDFTVA);
		textTransport = (TextView) findViewById(R.id.textTransport);

		if (UserInfo.getInstance().getTipUserSap().equals("KA3") && DateLivrare.getInstance().getTipPersClient().equals("D")) {
			layoutPretGEDFTva.setVisibility(View.VISIBLE);
		}

		if (CreareComandaGed.tipComandaGed == TipCmdGed.COMANDA_LIVRARE) {

			if (ListaArticoleComandaGed.getInstance().getListArticoleComanda().size() == 0) {
				CreareComandaGed.selectedDepozIndexClp = -1;
				CreareComandaGed.selectedDepartIndexClp = -1;
				CreareComandaGed.selectedDepartCod = "-1";
			}

			if (CreareComandaGed.selectedDepozIndexClp != -1) {
				if (spinnerDepart != null) {
					spinnerDepart.setSelection(CreareComandaGed.selectedDepartIndexClp);
					spinnerDepart.setEnabled(false);
				}

				spinnerDepoz.setSelection(CreareComandaGed.selectedDepozIndexClp);
				spinnerDepoz.setEnabled(false);
			}
		}

	}

	private void setDefaultDepoz(EnumDepoz depoz, ArrayList<String> listDepozite) {

		if (depoz == EnumDepoz.MAV1) {
			int position = 0;
			for (String dep : listDepozite) {
				if (dep.equals(depoz.toString())) {
					spinnerDepoz.setSelection(position);
					break;
				}
				position++;
			}
		}

	}

	private void addSpinnerDepartamente() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
				DepartamentAgent.getDepartamenteAgentGED());

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.spinner_layout, null);
		spinnerDepart = (Spinner) mCustomView.findViewById(R.id.spinnerDep);

		spinnerDepart.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedDepartamentAgent = EnumDepartExtra.getCodDepart(spinnerDepart.getSelectedItem().toString());
				populateListViewArticol(new ArrayList<ArticolDB>());

				if (selectedDepartamentAgent.equals("11") || selectedDepartamentAgent.equals("05")) {
					adapterSpinnerDepozite.clear();
					adapterSpinnerDepozite.addAll(UtilsGeneral.getDepoziteGed());
					UtilsGeneral.trateazaExceptieMAV_BU(adapterSpinnerDepozite);

					if (selectedDepartamentAgent.equals("11"))
						spinnerDepoz.setSelection(adapterSpinnerDepozite.getPosition("MAV1"));
					else
						spinnerDepoz.setSelection(0);
				} else {
					adapterSpinnerDepozite.clear();
					adapterSpinnerDepozite.addAll(UtilsGeneral.getDepoziteGed());
					UtilsGeneral.trateazaExceptieMAV_BU(adapterSpinnerDepozite);
					spinnerDepoz.setSelection(0);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerDepart.setAdapter(adapter);
		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);

	}

	private void initSelectionDepartament() {

		selectedDepartamentAgent = UserInfo.getInstance().getCodDepart();

		if (isCV())
			selectedDepartamentAgent = "";

		if (isKA())
			selectedDepartamentAgent = "00";

		if (isWood())
			selectedDepartamentAgent = "12";

	}

	boolean isKA() {
		return UserInfo.getInstance().getTipUser().equals("KA") || UserInfo.getInstance().getTipUser().equals("DK");
	}

	boolean isCV() {
		return UserInfo.getInstance().getTipUser().equals("CV") || UserInfo.getInstance().getTipUser().equals("SM")
				|| UserInfo.getInstance().getTipUser().equals("CVR") || UserInfo.getInstance().getTipUser().equals("SMR")
				|| UserInfo.getInstance().getTipUser().equals("WOOD") || UserInfo.getInstance().getTipUser().equals("SC");
	}

	boolean isWood() {
		return UserInfo.getInstance().getTipUser().equals("WOOD");
	}

	private boolean isUserCAG() {
		return UserInfo.getInstance().getTipUserSap().toUpperCase().startsWith("CAG");
	}

	private boolean isUserCVA() {
		return UserInfo.getInstance().getTipUserSap().toUpperCase().startsWith("CVA");
	}

	private void CreateMenu(Menu menu) {

		if ((UtilsUser.isUserExceptieBV90Ged() || UtilsUser.isUserSite() || isWood() || UtilsUser.isUserIP() || isUserCAG() || isUserCVA())
				&& CreareComandaGed.tipComandaGed == TipCmdGed.COMANDA_VANZARE) {
			MenuItem mnu1 = menu.add(0, 0, 0, "Filiala");
			{
				mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
				populateListViewArticol(new ArrayList<ArticolDB>());

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	// eveniment selectie unitate masura alternativa
	public class OnSelectUnitMas implements OnItemSelectedListener {
		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if (codArticol.length() > 0) {

				artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();
				selectedUnitMas = artMap.get("rowText");

				getFactoriConversie();

			}

		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private void getFactoriConversie() {
		try {

			HashMap<String, String> params = new HashMap<String, String>();

			if (codArticol.length() == 8)
				codArticol = "0000000000" + codArticol;

			params.put("codArt", codArticol);
			params.put("unitMas", selectedUnitMas);

			opArticol.getFactorConversie(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	public class OnSelectDepozit implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if (codArticol.length() > 0) {
				String[] tokenDep = spinnerDepoz.getSelectedItem().toString().split("-");

				if (tokenDep[0].trim().length() == 2)
					globalDepozSel = globalCodDepartSelectetItem.substring(0, 2) + tokenDep[0].trim();
				else
					globalDepozSel = tokenDep[0].trim();

				performListArtStoc();
			}

		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			showFilialaDialogBox();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void showFilialaDialogBox() {
		if (UtilsUser.isUserExceptieBV90Ged() || isWood() || UtilsUser.isUserIP() || isUserCAG() || isUserCVA()) {
			showFilialaDialogBV90();
		} else if (UtilsUser.isUserSite()) {
			showFilialaDialogUserSite();
		}
	}

	private void showFilialaDialogUserSite() {

		List<String> listDepozite = DepoziteUl.getInstance().getListDepozite();

		if (listDepozite == null)
			return;

		dialogSelFilArt = new Dialog(SelectArtCmdGed.this);
		dialogSelFilArt.setContentView(R.layout.select_fil_site);
		dialogSelFilArt.setTitle("Selectati filiala");
		dialogSelFilArt.setCancelable(false);
		dialogSelFilArt.show();

		final RadioButton radioFilAg = (RadioButton) dialogSelFilArt.findViewById(R.id.radio1);
		radioFilAg.setText(UserInfo.getInstance().getUnitLog());
		radioFilAg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					adapterSpinnerDepozite.clear();
					adapterSpinnerDepozite.addAll(UtilsGeneral.getDepoziteSite());
					spinnerDepoz.setSelection(0);
					isFilialaMavSite = false;
				}

			}
		});

		final RadioButton radioFilMav = (RadioButton) dialogSelFilArt.findViewById(R.id.radio2);

		if (HelperUserSite.hasDepozitMagazin(listDepozite)) {
			radioFilMav.setText("Magazin");
			radioFilMav.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					if (isChecked) {
						adapterSpinnerDepozite.clear();
						adapterSpinnerDepozite.addAll(UtilsGeneral.getDepoziteMav());
						adapterSpinnerDepozite.notifyDataSetChanged();
						spinnerDepoz.setSelection(0);
						isFilialaMavSite = true;
					}

				}
			});
		} else
			radioFilMav.setVisibility(View.GONE);

		final RadioButton radioFilBV90 = (RadioButton) dialogSelFilArt.findViewById(R.id.radio3);

		if (listDepozite.contains("BV90")) {
			radioFilBV90.setText("BV90");
			radioFilBV90.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					if (isChecked) {
						adapterSpinnerDepozite.clear();
						adapterSpinnerDepozite.addAll(UtilsGeneral.getDepoziteSite());
						spinnerDepoz.setSelection(0);
						isFilialaMavSite = false;
					}
				}
			});
		} else {
			radioFilBV90.setVisibility(View.GONE);
		}

		if (CreareComandaGed.filialaAlternativa.equals(UserInfo.getInstance().getUnitLog()))
			radioFilAg.setChecked(true);
		else if (CreareComandaGed.filialaAlternativa.equals("BV90"))
			radioFilBV90.setChecked(true);
		else
			radioFilMav.setChecked(true);

		Button btnOkFilArt = (Button) dialogSelFilArt.findViewById(R.id.btnOkSelFilArt);
		btnOkFilArt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (radioFilAg.isChecked()) {
					CreareComandaGed.filialaAlternativa = UserInfo.getInstance().getUnitLog();

				} else if (radioFilMav.isChecked()) {
					CreareComandaGed.filialaAlternativa = HelperUserSite.getDepozitMavSite();
				} else {
					CreareComandaGed.filialaAlternativa = "BV90";
				}

				if (!numeArticol.equals("")) {
					performListArtStoc();
				}
				dialogSelFilArt.dismiss();

			}
		});

	}

	private void showFilialaDialogBV90() {
		dialogSelFilArt = new Dialog(SelectArtCmdGed.this);
		dialogSelFilArt.setContentView(R.layout.selectfilartdialogbox);
		dialogSelFilArt.setTitle("Selectati filiala");
		dialogSelFilArt.setCancelable(false);
		dialogSelFilArt.show();

		final RadioButton radioFilAg = (RadioButton) dialogSelFilArt.findViewById(R.id.radio1);
		radioFilAg.setText(UserInfo.getInstance().getUnitLog());
		radioFilAg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			}
		});

		final RadioButton radioFilBV90 = (RadioButton) dialogSelFilArt.findViewById(R.id.radio2);

		radioFilBV90.setText("BV90");
		radioFilBV90.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			}
		});

		if (CreareComandaGed.filialaAlternativa.equals(UserInfo.getInstance().getUnitLog()))
			radioFilAg.setChecked(true);
		else
			radioFilBV90.setChecked(true);

		Button btnOkFilArt = (Button) dialogSelFilArt.findViewById(R.id.btnOkSelFilArt);
		btnOkFilArt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (radioFilAg.isChecked()) {
					CreareComandaGed.filialaAlternativa = UserInfo.getInstance().getUnitLog();

				} else {
					CreareComandaGed.filialaAlternativa = "BV90";
				}

				if (!numeArticol.equals("")) {
					performListArtStoc();
				}
				dialogSelFilArt.dismiss();

			}
		});

	}

	public void addListenerTextPromo() {
		textPromo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!artPromoText.equals(""))
					showPromoWindow(artPromoText);

			}
		});
	}

	public void addListenerTglProc() {
		tglProc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				NumberFormat nf2 = NumberFormat.getInstance(new Locale("en", "US"));
				if (CreareComandaGed.tipClient.equals("IP")) {
					nf2.setMinimumFractionDigits(2);
					nf2.setMaximumFractionDigits(2);
				} else {
					nf2.setMinimumFractionDigits(3);
					nf2.setMaximumFractionDigits(3);
				}
				nf2.setGroupingUsed(false);

				NumberFormat nForm2 = NumberFormat.getInstance(new Locale("en", "US"));
				nForm2.setMinimumFractionDigits(2);
				nForm2.setMaximumFractionDigits(2);

				if (globalCantArt > 0) {

					if (tglProc.isChecked()) {

						varProc = -1;

						textProcRed.setText(nf2.format(initPrice / globalCantArt * valMultiplu));

						textProcRed.requestFocus();
						textProcRed.setSelection(textProcRed.getText().length());

						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.showSoftInput(textProcRed, InputMethodManager.SHOW_IMPLICIT);

						txtPretArt.setText("0");

						pretMod = true;
						finalPrice = initPrice;

						textPretGED.setText(nf2.format(initPrice / globalCantArt * valMultiplu));
						textPretGEDFTva.setText(nf2.format((initPrice / globalCantArt * valMultiplu) / procentTVA));
						textTransport.setText(nForm2.format(initPrice * (procentTransport / 100) + valoareTransport));

					} else {

						varProc = 0;
						textProcRed.setText("");

						textProcRed.requestFocus();
						textProcRed.setSelection(textProcRed.getText().length());

						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.showSoftInput(textProcRed, InputMethodManager.SHOW_IMPLICIT);

						txtPretArt.setText(nf2.format(initPrice / globalCantArt * valMultiplu));
						pretMod = false;
						finalPrice = initPrice;

						textPretGED.setText(String.valueOf(nf2.format(initPrice / globalCantArt * valMultiplu)));
						textPretGEDFTva.setText(nf2.format((initPrice / globalCantArt * valMultiplu) / procentTVA));
						textTransport.setText(nForm2.format(initPrice * (procentTransport / 100) + valoareTransport));

					}
				}
			}
		});

	}

	public void addListenerProcArt() {

		textProcRed.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (textProcRed.hasFocus()) {
					InputMethodManager inputStatus = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputStatus.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				}

			}
		});

		textProcRed.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {

				try {

					NumberFormat nf2 = NumberFormat.getInstance(new Locale("en", "US"));
					if (CreareComandaGed.tipClient.equals("IP")) {
						nf2.setMinimumFractionDigits(2);
						nf2.setMaximumFractionDigits(2);
					} else {
						nf2.setMinimumFractionDigits(3);
						nf2.setMaximumFractionDigits(3);
					}

					NumberFormat nForm2 = NumberFormat.getInstance(new Locale("en", "US"));
					nForm2.setMinimumFractionDigits(2);
					nForm2.setMaximumFractionDigits(2);

					// verif. cantitate

					if (globalCantArt > 0) {

						if (!pretMod) // modificare valoare
						{
							if (isNumeric(textProcRed.getText().toString()) && isNumeric(textCant.getText().toString())) {

								if (varProc != -1) {
									varProc = Double.parseDouble(textProcRed.getText().toString());

									if (varProc > 0) {

										double newPr = (((initPrice / globalCantArt) * valMultiplu - (initPrice / globalCantArt) * valMultiplu
												* (varProc / 100)));

										txtPretArt.setText(nf2.format(newPr));
										finalPrice = newPr;
										textPretGEDFTva.setText(nf2.format(finalPrice / procentTVA));

										textTransport.setText(nForm2.format(((finalPrice / valMultiplu) * globalCantArt) * (procentTransport / 100)
												+ valoareTransport));

									}
								}

							} else {
								txtPretArt.setText(nf2.format(initPrice / globalCantArt * valMultiplu));
								textPretGEDFTva.setText(nf2.format((initPrice / globalCantArt * valMultiplu) / procentTVA));
								textTransport.setText(nForm2.format((initPrice) * (procentTransport / 100) + valoareTransport));

							}

						}// modificare procent
						else {

							if (isNumeric(textProcRed.getText().toString()) && isNumeric(textCant.getText().toString())) {

								double val1 = Double.parseDouble(textProcRed.getText().toString());

								procR = (((initPrice / globalCantArt * valMultiplu) - val1) / ((initPrice / globalCantArt * valMultiplu))) * 100;

								txtPretArt.setText(nf2.format(procR));
								finalPrice = Double.parseDouble(textProcRed.getText().toString());
								textPretGEDFTva.setText(nf2.format(finalPrice / procentTVA));
								textTransport.setText(nForm2.format(((finalPrice / valMultiplu) * globalCantArt) * (procentTransport / 100)
										+ valoareTransport));

							} else {
								txtPretArt.setText("0");
								finalPrice = 0;
								textPretGEDFTva.setText("0");
								textTransport.setText("0");

							}

						}

						afisPretUmAlternativa();

					}// sf. verif. cantitate

				} catch (Exception ex) {
					finalPrice = 0;
					Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
		});
	}

	public boolean isNumeric(String input) {
		try {
			Double.parseDouble(input.replace(",", ""));
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public void addListenerPretBtn() {
		pretBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				InputMethodManager mgr;

				try {

					if (textCant.getText().toString().trim().equals("")) {
						Toast.makeText(getApplicationContext(), "Cantitate invalida!", Toast.LENGTH_SHORT).show();
						return;
					}

					mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(textCant.getWindowToken(), 0);

					performGetPret();

				} catch (Exception ex) {
					Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	@SuppressWarnings("unchecked")
	protected void performGetPret() {

		try {

			selectedCant = Double.parseDouble(textCant.getText().toString().trim());

			selectedUnitMas = "";
			selectedUnitMasPret = "";
			listCabluri = null;
			if (listUmVanz.size() > 1) {
				artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();
				selectedUnitMas = artMap.get("rowText");
				selectedUnitMasPret = artMap.get("rowText");
			}

			if (selectedCant > 0) {
				callGetPret();

			}

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	@SuppressWarnings("unchecked")
	private void callGetPret() {
		HashMap<String, String> params = new HashMap<String, String>();

		if (codArticol.length() == 8)
			codArticol = "0000000000" + codArticol;

		String uLog = UserInfo.getInstance().getUnitLog().substring(0, 2) + "2" + UserInfo.getInstance().getUnitLog().substring(3, 4);
		String tipUser = UserInfo.getInstance().getTipUser();

		if (isWood()) {
			uLog = UserInfo.getInstance().getUnitLog().substring(0, 2) + "4" + UserInfo.getInstance().getUnitLog().substring(3, 4);
			tipUser = "CV";
		}

		String paramUnitMas = textUM.getText().toString();

		if (listUmVanz.size() > 1) {
			artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();
			paramUnitMas = artMap.get("rowText");

		}

		BeanParametruPretGed paramPret = new BeanParametruPretGed();
		paramPret.setClient(codClientVar);
		paramPret.setArticol(codArticol);
		paramPret.setCantitate(textCant.getText().toString().trim());
		paramPret.setDepart(globalCodDepartSelectetItem);
		paramPret.setUm(paramUnitMas);
		paramPret.setUl(uLog);
		paramPret.setDepoz(" ");
		paramPret.setCodUser(UserInfo.getInstance().getCod());
		paramPret.setCanalDistrib("20");
		paramPret.setTipUser(tipUser);
		paramPret.setMetodaPlata(DateLivrare.getInstance().getTipPlata());
		paramPret.setTermenPlata(DateLivrare.getInstance().getTermenPlata());
		paramPret.setCodJudet(getCodJudetPret());
		paramPret.setLocalitate(getLocalitatePret());
		paramPret.setFilialaAlternativa(CreareComandaGed.filialaAlternativa);
		paramPret.setCodClientParavan(CreareComandaGed.codClientParavan);
		paramPret.setFilialaClp(DateLivrare.getInstance().getCodFilialaCLP());

		params.put("parametruPret", opArticol.serializeParamPretGed(paramPret));

		opArticol.getPretGedJson(params);

	}

	private String getCodJudetPret() {

		if (DateLivrare.getInstance().isAltaAdresa()) {
			return DateLivrare.getInstance().getCodJudetD();
		} else {
			return DateLivrare.getInstance().getCodJudet();
		}

	}

	private String getLocalitatePret() {

		if (DateLivrare.getInstance().isAltaAdresa()) {
			return DateLivrare.getInstance().getOrasD();
		} else {
			return DateLivrare.getInstance().getOras();
		}

	}

	public void addListenerToggle() {
		tglButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (tglButton.isChecked()) {
					if (tglTipArtBtn.isChecked())
						txtNumeArticol.setHint("Introduceti cod sintetic");
					else
						txtNumeArticol.setHint("Introduceti cod articol");
				} else {
					if (tglTipArtBtn.isChecked())
						txtNumeArticol.setHint("Introduceti nume sintetic");
					else
						txtNumeArticol.setHint("Introduceti nume articol");
				}
			}
		});

	}

	public void addListenerTglTipArtBtn() {
		tglTipArtBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (tglTipArtBtn.isChecked()) {
					if (!tglButton.isChecked())
						txtNumeArticol.setHint("Introduceti nume sintetic");
					else
						txtNumeArticol.setHint("Introduceti cod sintetic");
				} else {
					if (!tglButton.isChecked())
						txtNumeArticol.setHint("Introduceti nume articol");
					else
						txtNumeArticol.setHint("Introduceti cod articol");

				}
			}
		});

	}

	public void populateListViewArticol(List<ArticolDB> resultsList) {

		clearTextField(txtNumeArticol);
		resultLayout.setVisibility(View.INVISIBLE);

		if (UtilsUser.isUserSite() && isFilialaMavSite)
			UtilsArticole.getArt111Only(resultsList);

		CautareArticoleAdapter adapterArticole = new CautareArticoleAdapter(this, resultsList);
		setListAdapter(adapterArticole);

	}

	private void clearTextField(EditText textField) {
		textField.setText("");
	}

	public void addListenerBtnArticole() {
		articoleBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (txtNumeArticol.getText().toString().length() > 0) {

						textNumeArticol.setVisibility(View.GONE);
						textCodArticol.setVisibility(View.GONE);
						textUM.setVisibility(View.GONE);
						textStoc.setVisibility(View.GONE);
						textCant.setVisibility(View.GONE);
						labelCant.setVisibility(View.GONE);
						labelStoc.setVisibility(View.GONE);
						saveArtBtn.setVisibility(View.GONE);

						pretBtn.setVisibility(View.GONE);

						redBtnTable.setVisibility(View.GONE);
						spinnerUnitMas.setVisibility(View.GONE);
						layoutStocKA.setVisibility(View.GONE);

						performGetArticole();

					} else {
						Toast.makeText(getApplicationContext(), "Introduceti nume articol!", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception ex) {
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	protected void performGetArticole() {
		if (isComandaDL() && DateLivrare.getInstance().getTipComandaGed() == TipCmdGed.COMANDA_LIVRARE) {
			performGetArticoleFurnizor();
		} else if (isComandaDL() && DateLivrare.getInstance().getTipComandaGed() == TipCmdGed.ARTICOLE_COMANDA)
			getArticoleACZC();
		else {
			performGetArticoleDistributie();
		}

	}

	private void getArticoleACZC() {
		performGetArticoleFurnizor();
	}

	protected void performGetArticoleDistributie() {

		String numeArticol = txtNumeArticol.getText().toString().trim();
		String tipCautare = "", tipArticol = "";

		if (tglButton.isChecked())
			tipCautare = "C";
		else
			tipCautare = "N";

		if (tglTipArtBtn.isChecked())
			tipArticol = "S";
		else
			tipArticol = "A";

		String departCautare = selectedDepartamentAgent;

		if (CreareComandaGed.tipComandaGed == TipCmdGed.COMANDA_LIVRARE && !CreareComandaGed.selectedDepartCod.equals("-1"))
			departCautare = CreareComandaGed.selectedDepartCod;

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("searchString", numeArticol);
		params.put("tipArticol", tipArticol);
		params.put("tipCautare", tipCautare);
		params.put("filiala", UserInfo.getInstance().getUnitLog());
		params.put("departament", departCautare);
		params.put("codUser", UserInfo.getInstance().getCod());

		opArticol.getArticoleDistributie(params);

	}

	private void performGetArticoleFurnizor() {
		HashMap<String, String> params = new HashMap<String, String>();

		String tipArticol1 = "", tipArticol2 = "";

		if (tglButton.isChecked()) {
			tipArticol1 = "1";
			if (tglTipArtBtn.isChecked()) {
				tipArticol2 = "2";
			} else {
				tipArticol2 = "1";
			}

		} else {
			tipArticol1 = "2";
			if (tglTipArtBtn.isChecked()) {
				tipArticol2 = "2";
			} else {
				tipArticol2 = "1";
			}

		}

		String numeArticol = txtNumeArticol.getText().toString().trim();

		params.put("codArticol", numeArticol);
		params.put("tip1", tipArticol1);
		params.put("tip2", tipArticol2);
		params.put("furnizor", DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa());
		params.put("codDepart", selectedDepartamentAgent);
		params.put("codUser", UserInfo.getInstance().getCod());
		params.put("filiala", UserInfo.getInstance().getUnitLog());

		if (DateLivrare.getInstance().getTipComandaGed() == TipCmdGed.ARTICOLE_COMANDA)
			params.put("aczc", "true");
		else
			params.put("aczc", "false");

		opArticol.getArticoleFurnizor(params);
	}

	boolean isAgentorSD() {
		return UserInfo.getInstance().getTipUser().equals("AV") || UserInfo.getInstance().getTipUser().equals("SD");
	}

	@SuppressWarnings("unchecked")
	public void addListenerBtnSaveArt() {
		saveArtBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				try {

					String localUnitMas = "";
					String alteValori = "", subCmp = "0";

					NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
					nf.setMinimumFractionDigits(2);
					nf.setMaximumFractionDigits(2);

					NumberFormat nfPret = NumberFormat.getInstance(new Locale("en", "US"));
					nfPret.setMinimumFractionDigits(3);
					nfPret.setMaximumFractionDigits(3);

					if (textCant.getVisibility() != View.VISIBLE) {
						return;
					}

					if (isConditieCabluri05BV90() && listCabluri == null) {
						getCabluri05(codArticol);
						return;
					}

					if (ListaArticoleComandaGed.getInstance().getListArticoleComanda().size() == 0) {
						unitLogUnic = CreareComandaGed.filialaAlternativa;
					} else if (ListaArticoleComandaGed.getInstance().getListArticoleComanda().size() > 0) {
						unitLogUnic = ListaArticoleComandaGed.getInstance().getListArticoleComanda().get(0).getFilialaSite();
					}

					if (totalNegociat && UtilsUser.isUserGed() && !globalCodDepartSelectetItem.substring(0, 2).equals("09")) {
						Toast.makeText(getApplicationContext(), "Pentru comenzile cu valoare negociata adaugati doar articole din divizia 09.",
								Toast.LENGTH_LONG).show();
						return;
					}

					if (!unitLogUnic.equals(CreareComandaGed.filialaAlternativa) && UtilsUser.isUserIP()
							&& CreareComandaGed.tipComandaGed == TipCmdGed.COMANDA_VANZARE) {
						Toast.makeText(getApplicationContext(), "Selectati articole dintr-o singura filiala!", Toast.LENGTH_LONG).show();
						return;
					}

					if (UtilsUser.isUserIP() && !conditiiCmdIP()) {
						return;
					}

					if (listUmVanz.size() > 1) {

						artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();
						String currentUnitMas = artMap.get("rowText");

						if (!currentUnitMas.equals(selectedUnitMasPret)) {
							Toast.makeText(getApplicationContext(), "Unitatea de masura nu corespunde celei pentru care a fost solicitat pretul.",
									Toast.LENGTH_LONG).show();
							return;

						}

					}

					String cantArticol = textCant.getText().toString().trim();

					if (selectedCant != Double.parseDouble(cantArticol)) {

						// pentru comenzile simulate fara rezervare de
						// stoc se poate afisa pretul
						if (tipComanda.equalsIgnoreCase("S") && !rezervStoc) {

						} else {
							Toast.makeText(getApplicationContext(), "Pretul nu corespunde cantitatii solicitate!", Toast.LENGTH_LONG).show();
							return;
						}
					}

					if (!isComandaDL()
							&& Double.parseDouble(textCant.getText().toString().trim()) * (valoareUmrez / valoareUmren) > Double.parseDouble(textStoc
									.getText().toString().replaceAll(",", ""))) {
						if (tipComanda.equalsIgnoreCase("S") && !rezervStoc) {

						} else {
							Toast.makeText(getApplicationContext(), "Stoc insuficient!", Toast.LENGTH_LONG).show();
							return;
						}
					}

					// verificare umvanz.

					localUnitMas = (textUM.getText().toString().trim().length() > 0) ? textUM.getText().toString().trim() : " ";

					if (listUmVanz.size() > 1) {

						artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();
						localUnitMas = artMap.get("rowText");

						if (!selectedUnitMas.equals(localUnitMas)) {
							Toast.makeText(getApplicationContext(), "U.m. vanzare eronata!", Toast.LENGTH_LONG).show();

							return;
						}

					}

					// verificare procent discount
					double procRedFin = 0, valArticol = 0;
					procentAprob = 0;

					if (finalPrice == initPrice) // pretul din sap e pe
													// cantitate, daca se
													// modifica devine pe
													// unitate
						finalPrice = (finalPrice / globalCantArt) * valMultiplu;

					valArticol = ((finalPrice / valMultiplu) * globalCantArt);

					if (initPrice != 0) {
						if (!tglProc.isChecked()) {
							if (textProcRed.getText().length() > 0)
								procRedFin = Double.parseDouble(textProcRed.getText().toString());
							else
								procRedFin = 0;
						} else
							procRedFin = (1 - finalPrice / (initPrice / globalCantArt * valMultiplu)) * 100;
					}

					if (procRedFin > 80) {
						// Toast.makeText(getApplicationContext(),
						// "Procentul de reducere depaseste pragul admis! ",
						// Toast.LENGTH_SHORT).show();
						// restrictie eliminata pentru a se putea vinde articole
						// cu promotii de la dep 03. 12 martie 2019
						// return;
					}

					// sf. verificare

					// pt. unele articole care nu au pret (servicii)
					if (pretVanzare == 0)
						pretVanzare = finalPrice;

					procentAprob = (1 - finalPrice / (pretVanzare / globalCantArt * valMultiplu)) * 100;

					if (finalPrice != 0) {

						tipAlert = " ";

						if (UtilsUser.isAgentOrSD() || UtilsUser.isConsWood()) {
							if (procentAprob > discMaxAV) {
								tipAlert = "SD";
							}
							if (procentAprob > discMaxSD) {
								tipAlert += ";DV";
							}
						}

						if (finalPrice < cmpArt) {
							subCmp = "1";
						}

						double factorConvUM = 1;
						if (!textUM.getText().toString().trim().equals(localUnitMas)) {
							factorConvUM = (Double.parseDouble(cantArticol) / valMultiplu) / globalCantArt;
						}

						alteValori = String.valueOf(valArticol) + "!" + String.valueOf(listPrice) + "!" + String.valueOf(finalPrice) + "!"
								+ String.valueOf(pretMediuDistrib * (valoareUmrez / valoareUmren)) + "!"
								+ String.valueOf(adaosMediuDistrib * (valoareUmrez / valoareUmren)) + "!" + codPromo + "!" + subCmp + "!"
								+ String.valueOf(factorConvUM) + "!";

						if (codArticol.length() == 18)
							codArticol = codArticol.substring(10, 18);

						double pretUnitClient = valArticol / Double.parseDouble(cantUmb);
						double pretUnitGed = initPrice / Double.parseDouble(cantUmb);

						ArticolComandaGed articol = new ArticolComandaGed();

						articol.setNumeArticol(numeArticol);
						articol.setCodArticol(codArticol);
						articol.setCantitate(Double.valueOf(cantArticol));
						articol.setPretUnitGed(pretUnitGed);
						articol.setUm(localUnitMas);
						articol.setDepozit(globalDepozSel);
						articol.setPretUnitarClient(pretUnitClient);
						articol.setPretUnit(pretUnitClient);
						articol.setTipAlert(tipAlert);
						articol.setPromotie(Integer.valueOf(codPromo));

						if (UtilsUser.isAgentOrSD() || UtilsUser.isConsWood()) {
							if (articol.getPromotie() >= 1)
								articol.setPonderare(0);
							else
								articol.setPonderare(1);
						} else {
							if (articol.getPromotie() >= 1)
								articol.setPonderare(0);
							else
								articol.setPonderare(2);
						}

						articol.setProcent(procRedFin);
						articol.setDiscClient(procDiscClient);
						articol.setProcAprob(procentAprob);
						articol.setMultiplu(valMultiplu);
						articol.setPret(articol.getPretUnitarClient() * articol.getCantUmb());
						articol.setInfoArticol(infoArticol);
						articol.setUmb(Umb);
						articol.setCantUmb(Double.valueOf(cantUmb));
						articol.setAlteValori(alteValori);
						articol.setDepart(globalCodDepartSelectetItem);
						articol.setDepartSintetic(articol.getDepart());
						articol.setCmp(cmpArt);
						articol.setCoefCorectie(coefCorectie);
						articol.setPretMediu(pretMediuDistrib);
						articol.setAdaosMediu(adaosMediuDistrib);
						articol.setTipArt(tipArticol);
						articol.setValTransport((articol.getPretUnitarClient() * articol.getCantUmb()) * (selectedArticol.getProcTransport() / 100)
								+ selectedArticol.getValTrap());
						articol.setProcTransport(selectedArticol.getProcTransport());
						articol.setDiscountAg(discMaxAV);
						articol.setDiscountSd(discMaxSD);
						articol.setUmPalet(articolDBSelected.isUmPalet());
						articol.setFilialaSite(CreareComandaGed.filialaAlternativa);
						articol.setLungime(articolDBSelected.getLungime());
						articol.setListCabluri(listCabluri);
						articol.setPretFaraTva(selectedArticol.getPretFaraTva());
						articol.setGreutate(selectedArticol.getGreutate());

						if (procRedFin > 0)
							articol.setIstoricPret(selectedArticol.getIstoricPret());

						ListaArticoleComandaGed listaArticole = ListaArticoleComandaGed.getInstance();
						listaArticole.addArticolComanda(articol);

						textNumeArticol.setText("");
						textCodArticol.setText("");
						textUM.setText("");
						textStoc.setText("");
						textCant.setText("");
						textPromo.setText("");

						numeArticol = "";
						codArticol = "";
						tipArticol = "";
						umStoc = "";
						globalCodDepartSelectetItem = "";

						localUnitMas = "";
						procDiscClient = 0;
						initPrice = 0;
						finalPrice = 0;
						valArticol = 0;
						globalCantArt = 0;

						cmpArt = 0;
						subCmp = "0";

						valoareUmrez = 1;
						valoareUmren = 1;

						listCabluri = null;

						redBtnTable.setVisibility(View.GONE);
						labelStoc.setVisibility(View.GONE);
						labelCant.setVisibility(View.GONE);
						textCant.setVisibility(View.GONE);
						pretBtn.setVisibility(View.GONE);
						spinnerUnitMas.setVisibility(View.GONE);
						layoutStocKA.setVisibility(View.GONE);

						resultLayout.setVisibility(View.INVISIBLE);

						if (!tglProc.isChecked())
							tglProc.performClick();

						InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.hideSoftInputFromWindow(textCant.getWindowToken(), 0);

					} else {

						Toast toast = Toast.makeText(getApplicationContext(), "Articolul nu are pret definit!", Toast.LENGTH_SHORT);
						toast.show();
					}

					textProcRed.setText("");

				} catch (Exception e) {

					Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		});

	}

	private boolean isConditieCabluri05BV90() {
		return articolDBSelected.getDepart().equals("05") && CreareComandaGed.filialaAlternativa.equals("BV90");
	}

	private void getCabluri05(String codArticol) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codArticol", codArticol);
		params.put("sinteticArticol", articolDBSelected.getSintetic());
		opArticol.getCabluri05(params);

	}

	private void afisCabluri05(List<BeanCablu05> listCabluri) {

		if (listCabluri.isEmpty()) {
			this.listCabluri = listCabluri;
			saveArtBtn.performClick();
			return;
		}

		int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
		int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);

		Cabluri05Dialog cabluriDialog = new Cabluri05Dialog(SelectArtCmdGed.this, listCabluri, textCant.getText().toString().trim());
		cabluriDialog.getWindow().setLayout(width, height);
		cabluriDialog.setCabluSelectedListener(this);
		cabluriDialog.show();

	}

	private boolean conditiiCmdIP() {

		if (CreareComandaGed.filialaAlternativa.equals("BV90")) {

			if (!globalCodDepartSelectetItem.equals("01") && !globalCodDepartSelectetItem.equals("02") && !globalCodDepartSelectetItem.equals("05")) {
				Toast.makeText(getApplicationContext(), "Din BV90 adaugati doar articole din departamentele 01, 02 sau 05.", Toast.LENGTH_LONG)
						.show();
				return false;
			}

			if (!DateLivrare.getInstance().getTransport().equals("TCLI") && !DateLivrare.getInstance().getTransport().equals("TERT")) {
				Toast.makeText(getApplicationContext(), "Din BV90 selectati doar transport TCLI sau TERT.", Toast.LENGTH_LONG).show();
				return false;
			}

			if (!globalDepozSel.equals("02V1") && !globalDepozSel.equals("05V1") && !globalDepozSel.equals("92V1") && !globalDepozSel.equals("95V1")) {
				Toast.makeText(getApplicationContext(), "Din BV90 adaugati doar articole din depozitele  02V1, 05V1, 92V1, 95V1.", Toast.LENGTH_LONG)
						.show();
				return false;
			}

		}

		return true;

	}

	private boolean isComandaDL() {
		return DateLivrare.getInstance().getFurnizorComanda() != null && DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa() != null;
	}

	@SuppressWarnings("unchecked")
	private void listArtStoc(String pretResponse) {

		if (!pretResponse.equals("-1")) {

			NumberFormat nf2 = NumberFormat.getInstance(new Locale("en", "US"));

			nf2.setMinimumFractionDigits(3);
			nf2.setMaximumFractionDigits(3);

			resultLayout.setVisibility(View.VISIBLE);

			String[] tokenPret = pretResponse.split("#");

			textNumeArticol.setVisibility(View.VISIBLE);
			textCodArticol.setVisibility(View.VISIBLE);
			textUM.setVisibility(View.VISIBLE);
			textStoc.setVisibility(View.VISIBLE);
			textCant.setVisibility(View.VISIBLE);
			labelCant.setVisibility(View.VISIBLE);
			labelStoc.setVisibility(View.VISIBLE);
			pretBtn.setVisibility(View.VISIBLE);

			textUM.setText(tokenPret[1]);
			textStoc.setText(nf2.format(Double.valueOf(tokenPret[0])));

			umStoc = tokenPret[1];

			artMap = (HashMap<String, String>) spinnerUnitMas.getSelectedItem();

			String stocUM = artMap.get("rowText");

			if (!stocUM.equals(tokenPret[1]) && !tokenPret[1].trim().equals("")) // um
																					// vanz
																					// si
																					// um
																					// vanz
																					// difera
			{
				HashMap<String, String> tempUmVanz;
				tempUmVanz = new HashMap<String, String>();
				tempUmVanz.put("rowText", tokenPret[1]);

				listUmVanz.add(0, tempUmVanz);

				spinnerUnitMas.setAdapter(adapterUmVanz);
				spinnerUnitMas.setVisibility(View.VISIBLE);
			}

		} else {

			Toast.makeText(getApplicationContext(), "Nu exista informatii.", Toast.LENGTH_SHORT).show();

			textUM.setText("");
			textStoc.setText("");
		}

	}

	private void listPretArticol(PretArticolGed pretArticol) {

		selectedArticol = pretArticol;

		NumberFormat nf2 = NumberFormat.getInstance(new Locale("en", "US"));
		if (CreareComandaGed.tipClient.equals("IP")) {
			nf2.setMinimumFractionDigits(2);
			nf2.setMaximumFractionDigits(2);
		} else {
			nf2.setMinimumFractionDigits(3);
			nf2.setMaximumFractionDigits(3);
		}
		nf2.setGroupingUsed(false);

		codPromo = "-1";
		procDiscClient = 0;
		saveArtBtn.setVisibility(View.VISIBLE);
		textPromo.setText("");

		valMultiplu = Double.valueOf(pretArticol.getMultiplu());
		coefCorectie = pretArticol.getCoefCorectie();

		textMultipluArt.setText("Unit.pret: " + pretArticol.getMultiplu() + " " + umStoc);

		globalCantArt = Double.valueOf(pretArticol.getCantitateUmBaza());
		cantUmb = pretArticol.getCantitateUmBaza();
		Umb = pretArticol.getUmBaza();
		cmpArt = Double.valueOf(pretArticol.getCmp());
		pretMediuDistrib = Double.valueOf(pretArticol.getPretMediu());
		adaosMediuDistrib = Double.valueOf(pretArticol.getAdaosMediu());

		initPrice = Double.valueOf(pretArticol.getPret());
		listPrice = Double.valueOf(pretArticol.getPretLista());

		afisIstoricPret(pretArticol.getIstoricPret());

		selectedArticol.setIstoricPret(UtilsFormatting.getIstoricPret(pretArticol.getIstoricPret(), EnumTipComanda.GED));

		txtImpachetare.setText(pretArticol.getImpachetare());

		if (!pretArticol.getErrMsg().isEmpty()) {
			Toast.makeText(getApplicationContext(), pretArticol.getErrMsg(), Toast.LENGTH_LONG).show();
			return;
		}

		if (globalDepozSel.substring(2, 3).equals("V")) {
			if (initPrice / globalCantArt * valMultiplu < cmpArt && !UtilsArticole.isArticolPermitSubCmp(codArticol)) {
				Toast.makeText(getApplicationContext(), "Pret sub cmp!", Toast.LENGTH_LONG).show();
				// eliminat pentru vanzare promotii 03
				// return;
			}

		}

		String[] condPret = pretArticol.getConditiiPret().split(";");

		int ii = 0;
		String[] tokPret;
		String stringCondPret = "";
		Double valCondPret = 0.0;

		// ZSTA taxa verde
		for (ii = 0; ii < condPret.length; ii++) {
			tokPret = condPret[ii].split(":");
			valCondPret = Double.valueOf(tokPret[1].replace(',', '.').trim());
			if (valCondPret != 0 && tokPret[0].equalsIgnoreCase("ZSTA")) {
				stringCondPret += "Taxa verde" + addSpace(15 - "Taxa verde".length()) + ":"
						+ addSpace(10 - String.valueOf(nf2.format(valCondPret)).length()) + nf2.format(valCondPret)
						+ System.getProperty("line.separator");

			}

		}

		textCondPret.setVisibility(View.VISIBLE);
		textCondPret.setText(stringCondPret);

		finalPrice = initPrice;
		textProcRed.setText("");

		pretVanzare = initPrice;

		redBtnTable.setVisibility(View.VISIBLE);
		textProcRed.setVisibility(View.VISIBLE);

		textPretGED.setVisibility(View.VISIBLE);
		textMultipluArt.setVisibility(View.VISIBLE);

		labelFactConv.setVisibility(View.INVISIBLE);

		txtPretArt.setText(nf2.format((initPrice / globalCantArt) * valMultiplu));
		txtPretArt.setHint(nf2.format((initPrice / globalCantArt) * valMultiplu));

		if (CreareComandaGed.tipClient.equals("IP"))
			((TextView) findViewById(R.id.labelPretGED)).setText("Pret GED fara tva: ");

		textPretGED.setText(String.valueOf(nf2.format((initPrice / globalCantArt) * valMultiplu)));
		infoArticol = pretArticol.getConditiiPret().replace(',', '.');

		afisPretUmAlternativa();

		procDisc.setText(nf2.format(procDiscClient));

		discMaxAV = pretArticol.getDiscMaxAV();
		discMaxSD = pretArticol.getDiscMaxSD();

		procentTVA = getProcentTVA(pretArticol);
		double pretUnitar = (initPrice / globalCantArt) * valMultiplu;
		double valoareFaraTva = pretUnitar / procentTVA;

		NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		textPretGEDFTva.setText(nf.format(valoareFaraTva));

		NumberFormat nForm2 = NumberFormat.getInstance(new Locale("en", "US"));
		nForm2.setMinimumFractionDigits(2);
		nForm2.setMaximumFractionDigits(2);

		procentTransport = pretArticol.getProcTransport();
		valoareTransport = pretArticol.getValTrap();
		double pretTransport = (initPrice) * (pretArticol.getProcTransport() / 100) + pretArticol.getValTrap();
		textTransport.setText(nForm2.format(pretTransport));

		// agentii nu pot modifica pretul
		if (userCannotModifyPrice()) {
			txtPretArt.setEnabled(false);
			textProcRed.setFocusable(false);
			tglProc.setEnabled(false);
		} else {
			txtPretArt.setEnabled(true);
			textProcRed.setFocusableInTouchMode(true);
			tglProc.setEnabled(true);
		}

		// pentru totaluri negociate nu se modifica preturi
		if (totalNegociat) {
			textProcRed.setFocusable(false);
			tglProc.setEnabled(false);
		}

		// se afiseaza direct pretul si nu procentul
		tglProc.setChecked(false);
		tglProc.performClick();

		if (pretArticol.getFaraDiscount().toUpperCase().equals("X"))
			codPromo = "1";

		// nu se acorda discounturi
		if (hasArticolDiscount(pretArticol)) {
			txtPretArt.setEnabled(false);
			textProcRed.setFocusable(false);
			tglProc.setEnabled(false);

			if (Double.parseDouble(pretArticol.getCantitateArticolPromo()) != 0) {

				artPromoText = "";
				textPromo.setVisibility(View.VISIBLE);
				textPromo.setText("Articol cu promotie!");

				double pret1 = (Double.parseDouble(pretArticol.getPret()) / Double.parseDouble(pretArticol.getCantitate())) * valMultiplu;
				double pret2 = (Double.parseDouble(pretArticol.getPretArticolPromo()) / Double.parseDouble(pretArticol.getCantitateArticolPromo()))
						* valMultiplu;

				artPromoText = "Din cantitatea comandata " + pretArticol.getCantitate() + " " + pretArticol.getUm() + " au pretul de "
						+ nf2.format(pret1) + " RON/" + pretArticol.getUm() + " si " + pretArticol.getCantitateArticolPromo() + " "
						+ pretArticol.getUmArticolPromo() + " au pretul de " + nf2.format(pret2) + " RON/" + pretArticol.getUmArticolPromo() + ".";
			}

		} else {

			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(textProcRed, InputMethodManager.SHOW_IMPLICIT);

			// verificare articole promotii
			if (Double.parseDouble(pretArticol.getCantitateArticolPromo()) != 0) {
				artPromoText = "";
				textPromo.setVisibility(View.VISIBLE);

				// articolul din promotie are alt pret
				if (Double.parseDouble(pretArticol.getPretArticolPromo()) != 0) {

				} else // articolul din promotie este gratuit
				{
					codPromo = "2";

					// verificare cantitati articole gratuite
					// cant. art promotie se adauga la cant. ceruta
					if (Double.parseDouble(textCant.getText().toString().trim()) == Double.parseDouble(pretArticol.getCantitate())) {

						// verificare cod articol promotie
						// art. promo = art. din comanda
						if (codArticol.equals(pretArticol.getCodArticolPromo())) {
							artPromoText = pretArticol.getCantitateArticolPromo() + " " + pretArticol.getUmArticolPromo() + " x " + numeArticol
									+ " gratuit. ";
						} else// art. promo diferit de art. din cmd.
						{
							artPromoText = pretArticol.getCantitateArticolPromo() + " " + pretArticol.getUmArticolPromo() + " x "
									+ pretArticol.getCodArticolPromo() + " gratuit. ";

						}

					} else // cant art. promotie se scade din cant.
							// ceruta
					{

						artPromoText = "Din cantitatea comandata " + pretArticol.getCantitateArticolPromo() + " " + pretArticol.getUmArticolPromo()
								+ " sunt gratis.";

					}

					textPromo.setText("Articol cu promotie");

				}

			}

		}

	}

	private void afisPretUmAlternativa() {

		if (valoareUmrez / valoareUmren != 1) {

			double pretUmAlt;

			if (pretMod) {

				if (textProcRed.getText().toString().trim().isEmpty())
					return;

				pretUmAlt = Double.parseDouble(textProcRed.getText().toString()) * valoareUmrez / valoareUmren;
			} else {

				if (txtPretArt.getText().toString().trim().isEmpty())
					return;

				pretUmAlt = Double.parseDouble(txtPretArt.getText().toString()) * valoareUmrez / valoareUmren;
			}

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
			((TextView) findViewById(R.id.txtPretUmAlt)).setText("1 " + selectedUnitMas + " = " + nf.format(pretUmAlt) + " RON");
		} else
			((TextView) findViewById(R.id.txtPretUmAlt)).setText("");

	}

	private String addSpace(int nrCars) {
		String retVal = "";

		for (int i = 0; i < nrCars; i++)
			retVal += " ";

		return retVal;
	}

	private void afisIstoricPret(String infoIstoric) {
		LinearLayout layoutIstoric1 = (LinearLayout) findViewById(R.id.layoutIstoricPret1);
		LinearLayout layoutIstoric2 = (LinearLayout) findViewById(R.id.layoutIstoricPret2);
		LinearLayout layoutIstoric3 = (LinearLayout) findViewById(R.id.layoutIstoricPret3);

		layoutIstoric1.setVisibility(View.GONE);
		layoutIstoric2.setVisibility(View.GONE);
		layoutIstoric3.setVisibility(View.GONE);

		DecimalFormat df = new DecimalFormat("#0.00");

		double valoarePret = 0;

		if (infoIstoric.contains(":")) {
			String[] arrayIstoric = infoIstoric.split(":");

			if (arrayIstoric.length > 0 && arrayIstoric[0].contains("@")) {

				layoutIstoric1.setVisibility(View.VISIBLE);

				String[] arrayPret = arrayIstoric[0].split("@");

				if (isComandaIP())
					valoarePret = Double.parseDouble(arrayPret[0]);
				else
					valoarePret = Double.parseDouble(arrayPret[0]) * Constants.TVA;

				TextView textIstoric1 = (TextView) findViewById(R.id.txtIstoricPret1);
				textIstoric1.setText(" " + df.format(valoarePret) + UtilsFormatting.addSpace(arrayPret[0].trim(), 6) + " /" + arrayPret[1] + " "
						+ arrayPret[2] + " - " + UtilsFormatting.getMonthNameFromDate(arrayPret[3], 2));

			}

			if (arrayIstoric.length > 1 && arrayIstoric[1].contains("@")) {

				layoutIstoric2.setVisibility(View.VISIBLE);

				String[] arrayPret = arrayIstoric[1].split("@");

				if (isComandaIP())
					valoarePret = Double.parseDouble(arrayPret[0]);
				else
					valoarePret = Double.parseDouble(arrayPret[0]) * Constants.TVA;

				TextView textIstoric2 = (TextView) findViewById(R.id.txtIstoricPret2);
				textIstoric2.setText(df.format(valoarePret) + UtilsFormatting.addSpace(arrayPret[0].trim(), 6) + " /" + arrayPret[1] + " "
						+ arrayPret[2] + " - " + UtilsFormatting.getMonthNameFromDate(arrayPret[3], 2));

			}

			if (arrayIstoric.length > 2 && arrayIstoric[2].contains("@")) {

				layoutIstoric3.setVisibility(View.VISIBLE);

				String[] arrayPret = arrayIstoric[2].split("@");

				if (isComandaIP())
					valoarePret = Double.parseDouble(arrayPret[0]);
				else
					valoarePret = Double.parseDouble(arrayPret[0]) * Constants.TVA;

				TextView textIstoric3 = (TextView) findViewById(R.id.txtIstoricPret3);
				textIstoric3.setText(df.format(valoarePret) + UtilsFormatting.addSpace(arrayPret[0].trim(), 6) + " /" + arrayPret[1] + " "
						+ arrayPret[2] + " - " + UtilsFormatting.getMonthNameFromDate(arrayPret[3], 2));

			}

		}

	}

	private static boolean isComandaIP() {
		return UtilsUser.isUserIP()
				&& (CreareComandaGed.tipClientIP == EnumTipClientIP.CONSTR || CreareComandaGed.tipClientIP == EnumTipClientIP.NONCONSTR);
	}

	private boolean userCannotModifyPrice() {
		return (UserInfo.getInstance().getTipUserSap().equals("CONS-GED") || UtilsUser.isSSCM() || UtilsUser.isCGED())
				&& !UtilsComenzi.isComandaInstPublica();
	}

	private double getProcentTVA(PretArticolGed pretArticol) {
		double procent = 0;
		double valMWSI = 0;
		if (pretArticol.getConditiiPret().contains(";")) {

			String[] arrayInfo = pretArticol.getConditiiPret().split(";");
			String[] tokValue;

			for (int i = 0; i < arrayInfo.length; i++) {
				if (arrayInfo[i].toUpperCase().contains("MWSI")) {
					tokValue = arrayInfo[i].split(":");
					valMWSI = Double.valueOf(tokValue[1].replace(',', '.'));
					break;
				}

			}

		}

		if (valMWSI == 0)
			procent = 0;
		else
			procent = Constants.TVA;

		return procent;
	}

	private boolean hasArticolDiscount(PretArticolGed pretArticol) {
		return pretArticol.getFaraDiscount().toUpperCase().equals("X");

	}

	public void showPromoWindow(String promoString) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(promoString).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).setTitle("Promotie!").setIcon(R.drawable.promotie96);

		AlertDialog alert = builder.create();
		alert.show();

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		ArticolDB articol = (ArticolDB) l.getAdapter().getItem(position);

		articolDBSelected = articol;

		numeArticol = articol.getNume();
		codArticol = articol.getCod();

		String umVanz = articol.getUmVanz();

		tipArticol = articol.getTipAB();

		if (!tipArticol.trim().equals(""))
			numeArticol += " (" + tipArticol + ")";
		else
			tipArticol = " ";

		globalCodDepartSelectetItem = articol.getDepart();

		textNumeArticol.setText(numeArticol);
		textCodArticol.setText(codArticol);

		textUM.setText("");
		textStoc.setText("");
		textCant.setText("");

		valoareUmrez = 1;
		valoareUmren = 1;

		listUmVanz.clear();
		spinnerUnitMas.setVisibility(View.GONE);
		HashMap<String, String> tempUmVanz;
		tempUmVanz = new HashMap<String, String>();
		tempUmVanz.put("rowText", umVanz);

		listUmVanz.add(tempUmVanz);
		spinnerUnitMas.setAdapter(adapterUmVanz);

		textNumeArticol.setVisibility(View.INVISIBLE);
		textCodArticol.setVisibility(View.INVISIBLE);
		saveArtBtn.setVisibility(View.GONE);

		redBtnTable.setVisibility(View.GONE);

		try {
			String[] tokenDep = spinnerDepoz.getSelectedItem().toString().split("-");

			if (tokenDep[0].trim().length() == 2)
				globalDepozSel = globalCodDepartSelectetItem.substring(0, 2) + tokenDep[0].trim();
			else
				globalDepozSel = tokenDep[0].trim();

			performListArtStoc();

		} catch (Exception ex) {
			Log.e("Error", ex.toString());
			Toast.makeText(getApplicationContext(), "Eroare!", Toast.LENGTH_SHORT).show();

		}

	}

	private String getFilialaLivrareCVIP() {

		String filialaLivrare;

		if (!isComandaClp())
			filialaLivrare = UserInfo.getInstance().getUnitLog();
		else
			filialaLivrare = DateLivrare.getInstance().getCodFilialaCLP();

		return filialaLivrare;

	}

	private boolean isComandaClp() {
		return !DateLivrare.getInstance().getCodFilialaCLP().trim().isEmpty() && DateLivrare.getInstance().getCodFilialaCLP().trim().length() == 4;
	}

	private void performListArtStoc() {

		HashMap<String, String> params = new HashMap<String, String>();

		String varLocalUnitLog = "";
		if (codArticol.length() == 8)
			codArticol = "0000000000" + codArticol;

		if (CreareComandaGed.tipComandaGed == TipCmdGed.COMANDA_LIVRARE) {
			if (globalDepozSel.equals("MAV1"))
				varLocalUnitLog = DateLivrare.getInstance().getCodFilialaCLP().substring(0, 2) + "2"
						+ DateLivrare.getInstance().getCodFilialaCLP().substring(3, 4);
			else
				varLocalUnitLog = DateLivrare.getInstance().getCodFilialaCLP();
		} else {

			if (globalDepozSel.equals("MAV1") || globalDepozSel.equals("MAV2")) {
				if (CreareComandaGed.filialaAlternativa.equals("BV90"))
					varLocalUnitLog = "BV92";
				else
					varLocalUnitLog = filialaAlternativa.substring(0, 2) + "2" + filialaAlternativa.substring(3, 4);
			} else if (globalDepozSel.equals("WOOD")) {
				varLocalUnitLog = filialaAlternativa.substring(0, 2) + "4" + filialaAlternativa.substring(3, 4);
			} else {

				if (CreareComandaGed.filialaAlternativa.equals("BV90"))
					varLocalUnitLog = CreareComandaGed.filialaAlternativa;
				else
					varLocalUnitLog = filialaAlternativa.substring(0, 2) + "1" + filialaAlternativa.substring(3, 4);
			}

			if (UtilsUser.isUserSite()) {
				varLocalUnitLog = UtilsUser.getULUserSite(CreareComandaGed.filialaAlternativa, globalDepozSel);
			}

		}

		if (articolDBSelected.getDepart().equals("11") && globalDepozSel.startsWith("11")) {
			Toast.makeText(getApplicationContext(), "Pentru articole din divizia 11 selectati un depozit aferent.", Toast.LENGTH_LONG).show();
			return;
		}

		params.put("codArt", codArticol);
		params.put("filiala", varLocalUnitLog);
		params.put("depozit", globalDepozSel);
		params.put("depart", UserInfo.getInstance().getCodDepart());

		opArticol.getStocDepozit(params);

	}

	private void loadFactorConversie(String result) {
		String[] convResult = result.split("#");

		valoareUmrez = Integer.parseInt(convResult[0]);
		valoareUmren = Integer.parseInt(convResult[1]);

	}

	public void onBackPressed() {
		finish();
		return;
	}

	public void operationComplete(EnumArticoleDAO methodName, Object result) {
		switch (methodName) {
		case GET_ARTICOLE_DISTRIBUTIE:
		case GET_ARTICOLE_FURNIZOR:
		case GET_ARTICOLE_ACZC:
			populateListViewArticol(opArticol.deserializeArticoleVanzare((String) result));
			break;
		case GET_STOC_DEPOZIT:
			listArtStoc((String) result);
			break;
		case GET_PRET_GED_JSON:
			listPretArticol(opArticol.deserializePretGed(result));
			break;
		case GET_FACTOR_CONVERSIE:
			loadFactorConversie((String) result);
			break;
		case GET_CABLURI_05:
			afisCabluri05(opArticol.deserializeCabluri05((String) result));
			break;
		default:
			break;

		}

	}

	@Override
	public void magazinMathausSelected(String filialaMathaus) {
		String filialaLivrare = getFilialaLivrareCVIP();

		if (!filialaLivrare.equals(filialaMathaus)) {
			CreareComandaGed.tipComandaGed = TipCmdGed.COMANDA_LIVRARE;
			DateLivrare.getInstance().setTipComandaGed(TipCmdGed.COMANDA_LIVRARE);
			DateLivrare.getInstance().setCodFilialaCLP(filialaMathaus);
		} else {
			CreareComandaGed.tipComandaGed = TipCmdGed.COMANDA_VANZARE;
			DateLivrare.getInstance().setTipComandaGed(TipCmdGed.COMANDA_VANZARE);
			DateLivrare.getInstance().setCodFilialaCLP("");
		}

		CreareComandaGed.permitArticoleDistribIP = false;
		ModificareComanda.permitArticoleDistribIP = false;

	}

	@Override
	public void cabluriSelected(List<BeanCablu05> listCabluri) {
		this.listCabluri = listCabluri;
		saveArtBtn.performClick();

	}

}
