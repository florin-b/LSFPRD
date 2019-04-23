package enums;

public enum EnumOperatiiSalarizare {

	GET_SALARIZARE_AGENT("getSalarizareAgent"), GET_SALARIZARE_DEPART("getSalarizareDepartament"), GET_SALARIZARE_SD("getSalarizareSd");

	private String numeComanda;

	EnumOperatiiSalarizare(String numeComanda) {
		this.numeComanda = numeComanda;
	}

	public String getNumeComanda() {
		return numeComanda;
	}

}
