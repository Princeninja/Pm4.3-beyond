package usrlib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class StructHelpers {

	// Get and set a byte type
	public static byte getByte( byte[] data, int offset ){
		return ( data[offset] );
	}
	
	public static void setByte( byte value, byte[] data, int offset ){
		data[offset] = value;
		return;
	}
	
	public static void setByte( int value, byte[] data, int offset ){
		setByte( (byte)(value & 0xff), data, offset );
		return;
	}
	
	
	
	
		
	// Get and set a 32bit integer type
	public static int getInt( byte[] data, int offset ){

		// Little Endian (LSB first)
		return ((data[offset]&0xff) | ((data[offset+1]&0xff) << 8) | ((data[offset+2]&0xff) << 16) | ((data[offset+3]&0xff) << 24 ));
		// Big Endian (MSB first)
		//return ((data[offset] <<24) | (data[offset+1] << 16) | (data[offset+2] << 8) | (data[offset+3]));
	}
	
	public static void setInt( int value, byte[] data, int offset ){
		
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & value);
		data[offset+1] = (byte)(0xff & (value>>8));
		data[offset+2] = (byte)(0xff & (value>>16));
		data[offset+3] = (byte)(0xff & (value>>24));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (value>>24));
		//data[offset+1] = (byte)(0xff & (value>>16));
		//data[offset+2] = (byte)(0xff & (value>>8));
		//data[offset+3] = (byte)(0xff & (value));
		return;
	}
	
	
		
	// Get and set a 32bit long integer type (SAME AS INT for now)
	public static int getLong32( byte[] data, int offset ){
		return StructHelpers.getInt( data, offset );
	}
	
	public static void setLong32( int value, byte[] data, int offset ){
		StructHelpers.setInt( value, data, offset);
		return;
	}
	
	
// TODO - Implement get/set 64 bit long	
//	// Get and set a 64 bit long integer type
//	public static int getLong64( byte data[], int offset ){
//		return <int>;
//	}
//	
//	public static void setLong64( int offset ){
//		return;
//	}
	

	
	// Get and set a 16bit short integer type
	public static int getShort( byte[] data, int offset ){
		// Little Endian (LSB first)
		return ((data[offset]&0xff) | ((data[offset+1]&0xff) << 8));
		// Big Endian (MSB first)
		//return ((data[0] <<8) | (data[offset+1]));
	}
	
	public static void setShort( int value, byte[] data, int offset ){
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & value);
		data[offset+1] = (byte)(0xff & (value>>8));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (value>>8));
		//data[offset+1] = (byte)(0xff & (value));
		return;
	}

	
	
	
	// Get and set a 16 bit unsigned short integer type
	public static int getUShort( byte[] data, int offset ){
		// Little Endian (LSB first)
		return ((data[offset]&0xff) | ((data[offset+1]&0xff) << 8));
		// Big Endian (MSB first)
		//return ((data[0] <<8) | (data[offset+1]));
	}
	
	public static void setUShort( int value, byte[] data, int offset ){
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & value);
		data[offset+1] = (byte)(0xff & (value>>8));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (value>>8));
		//data[offset+1] = (byte)(0xff & (value));
		return;
	}
	
	
	
	// Get and set string fields
	public static String getString( byte[] data, int offset, int len ){
		// this function has been updated to only allow char values from 32 'SPC' to 126 '~'
		StringBuilder s = new StringBuilder();
		for ( int cnt = 0; cnt < len; ++cnt, ++offset ){
			// null terminator
			if (( data[offset] & 0xff ) == 0 ) break;
			// filter non-printing characters
			if ((( data[offset]&0xff) > 31) && (( data[offset]&0xff) < 127 ))
				s.append( (char)( data[offset] & 0xff ));
		}
		return s.toString();
	}

	public static String getStringRaw( byte[] data, int offset, int len ){
		return new String( data, offset, len );
	}
	
	
	public static void setString( String string, byte[] data, int offset, int len ){
		setStringPadded( string, data, offset, len );
		return;
	}

	public static void setStringPadded( String string, byte[] data, int offset, int len ){
		int ulen = string.length();
		for ( int i=0; i < len; ++i )
			data[offset+i] = (byte)( i >= ulen ? ' ': string.charAt(i));
		return;
	}

	public static void setStringNoPad( String string, byte[] data, int offset, int len ){
		int ulen;
		ulen = ( string.length() <= len ) ? string.length(): len;
		for ( int i=0; i < ulen; ++i ) data[offset+i] = (byte)( string.charAt(i));
		return;
	}

	
	
	// Get/Set palmed Date type
	
	public static usrlib.Date getDate( byte[] data, int offset ){
		return new Date( data, offset );
	}
	
	public static void setDate( usrlib.Date date, byte[] data, int offset ){
		date.toBCD( data, offset );
		return;
	}

	
	
	
	// Get/Set palmed Time type
	
	public static usrlib.Time getTime( byte[] data, int offset ){
		return Time.fromBCD( data, offset, Time.BCDLEN.HHMM );
	}
	
	public static void setTime( usrlib.Time time, byte[] data, int offset ){
		time.toBCD( data, offset, Time.BCDLEN.HHMM );
		return;
	}

	
	
	
	/**
	 * zeroBytes() - Zero out an array of bytes
	 * 
	 * @param byte[]
	 * @return byte[]
	 */
	public static byte[] zeroBytes( byte[] data ){
		for ( int i=0; i < data.length; ++i ) data[i] = 0;
		return data;
	}

	
	
	/**
	 * Validity enum
	 * 
	 * ************MOVED TO VALIDITY.JAVA******************
	 */
	
