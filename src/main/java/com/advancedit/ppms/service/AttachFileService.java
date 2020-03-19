package com.advancedit.ppms.service;

import com.advancedit.ppms.controllers.beans.Apply;
import com.advancedit.ppms.controllers.beans.Assignment;
import com.advancedit.ppms.controllers.beans.Assignment.Action;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.*;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.repositories.OrganisationRepository;
import com.advancedit.ppms.repositories.PersonRepository;
import com.advancedit.ppms.repositories.ProjectRepository;
import com.advancedit.ppms.service.beans.AttachType;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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
