package com.advancedit.ppms.controllers;

import java.util.List;

import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.service.PersonService;

import static com.advancedit.ppms.utils.SecurityUtils.*;

@RestController
public class PersonController {

	@Autowired
    PersonService personService;

    @RequestMapping(method=RequestMethod.GET, value="/api/persons")
    public List<Person> all() {
        return  personService.getAllPersons(getCurrentTenantId());
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/persons/paged")
    public Page<Person> getPagedPerson(	@RequestParam("page") int page, @RequestParam("size") int size, 
    		@RequestParam(value = "function", required = false)PersonFunction personFunction,  @RequestParam(value = "status", required = false)String status,
    		@RequestParam(value = "name", required = false)String name) {
		return personService.getPagedListPerson(getCurrentTenantId(), page, size, personFunction, status, name);
	}

    @RequestMapping(method=RequestMethod.POST, value="/api/persons")
    public String save(@RequestBody Person person) {
        hasAnyRole(Role.ADMIN, Role.ADMIN_CREATOR);
    	return personService.addPerson(getCurrentTenantId(), person, true).getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/{id}")
    public String update(@PathVariable String id, @RequestBody Person person) {
    	person.setId(id);
    	if (! isHasAnyRole(Role.ADMIN, Role.ADMIN_CREATOR)){
    	    person.setValid(false);
        }
    	return personService.updatePerson(getCurrentTenantId(), person).getId();
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/{personId}/validate")
    public void validate(@PathVariable String personId) {
        hasAnyRole(Role.ADMIN, Role.ADMIN_CREATOR);
         personService.validatePerson(getCurrentTenantId(), personId);
    }


    @RequestMapping(method=RequestMethod.GET, value="/api/persons/{id}")
    public Person getDetail(@PathVariable String id) {
    	return personService.getPersonById(getCurrentTenantId(), id);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/persons/{id}")
    public void delete(@PathVariable String id) {
        hasAnyRole(Role.ADMIN, Role.ADMIN_CREATOR);
         personService.delete(getCurrentTenantId(), id);
    }

}
