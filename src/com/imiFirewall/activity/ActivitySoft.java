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

public class ActivitySoft extends ListActivity {

	public final static String AD_TITLE = "title";
	public final static String AD_DESC = "desc";
	public final static String AD_IMG = "img";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.softlayout);
		
		List<Map<String, Object>> value = new ArrayList<Map<String, Object>>();
		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put(AD_TITLE, "Ȩ�޹���");
		item1.put(AD_DESC, "�û����Բ鿴����Ӧ�õ�Ȩ��");
		item1.put(AD_IMG, R.drawable.ic_soft_pow);
		value.add(item1);

		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put(AD_TITLE, "���ж��");
		item2.put(AD_DESC, "�û�����ж�س���");
		item2.put(AD_IMG, R.drawable.ic_soft_un);
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
			//Ȩ�޹���
			Intent intentSoftPer = new Intent(this, ActivitySoftPerm.class);
			startActivity(intentSoftPer);
			break;
		case 1:
			//���ж��
			Intent intentSoftDelete = new Intent(this, ActivitySoftDelete.class);
			startActivity(intentSoftDelete);
			break;
		
		}
	}
}
