package com.advancedit.ppms;

import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.repositories.UserRepository;
import com.advancedit.ppms.repositories.VerificationTokenRepository;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

import com.advancedit.ppms.controllers.AuthBody;
import com.advancedit.ppms.models.organisation.Address;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.GoalStatus;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectStatus;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.service.ProjectService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FullIntegrationTest {

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private UserRepository userRepository;

	 @LocalServerPort
	 private int port;
	 
	 TestRestTemplate restTemplate = new TestRestTemplate();
	    HttpHeaders headers = new HttpHeaders();

	 
	@Before
	public void init() {
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
	
	}
	 
	@After
	public void finalize() {
	
	}
	
	@Test
	public void fullTest() throws Exception {
		//Register User
		User accountCreatorUserInput = registerUser(1, true);
		User accountCreatorUser = userRepository.findByEmail(accountCreatorUserInput.getEmail());

		//Validate Email
		UUID token = verificationTokenRepository.findByUserId(accountCreatorUser.getId()).getToken();
		validateEmail(token);

		//Super Admin Account Validation
		//Login
		String superAdminSecurityToken = login("asamet", "toutou");
		activateAccount(superAdminSecurityToken, accountCreatorUser.getId());

		//Login with User
		String adminCreatorSecurityToken = login(accountCreatorUser.getUsername(), accountCreatorUserInput.getPassword());

		//Organisation
		createOrganisation(adminCreatorSecurityToken);

		//Person
		Person person = aPerson(1);
		person.setPersonfunction(PersonFunction.TEACHER);
		String personId = addPerson(adminCreatorSecurityToken, person);
		assertEquals(person, getPerson(adminCreatorSecurityToken, personId));

		//Register User
		User user = registerUser(2, false);


		//login
		//Validate Email
		 token = verificationTokenRepository.findByUserId(accountCreatorUser.getId()).getToken();
		validateEmail(token);

		//Super Admin Account Validation
		//Login
		String userSecurityToken = login(user.getUsername(), user.getPassword());
		Project project = aProject(1);
			String projectId = addPProject(userSecurityToken, project);
		assertEquals(null, getProject(userSecurityToken, projectId));




	}

	private Organisation createOrganisation(String adminCreatorSecurityToken){

		Organisation organisation = anOrganisation("test");
		String organisationId = addOrganisation(adminCreatorSecurityToken, organisation);
		assertEquals(organisation, getOrganisation(adminCreatorSecurityToken, organisationId));

		//Department
		Department department = aDepartment("chemistry");
		String departmentId = addDepartment(adminCreatorSecurityToken, organisationId, department);
		assertEquals(department, getDepartment(adminCreatorSecurityToken, organisationId, departmentId));


		//Sector
		Sector sector = aSector("zeolite");
		String sectorId = addSector(adminCreatorSecurityToken, organisationId, departmentId, sector);
		assertEquals(sector, getSector(adminCreatorSecurityToken, organisationId, departmentId, sectorId));

		return organisation;
	}
	public String addDepartment(String securityToken, String organisationId, Department department) {
		HttpEntity<Department> entity1 = new HttpEntity<>(department, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/api/organisations/"+ organisationId+"/departments"), HttpMethod.POST, entity1, String.class);

		HttpStatus status1 =      response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public Department getDepartment(String token, String organisationId, String departmentId) {
		HttpEntity<String> entity1 = new HttpEntity<>(null, getHeadersWithSecurityToken(token));
		ResponseEntity<Department> response1 = restTemplate.exchange(
				createURLWithPort("/api/organisations/"+ organisationId+"/departments/"+departmentId), HttpMethod.GET, entity1, Department.class);

		HttpStatus status1 =      response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public String addSector(String securityToken, String organisationId, String departmentId, Sector sector) {
		HttpEntity<Sector> entity1 = new HttpEntity<>(sector, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/api/organisations/"+ organisationId+"/departments/"+ departmentId+"/sectors"), HttpMethod.POST, entity1, String.class);

		HttpStatus status1 =      response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public Sector getSector(String token , String organisationId, String departmentId, String sectorId) {
		HttpEntity<String> entity1 = new HttpEntity<>(null, getHeadersWithSecurityToken(token));
		ResponseEntity<Sector> response1 = restTemplate.exchange(
				createURLWithPort("/api/organisations/"+ organisationId+"/departments/"+departmentId + "/sectors/"+sectorId),
				HttpMethod.GET,
				entity1, Sector.class);

		HttpStatus status1 =      response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}


	public String addOrganisation(String securityToken, Organisation organisation) {
		HttpEntity<Organisation> entity1 = new HttpEntity<>(organisation, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/api/organisations"), HttpMethod.POST, entity1, String.class);
		HttpStatus status1 = response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public Organisation getOrganisation(String token, String organisationId) {
		HttpEntity<String> entity = new HttpEntity<String>(null, getHeadersWithSecurityToken(token));
		ResponseEntity<Organisation> response = restTemplate.exchange(
				createURLWithPort("/api/organisations/"+ organisationId), HttpMethod.GET, entity, Organisation.class);
		HttpStatus status1 = response.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response.getBody();
	}

	public Project getProject(String token, String projectId) {
		HttpEntity<String> entity = new HttpEntity<String>(null, getHeadersWithSecurityToken(token));
		ResponseEntity<Project> response = restTemplate.exchange(
				createURLWithPort("/api/projects/"+ projectId), HttpMethod.GET, entity, Project.class);
		HttpStatus status1 = response.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response.getBody();
	}

	public String addPProject(String securityToken, Project project) {
		HttpEntity<Project> entity1 = new HttpEntity<>(project, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/api/projects"), HttpMethod.POST, entity1, String.class);
		HttpStatus status1 = response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public String addPerson(String securityToken, Person person) {
		HttpEntity<Person> entity1 = new HttpEntity<>(person, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/api/persons"), HttpMethod.POST, entity1, String.class);
		HttpStatus status1 = response1.getStatusCode();
		Assert.assertEquals(status1, HttpStatus.OK);
		return response1.getBody();
	}

	public Person getPerson(String token, String personId) {
		HttpEntity<String> entity = new HttpEntity<String>(null, getHeadersWithSecurityToken(token));
		ResponseEntity<Person> response = restTemplate.exchange(
				createURLWithPort("/api/persons/"+ personId), HttpMethod.GET, entity, Person.class);
		return response.getBody();
	}



	public User registerUser(int index, boolean isCreator) throws Exception {
	    String email = "test"+index+"@gmail.com";
	    Set<Role> roles = new HashSet<>();
	    roles.add(Role.ADMIN_CREATOR);
		User user = new User();
				user.setEmail(email);
				user.setUsername("userName" + index);
				user.setPassword("password");
				user.setOrganisationCreationRequest(isCreator);
				user.setMessage("mmmmmmmm");
		//user
		  HttpEntity<User> entity = new HttpEntity<User>(user, getHeaders());
	        ResponseEntity<Void> response = restTemplate.exchange(
	          createURLWithPort("/api/auth/register"), HttpMethod.POST, entity, Void.class);
	   
	        HttpStatus status =      response.getStatusCode();
	        Assert.assertEquals(status, HttpStatus.NO_CONTENT);
	        
	      return user;
	          
		
	}

	public void validateEmail(UUID token) throws Exception {

		//user
		HttpEntity<String> entity = new HttpEntity<>(null, getHeaders());
		ResponseEntity<Void> response = restTemplate.exchange(
				createURLWithPort("/api/auth/validate?token=" + token.toString()), HttpMethod.GET, entity, Void.class);

		HttpStatus status =      response.getStatusCode();
		Assert.assertEquals(status, HttpStatus.NO_CONTENT);
	}

	public void activateAccount(String securityToken, String userId) throws Exception {



		HttpEntity<String> entity = new HttpEntity<>(null, getHeadersWithSecurityToken(securityToken));
		ResponseEntity<Void> response = restTemplate.exchange(
				createURLWithPort("/api/auth/activate/" + userId), HttpMethod.GET, entity, Void.class);

		HttpStatus status =      response.getStatusCode();
		Assert.assertEquals(status, HttpStatus.NO_CONTENT);
	}


	public String createNewOrganisation(String token) throws Exception {
		
		
	    Address address = new Address();
	    address.setAddress("Rue rooddde 13");
	    address.setCity("Brussels");
	    address.setZipCode("1030");
	    address.setCountry("BE");
    	Organisation organisation = new Organisation();
    	organisation.setName("nameOrg");
    	organisation.setEmail("organisationemail.gmail.com");
    	organisation.setDescription("blabla");
    	organisation.setAddress(address);
				
	
		  HttpEntity<Organisation> entity1 = new HttpEntity<>(organisation, getHeaders());
	        ResponseEntity<String> response1 = restTemplate.exchange(
	          createURLWithPort("/api/organisations"), HttpMethod.POST, entity1, String.class);
	   
	        HttpStatus status1 =      response1.getStatusCode();
	        Assert.assertEquals(status1, HttpStatus.OK);
	        String organisationId = response1.getBody();
	        Assert.assertNotNull(organisationId);	
	        
	        
	        HttpEntity<String> entity = new HttpEntity<String>(null, getHeaders());
	        ResponseEntity<Organisation> response = restTemplate.exchange(
	          createURLWithPort("/api/organisations/"+ organisationId), HttpMethod.GET, entity, Organisation.class);
	   
	        Organisation savedOrganisation =      response.getBody();
	        
	        Assert.assertEquals(organisation.getEmail(), savedOrganisation.getEmail());
	        Assert.assertEquals(organisation.getName(), savedOrganisation.getName());
	        Assert.assertEquals(organisation.getDescription(), savedOrganisation.getDescription());
	        Assert.assertEquals(organisation.getAddress().getCity(), savedOrganisation.getAddress().getCity());
		       
	        Assert.assertEquals(organisation.getAddress().getAddress(), savedOrganisation.getAddress().getAddress());
			  
	        
	        return organisationId;
		
	}
	
	
	public String login(String username, String password) throws Exception {
	

		AuthBody authBody = new AuthBody();
		authBody.setUsername(username);
		authBody.setPassword(password);
				
		//user
		  HttpEntity<AuthBody> entity1 = new HttpEntity<AuthBody>(authBody, getHeaders());
	        ResponseEntity<String> response1 = restTemplate.exchange(
	          createURLWithPort("/api/auth/login"), HttpMethod.POST, entity1, String.class);
	   
	        HttpStatus status1 =      response1.getStatusCode();
	        Assert.assertEquals(status1, HttpStatus.OK);
	        String token = response1.getBody();
	        Assert.assertNotNull(token);
	        
	        return token;
		
	}



	
	@Test
	public void getPersonList() throws Exception {
	
		  HttpEntity<String> entity = new HttpEntity<String>(null, getHeaders());
	        ResponseEntity<Person[]> response = restTemplate.exchange(
	          createURLWithPort("/api/persons"), HttpMethod.GET, entity, Person[].class);
	   
	        Person[] data =      response.getBody();
	   
	  int size= data.length;
	        /*	mvc.perform(get("/api/persons")
		      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(jsonPath("$[0].name", is("bob")));*/
		
	}
	
	
	 private String createURLWithPort(String uri) {
	        return "http://localhost:" + port + uri;
	 }

	 private Organisation anOrganisation(String name){
		 Address address = new Address();
		 address.setAddress("Rue rooddde 13");
		 address.setCity("Brussels");
		 address.setZipCode("1030");
		 address.setCountry("BE");
		 Organisation organisation = new Organisation();
		 organisation.setName("nameOrg");
		 organisation.setEmail("organisationemail.gmail.com");
		 organisation.setDescription("blabla");
		 organisation.setAddress(address);
		 return organisation;
	 }

	private Person aPerson(int index){
			Person person = new Person();
		person.setFirstName("name" + index);
		person.setEmail("name"+ index+".gmail.com");
		person.setLastName("last name" + index);
		person.setPersonfunction(PersonFunction.STUDENT);
		return person;
	}

   private Project aProject(int index){
	   Project p = new Project();
	   p.setName("Project "+index);
	   p.setStatus(ProjectStatus.NEW);
	   p.setDescription("Delkdslkfslfkslm");

	   return p;
   }

	private Department aDepartment(String name){
		Department department = new Department();
		department.setName(name);
		department.setDescription("deprtement description");
		return department;
	}

	private Sector aSector(String name){
		Sector sector = new Sector();
		sector.setName(name);
		sector.setDescription("sector description");
		return sector;
	}

	 void assertEquals(Organisation expected, Organisation saved){
		 Assert.assertEquals(expected.getEmail(), saved.getEmail());
		 Assert.assertEquals(expected.getName(), saved.getName());
		 Assert.assertEquals(expected.getDescription(), saved.getDescription());
		 Assert.assertEquals(expected.getAddress().getCity(), saved.getAddress().getCity());
		 Assert.assertEquals(expected.getAddress().getAddress(), saved.getAddress().getAddress());
	 }

	void assertEquals(Person expected, Person saved){
		Assert.assertEquals(expected.getEmail(), saved.getEmail());
		Assert.assertEquals(expected.getFirstName(), saved.getFirstName());
		Assert.assertEquals(expected.getLastName(), saved.getLastName());
		Assert.assertEquals(expected.getPersonfunction(), saved.getPersonfunction());
	}

	void assertEquals(Project expected, Project saved){
		Assert.assertEquals(expected.getName(), saved.getName());
		Assert.assertEquals(expected.getStatus(), saved.getStatus());
		Assert.assertEquals(expected.getDescription(), saved.getDescription());
	}

	void assertEquals(Department expected, Department saved){
		Assert.assertEquals(expected.getName(), saved.getName());
		Assert.assertEquals(expected.getDescription(), saved.getDescription());
	}

	void assertEquals(Sector expected, Sector saved){
		Assert.assertEquals(expected.getName(), saved.getName());
		Assert.assertEquals(expected.getDescription(), saved.getDescription());
	}

	private HttpHeaders getHeaders(){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
		return headers;
	}

	private HttpHeaders getHeadersWithSecurityToken(String token){
		HttpHeaders headers = getHeaders();
		headers.setBearerAuth(token);
		return headers;
	}

	
}

