package org.ikmich.dbtool.table;

/**
 * Error when a database table was not found.
 * 
 * @author Ikenna Agbasimalo
 */
public class TableNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	private static String msg = "No such table was found.";

	public TableNotFoundException()
	{
		super(msg);
	}
}
