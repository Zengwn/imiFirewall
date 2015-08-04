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
    itemMsg.put(AD_TITLE, "��������");
    itemMsg.put(AD_DESC, "���ö��Ź��򣬿��Խ����������ŵ�ɧ��");
    itemMsg.put(AD_IMG, R.drawable.ic_msg);
    value.add(itemMsg);
    
    Map<String, Object> itemTel = new HashMap<String, Object>();
    itemTel.put(AD_TITLE, "�绰����");
    itemTel.put(AD_DESC, "���õ绰���򣬼������ѵ绰��ɧ��");
    itemTel.put(AD_IMG, R.drawable.ic_tel);
    value.add(itemTel);
    
    Map<String,Object> itemNum = new HashMap<String, Object>();
    itemNum.put(AD_TITLE, "����������");
    itemNum.put(AD_DESC, "ͨ�����ú����������ض����������Ч����");
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
    	//�����������
         Intent intentMsg = new Intent(this, ActivityMsg.class);
         startActivity(intentMsg);
         break;
         
    case 1:
    	//����绰����
        Intent intentCall = new Intent(this, ActivityCall.class);
        startActivity(intentCall);
        break;
    case 2:
    	//�������������
    	Intent intentBlack = new Intent(ActivitySpam.this, ActivityBlack.class);
    	startActivity(intentBlack);
    	break;
         
    }
  }
}