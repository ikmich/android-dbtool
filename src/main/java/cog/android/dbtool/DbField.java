package cog.android.dbtool;

import java.util.ArrayList;

/**
 * The <b>DbField</b> class is simply an extension of ArrayList&lt;Object&gt;. A
 * <b>DbField</b> is expected to be returned when using the get(column) method
 * of Dbtool where only one column is to be returned. That method could as well
 * be made to return an ArrayList, but the decision to return a custom DbField
 * object is for potential design extensibility and integrability purposes.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class DbField extends ArrayList<String> {

	private static final long serialVersionUID = 1575814975434381995L;

	public DbField() {
		super();
	}

	public DbField(int capacity) {
		super(capacity);
	}
}
