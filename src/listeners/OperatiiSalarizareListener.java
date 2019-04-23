package listeners;

import enums.EnumOperatiiSalarizare;

public interface OperatiiSalarizareListener {
	public void operatiiSalarizareComplete(EnumOperatiiSalarizare numeComanda, Object result);
	public void detaliiAgentSelected(String codAgent, String numeAgent);

}
