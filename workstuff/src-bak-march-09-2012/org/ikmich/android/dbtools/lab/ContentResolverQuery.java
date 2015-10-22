package org.ikmich.android.dbtools.lab;

import org.ikmich.android.dbtools.SelectQuery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * THIS CLASS MIGHT BE INCOMPLETE!
 * 
 * @author ikmich
 * 
 */
class ContentResolverQuery extends SelectQuery {

	private Uri _uri;
	private String _uriString;
	private ContentResolver _resolver;
	private Activity _activity;
	private Boolean _isManaged;


	//private String _orderBy;

	//	private List<String> _columns;
	//	private String _whereClause;
	//	private List<String> _whereClauses;
	//	private Context _context;

	public ContentResolverQuery(Context c) {
		super(c);
		init();
	}


	public ContentResolverQuery uri(Uri uri) {
		_uri = uri;
		init();
		return this;
	}


	protected void init() {
		_isManaged = false;
		_resolver = _context.getContentResolver();
	}


	public ContentResolverQuery uri(String uriString) {
		_uriString = uriString;
		_uri = Uri.parse(_uriString);
		return this;
	}


	public ContentResolverQuery activity(Activity activity) {
		_activity = activity;
		//..if the activity is set, assume it's desired for the query to be a managed one..
		managed(true);
		return this;
	}


	public ContentResolverQuery managed(Boolean isManaged) {
		_isManaged = isManaged;
		return this;
	}


	@Override
	public ContentResolverQuery columns(String[] columns) {
		_columns = columns;
		return this;
	}


	@Override
	public ContentResolverQuery columns(String columns) {
		_columns = columns.split(",\\s*");
		return this;
	}


	@Override
	public ContentResolverQuery columns(Object... columns) {
		_columns = new String[columns.length];
		for(int i = 0; i < columns.length; i++) {
			_columns[i] = (String) columns[i];
		}
		return this;
	}


	@Override
	public Cursor run() {
		if(_uri == null) {
			try {
				throw new Exception("No URI defined.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * _activity.managedQuery(...); //or... _resolver.query(...);
		 */
		Cursor c;
		if(_isManaged) {
			c = _activity.managedQuery(_uri, _columns, _whereClause,
					_whereArgs, _orderBy);
		} else {
			c = _resolver.query(_uri, _columns, _whereClause, _whereArgs,
					_orderBy);
		}
		return c;
	}


//	@Override
//	void resetState() {
//
//	}

}
