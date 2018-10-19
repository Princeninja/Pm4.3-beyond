package palmed;

import usrlib.Address;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;

public class Phrds {
	// fields
    private byte[] dataStruct;
    private Rec phrdsRec = null;			// this Rec
    
    private final static String fn_phrds = "prm.ovd";
    private final static int recLength = 256;	// record length
    
    private RandomFile file = null;
    //private RandomFile file2;
    private Rec maxrec = null;

    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    }
    
	public Phrds(){
		
    	allocateBuffer();
	}

	
	
    public Phrds( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read phrds record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.phrdsRec = rec;
    }


	// read phrds
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Phrds.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fn_phrds, getRecordLength());
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// phrds rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_phrds, getRecordLength());
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Phrds.writeNew() bad rec" );

    	return rec;
    }
    
    
    public boolean write(){
    	return write( phrdsRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Phrds.write() bad rec" );

    	// write record
    	if ( file != null ){
    		file.putRecord( dataStruct, phrdsRec );
    	} else {
    		RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_phrds, getRecordLength());
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

   
    
    
//	char	Name[40];		/*  Pharmacy Name */
    
    /**
     * get Name()
     * 
     * Get Name from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getName(){
    	return StructHelpers.getString( dataStruct, 0, 40 ).trim();
    }

    /**
     * set Name()
     * 
     * Set Name in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setName( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 0, 40 );
    	return ;
    }

    
    
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
    	return StructHelpers.getString( dataStruct, 40, 10 ).trim();
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
    	StructHelpers.setStringPadded( s, dataStruct, 40, 10 );
    	return ;
    }
   

   
	// USRT flags;		// code flags, offset 220
    
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
     * Returns an Address object read from the prm (pharmacies) file
     * 
     * @param none
     * @return Address
     * 
     */
     public Address getAddress() {
     	return new Address (dataStruct, 52 );
     }
     
     /**
      * setAddress() - Set and address into prm.med
      * 
      * @param Address
      * @return void
      */
     
     public void setAddress (Address address){
     	address.toData(dataStruct, 52);
     	
     }
     
     
     // char contact[40]   /*  contact or other info  */


     /**
      * getContact()
      * 
      * Get contact from dataStruct.
      * 
      * @param none
      * @return String
      */
     public String getContact(){
     	return StructHelpers.getString( dataStruct, 140, 40 ).trim();
     }

     /**
      * setContact()
      * 
      * Set contact in dataStruct.
      * 
      * 
      * @param s
      * @return void
      */
     public void setContact( String s ){
     	StructHelpers.setStringPadded( s, dataStruct, 140, 40 );
     	return ;
     }


    

    
    

    //TODO - better implementation
    
	// byte valid;			// validity byte, offset 255
    
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getValid(){
    	return (int) dataStruct[255];
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
    	dataStruct[255] = (byte)( 0xff & valid );
    }

    /**
     * isValid  
     * */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[255] ) == Validity.VALID ); }

    /**
     * setValid()
     * 
     *  */
    public void setValid(){ dataStruct[255] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     * */
    public void setHidden(){ dataStruct[255] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     *isHidden() 
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[255] ) == Validity.HIDDEN ); }
    
     /**
      * getValidityByte()
      * 
      */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[255] ); }
    
    /**
     * setValidityByte()
     */
    public void setValidity( Validity valid ){ dataStruct[255] = (byte)( valid.getCode() & 0xff ); }
    
      

    public void dump(){
    	StructHelpers.dump( "Phrds", dataStruct, getRecordLength());
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
    	return String.format( "%s %s %s", this.getAbbr(), this.getName(), this.getContact());
    }


    
    
    
    
    
	// open
    public static Phrds open(){
    	
    	// create new Phrds object
    	Phrds phrds = new Phrds();

    	// open file, read maxrec, and set current rec to null
		phrds.file = RandomFile.open( Pm.getOvdPath(), fn_phrds, getRecordLength(), RandomFile.Mode.READWRITE  );
		phrds.maxrec = new Rec( phrds.file.getMaxRecord());
		phrds.phrdsRec = null;

		return phrds;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "Phrds.getNext() file not open" );

        // set initial record number
    	if ( phrdsRec == null ){
    		phrdsRec = new Rec( 2 );
    	} else if ( phrdsRec.getRec() < 2 ){
    		phrdsRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		phrdsRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( phrdsRec.getRec() > maxrec.getRec()) return false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
		file.getRecord( dataStruct, phrdsRec );
		return true;
    }	
    
    public Rec getRec(){ 
    	// return phrdsRec; this use to be it, however:
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we get the record value.
    	return new Rec( phrdsRec.getRec());
    	
    }
    public void setRec( Rec rec ){ phrdsRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
       	file = null;
    	phrdsRec = null;
    	maxrec = null;
    	return true;
    }

    
    
    

}


