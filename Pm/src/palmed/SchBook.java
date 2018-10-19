package palmed;


import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;




/*  Schedule Book  */

//typedef struct {
//
// 0	char    abbr[10];               /*  abbreviation  */
// 10	char    name[40];               /*  schedule book name  */
//
// 50	unsigned short    flags;        /*  flags  */
//
// 52	char    FillSlots;              /*  fill apmt cont slots? Y/N field  */
// 53	char    unused[45];             /*  unused field  */
// 98	unsigned short	  MonthsAhead;  /*  number of months ahead allowed to post*/
// 100	DATE    FirstDate;		/*  first day to post appointments */
// 104	DATE    LastDate;               /*  last  day to post appointments */
// 108	RECORD  DefaultProviderRec;     /*  default provider record number  */
// 112	RECORD  DefaultResourceRec;     /*  default resource record number  */
// 116	char    password[10];           /*  access password  */
// 126	char    TimIncrement;           /*  time increments  */
// 127	char    valid;                  /*  validity byte  */
//
//	} SCHBK;
//
//      sizecheck( 128, sizeof( SCHBK ), "SCHBK" );





public class SchBook {
	
	private static final String filename = "sch%02d.sch";
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
	
	public SchBook(){
		super();
		allocateBuffer();
				this.rec = new Rec( 0 );
	}
	
/*	public SchBook( String user, String password, Name name ){
		super();
		allocateBuffer();
		this.userRec = new Rec( 0 );
		setUser( user );
		setPassword( password );
		setName( name );
	}
*/
	public SchBook( Rec rec ){
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

   
    
    

    // char password[10];		// schedule book password, offset 116
	
    /**
     * getPassword()
     *
     * Gets the Password.
     * 
     * @param none
     * @return String
     */
    public String getPassword() {
        return new String( dataStruct, 116, 10 ).trim();
    }

    /**
     * setPassword()
     *
     * Sets the Password.
     * 
     * @param String
     * @return void
     */
    public void setPassword( String s ) {
    	// enforce uppercase
        StructHelpers.setStringPadded( s, dataStruct, 116, 10 );
    }

    /**
     * getPassword()
     *
     * Static version reads book's record and gets the Password.
     * 
     * @param rec
     * @return String
     */
    public static String getPassword( Rec rec ) {
    	String str = "";
    	if (( rec != null ) && ( rec.getRec() > 1 ))
    		str = (new SchBook( rec )).getAbbr();
        return str;
    }

   
    
    

    

	
	
	
    
    
    // DATE firstDate;			// first date to post appointments to, offset 100   
    
    /**
     * getFirstDate()
     * 
     * Get first date to post appointments to from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getFirstDate(){
       	return StructHelpers.getDate( dataStruct, 100 );
    }

    /**
     * setFirstDate()
     * 
     * Set first date to post appointments to in dataStruct.
     * 
     * @param usrlib.Date
     * @return void
     */
    public void setFirstDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 100 );
        return;
    }

   
    



    
    // DATE lastDate;			// last date to post appointments to, offset 104    
    
    /**
     * getLastDate()
     * 
     * Get last date to post appointments to from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getLastDate(){
       	return StructHelpers.getDate( dataStruct, 104 );
    }

    /**
     * setLastDate()
     * 
     * Set last date to post appointments to in dataStruct.
     * 
     * @param usrlib.Date
     * @return void
     */
    public void setLastDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 104 );
        return;
    }

   
    



    
    
    


    // unsigned short monthsAhead;			// months ahead to post appointments, offset 98    
    
    /**
     * getMonthsAhead()
     * 
     * Get MonthsAhead from dataStruct.
     * 
     * @param none
     * @return short
     */
    public int getMonthsAhead(){
       	
       	return StructHelpers.getShort( dataStruct, 98 );
    }

    /**
     * setMonthsAhead()
     * 
     * Set MonthsAhead in dataStruct.
     * 
     * @param int months
     * @return void
     */
    public void setMonthsAhead( int months ){
    	StructHelpers.setShort( months, dataStruct, 98 );
        return;
    }


    // unsigned short timeIncrement;			// schedule book time increments to display, offset 98    
    
    /**
     * getTimeIncrement()
     * 
     * Get TimeIncrement from dataStruct.
     * 
     * @param none
     * @return short
     */
    public int getTimeIncrement(){
       	
       	return (int)( StructHelpers.getByte( dataStruct, 126 ) & 0xff );
    }

    /**
     * setTimeIncrement()
     * 
     * Set TimeIncrement in dataStruct.
     * 
     * @param int months
     * @return void
     */
    public void setTimeIncrement( int mins ){
    	StructHelpers.setByte( (byte)(mins & 0xff), dataStruct, 126 );
        return;
    }


    

    
    
    
   // Rec defaultProvRec;			// default provider record, offset 108   
    
    /**
     * getDefaultProvRec()
     * 
     * Get default provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getDefaultProvRec(){
       	return Rec.fromInt( dataStruct, 108 );
    }

    /**
     * setDefaultProvRec()
     * 
     * Set default provider record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setDefaultProvRec( Rec rec ){
        if ( Rec.isValid( rec )) rec.toInt( dataStruct, 108 );
        return;
    }


    
    
    
   // Rec defaultResRec;			// default resource record, offset 112   
    
    /**
     * getDefaultResourceRec()
     * 
     * Get default resource record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getDefaultResourceRec(){
       	return Rec.fromInt( dataStruct, 112 );
    }

    /**
     * setDefaultResourceRec()
     * 
     * Set default resource record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setDefaultResourceRec( Rec rec ){
        if ( Rec.isValid( rec )) rec.toInt( dataStruct, 112 );
        return;
    }






    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[127] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[127] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[127] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[127] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[127] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[127] = (byte)( valid.getCode() & 0xff ); }
    
    
    

    

    
	
	// Validate a password.  Returns 'true' if password matches.
	//
	
/*	public boolean validatePassword( String testPassword ){

		return ( testPassword.trim().equals(this.getPassword()) ? true: false );
	}
*/	

	
	
	
	
	// Read info from disk file given a userRec
	//
	
	public void read( Rec rec ){

    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schBook.read() bad rec" );   	
		this.rec = rec;		
    	RandomFile.readRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
	}


	
	
	// Write info to disk file given a userRec
	//
	
	public void write( Rec rec ){
		
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schBook.read() bad rec" );					
    	this.rec = RandomFile.writeRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
	}
	
	
	
    public Rec newRec(){
    	   	
    	Rec rec = RandomFile.writeRec( null, dataStruct, Pm.getSchPath(), filename, getRecordLength());
    	return rec;
    }
    
    
    
    
	// open
    public static SchBook open(){
    	
    	// create new book object
    	SchBook b = new SchBook();

    	// open file, read maxrec, and set current rec to null
		b.file = RandomFile.open( Pm.getSchPath(), filename, getRecordLength(), RandomFile.Mode.READONLY );
		b.maxrec = new Rec( b.file.getMaxRecord());
		b.rec = null;
		return b;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( this.file == null ) SystemHelpers.seriousError( "schBook.getNext() file not open" );

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
		SchBook book = new SchBook();
		
		// Open usermap.ovd file
		RandomFile file = new RandomFile( Pm.getSchPath(), filename, SchBook.getRecordLength(), RandomFile.Mode.READONLY );
		
        // get max record
        maxrec = file.getMaxRecord();
        System.out.println( "maxrec = " + maxrec );
        System.out.println( "searching for " + abbr );

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( book.dataStruct, rec );
            if ( ! book.isValid()) continue;
            
            if ( abbr.equals( book.getAbbr())){
            	
                System.out.println( "found " + book.getAbbr() + " rec=" + rec );
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


