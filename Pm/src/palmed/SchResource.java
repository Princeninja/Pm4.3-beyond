package palmed;



import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;






/*  scheduler resource  */
//
//typedef struct {
//
// 0	char    abbr[10];               /*  resource abbreviation */
// 10	char    name[40];               /*  resource name  */
//
// 50	unsigned short minutes;         /*  minimum time allocation  */
// 52	unsigned short concurrent;      /*  number of concurrent allocations  */
// 54	unsigned short flags;           /*  flags  */
//
// 56	char    unused[23];             /*  unused area  */
// 79	char    valid;                  /*  validity byte  */
//
// 80	unsigned short utilization[2][12]; /*  resource utilization  */
//
//	} SCHRS;

//      sizecheck( 128, sizeof( SCHRS ), "SCHRS" );






public class SchResource {

	
	private static final String filename = "schrs.sch";
	private static final int recordLength = 128;
	
	private Rec rec = null;
	private Rec maxrec = null;
	private RandomFile file = null;
	
	
    public byte[] dataStruct;
    
    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    }
    
    

	
	//Constructors
	//
	
	public SchResource(){
		super();
		allocateBuffer();
				this.rec = new Rec( 0 );
	}
	
/*	public SchResource( String user, String password, Name name ){
		super();
		allocateBuffer();
		this.userRec = new Rec( 0 );
		setUser( user );
		setPassword( password );
		setName( name );
	}
*/
	public SchResource( Rec rec ){
		super();
		allocateBuffer();
		this.rec = new Rec( rec.getRec());
		read( rec );
	}
	
	
	
	
	
	
	
	
	
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recordLength];
    }

    public void setDataStruct(byte[] dataStruct){
        this.dataStruct = dataStruct;
    }

    public byte[] getDataStruct(){
        return dataStruct;
    }

    /**
     * getRecordLength
     */
    public static int getRecordLength(){
        return ( recordLength );
    }

    
    
    
    /**
     * getRec()
     * 
     * Get this entry's record number
     * 
     * @return Rec
     */
    public Rec getRec(){
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we iterate this table
    	return new Rec( this.rec.getRec());
    }
    
    
    
   
    
    
    /**
     * getAbbr()
     *
     * Gets the abbreviation.
     * 
     * @param none
     * @return String
     */
    public String getAbbr() {
        return new String( dataStruct, 0, 10 ).trim();
    }

    /**
     * setAbbr()
     *
     * Sets the abbreviation.
     * 
     * @param String
     * @return void
     */
    public void setAbbr( String s ) {
    	// enforce uppercase
        StructHelpers.setStringPadded( s.toUpperCase(), dataStruct, 0, 10 );
    }

    /**
     * getAbbr()
     *
     * Static version reads book's record and gets the abbreviation.
     * 
     * @param rec
     * @return String
     */
    public static String getAbbr( Rec rec ) {
    	String str = "";
    	if (( rec != null ) && ( rec.getRec() > 1 ))
    		str = (new SchBook( rec )).getAbbr();
        return str;
    }

   
    
    

	
	
    /**
     * getDesc()
     *
     * Gets the description.
     * 
     * @param none
     * @return String
     */
    public String getDesc() {
        return new String( dataStruct, 10, 40 ).trim();
    }

    /**
     * setDesc()
     *
     * Sets the description.
     * 
     * @param String
     * @return void
     */
    public void setDesc( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 10, 40 );
    }

    /**
     * getDesc()
     *
     * Static version reads book's record and gets the description.
     * 
     * @param rec
     * @return String
     */
    public static String getDesc( Rec rec ) {
    	String str = "";
    	if (( rec != null ) && ( rec.getRec() > 1 ))
    		str = (new SchBook( rec )).getDesc();
        return str;
    }

   
    
    

	
  
    

	
	
	

    // unsigned short minutes;			// resource blocked minutes, offset 50    
    
    /**
     * getMinutes()
     * 
     * Get resource blocked minutes from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getMinutes(){
       	
       	return StructHelpers.getShort( dataStruct, 50 );
    }

    /**
     * setMinutes()
     * 
     * Set resource blocked minutes in dataStruct.
     * 
     * @param int 
     * @return void
     */
    public void setMinutes( int min ){
    	StructHelpers.setShort( min, dataStruct, 50 );
        return;
    }


    
    // unsigned short concurrent;			// concurrent visits, offset 52  
    
    /**
     * getConcurrent()
     * 
     * Get concurrent visits from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getConcurrent(){
       	
       	return StructHelpers.getShort( dataStruct, 52 );
    }

    /**
     * setConcurrent()
     * 
     * Set Concurrent visits in dataStruct.
     * 
     * @param int 
     * @return void
     */
    public void setConcurrent( int num ){
    	StructHelpers.setShort( num, dataStruct, 52 );
        return;
    }


    
    
    
    // unsigned short flags;			// flags, offset 54  
    
    /**
     * getFlags()
     * 
     * Get flags from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getFlags(){
       	
       	return StructHelpers.getUShort( dataStruct, 54 );
    }

    /**
     * setFlags()
     * 
     * Set flags in dataStruct.
     * 
     * @param int flags
     * @return void
     */
    public void setFlags( int flags ){
    	StructHelpers.setUShort( flags, dataStruct, 54 );
        return;
    }


    




    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[79] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[79] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[79] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[79] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[79] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[79] = (byte)( valid.getCode() & 0xff ); }
    
    
    

    

    
	
	// Validate a password.  Returns 'true' if password matches.
	//
	
