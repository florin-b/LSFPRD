package adapters;

import java.util.List;

import my.logon.screen.R;
import utils.UtilsGeneral;

import adapters.AdapterAdreseLivrare.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import beans.ArticolPalet;
import beans.BeanAdresaLivrare;

public class AdapterPaleti extends BaseAdapter {

	private List<ArticolPalet> listPaleti;
	private Context context;

	public AdapterPaleti(Context context, List<ArticolPalet> listPaleti) {
		this.context = context;
		this.listPaleti = listPaleti;

	}

	static class ViewHolder {
		TextView textNumePalet,  textPretUnit, textFurnizor, textCantPalet, textNumeArticol, textCantArticol, textStatus;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.row_palet, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textNumePalet = (TextView) convertView.findViewById(R.id.textNumePalet);
			
			viewHolder.textPretUnit = (TextView) convertView.findViewById(R.id.textPretUnit);
			viewHolder.textFurnizor = (TextView) convertView.findViewById(R.id.textFurnizor);
			viewHolder.textCantPalet = (TextView) convertView.findViewById(R.id.textCantPalet);
			viewHolder.textNumeArticol = (TextView) convertView.findViewById(R.id.textNumeArticol);
			viewHolder.textCantArticol = (TextView) convertView.findViewById(R.id.textCantArticol);
			viewHolder.textStatus = (TextView) convertView.findViewById(R.id.textStatus);
			
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ArticolPalet palet = getItem(position);

		viewHolder.textNumePalet.setText(palet.getNumePalet());
		viewHolder.textCantPalet.setText(String.valueOf(palet.getCantitate()) + " BUC");
		
		viewHolder.textPretUnit.setText(String.valueOf(palet.getPretUnit()) + " RON/BUC");
		viewHolder.textFurnizor.setText("Furnizor palet: " + palet.getFurnizor());
		
		viewHolder.textNumeArticol.setText(palet.getNumeArticol());
		viewHolder.textCantArticol.setText(palet.getCantArticol() + " " + palet.getUmArticol());
		viewHolder.textStatus.setText(palet.isAdaugat() ? "Articol adaugat" : "");

		
		return convertView;

	}

	@Override
	public int getCount() {
		return listPaleti.size();
	}

	@Override
	public ArticolPalet getItem(int position) {
		return listPaleti.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
