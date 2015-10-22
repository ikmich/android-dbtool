package org.dbtool.trash;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public interface ICogSelectQueryX extends ICogQuery {
	
	public ICogSelectQueryX uri(String uri);
	public ICogSelectQueryX uri(Uri uri);
	
}
