package com.imiFirewall.activity;

import java.util.ArrayList;
import java.util.List;
import com.imiFirewall.APPEntity;
import com.imiFirewall.R;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivitySoftDelete extends ListActivity {
	
	List<APPEntity> apps = new ArrayList<APPEntity>();
	DelAdapter delAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PackageManager pm = getPackageManager();
		Intent query = new Intent(Intent.ACTION_MAIN);
		query.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolves = pm.queryIntentActivities(query, PackageManager.GET_ACTIVITIES);

		for (ResolveInfo r:resolves) {
			APPEntity appEntity = new APPEntity();
			appEntity.setAppPackage(r.activityInfo.packageName);
			appEntity.setAppName(r.loadLabel(pm).toString());
			apps.add(appEntity);
		}
		
		delAdapter = new DelAdapter(ActivitySoftDelete.this, apps);
		getListView().setAdapter(delAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				APPEntity app = apps.get(position);
				String packagename = app.getAppPackage();
				Uri packageUri = Uri.parse("package:"+ packagename);
				Intent intentDelete = new Intent(Intent.ACTION_DELETE, packageUri);
				startActivity(intentDelete);
				apps.remove(position);
			}
		});
		
		
	}
	
	class DelAdapter extends BaseAdapter {
		
		private List<APPEntity> mContent;
		private LayoutInflater mInflater;
		private PackageManager mPackageMgr;
		
		private DelAdapter(Context context, List<APPEntity> applist) {
			mContent = applist;
			mInflater = (LayoutInflater)LayoutInflater.from(context);
			mPackageMgr = context.getPackageManager();
		}
		
		private class DelHolder {
			TextView appName;
			ImageView appIcon;
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			DelHolder delHolder;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.del_list_item, null);
				delHolder = new DelHolder();
				delHolder.appIcon  = (ImageView) convertView.findViewById(R.id.del_app_icon);
				delHolder.appName = (TextView) convertView.findViewById(R.id.del_app_name);
				
				convertView.setTag(delHolder);
			} else {
				delHolder = (DelHolder)convertView.getTag();
			}
			
			APPEntity appEntity = getItem(position);
			Drawable drawable = null;
			try {
				drawable = mPackageMgr.getApplicationIcon(appEntity.getAppPackage());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			delHolder.appIcon.setImageDrawable(drawable);
			delHolder.appName.setText(appEntity.getAppName());
			
			return convertView;
		}
		
	}
		
		
}
