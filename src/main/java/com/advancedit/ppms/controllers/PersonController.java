package com.advancedit.ppms.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.advancedit.ppms.controllers.beans.PersonResource;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.VerificationToken;
import com.advancedit.ppms.service.EmailService;
import com.advancedit.ppms.service.OrganisationService;
import com.advancedit.ppms.service.PersonService;
import com.advancedit.ppms.service.VerificationTokenService;
import com.advancedit.ppms.utils.LoggedUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;

import javax.servlet.http.HttpServletRequest;

import static com.advancedit.ppms.controllers.presenter.PersonPresenter.toResource;
import static com.advancedit.ppms.utils.SecurityUtils.*;

@RestController
public class PersonController {

	@Autowired
    PersonService personService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailService emailService;


    @RequestMapping(method=RequestMethod.GET, value="/api/persons")
    public List<Person> all() {
        return  personService.getAllPersons(getCurrentTenantId());
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/persons/paged")
    public Page<PersonResource> getPagedPerson(@RequestParam("page") int page, @RequestParam("size") int size,
                                               @RequestParam(value = "function", required = false) PersonFunction personFunction,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "name", required = false) String name) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR, Role.MODULE_LEADER, Role.STAFF);
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Person person = personService.getPersonByEmail(loggedUserInfo.getTenantId(), loggedUserInfo.getEmail());
        Page<Person> pagedListPerson = personService.getPagedListPerson(getCurrentTenantId(), page, size, person.getDepartmentId(), personFunction,
                status, name);
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        List<PersonResource> collect = pagedListPerson.stream().map(p -> toResource(p, organisation)).collect(Collectors.toList());
        return new PageImpl<>(collect, pagedListPerson.getPageable(), pagedListPerson.getTotalElements());

    }

    @RequestMapping(method=RequestMethod.POST, value="/api/persons")
    public String save(@RequestBody Person person, HttpServletRequest request) throws MalformedURLException {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR, Role.MODULE_LEADER);

        Person p = personService.addPerson(getCurrentTenantId(), person);


        sendJoinInvitation(getCurrentTenantId(), p, organisationService.getOrganisationByTenantId(getCurrentTenantId()).get()
                , request.getHeader("Origin"));
        return p.getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/{id}")
    public String update(@PathVariable String id, @RequestBody Person person) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR, Role.MODULE_LEADER);
    	return personService.updatePerson(getCurrentTenantId(), person).getId();
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/{personId}/validate")
    public void validate(@PathVariable String personId) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR);
         personService.validatePerson(getCurrentTenantId(), personId);
    }


    @RequestMapping(method=RequestMethod.GET, value="/api/persons/{id}")
    public PersonResource getDetail(@PathVariable String id) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        Person person = personService.getPersonById(getCurrentTenantId(), id);
        return toResource(person, organisation);
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/persons/current/info")
    public PersonResource getCurrentDetail() {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        Person person = personService.getPersonByEmail(getCurrentTenantId(), loggedUserInfo.getEmail());
        return toResource(person, organisation);

    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/persons/{id}")
    public void delete(@PathVariable String id) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR);
         personService.delete(getCurrentTenantId(), id);
    }

    public void sendJoinInvitation(long tenantId, Person person, Organisation organisation, String domain){
        VerificationToken verificationToken = verificationTokenService.generateValidationEmailToken(tenantId, person.getEmail());
        emailService.sendJoinRequestForPerson(person, organisation, verificationToken, domain);
    }

}
