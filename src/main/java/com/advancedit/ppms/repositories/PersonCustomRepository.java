package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonCustomRepository {

    Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, PersonFunction personfunction, String status,
                                                          String departmentId, Pageable pageable);


}
