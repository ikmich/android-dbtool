package org.ikmich.dbtool.data;

import org.ikmich.dbtool.DbRecord;

public interface IEntityFactory
{
	public static final String COL_ID = "id";

	/// Define other column name constants here.

	/**
	 * Creates the table schema for this entity. If the table exists, an error
	 * will occur. To drop an existing table first, use :createSchema(boolean
	 * dropTable)".
	 * 
	 * @return
	 */
	public boolean createSchema();

	/**
	 * Creates the table schema for this entity. If the table exists, it is
	 * dropped first.
	 * 
	 * @param dropTable
	 *        Indicates whether to drop the table first if it exists.
	 * @return
	 */
	public boolean createSchema(boolean dropTable);

	/**
	 * Creates a db table row.
	 * 
	 * @param row
	 * @return
	 */
	public boolean create(DbRecord row);

	/**
	 * Updates a db table row.
	 * 
	 * @param id
	 * @param record
	 * @return
	 */
	public boolean update(long id, DbRecord record);

	/**
	 * Deletes a row from the db table according to the entry id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean delete(long id);

	/**
	 * Deletes a row from the db table acccording to the field and value.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean delete(String name, Object value);

	/**
	 * Deletes a row from the db table according to the specified record info.
	 * 
	 * @param record
	 * @return
	 */
	public boolean delete(DbRecord record);

	/**
	 * Checks if a db table has items.
	 * 
	 * @return
	 */
	public boolean hasItems();

	/**
	 * Returns the id of the last created record.
	 * 
	 * @return
	 */
	public long getLastCreatedRecordId();
}
