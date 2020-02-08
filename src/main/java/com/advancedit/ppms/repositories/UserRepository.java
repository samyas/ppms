package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.advancedit.ppms.models.user.User;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);

	User findByUsername(String username);

//	@Query("{$and: [ { tenantId :  ?0 } , {$or : [ { $where: '?1 == null' } , { personfunction :  ?1 } ]}, {$or : [ { $where: '?2 == null' } , { status : ?2  }] }]}")
//	Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, PersonFunction personfunction, String status, Pageable pageable);


	@Query("{$and: [ {$or : [ { $where: '?0 == null' } , { defaultTenantId :  ?0 } ]}, {$or : [ { $where: '?1 == null' } , { organisationCreationRequest :  ?1 } ]}, {$or : [ { $where: '?2 == null' } , { enabled : ?2  }]  } ] }")
	Page<User> findByAllCriteria(Long tenantId, Boolean isCreator, Boolean enabled, Pageable pageable);

}
