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
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;



@Service
public class OrganisationService {
	@Autowired
	private OrganisationRepository organisationRepository;
	
	@Autowired
	private FileStorageRepository fileStorageRepository;

	public List<Organisation> getAllOrganisations(){
    	return organisationRepository.findAll();
    }

    
    public Organisation getOrganisationById(String id){
    	return organisationRepository.findById(id).orElseThrow(() ->  
    	new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", id)));
    	
    }
    
    public Organisation addOrganisation(Organisation person){
       	person.setId(null);
    	return organisationRepository.save(person); 	
    }
    
    
    public Organisation updateOrganisation(Organisation person){
    	organisationRepository.findById(person.getId()).orElseThrow(() ->  new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", person.getId())));
    	return organisationRepository.save(person); 
    	
    }


	public void delete(String id) {
		Organisation savedOrganisation = organisationRepository.findById(id).orElseThrow(() ->  new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", id)));
    	if (savedOrganisation != null){
    		if (savedOrganisation.getLogoId() != null){
    			fileStorageRepository.delete(savedOrganisation.getLogoId());
    		}
    		
    		//TODO all related information
    		
    		organisationRepository.deleteById(id);
    	}
		
	}


	public Department addDepartment(String organisationId, Department department) {
		// TODO Auto-generated method stub
		return null;
	}


	public Department updateDepartment(String organisationId, String departmentId, Department department) {
		// TODO Auto-generated method stub
		return null;
	}


	public Department getDepartment(String organisationId, String departmentId) {
		// TODO Auto-generated method stub
		return null;
	}


	public void deleteDepartment(String organisationId, String departmentId) {
		// TODO Auto-generated method stub
	}


	public Sector addSector(String organisationId, String departmentId, Sector sector) {
		// TODO Auto-generated method stub
		return null;
	}


	public Sector updateSector(String organisationId, String departmentId, String sectorId, Sector sector) {
		// TODO Auto-generated method stub
		return null;
	}


	public Sector getSector(String organisationId, String departmentId, String sectorId) {
		// TODO Auto-generated method stub
		return null;
	}


	public void deleteSector(String organisationId, String departmentId, String sectorId) {
		// TODO Auto-generated method stub
		
	}
   
}
