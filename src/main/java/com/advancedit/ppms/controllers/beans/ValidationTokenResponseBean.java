package com.advancedit.ppms.controllers.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationTokenResponseBean {
    private String token;
    private String firstName;
    private String lastName;
    private ValidationTokenResult result = ValidationTokenResult.REGISTER;
    private boolean creator = false;
    private String email;
    private String username;

   public enum ValidationTokenResult{
        REGISTER,
        LOGIN
    }
}


