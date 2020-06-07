package com.advancedit.ppms.models.project;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ActivityStream {

	private ActivityType activityType;
	private String elementId;
	private String action;
	private ShortPerson actor;
	private String actionDate;
}
