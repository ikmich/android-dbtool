package org.ikmich.android.dbtools.lab;

import android.database.Cursor;

/**
 * Utility class with convenience methods for working with a Cursor.
 * 
 * @author ikmich
 */
public class CursorUtil {

	private Cursor _cursor;


	public CursorUtil(Cursor cursor) {
		_cursor = cursor;
	}


	private int fn_getColumnIndex(String key) {
		return _cursor.getColumnIndex(key);
	}


	public String getStringAt(String key) {
		String value = _cursor.getString(fn_getColumnIndex(key));
		return value;
	}


	public int getIntAt(String key) {
		int index = fn_getColumnIndex(key);
		int value = _cursor.getInt(index);
		return value;
	}


	public long getLongAt(String key) {
		int index = fn_getColumnIndex(key);
		long value = _cursor.getLong(index);
		return value;
	}


	public short getShortAt(String key) {
		short value = _cursor.getShort(fn_getColumnIndex(key));
		return value;
	}


	public double getDoubleAt(String key) {
		double value = _cursor.getDouble(fn_getColumnIndex(key));
		return value;
	}


	public float getFloatAt(String key) {
		float value = _cursor.getFloat(fn_getColumnIndex(key));
		return value;
	}


	public byte[] getBlobAt(String key) {
		byte[] blob = _cursor.getBlob(fn_getColumnIndex(key));
		return blob;
	}

}
