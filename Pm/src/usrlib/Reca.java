package usrlib;


/**
 * <p>Title: Reca</p>
 *
 * <p>Description: Annualized record number object.  Basically a wrapper around a 32 bit int subdivided into 1 byte year and 3 byte record.</p>
 * 
 * <P>On intel style (little endian) machines, the year is the most significant byte (the fourth byte) of a 4 byte data[] struct (was long int).
 * The record number is kept in the first three bytes, little end first.</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */

public class Reca {
	
	// fields
	int	year;		// year
	int	rec;		// record number
	
	
	
	// Constructors
	
	/**
	 * <p>Reca()</p>
	 * 
	 * <p>This CONSTRUCTOR creates a new blank Reca object with zeros for year and rec.</p>
	 * 
	 * @param none
	 */
	
	public Reca() { new Reca( 0, 0 ); }

	/**
	 * <p>Reca()</p>
	 * 
	 * <p>This CONSTRUCTOR creates a new Reca object with the given volume and rec.</p>
	 * 
	 * @param int vol		annualized file year
	 * @param int rec		record number
	 */

	public Reca( int vol, int rec ) {
		this.year = vol % 100;
		this.rec = rec;
	}

	
	/**
	 * <p>Reca()</p>
	 * 
	 * <p>This CONSTRUCTOR creates a new Reca object with the given volume and rec.</p>
	 * 
	 * @param int vol		annualized file year
	 * @param int rec		record number
	 */

	public Reca( int vol, Rec rec ) {
		this.year = vol % 100;
		this.rec = rec.getRec();
	}

	
	/**
	 * <p>setNull()</p>
	 * 
	 * <p>This method zeros out a Reca object with zeros for year and rec.</p>
	 * 
	 * @param int year		annualized file year
	 * @param int rec		record number
	 */

	public Reca setNull() {
		this.year = 0;
		this.rec = 0;
		return this;
	}

	
	/**
	 * <p>fromReca()</p>
	 * 
	 * <p>This static method reads a RECA field from a byte[] data struct and creates a new Reca object.</p>
	 * 
	 * @param byte[] data		byte data struct
	 * @return Reca			new Reca object
	 */
	
	public static Reca fromReca( byte[] data ) { return ( fromReca( data, 0 )); }

	/**
	 * <p>fromReca()</p>
	 * 
	 * <p>This static method reads a RECA field from a byte[] data struct and creates a new Reca object.</p>
	 * 
	 * @param byte[] data		byte data struct
	 * @param int offset		offset of start in byte data array
	 * @return Reca			new Reca object
	 */

	public static Reca fromReca( byte[] data, int offset ){
		
		// Little Endian (LSB first)
		int year = (int) data[offset+3];
		int rec = (((data[offset+0] )& 0x00ff) | ((data[offset+1] << 8)& 0x00ff00) | ((data[offset+2] << 16 )& 0x00ff0000));
		// Big Endian (MSB first)
		//year = (int) data[0];
		//rec = ((data[offset+1] << 16) | (data[offset+2] << 8) | (data[offset+3]));

		return new Reca( year, rec );
	}
	
	

	/**
	 * <p>toReca()</p>
	 * 
	 * <p>This method places a Reca object's data into a RECA field in a byte[] data struct.</p>
	 * 
	 * @param byte[] data		byte data struct
	 * @return none
	 */

	public void toReca( byte[] data ) { toReca( data, 0 ); }
	
	/**
	 * <p>toReca()</p>
	 * 
	 * <p>This method places a Reca object's data into a RECA field in a byte[] data struct.</p>
	 * 
	 * @param byte[] data		byte data struct
	 * @param int offset		offset of start in byte data array
	 * @return none
	 */

	public void toReca( byte[] data, int offset ){
		
		// Little Endian (LSB first)
		data[offset+3] = (byte)( this.year & 0xff );
		// Big Endian (MSB first)
		//data[offset] = (byte)( this.year & 0xff );
		
		// Little Endian (LSB first)
		data[offset+0] = (byte)(0xff & this.rec);
		data[offset+1] = (byte)(0xff & (this.rec>>8));
		data[offset+2] = (byte)(0xff & (this.rec>>16));
		// Big Endian (LSB first)
		//data[offset+1] = (byte)(0xff & (this.rec>>16));
		//data[offset+2] = (byte)(0xff & (this.rec>>8));
		//data[offset+3] = (byte)(0xff & (this.rec));

		return;
	}
	
	
	
	/**
	 * <p>setRec()</p>
	 * 
	 * <p>This method sets a Reca object's rec (record number) field.</p>
	 * 
	 * @param int rec			record number
	 * @return none
	 */

	public void setRec( int rec ) { this.rec = rec; }
	
	/**
	 * <p>setYear()</p>
	 * 
	 * <p>This method sets a Reca object's year field.</p>
	 * 
	 * @param int year			year
	 * @return none
	 */

	public void setYear( int year ) { this.year = year % 100; }
	
	

	
	/**
	 * <p>getRec()</p>
	 * 
	 * <p>This method returns a Reca object's rec (record number) field.</p>
	 * 
	 * @param none
	 * @return int rec			record number
	 */

	public int getRec() { return ( rec ); }

	/**
	 * <p>getYear()</p>
	 * 
	 * <p>This method returns a Reca object's year field.</p>
	 * 
	 * @param none
	 * @return int year			year
	 */
	
	public int getYear() { return ( year ); }
	
	
	/**
	 * <p>toString()</p>
	 * 
	 * <p>This method returns a string representation of the Reca object's fields.</p>
	 * 
	 * @param none
	 * @return String		string representation as yy-######
	 */
	
	public String toString() { return ( String.format( "%02d-%06d", this.year, this.rec )); }
	

	
	
	
	public static Reca fromString( String s ){
		
		if (( s.length() < 2 ) || ( s.indexOf( '-' ) < 1 )) return null;
		
		int rec;
		int vol;
		
		String[] q = s.split( "-" );
		vol = EditHelpers.parseInt( q[0] );
		if ((( vol < 0 ) || ( vol > 99 )) || ((( vol == 0 ) && ! q[0].equals( "00" )))) return null;
		rec = EditHelpers.parseInt( q[1] );
		if ( rec < 2 ) return null;
		//System.out.println( "Reca.fromString: vol=" + vol + " rec=" + rec );
		return new Reca( vol, rec );
	}
	
	
    /**
     * equals()
     * 
     * Do two Reca objects have the same numeric values stored in them?
     * 
     * @param rec
     * @return
     */
	public boolean equals( Reca reca ){
		return (( reca != null ) && ( this.year == reca.year ) && ( this.rec == reca.rec ));
	}

    public boolean equals( Object reca ){
    	return (( this.year == ((Reca)reca).year ) && ( this.rec == ((Reca)reca).rec ));
    }

	
	// isValid
	public boolean isValid(){
		return (( this.year >= 0 ) && ( this.year < 100 ) && ( this.rec > 1 ));
	}

	// static version of isValid() that gracefully handles null cases
	public static boolean isValid( Reca reca ){
		return (( reca != null ) && reca.isValid());
	}
	
	
	
	// get volume assuming today's year
	public static int todayVol(){
		return ( Date.today().getYear() % 100 );
	}
	
}


/**/

