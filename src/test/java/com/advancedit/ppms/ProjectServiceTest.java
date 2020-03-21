
package com.advancedit.ppms;

import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.service.DocumentManagementService;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.advancedit.ppms.service.ProjectService;
import org.thymeleaf.messageresolver.IMessageResolver;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectServiceTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private DocumentManagementService documentManagementService;
	
	 private List<String> ids;

	 private long tenantId = 10;
	@Before
	public void init() {
		ids = new ArrayList<>();
	//	projectService.deleteAll();
	}

	private Path createFile() throws IOException {
		Charset utf8 = StandardCharsets.UTF_8;
		List<String> list = Arrays.asList("Line 1", "Line 2");


			// If the file doesn't exists, create and write to it
			// If the file exists, truncate (remove all content) and write to it
			return Files.write(Paths.get("app.txt"), list, utf8);

	}

	/*private void readFile(){
		// Read
		try {
			byte[] content = Files.readAllBytes(Paths.get("app.log"));
			System.out.println(new String(content));

			// for binary
			//System.out.println(Arrays.toString(content));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	@Test
	public void uploadToAmazon() throws IOException {
		Path path = Paths.get("app 4 lol.txt");
		System.out.println("Path:" + path);
		String key = documentManagementService.uploadFile2(path.toFile());
		System.out.println("key:" + key);
	}

	@Test
	public void download() throws IOException {
	//	Path path = Paths.get("app 4 lol.txt");
	//	System.out.println("Path:" + path);
		byte[] data = documentManagementService.downloadFile("ORG 02 test/app3.txt");
		Files.write(Paths.get("app_6.txt"), data);
	}

	@Test
	public void deleteFile() throws IOException {
		//	Path path = Paths.get("app 4 lol.txt");
		//	System.out.println("Path:" + path);
		documentManagementService.deleteFile("ORG 02 test/app3.txt");

	}

	@After
	public void finalize() {
		if (ids.isEmpty()){
		//	ids.forEach(id -> projectService.deleteProject(id));
		}
	    ids.clear();
	}
	
	
/*
	@Test
	public void getPagedSummury() {
	//	addProjects();
		
		
		Page<Project> projects = projectService.getPagedListProject(2, 0, 10,
                "5e52a1d3a59ac9402d76a50c",
                //"7878787",
                null, null);
		assertNotNull(projects);
		assertNotNull(projects.getContent());
		assertEquals(projects.getContent().size(), 0);
	
	//	assertEquals(p.getName(), projects.getContent().get(0).getName());
	//	assertEquals(p.getStatus(), projects.getContent().get(0).getStatus());
	//	assertNotNull(projects.getContent().get(0).getGoals());
	//	assertEquals(projects.getContent().get(0).getGoals().size(),  0);
		
		
	}
	


	@Test
	public void saveProjects() {
		
		Project p = new Project();
		p.setName("Project 1");
		p.setStatus(ProjectStatus.NEW.name());
		
		Goal goal = new Goal();
		goal.setGoalId(new ObjectId().toHexString());
		goal.setName("Goal 1");
		goal.setStatus(GoalStatus.NEW);
		goal.setDescription("desciption");
		
		p.getGoals().add(goal);
		
		Project saved = projectService.addProject(23, p);
		
		ids.add(p.getProjectId());
		assertNotNull(saved.getProjectId());
		assertEquals(p.getName(), saved.getName());
		assertEquals(p.getStatus(), saved.getStatus());
		assertEquals(p.getGoals().size(), 1);
		assertEquals(saved.getGoals().get(0).getName(), goal.getName());
		assertEquals(saved.getGoals().get(0).getStatus(), goal.getStatus());
		assertEquals(saved.getGoals().get(0).getName(), goal.getName());
		assertNotNull(saved.getGoals().get(0).getGoalId());
		
		
		Page<Project> projects = projectService.getPagedListProject(23, 100, null, null);
		assertNotNull(projects);
		assertNotNull(projects.getContent());
		assertEquals(projects.getContent().size(), 1);
	
		assertEquals(p.getName(), projects.getContent().get(0).getName());
		assertEquals(p.getStatus(), projects.getContent().get(0).getStatus());
		assertNotNull(projects.getContent().get(0).getGoals());
		
		assertEquals(projects.getContent().get(0).getGoals().size(),  0);
		
		
	}

	@Test
	public void addGoal() {
		
		Project p = new Project();
		p.setName("Project 1");
		p.setStatus(ProjectStatus.NEW);
		
		
		Project saved = projectService.addProject(p);
		
		ids.add(p.getProjectId());
		assertNotNull(saved.getProjectId());
		assertEquals(p.getName(), saved.getName());
		assertEquals(p.getStatus(), saved.getStatus());
		
		
		{
		
		Goal goal = new Goal();
	
		goal.setName("Goal 1");
		goal.setStatus(GoalStatus.NEW);
		goal.setDescription("desciption");
		
		
		
		String goalId = projectService.addGoal(saved.getProjectId(), goal);
		
		
		
		Goal savedGoal = projectService.getGoal(saved.getProjectId(), goalId);
		//assertEquals(p.getGoals().size(), 1);
		assertEquals(savedGoal.getName(), goal.getName());
		assertEquals(savedGoal.getStatus(), goal.getStatus());
		assertEquals(savedGoal.getName(), goal.getName());
		
		}
		
		{
		
		Goal goal2 = new Goal();
		
		goal2.setName("Goal 2");
		goal2.setStatus(GoalStatus.NEW);
		goal2.setDescription("desciption2");
		
		String goalId2 = projectService.addGoal(saved.getProjectId(), goal2);
		
		
	
		//assertNotNull(saved.getGoals().get(0).getGoalId());
		
		Goal savedGoal2 = projectService.getGoal(saved.getProjectId(), goalId2);
		//assertEquals(p.getGoals().size(), 1);
		assertEquals(savedGoal2.getName(), goal2.getName());
		assertEquals(savedGoal2.getStatus(), goal2.getStatus());
		assertEquals(savedGoal2.getName(), goal2.getName());
		}
		
	}*/





	@Test
	public void addTask() {
		
		Project p = new Project();
		p.setName("Project 1");
		p.setStatus(ProjectStatus.NEW.name());
		
		
		Project saved = projectService.addProject(tenantId, p);

		ids.add(p.getProjectId());
		assertNotNull(saved.getProjectId());
		assertEquals(p.getName(), saved.getName());
		assertEquals(p.getStatus(), saved.getStatus());

		projectService.addAttachment(tenantId, saved.getProjectId(), new FileDescriptor("test.txt", "ORG-01/test.txt", "txt", "txt"));
		projectService.addAttachment(tenantId, saved.getProjectId(), new FileDescriptor("test2.txt", "ORG-01/test2.txt","txt", "txt"));

		Project saved1 = projectService.getProjectsById(tenantId, saved.getProjectId());
		assertEquals(saved1.getAttachments().size(), 2);

		projectService.deleteAttachment(tenantId, saved.getProjectId(), "ORG-01/test.txt");

		Project saved2 = projectService.getProjectsById(tenantId, saved.getProjectId());
		assertEquals(saved2.getAttachments().size(), 1);

		{
		
		Goal goal = new Goal();
	
		goal.setName("Goal 1");
		goal.setStatus(GoalStatus.NEW);
		goal.setDescription("desciption");
		
		
		
		String goalId = projectService.addGoal(tenantId, saved.getProjectId(), goal);
		
		
		
		Goal savedGoal = projectService.getGoal(tenantId, saved.getProjectId(), goalId);
		//assertEquals(p.getGoals().size(), 1);
		assertEquals(savedGoal.getName(), goal.getName());
		assertEquals(savedGoal.getStatus(), goal.getStatus());
		assertEquals(savedGoal.getName(), goal.getName());
		
		{
		Task task = new Task();
		task.setName("Task 1");
		task.setDescription("Desciption 1");
		
		String taskId = projectService.addNewTask(tenantId, saved.getProjectId(), goalId, task);
		
		 savedGoal = projectService.getGoal(tenantId, saved.getProjectId(), goalId);
			assertEquals(savedGoal.getTasks().size(), 1);
			assertEquals(savedGoal.getTasks().get(0).getName(), task.getName());
			assertEquals(savedGoal.getTasks().get(0).getDescription(), task.getDescription());
			
			
		Task  savedTask = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
				//assertEquals(savedGoal.getTasks().size(), 1);
				assertEquals(savedTask.getName(), task.getName());
				assertEquals(savedTask.getDescription(), task.getDescription());
			
		}
		
		
		{
		Task task = new Task();
		task.setName("Task 2");
		task.setDescription("Desciption 2");
		task.setStatus("OPEN");
		
		String taskId = projectService.addNewTask(tenantId, saved.getProjectId(), goalId, task);

			Assignment assignment = new Assignment();
			assignment.setAction(Assignment.Action.ADD);
			assignment.setPersonId("personId");
			assignment.setPosition(Assignment.Position.TEAM);

		Task  savedTask = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
				//assertEquals(savedGoal.getTasks().size(), 1);
				assertEquals(savedTask.getName(), task.getName());
				assertEquals(savedTask.getDescription(), task.getDescription());
			assertEquals(savedTask.getStatus(), task.getStatus());


			projectService.updateTaskStatus(tenantId, saved.getProjectId(), goalId, taskId, "PROGRESS" );
			projectService.assignTask(tenantId, saved.getProjectId(), goalId, taskId, assignment);

			Task  updatedTask = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
			assertEquals(updatedTask.getStatus(), "PROGRESS");
			assertEquals(updatedTask.getAssignedTo().get(0).getPersonId(), "1");

			projectService.addAttachment(tenantId,  saved.getProjectId(), goalId, taskId,
                    new FileDescriptor("test.txt", "ORG-01/test.txt", "","txt"));
			projectService.addAttachment(tenantId,  saved.getProjectId(), goalId, taskId,
					new FileDescriptor("test2.txt", "ORG-01/test2.txt","", "txt"));


			Task  getTask = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
            assertEquals(getTask.getAttachmentList().size(), 2);
			assertEquals(getTask.getAttachmentList().get(0).getFileName(), "test.txt");
			assertEquals(getTask.getAttachmentList().get(1).getFileName(), "test2.txt");


			projectService.deleteAttachment(tenantId,  saved.getProjectId(), goalId, taskId, "ORG-01/test.txt");

			Task  getTask2 = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
			assertEquals(getTask2.getAttachmentList().size(), 1);
			assertEquals(getTask2.getAttachmentList().get(0).getFileName(), "test2.txt");


			Message m1 = new Message();
			m1.setContent("m1");

			 projectService.addMessage(tenantId, saved.getProjectId(), goalId, taskId, m1);

			Message m2 = new Message();
			m2.setContent("m2");
			projectService.addMessage(tenantId, saved.getProjectId(), goalId, taskId, m2);
			Task  getTaskWithMessage = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);

			assertEquals(getTaskWithMessage.getMessages().size(), 2);
			assertEquals(getTaskWithMessage.getMessages().get(0).getContent(), m1.getContent());
			assertEquals(getTaskWithMessage.getMessages().get(1).getContent(), m2.getContent());

			Message savedMessage = getTaskWithMessage.getMessages().get(0);
			savedMessage.setContent("m1-modified");

			projectService.updateMessage(tenantId, saved.getProjectId(), goalId, taskId, savedMessage.getMessageId(), savedMessage);
			Task  getTaskWithMessage11 = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
			assertEquals(getTaskWithMessage11.getMessages().get(0).getContent(), savedMessage.getContent());

			projectService.deleteAttachment(tenantId,  saved.getProjectId(), goalId, taskId, "ORG-01/test2.txt");
			Task  getTask3 = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);

			assertEquals(getTask3.getAttachmentList().size(), 0);

			projectService.addAttachment(tenantId,  saved.getProjectId(), goalId, taskId,
					new FileDescriptor("test.txt", "ORG-01/test.txt", "", "txt"));
			projectService.addAttachment(tenantId,  saved.getProjectId(), goalId, taskId,
					new FileDescriptor("test2.txt", "ORG-01/test2.txt", "", "txt"));


			Task  getTask4 = projectService.getTask(tenantId, saved.getProjectId(), goalId, taskId);
			assertEquals(getTask4.getAttachmentList().size(), 2);
			assertEquals(getTask4.getAttachmentList().get(0).getFileName(), "test.txt");
			assertEquals(getTask4.getAttachmentList().get(1).getFileName(), "test2.txt");


		}


	}
		

		
	}

	
	public void addProjects() {
		
		for(int k = 0; k < 8; k++){
			Project p = new Project();
			p.setName("Project " + k);
			p.setStatus(ProjectStatus.NEW.name());
			Project saved = projectService.addProject(tenantId, p);
			ids.add(p.getProjectId());
			for(int i = 0; i < 10; i++)
			{
				Goal goal = new Goal();
				goal.setName("Goal " + i);
				goal.setStatus(( i % 2 == 0 )? GoalStatus.NEW: GoalStatus.PROGRESS);
				goal.setDescription("desciption " + i);
				String goalId = projectService.addGoal(tenantId, saved.getProjectId(), goal);
				for(int j = 0; j < 5; j++){
					Task task = new Task();
					task.setName("Task " + i + "-" + j);
					task.setDescription("Desciption " + i + "-" + j);
					projectService.addNewTask(tenantId, saved.getProjectId(), goalId, task);
				}
			}
		}

		

		
	}

}