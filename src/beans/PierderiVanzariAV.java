package beans;

import java.util.List;

public class PierderiVanzariAV {
	public List<PierdereVanz> pierderiHeader;
	public List<PierdereTipClient> listPierderiTipCl;
	public List<PierdereNivel1> listPierderiNivel1;

	public List<PierdereVanz> getPierderiHeader() {
		return pierderiHeader;
	}

	public void setPierderiHeader(List<PierdereVanz> pierderiHeader) {
		this.pierderiHeader = pierderiHeader;
	}

	public List<PierdereTipClient> getListPierderiTipCl() {
		return listPierderiTipCl;
	}

	public void setListPierderiTipCl(List<PierdereTipClient> listPierderiTipCl) {
		this.listPierderiTipCl = listPierderiTipCl;
	}

	public List<PierdereNivel1> getListPierderiNivel1() {
		return listPierderiNivel1;
	}

	public void setListPierderiNivel1(List<PierdereNivel1> listPierderiNivel1) {
		this.listPierderiNivel1 = listPierderiNivel1;
	}

}
