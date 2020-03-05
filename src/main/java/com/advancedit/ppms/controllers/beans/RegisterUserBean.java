package com.advancedit.ppms.controllers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserBean {

    private String username;
    private String password;
    private String email;
    private Boolean isCreator;
    private String message;
    private String firstName;
    private String lastName;
    private String emailToken;
}
