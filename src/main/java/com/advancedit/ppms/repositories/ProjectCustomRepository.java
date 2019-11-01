package com.advancedit.ppms.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.advancedit.ppms.models.Goal;
import com.advancedit.ppms.models.Project;
import com.advancedit.ppms.models.ProjectSummary;
import com.advancedit.ppms.models.Task;

public interface ProjectCustomRepository {

	Page<ProjectSummary> getPagedProjectSummary(int page, int size, Pageable pageable);
	

    Optional<Goal> getGoal(String projectId, String goalId);
    
    
    Goal addGoal(String projectId, Goal goal);

    Task addTask(String projectId, String goalId, Task task);
    
    
    Optional<Task> getTask(String projectId, String goalId, String taskId);
}
