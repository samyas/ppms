package com.advancedit.ppms.service;

import java.util.List;

import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.PersonRepository;



@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileStorageRepository fileStorageRepository;

    public List<Person> getAllPersons(long tenantId){
    	return personRepository.findByTenantId(tenantId);
    }
    
	public Page<Person> getPagedListPerson(long tenantId, int page, int size, PersonFunction function, String status, String name) {
		Pageable pageableRequest =  PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<Person> persons = null;
		if (StringUtils.isEmpty(name)){
			persons = personRepository.findByTenantIdAndPersonFunctionAndStatus(tenantId, function, status, pageableRequest);
		}else{
		    persons = personRepository.findByAllCriteria(tenantId, function, status, name, pageableRequest);
		}
		return persons;
	}

    public Person getPersonByEmail(long tenantId, String email){
    	return personRepository.findByTenantIdAndEmail(tenantId, email);
    }
    
    public Person getPersonById(long tenantId, String id){
    	return personRepository.findById(id)
				.filter(p -> p.getTenantId() == tenantId)
				.orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", id)));
    }


    
    public Person addPerson(long tenantId, Person person, boolean isValid){
       	if (personRepository.findByTenantIdAndEmail(tenantId, person.getEmail()) != null){
       		throw new PPMSException(ErrorCode.PERSON_EMAIL_ALREADY_EXIST, String.format("Email already exist '%s'.", person.getEmail()));
   	    }
       	person.setId(null);
		person.setTenantId(tenantId);
		person.setValid(isValid);
    	return personRepository.save(person); 	
    }
    
    
    public Person updatePerson(long tenantId, Person person){
		getPersonById(tenantId, person.getId());
		person.setTenantId(tenantId);
    	return personRepository.save(person);
    }

	public void validatePerson(long tenantId, String personId) {
		Person person = getPersonById(tenantId, personId);
		person.setValid(true);
		person = personRepository.save(person);
		User user = userRepository.findByEmail(person.getEmail());
		user.setEnabled(true);
		userRepository.save(user);
	}


	public void delete(long tenantId, String id) {
		Person savedPerson =  getPersonById(tenantId, id);
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
