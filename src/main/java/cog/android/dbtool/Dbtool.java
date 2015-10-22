package cog.android.dbtool;

import java.util.Stack;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import cog.android.dbtool.table.TableNotFoundException;
import cog.android.dbtool.table.TableProfile;

/**
 * Utility class to help with carrying out common actions on a database.
 * 
 * @author Ikenna Agbasimalo
 */
public class Dbtool implements IDbtoolAction {
	private SQLiteDatabase _sqliteDb;
	private String _dbName;
	private Stack<DbtoolActionUnit> actionUnitStack;
	private DbtoolActionUnit currentActionUnit;

	/**
	 * Private constructor.
	 * 
	 * @param c
	 *        The current context.
	 */
	private Dbtool(SQLiteDatabase db) {
		setDb(db);
		actionUnitStack = new Stack<DbtoolActionUnit>();
	}

	/**
	 * Returns an instance of Dbtool. THIS DOES <b>NOT</b> RETURN A Singleton.
	 * 
	 * @param c
	 *        The context.
	 * @param db
	 *        An SQLiteDatabase object.
	 * @return The Dbtool instance.
	 */
	public static Dbtool getInstance(SQLiteDatabase db) {
		return new Dbtool(db);
	}

	private void addActionUnit(DbtoolActionUnit actionUnit) {
		if (okToStartNewActionUnit()) {
			actionUnitStack.push(actionUnit);
			currentActionUnit = actionUnit;
		}
	}

	private DbtoolActionUnit getLatestActionUnit() {
		return actionUnitStack.elementAt(actionUnitStack.size() - 1);
	}

	/*
	 * This is called at the end of the run() method.
	 * 
	 * @return
	 */
	private DbtoolActionUnit popActionUnit() {
		currentActionUnit = actionUnitStack.pop();
		return currentActionUnit;
	}

