package com.imiFirewall.service;

import com.imiFirewall.Interface.CallEventListener;
import com.imiFirewall.broadcast.SMSBroadcast;
import com.imiFirewall.CallEntity;
import com.imiFirewall.imiCallEngine;
import com.imiFirewall.imiDataBase;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;



public class FireService extends Service implements CallEventListener
{

	private imiCallEngine mCallEngine;
	
	@Override
	public void onCreate(){
		super.onCreate();
		//设置来电监听器
		mCallEngine = new imiCallEngine(this, this);
		mCallEngine.StartListen((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));

		//设置短信监听广播
		SMSBroadcast mReceiver = new SMSBroadcast();
		IntentFilter iFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		iFilter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mReceiver, iFilter);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);
		
	}

	// 来电话
	@Override
	public void CallEventL(int state, String incomingNumber) {
		// 状态
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE: {
			break;
		}
		case TelephonyManager.CALL_STATE_RINGING: {
			processCall(incomingNumber);
			break;
		}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	

	//呼出电话操作
	public void processOutgoingCall(String outGoingNumber){
		
	}
   
	//呼入电话操作
	public void processCall(String incomingNumber) {
		// 若为黑名单号码
		if (isBlack(incomingNumber)) {
			final String incom = incomingNumber;
			mCallEngine.Hangup();
			new Thread(new Runnable() {
				@Override
				public void run() {
					CallEntity callEntity = new CallEntity(incom, System.currentTimeMillis());
					saveCallRecord(callEntity);
				}
			}).start();
		}
	}
	
	private boolean isBlack(String incomingNumber) {
		imiDataBase imidb = new imiDataBase(FireService.this);
		imidb.open();
		if (imidb.query(incomingNumber)) {
			imidb.close();
			return true;
		}
		imidb.close();
		return false;
	}
	
	private void saveCallRecord(CallEntity callEntity) {
		//存储电话拦截记录
		imiDataBase imidb = new imiDataBase(FireService.this);
		imidb.open();
		imidb.insert("CallTable", callEntity);
		imidb.close();
	}
	
}