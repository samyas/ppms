package com.advancedit.ppms.models.files;

import com.advancedit.ppms.service.beans.AttachType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class ModuleFile {

	@Id
	private String id;
	private long tenantId;
	private String moduleId;
	private AttachType type;
	private String identifier;
	private String fileName;
	private String key;
	private String url;
	private String contentType;
}
