package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.Sector;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrganisationService {


	@Autowired
	private OrganisationRepository organisationRepository;


	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private FileStorageRepository fileStorageRepository;



	public List<Organisation> getAllOrganisations(){
    	return organisationRepository.findAll();
    }

    
    public Organisation getOrganisationById(long tenantId, String id){
    	return organisationRepository.findById(id)
				.filter(o -> o.getTenantId() == tenantId)
				.orElseThrow(() ->
    	new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", id)));
    }

	public Optional<Organisation> getOrganisationByTenantId(long tenantId){
		return Optional.ofNullable(organisationRepository.findByTenantId(tenantId));
	}
    
    public Organisation addOrganisation(long tenantId, String email, Organisation organisation){
		Optional.ofNullable(organisationRepository.findByTenantId(tenantId))
				.ifPresent(o -> {throw new PPMSException("Organisation already created");});
       	organisation.setId(null);
       	organisation.setTenantId(tenantId);
       	organisation.setResponsibleEmail(email);
       	organisation.setDepartments(null);
    	return organisationRepository.save(organisation);
    }
    
    
    public Organisation updateOrganisation(long tenantId, Organisation organisation){
		getOrganisationByIdAndTenantId(tenantId, organisation.getId());
		organisation.setTenantId(tenantId);
    	return organisationRepository.save(organisation);
    	
    }


	public void delete(long tenantId, String id) {
		Organisation savedOrganisation = getOrganisationByIdAndTenantId(tenantId, id);
		if (savedOrganisation != null){
    		if (savedOrganisation.getLogoId() != null){
    			fileStorageRepository.delete(savedOrganisation.getLogoId());
    		}
    		
    		//TODO all related information
    		
    		organisationRepository.deleteById(id);
    	}
		
	}

	private ShortPerson getShortPerson(String personId) {
		return personRepository.findById(personId)
				.map(p -> new ShortPerson(p.getId(), p.getFirstName(), p.getLastName(), p.getPhotoFileId()))
				.orElseThrow(() -> new PPMSException(ErrorCode.PERSON_ID_NOT_FOUND,
						String.format("Person id not found '%s'.", personId)));

	}
	public String addDepartment(long tenantId, String organisationId, Department department) {
		//getOrganisationByIdAndTenantId(tenantId, organisationId);
		department.setId(new ObjectId().toHexString());
	/*	Optional<ShortPerson> responsable = Optional.ofNullable(department.getResponsible()).map(ShortPerson::getPersonId)
				.filter(StringUtils::isNotBlank)
				.map(this::getShortPerson);
		responsable.ifPresent(department::setResponsible);*/
		return organisationRepository.addDepartment(tenantId, organisationId, department).getId();
	}

	public String updateDepartment(long tenantId, String organisationId, String departmentId, Department department) {
		Organisation organisation = getOrganisationByIdAndTenantId(tenantId, organisationId);
		department.setId(departmentId);
		organisationRepository.updateDepartment(tenantId, organisationId, department);
		return departmentId;
	}

	public Department getDepartment(long tenantId, String organisationId, String departmentId) {
		return organisationRepository.getDepartment(tenantId, organisationId, departmentId)
				.orElseThrow(() -> new IllegalStateException(String.format("Department [%s] not found", departmentId)));
	}

	public void deleteDepartment(long tenantId, String organisationId, String departmentId) {
		getOrganisationByIdAndTenantId(tenantId, organisationId);
		 organisationRepository.deleteDepartment(tenantId, organisationId, departmentId);

	}


	public String addSector(long tenantId, String organisationId, String departmentId, Sector sector) {
		sector.setId(new ObjectId().toHexString());
		getOrganisationByIdAndTenantId(tenantId, organisationId);
		return organisationRepository.addSector(tenantId, organisationId, departmentId, sector).getId();
	}


	public Sector updateSector(long tenantId, String organisationId, String departmentId, String sectorId, Sector sector) {
		// TODO Auto-generated method stub
		return null;
	}


	public Sector getSector(long tenantId, String organisationId, String departmentId, String sectorId) {
		return organisationRepository.getSector(tenantId, organisationId, departmentId, sectorId)
				.orElseThrow(() -> new IllegalStateException(String.format("Sector [%s] not found", sectorId)));

	}


	public void deleteSector(long tenantId, String organisationId, String departmentId, String sectorId) {
		// TODO Auto-generated method stub
		
	}

	public Organisation getOrganisationByIdAndTenantId(long tenantId , String id){
		return organisationRepository.findById(id).filter(o -> o.getTenantId() == tenantId).orElseThrow(() ->
				new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", id)));

	}
   
}
