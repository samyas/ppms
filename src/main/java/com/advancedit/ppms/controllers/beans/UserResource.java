package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.user.Permission;
import com.advancedit.ppms.models.user.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResource {


	private String id;
	private String email;
	private String password;
	private String username;
	private boolean emailIsValid = false;
	private boolean enabled = false;
	private String firstName;
	private String lastName;
	private Boolean organisationCreationRequest;
	private String message;
	private List<Long> tenantIds = new ArrayList<>();
	private long defaultTenantId;
	private Set<Role> roles;
	private Set<Permission> permissions;
	private String personId;
	
}
