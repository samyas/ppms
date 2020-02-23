package com.advancedit.ppms.repositories;

import java.util.List;
import java.util.Optional;

import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;

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


}
