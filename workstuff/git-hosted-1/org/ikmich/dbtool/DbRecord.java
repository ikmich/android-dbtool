package org.ikmich.dbtool;

import java.util.ArrayList;

import android.content.ContentValues;

/**
 * Represents/mimics a database table row. It's basically wrapper over the ContentValues class, to allow for chained
 * method calls, and other functionality. You can retrieve the corresponding ContentValues object for use, by calling
 * toContentValues() on the instance.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class DbRecord
{
	ContentValues values;
	private ArrayList<String> keyList;
	private ArrayList<Object> valueList;

	public DbRecord()
	{
		values = new ContentValues();
		keyList = new ArrayList<String>();
		valueList = new ArrayList<Object>();
	}

	public DbRecord(ContentValues contentValues)
	{
		values = contentValues;
	}

	public DbRecord set(String key, String value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	/**
	 * Gets the string value identified by the key.
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key)
	{
		return values.getAsString(key);
	}

	public DbRecord set(String key, int value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public int getInt(String key)
	{
		return values.getAsInteger(key);
	}

	public DbRecord set(String key, long value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public long getLong(String key)
	{
		return values.getAsLong(key);
	}

	public DbRecord set(String key, short value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public short getShort(String key)
	{
		return values.getAsShort(key);
	}

	public DbRecord set(String key, double value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public double getDouble(String key)
	{
		return values.getAsDouble(key);
	}

	public DbRecord set(String key, float value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public float getFloat(String key)
	{
		return values.getAsFloat(key);
	}

	public DbRecord set(String key, byte value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public byte getByte(String key)
	{
		return values.getAsByte(key);
	}

	public DbRecord set(String key, boolean value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public boolean getBoolean(String key)
	{
		return values.getAsBoolean(key);
	}

	public DbRecord set(String key, byte[] value)
	{
		values.put(key, value);

		keyList.add(key);
		valueList.add(value);

		return this;
	}

	public byte[] getByteArray(String key)
	{
		return values.getAsByteArray(key);
	}

	/**
	 * Gets the ContentValues component of this DbRecord.
	 * 
	 * @return
	 */
	public ContentValues toContentValues()
	{
		return values;
	}

	/**
	 * Gets a list of the field names contained in this DbRecord.
	 * 
	 * @return
	 */
	public ArrayList<String> getFieldNames()
	{
		return keyList;
	}

	/**
	 * Gets a list of the field values contained in this DbRecord.
	 * 
	 * @return
	 */
	public ArrayList<Object> getFieldValues()
	{
		return valueList;
	}

}
