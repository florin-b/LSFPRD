package my.logon.screen;

import java.util.List;

import listeners.DocumentReturListener;
import listeners.ListaDocReturListener;
import model.CriteriuNrDocRetur;
import adapters.DocumenteReturAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import beans.BeanDocumentRetur;

public class DocumenteReturMarfa extends Fragment implements ListaDocReturListener {

	static String numeClient;

	TextView textClient;
	ListView listDocumenteRetur;
	DocumentReturListener documentListener;
	EditText textNumarDocument;
	CriteriuNrDocRetur criteriuNrDoc;
	List<BeanDocumentRetur> listDocumente;
	TextView selectIcon;
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.documente_retur_marfa, container, false);

		textClient = (TextView) v.findViewById(R.id.textClient);
		listDocumenteRetur = (ListView) v.findViewById(R.id.listDocumenteRetur);
		addListenerListDocumente();

		textNumarDocument = (EditText) v.findViewById(R.id.textNumarDocument);
		textNumarDocument.setHint("Numar document");
		textNumarDocument.setVisibility(View.INVISIBLE);
		addListenerTextDocument();

		criteriuNrDoc = new CriteriuNrDocRetur();
		selectIcon = (TextView) v.findViewById(R.id.selectIcon);
		selectIcon.setVisibility(View.INVISIBLE);

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			documentListener = (DocumentReturListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString());
		}
	}

	private void addListenerTextDocument() {
		textNumarDocument.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				List<BeanDocumentRetur> listDoc = criteriuNrDoc.indeplinesteCriteriul(listDocumente, s.toString());
				updateListDocumente(listDoc);

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void addListenerListDocumente() {
		listDocumenteRetur.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BeanDocumentRetur docRetur = (BeanDocumentRetur) arg0.getAdapter().getItem(arg2);
				documentListener.documentSelected(docRetur);

			}
		});
	}

	public static DocumenteReturMarfa newInstance() {
		DocumenteReturMarfa frg = new DocumenteReturMarfa();
		Bundle bdl = new Bundle();
		frg.setArguments(bdl);
		return frg;
	}

	@SuppressWarnings("unchecked")
	public void setListDocRetur(String numeClient, Object listDocumente) {

		textClient.setText("Client " + numeClient);
		selectIcon.setVisibility(View.VISIBLE);
		textNumarDocument.setVisibility(View.VISIBLE);

		this.listDocumente = (List<BeanDocumentRetur>) listDocumente;
		updateListDocumente(this.listDocumente);

	}

	private void updateListDocumente(List<BeanDocumentRetur> listDocumente) {
		DocumenteReturAdapter docAdapter = new DocumenteReturAdapter(getActivity(), (List<BeanDocumentRetur>) listDocumente);
		listDocumenteRetur.setAdapter(docAdapter);
	}

}
