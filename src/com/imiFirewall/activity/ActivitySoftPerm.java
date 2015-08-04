package com.imiFirewall.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.imiFirewall.APPEntity;
import com.imiFirewall.PermEntity;
import com.imiFirewall.R;
import com.imiFirewall.util.imiJson;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class ActivitySoftPerm extends ListActivity{
	
	ListView listV = null;
	List<APPEntity> apps = new ArrayList<APPEntity>();
	Map<String, PermEntity> perTable = null;
	PermissonAdapter permissionAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//读取权限对应列表
		perTable = imiJson.readPerm();
		if (perTable != null)
			Log.v("debug", "" + perTable.size());
		//获取所有的程序
		PackageManager pm = getPackageManager();
		Intent query = new Intent(Intent.ACTION_MAIN);
		query.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolves = pm.queryIntentActivities(query, PackageManager.GET_ACTIVITIES);
		//遍历获得权限
		for (ResolveInfo r:resolves) {
			APPEntity appEntity = new APPEntity();
			appEntity.setAppPackage(r.activityInfo.packageName);
			appEntity.setAppName(r.loadLabel(pm).toString());
			try {
//				appEntity.setAppPerm(pm.getPackageInfo(appEntity.getAppPackage(), PackageManager.GET_PERMISSIONS).requestedPermissions);
				String[] permEn = pm.getPackageInfo(appEntity.getAppPackage(), PackageManager.GET_PERMISSIONS).requestedPermissions;
				if (permEn == null)
					appEntity.setAppPerm(null);
				else {
					List<String> src = new ArrayList<String>();
					for (String str:permEn) {
						if (perTable.containsKey(str))
							src.add(perTable.get(str).getTitle());
					}
					appEntity.setAppPerm(src.toArray(new String[0]));
				}
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			apps.add(appEntity);
		}
		//设置adapter
		permissionAdapter = new PermissonAdapter(ActivitySoftPerm.this, apps);
		listV = getListView();
		listV.setAdapter(permissionAdapter);
		listV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//定位置至详情界面
//				Toast.makeText(ActivitySoftPerm.this, Arrays.toString(apps.get(position).getAppPerm()), Toast.LENGTH_LONG).show();
				Intent intentAppPer = new Intent(ActivitySoftPerm.this, ActivityAppPerm.class);
				intentAppPer.putExtra("APP", apps.get(position));
				startActivity(intentAppPer);
			}
		});
	}
	
	class PermissonAdapter extends BaseAdapter {
		
		private List<APPEntity> mContent;
		private LayoutInflater mInflater;
		private PackageManager mPackageMgr;
		
		private PermissonAdapter(Context context, List<APPEntity> applist) {
			mContent = applist;
			mInflater = (LayoutInflater)LayoutInflater.from(context);
			mPackageMgr = context.getPackageManager();
		}
		
		private class PermHolder {
			TextView appName;
			ImageView appIcon;
			TextView appPerm;
		}

		@Override
		public int getCount() {
			return mContent.size();
		}

		@Override
		public APPEntity getItem(int position) {
			return mContent.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			PermHolder permHolder;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.permission_list_item, null);
				permHolder = new PermHolder();
				permHolder.appIcon  = (ImageView) convertView.findViewById(R.id.per_app_icon);
				permHolder.appName = (TextView) convertView.findViewById(R.id.per_app_name);
				permHolder.appPerm = (TextView) convertView.findViewById(R.id.per_app_per);
				
				convertView.setTag(permHolder);
			} else {
				permHolder = (PermHolder)convertView.getTag();
			}
			
			APPEntity appEntity = getItem(position);
			Drawable drawable = null;
			try {
				drawable = mPackageMgr.getApplicationIcon(appEntity.getAppPackage());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			permHolder.appIcon.setImageDrawable(drawable);
			permHolder.appName.setText(appEntity.getAppName());
			if (appEntity.getAppPerm() != null)
				permHolder.appPerm.setText("该应用共需要" + appEntity.getAppPerm().length + "权限！");
			else
				permHolder.appPerm.setText("该应用权限获取失败！");
			
			return convertView;
		}
		
	}

}
