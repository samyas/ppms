package com.advancedit.ppms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;
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

	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}

	public Page<Project> getPagedListProject(int page, int size, String status, String name) {
		Pageable pageableRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "name"));
		Page<Project> projects = null;
	//	if (StringUtils.isEmpty(name) && StringUtils.isEmpty(status)) {
			projects = projectRepository.findByAll(pageableRequest);
	//	} else {
	//		projects = projectRepository.findByAllCriteria(status, name, pageableRequest);
	//	}

		return projects;
	}
	
	public Page<ProjectSummary> getPagedProjectSummary(int page, int size, String status, String name) {
		Pageable pageableRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "name"));
		Page<ProjectSummary> projects = null;
	//	if (StringUtils.isEmpty(name) && StringUtils.isEmpty(status)) {
			projects = projectRepository.getPagedProjectSummary(page, size, pageableRequest);
	//	} else {
	//		projects = projectRepository.findByAllCriteria(status, name, pageableRequest);
	//	}

		return projects;
	}

	public Project getProjectsById(String id) {

		return projectRepository.findById(id).orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
				String.format("Project id not found '%s'.", id)));

	}

	public Project addProject(Project project) {
		project.setProjectId(null);
		return projectRepository.save(project);
	}

	public Project updateProject(String id, Project project) {
		projectRepository.findById(project.getProjectId())
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
						String.format("Project id not found '%s'.", project.getProjectId())));
		return projectRepository.save(project);

	}

	public void delete(String id) {
		Project savedPerson = projectRepository.findById(id)
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
						String.format("Project id not found '%s'.", id)));
		if (savedPerson != null) {
			/*
			 * if (savedPerson.getPhotoFileId() != null){
			 * fileStorageRepository.delete(savedPerson.getPhotoFileId()); }
			 * 
			 * if (savedPerson.getCvFileId() != null){
			 * fileStorageRepository.delete(savedPerson.getCvFileId()); }
			 */

			projectRepository.deleteById(id);
		}

	}
	
	public void deleteAll() {
		projectRepository.deleteAll();
	}

	public void assign(String projectId, Assignment assignment) {
		Project project = projectRepository.findById(projectId)
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

	public Goal getGoal(String projectId, String goalId) {
		return projectRepository.getGoal(projectId, goalId)
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));
	}

	public String addGoal(String projectId, Goal goal) {
		goal.setGoalId(new ObjectId().toHexString());
		return projectRepository.addGoal(projectId, goal).getGoalId();
	}

	public String updateGoal(String projectId, String goalId, Goal goal) {
		// TODO Auto-generated method stub
		return null;
	}

	public Task getTask(String projectId, String goalId, String taskId) {
		return projectRepository.getTask(projectId, goalId, taskId)
				.orElseThrow(() -> new IllegalStateException(String.format("Task [%s] not found", taskId)));

		/*Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND,
						String.format("Project id not found '%s'.", projectId)));
		Goal goal = project.getGoals().stream().filter(g -> g.getGoalId().equals(goalId)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));

		return goal.getTasks().stream().filter(t -> t.getTaskId().equals(taskId)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Task [%s] not found", taskId)));*/
	}

	public String addNewTask(String projectId, String goalId, Task task) {
		task.setTaskId(new ObjectId().toHexString());
		return projectRepository.addTask(projectId, goalId, task).getTaskId();
	}

	public String updateTask(String projectId, String goalId, String taskId, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteGoal(String projectId, String goalId) {
		// TODO Auto-generated method stub

	}

	public void deleteTask(String projectId, String goalId, String taskId) {
		// TODO Auto-generated method stub

	}

	public void deleteProject(String id) {
		projectRepository.deleteById(id);

	}

	public void apply(String projectId, Apply apply) {
		Project p = getProjectsById(projectId);
		boolean alreadyApplied = p.getApplies().stream().anyMatch(app -> app.getPersonId().equals(apply.getPersonId()));
		if (alreadyApplied){
			throw new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
					String.format("Person id '%s' already applied .", apply.getPersonId()));
		}
		
		p.getApplies().add(apply);
		projectRepository.save(p);
		
	}
}
