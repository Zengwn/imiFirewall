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
    

    //�첽��������app��Ϣ
	private void showOrLoadApplications() {
    	if (imiApi.applications == null) {
    		progress = ProgressDialog.show(this, "æµ��", "���ڶ�ȡ��װ�����", true);
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
    
	 //չʾ���е�app��Ϣ
    private void showApplications() {
        final DroidApp[] apps = imiApi.getApps(this);
        //��ʾ��Ϣ����
        Arrays.sort(apps, new Comparator<DroidApp>() {
			@Override
			public int compare(DroidApp o1, DroidApp o2) {
				//���п����п����ģ�������
				if ((o1.selected_wifi|o1.selected_3g) == (o2.selected_wifi|o2.selected_3g)) {
					return o1.names[0].compareTo(o2.names[0]);
				}
				//���������ض�û����
				if (o1.selected_wifi || o1.selected_3g)
					return -1;
				return 1;
			}
        });
        
        //����ListView
        final LayoutInflater inflater = getLayoutInflater();
		adapter = new ArrayAdapter<DroidApp>(this, R.layout.listitem, R.id.itemtext, apps) {
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
       			ListEntry entry;
       			//�������в�����
        		if (convertView == null) {
        			convertView = inflater.inflate(R.layout.listitem, parent, false);
       				entry = new ListEntry();
       				entry.box_wifi = (CheckBox) convertView.findViewById(R.id.itemcheck_wifi);
       				entry.box_3g = (CheckBox) convertView.findViewById(R.id.itemcheck_3g);
       				entry.text = (TextView) convertView.findViewById(R.id.itemtext);
       				convertView.setTag(entry);
       				//���ü���
       				entry.box_wifi.setOnCheckedChangeListener(ActivityNetwork.this);
       				entry.box_3g.setOnCheckedChangeListener(ActivityNetwork.this);
        		} else {
        			//�������в�����
        			entry = (ListEntry) convertView.getTag();
        		}
        		//��������
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
    
    //�������
    public void saveData(){
    	//�߳���Ϣ���ݵ��첽����
    	final Handler handler;
    	final boolean enabled = imiApi.isEnabled(this);
    	progress=ProgressDialog.show(this,"Working...",(enabled?"Applying":"Saving")+"the Firewall rules", true);
    	handler = new Handler() {
    		public void handleMessage(Message msg){
    			if(progress!=null) 
    				progress.dismiss();
    			//�������ɹ� ��ֱ�Ӹ���
    			if (enabled) {
    				if (!imiApi.hasRootAccess(ActivityNetwork.this, true)) 
    					return;
					if (imiApi.applyIptablesRules(ActivityNetwork.this, true)) {
						Toast.makeText(ActivityNetwork.this, "Rules applied with success", Toast.LENGTH_SHORT).show();
					}
				} else {
					//������ǽû�п��� ��ѹ���洢
					imiApi.saveRules(ActivityNetwork.this);
					Toast.makeText(ActivityNetwork.this, "Rules saved with success", Toast.LENGTH_SHORT).show();
				}
    		}
    	};
    	handler.sendEmptyMessageDelayed(0, 200);
    }
    
    
    //���ÿ�������ı任
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
	    			new String[]{"ȫ������","ȫ������"}, 
	    			new DialogInterface.OnClickListener(){
	    				public void onClick(DialogInterface dialog, int which) {
	    					if (which == 0)
	    						Toast.makeText(ActivityNetwork.this, "ȫ������", Toast.LENGTH_SHORT).show();
//	    						allSelected(true);
	    					else if (which == 1)
	    						Toast.makeText(ActivityNetwork.this, "ȫ������", Toast.LENGTH_SHORT).show();
//	    						allCancle(true);
				}
	    	}).setTitle("ͨ��WIFI����").show();
			break;
		case R.id.netwrok_img_3g:
			new AlertDialog.Builder(this).setItems(
	    			new String[]{"ȫ������","ȫ������"}, 
	    			new DialogInterface.OnClickListener(){
	    				public void onClick(DialogInterface dialog, int which) {
	    					if (which == 0)
	    						Toast.makeText(ActivityNetwork.this, "ȫ������", Toast.LENGTH_SHORT).show();
	    					else if (which == 1)
	    						Toast.makeText(ActivityNetwork.this, "ȫ������", Toast.LENGTH_SHORT).show();
				}
	    	}).setTitle("ͨ����������").show();
			break;
		case R.id.data_save:
			saveData();
			break;
		}
	}
    
}