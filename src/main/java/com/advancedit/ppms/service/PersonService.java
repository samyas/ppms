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
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.PersonRepository;



@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private FileStorageRepository fileStorageRepository;

    public List<Person> getAllPersons(){
    	return personRepository.findAll();
    }
    
	public Page<Person> getPagedListPerson(int page, int size, PersonFunction function, String status, String name) {
		Pageable pageableRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "name"));
		Page<Person> persons = null;
		if (StringUtils.isEmpty(name)){
			persons = personRepository.findByPersonFunctionAndStatus(function, status, pageableRequest);
		}else{
		    persons = personRepository.findByAllCriteria(function, status, name, pageableRequest);
		}
	
		
		return persons;
	}
    

    public Person getPersonByEmail(String email){
    	return personRepository.findByEmail(email);
    }
    
    public Person getPersonById(String id){
    	
    	return personRepository.findById(id).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", id)));
    	
    }
    
    public Person addPerson(Person person){
       	if (personRepository.findByEmail(person.getEmail()) != null){
       		throw new PPMSException(ErrorCode.PERSON_EMAIL_ALREADY_EXIST, String.format("Email already exist '%s'.", person.getEmail()));
   	    }
       	person.setId(null);
    	return personRepository.save(person); 	
    }
    
    
    public Person updatePerson(Person person){
    	personRepository.findById(person.getId()).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", person.getId())));
    	return personRepository.save(person); 
    	
    }


	public void delete(String id) {
		Person savedPerson = personRepository.findById(id).orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", id)));
    	if (savedPerson != null){
    		if (savedPerson.getPhotoFileId() != null){
    			fileStorageRepository.delete(savedPerson.getPhotoFileId());
    		}
    		
    		if (savedPerson.getCvFileId() != null){
    			fileStorageRepository.delete(savedPerson.getCvFileId());
    		}
    		
    		personRepository.deleteById(id);
    	}
		
	}
   
}
