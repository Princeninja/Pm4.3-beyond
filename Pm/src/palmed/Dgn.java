package palmed;


/*  code file record structure  */

//typedef	struct {
//
// 0		char	abbr[10];		/*  code abbreviation  */
// 10		char	desc[30];		/*  code description  */
// 40		char	code1[8];		/*  first code set  */
// 48		char	code2[8];		/*  second code set  */
// 56		char	code3[8];		/*  third code set  */
// 64		char	toc[8];			/*  type of code  */
//
// 72		USRT	utilize[2][12];		/*  monthly utilization  */
// 120		DOLLAR	revenue[2][12];		/*  monthly revenue  */
//
// 216		DOLLAR	rv;			/*  relative value or DRG  */
//
// 220		USRT	flags;			/*  code flags  */
//
// 222		byte	valid;			/*  validity byte  */
// 223		char	unused[29];		/*  unused for now  */
//
// 252		RECORD	alpha;			/*  next alphabetic record  */
//
//	} CDEMN;
//
//#define	SZ_CDEMN	sizeof( CDEMN )		/*  size of CDEMN structure  */
///*	sizecheck( 256, sizeof( CDEMN ), "CDEMN" ); */





import org.zkoss.zul.Listbox;

import palmed.Dgn.Status;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;
import usrlib.ZkTools;

import usrlib.*;

public class Dgn {
	
	// fields
    private byte[] dataStruct;
    private Rec dgnRec = null;			// this Rec
    
    private final static String fn_dgn = "cdedgnmn.ovd";
    private final static int recLength = 256;	// record length
    
    private RandomFile file = null;  
    //private RandomFile file2;
    private Rec maxrec = null;

    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    	
    }      
    
	public Dgn(){
		
    	allocateBuffer();
	}

	
	
    public Dgn( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read dgn record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.dgnRec = rec;
    }


	// read dgn
    
    public void read( Rec rec ){
    			
    	if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "Dgn.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fn_dgn, getRecordLength());
		dgnRec = rec; //TODO remove if it fails.
    }	
    

    
    
    
    public Rec writeNew(){
    	
    	Rec rec;		// dgn rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_dgn, getRecordLength());
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Dgn.writeNew() bad rec" );
    	dgnRec = rec; //TODO remove if it fails 
    	return rec;
    }
    
    
    public boolean write(){
    	return write( dgnRec );
    }
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Dgn.write() bad rec" );

    	// write record
    	if ( file != null ){
    		file.putRecord( dataStruct, dgnRec );
    	} else {
    		RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_dgn, getRecordLength());
    	}
    	dgnRec = rec; //TODO remove if fails 
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
     * Get the rv 
     * 
     * @param none
     * @return Dollar
     */

    public Dollar getrv(){
    	return new Dollar (dataStruct, 216);
    }
    
    /**
     * setrv()
     * 
     * Set code rv in dataStruct.
     * 
     * @param rv - String rv
     * @return none
     */
    public void setrv( String rv ){
    	Dollar dollar = new Dollar(rv);
    	dollar.toBCD(dataStruct, 216 );
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
    	StructHelpers.dump( "Dgn", dataStruct, getRecordLength());
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
    public static Dgn open(){
    	
    	// create new Dgn object
    	Dgn dgn = new Dgn();

    	// open file, read maxrec, and set current rec to null
		dgn.file = RandomFile.open( Pm.getOvdPath(), fn_dgn, getRecordLength(), RandomFile.Mode.READWRITE  );
		dgn.maxrec = new Rec( dgn.file.getMaxRecord());
		dgn.dgnRec = null;

		return dgn;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "Dgn.getNext() file not open" );

        // set initial record number
    	if ( dgnRec == null ){
    		dgnRec = new Rec( 2 );
    	} else if ( dgnRec.getRec() < 2 ){
    		dgnRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		dgnRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( dgnRec.getRec() > maxrec.getRec()) return false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
		file.getRecord( dataStruct, dgnRec );
		return true;
    }	
    
    public Rec getRec(){ 
    	///return dgnRec; this use to be it, however:
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we get the record value.
    	return new Rec( dgnRec.getRec());
    }
    
    public void setRec( Rec rec ){ dgnRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
       	file = null;
    	dgnRec = null;
    	maxrec = null;
    	return true;
    }

    /**
     * fillListbox() - Static routine to fill a listbox with Dgn Codes to select from.
     * 
     * Returns int number found.
     * 
     * @param Listbox
     * @return int
     */
    public static int fillListbox( Listbox lb, boolean blank ){
    	
    	int rec;			// record number
    	int	maxrec;			// max record number
    	int fnd = 0;		// number found
    	
 
    	// create blank entry if asked for
    	if ( blank ) ZkTools.appendToListbox( lb, "", null );
    	
    	
    	// create new blank LabBat instance
    	Dgn dgn = new Dgn();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_dgn, getRecordLength(), RandomFile.Mode.READONLY );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( dgn.dataStruct, rec );

            if (( dgn.getValidity() == Validity.VALID ) && ( (dgn.isValid() ? Dgn.Status.ACTIVE: Dgn.Status.INACTIVE) == Status.ACTIVE )){
            	
				ZkTools.appendToListbox( lb, String.format( "%-10.10s %-40.40s", dgn.getAbbr(), dgn.getDesc()), new Rec( rec ));
            	++fnd;
            }
    	}
    	file.close();
    	
    	return ( fnd );
    }
    
    
    
	public static int fillListbox( Listbox lb, String loinc, String abbr, String desc, Status status ){
    	
		if ( loinc != null ) loinc = loinc.trim();
    	if ( abbr != null ) abbr = abbr.trim().toLowerCase();
    	if ( desc != null ) desc = desc.trim().toLowerCase();
    	
    	
    	Dgn v = new Dgn();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_dgn, getRecordLength(), RandomFile.Mode.READONLY );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
					    
			file.getRecord( v.dataStruct, rec );			
/*			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if (( status != null ) && ( status != v.getStatus())) continue;
			
			// if searching by cvx code, is this a match?
			if (( loinc != null ) && ( ! loinc.equals( v.getLOINC()))) continue;
*/			
			// if searching by abbreviation, is this a match?
			if (( abbr != null ) && ( ! abbr.equals( v.getAbbr().toLowerCase()))) continue;
			
			// if searching by description, is this desc contained in either the short or full description?
			if (( desc != null ) && ( v.getDesc().toLowerCase().indexOf( desc ) < 0 )) continue;
			
			// add this item to the list
			ZkTools.appendToListbox( lb, v.getAbbr() + " " + v.getDesc(), new Rec( rec ));
			++num;
		}
		file.close();
		
		return num;
    }

    
    
    


}
