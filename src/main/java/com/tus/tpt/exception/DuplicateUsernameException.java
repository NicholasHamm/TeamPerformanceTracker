package com.tus.tpt.exception;

public class DuplicateUsernameException extends RuntimeException {

	private static final long serialVersionUID = 654217308284482174L;

	public DuplicateUsernameException(String username) {
        super("Username [" + username + "] already exists");
    }
}

