package org.ikmich.android.dbtools.trash;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class CogConditionalQuery implements ICogConditionalQuery, ICogModifyingQuery {
	
	protected SQLiteDatabase _sqliteDb;
	protected Context _context;
	protected String _table = null;
	protected List<String> _whereClauses;
	protected List<String> _whereConditions;
	
	
	public CogConditionalQuery(SQLiteDatabase db) {
		_sqliteDb = db;
		_table = "";		
		_whereClauses = new ArrayList<String>();
		_whereConditions = new ArrayList<String>();
	}
	
	
	public ICogConditionalQuery context(Context c) {
		_context = c;
		return this;
	}
	
	
	public CogConditionalQuery table(String table) {
		_table = table;
		return this;
	}
	
	public ICogConditionalQuery where(String whereClause) {
		_whereClauses.add(whereClause);
		//_whereClause = whereClause;
		return this;
	}
	
	
	public ICogConditionalQuery and() {
		_whereConditions.add("AND");
		return this;
	}
	
	
	public ICogConditionalQuery or() {
		_whereConditions.add("OR");
		return this;
	}
	
	
	/**
	 * Experimental
	 * @param field The database field to compare.
	 * @param value The 'like' comparison value.
	 * @return CogUpdateQuery
	 */
	public ICogConditionalQuery whereLike(String field, Object value) {
		String whereClause;
		if(value instanceof String) {
			whereClause = field + " like '" + value + "'";
		}
		else {
			whereClause = field + " like " + value;
		}
		where(whereClause);
		return this;
	}
	
	
	public ICogConditionalQuery whereEquals(String field, Object value) {
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
	
	
	public ICogConditionalQuery whereLessThan(String field, Object value) {
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
	
	
	public ICogConditionalQuery whereGreaterThan(String field, Object value) {
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
	
	
	public ICogConditionalQuery whereGreaterThanOrEquals(String field, Object value) {
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
	
	
	public ICogConditionalQuery whereLessThanOrEquals(String field, Object value) {
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
	
	
	protected void toast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}
	
	abstract void resetState();
	public abstract Object run();
	
}
