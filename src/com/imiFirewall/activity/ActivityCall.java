package com.imiFirewall.activity;

import java.util.List;

import com.imiFirewall.CallEntity;
import com.imiFirewall.R;
import com.imiFirewall.imiDataBase;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityCall extends ListActivity implements OnClickListener, OnItemClickListener{
	
	ListView lv = null;
	List<CallEntity> calls = null;
	ImageButton ibtBack, ibtSet;
	ArrayAdapter<CallEntity> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_layout);
		
		imiDataBase imidb = new imiDataBase(ActivityCall.this);
		imidb.open();
		calls = imidb.queryCall();
		imidb.close();
		
		ibtBack = (ImageButton) findViewById(R.id.msg_back);
		ibtBack.setOnClickListener(this);
		
		ibtSet = (ImageButton) findViewById(R.id.msg_set);
		ibtSet.setOnClickListener(this);
		
		lv = getListView();
		
		adapter = new ArrayAdapter<CallEntity>(this, R.layout.msglist, calls);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.msg_back)
			this.finish();
		if (v.getId() == R.id.msg_set) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			String[] options = new String[3];
			options[0] = getString(R.string.phone_set_ontel);
			options[1] = getString(R.string.phone_set_off);
			options[2] = getString(R.string.phone_set_die);

			adb.setTitle(getString(R.string.phone_set_title));

			DialogInterface.OnClickListener opitemlistener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Toast.makeText(ActivityCall.this, "正在通话",
								Toast.LENGTH_LONG).show();
						break;
					case 1:
						Toast.makeText(ActivityCall.this, "已关机",
								Toast.LENGTH_LONG).show();
						break;
					case 2:
						Toast.makeText(ActivityCall.this, "已停机",
								Toast.LENGTH_LONG).show();
						break;
					}
				}
			};

			adb.setItems(options, opitemlistener);
			adb.create();
			adb.show();
		}
			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(ActivityCall.this, calls.get(position).toString(), Toast.LENGTH_LONG).show();
	}
	
}
