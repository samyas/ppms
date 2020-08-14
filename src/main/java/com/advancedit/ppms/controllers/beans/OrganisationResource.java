package com.advancedit.ppms.controllers.beans;

import com.advancedit.ppms.models.files.FileDescriptor;
import com.advancedit.ppms.models.organisation.Address;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.OrganisationType;
import com.advancedit.ppms.models.person.ShortPerson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrganisationResource {

    private String id;
    private long  tenantId;
    private String name;
    private String description;
    private String longDescription;
    private String email;
    private Address address;
    private String phone;
    private String creationDate;
    private String contactEmail;
    private String logoId;
    private ShortPerson responsible;
    private FileDescriptor logo;
    private OrganisationType type;
    private boolean isExtended;
    private List<Department> departments = new ArrayList<>();
}
