package cog.android.dbtool.query;

import android.database.sqlite.SQLiteDatabase;

public class DeleteQuery extends AbsQuery {

	public DeleteQuery(SQLiteDatabase db) {
		super(db);
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