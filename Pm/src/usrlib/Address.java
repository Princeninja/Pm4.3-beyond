package usrlib;


/**
 * <p>Title: PAL/MED Address Object</p>
 *
 * <p>Description: An Address object to implement palmed's ADDRESS Struct and
 *                 associated functions.</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */
public class Address {
	
	// Fields
	private String street;
	private String line2;
	private String city;
	private String state;
	private String zip_code;
	private String home_ph;
	private String work_ph;
	private String unused;
	
	
	
	// Default CONSTRUCTOR
	
	public Address(){
		setAddressFields( "", "", "", "", "", "", "", "" );
	}

	public Address( String street, String line2, String city, String state, String zip_code, String home_ph, String work_ph, String unused ){
		setAddressFields( street, line2, city, state, zip_code, home_ph, work_ph, unused );
	}

    // CONSTRUCTOR that takes byte[] data
	
	public Address( byte[] data ){ fromData( data, 0 ); }	
	public Address( byte[] data, int offset ){ fromData( data, offset ); }
	
	

	
	// Set Address fields - utility function primarily for constructors
	public void setAddressFields( String street, String line2, String city, String state, String zip_code, String home_ph, String work_ph, String unused ){
        setStreet( street );
        setLine2( line2 );
        setCity( city );
        setState( state );
        setZip_code( zip_code );
        setHome_ph( home_ph );
        setWork_ph( work_ph );
        setUnused( unused );
    }

    
    // GETTERS and SETTERS
    
    public String getStreet() { return street; }
	public void setStreet(String street) { this.street = street.trim(); }

	public String getLine2() { return line2; }
	public void setLine2(String line2) { this.line2 = line2.trim(); }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city.trim(); }

	public String getState() { return state; }
	public void setState(String state) { this.state = state.trim().toUpperCase(); }

	public String getZip_code() { return zip_code; }
	// get first 5 digits of zip code
	public String getZip5() { return ( zip_code.length() > 5 ) ? zip_code.substring( 0, 5 ): zip_code; }
	// get last 4 digits of 9 digit zip code
	public String getZipPlus4() { return ( zip_code.length() > 5 ) ? zip_code.substring( 5, zip_code.length() ): ""; }
	public void setZip_code(String zip_code) { this.zip_code = zip_code.trim(); }

	public String getHome_ph() { return home_ph; }
	public void setHome_ph(String home_ph) { this.home_ph = home_ph.trim(); }

	public String getWork_ph() { return work_ph; }
	public void setWork_ph(String work_ph) { this.work_ph = work_ph.trim(); }

	public String getUnused() { return unused; }
	public void setUnused(String unused) { this.unused = unused.trim(); }
	
	
	
	
	// Returns record length (in file)
    public int getRecordLength(){ return ( 88 ); }
    
    
    /**
     * getPrintableAddress()
     *
     *  This method returns a String for a printable address line according
     *  to the line number passed as an argument.
     *
     * @param  line     // the address line to print
     * 					//   usually line 1 is the street, line 2 is the extra line
     * 					//   and line 3 is the city, state  zip_code
     * 
     * @return Returns String address line as per above
     */
     public String getPrintableAddress( int line ) {

        switch ( line ){
       	
        case 1:
        	return ( this.getStreet());
        	
        case 2:
            return ( this.getLine2());

        case 3:
            return ( this.getCity() + ", " + this.getState() + "  " + this.getZip_code());

        case 0:
        default:
        	return ( this.getStreet() + ", " + this.getLine2() + ", " + this.getCity() + ", " + this.getState() + "  " + this.getZip_code());
        case 4:
           	return ( this.getStreet() + "^" + this.getLine2() + "^" + this.getCity() + "^" + this.getState() + "^" + this.getZip_code());
       
        }
    }
     
     


     /**
      * toData() - set an Address into a data struct
      * 
      * @param byte[] data
      * @param int offset
      * @return none
      * 
      */
     public void toData( byte[] data, int offset ){
    	 
    	StructHelpers.setStringPadded( getStreet(), data, offset + 0, 24 );
    	StructHelpers.setStringPadded( getLine2(), data, offset + 24, 24 );
    	StructHelpers.setStringPadded( getCity(), data, offset + 48, 20 );
    	StructHelpers.setStringPadded( getState(), data, offset + 68, 2 );
    	
    	Decoders.toBCDZipCode( zip_code, data, offset + 70 );
    	Decoders.toBCDPhone( home_ph, data, offset + 75 );
    	Decoders.toBCDPhone( work_ph, data, offset + 80 );    	 
    	return;
     }

     
     


	
     /**
      * fromData() - set an Address into a data struct
      * 
      * @param byte[] data
      * @param int offset
      * @return none
      * 
      */
     public void fromData( byte[] data, int offset ){
    	 
    	//StructHelpers.setStringPadded( getStreet(), data, offset + 0, 24 );
    	//StructHelpers.setStringPadded( getLine2(), data, offset + 24, 24 );
    	//StructHelpers.setStringPadded( getCity(), data, offset + 48, 20 );
    	//StructHelpers.setStringPadded( getState(), data, offset + 68, 2 );
    	    	
		setStreet( new String( data, 0 + offset, 24 ));
		setLine2( new String( data, 24 + offset, 24 ));
		setCity( new String( data, 48 + offset, 20 ));
		setState( new String( data, 68 + offset, 2 ));
		
		setZip_code( Decoders.fromBCDZipCode( data, 70 + offset ));
		setHome_ph( Decoders.fromBCDPhone( data, 75 + offset ));
		setWork_ph( Decoders.fromBCDPhone( data, 80 + offset ));
		
		setUnused( new String( data, 0 + 85, 3 ));

    	return;
     }

     
     


	
	
    /**
     * compare()
     *
     *  This function compares two addresses and returns 'true' if identical.
     *
     * @param  address2     // the addresses to compare to
     * @return Returns boolean true if match or false if no match
     */
    public boolean compare( Address address2 ) {

        // System.out.println( "comparing last name, length = " + LastName.length() );
        if (( this.getStreet().equals( address2.getStreet()))
        	&& ( this.getLine2().equals( address2.getLine2()))
        	&& ( this.getLine2().equals( address2.getLine2()))
        	&& ( this.getLine2().equals( address2.getLine2()))
        	&& ( this.getLine2().equals( address2.getLine2()))
        	&& ( this.getLine2().equals( address2.getLine2()))
        	&& ( this.getLine2().equals( address2.getLine2()))){
        	
        	return true;
        }

        return false;
       }
    
    public boolean compareStrict( Address address2 ) { return compare( address2 ); }
    


}

/**/

