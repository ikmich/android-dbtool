package org.ikmich.android.dbtools;

import android.content.ContentValues;
import android.content.Context;

public interface IQuery {

	/**
	 * Sets the context to work with.
	 * 
	 * @param c
	 *            <span>The current context.</span>
	 * @return The IQuery object.
	 */
	public IQuery context(Context c);


	public IQuery from(String table);


	public IQuery from(Object... tables);


	public IQuery getAll();
	
	public IQuery selectAll();


	public IQuery get(Object... columns);
	
	public IQuery select(Object... columns);


	public IQuery get(String[] columns);
	
	public IQuery select(String[] columns);


	public IQuery get(String columns);
	
	public IQuery select(String columns);


	/**
	 * Sets the table to perform a db query on.
	 * 
	 * @param table
	 *            <span>The table name.</span>
	 * @return The IQuery object.
	 */
	public IQuery table(String table);


	/**
	 * Sets the table(s) to perform a db query on.
	 * 
	 * @param tables
	 *            <span>The tables to work with</span>
	 * @return The IQuery object.
	 */
	public IQuery table(Object... tables);


	/**
	 * Sets a Byte value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param Byte
	 *            value The Byte value to set.
	 * @return The IQuery object.
	 */
	public IQuery set(String name, Byte value);


	/**
	 * Sets an int value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The int value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, int value);


	/**
	 * Sets a float value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The float value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, float value);


	/**
	 * Sets a short value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The short value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, short value);


	/**
	 * Sets a blob field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The byte[] value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, byte[] value);


	/**
	 * Sets a String value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The String value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, String value);


	/**
	 * Sets a double value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The double value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, double value);


	/**
	 * Sets a long value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The long value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, long value);


	/**
	 * Sets a boolean value field.
	 * 
	 * @param name
	 *            <span>The name of the column.</span>
	 * @param value
	 *            <span>The boolean value to set.</span>
	 * @return The IQuery object.
	 */
	public IQuery set(String name, boolean value);


	/**
	 * Sets the ContentValues object for a db query.
	 * 
	 * @param values
	 *            <span>The ContentValues object for the query.</span>
	 * @return The IQuery object.
	 */
	public IQuery values(ContentValues values);


	/**
	 * Sets the where clause for a db action.
	 * 
	 * @param whereClause
	 *            <span>The where clause string.</span>
	 * @return The IQuery object.
	 */
	public IQuery where(String whereClause);


	/**
	 * Sets a like condition for a where clause for a db query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereLike(String field, Object value);


	/**
	 * Sets an equals comparison for a where clause for a db query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereEquals(String field, Object value);


	/**
	 * Sets a less than comparison for a where clause for a db query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereLessThan(String field, Object value);


	/**
	 * Sets a greather than comparison for a where clause for a db query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereGreaterThan(String field, Object value);


	/**
	 * Sets a greater than or equals comparison for a where clause for a db
	 * query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereGreaterThanOrEquals(String field, Object value);


	/**
	 * Sets a less than or equals comparison for a where clause for a db query.
	 * 
	 * @param field
	 *            <span>The field name.</span>
	 * @param value
	 *            <span>The comparison value.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereLessThanOrEquals(String field, Object value);


	/**
	 * Sets the arguments for a where clause.
	 * 
	 * @param whereArgs
	 *            <span>The where clause arguments array.</span>
	 * @return The IQuery object.
	 */
	public IQuery whereArgs(String[] whereArgs);


	/**
	 * Indicates an and operator for a where clause.
	 * 
	 * @return The IQuery object.
	 */
	public IQuery and();


	/**
	 * Indicates an or condition for a where clause.
	 * 
	 * @return The IQuery object.
	 */
	public IQuery or();


	/**
	 * Indicates a 'distinct' qualifier for a query statement.
	 * 
	 * @return The IQuery object.
	 */
	public IQuery distinct();


	/**
	 * Sets the columns to act upon.
	 * 
	 * @param columns
	 * @return The IQuery object.
	 */
	public IQuery columns(String[] columns);


	/**
	 * Sets the columns to act upon.
	 * 
	 * @param columns
	 * @return The IQuery object.
	 */
	public IQuery columns(String columns);


	/**
	 * Sets the columns to act upon.
	 * 
	 * @param columns
	 * @return The IQuery object.
	 */
	public IQuery columns(Object... columns);


	/**
	 * Sets a group by qualifier for a query statement.
	 * 
	 * @param groupBy
	 * @return The IQuery object.
	 */
	public IQuery groupBy(String groupBy);
	
	public IQuery having(String having);
	
	public IQuery orderBy(String orderBy);
	
	public IQuery limit(String limit);


	/**
	 * Runs the query.
	 * 
	 * @return The IQuery object.
	 */
	public Object run();
}
