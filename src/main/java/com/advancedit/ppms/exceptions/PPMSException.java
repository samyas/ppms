package com.advancedit.ppms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PPMSException  extends RuntimeException {
	private static final long serialVersionUID = -6127508569926524106L;
	private ErrorCode code = ErrorCode.UNKNOW_ERROR_OCCURED;

	public PPMSException(ErrorCode code) {
		this.code = code;
	}

	public PPMSException(ErrorCode code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public PPMSException( ErrorCode code, String msg, Throwable t) {
		super(msg, t);
		this.code = code;
	}
	
	public PPMSException(String msg, Throwable t) {
		super(msg, t);
		
	}
	
	public PPMSException(String msg) {
		super(msg);
		
	}

	public ErrorCode getCode() {
		return code;
	}

}
