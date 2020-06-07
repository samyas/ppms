package com.advancedit.ppms.models.project;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
public class Member {
	private String personId;
	private String firstName;
	private String lastName;
	private String imageId;
	private String termId;
	private String termName;
	private Boolean signed;
}
