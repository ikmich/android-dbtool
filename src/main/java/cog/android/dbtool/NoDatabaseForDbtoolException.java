package cog.android.dbtool;

/**
 * Error when an SQLiteDatabase has not been set for the Dbtool instance.
 */
public class NoDatabaseForDbtoolException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	static String msg = "Null SQLiteDatabase for the Dbtool instance.";

	public NoDatabaseForDbtoolException() {
		super(msg);
	}
}
