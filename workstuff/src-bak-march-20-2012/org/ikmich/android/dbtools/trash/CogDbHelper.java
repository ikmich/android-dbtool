package org.ikmich.android.dbtools.trash;

import org.ikmich.android.dbtools.TableProfile;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CogDbHelper {

	private static Context _context;
	private String _databaseName;
	private static final int DB_VERSION = 1;
	private SQLiteDatabase _db;
	private DbHelper _dbHelper;
	private TableProfile _tableProfileOnCreateDb;


	public CogDbHelper(Context c) {
		_context = c;
	}


	public CogDbHelper(Context c, String name) {
		this(c);
		_databaseName = name;
		if(!databaseExists(_databaseName)) {
			createDatabase(_databaseName);
		}
	}
	
	
	public CogDbHelper(String name, Context c, TableProfile profile) {
		this(c, name);
		_tableProfileOnCreateDb = profile;
	}

	public static boolean databaseExists(String databaseName) {
		String[] dbNames = _context.databaseList();
		for(String dbName : dbNames) {
			if(databaseName.equals(dbName)) {
				return true;
			}
		}
		return false;
	}
	
	public SQLiteDatabase open() {
		return null;
	}
	
	public SQLiteDatabase createDatabase(String dbName) throws SQLiteException {
		_dbHelper = new DbHelper(_context, dbName, null, DB_VERSION);
		_db = _dbHelper.getWritableDatabase();
		return _db;
	}
	
	public void close() {
		_db.close();
	}


	public SQLiteDatabase getDatabase() {
		return _db != null ? _db : null;
	}


	public void createTable(TableProfile profile) {
		try {
			_db.execSQL(profile.getCreateSQL());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Inner class: CogDbHelper
	 * ==============================================================
	 */
	private class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context, String dbName, CursorFactory factory,
				int version) {
			super(context, dbName, factory, version);
		}

		public DbHelper(Context c) {
			super(c, _databaseName, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//create database
			//sqliteDb.execSQL(sql);
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}


		@Override
		public void onOpen(SQLiteDatabase db) {

		}

	} //..end CogDbHelper class

}
