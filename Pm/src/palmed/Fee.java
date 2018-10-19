package palmed;

/* define code fee schedule structure  

	  NOTE - This structure is used for the inventory fee  
	  fee schedule.  Any changes made here will also affect  
	  the inventory routines and data structures.  

//typedef	struct {

0		USRT	utilize[2][12];		/*  monthly utilization  
48		DOLLAR	revenue[2][12];		  monthly revenue  

144		DATE	effective[4];		  date fee became effective 
148		DOLLAR	Fee[4];			  fee charged  
152		DOLLAR	Adj[4];			  adjustment  
156		DOLLAR	Asn[4];			  assigned portion  

160		USRT	flags;			  flags  
	
162		char	unused[45];		  unused field  
207		byte	valid;			  validity byte  

	} CDEFEE;

#define	SZ_CDEFEE	sizeof( CDEFEE )	  size of CDEFEE structure  
	sizecheck( 256, sizeof( CDEFEE ), "CDEFEE" );
*/

import usrlib.*;

public class Fee {

	//fields
	private byte[] dataStruct;
	private Rec feeRec = null;
	
	private final static String fn_fee = "cdesrv00.ovd";
	private final static int recLength = 256;  // record Length
	
	private RandomFile file = null;
	//private RandomFile file2;
	private Rec maxrec = null;
	
	enum Status {
		ACTIVE, INACTIVE, REMOVED 
		
	}
	
	public Fee(){
		
		allocateBuffer();
		
	}
	
	public Fee( Rec rec ){
		
		//allocate space
		allocateBuffer();
		
		// read fee record
		read( rec );
		
		//set a local copy of the rec
		this.feeRec = rec;
		
	}
	
	// read fee
	
	public void read( Rec rec ){
		
		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Fee.read() bad rec" ); return; }
		RandomFile.readRec(rec, dataStruct, Pm.getOvdPath(), fn_fee, getRecordLength());
		
	}
	
	public Rec writeNew(){
		
		Rec rec; // fee rec
		
		rec = RandomFile.writeRec(null, this.dataStruct, Pm.getOvdPath(), fn_fee, getRecordLength());
		if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Fee.writeNew() bad rec" );
		return rec;
	}
	
	
	public boolean write(){
		return write( feeRec, 0 );
	}
	
	
	
	public boolean write( Rec rec, int f_no ){
		
		if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Fee.write() bad rec");
	
		// write record 
		if ( file != null ){
			file.putRecord(dataStruct, feeRec );
		} else {
			String fn_fees = "cdesrv"+ String.format("%02d", f_no) +".ovd";
			RandomFile.writeRec(rec, this.dataStruct, Pm.getOvdPath(), fn_fees, getRecordLength());	
			System.out.println("Information saved to:"+ fn_fees );
			System.out.println("Rec is: "+ rec );
		}
		return true;
	}
	
	/**
	 * getRecordLength()
	 * @return Record Length 
	 */
	public static int getRecordLength() { return ( recLength ); }
	
	private void allocateBuffer(){
		// allocate data structure space 
		dataStruct = new byte[getRecordLength()];		
	}
	
	public void setDataStruct( byte[] dataStruct ){ this.dataStruct = dataStruct;  }
	
	public byte[] getDataStruct() { return dataStruct; }
	
	
//0		USRT	utilize[2][12];		/*  monthly utilization  
//48		DOLLAR	revenue[2][12];		  monthly revenue  

	
//144		DATE	effective[4];		  date fee became effective 
	/**
	 * get Effective Date 
	 */
	public Date getEffectivedate() {
		return new Date ( dataStruct, 144 );
	}
	
	public void setEffectivedate( usrlib.Date date ){
		date.toBCD( dataStruct, 144 );
	}
	
//148		DOLLAR	Fee[4];			  fee charged  
	/**
	 * getFee
	 * 
	 * Get the Dollar Fee
	 * 
	 * @param none
	 * @return Dollar Fee
	 */
	public Dollar getFee(){
		return new Dollar ( dataStruct, 148);
	}
	
