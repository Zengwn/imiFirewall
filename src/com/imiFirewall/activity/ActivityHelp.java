package com.imiFirewall.activity;

import com.imiFirewall.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class ActivityHelp extends AlertDialog{

	@SuppressLint("InflateParams")
	public ActivityHelp(Context context) {
		super(context);
		final View view = getLayoutInflater().inflate(R.layout.help_dialog, null);
		setButton(context.getText(R.string.close), (OnClickListener)null);
		setView(view);
	}
   
}