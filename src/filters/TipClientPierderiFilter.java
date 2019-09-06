package filters;

import java.util.ArrayList;
import java.util.List;

import beans.PierdereTipClient;

public class TipClientPierderiFilter {

	public List<PierdereTipClient> getPierderiTipClient(List<PierdereTipClient> listPierderi, String codTipClient) {
		List<PierdereTipClient> newListPierderi = new ArrayList<PierdereTipClient>();

		for (PierdereTipClient p : listPierderi) {
			if (p.getCodTipClient().equals(codTipClient))
				newListPierderi.add(p);

		}

		return newListPierderi;

	}

}