/*	public boolean validatePassword( String testPassword ){

		return ( testPassword.trim().equals(this.getPassword()) ? true: false );
	}
*/	

	
	
	
	
	// Read info from disk file given a userRec
	//
	
	public void read( Rec rec ){

    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schResource.read() bad rec" );   	
		this.rec = rec;		
    	RandomFile.readRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
	}


	
	
	// Write info to disk file given a userRec
	//
	
	public void write( Rec rec ){
		
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schResource.read() bad rec" );					
    	this.rec = RandomFile.writeRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
	}
	
	
	
    public Rec newRec(){
    	   	
    	Rec rec = RandomFile.writeRec( null, dataStruct, Pm.getSchPath(), filename, getRecordLength());
    	return rec;
    }
    
    
    
    
	// open
    public static SchResource open(){
    	
    	// create new book object
    	SchResource b = new SchResource();

    	// open file, read maxrec, and set current rec to null
		b.file = RandomFile.open( Pm.getSchPath(), filename, getRecordLength(), RandomFile.Mode.READONLY );
		b.maxrec = new Rec( b.file.getMaxRecord());
		b.rec = null;
		return b;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( this.file == null ) SystemHelpers.seriousError( "schResource.getNext() file not open" );

        // set initial record number
    	if ( this.rec == null ){
    		this.rec = new Rec( 2 );
    	} else if ( this.rec.getRec() < 2 ){
    		this.rec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		this.rec.increment();
    	}
    	
    	// is this past maxrec
    	if ( this.rec.getRec() > this.maxrec.getRec()) return false;
 
    	// read record
		file.getRecord( dataStruct, this.rec );
		return true;
    }	
    
        
        
    // close()
    public boolean close(){
    	if ( this.file != null ) this.file.close();
    	this.file = null;
    	this.rec = null;
    	this.maxrec = null;
    	return true;
    }

    
    
    



	


	
	
	/**
	 * search() - Find a schBook given an abbreviation
	 * 
	 * returns null Rec if not found
	 * 
	 * @param String abbr
	 * @return Rec
	 */
	public static Rec search( String abbr ){
				
		int	rec;					// record number
		int	maxrec;					// max record number
		boolean flgFound = false;	// flag that abbr has been found
		
		
		// Make sure abbr is all caps and trimmed
		abbr = abbr.trim().toUpperCase();

		// Create new user object
		SchResource res = new SchResource();
		
		// Open usermap.ovd file
		RandomFile file = new RandomFile( Pm.getSchPath(), filename, SchBook.getRecordLength(), RandomFile.Mode.READONLY );
		
        // get max record
        maxrec = file.getMaxRecord();
        System.out.println( "maxrec = " + maxrec );
        System.out.println( "searching for " + abbr );

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( res.dataStruct, rec );
            if ( ! res.isValid()) continue;
            
            if ( abbr.equals( res.getAbbr())){
            	
                System.out.println( "found " + res.getAbbr() + " rec=" + rec );
        		flgFound = true;
        		break;
            }
        }
                
        System.out.println( "loop finished, flgFound = " + flgFound );

        // close file
        file.close();
		
		return flgFound ? new Rec( rec ): null;
	}

}


