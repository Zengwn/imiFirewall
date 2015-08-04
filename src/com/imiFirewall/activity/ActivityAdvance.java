package com.imiFirewall.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imiFirewall.R;
import com.imiFirewall.activity.process.ActivityProcess;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ActivityAdvance extends ListActivity {

	public final static String AD_TITLE = "title";
	public final static String AD_DESC = "desc";
	public final static String AD_IMG = "img";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools);
		List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put(AD_TITLE, "命令行工具");
		item1.put(AD_DESC, "用户可以使用预设的命令查看手机CPU,内存，硬盘和端口等信息");
		item1.put(AD_IMG, R.drawable.ic_cmd);
		value.add(item1);

		Map<String, Object> item3 = new HashMap<String, Object>();
		item3.put(AD_TITLE, "手机进程管理");
		item3.put(AD_DESC, "用户可以使用该工具，对本机的进程进行查看和卸载管理");
		item3.put(AD_IMG, R.drawable.ic_process);
		value.add(item3);

		setListAdapter(new SimpleAdapter(
				this, 
				value, 
				R.layout.advancelistitem,
				new String[] { AD_IMG, AD_TITLE, AD_DESC }, 
				new int[] {R.id.advance_cmd_img, R.id.advance_cmd_title,R.id.advance_cmd_desc }));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case 0:
			Intent intentShell = new Intent(this, ActivityShell.class);
			startActivity(intentShell);
			break;
		case 1:
			Intent intentProcess = new Intent(this, ActivityProcess.class);
			startActivity(intentProcess);
			break;
		}
	}
}