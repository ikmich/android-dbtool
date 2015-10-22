package org.ikmich.android.dbtools;

import java.lang.reflect.Field;
import java.util.Stack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Utility class to help with carrying out common actions on a database.
 * 
 * @author Ikenna Agbasimalo
 */
public class Dbtool {

	static SQLiteDatabase _db;
	static String _dbName;
	private static int _dbVersion = 1;
	static Context _context;
	private DbHelper _dbHelper;

	private Stack<DbtoolActionUnit> _actionStack;
	private DbtoolActionUnit _currentActionUnit;

	/**
	 * Constructor. Creates a Dbtool instance.
	 * 
	 * @param c
	 *            <span>The current context.</span>
	 */
	public Dbtool(Context c) {
		_context = c;
		init();
	}

	/**
	 * Constructor. Creates a Dbtool instance to work with an existing sqlite database object.
	 * 
	 * @param c
	 *            <span>The current context.</span>
	 * @param db
	 *            <span>The SQLiteDatabase to work with.</span>
	 */
	public Dbtool(Context c, SQLiteDatabase db) {
		this(c);
		// _db = db;
		setDb(db);
	}

	private void init() {
		_actionStack = new Stack<DbtoolActionUnit>();
	}

	private void addActionUnit(DbtoolActionUnit actionUnit) {
		_actionStack.push(actionUnit);
		_currentActionUnit = actionUnit;
	}

	private DbtoolActionUnit getLatestActionUnit() {
		return _actionStack.elementAt(_actionStack.size() - 1);
	}

	/*
	 * This should be called at the end of the run() method.
	 * 
	 * @return
	 */
	private DbtoolActionUnit popActionUnit() {
		_currentActionUnit = _actionStack.pop();
		return _currentActionUnit;
	}

	private boolean okToStartNewActionUnit() {
		if(_actionStack.size() < 1) {
			return true;
		}
		return true;
		/*
		 * It seems, from the content of this method, that only a return true is needed, or the
		 * method is not needed at all; but it is left here for design extensibility and uncertainty
		 * purposes, just in case...
		 */
	}

	/**
	 * Checks if any action unit is available for use.
	 * 
	 * @return <b>true</b> or <b>false</b>.
	 */
	private boolean hasActionUnit() {
		return _actionStack.size() > 0 ? true : false;
	}

	/**
	 * Sets the Context within which the Dbtool instance should currently operate.
	 * 
	 * @param c
	 * @return The Dbtool instance, for chaining purposes.
	 */
	public Dbtool context(Context c) {
		_context = c;
		return this;
	}

	/**
	 * Sets the sqlite database to act upon.
	 * 
	 * @param db
	 *            <span>The SQLiteDatabase instance</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	private Dbtool setDb(SQLiteDatabase db) {
		_db = db;
		openDb(_db);
		return this;
	}

	/**
	 * Gets the current sqlite database in use by this Dbtool instance.
	 * 
	 * @return The sqlite database. Returns null if no database is associated with this Dbtool
	 *         instance.
	 */
	public SQLiteDatabase getActiveDb() {
		return _db;
	}

	/**
	 * Gets the sqlite database represented by the name with which it's stored.
	 * 
	 * @param dbName
	 *            <span>The name of the database.</span>
	 * @return The SQLiteDatabase object or null if the database does not exist.
	 */
	public SQLiteDatabase getDatabase(String dbName) {
		if(dbExists(dbName)) {
			return (createOrOpenDatabase(dbName));
		}
		return null;
	}

	/**
	 * Gets the name of the database associated with this Dbtool instance.
	 * 
	 * @return The database name.
	 */
	public String getActiveDbName() {
		return _dbName;
	}

	/**
	 * Gets the name of an sqlite database object. As a <b>static</b> method, it should be called
	 * thus: Dbtool.getDbName(...);
	 * 
	 * @param db
	 *            <span>The SQLiteDatabase object whose name to get.</span>
	 * @return The database name by which it is stored.
	 */
	public static String getDbName(SQLiteDatabase db) {
		String[] sections = db.getPath().split("/");
		return sections.length > 1 ? sections[sections.length - 1] : sections[0];
	}

