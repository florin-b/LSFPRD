package adapters;

import java.util.List;

import my.logon.screen.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import beans.PierdereNivel1;

public class PierdereNivel1Adapter extends BaseAdapter {

	private Context context;
	private List<PierdereNivel1> listPierderi;

	private int[] colors = new int[] { 0x30FFFFFF, 0x30D7DBDD };

	public PierdereNivel1Adapter(List<PierdereNivel1> listPierderi, Context context) {
		this.context = context;
		this.listPierderi = listPierderi;

	}

	static class ViewHolder {
		public TextView textNivel1, textVenitLC, textVenitLC_1, textVenitLC_2;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.pierd_nivel1_row, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textNivel1 = (TextView) convertView.findViewById(R.id.textNivel1);
			viewHolder.textVenitLC = (TextView) convertView.findViewById(R.id.txtVenitLC);
			viewHolder.textVenitLC_1 = (TextView) convertView.findViewById(R.id.textVenitLC_1);
			viewHolder.textVenitLC_2 = (TextView) convertView.findViewById(R.id.textVenitLC_2);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final PierdereNivel1 pierdere = getItem(position);

		viewHolder.textNivel1.setText(pierdere.getNumeNivel1());
		viewHolder.textVenitLC.setText(String.valueOf(pierdere.getVenitLC()));
		viewHolder.textVenitLC_1.setText(String.valueOf(pierdere.getVenitLC1()));
		viewHolder.textVenitLC_2.setText(String.valueOf(pierdere.getVenitLC2()));

		convertView.setBackgroundColor(colors[position % 2]);

		return convertView;
	}

	public int getCount() {
		return listPierderi.size();
	}

	public PierdereNivel1 getItem(int position) {
		return listPierderi.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
