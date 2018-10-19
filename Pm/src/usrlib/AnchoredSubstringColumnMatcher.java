package usrlib;

import java.io.IOException;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.ColumnMatcher;
import com.healthmarketscience.jackcess.SimpleColumnMatcher;
import com.healthmarketscience.jackcess.Table;

public class AnchoredSubstringColumnMatcher implements ColumnMatcher {

	public static final AnchoredSubstringColumnMatcher INSTANCE = new AnchoredSubstringColumnMatcher();

	public AnchoredSubstringColumnMatcher(){		
	}

	public boolean matches(Table table, String columnName, Object value1, Object value2 ) {
				
		if( ! table.getColumn(columnName).getType().isTextual()){
	    	// use simple equality
	    	return SimpleColumnMatcher.INSTANCE.matches(table, columnName, value1, value2);
		}

		// convert both values to Strings and compare case-insensitively
		 
			String cs1 = value1.toString().toLowerCase();
			String cs2 = value2.toString().toLowerCase();
			 
			//System.out.println( "Comparing cs1=" + cs1 + " with cs2=" + cs2 );
			return((cs1 == cs2) || ((cs1 != null) && (cs2 != null) && cs2.startsWith(cs1)));
		 
		 

	}

}
