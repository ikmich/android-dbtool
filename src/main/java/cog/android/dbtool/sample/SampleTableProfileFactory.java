package cog.android.dbtool.sample;

import android.content.Context;
import android.widget.Toast;
import cog.android.dbtool.table.ColumnProfile;
import cog.android.dbtool.table.TableProfile;

/**
 * Creates sample TableProfile objects for test use.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class SampleTableProfileFactory {

	private static Context _context;

	public static void context(Context c) {
		_context = c;
	}

	private static TableProfile usersTable, productsTable, categoriesTable, stuffNamesTable;

	/**
	 * Gets the TableProfile for the 'uses' table.
	 * 
	 * @return
	 */
	public static TableProfile getUsers() {
		return new TableProfile("users").column(new ColumnProfile("id").type_int().primaryKey().notNull())
			.column(new ColumnProfile("username").type_text().unique())
			.column(new ColumnProfile("password").type_text().unique());
	}

	/**
	 * Gets the TableProfile for the 'products' table.
	 * 
	 * @return
	 */
	public static TableProfile getProducts() {
		return new TableProfile("products").column(new ColumnProfile("id").primaryKey().type_int().notNull())
			.column(new ColumnProfile("name").type_text().notNull())
			.column(new ColumnProfile("price").type_float().notNull())
			.column(new ColumnProfile("categoryId").type_int().notNull().foreignKey("categories", "categoryId"));
	}

	/**
	 * Gets the TableProfile for the 'categories' table.
	 * 
	 * @return
	 */
	public static TableProfile getCategories() {
		return new TableProfile("categories").column(
			new ColumnProfile("categoryId").primaryKey().type_int().notNull().autoIncrement()).column(
			new ColumnProfile("categoryName").type_text().notNull());
	}

	/**
	 * Gets the TableProfile for the 'stuffnames' table.
	 * 
	 * @return
	 */
	public static TableProfile getStuffNames() {
		return new TableProfile("stuffNames").column(
			new ColumnProfile("id").primaryKey().type_int().notNull().autoIncrement()).column(
			new ColumnProfile("stuffName").type_text().notNull());
	}

	/**
	 * Gets all the sample TableProfileS.
	 * 
	 * @return
	 */
	public static TableProfile[] getTableProfiles() {
		usersTable = getUsers();
		productsTable = getProducts();
		categoriesTable = getCategories();
		stuffNamesTable = getStuffNames();

		TableProfile[] dbTables = new TableProfile[] { usersTable, productsTable, categoriesTable, stuffNamesTable };

		return dbTables;
	}

	static void toast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
	}

	static void toastlong(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
	}

}
