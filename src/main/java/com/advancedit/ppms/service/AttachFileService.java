package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.files.ModuleFile;
import com.advancedit.ppms.models.organisation.Action;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import com.advancedit.ppms.repositories.ModuleFileRepository;
import com.advancedit.ppms.service.beans.AttachType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service
public class AttachFileService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

    @Autowired
	private ModuleFileRepository moduleFileRepository;

	public String generateFileKey(long tenantId, AttachType attachType, String identifier, String fileName){
		String organisationFolder = String.format("ORG-%d/", tenantId);
		List<String> ids = asList(identifier.split((":")));
		String relativePath = "";
		switch (attachType){
			case PROJECT:
				relativePath = String.format("projects/%s/%s", ids.get(0), fileName);
				break;
			case GOAL:
				relativePath = String.format("projects/%s/goals/%s", ids.get(0), fileName);
				break;
			case TASK:
				relativePath = String.format("projects/%s/tasks/%s", ids.get(0), fileName);
				break;
			case MODULE:
				relativePath = String.format("modules/%s/%s", ids.get(0), fileName);
				break;
			case ORGANISATION:
				relativePath = String.format("logo/%s", fileName);
				break;
			case PERSON:
				relativePath = String.format("persons/%s", fileName);
				break;
		}
		return organisationFolder + relativePath;
	}

	public void attach(long tenantId, AttachType attachType, String identifier, String fileName, String key, String url, String contentType){
         List<String> ids = asList(identifier.split((":")));
		switch (attachType){
			case PROJECT:
				projectRepository.addAttachment(tenantId, ids.get(0), new FileDescriptor(fileName, key, url, contentType));
				break;
			case GOAL:
				projectRepository.addAttachment(tenantId, ids.get(0), ids.get(1), new FileDescriptor(fileName, key, url, contentType));
				break;
			case TASK:
				projectRepository.addAttachment(tenantId, ids.get(0), ids.get(1), ids.get(2), new FileDescriptor(fileName, key, url, contentType));
				break;
			case ORGANISATION:
				organisationRepository.addLogo(tenantId, ids.get(0), new FileDescriptor(fileName, key, url, contentType));
				break;
			case PERSON:
				personRepository.updateImage(tenantId, ids.get(0), new FileDescriptor(fileName, key, url, contentType));
				break;
			case MODULE:
		//		organisationRepository.addAttachment(tenantId, ids.get(0), ids.get(1), ids.get(2), new FileDescriptor(fileName, key, url, contentType));
		//		break;

				ModuleFile moduleFile = moduleFileRepository.findByTenantIdAndUrlAndType(tenantId, url, AttachType.MODULE);
				if (moduleFile == null){
                    moduleFile = new ModuleFile();
					moduleFile.setModuleId(ids.get(0));
					moduleFile.setType(attachType);
					moduleFile.setContentType(contentType);
					moduleFile.setFileName(fileName);
					moduleFile.setIdentifier(identifier);
					moduleFile.setTenantId(tenantId);
					moduleFile.setKey(key);
					moduleFile.setUrl(url);
					moduleFileRepository.insert(moduleFile);
				}
                break;
            default:

		}
	}


	public void deleteAttach(long tenantId, AttachType attachType, String identifier, String key){
		List<String> ids = asList(identifier.split((":")));
		switch (attachType){
			case PROJECT:
				projectRepository.deleteAttachment(tenantId, ids.get(0), key);
				break;
			case GOAL:
				projectRepository.deleteAttachment(tenantId, ids.get(0), ids.get(1), key);
				break;
			case TASK:
				projectRepository.deleteAttachment(tenantId, ids.get(0), ids.get(1), ids.get(2), key);
				break;
			case MODULE:
				List<Action> actions = organisationRepository.getDepartment(tenantId, ids.get(0)).map(Department::getActions).orElse(Collections.emptyList());
				List<String> existingKeys = actions.stream().map(Action::getAttachmentList).flatMap(Collection::stream).map(FileDescriptor::getKey).collect(Collectors.toList());
				if (existingKeys.contains(key)){
					throw new PPMSException("File already used in actions");
				}
				moduleFileRepository.deleteByTenantIdAndModuleIdAndKey(tenantId, ids.get(0), key);
			//	organisationRepository.deleteAttachment(tenantId, ids.get(0), ids.get(1), ids.get(2), key);
				break;

		}
	}


	public List<ModuleFile> getModuleFiles(long tenantId, String moduleId){
		return moduleFileRepository.findByTenantIdAndModuleId(tenantId, moduleId);
	}


}
