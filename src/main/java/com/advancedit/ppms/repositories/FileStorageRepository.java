package com.advancedit.ppms.repositories;

import java.io.InputStream;
import java.util.List;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;

public interface FileStorageRepository {

	 public String store(InputStream inputStream, String fileName, String contentType, DBObject metaData);
			 
	public GridFSFile retrive(String fileName);

	public GridFSFile getById(String id);

	public GridFSFile getByFilename(String filename);

	public GridFSFindIterable findAll();
	
	public void delete(String id);
}
