package com.advancedit.ppms.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.advancedit.ppms.external.email.EmailSenderService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


@Service
public class EmailService {
	   /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    

    
	
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	
    @Value("${mail.from}")
    private String from;
	
	
    @Autowired
    private Configuration freemarkerConfig;

  
    
    public String generateEmailContentTemplate(String personId, String senderUserId) throws IOException, TemplateException{
    	
    	/*Person person = personRepository.findOne(personId);
    	if (person == null){
       		throw new UPCException(ErrorCode.PERSON_ID_NOT_FOUND, String.format("Person id not found '%s'.", personId));  		  
    	}
    	
    	User user = userRepository.findOne(senderUserId);
    	if (user == null){
       		throw new UPCException(ErrorCode.USER_ID_NOT_FOUND, String.format("User id not found '%s'.", senderUserId));  		  
    	}
    	
    	
    	Person sender = personRepository.findByEmail(user.getEmail());
    	if (sender == null){
       		throw new UPCException(ErrorCode.PERSON_EMAIL_NOT_FOUND, String.format("Email Person id not found '%s'.", user.getEmail()));  		  
    	}*/
    	
	  	 Map<String, Object> model = new HashMap<>();
	  	 model.put("person", null);
	  	 model.put("sender", null);
	  	 
	  

	  	
	      // set loading location to src/main/resources
	      // You may want to use a subfolder such as /templates here
	      freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/email-templates");
	      
	      Template t = freemarkerConfig.getTemplate("commercial-consultant.ftl");
	      String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
    	

      
    	return text; 	
    }
    
	
	
	public Pair<List<String>, List<String>> sendBulkEmail( int maxEmails ){
		
		
    return null;
	//	return emailSenderService.send(null);
			 //LOGGER.error("Error occur while sending email to (" + bean.getTo() + ")" , e); 
		 //}
		/* if (emailsBean != null && sendEmailNumber < emailsBean.size()){
	      		throw new UPCException(ErrorCode.EMAILS_NOT_SENT_SUCCESSFULLY, "Some email are not send sucessfully.");  		  

		 }*/
	}
	
	
  /*  private List<EmailBean>  mapToEmailBean(Email email, GridFSDBFile file, int maxEmails){
    	
    	List<EmailBean> emailsBean = new ArrayList<>();
    	byte[] binary = null;
    	if (file != null){
    	   binary = getBinaryFile(file);
    	}
    	int i = 0;
    	if (email.getTo() != null){
    		for(String to: email.getTo()){
    			
    			String receiverEmail = to;
    			String receiverName = "";
    			if (to.contains("(") && to.contains(")")){
    				receiverName = to.substring(0, to.indexOf("("));
    				receiverEmail = to.substring(to.indexOf("(") + 1, to.indexOf(")"));
    			}
    			if ((email.getSuccessed() != null && email.getSuccessed().contains(receiverEmail)) 
    					|| (email.getFailed() != null && email.getFailed().contains(receiverEmail))){
    				//Email already sent
 		        	continue;
 		        }
    			
    			EmailBean emailBean = new EmailBean();
    			String body = email.getBody();
    			
    			emailBean.setTo(Arrays.asList(receiverEmail));
				body = body.replaceAll("\\{receiverName\\}", receiverName);
    			emailBean.setSubject(email.getSubject());
		        emailBean.setMessage(body);
		        emailBean.setHtml(true);
		        emailBean.setFrom(email.getFrom());
			    if (file != null && binary != null){
				        emailBean.setAttachementBinary(binary);
				        emailBean.setAttachementFileName(file.getFilename());
				        emailBean.setAttachementFileType(file.getContentType());
			    }
		        emailsBean.add(emailBean);
		        i++;
		        if (maxEmails != 0 && i == maxEmails){
		        	break;
		        }
    		}
    	}
    
    	
    	return emailsBean;
    }
    
    private byte[] getBinaryFile(GridFSDBFile queryFile){
    	byte[] file = null;
    	if(queryFile != null){
    		ByteArrayOutputStream bao = null;
    		try {
    			bao = new ByteArrayOutputStream();
    			queryFile.writeTo(bao);
    			file = bao.toByteArray();
    		} catch (IOException e) {
    			
    		} finally{
    			IOUtils.closeQuietly(bao);
    		}
    	}
    	return file;
    }*/


     
   
}
