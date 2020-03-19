  package com.advancedit.ppms.configs;

import com.advancedit.ppms.utils.GeneralUtils;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

import static com.advancedit.ppms.utils.GeneralUtils.decode;

  @Configuration
@PropertySource("classpath:application.properties")
public class ExternalServiceConfig {

   @Value("${cloud.aws.credentials.accessKey}")
   String accessKey;
   @Value("${cloud.aws.credentials.secretKey}")
   String accessSecret;

   @Value("${cloud.aws.region.static}") String region;

   @Value("${spring.application.name}")
   private String appName;

@Bean
   public AmazonS3 amazonS3Client(//AWSCredentialsProvider credentialsProvider
                                 ) {


       AWSCredentials credentials = new BasicAWSCredentials(decode(accessKey),decode(accessSecret));
       AWSCredentialsProvider credentialsProvider1 =   new AWSStaticCredentialsProvider(credentials);
      /* BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
               session_creds.getAccessKeyId(),
               session_creds.getSecretAccessKey(),
               session_creds.getSessionToken());*/
       return AmazonS3ClientBuilder
               .standard()
               .withCredentials(credentialsProvider1)
               .withRegion(region)
               .build();
   }


}