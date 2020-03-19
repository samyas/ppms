package com.advancedit.ppms.repositories.impl;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;
import com.advancedit.ppms.models.project.ProjectSummary;
import com.advancedit.ppms.models.project.Task;
import com.advancedit.ppms.repositories.OrganisationCustomRepository;
import com.advancedit.ppms.repositories.ProjectCustomRepository;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
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
				.filterArray(Criteria.where("i._id").is(new ObjectId(department.getId())));
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
	public Optional<Department> getDepartment(long tenantId, String organisationId, String departmentId) {
		BasicQuery query = new BasicQuery( Criteria.where("id").is(organisationId).and("tenantId").is(tenantId).getCriteriaObject());
		query.fields().elemMatch("departments", Criteria.where("id").is(departmentId));
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
				Criteria.where("departments").elemMatch(Criteria.where("id").is(departmentId))).getCriteriaObject());

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
