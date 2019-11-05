package com.advancedit.ppms.external.email;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EmailBean {
	
	private String from;

	private List<String> to;

	private List<String> cc;

	private String subject;

	private String message;
	
	private boolean isHtml;
	
	private byte[] attachementBinary;
	
	private String attachementFileName;
	
	private String attachementFileType;

	public EmailBean() {
		this.to = new ArrayList<String>();
		this.cc = new ArrayList<String>();
	}

	public EmailBean(String from, List<String> toList, String subject, String message) {
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.to = toList;
	}

	public EmailBean(String from, List<String> toList, List<String> ccList, String subject, String message) {
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.to = toList;
		this.cc = ccList;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}

	public byte[] getAttachementBinary() {
		return attachementBinary;
	}

	public void setAttachementBinary(byte[] attachementBinary) {
		this.attachementBinary = attachementBinary;
	}

	public String getAttachementFileName() {
		return attachementFileName;
	}

	public void setAttachementFileName(String attachementFileName) {
		this.attachementFileName = attachementFileName;
	}

	public String getAttachementFileType() {
		return attachementFileType;
	}

	public void setAttachementFileType(String attachementFileType) {
		this.attachementFileType = attachementFileType;
	}

	
}