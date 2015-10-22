package org.ikmich.android.dbtools.trash;

public class DatabaseNotExistException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8615431272446382833L;

	public DatabaseNotExistException() {
		super("Database does not exist.");
	}
	
	public DatabaseNotExistException(String msg) {
		super(msg);
	}
	
}
