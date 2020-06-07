package com.advancedit.ppms.repositories.impl;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.*;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.repositories.OrganisationCustomRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Optional;


public class OrganisationRepositoryImpl implements OrganisationCustomRepository {

	private final MongoTemplate mongoTemplate;

    @Autowired
    public OrganisationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


	@Override
	public Department addDepartment(long tenantId, String organisationId, Department department) {
		Criteria findDepartmentCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("departments", department);
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findDepartmentCriteria.getCriteriaObject()), update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add goal");
		}
		return department;
	}

	@Override
	public void updateDepartment(long tenantId, String organisationId, Department department) {
		Criteria findDepartmentCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().set("departments.$[i]", department)
				.filterArray(Criteria.where("i._id").is(new ObjectId(department.getDepartmentId())));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findDepartmentCriteria.getCriteriaObject()), update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to update Department");
		}
	}

	@Override
	public void addLogo(long tenantId,  String organisationId,  FileDescriptor fileDescriptor) {
		final BasicQuery query = new BasicQuery(Criteria.where("id").is(organisationId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		final Update update = new Update().set("logo", fileDescriptor);
		final UpdateResult wr = mongoTemplate.updateFirst(query, update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to set logo");
		}
	}

	@Override
	public SupervisorTerm addTerm(long tenantId, String organisationId, String departmentId, SupervisorTerm term) {
		// the query object
		final BasicQuery query1 = new BasicQuery(Criteria.where("id").is(organisationId).and("tenantId").is(tenantId).andOperator(
				Criteria.where("departments").elemMatch(Criteria.where("departmentId").is(departmentId))).getCriteriaObject());
		final Update update = new Update().addToSet("departments.$.supervisorTerms", term);
		final UpdateResult wr = mongoTemplate.updateFirst(query1, update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add new term");
		}
		return term;
	}

	@Override
	public Optional<Department> getDepartment(long tenantId, String departmentId) {
		BasicQuery query = new BasicQuery( Criteria.where("tenantId").is(tenantId).getCriteriaObject());
		query.fields().elemMatch("departments", Criteria.where("departmentId").is(departmentId));
		Organisation ps = mongoTemplate.findOne(query,  Organisation.class);
		return (ps.getDepartments() != null && ps.getDepartments().size() == 1) ? Optional.of(ps.getDepartments().get(0)) : Optional.empty() ;
	}


	@Override
	public Optional<SupervisorTerm> getTerm(long tenantId,  String departmentId, String termId){
		return getDepartment(tenantId, departmentId).map(Department::getSupervisorTerms)
				.flatMap( terms -> terms.stream().filter(t -> t.getTermId().equals(termId)).findFirst());
	}

	@Override
	public void updateTerm(long tenantId, String organisationId, String departmentId, SupervisorTerm term) {
		UpdateResult wr2 = mongoTemplate.getCollection("organisations").updateMany(
				new Document().append("_id", new ObjectId(organisationId) ).append("tenantId", tenantId)
				,new Document().append("$set", new Document()
						.append("departments.$[i].supervisorTerms.$[j].name", term.getName())
						.append("departments.$[i].supervisorTerms.$[j].description", term.getDescription())
						.append("departments.$[i].supervisorTerms.$[j].quota", term.getQuota())
						.append("departments.$[i].supervisorTerms.$[j].mandatoryBeforeStart", term.getMandatoryBeforeStart())
						.append("departments.$[i].supervisorTerms.$[j].order", term.getOrder())
				),
				new UpdateOptions().arrayFilters(Arrays.asList(
						Filters.eq("i._id", new ObjectId(departmentId)),
						Filters.eq("j._id", new ObjectId(term.getTermId()))))
		);
		if (wr2.getModifiedCount() != 1){
			throw new PPMSException("Unable to update term");
		}

	}

	@Override
	public void deleteTerm(long tenantId, String organisationId, String departmentId, String termId) {
		Criteria findProjectCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().pull("departments.$[i].supervisorTerms", new BasicDBObject("_id", termId))
				.filterArray(Criteria.where("i._id").is(new ObjectId(departmentId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete term");
		}
	}

	@Override
	public Action addAction(long tenantId, String organisationId, String departmentId, Action action) {
		// the query object
		final BasicQuery query1 = new BasicQuery(Criteria.where("id").is(organisationId).and("tenantId").is(tenantId).andOperator(
				Criteria.where("departments").elemMatch(Criteria.where("departmentId").is(departmentId))).getCriteriaObject());
		final Update update = new Update().addToSet("departments.$.actions", action);
		final UpdateResult wr = mongoTemplate.updateFirst(query1, update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add new action");
		}
		return action;
	}

	@Override
	public void updateAction(long tenantId, String organisationId, String departmentId, Action action) {
		UpdateResult wr2 = mongoTemplate.getCollection("organisations").updateMany(
				new Document().append("_id", new ObjectId(organisationId) ).append("tenantId", tenantId)
				,new Document().append("$set", new Document()
						.append("departments.$[i].actions.$[j].name", action.getName())
						.append("departments.$[i].actions.$[j].description", action.getDescription())
						.append("departments.$[i].actions.$[j].startDate", action.getStartDate())
						.append("departments.$[i].actions.$[j].endDate", action.getEndDate())
						.append("departments.$[i].actions.$[j].dateNbr", action.getDateNbr())
						.append("departments.$[i].actions.$[j].weekNbr", action.getWeekNbr())
						.append("departments.$[i].actions.$[j].order", action.getOrder())
						.append("departments.$[i].actions.$[j].beforeStart", action.getBeforeStart())
				),
				new UpdateOptions().arrayFilters(Arrays.asList(
						Filters.eq("i._id", new ObjectId(departmentId)),
						Filters.eq("j._id", new ObjectId(action.getActionId()))))
		);
		if (wr2.getModifiedCount() != 1){
			throw new PPMSException("Unable to update term");
		}
	}

	@Override
	public void deleteAction(long tenantId, String organisationId, String departmentId, String actionId) {
		Criteria findProjectCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().pull("departments.$[i].actions", new BasicDBObject("_id", actionId))
				.filterArray(Criteria.where("i._id").is(new ObjectId(departmentId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()), update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete term");
		}
	}

	@Override
	public void addAttachment(long tenantId, String organisationId, String departmentId, String actionId, FileDescriptor fileDescriptor) {
		Criteria findProjectCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().addToSet("departments.$[i].actions.$[j].attachmentList", fileDescriptor)
				.filterArray(Criteria.where("i._id").is(new ObjectId(departmentId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(actionId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add attachment to action");
		}
	}


	@Override
	public void deleteAttachment(long tenantId, String organisationId, String departmentId, String actionId, String key) {

		Criteria findProjectCriteria = Criteria.where("id").is(organisationId).and("tenantId").is(tenantId);
		final Update update = new Update().pull("departments.$[i].actions.$[j].attachmentList", new BasicDBObject("key", key))
				.filterArray(Criteria.where("i._id").is(new ObjectId(departmentId))).
						filterArray(Criteria.where("j._id").is(new ObjectId(actionId)));
		final UpdateResult wr = mongoTemplate.updateFirst(new BasicQuery(findProjectCriteria.getCriteriaObject()),
				update, Project.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to delete attachment to action");
		}
	}

	@Override
	public Optional<Department> getDepartment(long tenantId, String organisationId, String departmentId) {
		BasicQuery query = new BasicQuery( Criteria.where("id").is(organisationId).and("tenantId").is(tenantId).getCriteriaObject());
		query.fields().elemMatch("departments", Criteria.where("departmentId").is(departmentId));
		Organisation ps = mongoTemplate.findOne(query,  Organisation.class);
		return (ps.getDepartments() != null && ps.getDepartments().size() == 1) ? Optional.of(ps.getDepartments().get(0)) : Optional.empty() ;
	}

	@Override
	public void deleteDepartment(long tenantId, String organisationId, String departmentId) {

	}

	@Override
	public Sector addSector(long tenantId, String organisationId, String departmentId, Sector sector) {
		// the query object
		final BasicQuery query1 = new BasicQuery(Criteria.where("id").is(organisationId).and("tenantId").is(tenantId).andOperator(
				Criteria.where("departments").elemMatch(Criteria.where("departmentId").is(departmentId))).getCriteriaObject());

		final Update update = new Update().addToSet("departments.$.sectors", sector);
		final UpdateResult wr = mongoTemplate.updateFirst(query1, update, Organisation.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to add sector");
		}
		return sector;
	}

	@Override
	public Optional<Sector> getSector(long tenantId, String organisationId, String departmentId, String sectorId) {
		return getDepartment(tenantId, organisationId, departmentId).map(Department::getSectors)
				.flatMap( tasks -> tasks.stream().filter(t -> t.getId().equals(sectorId)).findFirst());
	}
}
