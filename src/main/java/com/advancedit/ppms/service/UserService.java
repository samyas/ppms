package com.advancedit.ppms.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.advancedit.ppms.controllers.beans.ValidationTokenResponseBean;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.user.VerificationToken;
import com.advancedit.ppms.repositories.VerificationTokenRepository;
import com.advancedit.ppms.utils.LoggedUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.UserRepository;

import static com.advancedit.ppms.models.person.PersonFunction.*;
import static com.advancedit.ppms.utils.GeneralUtils.decode;


@Service
public class UserService {

	@Value("${email.token.expiration.days:5}")
	private int expirationDuration = 5;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private PersonService personService;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;



	public List<User> getAllUsers(){
    	return userRepository.findAll();
    }
    

    public User getUserByUsername(String username){
    	return userRepository.findByUsername(username);
    }
	public User getUserByEmail(String email){
		return userRepository.findByEmail(email);
	}


    public User getUserById(String id){
    	return userRepository.findById(id).orElseThrow(
    			() -> new PPMSException(ErrorCode.USER_ID_NOT_FOUND, String.format("User id not found '%s'.", id)));
    }



	public User register(User user){
		 Optional.ofNullable(userRepository.findByEmail(user.getEmail()))
				.ifPresent((s) -> {throw new PPMSException(ErrorCode.USER_EMAIL_ALREADY_EXIST, "Email:" + user.getEmail() + " already exists");});

		 Optional.ofNullable(userRepository.findByUsername(user.getUsername()))
				 .ifPresent((s) -> {throw new PPMSException(ErrorCode.USER_USERNAME_ALREADY_EXIST, "Username:" + user.getUsername() + " already exists");});
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		if (Boolean.TRUE.equals(user.getOrganisationCreationRequest())){
			user.setEnabled(false);
			user.setEmailIsValid(false);
		}else{
			user.setEnabled(true);
			user.setEmailIsValid(true);
		}
		user.setRoles(Collections.emptySet());
		user.setPermissions(Collections.emptySet());
		return userRepository.save(user);
	}

	public void activateAdminCreatorAccount(String userId) {
		User user = getUserById(userId);
		if (!user.isEmailIsValid()){
			throw new PPMSException("User email is not validated yet");
		}
		if (user.isEnabled()){
			throw new PPMSException("User account is already activated");
		}
       if (Boolean.TRUE.equals(user.getOrganisationCreationRequest())){
				user.setRoles(Collections.singleton(Role.ADMIN_CREATOR));
		}else{
				throw new PPMSException("Super Admin can only Enable/Disable account for Organisation Creator");
		}
         if (user.getDefaultTenantId() == 0){
			 long tenantId = sequenceGeneratorService.generateSequence(Organisation.SEQUENCE_NAME);
			 user.getTenantIds().add(tenantId);
			 user.setDefaultTenantId(tenantId);
		 }
         user.setEnabled(true);
		 userRepository.save(user);
		 Person person = new Person();
		 person.setEmail(user.getEmail());
		 person.setFirstName(user.getFirstName());
		 person.setLastName(user.getLastName());
		 personService.addPersonAdminCreator(user.getDefaultTenantId(), person);


	}

	public User updateUser(User user){
		return userRepository.save(user);
	}




	public User activateAccount(long tenantId, LoggedUserInfo userInfo) {
		User user = userRepository.findByEmail(userInfo.getEmail());
		Person person = personService.getPersonByEmail(tenantId, userInfo.getEmail());
		if (person == null){
			throw new PPMSException(ErrorCode.UNKNOW_ERROR_OCCURED, "Person not found");
		}
		if (!user.getTenantIds().contains(tenantId)){
			user.getTenantIds().add(tenantId);
			user.setDefaultTenantId(tenantId);
		}
		user.setEnabled(true);
		if (STAFF.equals(person.getPersonfunction())){
			user.setRoles(Collections.singleton(Role.STAFF));
		}else if (STUDENT.equals(person.getPersonfunction())){
			user.setRoles(Collections.singleton(Role.STUDENT));
		}else if(MODEL_LEADER.equals(person.getPersonfunction())){
			user.setRoles(Collections.singleton(Role.MODULE_LEADER));
		}
		person.setRegistered(true);
		personService.updatePerson(tenantId, person);
		return userRepository.save(user);

	}
	public User linkToAnOrganisation(long tenantId, LoggedUserInfo userInfo) {
		User user = userRepository.findByEmail(userInfo.getEmail());
		if (!user.getTenantIds().contains(tenantId)){
			user.getTenantIds().add(tenantId);
			user.setDefaultTenantId(tenantId);
			user = userRepository.save(user);
		}
		return user;
	}
    
 	void delete(String userId) {
		userRepository.deleteById(userId);;
	}




	public Page<User> getPagedUsers(long tenantId, int page, int size, Boolean isCreator, Boolean enabled, String name) {
		Pageable pageableRequest =  PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "email"));;
		Page<User> users = null;
		//if (StringUtils.isEmpty(name)){
		//	users = userRepository.findByTenantIdAndPersonFunctionAndStatus(tenantId, function, status, pageableRequest);
		//}else{
		   Long tenantIdLong = tenantId == 0 ? null : tenantId;

				   users = userRepository.findByAllCriteria(tenantIdLong, isCreator, enabled, pageableRequest);
		return users;
	}
}


