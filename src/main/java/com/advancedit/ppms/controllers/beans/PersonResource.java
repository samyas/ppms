package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.Skill;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class PersonResource {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String skype;
    private String photoFileId;
    private ShortDepartment department;
    private boolean valid = false;
    private PersonFunction personfunction;
    private String shortDescription;
    private String status;
    private long tenantId;
    private String job;
    private Integer yearsExperience;
    private String education;
    private String homeAddress;
    private String jobAddress;
    private String certifications;
    private List<Skill> skills;
    private String level;
    @DateTimeFormat(style = "M-")
    private Date lastCvUpdate;
    private String cvFileId;
    @DateTimeFormat(style = "M-")
    private Date startingDateDate;
    private List<ProjectResource> projects;


    private FileDescriptor image;
    private  int workload;
    private int currentProjects;
    private int previousProjects;
}
