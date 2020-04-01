package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonCustomRepository {

    Page<Person> findByTenantIdAndPersonFunctionAndStatus(long tenantId, PersonFunction personfunction, String status,
                                                          String departmentId, Pageable pageable);

    void updateImage(long tenantId,  String personId,  FileDescriptor fileDescriptor);

    Optional<Person> findByTenantIdAndPersonId(long tenantId, String personId);

}
