package enums;

public enum EnumArticoleDAO {

	GET_ARTICOLE_DISTRIBUTIE("getArticoleDistributie"), GET_PRET("getPret"), GET_STOC_DEPOZIT("getStocDepozit"), GET_ARTICOLE_COMPLEMENTARE(
			"getArticoleComplementare"), GET_ARTICOLE_FURNIZOR("getArticoleFurnizor"), GET_SINTETICE_DISTRIBUTIE("getSinteticeDistributie"), GET_NIVEL1_DISTRIBUTIE(
			"getNivel1Distributie"), GET_PRET_GED("getPretGed"), GET_FACTOR_CONVERSIE("getArtFactConvUM"), GET_PRET_GED_JSON("getPretGedJson"), GET_DEP_BV90(
			"getDivizieBV90"), GET_STOC_ARTICOLE("getStocArticole"), GET_COD_BARE("getCodBare"), GET_ARTICOLE_STATISTIC("getArticoleStatistic"), GET_STOC_CUSTODIE(
			"getStocCustodie"), GET_ARTICOLE_CUSTODIE("getListArticoleCustodie"), GET_ARTICOLE_CANT("getArticoleCant");

	String numeComanda;

	EnumArticoleDAO(String numeComanda) {
		this.numeComanda = numeComanda;
	}

	public String getComanda() {
		return numeComanda;
	}

}
