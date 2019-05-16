package model;

import java.util.HashMap;

import listeners.AsyncTaskListener;
import listeners.OperatiiMeniuListener;
import my.logon.screen.AsyncTaskWSCall;
import android.content.Context;
import enums.EnumOperatiiMeniu;

public class OperatiiMeniu implements AsyncTaskListener {

	private EnumOperatiiMeniu numeComanda;
	private Context context;
	private OperatiiMeniuListener meniuListener;

	public OperatiiMeniu(Context context) {
		this.context = context;
	}

	public void savePinMeniu(HashMap<String, String> params) {
		numeComanda = EnumOperatiiMeniu.SAVE_PIN;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNumeOp(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void blocheazaMeniu(HashMap<String, String> params) {
		numeComanda = EnumOperatiiMeniu.BLOCHEAZA_MENIU;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNumeOp(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void deblocheazaMeniu(HashMap<String, String> params) {
		numeComanda = EnumOperatiiMeniu.DEBLOCHEAZA_MENIU;
		AsyncTaskWSCall call = new AsyncTaskWSCall(numeComanda.getNumeOp(), params, (AsyncTaskListener) this, context);
		call.getCallResultsFromFragment();
	}

	public void setOperatiiMeniuListener(OperatiiMeniuListener meniuListener) {
		this.meniuListener = meniuListener;
	}

	@Override
	public void onTaskComplete(String methodName, Object result) {
		if (meniuListener != null)
			meniuListener.pinCompleted(numeComanda, Boolean.parseBoolean(result.toString().toLowerCase()));
	}

}
