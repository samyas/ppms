package com.advancedit.ppms.models.project;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document
public class Task {
	@Id
	private String taskId;
	private String name;
	private String shortDescription;
	private String description;
	private TaskStatus status;
	@DateTimeFormat(style = "M-")
	private Date startDate;
	@DateTimeFormat(style = "M-")
	private Date endDate;
	private ShortPerson createdBy;
	private List<ShortPerson> assignedTo = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
	private List<FileDescriptor> attachmentList = new ArrayList<>();


}
