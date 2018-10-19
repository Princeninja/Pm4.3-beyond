package usrlib;

public class StringHelpers {
	
	
	/**
	 * onlyDigits() - Filter a string to contain only digits, removing all letters,
	 * punctuation, and white space, etc.
	 * 
	 * @param s
	 * @return new String
	 */
	public static String onlyDigits( String s ){
		

		char[] c = s.toCharArray();
		char[] z = new char[c.length];
		
		int j = 0;
	
		// include only 9 digits
		for ( int i = 0; i < c.length; ++i ){
			if ( Character.isDigit(c[i])) z[j++] = c[i];
		}
		
		return new String( z, 0, j );
	}
	
	
	
	/**
	 * pad() - Pad a string to a length with spaces.
	 * 
	 * @param s
	 * @param len
	 * @return new String
	 */
	public static String pad( String s, int len ){ return StringHelpers.pad( s, len, ' ' ); }

	
	/**
	 * pad() - Pad a string to a length with a char.
	 * 
	 * @param s
	 * @param len
	 * @return new String
	 */
	public static String pad( String s, int len, char ch ){
		
		if ( s.length() >= len ) return s;
		
		char[] c = new char[len];
		s.getChars(0, s.length(), c, 0);
		for ( int i = s.length(); i < len; ++i ) c[i] = ch;
		return ( new String( c ));
	}

	
	
	
	/**
	 * truncate() - Truncate and return only the first part of a string (as a new string)
	 * 		(Note: this utility ALWAYS returns a valid string.)
	 * 
	 */
	public static String truncate( String s, int len ){
		if ( s == null ) return "";
		if ( s.length() <= len ) return s;
		return s.substring( 0, len );
	}







}
