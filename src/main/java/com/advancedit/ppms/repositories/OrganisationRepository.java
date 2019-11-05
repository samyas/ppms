package com.advancedit.ppms.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;

public interface OrganisationRepository extends MongoRepository<Organisation, String> {
	
	@Override
    void delete(Organisation deleted);
}
