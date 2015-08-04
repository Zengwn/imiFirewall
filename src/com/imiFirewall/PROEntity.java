package com.imiFirewall;

public class PROEntity {
	private long id;
	private boolean isChecked;
	private boolean isSystem;
	private long memory;
	private int pid;
	private String processName;
	private String title;
	private String[] pkgs;

	public long getId() {
		return this.id;
	}

	public long getMemory() {
		return this.memory;
	}

	public int getPid() {
		return this.pid;
	}

	public String getProcessName() {
		return this.processName;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getPKGs() {
		String res = "";
		for (String str:this.pkgs)
			res += str +"\n";
		return res;
	}

	public boolean isChecked() {
		return this.isChecked;
	}

	public boolean isSystem() {
		return this.isSystem;
	}

	public void setChecked(boolean paramBoolean) {
		this.isChecked = paramBoolean;
	}

	public void setId(long paramLong) {
		this.id = paramLong;
	}

	public void setMemory(long paramLong) {
		this.memory = paramLong;
	}

	public void setPid(int paramInt) {
		this.pid = paramInt;
	}

	public void setProcessName(String paramString) {
		this.processName = paramString;
	}

	public void setSystem(boolean paramBoolean) {
		this.isSystem = paramBoolean;
	}

	public void setTitle(String paramString) {
		this.title = paramString;
	}
	
	public void setPKGS(String[] strings) {
		this.pkgs = strings;
	}
}