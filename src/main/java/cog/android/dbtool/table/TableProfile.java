package cog.android.dbtool.table;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * Utility class to help build the SQL string required to create a database
 * table.
 * 
 * @author ikmich
 */
public class TableProfile {
	private String tableName;
	private String sqlCreatePrefix = "";
	private String sqlCreateSuffix = ");";
	private String sqlFullCreate = "";
	private ArrayList<String> columnDefinitions;
	private ArrayList<String> constraintDefinitions;
	private ArrayList<String> finalDefinitions;
	private List<ColumnProfile> columnProfiles;
	private boolean _built;

	public TableProfile() {
		this(null);
	}

	/**
	 * 
	 * @param name
	 */
	public TableProfile(String name) {
		columnDefinitions = new ArrayList<String>();
		constraintDefinitions = new ArrayList<String>();
		finalDefinitions = new ArrayList<String>();
		columnProfiles = new ArrayList<ColumnProfile>();

		if (!TextUtils.isEmpty(name)) {
			setTableName(name);
			sqlCreatePrefix = getCreateSQLPrefix(name);
		}
	}

	private String getCreateSQLPrefix(String tableName) {
		String s = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
		return s;
	}

	/**
	 * 
	 * Sets the table name.
	 * 
	 * @param name
	 * @return TableProfile
	 */
	public TableProfile setTableName(String name) {
		this.tableName = name;
		sqlCreatePrefix = getCreateSQLPrefix(this.tableName);
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
	 * @return The TableProfile object.
	 */
	public TableProfile column(String columnDefinition) {
		columnDefinitions.add(columnDefinition);
		finalDefinitions.add(columnDefinition);
		return this;
	}

	/**
	 * Adds a column to the TableProfile via a ColumnProfile.
	 * 
	 * @param columnProfile
	 * @return The TableProfile object.
	 */
	public TableProfile column(ColumnProfile columnProfile) {
		String def = columnProfile.build();
		columnDefinitions.add(def);
		finalDefinitions.add(def);
		return this;
	}

	/**
	 * Adds a list of columns to the TableProfile.
	 * 
	 * @param columnProfiles
	 *        The List of ColumnProfile objects.
	 * @return
	 */
	public TableProfile columns(List<ColumnProfile> columnProfiles) {
		if (columnProfiles == null)
			return this;

		for (int i = 0; i < columnProfiles.size(); i++) {
			column(columnProfiles.get(i));
		}
		return this;
	}

	public TableProfile addColumnProfile(ColumnProfile columnProfile) {
		if (columnProfile == null)
			return this;

		columnProfiles.add(columnProfile);
		return this;
	}

	/**
	 * Creates the string used to create one table-level constraint.
	 * 
	 * @param constraintDefinition
	 * @return The TableProfile object.
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
		if (!_built) {
			sqlFullCreate += sqlCreatePrefix;
			for (String definition : finalDefinitions) {
				sqlFullCreate += definition;
				if (finalDefinitions.indexOf(definition) < (finalDefinitions.size() - 1)) {
					sqlFullCreate += ", ";
				}
			}
			sqlFullCreate += sqlCreateSuffix;
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
		if (!this.isBuilt()) {
			this.build();
		}
		return this.sqlFullCreate;
	}

	public List<String> getColumnDefinitions() {
		return finalDefinitions;
	}

	/**
	 * Returns a list of the ColumnProfile instances used to add columns to this
	 * TableProfile via the addColumnProfile(...) method.
	 * 
	 * @return
	 */
	public List<ColumnProfile> getColumnProfiles() {
		return columnProfiles;
	}

}