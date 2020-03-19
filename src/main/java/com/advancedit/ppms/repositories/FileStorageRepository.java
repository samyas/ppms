package com.advancedit.ppms.repositories;

import java.io.InputStream;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;

public interface FileStorageRepository {

	 public String store(InputStream inputStream, String fileName, String contentType, DBObject metaData);
			 
	public GridFSFile retrive(String fileName);

	public GridFsResource getById(String id);

	public GridFSFile getByFilename(String filename);

	public GridFSFindIterable findAll();
	
	public void delete(String id);
}
