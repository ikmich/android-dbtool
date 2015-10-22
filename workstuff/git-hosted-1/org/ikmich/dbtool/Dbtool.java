package org.ikmich.dbtool;

import java.lang.reflect.Field;
import java.util.Stack;

import org.ikmich.dbtool.table.TableNotFoundException;
import org.ikmich.dbtool.table.TableProfile;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Utility class to help with carrying out common actions on a database.
 * 
 * @author Ikenna Agbasimalo
 */
public class Dbtool implements IDbtoolAction
{
	private SQLiteDatabase _db;
	private String _dbName;
	private int _dbVersion = 1;
	private DbHelper _dbHelper;
	private Stack<DbtoolActionUnit> _actionUnitStack;
	private DbtoolActionUnit _currentActionUnit;

	private static Context _context;
	private static Dbtool _instance;

	/**
	 * Private constructor. Creates a Dbtool instance.
	 * 
	 * @param c
	 *        The current context.
	 */
	private Dbtool(Context c)
	{
		this(c, null);
	}

	/**
	 * Private Constructor. Creates a Dbtool instance and assigns an SQLiteDatabase object to work with.
	 * 
	 * @param c
	 *        The current context.
	 * @param db
	 *        The SQLiteDatabase to work with.
	 */
	private Dbtool(Context c, SQLiteDatabase db)
	{
		_context = c;
		setDb(db);
		_actionUnitStack = new Stack<DbtoolActionUnit>();
	}

	/**
	 * Returns a new Dbtool instance or the last Dbtool instance if it has been created before.
	 * 
	 * @param c
	 *        The context.
	 * @return The Dbtool instance.
	 */
	public static Dbtool getInstance(Context c)
	{
		if (_context == null || _context != c || _instance == null)
		{
			_context = c;
			_instance = new Dbtool(_context);
		}

		return _instance;
	}

	/**
	 * Returns an instance of Dbtool.
	 * 
	 * @param c
	 *        The context.
	 * @param db
	 *        An SQLiteDatabase object.
	 * @return The Dbtool instance.
	 */
	public static Dbtool getInstance(Context c, SQLiteDatabase db)
	{
		_instance = getInstance(c);
		_instance.setDb(db);
		return _instance;
	}

	/**
	 * Returns a new Dbtool instance.
	 * 
	 * @param c
	 *        The context.
	 * @return The Dbtool instance.
	 */
	public static Dbtool newInstance(Context c)
	{
		return new Dbtool(c);
	}

	private void addActionUnit(DbtoolActionUnit actionUnit)
	{
		if (okToStartNewActionUnit())
		{
			_actionUnitStack.push(actionUnit);
			_currentActionUnit = actionUnit;
		}
	}

	private DbtoolActionUnit getLatestActionUnit()
	{
		return _actionUnitStack.elementAt(_actionUnitStack.size() - 1);
	}

	/*
	 * This is called at the end of the run() method.
	 * 
	 * @return
	 */
	private DbtoolActionUnit popActionUnit()
	{
		_currentActionUnit = _actionUnitStack.pop();
		return _currentActionUnit;
	}

	private boolean okToStartNewActionUnit()
	{
		//		if (_actionStack.size() < 1)
		//		{
		//			return true;
		//		}
		return true;
		//		/*
		//		 * It seems that only a return true is needed, or the method is not needed at all; but it is left here for
		//		 * design extensibility and uncertainty purposes, just in case... (paranoid, i know)
		//		 */
	}

	/**
	 * Checks if any action unit is available for use.
	 * 
	 * @return <b>true</b> or <b>false</b>.
	 */
	private boolean hasActionUnit()
	{
		return _actionUnitStack.size() > 0 ? true : false;
	}

	/**
	 * Sets the name of the database to work with. Does NOT open the database. You can call openDb() later to open the
	 * database.
	 * 
	 * @param databaseName
	 */
	public void setDb(String databaseName)
	{
		_dbName = databaseName;

		/*
		 * If an SQLiteDatabase object is already defined for this Dbtool instance and its name is
		 * not the same as the one set here, a new SQLiteDatabase needs to be created.
		 */
		if (_db != null && !_dbName.equals(getDbName(_db)))
		{
			initializeDbHelper(_dbName);
			_db = _dbHelper.getWritableDatabase();
		}
	}

