package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Sector;

import java.util.Optional;

public interface OrganisationCustomRepository {

    Department addDepartment(long tenantId, String organisationId, Department department);

    void updateDepartment(long tenantId, String organisationId, Department department);

    Optional<Department> getDepartment(long tenantId, String organisationId, String departmentId);

    void deleteDepartment(long tenantId, String organisationId, String departmentId);

    Sector addSector(long tenantId, String organisationId, String departmentId, Sector sector);

    Optional<Sector> getSector(long tenantId, String organisationId, String departmentId, String sectorId);
}
