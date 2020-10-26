package listeners;

import beans.ArticolPalet;
import enums.EnumPaleti;

public interface PaletiListener {
	void paletiStatus(EnumPaleti status, ArticolPalet palet);
}
