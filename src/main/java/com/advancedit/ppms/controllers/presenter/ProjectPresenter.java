package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.Member;
import com.advancedit.ppms.models.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectPresenter {

    public static ProjectResource toResource(Project project, Organisation organisation, String personId, boolean isAdmin){
        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(project.getProjectId());
        projectResource.setName(project.getName());
        projectResource.setShortDescription(project.getShortDescription());
        projectResource.setDescription(project.getDescription());
        projectResource.setType(project.getType());
        projectResource.setStatus(project.getStatus().getLabel());
        projectResource.setStatusCode(project.getStatus().name());
        projectResource.setCategory(project.getCategory());
        projectResource.setSectors(project.getSectors());
        projectResource.setKeywords(project.getKeywords());
        projectResource.setStartDate(project.getStartDate());
        projectResource.setEndDate(project.getEndDate());
        projectResource.setApplies(project.getApplies());
        projectResource.setTeam(project.getTeam());
        projectResource.setMembers(project.getMembers());
        projectResource.setCreator(project.getCreator());
        projectResource.setAssignedTo(project.getAssignedTo());
        projectResource.setDepartment(getDepartment(project.getDepartmentId(), organisation));

        projectResource.setLogoId(project.getLogoId());
        projectResource.setTechnologies(project.getTechnologies());
        projectResource.setOrganisationsId(project.getOrganisationsId());
        projectResource.setTenantId(project.getTenantId());
        projectResource.setAttachments(project.getAttachments());
        projectResource.setExtended(isAdmin || (personId != null && isBelongToProjectTeam(personId, project)));
        if (projectResource.isExtended()) {
            projectResource.setGoals(project.getGoals());
            projectResource.setBudget(project.getBudget());
        }
        return projectResource;
    }

    private static boolean isBelongToProjectTeam(String personId, Project project){
        List<String> projectPersonIds = new ArrayList<>();
        Optional.ofNullable(project.getCreator()).map(ShortPerson::getPersonId).ifPresent(projectPersonIds::add);
    //    project.getSupervisors().stream().map(ShortPerson::getPersonId).forEach(projectPersonIds::add);
    //    project.getExaminators().stream().map(ShortPerson::getPersonId).forEach(projectPersonIds::add);
        project.getTeam().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        project.getMembers().stream().map(Member::getPersonId).forEach(projectPersonIds::add);
        return projectPersonIds.contains(personId);
    }

    private static ShortDepartment getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getDepartmentId().equals(departmentId)).findFirst()
               .map(d -> new ShortDepartment(d.getDepartmentId(), d.getName())) .orElse(null);
    }
}
