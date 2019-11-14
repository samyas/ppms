package com.advancedit.ppms.models.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

	
	@Id
	private String id;
	@Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
	private String email;
	private String password;
	private String username;
	private boolean emailIsValid = false;
	private boolean enabled = false;
	
    private Boolean organisationCreationRequest;
    private String message;
	
    private List<Long> tenantIds = new ArrayList<>();
	
	//@DBRef
	private Set<Role> roles;
	private Set<Permission> permissions;
	
}
