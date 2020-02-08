package com.advancedit.ppms.controllers;

import com.advancedit.ppms.configs.JwtTokenProvider;
import com.advancedit.ppms.controllers.beans.AuthResponseBean;
import com.advancedit.ppms.controllers.beans.AuthUserBean;
import com.advancedit.ppms.controllers.beans.OrganisationShortBean;
import com.advancedit.ppms.controllers.beans.RegisterUserBean;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.UserRepository;
import com.advancedit.ppms.service.OrganisationService;
import com.advancedit.ppms.service.UserService;
import com.advancedit.ppms.services.CustomUserDetailsService;
import com.advancedit.ppms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.advancedit.ppms.models.user.Role.ADMIN_CREATOR;
import static com.advancedit.ppms.models.user.Role.SUPER_ADMIN;
import static com.advancedit.ppms.utils.SecurityUtils.*;

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
	OrganisationService organisationService;

	@RequestMapping(method= RequestMethod.POST, value="/login")
	public AuthResponseBean login(@RequestBody AuthBody credentials) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
			User user = userService.getUserByUsername(credentials.getUsername());
			if (!user.isEmailIsValid()) throw new PPMSException("Your email is not validated, please check your mailbox");
			//if (!user.isEnabled()) throw new PPMSException("Your account is not enabled yet by administrator");
			String token = jwtTokenProvider.createToken(credentials.getUsername(), user.getRoles(), user.getDefaultTenantId());
			AuthResponseBean authResponseBean = new AuthResponseBean();
			authResponseBean.setToken(token);
			authResponseBean.setNeedToSelect(user.getDefaultTenantId() == 0);
			if (user.getDefaultTenantId() == 0 && Boolean.TRUE.equals(user.getOrganisationCreationRequest())){
				authResponseBean.setNeedToSelect(false);
				authResponseBean.setEnabled(false);
			}else{
				authResponseBean.setEnabled(user.isEnabled());
			}

			if (user.getDefaultTenantId() != 0) authResponseBean.setNeedToInitOrg(!organisationService.getOrganisationByTenantId(user.getDefaultTenantId()).isPresent());
			return authResponseBean;
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Invalid username/password supplied");
		}
	}

	@RequestMapping(method= RequestMethod.POST, value="/register")
	public ResponseEntity register(@RequestBody RegisterUserBean userBean) {
		User user = new User();
		user.setUsername(userBean.getUsername());
		user.setEmail(userBean.getEmail());
		user.setPassword(userBean.getPassword());
		user.setOrganisationCreationRequest(userBean.getIsCreator());
		user.setMessage(userBean.getMessage());
		user.setFirstName(userBean.getFirstName());
		user.setLastName(userBean.getLastName());
		String userId = userService.register(user).getId();
		return ResponseEntity.ok(userId);
	}

	@RequestMapping(method= RequestMethod.GET, value="/validate")
	public ResponseEntity validateToken(@RequestParam("token") String token) {
		userService.validateToken(token);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method= RequestMethod.GET, value="/activate/{userId}")
	public ResponseEntity activateAccount(@PathVariable String userId) {
		hasAnyRole(SUPER_ADMIN, ADMIN_CREATOR);
		userService.activate(userId, isHasRole(SUPER_ADMIN));
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method=RequestMethod.GET, value="/users/paged")
	public Page<User> getPagedUsers(@RequestParam("page") int page, @RequestParam("size") int size,
									   @RequestParam(value = "creator", required = false) Boolean isCreator, @RequestParam(value = "enabled", required = false)Boolean enabled,
									   @RequestParam(value = "name", required = false)String name) {
		hasAnyRole(SUPER_ADMIN, ADMIN_CREATOR);
		long tenantId = getCurrentTenantId();
		if (isHasRole(SUPER_ADMIN) && Boolean.TRUE.equals(isCreator)){
			tenantId = 0;
		}
		return userService.getPagedUsers(tenantId, page, size, isCreator, enabled, name);
	}

	@RequestMapping(method= RequestMethod.GET, value="/info")
	public User getUserInfo() {
		return userService.getUserByUsername(getLoggedUserInfo().getUsername());
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
		User user = userService.linkToAnOrganisation(id, getLoggedUserInfo());
		String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles(), user.getDefaultTenantId());
		AuthResponseBean authResponseBean = new AuthResponseBean();
		authResponseBean.setToken(token);
		authResponseBean.setNeedToSelect(true);
		authResponseBean.setEnabled(user.isEnabled());
		return authResponseBean;
	}


	@RequestMapping(method= RequestMethod.GET, value="/getToken")
	public String getToken(@RequestParam("userId") String userId) {
		return userService.getEmailToken(userId);
	}
}
