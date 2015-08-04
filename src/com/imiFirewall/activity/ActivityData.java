package com.imiFirewall.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imiFirewall.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ActivityData extends ListActivity{
	public final static String AD_TITLE = "title";
	public final static String AD_DESC = "desc";
	public final static String AD_IMG = "img";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.softlayout);
		
		List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put(AD_TITLE, "联网防火墙");
		item1.put(AD_DESC, "用户可以配置联网程序的权限");
		item1.put(AD_IMG, R.drawable.ic_data_firewall);
		value.add(item1);

		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put(AD_TITLE, "流量监控");
		item2.put(AD_DESC, "用户可以查看各应用程序流量使用情况");
		item2.put(AD_IMG, R.drawable.ic_data_count);
		value.add(item2);

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
			//联网防火墙
			Intent intentNetwork = new Intent(this, ActivityNetwork.class);
			startActivity(intentNetwork);
			break;
		case 1:
			//流量统计
			Toast.makeText(ActivityData.this, "此功能还未开发！", Toast.LENGTH_LONG).show();;
//			Intent intentNetCount = new Intent(this, ActivityNetcount.class);
//			startActivity(intentNetCount);
			break;
		
		}
	}
}
