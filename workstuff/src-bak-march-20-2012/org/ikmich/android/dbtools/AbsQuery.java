package org.ikmich.android.dbtools;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public abstract class AbsQuery implements IQuery {

	protected SQLiteDatabase _sqliteDb;
	protected Context _context;
	protected String _table = null;
	protected String _whereClause = null;
	protected ArrayList<String> _whereClauses;
	protected ArrayList<String> _whereConditions;
	protected String[] _whereArgs = null;
	protected ContentValues _values = null;

	protected boolean _distinct = false;
	protected String[] _columns = null;
	protected String _groupBy = null;
	protected String _having = null;
	protected String _orderBy = null;
	protected String _limit = null;

	public AbsQuery() {
		init();
	}

	public AbsQuery(SQLiteDatabase db) {
		this();
		_sqliteDb = db;
	}

	protected void init() {
		_table = "";
		_values = new ContentValues();
		_whereClauses = new ArrayList<String>();
		_whereConditions = new ArrayList<String>();
	}

	public IQuery context(Context c) {
		_context = c;
		return this;
	}

	public IQuery from(String table) {
		_table = table;
		return this;
	}

	public IQuery from(Object... tables) {
		for(int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if(i < (tables.length - 1)) {
				_table += ", ";
			}
		}
		return this;
	}

	public IQuery table(String table) {
		_table = table;
		return this;
	}

	public IQuery table(Object... tables) {
		for(int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if(i < (tables.length - 1)) {
				_table += ", ";
			}
		}
		return this;
	}

	/**
	 * Indicates to perform a select all query.
	 */
	public IQuery getAll() {
		_columns = null;
		return this;
	}

	public IQuery selectAll() {
		return getAll();
	}

	/**
	 * Indicates the columns to perform a SELECT query on.
	 */
	public IQuery get(Object... columns) {
		_columns = new String[columns.length];
		for(int i = 0; i < columns.length; i++) {
			_columns[i] = (String) columns[i];
		}
		return this;
	}

	public IQuery select(Object... columns) {
		return this;
	}

	/**
	 * Indicates the columns to perform a SELECT query on. Returns a
	 * org.ikmich.android.db.RecordSet, as opposed to the select() method which returns a Cursor
	 */
	public IQuery get(String[] columns) {
		_columns = columns;
		return this;
	}

	/**
	 * Indicates the columns to perform a SELECT query on. Returns a Cursor.
	 */
	public IQuery select(String[] columns) {

		return this;
	}

	/**
	 * Indicates the columns to perform a SELECT query on.
	 */
	public IQuery get(String columns) {
		_columns = columns.split(",\\s*");
		return this;
	}

	public IQuery select(String columns) {

		return this;
	}

	public IQuery set(String name, Object value) {
		if(value instanceof String) {
			_values.put(name, (String) value);
		}
		else if(value instanceof Integer) {
			_values.put(name, (Integer) value);
		}
		else if(value instanceof Long) {
			_values.put(name, (Long) value);
		}
		else if(value instanceof Short) {
			_values.put(name, (Short) value);
		}
		else if(value instanceof Double) {
			_values.put(name, (Double) value);
		}
		else if(value instanceof Float) {
			_values.put(name, (Float) value);
		}
		else if(value instanceof Byte) {
			_values.put(name, (Byte) value);
		}
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, Byte value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, int value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, float value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, short value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, byte[] value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, String value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, double value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, long value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery set(String name, boolean value) {
		_values.put(name, value);
		return this;
	}

	/**
	 * Sets column values for an INSERT or UPDATE query.
	 */
	public IQuery values(ContentValues values) {
		_values = values;
		return this;
	}

	public IQuery where(String whereClause) {
		if(!whereClause.equals("")) {
			_whereClauses.add(whereClause);
		}
		return this;
	}

	public IQuery and() {
		_whereConditions.add("AND");
		return this;
	}

	public IQuery or() {
		_whereConditions.add("OR");
		return this;
	}

	/**
	 * Experimental
	 * 
	 * @param field
	 *            The database field to compare.
	 * @param value
	 *            The 'like' comparison value.
	 * @return CogUpdateQuery
	 */
	public IQuery whereLike(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			//since value is a string, add the single quotes to the query string..
			whereClause = field + " LIKE '" + value + "'";
		}
		else {
			whereClause = field + " LIKE " + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereEquals(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + " = '" + value + "' ";
		}
		else {
			whereClause = field + "=" + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereLessThan(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + "<'" + value + "'";
		}
		else {
			whereClause = field + "<" + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereGreaterThan(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + ">'" + value + "'";
		}
		else {
			whereClause = field + ">" + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereGreaterThanOrEquals(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + ">='" + value + "'";
		}
		else {
			whereClause = field + ">=" + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereLessThanOrEquals(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + "<='" + value + "'";
		}
		else {
			whereClause = field + "<=" + value;
		}
		where(whereClause);
		return this;
	}

	public IQuery whereArgs(String[] whereArgs) {
		_whereArgs = whereArgs;
		return this;
	}

	protected void buildWhereClause() {
		_whereClause = "";
		int index = 0;
		for(String whereClause : _whereClauses) {
			_whereClause += whereClause;
			if(_whereClauses.indexOf(whereClause) < (_whereClauses.size() - 1)) {
				_whereClause += " " + _whereConditions.get(index) + " ";
			}
			index++;
		}
		// remove trailing conditions...
		_whereClause = _whereClause
				.replaceFirst("(\\s+)((and|AND)|(or|OR)|(like|LIKE))(\\s+)$", "");
	}

	/**
	 * Indicates for a SELECT query to use the DISTINCT keyword to return unique rows.
	 */
	public IQuery distinct() {
		_distinct = true;
		return this;
	}

	/**
	 * Sets the columns array for the andoid sqlite query method.
	 */
	public IQuery columns(String[] columns) {
		_columns = columns;
		return this;
	}

	/**
	 * Sets the columns array for the andoid sqlite query method.
	 */
	public IQuery columns(String columns) {
		_columns = columns.split(",\\s*");
		return this;
	}

	/**
	 * Sets the columns array for the andoid sqlite query method.
	 * 
	 * @param columns
	 */
	public IQuery columns(Object... columns) {
		_columns = new String[columns.length];
		for(int i = 0; i < columns.length; i++) {
			_columns[i] = (String) columns[i];
		}
		return this;
	}

	/**
	 * Sets the GROUP BY clause for a query.
	 */
	public IQuery groupBy(String groupBy) {
		_groupBy = groupBy;
		return this;
	}

	/**
	 * Sets the HAVING clause for a query.
	 * 
	 * @param having
	 * @return
	 */
	public IQuery having(String having) {
		_having = having;
		return this;
	}

	/**
	 * Sets the ORDER BY clause for a query.
	 * 
	 * @param orderBy
	 * @return
	 */
	public IQuery orderBy(String orderBy) {
		_orderBy = orderBy;
		return this;
	}

	/**
	 * Sets the LIMIT clause for a query.
	 * 
	 * @param limit
	 * @return
	 */
	public IQuery limit(String limit) {
		_limit = limit;
		return this;
	}

	/**
	 * Runs the query. Implemented in the subclasses.
	 */
	public abstract Object run();

	/**
	 * Resets the state for a new query chain to be built and run.
	 */
	abstract void resetState();

	protected void toast(String msg) {
		if(_context != null)
			Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}
}
