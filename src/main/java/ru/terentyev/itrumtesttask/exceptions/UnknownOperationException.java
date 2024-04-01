package ru.terentyev.itrumtesttask.exceptions;

public class UnknownOperationException extends RuntimeException {
	
	private String message;

	public UnknownOperationException(String message) {
		super(message);
	}
	
}
