package com.advancedit.ppms.models.organisation;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.ShortPerson;

@Data
@NoArgsConstructor
@Document(collection = "departements")
public class Department {

	@Id
	private String departmentId;
	private String name;
	private String code;
	private String description;
	private String longDescription;
	private String email;
	private String type;
	private String subType;
	private Address address;
	private String phone;
	private String contactEmail;
	private int maxTeamNbr;
	private Boolean studentCannotCreateProject;
	private ShortPerson responsible;
	private List<SupervisorTerm> supervisorTerms = new ArrayList<>();
	private List<Action> actions = new ArrayList<>();
	private List<Sector> sectors = new ArrayList<>();

}
