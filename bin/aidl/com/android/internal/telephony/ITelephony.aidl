package com.android.internal.telephony;

/**
* Interface used to interact with the phone. Mostly this is used by the
* TelephonyManager class. A few places are still using this directly.
* Please clean them up if possible and use TelephonyManager instead.
* * {@hide}
*/

interface ITelephony {
	/** * End call or go to the Home screen *
	* @return whether it hung up
	*/
	boolean endCall();
	boolean isIdle();
	boolean isOffhook();
	boolean isRisinging();
	void answerRingingCall();
	void cancelMissedCallsNotification();
}
