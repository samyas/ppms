package com.advancedit.ppms.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.service.OrganisationService;

@RestController
public class OrganisationController {

	@Autowired
    OrganisationService organisationService;

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations")
    public List<Organisation> product() {
        return organisationService.getAllOrganisations();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/organisations")
    public String save(@RequestBody Organisation organisation) {
       return organisationService.addOrganisation(organisation).getId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}")
    public Organisation getDetail(@PathVariable String organisationId) {
        return organisationService.getOrganisationById(organisationId);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}")
    public String update(@PathVariable String organisationId, @RequestBody Organisation organisation) {
    	organisation.setId(organisationId);
        organisationService.updateOrganisation(organisation);
        return organisationId;
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}")
    public void delete(@PathVariable String organisationId) {
       organisationService.delete(organisationId);

    }
    
    //Department Management
    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments")
    public String addDepartment(@PathVariable String organisationId, @RequestBody Department department) {
    	   return organisationService.addDepartment(organisationId, department).getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public String updateDepartment(@PathVariable String organisationId, @PathVariable String departmentId, @RequestBody Department department) {
    	   return organisationService.updateDepartment(organisationId, departmentId, department).getId();
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public Department getDepartment(@PathVariable String organisationId, @PathVariable String departmentId) {
    	   return organisationService.getDepartment(organisationId, departmentId);
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public void deleteDepartment(@PathVariable String organisationId, @PathVariable String departmentId) {
    	    organisationService.deleteDepartment(organisationId, departmentId);
    }
    
    //Sector Management
    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors")
    public String addSector(@PathVariable String organisationId, @PathVariable String departmentId, @RequestBody Sector sector) {
    	   return organisationService.addSector(organisationId, departmentId, sector).getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public String updateSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId, @RequestBody Sector sector) {
    	   return organisationService.updateSector(organisationId, departmentId, sectorId, sector).getId();
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public Sector getSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId) {
    	   return organisationService.getSector(organisationId, departmentId, sectorId);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public void deleteSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId) {
    	    organisationService.deleteSector(organisationId, departmentId, sectorId);
    }

}
