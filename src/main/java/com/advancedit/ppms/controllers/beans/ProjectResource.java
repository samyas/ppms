package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.Goal;
import com.advancedit.ppms.models.project.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectResource {

    private String projectId;
    private String name;
    private String shortDescription;
    private String description;
    private String type;
    private String status;
    private String statusCode;
    private String category;
    private List<String> sectors = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    @DateTimeFormat(style = "M-")
    private Date startDate;
    @DateTimeFormat(style = "M-")
    private Date endDate;
    private List<Apply> applies = new ArrayList<>();
    private List<Member> team = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private ShortPerson creator;
    private List<ShortPerson> assignedTo = new ArrayList<>();
    private List<FileDescriptor> attachments;
    private ShortDepartment department;
    private String budget;
    private String logoId;
    private List<Goal> goals = new ArrayList<>();
    private List<String> technologies = new ArrayList<>();;
    private List<String> organisationsId = new ArrayList<>();
    private long tenantId;
    private boolean extended;
    private boolean edit;
    private Goal nextAction;
}
