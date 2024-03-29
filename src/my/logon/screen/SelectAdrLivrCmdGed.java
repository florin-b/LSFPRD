/**
 * @author florinb
 *
 */
package my.logon.screen;

import helpers.HelperAdreseLivrare;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import listeners.AsyncTaskListener;
import listeners.CautaObiectivListener;
import listeners.MapListener;
import listeners.OperatiiAdresaListener;
import listeners.OperatiiClientListener;
import model.DateLivrare;
import model.ListaArticoleComandaGed;
import model.OperatiiAdresa;
import model.OperatiiAdresaImpl;
import model.OperatiiClient;
import model.UserInfo;
import utils.MapUtils;
import utils.UtilsAddress;
import utils.UtilsComenzi;
import utils.UtilsDates;
import utils.UtilsGeneral;
import utils.UtilsUser;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import beans.Address;
import beans.BeanAdresaLivrare;
import beans.BeanAdreseJudet;
import beans.BeanClient;
import beans.BeanDateLivrareClient;
import beans.BeanLocalitate;
import beans.GeocodeAddress;
import beans.ObiectivConsilier;
import beans.StatusIntervalLivrare;

import com.google.android.gms.maps.model.LatLng;

import dialogs.CautaObiectivDialog;
import dialogs.MapAddressDialog;
import dialogs.SelectDateDialog;
import enums.EnumClienti;
import enums.EnumJudete;
import enums.EnumLocalitate;
import enums.EnumOperatiiAdresa;
import enums.TipCmdGed;

