package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.List;

import com.advancedit.ppms.models.files.FileDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Goal {


	@Id
	private String goalId;
	
	private String name;
	private String shortDescription;
	private String description;
	private GoalStatus status;
	private String startDate;
	private String endDate;
	private List<Task> tasks = new ArrayList<>();
	private List<FileDescriptor> attachmentsArrayList = new ArrayList<>();
	
	
	public String getGoalId() {
		return goalId;
	}
	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}
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
	public void setDescription(String description) {
		this.description = description;
	}
	public GoalStatus getStatus() {
		return status;
	}
	public void setStatus(GoalStatus status) {
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
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public List<FileDescriptor> getAttachmentsArrayList() { return attachmentsArrayList; }
	public void setAttachmentsArrayList(List<FileDescriptor> attachmentsArrayList) { this.attachmentsArrayList = attachmentsArrayList; }
}
