package com.advancedit.ppms.models.organisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.advancedit.ppms.models.files.FileDescriptor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "organisations")
public class Organisation {
	@Transient
	public static final String SEQUENCE_NAME = "organisations_sequence";

	@Id
	private String id;
	@Indexed(unique = true)
	private long tenantId;
	private String name;
	private String description;
	private String longDescription;
	private String email;
	private Address address;
	private String phone;
	private String creationDate;
	private String contactEmail;
	private String logoId;
	private String responsibleEmail;
	private FileDescriptor logo;


    
	private OrganisationType type;
	
	private List<Department> departments = new ArrayList<>();

    
}
