package usrlib;

/**
 * <p>Title: Rec</p>
 *
 * <p>Description: Record number object.  Basically a wrapper around a 32 bit int record number.</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */
public class Rec {

	// fields
    private int rec;

    
    // Constructors
    
    /**
     * <p>Rec()</p>
     *
     * <p>Desc: Constructor - Record number object.  Basically a wrapper around a 32 bit int record number.</p>
     *
     * @param none
     */
    public Rec () { new Rec( 0 ); }
    
    /**
     * <p>Rec( int )</p>
     *
     * <p>Desc: Constructor - Record number object.  Basically a wrapper around a 32 bit int record number.</p>
     *
     * @param int rec		Record number
     */
    
    public Rec( int rec ) {
        this.rec = rec;
    }
    

    
    /**
     * <p>fromInt( byte[] )</p>
     *
     * <p>Desc: Get record number from 32 bit int in 4 bytes[] of data and return Rec object.  Used to read PAL/MED RECORD data type.</p>
     *
     * @param byte[] data		32 bit (4 byte[]) record number in disk buffer
     * @return Rec				record number object
     */
    
    public static Rec fromInt( byte[] data ) { return Rec.fromInt( data, 0 ); }
    
    /**
     * <p>fromInt( byte[], offset )</p>
     *
     * <p>Desc: Get record number from 32 bit int in 4 bytes[] of data and return Rec object.   Used to read PAL/MED RECORD data type.</p>
     *
     * @param byte[] data		32 bit (4 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return Rec				record number object
     */

	public static Rec fromInt( byte[] data, int offset ){
		
		int	rec;		// record number

		// Little Endian (LSB first)
		rec = ((data[offset+0]&0xff) | ((data[offset+1]&0xff) << 8) | ((data[offset+2]&0xff) << 16) | ((data[offset+3]&0xff) << 24 ));
		// Big Endian (MSB first)
		//rec = (((data[0]&0xff) <<24) | ((data[offset+1]&0xff) << 16) | ((data[offset+2]&0xff) << 8) | (data[offset+3]&0xff));

		return new Rec( rec );
	}

    
    /**
     * <p>fromShort( byte[] )</p>
     *
     * <p>Desc: Get record number from 16 bit int in 2 bytes[] of data and return Rec object.  Used to read PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @return Rec				record number object
     */
    
    public static Rec fromShort( byte[] data ) { return Rec.fromShort( data, 0 ); }
    
    /**
     * <p>fromShort( byte[], offset )</p>
     *
     * <p>Desc: Get record number from 16 bit int in 2 bytes[] of data and return Rec object.  Used to read PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return Rec				record number object
     */

	public static Rec fromShort( byte[] data, int offset ){
		
		int	rec;		// record number

		// Little Endian (LSB first)
		rec = ((data[offset]&0xff) | ((data[offset+1]&0xff) << 8));
		// Big Endian (MSB first)
		//rec = (((data[offset]&0xff) << 8) | ((data[offset+1]&0xff));

		return new Rec( rec );
	}

	
	
	
	
    /**
     * <p>fromUShort( byte[] )</p>
     *
     * <p>Desc: Get record number from 16 bit unsigned int in 2 bytes[] of data and return Rec object.  Used to read PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @return Rec				record number object
     */
    
    public static Rec fromUShort( byte[] data ) { return Rec.fromUShort( data, 0 ); }
    
    /**
     * <p>fromUShort( byte[], offset )</p>
     *
     * <p>Desc: Get record number from 16 bit unsigned int in 2 bytes[] of data and return Rec object.  Used to read PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return Rec				record number object
     */

	public static Rec fromUShort( byte[] data, int offset ){
		
		int	rec;		// record number

		// Little Endian (LSB first)
		rec = ((data[offset]&0xff) | ((data[offset+1]&0xff) << 8));
		// Big Endian (MSB first)
		//rec = (((data[offset]&0xff) << 8) | ((data[offset+1]&0xff));

		return new Rec( rec );
	}

	
	
	
	
    /**
     * <p>toInt( data )</p>
     *
     * <p>Desc: Set record number in 4 byte[] data field.  Used to set PAL/MED RECORD data type.</p>
     *
     * @param byte[] data		32 bit (4 byte[]) record number in disk buffer
     * @return none
     */
  
	public void toInt( byte[] data ){ toInt( data, 0 ); }
	
    /**
     * <p>toInt( data, offset )</p>
     *
     * <p>Desc: Set record number in 4 byte[] data field.  Used to set PAL/MED RECORD data type.</p>
     *
     * @param byte[] data		32 bit (4 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return none
     */
  
