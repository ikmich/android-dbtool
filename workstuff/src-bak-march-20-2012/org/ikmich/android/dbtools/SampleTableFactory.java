package org.ikmich.android.dbtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Creates sample tables for org.ikmich.android.dbtools.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class SampleTableFactory {

	private static Context _context;

	public static void context(Context c) {
		_context = c;
	}

	private static TableProfile usersTable, productsTable, categoriesTable, stuffNamesTable;

	public static TableProfile getUsersTable() {
		return new TableProfile("users")
				.column(new ColumnProfile("id").type_int().primaryKey().notNull())
				.column(new ColumnProfile("username").type_text().unique())
				.column(new ColumnProfile("password").type_text().unique());
	}

	public static TableProfile getProductsTable() {
		return new TableProfile("products")
				.column(new ColumnProfile("id").primaryKey().type_int().notNull())
				.column(new ColumnProfile("name").type_text().notNull())
				.column(new ColumnProfile("price").type_float().notNull())
				.column(new ColumnProfile("categoryId").type_int().notNull()
						.foreignKey("categories", "categoryId"));
	}

	public static TableProfile getCategoriesTable() {
		return new TableProfile("categories").column(
				new ColumnProfile("categoryId").primaryKey().type_int().notNull().autoIncrement())
				.column(new ColumnProfile("categoryName").type_text().notNull());
	}

	public static TableProfile getStuffNamesTable() {
		return new TableProfile("stuffNames").column(
				new ColumnProfile("id").primaryKey().type_int().notNull().autoIncrement()).column(
				new ColumnProfile("stuffName").type_text().notNull());
	}

	public static TableProfile[] getSampleTableProfiles() {
		usersTable = getUsersTable();
		productsTable = getProductsTable();
		categoriesTable = getCategoriesTable();
		stuffNamesTable = getStuffNamesTable();

		TableProfile[] dbTables = new TableProfile[] { usersTable, productsTable, categoriesTable,
				stuffNamesTable };

		return dbTables;
	}

	static void toast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
	}

	static void toastlong(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}

}
