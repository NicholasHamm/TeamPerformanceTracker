package com.tus.tpt.Exception;

public class DuplicateUsernameException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateUsernameException(String username) {
        super("Username [" + username + "] already exists");
    }
}

