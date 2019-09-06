package listeners;

import enums.EnumOperatiiPierdereVanz;

public interface OperatiiPierdVanzListener {
	public void operationPierduteComplete(EnumOperatiiPierdereVanz methodName, Object result);
}