	/**
	 * Sets the SQLiteDatabase to work with and extracts the database name from it.
	 * 
	 * @param db
	 *        An SQLiteDatabase to use with the dbtool instance.
	 */
	public void setDb(SQLiteDatabase db)
	{
		_db = db;
		extractDbName(_db);
	}

	/**
	 * Extracts the database name from an SQLiteDatabase.
	 * 
	 * @param db
	 *        The SQLiteDatabase.
	 */
	private void extractDbName(SQLiteDatabase db)
	{
		try
		{
			_dbName = getDbName(db);
		}
		catch (NullPointerException ex)
		{}
	}

	/**
	 * Gets the current sqlite database in use by this Dbtool instance.
	 * 
	 * @return The sqlite database. Returns null if no database is associated with this Dbtool instance.
	 */
	public SQLiteDatabase getActiveDb()
	{
		return _db;
	}

	/**
	 * Gets the sqlite database for the Dbtool instance represented by the name with which it's stored.
	 * 
	 * @param dbName
	 *        The name of the database.
	 * @return The SQLiteDatabase object or null if the database does not exist.
	 */
	public static SQLiteDatabase getDatabase(String dbName)
	{
		if (dbExists(dbName))
		{
			return _instance.createOrOpenDatabase(dbName);
		}
		return null;
	}

	/**
	 * Gets the name of the database associated with this Dbtool instance.
	 * 
	 * @return The database name.
	 */
	public String getDbName()
	{
		if (_dbName == null && _db != null)
		{
			_dbName = getDbName(_db);
		}
		return _dbName;
	}

	/**
	 * Gets the name of an sqlite database object.
	 * 
	 * @param db
	 *        The SQLiteDatabase object whose name to get.
	 * @return The database name by which it is stored.
	 */
	public static String getDbName(SQLiteDatabase db)
	{
		String[] sections = db.getPath().split("/");
		return sections.length > 1 ? sections[sections.length - 1] : sections[0];
	}

	/**
	 * Sets the database version to be used when createDatabase() is called.
	 * 
	 * @param dbVersion
	 *        The database version.
	 * @return The Dbtool instance.
	 */
	public Dbtool setDbVersion(int dbVersion)
	{
		_dbVersion = dbVersion;
		return this;
	}

