package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
        projectResource.setEdit(canEdit(personId, project, isAdmin));
        if (projectResource.isExtended()) {
            projectResource.setGoals(project.getGoals());
            projectResource.getGoals().sort(ProjectPresenter::compare);
            projectResource.setBudget(project.getBudget());
            projectResource.setNextAction(projectResource.getGoals().stream().filter(g -> (g.getIsAction() != null && g.getIsAction() == Boolean.TRUE) &&
                    isNotEmpty(g.getActionId()) && !GoalStatus.COMPLETED.equals(g.getStatus())).findFirst().orElse(null));
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

    private static boolean canEdit(String personId, Project project, boolean isAdmin){
        if (Arrays.asList(ProjectStatus.PROPOSAL , ProjectStatus.ASSIGNED).contains(project.getStatus())){
           return isAdmin || isBelongToProjectTeam(personId, project);
        }
        return false;
    }

    private static ShortDepartment getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getDepartmentId().equals(departmentId)).findFirst()
               .map(d -> new ShortDepartment(d.getDepartmentId(), d.getName())) .orElse(null);
    }


    private static int compare(Goal o1, Goal o2) {
        if ( o1.getStartDate() != null && (o2.getStartDate() == null || o1.getStartDate().before(o2.getStartDate()))) return -1;
        return 2;
    }
}
