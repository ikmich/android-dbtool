package cog.android.dbtool.table;

import java.util.ArrayList;

public class ColumnProfile {

	private String name = "";
	//private Object defaultValue;
	private String defaultValueDefinition = "";
	private String foreignKeyDefinition = "";
	//private String foreignKeyDefinitionPrefix = "REFERENCES ";
	private ArrayList<String> foreignKeyDefinitions;
	private String uniqueDefinition = "";
	private String primaryKeyDefinition = "";
	private String type = "";
	private String notNullDefinition = "";
	private String checkDefinition = "";
	private String autoIncrementDefinition = "";

	private String fullColumnDefinition = "";
	private boolean defined = false;
	private boolean _built = false;

	/**
	 * Create ColumnProfile instance.
	 * 
	 * @param name
	 *        The column name.
	 */
	public ColumnProfile(String name) {
		this.name = name;
		foreignKeyDefinitions = new ArrayList<String>();
	}

	/**
	 * Create ColumnProfile instance.
	 * 
	 * @param name
	 *        The name of the column.
	 * @param columnDefinition
	 *        The column definition, WITHOUT the column name.
	 */
	public ColumnProfile(String name, String columnDefinition) {
		this(name);
		define(columnDefinition);
	}

	/**
	 * Sets the column definition for this ColumnProfile instance.
	 * 
	 * @param columnDefinition
	 *        The column definition, WITHOUT the column name.
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile define(String columnDefinition) {
		if (this.name == null || this.name.equals(""))
			return this;

		fullColumnDefinition = this.name + " " + columnDefinition;
		defined = true;
		return this;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the column.
	 * 
	 * @param name
	 *        The column name.
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile setColumnName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Indicates an 'integer' type for a column.
	 * 
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile type_int() {
		this.type = "INTEGER";
		return this;
	}

	/**
	 * Indicates a 'text' type for a column.
	 * 
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile type_text() {
		this.type = "TEXT";
		return this;
	}

	/**
	 * Indicates a 'null' type for a column.
	 * 
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile type_null() {
		this.type = "NULL";
		return this;
	}

	/**
	 * Indicates a 'float' type for a column.
	 * 
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile type_float() {
		this.type = "FLOAT";
		return this;
	}

	/**
	 * Indicates a 'blob' type for a column.
	 * 
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile type_blob() {
		this.type = "BLOB";
		return this;
	}

	/**
	 * Alias for type_blob()
	 * 
	 * @return
	 */
	public ColumnProfile type_object() {
		return type_blob();
	}

	/**
	 * Sets the default value for a column.
	 * 
	 * @param value
	 *        The default value.
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile setDefault(Object value) {
		this.defaultValueDefinition = "DEFAULT " + value.toString();
		return this;
	}

	/**
	 * Sets the default value for an 'int' column.
	 * 
	 * @param value
	 *        The default value.
	 * @return The ColumnProfile instance, for chaining.
	 */
	public ColumnProfile setDefault(int value) {
		this.defaultValueDefinition = "DEFAULT " + value;
		//this.defaultValue = value;
		return this;
	}

	/**
	 * Sets the default value for a long column.
	 * 
	 * @param value
	 *        The default value.
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile setDefault(long value) {
		this.defaultValueDefinition = "DEFAULT " + value;
		return this;
	}

	/**
	 * Sets the default value for a text column.
	 * 
	 * @param value
	 *        The default value.
	 * @return The ColumnProfile instance, for chained calls.
	 */
	public ColumnProfile setDefault(String value) {
		return setDefault(value, false);
	}

	/**
	 * Sets the default value for a text column.
	 * 
	 * @param value
	 * @param escapeSingleQuotes
	 *        Indicates whether to escape single quotes in the value. Having a
	 *        single quote in an sql value may cause an error. If you will be
	 *        having at least a single quote in the value, this argument should
	 *        be set to true.
	 * @return
	 */
	public ColumnProfile setDefault(String value, boolean escapeSingleQuotes) {
		if (escapeSingleQuotes) {
			/*
			 * Replace one occurrence of a single quote with two single
			 * quotes. That is the generally accepted way for SQL syntax.
			 */
			value = value.replaceAll("'{1}", "''");
		}

		this.defaultValueDefinition = "DEFAULT '" + value + "'";
		return this;
	}

