package com.advancedit.ppms.controllers.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthResponseBean {
    private boolean needToSelect;
    private String token;
    private boolean needToInitOrg;
    private boolean enabled;
}
