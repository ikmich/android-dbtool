package org.ikmich.dbtool.data;

import org.ikmich.dbtool.DbRecord;
import org.ikmich.dbtool.DbRecordSet;
import org.ikmich.dbtool.Dbtool;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public abstract class EntityFactory implements IEntityFactory
{
	protected static Context context;
	protected static Dbtool dbtool;
	protected static SQLiteDatabase db;
	protected long lastCreatedRecordId = 0;
	protected boolean hasDbAuthority = true;

	public EntityFactory(Context c)
	{
		context = c;
		dbtool = Dbtool.getInstance(context);
		dbtool.setDb(getDbName());
	}

	public EntityFactory(Context c, String dbName)
	{
		context = c;
		db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		dbtool = Dbtool.getInstance(context, db);
		hasDbAuthority = false;
	}

	public EntityFactory(Context c, SQLiteDatabase sqliteDb)
	{
		context = c;
		db = sqliteDb;
		dbtool = Dbtool.getInstance(context, db);
		hasDbAuthority = false;
	}

	protected boolean hasDbAuthority()
	{
		return hasDbAuthority;
	}

	protected void openDb()
	{
		if (hasDbAuthority())
		{
			dbtool.openDb();
		}
	}

	protected void closeDb()
	{
		if (hasDbAuthority())
		{
			dbtool.closeDb();
		}
	}

	/**
	 * Gets the database name.
	 * 
	 * @return
	 */
	public abstract String getDbName();

	/**
	 * Gets the table name for this entity.
	 * 
	 * @return
	 */
	public abstract String getTable();

	/**
	 * Gets a custom defined 'tag' for this entity.
	 * 
	 * @return
	 */
	public abstract String getTag();

	@Override
	public boolean create(DbRecord row)
	{
		try
		{
			openDb();

			lastCreatedRecordId = (Long) dbtool.insertInto(getTable())
				.record(row).run();
			return lastCreatedRecordId > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error creating ").append(getTag())
				.append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public boolean update(long id, DbRecord record)
	{
		try
		{
			openDb();

			int numRows = 0;
			dbtool.update(getTable());
			dbtool.record(record);
			dbtool.whereEquals(COL_ID, id);
			numRows = (Integer) dbtool.run();

			return numRows > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error updating ").append(getTag())
				.append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public boolean delete(long id)
	{
		try
		{
			openDb();
			int numRows = (Integer) dbtool.deleteFrom(getTable())
				.whereEquals(COL_ID, id).run();
			return numRows > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append(getTag())
				.append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public boolean delete(String name, Object value)
	{
		try
		{
			openDb();
			int n = (Integer) dbtool.deleteFrom(getTable())
				.whereEquals(name, value).run();
			return n > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append(getTag())
				.append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public boolean delete(DbRecord record)
	{
		try
		{
			openDb();
			int n = (Integer) dbtool.deleteFrom(getTable()).record(record)
				.run();
			return n > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append(getTag())
				.append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public boolean hasItems()
	{
		try
		{
			openDb();
			DbRecordSet records = (DbRecordSet) dbtool.get(COL_ID)
				.from(getTable()).run();
			return records != null && records.size() > 0;
		}
		catch (Exception ex)
		{
			alertError("Error checking items availability: " + ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	public boolean clearData()
	{
		try
		{
			openDb();
			int i = (Integer) dbtool.deleteFrom(getTable()).run();
			if (i >= 0)
			{
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			alertError("Error deleting data: " + ex.getMessage());
			return false;
		}
		finally
		{
			closeDb();
		}
	}

	@Override
	public long getLastCreatedRecordId()
	{
		return lastCreatedRecordId;
	}

	protected Context getContext()
	{
		return context;
	}

	protected void alert(Object msg)
	{
		alert(null, msg);
	}

	protected void alert(String title, Object msg)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
		b.setTitle(title).setMessage(msg.toString())
			.setPositiveButton("OK", null);
		AlertDialog d = b.create();
		d.show();
	}

	protected void alertError(Object msg)
	{
		alert("Error", msg);
	}

	protected void toast(Object msg)
	{
		Toast.makeText(getContext(), msg.toString(), Toast.LENGTH_SHORT).show();
	}

	protected void toast2(Object msg)
	{
		Toast.makeText(getContext(), msg.toString(), Toast.LENGTH_LONG).show();
	}
}
