package listeners;

import java.util.List;

import model.ArticolComanda;

public interface RezumatListener {
	void comandaEliminata(List<String> listArticole, String filialaLivrare);
	void adaugaArticol(ArticolComanda articolComanda);
	void eliminaArticol(ArticolComanda articolComanda);
	void setStareRezumat(String codStare, String fliala);
}
