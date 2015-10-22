package org.ikmich.android.dbtools;

import android.content.ContentValues;

/**
 * Represents/mimics a database table row. It's basically 'wrapper' over the ContentValues
 * class, to allow for chained method calls, and other functionality. You can retrieve the
 * corresponding ContentValues object for use, by calling toContentValues() on the instance.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class Record {

	ContentValues _values;

	public Record() {
		_values = new ContentValues();
	}

	public Record(ContentValues contentValues) {
		_values = contentValues;
	}

	public Record set(String key, String value) {
		_values.put(key, value);
		return this;
	}

	/**
	 * Gets the string value identified by the key.
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return _values.getAsString(key);
	}

	public Record set(String key, int value) {
		_values.put(key, value);
		return this;
	}

	public int getInt(String key) {
		return _values.getAsInteger(key);
	}

	public Record set(String key, long value) {
		_values.put(key, value);
		return this;
	}

	public long getLong(String key) {
		return _values.getAsLong(key);
	}

	public Record set(String key, short value) {
		_values.put(key, value);
		return this;
	}

	public short getShort(String key) {
		return _values.getAsShort(key);
	}

	public Record set(String key, double value) {
		_values.put(key, value);
		return this;
	}

	public double getDouble(String key) {
		return _values.getAsDouble(key);
	}

	public Record set(String key, float value) {
		_values.put(key, value);
		return this;
	}

	public float getFloat(String key) {
		return _values.getAsFloat(key);
	}

	public Record set(String key, byte value) {
		_values.put(key, value);
		return this;
	}

	public byte getByte(String key) {
		return _values.getAsByte(key);
	}

	public Record set(String key, boolean value) {
		_values.put(key, value);
		return this;
	}

	public boolean getBoolean(String key) {
		return _values.getAsBoolean(key);
	}

	public Record set(String key, byte[] value) {
		_values.put(key, value);
		return this;
	}

	public byte[] getByteArray(String key) {
		return _values.getAsByteArray(key);
	}

	public ContentValues toContentValues() {
		return _values;
	}

}
