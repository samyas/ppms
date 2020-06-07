package com.advancedit.ppms.controllers;

import java.util.List;
import java.util.Optional;

import com.advancedit.ppms.controllers.beans.OrganisationResource;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.*;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.service.PersonService;
import com.advancedit.ppms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

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
    
    /************************** Department Management *****************************************/
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

    private boolean isDepartmentModelLeader(String currentUserEmail, long tenantId, String organisationId, String departmentId){
        ShortPerson responsible = organisationService.getDepartment(tenantId, organisationId, departmentId).getResponsible();
        return  (responsible != null && personService.getPersonById(tenantId, responsible.getPersonId()).getEmail().equals( currentUserEmail));

    }

    /************************** Terms Management *****************************************/

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/terms")
    public List<SupervisorTerm> getTerms(@PathVariable String organisationId, @PathVariable String departmentId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.getDepartment(getCurrentTenantId(), organisationId, departmentId).getSupervisorTerms();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments/{departmentId}/terms")
    public String addTerm(@PathVariable String organisationId, @PathVariable String departmentId, @RequestBody SupervisorTerm term) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.addTerm(getCurrentTenantId(), organisationId, departmentId, term);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}/terms/{termId}")
    public String updateTerm(@PathVariable String organisationId, @PathVariable String departmentId,  @PathVariable String termId, @RequestBody SupervisorTerm term) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.updateTerm(getCurrentTenantId(),organisationId, departmentId, termId, term);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}/departments/{departmentId}/terms/{termId}")
    public void deleteTerm(@PathVariable String organisationId, @PathVariable String departmentId,  @PathVariable String termId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        organisationService.deleteTerm(getCurrentTenantId(), organisationId, departmentId, termId);
    }


    /************************** Actions Management *****************************************/


    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/actions")
    public List<Action> getActions(@PathVariable String organisationId, @PathVariable String departmentId) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.getDepartment(getCurrentTenantId(), organisationId, departmentId).getActions();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/organisations/{organisationId}/departments/{departmentId}/actions")
    public String addAction(@PathVariable String organisationId, @PathVariable String departmentId,
                            @RequestParam(value = "updateAllProject", required = false) Boolean updateAllProject,
                            @RequestParam (value = "projectIds", required = false) List<String> projectIds,
                            @RequestBody Action action) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.addAction(getCurrentTenantId(), organisationId, departmentId, action);
    }


    @RequestMapping(method=RequestMethod.PUT, value="/api/organisations/{organisationId}/departments/{departmentId}/actions/{actionId}")
    public String updateAction(@PathVariable String organisationId, @PathVariable String departmentId,  @PathVariable String actionId,
                               @RequestParam(value = "updateAllProject", required = false) Boolean updateAllProject,
                               @RequestParam (value = "projectIds", required = false) List<String> projectIds, @RequestBody Action action) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        return organisationService.updateAction(getCurrentTenantId(),organisationId, departmentId, actionId, action);
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/organisations/{organisationId}/departments/{departmentId}/actions/{actionId}")
    public Action getAction(@PathVariable String organisationId, @PathVariable String departmentId, @PathVariable String actionId ) {
        return organisationService.getAction(getCurrentTenantId(), organisationId, departmentId, actionId);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/organisations/{organisationId}/departments/{departmentId}/actions/{actionId}")
    public void deleteAction(@PathVariable String organisationId, @PathVariable String departmentId,  @PathVariable String actionId,
                            @RequestParam(value = "updateAllProject", required = false) Boolean updateAllProject,
                             @RequestParam (value = "projectIds", required = false) List<String> projectIds) {
        hasAnyRole(Role.ADMIN_CREATOR, Role.SUPER_ADMIN, Role.MODULE_LEADER);
        if (isHasRole(Role.MODULE_LEADER) && !isDepartmentModelLeader(getLoggedUserInfo().getEmail(), getCurrentTenantId(), organisationId, departmentId)){
            throw new AccessDeniedException("Only Module leader of this module can perform this operation");
        }
        organisationService.deleteAction(getCurrentTenantId(), organisationId, departmentId, actionId);
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
