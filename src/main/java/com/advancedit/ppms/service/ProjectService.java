package com.advancedit.ppms.service;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Action;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.SupervisorTerm;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.advancedit.ppms.models.project.ProjectStatus.*;
import static com.advancedit.ppms.utils.SecurityUtils.isHasAnyRole;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class ProjectService {

	private final static SimpleDateFormat ISO_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

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

	public Project addProject(long tenantId, Project project, Department department, Person creator) {
		project.setProjectId(null);
		project.setTenantId(tenantId);
		project.setStatus(ProjectStatus.PROPOSAL);
		project.setCreator(new ShortPerson(creator.getId(), creator.getFirstName(), creator.getLastName(), creator.getPhotoFileId()));

		List<Goal> goals = department.getActions().stream().map(this::createGoalFromAction).collect(Collectors.toList());
		project.setGoals(goals);
		if (isHasAnyRole(Role.STUDENT)){
			Member member = createMemberFromPersonAndTerm(creator, null, true);
			project.getTeam().add(member);
		}
		return projectRepository.save(project);
	}

	private Member createMemberFromPersonAndTerm(Person person, SupervisorTerm term, boolean signed){
		Member member = new Member();
		member.setFirstName(person.getFirstName());
		member.setLastName(person.getLastName());
		member.setPersonId(person.getId());
		member.setImageId(person.getPhotoFileId());
		member.setSigned(signed);
		if (term != null){
			member.setTermId(term.getTermId());
			member.setTermName(term.getName());
		}
	    return member;
	}

	private Goal createGoalFromAction(Action action)  {
		try {
			Goal goal = new Goal();
			goal.setGoalId(new ObjectId().toHexString());
			goal.setName(action.getName());
			goal.setDescription(action.getDescription());
			goal.setActionId(action.getActionId());
			goal.setStatus(GoalStatus.NEW);
			goal.setIsAction(true);
			if (action.getStartDate() == null) {
				LocalDate localDate = LocalDate.now().plusWeeks(action.getWeekNbr());
				Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				goal.setStartDate(date);
			} else {
				goal.setStartDate(ISO_FORMATTER.parse(action.getStartDate()));
			}
			return goal;
		}catch (ParseException e){
			throw new PPMSException("Fail to parse action start date", e);
		}
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


	public String getModuleId(long tenantId, String projectId) {
		return projectRepository.getDepartmentId(tenantId, projectId);
	}

	public boolean assignSupervisor(long tenantId, String projectId, String emailUser,
			String personIdToAssign, SupervisorTerm term, boolean isModelLeader) {
		Project project = projectRepository.getProjectWithoutGoals(tenantId, projectId);
		Person person = getPerson(tenantId, personIdToAssign);
		if (!PersonFunction.isStaff(person.getPersonfunction())){
			throw new PPMSException(String.format("%s %s is not a staff member", person.getFirstName(), person.getLastName()));
		}

		if (!isModelLeader){
			if (!project.getStatus().canAssignUnassign()){
				throw new PPMSException(ErrorCode.PROJECT_ASSIGN_NOT_ALLOWED, "Project Assign not allowed");
			}
			if (term.getOrder() == 1 && !person.getEmail().equals(emailUser)){
				throw new PPMSException(String.format(" Only Model leader can perform this operation or Staff can assign him self for %s", term.getName()));
			}
		}

		Optional<Member> existingMember = project.getMembers().stream().filter(m -> m.getTermId().equals(term.getTermId())).findFirst();
		if (existingMember.isPresent()){
			throw new PPMSException(String.format("%s already assigned to the position %s", existingMember.get().getFirstName(), existingMember.get().getTermName()));
		}
		Optional<Member> alreadyAssigned = project.getMembers().stream().filter(m -> m.getPersonId().equals(person.getId())).findFirst();
		if (alreadyAssigned.isPresent()){
			throw new PPMSException(String.format("%s %s already assigned to this project", alreadyAssigned.get().getFirstName(), alreadyAssigned.get().getLastName()));
		}

		Member member = createMemberFromPersonAndTerm(person, term, false);
		return projectRepository.assignPerson(tenantId, projectId, "members", member);

	}
	public boolean assignStudent(long tenantId, String projectId,
									String personIdToAssign, int maxTeamNumber, boolean isModelLeader) {
		Project project = projectRepository.getProjectWithoutGoals(tenantId, projectId);
		Person person = getPerson(tenantId, personIdToAssign);
		if (!person.getPersonfunction().equals(PersonFunction.STUDENT)){
			throw new PPMSException(String.format("%s %s is not a student", person.getFirstName(), person.getLastName()));
		}

		if (!isModelLeader){
			if (!project.getStatus().canAssignUnassign()){
				throw new PPMSException(ErrorCode.PROJECT_ASSIGN_NOT_ALLOWED, "Project Assign not allowed");
			}
		}

		Optional<Member> alreadyAssigned = project.getTeam().stream().filter(m -> m.getPersonId().equals(person.getId())).findFirst();
		if (alreadyAssigned.isPresent()){
			throw new PPMSException(String.format("%s %s already assigned to this project", alreadyAssigned.get().getFirstName(), alreadyAssigned.get().getLastName()));
		}
		if (project.getTeam().size() == maxTeamNumber ){
			throw new PPMSException(String.format("Fail to assign Maximum Team members reached: %d", maxTeamNumber));
		}
		 Member member = createMemberFromPersonAndTerm(person, null, false);
		return projectRepository.assignPerson(tenantId, projectId, "team", member);

	}


	public boolean unAssign(long tenantId, String projectId, String personIdToUnassign, String position,  boolean isModelLeader) {
		ProjectStatus projectStatus = projectRepository.getProjectStatus(tenantId, projectId);
		if (!projectStatus.canAssignUnassign()){
			throw new PPMSException(ErrorCode.PROJECT_ASSIGN_NOT_ALLOWED, "Project Assign not allowed");
		}
		return projectRepository.unAssignPerson(tenantId, projectId, position, personIdToUnassign);
	}


	//Project status flow
	public boolean changeStatusToAssigned(long tenantId, String projectId, String email, Department department, boolean isModelLeader) {

		Project project = projectRepository.getProjectWithoutGoals(tenantId, projectId);
		if (!project.getStatus().equals(ProjectStatus.PROPOSAL)){
			throw new PPMSException(String.format("Project status different from %s", ProjectStatus.PROPOSAL.getLabel()));
		}
		if (project.getTeam().size() == 0){
			throw new PPMSException("No Student is assigned");
		}

		if (project.getMembers().size() == 0){
			throw new PPMSException("No Supervisor is assigned");
		}
		SupervisorTerm principalSupervisor = department.getSupervisorTerms().stream().filter(t -> t.getOrder() == 1).findFirst()
				.orElseThrow(() -> new PPMSException("Principal supervisor is not defined"));

		Member member = project.getMembers().stream().filter(m -> m.getTermId().equals(principalSupervisor.getTermId())).findFirst()
				.orElseThrow(() -> new PPMSException(String.format("%s is not assigned yet", principalSupervisor.getName())));

		if (!isModelLeader){
			Person person = personRepository.findByTenantIdAndEmail(tenantId, email);
			if (!person.getId().equals(member.getPersonId())){
				throw new PPMSException(String.format("Only Model Leader or %s can change project status to %s", principalSupervisor.getName(),
						ProjectStatus.ASSIGNED.getLabel()));
			}
		}
		return projectRepository.updateProjectStatus(tenantId, projectId, ProjectStatus.ASSIGNED);
	}

	public boolean sign(long tenantId, String projectId, String email, String position) {
		Person person = personRepository.findByTenantIdAndEmail(tenantId, email);
		return projectRepository.sign(tenantId, projectId, position,
				person.getId());
	}

	public boolean changeStatusToRegistered(long tenantId, String projectId, String email, Department department, boolean isModelLeader) {

		Project project = getProjectsById(tenantId, projectId);

		//CHeck status
		if (!project.getStatus().equals(ProjectStatus.ASSIGNED)){
			throw new PPMSException(String.format("Project status different from %s", ProjectStatus.ASSIGNED.getLabel()));
		}

        //Check pre-actions is completed
        List<String> actionsToBeValid = department.getActions().stream().filter(ac -> Boolean.TRUE.equals(ac.getBeforeStart())).map(Action::getActionId)
                .collect(Collectors.toList());
        if (!actionsToBeValid.isEmpty()){
            Optional<Goal> notCompletedAction = project.getGoals().stream()
                    .filter(g -> g.getActionId() != null && actionsToBeValid.contains(g.getActionId()))
                    .filter(g -> !g.getStatus().equals(GoalStatus.COMPLETED))
                    .findFirst();
            if (notCompletedAction.isPresent()){
                throw new PPMSException(String.format("Action '%s' not yet %s" , notCompletedAction.get().getName(), GoalStatus.COMPLETED.getLabel()));
            }
        }

		//Check signed
		Optional<Member> notSignedMember = project.getMembers().stream().filter(m -> m.getSigned() == null || Boolean.FALSE.equals(m.getSigned()))
				.findFirst();
		if (notSignedMember.isPresent()){
			throw new PPMSException(String.format("%s %s not confirmed yet", notSignedMember.get().getFirstName(), notSignedMember.get().getLastName()));
		}
		notSignedMember = project.getTeam().stream().filter(m -> m.getSigned() == null || Boolean.FALSE.equals(m.getSigned()))
				.findFirst();
		if (notSignedMember.isPresent()){
			throw new PPMSException(String.format("%s %s not confirmed yet", notSignedMember.get().getFirstName(), notSignedMember.get().getLastName()));
		}

		//Check only principal supervisor or supervisor can perform action
		SupervisorTerm principalSupervisor = department.getSupervisorTerms().stream().filter(t -> t.getOrder() == 1).findFirst()
				.orElseThrow(() -> new PPMSException("Principal supervisor is not defined"));

		Member member = project.getMembers().stream().filter(m -> m.getTermId().equals(principalSupervisor.getTermId())).findFirst()
				.orElseThrow(() -> new PPMSException(String.format("%s is not assigned yet", principalSupervisor.getName())));

		if (!isModelLeader){
			Person person = personRepository.findByTenantIdAndEmail(tenantId, email);
			if (!person.getId().equals(member.getPersonId())){
				throw new PPMSException(String.format("Only Model Leader or %s can change project status to %s", principalSupervisor.getName(),
						ProjectStatus.REGISTRATION.getLabel()));
			}
		}

		//Change status
		return projectRepository.updateProjectStatus(tenantId, projectId, ProjectStatus.REGISTRATION);
	}


	public boolean changeStatusToStarted(long tenantId, String projectId, Department department) {

		Project project = getProjectsById(tenantId, projectId);

		//CHeck status
		if (!project.getStatus().equals(ProjectStatus.REGISTRATION)){
			throw new PPMSException(String.format("Project status different from %s", ProjectStatus.REGISTRATION.getLabel()));
		}

		//Check only principal supervisor or supervisor can perform action
		Map<String, String> termsBeforeStart = department.getSupervisorTerms().stream()
				.filter(t -> Boolean.TRUE.equals(t.getMandatoryBeforeStart()))
				.collect(Collectors.toMap(SupervisorTerm::getTermId, SupervisorTerm::getName));

		if (!termsBeforeStart.isEmpty()){
			List<String> confirmedAssignedTerms = project.getMembers().stream().filter(t -> Boolean.TRUE.equals(t.getSigned()))
					.map(Member::getTermId).collect(Collectors.toList());
			Optional<String> notAssignedTerm = termsBeforeStart.keySet().stream().filter(k -> !confirmedAssignedTerms.contains(k)).findFirst();
			if (notAssignedTerm.isPresent()){
				throw new PPMSException(String.format("%s (mandatory) is either not assigned neither confirmed", termsBeforeStart.get(notAssignedTerm.get())));
			}
		}

		//Change status
		return projectRepository.updateProjectStatus(tenantId, projectId, ProjectStatus.START);
	}


	public boolean changeStatusToCompleted(long tenantId, String projectId, Department department) {

		Project project = getProjectsById(tenantId, projectId);

		//CHeck status
		if (!project.getStatus().equals(PROGRESS)){
			throw new PPMSException(String.format("Project status different from %s", PROGRESS.getLabel()));
		}

		//Check pre-actions is completed
		Optional<Goal> notCompletedAction = project.getGoals().stream()
					.filter(g -> g.getActionId() != null)
					.filter(g -> !g.getStatus().equals(GoalStatus.COMPLETED))
					.findFirst();
		if (notCompletedAction.isPresent()){
				throw new PPMSException(String.format("Action '%s' not yet %s" , notCompletedAction.get().getName(), GoalStatus.COMPLETED.getLabel()));
		}
		//Change status
		return projectRepository.updateProjectStatus(tenantId, projectId, COMPLETED);
	}

	public boolean changeStatusToReAllocatedOrWithdrawOrSuspendOrProgress(long tenantId, String projectId, ProjectStatus nextStatus) {

		if (!asList(REALLOCATED, SUSPEND, WITHDRAW, PROGRESS).contains(nextStatus)){
			throw new PPMSException("Operation not allowed");
		}

		//CHeck status
		ProjectStatus savedStatus = projectRepository.getProjectStatus(tenantId, projectId);
		if (asList(REALLOCATED, SUSPEND, WITHDRAW).contains(nextStatus)){
			if (!savedStatus.equals(PROGRESS)){
				throw new PPMSException(String.format("Project status different from %s", PROGRESS.getLabel()));
			}
		}else{
			//nextStatus: PROGRESS
			if (!asList(START, REALLOCATED, SUSPEND).contains(savedStatus)){
				throw new PPMSException(String.format("Project status different from %s %s and %s", START.getLabel(), SUSPEND.getLabel(),
						REALLOCATED.getLabel()));
			}
		}


		return projectRepository.updateProjectStatus(tenantId, projectId, nextStatus);
	}

	public void addAttachment(long tenantId, String projectId, FileDescriptor fileDescriptor) {
		projectRepository.addAttachment(tenantId, projectId, fileDescriptor);
	}

	public void deleteAttachment(long tenantId, String projectId, String key) {
		projectRepository.deleteAttachment(tenantId, projectId, key);
	}

	public boolean updateStatus(long tenantId, String projectId, ProjectStatus status) {
		return projectRepository.updateProjectStatus(tenantId, projectId, status);
	}

	private ShortPerson getShortPerson(long tenantId, String personId) {
		return personRepository.findByTenantIdAndPersonId(tenantId, personId)
				.map(p -> new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()))
				.orElseThrow(() -> new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
						String.format("Person id not found '%s'.", personId)));
	}

	private Person getPerson(long tenantId, String personId) {
		return personRepository.findByTenantIdAndPersonId(tenantId, personId)
				//.map(p -> new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()))
				.orElseThrow(() -> new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
						String.format("Person id not found '%s'.", personId)));
	}


	/****************************** GOALS*************************************************/
	public Goal getGoal(long tenantId, String projectId, String goalId) {
		return projectRepository.getGoal(tenantId, projectId, goalId)
				.orElseThrow(() -> new IllegalStateException(String.format("Goal [%s] not found", goalId)));
	}

	public String addGoal(long tenantId, String projectId, Goal goal,  String emailCreator) {
		Person person = personRepository.findByTenantIdAndEmail(tenantId, emailCreator);
		goal.setGoalId(new ObjectId().toHexString());
		goal.setStatus(GoalStatus.NEW);
		goal.setCreatedBy( new ShortPerson(person.getId(), person.getFirstName(),
				person.getLastName(), person.getPhotoFileId()));
		return projectRepository.addGoal(tenantId, projectId, goal).getGoalId();
	}

	public void updateGoalStatus(long tenantId, String projectId, String goalId, String email,
								   GoalStatus newGoalStatus, Task task, boolean isModelLeader) {
		Person person = personRepository.findByTenantIdAndEmail(tenantId, email);
		Project project = projectRepository.getProjectWithoutGoals(tenantId, projectId);

		List<String> teamIds = project.getTeam().stream().map(Member::getPersonId).collect(Collectors.toList());
		List<String> membersIds = project.getMembers().stream().map(Member::getPersonId).collect(Collectors.toList());
		if (!isModelLeader){
			if (!teamIds.contains(person.getId()) && !membersIds.contains(person.getId())){
				throw new PPMSException("Only Member of the project can perform this action");
			}
		}
		Goal goal = getGoal(tenantId, projectId, goalId);
		switch (newGoalStatus){

				case REVIEW:
					if (task != null){
						task.setCreatedBy( new ShortPerson(person.getId(), person.getFirstName(),
								person.getLastName(), person.getPhotoFileId()));
						task.setName(newGoalStatus.name());
						task.setStartDate(new Date());
						task.setStatus(TaskStatus.REVIEW);
					}
					goal.setStatus(newGoalStatus);
					break;
				case DECLINED:
					if (task != null){
						task.setCreatedBy( new ShortPerson(person.getId(), person.getFirstName(),
								person.getLastName(), person.getPhotoFileId()));
						task.setName(newGoalStatus.name());
						task.setStartDate(new Date());
						task.setStatus(TaskStatus.DECLINED);
					}
					goal.setStatus(newGoalStatus);
					break;
				case START:
					goal.setActualStartDate(new Date());
					goal.setStatus(newGoalStatus);
					break;
				case COMPLETED:
					if (task != null){
						task.setCreatedBy( new ShortPerson(person.getId(), person.getFirstName(),
								person.getLastName(), person.getPhotoFileId()));
						task.setName(newGoalStatus.name());
						task.setStartDate(new Date());
						task.setStatus(TaskStatus.COMPLETED);
					}
					goal.setEndDate(new Date());
					goal.setStatus(newGoalStatus);
					break;
				default:
					throw new PPMSException(String.format("Goal status '%s' not allowed to updated ", newGoalStatus.getLabel()));
		}

		projectRepository.updateGoal(tenantId, projectId, goalId, goal);
		if (task != null && !isBlank(task.getName())){
			task.setTaskId(new ObjectId().toHexString());
			projectRepository.addTask(tenantId, projectId, goalId, task);
		}

	}

	public String updateGoal(long tenantId, String projectId, String goalId, Goal goal) {
		projectRepository.updateGoal(tenantId, projectId, goalId, goal);
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
		task.setStatus(TaskStatus.NEW);
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

	public boolean assignTask(long tenantId, String projectId, String goalId, String taskId, Assignment assignment) {

			return projectRepository.assignPerson(tenantId, projectId, goalId, taskId,
					getShortPerson(tenantId, assignment.getPersonId()));

	}


	public boolean unAssignTask(long tenantId, String projectId, String goalId, String taskId, Assignment assignment) {

			return projectRepository.unAssignPerson(tenantId, projectId, goalId, taskId,
					assignment.getPersonId());

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
