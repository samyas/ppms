package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.OrganisationResource;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.ShortPerson;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class OrganisationPresenter {

    public static OrganisationResource toResource(Organisation organisation, Person person, List<Person> departmentResponsibles){
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setId(organisation.getId());
        organisationResource.setTenantId(organisation.getTenantId());
        organisationResource.setName(organisation.getName());
        organisationResource.setDescription(organisation.getDescription());
        organisationResource.setLongDescription(organisation.getLongDescription());
        organisationResource.setEmail(organisation.getEmail());
        organisationResource.setAddress(organisation.getAddress());
        organisationResource.setPhone(organisation.getPhone());
        organisationResource.setCreationDate(organisation.getCreationDate());
        organisationResource.setContactEmail(organisation.getContactEmail());
        organisationResource.setLogoId(organisation.getLogoId());
        organisationResource.setLogo(organisation.getLogo());
        organisationResource.setResponsible(new ShortPerson(person.getId(), person.getFirstName(), person.getLastName(),
                Optional.ofNullable(person.getImage()).map(FileDescriptor::getUrl).orElse(null)));
        organisationResource.setType(organisation.getType());
        organisationResource.setDepartments(organisation.getDepartments().stream().map(d -> convert(d, departmentResponsibles)).collect(Collectors.toList()));
        return organisationResource;
    }

    public static Department convert(Department department, List<Person> personList){
        if (department == null) return null;
        department.setActions(emptyList());
        department.setSupervisorTerms(emptyList());
        if (department.getResponsible() == null) return department;
        Optional<Person> person = personList.stream().filter(p -> p.getId().equals(department.getResponsible().getPersonId())).findFirst();
        person.ifPresent( p ->  {if (p.getImage() != null) {
            ShortPerson shortPerson = department.getResponsible();
            shortPerson.setImageId(Optional.ofNullable(p.getImage()).map(FileDescriptor::getUrl).orElse(null));
            department.setResponsible(shortPerson);
        }});
        return department;
    }
}
