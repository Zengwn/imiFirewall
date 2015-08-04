package com.imiFirewall.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

public final class Commons
{
	private static String APP_PKG_NAME_21="com.android.settings.ApplicationPkgName";
	private static String APP_PKG_NAME_22="pkg";

	//卸载软件
	public static  Intent getPackageDetailIntent(String app)
	{	
		Intent intent = new Intent();  
	    final int apiLevel = Build.VERSION.SDK_INT;  
	    if (apiLevel >= 9) {
	        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  
	        Uri uri = Uri.fromParts("package", app, null);  
	        intent.setData(uri);  
	    } else {
	        final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22  
	                : APP_PKG_NAME_21);  
	        intent.setAction(Intent.ACTION_VIEW);  
	        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");  
	        intent.putExtra(appPkgName, app);  
	    }  
	    return intent;
	}
	
	
	
	public static class PDATA implements Parcelable
	{

		public long mDate;
	    public int mId;
	    public int mIntValue_1;
	    public int mIntValue_2;
	    public int mIntValue_3;
	    public int mIntValue_4;
	    public String mStringValue_1;
	    public String mStringValue_2;
	    public String mStringValue_3;
	    
		public static final Parcelable.Creator<PDATA> CREATOR = new Creator<PDATA>(){

			@Override
			public PDATA createFromParcel(Parcel source) {
				// TODO Auto-generated method stub
				Commons.PDATA pData = new Commons.PDATA();

				pData.mIntValue_1   = source.readInt();
				pData.mStringValue_1= source.readString();
				
				
				return pData;
			}

			@Override
			public PDATA[] newArray(int size) {
				// TODO Auto-generated method stub
				return new Commons.PDATA[size];
			}
			
		};
		
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeInt(mIntValue_1);
			dest.writeString(mStringValue_1);
		}
		
	}
	
	public class PersonTypeData{
		
		 public String mName;
		 public String[] mPhoneArray;  //联系人中有多个号码
		 public String mRev;
	}
	
}