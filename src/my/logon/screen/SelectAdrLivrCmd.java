/**
 * @author florinb
 *
 */
package my.logon.screen;

import helpers.HelperAdreseLivrare;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import listeners.AsyncTaskListener;
import listeners.AutocompleteDialogListener;
import listeners.MapListener;
import listeners.ObiectiveListener;
import listeners.OperatiiAdresaListener;
import main.ZoneBucuresti;
import model.ArticolComanda;
import model.Constants;
import model.DateLivrare;
import model.HandleJSONData;
import model.ListaArticoleComanda;
import model.OperatiiAdresa;
import model.OperatiiAdresaImpl;
import model.OperatiiObiective;
import model.UserInfo;
import utils.Exceptions;
import utils.MapUtils;
import utils.UtilsAddress;
import utils.UtilsComenzi;
import utils.UtilsDates;
import utils.UtilsGeneral;
import utils.UtilsUser;
import adapters.AdapterAdreseLivrare;
import adapters.AdapterObiective;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import beans.Address;
import beans.BeanAdresaLivrare;
import beans.BeanAdreseJudet;
import beans.BeanLocalitate;
import beans.BeanObiectivDepartament;
import beans.Delegat;
import beans.GeocodeAddress;
import beans.StatusIntervalLivrare;

import com.google.android.gms.maps.model.LatLng;

import dialogs.MapAddressDialog;
import dialogs.SelectDateDialog;
import enums.EnumFiliale;
import enums.EnumFilialeLivrare;
import enums.EnumJudete;
import enums.EnumLocalitate;
import enums.EnumOperatiiAdresa;
import enums.EnumOperatiiObiective;
import enums.EnumZona;
import enums.TipCmdDistrib;

