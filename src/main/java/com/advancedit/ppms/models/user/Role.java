package com.advancedit.ppms.models.user;

//@Document(collection = "roles")
public enum Role {
	SUPER_ADMIN,
	ADMIN,
	
	STUDENT,
	
	
	STAFF,
	
	ADMIN_CREATOR,
	
	;
	

	/*@Id
    private String id;
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)

    private String role;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}*/
    
}
