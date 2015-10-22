package org.ikmich.android.dbtools.trash;

public class NoAssociatedDatabaseException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -460053454977098684L;

	public NoAssociatedDatabaseException() {
		super("No associated database for this object.");
	}
	
	public NoAssociatedDatabaseException(String msg) {
		super(msg);
	}
	
}
