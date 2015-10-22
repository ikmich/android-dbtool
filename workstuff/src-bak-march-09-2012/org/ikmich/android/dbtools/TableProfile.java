package org.ikmich.android.dbtools;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to help build a string required to create a database table.
 * 
 * @author ikmich
 * 
 */
public class TableProfile {

	private String tableName;
	private String createSQLPrefix = "";
	private String createSQLSuffix = ");";
	private String fullCreateSQL = "";
	private List<String> columnDefinitions;
	private List<String> constraintDefinitions;
	private List<String> finalDefinitions;
	private boolean _built;


	public TableProfile() {
		columnDefinitions = new ArrayList<String>();
		constraintDefinitions = new ArrayList<String>();
		finalDefinitions = new ArrayList<String>();
	}


	public TableProfile(String name) {
		this();
		setTableName(name);
		createSQLPrefix = "create table if not exists " + name + "( ";
	}


	/**
	 * Sets the name of the table.
	 * 
	 * @param name
	 * @return
	 */
	public TableProfile setTableName(String name) {
		tableName = name;
		return this;
	}


	/**
	 * Retrieves the name of the table if set.
	 * 
	 * @return Returns the table name or null if not set.
	 */
	public String getTableName() {
		return tableName;
	}


	/**
	 * Creates the string used to create one column.
	 * 
	 * @param columnDefinition
	 * @return
	 */
	public TableProfile column(String columnDefinition) {
		columnDefinitions.add(columnDefinition);
		finalDefinitions.add(columnDefinition);
		return this;
	}
	
	
	public TableProfile column(ColumnProfile columnProfile) {
		String def = columnProfile.build();		
		columnDefinitions.add(def);
		finalDefinitions.add(def);
		return this;
	}


	/**
	 * Creates the string used to create one table-level constraint.
	 * 
	 * @param constraintDefinition
	 * @return
	 */
	public TableProfile constraint(String constraintDefinition) {
		constraintDefinitions.add("CONSTRAINT " + constraintDefinition);
		finalDefinitions.add(constraintDefinition);
		return this;
	}


	/**
	 * Checks if this TableProfile object is built i.e. if the required SQL
	 * query to create the table has been compiled.
	 * 
	 * @return
	 */
	public boolean isBuilt() {
		return _built;
	}


	/**
	 * Builds the table description string that can be used in a create/alter
	 * table sql statement. This description string can be retrieved using
	 * getCreateSQL() on this instance.
	 * 
	 * @return The TableProfile object, for chaining purposes.
	 */
	public TableProfile build() {
		if(!_built) {
			fullCreateSQL += createSQLPrefix;
			for(String definition : finalDefinitions) {
				fullCreateSQL += definition;
				if(finalDefinitions.indexOf(definition) < (finalDefinitions.size() - 1)) {
					fullCreateSQL += ", ";
				}
			}
			fullCreateSQL += createSQLSuffix;
			_built = true;
		}		
		return this;
	}


	/**
	 * Retrieves the SQL statement to create this table.
	 * 
	 * @return The SQL statement to create the table.
	 */
	public String getCreateSQL() {
		if(!this.isBuilt()) {
			this.build();
		}
		return this.fullCreateSQL;
	}

}