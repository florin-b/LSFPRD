package beans;

import java.util.List;

public class BeanClient {

	private String numeClient;
	private String codClient;
	private String tipClient;
	private String agenti;
	private List<String> termenPlata;

	public BeanClient() {

	}

	public String getNumeClient() {
		return numeClient;
	}

	public void setNumeClient(String numeClient) {
		this.numeClient = numeClient;
	}

	public String getCodClient() {
		return codClient;
	}

	public void setCodClient(String codClient) {
		this.codClient = codClient;
	}

	public String getTipClient() {
		return tipClient;
	}

	public void setTipClient(String tipClient) {
		this.tipClient = tipClient;
	}

	@Override
	public String toString() {
		return numeClient;
	}

	public String getAgenti() {
		return agenti;
	}

	public void setAgenti(String agenti) {
		this.agenti = agenti;
	}

	public List<String> getTermenPlata() {
		return termenPlata;
	}

	public void setTermenPlata(List<String> termenPlata) {
		this.termenPlata = termenPlata;
	}

}
