/**
 * @author florinb
 *
 */
package my.logon.screen;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.CustomSpinnerClass;
import listeners.CustomSpinnerListener;
import listeners.DlDAOListener;
import model.DlDAO;
import model.HandleJSONData;
import model.InfoStrings;
import model.UserInfo;
import adapters.ArticoleCLPAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import beans.ArticolCLP;
import beans.BeanDocumentCLP;
import beans.ComandaCLP;
import beans.DateLivrareCLP;
import enums.EnumDlDAO;

public class AprobareDispozitiiLivrare extends Activity implements CustomSpinnerListener, DlDAOListener {

	private Spinner spinnerCmdDl;
	public SimpleAdapter adapterComenziDl, adapterArtCmdDl;

	private static ArrayList<HashMap<String, String>> listComenziDl = new ArrayList<HashMap<String, String>>(),
			listArtCmdDl = new ArrayList<HashMap<String, String>>();
	public static String selectedCmd = "", selectedCmdSap = "";
	ListView listViewArtCmdDl;
	private HashMap<String, String> artMap = null;

	private TextView textAdrLivr, textPersContact, textTelefon, textOras, textJudet, textDataLivrare, textTipPlata,
			textTipTransport, textAprobatOC;

	LinearLayout layoutCmdCondHead;
	Button btnAprobaDl, btnRespingeDl;
	Integer tipOpCmdDl = -1;

	Button delDlBtn;
	DlDAO operatiiComenzi;

