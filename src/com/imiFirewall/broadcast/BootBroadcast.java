package com.imiFirewall.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//��̬��������������̬����
public class BootBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent){
		Intent intentFireService = new Intent();
		intentFireService.setAction("com.imiFirewall.service.FireService");
		context.startService(intentFireService);
	}
}