	/**
	 * Checks if a database exists. As a static method, it should be called thus: Dbtool.dbExists(...);
	 * 
	 * @param databaseName
	 *        The name of the database to check.
	 * @return <b>true</b> if the database exists; <b>false</b> otherwise.
	 */
	public static boolean dbExists(String databaseName)
	{
		String[] dbNames = _context.databaseList();
		for (String dbName : dbNames)
		{
			if (databaseName.equals(dbName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a table exists in the database specified for this Dbtool instance.
	 * 
	 * @param table
	 *        The table name.
	 * @return <b>true</b> if the table exists in the database associated with this Dbtool instance; <b>false</b> if
	 *         not.
	 */
	public boolean tableExists(String table) throws NoDatabaseForDbtoolException
	{
		if (_db == null)
			throw new NoDatabaseForDbtoolException();

		openDb();
		try
		{
			DatabaseUtils.queryNumEntries(_db, table);

			//If no SQLiteException occurs here, the table is assumed to exist.
			return true;
		}
		catch (SQLiteException ex)
		{
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	/**
	 * Checks if a database table in the database associated with the current Dbtool instance is empty.
	 * 
	 * @param table
	 *        The name of the table to check.
	 * @return <b>true</b> if the table is empty; <b>false</b> otherwise.
	 */
	public boolean tableIsEmpty(String table) throws NoDatabaseForDbtoolException, TableNotFoundException
	{
		if (_db == null)
			throw new NoDatabaseForDbtoolException();

		if (!this.tableExists(table))
			throw new TableNotFoundException();

		openDb();
		try
		{
			return DatabaseUtils.queryNumEntries(_db, table) < 1;
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	/**
	 * Indication to perform a 'select all' operation which when run() is called, the result is not a Cursor but a
	 * DbRecordSet.
	 * 
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getAll()
	{
		addActionUnit(new DbtoolActionUnit(_db).getAll());
		return this;
	}

	/**
	 * Indication to perform a 'select all' operation which when run() is called, the result will be a Cursor.
	 */
	public Dbtool selectAll()
	{
		addActionUnit(new DbtoolActionUnit(_db).selectAll());
		return this;
	}

	/**
	 * Indication to perform a 'select' operation.
	 * 
	 * @param columns
	 *        Array of column names to perform the query on.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String[] columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).get(columns));
		return this;
	}

	public Dbtool select(String[] columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query.
	 * 
	 * @param columns
	 *        Comma-separated string of columns.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).get(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query.
	 * 
	 * @param columns
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool select(String columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query.
	 * 
	 * @param columns
	 *        Variable-number arguments indicating the columns to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(Object... columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).get(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query. This method differs from the get() methods in that it returns a Cursor
	 * object.
	 * 
	 * @param columns
	 *        String arguments representing the columns to perfom the query on.
	 * @return
	 */
	public Dbtool select(Object... columns)
	{
		addActionUnit(new DbtoolActionUnit(_db).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is a string.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getString(String column)
	{
		addActionUnit(new DbtoolActionUnit(_db).getString(column));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is an integer.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getInt(String column)
	{
		addActionUnit(new DbtoolActionUnit(_db).getInt(column));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is a float.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getFloat(String column)
	{
		addActionUnit(new DbtoolActionUnit(_db).getFloat(column));
		return this;
	}

	public Dbtool getDouble(String column)
	{
		addActionUnit(new DbtoolActionUnit(_db).getDouble(column));
		return null;
	}

	/**
	 * Sets the database table to perform a 'select' or 'delete' query on.
	 * 
	 * @param table
	 *        The table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool from(String table)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().from(table);
		}
		return this;
	}

	/**
	 * Sets the table(s) to perform a 'select' or 'delete' query on.
	 * 
	 * @param tables
	 *        Variable-number arguments indicating the tables to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool from(Object... tables)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().from(tables);
		}
		return this;
	}

	/**
	 * Indication to perform an 'update' query on a table.
	 * 
	 * @param table
	 *        The table to update.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool update(String table)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).update(table));
		}
		return this;
	}

	/**
	 * Indication to perform an 'update' query on a table.
	 * 
	 * @param tables
	 *        Variable-number arguments indicating the tables to update.
	 * @return
	 */
	public Dbtool update(Object... tables)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).update(tables));
		}
		return this;
	}

	/**
	 * Indication to perform an 'insert' query on a table, passing the values to be inserted. The probable way this
	 * method would be used is thus: dbtool.insert(values).into(tablename).run();
	 * 
	 * @param values
	 *        A ContentValues object containing the values to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insert(ContentValues values)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).insert(values));
		}
		return this;
	}

	/**
	 * Sets the table on which to run an 'insert' query. Used thus: <b>dbtool.insert(values).into(table).run;</b>.
	 * 
	 * @param table
	 *        The database table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool into(String table)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).into(table));
		}
		return this;
	}

	/**
	 * Indication to perform an 'insert' query on a table.
	 * 
	 * @param table
	 *        The table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insertInto(String table)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).insertInto(table));
		}
		return this;
	}

	/**
	 * Sets the values to insert into a table. Used thus: <b>dbtool.insertInto(table).values(values).run();</b>.
	 * 
	 * @param values
	 *        A ContentValues object containing the values to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool values(ContentValues values)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().values(values);
		}
		return this;
	}

	/**
	 * Specifies a Record object to be used for an 'insert' query. Used thus:
	 * <b>dbtool.insertInto(table).record(row).run();</b>.
	 * 
	 * @param row
	 *        The Record object to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool record(DbRecord row)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().record(row);
		}
		return this;
	}

	/**
	 * Alias for <b>record(row)</b>.
	 */
	public Dbtool row(DbRecord row)
	{
		return this.record(row);
	}

	/**
	 * Specifies a RecordSet for an 'insert' operation. Used thus: <b>dbtool.insertInto(table).recordSet(rows).run;</b>.
	 * 
	 * @param rows
	 *        The RecordSet object to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool recordSet(DbRecordSet rows)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().recordSet(rows);
		}
		return this;
	}

	/**
	 * Specifies a set of rows for an <b>insert</b> operation. Alias for <b>recordSet(rows)</b>.
	 */
	public Dbtool rows(DbRecordSet rows)
	{
		return this.recordSet(rows);
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, Byte value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, int value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, float value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, short value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, byte[] value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, String value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, double value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, long value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *        The column name to set.
	 * @param value
	 *        The value to set.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, boolean value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Indication to perform a <b>delete</b> query on a table.
	 * 
	 * @param table
	 *        The table to delete from.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool deleteFrom(String table)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).deleteFrom(table));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>delete</b> query on (a) table(s).
	 * 
	 * @param tables
	 *        Variable-number arguments indicating the table(s) to delete from.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool deleteFrom(Object... tables)
	{
		if (okToStartNewActionUnit())
		{
			addActionUnit(new DbtoolActionUnit(_db).deleteFrom(tables));
		}
		return this;
	}

	/**
	 * Sets the <b>where</b> clause for a <b>select</b>, <b>delete</b>, or <b>update</b> query. Used thus:
	 * <b>dbtool.get(columnName).from(table).where(whereClause).run();</b>.
	 * 
	 * @param whereClause
	 *        The where clause.
	 * @return
	 */
	public Dbtool where(String whereClause)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().where(whereClause);
		}
		return this;
	}

	public Dbtool and()
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().and();
		}
		return this;
	}

	public Dbtool or()
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().or();
		}
		return this;
	}

	public Dbtool whereLike(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereLike(field, value);
		}
		return this;
	}