public class SelectAdrLivrCmdGed extends Activity implements AsyncTaskListener, OperatiiClientListener, OperatiiAdresaListener, MapListener,
		CautaObiectivListener {

	private Button saveAdrLivrBtn;
	private EditText txtPers, txtTel, txtObservatii;

	private TextView txtObsPlata, textMail;

	private static final String METHOD_NAME = "getClientJud";

	String[] tipTransport = { "TRAP - Transport Arabesque", "TCLI - Transport client", "TFRN - Transport furnizor" };

	String[] tipTransportIP = { "TRAP - Transport Arabesque", "TCLI - Transport client", "TERT - Transport tert", "TFRN - Transport furnizor" };

	String[] tipTransportOnline = { "TRAP - Transport Arabesque", "TCLI - Transport client", "TERT - Transport tert", "TFRN - Transport furnizor" };

	String[] docInsot = { "Factura", "Aviz de expeditie" };

	private String[] tipPlataContract = { "LC - Limita credit", "N - Numerar in filiala", "OPA - OP avans", "R - ramburs" };
	private String[] tipPlataClBlocatIP = { "N - Numerar in filiala", "OPA - OP avans", "R - ramburs" };
	private String[] tipPlataClBlocatNonIP = { "C - Card bancar", "N - Numerar in filiala", "OPA - OP avans", "R - Ramburs" };
	private String[] tipPlataRestIP = { "C - Card bancar", "N - Numerar in filiala", "OPA - OP avans", "R - Ramburs" };
	private String[] tipPlataRestNonIP = { "C - Card bancar", "N - Numerar in filiala", "OPA - OP avans", "R - Ramburs" };
	private String[] tipPlataDL = { "C - Card bancar", "N - Numerar in filiala", "OPA - OP avans" };

	public SimpleAdapter adapterJudete, adapterJudeteLivrare, adapterAdreseLivrare;

	private Spinner spinnerPlata, spinnerTransp, spinnerJudet, spinnerTermenPlata, spinnerJudetLivrare;
	private static ArrayList<HashMap<String, String>> listJudete = null, listJudeteLivrare = null;
	private ArrayAdapter<String> adapterTermenPlata;
	private LinearLayout layoutAdr1, layoutAdr2, layoutMail;

	int posJudetSel = 0;
	private NumberFormat nf;
	String articoleSite, umSite, cantSite, valSite;
	private String codJudetLivrare = "";
	private Spinner spinnerAdreseLivrare;

	public static RadioButton radioAdresaSediu, radioAltaAdresa, radioAdresaObiectiv;
	LinearLayout layoutAdrLivrare1, layoutAdrLivrare2;

	private ArrayList<HashMap<String, String>> listAdreseLivrare = null;
	private OperatiiClient operatiiClient;
	private List<BeanAdresaLivrare> listAdrese = null;
	private RadioButton radioListAdrese, radioTextAdrese;
	private LinearLayout layoutHeaderAdrese, layoutListAdrese, layoutAdrOras, layoutAdrStrada;
	private CheckBox checkMacara, chkbClientLaRaft;
	private OperatiiAdresa operatiiAdresa;
	private AutoCompleteTextView textLocalitate, textStrada, textLocalitateLivrare, textStradaLivrare;
	private Button btnPozitieAdresa;
	private EditText textNrStr;
	private Spinner spinnerIndoire, spinnerTonaj;
	private TextView textDataLivrare;
	private Button btnDataLivrare;
	private Spinner spinnerMeseriasi;
	private CheckBox checkFactPaleti;
	private CheckBox chkCamionDescoperit;
	private Spinner spinnerProgramLivrare;
	private CheckBox checkObsSofer;
	private List<String> listLocalitatiSediu;
	private List<String> listLocalitatiLivrare;

	private Button btnSelectObiectiv;
	private TextView textObiectivSelectat;
	private ObiectivConsilier obiectivSelectat;
	private ImageButton btnStergeObiectiv;
	private BeanDateLivrareClient dateLivrareClient;

	private CheckBox checkFactura, checkAviz;
	private BeanAdreseJudet listAdreseJudet, listAlteAdrese;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.selectadrlivrcmd_ged_header);

		try {

			ActionBar actionBar = getActionBar();
			actionBar.setTitle("Date livrare");
			actionBar.setDisplayHomeAsUpEnabled(true);

			nf = NumberFormat.getInstance();
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);

			this.saveAdrLivrBtn = (Button) findViewById(R.id.saveAdrLivrBtn);
			addListenerSaveAdr();

			this.layoutAdr1 = (LinearLayout) findViewById(R.id.layoutAdr1);
			this.layoutAdr2 = (LinearLayout) findViewById(R.id.layoutAdr2);

			textLocalitate = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocalitate);

			textNrStr = (EditText) findViewById(R.id.textNrStr);

			btnPozitieAdresa = (Button) findViewById(R.id.btnPozitieAdresa);
			setListnerBtnPozitieAdresa();

			chkbClientLaRaft = (CheckBox) findViewById(R.id.clientRaft);
			addListenerClientLaRaft();

			Bundle bundle = getIntent().getExtras();

			if (bundle.getString("parrentClass") != null && bundle.getString("parrentClass").equals("ModificareComanda")
					&& !DateLivrare.getInstance().getTipPersClient().equals("PF")) {
				LinearLayout layoutRaft = (LinearLayout) findViewById(R.id.layoutClientRaft);
				layoutRaft.setVisibility(View.GONE);

				if ((Double.parseDouble(bundle.getString("limitaCredit")) > 1) && !bundle.getString("termenPlata").equals("C000")) {
					CreareComandaGed.tipPlataContract = bundle.getString("tipPlataContract");
					DateLivrare.getInstance().setTipPlata("LC");
				}

			}

			textLocalitateLivrare = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocLivrare);

			textStrada = (AutoCompleteTextView) findViewById(R.id.autoCompleteStrada);
			textStrada.setText(UtilsAddress.getStreetFromAddress(DateLivrare.getInstance().getStrada()));

			textNrStr.setText(UtilsAddress.getStreetNumber(DateLivrare.getInstance().getStrada()));

			textStradaLivrare = (AutoCompleteTextView) findViewById(R.id.autoCompleteStradaLivrare);

			layoutMail = (LinearLayout) findViewById(R.id.layoutMail);
			textMail = (EditText) findViewById(R.id.txtMail);

			DateLivrare dateLivrareInstance = DateLivrare.getInstance();

			txtPers = (EditText) findViewById(R.id.txtPersCont);
			txtTel = (EditText) findViewById(R.id.txtTelefon);
			txtPers.setText(dateLivrareInstance.getPersContact());
			txtTel.setText(dateLivrareInstance.getNrTel());

			checkFactura = (CheckBox) findViewById(R.id.checkFactura);
			setListenerCheckFactura();
			checkAviz = (CheckBox) findViewById(R.id.checkAviz);
			setListenerCheckAviz();

			checkObsSofer = (CheckBox) findViewById(R.id.chkObsSofer);
			setListenerCheckObsSofer();

			txtObservatii = (EditText) findViewById(R.id.txtObservatii);

			if (DateLivrare.getInstance().getObsLivrare() != null && !DateLivrare.getInstance().getObsLivrare().trim().isEmpty()) {
				txtObservatii.setText(dateLivrareInstance.getObsLivrare());
				checkObsSofer.setChecked(true);
			}

			txtObsPlata = (EditText) findViewById(R.id.txtObsPlata);
			txtObsPlata.setText(dateLivrareInstance.getObsPlata());

			chkbClientLaRaft.setChecked(dateLivrareInstance.isClientRaft());

			spinnerPlata = (Spinner) findViewById(R.id.spinnerPlata);

			ArrayAdapter<String> adapterSpinnerPlata;
			ArrayAdapter<String> adapterSpinnerTransp;

			layoutMail.setVisibility(View.VISIBLE);
			textMail.setText(dateLivrareInstance.getMail().trim().replace("~", "@"));

			setTipTransportOptions();

			if (UserInfo.getInstance().getUserSite().equals("X") || UtilsUser.isConsWood() || isComandaClp()) {
				adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipTransportOnline);

			} else if (UtilsUser.isUserIP())
				adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipTransportIP);
			else {
				adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipTransport);
			}
			
            if (DateLivrare.getInstance().getTipComandaGed().equals(TipCmdGed.ARTICOLE_COMANDA)) {
                List<String> itemsTransp = new ArrayList<String>();
                for (int ii = 0; ii < adapterSpinnerTransp.getCount(); ii++) {
                    if (!adapterSpinnerTransp.getItem(ii).startsWith("TFRN")) {
                        itemsTransp.add( (String) adapterSpinnerTransp.getItem(ii));
                    }
                }
                adapterSpinnerTransp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsTransp);
            }

			if (DateLivrare.getInstance().isClientBlocat()) {
				if (CreareComandaGed.tipClient.equals("IP"))
					adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataClBlocatIP);
				else
					adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataClBlocatNonIP);

			} else {

				if (!CreareComandaGed.tipPlataContract.trim().isEmpty() || DateLivrare.getInstance().getTipPlata().equals("LC")) {

					if (!CreareComandaGed.tipPlataContract.trim().isEmpty()) {
						((TextView) findViewById(R.id.tipPlataContract)).setText("Contract: " + CreareComandaGed.tipPlataContract);
					}

					adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataContract);
				} else if (CreareComandaGed.tipClient.equals("IP"))
					adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataRestIP);
				else
					adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipPlataRestNonIP);

			}

			if (isComandaDl()) {
				List<String> metsPlata = new ArrayList<String>();

				for (int ii = 0; ii < adapterSpinnerPlata.getCount(); ii++)
					if (!adapterSpinnerPlata.getItem(ii).toString().startsWith("R"))
						metsPlata.add(adapterSpinnerPlata.getItem(ii));

				adapterSpinnerPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, metsPlata.toArray(new String[0]));

			}

			adapterSpinnerPlata.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerPlata.setAdapter(adapterSpinnerPlata);

			UtilsComenzi.setDefaultPlataMethod(spinnerPlata);

			addListenerTipPlata();

			checkMacara = (CheckBox) findViewById(R.id.checkMacara);
			setMacaraVisible();
			setListenerCheckMacara();

			checkFactPaleti = (CheckBox) findViewById(R.id.chkFactPaleti);
			checkFactPaleti.setChecked(DateLivrare.getInstance().isFactPaletSeparat());

			chkCamionDescoperit = (CheckBox) findViewById(R.id.chkCamionDescoperit);
			chkCamionDescoperit.setChecked(DateLivrare.getInstance().isCamionDescoperit());

			spinnerIndoire = (Spinner) findViewById(R.id.spinnerIndoire);
			setupSpinnerIndoire();

			spinnerTransp = (Spinner) findViewById(R.id.spinnerTransp);
			spinnerTonaj = (Spinner) findViewById(R.id.spinnerTonaj);
			setupSpinnerTonaj();

			adapterSpinnerTransp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerTransp.setAdapter(adapterSpinnerTransp);
			addListenerTipTransport();

			spinnerJudet = (Spinner) findViewById(R.id.spinnerJudet);
			spinnerJudet.setOnItemSelectedListener(new regionSelectedListener());

			spinnerJudet.setOnTouchListener(new regioClickListener());

			operatiiAdresa = new OperatiiAdresaImpl(this);
			operatiiAdresa.setOperatiiAdresaListener(this);

			listJudete = new ArrayList<HashMap<String, String>>();
			adapterJudete = new SimpleAdapter(this, listJudete, R.layout.rowlayoutjudete, new String[] { "numeJudet", "codJudet" }, new int[] {
					R.id.textNumeJudet, R.id.textCodJudet });

			spinnerTermenPlata = (Spinner) findViewById(R.id.spinnerTermenPlata);
			adapterTermenPlata = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			adapterTermenPlata.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerTermenPlata.setAdapter(adapterTermenPlata);

			if (CreareComandaGed.listTermenPlata != null && CreareComandaGed.listTermenPlata.size() > 0) {
				adapterTermenPlata.addAll(CreareComandaGed.listTermenPlata);

				if (UserInfo.getInstance().getTipUserSap().equals("CGED") || UtilsUser.isSSCM() || UtilsUser.isUserIP()) {
					spinnerTermenPlata.setSelection(CreareComandaGed.listTermenPlata.size() - 1);
				}

			} else {
				adapterTermenPlata.add("C000");
				if (DateLivrare.getInstance().getTermenPlata().trim().length() > 0) {
					String[] tokTermen = DateLivrare.getInstance().getTermenPlata().split(";");
					int nrLivr = 0;
					for (nrLivr = 0; nrLivr < tokTermen.length; nrLivr++) {
						if (!tokTermen[nrLivr].equals("C000"))
							adapterTermenPlata.add(tokTermen[nrLivr]);
					}

				}
			}

			addListenerTermenPlata();
			addAdresaLivrare();
			int i = 0;

			// document insotitor
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

			String strTipPlata = "";
			// tip plata

			for (i = 0; i < adapterSpinnerPlata.getCount(); i++) {

				strTipPlata = adapterSpinnerPlata.getItem(i).toString().substring(0, adapterSpinnerPlata.getItem(i).toString().indexOf("-") - 1)
						.trim();

				if (strTipPlata.equals(UtilsComenzi.setTipPlataClient(dateLivrareInstance.getTipPlata()))) {
					spinnerPlata.setSelection(i);
					break;
				}
			}

			// tip transport
			for (i = 0; i < adapterSpinnerTransp.getCount(); i++) {
				if (adapterSpinnerTransp.getItem(i).toString().substring(0, 4).equals(dateLivrareInstance.getTransport())) {
					spinnerTransp.setSelection(i);
					break;
				}
			}

			layoutAdr1.setVisibility(View.VISIBLE);
			layoutAdr2.setVisibility(View.VISIBLE);

			performGetJudete();

			radioListAdrese = (RadioButton) findViewById(R.id.radioListAdrese);
			radioTextAdrese = (RadioButton) findViewById(R.id.radioTextAdrese);

			layoutHeaderAdrese = (LinearLayout) findViewById(R.id.layoutHeaderAdrese);
			layoutListAdrese = (LinearLayout) findViewById(R.id.layoutListAdrese);

			layoutAdrOras = (LinearLayout) findViewById(R.id.layoutAdr1);
			layoutAdrStrada = (LinearLayout) findViewById(R.id.layoutAdr2);

			layoutHeaderAdrese.setVisibility(View.GONE);
			layoutListAdrese.setVisibility(View.GONE);

			operatiiClient = new OperatiiClient(this);
			operatiiClient.setOperatiiClientListener(SelectAdrLivrCmdGed.this);

			if (UserInfo.getInstance().getTipUserSap().equals("KA3") && DateLivrare.getInstance().getTipPersClient().equals("D")) {

				setupListAdreseLayout();
			}

			textDataLivrare = (TextView) findViewById(R.id.textDataLivrare);

			if (!dateLivrareInstance.getDataLivrare().isEmpty())
				textDataLivrare.setText(dateLivrareInstance.getDataLivrare());

			btnDataLivrare = (Button) findViewById(R.id.btnDataLivrare);
			addListenerDataLivrare();

			String tipUser;

			if (UtilsUser.isAgentOrSD())
				tipUser = "AV";
			else
				tipUser = "CV";

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("codFiliala", UserInfo.getInstance().getUnitLog());
			params.put("tipUser", tipUser);
			params.put("codUser", UserInfo.getInstance().getCod());
			params.put("codDepart", UserInfo.getInstance().getCodDepart());

			spinnerMeseriasi = (Spinner) findViewById(R.id.spinnerMeseriasi);
			operatiiClient.getMeseriasi(params);

			spinnerProgramLivrare = (Spinner) findViewById(R.id.spinnerProgramLivrare);
			if (!dateLivrareInstance.getProgramLivrare().equals("0"))
				spinnerProgramLivrare.setSelection(Integer.parseInt(dateLivrareInstance.getProgramLivrare()));

			textObiectivSelectat = (TextView) findViewById(R.id.textObiectivSelectat);

			if (!UtilsUser.isAgentOrSDorKA()) {

				((LinearLayout) findViewById(R.id.layoutObiectiveConsilieri)).setVisibility(View.VISIBLE);

				btnSelectObiectiv = (Button) findViewById(R.id.btnSelectObiectiv);
				setListenerSelectObiectiv();

				btnStergeObiectiv = (ImageButton) findViewById(R.id.btnStergeObiectiv);
				setListenerStergeObiectiv();

				if (DateLivrare.getInstance().isAdresaObiectiv()) {
					radioAdresaObiectiv.setVisibility(View.VISIBLE);
					radioAdresaObiectiv.setChecked(true);
				}

				if (DateLivrare.getInstance().getObiectivConsilier() != null) {
					textObiectivSelectat.setVisibility(View.VISIBLE);
					textObiectivSelectat.setText(DateLivrare.getInstance().getObiectivConsilier().getBeneficiar() + " / "
							+ DateLivrare.getInstance().getObiectivConsilier().getAdresa());
					btnStergeObiectiv.setVisibility(View.VISIBLE);
				}
			}

			if (UtilsUser.isCGED() || UtilsUser.isSSCM() || CreareComandaGed.tipClient.equals("IP")) {
				getDateLivrareClient();
			}

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	private void setTipPlataSelected(String tipPlata) {

		for (int i = 0; i < spinnerPlata.getAdapter().getCount(); i++) {
			if (spinnerPlata.getAdapter().getItem(i).toString().startsWith(tipPlata)) {
				spinnerPlata.setSelection(i);
				break;
			}
		}

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

	private void getDateLivrareClient() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codClient", CreareComandaGed.codClientVar);
		operatiiAdresa.getDateLivrareClient(params);

	}

	private void setListenerSelectObiectiv() {

		btnSelectObiectiv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CautaObiectivDialog obiectivDialog = new CautaObiectivDialog(SelectAdrLivrCmdGed.this);
				obiectivDialog.setObiectivListener(SelectAdrLivrCmdGed.this);
				obiectivDialog.show();

			}
		});

	}

	private void setAdresaLivrareObiectiv() {

		String[] tokenAdresa = obiectivSelectat.getAdresa().split(",");
		DateLivrare.getInstance().setCodJudetD(obiectivSelectat.getCodJudet());
		DateLivrare.getInstance().setOrasD(tokenAdresa[0]);

		if (obiectivSelectat.getCoordGps() != null && obiectivSelectat.getCoordGps().contains(",")) {

			String[] tokCoords = obiectivSelectat.getCoordGps().split(",");
			LatLng coordonateAdresa = new LatLng(Double.valueOf(tokCoords[0]), Double.valueOf(tokCoords[1]));
			DateLivrare.getInstance().setCoordonateAdresa(coordonateAdresa);

		}

		if (tokenAdresa.length == 2)
			DateLivrare.getInstance().setAdresaD(tokenAdresa[1].trim());
		else if (tokenAdresa.length == 3)
			DateLivrare.getInstance().setAdresaD(tokenAdresa[1].trim() + " , " + tokenAdresa[2].trim());
		else
			DateLivrare.getInstance().setAdresaD("-");

	}

	private void setListenerStergeObiectiv() {

		btnStergeObiectiv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				obiectivSelectat = null;
				textObiectivSelectat.setText("");
				radioAdresaObiectiv.setVisibility(View.GONE);
				radioAdresaSediu.setChecked(true);
				btnStergeObiectiv.setVisibility(View.GONE);
				textObiectivSelectat.setVisibility(View.GONE);
				DateLivrare.getInstance().setIdObiectiv("");
				DateLivrare.getInstance().setAdresaObiectiv(false);

			}
		});

	}

	private void setListenerCheckObsSofer() {
		checkObsSofer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					txtObservatii.setVisibility(View.VISIBLE);
					checkObsSofer.setText("Da");
					txtObservatii.setFocusableInTouchMode(true);
					txtObservatii.requestFocus();
				} else {
					checkObsSofer.setText("Nu");
					txtObservatii.setText("");
					txtObservatii.setVisibility(View.INVISIBLE);
				}

			}
		});
	}

	private void addListenerDataLivrare() {
		btnDataLivrare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Locale.setDefault(new Locale("ro"));

				int year = Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH);
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				SelectDateDialog datePickerDialog = new SelectDateDialog(SelectAdrLivrCmdGed.this, datePickerListener, year, month, day);
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

	private void setupListAdreseLayout() {

		layoutAdrOras.setVisibility(View.GONE);
		layoutAdrStrada.setVisibility(View.GONE);

		layoutHeaderAdrese.setVisibility(View.VISIBLE);
		layoutListAdrese.setVisibility(View.VISIBLE);

		setListenerRadioLista();

		setListenerRadioText();

		spinnerAdreseLivrare = (Spinner) findViewById(R.id.spinnerAdreseLivrare);
		getAdreseLivrareClient();
		setSpinnerAdreseItemSelectedListener();

		spinnerAdreseLivrare.setOnTouchListener(new SpinnerAdreseTouchListener());

	}

	private void setTipTransportOptions() {
		if (UtilsUser.isUserExceptieBV90Ged()) {
			List<String> tempArray = new ArrayList<String>(Arrays.asList(tipTransport));
			tempArray.add("TERT - Transport tert");
			tipTransport = tempArray.toArray(new String[tempArray.size()]);

		}

	}

	private void setListenerCheckMacara() {
		checkMacara.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DateLivrare.getInstance().setMasinaMacara(isChecked);
			}

		});
	}

	public class SpinnerAdreseTouchListener implements View.OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (existaArticole()) {
					Toast.makeText(getApplicationContext(), "Stergeti mai intai toate articolele.", Toast.LENGTH_SHORT).show();
					return true;
				}
			}
			return false;
		}

	}

	private void setSpinnerAdreseItemSelectedListener() {
		spinnerAdreseLivrare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DateLivrare.getInstance().setCodJudet(listAdrese.get(arg2).getCodJudet());
				DateLivrare.getInstance().setOras(listAdrese.get(arg2).getOras());
				DateLivrare.getInstance().setStrada(listAdrese.get(arg2).getOras());

			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void getAdreseLivrareClient() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codClient", CreareComandaGed.codClientVar);
		operatiiClient.getAdreseLivrareClient(params);

	}

	private void setListenerRadioLista() {
		radioListAdrese.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radioListAdrese.isChecked()) {
					layoutListAdrese.setVisibility(View.VISIBLE);
					layoutAdrOras.setVisibility(View.GONE);
					layoutAdrStrada.setVisibility(View.GONE);
				}

			}
		});
	}

	private void setListenerRadioText() {
		radioTextAdrese.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (radioTextAdrese.isChecked()) {
					layoutListAdrese.setVisibility(View.GONE);
					layoutAdrOras.setVisibility(View.VISIBLE);
					layoutAdrStrada.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	private void setupSpinnerTonaj() {

		String[] tonajValues = { "Selectati tonajul", "3.5 T", "10 T", "Fara restrictie de tonaj" };

		ArrayAdapter<String> adapterTonaj = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tonajValues);
		adapterTonaj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTonaj.setAdapter(adapterTonaj);

		if (DateLivrare.getInstance().getTonaj() != null)
			for (int i = 0; i < spinnerTonaj.getCount(); i++)
				if (spinnerTonaj.getItemAtPosition(i).toString().toUpperCase().contains(DateLivrare.getInstance().getTonaj())) {
					spinnerTonaj.setSelection(i);
					break;
				}

	}

	private void setupSpinnerIndoire() {

		String[] indoireValues = { "Tip prelucrare fier-beton 6 m", "TAIERE", "INDOIRE" };

		ArrayAdapter<String> adapterIndoire = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, indoireValues);
		adapterIndoire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIndoire.setAdapter(adapterIndoire);

	}

	private void addAdresaLivrare() {

		radioAdresaSediu = (RadioButton) findViewById(R.id.radioAdresaSediu);
		radioAltaAdresa = (RadioButton) findViewById(R.id.radioAltaAdresa);
		radioAdresaObiectiv = (RadioButton) findViewById(R.id.radioAdresaObiectiv);

		addListenerRadioAdresaSediu();
		addListenerRadioAltaAdresa();
		addListenerRadioAdresaObiectiv();

		layoutAdrLivrare1 = (LinearLayout) findViewById(R.id.layoutAdrLivrare1);
		layoutAdrLivrare1.setVisibility(View.GONE);
		layoutAdrLivrare2 = (LinearLayout) findViewById(R.id.layoutAdrLivrare2);
		layoutAdrLivrare2.setVisibility(View.GONE);

		spinnerJudetLivrare = (Spinner) findViewById(R.id.spinnerJudetLivrare);
		spinnerJudetLivrare.setOnItemSelectedListener(new regionLivrareSelectedListener());

		if (DateLivrare.getInstance().isAltaAdresa() || !DateLivrare.getInstance().getOrasD().trim().isEmpty()) {
			radioAltaAdresa.setChecked(true);

			textLocalitateLivrare.setText(DateLivrare.getInstance().getOrasD());
			textStradaLivrare.setText(DateLivrare.getInstance().getAdresaD());
			setJudetLivrare();
		}

	}

	private void setJudetLivrare() {

		if (DateLivrare.getInstance().getCodJudetD() != null) {

			listJudeteLivrare = new ArrayList<HashMap<String, String>>();

			adapterJudeteLivrare = new SimpleAdapter(this, listJudeteLivrare, R.layout.rowlayoutjudete, new String[] { "numeJudet", "codJudet" },
					new int[] { R.id.textNumeJudet, R.id.textCodJudet });

			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("numeJudet", UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudetD()));
			temp.put("codJudet", DateLivrare.getInstance().getCodJudetD());
			listJudeteLivrare.add(temp);

			spinnerJudetLivrare.setAdapter(adapterJudeteLivrare);
			spinnerJudetLivrare.setSelection(0);

		}

	}

	private void addJudetLivrare() {

		listJudeteLivrare = new ArrayList<HashMap<String, String>>(listJudete);

		adapterJudeteLivrare = new SimpleAdapter(this, listJudeteLivrare, R.layout.rowlayoutjudete, new String[] { "numeJudet", "codJudet" },
				new int[] { R.id.textNumeJudet, R.id.textCodJudet });

		spinnerJudetLivrare.setAdapter(adapterJudeteLivrare);
		spinnerJudetLivrare.setSelection(0);

		textStradaLivrare.setText("");

	}

	private void addListenerRadioAltaAdresa() {

		radioAltaAdresa.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (existaArticole()) {
					Toast.makeText(getApplicationContext(), "Stergeti mai intai toate articolele.", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		radioAltaAdresa.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {

					layoutAdrLivrare1.setVisibility(View.VISIBLE);
					layoutAdrLivrare2.setVisibility(View.VISIBLE);
					DateLivrare.getInstance().setAltaAdresa(true);
					addJudetLivrare();

				}

			}
		});

	}

	private void addListenerRadioAdresaSediu() {

		radioAdresaSediu.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (existaArticole()) {
					Toast.makeText(getApplicationContext(), "Stergeti toate articolele", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		radioAdresaSediu.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				if (arg1) {

					layoutAdrLivrare1.setVisibility(View.GONE);
					layoutAdrLivrare2.setVisibility(View.GONE);
					DateLivrare.getInstance().setCodJudetD(null);
					DateLivrare.getInstance().setOrasD(null);
					DateLivrare.getInstance().setAdresaD(null);
					DateLivrare.getInstance().setAltaAdresa(false);

				}

			}
		});

	}

	private void addListenerRadioAdresaObiectiv() {
		radioAdresaObiectiv.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					layoutAdrLivrare1.setVisibility(View.GONE);
					layoutAdrLivrare2.setVisibility(View.GONE);

					DateLivrare.getInstance().setAdresaObiectiv(true);
					DateLivrare.getInstance().setAltaAdresa(true);

				} else {
					DateLivrare.getInstance().setAdresaObiectiv(false);

				}

			}
		});
	}

	private void performGetJudete() {

		if (UtilsUser.isUserSite() || CreareComandaGed.tipClient.equals("IP") || !DateLivrare.getInstance().getCodJudet().isEmpty() || isComandaClp()
				|| isComandaDl() || UtilsUser.isUserIP()) {
			fillJudeteClient(EnumJudete.getRegionCodes());

		} else {
			String unitLog = UserInfo.getInstance().getUnitLog();

			if (unitLog.equals("NN10"))
				unitLog = "AG10";

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("filiala", unitLog);

			AsyncTaskWSCall call = new AsyncTaskWSCall(this, METHOD_NAME, params);
			call.getCallResultsSyncActivity();
		}

	}

	private boolean isComandaClp() {
		return DateLivrare.getInstance().getCodFilialaCLP() != null && DateLivrare.getInstance().getCodFilialaCLP().length() == 4;
	}

	private boolean isComandaDl() {
		return DateLivrare.getInstance().getFurnizorComanda() != null
				&& !DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa().isEmpty()
				&& DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa().length() > 4;
	}

	private void fillJudeteClient(String arrayJudete) {

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

	private void addListenerTermenPlata() {
		spinnerTermenPlata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DateLivrare.getInstance().setTermenPlata(spinnerTermenPlata.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void addListenerTipPlata() {

		spinnerPlata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				String rawTipPlataStr = spinnerPlata.getSelectedItem().toString();
				DateLivrare.getInstance().setTipPlata(rawTipPlataStr.substring(0, rawTipPlataStr.indexOf("-") - 1).trim());
				((TextView) findViewById(R.id.tipPlataContract)).setVisibility(View.INVISIBLE);
				spinnerTermenPlata.setEnabled(true);

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("N")
						|| spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("R")) {
					spinnerTermenPlata.setSelection(0);
					spinnerTermenPlata.setEnabled(false);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("O")) {
					spinnerTermenPlata.setSelection(0);
					spinnerTermenPlata.setEnabled(false);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("C")) {
					spinnerTermenPlata.setSelection(0);
					spinnerTermenPlata.setEnabled(false);
				}

				if (spinnerPlata.getSelectedItem().toString().substring(0, 1).equals("L")) {
					spinnerTermenPlata.setSelection(adapterTermenPlata.getCount() - 1);
					((TextView) findViewById(R.id.tipPlataContract)).setVisibility(View.VISIBLE);
				}

				if (pos == 0 || pos == 1 || pos == 2 || pos == 3) {
					if (spinnerTermenPlata != null)
						spinnerTermenPlata.setVisibility(View.VISIBLE);
				}

				if (pos == 2 || rawTipPlataStr.contains("E1")) {

					if (DateLivrare.getInstance().getTipPersClient().equals("PJ"))
						Toast.makeText(getApplicationContext(), "Valoare maxima comanda: 5000 RON", Toast.LENGTH_SHORT).show();

				}

				if (rawTipPlataStr.toLowerCase().contains("numerar") || rawTipPlataStr.toLowerCase().contains("ramburs")) {
					checkAviz.setChecked(false);
					checkAviz.setEnabled(false);
				} else
					checkAviz.setEnabled(true);

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void addListenerTipTransport() {
		spinnerTransp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if (pos == 0) {
					spinnerTonaj.setVisibility(View.VISIBLE);

				} else if (pos == 1) {
					DateLivrare.getInstance().setValTransport(0);
					DateLivrare.getInstance().setValTransportSAP(0);
					DateLivrare.getInstance().setMasinaMacara(false);
					checkMacara.setVisibility(View.INVISIBLE);
					spinnerTonaj.setVisibility(View.INVISIBLE);
					spinnerTonaj.setSelection(0);

					if (spinnerPlata.getSelectedItem().toString().contains("E1"))
						spinnerPlata.setSelection(0);

				}

				else {
					checkMacara.setChecked(DateLivrare.getInstance().isMasinaMacara());
					setMacaraVisible();
					spinnerTonaj.setVisibility(View.INVISIBLE);
					spinnerTonaj.setSelection(0);
				}

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void setMacaraVisible() {

		checkMacara.setVisibility(View.INVISIBLE);

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

	private class regionSelectedListener implements OnItemSelectedListener {

		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if (spinnerJudet.getAdapter().getCount() == 1)
				return;

			if (spinnerJudet.getSelectedItemPosition() > 0) {

				HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudet.getSelectedItem();
				DateLivrare.getInstance().setNumeJudet(tempMap.get("numeJudet"));
				DateLivrare.getInstance().setCodJudet(tempMap.get("codJudet"));

				HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
				params.put("codJudet", DateLivrare.getInstance().getCodJudet());
				operatiiAdresa.getAdreseJudet(params, EnumLocalitate.LOCALITATE_SEDIU);

			}

		}

		public void onNothingSelected(AdapterView<?> parent) {
			return;
		}

	}

	private void populateListLocSediu(BeanAdreseJudet listAdrese) {

		listAdreseJudet = listAdrese;

		textLocalitate.setVisibility(View.VISIBLE);
		textLocalitate.setText(DateLivrare.getInstance().getOras());

		String[] arrayLocalitati = listAdrese.getListStringLocalitati().toArray(new String[listAdrese.getListStringLocalitati().size()]);
		ArrayAdapter<String> adapterLoc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLocalitati);

		textLocalitate.setThreshold(0);
		textLocalitate.setAdapter(adapterLoc);
		listLocalitatiSediu = listAdrese.getListStringLocalitati();
		setListenerTextLocalitate();

		String[] arrayStrazi = listAdrese.getListStrazi().toArray(new String[listAdrese.getListStrazi().size()]);
		ArrayAdapter<String> adapterStrazi = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayStrazi);

		textStrada.setThreshold(0);
		textStrada.setAdapter(adapterStrazi);
		textStrada.setText(UtilsAddress.getStreetFromAddress(DateLivrare.getInstance().getStrada()));
		textNrStr.setText(UtilsAddress.getStreetNumber(DateLivrare.getInstance().getStrada()));

		setListenerTextStrada();

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
					verificaLocalitate("SEDIU");

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

		if (tipLocalitate.equals("SEDIU")) {
			textLoc = textLocalitate;
			localitateCurenta = textLoc.getText().toString().trim();
			listLocalitati = listLocalitatiSediu;

			@SuppressWarnings("unchecked")
			HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudet.getSelectedItem();
			numeJudet = tempMap.get("numeJudet");

		} else {
			textLoc = textLocalitateLivrare;
			localitateCurenta = textLoc.getText().toString().trim();
			listLocalitati = listLocalitatiLivrare;

			@SuppressWarnings("unchecked")
			HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudetLivrare.getSelectedItem();
			numeJudet = tempMap.get("numeJudet");
		}

		if (listLocalitati != null)
			for (String localitate : listLocalitati) {
				if (localitate.trim().equalsIgnoreCase(localitateCurenta)) {
					locExist = true;
					break;
				}

			}
		else
			locExist = true;

		if (!locExist && !localitateCurenta.isEmpty()) {
			String alert = "Localitatea " + localitateCurenta + " nu exista in judetul " + numeJudet + ". Completati alta localitate.";
			Toast.makeText(getApplicationContext(), alert, Toast.LENGTH_LONG).show();

			textLoc.setText("");
			textLoc.setFocusableInTouchMode(true);

		}

		return locExist;
	}

	private void setListenerTextStrada() {

		textStrada.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DateLivrare.getInstance().setStrada(textStrada.getText().toString().trim());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	private void populateListLocLivrare(BeanAdreseJudet listAdrese) {

		listAlteAdrese = listAdrese;

		textLocalitateLivrare.setVisibility(View.VISIBLE);
		textLocalitateLivrare.setText(DateLivrare.getInstance().getOrasD().trim());

		String[] arrayLocalitati = listAdrese.getListStringLocalitati().toArray(new String[listAdrese.getListStringLocalitati().size()]);
		ArrayAdapter<String> adapterLoc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLocalitati);

		textLocalitateLivrare.setThreshold(0);
		textLocalitateLivrare.setAdapter(adapterLoc);
		listLocalitatiLivrare = listAdrese.getListStringLocalitati();
		setListenerTextLocalitateLivrare();

		String[] arrayStrazi = listAdrese.getListStrazi().toArray(new String[listAdrese.getListStrazi().size()]);
		ArrayAdapter<String> adapterStrazi = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayStrazi);

		textStradaLivrare.setVisibility(View.VISIBLE);
		textStradaLivrare.setThreshold(0);
		textStradaLivrare.setAdapter(adapterStrazi);
		setListenerTextStradaLivrare();

	}

	private void setListenerTextLocalitateLivrare() {

		textLocalitateLivrare.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DateLivrare.getInstance().setOrasD(textLocalitateLivrare.getText().toString().trim());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		textLocalitateLivrare.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					textLocalitateLivrare.setText(textLocalitateLivrare.getText().toString().trim().toUpperCase());
					verificaLocalitate("LIVRARE");

				}

			}
		});

	}

	private void setListenerTextStradaLivrare() {

		textStradaLivrare.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				DateLivrare.getInstance().setAdresaD(textStradaLivrare.getText().toString().trim());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	public class regioClickListener implements View.OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (existaArticole()) {
					Toast.makeText(getApplicationContext(), "Stergeti mai intai toate articolele.", Toast.LENGTH_SHORT).show();
					return true;
				}
			}

			return false;
		}

	}

	public class regionLivrareSelectedListener implements OnItemSelectedListener {
		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if (spinnerJudetLivrare.getSelectedItemPosition() > 0) {

				HashMap<String, String> tempMap = (HashMap<String, String>) spinnerJudetLivrare.getSelectedItem();
				codJudetLivrare = tempMap.get("codJudet");

				DateLivrare.getInstance().setCodJudetD(codJudetLivrare);

				if (!codJudetLivrare.trim().equals("")) {
					HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
					params.put("codJudet", DateLivrare.getInstance().getCodJudetD());

					OperatiiAdresa operatiiAdresa1 = new OperatiiAdresaImpl(SelectAdrLivrCmdGed.this);
					operatiiAdresa1.setOperatiiAdresaListener(SelectAdrLivrCmdGed.this);

					operatiiAdresa1.getAdreseJudet(params, EnumLocalitate.LOCALITATE_LIVRARE);

				}
			}

		}

		public void onNothingSelected(AdapterView<?> parent) {
			return;
		}
	}

	private boolean existaArticole() {
		return ListaArticoleComandaGed.getInstance().getListArticoleComanda() != null
				&& ListaArticoleComandaGed.getInstance().getListArticoleComanda().size() > 0;
	}

	public void addListenerSaveAdr() {
		saveAdrLivrBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				valideazaAdresaLivrare();

			}
		});

	}

	private void valideazaDateLivrare() {
		String adresa = "";
		String strada = "";
		String pers = "";
		String telefon = "";
		String observatii = "", obsPlata = " ", strMailAddr = " ";

		String nrStrada = "";

		if (textNrStr.getText().toString().trim().length() > 0)
			nrStrada = " NR " + textNrStr.getText().toString().trim();

		strada = textStrada.getText().toString().trim() + " " + nrStrada;

		DateLivrare dateLivrareInstance = DateLivrare.getInstance();

		dateLivrareInstance.setStrada(strada);
		dateLivrareInstance.setAdrLivrNoua(true);
		dateLivrareInstance.setAddrNumber(" ");

		pers = txtPers.getText().toString().trim();
		telefon = txtTel.getText().toString().trim();
		observatii = txtObservatii.getText().toString().trim();
		obsPlata = txtObsPlata.getText().toString().trim();
		strMailAddr = textMail.getText().toString().trim();

		if (observatii.trim().length() == 0)
			observatii = " ";

		if (obsPlata.trim().length() == 0)
			obsPlata = " ";

		if (strMailAddr.trim().length() == 0)
			strMailAddr = " ";

		if (!(layoutListAdrese.getVisibility() == View.VISIBLE) && !(DateLivrare.getInstance().isAltaAdresa())) {

			verificaLocalitate("SEDIU");

			if (dateLivrareInstance.getCodJudet().equals("")) {
				Toast.makeText(getApplicationContext(), "Selectati judetul!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (DateLivrare.getInstance().getOras().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "Selectati localitatea!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (strada.trim().equals("") && !hasCoordinates()) {
				Toast.makeText(getApplicationContext(), "Completati strada sau pozitionati adresa pe harta!", Toast.LENGTH_SHORT).show();
				return;
			}

		}

		if (spinnerTransp.getSelectedItem().toString().toLowerCase().contains("tcli") && DateLivrare.getInstance().getTipPersClient().equals("PF")
				&& !DateLivrare.getInstance().isFacturaCmd()) {
			if (pers.equals(""))
				pers = " ";

			if (telefon.equals(""))
				telefon = " ";

		} else {
			if (pers.equals("")) {
				Toast.makeText(getApplicationContext(), "Completati persoana de contact!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (telefon.equals("")) {
				Toast.makeText(getApplicationContext(), "Completati nr. de telefon!", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (DateLivrare.getInstance().getDataLivrare().isEmpty()) {
			Toast.makeText(getApplicationContext(), "Selectati data livrare!", Toast.LENGTH_LONG).show();
			return;
		}

		if (spinnerTransp.getSelectedItem().toString().toLowerCase().contains("arabesque") && spinnerTonaj.getSelectedItemPosition() == 0) {
			Toast.makeText(getApplicationContext(), "Selectati tonajul!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (radioAltaAdresa.isChecked()) {

			verificaLocalitate("LIVRARE");

			if (DateLivrare.getInstance().getOrasD().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "Completati localitatea!", Toast.LENGTH_SHORT).show();
				return;
			}

		}

		if (spinnerProgramLivrare.getSelectedItemPosition() == 0 && spinnerTransp.getSelectedItem().toString().toLowerCase().contains("trap")) {
			Toast.makeText(getApplicationContext(), "Selectati perioada de livrare", Toast.LENGTH_SHORT).show();
			return;
		}

		dateLivrareInstance.setPersContact(pers);
		dateLivrareInstance.setNrTel(telefon);

		dateLivrareInstance.setTransport(spinnerTransp.getSelectedItem().toString().substring(0, 4));

		if (dateLivrareInstance.getTransport().equals("TCLI") && dateLivrareInstance.getTipPlata().equals("R")) {
			Toast.makeText(getApplicationContext(), "Pentru transport TCLI nu puteti selecta metoda de plata Ramburs.", Toast.LENGTH_LONG).show();
			return;
		}

		if (!isAdresaCorecta()) {
			Toast.makeText(getApplicationContext(), "Completati adresa corect sau pozitionati adresa pe harta.", Toast.LENGTH_LONG).show();
			return;
		}

		String cantar = "NU";

		dateLivrareInstance.setCantar("NU");

		String factRed = "NU";
		dateLivrareInstance.setRedSeparat(" ");

		String rawTipPlataStr = spinnerPlata.getSelectedItem().toString();

		if (rawTipPlataStr.startsWith("O") && (!strMailAddr.contains("@") || !strMailAddr.contains("."))) {
			Toast.makeText(getApplicationContext(), "Completati adresa de e-mail.", Toast.LENGTH_LONG).show();
			return;
		}

		dateLivrareInstance.setTipPlata(rawTipPlataStr.substring(0, rawTipPlataStr.indexOf("-") - 1).trim());

		if (dateLivrareInstance.getTransport().equals("TCLI") && dateLivrareInstance.getTipPlata().equals("E1")) {
			Toast.makeText(getApplicationContext(), "Pentru transport TCLI nu puteti selecta metoda de plata Numerar sofer.", Toast.LENGTH_LONG)
					.show();
			return;
		}

		adresa = dateLivrareInstance.getNumeJudet() + " " + dateLivrareInstance.getOras() + " " + dateLivrareInstance.getStrada();

		if (layoutHeaderAdrese.getVisibility() == View.VISIBLE && radioListAdrese.isChecked()) {
			int selectedAdress = spinnerAdreseLivrare.getSelectedItemPosition();
			adresa = UtilsGeneral.getNumeJudet(listAdrese.get(selectedAdress).getCodJudet()) + " " + listAdrese.get(selectedAdress).getOras() + " "
					+ listAdrese.get(selectedAdress).getStrada();

			dateLivrareInstance.setCodJudet(listAdrese.get(selectedAdress).getCodJudet());
			dateLivrareInstance.setOras(listAdrese.get(selectedAdress).getOras());
			dateLivrareInstance.setStrada(listAdrese.get(selectedAdress).getStrada());

		}

		if (DateLivrare.getInstance().isAltaAdresa()) {

			dateLivrareInstance.setAdresaD(textStradaLivrare.getText().toString().trim());

		}

		dateLivrareInstance.setDateLivrare(adresa + "#" + pers + "#" + telefon + "#" + cantar + "#" + dateLivrareInstance.getTipPlata() + "#"
				+ spinnerTransp.getSelectedItem().toString() + "#" + factRed + "#");

		dateLivrareInstance.setTermenPlata(spinnerTermenPlata.getSelectedItem().toString());
		
		 if (UtilsUser.isAgentOrSD() && !dateLivrareInstance.getTermenPlata().equals("C000") && dateLivrareInstance.getTipComandaGed().equals(TipCmdGed.ARTICOLE_COMANDA)) {
	            Toast.makeText(getApplicationContext(), "Pentru comenzile AC/ZC trebuie sa existe un proces verbal de angajament semnat de client.", Toast.LENGTH_LONG).show();
	        }

		dateLivrareInstance.setObsLivrare(observatii.replace("#", "-").replace("@", "-"));
		dateLivrareInstance.setObsPlata(obsPlata.replace("#", "-").replace("@", "-"));

		dateLivrareInstance.setMail(strMailAddr.replace("#", "-").replace("@", "~"));

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

		if (dateLivrareInstance.getTipDocInsotitor().equals("2") && dateLivrareInstance.getTipPlata().equals("E1")) {
			Toast.makeText(getApplicationContext(), "Pentru avizul de expeditie selectati alta metoda de plata.", Toast.LENGTH_LONG).show();
			return;
		}

		dateLivrareInstance.setTonaj("-1");

		if (radioAltaAdresa.isChecked() && !DateLivrare.getInstance().isAltaAdresa()) {
			dateLivrareInstance.setDateLivrare(getAdrLivrareJSON());
		} else if (radioAdresaObiectiv.isChecked()) {
			setAdresaLivrareObiectiv();
		}

		if (isConditiiTonaj(spinnerTransp, spinnerTonaj)) {
			String[] tonaj = spinnerTonaj.getSelectedItem().toString().split(" ");
			dateLivrareInstance.setTonaj(tonaj[0]);
		} else
			dateLivrareInstance.setTonaj("-1");

		if (spinnerIndoire.getVisibility() == View.VISIBLE && spinnerIndoire.getSelectedItemPosition() > 0) {
			dateLivrareInstance.setPrelucrare(spinnerIndoire.getSelectedItem().toString());
		} else
			dateLivrareInstance.setPrelucrare("-1");

		if (spinnerMeseriasi.getSelectedItem() != null)
			dateLivrareInstance.setCodMeserias(((BeanClient) spinnerMeseriasi.getSelectedItem()).getCodClient());
		else
			dateLivrareInstance.setCodMeserias("0");

		dateLivrareInstance.setFactPaletSeparat(checkFactPaleti.isChecked());
		dateLivrareInstance.setCamionDescoperit(chkCamionDescoperit.isChecked());

		if (spinnerTransp.getSelectedItem().toString().toLowerCase().contains("trap"))
			dateLivrareInstance.setProgramLivrare(String.valueOf(spinnerProgramLivrare.getSelectedItemPosition()));
		else
			dateLivrareInstance.setProgramLivrare("0");

		finish();

	}

	private boolean hasCoordinates() {
		if (DateLivrare.getInstance().getCoordonateAdresa() == null)
			return false;
		else if (DateLivrare.getInstance().getCoordonateAdresa().latitude == 0)
			return false;

		return true;
	}

	private boolean isConditiiTonaj(Spinner spinnerTransp, Spinner spinnerTonaj) {
		return spinnerTransp.getSelectedItem().toString().toLowerCase().contains("arabesque")
				&& spinnerTonaj.getSelectedItem().toString().split(" ")[1].equals("T");

	}

	private void setAdresaLivrare(Address address) {

		if (radioAdresaSediu.isChecked()) {

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

			if (address.getStreet() != null && !address.getStreet().isEmpty())
				textStrada.setText(address.getStreet());

			if (address.getNumber() != null && !address.getNumber().isEmpty())
				textNrStr.setText(address.getNumber());
		} else if (radioAltaAdresa.isChecked()) {

			if (address.getCity() != null && !address.getCity().isEmpty())
				textLocalitateLivrare.setText(address.getCity());

			if (address.getStreet() != null && !address.getStreet().isEmpty())
				textStradaLivrare.setText(address.getStreet());

			if (address.getNumber() != null && !address.getNumber().isEmpty())
				textStradaLivrare.setText(address.getStreet() + " " + address.getNumber());

			int nrJudete = spinnerJudetLivrare.getAdapter().getCount();

			for (int j = 0; j < nrJudete; j++) {
				HashMap<String, String> artMapLivr = (HashMap<String, String>) this.adapterJudeteLivrare.getItem(j);
				String numeJudet = artMapLivr.get("numeJudet").toString();

				if (address.getSector().equals(numeJudet)) {
					spinnerJudetLivrare.setSelection(j);
					break;
				}

			}

		}

	}

	private void valideazaAdresaLivrare() {

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("codJudet", DateLivrare.getInstance().getCodJudet());
		params.put("localitate", DateLivrare.getInstance().getOras());

		operatiiAdresa.isAdresaValida(params, EnumLocalitate.LOCALITATE_SEDIU);

	}

	private String getTipTransport() {
		return spinnerTransp.getSelectedItem().toString().substring(0, 4);
	}

	private String getAdrLivrareJSON() {
		String jsonData = "";

		HashMap<String, String> adrData = new HashMap<String, String>();
		adrData.put("codJudet", codJudetLivrare);
		adrData.put("oras", DateLivrare.getInstance().getOrasD().trim());
		adrData.put("strada", textStradaLivrare.getText().toString().trim());

		EncodeJSONData jsonAdrLivrare = new EncodeJSONData(this, adrData);

		jsonData = jsonAdrLivrare.encodeAdresaLivrareCV();

		return jsonData;

	}

	@Override
	public void onBackPressed() {
		finish();
		return;
	}

	private void dealTranspCmdSite(String result) {
		String[] pretResponse = {};

		if (!result.equals("-1")) {
			pretResponse = result.split("#");

			if (!spinnerTransp.getSelectedItem().toString().substring(0, 4).equals(pretResponse[1].toUpperCase(Locale.getDefault()))) {
				Toast.makeText(getApplicationContext(), "Tipul de transport recomandat este " + pretResponse[1], Toast.LENGTH_LONG).show();
			}
		} else {
			pretResponse[0] = "0.0";
		}

	}

	public void onTaskComplete(String methodName, Object result) {
		if (methodName.equals(METHOD_NAME)) {
			fillJudeteClient((String) result);
		}

		if (methodName.equals("getValTransportComandaSite")) {
			dealTranspCmdSite((String) result);
		}

		if (methodName.equals("getValTransportConsilieri")) {

		}

	}

	private void setListnerBtnPozitieAdresa() {
		btnPozitieAdresa.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				android.app.FragmentManager fm = getFragmentManager();

				if (!isAdresaComplet())
					return;

				MapAddressDialog mapDialog = new MapAddressDialog(getAddressFromForm(), SelectAdrLivrCmdGed.this, fm);
				mapDialog.setMapListener(SelectAdrLivrCmdGed.this);
				mapDialog.show();
			}
		});
	}

	private Address getAddressFromForm() {
		Address address = new Address();

		if (radioAdresaSediu.isChecked()) {
			address.setCity(DateLivrare.getInstance().getOras());
			address.setStreet(UtilsAddress.getStreetNoNumber(DateLivrare.getInstance().getStrada()));
			address.setSector(UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudet()));
		} else if (radioAltaAdresa.isChecked()) {
			address.setCity(DateLivrare.getInstance().getOrasD());
			address.setStreet(UtilsAddress.getStreetNoNumber(DateLivrare.getInstance().getAdresaD()));
			address.setSector(UtilsGeneral.getNumeJudet(DateLivrare.getInstance().getCodJudetD()));
		}

		return address;
	}

	private boolean isAdresaComplet() {
		if (DateLivrare.getInstance().getCodJudet().equals("") || DateLivrare.getInstance().getCodJudetD().equals("")) {
			Toast.makeText(this, "Selectati judetul", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (DateLivrare.getInstance().getOras().equals("") || DateLivrare.getInstance().getOrasD().equals("")) {
			Toast.makeText(this, "Completati localitatea", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private boolean isAdresaCorecta() {
		if (DateLivrare.getInstance().getTransport().equals("TRAP"))
			return isAdresaGoogleOk();
		else
			return true;

	}

	private boolean isAdresaGoogleOk() {

		GeocodeAddress geoAddress = MapUtils.geocodeAddress(getAddressFromForm(), getApplicationContext());
		DateLivrare.getInstance().setCoordonateAdresa(geoAddress.getCoordinates());

		boolean isAdresaOk = geoAddress.isAdresaValida();

		String localitate = DateLivrare.getInstance().getOras();
		List<BeanLocalitate> listLocalitati = listAdreseJudet.getListLocalitati();

		if (radioAltaAdresa.isChecked()) {
			localitate = DateLivrare.getInstance().getOrasD();

			if (listAlteAdrese == null)
				return isAdresaOk;

			listLocalitati = listAlteAdrese.getListLocalitati();
		}

		BeanLocalitate beanLocalitate = HelperAdreseLivrare.getDateLocalitate(listLocalitati, localitate);

		if (beanLocalitate.isOras()) {
			isAdresaOk = HelperAdreseLivrare.isDistantaCentruOk(getApplicationContext(), beanLocalitate, geoAddress.getCoordinates());

		}

		return isAdresaOk;

	}

	private void fillAdreseLivrareClient(String adreseLivrare) {

		listAdrese = operatiiClient.deserializeAdreseLivrare(adreseLivrare);

		listAdreseLivrare = new ArrayList<HashMap<String, String>>();
		adapterAdreseLivrare = new SimpleAdapter(this, listAdreseLivrare, R.layout.simplerowlayout_1, new String[] { "rowText", "rowId" }, new int[] {
				R.id.textRowName, R.id.textRowId });

		if (listAdrese.size() > 0) {

			HashMap<String, String> temp;

			String strAdresa = "";
			for (int i = 0; i < listAdrese.size(); i++) {
				temp = new HashMap<String, String>();

				strAdresa = UtilsGeneral.getNumeJudet(listAdrese.get(i).getCodJudet()) + "; " + listAdrese.get(i).getOras() + "; "
						+ listAdrese.get(i).getStrada() + "; " + listAdrese.get(i).getNrStrada() + ";";

				temp.put("rowText", strAdresa);
				temp.put("rowId", listAdrese.get(i).getCodAdresa());
				listAdreseLivrare.add(temp);

			}

			spinnerAdreseLivrare.setAdapter(adapterAdreseLivrare);
			adapterAdreseLivrare.notifyDataSetChanged();

		}

	}

	private void fillSpinnerMeseriasi(String result) {
		List<BeanClient> listMeseriasi = operatiiClient.deserializeListClienti(result);

		if (listMeseriasi.isEmpty()) {
			((LinearLayout) findViewById(R.id.layoutMeseriasi)).setVisibility(View.GONE);
			return;
		} else
			((LinearLayout) findViewById(R.id.layoutMeseriasi)).setVisibility(View.VISIBLE);

		BeanClient client = new BeanClient();
		client.setNumeClient("Selectati un meserias");
		client.setCodClient("0");
		listMeseriasi.add(0, client);

		ArrayAdapter<BeanClient> spinnerArrayAdapter = new ArrayAdapter<BeanClient>(this, android.R.layout.simple_spinner_item, listMeseriasi);

		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMeseriasi.setAdapter(spinnerArrayAdapter);

		if (!DateLivrare.getInstance().getCodMeserias().isEmpty()) {

			for (int i = 0; i < spinnerMeseriasi.getAdapter().getCount(); i++)
				if (((BeanClient) spinnerMeseriasi.getAdapter().getItem(i)).getCodClient().equals(DateLivrare.getInstance().getCodMeserias()))
					spinnerMeseriasi.setSelection(i);
		}

	}

	public void operationComplete(EnumClienti methodName, Object result) {

		switch (methodName) {
		case GET_ADRESE_LIVRARE:
			fillAdreseLivrareClient((String) result);
			break;
		case GET_MESERIASI:
			fillSpinnerMeseriasi((String) result);
			break;
		default:
			break;

		}

	}

	private void valideazaAdresaResponse(String result) {

		valideazaDateLivrare();

	}

	private void loadDateLivrareClient(BeanDateLivrareClient dateLivrareClient) {

		this.dateLivrareClient = dateLivrareClient;

		int nrJudete = spinnerJudet.getAdapter().getCount();

		for (int i = 0; i < nrJudete; i++) {

			@SuppressWarnings("unchecked")
			HashMap<String, String> artMap = (HashMap<String, String>) spinnerJudet.getAdapter().getItem(i);

			if (artMap.get("codJudet").equals(dateLivrareClient.getCodJudet())) {
				spinnerJudet.setSelection(i);
				break;
			}

		}

	}

	private void setDateLivrareClient() {

		if ((UtilsUser.isCGED() || UtilsUser.isSSCM() || CreareComandaGed.tipClient.equals("IP")) && dateLivrareClient != null) {
			textLocalitate.setText(dateLivrareClient.getLocalitate());

			if (!dateLivrareClient.getStrada().isEmpty() && !dateLivrareClient.getStrada().equals("null")) {
				textStrada.setText(dateLivrareClient.getStrada());
			}

			if (!dateLivrareClient.getNrStrada().isEmpty() && !dateLivrareClient.getNrStrada().equals("null")) {
				textNrStr.setText(dateLivrareClient.getNrStrada());
			}

			if (!dateLivrareClient.getNumePersContact().isEmpty() && !dateLivrareClient.getNumePersContact().equals("null")) {
				txtPers.setText(dateLivrareClient.getNumePersContact());
			}

			if (!dateLivrareClient.getTelPersContact().isEmpty() && !dateLivrareClient.getTelPersContact().equals("null")) {
				txtTel.setText(dateLivrareClient.getTelPersContact());
			}

			if ((UtilsUser.isCGED() || UtilsUser.isSSCM()) && dateLivrareClient.getTermenPlata().trim().length() > 0) {
				String[] tokTermen = dateLivrareClient.getTermenPlata().split(";");
				for (int nrLivr = 0; nrLivr < tokTermen.length; nrLivr++) {
					if (!tokTermen[nrLivr].equals("C000") && !tokTermen[nrLivr].equals("null"))
						adapterTermenPlata.add(tokTermen[nrLivr]);
				}

			}

		}

	}

	public void operatiiAdresaComplete(EnumOperatiiAdresa numeComanda, Object result, EnumLocalitate tipLocalitate) {

		if (numeComanda == EnumOperatiiAdresa.IS_ADRESA_VALIDA) {
			valideazaAdresaResponse((String) result);
		} else if (numeComanda == EnumOperatiiAdresa.GET_DATE_LIVRARE_CLIENT) {
			loadDateLivrareClient(operatiiAdresa.deserializeDateLivrareClient((String) result));
		} else {
			switch (tipLocalitate) {
			case LOCALITATE_SEDIU:
				populateListLocSediu(operatiiAdresa.deserializeListAdrese(result));
				setDateLivrareClient();
				break;
			case LOCALITATE_LIVRARE:
				populateListLocLivrare(operatiiAdresa.deserializeListAdrese(result));
				break;
			default:
				break;

			}
		}

	}

	@Override
	public void addressSelected(LatLng coord, android.location.Address address) {
		DateLivrare.getInstance().setCoordonateAdresa(coord);
		setAdresaLivrare(MapUtils.getAddress(address));

	}

	@Override
	public void obiectivSelected(ObiectivConsilier obiectiv) {
		textObiectivSelectat.setVisibility(View.VISIBLE);
		textObiectivSelectat.setText(obiectiv.getBeneficiar() + " / " + obiectiv.getAdresa());
		obiectivSelectat = obiectiv;
		radioAdresaObiectiv.setVisibility(View.VISIBLE);
		radioAdresaObiectiv.setChecked(true);
		btnStergeObiectiv.setVisibility(View.VISIBLE);
		DateLivrare.getInstance().setIdObiectiv(obiectivSelectat.getId());
		DateLivrare.getInstance().setObiectivConsilier(obiectiv);

	}

}
