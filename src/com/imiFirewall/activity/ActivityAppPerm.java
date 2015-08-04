package com.imiFirewall.activity;


import com.imiFirewall.APPEntity;

import com.imiFirewall.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ActivityAppPerm extends ListActivity{
	

	APPEntity app = null;
	ListView lv = null;
	ListAdapter adapter = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.permission_detail_layout);
		
		app = getIntent().getParcelableExtra("APP");
		lv = (ListView) getListView();
		
		
		adapter = new ArrayAdapter<String>(this, R.layout.blacklist, app.getAppPerm());
		lv.setAdapter(adapter);
		
	}

}
