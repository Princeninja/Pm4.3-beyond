/**
 * Flat file data structures support class
 * (designed to be extended)
 * 
 * package usrlib;
 * @author J. Richard Palen, MD
 * 
 * Users must supply their own getRecLen() and getFileName() methods.  The constructor 
 * needs getRecLen() to allocate the data array.
 *
 */

package usrlib;


public class Struct {
	
	public byte data[];

	public Struct (){
		this.data = new byte[this.getRecLen()];
		return;
	}
	
	// Get RecLen() method - this should be overridden
	public int getRecLen(){
		System.out.println( "error: Struct.getRecLen() should be overridden.");
		return ( 0 );
	}
	
	// Get filename method - this should be overridden
	public String getFileName(){
		System.out.println( "error: Struct.getFileName() should be overridden.");
		return ( "error" );
	}
	
	
	
	
	
	// Get and set a 32bit integer type
	public int getInt( int offset ){

		// Little Endian (LSB first)
		return (data[offset] | (data[offset+1] << 8) | (data[offset+2] << 16) | (data[offset+3] << 24 ));
		// Big Endian (MSB first)
		//return ((data[offset] <<24) | (data[offset+1] << 16) | (data[offset+2] << 8) | (data[offset+3]));
	}
	
	public void setInt( int value, int offset ){
		StructHelpers.setInt( value, data, offset );
		return;
	}
	
	
		
	// Get and set a 32bit long integer type (SAME AS INT for now)
	public int getLong32( int offset ){
		return StructHelpers.getInt( data, offset );
	}
	
	public void setLong32( int value, int offset ){
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
	public int getShort( int offset ){
		return StructHelpers.getShort(data, offset);
	}
	
	public void setShort( int value, int offset ){
		StructHelpers.setShort(value, data, offset);
		return;
	}

	
	
	
	// Get and set a 16 bit unsigned short integer type
	public int getUShort( int offset ){
		return StructHelpers.getUShort(data, offset);
	}
	
	public void setUShort( int value, int offset ){
		StructHelpers.setUShort( value, data, offset );
		return;
	}
	
	
	
	// Get and set string fields
	public String getString( int offset, int len ){
		return StructHelpers.getString( data, offset, len );
	}
	
	public void setString( String string, int offset, int len ){
		StructHelpers.setStringPadded( string, data, offset, len );
		return;
	}

	public void setStringPadded( String string, int offset, int len ){
		StructHelpers.setStringPadded(string, data, offset, len);
		return;
	}

	public void setStringNoPad( String string, int offset, int len ){
		StructHelpers.setStringNoPad(string, data, offset, len);
		return;
	}


	// Validity byte methods
	// from validity.h  -->>  V_INVALID = 1, V_VALID = 2, V_HIDDEN = 3
	public boolean isValid( int offset ){
		return ( Validity.get( (int) data[offset] ) == Validity.VALID );
	}
	public boolean isInvalid( int offset ){
		return ( Validity.get( (int) data[offset] ) == Validity.INVALID );
	}
	public boolean isHidden( int offset ){
		return ( Validity.get( (int) data[offset] ) == Validity.HIDDEN );
	}

	public Validity getValidity( int offset ){
		return Validity.get( (int) data[offset] );
	}
	public void setValidity( Validity validity, int offset ){
		data[offset] = (byte)( validity.getCode() & 0xff );
		return;
	}

	public void setValid( int offset ){
		data[offset] = (byte)( Validity.VALID.getCode() & 0xff );
		return;
	}
	public void setInvalid( int offset ){
		data[offset] = (byte)( Validity.INVALID.getCode() & 0xff );
		return;
	}
	public void setHidden( int offset ){
		data[offset] = (byte)( Validity.HIDDEN.getCode() & 0xff );
		return;
	}

	
	
	
	// Methods to get or set data 
	public byte[] getData(){
		return data;
	}
	
	
}

/**/

