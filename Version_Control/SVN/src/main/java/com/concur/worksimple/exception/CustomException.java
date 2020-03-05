package com.concur.worksimple.exception;

import javax.xml.ws.http.HTTPException;

public class CustomException extends HTTPException {
	private int statusCode;
	private boolean error;
	private String message;
	
	public CustomException(int statusCode) {
		super(statusCode);
	}
	
	public CustomException(int statusCode,boolean error,String message) {
		super(statusCode);
		this.statusCode =  statusCode;
		this.error=error;
		this.message=message;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
