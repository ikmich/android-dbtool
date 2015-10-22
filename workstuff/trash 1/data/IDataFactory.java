package org.ikmich.dbtool.data;

import org.ikmich.dbtool.Dbtool;

import android.content.Context;

public interface IDataFactory
{
	public String getDb();

	public Dbtool getDbtool(Context c);

	/**
	 * Deletes the application db.
	 * 
	 * @return
	 */
	public boolean deleteDb();

	/**
	 * Creates the application database.
	 * 
	 * @return
	 */
	public boolean createDb();

	/**
	 * Initializes the database.
	 * 
	 * @throws Exception
	 */
	public abstract void initialize() throws Exception;
}
