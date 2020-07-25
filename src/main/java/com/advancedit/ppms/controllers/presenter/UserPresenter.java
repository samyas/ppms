package com.advancedit.ppms.controllers.presenter;

import com.advancedit.ppms.controllers.beans.PersonResource;
import com.advancedit.ppms.controllers.beans.UserResource;
import com.advancedit.ppms.models.organisation.Department;
import com.advancedit.ppms.models.organisation.Organisation;
import com.advancedit.ppms.models.organisation.ShortDepartment;
import com.advancedit.ppms.models.person.Person;
import com.advancedit.ppms.models.person.PersonFunction;
import com.advancedit.ppms.models.person.ShortPerson;
import com.advancedit.ppms.models.user.Role;
import com.advancedit.ppms.models.user.User;

import java.util.Optional;
import java.util.Set;

import static com.advancedit.ppms.models.person.PersonFunction.isStaff;

public class UserPresenter {


    public static UserResource toResource(User user , Person person, Set<Role> roles, Department department){
     UserResource userResource = new UserResource();
        userResource.setId(user.getId());
        userResource.setEmail(user.getEmail());
        userResource.setUsername(user.getUsername());
        userResource.setEmailIsValid(user.isEmailIsValid());
        userResource.setEnabled(user.isEnabled());
        userResource.setFirstName(user.getFirstName());
        userResource.setLastName(user.getLastName());
        userResource.setOrganisationCreationRequest(user.getOrganisationCreationRequest());
        userResource.setMessage(user.getMessage());
        userResource.setTenantIds(user.getTenantIds());
        userResource.setDefaultTenantId(user.getDefaultTenantId());
        userResource.setRoles(roles);
        userResource.setPermissions(user.getPermissions());
        userResource.setPersonId(person.getId());
        if (department != null) userResource.setDepartment(new ShortDepartment(department.getDepartmentId(), department.getName()));
     return userResource;
    }


}
