package org.ikmich.android.dbtools;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DeleteQuery extends AbsQuery {
	
	public DeleteQuery(SQLiteDatabase db) {
		super(db);
	}
	
	@Override
	public DeleteQuery context(Context c) {
		_context = c;
		return this;
	}
	
	public Integer run() {
		buildWhereClause();
		int numRows = _sqliteDb.delete(_table, _whereClause, _whereArgs);
		resetState();
		return numRows;
	}
	
	void resetState() {
		_table = "";
		_whereClauses.clear();
		_whereConditions.clear();
		_whereClause = "";
	}
	
}