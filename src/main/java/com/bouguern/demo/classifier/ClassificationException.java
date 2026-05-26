package com.bouguern.demo.classifier;

public class ClassificationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClassificationException(String message) {
        super(message);
    }

    public ClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}