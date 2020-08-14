package com.advancedit.ppms.repositories;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonCustomRepository {

    Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, List<PersonFunction> personFunctions, String name, String status,
                                                          String departmentId, Pageable pageable);


    List<Person> findListByTenantIdAndDepartmentId(long tenantId, String departmentId);

    void updateImage(long tenantId,  String personId,  FileDescriptor fileDescriptor);

    FileDescriptor getImage(long tenantId, String personId);

    List<Person> findByTenantIdAndPersonIds(long tenantId, List<String> personId);

    Optional<Person> findByTenantIdAndPersonId(long tenantId, String personId);

    void updateProjectInfo(long tenantId,  String personId, int workload, int currentProjects, int previousProjects);

    String getDepartmentId(long tenantId, String personId);

}
