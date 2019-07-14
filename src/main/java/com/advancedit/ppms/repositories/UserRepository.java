package com.advancedit.ppms.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.advancedit.ppms.models.User;

public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);
}
