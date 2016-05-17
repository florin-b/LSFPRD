package adapters;

import java.util.List;

import my.logon.screen.R;
import utils.UtilsFormatting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import beans.BeanObiectivHarta;
import enums.EnumJudete;

public class AdapterObiectiveHarta extends BaseAdapter {

	private List<BeanObiectivHarta> listObiective;
	private Context context;

	public AdapterObiectiveHarta(Context context, List<BeanObiectivHarta> listObiective) {
		this.context = context;
		this.listObiective = listObiective;
	}

	public static class ViewHolder {
		TextView textNumeObiectiv, textBeneficiar, textStadiu, textDatacreare, textNrCrt;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.obiectiv_harta_item, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textNrCrt = (TextView) convertView.findViewById(R.id.textNrCrt);
			viewHolder.textNumeObiectiv = (TextView) convertView.findViewById(R.id.textNumeObiectiv);
			viewHolder.textBeneficiar = (TextView) convertView.findViewById(R.id.textBeneficiar);
			viewHolder.textStadiu = (TextView) convertView.findViewById(R.id.textStadiu);
			viewHolder.textDatacreare = (TextView) convertView.findViewById(R.id.textDatacreare);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		BeanObiectivHarta obiectiv = getItem(position);

		viewHolder.textNrCrt.setText(String.valueOf(position + 1) + ".");
		viewHolder.textNumeObiectiv.setText(obiectiv.getNume());
		viewHolder.textBeneficiar.setText(obiectiv.getBeneficiar());
		viewHolder.textStadiu.setText(formatAddress(obiectiv.getAddress()));
		viewHolder.textDatacreare.setText(UtilsFormatting.formatDate(obiectiv.getData()));

		if (position % 2 == 0)
			convertView.setBackgroundResource(R.drawable.shadow_dark);
		else
			convertView.setBackgroundResource(R.drawable.shadow_light);
		
		return convertView;
	}

	public int getCount() {
		return listObiective.size();
	}

	public BeanObiectivHarta getItem(int position) {
		return listObiective.get(position);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	private String formatAddress(String address) {
		String formatted = address;

		if (address != null && address.contains("/")) {
			String[] adrTokens = address.split("/");

			for (int i = 0; i < adrTokens.length; i++)
				if (i == 0)
					formatted = EnumJudete.getNumeJudet(Integer.parseInt(adrTokens[i]));
				else
					formatted += "/" + adrTokens[i];

		}

		return formatted;
	}
}
