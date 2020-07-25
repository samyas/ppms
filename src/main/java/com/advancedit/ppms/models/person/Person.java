package com.advancedit.ppms.models.person;

import com.advancedit.ppms.models.files.FileDescriptor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document
public class Person {

	@Id
	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String skype;
	private String photoFileId;
	private String departmentId;
	private boolean registered = false;
	private PersonFunction personfunction;
	private String status;
	private long tenantId;
	private FileDescriptor image;
	private String shortDescription;


	//@NotNull
	private String job;
	private Integer yearsExperience;
	private String education;
	private String homeAddress;
	private String jobAddress;
	private String certifications;
	private List<Skill> skills;
	private String level;
	@DateTimeFormat(style = "M-")
    private Date lastCvUpdate;
	private String cvFileId;
	@DateTimeFormat(style = "M-")
    private Date startingDateDate;

	private  int workload;
	private int currentProjects;
	private int previousProjects;
}


