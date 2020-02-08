package com.advancedit.ppms.repositories.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.repositories.ProjectCustomRepository;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MappedDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;
import com.mongodb.client.result.UpdateResult;

import javax.swing.plaf.basic.BasicListUI;


public class ProjectRepositoryImpl implements ProjectCustomRepository {

	private final MongoTemplate mongoTemplate;
    
    @Autowired
    public ProjectRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
	@Override
	public Page<ProjectSummary> getPagedProjectSummary(long tenantId, int page, int size, Pageable pageable) {
		
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
	public Optional<Goal> getGoal(long tenantId, String projectId, String goalId) {
		BasicQuery query = new BasicQuery( Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId).getCriteriaObject());
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
	public Goal addGoal(long tenantId, String projectId, Goal goal) {
		
		   // the query object
        Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("goals", goal);
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Project.class);
		if (wr.getModifiedCount() != 1){
			  throw new PPMSException("Unable to add goal");
		}
		return goal;
	}
	
	@Override
	public void updateGoal(long tenantId, String projectId, Goal goal) {
		
		   // the query object
        Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("goals", goal);
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Project.class);
		if (wr.getModifiedCount() != 1){
			  throw new PPMSException("Unable to add goal");
		}
		
	}

	@Override
	public void assignTask(long tenantId, String projectId, String goalId, String taskId, List<ShortPerson> shortPersonList){
	/*	UpdateResult wr2 = mongoTemplate.getCollection("project").updateMany(
				new Document().append("_id", new ObjectId(projectId) ).append("tenantId", tenantId),
				new Document().append("$set", new Document().append("goals.$[i].tasks.$[j].assignedTo", shortPersonList)),
				new UpdateOptions()
						.arrayFilters(Arrays.asList(Filters.eq("i._id", new ObjectId(goalId)),
								Filters.eq("j._id", new ObjectId(taskId))))
		);

		if (wr2.getModifiedCount() != 1){
			throw new PPMSException("Unable to update task");
		}*/
		// the query object
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().set("goals.$[i].tasks.$[j].assignedTo", shortPersonList)
			//	.filterArray(Criteria.where("i.goalId").is(new ObjectId(goalId))).filterArray(Criteria.where("j.taskId").is(new ObjectId(taskId)));
			.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));
//.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));

		//.filterArray("i.goalId", goalId).filterArray("j.taskId", taskId);
;
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to assign task");
		}

	}

	@Override
	public void updateProjectStatus(long tenantId, String projectId, String status){
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().set("status", status);
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to update project");
		}
	}

	@Override
	public void updateTaskStatus(long tenantId, String projectId, String goalId, String taskId, String status) {

		UpdateResult wr2 = mongoTemplate.getCollection("project").updateMany(
				new Document().append("_id", new ObjectId(projectId) ).append("tenantId", tenantId),
				new Document().append("$set", new Document().append("goals.$[i].tasks.$[j].status", status)),
				new UpdateOptions()
						.arrayFilters(Arrays.asList(Filters.eq("i._id", new ObjectId(goalId)),
								Filters.eq("j._id", new ObjectId(taskId))))
		);

		if (wr2.getModifiedCount() != 1){
			throw new PPMSException("Unable to update task");
		}
	}
	
	@Override
	public Task addTask(long tenantId, String projectId, String goalId, Task task) {
		
		   // the query object
        final BasicQuery query1 = new BasicQuery(Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId).andOperator(
            Criteria.where("goals").elemMatch(Criteria.where("goalId").is(goalId))).getCriteriaObject());
     
		final Update update = new Update().addToSet("goals.$.tasks", task);
		final UpdateResult wr = mongoTemplate.updateFirst(query1, update, Project.class);
		if (wr.getModifiedCount() != 1){
			  throw new PPMSException("Unable to add task");
		}
		return task;
	}



	@Override
	public Optional<Task> getTask(long tenantId, String projectId, String goalId, String taskId) {
		
		return getGoal(tenantId, projectId, goalId).map(Goal::getTasks)
		 .flatMap( tasks -> tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst());
		
	/*		BasicQuery subQuery = new BasicQuery( Criteria.where("goalId").is(goalId).getCriteriaObject());
		subQuery.fields().elemMatch("tasks", Criteria.where("taskId").is(taskId));
		
		BasicQuery query = new BasicQuery( Criteria.where("projectId").is(projectId)
			//	.andOperator(Criteria.where("goals.tasks.taskId").is(taskId))
				.getCriteriaObject());
	//	query.fields().elemMatch("goals.tasks", Criteria.where("taskId").is(taskId));

	query.fields().include("goals.tasks").elemMatch("goals.$.tasks", Criteria.where("taskId").is(taskId));
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
		
	/*	  Aggregation aggregation = Aggregation.newAggregation(
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
		

		return Optional.of(tasks.get(0));*/
	}


	public boolean existByProjectIdAndTenantId(String projectId, long tenantId){
		Criteria criteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
        return mongoTemplate.exists(new BasicQuery(criteria.getCriteriaObject()), Project.class);
	}

}
