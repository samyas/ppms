package com.advancedit.ppms.configs;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		UserDetailsService userDetailsService = mongoUserDetails();
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().httpBasic().disable().csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/auth/login").permitAll()
				.antMatchers("/api/auth/register").permitAll()
				.antMatchers("/api/auth/validate").permitAll()
				.antMatchers("/api/auth/validate-creator").permitAll()
				.antMatchers("/api/auth/validate-join-request").permitAll()
				.antMatchers("/api/auth/getToken").permitAll()
				.antMatchers("/api/auth/reset-password-request").permitAll()
				.antMatchers("/api/auth/validate-reset-request").permitAll()
				.antMatchers("/api/auth/reset-password").permitAll()


				//	.antMatchers("/api/projects/**").hasAnyAuthority(Role.ADMIN.name(), Role.STAFF.name(), Role.STUDENT.name()).anyRequest().authenticated()
			//	.antMatchers("/api/persons/**").hasAnyAuthority(Role.ADMIN.name(), Role.STAFF.name(), Role.STUDENT.name()).anyRequest().authenticated()
			//	.antMatchers("/api/users/**").hasAnyAuthority(Role.ADMIN.name(), Role.STAFF.name(), Role.STUDENT.name()).anyRequest().authenticated()

				.antMatchers("/api/**").fullyAuthenticated()
				.and().csrf().disable()
				//.apply(new JwtConfigurer(jwtTokenProvider))
			//	.exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint()).and()
				.apply(new JwtConfigurer(jwtTokenProvider));
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Unauthorized");
	}

	@Bean
	public UserDetailsService mongoUserDetails() {
		return new CustomUserDetailsService();
	}
}
