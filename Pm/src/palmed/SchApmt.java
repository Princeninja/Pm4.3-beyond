package palmed;



	import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.Par.Status;
import usrlib.Decoders;
import usrlib.Name;
import usrlib.RandomFile;
	import usrlib.Rec;
	import usrlib.StructHelpers;
import usrlib.Validity;




/*  appointment structure  */

//	#define SCHAPUN         0x0100          /*  appointment un-scheduled  */
//	#define SCHAPRE         0x0200          /*  appointment re-scheduled  */

//	typedef struct {
//
// 0		RECORD  patient;                /*  patient record number  */
// 4		RECORD  explain;                /*  explanation  */
//
// 8		DATE    date;                   /*  date of appointment  */
// 12		TIME    time;                   /*  time of appointment  */
//
// 14		unsigned short flags;           /*  flags  */
// 16		unsigned short resource[4];     /*  resource to schedule  */
// 24		unsigned short provider[4];     /*  provider to schedule  */
// 32		unsigned short minutes;         /*  # of minutes to schedule  */
// 34		unsigned short user;            /*  user scheduling appointment  */
//
// 36		NAME    name;                   /*  patient's name  */
// 100		unsigned char phone[5];         /*  patient's phone number  */
// 105		char    chartno[10];            /*  pt's chart number  */
//
// 115		DATE    pstdte;                 /*  date posted  */
// 119		TIME    psttim;                 /*  time posted  */
//
// 121		char	status;			/*  apmt status (missed, showed, etc.)*/
// 122		char	FirstAppnt;		/*  T/F  pt's first appointment  */
// 123		char    unused[35];             /*  unused field  */
// 158		char    BookNum;                /*  schedule book number  */
// 159		char    valid;                  /*  validity byte  */
//
//		} SCHAP;
//
//     sizecheck( 160, sizeof( SCHAP ), "SCHAP" );







	public class SchApmt {
		
		private static final String filename = "ap%02d%02d%02d.sch";
		private static final int recordLength = 160;
		
		private Rec rec = null;
		private Rec maxrec = null;
		private RandomFile file = null;
		
		
	    public byte[] dataStruct;
	    
		public enum Status {
			
			SHOW( "Showed", 'S' ),	
			NOSHOW( "No Show", 'N' ),
			RESCHEDULED( "Rescheduled", 'R' ),
			CANCELLED( "Cancelled", 'C' );
			
			private String label;
			private int code;

			private static final Map<Integer, Status> lookup = new HashMap<Integer,Status>();
			
			static {
				for ( Status r : EnumSet.allOf(Status.class))
					lookup.put(r.getCode(), r );
			}


			Status ( String label, int code ){
				this.label = label;
				this.code = code & 0xff;
			}
			
			public String getLabel(){ return this.label; }
			public int getCode(){ return this.code; }
			public static Status get( int code ){ return lookup.get( code & 0xff ); }

		}
	    

		
		//Constructors
		//
		
		public SchApmt(){
			super();
			allocateBuffer();
					this.rec = new Rec( 0 );
		}
		
	/*	public SchApmt( String user, String password, Name name ){
			super();
			allocateBuffer();
			this.userRec = new Rec( 0 );
			setUser( user );
			setPassword( password );
			setName( name );
		}
	*/
		public SchApmt( Rec rec ){
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
	    
	    
	    
	   
	    
	   // Rec patient;			// get patient record, offset 0   
	    
	    /**
	     * getPtRec()
	     * 
	     * Get patient record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getPtRec(){
	       	return Rec.fromInt( dataStruct, 0 );
	    }

	    /**
	     * setPtRec()
	     * 
	     * Set patient record in dataStruct.
	     * 
	     * @param Rec	
	     * @return void
	     */
	    public void setPtRec( Rec rec ){
	        if ( Rec.isValid( rec )) rec.toInt( dataStruct, 0 );
	        return;
	    }


	    
	    
	    

	    
	   // Rec explain;			// get explanation record, offset 4   
	    
	    /**
	     * getExpRec()
	     * 
	     * Get explanation record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getExpRec(){
	       	return Rec.fromInt( dataStruct, 4 );
	    }

	    /**
	     * setExpRec()
	     * 
	     * Set explanation record in dataStruct.
	     * 
	     * @param Rec	
	     * @return void
	     */
	    public void setExpRec( Rec rec ){
	        if ( Rec.isValid( rec )) rec.toInt( dataStruct, 4 );
	        return;
	    }


	    
	    
	    
	    // DATE Date;			// date, offset 8    
	    
	    /**
	     * getApmtDate()
	     * 
	     * Get appointment date from dataStruct.
	     * 
	     * @param none
	     * @return usrlib.Date
	     */
	    public usrlib.Date getApmtDate(){
	       	return StructHelpers.getDate( dataStruct, 8 );
	    }

	    /**
	     * setApmtDate()
	     * 
	     * Set appointment date in dataStruct.
	     * 
	     * @param usrlib.Date		date
	     * @return void
	     */
	    public void setApmtDate( usrlib.Date date ){
	        StructHelpers.setDate( date, dataStruct, 8 );
	        return;
	    }


	    
	    
	    

	    // TIME time;			// time, offset 12    
	    
	    /**
	     * getApmtTime()
	     * 
	     * Get appointment time from dataStruct.
	     * 
	     * @param none
	     * @return usrlib.Time
	     */
	    public usrlib.Time getApmtTime(){
	       	return StructHelpers.getTime( dataStruct, 12 );
	    }

	    /**
	     * setApmtTime()
	     * 
	     * Set appointment time in dataStruct.
	     * 
	     * @param usrlib.Time		time
	     * @return void
	     */
	    public void setApmtTime( usrlib.Time time ){
	        StructHelpers.setTime( time, dataStruct, 12 );
	        return;
	    }


	    
	    
	    

	    // unsigned short flags;			// flags, offset 14   
	    
	    /**
	     * getFlags()
	     * 
	     * Get flags from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getFlags(){
	       	
	       	return StructHelpers.getShort( dataStruct, 14 );
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
	    	StructHelpers.setShort( flags, dataStruct, 14 );
	        return;
	    }


	    
	    
	    
	    
	    
	   // unsigned short prov[4];			// get provider record, offset 16   
	    
	    /**
	     * getProvRec()
	     * 
	     * Get provider record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getProvRec( int num ){
	       	return Rec.fromUShort( dataStruct, 16 + ( 2 * num));
	    }

	    public Rec getProvRec(){
	    	return getProvRec( 0 );
	    }
	    
	    
	    /**
	     * setProvRec()
	     * 
	     * Set provider record in dataStruct.
	     * 
	     * @param Rec	
	     * @return void
	     */
	    public void setProvRec( int num, Rec rec ){
	        if ( Rec.isValid( rec )) rec.toUShort( dataStruct, 16 + ( 2 * num));
	        return;
	    }
	    
	    public void setProvRec( Rec rec ){
	    	setProvRec( 0, rec );
	    }


	    
	    
	    

	    
	   // unsigned short resource[4];			// get provider record, offset 24   
	    
	    /**
	     * getResRec()
	     * 
	     * Get resource record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getResRec( int num ){
	       	return Rec.fromShort( dataStruct, 24 + ( 2 * num));
	    }

	    public Rec getResRec(){
	    	return getResRec( 0 );
	    }
	    
	    
	    /**
	     * setResRec()
	     * 
	     * Set resource record in dataStruct.
	     * 
	     * @param Rec	
	     * @return void
	     */
	    public void setResRec( int num, Rec rec ){
	        if ( Rec.isValid( rec )) rec.toShort( dataStruct, 24 + ( 2 * num));
	        return;
	    }
	    
	    public void setResRec( Rec rec ){
	    	setResRec( 0, rec );
	    }


	    
	    
	    

	    
	    // unsigned short minutes;			// minutes, offset 32  
	    
	    /**
	     * getMinutes()
	     * 
	     * Get minutes from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getMinutes(){
	       	
	       	return StructHelpers.getShort( dataStruct, 32 );
	    }

	    /**
	     * setMinutes()
	     * 
	     * Set minutes in dataStruct.
	     * 
	     * @param int 
	     * @return void
	     */
	    public void setMinutes( int flags ){
	    	StructHelpers.setShort( flags, dataStruct, 32 );
	        return;
	    }


	    
	    
	    

	   // unsigned short User;			// get posting user record, offset 34   
	    
	    /**
	     * getUserRec()
	     * 
	     * Get posting user record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getUserRec(){
	       	return Rec.fromUShort( dataStruct, 34 );
	    }

	    /**
	     * setUserRec()
	     * 
	     * Set posting user record in dataStruct.
	     * 
	     * @param Rec	
	     * @return void
	     */
	    public void setUserRec( Rec rec ){
	        if ( Rec.isValid( rec )) rec.toUShort( dataStruct, 34 );
	        return;
	    }


	    
	    
	    

		
		// Name name;		// patient's name, offset 36
	    
	    /**
	     * getName()
	     *
	     * Gets the name field.
	     * 
	     * @param none
	     * @return String
	     */
	    public Name getName() {
	        return new Name().fromData( dataStruct, 36 );
	    }

	    /**
	     * setName()
	     *
	     * Sets the name field.
	     * 
	     * @param String
	     * @return void
	     */
	    public void setName( Name n ) {
	        n.toData( dataStruct, 36 );
	    }


	   
	    
	    
	    // char phone[5]		// patient's phone number, offset 100

	    /**
	     * getPhone()
	     *
	     * Gets the phone number.
	     * 
	     * @param none
	     * @return String
	     */
	    public String getPhone() {
	        return Decoders.fromBCDPhone( dataStruct, 100 );
	    }

	    /**
	     * setPhone()
	     *
	     * Sets the phone number.
	     * 
	     * @param String
	     * @return void
	     */
	    public void setPhone( String s ) {
	    	Decoders.toBCDPhone( s, dataStruct, 100 );
	    }


		
	  
	    

		
		
		
	    // char chartno[5]		// patient's phone number, offset 105

	    /**
	     * getChartNum()
	     *
	     * Gets the chart number.
	     * 
	     * @param none
	     * @return String
	     */
	    public String getChartNum() {
	        return new String( dataStruct, 105, 10 ).trim();
	    }

	    /**
	     * setChartNum()
	     *
	     * Sets the chart number.
	     * 
	     * @param String
	     * @return void
	     */
	    public void setChartNum( String s ) {
	    	// enforce uppercase
	        StructHelpers.setStringPadded( s.toUpperCase(), dataStruct, 105, 10 );
	    }


		
	  
	    

		
		
	    // DATE pstdte;			// posting date, offset 115   
	    
	    /**
	     * getPostDate()
	     * 
	     * Get posting date from dataStruct.
	     * 
	     * @param none
	     * @return usrlib.Date
	     */
	    public usrlib.Date getPostDate(){
	       	return StructHelpers.getDate( dataStruct, 115 );
	    }

	    /**
	     * setPostDate()
	     * 
	     * Set posting date in dataStruct.
	     * 
	     * @param usrlib.Date		date
	     * @return void
	     */
	    public void setPostDate( usrlib.Date date ){
	        StructHelpers.setDate( date, dataStruct, 115 );
	        return;
	    }


	    
	    
	    

	    // TIME time;			// time, offset 119    
	    
	    /**
	     * getPostTime()
	     * 
	     * Get posting time from dataStruct.
	     * 
	     * @param none
	     * @return usrlib.Time
	     */
	    public usrlib.Time getPostTime(){
	       	return StructHelpers.getTime( dataStruct, 119 );
	    }

	    /**
	     * setPostTime()
	     * 
	     * Set posting time in dataStruct.
	     * 
	     * @param usrlib.Time		time
	     * @return void
	     */
	    public void setPostTime( usrlib.Time time ){
	        StructHelpers.setTime( time, dataStruct, 119 );
	        return;
	    }


	    
	    
	    

		
	    
	    
	    
	    


	    // char status;			// appointment status (missed, showed, etc), offset 121  
	    
	    /**
	     * getStatus()
	     * 
	     * Get appointment status from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public Status getStatus(){
	       	
	       	return Status.get( dataStruct[121] );
	    }

	    /**
	     * setStatus()
	     * 
	     * Set appointment status in dataStruct.
	     * 
	     * @param int 
	     * @return void
	     */
	    public void setStatus( Status s ){
	    	dataStruct[121] = (byte) s.getCode();
	        return;
	    }


	    
	    
	    
	    // char FirstAppnt;			// first appointment for this patient (T/F), offset 122  
	    
	    /**
	     * getNewPt()
	     * 
	     * Get new/first appointment flag from dataStruct.
	     * 
	     * @param none
	     * @return boolean
	     */
	    public boolean getNewPt(){	       	
	       	return ( dataStruct[122] == 'T' );
	    }

	    /**
	     * setStatus()
	     * 
	     * Set new/first appointment flag in dataStruct.
	     * 
	     * @param boolean 
	     * @return void
	     */
	    public void setNewPt( boolean s ){
	    	dataStruct[122] = (byte) (s ? 'T': 'F');
	        return;
	    }


	    

	    
	    
	    // char bookNum;			// book number, offset 158 
	    
	    /**
	     * getBookNum()
	     * 
	     * Get book number from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getBookNum(){
	       	
	       	return StructHelpers.getByte( dataStruct, 158 );
	    }

	    /**
	     * setBookNum()
	     * 
	     * Set book number in dataStruct.
	     * 
	     * @param int 
	     * @return void
	     */
	    public void setBookNum( int num ){
	    	StructHelpers.setByte( num, dataStruct, 158 );
	        return;
	    }


	    
	    
	    


	    
	    /**
	     * isValid
	     */
	    public boolean isValid() { return ( Validity.get( (int) dataStruct[159] ) == Validity.VALID ); }
	    
	    /**
	     * setValid()
	     * 
	     */
	    public void setValid(){ dataStruct[159] = (byte)( Validity.VALID.getCode() & 0xff ); }
	    
	    /**
	     * setHidden()
	     * 
	     */
	    public void setHidden(){ dataStruct[159] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
	    
	    /**
	     * isHidden()
	     */
	    public boolean isHidden() { return ( Validity.get( (int) dataStruct[159] ) == Validity.HIDDEN ); }
	    
	    /**
	     * getValidityByte()
	     * 
	     */
	    public Validity getValidity(){ return Validity.get( (int) dataStruct[159] ); }
	    
	    /**
	     * setValidityByte()
	     * 
	     */
	    public void setValidity( Validity valid ){ dataStruct[159] = (byte)( valid.getCode() & 0xff ); }
	    
	    
	    

	    

	    
		
		// Validate a password.  Returns 'true' if password matches.
		//
		
	/*	public boolean validatePassword( String testPassword ){

			return ( testPassword.trim().equals(this.getPassword()) ? true: false );
		}
	*/	

		
		
		
		
		// Read info from disk file given a userRec
		//
		
		public void read( Rec rec ){

	    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schProvider.read() bad rec" );   	
			this.rec = rec;		
	    	RandomFile.readRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
		}


		
		
		// Write info to disk file given a userRec
		//
		
		public void write( Rec rec ){
			
	    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "schProvider.read() bad rec" );					
	    	this.rec = RandomFile.writeRec( rec, dataStruct, Pm.getSchPath(), filename, getRecordLength());
		}
		
		
		
	    public Rec newRec(){
	    	   	
	    	Rec rec = RandomFile.writeRec( null, dataStruct, Pm.getSchPath(), filename, getRecordLength());
	    	return rec;
	    }
	    
	    
	    
	    
		// open
	    public static SchApmt open( int book, int month, int year ){
	    	
	    	String fname = String.format( filename, book, month, year % 100 );
	    	// create new book object
	    	SchApmt b = new SchApmt();

	    	// open file, read maxrec, and set current rec to null
			b.file = RandomFile.open( Pm.getSchPath(), filename, getRecordLength(), RandomFile.Mode.READONLY );
			b.maxrec = new Rec( b.file.getMaxRecord());
			b.rec = null;
			return b;
	    }
	    
	    
	    
	    // getNext()
	    public boolean getNext(){
	        	
	        // is file open??
	        if ( this.file == null ) SystemHelpers.seriousError( "schApmt.getNext() file not open" );

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
		 * searchName() - Find an appointment given a patient name
		 * 
		 * returns null Rec if not found
		 * 
		 * @param String abbr
		 * @return Rec
		 */
		public static Rec searchName( Name name, int book, int month, int year ){
					
			int	rec;					// record number
			int	maxrec;					// max record number
			boolean flgFound = false;	// flag that abbr has been found
			
			

			// Create new user object
			SchApmt apmt = new SchApmt();
			
			// Open file
			String fname = String.format( filename, book, month, year % 100 );
			RandomFile file = new RandomFile( Pm.getSchPath(), fname, SchApmt.getRecordLength(), RandomFile.Mode.READONLY );
			
	        // get max record
	        maxrec = file.getMaxRecord();
	        System.out.println( "maxrec = " + maxrec );
	        System.out.println( "searching for " + name.getPrintableNameLFM());

	        // loop through all pt records
	        for ( rec = 2; rec <= maxrec; ++rec ){

	            file.getRecord( apmt.dataStruct, rec );
	            if ( ! apmt.isValid()) continue;
	            
	            if ( name.compare( apmt.getName())){
	            	
	                System.out.println( "found " + apmt.getName() + " rec=" + rec );
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

