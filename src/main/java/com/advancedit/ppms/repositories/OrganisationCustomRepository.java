package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Action;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.models.organisation.SupervisorTerm;

import java.util.Optional;

public interface OrganisationCustomRepository {

    Department addDepartment(long tenantId, String organisationId, Department department);

    void updateDepartment(long tenantId, String organisationId, Department department);

    Optional<Department> getDepartment(long tenantId, String organisationId, String departmentId);

    void deleteDepartment(long tenantId, String organisationId, String departmentId);

    Sector addSector(long tenantId, String organisationId, String departmentId, Sector sector);

    Optional<Sector> getSector(long tenantId, String organisationId, String departmentId, String sectorId);

    FileDescriptor getLogo(long tenantId,  String organisationId);

    void addLogo(long tenantId,  String organisationId,  FileDescriptor fileDescriptor);

    SupervisorTerm addTerm(long tenantId, String organisationId, String departmentId, SupervisorTerm term);

    Optional<Department> getDepartment(long tenantId, String departmentId);

    Optional<SupervisorTerm> getTerm(long tenantId,  String departmentId, String termId);

    void updateTerm(long tenantId, String organisationId, String departmentId, SupervisorTerm term);

    void deleteTerm(long tenantId, String organisationId, String departmentId, String termId);

    Action addAction(long tenantId, String organisationId, String departmentId, Action action);

    void updateAction(long tenantId, String organisationId, String departmentId, Action action);

    void deleteAction(long tenantId, String organisationId, String departmentId, String actionId);

    void addAttachment(long tenantId, String organisationId, String departmentId, String actionId, FileDescriptor fileDescriptor);

    void deleteAttachment(long tenantId, String organisationId, String departmentId, String actionId, String key);
}
