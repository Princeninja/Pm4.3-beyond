 package palmed;

import usrlib.Date;
import usrlib.Dollar;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;

/*  INSNUM structure definitions  

 typedef	struct {

	//char	CarrierName[20];	/*  company name  */
	//char	PolicyNumber[16];	/*  policy numbers  */
	//char	GroupNumber[16];	/*  group numbers  */

	//RECORD	PpoRec;			/*  ppo record number  */
	//DATE	EffectiveDate;		/*  effective date  */
	//DATE	ExpirationDate;		/*  expiration date  */
	//DOLLAR	Deductible;		/*  deductible  */
	//DOLLAR	CopayAmt;		/*  copay dollar amount  */

	//char	unused[19];		/*  unused field  */

	//byte	CopayPct;		/*  copay percent  */
	//byte	Assignment;		/*  accept assignment  */
	//byte	Signature;		/*  signature on file  */
	//byte	Relation;		/*  relationship  */
	//byte	Valid;			/*  validity byte  */

	//} INSNUM;

	//sizecheck( 96, sizeof( INSNUM ), "INSNUM" );
    //*/




public class Insure {
	// fields
    private byte[] dataStruct;
    private Rec insnumRec = null;			// this Rec
    
    private final static String fn_insnum = "insnum.med";
    private final static int recLength = 96;	// record length
    
    private RandomFile file = null;
    //private RandomFile file2;
    private Rec maxrec = null;
	
    
    public Insure(){
    	
    	allocateBuffer();
    	
    }
    
    public Insure( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read insurenum record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.insnumRec = rec;
    }


	// read insure
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Insure.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fn_insnum, getRecordLength());
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// insnum rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getMedPath(), fn_insnum, getRecordLength());
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Insure.writeNew() bad rec" );

    	return rec;
    }
    
    
    public boolean write(){
    	return write( insnumRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Insure.write() bad rec" );

    	// write record
    	if ( file != null ){
    		file.putRecord( dataStruct, insnumRec );
    		System.out.println("saved in datastruct");
    	} else {
    		RandomFile.writeRec( rec, this.dataStruct, Pm.getMedPath(), fn_insnum, getRecordLength());
    	}
        return true;
    }
    
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[getRecordLength()];
    }

    public void setDataStruct(byte[] dataStruct) { this.dataStruct = dataStruct; }

    public byte[] getDataStruct() { return dataStruct; }

    
//	char	abbr[10];		/*  code abbreviation  */
    
    /**
     * getCarrierName()
     * 
     * Get company name from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getCarrierName(){
    	return StructHelpers.getString( dataStruct, 0, 20 ).trim();
    }

    /**
     * setCarrierName()
     * 
     * Set company name in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setCarrierName( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 0, 20 );
    	return ;
    }

//  char   PolicyNumber[16];  /*  policy numbers */
    
    
    /**
     * getPolicyNumber()
     * 
     * Get Policy Number from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getPolicyNumber(){
    	return StructHelpers.getString( dataStruct, 20, 16 ).trim();
    }

    /**
     * setPolicyNumber()
     * 
     * Set Policy Number in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setPolicyNumber( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 20, 16 );
    	return ;
    }
    
    
 // char	GroupNumber[16];	/*  group numbers  */
    
    
    
    /**
     * getGroupNumber()
     * 
     * Get Group Number from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getGroupNumber(){
    	return StructHelpers.getString( dataStruct, 36, 16 ).trim();
    }

    /**
     * set GroupNumber()
     * 
     * Set Group Number in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setGroupNumber( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 36, 16 );
    	return ;
    }
 
// 	RECORD	PpoRec;			/*  ppo record number  */
	    
    /**
     * getPpoRec()
     *
     * Gets the ppo record number
     * 
     * @param none
     * @return Rec
     */
    public Rec getPpoRec(){
        return Rec.fromInt( dataStruct, 52 );
    }

    /**
     * setPpoRec()
     *
     * Sets the ppo record number
     * 
     * @param Rec
     * @return void
     */
    public void setPpoRec( Rec rec ) {
        rec.toInt( dataStruct, 52 );
        return;
    }
    
    
