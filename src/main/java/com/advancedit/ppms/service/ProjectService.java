package com.advancedit.ppms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.project.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.controllers.beans.Assignment.Action;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private FileStorageRepository fileStorageRepository;

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

	public Project updateProject(long tenantId, String id, Project project) {
		checkIfProjectExist(tenantId, id);
		project.setTenantId(tenantId);
		project.setProjectId(id);
		return projectRepository.save(project);

	}

	public void delete(long tenantId, String id) {
		checkIfProjectExist(tenantId, id);
		projectRepository.deleteById(id);
	}
	
	public void deleteAll() {
		projectRepository.deleteAll();
	}

	public void assign(long tenantId, String projectId, Assignment assignment) {
		Project project = projectRepository.findById(projectId)
				.filter(project1 -> project1.getTenantId() == tenantId)
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
						String.format("Project id not found '%s'.", projectId)));
		switch (assignment.getPosition()) {
		case EXAMINATOR:
			project.setExaminator(
					getShortPersonIfAdd(assignment.getAction(), () -> getShortPerson(assignment.getPersonId())));
			break;
		case SUPERVISOR:
			project.setSupervisor(
					getShortPersonIfAdd(assignment.getAction(), () -> getShortPerson(assignment.getPersonId())));
			break;
		case TEAM:
			List<ShortPerson> team = project.getTeam() != null ? project.getTeam() : new ArrayList<>();
			Optional<ShortPerson> assignedPerson = team.stream()
					.filter(sp -> sp.getPersonId().equals(assignment.getPersonId())).findFirst();
			if (assignment.getAction().equals(Action.ADD)) {
				if (!assignedPerson.isPresent())
					team.add(getShortPerson(assignment.getPersonId()));
			} else {
				if (assignedPerson.isPresent())
					team.removeIf(sp -> sp.getPersonId().equals(assignment.getPersonId()));
			}
			project.setTeam(team);
			break;
		default:
			break;

		}
		projectRepository.save(project);
	}

	private ShortPerson getShortPerson(String personId) {
		return personRepository.findById(personId)
				.map(p -> new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()))
				.orElseThrow(() -> new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
						String.format("Person id not found '%s'.", personId)));

	}

	private ShortPerson getShortPersonIfAdd(Action action, Supplier<ShortPerson> getPerson) {
		return (!action.equals(Action.ADD)) ? null : getPerson.get();
	}

	public Goal getGoal(long tenantId, String projectId, String goalId) {
		return projectRepository.getGoal(tenantId, projectId, goalId)
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));
	}

	public String addGoal(long tenantId, String projectId, Goal goal) {
		goal.setGoalId(new ObjectId().toHexString());
		return projectRepository.addGoal(tenantId, projectId, goal).getGoalId();
	}

	public String updateGoal(long tenantId, String projectId, String goalId, Goal goal) {
	    goal.setGoalId(goalId);
		projectRepository.updateGoal(tenantId, projectId, goal);
		return goalId;
	}

	public Task getTask(long tenantId, String projectId, String goalId, String taskId) {
		return projectRepository.getTask(tenantId, projectId, goalId, taskId)
				.orElseThrow(() -> new IllegalStateException(String.format("Task [%s] not found", taskId)));

		/*Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
						String.format("Project id not found '%s'.", projectId)));
		Goal goal = project.getGoals().stream().filter(g -> g.getGoalId().equals(goalId)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));

		return goal.getTasks().stream().filter(t -> t.getTaskId().equals(taskId)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Task [%s] not found", taskId)));*/
	}

	public String addNewTask(long tenantId, String projectId, String goalId, Task task) {
		task.setTaskId(new ObjectId().toHexString());
		return projectRepository.addTask(tenantId, projectId, goalId, task).getTaskId();
	}

	public void updateTaskStatus(long tenantId, String projectId, String goalId, String taskId, String status) {
		projectRepository.updateTaskStatus(tenantId, projectId, goalId, taskId, status);
	}

	public void addAttachment(long tenantId, String projectId, FileDescriptor fileDescriptor) {
		projectRepository.addAttachment(tenantId, projectId, fileDescriptor);
	}

	public void deleteAttachment(long tenantId, String projectId, String url) {
		projectRepository.deleteAttachment(tenantId, projectId, url);
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
		Task task = projectRepository.getTask(tenantId, projectId, goalId, taskId)
				.orElseThrow(() -> new PPMSException(ErrorCode.TASK_ID_NOT_FOUND,
						String.format("Task id not found '%s'.", taskId)));
		switch (assignment.getPosition()) {
			case EXAMINATOR:
			case SUPERVISOR:
			case TEAM:
				List<ShortPerson> team = task.getAssignedTo() != null ? task.getAssignedTo() : new ArrayList<>();
				Optional<ShortPerson> assignedPerson = team.stream()
						.filter(sp -> sp.getPersonId().equals(assignment.getPersonId())).findFirst();
				if (assignment.getAction().equals(Action.ADD)) {
					if (!assignedPerson.isPresent())
						team.add(getShortPerson(assignment.getPersonId()));
				} else {
					if (assignedPerson.isPresent())
						team.removeIf(sp -> sp.getPersonId().equals(assignment.getPersonId()));
				}
			//    ShortPerson shortPerson = new ShortPerson("1", "first", "last", "789456123");
				//team.add(shortPerson);
				projectRepository.assignTask(tenantId, projectId, goalId, taskId, team);
				break;
			default:
				break;

		}
	}

	public void updateStatus(long tenantId, String projectId, String status) {
		projectRepository.updateProjectStatus(tenantId, projectId, status);
	}

	public String updateTask(long tenantId, String projectId, String goalId, String taskId, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteGoal(long tenantId, String projectId, String goalId) {
		// TODO Auto-generated method stub

	}

	public void deleteTask(long tenantId, String projectId, String goalId, String taskId) {
		// TODO Auto-generated method stub

	}

	public void deleteProject(long tenantId, String id) {
		projectRepository.deleteById(id);

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
