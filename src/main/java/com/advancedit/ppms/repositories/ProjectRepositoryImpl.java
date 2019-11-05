package com.advancedit.ppms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.GoalStatus;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;



public class ProjectRepositoryImpl implements ProjectCustomRepository {

	private final MongoTemplate mongoTemplate;
    
    @Autowired
    public ProjectRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
	@Override
	public Page<ProjectSummary> getPagedProjectSummary(int page, int size, Pageable pageable) {
		
		final Aggregation aggregation = Aggregation.newAggregation(
             //   Aggregation.match(Criteria.where("facebookPosts.date").regex(REGEX)),
            //    Aggregation.unwind("goals"),
               Aggregation.group("projectId").push("goals.tasks.taskId").as("taskIds"),
             /*   Aggregation.project().andInclude("name", "description", "status")
                .and("goals")// .filter("status",  )
                .size() 
                .as("nbrGoals"),*/
                Aggregation.project()//.andInclude("name", "description", "status", "goals")
                
                .and("taskIds").size().as("nbrTasks")
				);

		
		  AggregationResults<ProjectSummary> results = mongoTemplate.aggregate(aggregation,
		    		Project.class, ProjectSummary.class);

		    List<ProjectSummary> ps  = results.getMappedResults();
		
		return null;
	}

	@Override
	public Optional<Goal> getGoal(String projectId, String goalId) {
		BasicQuery query = new BasicQuery( Criteria.where("projectId").is(projectId).getCriteriaObject());
		query.fields().elemMatch("goals", Criteria.where("goalId").is(goalId));
		Project ps = mongoTemplate.findOne(query,  Project.class);
		return ( ps.getGoals().size() == 1) ? Optional.of(ps.getGoals().get(0)) : Optional.empty() ;
/*		
		// the query object
        Criteria findProjectCriteria = Criteria.where("projectId").is(projectId);
        
      //  findProjectCriteria.andOperator( Criteria.where("goals.").elemMatch(Criteria.where("name").is("Goal 1")));
     //   findProjectCriteria.andOperator( Criteria.where("goals.name").is("Goal 1"));
        
        //  Criteria findGoalCriteria = Criteria.where("goals").elemMatch(Criteria.where("goalId").is(goalId));
        BasicQuery query = new BasicQuery(findProjectCriteria.andOperator( Criteria.where("goals.name").is("Goal 1")).getCriteriaObject());//, findGoalCriteria.getCriteriaObject());
        query.fields().include("goals");
 
     
	//	final Update update = new Update().addToSet("goals.$.tasks", task);//.set("listA.$.thing", "thing");
       Project ps = mongoTemplate.findOne(query,  Project.class);
		return ( ps.getGoals().size() == 1) ? Optional.of(ps.getGoals().get(0)) : Optional.empty() ;*/
	}

	@Override
	public Goal addGoal(String projectId, Goal goal) {
		
		   // the query object
        Criteria findProjectCriteria = Criteria.where("projectId").is(projectId);
     
		final Update update = new Update().addToSet("goals", goal);//.set("listA.$.thing", "thing");

		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Project.class);

		if (wr.getModifiedCount() != 1){
			  throw new PPMSException("Unable to add goal");
		}
		return goal;
	}
	
	
	@Override
	public Task addTask(String projectId, String goalId, Task task) {
		
		   // the query object
        Criteria findProjectCriteria = Criteria.where("projectId").is(projectId);
        
        Criteria findGoalCriteria = Criteria.where("goals").elemMatch(Criteria.where("goalId").is(goalId));
        BasicQuery query = new BasicQuery(findProjectCriteria.getCriteriaObject(), findGoalCriteria.getCriteriaObject());
 
        final BasicQuery query1 = new BasicQuery(Criteria.where("projectId").is(projectId).andOperator(
         
                Criteria.where("goals").elemMatch(Criteria.where("goalId").is(goalId))
        ).getCriteriaObject());
     
		final Update update = new Update().addToSet("goals.$.tasks", task);//.set("listA.$.thing", "thing");

		final UpdateResult wr = mongoTemplate.updateFirst(query1, update, Project.class);

		if (wr.getModifiedCount() != 1){
			  throw new PPMSException("Unable to add task");
		}
		return task;
	}

	@Override
	public Optional<Task> getTask(String projectId, String goalId, String taskId) {
		
	/*	BasicQuery subQuery = new BasicQuery( Criteria.where("goalId").is(goalId).getCriteriaObject());
		subQuery.fields().elemMatch("tasks", Criteria.where("taskId").is(taskId));
		
		BasicQuery query = new BasicQuery( Criteria.where("projectId").is(projectId)
			//	.andOperator(Criteria.where("goals.tasks.taskId").is(taskId))
				.getCriteriaObject());
		query.fields().elemMatch("goals.tasks", Criteria.where("taskId").is(taskId))
				;

//	query.fields().include("goals.tasks").elemMatch("goals.$.tasks", Criteria.where("taskId").is(taskId));
		Project ps = mongoTemplate.findOne(query,  Project.class);
		return ( ps.getGoals().size() == 1) ? 
				
				( ps.getGoals().get(0).getTasks().size() == 1 ? Optional.of(ps.getGoals().get(0).getTasks().get(0)) : Optional.empty() )
				
				: Optional.empty() ;*/
		
		
	/*	Aggregation aggregation = newAggregation(
			     Aggregation.match(Criteria.where("projectId").is(projectId)),
			     Aggregation.project("goals.tasks")
			                 .and(ArrayOperators.Filter.filter("goals.tasks").as("task")                          
			                 .by(ComparisonOperators.Eq.valueOf("task.taskId").equalToValue(taskId))
			                    ).as("tasks")
			 );*/
		
		  Aggregation aggregation = Aggregation.newAggregation(
		            Aggregation.match(Criteria.where("projectId").is(projectId)),
		            Aggregation.unwind("goals.tasks", true),
		            Aggregation.match(Criteria.where("goals.tasks.taskId").is(taskId)),
		            Aggregation.project("goals.tasks").and(ArrayOperators.Filter.filter("goals.tasks").as("task")                          
			                 .by(ComparisonOperators.Eq.valueOf("task.taskId").equalToValue(taskId))
		                    ).as("tasks")
		    );
		    AggregationResults<Task> results = mongoTemplate.aggregate(aggregation,
		    		Project.class, Task.class);

		    List<Task> tasks  = results.getMappedResults();
		

		return Optional.of(tasks.get(0));
	}
	

}
