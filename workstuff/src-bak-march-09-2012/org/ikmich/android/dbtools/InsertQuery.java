package org.ikmich.android.dbtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class InsertQuery extends AbsQuery {

	private String _nullColumnHack = null;
	private RecordSet _rows;
	private Record _row;
	private boolean _insertingMultipleRows = false;

	public InsertQuery(SQLiteDatabase db) {
		super(db);
		_rows = new RecordSet();
		_row = new Record();
		_values = new ContentValues();
	}

	@Override
	public InsertQuery context(Context c) {
		_context = c;
		return this;
	}

	public InsertQuery nullColumnHack(String nullColumnHack) {
		_nullColumnHack = nullColumnHack;
		return this;
	}

	public InsertQuery recordSet(RecordSet rows) {
		_insertingMultipleRows = true;
		_rows = rows;
		return this;
	}

	public InsertQuery rows(RecordSet rows) {
		return this.recordSet(rows);
	}

	public InsertQuery record(Record row) {
		_row = row;
		_values = _row.toContentValues();
		return this;
	}

	public InsertQuery row(Record row) {
		return this.record(row);
	}

	public Long run() {
		long rowId = 0;
		if(_insertingMultipleRows) {
			for(Record row : _rows) {
				_values = row.toContentValues();
				rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
			}
		}
		else {
			rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
		}
		resetState();
		return rowId;
	}

	//	public Long run(boolean preserveState) {
	//		long rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
	//		if(!preserveState) {
	//			resetState();
	//		}
	//		return rowId;
	//	}

	void resetState() {
		_table = "";
		_values.clear();
	}

}