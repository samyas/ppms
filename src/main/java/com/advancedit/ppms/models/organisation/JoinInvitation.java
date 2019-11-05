package com.advancedit.ppms.models.organisation;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invitations")
public class JoinInvitation {

	@Id
	private String id;
	private String name;
	private String description;
	private String longDescription;
	private String email;
	private Address address;
	private String phone;
	private String contactEmail;
	private String logoId;
    
	private OrganisationType type;
	
	private List<Department> departments;
    
	@Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
	private String tenantId;
    
	public JoinInvitation() {
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



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getLongDescription() {
		return longDescription;
	}



	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public Address getAddress() {
		return address;
	}



	public void setAddress(Address address) {
		this.address = address;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getContactEmail() {
		return contactEmail;
	}



	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}


	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}



	public String getLogoId() {
		return logoId;
	}



	public void setLogoId(String logoId) {
		this.logoId = logoId;
	}



	public OrganisationType getType() {
		return type;
	}



	public void setType(OrganisationType type) {
		this.type = type;
	}



	public List<Department> getDepartments() {
		return departments;
	}



	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}
	
	
	
    
}
