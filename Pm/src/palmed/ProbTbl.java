


/**
 *  Problem Table
 *  
 *  This class defines a table of problems used to link to from the problem list
 *  that then links to both SNOMED codes and to our own Dgn table used on the 
 *  financial side.
 *  
 *  @change 2011-02-17 jrp - created
 */



// C Data Structure

//typedef	struct {
//
// 0		char	abbr[10];		/*  code abbreviation  */
// 10		char	desc[64];		/*  code description  */
// 74		char	snomed[20];		/*  first code set  */
// 94		char	icd9[10];		/*  second code set  */
// 104		char	icd10[10];		/*  third code set  */
// 114		char	code4[20];		/*  type of code  */
//
// 134		RECORD	dgnRec;			// Dgn code record
// 138		RECORD	protocol;		// follow list protocol
// 142		RECORD	edRec;			// educational link
// 146		char	resource[40];	// unused
// 186		char	unused[69];		// unused

// 255		byte	status;			/*  status byte: 'A' - active, 'I' - inactive, 'R' - removed */
//
//	} ProbTable;
//
///*	sizecheck( 256, sizeof( ProbTable ), "ProbTable" ); */





package palmed;

import org.zkoss.zul.Listbox;

import palmed.LabObsTbl.Status;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.ZkTools;

public class ProbTbl {
	
	// fields
    private byte[] dataStruct;
    private Rec probTblRec = null;			// this Rec
    private RandomFile file = null;			// file - if open
    private Rec maxrec = null;				// max record
    
    private final static String fn_probTbl = "probtbl.ovd";
    private final static int recLength = 256;	// record length
    
    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    }
    
    
    
    
    
	public ProbTbl(){
		
    	allocateBuffer();
	}

	
	
    public ProbTbl( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read probTable record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.probTblRec = rec;
    }


	// read probTable
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "ProbTable.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fn_probTbl, getRecordLength());
		probTblRec = rec;
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// probTable rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_probTbl, getRecordLength());
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "ProbTable.writeNew() bad rec" );
    	probTblRec = rec;
    	return rec;
    }
    
    
    public boolean write(){
    	return write( probTblRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "ProbTable.write() bad rec" );
    	// write record
        RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_probTbl, getRecordLength());
        probTblRec = rec;
        return true;
    }



    
    
	// open
    public static ProbTbl open(){
    	
    	// create new ProbTbl object
    	ProbTbl p = new ProbTbl();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fn_probTbl, getRecordLength(), RandomFile.Mode.READONLY );
		p.maxrec = new Rec( p.file.getMaxRecord());
		p.probTblRec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "ProbTbl.getNext() file not open" );

        // set initial record number
    	if ( probTblRec == null ){
    		probTblRec = new Rec( 2 );
    	} else if ( probTblRec.getRec() < 2 ){
    		probTblRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		probTblRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( probTblRec.getRec() > maxrec.getRec()) return false;
 
    	// read record
		file.getRecord( dataStruct, probTblRec );
		return true;
    }	
    
        
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
    	file = null;
    	probTblRec = null;
    	maxrec = null;
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
    	return new Rec( probTblRec.getRec());
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



    
    
//	char	desc[64];		/*  code description  */
    
    /**
     * getDesc()
     * 
     * Get desc from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 10, 64 ).trim();
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
    	StructHelpers.setStringPadded( s, dataStruct, 10, 64 );
    	return ;
    }

    /**
     * getDesc()
     * 
     * Static method to get a ProbTbl description given a ProbTbl record number
     * @param rec
     * @return String
     */
    public static String getDesc( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "bad rec" ); return null; }
		ProbTbl p = new ProbTbl( rec );
		return p.getDesc();
    }


     
    
//	char	snomed[20];		// SNOMED code, offset 50

    /**
     * getSNOMED()
     * 
     * Get SNOMED code from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getSNOMED(){
    	return StructHelpers.getString( dataStruct, 74, 20 ).trim();
    }

    /**
     * setSNOMED()
     * 
     * Set SNOMED code in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setSNOMED( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 74, 20 );
    	return ;
    }

    /**
     * getSNOMED()
     * 
     * Static method to get a ProbTbl SNOMED code given a ProbTbl record number
     * @param rec
     * @return String
     */
    public static String getSNOMED( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "bad rec" ); return null; }
		ProbTbl p = new ProbTbl( rec );
		return p.getSNOMED();
    }


    
   
    
    
    
//	char	icd9[8];		// ICD-9 code, offset 70
  
    /**
     * getICD9()
     * 
     * Get ICD-9 code from dataStruct.
     * 
     * @return String
     */
    public String getICD9(){
    	return StructHelpers.getString( dataStruct, 94, 10 ).trim();
    }

    /**
     * setICD9()
     * 
     * Set ICD-9 in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setICD9( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 94, 10 );
    	return ;
    }

    /**
     * getICD9()
     * 
     * Static method to get a ProbTbl ICD9 code given a ProbTbl record number
     * @param rec
     * @return String
     */
    public static String getICD9( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "bad rec" ); return null; }
		ProbTbl p = new ProbTbl( rec );
		return p.getICD9();
    }


    
