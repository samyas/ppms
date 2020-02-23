package com.advancedit.ppms.models.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "activation_tokens")
public class ActivationToken {
	@Id
	private String id;
	private String email;
	private String token;
	private long tenantId;
	private Date creationDate;
	
}
