package com.advancedit.ppms.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.UserRepository;




@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private PersonRepository personRepository;

    public List<User> getAllUsers(){
    	return userRepository.findAll();
    }
    

    public User getUserByUsername(String username){
    	return userRepository.findByEmail(username);
    	
    }
    
    public User getUserById(String id){
    	
    	User user = userRepository.findById(id).orElseThrow(() -> new PPMSException(ErrorCode.USER_ID_NOT_FOUND, String.format("User id not found '%s'.", id)));
    	return user;
    	
    	
    }
    
    public User addUser(User user){
       	if (userRepository.findByEmail(user.getEmail()) != null){
       		throw new PPMSException(ErrorCode.USER_EMAIL_ALREADY_EXIST, String.format("Email already exist '%s'.", user.getEmail()));
   	    }
       	user.setId(null);
       	user.setRoles(Collections.singleton(Role.STUDENT));
       /*	if (personRepository.findByEmail(user.getEmail()) == null){
           	Person p = new Person();
           	p.setEmail(user.getEmail());
           	personRepository.save(p);
       	}*/

    	return userRepository.save(user);
    	
    }
    
    
 void delete(String userId) {
		userRepository.deleteById(userId);;
	}
}
