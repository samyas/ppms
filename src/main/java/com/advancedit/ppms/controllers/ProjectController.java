package com.advancedit.ppms.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.controllers.presenter.ProjectPresenter;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.Message;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.service.OrganisationService;
import com.advancedit.ppms.service.PersonService;
import com.advancedit.ppms.utils.LoggedUserInfo;
import com.advancedit.ppms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.Task;
import com.advancedit.ppms.service.ProjectService;

import static com.advancedit.ppms.controllers.presenter.ProjectPresenter.toResource;
import static com.advancedit.ppms.utils.SecurityUtils.*;

@RestController
public class ProjectController {

	@Autowired
    ProjectService projectService;

    @Autowired
    PersonService personService;

    @Autowired
    OrganisationService organisationService;

    @RequestMapping(method=RequestMethod.GET, value="/api/projects")
    public List<Project> all() {
        return  projectService.getAllProjects(getCurrentTenantId());
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/paged")
    public Page<ProjectResource> getPagedProjects(@RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam(name = "status", required=false)String status,
                                                  @RequestParam(name = "name", required=false)String name) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
               .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        Page<Project> pagedListProject = projectService.getPagedListProject(getCurrentTenantId(), page, size, person.getDepartmentId(), status, name);
        List<ProjectResource> collect = pagedListProject.stream().map(p -> toResource(p, organisation, null, false)).collect(Collectors.toList());
        return new PageImpl<>(collect, pagedListProject.getPageable(), pagedListProject.getTotalElements());
    }
    
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/apply")
    public void apply(@PathVariable String projectId, @RequestBody Apply apply) {
    	 projectService.apply(getCurrentTenantId(), projectId, apply);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/assign")
    public void assign(@PathVariable String projectId, @RequestBody Assignment assignment) {
    	 projectService.assign(getCurrentTenantId(), projectId, assignment);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goal")
    public String addGoal(@PathVariable String projectId, @RequestBody Goal goal) {
    	return projectService.addGoal(getCurrentTenantId(), projectId, goal);
    	 
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects")
    public String save(@RequestBody Project project) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        project.setCreator(new ShortPerson(person.getId(), person.getFirstName(), person.getLastName(), person.getPhotoFileId()));
    	return projectService.addProject(getCurrentTenantId(), project).getProjectId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{id}")
    public String update(@PathVariable String id, @RequestBody Project project) {
    	return projectService.updateProject(getCurrentTenantId(), id, project).getProjectId();
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{id}/status")
    public void updateStatus(@PathVariable String id, @RequestBody String status) {
         projectService.updateStatus(getCurrentTenantId(), id, status);
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
        return toResource(project, organisation, person.getId(), isAdmin);
    }


    
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{id}")
    public void deleteProject(@PathVariable String id) {
    	 projectService.deleteProject(getCurrentTenantId(), id);
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
    	return projectService.addGoal(getCurrentTenantId(), projectId, goal);
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goals/{goalId}")
    public String updateGoal(@PathVariable String projectId, @PathVariable String goalId, @RequestBody Goal goal) {
    	return projectService.updateGoal(getCurrentTenantId(), projectId, goalId, goal);
    }
    
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
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goal/{goalId}/tasks/{taskId}")
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
