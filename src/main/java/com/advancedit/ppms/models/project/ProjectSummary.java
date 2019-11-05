package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.ShortPerson;

@Document
public class ProjectSummary {


	@Id
	private String projectId;
	
	private String name;
	private String shortDescription;
	private String description;
	private ProjectStatus status;
	
	private String category;
	private List<String> sectors = new ArrayList<>();
	
	private String startDate;
	private String endDate;
	
	
	private String managerPersonId;
	
	private List<ShortPerson> team = new ArrayList<>();
	
	private ShortPerson supervisor;
    private ShortPerson examinator;
    private ShortPerson creator;
    private List<ShortPerson> assignedTo = new ArrayList<>();;
	
	private String budget;
	
	private String logoId;
	
	private int nbrGoals;
	
	private int nbrTasks;
	
	private List<Goal> goals = new ArrayList<>();
	
	
	
	
	public List<Goal> getGoals() {
		return goals;
	}


	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}


	public int getNbrTasks() {
		return nbrTasks;
	}


	public void setNbrTasks(int nbrTasks) {
		this.nbrTasks = nbrTasks;
	}


	public int getNbrGoals() {
		return nbrGoals;
	}


	public void setNbrGoals(int nbrGoals) {
		this.nbrGoals = nbrGoals;
	}


	private List<String> technologies = new ArrayList<>();;
	
	
	private List<String> organisationsId = new ArrayList<>();


	public String getProjectId() {
		return projectId;
	}


	public void setProjectId(String projectId) {
		this.projectId = projectId;
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


	public ProjectStatus getStatus() {
		return status;
	}


	public void setStatus(ProjectStatus status) {
		this.status = status;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public List<String> getSectors() {
		return sectors;
	}


	public void setSectors(List<String> sectors) {
		this.sectors = sectors;
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


	public String getManagerPersonId() {
		return managerPersonId;
	}


	public void setManagerPersonId(String managerPersonId) {
		this.managerPersonId = managerPersonId;
	}


	public List<ShortPerson> getTeam() {
		return team;
	}


	public void setTeam(List<ShortPerson> team) {
		this.team = team;
	}


	public String getBudget() {
		return budget;
	}


	public void setBudget(String budget) {
		this.budget = budget;
	}


	public String getLogoId() {
		return logoId;
	}


	public void setLogoId(String logoId) {
		this.logoId = logoId;
	}


	public List<String> getTechnologies() {
		return technologies;
	}


	public void setTechnologies(List<String> technologies) {
		this.technologies = technologies;
	}


	public List<String> getOrganisationsId() {
		return organisationsId;
	}


	public void setOrganisationsId(List<String> organisationsId) {
		this.organisationsId = organisationsId;
	}


	public ShortPerson getSupervisor() {
		return supervisor;
	}


	public void setSupervisor(ShortPerson supervisor) {
		this.supervisor = supervisor;
	}


	public ShortPerson getExaminator() {
		return examinator;
	}


	public void setExaminator(ShortPerson examinator) {
		this.examinator = examinator;
	}


	public ShortPerson getCreator() {
		return creator;
	}


	public void setCreator(ShortPerson creator) {
		this.creator = creator;
	}


	public List<ShortPerson> getAssignedTo() {
		return assignedTo;
	}


	public void setAssignedTo(List<ShortPerson> assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	

}
