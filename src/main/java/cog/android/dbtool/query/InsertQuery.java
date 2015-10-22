package cog.android.dbtool.query;

import android.database.sqlite.SQLiteDatabase;
import cog.android.dbtool.DbRecord;
import cog.android.dbtool.DbRecordSet;

public class InsertQuery extends AbsQuery {

	private String _nullColumnHack = null;
	private DbRecordSet _rows;
	private DbRecord _row;
	private boolean _insertingMultipleRows = false;
	private int _conflictAlgorithm = -1;

	public InsertQuery(SQLiteDatabase db) {
		super(db);

		_rows = new DbRecordSet();
		_row = new DbRecord();
	}

	public InsertQuery nullColumnHack(String nullColumnHack) {
		_nullColumnHack = nullColumnHack;
		return this;
	}

	public InsertQuery recordSet(DbRecordSet rows) {
		_insertingMultipleRows = true;
		_rows = rows;
		return this;
	}

	public InsertQuery rows(DbRecordSet rows) {
		return this.recordSet(rows);
	}

	public InsertQuery record(DbRecord row) {
		_row = row;
		_values = _row.toContentValues();
		return this;
	}

	public InsertQuery row(DbRecord row) {
		return this.record(row);
	}

	public int getConflictAlgorithm() {
		return _conflictAlgorithm;
	}

	public void setConflictAlgorithm(int _conflictAlgorithm) {
		this._conflictAlgorithm = _conflictAlgorithm;
	}

	public Long run() {
		long rowId = 0;
		if (_insertingMultipleRows) {
			for (DbRecord row : _rows) {
				_values = row.toContentValues();
			}
		}

		if (_conflictAlgorithm == -1) {
			rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
		}
		else {
			rowId = _sqliteDb.insertWithOnConflict(_table, _nullColumnHack, _values, _conflictAlgorithm);
		}

		resetState();
		return rowId;
	}

	void resetState() {
		_table = "";
		_values.clear();
	}

}