package org.ikmich.android.dbtools.trash;

import android.database.sqlite.SQLiteDatabase;

public abstract class CogModifyingQuery extends CogConditionalQuery implements ICogModifyingQuery {
	
	public CogModifyingQuery(SQLiteDatabase db) {
		super(db);
	}
	
	
	
}
