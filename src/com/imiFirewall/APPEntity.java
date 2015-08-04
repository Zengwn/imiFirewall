package com.imiFirewall;

import android.os.Parcel;
import android.os.Parcelable;

public class APPEntity implements Parcelable{
	private String appName;
	private String[] appPerm;
	private String appPackage;
	
	public APPEntity() {
		
	}
	
	public APPEntity(String appName, String appPackage, String[] appPerm) {
		this.appName = appName;
		this.appPackage = appPackage;
		this.appPerm = appPerm;
	}
	
	public String getAppPackage() {
		return appPackage;
	}
	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String[] getAppPerm() {
		return appPerm;
	}
	public void setAppPerm(String[] appPerm) {
		this.appPerm = appPerm;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appName);
		dest.writeString(appPackage);
		dest.writeStringArray(appPerm);
	}
	
	public static final Parcelable.Creator<APPEntity> CREATOR = new Parcelable.Creator<APPEntity>() {

		@Override
		public APPEntity createFromParcel(Parcel source) {
			return new APPEntity(source.readString(), source.readString(), source.createStringArray());
		}

		@Override
		public APPEntity[] newArray(int size) {
			return new APPEntity[size];
		}
	};
}
