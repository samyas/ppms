package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.project.Project;

public class ProjectPresenter {

    public static ProjectResource toResource(Project project, Organisation organisation){
        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(project.getProjectId());
        projectResource.setName(project.getName());
        projectResource.setShortDescription(project.getShortDescription());
        projectResource.setDescription(project.getDescription());
        projectResource.setType(project.getType());
        projectResource.setStatus(project.getStatus());
        projectResource.setCategory(project.getCategory());
        projectResource.setSectors(project.getSectors());
        projectResource.setKeywords(project.getKeywords());
        projectResource.setStartDate(project.getStartDate());
        projectResource.setEndDate(project.getEndDate());
        projectResource.setApplies(project.getApplies());
        projectResource.setTeam(project.getTeam());
        projectResource.setSupervisor(project.getSupervisor());
        projectResource.setExaminator(project.getExaminator());
        projectResource.setCreator(project.getCreator());
        projectResource.setAssignedTo(project.getAssignedTo());
        projectResource.setDepartment(getDepartment(project.getDepartmentId(), organisation));
        projectResource.setBudget(project.getBudget());
        projectResource.setLogoId(project.getLogoId());
        projectResource.setGoals(project.getGoals());
        projectResource.setTechnologies(project.getTechnologies());
        projectResource.setOrganisationsId(project.getOrganisationsId());
        projectResource.setTenantId(project.getTenantId());
        return projectResource;
    }

    public static ProjectResource toShortResource(Project project, Organisation organisation){
        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(project.getProjectId());
        projectResource.setName(project.getName());
        projectResource.setShortDescription(project.getShortDescription());
        projectResource.setDescription(project.getDescription());
        projectResource.setType(project.getType());
        projectResource.setStatus(project.getStatus());
        projectResource.setCategory(project.getCategory());
        projectResource.setSectors(project.getSectors());
        projectResource.setKeywords(project.getKeywords());
        projectResource.setStartDate(project.getStartDate());
        projectResource.setEndDate(project.getEndDate());
        projectResource.setApplies(project.getApplies());
        projectResource.setTeam(project.getTeam());
        projectResource.setSupervisor(project.getSupervisor());
        projectResource.setExaminator(project.getExaminator());
        projectResource.setCreator(project.getCreator());
        projectResource.setAssignedTo(project.getAssignedTo());
        projectResource.setDepartment(getDepartment(project.getDepartmentId(), organisation));
        projectResource.setBudget(project.getBudget());
        projectResource.setLogoId(project.getLogoId());
        projectResource.setTechnologies(project.getTechnologies());
        projectResource.setOrganisationsId(project.getOrganisationsId());
        projectResource.setTenantId(project.getTenantId());
        return projectResource;
    }

    private static ShortDepartment getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getId().equals(departmentId)).findFirst()
               .map(d -> new ShortDepartment(d.getId(), d.getName())) .orElse(null);
    }
}
