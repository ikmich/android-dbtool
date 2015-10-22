package org.ikmich.android.dbtools.bak;
//package org.ikmich.android.db.bak;
//
//import java.util.Stack;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.widget.Toast;
//
///**
// * Utility class to assist with common actions on a database.
// * 
// * @author Ikenna Agbasimalo
// */
//public class DbAction3 implements IQuery {
//
//	private SQLiteDatabase _db;
//	private String _dbName;
//	private static int _dbVersion = 1;
//
//	private DbHelper _dbHelper;
//	private static Context _context;
//
//	private String[] _columns = null;
//	private String _table = null;
//	private String _whereClause = "";
//	private String _action = "";
//
//	private Stack<String> tableStack;
//	private Stack<String> actionStack;
//	private Stack<String> whereStack;
//	private Stack<String[]> columnsStack;
//
//	private static final String ACTION_INSERT = "insert";
//	private static final String ACTION_DELETE = "delete";
//	private static final String ACTION_SELECT = "select";
//	private static final String ACTION_UPDATE = "update";
//
//	private SelectQuery _selectQuery = null;
//	private UpdateQuery _updateQuery = null;
//	private DeleteQuery _deleteQuery = null;
//	private InsertQuery _insertQuery = null;
//
//	private boolean _gettingSingleValue = false;
//	private String _singleValueType = ""; //string | integer | float
//	private static final String SINGLE_VALUE_TYPE_STRING = "string";
//	private static final String SINGLE_VALUE_TYPE_INT = "integer";
//	private static final String SINGLE_VALUE_TYPE_FLOAT = "float";
//
//
//	/**
//	 * Constructor. Creates a DbAction2 instance.
//	 * 
//	 * @param c
//	 *            <span>The current context.</span>
//	 */
//	public DbAction3(Context c) {
//		_context = c;
//		init();
//	}
//
//
//	/**
//	 * Constructor. Creates a DbAction2 instance to work with an existing sqlite
//	 * database object.
//	 * 
//	 * @param c
//	 *            <span>The current context.</span>
//	 * @param SQLiteDatabase
//	 *            db
//	 */
//	public DbAction3(Context c, SQLiteDatabase db) {
//		this(c);
//		setDatabase(db);
//	}
//
//
//	/**
//	 * Perform initializations.
//	 */
//	private void init() {
//		_table = "";
//		_action = "";
//		tableStack = new Stack<String>();
//		actionStack = new Stack<String>();
//		whereStack = new Stack<String>();
//		columnsStack = new Stack<String[]>();
//	}
//
//
//	@Override
//	public DbAction3 context(Context c) {
//		_context = c;
//		return this;
//	}
//
//
//	/**
//	 * Sets the sqlite database to act upon. Closes the last database opened by
//	 * this DbAction2 object.
//	 * 
//	 * @param db
//	 *            <span>The SQLiteDatabase instance</span>
//	 * @return The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 setDatabase(SQLiteDatabase db) {
//		_db = db;
//		openDatabase(_db);
//		return this;
//	}
//
//
//	/**
//	 * Sets the active sqlite database (via its name).
//	 * 
//	 * @param dbName
//	 *            <span>The name of the sqlite database.</span>
//	 * 
//	 * @return The DbAction2 object, for chaining purposes.
//	 * @throws org.ikmich.android.db.DatabaseNotExistException
//	 */
//	public DbAction3 setDatabase(String dbName) throws DatabaseNotExistException {
//		if(databaseExists(dbName)) {
//			setDatabase(createOrOpenDatabase(dbName));
//		} else {
//			throw new DatabaseNotExistException();
//		}
//		return this;
//	}
//
//
//	/**
//	 * Gets the current sqlite database in use by this DbAction2 instance.
//	 * 
//	 * @return The sqlite database. Returns null if no database is associated
//	 *         with this DbAction2 instance.
//	 */
//	public SQLiteDatabase getDatabase() {
//		return _db;
//	}
//
//
//	/**
//	 * Gets the sqlite database object represented by the name with which it's
//	 * stored.
//	 * 
//	 * @param dbName
//	 *            <span>The name of the database.</span>
//	 * @return The SQLiteDatabase object.
//	 * @throws org.ikmich.android.db.DatabaseNotExistException
//	 */
//	public SQLiteDatabase getDatabase(String dbName)
//			throws DatabaseNotExistException {
//		if(databaseExists(dbName)) {
//			return (createOrOpenDatabase(dbName));
//		}
//		throw new DatabaseNotExistException();
//	}
//
//
//	/**
//	 * Gets the name of the database associated with this DbAction2 instance.
//	 * 
//	 * @return
//	 */
//	public String getDatabaseName() {
//		return _dbName;
//	}
//
//
//	/**
//	 * Gets the name of an sqlite database object.
//	 * 
//	 * @param db
//	 *            <span>The SQLiteDatabase object whose name to get.</span>
//	 * @return The database name by which it is stored.
//	 */
//	public String getDatabaseName(SQLiteDatabase db) {
//		String path = db.getPath();
//		String[] sections = path.split("/");
//		return path.split("/")[sections.length - 1];
//	}
//
//
//	/**
//	 * Sets the database version to be used when createDatabase() is called.
//	 * 
//	 * @param dbVersion
//	 *            <span>The database version</span>
//	 * @return The DbAction2 object for chaining purposes.
//	 */
//	public DbAction3 setDatabaseVersion(int dbVersion) {
//		_dbVersion = dbVersion;
//		return this;
//	}
//
//
//	private void initSelectQuery() {
//		if(_selectQuery == null) {
//			_selectQuery = new SelectQuery(_db).context(_context);
//		}
//	}
//
//
//	private void initInsertQuery() {
//		if(_insertQuery == null) {
//			_insertQuery = new InsertQuery(_db);
//		}
//	}
//
//
//	private void initUpdateQuery() {
//		if(_updateQuery == null) {
//			_updateQuery = new UpdateQuery(_db);
//		}
//	}
//
//
//	private void initDeleteQuery() {
//		if(_deleteQuery == null) {
//			_deleteQuery = new DeleteQuery(_db);
//		}
//	}
//
//
//	private void initDbHelper(String dbName) {
//		_dbHelper = new DbHelper(dbName);
//	}
//
//
//	private void setSelectAction() {
//		_action = ACTION_SELECT;
//		addActiveAction(ACTION_SELECT);
//	}
//
//
//	private void setInsertAction() {
//		_action = ACTION_INSERT;
//		addActiveAction(ACTION_INSERT);
//	}
//
//
//	private void setUpdateAction() {
//		_action = ACTION_UPDATE;
//		addActiveAction(ACTION_UPDATE);
//	}
//
//
//	private void setDeleteAction() {
//		_action = ACTION_DELETE;
//		addActiveAction(ACTION_DELETE);
//	}
//
//
//	private boolean isSelectAction() {
//		return _action == ACTION_SELECT ? true : false;
//	}
//
//
//	private boolean isInsertAction() {
//		return _action == ACTION_INSERT ? true : false;
//	}
//
//
//	private boolean isUpdateAction() {
//		return _action == ACTION_UPDATE ? true : false;
//	}
//
//
//	private boolean isDeleteAction() {
//		return _action == ACTION_DELETE ? true : false;
//	}
//
//
//	public static boolean databaseExists(String databaseName) {
//		String[] dbNames = _context.databaseList();
//		for(String dbName : dbNames) {
//			if(databaseName.equals(dbName)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//
//	private String getActiveTable() {
//		if(tableStack.size() == 1) {
//			return tableStack.elementAt(0);
//		}
//		return tableStack.pop();
//	}
//
//
//	private void addActiveTable(String table) {
//		tableStack.push(table);
//	}
//
//
//	private String getActiveAction() {
//		if(actionStack.size() == 1) {
//			return actionStack.elementAt(0);
//		}
//		return actionStack.pop();
//	}
//
//
//	private void addActiveAction(String action) {
//		actionStack.push(action);
//	}
//	
//	
//	private String getActiveWhereClause() {
//		if(whereStack.size() == 1) {
//			return whereStack.elementAt(0);
//		}
//		return whereStack.pop();
//	}
//	
//	private void addActiveWhereClause(String whereClause) {
//		whereStack.push(whereClause);
//	}
//	
//	private String[] getActiveColumns() {
//		if(columnsStack.size() == 1) {
//			return columnsStack.elementAt(0);
//		}
//		return columnsStack.pop();
//	}
//	
//	private void addActiveColumns(String[] columns) {
//		columnsStack.push(columns);
//	}
//
//
//	/**
//	 * Checks if a table exists in the current database associated with this
//	 * DbAction2 instance.
//	 * 
//	 * @param String
//	 *            table The table name.
//	 * @return boolean
//	 */
//	public boolean tableExists(String table)
//			throws NoAssociatedDatabaseException {
//		if(_db != null) {
//			try {
//				this.getAll().from(table).run();
//				return true;
//			} catch (Exception e) {
//				return false;
//			}
//		}
//		throw new NoAssociatedDatabaseException();
//	}
//
//
//	@Override
//	public DbAction3 table(String table) {
//		addActiveTable(table);
//		_table = getActiveTable();
//
//		if(_selectQuery != null) {
//			_selectQuery.table(_table);
//		}
//		if(_insertQuery != null) {
//			_insertQuery.table(_table);
//		}
//		if(_updateQuery != null) {
//			_updateQuery.table(_table);
//		}
//		if(_deleteQuery != null) {
//			_deleteQuery.table(_table);
//		}
//
//		return this;
//	}
//
//
//	@Override
//	public DbAction3 table(Object... tables) {
//		for(int i = 0; i < tables.length; i++) {
//			_table += (String) tables[i];
//			if(i < (tables.length - 1)) {
//				_table += ", ";
//			}
//		}
//
//		addActiveTable(_table);
//		_table = getActiveTable();
//
//		if(_selectQuery != null) {
//			_selectQuery.table(_table);
//		}
//		if(_insertQuery != null) {
//			_insertQuery.table(_table);
//		}
//		if(_updateQuery != null) {
//			_updateQuery.table(_table);
//		}
//		if(_deleteQuery != null) {
//			_deleteQuery.table(_table);
//		}
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a select all query.
//	 * 
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 getAll() {
//		setSelectAction();
//		initSelectQuery();
//		_columns = null;
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a select query.
//	 * 
//	 * @param String
//	 *            [] columns Array of column names to perform the query on.
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 get(String[] columns) {
//		this._gettingSingleValue = false;
//		setSelectAction();
//		initSelectQuery();
//		
//		addActiveColumns(columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a select query.
//	 * 
//	 * @param String
//	 *            columns Comma-separated string of columns.
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 get(String columns) {
//		this._gettingSingleValue = false;
//		setSelectAction();
//		initSelectQuery();
//		
//		_columns = columns.split(",\\s*");
//		
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//
//		return this;
//	}
//
//
//	public DbAction3 getString(String column) {
//		this._gettingSingleValue = true;
//		this._singleValueType = SINGLE_VALUE_TYPE_STRING;
//
//		setSelectAction();
//		initSelectQuery();
//		_columns = new String[] { column };
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//		return this;
//	}
//
//
//	public DbAction3 getInt(String column) {
//		_gettingSingleValue = true;
//		_singleValueType = SINGLE_VALUE_TYPE_INT;
//
//		setSelectAction();
//		initSelectQuery();
//		_columns = new String[] { column };
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//
//		return this;
//	}
//
//
//	public DbAction3 getFloat(String column) {
//		this._gettingSingleValue = true;
//		this._singleValueType = SINGLE_VALUE_TYPE_FLOAT;
//
//		setSelectAction();
//		initSelectQuery();
//		_columns = new String[] { column };
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a select query.
//	 * 
//	 * @param Object
//	 *            [] columns Variable-number arguments indicating the columns to
//	 *            query.
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 get(Object... columns) {
//		this._gettingSingleValue = false;
//		setSelectAction();
//		initSelectQuery();
//
//		_columns = new String[columns.length];
//		for(int i = 0; i < columns.length; i++) {
//			_columns[i] = (String) columns[i];
//		}
//		
//		addActiveColumns(_columns);
//		_columns = getActiveColumns();
//		_selectQuery.columns(_columns);
//
//		return this;
//	}
//
//
//	/**
//	 * Sets the table to perform a select or delete query on.
//	 * 
//	 * @param String
//	 *            table
//	 * @return DbAction2
//	 */
//	public DbAction3 from(String table) {
//		_table = table;
//		addActiveTable(_table);
//
//		initSelectQuery();
//		initDeleteQuery();
//
//		_table = getActiveTable();
//		_selectQuery.table(_table);
//		_deleteQuery.table(_table);
//
//		return this;
//	}
//
//
//	/**
//	 * Sets the table(s) to perform a select or delete query on.
//	 * 
//	 * @param String
//	 *            tables
//	 * @return
//	 */
//	public DbAction3 from(Object... tables) {
//		for(int i = 0; i < tables.length; i++) {
//			_table += (String) tables[i];
//			if(i < (tables.length - 1)) {
//				_table += ", ";
//			}
//		}
//
//		addActiveTable(_table);
//
//		initSelectQuery();
//		initDeleteQuery();
//
//		_table = getActiveTable();
//		_selectQuery.table(_table);
//		_deleteQuery.table(_table);
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform an update query.
//	 * 
//	 * @return
//	 */
//	public DbAction3 update() {
//		setUpdateAction();
//		initUpdateQuery();
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform an update query on a table.
//	 * 
//	 * @param String
//	 *            table
//	 * @return
//	 */
//	public DbAction3 update(String table) {
//		update();
//		//_activeTable = table;
//		addActiveTable(_table);
//		_table = getActiveTable();
//		_updateQuery.table(_table);
//
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform an insert query.
//	 * 
//	 * @return
//	 */
//	protected DbAction3 insert() {
//		setInsertAction();
//		initInsertQuery();
//		return this;
//	}
//
//
//	public DbAction3 insert(ContentValues values) {
//		setInsertAction();
//		initInsertQuery();
//
//		_insertQuery.values(values);
//		return this;
//	}
//
//
//	public DbAction3 into(String table) {
//		return this.insertInto(table);
//	}
//
//
//	/**
//	 * Indication to perform an insert query on a table.
//	 * 
//	 * @param String
//	 *            table
//	 * @return
//	 */
//	public DbAction3 insertInto(String table) {
//		setInsertAction();
//		initInsertQuery();
//
//		_table = table;
//		addActiveTable(_table);
//		_table = getActiveTable();
//		_insertQuery.table(_table);
//		return this;
//	}
//
//
//	@Override
//	public DbAction3 values(ContentValues values) {
//		initUpdateQuery();
//		initInsertQuery();
//
//		_updateQuery.values(values);
//		_insertQuery.values(values);
//
//		return this;
//	}
//
//
//	//for update, insert
//	@Override
//	public DbAction3 set(String name, Byte value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	//for update, insert
//	@Override
//	public DbAction3 set(String name, int value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, float value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, short value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, byte[] value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, String value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, double value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, long value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/*
//	 * for update, insert
//	 */
//	@Override
//	public DbAction3 set(String name, boolean value) {
//		initUpdateQuery();
//		initInsertQuery();
//		_updateQuery.set(name, value);
//		_insertQuery.set(name, value);
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a delete query.
//	 * 
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 delete() {
//		setDeleteAction();
//		initDeleteQuery();
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a delete query on a table.
//	 * 
//	 * @param String
//	 *            table
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 deleteFrom(String table) {
//		delete();
//		_table = table;
//		addActiveTable(_table);
//		_table = getActiveTable();
//		_deleteQuery.table(_table);
//		return this;
//	}
//
//
//	/**
//	 * Indication to perform a delete query on (a) table(s).
//	 * 
//	 * @param Object
//	 *            [] tables
//	 * @return DbAction2 The DbAction2 object, for chaining purposes.
//	 */
//	public DbAction3 deleteFrom(Object... tables) {
//		delete();
//		for(int i = 0; i < tables.length; i++) {
//			_table += (String) tables[i];
//			if(i < (tables.length - 1)) {
//				_table += ", ";
//			}
//		}
//		addActiveTable(_table);
//		_table = getActiveTable();
//		_deleteQuery.table(_table);
//		return this;
//	}
//
//
//	@Override
//	public DbAction3 where(String whereClause) {
//		initSelectQuery();
//		initUpdateQuery();
//		initDeleteQuery();
//
//		//_whereClause = whereClause;
//		addActiveWhereClause(whereClause);
//		_whereClause = getActiveWhereClause();
//		
//		if(isSelectAction()) {
//			_selectQuery.where(_whereClause);
//		} else if(isUpdateAction()) {
//			_updateQuery.where(_whereClause);
//		} else if(isDeleteAction()) {
//			_deleteQuery.where(_whereClause);
//		}
//
//		return this;
//	}
//
//
//	public DbAction3 and() {
//		if(isSelectAction()) {
//			_selectQuery.and();
//		} else if(isUpdateAction()) {
//			_updateQuery.and();
//		} else if(isDeleteAction()) {
//			_deleteQuery.and();
//		}
//
//		return this;
//	}
//
//
//	public DbAction3 or() {
//		if(isSelectAction()) {
//			_selectQuery.or();
//		} else if(isUpdateAction()) {
//			_updateQuery.or();
//		} else if(isDeleteAction()) {
//			_deleteQuery.or();
//		}
//
//		return this;
//	}
//
//
//	public DbAction3 whereLike(String field, Object value) {
//		if(isSelectAction()) {
//			_selectQuery.and();
//		} else if(isUpdateAction()) {
//			_updateQuery.whereLike(field, value);
//		} else if(isDeleteAction()) {
//			_deleteQuery.and();
//		}
//
//		return this;
//	}
//
//
//	public DbAction3 whereEquals(String field, Object value) {
//		String whereClause;
//		if(value instanceof String) {
//			whereClause = field + "='" + value + "'";
//		} else {
//			whereClause = field + "=" + value;
//		}
//		addActiveWhereClause(whereClause);
//		_whereClause = getActiveWhereClause();
//		where(_whereClause);
//		return this;
//	}
//
//
//	public DbAction3 whereLessThan(String field, Object value) {
//		String whereClause;
//		if(value instanceof String) {
//			whereClause = field + "<'" + value + "'";
//		} else {
//			whereClause = field + "<" + value;
//		}
//		where(whereClause);
//		return this;
//	}
//
//
//	public DbAction3 whereGreaterThan(String field, Object value) {
//		String whereClause;
//		if(value instanceof String) {
//			whereClause = field + ">'" + value + "'";
//		} else {
//			whereClause = field + ">" + value;
//		}
//		where(whereClause);
//		return this;
//	}
//
//
//	public DbAction3 whereGreaterThanOrEquals(String field, Object value) {
//		String whereClause;
//		if(value instanceof String) {
//			whereClause = field + ">='" + value + "'";
//		} else {
//			whereClause = field + ">=" + value;
//		}
//		where(whereClause);
//		return this;
//	}
//
//
//	public DbAction3 whereLessThanOrEquals(String field, Object value) {
//		String whereClause;
//		if(value instanceof String) {
//			whereClause = field + ">='" + value + "'";
//		} else {
//			whereClause = field + ">=" + value;
//		}
//		where(whereClause);
//		return this;
//	}
//
//
//	/*
//	 * for select, update, insert, delete
//	 */
//	public DbAction3 whereArgs(String[] whereArgs) {
//		initSelectQuery();
//		initUpdateQuery();
//		initDeleteQuery();
//
//		_selectQuery.whereArgs(whereArgs);
//		_updateQuery.whereArgs(whereArgs);
//		_deleteQuery.whereArgs(whereArgs);
//
//		return this;
//	}
//
//
//	protected SQLiteDatabase createOrOpenDatabase(String dbName)
//			throws SQLiteException {
//		_dbName = dbName;
//		initDbHelper(_dbName);
//		_db = _dbHelper.getWritableDatabase();
//		return _db;
//	}
//
//
//	protected SQLiteDatabase createOrOpenDatabase(String dbName,
//			Runnable actionOnCreate) {
//		if(actionOnCreate != null) {
//			_dbName = dbName;
//			initDbHelper(_dbName);
//			_dbHelper.setActionOnCreate(actionOnCreate);
//			_db = _dbHelper.getWritableDatabase();
//			return _db;
//		} else {
//			return createOrOpenDatabase(dbName);
//		}
//	}
//
//
//	protected SQLiteDatabase createOrOpenDatabase(String dbName,
//			Runnable actionOnCreate, Runnable actionOnOpen,
//			Runnable actionOnUpdate) {
//		if(actionOnCreate != null || actionOnOpen != null
//				|| actionOnUpdate != null) {
//			_dbName = dbName;
//			initDbHelper(_dbName);
//			if(actionOnCreate != null) {
//				_dbHelper.setActionOnCreate(actionOnCreate);
//			}
//			if(actionOnOpen != null) {
//				_dbHelper.setActionOnOpen(actionOnOpen);
//			}
//			if(actionOnUpdate != null) {
//				_dbHelper.setActionOnUpdate(actionOnUpdate);
//			}
//			_db = _dbHelper.getWritableDatabase();
//			return _db;
//		} else {
//			return createOrOpenDatabase(dbName);
//		}
//	}
//
//
//	/**
//	 * Create an sqlite database with the name supplied.
//	 * 
//	 * @param dbName
//	 *            The name of the database.
//	 * @return The SQLiteDatabase object.
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase createDatabase(String dbName) throws SQLiteException {
//		return createOrOpenDatabase(dbName);
//	}
//
//
//	/**
//	 * Creates an sqlite database and creates an initial table represented by a
//	 * TableProfile object.
//	 * 
//	 * @param dbName
//	 *            The database name to create.
//	 * @param profile
//	 *            The TableProfile object representing the table to create.
//	 * @see org.ikmich.android.db.TableProfile
//	 * @return The SQLiteDatabase object.
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase createDatabase(String dbName, TableProfile profile)
//			throws SQLiteException {
//		_db = createOrOpenDatabase(dbName);
//		createTable(profile);
//		return _db;
//	}
//
//
//	/**
//	 * Creates an sqlite database and creates initial tables represented by an
//	 * array of TableProfile objects.
//	 * 
//	 * @param dbName
//	 *            <span>The database name.</span>
//	 * @param profiles
//	 *            <span>The array of TableProfile objects representing the
//	 *            tables to create.</span>
//	 * @return The SQLiteDatabase object.
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase createDatabase(String dbName, TableProfile[] profiles)
//			throws SQLiteException {
//		_db = createOrOpenDatabase(dbName);
//		for(int i = 0; i < profiles.length; i++) {
//			if(!createTable(profiles[i])) {
//				break; //break if failed.
//			}
//		}
//		return _db;
//	}
//
//
//	/**
//	 * Creates an sqlite database and executes a Runnable action when the
//	 * database is created.
//	 * 
//	 * @param dbName
//	 *            <span>The database name.</span>
//	 * @param actionOnCreate
//	 *            <span>A Runnable action to execute when the database is
//	 *            created.</span>
//	 * @return The SQLiteDatabase object.
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase createDatabase(String dbName, Runnable actionOnCreate)
//			throws SQLiteException {
//		return createOrOpenDatabase(dbName, actionOnCreate);
//	}
//
//
//	/**
//	 * Creates an sqlite database and executes corresponding actions when the
//	 * database is created, opened, and updated respectively.
//	 * 
//	 * @param dbName
//	 *            <span>The database name.</span>
//	 * @param actionOnCreate
//	 *            <span>A Runnable action to execute when the database is
//	 *            created.</span>
//	 * @param actionOnOpen
//	 *            <span>A Runnable action to execute when the database is
//	 *            opened.</span>
//	 * @param actionOnUpdate
//	 *            <span>A Runnable action to execute when the database is
//	 *            updated.</span>
//	 * @return The SQLiteDatabase object.
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase createDatabase(String dbName,
//			Runnable actionOnCreate, Runnable actionOnOpen,
//			Runnable actionOnUpdate) throws SQLiteException {
//		return createOrOpenDatabase(dbName, actionOnCreate, actionOnOpen,
//				actionOnUpdate);
//	}
//
//
//	/**
//	 * Opens a database. Effectively the same as createDatabase(), since the
//	 * latter opens the database if it exists.
//	 * 
//	 * @param dbName
//	 *            <span>The database name.</span>
//	 * @return
//	 * @throws SQLiteException
//	 */
//	public SQLiteDatabase openDatabase(String dbName) throws SQLiteException {
//		return createOrOpenDatabase(dbName);
//	}
//
//
//	public static void openDatabase(SQLiteDatabase db) {
//		SQLiteDatabase.openDatabase(db.getPath(), null,
//				SQLiteDatabase.OPEN_READWRITE);
//	}
//
//
//	private void openDatabase() {
//		if(_db != null) {
//			SQLiteDatabase.openDatabase(_db.getPath(), null,
//					SQLiteDatabase.OPEN_READWRITE);
//		}
//	}
//
//
//	/**
//	 * Closes the current database associated with this DbAction2 instance.
//	 */
//	public void closeDatabase() {
//		if(_db != null) {
//			_db.close();
//		}
//	}
//
//
//	/**
//	 * Closes an sqlite database.
//	 * 
//	 * @param db
//	 *            <span>The SQLiteDatabase object to close</span>
//	 */
//	public static void closeDatabase(SQLiteDatabase db) {
//		db.close();
//	}
//
//
//	/**
//	 * Drops/deletes a database.
//	 * 
//	 * @param dbName
//	 *            <span>The database name.</span>
//	 * @return
//	 */
//	public static boolean dropDatabase(String dbName) {
//		if(_context != null) {
//			return _context.deleteDatabase(dbName);
//		}
//		return false;
//	}
//
//
//	/**
//	 * Creates a database table from a TableProfile object.
//	 * 
//	 * @param tableProfile
//	 *            <span>The TableProfile object to create the table from.</span>
//	 * @return TRUE on success. FALSE otherwise.
//	 */
//	public Boolean createTable(TableProfile tableProfile) {
//		if(_db != null) {
//			dropTable(tableProfile.getTableName());
//			if(!tableProfile.isBuilt()) {
//				tableProfile.build();
//			}
//			try {
//				this.exec(tableProfile.getCreateSQL());
//				return true;
//			} catch (Exception e) {
//				return false;
//			}
//		}
//		return false;
//	}
//
//
//	/**
//	 * Creates a database table from an SQL create table string.
//	 * 
//	 * @param String
//	 *            createTableSql
//	 * @return boolean true or false depending on the outcome.
//	 */
//	public Boolean createTable(String createTableSql) {
//		if(_db != null) {
//			try {
//				this.exec(createTableSql);
//				return true;
//			} catch (Exception e) {
//				return false;
//			}
//		}
//		return false;
//	}
//
//
//	/**
//	 * Deletes a table from the active database.
//	 * 
//	 * @param table
//	 *            The table name to drop.
//	 */
//	public void dropTable(String table) {
//		if(_db != null) {
//			String sql = "DROP TABLE IF EXISTS " + table;
//			this.exec(sql);
//		}
//	}
//
//
//	@Override
//	public IQuery distinct() {
//		initSelectQuery();
//		_selectQuery.distinct();
//		return this;
//	}
//
//
//	@Override
//	public IQuery columns(String[] columns) {
//		initSelectQuery();
//		_selectQuery.columns(columns);
//		return this;
//	}
//
//
//	@Override
//	public IQuery columns(String columns) {
//		initSelectQuery();
//		_selectQuery.columns(columns);
//		return this;
//	}
//
//
//	@Override
//	public IQuery columns(Object... columns) {
//		initSelectQuery();
//		_selectQuery.columns(columns);
//		return this;
//	}
//
//
//	@Override
//	public IQuery groupBy(String groupBy) {
//		initSelectQuery();
//		_selectQuery.groupBy(groupBy);
//		return this;
//	}
//
//
//	@Override
//	public IQuery having(String having) {
//		initSelectQuery();
//		_selectQuery.having(having);
//		return this;
//	}
//
//
//	@Override
//	public IQuery orderBy(String orderBy) {
//		initSelectQuery();
//		_selectQuery.orderBy(orderBy);
//		return this;
//	}
//
//
//	@Override
//	public IQuery limit(String limit) {
//		initSelectQuery();
//		_selectQuery.limit(limit);
//		return this;
//	}
//
//
//	/**
//	 * Run a query with sqliteDb.query(...).
//	 */
//	public Object run() {
//		openDatabase();
//		_action = getActiveAction();
//		if(_action == ACTION_UPDATE) {
//			Integer numRows = _updateQuery.run();
//			return numRows;
//		}
//		if(_action == ACTION_INSERT) {
//			Long rowId = _insertQuery.run();
//			return rowId;
//		}
//		if(_action == ACTION_DELETE) {
//			Integer numRows = _deleteQuery.run();
//			return numRows;
//		}
//		if(_action == ACTION_SELECT) {
//			Cursor c = _selectQuery.run();
//			/*
//			 * if getting one value, return only that value, instead of a Cursor
//			 */
//			if(this._gettingSingleValue) {
//				//_columns = getActiveColumns();
//				if(_columns.length == 1) {
//					String columnName = _columns[0];
//					while (c.moveToNext()) {
//						if(this._singleValueType == SINGLE_VALUE_TYPE_STRING) {
//							String value = c.getString(c
//									.getColumnIndex(columnName));
//							return value;
//						} else if(this._singleValueType == SINGLE_VALUE_TYPE_INT) {
//							Integer value = c.getInt(c
//									.getColumnIndex(columnName));
//							return value;
//						} else if(this._singleValueType == SINGLE_VALUE_TYPE_FLOAT) {
//							Float value = c.getFloat(c
//									.getColumnIndex(columnName));
//							return value;
//						}
//					}
//				}
//			}
//			return c;
//		}
//
//		closeDatabase();
//		return null;
//	}
//
//
//	/**
//	 * Executes a query with sqliteDb.execSQL(...)
//	 * 
//	 * @param sql
//	 */
//	public void exec(String sql) {
//		try {
//			_db.execSQL(sql);
//		} catch (SQLiteException e) {
//			toastlong("DbAction2.java->exec(): " + e.getMessage());
//		}
//	}
//
//
//	protected void toast(String msg) {
//		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
//	}
//
//
//	protected void toastlong(String msg) {
//		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
//	}
//
//	/*
//	 * Inner class: CogDbHelper
//	 * ==============================================================
//	 */
//	private class DbHelper extends SQLiteOpenHelper {
//
//		private Runnable actionOnCreate;
//		private Runnable actionOnUpdate;
//		private Runnable actionOnOpen;
//
//
//		public DbHelper(String dbName) {
//			super(_context, dbName, null, _dbVersion);
//		}
//
//
//		public DbHelper(Context context, String name, CursorFactory factory,
//				int version) {
//			super(context, name, factory, version);
//		}
//
//
//		public void setActionOnCreate(Runnable action) {
//			this.actionOnCreate = action;
//		}
//
//
//		public void setActionOnUpdate(Runnable action) {
//			this.actionOnUpdate = action;
//		}
//
//
//		public void setActionOnOpen(Runnable action) {
//			this.actionOnOpen = action;
//		}
//
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//			if(this.actionOnCreate != null) {
//				this.actionOnCreate.run();
//			}
//		}
//
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			if(this.actionOnUpdate != null) {
//				this.actionOnUpdate.run();
//			}
//		}
//
//
//		@Override
//		public void onOpen(SQLiteDatabase db) {
//			if(this.actionOnOpen != null) {
//				this.actionOnOpen.run();
//			}
//		}
//
//	} //..end CogDbHelper class
//
//}
