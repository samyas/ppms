package com.advancedit.ppms.exceptions;

public enum ErrorCode {

	//GLOBAL ERROR
	UNKNOW_ERROR_OCCURED("global.unkhnown.error"),
	
	//AUTH
	UNAUTHORIZED("unauthorized"),
	FORBIDDEN("forbidden"),
	
	//USER MODULE
	USER_USERNAME_NOT_FOUND("user.username.not.found"),
	USER_ID_NOT_FOUND("user.id.not.found"),
	USER_EMAIL_ALREADY_EXIST("error.email.emailExist"),
	USER_USERNAME_ALREADY_EXIST("error.username.userExist"),
	
	//PERSON MODULE
	PERSON_ID_NOT_FOUND("person.id.not.found"),
	PERSON_EMAIL_ALREADY_EXIST("error.email.emailExist"),
	PERSON_EMAIL_NOT_FOUND("error.email.emailNotFound"),

	
	//COMPANY MODULE
	COMPANY_ID_NOT_FOUND("company.id.not.found"),
	COMPANY_EMAIL_ALREADY_EXIST("error.email.emailExist"),
	COMPANY_NAME_ALREADY_EXIST("error.name.companyNameExist"),
	
	
	
	//EMAIL MODULE
	EMAIL_ID_NOT_FOUND("email.id.not.found"),
	EMAIL_NAME_ALREADY_EXIST("error.name.emailNameExist"),
	
	EMAILS_NOT_SENT_SUCCESSFULLY("email.not.sent.successfully"),
	;
	private String code;

	ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
