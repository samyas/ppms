package com.advancedit.ppms.service;

import com.advancedit.ppms.external.email.EmailBean;
import com.advancedit.ppms.external.email.EmailClient;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.models.user.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static com.advancedit.ppms.utils.GeneralUtils.encode;
import static java.util.Arrays.asList;


@Service
public class EmailService {
	   /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private EmailClient emailClient;
	
    @Value("${mail.from}")
    private String from;
	
    @Autowired
    private TemplateEngine templateEngine;
    
    public void sendJoinRequestForPerson(Person receiver, Organisation organisation,
										 VerificationToken verificationToken, String domain){

    	EmailBean emailBean = new EmailBean();
    	emailBean.setHtml(true);
    	Map<String, Object> data = new HashMap<>();
		data.put("receiverFirstName", receiver.getFirstName());
		data.put("receiverLastName", receiver.getLastName());
		data.put("moduleName", organisation.getDepartments().stream()
				 .filter( d -> d.getId().equals(receiver.getDepartmentId()))
				.findFirst().map(Department::getName).orElse(null));
		data.put("organisationName", organisation.getName());
		data.put("domain", domain);
		data.put("token",  encode(verificationToken.getEmail() + ":" + verificationToken.getToken()));

		emailBean.setMessage(generateHtmlContent("mail-template", data));

		emailBean.setFrom("abdessalemsamet@gmail.com");
    	emailBean.setTo(asList("imed.romdhani@gmail.com"));
    	emailBean.setSubject("Join 3C");
    	emailClient.send(emailBean);
    }

	public void sendEmailConfirmation(User receiver, VerificationToken verificationToken,
									  String domain){

		EmailBean emailBean = new EmailBean();
		emailBean.setHtml(true);
		Map<String, Object> data = new HashMap<>();
		data.put("receiverFirstName", receiver.getFirstName());
		data.put("receiverLastName", receiver.getLastName());
		data.put("token", encode(verificationToken.getEmail() + ":" + verificationToken.getToken()));
		data.put("domain", domain);
		emailBean.setMessage(generateHtmlContent("confirm-mail-template", data));

		emailBean.setFrom("abdessalemsamet@gmail.com");
		emailBean.setTo(asList( "imed.romdhani@gmail.com"));
		emailBean.setSubject("Welcome to 3C");
		emailClient.send(emailBean);
	}


	public void sendRestPassword(User receiver, VerificationToken verificationToken,
									  String domain){

		EmailBean emailBean = new EmailBean();
		emailBean.setHtml(true);
		Map<String, Object> data = new HashMap<>();
		data.put("receiverFirstName", receiver.getFirstName());
		data.put("receiverLastName", receiver.getLastName());
		data.put("token", encode(verificationToken.getEmail() + ":" + verificationToken.getToken()));
		data.put("domain", domain);
		emailBean.setMessage(generateHtmlContent("reset-password-template", data));

		emailBean.setFrom("abdessalemsamet@gmail.com");
		emailBean.setTo(asList( "imed.romdhani@gmail.com"));
		emailBean.setSubject("3C : Reset Password");
		emailClient.send(emailBean);
	}

    private String generateHtmlContent(String template, Map<String, Object> data){
		Context context = new Context();
		data.forEach(context::setVariable);
		return templateEngine.process(template, context);
	}
	
	/*
   private List<EmailBean>  mapToEmailBean(Email email, GridFSDBFile file, int maxEmails){
    	
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
