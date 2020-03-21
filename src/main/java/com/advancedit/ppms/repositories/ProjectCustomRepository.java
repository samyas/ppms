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

	Page<Project> findByAll(long tenantId, String departmentId, Pageable pageable);
	

    Optional<Goal> getGoal(long tenantId, String projectId, String goalId);
    
    
    Goal addGoal(long tenantId, String projectId, Goal goal);

    Task addTask(long tenantId, String projectId, String goalId, Task task);
    
    
    Optional<Task> getTask(long tenantId, String projectId, String goalId, String taskId);
    

	void updateGoal(long tenantId, String projectId, Goal goal);

    void updateTaskStatus(long tenantId, String projectId, String goalId, String taskId, String status);

    void assignTask(long tenantId, String projectId, String goalId, String taskId, List<ShortPerson> shortPersonList);

    void updateProjectStatus(long tenantId, String projectId, String status);


    boolean existByProjectIdAndTenantId(String projectId, long tenantId);


    void addAttachment(long tenantId, String projectId, FileDescriptor fileDescriptor);

    void addAttachment(long tenantId, String projectId, String goalId, FileDescriptor fileDescriptor);

    void addAttachment(long tenantId, String projectId, String goalId, String taskId, FileDescriptor fileDescriptor);

    void deleteAttachment(long tenantId, String projectId, String fileName);

    void deleteAttachment(long tenantId, String projectId, String goalId, String fileName);

    void deleteAttachment(long tenantId, String projectId, String goalId, String taskId, String fileName);

    String addMessage(long tenantId, String projectId, String goalId, String taskId, Message message);

    String updateMessage(long tenantId, String projectId, String goalId, String taskId, String messageId, Message message);
}
