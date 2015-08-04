package com.imiFirewall;

public class PersonEntity {
	private String name = null;
	private String tel = null;
	
	public PersonEntity() {
	}
	
	public PersonEntity(String name, String tel) {
		super();
		this.name = name;
		this.tel = tel;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	@Override
	public String toString() {
		return name + " " + tel;
	}
}
