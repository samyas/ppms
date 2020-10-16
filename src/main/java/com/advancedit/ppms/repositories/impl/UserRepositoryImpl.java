package com.advancedit.ppms.repositories.impl;

import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.repositories.UserCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class UserRepositoryImpl implements UserCustomRepository {

	private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

	@Override
	public List<User> findByEmails(long tenantId, List<String> emails) {
		Criteria criteria = Criteria.where("tenantIds").in(tenantId);

		if (emails != null && !emails.isEmpty()){
			criteria = criteria.and("email").in(emails);
		}
		Query query = new BasicQuery( criteria.getCriteriaObject());
		return mongoTemplate.find(query, User.class);
	}


}