	private CustomSpinnerClass spinnerListener = new CustomSpinnerClass();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.aprobare_dl_header);

		operatiiComenzi = new DlDAO(this);
		operatiiComenzi.setDlDAOListener(this);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Aprobare DL");
		actionBar.setDisplayHomeAsUpEnabled(true);

		spinnerCmdDl = (Spinner) findViewById(R.id.spinnerCmdDl);

		adapterComenziDl = new SimpleAdapter(this, listComenziDl, R.layout.list_comenzi_clp, new String[] { "idCmdClp",
				"numeClient", "data", "cmdSAP", "ul", "agent", "depoz" }, new int[] { R.id.textIdCmdClp,
				R.id.textClient, R.id.textData, R.id.textCmdSAP, R.id.textUL, R.id.textAgent, R.id.textDepoz });

		spinnerCmdDl.setAdapter(adapterComenziDl);

		spinnerCmdDl.setOnItemSelectedListener(spinnerListener);
		spinnerListener.setListener(this);

		listViewArtCmdDl = (ListView) findViewById(R.id.listArtCmdDl);

		adapterArtCmdDl = new SimpleAdapter(this, listArtCmdDl, R.layout.art_comenzi_clp, new String[] { "nrCrt",
				"numeArt", "codArt", "cantArt", "umArt", "depozit", "status" }, new int[] { R.id.textNrCrt,
				R.id.textNumeArt, R.id.textCodArt, R.id.textCantArt, R.id.textUmArt, R.id.textDepozit,
				R.id.textStatusArt });

		listViewArtCmdDl.setAdapter(adapterArtCmdDl);

		textAdrLivr = (TextView) findViewById(R.id.textAdrLivr);
		textPersContact = (TextView) findViewById(R.id.textPersContact);
		textTelefon = (TextView) findViewById(R.id.textTelefon);
		textOras = (TextView) findViewById(R.id.textOras);
		textJudet = (TextView) findViewById(R.id.textJudet);
		textDataLivrare = (TextView) findViewById(R.id.textDataLivrare);

		layoutCmdCondHead = (LinearLayout) findViewById(R.id.layoutCmdCondHead);
		layoutCmdCondHead.setVisibility(View.INVISIBLE);

		btnAprobaDl = (Button) findViewById(R.id.aprobaClp);
		btnRespingeDl = (Button) findViewById(R.id.respingeClp);

		btnAprobaDl.setVisibility(View.INVISIBLE);
		addListenerAprobaClp();

		btnRespingeDl.setVisibility(View.INVISIBLE);
		addListenerRespingeClp();

		textTipPlata = (TextView) findViewById(R.id.textTipPlata);
		textTipTransport = (TextView) findViewById(R.id.textTransport);
		textAprobatOC = (TextView) findViewById(R.id.textAprobatOC);

		delDlBtn = (Button) findViewById(R.id.delDlBtn);
		delDlBtn.setVisibility(View.GONE);

		try {

			refreshDlList();

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			clearAllData();
			UserInfo.getInstance().setParentScreen("");
			Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);

			startActivity(nextScreen);

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	protected void populateCmdList(String cmdList) {

		HandleJSONData objDocList = new HandleJSONData(this, cmdList);
		ArrayList<BeanDocumentCLP> docDLArray = objDocList.decodeJSONDocumentCLP();

		if (docDLArray.size() > 0) {

			NumberFormat nf2 = NumberFormat.getInstance();
			nf2.setMinimumFractionDigits(2);
			nf2.setMaximumFractionDigits(2);

			listComenziDl.clear();
			spinnerCmdDl.setVisibility(View.VISIBLE);

			HashMap<String, String> temp;

			BeanDocumentCLP documentDl;
			for (int i = 0; i < docDLArray.size(); i++) {
				temp = new HashMap<String, String>(20, 0.75f);

				documentDl = docDLArray.get(i);
				temp.put("idCmdClp", documentDl.getNrDocument());
				temp.put("numeClient", documentDl.getNumeClient());
				temp.put("data", documentDl.getDataDocument());

				if (!documentDl.getNrDocumentSap().equals("-1")) {
					temp.put("cmdSAP", documentDl.getNrDocumentSap());
				} else {
					temp.put("cmdSAP", " ");
				}

				temp.put("ul", documentDl.getUnitLog());
				temp.put("agent", documentDl.getNumeAgent());
				temp.put("depoz", documentDl.getDepozit());

				listComenziDl.add(temp);

			}

			spinnerCmdDl.setAdapter(adapterComenziDl);
			layoutCmdCondHead.setVisibility(View.VISIBLE);

			btnAprobaDl.setVisibility(View.VISIBLE);
			btnRespingeDl.setVisibility(View.VISIBLE);

		} else {

			listComenziDl.clear();
			spinnerCmdDl.setAdapter(adapterComenziDl);
			spinnerCmdDl.setVisibility(View.INVISIBLE);

			listArtCmdDl.clear();
			adapterArtCmdDl.notifyDataSetChanged();
			listViewArtCmdDl.setVisibility(View.INVISIBLE);

			layoutCmdCondHead.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "Nu exista comenzi!", Toast.LENGTH_SHORT).show();

			btnAprobaDl.setVisibility(View.INVISIBLE);
			btnRespingeDl.setVisibility(View.INVISIBLE);

		}

	}

	private void performArtCmd() {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("nrCmd", selectedCmd);

			operatiiComenzi.getArticoleComandaJSON(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	private void populateArtCmdList(ComandaCLP dateComanda) {

		if (dateComanda.getArticole().size() > 0) {

			DateLivrareCLP dateLivrare = dateComanda.getDateLivrare();

			listViewArtCmdDl.setVisibility(View.VISIBLE);

			textAdrLivr.setText(dateLivrare.getAdrLivrare());
			textPersContact.setText(dateLivrare.getPersContact());
			textTelefon.setText(dateLivrare.getTelefon());
			textOras.setText(dateLivrare.getOras());
			textJudet.setText(dateLivrare.getJudet());
			textDataLivrare.setText(dateLivrare.getData());
			textTipPlata.setText(dateLivrare.getTipPlata());
			textTipTransport.setText(dateLivrare.getMijlocTransport());
			textAprobatOC.setText(dateLivrare.getAprobatOC());

			List<ArticolCLP> listArticole = dateComanda.getArticole();

			ArticoleCLPAdapter adapterArticole = new ArticoleCLPAdapter(this, listArticole);
			listViewArtCmdDl.setAdapter(adapterArticole);

		}

	}

	private void populateArtCmdList1(String articoleComanda) {

		listArtCmdDl.clear();
		adapterArtCmdDl.notifyDataSetChanged();

		NumberFormat nf2 = new DecimalFormat("#0.00");

		if (!articoleComanda.equals("-1") && articoleComanda.length() > 0) {
			HashMap<String, String> temp;
			String[] tokenMain = articoleComanda.split("@@");
			String[] tokenAntet = tokenMain[0].split("#");

			listViewArtCmdDl.setVisibility(View.VISIBLE);

			textAdrLivr.setText(tokenAntet[2]);
			textPersContact.setText(tokenAntet[0]);
			textTelefon.setText(tokenAntet[1]);
			textOras.setText(tokenAntet[3]);

			String varNumeJudet = InfoStrings.numeJudet(tokenAntet[4]);

			if (varNumeJudet.equals("Nedefinit"))
				varNumeJudet = " ";

			textJudet.setText(varNumeJudet);

			textDataLivrare.setText(tokenAntet[5]);

			int nrArt = Integer.parseInt(tokenAntet[10]); // nr. articole
															// comanda

			textTipPlata.setText(InfoStrings.getTipPlata(tokenAntet[11]));
			textTipTransport.setText(InfoStrings.getTipTransport(tokenAntet[12]));
			textAprobatOC.setText(InfoStrings.getTipAprobare(tokenAntet[13]));

			String[] tokenArt;
			String client = "", statusArt = "";

			for (int i = 1; i <= nrArt; i++) {

				statusArt = "";
				temp = new HashMap<String, String>(20, 0.75f);
				client = tokenMain[i];
				tokenArt = client.split("#");

				temp.put("nrCrt", String.valueOf(i) + ".");
				temp.put("numeArt", tokenArt[1]);
				temp.put("codArt", tokenArt[0]);
				temp.put("cantArt", nf2.format(Float.parseFloat(tokenArt[2])));
				temp.put("umArt", tokenArt[3]);
				temp.put("depozit", tokenArt[4]);

				statusArt = "";
				if (tokenArt[5].equals("9")) {
					statusArt = "Stoc insuficient";
				}

				temp.put("status", statusArt);

				listArtCmdDl.add(temp);

			}// sf. for

			listViewArtCmdDl.setAdapter(adapterArtCmdDl);

		}

	}

	public void addListenerAprobaClp() {
		btnAprobaDl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showConfirmationApprovalAlert();

			}
		});

	}

	public void showConfirmationApprovalAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Aprobati cererea?").setCancelable(false)
				.setPositiveButton("Da", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						tipOpCmdDl = 0; // aprobare
						opereazaComandaClp();

					}
				}).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				}).setTitle("Confirmare").setIcon(R.drawable.warning96);

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void addListenerRespingeClp() {
		btnRespingeDl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showConfirmationDenialAlert();

			}
		});

	}

	public void showConfirmationDenialAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Respingeti cererea?").setCancelable(false)
				.setPositiveButton("Da", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						tipOpCmdDl = 1; // respingere
						opereazaComandaClp();

					}
				}).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				}).setTitle("Confirmare").setIcon(R.drawable.warning96);

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void opereazaComandaClp() {

		try {

			HashMap<String, String> params = new HashMap<String, String>();

			NumberFormat nf3 = new DecimalFormat("00000000");
			String fullCode = nf3.format(Integer.parseInt(UserInfo.getInstance().getCod())).toString();
			String localTipUser = "NN";

			if (UserInfo.getInstance().getTipAcces().equals("9"))
				localTipUser = "AV";

			if (UserInfo.getInstance().getTipAcces().equals("10"))
				localTipUser = "SD";

			if (UserInfo.getInstance().getTipAcces().equals("12") || UserInfo.getInstance().getTipAcces().equals("14"))
				localTipUser = "DV";

			params.put("nrCmd", selectedCmd);
			params.put("nrCmdSAP", selectedCmdSap);
			params.put("tipOp", String.valueOf(tipOpCmdDl));
			params.put("codUser", fullCode);
			params.put("tipUser", localTipUser);
			params.put("filiala", UserInfo.getInstance().getUnitLog());

			operatiiComenzi.operatiiComanda(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private void refreshDlList() {

		try {

			listComenziDl.clear();
			spinnerCmdDl.setAdapter(adapterComenziDl);

			listArtCmdDl.clear();
			listViewArtCmdDl.setAdapter(adapterArtCmdDl);

			HashMap<String, String> params = new HashMap<String, String>();

			String localTipUser = "NN";

			if (UserInfo.getInstance().getTipAcces().equals("9"))
				localTipUser = "AV";

			if (UserInfo.getInstance().getTipAcces().equals("10"))
				localTipUser = "SD";

			params.put("filiala", UserInfo.getInstance().getUnitLog());
			params.put("depart", UserInfo.getInstance().getCodDepart());
			params.put("tipClp", "-1"); // comenzi spre aprobare
			params.put("interval", "");
			params.put("tipUser", localTipUser);
			params.put("codUser", UserInfo.getInstance().getCod());

			operatiiComenzi.getListComenzi(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	private void clearAllData() {
		selectedCmd = "";
		selectedCmdSap = "";
		listComenziDl.clear();
		spinnerCmdDl.setAdapter(adapterComenziDl);

		listArtCmdDl.clear();
		listViewArtCmdDl.setAdapter(adapterArtCmdDl);

	}

	@Override
	public void onBackPressed() {

		clearAllData();
		UserInfo.getInstance().setParentScreen("");
		Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);

		startActivity(nextScreen);

		finish();
		return;
	}

	public void onSelectedSpinnerItem(int spinnerId, HashMap<String, String> map, int position) {
		switch (spinnerId) {
		case R.id.spinnerCmdDl:

			selectedCmd = map.get("idCmdClp");
			selectedCmdSap = map.get("cmdSAP");
			performArtCmd();

			break;
		}

	}

	public void operationDlComplete(EnumDlDAO methodName, Object result) {
		switch (methodName) {
		case GET_ARTICOLE_COMANDA_JSON:
			populateArtCmdList(operatiiComenzi.decodeArticoleComanda((String) result));
			break;
		case OPERATIE_COMANDA:
			selectedCmd = "-1";
			selectedCmdSap = "-1";
			Toast.makeText(getApplicationContext(), (String) result, Toast.LENGTH_SHORT).show();
			refreshDlList();
			break;
		case GET_LIST_COMENZI:
			populateCmdList((String) result);
			break;
		default:
			break;
		}

	}
}
