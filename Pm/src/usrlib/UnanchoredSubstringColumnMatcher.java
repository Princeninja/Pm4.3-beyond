package usrlib;

import com.healthmarketscience.jackcess.ColumnMatcher;
import com.healthmarketscience.jackcess.SimpleColumnMatcher;
import com.healthmarketscience.jackcess.Table;

public class UnanchoredSubstringColumnMatcher implements ColumnMatcher {

	public static final UnanchoredSubstringColumnMatcher INSTANCE = new UnanchoredSubstringColumnMatcher();

	public UnanchoredSubstringColumnMatcher(){		
	}

	public boolean matches(Table table, String columnName, Object value1, Object value2 ) {
				
		if( ! table.getColumn(columnName).getType().isTextual()){
	    	// use simple equality
	    	return SimpleColumnMatcher.INSTANCE.matches(table, columnName, value1, value2);
		}

		// convert both values to Strings and compare case-insensitively
		if (( value1 == null) || ( value2 == null )) return false;
		
		
			String cs1 = value1.toString().toLowerCase();
			String cs2 = value2.toString().toLowerCase();
			 
			//System.out.println( "Comparing cs1=" + cs1 + " with cs2=" + cs2 );
			return((cs1 == cs2) || ((cs1 != null) && (cs2 != null) && ( cs2.indexOf(cs1) >= 0 )));
	}

}
