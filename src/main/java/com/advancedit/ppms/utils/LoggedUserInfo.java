package com.advancedit.ppms.utils;

import com.advancedit.ppms.models.user.Permission;
import com.advancedit.ppms.models.user.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class LoggedUserInfo {

    String email;
    long tenantId;
    String moduleId;
    Set<Role> roles;
    Set<Permission> permissions;



}
