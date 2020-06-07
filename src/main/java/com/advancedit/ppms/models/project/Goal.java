package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@Document
public class Goal {


	@Id
	private String goalId;
	
	private String name;
	private String shortDescription;
	private String description;
	private GoalStatus status;
	@DateTimeFormat(style = "M-")
	private Date startDate;
	@DateTimeFormat(style = "M-")
	private Date endDate;

	@DateTimeFormat(style = "M-")
	private Date actualStartDate;
	@DateTimeFormat(style = "M-")
	private Date actualEndDate;

	private Boolean isAction;
	private ShortPerson createdBy;
	private int score;
	private String actionId;
	private List<Task> tasks = new ArrayList<>();
	private List<FileDescriptor> attachmentsArrayList = new ArrayList<>();
}