	private boolean okToStartNewActionUnit() {
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
	private boolean hasActionUnit() {
		return actionUnitStack.size() > 0 ? true : false;
	}

	/**
	 * Sets the SQLiteDatabase to work with and extracts the database name from
	 * it. This does NOT open the database. You have to explicitly call
	 * dbtool.open().
	 * 
	 * @param db
	 *        An SQLiteDatabase to use with the dbtool instance.
	 */
	public void setDb(SQLiteDatabase db) {
		_sqliteDb = db;
		extractDbName(_sqliteDb);
	}

	/**
	 * Extracts the database name from an SQLiteDatabase.
	 * 
	 * @param db
	 *        The SQLiteDatabase.
	 */
	private void extractDbName(SQLiteDatabase db) {
		if (db == null) {
			throw new RuntimeException("Null SQLiteDatabase");
		}
		_dbName = getDbName(db);
	}

	/**
	 * Gets the current sqlite database in use by this Dbtool instance.
	 * 
	 * @return The sqlite database. Returns null if no database is associated
	 *         with this Dbtool instance.
	 */
	public SQLiteDatabase getSQLiteDb() {
		return _sqliteDb;
	}

	/**
	 * Gets the name of the database associated with this Dbtool instance.
	 * 
	 * @return The database name.
	 */
	public String getDbName() {
		if (_sqliteDb == null) {
			throw new NoDatabaseForDbtoolException();
		}
		_dbName = getDbName(_sqliteDb);
		return _dbName;
	}

	/**
	 * Gets the name of an sqlite database object.
	 * 
	 * @param db
	 *        The SQLiteDatabase object whose name to get.
	 * @return The database name by which it is stored.
	 */
	public static String getDbName(SQLiteDatabase db) {
		String[] sections = db.getPath().split("/");
		return sections.length > 1 ? sections[sections.length - 1] : sections[0];
	}

	/**
	 * Checks if a database exists. As a static method, it should be called
	 * thus: Dbtool.dbExists(...);
	 * 
	 * @param databaseName
	 *        The name of the database to check.
	 * @return <b>true</b> if the database exists; <b>false</b> otherwise.
	 */
	public static boolean dbExists(Context c, String databaseName) {
		String[] dbNames = c.databaseList();
		String dbName;
		for (int i = 0; i < dbNames.length; i++) {
			dbName = dbNames[i];
			if (databaseName != null && databaseName.equals(dbName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a table exists in the database specified for this Dbtool
	 * instance.
	 * 
	 * @param table
	 *        The table name.
	 * @return <b>true</b> if the table exists in the database associated with
	 *         this Dbtool instance; <b>false</b> if not.
	 */
	public boolean tableExists(String table) throws NoDatabaseForDbtoolException {
		if (_sqliteDb == null) {
			throw new NoDatabaseForDbtoolException();
		}
		try {
			DatabaseUtils.queryNumEntries(_sqliteDb, table);
			//If no SQLiteException occurs here, the table is assumed to exist.
			return true;
		} catch (SQLiteException ex) {
			return false;
		}
	}

	/**
	 * Checks if a database table in the database associated with the current
	 * Dbtool instance is empty.
	 * 
	 * @param table
	 *        The name of the table to check.
	 * @return <b>true</b> if the table is empty; <b>false</b> otherwise.
	 */
	public boolean tableIsEmpty(String table) throws NoDatabaseForDbtoolException, TableNotFoundException {
		if (_sqliteDb == null) {
			throw new NoDatabaseForDbtoolException();
		}

		//		if (!this.tableExists(table)) {
		//			throw new TableNotFoundException();
		//		}

		return DatabaseUtils.queryNumEntries(_sqliteDb, table) < 1;
	}

	/**
	 * Indication to perform a 'select all' operation which when run() is
	 * called, the result is not a Cursor but a DbRecordSet.
	 * 
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getAll() {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).getAll());
		return this;
	}

	/**
	 * Indication to perform a 'select all' operation for which when run() is
	 * called, the result will be a Cursor.
	 */
	public Dbtool selectAll() {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).selectAll());
		return this;
	}

	/**
	 * Indication to perform a 'select' operation for which when 'run()' is
	 * called, the result will be one other than Cursor.
	 * 
	 * @param columns
	 *        Array of column names to perform the query on.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String[] columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).get(columns));
		return this;
	}

	public Dbtool select(String[] columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query for which when 'run()' is called,
	 * the result will be one other than Cursor.
	 * 
	 * @param columns
	 *        Comma-separated string of columns.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(String columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).get(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query.
	 * 
	 * @param columns
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool select(String columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query.
	 * 
	 * @param columns
	 *        Variable-number arguments indicating the columns to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool get(Object... columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).get(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query. This method differs from the
	 * get() methods in that it returns a Cursor object.
	 * 
	 * @param columns
	 *        String arguments representing the columns to perfom the query on.
	 * @return
	 */
	public Dbtool select(Object... columns) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).select(columns));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is a
	 * string.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getString(String column) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).getString(column));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is an
	 * integer.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getInt(String column) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).getInt(column));
		return this;
	}

	/**
	 * Indication to perform a 'select' query to get a single value which is a
	 * float.
	 * 
	 * @param column
	 *        The column to query.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool getFloat(String column) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).getFloat(column));
		return this;
	}

	public Dbtool getDouble(String column) {
		addActionUnit(new DbtoolActionUnit(_sqliteDb).getDouble(column));
		return null;
	}

	/**
	 * Sets the database table to perform a 'select' or 'delete' query on.
	 * 
	 * @param table
	 *        The table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool from(String table) {
		if (hasActionUnit()) {
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
	public Dbtool from(Object... tables) {
		if (hasActionUnit()) {
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
	public Dbtool update(String table) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).update(table));
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
	public Dbtool update(Object... tables) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).update(tables));
		}
		return this;
	}

	/**
	 * Indication to perform an 'insert' query on a table, passing the values to
	 * be inserted. The probable way this method would be used is thus:
	 * dbtool.insert(values).into(tablename).run();
	 * 
	 * @param values
	 *        A ContentValues object containing the values to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insert(ContentValues values) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).insert(values));
		}
		return this;
	}

	public Dbtool handleInsertConflict(int conflictAlgorithm) {
		getLatestActionUnit().handleInsertConflict(conflictAlgorithm);
		return this;
	}

	/**
	 * Sets the table on which to run an 'insert' query. Used thus:
	 * <b>dbtool.insert(values).into(table).run;</b>.
	 * 
	 * @param table
	 *        The database table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool into(String table) {
		getLatestActionUnit().into(table);
		return this;
	}

	/**
	 * Indication to perform an 'insert' query on a table.
	 * 
	 * @param table
	 *        The table name.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool insertInto(String table) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).insertInto(table));
		}
		return this;
	}

	/**
	 * Sets the values to insert into a table. Used thus:
	 * <b>dbtool.insertInto(table).values(values).run();</b>.
	 * 
	 * @param values
	 *        A ContentValues object containing the values to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool values(ContentValues values) {
		if (hasActionUnit()) {
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
	public Dbtool record(DbRecord row) {
		if (hasActionUnit()) {
			getLatestActionUnit().record(row);
		}
		return this;
	}

	/**
	 * Alias for <b>record(row)</b>.
	 */
	public Dbtool row(DbRecord row) {
		return this.record(row);
	}

	/**
	 * Specifies a RecordSet for an 'insert' operation. Used thus:
	 * <b>dbtool.insertInto(table).recordSet(rows).run;</b>.
	 * 
	 * @param rows
	 *        The RecordSet object to insert.
	 * @return The Dbtool object, for chaining purposes.
	 */
	public Dbtool recordSet(DbRecordSet rows) {
		if (hasActionUnit()) {
			getLatestActionUnit().recordSet(rows);
		}
		return this;
	}

	/**
	 * Specifies a set of rows for an <b>insert</b> operation. Alias for
	 * <b>recordSet(rows)</b>.
	 */
	public Dbtool rows(DbRecordSet rows) {
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
	public Dbtool set(String name, Object value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, Byte value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, int value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, float value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, short value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, byte[] value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, String value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, double value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, long value) {
		if (hasActionUnit()) {
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
	public Dbtool set(String name, boolean value) {
		if (hasActionUnit()) {
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
	public Dbtool deleteFrom(String table) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).deleteFrom(table));
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
	public Dbtool deleteFrom(Object... tables) {
		if (okToStartNewActionUnit()) {
			addActionUnit(new DbtoolActionUnit(_sqliteDb).deleteFrom(tables));
		}
		return this;
	}

	/**
	 * Sets the <b>where</b> clause for a <b>select</b>, <b>delete</b>, or
	 * <b>update</b> query. Used thus:
	 * <b>dbtool.get(columnName).from(table).where(whereClause).run();</b>.
	 * 
	 * @param whereClause
	 *        The where clause.
	 * @return
	 */
	public Dbtool where(String whereClause) {
		if (hasActionUnit()) {
			getLatestActionUnit().where(whereClause);
		}
		return this;
	}

	public Dbtool and() {
		if (hasActionUnit()) {
			getLatestActionUnit().and();
		}
		return this;
	}

	public Dbtool or() {
		if (hasActionUnit()) {
			getLatestActionUnit().or();
		}
		return this;
	}

	public Dbtool whereLike(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereLike(field, value);
		}
		return this;
	}

	public Dbtool whereEquals(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereEquals(field, value);
		}
		return this;
	}

	public Dbtool whereNotEquals(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereNotEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThan(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereLessThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThan(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereGreaterThan(field, value);
		}
		return this;
	}

	public Dbtool whereGreaterThanOrEquals(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereGreaterThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereLessThanOrEquals(String field, Object value) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereLessThanOrEquals(field, value);
		}
		return this;
	}

	public Dbtool whereArgs(String[] whereArgs) {
		if (hasActionUnit()) {
			getLatestActionUnit().whereArgs(whereArgs);
		}
		return this;
	}

	public Dbtool distinct() {
		if (hasActionUnit()) {
			getLatestActionUnit().distinct();
		}
		return this;
	}

	public Dbtool columns(String[] columns) {
		if (hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(String columns) {
		if (hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool columns(Object... columns) {
		if (hasActionUnit()) {
			getLatestActionUnit().columns(columns);
		}
		return this;
	}

	public Dbtool groupBy(String groupBy) {
		if (hasActionUnit()) {
			getLatestActionUnit().groupBy(groupBy);
		}
		return this;
	}

	public Dbtool having(String having) {
		if (hasActionUnit()) {
			getLatestActionUnit().having(having);
		}
		return this;
	}

	public Dbtool orderBy(String orderBy) {
		if (hasActionUnit()) {
			getLatestActionUnit().orderBy(orderBy);
		}
		return this;
	}

	public Dbtool limit(String limit) {
		if (hasActionUnit()) {
			getLatestActionUnit().limit(limit);
		}
		return this;
	}

	public boolean isOpen() {
		return _sqliteDb.isOpen();
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
	public static boolean dropDb(Context context, String dbName) {
		return context.deleteDatabase(dbName);
	}

	/**
	 * Creates a database table from a TableProfile object.
	 * 
	 * @param tableProfile
	 *        The TableProfile object to create the table from.
	 * @return TRUE on success. FALSE otherwise.
	 */
	public boolean createTable(TableProfile tableProfile) {
		if (_sqliteDb != null && tableProfile != null) {
			if (!tableProfile.isBuilt()) {
				tableProfile.build();
			}
			this.exec(tableProfile.getCreateSQL());
			return true;
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
	public void createTable(String createTableSql) {
		this.exec(createTableSql);
	}

	/**
	 * Creates a new database table from a TableProfile object. Deletes the
	 * table first if it exists.
	 * 
	 * @param tableProfile
	 *        The TableProfile object to create the table from.
	 * @return TRUE on success. FALSE otherwise.
	 */
	public void createNewTable(TableProfile tableProfile) {
		if (tableProfile == null) {
			throw new RuntimeException("Null TableProfile object.");
		}
		dropTable(tableProfile.getTableName());
		if (!tableProfile.isBuilt()) {
			tableProfile.build();
		}
		this.exec(tableProfile.getCreateSQL());
	}

	/**
	 * Deletes a table from the active database.
	 * 
	 * @param table
	 *        The table to drop.
	 */
	public void dropTable(String table) {
		if (_sqliteDb == null) {
			throw new NoDatabaseForDbtoolException();
		}

		String sql = "DROP TABLE IF EXISTS " + table;
		this.exec(sql);
	}

	/**
	 * Run the active query with sqliteDb.query(...).
	 */
	public Object run() {
		if (hasActionUnit()) {
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
	public void exec(String sql) {
		if (_sqliteDb == null)
			throw new NoDatabaseForDbtoolException();
		_sqliteDb.execSQL(sql);
	}

}