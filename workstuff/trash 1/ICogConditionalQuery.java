package org.dbtool.trash;
import android.content.Context;

public interface ICogConditionalQuery extends ICogQuery {
	public ICogConditionalQuery where(String whereClause);
	public ICogConditionalQuery whereEquals(String field, Object value);
	public ICogConditionalQuery whereGreaterThan(String field, Object value);
	public ICogConditionalQuery whereLessThan(String field, Object value);
	public ICogConditionalQuery whereGreaterThanOrEquals(String field, Object value);
	public ICogConditionalQuery whereLessThanOrEquals(String field, Object value);
	public ICogConditionalQuery and();
	public ICogConditionalQuery or();
	public ICogConditionalQuery context(Context c);
	public ICogConditionalQuery table(String table);
}
