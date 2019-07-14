package com.advancedit.ppms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.advancedit.ppms.models.Project;



public interface ProjectRepository extends MongoRepository<Project, String> {
	

//	@Query("{$and: [{$or : [ { $where: '?0 == null' } , { personfunction :  ?0 } ]}, {$or : [ { $where: '?1 == null' } , { status : ?1  }] }]}")
  //  public Page<Project> findByPersonFunctionAndStatus(PersonFunction personfunction, String status, Pageable pageable);

	
	@Query("{$and: [{$or : [ { $where: '?0 == null' } , { status : ?0  }]  },  {$or : [ { $where: '?1 == null' } , { firstName : { $regex: ?1, $options: 'i' }  }]  } ] }")
    public Page<Project> findByAllCriteria(String status,  String name, Pageable pageable);


}
