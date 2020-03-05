package com.advancedit.ppms.external.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class EmailClient {
	
	@Autowired
	private JavaMailSender mailSender;
	



	   /** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);

	
	public void send(EmailBean eParams) {
		if (eParams.isHtml()) {
			try {
				sendHtmlMail(eParams);
			} catch (MessagingException e) {
				logger.error("Could not send email to : {} Error = {}", eParams.getTo(), e.getMessage());
			}
		} else {
			sendPlainTextMail(eParams);
		}
	}
	
	/*public  Pair<List<String>, List<String>> send(List<EmailBean> eParams) {
		 List<String> failedAddress = new ArrayList<String>();
		 List<String> successedAddress = new ArrayList<String>();
		 try {
	
				sendHtmlMail(eParams);
			}catch (MailSendException e) {
				Map<Object, Exception> failMessages = e.getFailedMessages();
				failMessages.forEach((k,v)->{
					if (k instanceof MimeMessage) {
						MimeMessage msg = (MimeMessage) k;
						List<String> toAddresses = new ArrayList<String>();
						Address[] recipients;
						try {
							recipients = msg.getRecipients(Message.RecipientType.TO);
							for (Address address : recipients) {
							    toAddresses.add(address.toString());
							}
							logger.error("Could not send email to : {} Error = {}", toAddresses.get(0), e.getMessage());
							failedAddress.add(toAddresses.get(0));
						} catch (MessagingException e1) {
							logger.error("Failed to retreive email receiver : Error = {}", e1.getMessage());
						}
		
					}
					
				});
			}catch (MessagingException e) {
				logger.error("Could not send bulk  email , Error = {}" , e.getMessage());
				if (eParams != null){
					for(EmailBean eb : eParams){
							failedAddress.add(eb.getTo().get(0));
					}
				}
				//throw new UPCException(ErrorCode.EMAILS_NOT_SENT_SUCCESSFULLY, e.getMessage());
			}
			
			if (eParams != null){
				for(EmailBean eb : eParams){
					if (!failedAddress.contains(eb.getTo().get(0))){
						successedAddress.add(eb.getTo().get(0));
					}
					
				}
			}
			return Pair.of(successedAddress, failedAddress);
	}*/

	private void sendHtmlMail(EmailBean eParams) throws MessagingException {
		boolean isHtml = true;
		MimeMessage message = mailSender.createMimeMessage();
		
		 // pass 'true' to the constructor to create a multipart message
		//MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		//Insert image
//		 helper.setText("<html><body>Here is a cat picture! <img src='cid:id101'/><body></html>", true);
//		 ClassPathResource file = new ClassPathResource("cat.jpg");
//		 helper.addInline("id101", file);
		
		MimeMessageHelper helper = null;
		if (eParams.getAttachementFileName() != null){
			helper = new MimeMessageHelper(message, true);
			   DataSource source = new ByteArrayDataSource(eParams.getAttachementBinary(), eParams.getAttachementFileType());  
		       helper.addAttachment(eParams.getAttachementFileName(), source);
		    				
		}else{
			helper = new MimeMessageHelper(message);
		}
	
		helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		helper.setReplyTo(eParams.getFrom());
		helper.setFrom(eParams.getFrom());
		helper.setSubject(eParams.getSubject());
		helper.setText(eParams.getMessage(), isHtml);
	
		if (eParams.getCc() != null && eParams.getCc().size() > 0) {
			helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
		}
		mailSender.send(message);
	}
	
	
	private void sendHtmlMail(List<EmailBean> eParams) throws MessagingException {
		
		if (eParams != null){
			MimeMessage[] msgs = new MimeMessage[eParams.size()];
			int i = 0;
			for(EmailBean eb: eParams){
				MimeMessage message = mailSender.createMimeMessage();
				fillMimeMessage(message, eb);
				msgs[i] = message;
				i++;
			}
			mailSender.send(msgs);
		}
	}
	
	private void fillMimeMessage(MimeMessage message, EmailBean eParams) throws MessagingException {
		boolean isHtml = true;
		MimeMessageHelper helper = null;
		if (eParams.getAttachementFileName() != null){
			helper = new MimeMessageHelper(message, true);
			   DataSource source = new ByteArrayDataSource(eParams.getAttachementBinary(), eParams.getAttachementFileType());  
		       helper.addAttachment(eParams.getAttachementFileName(), source);
		    				
		}else{
			helper = new MimeMessageHelper(message);
		}
	
		helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		helper.setReplyTo(eParams.getFrom());
		helper.setFrom(eParams.getFrom());
		helper.setSubject(eParams.getSubject());
		helper.setText(eParams.getMessage(), isHtml);
	
		if (eParams.getCc() != null && eParams.getCc().size() > 0) {
			helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
		}
	}
	

	private void sendPlainTextMail(EmailBean eParams) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		eParams.getTo().toArray(new String[eParams.getTo().size()]);
		mailMessage.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		mailMessage.setReplyTo(eParams.getFrom());
		mailMessage.setFrom(eParams.getFrom());
		mailMessage.setSubject(eParams.getSubject());
		mailMessage.setText(eParams.getMessage());
		if (eParams.getCc().size() > 0) {
			mailMessage.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
		}
		mailSender.send(mailMessage);
	}
}
