package com.advancedit.ppms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.models.Goal;
import com.advancedit.ppms.models.Project;
import com.advancedit.ppms.models.Task;
import com.advancedit.ppms.service.ProjectService;

@RestController
public class ProjectController {

	@Autowired
    ProjectService projectService;

    @RequestMapping(method=RequestMethod.GET, value="/api/projects")
    public List<Project> all() {
        return  projectService.getAllProjects();
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/paged")
    public Page<Project> getPagedProjects(	@RequestParam("page") int page, @RequestParam("size") int size, 
    		 @RequestParam(name = "status", required=false)String status, @RequestParam(name = "name", required=false)String name) {
		return projectService.getPagedListProject(page, size, status, name);
	}
    
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/apply")
    public void apply(@PathVariable String projectId, @RequestBody Apply apply) {
    	 projectService.apply(projectId, apply);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/assign")
    public void assign(@PathVariable String projectId, @RequestBody Assignment assignment) {
    	 projectService.assign(projectId, assignment);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goal")
    public String addGoal(@PathVariable String projectId, @RequestBody Goal goal) {
    	return projectService.addGoal(projectId, goal);
    	 
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/projects")
    public String save(@RequestBody Project project) {
    	return projectService.addProject(project).getProjectId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{id}")
    public String update(@PathVariable String id, @RequestBody Project project) {
    	return projectService.updateProject(id, project).getProjectId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{id}")
    public Project show(@PathVariable String id) {
    	return projectService.getProjectsById(id);
    }
    
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{id}")
    public void deleteProject(@PathVariable String id) {
    	 projectService.deleteProject(id);
    }
    
    
    
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{projectId}/goals/{goalId}")
    public Goal getGoal(@PathVariable String projectId, @PathVariable String goalId) {
    	return projectService.getGoal(projectId, goalId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{projectId}/goals/{goalId}")
    public void deleteGoal(@PathVariable String projectId, @PathVariable String goalId) {
    	 projectService.deleteGoal(projectId, goalId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals")
    public String addNewGoal(@PathVariable String projectId, @RequestBody Goal goal) {
    	return projectService.addGoal(projectId, goal); 
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goals/{goalId}")
    public String updateGoal(@PathVariable String projectId, @PathVariable String goalId, @RequestBody Goal goal) {
    	return projectService.updateGoal(projectId, goalId, goal); 
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}")
    public Task getTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId) {
    	return projectService.getTask(projectId, goalId, taskId);
    	 
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/projects/{projectId}/goals/{goalId}/tasks/{taskId}")
    public void deleteTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId) {
    	 projectService.deleteTask(projectId, goalId, taskId);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/api/projects/{projectId}/goals/{goalId}/tasks")
    public String addNewTask(@PathVariable String projectId, @PathVariable String goalId, @RequestBody Task task) {
    	return projectService.addNewTask(projectId, goalId, task); 
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/projects/{projectId}/goal/{goalId}/tasks/{taskId}")
    public String updateTask(@PathVariable String projectId, @PathVariable String goalId, @PathVariable String taskId, @RequestBody Task task) {
    	return projectService.updateTask(projectId, goalId, taskId, task); 
    }

}
