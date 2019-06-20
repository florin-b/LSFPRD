package filters;

import java.util.ArrayList;
import java.util.List;

import beans.SalarizareDetaliiMalus;

public class ClientMalusFilter {

	public List<SalarizareDetaliiMalus> getMalusClient(List<SalarizareDetaliiMalus> listMalus, String codClient) {
		List<SalarizareDetaliiMalus> clientMalus = new ArrayList<SalarizareDetaliiMalus>();

		for (SalarizareDetaliiMalus malus : listMalus) {
			if (malus.getCodClient().equals(codClient))
				clientMalus.add(malus);

		}

		return clientMalus;
	}

}
