package com.advancedit.ppms.models.organisation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "supervisorTerms")
public class SupervisorTerm {

	@Id
	private String termId;
	private String name;
	private String description;
	private int quota;
	private int order;
	private Boolean mandatoryBeforeStart;

}
