package com.advancedit.ppms.controllers;

import com.advancedit.ppms.controllers.beans.PersonResource;
import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.controllers.presenter.ProjectPresenter;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.SupervisorTerm;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.Member;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectStatus;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.VerificationToken;
import com.advancedit.ppms.service.*;
import com.advancedit.ppms.utils.LoggedUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.advancedit.ppms.controllers.presenter.PersonPresenter.toResource;
import static com.advancedit.ppms.utils.SecurityUtils.*;
import static java.util.Arrays.asList;

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

    @Autowired
    ProjectService projectService;


    @RequestMapping(method=RequestMethod.GET, value="/api/persons")
    public List<Person> all() {
        return  personService.getAllPersons(getCurrentTenantId());
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/persons/paged")
    public Page<PersonResource> getPagedPerson(@RequestParam("page") int page, @RequestParam("size") int size,
                                               @RequestParam(value = "function", required = false) PersonFunction personFunction,
                                               @RequestParam(value = "student", required = false) Boolean isStudent,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "name", required = false) String name) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR, Role.MODULE_LEADER, Role.STAFF);
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        List<PersonFunction> functions = new ArrayList<>();
        if (isStudent != null){
            if (Boolean.TRUE.equals(isStudent)){
                functions.add(PersonFunction.STUDENT);
            }else{
                functions.addAll(asList(PersonFunction.STAFF, PersonFunction.MODEL_LEADER));
            }
        }
        if (personFunction != null) functions.add(personFunction);
        Page<Person> pagedListPerson = personService.getPagedListPerson(getCurrentTenantId(), page, size, loggedUserInfo.getModuleId(), functions,
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

    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/sync-all")
    public void syncAll( @RequestParam(value = "departmentId", required = false) String departmentId) {
        hasAnyRole(Role.SUPER_ADMIN, Role.ADMIN_CREATOR, Role.MODULE_LEADER);

        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        long tenantId = loggedUserInfo.getTenantId();
        String currentDepartmentId = Optional.ofNullable(loggedUserInfo.getModuleId()).orElse(departmentId);
        if (currentDepartmentId == null){
            throw new PPMSException("DepartmentId should not be empty");
        }
        List<Person> lListPerson = personService.getListPerson(tenantId, currentDepartmentId);
        Department department = organisationService.getDepartment(tenantId, currentDepartmentId);

        lListPerson.forEach( p ->   updatePersonProjectInfo(tenantId, p, department.getSupervisorTerms()));
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

    @RequestMapping(method=RequestMethod.GET, value="/api/persons/{id}/projects")
    public List<ProjectResource> getRelatedProject(@PathVariable String id, @RequestParam(value = "status", required = false) ProjectStatus status) {
        LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
        Organisation organisation = organisationService.getOrganisationByTenantId(loggedUserInfo.getTenantId())
                .orElseThrow(() -> new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, "Organisation was not found"));
        Person person = personService.getPersonById(getCurrentTenantId(), id);
        Person connectedPerson = personService.getPersonByEmail(getCurrentTenantId(), loggedUserInfo.getEmail());
        List<Project> projects = projectService.getProjectListByPerson(loggedUserInfo.getTenantId(), person.getDepartmentId(),
                id, person.getPersonfunction(), status);
        List<String> allPersonIds =  projects.stream().map(this::projectRelatedPersons).flatMap(Collection::stream).collect(Collectors.toList());
        List<Person> personList = personService.getAllPersonsByIds(loggedUserInfo.getTenantId(), allPersonIds);
        return  projects.stream().map(p -> ProjectPresenter.toResource(p, organisation, connectedPerson.getId(),  isHasRole(Role.MODULE_LEADER), personList))
             .collect(Collectors.toList());
    }

    private List<String> projectRelatedPersons(Project project){
        List<String> projectPersonIds = new ArrayList<>();
        Optional.ofNullable(project.getCreator()).map(ShortPerson::getPersonId).ifPresent(projectPersonIds::add);
        project.getTeam().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        project.getMembers().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        return projectPersonIds;
    }

    private void updatePersonProjectInfo(long tenantId, Person person, List<SupervisorTerm> terms){
        List<Project> projects = projectService.getProjectListByPerson(tenantId, person.getDepartmentId(),
                person.getId(), person.getPersonfunction(), null);

        int workload = 0;
        int currentProjects = 0;
        int previousProjects = 0;
        if (projects!= null && !projects.isEmpty()){
            for(Project p : projects){
                if (p.getStatus() == null || ProjectStatus.CLOSED.equals(p.getStatus())){
                    previousProjects = previousProjects + 1;
                }else {
                    currentProjects++;
                }
                if (!person.getPersonfunction().equals(PersonFunction.STUDENT)){
                    Optional<SupervisorTerm> supervisorTerm = p.getMembers().stream().filter(m -> m.getPersonId().equals(person.getId()))
                            .findFirst().flatMap(m -> terms.stream().filter(s -> s.getTermId().equals(m.getTermId())).findFirst());
                    if (supervisorTerm.isPresent()){
                        workload = workload + supervisorTerm.get().getQuota();
                    }
                }
            }
        }
        if (!(person.getWorkload() == workload && person.getCurrentProjects() == currentProjects && person.getPreviousProjects() == previousProjects)){
            personService.updateProjectInfo(tenantId, person.getId(), workload, currentProjects, previousProjects);
        }


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