	public Dbtool whereEquals(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereEquals(field, value);
		}
		return this;
	}

	public Dbtool whereNotEquals(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereNotEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThan(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereLessThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThan(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereGreaterThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThanOrEquals(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereGreaterThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThanOrEquals(String field, Object value)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereLessThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereArgs(String[] whereArgs)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().whereArgs(whereArgs);
		}
		return this;
	}

	public Dbtool distinct()
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().distinct();
		}
		return this;
	}

	public Dbtool columns(String[] columns)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(String columns)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(Object... columns)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool groupBy(String groupBy)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().groupBy(groupBy);
		}
		return this;
	}

	public Dbtool having(String having)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().having(having);
		}
		return this;
	}

	public Dbtool orderBy(String orderBy)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().orderBy(orderBy);
		}
		return this;
	}

	public Dbtool limit(String limit)
	{
		if (hasActionUnit())
		{
			getLatestActionUnit().limit(limit);
		}
		return this;
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName)
	{
		setDb(dbName);
		initializeDbHelper(_dbName);

		_db = _dbHelper.getWritableDatabase();
		return _db;
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName, Runnable actionOnCreate)
	{
		if (actionOnCreate != null)
		{
			setDb(dbName);
			initializeDbHelper(_dbName);
			_dbHelper.setActionOnCreate(actionOnCreate);
			try
			{
				_db = _dbHelper.getWritableDatabase();
			}
			catch (SQLiteException e)
			{
				throw e;
			}
			return _db;
		}
		else
		{
			return createOrOpenDatabase(dbName);
		}
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName, Runnable actionOnCreate,
		Runnable actionOnOpen, Runnable actionOnUpgrade)
	{
		if (actionOnCreate != null || actionOnOpen != null || actionOnUpgrade != null)
		{
			setDb(dbName);
			initializeDbHelper(_dbName);

			if (actionOnCreate != null)
			{
				_dbHelper.setActionOnCreate(actionOnCreate);
			}

			if (actionOnOpen != null)
			{
				_dbHelper.setActionOnOpen(actionOnOpen);
			}

			if (actionOnUpgrade != null)
			{
				try
				{
					_dbHelper.setActionOnUpdate(actionOnUpgrade);
				}
				catch (SQLiteException e)
				{
					throw e;
				}
			}

			_db = _dbHelper.getWritableDatabase();
			return _db;
		}
		else
		{
			return createOrOpenDatabase(dbName);
		}
	}

	/**
	 * Create an sqlite database with the name supplied.
	 * 
	 * @param dbName
	 *        The name of the database.
	 * @return The SQLiteDatabase object.
	 */
	public SQLiteDatabase createDb(String dbName)
	{
		return createOrOpenDatabase(dbName);
	}

	/**
	 * Creates an sqlite database and creates an initial table represented by a TableProfile object.
	 * 
	 * @param dbName
	 *        The database name to create.
	 * @param profile
	 *        The TableProfile object representing the table to create.
	 * @see org.ikmich.dbtool.table.TableProfile
	 * @return The SQLiteDatabase object.
	 */
	public SQLiteDatabase createDb(String dbName, TableProfile profile)
	{
		_db = createOrOpenDatabase(dbName);
		if (!createTable(profile))
		{
			return null;
		}
		return _db;
	}

	/**
	 * Creates an sqlite database and creates initial tables represented by an array of TableProfile objects.
	 * 
	 * @param dbName
	 *        The database name.
	 * @param profiles
	 *        The array of TableProfile objects representing the tables to create.
	 * @return The SQLiteDatabase object.
	 */
	public SQLiteDatabase createDb(String dbName, TableProfile[] profiles)
	{
		_db = createOrOpenDatabase(dbName);
		for (int i = 0; i < profiles.length; i++)
		{
			if (!createTable(profiles[i]))
			{
				return null;
			}
		}
		return _db;
	}

	/**
	 * Creates an sqlite database and executes a Runnable action when the database is created.
	 * 
	 * @param dbName
	 *        The database name.
	 * @param actionOnCreate
	 *        A Runnable action to execute when the database is created.
	 * @return The SQLiteDatabase object.
	 */
	public SQLiteDatabase createDb(String dbName, Runnable actionOnCreate)
	{
		return createOrOpenDatabase(dbName, actionOnCreate);
	}

	/**
	 * Creates an sqlite database and executes corresponding actions when the database is created, opened, and updated
	 * respectively.
	 * 
	 * @param dbName
	 *        The database name.
	 * @param actionOnCreate
	 *        A Runnable action to execute when the database is created.
	 * @param actionOnOpen
	 *        A Runnable action to execute when the database is opened.
	 * @param actionOnUpgrade
	 *        A Runnable action to execute when the database is upgraded.
	 * @return The SQLiteDatabase object.
	 */
	public SQLiteDatabase createDb(String dbName, Runnable actionOnCreate, Runnable actionOnOpen,
		Runnable actionOnUpgrade)
	{
		return createOrOpenDatabase(dbName, actionOnCreate, actionOnOpen, actionOnUpgrade);
	}

	/**
	 * Opens the SQLiteDabase for this Dbtool instance, if already defined via setDb(String dbName) or
	 * setDb(SQLiteDatabase db).
	 * 
	 * @return
	 */
	public SQLiteDatabase openDb()
	{
		if (_dbName != null)
		{
			return openDb(_dbName);
		}

		if (_db != null)
		{
			return openDb(_db);
		}

		throw new Error("No database defined.");
	}

	/**
	 * Opens a database.
	 * 
	 * @param dbName
	 *        The database name.
	 * @return
	 */
	public SQLiteDatabase openDb(String dbName)
	{
		/*
		 * If the db is already open, do not call method to open it. It could silently throw a private
		 * DatabaseNotClosedException that will show in the logcat. This may not disturb the running of
		 * the app, but it is good practice to take this behaviour into consideration.
		 */
		if (_db != null && dbName.equals(getDbName(_db)) && _db.isOpen())
		{
			return _db;
		}

		return createOrOpenDatabase(dbName);
	}

	public static SQLiteDatabase openDb(SQLiteDatabase db)
	{
		return SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Closes the current database associated with this Dbtool instance.
	 */
	public void closeDb()
	{
		/*
		 * Close the database if it is open.
		 */
		try
		{
			if (_db != null && _db.isOpen())
			{
				_db.close();
				//_dbHelper.close();
			}
		}
		catch (NullPointerException ex)
		{}
	}

	/**
	 * Utility method to close an sqlite database.
	 * 
	 * @param db
	 *        The SQLiteDatabase object to close.
	 */
	public static void closeDb(SQLiteDatabase db)
	{
		db.close();
	}

	public boolean isOpen()
	{
		return _db != null && _db.isOpen();
	}

	public void destroy()
	{
		try
		{
			_instance = null;
			_context = null;
			_db = null;
			closeDb();
		}
		catch (NullPointerException ex)
		{}
	}

	/**
	 * Drops/deletes a database.
	 * 
	 * @param dbName
	 *        The database name.
	 * @return
	 */
	public boolean dropDb(String dbName)
	{
		if (_context != null)
		{
			try
			{
				return _context.deleteDatabase(dbName);
			}
			catch (Exception ex)
			{
				// Maybe the database does not exist.
				return false;
			}
		}
		return false;
	}

	/**
	 * Drops/deletes a database.
	 * 
	 * @param context
	 *        The working context.
	 * @param dbName
	 *        The database name.
	 * @return true on success. false otherwise.
	 */
	public static boolean dropDb(Context context, String dbName)
	{
		return context.deleteDatabase(dbName);
	}

	/**
	 * Creates a database table from a TableProfile object.
	 * 
	 * @param tableProfile
	 *        The TableProfile object to create the table from.
	 * @return TRUE on success. FALSE otherwise.
	 */
	public boolean createTable(TableProfile tableProfile)
	{
		if (_db != null && tableProfile != null)
		{
			if (!tableProfile.isBuilt())
			{
				tableProfile.build();
			}
			try
			{
				this.exec(tableProfile.getCreateSQL());
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * Creates a database table from an SQL create table string.
	 * 
	 * @param String
	 *        createTableSql
	 * @return boolean true or false depending on the outcome.
	 */
	public Boolean createTable(String createTableSql)
	{
		if (_db != null)
		{
			try
			{
				this.exec(createTableSql);
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * Creates a new database table from a TableProfile object. Deletes the table first if it exists.
	 * 
	 * @param tableProfile
	 *        The TableProfile object to create the table from.
	 * @return TRUE on success. FALSE otherwise.
	 */
	public boolean createNewTable(TableProfile tableProfile)
	{
		if (_db != null)
		{
			dropTable(tableProfile.getTableName());
			if (!tableProfile.isBuilt())
			{
				tableProfile.build();
			}
			try
			{
				this.exec(tableProfile.getCreateSQL());
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * Deletes a table from the active database.
	 * 
	 * @param table
	 *        The table to drop.
	 */
	public void dropTable(String table)
	{
		if (_db == null)
			throw new NoDatabaseForDbtoolException();

		String sql = "DROP TABLE IF EXISTS " + table;
		this.exec(sql);
	}

	/**
	 * Run the active query with sqliteDb.query(...).
	 */
	public Object run()
	{
		if (hasActionUnit())
		{
			return popActionUnit().run();
		}
		return null;
	}

	/**
	 * Executes a query with sqliteDb.execSQL(...)
	 * 
	 * @param sql
	 *        The query to execute.
	 */
	public void exec(String sql)
	{
		if (_db == null)
			throw new NoDatabaseForDbtoolException();
		_db.execSQL(sql);
	}

	/**
	 * Initializes this instance's DbHelper.
	 * 
	 * @param dbName
	 *        The name of the database.
	 */
	private void initializeDbHelper(String dbName)
	{
		_dbHelper = new DbHelper(dbName);
	}

	/**
	 * Helper class to facilitate working with an SQLite database.
	 */
	private class DbHelper extends SQLiteOpenHelper
	{
		private Runnable actionOnCreate;
		private Runnable actionOnUpgrade;
		private Runnable actionOnOpen;

		DbHelper(String dbName)
		{
			super(_context, dbName, null, _dbVersion);
		}

		DbHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}

		/**
		 * Specifies a Runnable action to run on-create of the db.
		 * 
		 * @param action
		 */
		void setActionOnCreate(Runnable action)
		{
			this.actionOnCreate = action;
		}

		/**
		 * Specifies a Runnable action to run on-update of the db.
		 * 
		 * @param action
		 */
		void setActionOnUpdate(Runnable action)
		{
			this.actionOnUpgrade = action;
		}

		/**
		 * Specifies a Runnable action to run on-open of the db.
		 * 
		 * @param action
		 */
		void setActionOnOpen(Runnable action)
		{
			this.actionOnOpen = action;
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			if (dbExists(Dbtool.getDbName(db)))
			{
				if (this.actionOnCreate != null)
				{
					try
					{
						/*
						 * Use reflection to get and instantiate the SQLite Database as defined by the user in the
						 * Runnable parameter passed to the createDb overloaded method of the Dbtool instance.
						 */
						Class<?> actionClass = actionOnCreate.getClass();
						Field[] fields = actionClass.getFields();
						for (Field field : fields)
						{
							if (field.getType() == Class.forName("android.database.sqlite.SQLiteDatabase"))
							{
								field.set(actionOnCreate, db);
								break;
							}
						}
						this.actionOnCreate.run();
					}
					catch (ClassNotFoundException ex)
					{
						// No SQLiteDatabase field in the runnable. Assume user
						// does not need to use any.
					}
					catch (Exception ex)
					{
						// other error.
					}
				}
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (this.actionOnUpgrade != null)
			{
				try
				{
					/*
					 * Use reflection to get and instantiate the SQLite Database, the old verion number, and the new
					 * version number, as defined by the user in the Runnable parameter passed to the createDb(String
					 * dbName, Runnable actionOnUpgrade) method of the Dbtool instance.
					 */
					Class<?> actionClass = actionOnUpgrade.getClass();
					Field[] fields = actionClass.getFields();
					for (Field field : fields)
					{
						if (field.getType() == Class.forName("android.database.sqlite.SQLiteDatabase"))
						{
							field.set(actionOnUpgrade, db);
						}

						if (field.getType() == Class.forName("java.lang.Integer"))
						{
							if (field.getName() == "oldVersion")
							{
								field.set(actionOnUpgrade, oldVersion);
							}
							if (field.getName() == "newVersion")
							{
								field.set(actionOnUpgrade, newVersion);
							}
						}
					}
					this.actionOnUpgrade.run();
				}
				catch (ClassNotFoundException ex)
				{
					// No SQLiteDatabase field in the runnable. Assume user does
					// not need to use any.
				}
				catch (Exception ex)
				{
					// Other error.
				}
			}
		}

		@Override
		public void onOpen(SQLiteDatabase db)
		{
			if (this.actionOnOpen != null)
			{
				try
				{
					/*
					 * Use reflection to get and instantiate the SQLite Database as defined by the user in the Runnable
					 * parameter that is passed to the createDb overloaded method of the Dbtool instance.
					 */
					Class<?> actionClass = actionOnOpen.getClass();
					Field[] fields = actionClass.getFields();
					for (Field field : fields)
					{
						if (field.getType() == Class.forName("android.database.sqlite.SQLiteDatabase"))
						{
							field.set(actionOnOpen, db);
							break;
						}
					}
					this.actionOnOpen.run();
				}
				catch (ClassNotFoundException ex)
				{
					// No SQLiteDatabase field in the runnable. Assume user does
					// not need to use any.
				}
				catch (Exception ex)
				{
					// Other error.
				}
			}
		}

	} // ..end DbHelper class

}