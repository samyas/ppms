package com.advancedit.ppms.repositories.impl;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.repositories.PersonCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class PersonRepositoryImpl implements PersonCustomRepository {

	private final MongoTemplate mongoTemplate;

    @Autowired
    public PersonRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

	@Override
	public Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, PersonFunction personfunction,
																 String status, String departmentId, Pageable pageable) {
		Criteria criteria = Criteria.where("tenantId").is(tenantId);
		if (departmentId != null) {
			criteria = criteria.and("departmentId").is(departmentId);
		}
		if (personfunction != null){
			criteria = criteria.and("personfunction").is(personfunction);
		}
		if (isNotBlank(status)){
			criteria = criteria.and("status").is(status);
		}
		Query query = new BasicQuery( criteria.getCriteriaObject()).with(pageable);
		List<Person> persons = mongoTemplate.find(query, Person.class);
		long count = mongoTemplate.count(query, Person.class);
		return  new PageImpl<>(persons , pageable, count);
	}
}
