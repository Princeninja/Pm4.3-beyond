package usrlib;



/**
 * Class: Decoders
 * 
 * This class has a bunch of static methods used primarily to decode/encode some
 * legacy data types in PAL/MED.
 * 
 * <p>Copyright: Copyright (c) 2010 PAL/MED Development</p>
 *
 * @author J. Richard Palen, MD
 * @version 5.0
 */
public class Decoders {

	
	
	
	/**
	 * fromBCD()
	 * 
	 * This method generates an numeric String from the passed BCD data.
	 * 
	 * Note: there is a version with a third - offset - parameter
	 * 
	 * @param data			byte data array
	 * @param len			length of BCD data in bytes (usually half the number of digits)
	 * @return String		Returns String of numeric digits decoded
	 */
	
	public static String fromBCD( byte[] data, int len ){ return fromBCD( data, len, 0 ); }
	
	/**
	 * fromBCD()
	 * 
	 * This method generates an numeric String from the passed BCD data.
	 * 
	 * @param data			byte data array
	 * @param len			length of BCD data in bytes (usually half the number of digits)
	 * @param offset		offset to start decoding in byte data array
	 * @return String		Returns String of numeric digits decoded
	 */

    public static String fromBCD( byte[] data, int len, int offset ){
    	
    	String out = "";
    	
    	//System.out.println( "fromBCD( len=" + len + ", offset=" + offset + ")");
    	//System.out.println( "fromBCD() data=" + String.format( "%02x%02x%02x%02x", data[offset], data[offset+1], data[offset+2], data[offset+3] ));
    	
    	for ( int count = 0; count < len; ++ count )
    		out = out.concat( String.format( "%02x", data[offset+count] ));
    		
    		// THIS DIDN'T WORK???	out = out.concat( String.format( "%02d", ((data[offset+count]&0xf0)/16)*10 + data[offset+count]&0x0f ));

        return out;
    }
    
    

	/**
	 * toBCD()
	 * 
	 * This method generates a BCD data from a passed numeric String.
	 * 
	 * Note: there is a version with a third - offset - parameter
	 * 
	 * @param String		numeric string to encode
	 * @param data			byte data array
	 * @param len			length of BCD data in bytes (usually half the number of digits)
	 * @return byte[]		Returns byte[]
	 */
	
	public static byte[] toBCD( String s, byte[] data, int len ){ return toBCD( s, data, len, 0 ); }
	
	/**
	 * toBCD()
	 * 
	 * This method generates a BCD data from a passed numeric String.
	 * 
	 * @param String		numeric string to encode
	 * @param data			byte data array
	 * @param len			length of BCD data in bytes (usually half the number of digits)
	 * @param offset		offset to start decoding in byte data array
	 * @return byte[]		Returns byte[]
	 */

    public static byte[] toBCD( String s, byte[] data, int len, int offset ){

		int last;			// position in string
		int pos;			// position in BCD data string
		boolean toggle;		// toggle to show which half of BCD byte
		
    	
    	pos = len - 1;
		toggle = false;
		
    	for ( last = s.length() - 1; ( last >= 0 ) && ( pos >= 0 ); --last ){
    		
    		// Start with the end of the string and encode forward.  Will cause
    		// leading zeros.
    		
    		if ( Character.isDigit( s.charAt( last ))){
    			
    			if ( toggle ){
    				// high nibble
    				data[offset+pos] = (byte) (data[offset+pos] + ( s.charAt( last ) - '0' ) * 16);
    				--pos;
    			} else {
    				// low nibble
    				data[offset+pos] = (byte) (s.charAt( last ) - '0');
    			}
    			
    			toggle = ! toggle;
    		}
    	}
    	
        return data;
    }
    
    
    

    
    
