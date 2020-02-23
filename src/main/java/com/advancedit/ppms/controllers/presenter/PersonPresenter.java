package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.PersonResource;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.Person;

public class PersonPresenter {

    public static PersonResource toResource(Person person, Organisation organisation){
        PersonResource personResource = new PersonResource();
        personResource.setId(person.getId());
        personResource.setEmail(person.getEmail());
        personResource.setFirstName(person.getFirstName());
        personResource.setLastName(person.getLastName());
        personResource.setPhone(person.getPhone());
        personResource.setSkype(person.getSkype());
        personResource.setPhotoFileId(person.getPhotoFileId());
        personResource.setDepartment(getDepartment(person.getDepartmentId(), organisation));
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
        return personResource;
    }


    private static ShortDepartment getDepartment(String departmentId, Organisation organisation){
       return organisation.getDepartments().stream().filter(d -> d.getId().equals(departmentId)).findFirst()
               .map(d -> new ShortDepartment(d.getId(), d.getName())) .orElse(null);
    }
}
