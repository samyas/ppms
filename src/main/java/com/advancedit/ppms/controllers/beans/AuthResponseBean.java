package com.advancedit.ppms.controllers.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthResponseBean {
    private String token;
    private boolean needToInitOrg = false;
    private boolean enabled = false;
    private boolean needToActivate = false;
}
