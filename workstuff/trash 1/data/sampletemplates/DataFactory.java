package org.ikmich.dbtool.data.sampletemplates;

import org.ikmich.dbtool.data.AbsDataFactory;

import android.content.Context;

/**
 * This is a sample template for the recommended way to use the DataFactory
 * design in AndroidDbtool. You can decide to use it in any way you deem
 * suitable.
 * 
 * @author Ikmich
 */
public class DataFactory extends AbsDataFactory
{
	/*
	 * Declare private instance for use in singleton pattern instantiation.
	 */
	private static DataFactory instance;

	private DataFactory(Context c, String database)
	{
		super(c, database);
	}

	/*
	 * Define the public singleton instantiation method.
	 */
	public static DataFactory getInstance(Context c, String database)
	{
		if (instance == null || context != c)
		{
			instance = new DataFactory(c, database);
		}
		return instance;
	}

	@Override
	public String getDb()
	{
		return this.dbName;
	}

	@Override
	public void initialize() throws Exception
	{
		/*
		 * Here, you will setup the database schema. 
		 * Create db tables using the EntityFactory subclasses that 
		 * you create for each table.
		 */
		UserFactory uf = UserFactory.getInstance(context);
		uf.createSchema(true);

		/*
		 * Go on and do this for the other EntityFactory instances
		 * that you will use in your application.
		 */
	}
}
