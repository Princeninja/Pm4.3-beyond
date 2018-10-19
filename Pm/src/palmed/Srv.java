package palmed;


import usrlib.Dollar;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;

public class Srv {
	// fields
    private byte[] dataStruct;
    private Rec srvRec = null;			// this Rec
    
    private final static String fn_srv = "cdesrvmn.ovd";
    private final static int recLength = 256;	// record length
    
    private RandomFile file = null;
    //private RandomFile file2;
    private Rec maxrec = null;
    
    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    	
    }
    
	public Srv(){
		
    	allocateBuffer();
	}

	
	
    public Srv( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read srv record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.srvRec = rec;
    }


	// read srv
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Srv.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fn_srv, getRecordLength());
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// srv rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_srv, getRecordLength());
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Srv.writeNew() bad rec" );

    	return rec;
    }
    
    
    public boolean write(){
    	return write( srvRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Srv.write() bad rec" );

    	// write record
    	if ( file != null ){
    		file.putRecord( dataStruct, srvRec );
    	} else {
    		RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_srv, getRecordLength());
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



    
    
//	char	desc[30];		/*  code description  */
    
    /**
     * getDesc()
     * 
     * Get desc from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 10, 30 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set desc in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 10, 30 );
    	return ;
    }



    
//	char	code1[8];		/*  first code set  */
//	char	code2[8];		/*  second code set  */
//	char	code3[8];		/*  third code set  */

    /**
     * getCode()
     * 
     * Get code from dataStruct.
     * 
     * @param set - code set number (1, 2, or 3)
     * @return String
     */
    public String getCode( int set ){
    	if (( set < 1 ) || ( set > 3 )) set = 1;
    	return StructHelpers.getString( dataStruct, 40 + ( 8 * (set - 1)), 8 ).trim();
    }

    /**
     * setCode()
     * 
     * Set code in dataStruct.
     * 
     * @param set - code set number (1, 2, or 3)
     * @param s
     * @return void
     */
    public void setCode( int set, String s ){
    	if (( set < 1 ) || ( set > 3 )) set = 1;
    	StructHelpers.setStringPadded( s, dataStruct, 40 + ( 8 * (set - 1)), 8 );
    	return ;
    }



    
    
//	char	toc[8];			/*  type of code  */

    /**
     * getTOC()
     * 
     * Get TOC from dataStruct.
     * 
     * @return String
     */
    public String getTOC(){
    	return StructHelpers.getString( dataStruct, 64, 8 ).trim();
    }

    /**
     * setTOC()
     * 
     * Set TOC in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setTOC( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 64, 8 );
    	return ;
    }

 //DOLLAR rv[4];    //relative value or DRG 
    
    /**
     * getrv()
     * 
     * Get the rv amount
     * 
     * @param none
     * @return Dollar
     */

    public Dollar getrv(){
    	return new Dollar(dataStruct, 216);
    }
    
    /**
     * setrv()
     * 
     * Set Dollar rv in dataStruct.
     * 
     * @param dollar - Dollar rv
     * @return void
     */
    public void setrv( String rv ){
    	Dollar dollar = new Dollar(rv);
    	dollar.toBCD( dataStruct, 216 );
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
    	return StructHelpers.getShort( dataStruct, 220 );
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
    	StructHelpers.setShort( flags, dataStruct, 220 );
    }
 
    

    //TODO - better implementation
    
	// byte valid;			// validity byte, offset 222
    
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getValid(){
    	return (int) dataStruct[222];
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
    	dataStruct[222] = (byte)( 0xff & valid );
    }

    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[222] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[222] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[222] = (byte)( Validity.HIDDEN.getCode() & 0xff );}
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[222] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[222] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[222] = (byte)( valid.getCode() & 0xff ); }
    
    
    public void dump(){
    	StructHelpers.dump( "Srv", dataStruct, getRecordLength());
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
    	return String.format( "%s %s %s %s %s %s", this.getAbbr(), this.getDesc(), this.getTOC(), this.getCode( 1 ), this.getCode( 2 ), this.getCode( 3 ));
    }


    
    
    
    
    
	// open
    public static Srv open(){
    	
    	// create new Srv object
    	Srv srv = new Srv();

    	// open file, read maxrec, and set current rec to null
		srv.file = RandomFile.open( Pm.getOvdPath(), fn_srv, getRecordLength(), RandomFile.Mode.READWRITE  );
		srv.maxrec = new Rec( srv.file.getMaxRecord());
		srv.srvRec = null;

		return srv;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "Srv.getNext() file not open" );

        // set initial record number
    	if ( srvRec == null ){
    		srvRec = new Rec( 2 );
    	} else if ( srvRec.getRec() < 2 ){
    		srvRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		srvRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( srvRec.getRec() > maxrec.getRec()) return false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
		file.getRecord( dataStruct, srvRec );
		return true;
    }	
    
    public Rec getRec(){
    	//return srvRec;this use to be it, however:
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we get the record value.
    	return new Rec( srvRec.getRec()); 
    	
    }
    public void setRec( Rec rec ){ srvRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
       	file = null;
    	srvRec = null;
    	maxrec = null;
    	return true;
    }

    
    
    

}
