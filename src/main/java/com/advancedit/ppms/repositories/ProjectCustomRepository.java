package com.advancedit.ppms.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;

public interface ProjectCustomRepository {

	Page<ProjectSummary> getPagedProjectSummary(int page, int size, Pageable pageable);
	

    Optional<Goal> getGoal(String projectId, String goalId);
    
    
    Goal addGoal(String projectId, Goal goal);

    Task addTask(String projectId, String goalId, Task task);
    
    
    Optional<Task> getTask(String projectId, String goalId, String taskId);
    

	void updateGoal(String projectId, Goal goal);


}
