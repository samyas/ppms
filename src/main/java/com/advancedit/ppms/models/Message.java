package com.advancedit.ppms.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class Message {
	
	private String content;
	private Date start;
	private ShortPerson writer;
	private List<Message> replies = new ArrayList<>();
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public ShortPerson getWriter() {
		return writer;
	}
	public void setWriter(ShortPerson writer) {
		this.writer = writer;
	}
	public List<Message> getReplies() {
		return replies;
	}
	public void setReplies(List<Message> replies) {
		this.replies = replies;
	}
	
	
	

}
