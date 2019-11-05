package com.advancedit.ppms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, PersonRepository personRepository, ProjectRepository projectRepository, 
		
			 PasswordEncoder bCryptPasswordEncoder) {

	    return args -> {

	      	    
	    	
	    	if (userRepository.count() == 0){
	    		
	    		User user = new User();
	    		user.setEmail("imed.romdhani@gmail.com");
	    		user.setFullname("Imed  Romdhani");
	    		user.setPassword("ppms2020");
	    		  user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	    		user.setRoles(Collections.singleton(Role.STAFF));
	    		user.setEnabled(true);
	    		user.setPermissions(new HashSet<>(Arrays.asList(Permission.CREATE_PROJECT, Permission.ASSIGN)));
	    		
	    		userRepository.save(user);
	    		
	    		
	    		User user1 = new User();
	    		user1.setEmail("abdessalem.samet@gmail.com");
	    		user1.setFullname("Abdessalem  SAMET");
	    		user1.setPassword("ppms2019");
	    		user1.setPassword(bCryptPasswordEncoder.encode(user1.getPassword()));
	    		user1.setRoles(Collections.singleton(Role.STUDENT));
	    		user1.setEnabled(true);
	    	//	user1.setPermissions(new HashSet<>(Arrays.asList(Permission.CREATE_PROJECT, Permission.ASSIGN)));
	    		
	    		userRepository.save(user1);
	    		
	    		
	    		
	    		personRepository.saveAll(getDefaultPerson());
	    		
	    		
	    		projectRepository.saveAll(getDefaultProjects());
	    		
	    		
	    	}
	    		
	    };

	}
	
	private List<Person> getDefaultPerson() {
		
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
	
	}

	
    private List<Project> getDefaultProjects() {
		
		List<Project> projects = new ArrayList<>();
		for(int i = 0; i< 30; i++){
			Project p1 = new Project();
			p1.setName("Project " + i);
			p1.setDescription("description " + 1);
			p1.setCategory("Chimestry & Physics");
			projects.add(p1);
		}
		return projects;
	
	}

}

