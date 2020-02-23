package com.advancedit.ppms;

import java.util.*;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.user.Permission;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import com.advancedit.ppms.repositories.UserRepository;

@SpringBootApplication
public class PPMSApplication {

	public static void main(String[] args) {
		SpringApplication.run(PPMSApplication.class, args);
	}

	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, PersonRepository personRepository,
						   ProjectRepository projectRepository, OrganisationRepository organisationRepository,
						   SequenceGeneratorService sequenceGeneratorService,
		
			 PasswordEncoder bCryptPasswordEncoder) {

	    return args -> {

	      	    
	    	
	    	if (userRepository.count() == 0){
	    		
	    		
	    		User user1 = new User();
	    		user1.setEmail("abdessalemsamet@gmail.com");
	    		user1.setUsername("asamet");
	    		user1.setPassword("toutou");
	    		user1.setFirstName("SAMET");
	    		user1.setLastName("Abdessalem");
	    		user1.setPassword(bCryptPasswordEncoder.encode(user1.getPassword()));
	    		user1.setRoles(Collections.singleton(Role.SUPER_ADMIN));
	    		user1.setEmailIsValid(true);
	    		user1.setEnabled(true);
	    	//	user1.setPermissions(new HashSet<>(Arrays.asList(Permission.CREATE_PROJECT, Permission.ASSIGN)));
	    		


				long tenantId = sequenceGeneratorService.generateSequence(Organisation.SEQUENCE_NAME);

				Organisation organisation = new Organisation() ;
				organisation.setId(null);
				organisation.setTenantId(tenantId);
				organisation.setName("Default");
				organisation.setDepartments(null);
				 organisationRepository.save(organisation);

				 user1.setDefaultTenantId(tenantId);
				 user1.setTenantIds(Collections.singletonList(tenantId));


				 Person person = new Person();
				 person.setFirstName(user1.getFirstName());
				 person.setLastName(user1.getLastName());
				 person.setEmail(user1.getEmail());
				 person.setPersonfunction(PersonFunction.ADMIN_CREATOR);
				 person.setRegistered(true);
				 person.setTenantId(tenantId);

				 personRepository.save(person);

				userRepository.save(user1);
	    		
	    	//	personRepository.saveAll(getDefaultPerson());
	    		
	    		
	    	//	projectRepository.saveAll(getDefaultProjects());
	    		
	    		
	    	}
	    		
	    };

	}
	
	/*private List<Person> getDefaultPerson() {
		
		List<Person> persons = new ArrayList<>();
		for(int i = 0; i< 30; i++){
			Person p1 = new Person( "user" + i + "@napier.uk", "Sebastian" + i, "Mike" + i);

			p1.setJob("Devloppeur java");
			p1.setYearsExperience(5);
			p1.setPhone("+878798989"+i);
			p1.setPersonfunction(i % 2 == 0 ? PersonFunction.PHD_STUDENT : PersonFunction.TEACHER);
			persons.add(p1);
		}
		return persons;
	
	}*/

	
    /*private List<Project> getDefaultProjects() {
		
		List<Project> projects = new ArrayList<>();
		for(int i = 0; i< 30; i++){
			Project p1 = new Project();
			p1.setName("Project " + i);
			p1.setDescription("description " + 1);
			p1.setCategory("Chimestry & Physics");
			projects.add(p1);
		}
		return projects;
	
	}*/

}

