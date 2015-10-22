package org.ikmich.android.dbtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Ikenna Agbasimalo
 */
class DbtoolActionUnit {

	private SQLiteDatabase _db;
	private static Context _context;

	private String[] _columns = null;
	private String _table = null;
	private String _whereClause = "";
	private String _action = "";

	private static final String ACTION_INSERT = "insert";
	private static final String ACTION_DELETE = "delete";
	private static final String ACTION_SELECT = "select";
	private static final String ACTION_UPDATE = "update";

	private SelectQuery _selectQuery = null;
	private UpdateQuery _updateQuery = null;
	private DeleteQuery _deleteQuery = null;
	private InsertQuery _insertQuery = null;

	private boolean _gettingSingleValue = false;
	private boolean _gettingCursor = false;
	private String _singleValueType = ""; // string | integer | float
	static final String SINGLE_VALUE_TYPE_STRING = "string";
	static final String SINGLE_VALUE_TYPE_INT = "integer";
	static final String SINGLE_VALUE_TYPE_FLOAT = "float";

	private boolean _callingGetAllMethod = false;
	private boolean _callingGetMethod = false;

	// private boolean _returningCursor = true;

	DbtoolActionUnit() {
		init();
	}

	DbtoolActionUnit(Context c) {
		_context = c;
		init();
	}

	/**
	 * initializations.
	 */
	private void init() {
		_table = "";
		_action = "";
		_context = Dbtool._context;
		_db = Dbtool._db;
	}

	DbtoolActionUnit context(Context c) {
		_context = c;
		return this;
	}

	String getAction() {
		return _action;
	}

	private void initSelectQuery() {
		if(_selectQuery == null) {
			_selectQuery = new SelectQuery(_db).context(_context);
		}
	}

	private void initInsertQuery() {
		if(_insertQuery == null) {
			_insertQuery = new InsertQuery(_db);
		}
	}

	private void initUpdateQuery() {
		if(_updateQuery == null) {
			_updateQuery = new UpdateQuery(_db);
		}
	}

	private void initDeleteQuery() {
		if(_deleteQuery == null) {
			_deleteQuery = new DeleteQuery(_db);
		}
	}

	private void setSelectAction() {
		_action = ACTION_SELECT;
	}

	private void setInsertAction() {
		_action = ACTION_INSERT;
	}

	private void setUpdateAction() {
		_action = ACTION_UPDATE;
	}

	private void setDeleteAction() {
		_action = ACTION_DELETE;
	}

	private boolean isSelectAction() {
		return _action == ACTION_SELECT ? true : false;
	}

	private boolean isInsertAction() {
		return _action == ACTION_INSERT ? true : false;
	}

	private boolean isUpdateAction() {
		return _action == ACTION_UPDATE ? true : false;
	}

	private boolean isDeleteAction() {
		return _action == ACTION_DELETE ? true : false;
	}

	// DbToolActionUnit setColumns(String[] columns) {
	// _columns = columns;
	// return this;
	// }

	// ActionUnit table(String table) {
	// _table = table;
	//
	// if(_selectQuery != null) {
	// _selectQuery.table(_table);
	// }
	// if(_insertQuery != null) {
	// _insertQuery.table(_table);
	// }
	// if(_updateQuery != null) {
	// _updateQuery.table(_table);
	// }
	// if(_deleteQuery != null) {
	// _deleteQuery.table(_table);
	// }
	//
	// return this;
	// }

	// ActionUnit table(Object... tables) {
	// for(int i = 0; i < tables.length; i++) {
	// _table += (String) tables[i];
	// if(i < (tables.length - 1)) {
	// _table += ", ";
	// }
	// }
	//
	// if(_selectQuery != null) {
	// _selectQuery.table(_table);
	// }
	// if(_insertQuery != null) {
	// _insertQuery.table(_table);
	// }
	// if(_updateQuery != null) {
	// _updateQuery.table(_table);
	// }
	// if(_deleteQuery != null) {
	// _deleteQuery.table(_table);
	// }
	//
	// return this;
	// }

