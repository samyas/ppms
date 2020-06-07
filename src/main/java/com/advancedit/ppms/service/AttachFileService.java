package com.advancedit.ppms.service;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import com.advancedit.ppms.service.beans.AttachType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
public class AttachFileService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

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

		}
	}


}
