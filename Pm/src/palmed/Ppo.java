package palmed;

/*  PPO record structure  

	//typedef struct  {

	//char	abbr[10];	/*  ppo abbreviation  */
	//char	name[40];	/*  ppo name  */

	//USRT	flags;		/*  flags  */
	//ADDRESS	address;	/*  ppo address  */
	//RECORD	alpha;		/*  alphabetic chain  */

	//char	contact[4][32];	/*  contact persons  */
	//char	note[4][32];	/*  contact note  */
	//char	phone[6][16];	/*  contact phone  */

	//char	unused[12];	/*  unused field  */
	//byte	SrvCodeSet;		// service code set to use
	//byte	DgnCodeSet;		// diagnosis code set to use
	//byte	TosCodeSet;		// type of service code set to use
	//byte	valid;		/*  validity byte  */

	//} PPO;

/*	sizecheck( 512, sizeof( PPO ), "PPO" );
*/



import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.*;


public class Ppo {
	// fields
    private byte[] dataStruct;
    private Rec ppoRec = null;			// this Rec
    
    private final static String fn_ppo = "ppo.med";
    private final static int recLength = 512;	// record length
    
    private RandomFile file = null;
    //private RandomFile file2;
    private Rec maxrec = null;

    enum Status {
    	ACTIVE, INACTIVE, REMOVED 
    }
    
	public Ppo(){
		
    	allocateBuffer();
	}

	
	
    public Ppo( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read ppo record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.ppoRec = rec;
    }


