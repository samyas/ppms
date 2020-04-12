package com.advancedit.ppms.controllers;

import com.advancedit.ppms.configs.JwtTokenProvider;
import com.advancedit.ppms.controllers.beans.AuthResponseBean;
import com.advancedit.ppms.controllers.beans.OrganisationShortBean;
import com.advancedit.ppms.controllers.beans.RegisterUserBean;
import com.advancedit.ppms.controllers.beans.ValidationTokenResponseBean;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.models.user.VerificationToken;
import com.advancedit.ppms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.advancedit.ppms.models.person.PersonFunction.*;
import static com.advancedit.ppms.models.user.Role.ADMIN_CREATOR;
import static com.advancedit.ppms.models.user.Role.SUPER_ADMIN;
import static com.advancedit.ppms.utils.SecurityUtils.*;
import static java.lang.Boolean.TRUE;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	private UserService userService;

	@Autowired
	private PersonService personService;

	@Autowired
	OrganisationService organisationService;

	@Autowired
   VerificationTokenService verificationTokenService;

	@Autowired
	EmailService emailService;

	@RequestMapping(method= RequestMethod.POST, value="/login")
	public AuthResponseBean login(@RequestBody AuthBody credentials) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
			User user = userService.getUserByUsername(credentials.getUsername());
			if (!user.isEmailIsValid()) throw new PPMSException("Your email is not validated, please check your mailbox");
			//if (!user.isEnabled()) throw new PPMSException("Your account is not enabled yet by administrator");
			Set<Role> roles = user.getRoles();
			if (user.getDefaultTenantId() != 0) {
				Optional<Role> role = Optional.of(personService.getPersonByEmail(user.getDefaultTenantId(),
						user.getEmail()))
						.map(p -> getRoleFromPersonFunction(user.getDefaultTenantId(), p));
				role.map(roles::add);
			}
			String token = jwtTokenProvider.createToken(user.getEmail(), roles, user.getDefaultTenantId());
			AuthResponseBean authResponseBean = new AuthResponseBean();
			authResponseBean.setToken(token);
			authResponseBean.setEnabled(user.isEnabled());
			if (TRUE.equals(user.getOrganisationCreationRequest())){
				if (user.getDefaultTenantId() != 0) {
					Optional<Organisation> organisation = organisationService.getOrganisationByTenantId(user.getDefaultTenantId());
					authResponseBean.setNeedToInitOrg(!organisation.isPresent());
				}
			} else{
				authResponseBean.setNeedToActivate(user.getDefaultTenantId() == 0);
			}
			return authResponseBean;
		} catch (AuthenticationException e) {
			throw new PPMSException(ErrorCode.INVALID_USERNAME_PASSWORD, "Invalid username/password");
		}
	}

	@RequestMapping(method= RequestMethod.POST, value="/register")
	public ResponseEntity register(@RequestBody RegisterUserBean userBean , HttpServletRequest request) {
		User user = new User();
		if(!userBean.getIsCreator()){
			VerificationToken verificationToken = Optional.ofNullable(userBean.getEmailToken()).map(et ->
					verificationTokenService.validateToken(et))
					.orElseThrow(() -> new PPMSException("Email Token cannot be empty"));
			user.setEmail(verificationToken.getEmail());
			user.getTenantIds().add(verificationToken.getTenantId());
			user.setDefaultTenantId(verificationToken.getTenantId());
		} else {
			user.setEmail(userBean.getEmail());
		}

		user.setUsername(userBean.getUsername());
		user.setPassword(userBean.getPassword());
		user.setOrganisationCreationRequest(userBean.getIsCreator());
		user.setMessage(userBean.getMessage());
		user.setFirstName(userBean.getFirstName());
		user.setLastName(userBean.getLastName());
		String userId = userService.register(user).getId();
		if(userBean.getIsCreator()){
			VerificationToken verificationToken = verificationTokenService.generateValidationEmailToken(0, user.getEmail());
			// Send email to the user
			emailService.sendEmailConfirmation(user, verificationToken, request.getHeader("Origin"));
		}
		return ResponseEntity.ok(userId);
	}


	@RequestMapping(method= RequestMethod.POST, value="/reset-password-request")
	public ResponseEntity resetPassword(@RequestBody String emailOrUsername , HttpServletRequest request) {

		Optional<User> user = Optional.ofNullable(userService.getUserByEmail(emailOrUsername));
		if (!user.isPresent()){
			user = Optional.ofNullable(userService.getUserByUsername(emailOrUsername));
		}
		if (!user.isPresent()){
			throw  new PPMSException(String.format("%s was not found", emailOrUsername));
		}
		VerificationToken verificationToken = verificationTokenService.generateValidationEmailToken(0, user.get().getEmail());
		// Send email to the user
		emailService.sendRestPassword(user.get(), verificationToken, request.getHeader("Origin"));
		return ResponseEntity.noContent().build();
	}

	private Role getRoleFromPersonFunction(long tenantId, Person person){

		if(PersonFunction.ADMIN_CREATOR.equals(person.getPersonfunction())){
			return  Role.ADMIN_CREATOR;
		}

		if (STUDENT.equals(person.getPersonfunction())){
			return  Role.STUDENT;
		}

		if (STAFF.equals(person.getPersonfunction()) || MODEL_LEADER.equals(person.getPersonfunction())){
			boolean isModelLeader = organisationService.getOrganisationByTenantId(tenantId)
					.map(Organisation::getDepartments)
					.flatMap(deps -> deps.stream().filter( d-> d.getId().equals(person.getDepartmentId())).findFirst())
					.flatMap(d -> Optional.ofNullable(d.getResponsible()))
					.map(sp -> sp.getPersonId().equals(person.getId()))
					.orElse(false);
			return isModelLeader   ? Role.MODULE_LEADER:   Role.STAFF ;
		}
		return null;
	}

	@RequestMapping(method= RequestMethod.GET, value="/validate-join-request")
	public ValidationTokenResponseBean validateJoinRequestToken(@RequestParam("token") String emailToken) {
		ValidationTokenResponseBean validationTokenResponseBean = new ValidationTokenResponseBean();
		VerificationToken verificationToken = verificationTokenService.validateToken(emailToken);
		validationTokenResponseBean.setToken(emailToken);
		User user = userService.getUserByEmail(verificationToken.getEmail());
		if (user != null){
			if (user.getTenantIds().contains(verificationToken.getTenantId())){
				user.getTenantIds().add(verificationToken.getTenantId());
				userService.updateUser(user);
				validationTokenResponseBean.setResult(ValidationTokenResponseBean.ValidationTokenResult.LOGIN);
			}
		} else {
			Person person = personService.getPersonByEmail(verificationToken.getTenantId(), verificationToken.getEmail());
			validationTokenResponseBean.setFirstName(person.getFirstName());
			validationTokenResponseBean.setLastName(person.getLastName());
			validationTokenResponseBean.setToken(emailToken);
			validationTokenResponseBean.setEmail(verificationToken.getEmail());
			validationTokenResponseBean.setResult(ValidationTokenResponseBean.ValidationTokenResult.REGISTER);
		}
		return validationTokenResponseBean;
	}

	@RequestMapping(method= RequestMethod.GET, value="/validate-reset-request")
	public ValidationTokenResponseBean validateResetToken(@RequestParam("token") String emailToken) {
		ValidationTokenResponseBean validationTokenResponseBean = new ValidationTokenResponseBean();
		VerificationToken verificationToken = verificationTokenService.validateToken(emailToken);
		validationTokenResponseBean.setToken(emailToken);
		User user = Optional.ofNullable(userService.getUserByEmail(verificationToken.getEmail()))
				.orElseThrow(() -> new IllegalStateException("User not found"));
		validationTokenResponseBean.setUsername(user.getUsername());
		validationTokenResponseBean.setToken(emailToken);
		validationTokenResponseBean.setEmail(verificationToken.getEmail());
		return validationTokenResponseBean;
	}

	@RequestMapping(method= RequestMethod.POST, value="/reset-password")
	public ResponseEntity resetPassword(@RequestBody RegisterUserBean userBean) {
		VerificationToken verificationToken = Optional.ofNullable(userBean.getEmailToken()).map(et ->
				verificationTokenService.validateToken(et))
				.orElseThrow(() -> new PPMSException("Token is empty or invalid"));
		userService.updatePassword(verificationToken.getEmail(), userBean.getPassword());
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method= RequestMethod.GET, value="/validate-creator")
	public ResponseEntity validateToken(@RequestParam("token") String emailToken) {
		ValidationTokenResponseBean validationTokenResponseBean = new ValidationTokenResponseBean();
		VerificationToken verificationToken = verificationTokenService.validateToken(emailToken);
		validationTokenResponseBean.setToken(emailToken);
		User user = Optional.ofNullable(userService.getUserByEmail(verificationToken.getEmail()))
				.orElseThrow(() ->  new PPMSException("User not found with email " + verificationToken.getEmail()));
		user.setEmailIsValid(true);
		userService.updateUser(user);
		//send email to super users
		//emailService.sendJoinRequestForPerson();
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method= RequestMethod.GET, value="/activate/{userId}")
	public ResponseEntity activateAdminCreatorAccount(@PathVariable String userId) {
		hasAnyRole(SUPER_ADMIN);
		userService.activateAdminCreatorAccount(userId);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method=RequestMethod.GET, value="/users/paged")
	public Page<User> getPagedUsers(@RequestParam("page") int page, @RequestParam("size") int size,
									   @RequestParam(value = "creator", required = false) Boolean isCreator, @RequestParam(value = "enabled", required = false)Boolean enabled,
									   @RequestParam(value = "name", required = false)String name) {
		hasAnyRole(SUPER_ADMIN, ADMIN_CREATOR);
		long tenantId = getCurrentTenantId();
		if (isHasRole(SUPER_ADMIN) && TRUE.equals(isCreator)){
			tenantId = 0;
		}
		return userService.getPagedUsers(tenantId, page, size, isCreator, enabled, name);
	}

	@RequestMapping(method= RequestMethod.GET, value="/info")
	public User getUserInfo() {
		return userService.getUserByEmail(getLoggedUserInfo().getEmail());
	}

	@RequestMapping(method=RequestMethod.GET, value="/organisations")
	public List<OrganisationShortBean> organisations() {
		List<Organisation> organisations = organisationService.getAllOrganisations();
		return organisations.stream().map(organisation -> {
			OrganisationShortBean bean = new OrganisationShortBean();
			bean.setName(organisation.getName());
			bean.setTenantId(organisation.getTenantId());
			return bean;
		}).collect(Collectors.toList());
	}

	@RequestMapping(method= RequestMethod.GET, value="/link")
	public AuthResponseBean linkToOrganisation(@RequestParam("id") long id) {
		hasAnyRole(SUPER_ADMIN);
		User user = userService.linkToAnOrganisation(id, getLoggedUserInfo());
		String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles(), user.getDefaultTenantId());
		AuthResponseBean authResponseBean = new AuthResponseBean();
		authResponseBean.setToken(token);
		//authResponseBean.setNeedToSelect(true);
		authResponseBean.setEnabled(user.isEnabled());
		return authResponseBean;
	}

/*	@RequestMapping(method= RequestMethod.GET, value="/activate-account")
	public AuthResponseBean activateAccount(@RequestParam("code") String code) {
		LoggedUserInfo loggedUserInfo = getLoggedUserInfo();
		VerificationToken activationToken = verificationTokenService.validateToken(loggedUserInfo.getEmail(), code)
				.orElseThrow(() -> new PPMSException(ErrorCode.ACTIVATION_TOKEN_EXPIRED, "Invalid code"));

		User user = userService.activateAccount(activationToken.getTenantId(), getLoggedUserInfo());
		String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles(), user.getDefaultTenantId());
		AuthResponseBean authResponseBean = new AuthResponseBean();
		authResponseBean.setToken(token);
		authResponseBean.setEnabled(user.isEnabled());
		return authResponseBean;
	}*/



/*	@RequestMapping(method= RequestMethod.GET, value="/getActivationToken")
	public String getActivationToken() {
		return activationTokenService.getActivationToken(getLoggedUserInfo().getEmail());
	}*/

}
