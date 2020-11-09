package com.advancedit.ppms.controllers.beans;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
public class MemberResource {
	private String personId;
	private String firstName;
	private String lastName;
	private String imageId;
	private String termId;
	private String termName;
	private Boolean signed;
}
