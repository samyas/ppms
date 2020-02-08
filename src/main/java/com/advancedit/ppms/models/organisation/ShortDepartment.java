package com.advancedit.ppms.models.organisation;

public class ShortDepartment {
	private String id;
	private String name;
	public ShortDepartment(){}
	public ShortDepartment(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
