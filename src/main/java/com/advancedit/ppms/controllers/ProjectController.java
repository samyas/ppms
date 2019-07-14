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

import com.advancedit.ppms.models.Project;
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

    @RequestMapping(method=RequestMethod.POST, value="/api/projects")
    public String save(@RequestBody Project project) {
    	return projectService.addProject(project).getProjectId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/projects/{id}")
    public Project show(@PathVariable String id) {
    	return projectService.getProjectsById(id);
    }

}
