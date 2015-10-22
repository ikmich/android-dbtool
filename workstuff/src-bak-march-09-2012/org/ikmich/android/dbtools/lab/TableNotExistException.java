package org.ikmich.android.dbtools.lab;

public class TableNotExistException extends Exception {

	private static final long serialVersionUID = -4194041555412207611L;

	public TableNotExistException() {
		super("The database table does not exist.");
	}

	public TableNotExistException(String errorMessage) {
		super(errorMessage);
	}
	
}
