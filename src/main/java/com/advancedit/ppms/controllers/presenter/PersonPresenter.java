package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.PersonResource;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.user.User;

import java.util.Optional;

import static com.advancedit.ppms.models.person.PersonFunction.isStaff;

public class PersonPresenter {


    public static PersonResource toResource(Person person, Organisation organisation, Optional<User> relatedUser){
        PersonResource resource = toResource(person, organisation);
        resource.setValid(relatedUser.map(User::isEnabled).orElse(false));
        return resource;
    }
    public static PersonResource toResource(Person person, Organisation organisation){
       Optional<Department> department = organisation.getDepartments()
               .stream().filter(d -> d.getDepartmentId().equals(person.getDepartmentId())).findFirst();

        if (department.isPresent() && isStaff(person.getPersonfunction())){
            Optional.ofNullable(department.get().getResponsible())
                    .map(ShortPerson::getPersonId)
                    .map(pId -> {
                        if (pId.equals(person.getId())) {
                            person.setPersonfunction(PersonFunction.MODEL_LEADER);
                        } else {
                            person.setPersonfunction(PersonFunction.STAFF);
                        }
                        return person;
                    }).orElseGet( () -> {
                person.setPersonfunction(PersonFunction.STAFF);
                        return person;
            });
       }
    //    PersonFunction calculatedPersonFunction = Optional.ofNullable(department.getResponsible())
      //          .map(p -> p.getPersonId()).map( pId -> pId.equals(person.getId()) && isStaff(person.getPersonfunction()))
        PersonResource personResource = new PersonResource();
        personResource.setId(person.getId());
        personResource.setEmail(person.getEmail());
        personResource.setFirstName(person.getFirstName());
        personResource.setLastName(person.getLastName());
        personResource.setPhone(person.getPhone());
        personResource.setSkype(person.getSkype());
        personResource.setPhotoFileId(person.getPhotoFileId());
        personResource.setDepartment(department.map(d -> new ShortDepartment(d.getDepartmentId(), d.getName())) .orElse(null));
        personResource.setValid(person.isRegistered());
        personResource.setPersonfunction(person.getPersonfunction());
        personResource.setStatus(person.getStatus());
        personResource.setTenantId(person.getTenantId());
        personResource.setJob(person.getJob());
        personResource.setYearsExperience(person.getYearsExperience());
        personResource.setEducation(person.getEducation());
        personResource.setHomeAddress(person.getHomeAddress());
        personResource.setJobAddress(person.getJobAddress());
        personResource.setCertifications(person.getCertifications());
        personResource.setSkills(person.getSkills());
        personResource.setLevel(person.getLevel());
        personResource.setLastCvUpdate(person.getLastCvUpdate());
        personResource.setCvFileId(person.getCvFileId());
        personResource.setStartingDateDate(person.getStartingDateDate());
        personResource.setImage(person.getImage());
        personResource.setCurrentProjects(person.getCurrentProjects());
        personResource.setWorkload(person.getWorkload());
        personResource.setPreviousProjects(person.getPreviousProjects());
        return personResource;
    }


    private static ShortDepartment getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getDepartmentId().equals(departmentId)).findFirst()
               .map(d -> new ShortDepartment(d.getDepartmentId(), d.getName())) .orElse(null);
    }
}
