package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.ShortPerson;

@Document
public class Message {
	@Id
	private String messageId;

	private String content;
	private Date start;
	private ShortPerson writer;
	private Date modifiedTime;
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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
}
