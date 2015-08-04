package com.imiFirewall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.imiFirewall.Interface.CallEventListener;

import android.app.Service;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class imiCallEngine extends PhoneStateListener {

	// 关联 的 服务对象
	private Service mContext;

	private CallEventListener mListener;

	public imiCallEngine(Service context, CallEventListener mEventL) {
		mContext = context;
		mListener = mEventL;
	}

	// 监听电话线路
	public void StartListen(TelephonyManager telMgr) {
		telMgr.listen(this, LISTEN_CALL_STATE);
	}

	public void Release() {
		TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		telMgr.listen(this, LISTEN_NONE);
	}

	public void Hangup() {
		ITelephony iTelephony = null;
		TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

		// 初始化iTelephony
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		try {
			iTelephony.endCall();
		} catch (Exception e) {

		}

	}

	public void onCallStateChanged(int state, String incomingNumber) {
		mListener.CallEventL(state, incomingNumber);
		super.onCallStateChanged(state, incomingNumber);
	}
}