	/**
	 * fromBCDPhone()
	 * 
	 * This method generates an phone number String from the passed BCD data.  It includes
	 * the dashes.  It leaves out the leading zeros (ie. if there was no area code entered).
	 * 
	 * Note: there are versions with and without the offset parameter
	 * 
	 * @param data			byte data array
	 * @param offset		offset to start decoding in byte data array
	 * @param dashes		flag to include dashes
	 * @return String		Returns String of numeric digits decoded
	 */
   
    public static String fromBCDPhone( byte[] data, int offset, boolean dashes ){

    	// convert BCD phone number to string of digits and remove any leading zeros
    	String out = Decoders.fromBCD( data, 5, offset );
    	
    	
    	// handle empty phone number
    	if ( out.equals( "0000000000" )) return "";
    	
    	// remove missing area code
    	if ( out.startsWith( "000" )) out = out.substring( 3 );
    	
    	// place dashes in phone number
    	if ( dashes ){    		
	    	int len = out.length();
	    	if ( len >= 7 ) out = out.substring( 0, len - 4 ) + "-" + out.substring( len - 4, len );
	    	len = out.length();
	    	if ( len >= 10 ) out = out.substring( 0, len - 8 ) + "-" + out.substring( len - 8, len );
    	}
    	return out;
    }
    
    public static String fromBCDPhone( byte[] data ){ return fromBCDPhone( data, 0, true ); }
    public static String fromBCDPhone( byte[] data, boolean dashes ){ return fromBCDPhone( data, 0, dashes ); }
    public static String fromBCDPhone( byte[] data, int offset ){ return ( fromBCDPhone( data, offset, true )); }

	/**
	 * toBCDPhone()
	 * 
	 * This method encodes a Phone Number String to BCD data.   
	 *  
	 * @param String phone - phone number string to encode
	 * @param byte[] data - byte data array
	 * @param int offset - offset to start encoding into byte data array
	 * @return void
	 */
    public static void toBCDPhone( String phone, byte[] data, int offset ){ toBCD( phone, data, 5, offset ); return; }
    

    
    
    
    
    

	/**
	 * fromBCDSSN()
	 * 
	 * This method generates a SSN String from the passed BCD data.  It includes
	 * the dashes. 
	 * 
	 * Note: there are versions with and without the offset parameter
	 * 
	 * @param data			byte data array
	 * @param offset		offset to start decoding in byte data array
	 * @param dashes		flag to include dashes
	 * @return String		Returns String of numeric digits decoded
	 */
   
    public static String fromBCDSSN( byte[] data, int offset, boolean dashes ){

    	// convert BCD SSN to string of digits
    	String out = Decoders.fromBCD( data, 5, offset );
    	//System.out.println( out );
    	
    	// handle empty SSN
    	if ( out.equals( "0000000000" )) return "";
    	
    	
    	// out = out.substring( 1, 4 ) + "-" + out.substring( 4, 6 ) + "-" + out.substring( 6, 10 );  REPLACED with below 2010-12-20

    	// Note - PAL/MED encoded SSNs have a leading (unused) 0 at char position 0
    	// place dashes in SSN if requested
    	StringBuilder sb = new StringBuilder();
    	sb.append( out.substring( 1, 4 ));
    	if ( dashes ) sb.append( '-' );
    	sb.append( out.substring( 4, 6 ));
    	if ( dashes ) sb.append( '-' );
    	sb.append( out.substring( 6, 10 ));    	
    	out = sb.toString();

    	return out;
    }
    
    public static String fromBCDSSNDSH( byte[] data, int offset ){

    	// convert BCD SSN to string of digits
    	String out = Decoders.fromBCD( data, 5, offset );
    	//System.out.println( out );
    	
    	// handle empty SSN
    	if ( out.equals( "0000000000" )) return "";
    	
    	
    	return out;
    }

