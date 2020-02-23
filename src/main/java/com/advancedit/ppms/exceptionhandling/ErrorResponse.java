package com.advancedit.ppms.exceptionhandling;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;

import java.util.Date;

public class ErrorResponse {


    private String key;
    private String message;
    private String requestedURI;
    private Date date;

    public ErrorResponse(String key, String message, String requestedURI) {
        this.key = key;
        this.message = message;
        this.requestedURI = requestedURI;
        this.date = new Date();
    }

    public ErrorResponse(PPMSException ex, String requestedURI) {
        this.key = ex.getCode().getCode();
        this.message = ex.getMessage();
        this.requestedURI = requestedURI;
        this.date = new Date();
    }

    public ErrorResponse(Exception ex, String requestedURI) {
        this.key = ErrorCode.UNKNOW_ERROR_OCCURED.getCode();
        this.message = ex.getMessage();
        this.requestedURI = requestedURI;
        this.date = new Date();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRequestedURI() {
        return requestedURI;
    }

    public void setRequestedURI(String requestedURI) {
        this.requestedURI = requestedURI;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
