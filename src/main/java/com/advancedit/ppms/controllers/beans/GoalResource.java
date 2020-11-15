package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.project.GoalStatus;
import com.advancedit.ppms.models.project.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class GoalResource {

    private String goalId;

    private String name;
    private String shortDescription;
    private String description;
    private GoalStatus status;
    @DateTimeFormat(style = "M-")
    private Date startDate;
    @DateTimeFormat(style = "M-")
    private Date endDate;

    @DateTimeFormat(style = "M-")
    private Date actualStartDate;
    @DateTimeFormat(style = "M-")
    private Date actualEndDate;

    private Boolean isAction;
    private Boolean beforeStart;
    private ShortPerson createdBy;
    private int score;
    private String actionId;
    private List<Task> tasks = new ArrayList<>();
    private List<FileDescriptor> attachmentsArrayList = new ArrayList<>();
}
