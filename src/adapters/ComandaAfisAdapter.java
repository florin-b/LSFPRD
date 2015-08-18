package adapters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import my.logon.screen.R;
import utils.UtilsFormatting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import beans.BeanComandaCreata;

public class ComandaAfisAdapter extends BaseAdapter {

	private Context context;
	private List<BeanComandaCreata> listComenzi;
	NumberFormat numberFormat = new DecimalFormat("#0.00");

	public ComandaAfisAdapter(List<BeanComandaCreata> listComenzi, Context context) {
		this.context = context;
		this.listComenzi = listComenzi;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.customrowcmd, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textIdCmd = (TextView) convertView.findViewById(R.id.textIdCmd);
			viewHolder.textClient = (TextView) convertView.findViewById(R.id.textClient);
			viewHolder.textData = (TextView) convertView.findViewById(R.id.textData);
			viewHolder.textSuma = (TextView) convertView.findViewById(R.id.textSuma);
			viewHolder.textStare = (TextView) convertView.findViewById(R.id.textStare);
			viewHolder.textMoneda = (TextView) convertView.findViewById(R.id.textMoneda);
			viewHolder.textSumaTVA = (TextView) convertView.findViewById(R.id.textSumaTVA);
			viewHolder.textMonedaTVA = (TextView) convertView.findViewById(R.id.textMonedaTVA);
			viewHolder.textCmdSap = (TextView) convertView.findViewById(R.id.textCmdSap);
			viewHolder.textTipClient = (TextView) convertView.findViewById(R.id.textTipClient);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		BeanComandaCreata comanda = getItem(position);

		viewHolder.textIdCmd.setText(comanda.getId());
		viewHolder.textClient.setText(comanda.getNumeClient());
		viewHolder.textData.setText(UtilsFormatting.formatDate(comanda.getData()));
		viewHolder.textSuma.setText(numberFormat.format(Double.valueOf(comanda.getSuma())));
		viewHolder.textStare.setText(comanda.getStare());
		viewHolder.textMoneda.setText(comanda.getMoneda());
		viewHolder.textSumaTVA.setText(numberFormat.format(Double.valueOf(comanda.getSumaTva())));
		viewHolder.textMonedaTVA.setText(comanda.getMonedaTva());
		viewHolder.textCmdSap.setText(comanda.getCmdSap());
		viewHolder.textTipClient.setText(comanda.getTipClient());

		return convertView;
	}

	static class ViewHolder {
		public TextView textIdCmd, textClient, textData, textSuma, textStare, textMoneda, textSumaTVA, textMonedaTVA, textCmdSap, textTipClient;
	}

	public int getCount() {
		return listComenzi.size();
	}

	public BeanComandaCreata getItem(int position) {
		return listComenzi.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setListComenzi(List<BeanComandaCreata> listComenzi) {
		this.listComenzi = listComenzi;
	}

}
