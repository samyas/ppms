package com.advancedit.ppms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.advancedit.ppms.models.Person;
import com.advancedit.ppms.models.PersonFunction;




public interface PersonRepository extends MongoRepository<Person, String> {

	Person findByEmail(String email);
	

	@Query("{$and: [{$or : [ { $where: '?0 == null' } , { personfunction :  ?0 } ]}, {$or : [ { $where: '?1 == null' } , { status : ?1  }] }]}")
    public Page<Person> findByPersonFunctionAndStatus(PersonFunction personfunction, String status, Pageable pageable);

	
	@Query("{$and: [{$or : [ { $where: '?0 == null' } , { personfunction :  ?0 } ]}, {$or : [ { $where: '?1 == null' } , { status : ?1  }]  },  {$or : [ { $where: '?2 == null' } , { firstName : { $regex: ?2, $options: 'i' }  }]  } ] }")
    public Page<Person> findByAllCriteria(PersonFunction personfunction, String status,  String name, Pageable pageable);

}