	private void gettingCursor() {
		this._gettingCursor = true;
	}

	private void notGettingCursor() {
		this._gettingCursor = false;
	}

	private void prepForSelect() {
		setSelectAction();
		initSelectQuery();
	}

	private void setCallingGetMethod() {
		_callingGetMethod = true;
		_callingGetAllMethod = false;
	}

	private void setCallingGetAllMethod() {
		_callingGetAllMethod = true;
		_callingGetMethod = false;
	}

	DbtoolActionUnit getAll() {
		setCallingGetAllMethod();
		notGettingCursor();
		prepForSelect();
		_columns = null;
		return this;
	}

	DbtoolActionUnit selectAll() {
		gettingCursor();
		prepForSelect();
		_columns = null;
		return this;
	}

	DbtoolActionUnit get(String[] columns) {
		setCallingGetMethod();
		notGettingCursor();

		this._gettingSingleValue = false;
		prepForSelect();

		_columns = columns;
		_selectQuery.columns(_columns);
		return this;
	}

	DbtoolActionUnit select(String[] columns) {
		gettingCursor();
		prepForSelect();
		_columns = columns;
		_selectQuery.columns(_columns);
		return this;
	}

	DbtoolActionUnit get(String columns) {
		setCallingGetMethod();
		notGettingCursor();
		this._gettingSingleValue = false;
		prepForSelect();
		_columns = columns.split(",\\s*");
		_selectQuery.columns(_columns);
		return this;
	}

	DbtoolActionUnit select(String columns) {
		gettingCursor();
		prepForSelect();
		_columns = columns.split(",\\s*");
		_selectQuery.columns(_columns);
		return this;
	}

