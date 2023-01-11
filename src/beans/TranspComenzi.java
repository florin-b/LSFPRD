package beans;

public class TranspComenzi {
	private String filiala;
	private String transport;

	public String getFiliala() {
		return filiala;
	}

	public void setFiliala(String filiala) {
		this.filiala = filiala;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	@Override
	public String toString() {
		return "TranspComenzi{" + "filiala='" + filiala + '\'' + ", transport='" + transport + '\'' + '}';
	}
}
