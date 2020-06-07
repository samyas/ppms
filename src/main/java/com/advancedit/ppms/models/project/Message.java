package com.advancedit.ppms.models.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.advancedit.ppms.models.person.ShortPerson;

@Data
@NoArgsConstructor
@Document
public class Message {
	@Id
	private String messageId;

	private String content;
	private Date start;
	private ShortPerson writer;
	private Date modifiedTime;

}
