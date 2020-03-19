package com.advancedit.ppms.controllers;

import com.advancedit.ppms.service.AttachFileService;
import com.advancedit.ppms.service.DocumentManagementService;
import com.advancedit.ppms.service.FileService;
import com.advancedit.ppms.service.beans.AttachType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

import static com.advancedit.ppms.service.beans.AttachType.ORGANISATION;
import static com.advancedit.ppms.service.beans.AttachType.PERSON;
import static com.advancedit.ppms.utils.SecurityUtils.getCurrentTenantId;

@RestController
@RequestMapping("/api/files")
public class FileController {

	 private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
	 
	 
	@Autowired
	FileService fileService;

	@Autowired
	DocumentManagementService documentManagementService;

	@Autowired
	AttachFileService attachFileService;


	@PostMapping("/upload")
	public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file,
									 @RequestParam("type") AttachType type, @RequestParam("id") String id) throws IOException {
		LOGGER.info("Upload file ...");
		// local variables
		String mimeType = file.getContentType();
		String fileName = file.getOriginalFilename();
		/*DBObject metaData = new BasicDBObject();
		metaData.put("brand", "Audi");
		metaData.put("model", "Audi A3");
		metaData.put("description",
				"Audi german automobile manufacturer that designs, engineers, and distributes automobiles");
		String uploadFilePath = fileService.store(file.getInputStream(), fileName, mimeType, metaData);*/

		String fileKey = attachFileService.generateFileKey(getCurrentTenantId(), type, id, fileName);

		String url = documentManagementService.uploadFile(fileKey, file, Arrays.asList(ORGANISATION, PERSON).contains(type));

		attachFileService.attach(getCurrentTenantId(), type, id, fileName, fileKey, url, mimeType);

		return ResponseEntity.ok(url);
	}


	@RequestMapping(method= RequestMethod.POST, value="/delete")
	public ResponseEntity deleteFile( @RequestParam("key") String key, @RequestParam("type") AttachType type, @RequestParam("id") String id) {
		 documentManagementService.deleteFile(key);
		attachFileService.deleteAttach(getCurrentTenantId(), type, id, key);
		return ResponseEntity.noContent().build();
	}
	

	@RequestMapping(method= RequestMethod.GET, value="/download")
	public ResponseEntity downloadImageFile( @RequestParam("key") String key) {
	 
	   // set file (and path) to be download
	
	  /* GridFsResource file = fileService.getById(id);
	   if (file == null){
		   return ResponseEntity.status(404).body("FILE NOT FOUND with id:" + id);
	   }*/

		byte[] data = documentManagementService.downloadFile(key);
		 String fileName = key.substring(key.lastIndexOf("/") + 1);

		return ResponseEntity.ok()
				.contentLength(data.length)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(new ByteArrayResource(data));
	}
}
