package com.backend.responses;

public class ChangePasswordResponse {

	private String message;

	public ChangePasswordResponse() {
		super();
	}

	public ChangePasswordResponse(String message) {
		super();
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
