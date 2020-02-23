package com.advancedit.ppms.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Project;


@Repository
public interface ProjectRepository extends MongoRepository<Project, String>, ProjectCustomRepository {
	
	@Query("{$and: [{$or : [ { $where: '?0 == null' } , { status : ?0  }]  },  {$or : [ { $where: '?1 == null' } , { firstName : { $regex: ?1, $options: 'i' }  }]  } ] }")
    public Page<Project> findByAllCriteria(String status,  String name, Pageable pageable);


/*	@Query(value = "{$and: " +
			"[" +
			       "{ tenantId :  ?0 }, " +
			       "{$or : [ { $where: '?1 == null' } , { departmentId :  ?1 } ]}," +
			"]}",  fields="{goals : 0}")
	public Page<Project> findByAll(long tenantId, String departmentId, Pageable pageable);

*/

	 

}
