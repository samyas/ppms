package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.MemberResource;
import com.advancedit.ppms.controllers.beans.ProjectResource;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.organisation.SupervisorTerm;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ProjectPresenter {

    public static ProjectResource toResource(Project project, Organisation organisation, String personId, boolean isAdmin, List<Person> personList){
        Department department = getDepartment(project.getDepartmentId(), organisation);
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
        projectResource.setTeam(project.getTeam().stream().map(m -> convert(m, personList, Collections.emptyList())).collect(Collectors.toList()));
        projectResource.setMembers(department.getSupervisorTerms().stream().map(st ->  convert(st, project.getMembers(), personList)).collect(Collectors.toList()));
        projectResource.setCreator(convert(project.getCreator(), personList));
        projectResource.setAssignedTo(project.getAssignedTo());
        projectResource.setDepartment(new ShortDepartment(department.getDepartmentId(), department.getName()));
        projectResource.setLogo(project.getImage());
        projectResource.setLogoId(project.getLogoId());
        projectResource.setTechnologies(project.getTechnologies());
        projectResource.setOrganisationsId(project.getOrganisationsId());
        projectResource.setTenantId(project.getTenantId());
        projectResource.setAttachments(project.getAttachments());
        projectResource.setExtended(isAdmin || (personId != null && isBelongToProjectTeam(personId, project)));
        projectResource.setCanEdit(personId != null &&  canEdit(personId, project, isAdmin));
        projectResource.setCanDelete(personId != null && canDelete(personId, project, isAdmin));
        projectResource.setMaxTeamMembers(department.getMaxTeamNbr());
        projectResource.setTotalMembers(project.getMembers().size() + project.getTeam().size());

        if (projectResource.isExtended()) {
            projectResource.setGoals(project.getGoals());
            projectResource.getGoals().sort(ProjectPresenter::compare);
            projectResource.setGoals(projectResource.getGoals().stream().map( g -> convert(g, personList)).collect(Collectors.toList()));
            projectResource.setBudget(project.getBudget());
            projectResource.setProgress(calculateProgress(project.getGoals()));
            projectResource.setNextAction(projectResource.getGoals().stream().filter(g -> (g.getIsAction() != null && g.getIsAction() == Boolean.TRUE) &&
                    isNotEmpty(g.getActionId()) && !GoalStatus.COMPLETED.equals(g.getStatus())).findFirst().orElse(null));
        }
        return projectResource;
    }
     private static int calculateProgress(List<Goal> goals){
        List<Goal> actions = goals.stream().filter(g -> (g.getIsAction() != null && g.getIsAction() == Boolean.TRUE)).collect(Collectors.toList());
        int nbrActions = actions.size() ;
        int completedActions = (int) actions.stream().filter(a -> a.getStatus().equals(GoalStatus.COMPLETED)).count();
         return  Math.floorDiv(completedActions * 100 , nbrActions);
     }

    private static MemberResource convert(Member member, List<Person> personList, List<SupervisorTerm> terms){
        if (member == null) return null;
        MemberResource memberResource = new MemberResource();

        Optional<Person> person = personList.stream().filter(p -> p.getId().equals(member.getPersonId())).findFirst();
        person.ifPresent( p ->  {
            memberResource.setFirstName(p.getFirstName());
            memberResource.setLastName(p.getLastName());
            memberResource.setPersonId(p.getId());
            memberResource.setSigned(member.getSigned());
            Optional.ofNullable(p.getImage()).ifPresent( i -> memberResource.setImageId(i.getUrl()));
        });

        Optional<SupervisorTerm> term = terms.stream().filter(t -> t.getTermId().equals(member.getTermId())).findFirst();
        term.ifPresent( t -> {
            memberResource.setTermId(t.getTermId());
            memberResource.setTermName(t.getName());
        });
        return memberResource;
    }

    private static MemberResource convert(SupervisorTerm term, List<Member> members, List<Person> personList) {
        if (term == null) return null;
        MemberResource memberResource = new MemberResource();
        memberResource.setTermId(term.getTermId());
        memberResource.setTermName(term.getName());
        Optional<Member> assignedMember = members.stream().filter(m -> term.getTermId().equals(m.getTermId())).findFirst();
        if (assignedMember.isPresent()){
            Optional<Person> person = personList.stream().filter(p -> p.getId().equals(assignedMember.get().getPersonId())).findFirst();
            person.ifPresent(p -> {
                memberResource.setFirstName(p.getFirstName());
                memberResource.setLastName(p.getLastName());
                memberResource.setPersonId(p.getId());
                memberResource.setSigned(assignedMember.get().getSigned());
                Optional.ofNullable(p.getImage()).ifPresent(i -> memberResource.setImageId(i.getUrl()));
            });
        }
        return memberResource;

    }
        private static ShortPerson convert(ShortPerson member, List<Person> personList){
        if (member == null) return null;
        Optional<Person> person = personList.stream().filter(p -> p.getId().equals(member.getPersonId())).findFirst();
        person.ifPresent( p ->  {
            member.setFirstName(p.getFirstName());
            member.setLastName(p.getLastName());
            Optional.ofNullable(p.getImage()).ifPresent( i -> member.setImageId(i.getUrl()));
        });
        return member;
    }

    private static Task convert(Task task, List<Person> personList){
        if (task == null) return null;
        if(!CollectionUtils.isEmpty(task.getAssignedTo())){
            task.setAssignedTo(task.getAssignedTo().stream().map(p -> convert(p, personList)).collect(Collectors.toList()));
        }
        task.setCreatedBy(convert(task.getCreatedBy(), personList));
        return task;
    }

    private static Goal convert(Goal goal, List<Person> personList){
        if (goal == null) return null;
        goal.setCreatedBy(convert(goal.getCreatedBy(), personList));
        goal.setTasks(goal.getTasks().stream().map( t -> convert(t, personList)).collect(Collectors.toList()));
        return goal;
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

    private static boolean canDelete(String personId, Project project, boolean isAdmin){
        return (ProjectStatus.PROPOSAL.equals(project.getStatus()) && (personId.equals(project.getCreator().getPersonId()) || isAdmin)
        && (project.getTeam().isEmpty() && project.getMembers().isEmpty()));
    }

    private static Department getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getDepartmentId().equals(departmentId)).findFirst()
               .orElseThrow(() -> new PPMSException("Department not found"));
    }


    private static int compare(Goal o1, Goal o2) {
        if ( o1.getStartDate() != null && (o2.getStartDate() == null || o1.getStartDate().before(o2.getStartDate()))) return -1;
        return 2;
    }
}
