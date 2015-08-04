package com.imiFirewall.activity.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.imiFirewall.PROEntity;
import com.imiFirewall.R;
import com.imiFirewall.common.Commons;
import com.imiFirewall.common.ProgressUtils;
import com.imiFirewall.util.imiProcess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ActivityProcess extends Activity
{
	
	public Handler handler;
	public ListView mList;
	public ProcessAdapter adapter;
	public ActivityManager mActivityMgr;
	public int mPosition;
	public ProgressUtils mProgress;
	public ProgressThread mProgressThread;

	
	public ActivityProcess(){
		handler = new ProcessHanlder();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_layout);
		
		if(savedInstanceState!=null)
		{
			int i = savedInstanceState.getInt("last_position", 0);
			mPosition=i;
		}
		//获取ListView，设置监听
		mList = (ListView) findViewById(R.id.process_listview);
		mList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				mPosition=arg2;
				createOperationDialog().show();
			}
			
		});
		mActivityMgr = (ActivityManager)getSystemService("activity");
		ViewStub stub = (ViewStub) findViewById(R.id.pro_viewstubid);
		mProgress = new ProgressUtils(this, stub);
				
	}
  
	//单击进程选择功能
	private Dialog createOperationDialog()
	{
		AlertDialog.Builder op_dialog = new AlertDialog.Builder(this);
		//设置选项
		String[] op_string = new String[3];
		op_string[0]= getString(R.string.process_op1);
		op_string[1]= getString(R.string.process_op2);
		op_string[2]= getString(R.string.process_op3);
		//设置标题、监听器
		op_dialog.setTitle(getString(R.string.process_dialog_title));
		DialogInterface.OnClickListener opitemlistener = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int pos = mPosition;
				switch(which)
				{
				case 0:
					launchProcess(pos);
					break;
				case 1:
					closeProcess(pos);
				    break;
				case 2:
					showProcessDetail(pos);
					break;
				}
				
			}
					
		};
		//弹出对话框
		op_dialog.setItems(op_string, opitemlistener);
		return op_dialog.create();
	}
	
	//打开进程
	private void launchProcess(int pos)
	{
		String app=((PROEntity)mList.getItemAtPosition(pos)).getProcessName();		
		Intent launch_intent=getPackageManager().getLaunchIntentForPackage(app);
		if(launch_intent!=null)
		{
			startActivity(launch_intent);
		}
	}
	//终结进程
	private void closeProcess(int pos)
	{
		String app=((PROEntity)mList.getItemAtPosition(pos)).getProcessName();
		ActivityManager am = this.mActivityMgr;
		imiProcess.killByLevel7(am,app);		
		((ProcessAdapter)mList.getAdapter()).removeContent(pos);
	}
	
	//显示详情
	private void showProcessDetail(int pos)
	{
	   String app=((PROEntity)mList.getItemAtPosition(pos)).getProcessName();
	   Intent detail =  Commons.getPackageDetailIntent(app); 	
	   startActivity(detail);
	}


	@Override
	protected void onStart() {
		super.onStart();
		mProgress.showSearchPanel();
		mProgressThread = new ProgressThread(handler);
		mProgressThread.start();
		
	}
	
	//通过进程获取运行进程的信息
	public PROEntity BuildProcessEntity(ActivityManager.RunningAppProcessInfo runningprocessInfo) throws NameNotFoundException
	{
		if (runningprocessInfo != null) {
			ApplicationInfo applicationinfo = null;
			PackageManager packagemanager = getPackageManager();

			PROEntity process = new PROEntity();

			applicationinfo = packagemanager.getPackageInfo(
					runningprocessInfo.processName, 1).applicationInfo;

			if (applicationinfo != null) {
				process.setTitle(applicationinfo.loadLabel(packagemanager).toString());
				process.setPid(runningprocessInfo.pid);
				process.setProcessName(runningprocessInfo.processName);
				process.setId(runningprocessInfo.pid);
				process.setPKGS(runningprocessInfo.pkgList);
			}
			return process;
		}

		return null;
	}
	
	//加载进程线程
	class ProgressThread extends Thread
	{
		private Handler mHandler;
		
		public ProgressThread(Handler handler){
			mHandler = handler;
		}
		
		private void initAdapter()
		{
			//获取所有的正在运行的进程
			List<RunningAppProcessInfo> pList = mActivityMgr.getRunningAppProcesses();
			
			ArrayList<PROEntity> mProcessArray =new ArrayList<PROEntity>();
			Iterator<RunningAppProcessInfo> it = pList.iterator();
			
			while(it.hasNext())
			{
				PROEntity processentity=null;
				try {
					ActivityManager.RunningAppProcessInfo runningappprocessinfo = (ActivityManager.RunningAppProcessInfo)it.next();
					processentity = BuildProcessEntity(runningappprocessinfo);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				
				if(processentity!=null)
				{
					mProcessArray.add(processentity);
					Message msg = mHandler.obtainMessage();			
					Bundle bundle = new Bundle();		
					String processname = processentity.getProcessName();
			        bundle.putString("content", processname);
			        msg.setData(bundle);
			     	msg.what=0;					
				    Handler handler1 = mHandler;
			     	handler1.sendMessage(msg);
				}
			   
			 
		    }
		
			adapter = new ProcessAdapter(ActivityProcess.this, mProcessArray);
			Message msg1 = mHandler.obtainMessage();
			msg1.what = 2;		
			Handler handler2 = mHandler;
			handler2.sendMessage(msg1);  
			
		}
		
		public void run()
		{
			initAdapter();
		}	
	}
	
	//更新界面
	@SuppressLint("HandlerLeak")
	class ProcessHanlder extends Handler                 //利用Handler进行异步通讯处理
	{
		public void handleMessage(Message msg)
		{
			if(msg.what==0)
			{
				Bundle bundle = msg.getData();
				if(bundle.containsKey("content"))
				{
				   //完成 进度条的相关操作
					String s1 = bundle.getString("content");
					mProgress.changeSearchPanelTitle(s1);
				}
			}
			if(msg.what==2)
			{
				//扫描完成
				 ProcessAdapter processadapter = adapter;								
			     mList.setAdapter(processadapter);
			     mProgress.hideSearchPanel();
			     
			}
		}
	}
	
	//设置adpater
	class ProcessAdapter extends BaseAdapter{
				
		private List mContent;
		private LayoutInflater mInflater;
		private PackageManager mPackageMgr;
				
		private ProcessAdapter(Context context, List applist){
			
			mContent   =  applist;
			mInflater  = (LayoutInflater)LayoutInflater.from(context);
			mPackageMgr= (PackageManager) context.getPackageManager();
			
		}		
		
		private class ViewHolder{
			TextView appName;
			ImageView appIcon;
			TextView appList;
		}

		@Override
		public int getCount() {
			return mContent.size();
		}

		@Override
		public PROEntity getItem(int position) {
			return (PROEntity)mContent.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder itemView;
			
			if(convertView==null)
			{
				convertView = mInflater.inflate(R.layout.process_list_item, null);
				itemView = new ViewHolder();
				itemView.appIcon=(ImageView)convertView.findViewById(R.id.process_app_icon);
				itemView.appName=(TextView)convertView.findViewById(R.id.process_app_name);
				itemView.appList=(TextView)convertView.findViewById(R.id.process_app_pks);
				
				convertView.setTag(itemView);
			}else{
				itemView= (ViewHolder)convertView.getTag();
			}
			
			PROEntity proentity =getItem(position);
			String processname = proentity.getProcessName();
			Drawable drawable=null;
			
			try {
				drawable =mPackageMgr.getApplicationIcon(processname);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			itemView.appIcon.setImageDrawable(drawable);
			String  processtitle = proentity.getTitle();			
			itemView.appName.setText(processtitle);
			String pks = proentity.getPKGs();
			itemView.appList.setText(pks);
			
			return convertView;
		}
		
		public void removeContent(int position){
			mContent.remove(position);
			notifyDataSetChanged();
		}
	}
}