public class SelectAdrLivrCmd extends Activity implements OnTouchListener, OnItemClickListener, OperatiiAdresaListener, ObiectiveListener,
		MapListener, AutocompleteDialogListener, AsyncTaskListener {

	private Button saveAdrLivrBtn;
	private EditText txtPers, txtTel, txtObservatii, txtValoareIncasare;

	private static final String METHOD_NAME = "getClientJud";
	int posJudetSel = 0;

	private String[] tipPlataContract = { "LC - Limita credit", "N - Numerar in filiala", "OPA - OP avans", "R - ramburs" };
	private String[] tipPlataClBlocat = { "OPA - OP avans", "R - ramburs", "N - Numerar in filiala" };
	private String[] tipPlataRest = { "OPA - OP avans", "R - ramburs", "N - Numerar in filiala" };

	private String[] tipTransport = { "TRAP - Transport Arabesque", "TCLI - Transport client", "TFRN - Transport furnizor" };

	String[] tipResponsabil = { "AV - Agent vanzari", "SO - Sofer", "OF - Operator facturare" };

	String[] docInsot = { "Factura", "Aviz de expeditie" };

	public SimpleAdapter adapterJudete, adapterAdreseLivrare;

	private Spinner spinnerPlata, spinnerTransp, spinnerJudet, spinnerTermenPlata, spinnerAdreseLivrare, spinnerTipReducere, spinnerResponsabil;
	private static ArrayList<HashMap<String, String>> listJudete = null, listAdreseLivrare = null;
	private ArrayAdapter<String> adapterDataLivrare, adapterTermenPlata, adapterResponsabil;
	private LinearLayout layoutAdrese, layoutAdr1, layoutAdr2, layoutValoareIncasare;
	private String globalCodClient = "";

	private RadioButton radioLista, radioText, radioObiectiv;
	private boolean adrNouaModifCmd = false;
	private int selectedAddrModifCmd = -1;
	private CheckBox checkMacara, chkbClientLaRaft;
	private Spinner spinnerObiective;
	private LinearLayout layoutObiective;

	private ListPopupWindow lpw;
	private String[] listPersCont, listTel;
	CheckBox checkModifValInc;
	NumberFormat nf2;
	private OperatiiAdresa operatiiAdresa;
	private OperatiiObiective operatiiObiective;
	private BeanObiectivDepartament obiectivSelectat;
	private AutoCompleteTextView textLocalitate, textStrada;
	private Button btnPozitieAdresa;
	private EditText textNrStr;
	private Spinner spinnerTonaj, spinnerIndoire;
	private ArrayList<BeanAdresaLivrare> adreseList;
	private LinearLayout layoutPrelucrare04;

	private LinearLayout layoutHarta;

	private static final int LIST_LOCALITATI = 1;
	private static final int LIST_ADRESE = 2;
	private TextView textDataLivrare;
	private Button btnDataLivrare;
	private CheckBox checkFactPaleti;
	private CheckBox chkCamionDescoperit;
	private CheckBox checkObsSofer;
	private TextView txtBlocScara;
	private ArrayAdapter<String> adapterSpinnerTransp;
	private List<String> listLocalitatiLivrare;

	private CheckBox checkFactura, checkAviz;
	private BeanAdreseJudet listAdreseJudet;
	private BeanAdresaLivrare adresaLivrareSelected;
	private Spinner spinnerFilialeTCLI;
	private boolean isAdresaLivrareTCLI;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.selectadrlivrcmdheader);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Date livrare");
		actionBar.setDisplayHomeAsUpEnabled(true);

		operatiiAdresa = new OperatiiAdresaImpl(this);
		operatiiAdresa.setOperatiiAdresaListener(this);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null && bundle.getString("parrentClass") != null && bundle.getString("parrentClass").equals("ModificareComanda")) {

			if ((Double.parseDouble(bundle.getString("limitaCredit")) > 1) && !bundle.getString("termenPlata").equals("C000")) {

				if (!DateLivrare.getInstance().getTipPlata().equals("N") && !DateLivrare.getInstance().getTipPlata().equals("R")) {
					CreareComanda.tipPlataContract = bundle.getString("tipPlataContract");
					DateLivrare.getInstance().setTipPlata("LC");
				}
			}

		}

		this.saveAdrLivrBtn = (Button) findViewById(R.id.saveAdrLivrBtn);
		addListenerSaveAdr();

		this.layoutAdrese = (LinearLayout) findViewById(R.id.layoutListAdrese);
		layoutAdrese.setVisibility(View.GONE);

		this.layoutAdr1 = (LinearLayout) findViewById(R.id.layoutAdr1);
		this.layoutAdr2 = (LinearLayout) findViewById(R.id.layoutAdr2);

		layoutHarta = (LinearLayout) findViewById(R.id.layoutHarta);
		layoutHarta.setVisibility(View.GONE);

		textLocalitate = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocalitate);
		textLocalitate.setVisibility(View.INVISIBLE);

		textNrStr = (EditText) findViewById(R.id.textNrStr);

		btnPozitieAdresa = (Button) findViewById(R.id.btnPozitieAdresa);
		setListnerBtnPozitieAdresa();

		textStrada = (AutoCompleteTextView) findViewById(R.id.autoCompleteStrada);

		txtPers = (EditText) findViewById(R.id.txtPersCont);
		txtTel = (EditText) findViewById(R.id.txtTelefon);

		listPersCont = DateLivrare.getInstance().getPersContact().split(":");
		listTel = DateLivrare.getInstance().getNrTel().split(":");

		lpw = new ListPopupWindow(this);
		lpw.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listPersCont));
		lpw.setAnchorView(txtPers);
		lpw.setModal(true);
		lpw.setOnItemClickListener(this);

		txtPers.setText(listPersCont[0]);
		txtPers.setOnTouchListener(this);

		layoutValoareIncasare = (LinearLayout) findViewById(R.id.layoutValoareIncasare);
		layoutValoareIncasare.setVisibility(View.GONE);

		txtValoareIncasare = (EditText) findViewById(R.id.txtValoareIncasare);
		txtValoareIncasare.setFocusable(false);

		checkModifValInc = (CheckBox) findViewById(R.id.checkboxModifValInc);
		addListenerModifValInc();

		nf2 = NumberFormat.getInstance();
		nf2.setMinimumFractionDigits(2);
		nf2.setMaximumFractionDigits(2);

		if (DateLivrare.getInstance().isValIncModif()) {
			txtValoareIncasare.setText(DateLivrare.getInstance().getValoareIncasare());
			checkModifValInc.setChecked(true);
		} else {

			double localValCmd = 0;
			if (!CreareComanda.codClientVar.equals(""))
				localValCmd = CreareComanda.totalComanda * Constants.TVA;
			else
				localValCmd = ModificareComanda.totalComanda * Constants.TVA;

			txtValoareIncasare.setText(nf2.format(localValCmd));

			checkModifValInc.setChecked(false);
		}

		txtTel.setText(listTel[0]);

		checkObsSofer = (CheckBox) findViewById(R.id.chkObsSofer);
		setListenerCheckObsSofer();
		txtObservatii = (EditText) findViewById(R.id.txtObservatii);
		if (DateLivrare.getInstance().getObsLivrare() != null && !DateLivrare.getInstance().getObsLivrare().trim().isEmpty()) {
			txtObservatii.setText(DateLivrare.getInstance().getObsLivrare());
			checkObsSofer.setChecked(true);
		}

		chkbClientLaRaft = (CheckBox) findViewById(R.id.clientRaft);
		addListenerClientLaRaft();

		CreareComanda.dateLivrare = "";

		spinnerResponsabil = (Spinner) findViewById(R.id.spinnerResponsabil);
		adapterResponsabil = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipResponsabil);
		adapterResponsabil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		addListenerResponsabil();
		spinnerResponsabil.setAdapter(adapterResponsabil);

		for (int ii = 0; ii < adapterResponsabil.getCount(); ii++) {
			if (adapterResponsabil.getItem(ii).toString().split("-")[0].trim().equals(DateLivrare.getInstance().getObsPlata())) {
				spinnerResponsabil.setSelection(ii);
				break;
			}
		}

		spinnerPlata = (Spinner) findViewById(R.id.spinnerPlata);

		ArrayAdapter<String> adapterSpinnerPlata;

		if (DateLivrare.getInstance().isClientBlocat())
			adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataClBlocat);
		else if (!CreareComanda.tipPlataContract.trim().isEmpty() || DateLivrare.getInstance().getTipPlata().equals("LC")) {

			if (!CreareComanda.tipPlataContract.trim().isEmpty()) {

				if (!DateLivrare.getInstance().getTipPlata().equals("N") && !DateLivrare.getInstance().getTipPlata().equals("R")) {
					((TextView) findViewById(R.id.tipPlataContract)).setText("Contract: " + CreareComanda.tipPlataContract);
					DateLivrare.getInstance().setTipPlata("LC");
				}
			}
			adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataContract);
		} else
			adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataRest);

		if (isComandaDl()) {
			List<String> metsPlata = new ArrayList<String>();

			for (int ii = 0; ii < adapterSpinnerPlata.getCount(); ii++)
				if (!adapterSpinnerPlata.getItem(ii).toString().startsWith("R"))
					metsPlata.add(adapterSpinnerPlata.getItem(ii));

			adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, metsPlata.toArray(new String[0]));

		}

		adapterSpinnerPlata.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPlata.setAdapter(adapterSpinnerPlata);
		addListenerTipPlata();

		if (HelperAdreseLivrare.isConditiiCurierRapid()) {

			if (HelperAdreseLivrare.getLocalitatiAcceptate() == null) {
				OperatiiAdresaImpl opAdr = new OperatiiAdresaImpl(this);
				opAdr.setOperatiiAdresaListener(this);
				opAdr.getLocalitatiLivrareRapida();
			}

			tipTransport = HelperAdreseLivrare.adaugaTransportCurierRapid(tipTransport);
		}

		spinnerTransp = (Spinner) findViewById(R.id.spinnerTransp);
		adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipTransport);

		if (isComandaACZC()) {
			List<String> itemsTransp = new ArrayList<String>();
			for (int ii = 0; ii < adapterSpinnerTransp.getCount(); ii++) {
				if (!adapterSpinnerTransp.getItem(ii).startsWith("TFRN")) {
					itemsTransp.add((String) adapterSpinnerTransp.getItem(ii));
				}
			}
			adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsTransp);
		}

		adapterSpinnerTransp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTransp.setAdapter(adapterSpinnerTransp);
		spinnerFilialeTCLI = (Spinner) findViewById(R.id.spinnerFiliale);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
				EnumFilialeLivrare.getFiliale());
		spinnerFilialeTCLI.setAdapter(adapter);
		setSpinnerFilialeTCLIListener();
		addSpinnerTranspListener();
		setTipTranspInfoAgent();
		setFilialaLivrareTCLI();

		spinnerJudet = (Spinner) findViewById(R.id.spinnerJudet);
		spinnerJudet.setOnItemSelectedListener(new regionSelectedListener());

		listJudete = new ArrayList<HashMap<String, String>>();
		adapterJudete = new SimpleAdapter(this, listJudete, R.layout.rowlayoutjudete, new String[] { "numeJudet", "codJudet" }, new int[] {
				R.id.textNumeJudet, R.id.textCodJudet });

		spinnerTermenPlata = (Spinner) findViewById(R.id.spinnerTermenPlata);
		adapterTermenPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapterTermenPlata.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTermenPlata.setAdapter(adapterTermenPlata);

		spinnerAdreseLivrare = (Spinner) findViewById(R.id.spinnerAdreseLivrare);

		setListenerSpinnerAdreseLivrare();

		listAdreseLivrare = new ArrayList<HashMap<String, String>>();
		selectedAddrModifCmd = -1;

		if (CreareComanda.termenPlata.trim().length() > 0) {

			if (DateLivrare.getInstance().isClientBlocat()) {
				adapterTermenPlata.add("C000");
			} else {
				String[] tokTermen = CreareComanda.termenPlata.split(";");
				int nrLivr = 0;
				for (nrLivr = 0; nrLivr < tokTermen.length; nrLivr++) {
					adapterTermenPlata.add(tokTermen[nrLivr]);
				}
				spinnerTermenPlata.setSelection(nrLivr);
			}

			globalCodClient = CreareComanda.codClientVar;

		} else {

			if (!DateLivrare.getInstance().isClientBlocat() && DateLivrare.getInstance().getTermenPlata() != null
					&& !DateLivrare.getInstance().getTermenPlata().isEmpty())
				adapterTermenPlata.add(DateLivrare.getInstance().getTermenPlata());
			else
				adapterTermenPlata.add("C000");

			if (!ModificareComanda.codClientVar.equals("")) {
				globalCodClient = ModificareComanda.codClientVar;

				LinearLayout layoutRaft = (LinearLayout) findViewById(R.id.layoutClientRaft);
				layoutRaft.setVisibility(View.GONE);

			}

		}

		performGetJudete();

		int i = 0;

		// document insotitor

		checkFactura = (CheckBox) findViewById(R.id.checkFactura);
		setListenerCheckFactura();
		checkAviz = (CheckBox) findViewById(R.id.checkAviz);
		setListenerCheckAviz();

		checkFactura.setChecked(false);
		checkAviz.setChecked(false);

		if (DateLivrare.getInstance().getTipDocInsotitor().equals("1"))
			checkFactura.setChecked(true);
		else if (DateLivrare.getInstance().getTipDocInsotitor().equals("2"))
			checkAviz.setChecked(true);
		else if (DateLivrare.getInstance().getTipDocInsotitor().equals("3")) {
			checkFactura.setChecked(true);
			checkAviz.setChecked(true);
		}

		// sf. doc insot

		// tip plata

		for (i = 0; i < adapterSpinnerPlata.getCount(); i++) {
			if (adapterSpinnerPlata.getItem(i).toString().split("-")[0].trim().equals(
					UtilsComenzi.setTipPlataClient(DateLivrare.getInstance().getTipPlata()))) {
				spinnerPlata.setSelection(i);
				break;
			}
		}

		spinnerTipReducere = (Spinner) findViewById(R.id.spinnerTipReducere);
		ArrayAdapter<String> adapterTipReducere = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UtilsGeneral.tipReducere);
		adapterTipReducere.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTipReducere.setAdapter(adapterTipReducere);

		// fact. red
		if (DateLivrare.getInstance().getRedSeparat().equals("X")) {
			spinnerTipReducere.setSelection(1); // 2 facturi

		} else {

			if (DateLivrare.getInstance().getRedSeparat().equals("R")) {
				spinnerTipReducere.setSelection(2); // 1 factura red. separat
			} else {
				spinnerTipReducere.setSelection(0); // 1 factura red in pret
			}

		}

		if (CreareComanda.canalDistrib.equals("20"))
			spinnerTipReducere.setEnabled(false);
		else
			spinnerTipReducere.setEnabled(true);

		// tip transport
		for (i = 0; i < adapterSpinnerTransp.getCount(); i++) {
			if (adapterSpinnerTransp.getItem(i).toString().substring(0, 4).equals(DateLivrare.getInstance().getTransport())) {
				spinnerTransp.setSelection(i);
				break;
			}
		}

		checkMacara = (CheckBox) findViewById(R.id.checkMacara);
		setMacaraVisible();
		setListenerCheckMacara();

		checkFactPaleti = (CheckBox) findViewById(R.id.chkFactPaleti);
		checkFactPaleti.setChecked(DateLivrare.getInstance().isFactPaletSeparat());

		chkCamionDescoperit = (CheckBox) findViewById(R.id.chkCamionDescoperit);
		chkCamionDescoperit.setChecked(DateLivrare.getInstance().isCamionDescoperit());

		spinnerTonaj = (Spinner) findViewById(R.id.spinnerTonaj);
		setupSpinnerTonaj();

		spinnerIndoire = (Spinner) findViewById(R.id.spinnerIndoire);
		setupSpinnerIndoire();

		layoutPrelucrare04 = (LinearLayout) findViewById(R.id.layoutIndoire);
		layoutPrelucrare04.setVisibility(View.INVISIBLE);

		if (UtilsUser.isAgentOrSD() && UserInfo.getInstance().getCodDepart().startsWith("04"))
			layoutPrelucrare04.setVisibility(View.VISIBLE);

		if (UtilsUser.isKA() || UtilsUser.isUserSDKA() || UtilsUser.isUserSK())
			layoutPrelucrare04.setVisibility(View.VISIBLE);

		radioLista = (RadioButton) findViewById(R.id.radioLista);
		addListenerRadioLista();

		radioText = (RadioButton) findViewById(R.id.radioText);
		addListenerRadioText();

		radioObiectiv = (RadioButton) findViewById(R.id.radioObiectiv);
		addListenerRadioObiectiv();

		radioObiectiv.setVisibility(View.INVISIBLE);

		spinnerObiective = (Spinner) findViewById(R.id.spinnerObiective);
		setSpinnerObiectiveListener();
		layoutObiective = (LinearLayout) findViewById(R.id.layoutObiective);
		displayObiective(false);

		layoutAdrese.setVisibility(View.VISIBLE);
		layoutAdr1.setVisibility(View.GONE);
		layoutAdr2.setVisibility(View.GONE);

		textDataLivrare = (TextView) findViewById(R.id.textDataLivrare);

		if (!DateLivrare.getInstance().getDataLivrare().isEmpty())
			textDataLivrare.setText(DateLivrare.getInstance().getDataLivrare());

		txtBlocScara = (TextView) findViewById(R.id.txtBlocScara);
		if (!DateLivrare.getInstance().getBlocScara().isEmpty())
			txtBlocScara.setText(DateLivrare.getInstance().getBlocScara());

		if (DateLivrare.getInstance().getDelegat() != null) {
			((EditText) findViewById(R.id.txtNumeDelegat)).setText(DateLivrare.getInstance().getDelegat().getNume());
			((EditText) findViewById(R.id.txtCIDelegat)).setText(DateLivrare.getInstance().getDelegat().getSerieNumarCI());
			((EditText) findViewById(R.id.txtAutoDelegat)).setText(DateLivrare.getInstance().getDelegat().getNrAuto());
		}

		if (UtilsUser.isASDL() || UtilsUser.isOIVPD()) {
			((LinearLayout) findViewById(R.id.layoutObsSofer)).setVisibility(View.INVISIBLE);
			((LinearLayout) findViewById(R.id.layoutCamionDescoperit)).setVisibility(View.INVISIBLE);
			spinnerTransp.setSelection(1);

			if (UtilsUser.isOIVPD())
				spinnerTransp.setEnabled(false);

			chkbClientLaRaft.setChecked(true);
			chkbClientLaRaft.setEnabled(false);

		}

		btnDataLivrare = (Button) findViewById(R.id.btnDataLivrare);
		addListenerDataLivrare();

		setLivrareCustodieLayout();

		performGetAdreseLivrare();

		if (isComandaBV() && DateLivrare.getInstance().getTipPlata().substring(0, 1).equals("E")) {
			checkAviz.setChecked(false);
			checkAviz.setEnabled(false);
		}

		if (DateLivrare.getInstance().isAdrLivrNoua()) {
			radioText.setChecked(true);
			textLocalitate.setText(DateLivrare.getInstance().getOras());
			textStrada.setText(DateLivrare.getInstance().getStrada());
		}

		isAdresaLivrareTCLI = false;
		if (bundle != null && bundle.getString("parrentClass") != null && bundle.getString("parrentClass").equals("CreareComanda")) {
			if (bundle.getString("adrLivrareTCLI").equals(("true"))) {
				isAdresaLivrareTCLI = true;
				getJudeteFilialaLivrare();
			}
		}

	}

	private void afisAdreseLivrareTCLI(String judeteTCLI) {

		((LinearLayout) findViewById(R.id.layoutRadioAdrese)).setVisibility(View.VISIBLE);
		layoutAdrese.setVisibility(View.VISIBLE);
		radioLista.setChecked(true);
		((LinearLayout) findViewById(R.id.layoutBlocScara)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutFilLivrare)).setVisibility(View.GONE);

		List<HashMap<String, String>> listJudeteTCLI = new ArrayList<HashMap<String, String>>();

		for (HashMap<String, String> ll : listJudete) {
			if (judeteTCLI.contains(ll.get("codJudet")))
				listJudeteTCLI.add(ll);

		}

		SimpleAdapter adapterJudeteTCLI = new SimpleAdapter(this, listJudeteTCLI, R.layout.rowlayoutjudete, new String[] { "numeJudet", "codJudet" },
				new int[] { R.id.textNumeJudet, R.id.textCodJudet });

		spinnerJudet.setAdapter(adapterJudeteTCLI);

		List<BeanAdresaLivrare> adreseListTCLI = new ArrayList<BeanAdresaLivrare>();

		for (BeanAdresaLivrare adresaTCLI : adreseList) {
			if (judeteTCLI.contains(adresaTCLI.getCodJudet()))
				adreseListTCLI.add(adresaTCLI);
		}

		AdapterAdreseLivrare adapterAdreseTCLI = new AdapterAdreseLivrare(this, adreseListTCLI);
		spinnerAdreseLivrare.setAdapter(adapterAdreseTCLI);

		if (spinnerAdreseLivrare.getAdapter() != null && spinnerAdreseLivrare.getAdapter().getCount() > 0)
			setAdresaLivrareFromList((BeanAdresaLivrare) spinnerAdreseLivrare.getAdapter().getItem(0));

		spinnerTonaj.setVisibility(View.VISIBLE);
		spinnerTransp.setSelection(0);

	}

	private boolean isComandaBV() {

		boolean isBV90 = false;

		for (ArticolComanda articol : ListaArticoleComanda.getInstance().getListArticoleComanda()) {
			if (articol.getFilialaSite() != null && articol.getFilialaSite().equals("BV90")) {
				isBV90 = true;
				break;
			}
		}

		return isBV90;
	}

	private void setListenerCheckFactura() {
		checkFactura.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					checkAviz.setChecked(false);
			}
		});
	}

	private void setListenerCheckAviz() {
		checkAviz.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					checkFactura.setChecked(false);
			}
		});
	}

	private void setLivrareCustodieLayout() {
		if (UtilsComenzi.isLivrareCustodie()) {
			((LinearLayout) findViewById(R.id.layoutTipReducere)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutDocInsot)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutPlata)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutResponsabil)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutClientRaft)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutFactPaleti)).setVisibility(View.GONE);
		} else {
			((LinearLayout) findViewById(R.id.layoutTipReducere)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutDocInsot)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutPlata)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutResponsabil)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutClientRaft)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutFactPaleti)).setVisibility(View.VISIBLE);
		}
	}

	private void setTipTranspInfoAgent() {
		if (UserInfo.getInstance().getTipUserSap().equals(Constants.tipInfoAv)) {
			DateLivrare.getInstance().setTransport("TCLI");
			spinnerTransp.setEnabled(false);

		}
	}

	private void addListenerDataLivrare() {
		btnDataLivrare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Locale.setDefault(new Locale("ro"));

				int year = Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH);
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				SelectDateDialog datePickerDialog = new SelectDateDialog(SelectAdrLivrCmd.this, datePickerListener, year, month, day);
				datePickerDialog.setTitle("Data livrare");

				datePickerDialog.show();

			}
		});
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

			if (view.isShown()) {

				Calendar calendar = new GregorianCalendar(selectedYear, selectedMonth, selectedDay);

				Calendar calendarNow = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
						Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

				int dayLivrare = calendar.get(Calendar.DAY_OF_WEEK);
				int dayNow = calendarNow.get(Calendar.DAY_OF_WEEK);

				String tipTransp = spinnerTransp.getSelectedItem().toString();

				if (tipTransp.toLowerCase().contains("trap")) {
					if ((dayNow == 5 || dayNow == 6) && dayLivrare == 6) {
						showDialogLivrareSambata(calendar);
						setDataLivrare(calendar);
					} else {
						if (calendar.getTime().getTime() == calendarNow.getTime().getTime())
							showDialogLivrareAstazi(calendar);
						else
							setDataLivrare(calendar);
					}
				} else
					setDataLivrare(calendar);

			}

		}
	};

	private void showDialogLivrareAstazi(final Calendar dataLivrare) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Atentie!");
		builder.setMessage("Ai solicitat livrare in cursul zilei de azi! Sigur este corect?");
		builder.setCancelable(false);

		builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				setDataLivrare(dataLivrare);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Nu", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				btnDataLivrare.performClick();
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void setDataLivrare(Calendar dataLivrare) {
		SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy");

		StatusIntervalLivrare statusInterval = UtilsDates.getStatusIntervalLivrare(dataLivrare.getTime());

		if (statusInterval.isValid()) {
			textDataLivrare.setText(displayFormat.format(dataLivrare.getTime()));
			DateLivrare.getInstance().setDataLivrare(displayFormat.format(dataLivrare.getTime()));
		} else
			Toast.makeText(getApplicationContext(), statusInterval.getMessage(), Toast.LENGTH_LONG).show();

	}

	private void showDialogLivrareSambata(final Calendar dataLivrare) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Atentie!");
		builder.setMessage("Clientul are program de lucru si sambata?");
		builder.setCancelable(false);
		builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				DateLivrare.getInstance().setLivrareSambata("X");
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Nu", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DateLivrare.getInstance().setLivrareSambata("-");
				dialog.dismiss();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void addListenerClientLaRaft() {

		chkbClientLaRaft.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					chkbClientLaRaft.setText("Da");
				} else {
					chkbClientLaRaft.setText("Nu");
				}

				DateLivrare.getInstance().setClientRaft(isChecked);
			}

		});

	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		try {
			txtPers.setText(listPersCont[position]);
			txtTel.setText(listTel[position]);
			lpw.dismiss();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		final int DRAWABLE_RIGHT = 2;

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getX() >= (v.getWidth() - ((EditText) v).getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
				lpw.show();
				return true;
			}
		}
		return false;
	}

	private void setupSpinnerTonaj() {

		String[] tonajValues = { "Selectati tonajul", "3.5 T", "10 T", "Fara restrictie de tonaj" };

		ArrayAdapter<String> adapterTonaj = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tonajValues);
		adapterTonaj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTonaj.setAdapter(adapterTonaj);

	}

	private void setupSpinnerIndoire() {

		String[] indoireValues = { "Tip prelucrare fier-beton 6 m", "TAIERE", "INDOIRE" };

		ArrayAdapter<String> adapterIndoire = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, indoireValues);
		adapterIndoire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIndoire.setAdapter(adapterIndoire);

	}

	private void addSpinnerTranspListener() {
		spinnerTransp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					checkMacara.setChecked(DateLivrare.getInstance().isMasinaMacara());
					setMacaraVisible();
					spinnerTonaj.setVisibility(View.VISIBLE);
					spinnerTonaj.setSelection(0);
					setDateDelegatVisibility(false);
				} else {

					if (arg2 == 1)
						setDateDelegatVisibility(true);
					else
						setDateDelegatVisibility(false);

					checkMacara.setChecked(false);
					checkMacara.setVisibility(View.INVISIBLE);
					if (!isAdresaLivrareTCLI)
						spinnerTonaj.setVisibility(View.INVISIBLE);
				}

				String tipTranspSel = spinnerTransp.getSelectedItem().toString().split("-")[0].trim();
				setTipTranspOpt(tipTranspSel);

				setFilialaPlataVisibility();

			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void setTipTranspOpt(String tipTransp) {

		if (!DateLivrare.getInstance().getTipComandaDistrib().equals(TipCmdDistrib.COMANDA_VANZARE)
				&& !DateLivrare.getInstance().getTipComandaDistrib().equals(TipCmdDistrib.COMANDA_LIVRARE))
			return;

		if (isAdresaLivrareTCLI)
			return;

		if (tipTransp.equals("TCLI") && ModificareComanda.selectedCmd.equals("")) {
			layoutAdrese.setVisibility(View.GONE);
			layoutAdr1.setVisibility(View.GONE);
			layoutAdr2.setVisibility(View.GONE);
			layoutHarta.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutRadioAdrese)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutBlocScara)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutFilLivrare)).setVisibility(View.VISIBLE);

		} else {
			((LinearLayout) findViewById(R.id.layoutRadioAdrese)).setVisibility(View.VISIBLE);
			layoutAdrese.setVisibility(View.VISIBLE);
			radioLista.setChecked(true);
			DateLivrare.getInstance().setFilialaLivrareTCLI("");

			((LinearLayout) findViewById(R.id.layoutBlocScara)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutFilLivrare)).setVisibility(View.GONE);

			if (spinnerAdreseLivrare.getAdapter() != null && spinnerAdreseLivrare.getAdapter().getCount() > 0)
				setAdresaLivrareFromList((BeanAdresaLivrare) spinnerAdreseLivrare.getAdapter().getItem(0));

		}

	}

	private void setSpinnerFilialeTCLIListener() {
		spinnerFilialeTCLI.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if (arg2 > 0) {

					String filialaLivrareTCLI = EnumFilialeLivrare.getCodFiliala(spinnerFilialeTCLI.getSelectedItem().toString());
					DateLivrare.getInstance().setFilialaLivrareTCLI(filialaLivrareTCLI);
					CreareComanda.filialaLivrareMathaus = filialaLivrareTCLI;
					filialaLivrareTCLI = UtilsGeneral.getUnitLogDistrib(filialaLivrareTCLI);

					if (!filialaLivrareTCLI.equals(UserInfo.getInstance().getUnitLog())) {
						DateLivrare.getInstance().setTipComandaDistrib(TipCmdDistrib.COMANDA_LIVRARE);
						DateLivrare.getInstance().setCodFilialaCLP(filialaLivrareTCLI);
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("filiala", filialaLivrareTCLI);
						operatiiAdresa.getAdresaFiliala(params);

					} else {
						DateLivrare.getInstance().setTipComandaDistrib(TipCmdDistrib.COMANDA_VANZARE);
						DateLivrare.getInstance().setCodFilialaCLP("");
					}

					setFilialaPlataVisibility();
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void setFilialaLivrareTCLI() {

		if (DateLivrare.getInstance().getCodFilialaCLP() != null && !DateLivrare.getInstance().getCodFilialaCLP().isEmpty()) {

			for (int ii = 0; ii < spinnerFilialeTCLI.getAdapter().getCount(); ii++) {
				if (EnumFilialeLivrare.getCodFiliala(spinnerFilialeTCLI.getItemAtPosition(ii).toString()).equals(
						DateLivrare.getInstance().getCodFilialaCLP())) {
					spinnerFilialeTCLI.setSelection(ii);
					break;
				}
			}
			spinnerFilialeTCLI.setEnabled(false);
		} else if (DateLivrare.getInstance().getFilialaLivrareTCLI() != null && !DateLivrare.getInstance().getFilialaLivrareTCLI().isEmpty()) {
			for (int ii = 0; ii < spinnerFilialeTCLI.getAdapter().getCount(); ii++) {
				if (EnumFilialeLivrare.getCodFiliala(spinnerFilialeTCLI.getItemAtPosition(ii).toString()).equals(
						DateLivrare.getInstance().getFilialaLivrareTCLI())) {
					spinnerFilialeTCLI.setSelection(ii);
					break;
				}
			}

			if (ListaArticoleComanda.getInstance().getListArticoleComanda() != null
					&& ListaArticoleComanda.getInstance().getListArticoleComanda().size() > 0) {
				spinnerTransp.setEnabled(false);
				spinnerFilialeTCLI.setEnabled(false);
			}

		}
	}

	private void setFilialaPlataVisibility() {

		String tipTranspSel = spinnerTransp.getSelectedItem().toString().split("-")[0].trim();
		String tipPlata = spinnerPlata.getSelectedItem().toString().split("-")[0].trim();

		if ((DateLivrare.getInstance().getTipComandaDistrib().equals(TipCmdDistrib.COMANDA_LIVRARE) || isComandaClp()) && tipTranspSel.equals("TCLI")
				&& tipPlata.equals("N")) {
			((LinearLayout) findViewById(R.id.layoutFilialaPlata)).setVisibility(View.VISIBLE);
			((RadioButton) findViewById(R.id.radioPlataFilialaAg)).setText(EnumFiliale.getNumeFiliala(UserInfo.getInstance().getUnitLog()));
			((RadioButton) findViewById(R.id.radioPlataFilialaLivrare)).setText(EnumFiliale.getNumeFiliala(DateLivrare.getInstance()
					.getCodFilialaCLP()));

			String localFilialaPlata = DateLivrare.getInstance().getFilialaPlata() != null ? DateLivrare.getInstance().getFilialaPlata() : "";

			if (!localFilialaPlata.trim().isEmpty()) {
				if (localFilialaPlata.equals(DateLivrare.getInstance().getCodFilialaCLP()))
					((RadioButton) findViewById(R.id.radioPlataFilialaLivrare)).setChecked(true);
				else
					((RadioButton) findViewById(R.id.radioPlataFilialaAg)).setChecked(true);
			}
		} else
			((LinearLayout) findViewById(R.id.layoutFilialaPlata)).setVisibility(View.GONE);

	}

	private void setDateDelegatVisibility(boolean isVisible) {
		if (isVisible) {
			((LinearLayout) findViewById(R.id.layoutDelegat)).setVisibility(View.VISIBLE);
		} else {
			((LinearLayout) findViewById(R.id.layoutDelegat)).setVisibility(View.INVISIBLE);
		}
	}

	private void setListenerCheckMacara() {
		checkMacara.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DateLivrare.getInstance().setMasinaMacara(isChecked);
			}
		});
	}

	private void setListenerCheckObsSofer() {
		checkObsSofer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					txtObservatii.setVisibility(View.VISIBLE);
					checkObsSofer.setText("Da");
				} else {
					checkObsSofer.setText("Nu");
					txtObservatii.setText("");
					txtObservatii.setVisibility(View.INVISIBLE);
				}

			}
		});
	}

	private void displayObiective(boolean isVisible) {
		if (isVisible) {
			layoutObiective.setVisibility(View.VISIBLE);
		} else {
			layoutObiective.setVisibility(View.INVISIBLE);
		}
	}

	private void setSpinnerObiectiveListener() {
		spinnerObiective.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				if (pos > 0) {
					radioObiectiv.setVisibility(View.VISIBLE);
					radioObiectiv.setChecked(true);
					obiectivSelectat = (BeanObiectivDepartament) parent.getAdapter().getItem(pos);
					setAdresaLivrareFromObiectiv();
				} else {
					radioObiectiv.setVisibility(View.INVISIBLE);
					radioLista.setChecked(true);
					obiectivSelectat = null;
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				return;

			}
		});
	}

	private void addListenerModifValInc() {
		checkModifValInc.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (checkModifValInc.isChecked()) {
					DateLivrare.getInstance().setValIncModif(true);
					txtValoareIncasare.setFocusableInTouchMode(true);
					txtValoareIncasare.setText(DateLivrare.getInstance().getValoareIncasare());

				} else {
					DateLivrare.getInstance().setValIncModif(false);
					txtValoareIncasare.setFocusable(false);

					double localValCmd = 0;
					if (!CreareComanda.codClientVar.equals(""))
						localValCmd = CreareComanda.totalComanda * Constants.TVA;
					else
						localValCmd = ModificareComanda.totalComanda * Constants.TVA;

					txtValoareIncasare.setText(nf2.format(localValCmd));

				}
			}
		});

	}

	private void performGetJudete() {
		fillJudeteClient(EnumJudete.getRegionCodes());
	}

	private void getJudeteFilialaLivrare() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("filiala", DateLivrare.getInstance().getFilialaLivrareTCLI());

		AsyncTaskWSCall call = new AsyncTaskWSCall(this, METHOD_NAME, params);
		call.getCallResultsSyncActivity();

	}

	private boolean isComandaClp() {
		return !DateLivrare.getInstance().getCodFilialaCLP().trim().isEmpty() && DateLivrare.getInstance().getCodFilialaCLP().trim().length() == 4;
	}

	private boolean isComandaDl() {
		return DateLivrare.getInstance().getFurnizorComanda() != null
				&& !DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa().isEmpty()
				&& DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa().length() > 4;
	}

	private boolean isLivrareCustodie() {
		return DateLivrare.getInstance().getTipComandaDistrib().equals(TipCmdDistrib.LIVRARE_CUSTODIE);
	}

	private boolean isComandaACZC() {
		return DateLivrare.getInstance().getTipComandaDistrib().equals(TipCmdDistrib.ARTICOLE_COMANDA);
	}

	private void fillJudeteClient(String arrayJudete) {

		if (listJudete != null)
			listJudete.clear();

		HashMap<String, String> temp;
		String numeJudSel = "";
		int i;
		temp = new HashMap<String, String>();
		temp.put("numeJudet", "Selectati judetul");
		temp.put("codJudet", "");
		listJudete.add(temp);

		int nrJud = 0;
		for (i = 0; i < UtilsGeneral.numeJudete.length; i++) {

			if (arrayJudete.contains(UtilsGeneral.codJudete[i])) {
				temp = new HashMap<String, String>();
				temp.put("numeJudet", UtilsGeneral.numeJudete[i]);
				temp.put("codJudet", UtilsGeneral.codJudete[i]);
				listJudete.add(temp);

				nrJud++;

				if (DateLivrare.getInstance().getCodJudet().equals(UtilsGeneral.codJudete[i])) {
					posJudetSel = nrJud;
					numeJudSel = UtilsGeneral.numeJudete[i];
				}
			}

		}

		spinnerJudet.setAdapter(adapterJudete);

		if (posJudetSel > 0) {
			DateLivrare.getInstance().setNumeJudet(numeJudSel);
			spinnerJudet.setSelection(posJudetSel);
		}

	}

	private void addListenerTipPlata() {
		spinnerPlata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				checkAviz.setEnabled(true);
				spinnerResponsabil.setEnabled(true);
				spinnerTermenPlata.setEnabled(true);
				((TextView) findViewById(R.id.tipPlataContract)).setVisibility(View.INVISIBLE);
				String rawTipPlataStr = spinnerPlata.getSelectedItem().toString();

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("N")
						|| spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("R")) {

					if (isComandaBV()) {
						checkAviz.setEnabled(false);
						checkAviz.setChecked(false);
					}
					spinnerResponsabil.setSelection(2);
					spinnerResponsabil.setEnabled(false);
					spinnerTermenPlata.setSelection(0);
					spinnerTermenPlata.setEnabled(false);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("R")) {
					spinnerResponsabil.setEnabled(true);
					spinnerResponsabil.setSelection(1);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("O")) {
					spinnerTermenPlata.setSelection(0);
					spinnerTermenPlata.setEnabled(false);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("L")) {
					spinnerTermenPlata.setSelection(adapterTermenPlata.getCount() - 1);
					((TextView) findViewById(R.id.tipPlataContract)).setVisibility(View.VISIBLE);
				}

				if (pos == 0 || pos == 1 || pos == 2 || pos == 3) {
					spinnerTermenPlata.setVisibility(View.VISIBLE);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("R") && spinnerResponsabil.getSelectedItemPosition() == 1) {
					layoutValoareIncasare.setVisibility(View.VISIBLE);

					if (DateLivrare.getInstance().isValIncModif()) {
						txtValoareIncasare.setText(DateLivrare.getInstance().getValoareIncasare());

					} else {
						double localValCmd = 0;
						if (!CreareComanda.codClientVar.equals("")) {
							if (CreareComanda.canalDistrib.equals("10"))
								localValCmd = CreareComanda.totalComanda * Constants.TVA;
							else if (CreareComanda.canalDistrib.equals("20"))
								localValCmd = CreareComanda.totalComanda;
						} else
							localValCmd = ModificareComanda.totalComanda * Constants.TVA;

						txtValoareIncasare.setText(nf2.format(localValCmd));

					}

				} else {
					layoutValoareIncasare.setVisibility(View.GONE);
				}

				if (rawTipPlataStr.toLowerCase().contains("numerar") || rawTipPlataStr.toLowerCase().contains("ramburs")) {
					checkAviz.setChecked(false);
					checkAviz.setEnabled(false);
				} else
					checkAviz.setEnabled(true);

				setFilialaPlataVisibility();
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	private void addListenerResponsabil() {
		spinnerResponsabil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if (pos == 1 && spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("R")) {
					layoutValoareIncasare.setVisibility(View.VISIBLE);

					if (DateLivrare.getInstance().isValIncModif()) {
						txtValoareIncasare.setText(DateLivrare.getInstance().getValoareIncasare());

					} else {
						double localValCmd = 0;
						if (!CreareComanda.codClientVar.equals("")) {
							if (CreareComanda.canalDistrib.equals("10"))
								localValCmd = CreareComanda.totalComanda * Constants.TVA;
							else if (CreareComanda.canalDistrib.equals("20"))
								localValCmd = CreareComanda.totalComanda;
						} else
							localValCmd = ModificareComanda.totalComanda * Constants.TVA;

						txtValoareIncasare.setText(nf2.format(localValCmd));

					}

				} else {
					layoutValoareIncasare.setVisibility(View.GONE);
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addListenerRadioLista() {

		radioLista.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearAdresaLivrare();

			}
		});

		radioLista.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				if (arg1) {

					layoutAdrese.setVisibility(View.VISIBLE);
					layoutAdr1.setVisibility(View.GONE);
					layoutAdr2.setVisibility(View.GONE);
					layoutHarta.setVisibility(View.GONE);

					if (adreseList == null || adreseList.isEmpty())
						performGetAdreseLivrare();
					else if (selectedAddrModifCmd != -1)
						spinnerAdreseLivrare.setSelection(selectedAddrModifCmd);
					else
						setAdresaLivrareFromList((BeanAdresaLivrare) spinnerAdreseLivrare.getAdapter().getItem(0));

				}

			}
		});
	}

	public void addListenerRadioText() {

		radioText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearAdresaLivrare();
				spinnerTonaj.setEnabled(true);
				spinnerTonaj.setSelection(0);

			}
		});

		radioText.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				if (arg1) {
					layoutAdrese.setVisibility(View.GONE);
					layoutAdr1.setVisibility(View.VISIBLE);
					layoutAdr2.setVisibility(View.VISIBLE);
					layoutHarta.setVisibility(View.VISIBLE);

				}

			}
		});
	}

	private void addListenerRadioObiectiv() {

		radioObiectiv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearAdresaLivrare();
				spinnerTonaj.setEnabled(true);
				spinnerTonaj.setSelection(0);

			}
		});

		radioObiectiv.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				if (arg1) {
					layoutAdrese.setVisibility(View.GONE);
					layoutAdr1.setVisibility(View.GONE);
					layoutAdr2.setVisibility(View.GONE);
					layoutHarta.setVisibility(View.GONE);

				}

			}
		});
	}

	protected void performGetAdreseLivrare() {

		String filiala = "";

		if (!isComandaClp() && !isComandaBV() && !isComandaDl() && !DateLivrare.getInstance().isClientFurnizor())
			filiala = UserInfo.getInstance().getUnitLog();

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codClient", globalCodClient);
		params.put("filiala", filiala);

		operatiiAdresa.getAdreseLivrareClient(params);

	}

	private void getObiectiveDepartament() {
		operatiiObiective = new OperatiiObiective(this);
		operatiiObiective.setObiectiveListener(this);

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();

		params.put("filiala", UserInfo.getInstance().getUnitLog());
		params.put("departament", UserInfo.getInstance().getCodDepart());
		params.put("codClient", CreareComanda.codClientVar);
		params.put("tipUser", UserInfo.getInstance().getTipUser());
		params.put("codUser", UserInfo.getInstance().getCod());

		operatiiObiective.getObiectiveDepartament(params);
	}

	private void fillListAdrese(String adrese) {

		HandleJSONData objClientList = new HandleJSONData(this, adrese);
		adreseList = objClientList.decodeJSONAdresaLivrare();

		if (adreseList.size() > 0) {

			AdapterAdreseLivrare adapterAdrese = new AdapterAdreseLivrare(this, adreseList);
			spinnerAdreseLivrare.setAdapter(adapterAdrese);

			if (selectedAddrModifCmd != -1)
				spinnerAdreseLivrare.setSelection(selectedAddrModifCmd);

			if (!ModificareComanda.selectedCmd.equals("")) {
				if (!adrNouaModifCmd)
					getCmdDateLivrare();
				else
					spinnerAdreseLivrare.setSelection(selectedAddrModifCmd);
			}

		}

		if (UtilsUser.hasObiective() && CreareComanda.codClientVar.trim().length() > 0)
			getObiectiveDepartament();

	}

	private void getCmdDateLivrare() {

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("idCmd", ModificareComanda.selectedCmd);
		operatiiAdresa.getDateLivrare(params);

	}

	@SuppressWarnings("unchecked")
	private void fillDateLivrare(String dateLivrare) {

		try {
			// incarcare date livrare comanda modificata
			String[] tokLivrare = dateLivrare.split("#");
			HashMap<String, String> artMapLivr = null;

			// adresa de livrare
			if (tokLivrare[13].equals("X")) // adresa noua
			{
				radioText.setChecked(true);
				int nrJudete = listJudete.size(), j = 0;
				String codJudet = "";

				for (j = 0; j < nrJudete; j++) {
					artMapLivr = (HashMap<String, String>) this.adapterJudete.getItem(j);
					codJudet = artMapLivr.get("codJudet").toString();

					if (tokLivrare[8].equals(codJudet)) {
						spinnerJudet.setSelection(j);
						break;
					}

				}

				DateLivrare.getInstance().setOras(tokLivrare[7]);

				textLocalitate.setText(tokLivrare[7]);

				textStrada.setText(tokLivrare[4]);
				adrNouaModifCmd = true;

			} else {
				radioLista.setChecked(true);

				int nrAdrese = spinnerAdreseLivrare.getAdapter().getCount();

				int j = 0;
				String adr_number = "";

				for (j = 0; j < nrAdrese; j++) {

					BeanAdresaLivrare adresaLivrare = (BeanAdresaLivrare) spinnerAdreseLivrare.getAdapter().getItem(j);

					adr_number = adresaLivrare.getCodAdresa();

					if (tokLivrare[15].equals(adr_number)) {
						spinnerAdreseLivrare.setSelection(j);
						selectedAddrModifCmd = j;
						break;
					}

				}

			}

			// pers. contact
			txtPers.setText(tokLivrare[2]);

			// telefon
			txtTel.setText(tokLivrare[3]);

			// fact.red. separat
			if (tokLivrare[6].equals("X"))
				spinnerTipReducere.setSelection(1);
			if (tokLivrare[6].equals("R"))
				spinnerTipReducere.setSelection(2);
			if (tokLivrare[6].equals(" "))
				spinnerTipReducere.setSelection(0);

			// doc. insot
			checkFactura.setChecked(false);
			checkAviz.setChecked(false);

			if (tokLivrare[12].equals("1"))
				checkFactura.setChecked(true);
			else if (tokLivrare[12].equals("2"))
				checkAviz.setChecked(true);
			else if (tokLivrare[12].equals("3")) {
				checkFactura.setChecked(true);
				checkAviz.setChecked(true);
			}

			// tip plata
			if (tokLivrare[12].equals("B"))
				spinnerPlata.setSelection(0);
			if (tokLivrare[12].equals("C"))
				spinnerPlata.setSelection(1);
			if (tokLivrare[12].equals("E"))
				spinnerPlata.setSelection(2);
			if (tokLivrare[12].equals("O"))
				spinnerPlata.setSelection(3);

			// termen plata
			adapterTermenPlata.clear();
			adapterTermenPlata.add("C000");
			if (!ModificareComanda.codClientVar.equals("")) {

				if (!tokLivrare[9].equals("C000")) {
					if (!DateLivrare.getInstance().getTermenPlata().trim().equals("")) {
						adapterTermenPlata.add(DateLivrare.getInstance().getTermenPlata());
						spinnerTermenPlata.setSelection(1);
					}
				}
			}

			for (int ii = 0; ii < spinnerTransp.getAdapter().getCount(); ii++) {
				if (spinnerTransp.getAdapter().getItem(ii).toString().toUpperCase().contains(tokLivrare[5])) {
					spinnerTransp.setSelection(ii);
					break;
				}
			}

			// obs livrare
			txtObservatii.setText(tokLivrare[10]);

			DateLivrare.getInstance().setValoareIncasare(tokLivrare[16]);

			double localValCmd = Double.parseDouble(DateLivrare.getInstance().getValoareIncasare());

			if (tokLivrare[17].equals("X")) {
				DateLivrare.getInstance().setValIncModif(true);
				checkModifValInc.setChecked(true);
			} else {
				DateLivrare.getInstance().setValIncModif(false);
				checkModifValInc.setChecked(false);

				if (!CreareComanda.codClientVar.equals(""))
					localValCmd = CreareComanda.totalComanda * Constants.TVA;
				else
					localValCmd = ModificareComanda.totalComanda * Constants.TVA;

			}

			txtValoareIncasare.setText(nf2.format(localValCmd));

			// obs. plata (responsabil livrare)
			spinnerResponsabil.setSelection(0);

			if (tokLivrare[14].equals("AV")) {
				spinnerResponsabil.setSelection(0);
			} else if (tokLivrare[14].equals("SO")) {
				spinnerResponsabil.setSelection(1);
			} else if (tokLivrare[14].equals("OC")) {
				spinnerResponsabil.setSelection(2);
			}

			DateLivrare.getInstance().setPrelucrare(tokLivrare[18]);
			for (int i = 0; i < spinnerIndoire.getCount(); i++)
				if (spinnerIndoire.getItemAtPosition(i).toString().toUpperCase().contains(tokLivrare[18])) {
					spinnerIndoire.setSelection(i);
					break;
				}

			DateLivrare.getInstance().setClientRaft(tokLivrare[20].equals("X") ? true : false);

		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	private void setSpinnerTonajValue(String tonaj) {

		if (tonaj == null || tonaj.equals("0")) {
			spinnerTonaj.setSelection(0);
			spinnerTonaj.setEnabled(true);
			return;
		}

		DateLivrare.getInstance().setTonaj(tonaj);

		int pos = HelperAdreseLivrare.getTonajSpinnerPos(tonaj);

		if (pos > 0) {
			spinnerTonaj.setSelection(pos);
			spinnerTonaj.setEnabled(false);
		}

	}

	private void setMacaraVisible() {

		checkMacara.setVisibility(View.INVISIBLE);

	}

	private boolean isArtException() {

		List<String> articoleExceptii = Exceptions.getExceptionsMacara(this);
		List<ArticolComanda> articoleComanda = ListaArticoleComanda.getInstance().getListArticoleComanda();

		for (String exceptie : articoleExceptii) {

			for (ArticolComanda articol : articoleComanda) {
				if (exceptie.equals(articol.getCodArticol()))
					return true;
			}

		}

		return false;
	}

	private boolean existaArticole() {
		return ListaArticoleComanda.getInstance().getListArticoleComanda() != null
				&& ListaArticoleComanda.getInstance().getListArticoleComanda().size() > 0 && !DateLivrare.getInstance().getTransport().equals("TCLI");
	}

	public class regionSelectedListener implements OnItemSelectedListener {
		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if (spinnerJudet.getSelectedItemPosition() > 0) {

				HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudet.getSelectedItem();
				DateLivrare.getInstance().setNumeJudet(tempMap.get("numeJudet"));
				DateLivrare.getInstance().setCodJudet(tempMap.get("codJudet"));

				HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
				params.put("codJudet", DateLivrare.getInstance().getCodJudet());
				operatiiAdresa.getAdreseJudet(params, null);

			}

		}

		public void onNothingSelected(AdapterView<?> parent) {
			return;
		}
	}

	private void populateListLocalitati(BeanAdreseJudet listAdrese) {

		listAdreseJudet = listAdrese;

		textLocalitate.setVisibility(View.VISIBLE);
		textLocalitate.setText(DateLivrare.getInstance().getOras());

		String[] arrayLocalitati = listAdrese.getListStringLocalitati().toArray(new String[listAdrese.getListStringLocalitati().size()]);
		ArrayAdapter<String> adapterLoc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLocalitati);

		textLocalitate.setThreshold(0);
		textLocalitate.setAdapter(adapterLoc);

		String[] arrayStrazi = listAdrese.getListStrazi().toArray(new String[listAdrese.getListStrazi().size()]);
		ArrayAdapter<String> adapterStrazi = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayStrazi);

		textStrada.setThreshold(0);
		textStrada.setAdapter(adapterStrazi);

		listLocalitatiLivrare = listAdrese.getListStringLocalitati();

		setListenerTextLocalitate();

		getFilialaLivrareMathaus();

	}

	private void getFilialaLivrareMathaus() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codJudet", DateLivrare.getInstance().getCodJudet());
		operatiiAdresa.getFilialaLivrareMathaus(params);
	}

	private void setListenerTextLocalitate() {

		textLocalitate.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DateLivrare.getInstance().setOras(textLocalitate.getText().toString().trim());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		textLocalitate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					textLocalitate.setText(textLocalitate.getText().toString().trim().toUpperCase());
					verificaLocalitate("LIVRARE");

				}

			}
		});

	}

	private boolean verificaLocalitate(String tipLocalitate) {
		boolean locExist = false;
		List<String> listLocalitati = new ArrayList<String>();
		String localitateCurenta = "";
		String numeJudet = "";
		EditText textLoc = null;

		if (tipLocalitate.equals("LIVRARE")) {
			textLoc = textLocalitate;
			localitateCurenta = textLoc.getText().toString().trim();
			listLocalitati = listLocalitatiLivrare;

			@SuppressWarnings("unchecked")
			HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudet.getSelectedItem();
			numeJudet = tempMap.get("numeJudet");

		}

		for (String localitate : listLocalitati) {
			if (localitate.trim().equalsIgnoreCase(localitateCurenta)) {
				locExist = true;
				break;
			}

		}

		if (!locExist && !localitateCurenta.isEmpty()) {
			String alert = "Localitatea " + localitateCurenta + " nu exista in judetul " + numeJudet + ". Completati alta localitate.";
			Toast.makeText(getApplicationContext(), alert, Toast.LENGTH_LONG).show();

			textLoc.setText("");
			textLoc.setFocusableInTouchMode(true);

		}

		return locExist;
	}

	private void displayObiectiveDepartament(List<BeanObiectivDepartament> obiectiveDepart) {

		if (obiectiveDepart.size() > 0) {

			displayObiective(true);

			BeanObiectivDepartament dummyOb = new BeanObiectivDepartament();

			dummyOb.setNume("Selectati un obiectiv");
			dummyOb.setAdresa(" ");
			dummyOb.setBeneficiar(" ");
			dummyOb.setDataCreare(" ");
			dummyOb.setId(" ");

			obiectiveDepart.add(0, dummyOb);

			AdapterObiective adapterOb = new AdapterObiective(getApplicationContext(), obiectiveDepart);
			spinnerObiective.setAdapter(adapterOb);
		} else {
			displayObiective(false);
		}

	}

	private void setListnerBtnPozitieAdresa() {
		btnPozitieAdresa.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				android.app.FragmentManager fm = getFragmentManager();

				Address address = new Address();

				if (radioText.isChecked()) {
					address.setCity(textLocalitate.getText().toString().trim());
					address.setStreet(textStrada.getText().toString().trim());
					address.setSector(UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudet()));

					if (!isAdresaComplet())
						return;

					String nrStrada = " ";

					if (textNrStr.getText().toString().trim().length() > 0)
						nrStrada = " nr " + textNrStr.getText().toString().trim();

					DateLivrare.getInstance().setOras(address.getCity());
					DateLivrare.getInstance().setStrada(address.getStreet() + nrStrada);

				} else {
					address.setCity(DateLivrare.getInstance().getOras());
					address.setStreet(DateLivrare.getInstance().getStrada());
					address.setSector(UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudet()));
				}

				MapAddressDialog mapDialog = new MapAddressDialog(address, SelectAdrLivrCmd.this, fm);
				mapDialog.setCoords(DateLivrare.getInstance().getCoordonateAdresa());
				mapDialog.setMapListener(SelectAdrLivrCmd.this);
				mapDialog.show();
			}
		});
	}

	private boolean isAdresaComplet() {
		if (spinnerJudet.getSelectedItemPosition() == 0) {
			Toast.makeText(this, "Selectati judetul", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (textLocalitate.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Completati localitatea", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	public void addListenerSaveAdr() {
		saveAdrLivrBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (radioText.isChecked())
					valideazaAdresaLivrare();
				else
					valideazaDateLivrare();

			}
		});

	}

	private void valideazaDateLivrare() {
		String adresa = "";
		String pers = "";
		String telefon = "";
		String observatii = "", obsPlata = " ";

		DateLivrare dateLivrareInstance = DateLivrare.getInstance();

		if (layoutAdrese.getVisibility() == View.VISIBLE) {

			if (listAdreseLivrare.size() > 0) {
				BeanAdresaLivrare adresaLivrare = (BeanAdresaLivrare) spinnerAdreseLivrare.getSelectedItem();
				setAdresaLivrareFromList(adresaLivrare);

			}

		} else if (((LinearLayout) findViewById(R.id.layoutFilLivrare)).getVisibility() == View.VISIBLE) {

		} else {

			verificaLocalitate("LIVRARE");

			String nrStrada = "";

			if (textNrStr.getText().toString().trim().length() > 0)
				nrStrada = " NR " + textNrStr.getText().toString().trim();

			dateLivrareInstance.setStrada(textStrada.getText().toString().trim() + nrStrada);
			dateLivrareInstance.setAdrLivrNoua(true);
			dateLivrareInstance.setAddrNumber(" ");

		}

		setAdresaLivrareFromObiectiv();

		pers = txtPers.getText().toString().trim();
		telefon = txtTel.getText().toString().trim();
		observatii = txtObservatii.getText().toString().trim();
		obsPlata = spinnerResponsabil.getSelectedItem().toString().substring(0, 2);

		if (observatii.trim().length() == 0)
			observatii = " ";

		dateLivrareInstance.setPersContact(pers);
		dateLivrareInstance.setNrTel(telefon);

		if (DateLivrare.getInstance().getOras().trim().equals("")) {
			Toast.makeText(getApplicationContext(), "Selectati localitatea!", Toast.LENGTH_LONG).show();
			return;
		}

		if (dateLivrareInstance.getCodJudet().equals("")) {
			Toast.makeText(getApplicationContext(), "Selectati judetul!", Toast.LENGTH_LONG).show();
			return;
		}

		if (pers.equals("") || pers.equals("-")) {
			Toast.makeText(getApplicationContext(), "Completati persoana de contact!", Toast.LENGTH_LONG).show();
			return;
		}

		if (telefon.equals("") || telefon.equals("-")) {
			Toast.makeText(getApplicationContext(), "Completati nr. de telefon!", Toast.LENGTH_LONG).show();
			return;
		}

		if (spinnerTransp.getSelectedItem().toString().toLowerCase().contains("arabesque") && spinnerTonaj.getSelectedItemPosition() == 0) {
			Toast.makeText(getApplicationContext(), "Selectati tonajul!", Toast.LENGTH_LONG).show();
			return;
		}

		if (DateLivrare.getInstance().getDataLivrare().isEmpty()) {
			Toast.makeText(getApplicationContext(), "Selectati data livrare!", Toast.LENGTH_LONG).show();
			return;
		}

		if (layoutValoareIncasare.getVisibility() == View.VISIBLE && txtValoareIncasare.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "Completati valoarea incasarii!", Toast.LENGTH_LONG).show();
			return;
		}

		if (((LinearLayout) findViewById(R.id.layoutFilLivrare)).getVisibility() == View.VISIBLE && spinnerFilialeTCLI.getSelectedItemPosition() == 0) {
			Toast.makeText(getApplicationContext(), "Selectati filiala care livreaza.", Toast.LENGTH_LONG).show();
			return;
		}

		if (((LinearLayout) findViewById(R.id.layoutFilialaPlata)).getVisibility() == View.VISIBLE) {
			if (((RadioButton) findViewById(R.id.radioPlataFilialaAg)).isChecked())
				DateLivrare.getInstance().setFilialaPlata(UserInfo.getInstance().getUnitLog());
			else if (((RadioButton) findViewById(R.id.radioPlataFilialaLivrare)).isChecked())
				DateLivrare.getInstance().setFilialaPlata(DateLivrare.getInstance().getCodFilialaCLP());
			else {
				Toast.makeText(getApplicationContext(), "Selectati filiala in care se plateste.", Toast.LENGTH_LONG).show();
				return;
			}
		} else
			DateLivrare.getInstance().setFilialaPlata("");

		dateLivrareInstance.setTipPlata(spinnerPlata.getSelectedItem().toString().split("-")[0].trim());
		dateLivrareInstance.setTransport(spinnerTransp.getSelectedItem().toString().substring(0, 4));

		if (dateLivrareInstance.getTransport().equalsIgnoreCase("TERR") && !HelperAdreseLivrare.isAdresaLivrareRapida()) {
			Toast.makeText(getApplicationContext(), "In aceasta localitate nu se face livrare rapida.", Toast.LENGTH_LONG).show();
			return;
		}

		if (!isAdresaCorecta()) {
			Toast.makeText(getApplicationContext(), "Completati adresa corect sau pozitionati adresa pe harta.", Toast.LENGTH_LONG).show();
			return;
		}

		if (DateLivrare.getInstance().getTransport().equals("TRAP") && isAdresaText()) {
			valideazaTonajAdresaNoua();

		}

		String cantar = "NU";
		dateLivrareInstance.setCantar("NU");

		if (spinnerTipReducere.getSelectedItemPosition() == 0) {
			dateLivrareInstance.setRedSeparat(" ");
		}

		if (spinnerTipReducere.getSelectedItemPosition() == 1) {
			dateLivrareInstance.setRedSeparat("X");
		}

		if (spinnerTipReducere.getSelectedItemPosition() == 2) {
			dateLivrareInstance.setRedSeparat("R");
		}

		if (layoutValoareIncasare.getVisibility() == View.VISIBLE)
			dateLivrareInstance.setValoareIncasare(txtValoareIncasare.getText().toString());
		else
			dateLivrareInstance.setValoareIncasare("0");

		adresa = dateLivrareInstance.getNumeJudet() + " " + dateLivrareInstance.getOras() + " " + dateLivrareInstance.getStrada();

		dateLivrareInstance.setDateLivrare(adresa + "#" + pers + "#" + telefon + "#" + cantar + "#" + dateLivrareInstance.getTipPlata() + "#"
				+ dateLivrareInstance.getTransport() + "#" + dateLivrareInstance.getRedSeparat() + "#");

		if (((LinearLayout) findViewById(R.id.layoutPlata)).getVisibility() == View.VISIBLE)
			dateLivrareInstance.setTermenPlata(spinnerTermenPlata.getSelectedItem().toString());

		dateLivrareInstance.setObsLivrare(observatii.replace("#", "-").replace("@", "-"));
		dateLivrareInstance.setObsPlata(obsPlata);

		String tipDocInsot = "";
		if (checkFactura.isChecked() && checkAviz.isChecked())
			tipDocInsot = "3";
		else if (checkFactura.isChecked())
			tipDocInsot = "1";
		else if (checkAviz.isChecked())
			tipDocInsot = "2";

		dateLivrareInstance.setTipDocInsotitor(tipDocInsot);

		if (dateLivrareInstance.getTipDocInsotitor().isEmpty()) {
			Toast.makeText(getApplicationContext(), "Selectati documentul insotitor!", Toast.LENGTH_LONG).show();
			return;
		}

		if (isConditiiTonaj(spinnerTransp, spinnerTonaj)) {
			dateLivrareInstance.setTonaj(HelperAdreseLivrare.getTonajSpinnerValue(spinnerTonaj.getSelectedItemPosition()));
		} else
			dateLivrareInstance.setTonaj("-1");

		if (spinnerIndoire.getVisibility() == View.VISIBLE && spinnerIndoire.getSelectedItemPosition() > 0) {
			dateLivrareInstance.setPrelucrare(spinnerIndoire.getSelectedItem().toString());
		} else
			dateLivrareInstance.setPrelucrare("-1");

		if (dateLivrareInstance.getOras().equalsIgnoreCase("bucuresti") || dateLivrareInstance.getOras().toLowerCase().contains("sector")) {
			beans.LatLng coordAdresa = new beans.LatLng(dateLivrareInstance.getCoordonateAdresa().latitude,
					dateLivrareInstance.getCoordonateAdresa().longitude);
			EnumZona zona = ZoneBucuresti.getZonaBucuresti(coordAdresa);

			dateLivrareInstance.setZonaBucuresti(zona);
		} else
			dateLivrareInstance.setZonaBucuresti(EnumZona.NEDEFINIT);

		dateLivrareInstance.setFactPaletSeparat(checkFactPaleti.isChecked());
		dateLivrareInstance.setCamionDescoperit(chkCamionDescoperit.isChecked());
		dateLivrareInstance.setBlocScara(txtBlocScara.getText().toString().trim());

		Delegat delegat = new Delegat();

		if (UserInfo.getInstance().getTipUserSap().equals("ASDL") && DateLivrare.getInstance().getTransport().equals("TCLI")) {

			if (((EditText) findViewById(R.id.txtNumeDelegat)).getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "Completati numele delegatului.", Toast.LENGTH_LONG).show();
				return;
			}

			if (((EditText) findViewById(R.id.txtCIDelegat)).getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "Completati seria CI a delegatului.", Toast.LENGTH_LONG).show();
				return;
			}

		}

		if (DateLivrare.getInstance().getTransport().equals("TCLI")) {
			delegat.setNume(((EditText) findViewById(R.id.txtNumeDelegat)).getText().toString().trim());
			delegat.setSerieNumarCI(((EditText) findViewById(R.id.txtCIDelegat)).getText().toString().trim());
			delegat.setNrAuto(((EditText) findViewById(R.id.txtAutoDelegat)).getText().toString().trim());
		}

		dateLivrareInstance.setDelegat(delegat);

		if (radioText.isChecked() && adresaNouaExista())
			return;

		finish();

	}

	private boolean isAdresaCorecta() {
		if (DateLivrare.getInstance().getTransport().equals("TRAP"))
			return isAdresaGoogleOk();
		else
			return true;

	}

	private void valideazaTonajAdresaNoua() {
		String tonajAdresaNoua = HelperAdreseLivrare.getTonajAdresaNoua(adreseList, DateLivrare.getInstance().getCoordonateAdresa());

		String tonajExistent = HelperAdreseLivrare.getTonajSpinnerValue(spinnerTonaj.getSelectedItemPosition());

		if (!tonajAdresaNoua.equals("0"))
			setSpinnerTonajValue(tonajAdresaNoua);
		else {
			setSpinnerTonajValue(tonajExistent);
			spinnerTonaj.setEnabled(true);
		}

		if (!tonajAdresaNoua.equals("0") && !tonajAdresaNoua.equals(tonajExistent))
			Toast.makeText(getApplicationContext(), "Pentru aceasta zona tonajul a fost preluat din sistem.", Toast.LENGTH_LONG).show();
	}

	private boolean adresaNouaExista() {

		int posAdresa = HelperAdreseLivrare.verificaDistantaAdresaNoua(adreseList, DateLivrare.getInstance().getCoordonateAdresa());

		int adresaExista = HelperAdreseLivrare.adresaExista(adreseList);

		if (adresaExista != -1) {
			Toast.makeText(getApplicationContext(), "Aceasta adresa exista deja in lista de adrese.", Toast.LENGTH_LONG).show();
			selectedAddrModifCmd = posAdresa != -1 ? posAdresa : adresaExista;
			radioLista.setChecked(true);
			spinnerAdreseLivrare.setSelection(posAdresa != -1 ? posAdresa : adresaExista);
			return true;
		} else if (posAdresa != -1) {
			valideazaTonajAdresaNoua();
			return false;
		} else
			return false;

	}

	private boolean isAdresaText() {
		return radioText != null && radioText.isChecked();
	}

	private boolean hasCoordinates() {
		if (DateLivrare.getInstance().getCoordonateAdresa() == null)
			return false;
		else if (DateLivrare.getInstance().getCoordonateAdresa().latitude == 0)
			return false;

		return true;
	}

	private boolean isConditiiTonaj(Spinner spinnerTransp, Spinner spinnerTonaj) {
		return spinnerTransp.getSelectedItem().toString().toLowerCase().contains("arabesque") && spinnerTonaj.getSelectedItemPosition() > 0;

	}

	private boolean isAdresaGoogleOk() {

		LatLng addressCoordinates = null;
		boolean isAdresaOk = true;

		BeanLocalitate beanLocalitate = new BeanLocalitate();

		if (isAdresaText()) {
			GeocodeAddress geoAddress = MapUtils.geocodeAddress(getAddressFromForm(), getApplicationContext());
			DateLivrare.getInstance().setCoordonateAdresa(geoAddress.getCoordinates());
			addressCoordinates = geoAddress.getCoordinates();
			beanLocalitate = HelperAdreseLivrare.getDateLocalitate(listAdreseJudet.getListLocalitati(), DateLivrare.getInstance().getOras());
			isAdresaOk = geoAddress.isAdresaValida();

		} else {
			beanLocalitate.setOras(adresaLivrareSelected.isOras());
			beanLocalitate.setRazaKm(adresaLivrareSelected.getRazaKm());
			beanLocalitate.setCoordonate(adresaLivrareSelected.getCoordsCentru());

			addressCoordinates = DateLivrare.getInstance().getCoordonateAdresa();
		}

		if (beanLocalitate.isOras()) {
			isAdresaOk = HelperAdreseLivrare.isDistantaCentruOk(getApplicationContext(), beanLocalitate, addressCoordinates);

		}

		return isAdresaOk;

	}

	private Address getAddressFromForm() {
		Address address = new Address();

		address.setCity(DateLivrare.getInstance().getOras());
		address.setStreet(UtilsAddress.getStreetNoNumber(DateLivrare.getInstance().getStrada()));
		address.setSector(UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudet()));

		return address;
	}

	private void setAdresalivrareFiliala(String adresaFiliala) {
		String[] tokenAdresa = adresaFiliala.split("#");

		DateLivrare.getInstance().setNumeJudet(tokenAdresa[3]);
		DateLivrare.getInstance().setCodJudet(tokenAdresa[2]);
		DateLivrare.getInstance().setOras(tokenAdresa[1]);
		DateLivrare.getInstance().setStrada(tokenAdresa[0]);
		DateLivrare.getInstance().setCoordonateAdresa(new LatLng(Double.valueOf(tokenAdresa[4]), Double.valueOf(tokenAdresa[5])));
		spinnerTonaj.setSelection(1);
	}

	private void setAdresaLivrareFromList(BeanAdresaLivrare adresaLivrare) {

		adresaLivrareSelected = adresaLivrare;

		DateLivrare.getInstance().setAddrNumber(adresaLivrare.getCodAdresa());
		DateLivrare.getInstance().setNumeJudet(UtilsGeneral.getNumeJudet(adresaLivrare.getCodJudet()));

		if (!adresaLivrare.getOras().trim().equals(""))
			DateLivrare.getInstance().setOras(adresaLivrare.getOras());
		else
			DateLivrare.getInstance().setOras("-");

		if (!adresaLivrare.getStrada().trim().equals("")) {
			DateLivrare.getInstance().setStrada(adresaLivrare.getStrada() + " " + adresaLivrare.getNrStrada());

		} else {
			DateLivrare.getInstance().setStrada("-");

		}

		DateLivrare.getInstance().setCodJudet(adresaLivrare.getCodJudet());
		DateLivrare.getInstance().setAdrLivrNoua(false);

		String[] tokenCoords = adresaLivrare.getCoords().split(",");

		DateLivrare.getInstance().setCoordonateAdresa(new LatLng(Double.valueOf(tokenCoords[0]), Double.valueOf(tokenCoords[1])));

		setSpinnerTonajValue(adresaLivrare.getTonaj());

		getFilialaLivrareMathaus();

	}

	private void setAdresaLivrare(Address address) {

		textLocalitate.getText().clear();
		textStrada.getText().clear();
		textNrStr.getText().clear();

		int nrJudete = spinnerJudet.getAdapter().getCount();

		for (int j = 0; j < nrJudete; j++) {
			HashMap<String, String> artMapLivr = (HashMap<String, String>) this.adapterJudete.getItem(j);
			String numeJudet = artMapLivr.get("numeJudet").toString();

			if (address.getSector().equals(numeJudet)) {
				spinnerJudet.setSelection(j);
				break;
			}

		}

		if (address.getCity() != null && !address.getCity().isEmpty())
			textLocalitate.setText(address.getCity());

		if (address.getStreet() != null && !address.getStreet().isEmpty() && !address.getStreet().toUpperCase().contains("UNNAMED"))
			textStrada.setText(address.getStreet().trim());

		if (address.getNumber() != null && address.getNumber().length() > 0)
			textNrStr.setText(address.getNumber());

	}

	private void valideazaAdresaLivrare() {

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("codJudet", DateLivrare.getInstance().getCodJudet());
		params.put("localitate", DateLivrare.getInstance().getOras());

		operatiiAdresa.isAdresaValida(params, EnumLocalitate.LOCALITATE_SEDIU);

	}

	private void setListenerSpinnerAdreseLivrare() {
		spinnerAdreseLivrare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				BeanAdresaLivrare adresaLivrare = (BeanAdresaLivrare) spinnerAdreseLivrare.getAdapter().getItem(position);
				setAdresaLivrareFromList(adresaLivrare);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void clearAdresaLivrare() {
		DateLivrare.getInstance().setOras("");
		DateLivrare.getInstance().setStrada("");
		DateLivrare.getInstance().setCodJudet("");
		DateLivrare.getInstance().setCoordonateAdresa(null);

		spinnerJudet.setSelection(0);
		textLocalitate.setText("");
		textNrStr.setText("");
		textStrada.setText("");

	}

	private void setAdresaLivrareFromObiectiv() {

		if (radioObiectiv.getVisibility() == View.VISIBLE)
			if (radioObiectiv.isChecked()) {
				DateLivrare.getInstance().setAdresaObiectiv(true);
			} else {
				DateLivrare.getInstance().setAdresaObiectiv(false);
			}

		if (spinnerObiective.getSelectedItemPosition() > 0) {
			String[] arrObiectiv = obiectivSelectat.getAdresa().split("/");

			DateLivrare.getInstance().setIdObiectiv(obiectivSelectat.getId());

			if (radioObiectiv.isChecked()) {
				DateLivrare.getInstance().setCodJudet(arrObiectiv[0]);
				DateLivrare.getInstance().setNumeJudet(UtilsGeneral.getNumeJudet(arrObiectiv[0]));
				DateLivrare.getInstance().setOras(arrObiectiv[1]);
				if (arrObiectiv.length == 3)
					DateLivrare.getInstance().setStrada(arrObiectiv[2]);
				else
					DateLivrare.getInstance().setStrada("-");
			}

		}

	}

	@Override
	public void onBackPressed() {
		finish();
		return;
	}

	private void valideazaAdresaResponse(String result) {
		valideazaDateLivrare();

	}

	public void onTaskComplete(String methodName, Object result) {
		if (methodName.equals(METHOD_NAME)) {
			afisAdreseLivrareTCLI((String) result);
		}

	}

	public void operatiiAdresaComplete(EnumOperatiiAdresa numeComanda, Object result, EnumLocalitate tipLocalitate) {

		switch (numeComanda) {
		case GET_ADRESE_JUDET:
			populateListLocalitati(operatiiAdresa.deserializeListAdrese(result));
			break;
		case IS_ADRESA_VALIDA:
			valideazaAdresaResponse((String) result);
			break;
		case GET_DATE_LIVRARE:
			fillDateLivrare((String) result);
			break;
		case GET_ADRESE_LIVR_CLIENT:
			fillListAdrese((String) result);
			break;
		case GET_LOCALITATI_LIVRARE_RAPIDA:
			HelperAdreseLivrare.setLocalitatiAcceptate((String) result);
			break;
		case GET_FILIALA_MATHAUS:
			CreareComanda.filialaLivrareMathaus = ((String) result).split(",")[0];
			CreareComanda.filialeArondateMathaus = (String) result;
			break;
		case GET_ADRESA_FILIALA:
			setAdresalivrareFiliala((String) result);
			break;
		default:
			break;
		}

	}

	public void operationObiectivComplete(EnumOperatiiObiective numeComanda, Object result) {
		switch (numeComanda) {
		case GET_OBIECTIVE_DEPARTAMENT:
			displayObiectiveDepartament(operatiiObiective.deserializeObiectiveDepart((String) result));
			break;
		default:
			break;
		}

	}

	@Override
	public void addressSelected(LatLng coord, android.location.Address address) {
		DateLivrare.getInstance().setCoordonateAdresa(coord);
		setAdresaLivrare(MapUtils.getAddress(address));
		valideazaTonajAdresaNoua();

	}

	@Override
	public void selectionComplete(String selectedItem, int actionId) {

		switch (actionId) {
		case LIST_LOCALITATI:
			textLocalitate.setText(selectedItem);
			break;
		case LIST_ADRESE:
			textStrada.setText(selectedItem);
			break;
		default:
			break;
		}

	}

}
