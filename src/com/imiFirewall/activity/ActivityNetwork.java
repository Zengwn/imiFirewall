package com.imiFirewall.activity;

import java.util.Arrays;
import java.util.Comparator;
import java.lang.String;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.content.DialogInterface;

import com.imiFirewall.R;
import com.imiFirewall.imiApi;
import com.imiFirewall.imiApi.DroidApp;

public class ActivityNetwork extends Activity implements OnCheckedChangeListener, OnClickListener{
	
	private ListView listview;
	private ProgressDialog progress = null;
	private ImageButton btnWifi, btn3G;
	private TextView btnSave;
	private ListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_layout);
        
        btnWifi = (ImageButton) findViewById(R.id.network_img_wifi);
        btnWifi.setOnClickListener(this);
        
        btn3G = (ImageButton) findViewById(R.id.netwrok_img_3g);
        btn3G.setOnClickListener(this);
        
        btnSave = (TextView) findViewById(R.id.data_save);
        btnSave.setOnClickListener(this);
    }

    
    @Override
    public void onResume(){
    	super.onResume();
    	if(this.listview==null){
    		this.listview =(ListView) this.findViewById(R.id.listview);
    	}
    	showOrLoadApplications();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	this.listview.setAdapter(null);
    }
    

    //异步加载所有app信息
	private void showOrLoadApplications() {
    	if (imiApi.applications == null) {
    		progress = ProgressDialog.show(this, "忙碌中", "正在读取安装的软件", true);
        	final Handler handler = new Handler() {
        		public void handleMessage(Message msg) {
        			if (progress != null) 
        				progress.dismiss();
        			showApplications();
        		}
        	};
        	new Thread() {
        		public void run() {
        			imiApi.getApps(ActivityNetwork.this);
        			handler.sendEmptyMessage(0);
        		}
        	}.start();
    	} else {
        	showApplications();
    	}
	}
    
	 //展示所有的app信息
    private void showApplications() {
        final DroidApp[] apps = imiApi.getApps(this);
        //显示信息排序
        Arrays.sort(apps, new Comparator<DroidApp>() {
			@Override
			public int compare(DroidApp o1, DroidApp o2) {
				//若有开关有开启的，则按名称
				if ((o1.selected_wifi|o1.selected_3g) == (o2.selected_wifi|o2.selected_3g)) {
					return o1.names[0].compareTo(o2.names[0]);
				}
				//若两个开关都没开启
				if (o1.selected_wifi || o1.selected_3g)
					return -1;
				return 1;
			}
        });
        
        //设置ListView
        final LayoutInflater inflater = getLayoutInflater();
		adapter = new ArrayAdapter<DroidApp>(this, R.layout.listitem, R.id.itemtext, apps) {
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
       			ListEntry entry;
       			//若缓存中不存在
        		if (convertView == null) {
        			convertView = inflater.inflate(R.layout.listitem, parent, false);
       				entry = new ListEntry();
       				entry.box_wifi = (CheckBox) convertView.findViewById(R.id.itemcheck_wifi);
       				entry.box_3g = (CheckBox) convertView.findViewById(R.id.itemcheck_3g);
       				entry.text = (TextView) convertView.findViewById(R.id.itemtext);
       				convertView.setTag(entry);
       				//设置监听
       				entry.box_wifi.setOnCheckedChangeListener(ActivityNetwork.this);
       				entry.box_3g.setOnCheckedChangeListener(ActivityNetwork.this);
        		} else {
        			//若缓存中不存在
        			entry = (ListEntry) convertView.getTag();
        		}
        		//设置数据
        		final DroidApp app = apps[position];
        		entry.text.setText(app.getName());
        		final CheckBox box_wifi = entry.box_wifi;
        		box_wifi.setTag(app);
        		box_wifi.setChecked(app.selected_wifi);
        		final CheckBox box_3g = entry.box_3g;
        		box_3g.setTag(app);
        		box_3g.setChecked(app.selected_3g);
       			return convertView;
        	}
        	
        };
        this.listview.setAdapter(adapter);
    }
    
    //保存更改
    public void saveData(){
    	//线程消息传递的异步处理
    	final Handler handler;
    	final boolean enabled = imiApi.isEnabled(this);
    	progress=ProgressDialog.show(this,"Working...",(enabled?"Applying":"Saving")+"the Firewall rules", true);
    	handler = new Handler() {
    		public void handleMessage(Message msg){
    			if(progress!=null) 
    				progress.dismiss();
    			//若开启成功 则直接更改
    			if (enabled) {
    				if (!imiApi.hasRootAccess(ActivityNetwork.this, true)) 
    					return;
					if (imiApi.applyIptablesRules(ActivityNetwork.this, true)) {
						Toast.makeText(ActivityNetwork.this, "Rules applied with success", Toast.LENGTH_SHORT).show();
					}
				} else {
					//若防火墙没有开启 则把规则存储
					imiApi.saveRules(ActivityNetwork.this);
					Toast.makeText(ActivityNetwork.this, "Rules saved with success", Toast.LENGTH_SHORT).show();
				}
    		}
    	};
    	handler.sendEmptyMessageDelayed(0, 200);
    }
    
    
    //检查每个按键的变换
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final DroidApp app = (DroidApp) buttonView.getTag();
		if (app != null) {
			switch (buttonView.getId()) {
				case R.id.itemcheck_wifi: 
					app.selected_wifi = isChecked; 
					break;
				case R.id.itemcheck_3g: 
					app.selected_3g = isChecked; 
					break;
			}
		}
	}
    
    
    private static class ListEntry {
		private CheckBox box_wifi;
		private CheckBox box_3g;
		private TextView text;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.network_img_wifi:
	    	new AlertDialog.Builder(this).setItems(
	    			new String[]{"全部开启","全部禁用"}, 
	    			new DialogInterface.OnClickListener(){
	    				public void onClick(DialogInterface dialog, int which) {
	    					if (which == 0)
	    						Toast.makeText(ActivityNetwork.this, "全部开启", Toast.LENGTH_SHORT).show();
//	    						allSelected(true);
	    					else if (which == 1)
	    						Toast.makeText(ActivityNetwork.this, "全部禁用", Toast.LENGTH_SHORT).show();
//	    						allCancle(true);
				}
	    	}).setTitle("通过WIFI联网").show();
			break;
		case R.id.netwrok_img_3g:
			new AlertDialog.Builder(this).setItems(
	    			new String[]{"全部开启","全部禁用"}, 
	    			new DialogInterface.OnClickListener(){
	    				public void onClick(DialogInterface dialog, int which) {
	    					if (which == 0)
	    						Toast.makeText(ActivityNetwork.this, "全部开启", Toast.LENGTH_SHORT).show();
	    					else if (which == 1)
	    						Toast.makeText(ActivityNetwork.this, "全部禁用", Toast.LENGTH_SHORT).show();
				}
	    	}).setTitle("通过数据联网").show();
			break;
		case R.id.data_save:
			saveData();
			break;
		}
	}
    
}