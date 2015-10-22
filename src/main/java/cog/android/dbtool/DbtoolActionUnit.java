package cog.android.dbtool;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cog.android.dbtool.query.DeleteQuery;
import cog.android.dbtool.query.InsertQuery;
import cog.android.dbtool.query.QueryType;
import cog.android.dbtool.query.SelectQuery;
import cog.android.dbtool.query.UpdateQuery;

/**
 * The actual execution unit for a single query defined in the Dbtool instance.
 * This class is used internally by the Dbtool class.
 * 
 * @author Ikenna Agbasimalo
 */
class DbtoolActionUnit implements IDbtoolAction {
	private SQLiteDatabase _db;

	private String[] _columns = null;
	private String _table = null;
	private String _whereClause = "";
	private String action = "";

	private static final String ACTION_INSERT = "insert";
	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_SELECT = "select";
	private static final String ACTION_UPDATE = "update";

	private SelectQuery selectQuery = null;
	private UpdateQuery updateQuery = null;
	private DeleteQuery deleteQuery = null;
	private InsertQuery insertQuery = null;

	private boolean gettingSingleValue = false;
	private boolean gettingCursor = false;

	private String singleValueType = ""; // string | integer | float
	static final String SINGLE_VALUE_TYPE_STRING = "string";
	static final String SINGLE_VALUE_TYPE_INT = "integer";
	static final String SINGLE_VALUE_TYPE_FLOAT = "float";
	static final String SINGLE_VALUE_TYPE_DOUBLE = "double";

	DbtoolActionUnit(SQLiteDatabase db) {
		_db = db;
		_table = "";
		action = "";
	}

	String getAction() {
		return action;
	}

	private void initQuery(QueryType queryType) {
		switch (queryType) {
			case DELETE:
				if (deleteQuery == null)
					deleteQuery = new DeleteQuery(_db);
				break;
			case SELECT:
				if (selectQuery == null)
					selectQuery = new SelectQuery(_db);
				break;
			case UPDATE:
				if (updateQuery == null)
					updateQuery = new UpdateQuery(_db);
				break;
			case INSERT:
				if (insertQuery == null)
					insertQuery = new InsertQuery(_db);
				break;
		}
	}

	private void setAction(QueryType type) {
		switch (type) {
			case SELECT:
				this.action = ACTION_SELECT;
				break;
			case INSERT:
				this.action = ACTION_INSERT;
				break;
			case UPDATE:
				this.action = ACTION_UPDATE;
				break;
			case DELETE:
				this.action = ACTION_DELETE;
				break;
		}
	}

	private boolean isAction(QueryType type) {
		switch (type) {
			case SELECT:
				return this.action.equals(ACTION_SELECT) ? true : false;
			case INSERT:
				return this.action.equals(ACTION_INSERT) ? true : false;
			case UPDATE:
				return this.action.equals(ACTION_UPDATE) ? true : false;
			case DELETE:
				return this.action.equals(ACTION_DELETE) ? true : false;
			default:
				throw new RuntimeException("No suitable QueryType");
		}
	}

	private void setGettingCursor(boolean b) {
		gettingCursor = b;
	}

	private void setGettingValue(boolean b) {
		gettingSingleValue = b;
	}

	private void prepForSelect() {
		setAction(QueryType.SELECT);
		initQuery(QueryType.SELECT);
	}

	public DbtoolActionUnit getAll() {
		setGettingCursor(false);
		prepForSelect();
		_columns = null;

		return this;
	}

	public DbtoolActionUnit selectAll() {
		setGettingCursor(true);
		prepForSelect();
		_columns = null;

		return this;
	}

