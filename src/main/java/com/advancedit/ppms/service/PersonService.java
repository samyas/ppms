package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.advancedit.ppms.models.person.PersonFunction.MODEL_LEADER;


@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileStorageRepository fileStorageRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private VerificationTokenService verificationTokenService;

	@Autowired
	private EmailService emailService;

    public List<Person> getAllPersons(long tenantId){
    	return personRepository.findByTenantId(tenantId);
    }
    
	public Page<Person> getPagedListPerson(long tenantId, int page, int size, String departmentId, List<PersonFunction> functions, String status, String name) {
		Pageable pageableRequest =  PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		return personRepository.findByTenantIdAndPersonFunctionAndStatus(tenantId, functions, name, status, departmentId, pageableRequest);
	}

	public List<Person> getListPerson(long tenantId,  String departmentId) {
		return personRepository.findListByTenantIdAndDepartmentId(tenantId, departmentId);
	}
    public Person getPersonByEmail(long tenantId, String email){
    	return personRepository.findByTenantIdAndEmail(tenantId, email);
    }
    
    public Person getPersonById(long tenantId, String id){
    	return personRepository.findById(id)
				.filter(p -> p.getTenantId() == tenantId)
				.orElseThrow(() ->  new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", id)));
    }

	public Person addPersonAdminCreator(long tenantId, Person person){
		if (personRepository.findByTenantIdAndEmail(tenantId, person.getEmail()) != null){
			throw new PPMSException(ErrorCode.PERSON_EMAIL_ALREADY_EXIST, String.format("Email already exist '%s'.", person.getEmail()));
		}
		person.setId(null);
		person.setTenantId(tenantId);
		person.setPersonfunction(PersonFunction.ADMIN_CREATOR);
		person.setRegistered(true);
		return personRepository.save(person);
	}
    
    public Person addPerson(long tenantId, Person person){
       	if (personRepository.findByTenantIdAndEmail(tenantId, person.getEmail()) != null){
       		throw new PPMSException(ErrorCode.PERSON_EMAIL_ALREADY_EXIST, String.format("Email already exist '%s'.", person.getEmail()));
   	    }
       Organisation organisation =	organisationRepository.findByTenantId(tenantId);
       String departmentId = Optional.ofNullable(person.getDepartmentId())
			   .orElseThrow(() -> new PPMSException("Department is mandatory"));

		Department department =organisation.getDepartments().stream().filter( d -> d.getDepartmentId().equals(departmentId))
				.findFirst().orElseThrow(() -> new PPMSException("Department was not found"));

		person.setId(null);
		person.setTenantId(tenantId);
		person.setRegistered(false);
    	Person p =  personRepository.save(person);
		if (MODEL_LEADER.equals(person.getPersonfunction())){
			department.setResponsible( new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()));
			organisationRepository.updateDepartment(tenantId, organisation.getId(), department);
		}

		return p;
    }

	public void updateProjectInfo(long tenantId, String personId, int workload, int currentProjects, int previousProjects) {
    	personRepository.updateProjectInfo(tenantId, personId, workload, currentProjects, previousProjects);
	}
    
    public Person updatePerson(long tenantId, Person updatePerson) {
		Person savedPerson = getPersonById(tenantId, updatePerson.getId());
		String departmentId = Optional.ofNullable(updatePerson.getDepartmentId())
				.orElseThrow(() -> new PPMSException("Department is mandatory"));

		Organisation organisation = organisationRepository.findByTenantId(tenantId);
		Department department = organisation.getDepartments().stream().filter(d -> d.getDepartmentId().equals(departmentId))
				.findFirst().orElseThrow(() -> new PPMSException("Department was not found"));

		if (MODEL_LEADER.equals(updatePerson.getPersonfunction()) && (department.getResponsible() == null || !department.getResponsible().getPersonId().equals(updatePerson.getId()))) {
				if(department.getResponsible() != null){
					personRepository.findByTenantIdAndPersonId(tenantId, department.getResponsible().getPersonId()).ifPresent(p -> {
						p.setPersonfunction(PersonFunction.STAFF);
						personRepository.save(p);
					});
				}
			   department.setResponsible(new ShortPerson(savedPerson.getId(), savedPerson.getFirstName(), savedPerson.getLastName(), savedPerson.getPhotoFileId()));
				organisationRepository.updateDepartment(tenantId, organisation.getId(), department);
		}else {
				if (department.getResponsible() != null &&
						savedPerson.getId().equals(department.getResponsible().getPersonId())){
					department.setResponsible(null);
					organisationRepository.updateDepartment(tenantId, organisation.getId(), department);
				}
		}
		savedPerson.setShortDescription(updatePerson.getShortDescription());
        savedPerson.setPersonfunction(updatePerson.getPersonfunction());
		savedPerson.setDepartmentId(updatePerson.getDepartmentId());
        return personRepository.save(savedPerson);
	}


	public void validatePerson(long tenantId, String personId) {
		Person person = getPersonById(tenantId, personId);
		person.setRegistered(true);
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
