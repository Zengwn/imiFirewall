package com.imiFirewall.broadcast;

import java.util.List;

import com.imiFirewall.MsgEntity;
import com.imiFirewall.imiDataBase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SMSBroadcast extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object p : pdus) {
			byte[] pdu = (byte[]) p;
			SmsMessage message = SmsMessage.createFromPdu(pdu);
			final String senderNumber = message.getOriginatingAddress();
			final String smsContent = message.getDisplayMessageBody();
			final Context ctx = context;
			if (isSpam(senderNumber, smsContent, context)) {
				abortBroadcast();
				MsgEntity msg = new MsgEntity(senderNumber, smsContent, System.currentTimeMillis());
				saveMsgInter(msg, ctx);
			}
		}
	}
	
	private boolean isSpam(String telNumber, String smsContent, Context context) {
		//黑名单拦截
		if (isBlack(telNumber, context))
			return true;
		//短信内容过滤
		if (isSpamCon(smsContent, context))
			return true;
		return false;
	}
	
	//黑名单
	private boolean isBlack(String incomingNumber, Context context) {
		imiDataBase imidb = new imiDataBase(context);
		imidb.open();
		if (imidb.query(incomingNumber)) {
			imidb.close();
			return true;
		}
		imidb.close();
		return false;
	}
	
	//短信内容过滤
	private boolean isSpamCon(String smsContent, Context context) {
		
		imiDataBase imidb = new imiDataBase(context);
		imidb.open();
		List<String> ruls = imidb.queryRul();
		imidb.close();
		
		if (ruls.size() == 0)
			return false;
		
		for (String str:ruls) {
			if (smsContent.contains(str)) {
				return true;
			}
		}
		return false;
	}
	
	private void saveMsgInter(MsgEntity msg, Context context) {
		// 存储短信内容
		imiDataBase imidb = new imiDataBase(context);
		imidb.open();
		imidb.insert("MsgTable", msg);
		imidb.close();
	}

}
