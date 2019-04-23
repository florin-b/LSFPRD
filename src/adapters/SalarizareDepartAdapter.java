package adapters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import listeners.OperatiiSalarizareListener;
import my.logon.screen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import beans.BeanSalarizareAgentAfis;

public class SalarizareDepartAdapter extends BaseAdapter {

	private Context context;
	private List<BeanSalarizareAgentAfis> listAgenti;
	private NumberFormat numberFormat;
	private int[] colors = new int[] { 0x30FFFFFF, 0x30D7DBDD };
	private OperatiiSalarizareListener listener;

	public void setSalarizareDepartListener(OperatiiSalarizareListener listener) {
		this.listener = listener;
	}

	public SalarizareDepartAdapter(List<BeanSalarizareAgentAfis> listAgenti, Context context) {
		this.context = context;
		this.listAgenti = listAgenti;

		numberFormat = NumberFormat.getNumberInstance(Locale.US);
		numberFormat.setMaximumFractionDigits(2);

	}

	static class ViewHolder {
		public TextView textNumeAgent, textVenitT1, textVenitTCF, textCorectieInc, textVenitFinal, textDetalii;
		public Button detaliiBtn;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		int colorPos = position % 2;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.salarizare_depart_row, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textNumeAgent = (TextView) convertView.findViewById(R.id.textNumeAgent);
			viewHolder.textVenitT1 = (TextView) convertView.findViewById(R.id.textVenitT1);
			viewHolder.textVenitTCF = (TextView) convertView.findViewById(R.id.textVenitTCF);
			viewHolder.textCorectieInc = (TextView) convertView.findViewById(R.id.textCorectieInc);
			viewHolder.textVenitFinal = (TextView) convertView.findViewById(R.id.textVenitFinal);
			viewHolder.detaliiBtn = (Button) convertView.findViewById(R.id.btnDetalii);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final BeanSalarizareAgentAfis agent = getItem(position);

		viewHolder.textNumeAgent.setText(agent.getNumeAgent());
		viewHolder.textVenitT1.setText(numberFormat.format(agent.getDatePrincipale().getVenitMJ_T1()));
		viewHolder.textVenitTCF.setText(numberFormat.format(agent.getDatePrincipale().getVenitTCF()));
		viewHolder.textCorectieInc.setText(numberFormat.format(agent.getDatePrincipale().getCorectieIncasare()));
		viewHolder.textVenitFinal.setText(numberFormat.format(agent.getDatePrincipale().getVenitFinal()));

		viewHolder.detaliiBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (listener != null)
					listener.detaliiAgentSelected(agent.getCodAgent(), agent.getNumeAgent());

			}
		});

		if (colorPos % 2 == 0)
			convertView.setBackgroundColor(colors[0]);
		else
			convertView.setBackgroundColor(colors[1]);

		return convertView;
	}

	public int getCount() {
		return listAgenti.size();
	}

	public BeanSalarizareAgentAfis getItem(int position) {
		return listAgenti.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
