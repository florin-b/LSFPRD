package dialogs;

import java.math.BigDecimal;
import java.util.List;

import listeners.PaletiListener;
import my.logon.screen.R;
import adapters.AdapterPaleti;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import beans.ArticolPalet;
import enums.EnumPaleti;
import filters.PaletiFilter;

public class CostPaletiDialog extends Dialog {

	private Context context;
	private Spinner spinnerPaleti;
	private AdapterPaleti adapterPaleti;

	private List<ArticolPalet> listPaleti;
	private PaletiListener listener;
	private String tipTransport;
	private Button btnAdaugaPaleti;
	private TextView textNumePalet, textUmPalet, textValPalet;
	private EditText textCantPalet;
	private Button btnRenuntaPaleti;
	private ArticolPalet paletSelectat;

	public CostPaletiDialog(Context context, List<ArticolPalet> listPaleti, String tipTransport) {
		super(context);
		this.context = context;
		this.listPaleti = listPaleti;
		this.tipTransport = tipTransport;

		setContentView(R.layout.select_paleti_dialog);
		setTitle("Selectie paleti");
		setCancelable(true);

		setUpLayout();

	}

	public void showDialog() {
		this.show();
	}

	private void setUpLayout() {
		spinnerPaleti = (Spinner) findViewById(R.id.spinnerPaleti);
		setSpinnerPaletiListener();
		adapterPaleti = new AdapterPaleti(context, listPaleti);
		spinnerPaleti.setAdapter(adapterPaleti);
		textNumePalet = (TextView) findViewById(R.id.textNumePalet);
		textUmPalet = (TextView) findViewById(R.id.textUmPalet);
		textCantPalet = (EditText) findViewById(R.id.textCantPalet);
		textValPalet = (TextView) findViewById(R.id.textValPalet);
		setTextCantListener();

		btnAdaugaPaleti = (Button) findViewById(R.id.btnOkPalet);
		addBtnAcceptaListener();

		btnRenuntaPaleti = (Button) findViewById(R.id.btnCancelPalet);
		if (!tipTransport.equals("TCLI"))
			btnRenuntaPaleti.setVisibility(View.INVISIBLE);

		addBtnRespingeListener();

	}

	private void setSpinnerPaletiListener() {
		spinnerPaleti.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				paletSelectat = (ArticolPalet) arg0.getAdapter().getItem(arg2);

				if (paletSelectat.isAdaugat()) {
					setPaletSelVisibility(false);
				} else {
					setPaletSelVisibility(true);
					
					BigDecimal b1 = BigDecimal.valueOf(paletSelectat.getCantitate());
					BigDecimal b2 = BigDecimal.valueOf(paletSelectat.getPretUnit());
					BigDecimal b3 = b1.multiply(b2);
					
					textValPalet.setText(b3.toString());
					
					textNumePalet.setText(paletSelectat.getNumePalet());
					textCantPalet.setFilters(new InputFilter[] { new PaletiFilter(1, paletSelectat.getCantitate()) });
					textCantPalet.setText(String.valueOf(paletSelectat.getCantitate()));
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void setTextCantListener() {
		textCantPalet.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (textCantPalet.getText().toString().trim().isEmpty())
					textValPalet.setText("0");
				else {
					
					BigDecimal b1 = BigDecimal.valueOf(Integer.parseInt(textCantPalet.getText().toString().trim()));
					BigDecimal b2 = BigDecimal.valueOf(paletSelectat.getPretUnit());
					BigDecimal b3 = b1.multiply(b2);

					textValPalet.setText(b3.toString());
					
				}

			}
		});
	}

	private void setPaletSelVisibility(boolean isVisible) {
		if (isVisible) {
			textNumePalet.setVisibility(View.VISIBLE);
			textCantPalet.setVisibility(View.VISIBLE);
			textUmPalet.setVisibility(View.VISIBLE);
		} else {
			textNumePalet.setVisibility(View.INVISIBLE);
			textCantPalet.setVisibility(View.INVISIBLE);
			textUmPalet.setVisibility(View.INVISIBLE);
		}

	}

	private void addBtnAcceptaListener() {
		btnAdaugaPaleti.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (listener != null) {

					if (!paletSelectat.isAdaugat() && !textCantPalet.getText().toString().isEmpty()) {
						paletSelectat.setCantitate(Integer.parseInt(textCantPalet.getText().toString().trim()));
						paletSelectat.setAdaugat(true);
						adapterPaleti.notifyDataSetChanged();
						listener.paletiStatus(EnumPaleti.ACCEPTA, paletSelectat);

						selectNextPalet();

						if (isAcceptTotiPaleti())
							btnAdaugaPaleti.setText("Salveaza");

					} else if (isAcceptTotiPaleti()) {
						listener.paletiStatus(EnumPaleti.FINALIZEAZA, paletSelectat);
						dismiss();
					}

				}

			}
		});
	}

	private boolean isAcceptTotiPaleti() {
		boolean isAcceptTotal = true;

		for (ArticolPalet palet : listPaleti) {
			if (!palet.isAdaugat()) {
				isAcceptTotal = false;
				break;
			}
		}

		return isAcceptTotal;
	}

	private void selectNextPalet() {

		int nrPaleti = listPaleti.size();

		for (int i = 0; i < nrPaleti; i++) {
			if (!listPaleti.get(i).isAdaugat()) {
				spinnerPaleti.setSelection(i);
				break;
			}
		}

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