	public DbtoolActionUnit get(String[] columns) {
		setGettingCursor(false);

		setGettingValue(false);
		prepForSelect();

		_columns = columns;
		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit select(String[] columns) {
		setGettingCursor(true);
		prepForSelect();

		_columns = columns;
		selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit get(String columns) {
		setGettingCursor(false);

		setGettingValue(false);
		prepForSelect();
		_columns = columns.split(",\\s*");
		selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit select(String columns) {
		setGettingCursor(true);
		prepForSelect();
		_columns = columns.split(",\\s*");
		selectQuery.columns(_columns);
		return this;
	}

	public DbtoolActionUnit get(Object... columns) {
		setGettingCursor(false);
		setGettingValue(false);
		prepForSelect();

		_columns = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			_columns[i] = (String) columns[i];
		}

		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit select(Object... columns) {
		setGettingCursor(true);
		prepForSelect();
		return this;
	}

	public DbtoolActionUnit getString(String column) {
		setGettingCursor(false);
		setGettingValue(true);
		singleValueType = SINGLE_VALUE_TYPE_STRING;

		prepForSelect();
		_columns = new String[] { column };

		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit getInt(String column) {
		setGettingCursor(false);
		setGettingValue(true);
		singleValueType = SINGLE_VALUE_TYPE_INT;

		prepForSelect();

		_columns = new String[] { column };
		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit getFloat(String column) {
		setGettingCursor(false);
		setGettingValue(true);
		singleValueType = SINGLE_VALUE_TYPE_FLOAT;

		prepForSelect();
		_columns = new String[] { column };
		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit getDouble(String column) {
		setGettingCursor(false);
		setGettingValue(true);
		singleValueType = SINGLE_VALUE_TYPE_DOUBLE;

		prepForSelect();
		_columns = new String[] { column };
		selectQuery.columns(_columns);

		return this;
	}

	public DbtoolActionUnit from(String table) {
		_table = table;

		initQuery(QueryType.SELECT);
		initQuery(QueryType.DELETE);

		selectQuery.table(_table);
		deleteQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit from(Object... tables) {
		for (int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if (i < (tables.length - 1)) {
				_table += ", ";
			}
		}

		initQuery(QueryType.SELECT);
		initQuery(QueryType.DELETE);

		selectQuery.table(_table);
		deleteQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit update(String table) {
		setAction(QueryType.UPDATE);
		initQuery(QueryType.UPDATE);

		_table = table;
		updateQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit update(Object... tables) {
		for (int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if (i < (tables.length - 1)) {
				_table += ", ";
			}
		}

		setAction(QueryType.UPDATE);
		initQuery(QueryType.UPDATE);

		updateQuery.table(_table);
		return this;
	}

	public DbtoolActionUnit insert(ContentValues values) {
		setAction(QueryType.INSERT);
		initQuery(QueryType.INSERT);
		insertQuery.values(values);

		return this;
	}

	public DbtoolActionUnit handleInsertConflict(int conflictAlgorithm) {
		setAction(QueryType.INSERT);
		initQuery(QueryType.INSERT);
		insertQuery.setConflictAlgorithm(conflictAlgorithm);
		return this;
	}

	public DbtoolActionUnit into(String table) {
		setAction(QueryType.INSERT);
		initQuery(QueryType.INSERT);

		_table = table;
		insertQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit insertInto(String table) {
		setAction(QueryType.INSERT);
		initQuery(QueryType.INSERT);

		_table = table;
		insertQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit values(ContentValues values) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.values(values);
		insertQuery.values(values);

		return this;
	}

	public DbtoolActionUnit record(DbRecord row) {
		initQuery(QueryType.INSERT);
		initQuery(QueryType.UPDATE);

		updateQuery.values(row.toContentValues());
		insertQuery.values(row.toContentValues());

		return this;
	}

	public DbtoolActionUnit recordSet(DbRecordSet rows) {
		initQuery(QueryType.INSERT);
		insertQuery.recordSet(rows);

		return this;
	}

	public DbtoolActionUnit set(String name, Object value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, Byte value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, int value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, float value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, short value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, byte[] value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, String value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, double value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, long value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	public DbtoolActionUnit set(String name, boolean value) {
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.INSERT);

		updateQuery.set(name, value);
		insertQuery.set(name, value);

		return this;
	}

	private DbtoolActionUnit delete() {
		setAction(QueryType.DELETE);
		initQuery(QueryType.DELETE);

		return this;
	}

	public DbtoolActionUnit deleteFrom(String table) {
		delete();
		_table = table;
		deleteQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit deleteFrom(Object... tables) {
		this.delete();
		for (int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if (i < (tables.length - 1)) {
				_table += ", ";
			}
		}
		deleteQuery.table(_table);

		return this;
	}

	public DbtoolActionUnit where(String whereClause) {
		initQuery(QueryType.SELECT);
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.DELETE);

		_whereClause = whereClause;

		if (isAction(QueryType.SELECT)) {
			selectQuery.where(_whereClause);
		}
		else if (isAction(QueryType.UPDATE)) {
			updateQuery.where(_whereClause);
		}
		else if (isAction(QueryType.DELETE)) {
			deleteQuery.where(_whereClause);
		}

		return this;
	}

	public DbtoolActionUnit and() {
		if (isAction(QueryType.SELECT)) {
			selectQuery.and();
		}
		else if (isAction(QueryType.UPDATE)) {
			updateQuery.and();
		}
		else if (isAction(QueryType.DELETE)) {
			deleteQuery.and();
		}

		return this;
	}

	public DbtoolActionUnit or() {
		if (isAction(QueryType.SELECT)) {
			selectQuery.or();
		}
		else if (isAction(QueryType.UPDATE)) {
			updateQuery.or();
		}
		else if (isAction(QueryType.DELETE)) {
			deleteQuery.or();
		}

		return this;
	}

	public DbtoolActionUnit whereLike(String field, Object value) {
		if (isAction(QueryType.SELECT)) {
			selectQuery.whereLike(field, value);
		}
		else if (isAction(QueryType.UPDATE)) {
			updateQuery.whereLike(field, value);
		}
		else if (isAction(QueryType.DELETE)) {
			deleteQuery.whereLike(field, value);
		}

		return this;
	}

	public DbtoolActionUnit whereEquals(String field, Object value) {
		//String whereClause;

		if (value == null) {
			_whereClause = field + " is null";
		}
		else if (value instanceof String) {
			_whereClause = field + "='" + value + "'";
		}
		else {
			_whereClause = field + "=" + value;
		}

		//this._whereClause = whereClause;
		this.where(_whereClause);

		return this;
	}

	public DbtoolActionUnit whereNotEquals(String field, Object value) {
		if (value == null) {
			_whereClause = field + " is not null";
		}
		else if (value instanceof String) {
			_whereClause = field + " != '" + value + "';";
		}
		else {
			_whereClause = field + " != " + value;
		}

		this.where(_whereClause);

		return this;
	}

	public DbtoolActionUnit whereLessThan(String field, Object value) {
		String whereClause;
		if (value instanceof String) {
			whereClause = field + "<'" + value + "'";
		}
		else {
			whereClause = field + "<" + value;
		}
		this.where(whereClause);
		return this;
	}

	public DbtoolActionUnit whereGreaterThan(String field, Object value) {
		String whereClause;
		if (value instanceof String) {
			whereClause = field + ">'" + value + "'";
		}
		else {
			whereClause = field + ">" + value;
		}
		this.where(whereClause);
		return this;
	}

	public DbtoolActionUnit whereGreaterThanOrEquals(String field, Object value) {
		String whereClause;
		if (value instanceof String) {
			whereClause = field + ">='" + value + "'";
		}
		else {
			whereClause = field + ">=" + value;
		}
		this.where(whereClause);
		return this;
	}

	public DbtoolActionUnit whereLessThanOrEquals(String field, Object value) {
		String whereClause;
		if (value instanceof String) {
			whereClause = field + ">='" + value + "'";
		}
		else {
			whereClause = field + ">=" + value;
		}
		this.where(whereClause);
		return this;
	}

	public DbtoolActionUnit whereArgs(String[] whereArgs) {
		initQuery(QueryType.SELECT);
		initQuery(QueryType.UPDATE);
		initQuery(QueryType.DELETE);

		selectQuery.whereArgs(whereArgs);
		updateQuery.whereArgs(whereArgs);
		deleteQuery.whereArgs(whereArgs);

		return this;
	}

	public DbtoolActionUnit distinct() {
		initQuery(QueryType.SELECT);
		selectQuery.distinct();

		return this;
	}

	public DbtoolActionUnit columns(String[] columns) {
		initQuery(QueryType.SELECT);
		selectQuery.columns(columns);

		return this;
	}

	public DbtoolActionUnit columns(String columns) {
		initQuery(QueryType.SELECT);
		selectQuery.columns(columns);

		return this;
	}

	public DbtoolActionUnit columns(Object... columns) {
		initQuery(QueryType.SELECT);
		selectQuery.columns(columns);

		return this;
	}

	public DbtoolActionUnit groupBy(String groupBy) {
		initQuery(QueryType.SELECT);
		selectQuery.groupBy(groupBy);

		return this;
	}

	public DbtoolActionUnit having(String having) {
		initQuery(QueryType.SELECT);
		selectQuery.having(having);

		return this;
	}

	public DbtoolActionUnit orderBy(String orderBy) {
		initQuery(QueryType.SELECT);
		selectQuery.orderBy(orderBy);

		return this;
	}

	public DbtoolActionUnit limit(String limit) {
		initQuery(QueryType.SELECT);
		selectQuery.limit(limit);

		return this;
	}

	Object run() {
		if (this.action == ACTION_UPDATE) {
			Integer numRows = updateQuery.run();
			return numRows;
		}

		if (this.action == ACTION_INSERT) {
			Long rowId = insertQuery.run();
			return rowId;
		}

		if (this.action == ACTION_DELETE) {
			Integer numRows = deleteQuery.run();
			return numRows;
		}

		if (this.action == ACTION_SELECT) {
			Cursor c = null;
			c = selectQuery.run();
			if (this.gettingCursor) {
				return c;
			}
			else {
				/*
				 * if getting one value, return only that value, instead of a Cursor
				 */
				if (gettingSingleValue) {
					if (c.getColumnCount() == 1) {
						String columnName = _columns[0];
						while (c.moveToNext()) {
							if (singleValueType == SINGLE_VALUE_TYPE_STRING) {
								String val = c.getString(c.getColumnIndex(columnName));
								c.close();
								return val;
							}
							else if (singleValueType == SINGLE_VALUE_TYPE_INT) {
								Integer val = c.getInt(c.getColumnIndex(columnName));
								c.close();
								return val;
							}
							else if (singleValueType == SINGLE_VALUE_TYPE_FLOAT) {
								Float val = c.getFloat(c.getColumnIndex(columnName));
								c.close();
								return val;
							}
							else if (singleValueType == SINGLE_VALUE_TYPE_DOUBLE) {
								Double val = c.getDouble(c.getColumnIndex(columnName));
								c.close();
								return val;
							}
						}
					}
				}
				else {
					/*
					 * Return a recordset.
					 */
					DbRecordSet recSet = new DbRecordSet();
					DbRecord rec;
					if (c.moveToFirst()) {
						do {
							rec = new DbRecord();
							for (int i = 0; i < c.getColumnCount(); i++) {
								String key = c.getColumnName(i);
								String value = c.getString(i);
								rec.set(key, value);
							}
							recSet.add(rec);
						} while (c.moveToNext());
						c.close();
						return recSet;
					}

					return null;
				}
			}
		}
		return null;
	}
}
