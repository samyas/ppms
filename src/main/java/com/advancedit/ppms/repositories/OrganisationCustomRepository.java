package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Sector;

import java.util.Optional;

public interface OrganisationCustomRepository {

    Department addDepartment(String organisationId, Department department);

    void updateDepartment(String organisationId, Department department);

    Optional<Department> getDepartment(String organisationId, String departmentId);

    void deleteDepartment(String organisationId, String departmentId);

    Sector addSector(String organisationId, String departmentId, Sector sector);

    Optional<Sector> getSector(String organisationId, String departmentId, String sectorId);
}
