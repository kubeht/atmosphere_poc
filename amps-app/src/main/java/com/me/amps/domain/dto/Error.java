package com.me.amps.domain.dto;

public class Error {

    private final String message;
    private final int code;

    public Error() {
    	this(0, null);
    }	
    public Error(String msg) {
    	this(0, msg);
    }
    
    public Error(int code, String msg) {
        this.message = msg;
        this.code = code;
    }

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Error [message=").append(message)
				.append(", code=").append(code).append("]");
		return builder.toString();
	}

	
	
}