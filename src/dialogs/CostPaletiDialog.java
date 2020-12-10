package dialogs;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import listeners.PaletiListener;
import my.logon.screen.R;
import adapters.AdapterPaleti;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import beans.ArticolPalet;
import enums.EnumPaleti;

public class CostPaletiDialog extends Dialog {

	private Context context;
	private ListView spinnerPaleti;
	private AdapterPaleti adapterPaleti;
	private List<ArticolPalet> listPaleti;
	private PaletiListener listener;
	private String tipTransport;
	private Button btnAdaugaPaleti;
	private TextView textValPalet;
	private Button btnRenuntaPaleti;
	

	public CostPaletiDialog(Context context, List<ArticolPalet> listPaleti, String tipTransport) {
		super(context);
		this.context = context;
		this.listPaleti = listPaleti;
		this.tipTransport = tipTransport;

		setContentView(R.layout.select_paleti_dialog);
		setTitle("Adaugare paleti");
		setCancelable(true);

		setUpLayout();

	}

	public void showDialog() {
		this.show();
	}

	private void setUpLayout() {
		spinnerPaleti = (ListView) findViewById(R.id.spinnerPaleti);

		LinearLayout paletiLayout = (LinearLayout) findViewById(R.id.layoutPaleti);

		paletiLayout.getLayoutParams().height = (int) (getScreenHeight(context) * 0.38);

		adapterPaleti = new AdapterPaleti(context, listPaleti);
		spinnerPaleti.setAdapter(adapterPaleti);
		textValPalet = (TextView) findViewById(R.id.textValPalet);
		setTotalPaleti();

		btnAdaugaPaleti = (Button) findViewById(R.id.btnOkPalet);
		addBtnAcceptaListener();

		btnRenuntaPaleti = (Button) findViewById(R.id.btnCancelPalet);

		btnRenuntaPaleti.setVisibility(View.INVISIBLE);

		addBtnRespingeListener();

	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	private void setTotalPaleti() {
		double cantTotal = 0;
		double valTotal = 0;
		NumberFormat nf2 = new DecimalFormat("#0.00");

		for (ArticolPalet palet : listPaleti) {
			cantTotal += palet.getCantitate();
			valTotal += palet.getCantitate() * palet.getPretUnit();

		}

		textValPalet.setText((int) cantTotal + " BUC   " + nf2.format(valTotal) + " RON");

	}

	private void addBtnAcceptaListener() {
		btnAdaugaPaleti.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (listener != null) {

					for (ArticolPalet palet : listPaleti)
						listener.paletiStatus(EnumPaleti.ACCEPTA, palet);

					listener.paletiStatus(EnumPaleti.FINALIZEAZA, null);
					dismiss();

				}

			}
		});
	}

	private void addBtnRespingeListener() {
		btnRenuntaPaleti.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.paletiStatus(EnumPaleti.RESPINGE, null);
					dismiss();

				}

			}

		});
	}

	public void setPaletiDialogListener(PaletiListener listener) {
		this.listener = listener;
	}

}
