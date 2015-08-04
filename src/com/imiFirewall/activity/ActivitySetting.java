package com.imiFirewall.activity;

import com.imiFirewall.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class ActivitySetting extends PreferenceActivity implements OnPreferenceClickListener{
	
	CheckBoxPreference warnCheckPref;
	EditTextPreference mobile_warn;
	EditTextPreference wifi_warn;
	Preference reset_data;
	Preference help;
	
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
		
		
	    super.onCreate(savedInstanceState);
	    
	    
	    addPreferencesFromResource(R.xml.setting);
	    
	    mobile_warn =  (EditTextPreference)findPreference("mobile_warn");
		wifi_warn   =  (EditTextPreference)findPreference("wifi_warn");
		
		//流量预警
	    warnCheckPref= (CheckBoxPreference)findPreference("network_warn");
	    warnCheckPref.setOnPreferenceClickListener(this);
	    if (warnCheckPref.isChecked()) {
	    	mobile_warn.setEnabled(true);
	    	wifi_warn.setEnabled(true);
	    } else {
	    	mobile_warn.setEnabled(false);
	    	wifi_warn.setEnabled(false);
	    }
	    
	    //流量重置
	    reset_data =  (Preference)findPreference("reset_data");
	    reset_data.setOnPreferenceClickListener(this);
	    
	    help = findPreference("help");
	    help.setOnPreferenceClickListener(this);
	  }
	
	//重置流量
	public void reset_data() throws Exception{
		//重置流量操作
	}
	 
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// 重置流量
		if (preference.getKey().equals("reset_data")) {
			try {
				reset_data();
			} catch (Exception e) {
				e.printStackTrace();
			}

			Toast.makeText(ActivitySetting.this, R.string.toast_resetdata_text,
					Toast.LENGTH_LONG).show();

			return true;

		}
		if(preference.getKey().equals("help")) {
			new ActivityHelp(ActivitySetting.this).show();
		}
		// 开启流量预警
		if (warnCheckPref.isChecked()) {
			mobile_warn.setEnabled(true);
			wifi_warn.setEnabled(true);
			return true;

		}
		// 关闭流量预警
		if (!warnCheckPref.isChecked()) {

			mobile_warn.setEnabled(false);
			wifi_warn.setEnabled(false);
			return true;
		}
		return true;
	}
   
}