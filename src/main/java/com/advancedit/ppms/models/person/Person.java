package com.advancedit.ppms.models.person;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

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
	private boolean valid = false;
	private PersonFunction personfunction;
	private String status;
	private long tenantId;
	
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
}


