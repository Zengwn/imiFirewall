package com.imiFirewall.activity;

import java.util.List;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class ActivityMsgRul extends ListActivity implements OnClickListener, OnItemClickListener{
	
	ImageButton ibtBack, ibtAdd;
	ListView lv = null;
	List<String> ruls = null;
	ArrayAdapter<String> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.msg_rul_layout);
		
		imiDataBase imidb = new imiDataBase(this);
		imidb.open();
		ruls = imidb.queryRul();
		imidb.close();
		
		ibtBack = (ImageButton) findViewById(R.id.black_back);
		ibtBack.setOnClickListener(this);
		
		ibtAdd = (ImageButton) findViewById(R.id.black_add);
		ibtAdd.setOnClickListener(this);
		
		lv = getListView();
		adapter = new ArrayAdapter<String>(this, R.layout.blacklist, ruls);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.black_back) {
			this.finish();
			//存储数据
			new Thread(new Runnable() {
				@Override
				public void run() {
					imiDataBase imidb = new imiDataBase(ActivityMsgRul.this);
					imidb.open();
					imidb.dropAllBlack("RuleTable");
					for (String str:ruls) {
						imidb.insert("RuleTable", str);
					}
					imidb.close();
				}
			}).start();
		} else if (v.getId() == R.id.black_add) {
			final EditText et = new EditText(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("添加规则").setView(et).setNegativeButton("cancel", null);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String temp = et.getText().toString();
					if (temp.length() != 0) {
						ruls.add(et.getText().toString());
						adapter.notifyDataSetChanged();
					}
				}
			});
			builder.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final int po = position;
		AlertDialog.Builder dialog =  new AlertDialog.Builder(this);
		String[] op = {"删除规则","修改规则"};
		dialog.setTitle("选择操作");
		DialogInterface.OnClickListener itemClickLisenter = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0: {
					ruls.remove(po);
					adapter.notifyDataSetChanged();
					break;
				}
				case 1: {
					final EditText et_m = new EditText(ActivityMsgRul.this);
					final String str_m = ruls.get(po);
					et_m.setText(str_m);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMsgRul.this);
					builder.setTitle("修改规则").setView(et_m).setNegativeButton("cancel", null);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String temp = et_m.getText().toString();
							if (temp.length() != 0 && !temp.equals(str_m)) {
								ruls.remove(po);
								ruls.add(et_m.getText().toString());
								adapter.notifyDataSetChanged();
							}
						}
					});
					builder.show();
				}
				}
			}
		};
		dialog.setItems(op, itemClickLisenter);
		dialog.show();
	}
	
	

}
