package org.ikmich.dbtool;

import android.content.ContentValues;

/**
 * Defines contract between the Dbtool and DbtoolActionUnit classes.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public interface IDbtoolAction
{
	IDbtoolAction getAll();

	IDbtoolAction selectAll();

	IDbtoolAction get(String[] columns);

	IDbtoolAction select(String[] columns);

	IDbtoolAction get(Object... columns);

	IDbtoolAction select(Object... columns);

	IDbtoolAction getString(String column);

	IDbtoolAction getInt(String column);

	IDbtoolAction getFloat(String column);
	
	IDbtoolAction getDouble(String column);

	IDbtoolAction from(String table);

	IDbtoolAction from(Object... tables);

	IDbtoolAction update(String table);

	IDbtoolAction update(Object... tables);

	IDbtoolAction insert(ContentValues values);

	IDbtoolAction into(String table);

	IDbtoolAction insertInto(String table);

	IDbtoolAction values(ContentValues values);

	IDbtoolAction record(DbRecord row);

	IDbtoolAction recordSet(DbRecordSet rows);

	IDbtoolAction set(String name, Object value);

	IDbtoolAction set(String name, Byte value);

	IDbtoolAction set(String name, int value);

	IDbtoolAction set(String name, float value);

	IDbtoolAction set(String name, short value);

	IDbtoolAction set(String name, byte[] value);

	IDbtoolAction set(String name, String value);

	IDbtoolAction set(String name, double value);

	IDbtoolAction set(String name, long value);

	IDbtoolAction set(String name, boolean value);

	IDbtoolAction deleteFrom(String table);

	IDbtoolAction deleteFrom(Object... tables);

	IDbtoolAction where(String whereClause);

	IDbtoolAction and();

	IDbtoolAction or();

	IDbtoolAction whereLike(String field, Object value);

	IDbtoolAction whereEquals(String field, Object value);
	
	IDbtoolAction whereNotEquals(String field, Object value);

	IDbtoolAction whereLessThan(String field, Object value);

	IDbtoolAction whereGreaterThan(String field, Object value);

	IDbtoolAction whereGreaterThanOrEquals(String field, Object value);

	IDbtoolAction whereLessThanOrEquals(String field, Object value);

	IDbtoolAction whereArgs(String[] whereArgs);

	IDbtoolAction distinct();

	IDbtoolAction columns(String[] columns);

	IDbtoolAction columns(String columns);

	IDbtoolAction columns(Object... columns);

	IDbtoolAction groupBy(String groupBy);

	IDbtoolAction having(String having);

	IDbtoolAction orderBy(String orderBy);

	IDbtoolAction limit(String limit);
}
