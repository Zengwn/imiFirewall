package com.imiFirewall;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class MsgEntity {
	
	private String phoneNum = null;
	private String contentName = null;
	private long dateNow = 0L;
	
	public MsgEntity() {
		
	}
	
	public MsgEntity(String phoneNum, String contentName, long dateNow) {
		this.phoneNum = phoneNum;
		this.contentName = contentName;
		this.dateNow = dateNow;
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}
	
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	public String getContentName() {
		return contentName;
	}
	
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	
	public long getDateNow() {
		return dateNow;
	}
	
	public void setDateNow(long dateNow) {
		this.dateNow = dateNow;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date(this.dateNow);
		return "����:" + this.phoneNum + "\nʱ��:" + sdf.format(date) + "\n����:" + this.contentName;
	}
}
