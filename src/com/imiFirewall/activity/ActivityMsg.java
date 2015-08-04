package com.imiFirewall.activity;

import java.util.List;

import com.imiFirewall.MsgEntity;
import com.imiFirewall.R;
import com.imiFirewall.imiDataBase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityMsg extends ListActivity implements OnClickListener, OnItemClickListener{
	
	ListView lv = null;
	List<MsgEntity> msgs = null;
	ImageButton ibtBack, ibtSet;
	ArrayAdapter<MsgEntity> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_layout);
		
		imiDataBase imidb = new imiDataBase(ActivityMsg.this);
		imidb.open();
		msgs = imidb.queryMsg();
		imidb.close();
		
		ibtBack = (ImageButton) findViewById(R.id.msg_back);
		ibtBack.setOnClickListener(this);
		
		ibtSet = (ImageButton) findViewById(R.id.msg_set);
		ibtSet.setOnClickListener(this);
		
		lv = getListView();
		
		adapter = new ArrayAdapter<MsgEntity>(this, R.layout.msglist, msgs);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.msg_back)
			this.finish();
		if (v.getId() == R.id.msg_set) {
			Intent intentMsgSet = new Intent(ActivityMsg.this, ActivityMsgRul.class);
			startActivity(intentMsgSet);
		}
			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(ActivityMsg.this, msgs.get(position).toString(), Toast.LENGTH_LONG).show();
	}

	
	
}
