package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.files.ModuleFile;
import com.advancedit.ppms.models.organisation.*;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ModuleFileRepository;
import com.advancedit.ppms.service.beans.AttachType;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isEmpty;


@Service
public class OrganisationService {


	@Autowired
	private OrganisationRepository organisationRepository;


	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private FileStorageRepository fileStorageRepository;


	@Autowired
	private ModuleFileRepository moduleFileRepository;



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

	public Organisation getOrganisationByIdAndTenantId(long tenantId , String id){
		return organisationRepository.findById(id).filter(o -> o.getTenantId() == tenantId).orElseThrow(() ->
				new PPMSException(ErrorCode.ORGANISATION_ID_NOT_FOUND, String.format("Organisation id not found '%s'.", id)));

	}

	/*************************** DEPARTMENTS *************************************/
	public String addDepartment(long tenantId, String organisationId, Department department) {
		//getOrganisationByIdAndTenantId(tenantId, organisationId);
		department.setDepartmentId(new ObjectId().toHexString());
	/*	Optional<ShortPerson> responsable = Optional.ofNullable(department.getResponsible()).map(ShortPerson::getPersonId)
				.filter(StringUtils::isNotBlank)
				.map(this::getShortPerson);
		responsable.ifPresent(department::setResponsible);*/
		return organisationRepository.addDepartment(tenantId, organisationId, department).getDepartmentId();
	}

	public String updateDepartment(long tenantId, String organisationId, String departmentId, Department department) {
		Department departmentToUpdate = getDepartment(tenantId, organisationId, departmentId);
		departmentToUpdate.setName(department.getName());
		departmentToUpdate.setMaxTeamNbr(department.getMaxTeamNbr());
		departmentToUpdate.setDescription(department.getDescription());
		departmentToUpdate.setResponsible(department.getResponsible());
		departmentToUpdate.setLongDescription(department.getLongDescription());
		organisationRepository.updateDepartment(tenantId, organisationId, departmentToUpdate);
		return departmentId;
	}

	public Department getDepartment(long tenantId,  String departmentId) {
		return organisationRepository.getDepartment(tenantId, departmentId)
				.orElseThrow(() -> new IllegalStateException(String.format("Department [%s] not found", departmentId)));
	}

	public Department getDepartment(long tenantId, String organisationId, String departmentId) {
		return organisationRepository.getDepartment(tenantId, organisationId, departmentId)
				.orElseThrow(() -> new IllegalStateException(String.format("Department [%s] not found", departmentId)));
	}

	public void deleteDepartment(long tenantId, String organisationId, String departmentId) {
		getOrganisationByIdAndTenantId(tenantId, organisationId);
		 organisationRepository.deleteDepartment(tenantId, organisationId, departmentId);

	}


	/******************************* TERMS *************************************/
	public Optional<SupervisorTerm> getTerm(long tenantId, String departmentId, String termId) {
		return organisationRepository.getTerm(tenantId, departmentId, termId);
	}

	public String addTerm(long tenantId, String organisationId, String departmentId, SupervisorTerm term) {
		term.setTermId(new ObjectId().toHexString());
		return organisationRepository.addTerm(tenantId, organisationId, departmentId, term).getTermId();
	}

	public String updateTerm(long tenantId, String organisationId, String departmentId, String termId,  SupervisorTerm term) {
		term.setTermId(termId);
		organisationRepository.updateTerm(tenantId, organisationId, departmentId, term);
		return termId;
	}

	public void deleteTerm(long tenantId, String organisationId, String departmentId, String termId) {
		organisationRepository.deleteTerm(tenantId, organisationId, departmentId, termId);
	}

	/******************************* ACTIONS *************************************/

	public String addAction(long tenantId, String organisationId, String departmentId, Action action) {
		action.setActionId(new ObjectId().toHexString());
		if (action.getAttachmentList() != null){
            action.setAttachmentList( getFileDescriptors(tenantId, action.getAttachmentList()));
        }
		String actionId = organisationRepository.addAction(tenantId, organisationId, departmentId, action).getActionId();
		//files.keySet().forEach( id -> unlinkedFileRepository.deleteById(id));
		return actionId;
	}

	public Action getAction(long tenantId, String organisationId, String departmentId, String actionId) {
		return organisationRepository.getDepartment(tenantId, organisationId, departmentId)
				.flatMap(d -> d.getActions().stream().filter(a -> a.getActionId().equals(actionId)).findFirst())
				.orElseThrow(() -> new IllegalStateException(String.format("Action [%s] not found", departmentId)));
	}

	public String updateAction(long tenantId, String organisationId, String departmentId, String actionId,  Action action) {
		action.setActionId(actionId);
        if (action.getAttachmentList() != null){
            action.setAttachmentList( getFileDescriptors(tenantId, action.getAttachmentList()));
        }
		organisationRepository.updateAction(tenantId, organisationId, departmentId, action);
		return actionId;
	}

	public void deleteAction(long tenantId, String organisationId, String departmentId, String actionId) {
		organisationRepository.deleteAction(tenantId, organisationId, departmentId, actionId);
	}


    private List<FileDescriptor> getFileDescriptors(long tenantId,  List<FileDescriptor> fileDescriptors){
        return fileDescriptors.stream()
                .map(fd -> isEmpty(fd.getKey()) ? getFileDescriptorByUrl(tenantId, fd.getUrl()): fd)
                .collect(Collectors.toList());
    }

    private FileDescriptor getFileDescriptorByUrl(long tenantId, String url){
        ModuleFile moduleFile = moduleFileRepository.findByTenantIdAndUrlAndType(tenantId, url, AttachType.MODULE);
        if (moduleFile == null){
            throw new PPMSException(String.format("Linked File %s not found", url));
        }
        return  new FileDescriptor(moduleFile.getFileName(), moduleFile.getKey(), url, moduleFile.getContentType());
    }


  /*  private Map.Entry<String, FileDescriptor> getFileDescriptorByUrl(long tenantId, String url){
		 UnlinkedFile unlinkedFile = unlinkedFileRepository.findByTenantIdAndUrlAndAttachType(tenantId, url, AttachType.MODULE);
		 if (unlinkedFile == null){
		 	throw new PPMSException(String.format("Linked File %s not found", url));
		 }
		 return new AbstractMap.SimpleEntry<>(unlinkedFile.getId(), new FileDescriptor(unlinkedFile.getFileName(), unlinkedFile.getKey(), url, unlinkedFile.getContentType()));
	 }*/

	/*************************** SECTORS *************************************/


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


   
}
