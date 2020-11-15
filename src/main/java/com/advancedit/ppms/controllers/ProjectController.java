package com.advancedit.ppms.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.advancedit.ppms.controllers.beans.ProjectFilter;
import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.SupervisorTerm;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.service.*;
import com.advancedit.ppms.service.beans.AttachType;
import com.advancedit.ppms.utils.LoggedUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.advancedit.ppms.models.project.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import org.springframework.web.multipart.MultipartFile;

import static com.advancedit.ppms.controllers.presenter.ProjectPresenter.toResource;
import static com.advancedit.ppms.utils.SecurityUtils.*;
import static java.util.Arrays.asList;

@RestController
public class ProjectController {

	@Autowired
    ProjectService projectService;

    @Autowired
    PersonService personService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    DocumentManagementService documentManagementService;

    @Autowired
    AttachFileService attachService;

    @RequestMapping(method=RequestMethod.GET, value="/api/projects")
    public List<Project> all() {
        return  projectService.getAllProjects(getCurrentTenantId());
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/paged")
    public Page<ProjectResource> getPagedProjects(@RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam(name = "status", required=false)List<ProjectStatus> status,
                                                  @RequestParam(name = "keyword", required=false)List<String> keywords,
                                                  @RequestParam(name = "name", required=false)String name,
                                                  @RequestParam(name = "departmentId", required=false)String departmentIdInput,
                                                  @RequestParam(name = "assignedToMe", required=false, defaultValue = "false")Boolean assignedToMe) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
               .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));

        String departmentId = Optional.ofNullable(person.getDepartmentId()).orElse(departmentIdInput);
        ProjectFilter projectFilter = ProjectFilter.builder().departmentId(departmentId).isStudent(isHasRole(Role.STUDENT))
                .isModuleLeaderOrAdmin(isHasAnyRole(Role.MODULE_LEADER, Role.ADMIN_CREATOR)).personId(person.getId()).statuses(status)
        .onlyAssignedToPersonId(assignedToMe).keywords(keywords).name(name).build();
        Page<Project> pagedListProject = projectService.getPagedListProject(getCurrentTenantId(), page, size, projectFilter);

       List<String> allPersonIds =  pagedListProject.stream().map(this::projectRelatedPersons).flatMap(Collection::stream).collect(Collectors.toList());
        List<Person> personList = personService.getAllPersonsByIds(loggedUserInfo.getTenantId(), allPersonIds);
        List<ProjectResource> collect = pagedListProject.stream().map(p -> toResource(p, organisation, null, false, personList)).collect(Collectors.toList());
        return new PageImpl<>(collect, pagedListProject.getPageable(), pagedListProject.getTotalElements());
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/projects/with-goals/paged")
    public Page<ProjectResource> getPagedProjectsWithGoal(@RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam(name = "status", required=false)ProjectStatus status,
                                                  @RequestParam(name = "name", required=false)String name) {

        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        Page<Project> pagedListProject = projectService.getPagedListProjectWithGoal(getCurrentTenantId(), page, size, person.getDepartmentId(), status, name);
        List<String> allPersonIds =  pagedListProject.stream().map(this::projectRelatedPersons).flatMap(Collection::stream).collect(Collectors.toList());
        List<Person> personList = personService.getAllPersonsByIds(loggedUserInfo.getTenantId(), allPersonIds);
        List<ProjectResource> collect = pagedListProject.stream().map(p -> toResource(p, organisation, null, true, personList)).collect(Collectors.toList());
        return new PageImpl<>(collect, pagedListProject.getPageable(), pagedListProject.getTotalElements());
    }

    private List<String> projectRelatedPersons(Project project){
        List<String> projectPersonIds = new ArrayList<>();
        Optional.ofNullable(project.getCreator()).map(ShortPerson::getPersonId).ifPresent(projectPersonIds::add);
        project.getTeam().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        project.getMembers().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        return projectPersonIds;
    }

    //Assign Process
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/apply-with-upload", consumes = {"multipart/form-data"})
    public void apply(@PathVariable String projectId,
                                  @RequestPart("apply")  Apply apply,
                                  @RequestPart("files") List<MultipartFile> files){

      //  hasAnyRole(Role.STUDENT);
        long tenantId = getCurrentTenantId();
        String projectModuleId = projectService.getModuleId(tenantId, projectId);
        isSameModule(projectModuleId);
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
    	 projectService.apply(tenantId, projectId, apply, loggedUserInfo.getEmail(), files);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/assign-supervisor")
    public void assignSupervisor(@PathVariable String projectId, @RequestBody Assignment assignment) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER, Role.STAFF, Role.STUDENT);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);
        SupervisorTerm term = organisationService.getTerm(getCurrentTenantId(), projectModuleId, assignment.getTermId())
                .orElseThrow(() -> new PPMSException("Supervisor Termanology not found"));
        projectService.assignSupervisor(getCurrentTenantId(), projectId, getLoggedUserInfo().getEmail(),
                assignment.getPersonId(), term, isHasRole(Role.MODULE_LEADER));
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/assign-students")
    public void assignStudent(@PathVariable String projectId, @RequestBody Assignment assignment) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER, Role.STAFF);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF)) isSameModule( projectModuleId);
        Department department = organisationService.getDepartment(getCurrentTenantId() , projectModuleId);
        projectService.assignStudent(getCurrentTenantId(), projectId, assignment.getPersonId(),
                department.getMaxTeamNbr(), getLoggedUserInfo().getEmail(), isHasRole(Role.MODULE_LEADER));
    }


    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/unassign")
    public void unassign(@PathVariable String projectId, @RequestParam("position") String position,
                         @RequestParam("personId") String personId,  @RequestParam(value = "termId",required = false) String termId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER, Role.STAFF);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF)) isSameModule(projectModuleId);
        SupervisorTerm term = Optional.ofNullable(termId).flatMap( t -> organisationService.getTerm(getCurrentTenantId(), projectModuleId, t))
                .orElse(null);
        projectService.unAssign(getCurrentTenantId(), projectId, personId, position,  term,
                getLoggedUserInfo().getEmail(), isHasRole(Role.MODULE_LEADER));
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/sign")
    public void sign(@PathVariable String projectId, @RequestParam("position") String position) {
        projectService.sign(getCurrentTenantId(), projectId, getLoggedUserInfo().getEmail(), position);
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/status/to")
    public void changeProjectStatusToAssigned(@PathVariable String projectId, @RequestParam("status") String status) {
        ProjectStatus projectStatus = ProjectStatus.fromLabel(status);
        switch (projectStatus){
            case ASSIGNED:
                changeProjectStatusToAssigned(projectId);
                break;
            case REGISTRATION:
                changeProjectStatusToRegistered(projectId);
                break;

            case START:
                changeProjectStatusToStarted(projectId);
                break;

            case REALLOCATED:
            case WITHDRAW:
            case SUSPEND:
            case PROGRESS:
                changeProjectStatusToReAllocatedOrWithdrawOrSuspendOrProgress(projectId, projectStatus);
                break;
            case COMPLETED:
                changeProjectStatusToCompleted(projectId);
                break;

            default:
                throw new PPMSException("Unsupported Status:" + projectStatus.getLabel());
        }

    }
    private void changeProjectStatusToAssigned(String projectId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER, Role.STAFF);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF)) isSameModule(projectModuleId);
        Department department = organisationService.getDepartment(getCurrentTenantId() , projectModuleId);
        projectService.changeStatusToAssigned(getCurrentTenantId(), projectId,
                getLoggedUserInfo().getEmail(), department, isHasRole(Role.MODULE_LEADER));
    }

    private void changeProjectStatusToRegistered(String projectId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER, Role.STAFF);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF)) isSameModule(projectModuleId);
        Department department = organisationService.getDepartment(getCurrentTenantId() , projectModuleId);
        projectService.changeStatusToRegistered(getCurrentTenantId(), projectId,
                getLoggedUserInfo().getEmail(), department, isHasRole(Role.MODULE_LEADER));
    }


    private void changeProjectStatusToStarted(String projectId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole( Role.MODULE_LEADER)) isSameModule(projectModuleId);
        Department department = organisationService.getDepartment(getCurrentTenantId() , projectModuleId);
        projectService.changeStatusToStarted(getCurrentTenantId(), projectId, department);
    }


    private void changeProjectStatusToReAllocatedOrWithdrawOrSuspendOrProgress(String projectId, ProjectStatus status) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole( Role.MODULE_LEADER)) isSameModule(projectModuleId);
        projectService.changeStatusToReAllocatedOrWithdrawOrSuspendOrProgress(getCurrentTenantId(), projectId, status);
    }

    private void changeProjectStatusToProgress(String projectId, ProjectStatus status) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole( Role.MODULE_LEADER)) isSameModule(projectModuleId);
        projectService.changeStatusToReAllocatedOrWithdrawOrSuspendOrProgress(getCurrentTenantId(), projectId, status);
    }

    private void changeProjectStatusToCompleted(String projectId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole( Role.MODULE_LEADER)) isSameModule(projectModuleId);
        Department department = organisationService.getDepartment(getCurrentTenantId() , projectModuleId);
        projectService.changeStatusToCompleted(getCurrentTenantId(), projectId, department);
    }

    private void isSameModule(String projectModuleId){
            if(!projectModuleId.equals(getLoggedUserInfo().getModuleId())){
                throw new AccessDeniedException("Staff cannot perform operation on different module");
            }
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects")
    public String addProject(@RequestBody Project project) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        if (isHasAnyRole( Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(project.getDepartmentId());
        Department department = organisationService.getDepartment(getCurrentTenantId() , project.getDepartmentId());
        if (Boolean.TRUE.equals(department.getStudentCannotCreateProject())){
            throw new PPMSException("Student cannot create project in this module");
        }
    	return projectService.addProject(getCurrentTenantId(), project, department, person).getProjectId();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/with-upload", consumes = {"multipart/form-data"})
    public String addProjectWithLogo(@RequestPart("project") Project project,
                                  @RequestPart("image") MultipartFile image) {

        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        if (isHasAnyRole( Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(project.getDepartmentId());
        Department department = organisationService.getDepartment(getCurrentTenantId() , project.getDepartmentId());
        if (Boolean.TRUE.equals(department.getStudentCannotCreateProject())){
            throw new PPMSException("Student cannot create project in this module");
        }

        String projectId = projectService.addProject(getCurrentTenantId(), project, department, person).getProjectId();
        if (image != null) {
            FileDescriptor fileDescriptor = storeImage(loggedUserInfo.getTenantId(), projectId, image);
            this.attachService.attach(getCurrentTenantId(), AttachType.PROJECT_LOGO, projectId,
                    fileDescriptor.getFileName(), fileDescriptor.getKey(), fileDescriptor.getUrl(), fileDescriptor.getContentType());
        }
        return projectId;
    }

        @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{id}")
    public String update(@PathVariable String id, @RequestBody Project project) {
            LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
            Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
            if (isHasAnyRole( Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(project.getDepartmentId());
            return projectService.updateProject(getCurrentTenantId(), id, project, person.getId());
    }


    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{id}")
    public ProjectResource show(@PathVariable String id) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));

        Project project = projectService.getProjectsById(getCurrentTenantId(), id);
        if (person.getDepartmentId()!= null && !project.getDepartmentId().equals(person.getDepartmentId()))
            throw new PPMSException(ErrorCode.PROJECT_ID_NOT_FOUND, "Project not found");

        boolean isAdmin = isHasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        List<String> allPersonIds =  projectRelatedPersons(project);
        List<Person> personList = personService.getAllPersonsByIds(loggedUserInfo.getTenantId(), allPersonIds);
        return toResource(project, organisation, person.getId(), isAdmin, personList);
    }


    
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{id}")
    public void deleteProject(@PathVariable String id) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());

        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), id);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);

        projectService.delete(getCurrentTenantId(), id, person.getId(), isHasRole(Role.MODULE_LEADER));
    }
    
    /****************************************Goals*************************************************************/

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/status")
    public String setGoalStatus(@PathVariable String projectId, @PathVariable String goalId,
                                @RequestParam("status") GoalStatus newStatus, @RequestBody Task task) {
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);

        if (asList(GoalStatus.COMPLETED, GoalStatus.DECLINED).contains(newStatus) ){
            hasAnyRole(Role.ADMIN_CREATOR, Role.MODULE_LEADER, Role.STAFF);
        }
         projectService.updateGoalStatus(getCurrentTenantId(), projectId, goalId, getLoggedUserInfo().getEmail(),
                newStatus, task, isHasAnyRole(Role.MODULE_LEADER));
        return goalId;
    }


    private FileDescriptor storeFile(long tenantId, String projectId, String type, MultipartFile file){
        String mimeType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String fileKey = String.format("ORG-%d/projects/%s/%s/%s", tenantId, projectId, type, fileName);
        String url = documentManagementService.uploadFile(fileKey, file, false);
        return new FileDescriptor(fileName, fileKey, url, mimeType);
    }

    private FileDescriptor storeImage(long tenantId, String projectId, MultipartFile file){
        String mimeType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String fileKey = String.format("ORG-%d/projects/%s/logo/%s", tenantId, projectId, fileName);
        String url = documentManagementService.uploadFile(fileKey, file, true);
        return new FileDescriptor(fileName, fileKey, url, mimeType);
    }


    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/status-with-upload" , consumes = {"multipart/form-data"})
    public String setGoalStatusWithFiles(@PathVariable String projectId, @PathVariable String goalId,
                                      @RequestParam("status") GoalStatus newStatus,
                                      @RequestPart("task") Task task,
                                      @RequestPart("files") List<MultipartFile> files) {
        long tenantId = getCurrentTenantId();
        String projectModuleId = projectService.getModuleId(tenantId, projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);

        if (asList(GoalStatus.COMPLETED, GoalStatus.DECLINED).contains(newStatus) ){
            hasAnyRole(Role.ADMIN_CREATOR, Role.MODULE_LEADER, Role.STAFF);
        }
        if (task != null && !CollectionUtils.isEmpty(files)){
            task.setAttachmentList(files.stream().map(f -> storeFile(tenantId, projectId, "actions", f)).collect(Collectors.toList()));
        }

        projectService.updateGoalStatus(tenantId, projectId, goalId, getLoggedUserInfo().getEmail(),
                newStatus, task, isHasAnyRole(Role.MODULE_LEADER));

        return goalId;
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{projectId}/goals/{goalId}")
    public Goal getGoal(@PathVariable String projectId, @PathVariable String goalId) {
    	return projectService.getGoal(getCurrentTenantId(), projectId, goalId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{projectId}/goals/{goalId}")
    public void deleteGoal(@PathVariable String projectId, @PathVariable String goalId) {
    	 projectService.deleteGoal(getCurrentTenantId(), projectId, goalId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals")
    public String addNewGoal(@PathVariable String projectId, @RequestBody Goal goal) {
    	return projectService.addGoal(getCurrentTenantId(), projectId, goal, getLoggedUserInfo().getEmail());
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals-with-upload", consumes = {"multipart/form-data"})
    public String addGoalWithFile(@PathVariable String projectId,
                                  @RequestPart("goal")  Goal goal,
                                  @RequestPart("files") List<MultipartFile> files) {
        long tenantId = getCurrentTenantId();
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);

        if (goal != null && !CollectionUtils.isEmpty(files)){
            goal.setAttachmentsArrayList(files.stream().map(f -> storeFile(tenantId, projectId, "goals", f)).collect(Collectors.toList()));
        }
        return projectService.addGoal(getCurrentTenantId(), projectId, goal, getLoggedUserInfo().getEmail());
    }
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goals/{goalId}")
    public String updateGoal(@PathVariable String projectId, @PathVariable String goalId, @RequestBody Goal goal){
        return projectService.updateGoal(getCurrentTenantId(), projectId, goalId, goal, Collections.emptyList());
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goals-with-upload/{goalId}", consumes = {"multipart/form-data"})
    public String updateGoalWithFile(@PathVariable String projectId, @PathVariable String goalId,   @RequestPart("goal")  Goal goal,
                             @RequestPart("files") List<MultipartFile> files) {
        long tenantId = getCurrentTenantId();
        String projectModuleId = projectService.getModuleId(getCurrentTenantId(), projectId);
        if (isHasAnyRole(Role.MODULE_LEADER, Role.STAFF, Role.STUDENT)) isSameModule(projectModuleId);

        List<FileDescriptor> fileDescriptors = Optional.ofNullable(files)
                .map(fs -> fs.stream().map(f -> storeFile(tenantId, projectId, "goals", f)).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    	return projectService.updateGoal(getCurrentTenantId(), projectId, goalId, goal, fileDescriptors);
    }

    /******************************************** TASKS********************************************************/
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}")
    public Task getTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId) {
    	return projectService.getTask(getCurrentTenantId(), projectId, goalId, taskId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}")
    public void deleteTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId) {
    	 projectService.deleteTask(getCurrentTenantId(), projectId, goalId, taskId);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks")
    public String addNewTask(@PathVariable String projectId, @PathVariable String goalId, @RequestBody Task task) {
    	return projectService.addNewTask(getCurrentTenantId(), projectId, goalId, task);
    }



    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}")
    public String updateTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId, @RequestBody Task task) {
    	return projectService.updateTask(getCurrentTenantId(), projectId, goalId, taskId, task);
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}/messages")
    public String addMessage(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId, @RequestBody Message message) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        message.setWriter(new ShortPerson(person.getId(), person.getFirstName(), person.getLastName(), person.getPhotoFileId()));
        return projectService.addMessage(getCurrentTenantId(), projectId, goalId, taskId, message);
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}/messages/{messageId}")
    public String updateMessage(@PathVariable String projectId, @PathVariable String goalId,
                             @PathVariable String taskId, @PathVariable String messageId, @RequestBody Message message) {
        return projectService.updateMessage(getCurrentTenantId(), projectId, goalId, taskId, messageId, message);
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}/status")
    public void updateTaskStatus(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId, @RequestBody String status) {
        projectService.updateTaskStatus(getCurrentTenantId(), projectId, goalId, taskId, status);
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}/assign")
    public void assignTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId,  @RequestBody Assignment assignment) {
        projectService.assignTask(getCurrentTenantId(), projectId, goalId, taskId, assignment);
    }
}
