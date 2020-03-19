package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.PPMSException;
//import com.amazonaws.services.s3.model.*;
//import com.amazonaws.util.IOUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class DocumentManagementService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${app.awsServices.bucketName}")
    String defaultBucketName;



    public String uploadFile(String fileKey, MultipartFile multipartFile , boolean isPublic) {
        File file = null;
        try {
             file = convertMultiPartFileToFile(multipartFile);
            PutObjectRequest putObjectRequest = new PutObjectRequest(defaultBucketName, fileKey, file);
            if (isPublic){
                putObjectRequest = putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
           return amazonS3Client.getUrl(defaultBucketName, fileKey).toExternalForm();
        }finally {
            if(file != null)
            file.delete();
        }

    }

    public String uploadFile2(File file) {

        try {
           // file = convertMultiPartFileToFile(multipartFile);
        //    String fileName =/* System.currentTimeMillis() + "_" +*/ multipartFile.getOriginalFilename();
            String key = "ORG 04 test/" + file.getName();
            PutObjectResult putObjectResult = amazonS3Client.putObject(new PutObjectRequest(defaultBucketName, key, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            return amazonS3Client.getUrl(defaultBucketName, key).toExternalForm();
            //  return null;
        }catch (Exception e){
              throw new PPMSException("Fail to upload file to amazon", e);
        }

    }

    public void deleteFile(String fileName) {
           amazonS3Client.deleteObject(new DeleteObjectRequest(defaultBucketName, fileName));
    }

    public  byte[]  downloadFile(String fileName)  {
        S3Object object = amazonS3Client.getObject(defaultBucketName,  fileName);
        try {
           return IOUtils.toByteArray(object.getObjectContent());
        } catch (IOException e) {
           throw new PPMSException("Fail to download file from amazon", e);
        }finally {
            try {
                object.close();
            } catch (IOException e) {
                //Fail to close stream
            }
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}