    public static String fromBCDSSN( byte[] data ){ return fromBCDSSN( data, 0, true ); }
    public static String fromBCDSSN( byte[] data, boolean dashes ){ return fromBCDSSN( data, 0, dashes ); }
    public static String fromBCDSSN( byte[] data, int offset ){ return fromBCDSSN( data, offset, true ); }
    
    
	/**
	 * toBCDSSN()
	 * 
	 * This method encodes a SSN String to 5 byte BCD data. Result will have a leading
	 * zero in first nibble and be padded with zeros on end to ensure 9 digits.  
	 *  
	 * @param String ssn - ssn string to encode
	 * @param byte[] data - byte data array
	 * @param int offset - offset to start decoding in byte data array
	 * @return void
	 */
    public static void toBCDSSN( String ssn, byte[] data, int offset ){
    	
    	char[] c = ssn.toCharArray();
    	char[] z = new char[9];
    	
    	int j = 0;
  
    	// include only 9 digits
    	for ( int i = 0; i < c.length; ++i ){
    		if ( Character.isDigit(c[i])) z[j++] = c[i];
    		if ( j >= 9 ) break;	// max length
    	}
    	
    	// pad with zeros to right for 9 digit length (didn't enter 9 digits)
    	while ( j < 9 ) z[j++] = '0';
    	//System.out.println( "ssn=" + ssn );
    	//System.out.println( new String( z ));
    	
    	// encode to 5 byte BCD data
    	toBCD( new String( z ), data, 5, offset );
    	return;
    }

    	
    	
    	
    	
    


    
    
    
    
    
	/**
	 * fromBCDZipCode()
	 * 
	 * This method generates a Zip Code String from the passed BCD data.  It includes
	 * the dash and the 9 digit zip if not all zeros. 
	 * 
	 * Note: there are versions with and without the offset parameter
	 * 
	 * @param data			byte data array
	 * @param offset		offset to start decoding in byte data array
	 * @return String		Returns String of numeric digits decoded
	 */
   
    public static String fromBCDZipCode( byte[] data ){ return fromBCDZipCode( data, 0 ); }
    
    public static String fromBCDZipCode( byte[] data, int offset ){

    	// convert BCD Zip Code to string of digits, remove last digit
    	String s = Decoders.fromBCD( data, 5, offset ).substring( 1, 10 );
    	//System.out.println( "fromBCDZipCode() offset=" + offset + ", s=" + s );
    	
    	// Note - PAL/MED encoded SSNs have a leading (unused) 0 at char position 0
    	// place dashes in SSN
    	
    	// handle empty zipcode
    	if ( s.equals( "000000000" )) return "";
    	
    	
    	// place dash if zip+4 entered
    	String out = s.substring( 0, 5 );
    	
    	if ( ! s.substring( 5, 9 ).equals( "0000" ))
    		out = out.concat( "-" + s.substring( 5, 9 ));
    	//System.out.println( "fromBCDZipCode() returns=" + out );
    	
    	return out;
    }
    
	/**
	 * toBCDZipCode()
	 * 
	 * This method encodes a Zip Code String to 5 byte BCD data.   The result will
	 * be padded on the right with zeros if a zip+4 was not entered.  There will 
	 * be one leading zero. 
	 *  
	 * @param String zip - zip code string to encode
	 * @param byte[] data - byte data array
	 * @param int offset - offset to start encoding into byte data array
	 * @return void
	 */
    public static void toBCDZipCode( String zip, byte[] data, int offset ){
    	
    	char[] c = zip.toCharArray();
    	char[] z = new char[9];   	
    	int j = 0;

    	// copy only max 9 digits
    	for ( int i = 0; i < c.length; ++i ){
    		if ( Character.isDigit(c[i])) z[j++] = c[i];
    		if ( j >= 9 ) break;	// max length
    	}
    	
    	// pad with zeros to right for 9 digit length (didn't enter zip+4)
    	while ( j < 9 ) z[j++] = '0';
    	
    	//System.out.println( "zip=" + zip );
    	//System.out.println( new String( z ));
 
    	// convert to 5 byte BCD
    	toBCD( new String( z ), data, 5, offset );
    	return;
    }
    


    


}
