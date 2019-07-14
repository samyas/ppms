package com.advancedit.ppms.exceptions;


public class PPMSException  extends RuntimeException {
	private static final long serialVersionUID = -6127508569926524106L;
	private ErrorCode code;

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
