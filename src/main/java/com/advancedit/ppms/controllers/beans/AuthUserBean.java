package com.advancedit.ppms.controllers.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthUserBean {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isCreator;
    private String token;
    List<Long> tenantId;
}
