package beans;

public class BeanClient {

	private String numeClient;
	private String codClient;
	private String tipClient;
	private String codAgent;
	private String numeAgent;

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

	public String getCodAgent() {
		return codAgent;
	}

	public void setCodAgent(String codAgent) {
		this.codAgent = codAgent;
	}

	public String getNumeAgent() {
		return numeAgent;
	}

	public void setNumeAgent(String numeAgent) {
		this.numeAgent = numeAgent;
	}

}
