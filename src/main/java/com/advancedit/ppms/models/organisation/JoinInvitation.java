package com.advancedit.ppms.models.organisation;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;

@Document(collection = "invitations")
public class JoinInvitation {

	@Id
	private String id;
	private String email;
    private ShortPerson sender;
    private PersonFunction personfunction;
    
	public JoinInvitation() {
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public ShortPerson getSender() {
		return sender;
	}



	public void setSender(ShortPerson sender) {
		this.sender = sender;
	}



	public PersonFunction getPersonfunction() {
		return personfunction;
	}



	public void setPersonfunction(PersonFunction personfunction) {
		this.personfunction = personfunction;
	}
	
	
    
}
