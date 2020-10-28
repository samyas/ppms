/*
 * 
 */
package com.advancedit.ppms.models.project;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Apply {

	private String description;
	@DateTimeFormat(style = "M-")
	private Date submitDate;
	private List<FileDescriptor> files = new ArrayList<>();
	private ShortPerson createdBy;
    private String termId;
}