	/**
	 * Sets the database version to be used when createDatabase() is called.
	 * 
	 * @param dbVersion
	 *            <span>The database version</span>
	 * @return The Dbtool object for chaining purposes.
	 */
	public Dbtool setDbVersion(int dbVersion) {
		_dbVersion = dbVersion;
		return this;
	}

	private void initDbHelper(String dbName) {
		_dbHelper = new DbHelper(dbName);
	}

	/**
	 * Checks if a database exists. As a <b>static</b> method, it should be called thus:
	 * Dbtool.dbExists(...);
	 * 
	 * @param databaseName
	 *            <span>The name of the database to check.</span>
	 * @return <b>true</b> if the database exists; <b>false</b> otherwise.
	 */
	public boolean dbExists(String databaseName) {
		String[] dbNames = _context.databaseList();
		for(String dbName : dbNames) {
			if(databaseName.equals(dbName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a table exists in the current database associated with this Dbtool instance.
	 * 
	 * @param table
	 *            <span>The table name.</span>
	 * @return <b>TRUE</b> if the table exists in the current database associated with this Dbtool
	 *         instance; <b>FALSE</b> if not.
	 * @throws Exception
	 */
	public boolean tableExists(String table) throws Exception {
		if(_db != null) {
			try {
				IQuery query = new SelectQuery(_db).selectAll().from(table);
				Cursor c = (Cursor) query.run();
				c.close();
				return true;
			}
			catch(SQLiteException e) {
				return false;
			}
			catch (Exception e) {
				return false;
			}
		}
		else {
			throw new Exception("No opened or selected database.");
		}
	}

	/**
	 * Checks if a database table is empty.
	 * 
	 * @param table
	 *            <span>The name of the table to check.</span>
	 * @return TRUE or FALSE depending on whether the table is empty or not.
	 * @throws Exception
	 */
	public boolean tableIsEmpty(String table) throws Exception {
		if(_db != null) {
			if(this.tableExists(table)) {
				IQuery query = new SelectQuery(_db).selectAll().from(table);
				Cursor c = (Cursor) query.run();
				if(c.getCount() == 0) {
					c.close();
					return true;
				}
				c.close();
				return false;
			}
			else {				
				throw new Exception("Table does not exist.");
			}
		}
		else {
			throw new Exception("No opened or selected database.");
		}
	}

	/**
	 * Indication to perform a <b>select all</b> query.
	 * 
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getAll() {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().getAll());
		}
		return this;
	}

	public Dbtool selectAll() {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().selectAll());
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query.
	 * 
	 * @param columns
	 *            <span>Array of column names to perform the query on.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String[] columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().get(columns));
		}
		return this;
	}

	public Dbtool select(String[] columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().select(columns));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query.
	 * 
	 * @param columns
	 *            <span>Comma-separated string of columns.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().get(columns));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query.
	 * 
	 * @param columns
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool select(String columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().select(columns));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query.
	 * 
	 * @param columns
	 *            <span>Variable-number arguments indicating the columns to query.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(Object... columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().get(columns));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query. This method differs from the get() methods in
	 * that it returns a Cursor object.
	 * 
	 * @param columns
	 *            <span>String arguments representing the columns to perfom the query on.</span>
	 * @return
	 */
	public Dbtool select(Object... columns) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().select(columns));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query to get a single value which is a string.
	 * 
	 * @param column
	 *            <span>The column to query.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getString(String column) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().getString(column));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query to get a single value which is an integer.
	 * 
	 * @param column
	 *            <span>The column to query.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getInt(String column) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().getInt(column));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>select</b> query to get a single value which is a float.
	 * 
	 * @param column
	 *            <span>The column to query.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getFloat(String column) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().getFloat(column));
		}
		return this;
	}

	/**
	 * Sets the database table to perform a <b>select</b> or <b>delete</b> query on.
	 * 
	 * @param table
	 *            <span>The table name.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool from(String table) {
		if(hasActionUnit()) {
			getLatestActionUnit().from(table);
		}
		return this;
	}

	/**
	 * Sets the table(s) to perform a <b>select</b> or <b>delete</b> query on.
	 * 
	 * @param tables
	 *            <span>Variable-number arguments indicating the tables to query.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool from(Object... tables) {
		if(hasActionUnit()) {
			getLatestActionUnit().from(tables);
		}
		return this;
	}

	/**
	 * Indication to perform an <b>update</b> query on a table.
	 * 
	 * @param table
	 *            <span>The table to update.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool update(String table) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().update(table));
		}
		return this;
	}

	/**
	 * Indication to perform an <b>update</b> query on a table.
	 * 
	 * @param tables
	 *            <span>Variable-number arguments indicating the tables to update.</span>
	 * @return
	 */
	public Dbtool update(Object... tables) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().update(tables));
		}
		return this;
	}

	/**
	 * Indication to perform an <b>insert</b> query on a table, passing the values to be inserted.
	 * The probable way this method would be used is thus:
	 * dbtool.insert(values).into(tablename).run();
	 * 
	 * @param values
	 *            <span>A ContentValues object containing the values to insert.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insert(ContentValues values) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().insert(values));
		}
		return this;
	}

	/**
	 * Sets the table on which to run an <b>insert</b> query. Used thus:
	 * <b>dbtool.insert(values).into(table).run;</b>.
	 * 
	 * @param table
	 *            <span>The database table name.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool into(String table) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().into(table));
		}
		return this;
	}

	/**
	 * Indication to perform an <b>insert</b> query on a table.
	 * 
	 * @param table
	 *            <span>The table name.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insertInto(String table) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().insertInto(table));
		}
		return this;
	}

	/**
	 * Sets the values to insert into a table. Used thus:
	 * <b>dbtool.insertInto(table).values(values).run();</b>.
	 * 
	 * @param values
	 *            <span>A ContentValues object containing the values to insert.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool values(ContentValues values) {
		if(hasActionUnit()) {
			getLatestActionUnit().values(values);
		}
		return this;
	}

	/**
	 * Specifies a Record object to be used for an <b>insert</b> query. Used thus:
	 * <b>dbtool.insertInto(table).record(row).run();</b>.
	 * 
	 * @param row
	 *            <span>The Record object to insert.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool record(Record row) {
		if(hasActionUnit()) {
			getLatestActionUnit().record(row);
		}
		return this;
	}

	/**
	 * Alias for <b>record(row)</b>.
	 */
	public Dbtool row(Record row) {
		return this.record(row);
	}

	/**
	 * Specifies a RecordSet for an <b>insert</b> operation. Used thus:
	 * <b>dbtool.insertInto(table).recordSet(rows).run;</b>.
	 * 
	 * @param rows
	 *            <span>The RecordSet object to insert.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool recordSet(RecordSet rows) {
		if(hasActionUnit()) {
			getLatestActionUnit().recordSet(rows);
		}
		return this;
	}

	/**
	 * Specifies a set of rows for an <b>insert</b> operation. Alias for <b>recordSet(rows)</b>.
	 */
	public Dbtool rows(RecordSet rows) {
		return this.recordSet(rows);
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, Byte value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, int value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, float value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, short value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, byte[] value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, String value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, double value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, long value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Sets the value of a column in an <b>insert</b> or <b>update</b> query.
	 * 
	 * @param name
	 *            <span>The column name to set.</span>
	 * @param value
	 *            <span>The value to set.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool set(String name, boolean value) {
		if(hasActionUnit()) {
			getLatestActionUnit().set(name, value);
		}
		return this;
	}

	/**
	 * Indication to perform a <b>delete</b> query on a table.
	 * 
	 * @param table
	 *            <span>The table to delete from.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool deleteFrom(String table) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().deleteFrom(table));
		}
		return this;
	}

	/**
	 * Indication to perform a <b>delete</b> query on (a) table(s).
	 * 
	 * @param tables
	 *            <span>Variable-number arguments indicating the table(s) to delete from.</span>
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool deleteFrom(Object... tables) {
		if(okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit().deleteFrom(tables));
		}
		return this;
	}

	/**
	 * Sets the <b>where</b> clause for a <b>select</b>, <b>delete</b>, or <b>update</b> query. Used
	 * thus: <b>dbtool.get(columnName).from(table).where(whereClause).run();</b>.
	 * 
	 * @param whereClause
	 *            The where clause.
	 * @return
	 */
	public Dbtool where(String whereClause) {
		if(hasActionUnit()) {
			getLatestActionUnit().where(whereClause);
		}
		return this;
	}

	public Dbtool and() {
		if(hasActionUnit()) {
			getLatestActionUnit().and();
		}
		return this;
	}

	public Dbtool or() {
		if(hasActionUnit()) {
			getLatestActionUnit().or();
		}
		return this;
	}

	public Dbtool whereLike(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereLike(field, value);
		}
		return this;
	}

	public Dbtool whereEquals(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThan(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereLessThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThan(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereGreaterThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThanOrEquals(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereGreaterThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThanOrEquals(String field, Object value) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereLessThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereArgs(String[] whereArgs) {
		if(hasActionUnit()) {
			getLatestActionUnit().whereArgs(whereArgs);
		}
		return this;
	}

	public Dbtool distinct() {
		if(hasActionUnit()) {
			getLatestActionUnit().distinct();
		}
		return this;
	}

	public Dbtool columns(String[] columns) {
		if(hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(String columns) {
		if(hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(Object... columns) {
		if(hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool groupBy(String groupBy) {
		if(hasActionUnit()) {
			getLatestActionUnit().groupBy(groupBy);
		}
		return this;
	}

	public Dbtool having(String having) {
		if(hasActionUnit()) {
			getLatestActionUnit().having(having);
		}
		return this;
	}

	public Dbtool orderBy(String orderBy) {
		if(hasActionUnit()) {
			getLatestActionUnit().orderBy(orderBy);
		}
		return this;
	}

	public Dbtool limit(String limit) {
		if(hasActionUnit()) {
			getLatestActionUnit().limit(limit);
		}
		return this;
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName) {
		_dbName = dbName;
		initDbHelper(_dbName);
		try {
			_db = _dbHelper.getWritableDatabase();
		}
		catch (SQLiteException e) {
			throw e;
		}
		return _db;
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName, Runnable actionOnCreate) {
		if(actionOnCreate != null) {
			_dbName = dbName;
			initDbHelper(_dbName);
			_dbHelper.setActionOnCreate(actionOnCreate);
			try {
				_db = _dbHelper.getWritableDatabase();
			}
			catch (SQLiteException e) {
				throw e;
			}
			return _db;
		}
		else {
			return createOrOpenDatabase(dbName);
		}
	}

	protected SQLiteDatabase createOrOpenDatabase(String dbName, Runnable actionOnCreate,
			Runnable actionOnOpen, Runnable actionOnUpgrade) {
		if(actionOnCreate != null || actionOnOpen != null || actionOnUpgrade != null) {
			_dbName = dbName;
			initDbHelper(_dbName);
			if(actionOnCreate != null) {
				_dbHelper.setActionOnCreate(actionOnCreate);
			}
			if(actionOnOpen != null) {
				_dbHelper.setActionOnOpen(actionOnOpen);
			}
			if(actionOnUpgrade != null) {
				try {
					_dbHelper.setActionOnUpdate(actionOnUpgrade);
				}
				catch (SQLiteException e) {
					throw e;
				}
			}
			_db = _dbHelper.getWritableDatabase();
			return _db;
		}
		else {
			return createOrOpenDatabase(dbName);
		}
	}

	/**
	 * Create an sqlite database with the name supplied.
	 * 
	 * @param dbName
	 *            The name of the database.
	 * @return The SQLiteDatabase object.
	 * @throws SQLiteException
	 */
	public SQLiteDatabase createDb(String dbName) {
		return createOrOpenDatabase(dbName);
	}

	/**
	 * Creates an sqlite database and creates an initial table represented by a TableProfile object.
	 * 
	 * @param dbName
	 *            The database name to create.
	 * @param profile
	 *            The TableProfile object representing the table to create.
	 * @see org.ikmich.android.dbtools.TableProfile
	 * @return The SQLiteDatabase object.
	 * @throws SQLiteException
	 */
	public SQLiteDatabase createDb(String dbName, TableProfile profile) {
		_db = createOrOpenDatabase(dbName);
		if(!createTable(profile)) {
			return null;
		}
		return _db;
	}

	/**
	 * Creates an sqlite database and creates initial tables represented by an array of TableProfile
	 * objects.
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 * @param profiles
	 *            <span>The array of TableProfile objects representing the tables to create.</span>
	 * @return The SQLiteDatabase object.
	 * @throws SQLiteException
	 */
	public SQLiteDatabase createDb(String dbName, TableProfile[] profiles) throws SQLiteException {
		_db = createOrOpenDatabase(dbName);
		for(int i = 0; i < profiles.length; i++) {
			if(!createTable(profiles[i])) {
				return null;
				// break; // break if failed.
			}
		}
		return _db;
	}

	/**
	 * Creates an sqlite database and executes a Runnable action when the database is created.
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 * @param actionOnCreate
	 *            <span>A Runnable action to execute when the database is created.</span>
	 * @return The SQLiteDatabase object.
	 * @throws SQLiteException
	 */
	public SQLiteDatabase createDb(String dbName, Runnable actionOnCreate) throws SQLiteException {
		return createOrOpenDatabase(dbName, actionOnCreate);
	}

	/**
	 * Creates an sqlite database and executes corresponding actions when the database is created,
	 * opened, and updated respectively.
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 * @param actionOnCreate
	 *            <span>A Runnable action to execute when the database is created.</span>
	 * @param actionOnOpen
	 *            <span>A Runnable action to execute when the database is opened.</span>
	 * @param actionOnUpgrade
	 *            <span>A Runnable action to execute when the database is upgraded.</span>
	 * @return The SQLiteDatabase object.
	 * @throws SQLiteException
	 */
	public SQLiteDatabase createDb(String dbName, Runnable actionOnCreate, Runnable actionOnOpen,
			Runnable actionOnUpgrade) {
		return createOrOpenDatabase(dbName, actionOnCreate, actionOnOpen, actionOnUpgrade);
	}

	/**
	 * Opens a database.
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 * @return
	 * @throws SQLiteException
	 */
	public SQLiteDatabase openDb(String dbName) {
		return createOrOpenDatabase(dbName);
	}

	/**
	 * Another way to call openDb(String dbName).
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 */
	public void selectDb(String dbName) {
		openDb(dbName);
	}

	public static SQLiteDatabase openDb(SQLiteDatabase db) {
		return SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Closes the current database associated with this Dbtool instance.
	 */
	public void closeDb() {
		if(_db != null) {
			_db.close();
		}
	}

	/**
	 * Closes an sqlite database.
	 * 
	 * @param db
	 *            <span>The SQLiteDatabase object to close</span>
	 */
	public static void closeDb(SQLiteDatabase db) {
		db.close();
	}

	/**
	 * Drops/deletes a database.
	 * 
	 * @param dbName
	 *            <span>The database name.</span>
	 * @return
	 */
	public boolean dropDb(String dbName) {
		if(_context != null) {
			try {
				return _context.deleteDatabase(dbName);
			}
			catch (Exception ex) {
				// maybe the database does not exist.
				return false;
			}
		}
		return false;
	}

	/**
	 * Drops/deletes a database.
	 * 
	 * @param context
	 *            <span>The working context.</span>
	 * @param dbName
	 *            <span>The database name.</span>
	 * @return true on success. false otherwise.
	 */
	public static boolean dropDb(Context context, String dbName) {
		return context.deleteDatabase(dbName);
	}

	/**
	 * Creates a database table from a TableProfile object. Deletes the table first if it exists.
	 * 
	 * @param tableProfile
	 *            <span>The TableProfile object to create the table from.</span>
	 * @return TRUE on success. FALSE otherwise.
	 */
	public Boolean createTable(TableProfile tableProfile) {
		if(_db != null) {
			dropTable(tableProfile.getTableName());
			if(!tableProfile.isBuilt()) {
				tableProfile.build();
			}
			try {
				this.exec(tableProfile.getCreateSQL());
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Creates a database table from an SQL create table string.
	 * 
	 * @param String
	 *            createTableSql
	 * @return boolean true or false depending on the outcome.
	 */
	public Boolean createTable(String createTableSql) {
		if(_db != null) {
			try {
				this.exec(createTableSql);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Deletes a table from the active database.
	 * 
	 * @param table
	 *            The table name to drop.
	 */
	public void dropTable(String table) {
		if(_db != null) {
			String sql = "DROP TABLE IF EXISTS " + table;
			this.exec(sql);
		}
	}

	/**
	 * Run a query with sqliteDb.query(...).
	 */
	public Object run() {
		if(hasActionUnit()) {
			return popActionUnit().run();
		}
		return null;
	}

	/**
	 * Executes a query with sqliteDb.execSQL(...)
	 * 
	 * @param sql
	 */
	public void exec(String sql) {
		_db.execSQL(sql);
	}

	protected void toast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
	}

	protected void toastlong(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}

	/*
	 * Inner class: CogDbHelper ==============================================================
	 */
	private class DbHelper extends SQLiteOpenHelper {

		private Runnable actionOnCreate;
		private Runnable actionOnUpgrade;
		private Runnable actionOnOpen;

		DbHelper(String dbName) {
			super(_context, dbName, null, _dbVersion);
		}

		DbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		void setActionOnCreate(Runnable action) {
			this.actionOnCreate = action;
		}

		void setActionOnUpdate(Runnable action) {
			this.actionOnUpgrade = action;
		}

		void setActionOnOpen(Runnable action) {
			this.actionOnOpen = action;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if(dbExists(Dbtool.getDbName(db))) {
				if(this.actionOnCreate != null) {
					try {
						/*
						 * Use reflection to get and instantiate the SQLite Database as defined by
						 * the user in the Runnable parameter passed to the createDb overloaded
						 * method of Dbtool.
						 */
						Class<?> actionClass = actionOnCreate.getClass();
						Field[] fields = actionClass.getFields();
						for(Field field : fields) {
							if(field.getType() == Class
									.forName("android.database.sqlite.SQLiteDatabase")) {
								field.set(actionOnCreate, db);
								break;
							}
						}
						this.actionOnCreate.run();
					}
					catch (ClassNotFoundException ex) {
						// no SQLiteDatabase field in the runnable. Assume user
						// does not need to use any.
					}
					catch (Exception ex) {
						// other error.
					}
				}
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(this.actionOnUpgrade != null) {
				try {
					/*
					 * Use reflection to get and instantiate the SQLite Database, the old verion
					 * number, and the new version number, as defined by the user in the Runnable
					 * parameter passed to the createDb(String dbName, Runnable actionOnUpgrade)
					 * method of Dbtool.
					 */
					Class<?> actionClass = actionOnUpgrade.getClass();
					Field[] fields = actionClass.getFields();
					for(Field field : fields) {
						if(field.getType() == Class
								.forName("android.database.sqlite.SQLiteDatabase")) {
							field.set(actionOnUpgrade, db);
						}
						if(field.getType() == Class.forName("java.lang.Integer")) {
							if(field.getName() == "oldVersion") {
								field.set(actionOnUpgrade, oldVersion);
							}
							if(field.getName() == "newVersion") {
								field.set(actionOnUpgrade, newVersion);
							}
						}
					}
					this.actionOnUpgrade.run();
				}
				catch (ClassNotFoundException ex) {
					// no SQLiteDatabase field in the runnable. Assume user does
					// not need to use any.
				}
				catch (Exception ex) {
					// other error.
				}
			}
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			if(this.actionOnOpen != null) {
				try {
					/*
					 * Use reflection to get and instantiate the SQLite Database as defined by the
					 * user in the Runnable parameter passed to the createDb overloaded method of
					 * Dbtool.
					 */
					Class<?> actionClass = actionOnOpen.getClass();
					Field[] fields = actionClass.getFields();
					for(Field field : fields) {
						if(field.getType() == Class
								.forName("android.database.sqlite.SQLiteDatabase")) {
							field.set(actionOnOpen, db);
							break;
						}
					}
					this.actionOnOpen.run();
				}
				catch (ClassNotFoundException ex) {
					// no SQLiteDatabase field in the runnable. Assume user does
					// not need to use any.
				}
				catch (Exception ex) {
					// other error.
				}
			}
		}

	} // ..end DbHelper class

}