package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.project.ProjectStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder(toBuilder = true)
//@NoArgsConstructor
public class ProjectFilter {

    private String personId;
    private boolean isStudent;
    private boolean onlyAssignedToPersonId;
    private List<ProjectStatus> statuses;
    private boolean isModuleLeaderOrAdmin;
    private String departmentId;
    private String name;
    private List<String> keywords;



    public ProjectFilter rebuildFilter(){
        List<ProjectStatus> statusList = statuses;
        if (!onlyAssignedToPersonId && (this.isStudent || !isModuleLeaderOrAdmin)){
            statusList =  Collections.singletonList(ProjectStatus.PROPOSAL);
        }
        return this.toBuilder().statuses(statusList).build();
    }
}
