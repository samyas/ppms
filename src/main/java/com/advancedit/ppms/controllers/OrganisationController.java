package com.advancedit.ppms.controllers;

import java.util.List;
import java.util.Optional;

import com.advancedit.ppms.controllers.beans.OrganisationResource;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.service.PersonService;
import com.advancedit.ppms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

import static com.advancedit.ppms.controllers.presenter.OrganisationPresenter.toResource;
import static com.advancedit.ppms.utils.SecurityUtils.*;

@RestController
public class OrganisationController {

	@Autowired
    OrganisationService organisationService;

    @Autowired
    PersonService personService;

   @RequestMapping(method=RequestMethod.GET, value="/api/organisations")
    public List<Organisation> organisations() {
       hasRole(Role.SUPER_ADMIN);
        return organisationService.getAllOrganisations();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/organisations")
    public String save(@RequestBody Organisation organisation) {
        hasRole(Role.ADMIN_CREATOR);
       return organisationService.addOrganisation(getCurrentTenantId(), getLoggedUserInfo().getEmail(),  organisation).getId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/connected/user")
    public OrganisationResource getDetailByUser() {
        Organisation organisation = organisationService.getOrganisationByTenantId(getCurrentTenantId()).orElseThrow(() ->
                new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "No organisation found linked to the connected user"));
       return toResource(organisation, personService.getPersonByEmail(getCurrentTenantId(), organisation.getResponsibleEmail()));
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}")
    public Organisation getDetail(@PathVariable String organisationId) {
        return organisationService.getOrganisationById(getCurrentTenantId(), organisationId);
    }


    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}")
    public String update(@PathVariable String organisationId, @RequestBody Organisation organisation) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
        organisation.setId(organisationId);
        organisationService.updateOrganisation(getCurrentTenantId(), organisation);
        return organisationId;
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}")
    public void delete(@PathVariable String organisationId) {
        hasAnyRole(Role.ADMIN_CREATOR);
       organisationService.delete(getCurrentTenantId(),organisationId);

    }
    
    //Department Management
    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments")
    public String addDepartment(@PathVariable String organisationId, @RequestBody Department department) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
        return organisationService.addDepartment(getCurrentTenantId(), organisationId, department);
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public String updateDepartment(@PathVariable String organisationId, @PathVariable String departmentId, @RequestBody Department department) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
    	   return organisationService.updateDepartment(getCurrentTenantId(),organisationId, departmentId, department);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public Department getDepartment(@PathVariable String organisationId, @PathVariable String departmentId) {
        return organisationService.getDepartment(getCurrentTenantId(), organisationId, departmentId);
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}/departments/{departmentId}")
    public void deleteDepartment(@PathVariable String organisationId, @PathVariable String departmentId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
        organisationService.deleteDepartment(getCurrentTenantId(), organisationId, departmentId);
    }
    
    //Sector Management
    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors")
    public String addSector(@PathVariable String organisationId, @PathVariable String departmentId, @RequestBody Sector sector) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
    	   return organisationService.addSector(getCurrentTenantId(), organisationId, departmentId, sector);
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public String updateSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId, @RequestBody Sector sector) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
        return organisationService.updateSector(getCurrentTenantId(), organisationId, departmentId, sectorId, sector).getId();
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public Sector getSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId) {

    	   return organisationService.getSector(getCurrentTenantId(), organisationId, departmentId, sectorId);
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}/departments/{departmentId}/sectors/{sectorId}")
    public void deleteSector(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String sectorId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN);
        organisationService.deleteSector(getCurrentTenantId(), organisationId, departmentId, sectorId);
    }

}
