package filters;

import java.util.ArrayList;
import java.util.List;

import beans.PierdereNivel1;

public class Nivel1PierderiFilter {

	public List<PierdereNivel1> getPierderiNivel1(List<PierdereNivel1> listPierderi, String numeClient) {
		List<PierdereNivel1> newListPierderi = new ArrayList<PierdereNivel1>();

		for (PierdereNivel1 p : listPierderi) {
			if (p.getNumeClient().equals(numeClient))
				newListPierderi.add(p);

		}

		return newListPierderi;

	}

}
