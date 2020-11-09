package com.advancedit.ppms.models.project;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Member {
	private String personId;
	private String termId;
	private Boolean signed;

	//TODO to delete
	private String termName;
	private String firstName;
	private String lastName;
	private String imageId;
}
