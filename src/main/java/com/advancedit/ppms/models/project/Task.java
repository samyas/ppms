package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.ShortPerson;

@Document
public class Task {


	@Id
	private String taskId;
	
	private String name;
	private String shortDescription;
	private String description;
	private String status;
	private String startDate;
	private String endDate;
	private ShortPerson createdBy;
	private List<ShortPerson> assignedTo = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
	 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getDescription() {
		return description;
	}
	
	public ShortPerson getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(ShortPerson createdBy) {
		this.createdBy = createdBy;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public List<ShortPerson> getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(List<ShortPerson> assignedTo) {
		this.assignedTo = assignedTo;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	

}
