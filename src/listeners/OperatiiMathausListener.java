package listeners;

import enums.EnumOperatiiMathaus;

public interface OperatiiMathausListener {
	public void operationComplete(EnumOperatiiMathaus methodName, Object result);
}
