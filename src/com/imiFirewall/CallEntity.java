package com.imiFirewall;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressLint("SimpleDateFormat")
public class CallEntity {
	
	private String number;
	private Long date;
	
	public CallEntity() {
		
	}
	
	public CallEntity(String number, Long date) {
		this.number = number;
		this.date = date;
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public Long getDate() {
		return date;
	}
	
	public void setDate(Long date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date(this.date);
		return "来电：" + this.number + "\n时间：" + sdf.format(date);
	}

}
