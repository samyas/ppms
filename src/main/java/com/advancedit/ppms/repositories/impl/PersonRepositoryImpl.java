package com.advancedit.ppms.repositories.impl;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.repositories.PersonCustomRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class PersonRepositoryImpl implements PersonCustomRepository {

	private final MongoTemplate mongoTemplate;

    @Autowired
    public PersonRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

	@Override
	public Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, List<PersonFunction> personFunctions, String name,
																 String status, String departmentId, Pageable pageable) {
		Criteria criteria = Criteria.where("tenantId").is(tenantId);
		if (departmentId != null) {
			criteria = criteria.and("departmentId").is(departmentId);
		}
		if (personFunctions != null && !personFunctions.isEmpty()){
			criteria = criteria.and("personfunction").in(personFunctions.stream().map(PersonFunction::name).collect(Collectors.toList()));
		}
		if (isNotBlank(name)){
			Criteria firstNameQuery = Criteria.where("firstName").regex(name);
			Criteria lastNameQuery = Criteria.where("lastName").regex(name);
			criteria = criteria.andOperator(new Criteria().orOperator(firstNameQuery, lastNameQuery));
		}
		if (isNotBlank(status)){
			criteria = criteria.and("status").is(status);
		}
		Query query = new BasicQuery( criteria.getCriteriaObject());
		long count = mongoTemplate.count(query, Person.class);
		List<Person> persons = mongoTemplate.find(query.with(pageable), Person.class);

		return  new PageImpl<>(persons , pageable, count);
	}

	@Override
	public List<Person> findListByTenantIdAndDepartmentId(long tenantId, String departmentId){
		Criteria criteria = Criteria.where("tenantId").is(tenantId);
		if (departmentId != null) {
			criteria = criteria.and("departmentId").is(departmentId);
		}
		Query query = new BasicQuery( criteria.getCriteriaObject());
		return  mongoTemplate.find(query, Person.class);
	}

	@Override
	public String getDepartmentId(long tenantId, String personId){
		Criteria criteria = Criteria.where("id").is(personId).and("tenantId").is(tenantId);
		Query query = new BasicQuery( criteria.getCriteriaObject());
		query.fields().include("departmentId");
		Person person = Optional.ofNullable(mongoTemplate.findOne(query, Person.class))
				.orElseThrow(() -> new PPMSException("Person not found"));
		return person.getDepartmentId();
	}

	@Override
	public void updateImage(long tenantId,  String personId,  FileDescriptor fileDescriptor) {
		final BasicQuery query = new BasicQuery(Criteria.where("id").is(personId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		final Update update = new Update().set("image", fileDescriptor);
		final UpdateResult wr = mongoTemplate.updateFirst(query, update, Person.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to set image");
		}
	}

	@Override
	public void updateProjectInfo(long tenantId,  String personId, int workload, int currentProjects, int previousProjects) {
		final BasicQuery query = new BasicQuery(Criteria.where("id").is(personId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		final Update update = new Update().set("workload", workload).set("currentProjects", currentProjects)
				.set("previousProjects", previousProjects);
		final UpdateResult wr = mongoTemplate.updateFirst(query, update, Person.class);
		if (wr.getModifiedCount() != 1){
			throw new PPMSException("Unable to update workload");
		}
	}

	@Override
	public Optional<Person> findByTenantIdAndPersonId(long tenantId, String personId) {
		final BasicQuery query = new BasicQuery(Criteria.where("id").is(personId).and("tenantId").is(tenantId)
				.getCriteriaObject());
		return Optional.ofNullable(mongoTemplate.findOne(query, Person.class));

	}
}
