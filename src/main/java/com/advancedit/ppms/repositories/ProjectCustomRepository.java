package com.advancedit.ppms.repositories;

import java.util.List;
import java.util.Optional;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectCustomRepository {

	Page<ProjectSummary> getPagedProjectSummary(long tenantId, int page, int size, Pageable pageable);

	Page<Project> findByAll(long tenantId, String departmentId, ProjectStatus status, Pageable pageable);

    Page<Project> findWithGoalByAll(long tenantId, String departmentId, ProjectStatus status, Pageable pageable);

    List<Project> findAllByPersonId(long tenantId, String key, String personId, String departmentId, ProjectStatus status);

    boolean existByProjectIdAndTenantId(String projectId, long tenantId);

    boolean updateProjectStatus(long tenantId, String projectId, ProjectStatus status);

    String updateProjectNameAndDescriptionAndKeywords(long tenantId, String projectId, Project project);

    String getDepartmentId(long tenantId, String projectId);

    Project getProjectWithoutGoals(long tenantId, String projectId);

    ProjectStatus getProjectStatus(long tenantId, String projectId);

     FileDescriptor getProjectImage(long tenantId, String projectId);

    boolean assignPerson(long tenantId, String projectId, String key, Member member);

    boolean unAssignPerson(long tenantId, String projectId, String key, String personId);

    boolean sign(long tenantId, String projectId, String attribute, String personId);

    void addAttachment(long tenantId, String projectId, FileDescriptor fileDescriptor);

    void updateImage(long tenantId,  String projectId,  FileDescriptor fileDescriptor);

    void deleteAttachment(long tenantId, String projectId, String fileName);

    /************************** GOALS OPS************************************/

    Optional<Goal> getGoal(long tenantId, String projectId, String goalId);

    void addAttachment(long tenantId, String projectId, String goalId, FileDescriptor fileDescriptor);

    void deleteAttachment(long tenantId, String projectId, String goalId, String fileName);

    Goal addGoal(long tenantId, String projectId, Goal goal);

	String updateGoal(long tenantId, String projectId, String goalId, Goal goal);

    void deleteGoal(long tenantId, String projectId, String goalId);

    /************************** TASKS OPS************************************/

    Task addTask(long tenantId, String projectId, String goalId, Task task);

    Optional<Task> getTask(long tenantId, String projectId, String goalId, String taskId);

    String updateTaskNameAndDescription(long tenantId, String projectId, String goalId, String taskId, Task task);

    void updateTaskStatus(long tenantId, String projectId, String goalId, String taskId, String status);

    boolean assignPerson(long tenantId, String projectId,  String goalId, String taskId, ShortPerson shortPerson);

    boolean unAssignPerson(long tenantId, String projectId, String goalId, String taskId, String personId);



    void addAttachment(long tenantId, String projectId, String goalId, String taskId, FileDescriptor fileDescriptor);

    void deleteAttachment(long tenantId, String projectId, String goalId, String taskId, String fileName);

    String addMessage(long tenantId, String projectId, String goalId, String taskId, Message message);

    String updateMessage(long tenantId, String projectId, String goalId, String taskId, String messageId, Message message);

    void deleteTask(long tenantId, String projectId, String goalId, String taskId);


}