//  DATE	EffectiveDate;		/*  effective date  */    
    /**
     * getEffectiveDate
     */
    public Date getEffectiveDate() {
        byte bcd[] = new byte[4];
        bcd[0] = dataStruct[56];
        bcd[1] = dataStruct[57];
        bcd[2] = dataStruct[58];
        bcd[3] = dataStruct[59];
        Date birthdate = new Date( bcd );
        return ( birthdate );
    }
    
    public void setEffectiveDate( usrlib.Date date ){
    	date.toBCD( dataStruct, 56 );
    }
    
//  DATE	ExpirationDate;		/*  expiration date  */	     
    /**
     * getBirthdate
     */
    public Date getExpirationDate() {
        byte bcd[] = new byte[4];
        bcd[0] = dataStruct[60];
        bcd[1] = dataStruct[61];
        bcd[2] = dataStruct[62];
        bcd[3] = dataStruct[63];
        Date birthdate = new Date( bcd );
        return ( birthdate );
    }
    
    public void setExpirationDate( usrlib.Date date ){
    	date.toBCD( dataStruct, 60 );
    }
    
//  DOLLAR	Deductible;		/*  deductible  */
    
    /**
	    * getDeductible()
	    * 
	    * Get the Deductible 
	    * 
	    * @param none
	    * @return Dollar
	    */

	    public Dollar getDeductible(){
	    	return new Dollar (dataStruct, 64);
	    }
	    
	    /**
	     * setDeductible()
	     * 
	     * Set the Dollar Deductible in dataStruct.
	     * 
	     * @param Deductible - String Deductible
	     * @return none
	     */
	    public void setDeductible( String Deductible ){
	    	Dollar dollar = new Dollar(Deductible);
	    	dollar.toBCD( dataStruct, 64 );
	    } 
    
//  DOLLAR	CopayAmt;		/*  copay dollar amount  */    
	    
	    /**
	        * getCopayAmt()
		    * 
		    * Get the CopayAmt 
		    * 
		    * @param none
		    * @return Dollar
		    */

		    public Dollar getCopayAmt(){
		    	return new Dollar (dataStruct, 68);
		    }
		    
		    /**
		     * setCopayAmt()
		     * 
		     * Set the Dollar CopayAmt in dataStruct.
		     * 
		     * @param CopayAmt - String CopayAmt
		     * @return none
		     */
		    public void setCopayAmt( String CopayAmt ){
		    	Dollar dollar = new Dollar(CopayAmt);
		    	dollar.toBCD( dataStruct, 68 );
		    } 
		    
		  //char	unused[19];		/*  unused field  */		    
		    
//  byte CopayPct  /* copay percent */	
		
		    /**
		     * getCopayPct()
		     * 
		     * Get Copay Pct from dataStruct.
		     * 
		     * @param none
		     * @return int
		     */
		    public int getCopayPct(){
		    	return (int) dataStruct[91];
		    }

		    /**
		     * setCopayPct()
		     * 
		     * Set Copay Pct in dataStruct.
		     * 
		     * @param int		'CopayPct'
		     * @return void
		     */
		    public void setCopayPct( int copaypct ){
		    	dataStruct[91] = (byte)( 0xff & copaypct );
		    }
   
//  byte Assignment /* accept assignment */  
		    
		    /**
		     * getAssignment()
		     * 
		     * Get Assignment byte from dataStruct.
		     * 
		     * @param none
		     * @return int
		     */
		    public int getAssignment(){
		    	return (int) dataStruct[92];
		    }

		    /**
		     * setAssignment()
		     * 
		     * Set Assignment byte in dataStruct.
		     * 
		     * @param int		'Assignment'
		     * @return void
		     */
		    public void setAssignment( int assignment ){
		    	dataStruct[92] = (byte)( 0xff & assignment );
		    }