	/**
	 * Sets the default value for a float/decimal column.
	 * 
	 * @param value
	 *        The default value.
	 * @return The ColumnProfile instance for chained calls.
	 */
	public ColumnProfile setDefault(float value) {
		this.defaultValueDefinition = "DEFAULT " + String.valueOf(value);
		return this;
	}

	/**
	 * Sets the 'NOT NULL' qualifier for a column.
	 * 
	 * @return The ColumnProfile instance for chaned calls.
	 */
	public ColumnProfile notNull() {
		this.notNullDefinition = "NOT NULL";
		return this;
	}

	/**
	 * Sets the 'unique' constraint for a column.
	 * 
	 * @return The ColumnProfile instance for chained calls.
	 */
	public ColumnProfile unique() {
		this.uniqueDefinition = "UNIQUE";
		return this;
	}

	/**
	 * Sets the 'autoincrement' qualifier for a column.
	 * 
	 * @return The ColumnProfile instance for chained calls.
	 */
	public ColumnProfile autoIncrement() {
		this.autoIncrementDefinition = "AUTOINCREMENT"; //should come before NOT NULL in the statement.
		return this;
	}

	/**
	 * Sets the 'primary key' constraint for a column.
	 * 
	 * @return The ColumnProfile instance for chained calls.
	 */
	public ColumnProfile primaryKey() {
		this.primaryKeyDefinition = "PRIMARY KEY";
		return this;
	}

	/**
	 * Sets a 'foreign key' constraint on a column.
	 * 
	 * @param referenceTable
	 *        The referenced table.
	 * @param referenceColumns
	 *        The columns in the referenced table.
	 * @return
	 */
	public ColumnProfile foreignKey(String referenceTable, String referenceColumns) {
		String def = referenceTable + "(" + referenceColumns + ")";
		this.foreignKeyDefinitions.add(def);
		return this;
	}

	public ColumnProfile check(String expr) {
		this.checkDefinition = "CHECK (" + expr + ")";
		return this;
	}

	private String buildForeignKeyDefinition() {
		this.foreignKeyDefinition += "REFERENCES ";
		for (String def : this.foreignKeyDefinitions) {
			this.foreignKeyDefinition += def;
			if (this.foreignKeyDefinitions.indexOf(def) < (this.foreignKeyDefinitions.size() - 1)) {
				this.foreignKeyDefinition += ", ";
			}
		}
		return this.foreignKeyDefinition;
	}

	/**
	 * Builds the column definition string for use within a 'create table' SQL
	 * statement.
	 * 
	 * @return The column definition string.
	 */
	public String build() {
		if (!_built) {
			StringBuilder sb = new StringBuilder();

			if (this.name == null || this.name.equals("")) {
				return "";
			}

			// Set a default type if none set.
			if (this.type == "") {
				this.type_int();
			}

			if (defined) {
				_built = true;
			}
			else {
				sb.append(this.name);
				sb.append(" " + this.type);
				sb.append(this.primaryKeyDefinition == "" ? "" : (" " + this.primaryKeyDefinition));
				sb.append(this.autoIncrementDefinition == "" ? "" : (" " + this.autoIncrementDefinition));
				sb.append(this.uniqueDefinition == "" ? "" : (" " + this.uniqueDefinition));
				sb.append(this.defaultValueDefinition == "" ? "" : (" " + this.defaultValueDefinition));
				sb.append(this.foreignKeyDefinitions.isEmpty() ? "" : (" " + this.buildForeignKeyDefinition()));
				sb.append(this.checkDefinition == "" ? "" : " " + this.checkDefinition);
				sb.append(this.notNullDefinition == "" ? "" : (" " + this.notNullDefinition));
				this.fullColumnDefinition = sb.toString();
				_built = true;
			}
		}

		return this.fullColumnDefinition;
	}

	/**
	 * Checks whether the column definition string has been built.
	 * 
	 * @return
	 */
	public boolean isBuilt() {
		return _built;
	}

	@Override
	public String toString() {
		return this.fullColumnDefinition;
	}

}