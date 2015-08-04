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
        //设置桌面图标
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
        //设置按钮
        btnSet = (ImageButton) findViewById(R.id.mainset);
        btnSet.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSet = new Intent(ActivityMain.this, ActivitySetting.class);
				startActivity(intentSet);
			}
        	
        });
        
        //流量监控设置
        btnData = (ImageButton) findViewById(R.id.btnData);
        btnData.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentData = new Intent(ActivityMain.this, ActivityData.class);
				startActivity(intentData);
			}
        });
        
        //软件管理权限管理
        btnSoft = (ImageButton) findViewById(R.id.btnSoft);
        btnSoft.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSoft = new Intent(ActivityMain.this, ActivitySoft.class);
				startActivity(intentSoft);
			}
        });
        
        //骚扰拦截
        btnHold = (ImageButton) findViewById(R.id.btnHold);
        btnHold.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSpam = new Intent(ActivityMain.this,ActivitySpam.class);
				startActivity(intentSpam);
			}
        });
        
        //高级工具
        btnTools = (ImageButton) findViewById(R.id.btnTools);
        btnTools.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentTools = new Intent(ActivityMain.this, ActivityAdvance.class);
				startActivity(intentTools);
			}
        });
             
    }
    
    //设置Menu按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
          super.onCreateOptionsMenu(menu);
          menu.add(0, ITEM0, 0, "退出程序");
          return true;
    }
    
    //设置监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    
      switch(item.getItemId()){
      case ITEM0:
    	  System.exit(0);
    	  break;
      }
      return super.onOptionsItemSelected(item);
    }
    
   //安装快捷图标
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