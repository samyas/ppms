package com.advancedit.ppms.models.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "verification_tokens")
public class VerificationToken {
	@Id
	private String id;
	private String email;
	private UUID token;
	private Date creationDate;
	private long tenantId;
	
}
