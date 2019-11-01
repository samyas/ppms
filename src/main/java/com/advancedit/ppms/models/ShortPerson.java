package com.advancedit.ppms.models;

public class ShortPerson {
	private String personId;
	private String firstName;
	private String lastName;
	private String imageId;
	public ShortPerson(String personId, String firstName, String lastName, String imageId) {
		super();
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageId = imageId;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	
}