//	char	icd10[8];		// ICD-9 code, offset 78
    
    /**
     * getICD10()
     * 
     * Get ICD-10 code from dataStruct.
     * 
     * @return String
     */
    public String getICD10(){
    	return StructHelpers.getString( dataStruct, 104, 10 ).trim();
    }

    /**
     * setICD10()
     * 
     * Set ICD-10 in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setICD10( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 104, 10 );
    	return ;
    }


    
    
//	char	code4[8];		// Misc code set #4, offset 86
    
    /**
     * getCode4()
     * 
     * Get Code4 code from dataStruct.
     * 
     * @return String
     */
    public String getCode4(){
    	return StructHelpers.getString( dataStruct, 114, 20 ).trim();
    }

    /**
     * setCode4()
     * 
     * Set Code4 in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setCode4( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 114, 20 );
    	return ;
    }


    
    
    
   
//	char	dgnRec;		// diagnosis code rec (financial side), offset 94
    
    /**
     * getDgnRec()
     * 
     * Get diagnosis code rec from dataStruct.
     * 
     * @return Rec
     */
    public Rec getDgnRec(){
    	return Rec.fromInt( dataStruct, 134);
    }

    /**
     * setDgnRec()
     * 
     * Set diagnosis code rec in dataStruct.
     * 
     * @param rec
     * @return void
     */
    public void setDgnRec( Rec rec ){
    	rec.toInt( dataStruct, 134 );
    	return ;
    }

    /**
     * getDgnRec()
     * 
     * Static method to get a ProbTbl diagnosis code rec given a ProbTbl record number
     * @param rec
     * @return Rec
     */
    public static Rec getDgnRec( Rec rec ){    	
		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "ProbTable.getDgnRec( rec ) bad rec" ); return null; }
		ProbTbl p = new ProbTbl( rec );
		return p.getDgnRec();
    }

    
    
    
   
//	char	protocol;		// follow list protocol rec, offset 98
    
    /**
     * getProtocol()
     * 
     * Get follow list protocol rec from dataStruct.
     * 
     * @return Rec
     */
    public Rec getProtocol(){
    	return Rec.fromInt( dataStruct, 138 );
    }

    /**
     * setProtocol()
     * 
     * Set follow list protocol rec in dataStruct.
     * 
     * @param rec
     * @return void
     */
    public void setProtocol( Rec rec ){
    	rec.toInt( dataStruct, 138 );
    	return ;
    }

//    RECORD	edRec;			// educational link , 142   
    /**
     * getedRec()
     * 
     * Get educational link from rec from dataStruct.
     * 
     * @return Rec
     */
    public Rec getedRec(){
    	return Rec.fromInt( dataStruct, 142 );
    }

    /**
     * setedRec()
     * 
     * Set educational link to rec in dataStruct.
     * 
     * @param rec
     * @return void
     */
    public void setedRec( Rec rec ){
    	rec.toInt( dataStruct, 142 );
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
//    public int getFlags(){
//    	return StructHelpers.getShort( dataStruct, 220 );
//    }

    /**
     * setFlags()
     * 
     * Set code flags in dataStruct.
     * 
     * @param flags - code flags
     * @return void
     */
//    public void setFlags( int flags ){
//  	StructHelpers.setShort( flags, dataStruct, 220 );
//    }


    
   
    
    
    
//	char	desc[64];		/*  code description  */
    
    /**
     * getResource()
     * 
     * Get patient ed resource from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getResource(){
    	return StructHelpers.getString( dataStruct, 146, 40 ).trim();
    }

    /**
     * setResource()
     * 
     * Set pt ed resource in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setResource( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 146, 40 );
    	return ;
    }

    /**
     * getResource()
     * 
     * Static method to get a ProbTbl pt ed resource given a ProbTbl record number
     * @param rec
     * @return String
     */
    public static String getResource( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "ProbTable.getResource( rec ) bad rec" ); return null; }
		ProbTbl p = new ProbTbl( rec );
		return p.getResource();
    }


     

    

    
    

   
	// byte status;			// status byte, offset 127
    
    /**
     * getStatus()
     * 
     * Get status byte from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	if ( dataStruct[255] == 'A' ) return Status.ACTIVE;
    	if ( dataStruct[255] == 'I' ) return Status.INACTIVE;
    	if ( dataStruct[255] == 'R' ) return Status.REMOVED;
    	return Status.REMOVED;
    }

    /**
     * setStatus()
     * 
     * Set status byte in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Status status ){
    	dataStruct[255] = 'R';
    	if ( status == Status.ACTIVE ) dataStruct[255] = 'A';
    	if ( status == Status.INACTIVE ) dataStruct[255] = 'I';
    	if ( status == Status.REMOVED ) dataStruct[255] = 'R';
    	return;
    }


 
 



    public void dump(){
    	StructHelpers.dump( "ProbTbl", dataStruct, getRecordLength());
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
    	return String.format( "%s %s %s %s %s %s", this.getAbbr(), this.getDesc(), this.getSNOMED(), this.getICD9(), this.getICD10(), this.getCode4());
    }


    
    public static int fillListbox( Listbox lb, String desc ){
    	
    	desc = desc.trim().toUpperCase();
    	if ( desc.length() < 3 ) return 0;
    	
    	ProbTbl v = new ProbTbl();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_probTbl, getRecordLength(), RandomFile.Mode.READONLY );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			file.getRecord( v.dataStruct, rec );			
/*			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if (( status != null ) && ( status != v.getStatus())) continue;
*/			
			// if searching by description, is this desc contained in either the short or full description?
			if ( v.getDesc().toUpperCase().indexOf( desc ) < 0 ) continue;
			
			// add this item to the list
			ZkTools.appendToListbox( lb, v.getDesc(), new Rec( rec ));
			++num;
		}
		file.close();
		
		return num;
    }
    


}
