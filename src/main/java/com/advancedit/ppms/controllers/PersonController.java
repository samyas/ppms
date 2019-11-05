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

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.service.PersonService;

@RestController
public class PersonController {

	@Autowired
    PersonService personService;

    @RequestMapping(method=RequestMethod.GET, value="/api/persons")
    public List<Person> all() {
        return  personService.getAllPersons();
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/api/pagedPersons")
    public Page<Person> getPagedPerson(	@RequestParam("page") int page, @RequestParam("size") int size, 
    		@RequestParam("function")PersonFunction personFunction,  @RequestParam("status")String status, 
    		@RequestParam("name")String name) {
		return personService.getPagedListPerson(page, size, personFunction, status, name);
	}

    @RequestMapping(method=RequestMethod.POST, value="/api/persons")
    public String save(@RequestBody Person person) {
    	return personService.addPerson(person).getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/api/persons/{id}")
    public String update(@PathVariable String id, @RequestBody Person person) {
    	person.setId(id);
    	return personService.updatePerson(person).getId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/persons/{id}")
    public Person show(@PathVariable String id) {
    	return personService.getPersonById(id);
    }

}
