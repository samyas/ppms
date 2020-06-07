package com.advancedit.ppms.exceptions;

public enum ErrorCode {

	//GLOBAL ERROR
	UNKNOW_ERROR_OCCURED("global.unkhnown.error"),

	TOKEN_EXPIRED("token.expired"),
	INVALID_USERNAME_PASSWORD("unvalid.username.password"),

	ACTIVATION_TOKEN_EXPIRED("activation.code.unvalid"),
	
	//AUTH
	UNAUTHORIZED("unauthorized"),
	FORBIDDEN("forbidden"),


	ORGANISATION_SELECTION_REQUIRED("organisation.selection.required"),
	
	//USER MODULE
	USER_USERNAME_NOT_FOUND("user.username.not.found"),
	USER_ID_NOT_FOUND("user.id.not.found"),
	USER_EMAIL_ALREADY_EXIST("error.email.exist"),
	USER_USERNAME_ALREADY_EXIST("error.user.exist"),
	
	//PERSON MODULE
	PERSON_ID_NOT_FOUND("person.id.not.found"),
	PERSON_EMAIL_ALREADY_EXIST("error.email.emailExist"),
	PERSON_EMAIL_NOT_FOUND("error.email.emailNotFound"),

	
	//PROJECT MODULE
	PROJECT_ID_NOT_FOUND("project.id.not.found"),
	PROJECT_ALREADY_EXIST("project.name.nameExist"),

	PROJECT_ASSIGN_NOT_ALLOWED("project.assign.not.allowed"),

	TASK_ID_NOT_FOUND("task.id.not.found"),


	//COMPANY MODULE
	COMPANY_ID_NOT_FOUND("company.id.not.found"),
	COMPANY_EMAIL_ALREADY_EXIST("error.email.emailExist"),
	COMPANY_NAME_ALREADY_EXIST("error.name.companyNameExist"),
	
	
	
	//EMAIL MODULE
	EMAIL_ID_NOT_FOUND("email.id.not.found"),
	EMAIL_NAME_ALREADY_EXIST("error.email.exist"),
	
	EMAILS_NOT_SENT_SUCCESSFULLY("email.not.sent.successfully"),
	
	
	//ORGANISATION MODULE
	ORGANISATION_ID_NOT_FOUND("organisation.id.not.found"),
	;
	private String code;

	ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
