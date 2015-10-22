package org.ikmich.dbtool.query;

import org.ikmich.dbtool.DbRecord;
import org.ikmich.dbtool.DbRecordSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class InsertQuery extends AbsQuery
{

	private String _nullColumnHack = null;
	private DbRecordSet _rows;
	private DbRecord _row;
	private boolean _insertingMultipleRows = false;

	public InsertQuery(SQLiteDatabase db)
	{
		this(null, db);
	}

	public InsertQuery(Context c, SQLiteDatabase db)
	{
		super(c, db);

		_rows = new DbRecordSet();
		_row = new DbRecord();
		_values = new ContentValues();
	}

	@Override
	public InsertQuery context(Context c)
	{
		_context = c;
		return this;
	}

	public InsertQuery nullColumnHack(String nullColumnHack)
	{
		_nullColumnHack = nullColumnHack;
		return this;
	}

	public InsertQuery recordSet(DbRecordSet rows)
	{
		_insertingMultipleRows = true;
		_rows = rows;
		return this;
	}

	public InsertQuery rows(DbRecordSet rows)
	{
		return this.recordSet(rows);
	}

	public InsertQuery record(DbRecord row)
	{
		_row = row;
		_values = _row.toContentValues();
		return this;
	}

	public InsertQuery row(DbRecord row)
	{
		return this.record(row);
	}

	public Long run()
	{
		long rowId = 0;
		if (_insertingMultipleRows)
		{
			for (DbRecord row : _rows)
			{
				_values = row.toContentValues();
				//rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
			}
		}
		//		else
		//		{
		//			rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);
		//		}

		rowId = _sqliteDb.insert(_table, _nullColumnHack, _values);

		resetState();
		return rowId;
	}

	void resetState()
	{
		_table = "";
		_values.clear();
	}

}