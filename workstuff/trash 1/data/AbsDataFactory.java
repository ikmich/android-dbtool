package org.ikmich.dbtool.data;

import org.ikmich.dbtool.Dbtool;

import android.content.Context;

public abstract class AbsDataFactory implements IDataFactory
{
	protected static Context context;
	protected Dbtool dbtool;
	protected String dbName;

	protected AbsDataFactory(Context c, String dbName)
	{
		context = c;
		this.dbName = dbName;
		dbtool = Dbtool.getInstance(context);
		dbtool.setDb(dbName);
	}

	@Override
	public Dbtool getDbtool(Context c)
	{
		if (dbtool == null || context != c)
		{
			context = c;
			dbtool = Dbtool.getInstance(c);
		}
		return dbtool;
	}

	/**
	 * Deletes the app database.
	 * 
	 * @return
	 */
	@Override
	public boolean deleteDb()
	{
		try
		{
			dbtool.openDb();
			return dbtool.dropDb(dbName);
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean createDb()
	{
		//deleteAppDb(); //for test purposes.
		try
		{
			dbtool.createDb(dbName);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

}
