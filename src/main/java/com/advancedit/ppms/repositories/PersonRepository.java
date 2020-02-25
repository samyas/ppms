package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface PersonRepository extends MongoRepository<Person, String>, PersonCustomRepository {

	Person findByEmail(String email);


	List<Person> findByTenantId(long tenantId);

	Person findByTenantIdAndEmail(long tenantId, String email);

	/*@Query("{$and: [ " +
			          "{ tenantId :  ?0 } , " +
			          "{$or : [ { $where: '?1 == null' } , { personfunction :  ?1 } ]}, " +
			          "{$or : [ { $where: '?2 == null' } , { status : ?2  }         ]}" +
		              "{$or : [ { $where: '?3 == null' } , { departmentId : ?3  }   ]}" +
			       "]" +

			"}"
	)
	Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, PersonFunction personfunction, String status,
														  String departmentId, Pageable pageable);
*/

	@Query("{$and: [" +
						"{ tenantId :  ?0 } , " +
						"{$or : [ { $where: '?1 == null' } , { personfunction :  ?1 } ]}, " +
						"{$or : [ { $where: '?2 == null' } , { status : ?2  }]  }, " +
						"{$or : [ { $where: '?3 == null' } , { firstName : { $regex: ?3, $options: 'i' }}]  }, " +
			            "{$or : [ { $where: '?4 == null' } , { departmentId : ?4  }]  }, " +
			"] }")
	Page<Person> findByAllCriteria(long tenantId, PersonFunction personfunction, String status,  String name,
								   String departmentId,  Pageable pageable);

}






