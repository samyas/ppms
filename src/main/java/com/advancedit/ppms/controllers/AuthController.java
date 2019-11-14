package com.advancedit.ppms.controllers;

import com.advancedit.ppms.configs.JwtTokenProvider;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.UserRepository;
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

import static com.advancedit.ppms.models.user.Role.SUPER_ADMIN;
import static com.advancedit.ppms.utils.SecurityUtils.hasRole;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	private UserService userService;

	@RequestMapping(method= RequestMethod.POST, value="/login")
	public String login(@RequestBody AuthBody credentials) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
			User user = userService.getUserByUsername(credentials.getUsername());
			return  jwtTokenProvider.createToken(credentials.getUsername(), user.getRoles());
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Invalid email/password supplied");
		}
	}

	@RequestMapping(method= RequestMethod.POST, value="/register")
	public ResponseEntity register(@RequestBody User user) {
		userService.register(user);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method= RequestMethod.GET, value="/validate")
	public ResponseEntity validateToken(@RequestParam("token") String token) {
		userService.validateToken(token);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method= RequestMethod.GET, value="/activate/{userId}")
	public ResponseEntity activateAccount(@PathVariable String userId) {
		hasRole(SUPER_ADMIN);
		userService.activate(userId);
		return ResponseEntity.noContent().build();
	}


	@RequestMapping(method= RequestMethod.GET, value="/link")
	public ResponseEntity linkToOrganisation(@RequestParam("organisation") long tenantId) {
		userService.linkToAnOrganisation(tenantId, SecurityUtils.getLoggedUserInfo());
		return ResponseEntity.noContent().build();
	}

}
