package com.advancedit.ppms.models.organisation;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.project.Task;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "actions")
public class Action {

	@Id
	private String actionId;
	
	private String name;
	private String description;
	private String startDate;
	private String endDate;
	private int weekNbr;
	private int dateNbr;
	private int order;
	private Boolean beforeStart;
	private List<Task> tasks = new ArrayList<>();
	private List<FileDescriptor> attachmentList = new ArrayList<>();


	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getWeekNbr() {
		return weekNbr;
	}

	public void setWeekNbr(int weekNbr) {
		this.weekNbr = weekNbr;
	}

	public int getDateNbr() {
		return dateNbr;
	}

	public void setDateNbr(int dateNbr) {
		this.dateNbr = dateNbr;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Boolean getBeforeStart() {
		return beforeStart;
	}

	public void setBeforeStart(Boolean beforeStart) {
		this.beforeStart = beforeStart;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<FileDescriptor> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<FileDescriptor> attachmentList) {
		this.attachmentList = attachmentList;
	}
}