	/**
	 * setFee()
	 * 
	 * Set Fee in dataStruct 
	 * 
	 * @param Fee
	 * @return none
	 */
	public void setFee( String Fee ){
		Dollar dollar = new Dollar(Fee);
		dollar.toBCD( dataStruct, 148 );
	}
		
	
//152		DOLLAR	Adj[4];			  adjustment  

	/**
	 * getAdj
	 * 
	 * Get the Adjustment Dollar amount
	 * 
	 * @param none
	 * @return Dollar Adjustment
	 */
	public Dollar getAdj(){
		return new Dollar ( dataStruct, 152 );
	}
	
	/**
	 * setAdj()
	 * 
	 * Set Adjustment Dollar value in dataStruct 
	 * 
	 * @param Adj
	 * @return none
	 */
	public void setAdj( String Adj ){
		Dollar dollar = new Dollar( Adj );
		dollar.toBCD( dataStruct, 152 );
	}
	
//156		DOLLAR	Asn[4];			  assigned portion  

	/**
	 * getAsn
	 * 
	 * Get the Dollar Assigned portion
	 * 
	 * @param none
	 * @return Dollar Asn
	 */
	public Dollar getAsn(){
		return new Dollar ( dataStruct, 156 );
	}
	
	/**
	 * setAsn()
	 * 
	 * Set Asn in dataStruct 
	 * 
	 * @param Asn
	 * @return none
	 */
	public void setAsn( String Asn ){
		Dollar dollar = new Dollar( Asn );
		dollar.toBCD( dataStruct, 156 );
	}
	
	
//160		USRT	flags;			  flags  
	  /**
     * getFlags()
     * 
     * Get code flags from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getFlags(){
    	return StructHelpers.getShort( dataStruct, 160 );
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
    	StructHelpers.setShort( flags, dataStruct, 160 );
    }
	
//162		char	unused[45];		  unused field  
    
//207		byte	valid;			  validity byte  
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getValid(){
    	return (int) dataStruct[207];
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
    	dataStruct[207] = (byte)( 0xff & valid );
    }


    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[207] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[207] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[207] = (byte)( Validity.HIDDEN.getCode() & 0xff );}
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[207] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[207] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[207] = (byte)( valid.getCode() & 0xff ); }
    	

	public void dump(){
		StructHelpers.dump( "Fee", dataStruct, getRecordLength());
		return;
	}
	
	
	public String toString(){
		return String.format( "%s %s %s %s ", this.getEffectivedate().getPrintable(),this.getFee().getPrintable(), this.getAdj().getPrintable(), this.getAsn().getPrintable() );
	}
	
	
	//open
	public static Fee open(){
		
		// create new Fee object
		Fee fee = new Fee();
		
		// open file, read maxrec, and set current rec to null 
		fee.file = RandomFile.open( Pm.getOvdPath(), fn_fee , getRecordLength() , RandomFile.Mode.READWRITE );
		fee.maxrec = new Rec( fee.file.getMaxRecord());
		fee.feeRec = null;
		
		return fee;
		
	}
	
	// getNext()
	public boolean getNext(){
		
		// is the file open?
		if ( file == null ) SystemHelpers.seriousError( "Fee.getNext() file not open" );
		
		// set initial record number 
		if ( feeRec == null ){
			feeRec = new Rec( 2 );
		}else if ( feeRec.getRec() < 2 ){
			feeRec.setRec( 2 );
			
		 // else, increment record number 	
		} else {
			feeRec.increment();
		}
		
		// is this past maxrec
		if ( feeRec.getRec() > maxrec.getRec()) return false;
		
		//read record
		file.getRecord( dataStruct, feeRec );
		return true;	
	}

	
	public Rec getRec(){
		return new Rec( feeRec.getRec() );
	}
	
	public void setRec( Rec rec ){ feeRec = rec; }
	
	//close()
	public boolean close(){
		if ( file!= null ) file.close();
		file = null;
		feeRec = null;
		maxrec = null;
		return true;
	}
	
	
	
	
	
}