	public void toInt( byte[] data, int offset ){
		
		int rec = this.getRec();
		
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & rec );
		data[offset+1] = (byte)(0xff & (rec>>8));
		data[offset+2] = (byte)(0xff & (rec>>16));
		data[offset+3] = (byte)(0xff & (rec>>24));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (rec>>24));
		//data[offset+1] = (byte)(0xff & (rec>>16));
		//data[offset+2] = (byte)(0xff & (rec>>8));
		//data[offset+3] = (byte)(0xff & (rec));
		return;
	}

	
	
	
    /**
     * <p>toShort( data )</p>
     *
     * <p>Desc: Set record number in 2 byte[] data field.  Used to set PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @return none
     */
    
	public void toShort( byte[] data ){ toShort( data, 0 ); }
	
    /**
     * <p>toShort( data, offset )</p>
     *
     * <p>Desc: Set record number in 2 byte[] data field.  Used to set PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return none
     */
  
	public void toShort( byte[] data, int offset ){
		
		int rec = this.getRec();
		
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & rec );
		data[offset+1] = (byte)(0xff & (rec>>8));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (rec>>8));
		//data[offset+1] = (byte)(0xff & (rec));
		return;
	}

    
	
    /**
     * <p>toUShort( data )</p>
     *
     * <p>Desc: Set unsigned short in 2 byte[] data field.  Used to set PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @return none
     */
    
	public void toUShort( byte[] data ){ toUShort( data, 0 ); }
	
    /**
     * <p>toUShort( data, offset )</p>
     *
     * <p>Desc: Set unsigned short in 2 byte[] data field.  Used to set PAL/MED RECSH data type.</p>
     *
     * @param byte[] data		16 bit (2 byte[]) record number in disk buffer
     * @param int offset		offset of data in buffer 
     * @return none
     */
  
	public void toUShort( byte[] data, int offset ){
		
		int rec = this.getRec();
		
		// Little Endian (LSB first)
		data[offset] = (byte)(0xff & rec );
		data[offset+1] = (byte)(0xff & (rec>>8));
		// Big Endian (LSB first)
		//data[offset] = (byte)(0xff & (rec>>8));
		//data[offset+1] = (byte)(0xff & (rec));
		return;
	}

    
	
    /**
     * <p>setRec( int )</p>
     *
     * <p>Desc: set int record number.</p>
     *
     * @param int rec		Record number
     */

    public Rec setRec( int rec ) {
        this.rec = rec;
        return ( this );
    }

    /**
     * <p>getRec()</p>
     *
     * <p>Desc: get int record number.</p>
     *
     * @param none
     * @return int		Record number as int
     */
    
    public int getRec() { return (int) rec; }

    /**
     * <p>getRecLong()</p>
     *
     * <p>Desc: get long record number.</p>
     *
     * @param none
     * @return long		Record number as long int
     */

    public long getRecLong() { return (long) rec; }
    
    /**
     * <p>getRecInt()</p>
     *
     * <p>Desc: get int record number.</p>
     *
     * @param none
     * @return int		Record number as int
     */

    public int getRecInt(){ return (int) rec; }

    /**
     * <p>increment()</p>
     *
     * <p>Desc: Increment record number.</p>
     *
     * @param none
     * @return Rec		Record number object.
     */
    
    public Rec increment() { 
        ++ this.rec;
        return ( this );
    }
    
    
    /**
     * <p>increment()</p>
     *
     * <p>Desc: Increment record number by an amount (positive or negative).</p>
     *
     * @param int amount
     * @return Rec		Record number object.
     */
    
    public Rec increment( int amount ) { 
        this.rec += amount;
        return ( this );
    }
    

    /**
     * <p>toString()</p>
     *
     * <p>Desc: Make String for record number.</p>
     *
     * @param none
     * @return String		Record number string.
     */
    
    public String toString() { return (  String.valueOf( this.rec )); }
    
    
    public boolean isValid(){
    	return ( this.getRec() > 1 );
    }
    
    public static boolean isValid( Rec rec ){
    	return (( rec != null ) && rec.isValid());
    }
    
    
    /**
     * equals()
     * 
     * Do two Rec objects have the same numeric value stored in them?
     * 
     * @param rec
     * @return
     */
    public boolean equals( Rec rec ){
    	//System.out.println( "IN Rec.equals() this.rec=" + this.rec + "  rec.rec=" + rec.rec + ".");
    	if (( rec == null )) return false;
    	return (( this.rec == rec.rec ));
    }
    public boolean equals( Object rec ){
    	//System.out.println( "IN Rec.equalsObj() this.rec=" + this.rec + "  rec.rec=" + ((Rec)rec).rec + ".");
    	if (( rec == null )) return false;
    	return (( this.rec == ((Rec)rec).rec ));
    }
}


/**/