//  byte Signature /* signature on file */ 
		    
		    /**
		     * getSignature()
		     * 
		     * Get signature byte from dataStruct.
		     * 
		     * @param none
		     * @return int
		     */
		    public int getSignature(){
		    	return (int) dataStruct[93];
		    }

		    /**
		     * setSignature()
		     * 
		     * Set signature byte in dataStruct.
		     * 
		     * @param int		'signature'
		     * @return void
		     */
		    public void setSignature ( int signature ){
		    	dataStruct[93] = (byte)( 0xff & signature );
		    }

// byte Relation  /* relationship  */ 		    
		    
		    /**
		     * getRealtion()
		     * 
		     * Get relation byte from dataStruct.
		     * 
		     * @param none
		     * @return int
		     */
		    public int getRelation(){
		    	return (int) dataStruct[94];
		    }

		    /**
		     * setRelation()
		     * 
		     * Set relation byte in dataStruct.
		     * 
		     * @param int		'relation'
		     * @return void
		     */
		    public void setRelation( int relation ){
		    	dataStruct[94] = (byte)( 0xff & relation );
		    }
		    
// byte Valid /* validity byte */ 
		    
		    /**
		     * getValid()
		     * 
		     * Get validity byte from dataStruct.
		     * 
		     * @param none
		     * @return int
		     */
		    public int getValid(){
		    	return (int) dataStruct[95];
		    }

		    /**
		     * setValid()
		     * 
		     * Set validity byte in dataStruct.
		     * 
		     * @param int		'valid'
		     * @return void
		     */
		    public void setValid( int valid ){
		    	dataStruct[95] = (byte)( 0xff & valid );
		    }
		    
		    /**
		     * setValid()
		     * 
		     *  */
		    public void setValid(){ dataStruct[95] = (byte)( Validity.VALID.getCode() & 0xff ); }
		    
		    
		    public void dump(){
		    	StructHelpers.dump( "Insure", dataStruct, getRecordLength());
		    	return;
		    }
		    
		    
		    /**
		     * toString()
		     * 
		     * Get a printable String representation of this entry.
		     * 
		     * @param none
		     * @return void
		     */
		  
		    public String toString(){
		    	return String.format( "%s %s %s ", this.getCarrierName(), this.getPolicyNumber() ,this.getGroupNumber());
		    }
 
		    
		    
			// open
		    public static Insure open(){
		    	
		    	// create new Insure object
		    	Insure insure = new Insure();

		    	// open file, read maxrec, and set current rec to null
				insure.file = RandomFile.open( Pm.getMedPath(), fn_insnum, getRecordLength(), RandomFile.Mode.READWRITE  );
				insure.maxrec = new Rec( insure.file.getMaxRecord());
				insure.insnumRec = null;

				return insure;
		    }
		    
		    
		    
		    // getNext()
		    public boolean getNext(){
		        	
		        // is file open??
		        if ( file == null ) SystemHelpers.seriousError( "Insure.getNext() file not open" );

		        // set initial record number
		    	if ( insnumRec == null ){
		    		insnumRec = new Rec( 2 );
		    	} else if ( insnumRec.getRec() < 2 ){
		    		insnumRec.setRec( 2 );
		    		
		    	// else, increment record number
		    	} else {
		    		insnumRec.increment();
		    	}
		    	
		    	// is this past maxrec
		    	if ( insnumRec.getRec() > maxrec.getRec()) return false;
		    	
		    	//System.out.println( ptRec.toString());
		 
		    	// read record
				file.getRecord( dataStruct, insnumRec );
				return true;
		    }	
		    
		    public Rec getRec(){
		    	//return insnumRec;this use to be it, however:
		    	// this needs to return a new object each time so that there are no
		    	// data inconsistencies as we get the record value.
		    	return new Rec( insnumRec.getRec());
		    	
		    }
		    public void setRec( Rec rec ){ insnumRec = rec; }
		    
		        
		    // close()
		    public boolean close(){
		    	if ( file != null ) file.close();
		       	file = null;
		    	insnumRec = null;
		    	maxrec = null;
		    	return true;
		    }
		      
		    
		    
}
