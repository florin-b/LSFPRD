package beans;

public class CostTransportMathaus {

	private String filiala;
	private String tipTransp;
	private String valTransp;
	private String codArtTransp;
	private String depart;

	public String getFiliala() {
		return filiala;
	}

	public void setFiliala(String filiala) {
		this.filiala = filiala;
	}

	public String getTipTransp() {
		return tipTransp;
	}

	public void setTipTransp(String tipTransp) {
		this.tipTransp = tipTransp;
	}

	public String getValTransp() {
		return valTransp;
	}

	public void setValTransp(String valTransp) {
		this.valTransp = valTransp;
	}

	public String getCodArtTransp() {
		return codArtTransp;
	}

	public void setCodArtTransp(String codArtTransp) {
		this.codArtTransp = codArtTransp;
	}
	
	

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	@Override
	public String toString() {
		return "CostTransportMathaus [filiala=" + filiala + ", tipTransp=" + tipTransp + ", valTransp=" + valTransp + ", codArtTransp=" + codArtTransp + "]";
	}
	
	

}
