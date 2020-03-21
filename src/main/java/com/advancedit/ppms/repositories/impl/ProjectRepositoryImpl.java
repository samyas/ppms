package com.advancedit.ppms.repositories.impl;

import java.util.*;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.repositories.ProjectCustomRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
               Aggregation.group("projectId").push("goals.tasks.taskId").as("taskIds"),
                Aggregation.project()
                .and("taskIds").size().as("nbrTasks")
				);
		  AggregationResults<ProjectSummary> results = mongoTemplate.aggregate(aggregation,
		    		Project.class, ProjectSummary.class);

		    List<ProjectSummary> ps  = results.getMappedResults();
		
		return null;
	}

	@Override
	public Page<Project> findByAll(long tenantId, String departmentId, Pageable pageable){
		Criteria criteria = Criteria.where("tenantId").is(tenantId);
		if (departmentId != null) {
			criteria = criteria.and("departmentId").is(departmentId);
		}
		Query query = new BasicQuery( criteria.getCriteriaObject()).with(pageable);
		query.fields().exclude("goals");
		List<Project> projects = mongoTemplate.find(query, Project.class);
		long count = mongoTemplate.count(query, Project.class);
		return  new PageImpl<>(projects , pageable, count);
	}



	@Override
	public Optional<Goal> getGoal(long tenantId, String projectId, String goalId) {
		BasicQuery query = new BasicQuery( Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId).getCriteriaObject());
		query.fields().elemMatch("goals", Criteria.where("goalId").is(goalId));
		Project ps = mongoTemplate.findOne(query,  Project.class);
		return ( ps.getGoals().size() == 1) ? Optional.of(ps.getGoals().get(0)) : Optional.empty() ;
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

		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().set("goals.$[i].tasks.$[j].assignedTo", shortPersonList)
					.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));
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
	public void addAttachment(long tenantId,  String projectId,  FileDescriptor fileDescriptor) {
		final BasicQuery query = new BasicQuery(Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		final Update update = new Update().addToSet("attachments", fileDescriptor);
		final UpdateResult wr = mongoTemplate.updateFirst(query, update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add attachment to project");
		}
	}

	@Override
	public void addAttachment(long tenantId,  String projectId, String goalId,  FileDescriptor fileDescriptor) {
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("goals.$[i].attachmentsArrayList", fileDescriptor)
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add attachment to goal");
		}
	}

	@Override
	public void addAttachment(long tenantId,  String projectId, String goalId, String taskId, FileDescriptor fileDescriptor) {
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("goals.$[i].tasks.$[j].attachmentList", fileDescriptor)
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add attachment to task");
		}
	}

	@Override
	public void deleteAttachment(long tenantId,  String projectId,  String key) {
		final BasicQuery query = new BasicQuery(Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		final Update update = new Update().pull("attachments",  new BasicDBObject("key", key));
		final UpdateResult wr = mongoTemplate.updateFirst(query, update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete attachment to project");
		}
	}

	@Override
	public void deleteAttachment(long tenantId,  String projectId, String goalId,  String key) {
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().pull("goals.$[i].attachmentsArrayList", new BasicDBObject("key", key))
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete attachment to task");
		}

	}

	@Override
	public void deleteAttachment(long tenantId,  String projectId, String goalId, String taskId, String key) {

		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().pull("goals.$[i].tasks.$[j].attachmentList", new BasicDBObject("key", key))
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete attachment to task");
		}
	}

	@Override
	public String addMessage(long tenantId, String projectId, String goalId, String taskId, Message message){
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("goals.$[i].tasks.$[j].messages", message)
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(taskId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add message to task");
		}
		return message.getMessageId();
	}

	@Override
	public String updateMessage(long tenantId, String projectId, String goalId, String taskId, String messageId, Message message){
		Criteria findProjectCriteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
		final Update update = new Update().set("goals.$[i].tasks.$[j].messages.$[k].content", message.getContent())
				.set("goals.$[i].tasks.$[j].messages.$[k].modifiedTime", new Date())
				.filterArray(Criteria.where("i._id").is(new ObjectId(goalId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(taskId)))
				.filterArray(Criteria.where("k._id").is(new ObjectId(messageId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to update message to task");
		}
		return message.getMessageId();
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
	}

	public boolean existByProjectIdAndTenantId(String projectId, long tenantId){
		Criteria criteria = Criteria.where("projectId").is(projectId).and("tenantId").is(tenantId);
        return mongoTemplate.exists(new BasicQuery(criteria.getCriteriaObject()), Project.class);
	}


}
