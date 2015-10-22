package org.ikmich.android.dbtools.trash;

public interface ICogModifyingQuery extends ICogConditionalQuery {
	
	public ICogModifyingQuery set(String name, int value);
	public ICogModifyingQuery set(String name, Byte value);
	public ICogModifyingQuery set(String name, float value);
	public ICogModifyingQuery set(String name, short value);
	public ICogModifyingQuery set(String name, byte[] value);
	public ICogModifyingQuery set(String name, String value);
	public ICogModifyingQuery set(String name, double value);
	public ICogModifyingQuery set(String name, long value);
	public ICogModifyingQuery set(String name, boolean value);
	
}
