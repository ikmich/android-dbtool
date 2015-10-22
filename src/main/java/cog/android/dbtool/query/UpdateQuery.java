package cog.android.dbtool.query;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UpdateQuery extends AbsQuery {

	public UpdateQuery(SQLiteDatabase db) {
		super(db);
		_values = new ContentValues();
	}

	void resetState() {
		_table = "";
		_whereClauses.clear();
		_whereConditions.clear();
		_whereClause = "";
	}

	public Integer run() {
		buildWhereClause();
		int numRows = _sqliteDb.update(_table, _values, _whereClause, _whereArgs);
		resetState();

		return numRows;
	}

}