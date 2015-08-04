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

public class ActivitySpam extends ListActivity {

  public final static String AD_TITLE="title";
  public final static String AD_DESC ="desc";
  public final static String AD_IMG="img";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.spam_layout);
   
    List<Map<String, Object>> value= new ArrayList<Map<String,Object>>();
    
    Map<String, Object> itemMsg = new HashMap<String, Object>();
    itemMsg.put(AD_TITLE, "短信拦截");
    itemMsg.put(AD_DESC, "设置短信规则，可以较少垃圾短信的骚扰");
    itemMsg.put(AD_IMG, R.drawable.ic_msg);
    value.add(itemMsg);
    
    Map<String, Object> itemTel = new HashMap<String, Object>();
    itemTel.put(AD_TITLE, "电话拦截");
    itemTel.put(AD_DESC, "设置电话规则，减少吸费电话的骚扰");
    itemTel.put(AD_IMG, R.drawable.ic_tel);
    value.add(itemTel);
    
    Map<String,Object> itemNum = new HashMap<String, Object>();
    itemNum.put(AD_TITLE, "黑名单设置");
    itemNum.put(AD_DESC, "通过设置黑名单，对特定号码进行有效拦截");
    itemNum.put(AD_IMG, R.drawable.ic_number);
    value.add(itemNum);
    
    setListAdapter(new SimpleAdapter(this, value, R.layout.advancelistitem,
    			new String[] {AD_IMG,AD_TITLE, AD_DESC }, 
    			new int[] {R.id.advance_cmd_img,R.id.advance_cmd_title, R.id.advance_cmd_desc }));
  }
 
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    switch (position) {
    case 0:
    	//进入短信拦截
         Intent intentMsg = new Intent(this, ActivityMsg.class);
         startActivity(intentMsg);
         break;
         
    case 1:
    	//进入电话拦截
        Intent intentCall = new Intent(this, ActivityCall.class);
        startActivity(intentCall);
        break;
    case 2:
    	//进入黑名单设置
    	Intent intentBlack = new Intent(ActivitySpam.this, ActivityBlack.class);
    	startActivity(intentBlack);
    	break;
         
    }
  }
}