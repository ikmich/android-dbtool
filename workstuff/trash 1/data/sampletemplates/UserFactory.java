package org.ikmich.dbtool.data.sampletemplates;

import org.ikmich.dbtool.data.EntityFactory;
import org.ikmich.dbtool.table.ColumnProfile;
import org.ikmich.dbtool.table.TableProfile;

import android.content.Context;

public class UserFactory extends EntityFactory
{

	/*
	 * Define static instance for use with a singleton instantiation
	 * implementation.
	 * 
	 * It is not compulsory to do it this way; you can implement the 
	 * instantiation how you deem suitable.
	 */
	private static UserFactory instance;

	/*
	 * Define string constants to represent the column names.
	 * COL_ID is already defined within the IEntityFactory interface.
	 */
	public static final String COL_USERNAME = "username";
	public static final String COL_PASSWORD = "password";

	/*
	 * Use a private constructor in order to implement a singleton
	 * instantiation pattern.
	 * 
	 * This is not compulsory; you can implement this as you deem
	 * suitable.
	 */
	private UserFactory(Context c)
	{
		super(c);
	}

	public static UserFactory getInstance(Context c)
	{
		if (instance == null || context != c)
		{
			instance = new UserFactory(c);
		}
		return instance;
	}

	@Override
	public boolean createSchema()
	{
		return createSchema(false);
	}

	@Override
	public boolean createSchema(boolean dropTable)
	{
		try
		{
			/*
			 * Open the Dbtool instance. The db name has been set
			 * in the super class constructor.
			 */
			dbtool.openDb();

			/*
			 * Apply the 'drop table first' policy if set in the argument.
			 */
			if (dropTable)
			{
				if (dbtool.tableExists(getTable()))
				{
					dbtool.dropTable(getTable());
				}
			}

			/*
			 * Use TableProfile and ColumnProfile to easily create database
			 * tables.
			 */
			TableProfile tp = new TableProfile(getTable());
			tp.column(new ColumnProfile(COL_ID).type_int().autoIncrement().primaryKey().notNull());
			tp.column(new ColumnProfile(COL_USERNAME).type_text().unique().notNull());
			tp.column(new ColumnProfile(COL_PASSWORD).type_text().notNull());

			/*
			 * Create the table using Dbtool's createTable(TableProfile tp) method.
			 */
			return dbtool.createTable(tp);
		}
		catch (Exception ex)
		{
			/*
			 * An error occurred, and the table could thus not be created.
			 * You can decide what to do with the exception (and its containing 
			 * message) returned here.
			 */
			return false;
		}
		finally
		{
			/*
			 * Close the Dbtool instance.
			 */
			dbtool.closeDb();
		}
	}

	@Override
	public String getDbName()
	{
		return "app_db";
	}

	@Override
	public String getTable()
	{
		return "users";
	}

	@Override
	public String getTag()
	{
		return "Users";
	}

}
