package cog.android.dbtool.query;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SelectQuery extends AbsQuery {

	public SelectQuery(SQLiteDatabase db) {
		super(db);
	}

	public SelectQuery selectionArgs(String[] selArgs) {
		_whereArgs = selArgs;
		return this;
	}

	public Cursor run() {
		Cursor c;
		buildWhereClause();

		//..determine which overload to use..
		if (_distinct && _limit != null) {
			//..use method with both a 'distinct' and a 'limit' argument
			c = _sqliteDb.query(_distinct, _table, _columns, _whereClause, _whereArgs, _groupBy, _having, _orderBy,
				_limit);
		}
		else if (_limit != null) {
			//..use method with a 'limit' argument, and without a distinct argument
			c = _sqliteDb.query(_table, _columns, _whereClause, _whereArgs, _groupBy, _having, _orderBy, _limit);
		}
		else {
			//..use method without a 'limit' and a 'distinct' argument
			c = _sqliteDb.query(_table, _columns, _whereClause, _whereArgs, _groupBy, _having, _orderBy);
		}

		resetState();
		return c;
	}

	void resetState() {
		_table = "";
		_columns = null;
		_whereClauses.clear();
		_whereConditions.clear();
		_whereClause = "";
	}

}