/*	public enum Validity {
		INVALID(1),
		VALID(2),
		HIDDEN(3);
		
		private static final Map<Integer, Validity> lookup = new HashMap<Integer,Validity>();
		
		static {
			for ( Validity s : EnumSet.allOf(Validity.class))
				lookup.put(s.getCode(), s );
		}
		
		private int code;
		
		private Validity( int code ){ this.code = code; }
		
		public int getCode() { return code; }
		
		public static Validity get( int code ){ return lookup.get( code ); }
	}
	
	
	// Validity byte methods
	// from validity.h  -->>  V_INVALID = 1, V_VALID = 2, V_HIDDEN = 3
	public static boolean isValid( byte[] data, int offset ){
		return ( data[offset] == 2 ? true: false );
	}
	public static boolean isInvalid( byte[] data, int offset ){
		return ( data[offset] == 1 ? true: false );
	}
	public static boolean isHidden( byte[] data, int offset ){
		return ( data[offset] == 3 ? true: false );
	}

	public static byte getValidityByte( byte[] data, int offset ){
		return ( data[offset]);
	}
	public static void setValidityByte( byte validity, byte[] data, int offset ){
		data[offset] = validity;
	}

	public static void setValid( byte[] data, int offset ){
		data[offset] = 2;
	}
	public static void setInvalid( byte[] data, int offset ){
		data[offset] = 1;
	}
	public static void setHidden( byte[] data, int offset ){
		data[offset] = 3;
	}
*/	
	
	
	
	
	/**
	 * dump() - Function to print in Hex Dump style a byte[] data structure.
	 * 
	 * @param title
	 * @param data
	 * @param length
	 * @return void
	 */
	public static void dump( String title, byte[] data, int length ){
    	
        int BYTES_PER_LINE = 16;
        int i;
        int j;
        byte b;
        
        StringBuilder sb = new StringBuilder();
        if ( title == null ) title = "Dump";
        
        
        for (i = 0, j = 0; j < length; i++ ){
        	
            if (i == 0){
            	System.out.println( title );
            	
            } else if ( i % BYTES_PER_LINE == 0){
            	System.out.println( "    " + sb.toString() + "\n" /*, i */); 
            	sb.delete( 0, sb.length()); 
            	
            } else {
            	System.out.print(" ");
            }
            
            char c = (char) data[j++];
            System.out.printf("%02x", c & 0xff);
            if ( ! Character.isISOControl(c)){
            	sb.append(c); 
            } else {
            	sb.append( '.' );
            }
            
            if (i % BYTES_PER_LINE == 7 ) System.out.print( "  " );
        }
        
        System.out.println();
        System.out.println( i + " bytes" );
        
        /*for (i = 0, j=0; !BinaryStdIn.isEmpty(); i++) {
            if (BYTES_PER_LINE == 0) { BinaryStdIn.readChar(); continue; }
            if (i == 0) System.out.printf("");
            else if (i % BYTES_PER_LINE == 0) System.out.printf("\n", i);
            else System.out.print(" ");
            char c = BinaryStdIn.readChar();
            System.out.printf("%02x", c & 0xff);
        }*/

        return;
    }



}


/**/
