package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.OrganisationResource;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.ShortPerson;

public class OrganisationPresenter {

    public static OrganisationResource toResource(Organisation organisation, Person person){
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
        organisationResource.setResponsible(new ShortPerson(person.getId(), person.getFirstName(), person.getLastName(),
                person.getPhotoFileId()));
        organisationResource.setType(organisation.getType());
        organisationResource.setDepartments(organisation.getDepartments());
        return organisationResource;
    }
}
