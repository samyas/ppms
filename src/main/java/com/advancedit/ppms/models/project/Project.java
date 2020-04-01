package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.models.person.ShortPerson;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@Document
public class Project {
	@Id
	private String projectId;
	private String name;
	private String shortDescription;
	private String description;
	private String type;
	private String status;
	private String category;
	private List<String> sectors = new ArrayList<>();
	private List<String> keywords = new ArrayList<>();
	@DateTimeFormat(style = "M-")
	private Date startDate;
	@DateTimeFormat(style = "M-")
	private Date endDate;
	private List<Apply> applies = new ArrayList<>();
	private String managerPersonId;
	private List<ShortPerson> team = new ArrayList<>();
	private ShortPerson supervisor;
    private ShortPerson examinator;
    private List<ShortPerson> supervisors = new ArrayList<>();
    private List<ShortPerson> examinators = new ArrayList<>();
    private ShortPerson creator;
    private List<ShortPerson> assignedTo = new ArrayList<>();
	private String departmentId;
	private String budget;
	private String logoId;
	private List<Goal> goals = new ArrayList<>();
	private List<String> technologies = new ArrayList<>();;
	private List<String> organisationsId = new ArrayList<>();
	private List<FileDescriptor> attachments = new ArrayList<>();
	private long tenantId;
}
