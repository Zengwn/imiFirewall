package com.imiFirewall.activity;


import com.imiFirewall.R;
import com.imiFirewall.imiApi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends Activity {
    
	ImageButton btnSet, btnData, btnSoft, btnHold, btnTools;
	
	public static final int ITEM0 = Menu.FIRST;
	
	private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	private static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //��������ͼ��
        final SharedPreferences prefs = getSharedPreferences(imiApi.PREFS_NAME, MODE_PRIVATE);
        final boolean hasshortcut = prefs.getBoolean(imiApi.PREF_SHORTCUT, false);        
        if(!hasshortcut)
        {
        	InstallShortCut();
        	final Editor edit = prefs.edit();
            edit.putBoolean(imiApi.PREF_SHORTCUT, true);
            edit.commit();
        }
        
        setContentView(R.layout.mainlayout);
        //���ð�ť
        btnSet = (ImageButton) findViewById(R.id.mainset);
        btnSet.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSet = new Intent(ActivityMain.this, ActivitySetting.class);
				startActivity(intentSet);
			}
        	
        });
        
        //�����������
        btnData = (ImageButton) findViewById(R.id.btnData);
        btnData.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentData = new Intent(ActivityMain.this, ActivityData.class);
				startActivity(intentData);
			}
        });
        
        //�������Ȩ�޹���
        btnSoft = (ImageButton) findViewById(R.id.btnSoft);
        btnSoft.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSoft = new Intent(ActivityMain.this, ActivitySoft.class);
				startActivity(intentSoft);
			}
        });
        
        //ɧ������
        btnHold = (ImageButton) findViewById(R.id.btnHold);
        btnHold.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSpam = new Intent(ActivityMain.this,ActivitySpam.class);
				startActivity(intentSpam);
			}
        });
        
        //�߼�����
        btnTools = (ImageButton) findViewById(R.id.btnTools);
        btnTools.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentTools = new Intent(ActivityMain.this, ActivityAdvance.class);
				startActivity(intentTools);
			}
        });
             
    }
    
    //����Menu��ť
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
          super.onCreateOptionsMenu(menu);
          menu.add(0, ITEM0, 0, "�˳�����");
          return true;
    }
    
    //���ü����¼�
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    
      switch(item.getItemId()){
      case ITEM0:
    	  System.exit(0);
    	  break;
      }
      return super.onOptionsItemSelected(item);
    }
    
   //��װ���ͼ��
    public void InstallShortCut()
    {
    	
    	Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);  
    	shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
    	shortcutIntent.putExtra(EXTRA_SHORTCUT_DUPLICATE, false);
    	Intent intent = new Intent();  
    	intent.setComponent(new ComponentName(this.getPackageName(),".ActivityMain"));
    	shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);  
    	shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this,R.drawable.icon));  
    	sendBroadcast(shortcutIntent);  
    	
    }
    
    
}