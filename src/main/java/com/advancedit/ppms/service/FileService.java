package com.advancedit.ppms.service;

import com.advancedit.ppms.controllers.FileController;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.files.ModuleFile;
import com.advancedit.ppms.repositories.FileStorageRepository;
import com.advancedit.ppms.service.beans.AttachType;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.advancedit.ppms.service.beans.AttachType.ORGANISATION;
import static com.advancedit.ppms.service.beans.AttachType.PERSON;
import static com.advancedit.ppms.utils.SecurityUtils.getCurrentTenantId;

@Service
public class FileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

	@Autowired
	DocumentManagementService documentManagementService;

	@Autowired
	AttachFileService attachFileService;




	public String uploadFile( MultipartFile file, AttachType type, String id)  {
		LOGGER.info("Upload file ...");

		if (StringUtils.isEmpty(id)){
			throw new PPMSException("File id is empty");
		}
		// local variables
		String mimeType = file.getContentType();
		String fileName = file.getOriginalFilename();
		String fileKey = attachFileService.generateFileKey(getCurrentTenantId(), type, id, fileName);
		String url = documentManagementService.uploadFile(fileKey, file, Arrays.asList(ORGANISATION, PERSON).contains(type));
		attachFileService.attach(getCurrentTenantId(), type, id, fileName, fileKey, url, mimeType);
		return url;
	}


	public void deleteFile( String key,  AttachType type,  String id) {
		documentManagementService.deleteFile(key);
		attachFileService.deleteAttach(getCurrentTenantId(), type, id, key);
	}


	
	
}
