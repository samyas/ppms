package com.advancedit.ppms.service;

import com.advancedit.ppms.repositories.FileStorageRepository;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileService {

	@Autowired
	private FileStorageRepository fileStorageRepository;
	

	 public String store(InputStream inputStream, String fileName,
	   String contentType, DBObject metaData) {
	  return fileStorageRepository
	    .store(inputStream, fileName, contentType, metaData);
	 }

	
	 public GridFsResource getById(String id) {
	  return fileStorageRepository.getById(id);
	 }

	
	
}