	DbtoolActionUnit get(Object... columns) {
		setCallingGetMethod();
		notGettingCursor();
		this._gettingSingleValue = false;
		prepForSelect();

		_columns = new String[columns.length];
		for(int i = 0; i < columns.length; i++) {
			_columns[i] = (String) columns[i];
		}

		_selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit select(Object... columns) {
		gettingCursor();
		prepForSelect();
		return this;
	}

	DbtoolActionUnit getString(String column) {
		notGettingCursor();
		this._gettingSingleValue = true;
		this._singleValueType = SINGLE_VALUE_TYPE_STRING;

		prepForSelect();
		_columns = new String[] { column };

		_selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit getInt(String column) {
		notGettingCursor();
		_gettingSingleValue = true;
		_singleValueType = SINGLE_VALUE_TYPE_INT;

		prepForSelect();

		_columns = new String[] { column };
		_selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit getFloat(String column) {
		notGettingCursor();
		this._gettingSingleValue = true;
		this._singleValueType = SINGLE_VALUE_TYPE_FLOAT;

		prepForSelect();
		_columns = new String[] { column };
		_selectQuery.columns(_columns);

		return this;
	}

	DbtoolActionUnit from(String table) {
		_table = table;

		initSelectQuery();
		initDeleteQuery();

		_selectQuery.table(_table);
		_deleteQuery.table(_table);

		return this;
	}

	DbtoolActionUnit from(Object... tables) {
		for(int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if(i < (tables.length - 1)) {
				_table += ", ";
			}
		}

		initSelectQuery();
		initDeleteQuery();

		_selectQuery.table(_table);
		_deleteQuery.table(_table);

		return this;
	}

	DbtoolActionUnit update(String table) {
		setUpdateAction();
		initUpdateQuery();
		_table = table;
		_updateQuery.table(_table);

		return this;
	}

	DbtoolActionUnit update(Object... tables) {
		for(int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if(i < (tables.length - 1)) {
				_table += ", ";
			}
		}
		setUpdateAction();
		initUpdateQuery();
		_updateQuery.table(_table);
		return this;
	}

	DbtoolActionUnit insert(ContentValues values) {
		setInsertAction();
		initInsertQuery();

		_insertQuery.values(values);
		return this;
	}

	DbtoolActionUnit into(String table) {
		setInsertAction();
		initInsertQuery();

		_table = table;
		_insertQuery.table(_table);
		return this;
	}

	DbtoolActionUnit insertInto(String table) {
		setInsertAction();
		initInsertQuery();

		_table = table;
		_insertQuery.table(_table);
		return this;
	}

	DbtoolActionUnit values(ContentValues values) {
		initUpdateQuery();
		initInsertQuery();

		_updateQuery.values(values);
		_insertQuery.values(values);

		return this;
	}

	DbtoolActionUnit record(Record row) {
		setInsertAction();
		initInsertQuery();
		_updateQuery.values(row.toContentValues());
		_insertQuery.values(row.toContentValues());
		return this;
	}

	DbtoolActionUnit recordSet(RecordSet rows) {
		setInsertAction();
		initInsertQuery();
		_insertQuery.recordSet(rows);
		return this;
	}

	DbtoolActionUnit set(String name, Object value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, Byte value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, int value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, float value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, short value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, byte[] value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, String value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);

		return this;
	}

	DbtoolActionUnit set(String name, double value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, long value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	DbtoolActionUnit set(String name, boolean value) {
		initUpdateQuery();
		initInsertQuery();
		_updateQuery.set(name, value);
		_insertQuery.set(name, value);
		return this;
	}

	private DbtoolActionUnit delete() {
		setDeleteAction();
		initDeleteQuery();
		return this;
	}

	DbtoolActionUnit deleteFrom(String table) {
		delete();
		_table = table;
		_deleteQuery.table(_table);

		return this;
	}

	DbtoolActionUnit deleteFrom(Object... tables) {
		delete();
		for(int i = 0; i < tables.length; i++) {
			_table += (String) tables[i];
			if(i < (tables.length - 1)) {
				_table += ", ";
			}
		}
		_deleteQuery.table(_table);

		return this;
	}

	DbtoolActionUnit where(String whereClause) {
		initSelectQuery();
		initUpdateQuery();
		initDeleteQuery();

		_whereClause = whereClause;

		if(isSelectAction()) {
			_selectQuery.where(_whereClause);
		}
		else if(isUpdateAction()) {
			_updateQuery.where(_whereClause);
		}
		else if(isDeleteAction()) {
			_deleteQuery.where(_whereClause);
		}

		return this;
	}

	DbtoolActionUnit and() {
		if(isSelectAction()) {
			_selectQuery.and();
		}
		else if(isUpdateAction()) {
			_updateQuery.and();
		}
		else if(isDeleteAction()) {
			_deleteQuery.and();
		}

		return this;
	}

	DbtoolActionUnit or() {
		if(isSelectAction()) {
			_selectQuery.or();
		}
		else if(isUpdateAction()) {
			_updateQuery.or();
		}
		else if(isDeleteAction()) {
			_deleteQuery.or();
		}

		return this;
	}

	DbtoolActionUnit whereLike(String field, Object value) {
		if(isSelectAction()) {
			_selectQuery.whereLike(field, value);
		}
		else if(isUpdateAction()) {
			_updateQuery.whereLike(field, value);
		}
		else if(isDeleteAction()) {
			_deleteQuery.whereLike(field, value);
		}

		return this;
	}

	DbtoolActionUnit whereEquals(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + "='" + value + "'";
		}
		else {
			whereClause = field + "=" + value;
		}
		_whereClause = whereClause;
		where(_whereClause);
		return this;
	}

	DbtoolActionUnit whereLessThan(String field, Object value) {
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

	DbtoolActionUnit whereGreaterThan(String field, Object value) {
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

	DbtoolActionUnit whereGreaterThanOrEquals(String field, Object value) {
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

	DbtoolActionUnit whereLessThanOrEquals(String field, Object value) {
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

	DbtoolActionUnit whereArgs(String[] whereArgs) {
		initSelectQuery();
		initUpdateQuery();
		initDeleteQuery();

		_selectQuery.whereArgs(whereArgs);
		_updateQuery.whereArgs(whereArgs);
		_deleteQuery.whereArgs(whereArgs);

		return this;
	}

	DbtoolActionUnit distinct() {
		initSelectQuery();
		_selectQuery.distinct();
		return this;
	}

	DbtoolActionUnit columns(String[] columns) {
		initSelectQuery();
		_selectQuery.columns(columns);
		return this;
	}

	DbtoolActionUnit columns(String columns) {
		initSelectQuery();
		_selectQuery.columns(columns);
		return this;
	}

	DbtoolActionUnit columns(Object... columns) {
		initSelectQuery();
		_selectQuery.columns(columns);
		return this;
	}

	DbtoolActionUnit groupBy(String groupBy) {
		initSelectQuery();
		_selectQuery.groupBy(groupBy);
		return this;
	}

	DbtoolActionUnit having(String having) {
		initSelectQuery();
		_selectQuery.having(having);
		return this;
	}

	DbtoolActionUnit orderBy(String orderBy) {
		initSelectQuery();
		_selectQuery.orderBy(orderBy);
		return this;
	}

	DbtoolActionUnit limit(String limit) {
		initSelectQuery();
		_selectQuery.limit(limit);
		return this;
	}

	Object run() {
		if(_action == ACTION_UPDATE) {
			Integer numRows = _updateQuery.run();
			return numRows;
		}
		if(_action == ACTION_INSERT) {
			Long rowId = _insertQuery.run();
			return rowId;
		}
		if(_action == ACTION_DELETE) {
			Integer numRows = _deleteQuery.run();
			return numRows;
		}
		if(_action == ACTION_SELECT) {
			Cursor c = _selectQuery.run();
			if(this._gettingCursor) {
				return c;
			}
			else {
				/*
				 * if getting one value, return only that value, instead of a Cursor
				 */
				if(this._gettingSingleValue) {
					if(c.getColumnCount() == 1) { //TODO: ensure _columns is not null
						String columnName = _columns[0];
						while (c.moveToNext()) {
							if(this._singleValueType == SINGLE_VALUE_TYPE_STRING) {
								String value = c.getString(c.getColumnIndex(columnName));
								c.close();
								return value;
							}
							else if(this._singleValueType == SINGLE_VALUE_TYPE_INT) {
								Integer value = c.getInt(c.getColumnIndex(columnName));
								c.close();
								return value;
							}
							else if(this._singleValueType == SINGLE_VALUE_TYPE_FLOAT) {
								Float value = c.getFloat(c.getColumnIndex(columnName));
								c.close();
								return value;
							}
						}
					}
				}
				else {
					/*
					 * If returning one column from the query, return a Field object.
					 */
					if(c.getColumnCount() == 1) {
						/*
						 * Return a Field object.
						 */
						Field field = new Field(c.getCount());
						if(c.moveToFirst()) {
							do {
								field.add(c.getString(0));
							}
							while (c.moveToNext());
						}
						c.close();
						return field;
					}
					/*
					 * Return a RecordSet object if: - returning more than one row; or - returning
					 * only one row but the getAll() method of Dbtool was called;
					 */
					else if(c.getCount() > 1 || (c.getCount() == 1 && _callingGetAllMethod)) { //TODO: ensure _columns is not null //|| c.getColumnCount() == 1
						RecordSet recordSet = new RecordSet();
						Record record;
						if(c.moveToFirst()) {
							do {
								record = new Record();
								for(int i = 0; i < c.getColumnCount(); i++) {
									String key = c.getColumnName(i);
									String value = c.getString(i);
									record.set(key, value);
								}
								recordSet.add(record);
							}
							while (c.moveToNext());
							c.close();
							return recordSet;
						}
					}
					/*
					 * Return a Record.
					 */
					else {
						Record record = new Record();
						if(c.moveToFirst()) {
							for(int i = 0; i < c.getColumnCount(); i++) {
								String key = c.getColumnName(i);
								String value = c.getString(i);
								record.set(key, value);
							}
							c.close();
							return record;
						}
					}

					return null;
				}
			}
		}
		return null;
	}

	protected void toast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
	}

	protected void toastlong(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}

}
