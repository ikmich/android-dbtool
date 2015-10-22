package org.ikmich.dbtool.table;

import java.util.ArrayList;

public class ColumnProfile
{

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
	private boolean _built = false;

	public ColumnProfile()
	{
		this(null);
	}

	public ColumnProfile(String name)
	{
		foreignKeyDefinitions = new ArrayList<String>();
		this.name = name;
	}

	public ColumnProfile setColumnName(String name)
	{
		this.name = name;
		return this;
	}

	public ColumnProfile type_int()
	{
		this.type = "INTEGER";
		return this;
	}

	public ColumnProfile type_text()
	{
		this.type = "TEXT";
		return this;
	}

	public ColumnProfile type_null()
	{
		this.type = "NULL";
		return this;
	}

	public ColumnProfile type_float()
	{
		this.type = "FLOAT";
		return this;
	}

	public ColumnProfile type_blob()
	{
		this.type = "BLOB";
		return this;
	}

	public ColumnProfile type_object()
	{
		return type_blob();
	}

	public ColumnProfile setDefault(Object value)
	{
		this.defaultValueDefinition = "DEFAULT " + value.toString();
		return this;
	}

	public ColumnProfile setDefault(int value)
	{
		this.defaultValueDefinition = "DEFAULT " + value;
		//this.defaultValue = value;
		return this;
	}

	public ColumnProfile setDefault(long value)
	{
		this.defaultValueDefinition = "DEFAULT " + value;
		return this;
	}

	public ColumnProfile setDefault(String value)
	{
		//TODO Consider escaping single quotes
		this.defaultValueDefinition = "DEFAULT '" + value + "'";
		return this;
	}

	public ColumnProfile setDefault(float value)
	{
		this.defaultValueDefinition = "DEFAULT " + String.valueOf(value);
		return this;
	}

	public ColumnProfile notNull()
	{
		this.notNullDefinition = "NOT NULL";
		return this;
	}

	public ColumnProfile unique()
	{
		this.uniqueDefinition = "UNIQUE";
		return this;
	}

	public ColumnProfile autoIncrement()
	{
		this.autoIncrementDefinition = "AUTOINCREMENT"; //should come before NOT NULL in the statement.
		return this;
	}

	public ColumnProfile primaryKey()
	{
		this.primaryKeyDefinition = "PRIMARY KEY";
		return this;
	}

	public ColumnProfile foreignKey(String referenceTable, String referenceColumns)
	{
		String def = referenceTable + "(" + referenceColumns + ")";
		this.foreignKeyDefinitions.add(def);
		return this;
	}

	public ColumnProfile check(String expr)
	{
		this.checkDefinition = "CHECK (" + expr + ")";
		return this;
	}

	private String buildForeignKeyDefinition()
	{
		this.foreignKeyDefinition += "REFERENCES ";
		for (String def : this.foreignKeyDefinitions)
		{
			this.foreignKeyDefinition += def;
			if (this.foreignKeyDefinitions.indexOf(def) < (this.foreignKeyDefinitions.size() - 1))
			{
				this.foreignKeyDefinition += ", ";
			}
		}
		return this.foreignKeyDefinition;
	}

	public String build()
	{
		if (!_built)
		{
			StringBuilder sb = new StringBuilder();

			if (this.name == "")
			{
				return "";
			}

			//Set a default type if none set.
			if (this.type == "")
			{
				this.type_int();
			}

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

		return this.fullColumnDefinition;
	}

	public boolean isBuilt()
	{
		return _built;
	}

	@Override
	public String toString()
	{
		return this.fullColumnDefinition;
	}

}
