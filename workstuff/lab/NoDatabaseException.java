package org.ikmich.android.dbtools.lab;

public class NoDatabaseException extends Exception {
	
	private static final long serialVersionUID = -2475528276903419271L;
	
	public NoDatabaseException() {
		super("The database does not exist or none selected or opened.");
	}
	
	public NoDatabaseException(String errorMessage) {
		super(errorMessage);
	}
	
}
