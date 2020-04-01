package com.advancedit.ppms.service;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.controllers.beans.Assignment.Action;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ProjectService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private FileStorageRepository fileStorageRepository;

	/***************************** PROJECTS***********************************/

	public List<Project> getAllProjects(long tenantId) {
		return projectRepository.findAll();
	}

	public Page<Project> getPagedListProject(long tenantId, int page, int size, String departmentId, String status, String name) {
		Pageable pageableRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));;
		Page<Project> projects = null;
	//	if (StringUtils.isEmpty(name) && StringUtils.isEmpty(status)) {
			projects = projectRepository.findByAll(tenantId, departmentId, pageableRequest);
	//	} else {
	//		projects = projectRepository.findByAllCriteria(status, name, pageableRequest);
	//	}

		return projects;
	}
	
	public Page<ProjectSummary> getPagedProjectSummary(long tenantId, int page, int size, String status, String name) {
		Pageable pageableRequest =  PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		return projectRepository.getPagedProjectSummary(tenantId, page, size, pageableRequest);
	}

	public Project getProjectsById(long tenantId, String id) {
		return projectRepository.findById(id)
				.filter(project -> project.getTenantId() == tenantId)
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
				String.format("Project id not found '%s'.", id)));

	}

	public Project addProject(long tenantId, Project project) {
		project.setProjectId(null);
		project.setTenantId(tenantId);
		project.setStatus(ProjectStatus.NEW.name());
		return projectRepository.save(project);
	}

	private void checkIfProjectExist(long tenantId, String id){
		if (!projectRepository.existByProjectIdAndTenantId(id, tenantId))
			throw new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
					String.format("Project id not found '%s'.", id));
	}

	public String updateProject(long tenantId, String id, Project project) {
		checkIfProjectExist(tenantId, id);
		return projectRepository.updateProjectNameAndDescriptionAndKeywords(tenantId, id, project);

	}

	public void delete(long tenantId, String id) {
		checkIfProjectExist(tenantId, id);
		projectRepository.deleteById(id);
	}
	
	public void deleteAll() {
		projectRepository.deleteAll();
	}

	public void assign(long tenantId, String projectId, Assignment assignment) {
		if (assignment.getAction().equals(Action.ADD)){
			projectRepository.assignPerson(tenantId, projectId, assignment.getPosition().getAttribute(),
					getShortPerson(tenantId, assignment.getPersonId()));
		} else if (assignment.getAction().equals(Action.REMOVE)){
			projectRepository.unAssignPerson(tenantId, projectId, assignment.getPosition().getAttribute(),
					assignment.getPersonId());
		}
	}

	public void addAttachment(long tenantId, String projectId, FileDescriptor fileDescriptor) {
		projectRepository.addAttachment(tenantId, projectId, fileDescriptor);
	}

	public void deleteAttachment(long tenantId, String projectId, String key) {
		projectRepository.deleteAttachment(tenantId, projectId, key);
	}

	public void updateStatus(long tenantId, String projectId, String status) {
		projectRepository.updateProjectStatus(tenantId, projectId, status);
	}

	private ShortPerson getShortPerson(long tenantId, String personId) {
		return personRepository.findByTenantIdAndPersonId(tenantId, personId)
				.map(p -> new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()))
				.orElseThrow(() -> new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
						String.format("Person id not found '%s'.", personId)));
	}

    /****************************** GOALS*************************************************/
	public Goal getGoal(long tenantId, String projectId, String goalId) {
		return projectRepository.getGoal(tenantId, projectId, goalId)
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));
	}

	public String addGoal(long tenantId, String projectId, Goal goal) {
		goal.setGoalId(new ObjectId().toHexString());
		return projectRepository.addGoal(tenantId, projectId, goal).getGoalId();
	}

	public String updateGoal(long tenantId, String projectId, String goalId, Goal goal) {
		projectRepository.updateGoalNameAndDescription(tenantId, projectId, goalId, goal);
		return goalId;
	}

	public void deleteGoal(long tenantId, String projectId, String goalId) {
		projectRepository.deleteGoal(tenantId, projectId, goalId);
	}



	/****************************** TASKS*************************************************/

	public Task getTask(long tenantId, String projectId, String goalId, String taskId) {
		return projectRepository.getTask(tenantId, projectId, goalId, taskId)
				.orElseThrow(() -> new IllegalStateException(String.format("Task [%s] not found", taskId)));
	}

	public String addNewTask(long tenantId, String projectId, String goalId, Task task) {
		task.setTaskId(new ObjectId().toHexString());
		return projectRepository.addTask(tenantId, projectId, goalId, task).getTaskId();
	}

	public void updateTaskStatus(long tenantId, String projectId, String goalId, String taskId, String status) {
		projectRepository.updateTaskStatus(tenantId, projectId, goalId, taskId, status);
	}

	public String updateTask(long tenantId, String projectId, String goalId, String taskId, Task task) {
		return projectRepository.updateTaskNameAndDescription(tenantId, projectId, goalId, taskId, task);
	}

	public void deleteTask(long tenantId, String projectId, String goalId, String taskId) {
		projectRepository.deleteTask(tenantId, projectId, goalId, taskId);
	}

	public void addAttachment(long tenantId, String projectId, String goalId, String taskId, FileDescriptor fileDescriptor) {
		projectRepository.addAttachment(tenantId, projectId, goalId, taskId, fileDescriptor);
	}

	public void deleteAttachment(long tenantId, String projectId, String goalId, String taskId, String url) {
		projectRepository.deleteAttachment(tenantId, projectId, goalId, taskId, url);
	}

	public String addMessage(long tenantId, String projectId, String goalId,
							 String taskId, Message message) {
		message.setMessageId(new ObjectId().toHexString());
		message.setStart(new Date());
		return projectRepository.addMessage(tenantId, projectId, goalId, taskId, message);
	}

	public String updateMessage(long tenantId, String projectId, String goalId,
								String taskId, String messageId, Message message) {
		return projectRepository.updateMessage(tenantId, projectId, goalId, taskId, messageId, message);
	}

	public void assignTask(long tenantId, String projectId, String goalId, String taskId, Assignment assignment) {
		if (assignment.getAction().equals(Action.ADD)){
			projectRepository.assignPerson(tenantId, projectId, goalId, taskId,
					getShortPerson(tenantId, assignment.getPersonId()));
		} else if (assignment.getAction().equals(Action.REMOVE)){
			projectRepository.unAssignPerson(tenantId, projectId, goalId, taskId,
					assignment.getPersonId());
		}
	}





	public void apply(long tenantId, String projectId, Apply apply) {
		Project p = getProjectsById(tenantId, projectId);
		boolean alreadyApplied = p.getApplies().stream().anyMatch(app -> app.getPersonId().equals(apply.getPersonId()));
		if (alreadyApplied){
			throw new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
					String.format("Person id '%s' already applied .", apply.getPersonId()));
		}
		
		p.getApplies().add(apply);
		projectRepository.save(p);
		
	}


}