	// read ppo
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Ppo.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fn_ppo, getRecordLength());
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// ppo rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getMedPath(), fn_ppo, getRecordLength());
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Ppo.writeNew() bad rec" );

    	return rec;
    }
    
    
    public boolean write(){
    	return write( ppoRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Ppo.write() bad rec" );

    	// write record
    	if ( file != null ){
    		file.putRecord( dataStruct, ppoRec );
    	} else {
    		RandomFile.writeRec( rec, this.dataStruct, Pm.getMedPath(), fn_ppo, getRecordLength());
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
     * getAbbr()
     * 
     * Get abbreviation from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getAbbr(){
    	return StructHelpers.getString( dataStruct, 0, 10 ).trim();
    }

    /**
     * setAbbr()
     * 
     * Set abbreviation in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setAbbr( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 0, 10 );
    	return ;
    }



    
    
//	char	name[40];		/*  ppo name  */
    
    /**
     * get name()
     * 
     * Get name from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getname(){
    	return StructHelpers.getString( dataStruct, 10, 40 ).trim();
    }

    /**
     * set name()
     * 
     * Set name in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setname( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 10, 40 );
    	return ;
    }

    
	// USRT flags;		// code flags, offset 50    
    /**
     * getFlags()
     * 
     * Get code flags from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getFlags(){
    	return StructHelpers.getShort( dataStruct, 50 );
    }

    /**
     * setFlags()
     * 
     * Set code flags in dataStruct.
     * 
     * @param flags - code flags
     * @return void
     */
    public void setFlags( int flags ){
    	StructHelpers.setShort( flags, dataStruct, 50 );
    }
   

   /**
    * getAddress()
    * 
    * Returns an Address object read from the ppo file
    * 
    * @param none
    * @return Address
    * 
    */
    public Address getAddress() {
    	return new Address (dataStruct, 52 );
    }
    
    /**
     * setAddress() - Set and address into ppo.med
     * 
     * @param Address
     * @return void
     */
    
    public void setAddress (Address address){
    	address.toData(dataStruct, 52);
    	
    }
    
    
    
    // char contact[4][32]   /* 4 possible contact persons  */


    /**
     * getContact()
     * 
     * Get contact from dataStruct.
     * 
     * @param set - contact number (1, 2, 3 or 4)
     * @return String
     */
    public String getContact(int set){
    	if ((set < 1) || (set > 4 )) set = 1;
    	return StructHelpers.getString( dataStruct, 144 + (32 * (set-1)), 32 ).trim();
    }

    /**
     * setContact()
     * 
     * Set contact in dataStruct.
     * 
     * @param set - contact set number (1, 2, 3 or 4)
     * @param s
     * @return void
     */
    public void setContact( int set, String s ){
    	if ((set < 1) || (set > 4 )) set = 1;
    	StructHelpers.setStringPadded( s, dataStruct, 144 + (32 * (set-1)), 32 );
    	return ;
    }



    
    
//	char	note[4][32];			/*  4 possible contact note  */

    /**
     * get note()
     * 
     * Get note from dataStruct.
     * @param set - note set number (1, 2, 3 or 4)
     * @return String
     */
    public String getnote(int set){
    	if ((set < 1) || (set > 4 )) set = 1;
    	return StructHelpers.getString( dataStruct, 272 +(32 * (set-1)), 32 ).trim();
    }

    /**
     * set note()
     * 
     * Set note in dataStruct.
     * @param set - note set number (1, 2, 3 or 4)
     * @param s
     * @return void
     */
    public void setnote(int set, String s ){
    	if ((set < 1) || (set > 4 )) set = 1;
    	StructHelpers.setStringPadded( s, dataStruct, 272 + (32 * (set-1)), 32 );
    	return ;
    }



//	char	phone[6][16];			/* 6 possible phone numbers  */

    /**
     * get phone()
     * 
     * Get phone from dataStruct.
     * @param set - phone set number (1, 2, 3, 4, 5, or 6)
     * @return String
     */
    public String getphone(int set){
    	if ((set < 1) || (set > 6 )) set = 1;
    	return StructHelpers.getString( dataStruct, 400 + (16 * (set-1)), 16 ).trim();
    }

    /**
     * set phone()
     * 
     * Set phone in dataStruct.
     * 
     * @param set - phone set number (1, 2, 3, 4, 5 or 6)
     * @param s
     * @return void
     */
    public void setphone(int set, String s ){
    	if ((set < 1) || (set > 6 )) set = 1;
    	StructHelpers.setStringPadded( s, dataStruct, 400 + (16*(set-1)), 16 );
    	return ;
    }
    
           
    

    //TODO - better implementation
    
	// byte valid;			// validity byte, offset 511
    
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getValid(){
    	return (int) dataStruct[511];
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
    	dataStruct[511] = (byte)( 0xff & valid );
    }


    /**
     * isValid  
     * */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[511] ) == Validity.VALID ); }

    /**
     * setValid()
     * 
     *  */
    public void setValid(){ dataStruct[511] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     * */
    public void setHidden(){ dataStruct[511] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     *isHidden() 
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[511] ) == Validity.HIDDEN ); }
    
     /**
      * getValidityByte()
      * 
      */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[511] ); }
    
    /**
     * setValidityByte()
     */
    public void setValidity( Validity valid ){ dataStruct[511] = (byte)( valid.getCode() & 0xff ); }
    
    
    
    public void dump(){
    	StructHelpers.dump( "Ppo", dataStruct, getRecordLength());
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
    	return String.format( "%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s", this.getAbbr(), this.getname() ,this.getnote(1),this.getnote(2),this.getnote(3),this.getnote(4),this.getContact(1),this.getContact(2),this.getContact(3),this.getContact(4),this.getphone(1),this.getphone(2),this.getphone(3),this.getphone(4),this.getphone(5),this.getphone(6));
    }
    
    
    
	// open
    public static Ppo open(){
    	
    	// create new Ppo object
    	Ppo ppo = new Ppo();

    	// open file, read maxrec, and set current rec to null
		ppo.file = RandomFile.open( Pm.getMedPath(), fn_ppo, getRecordLength(), RandomFile.Mode.READWRITE  );
		ppo.maxrec = new Rec( ppo.file.getMaxRecord());
		ppo.ppoRec = null;

		return ppo;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "Ppo.getNext() file not open" );

        // set initial record number
    	if ( ppoRec == null ){
    		ppoRec = new Rec( 2 );
    	} else if ( ppoRec.getRec() < 2 ){
    		ppoRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		ppoRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( ppoRec.getRec() > maxrec.getRec()) return false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
		file.getRecord( dataStruct, ppoRec );
		return true;
    }	
    
    public Rec getRec(){
    	//return ppoRec;this use to be it, however:
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we get the record value.
    	return new Rec( ppoRec.getRec());
    	
    }
    public void setRec( Rec rec ){ ppoRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
       	file = null;
    	ppoRec = null;
    	maxrec = null;
    	return true;
    }

    
    
    

}


