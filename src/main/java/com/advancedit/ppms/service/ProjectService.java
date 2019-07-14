package com.advancedit.ppms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.Person;
import com.advancedit.ppms.models.PersonFunction;
import com.advancedit.ppms.models.Project;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;



@Service
public class ProjectService {
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private FileStorageRepository fileStorageRepository;

    public List<Project> getAllProjects(){
    	return projectRepository.findAll();
    }
    
	public Page<Project> getPagedListProject(int page, int size, String status, String name) {
		Pageable pageableRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "name"));
		Page<Project> projects = null;
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(status)){
			projects = projectRepository.findAll(pageableRequest);
		}else{
			projects = projectRepository.findByAllCriteria(status, name, pageableRequest);
		}
	
		
		return projects;
	}
    

    public Project getProjectsById(String id){
    	
    	return projectRepository.findById(id).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Project id not found '%s'.", id)));
    	
    }
    
    public Project addProject(Project project){
       	project.setProjectId(null);
    	return projectRepository.save(project); 	
    }
    
    
    public Project updateProject(Project project){
    	projectRepository.findById(project.getProjectId()).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Project id not found '%s'.", project.getProjectId())));
    	return projectRepository.save(project); 
    	
    }


	public void delete(String id) {
		Project savedPerson = projectRepository.findById(id).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Project id not found '%s'.", id)));
    	if (savedPerson != null){
    		/*if (savedPerson.getPhotoFileId() != null){
    			fileStorageRepository.delete(savedPerson.getPhotoFileId());
    		}
    		
    		if (savedPerson.getCvFileId() != null){
    			fileStorageRepository.delete(savedPerson.getCvFileId());
    		}*/
    		
    		projectRepository.deleteById(id);
    	}
		
	}
   
}
