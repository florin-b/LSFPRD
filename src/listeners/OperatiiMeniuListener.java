package listeners;

import enums.EnumOperatiiMeniu;

public interface OperatiiMeniuListener {
	public void pinCompleted(EnumOperatiiMeniu numeOp, boolean isSuccess);
}
