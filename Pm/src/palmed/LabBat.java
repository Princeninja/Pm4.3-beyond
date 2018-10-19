package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Listbox;

import palmed.LabBat.Status;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;
import usrlib.ZkTools;



/*  lab battery structure  */
//
//typedef	struct {
//
// 0	char	abbr[10];			/*  code abbreviation  */
// 10	char	desc[40];			/*  code description  */
//
// 50	char	SNOMED[10];			// SNOMED code
// 60	char	LOINC[10];			// LOINC code
//
// 70	RECORD	submission;			/*  submission requirements  */
// 74	RECORD	procedure;			/*  test procedure  */
// 78	RECORD	interpret;			/*  result interpretation  */
// 82	RECORD	ptEdRec;			// patient education record number
//	
// 86	RECORD	obsRecs[32];		/*  records of observations  */
//
// 214	char	unused[40];			/*  unused for now  */
//
// 254	byte	Status;				// status flag
// 255	byte	Valid;				/* validity byte  */
//
//} LABTST;
//
//	sizecheck( 256, sizeof( LABOBS ), "LABBAT" );


public class LabBat {

	public enum Status {
		
		ACTIVE( "Active", 'A' ),	
		INACTIVE( "Inactive", 'I' ),
		CHANGED( "Superceeded", 'E' );
		
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


	
	

		
		

	

	
	// fields
    private byte[] dataStruct;
    private Rec rec;			// this Rec
    
	public static final String fnsub ="labbat.ovd";
	public static final int recordLength = 256;
	
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    RandomFile file;
    int	maxrec;
    
    

    
    
    
    public LabBat() {
    	allocateBuffer();
    }
	
    public LabBat( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read record
    	if ( Rec.isValid( rec )){
    		this.read( rec );
    	}
    	
    	// set local copy of rec
    	this.rec = rec;
    }


	// read PAR
    
    public void read( Rec rec ){
    	
    	flgNoteTxtRead = false;				// haven't read note text record yet
    	
		if ( ! Rec.isValid( rec )) return;		
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fnsub, getRecordLength());
   }	


    
    
    public Rec writeNew(){
    	
     	
/*    	// handle note text
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
*/
    	
    	Rec rec = RandomFile.writeRec( null, dataStruct, Pm.getOvdPath(), fnsub, getRecordLength());
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "writeNew() bad rec" );

    	return rec;
    }
    
    
    
    public boolean write( Rec rec ){
    	
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "write() bad rec" );

/*    	// handle addlSig text
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
*/
    	
    	// write record
        RandomFile.writeRec( rec, dataStruct, Pm.getOvdPath(), fnsub, getRecordLength());
        return true;
    }    
    
    

	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recordLength ); }
    
    private void allocateBuffer(){
        dataStruct = new byte[recordLength];
    }

    public Rec getRec() { return rec; }
    
    
    
    
   
    
    
	// char	abbr[10];		// abbreviation, offset 0
    
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
     * @param String
     * @return void
     */
    public void setAbbr( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 0, 10 );
    	return ;
    }


    
    
    
	// char	desc[40];		// description, offset 10
    
    /**
     * getDesc()
     * 
     * Get description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 10, 40 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set description in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 10, 40 );
    	return ;
    }


    
    
    

    

    
    
    
    
    
	// char	SNOMED[10];		// SNOMED, offset 50
    
    /**
     * getSNOMED()
     * 
     * Get SNOMED from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getSNOMED(){
    	return StructHelpers.getString( dataStruct, 50, 10 ).trim();
    }

    /**
     * setSNOMED()
     * 
     * Set SNOMED in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setSNOMED( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 50, 10 );
    	return ;
    }


    
    
    

	// char	LOINC[10];		// LOINC, offset 60
    
    /**
     * getLOINC()
     * 
     * Get LOINC from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getLOINC(){
    	return StructHelpers.getString( dataStruct, 60, 10 ).trim();
    }

    /**
     * setLOINC()
     * 
     * Set LOINC in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setLOINC( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 60, 10 );
    	return ;
    }


    
    
    



	// Rec	obsRecs[32];		// LOINC, offset 86
    
    /**
     * getObsRecs()
     * 
     * Get LabObsTbl Recs from dataStruct.
     * 
     * @param none
     * @return Rec[]
     */
    public Rec[] getObsRecs(){
    	Rec[] rec = new Rec[32];
    	for ( int i = 0; i < 32; ++i )
    		rec[i] = Rec.fromInt( dataStruct, 86 + (i * 4));
		return rec;
    }

    /**
     * setObsRecs()
     * 
     * Set LabObsTbl Recs in dataStruct.
     * 
     * @param Rec[]
     * @return void
     */
    public void setObsRecs( Rec[] rec ){
    	int max = ( rec.length > 32 ) ? 32: rec.length;
    	int i;
    	for ( i = 0; i < max; ++i )
    		rec[i].toInt( dataStruct, 86 + (i * 4));
    	for ( ; i < 32; ++i )
    		(new Rec( 0 )).toInt( dataStruct, 86 + (i * 4));
    	return ;
    }


    
    
    


	// Rec	obsRecs[32];		// Obs Recs, offset 86
    
    /**
     * getObsRec()
     * 
     * Get a single LabObsTbl Rec from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getObsRec( int i ){
    	return Rec.fromInt( dataStruct, 86 + (i * 4));
    }

    /**
     * setObsRec()
     * 
     * Set a single LabObsTbl Rec in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setObsRec( Rec rec, int i ){
     	rec.toInt( dataStruct, 86 + (i * 4));
    	return ;
    }


    
    
    




    
    
	// char	status;			// record status, offset 254
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	return Status.get( dataStruct[254] & 0xff );
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param Member
     * @return void
     */
    public void setStatus( Status status ){
    	dataStruct[254] = (byte)( status.getCode() & 0xff );
    }


    
    
    
    
	// char	valid;			// validity byte, offset 255
    
    /**
     * getValid()
     * 
     * Get validity from dataStruct.
     * 
     * @param none
     * @return Validity
     */
    public Validity getValid(){
    	return Validity.get( dataStruct[255] & 0xff );
    }

    /**
     * setValid()
     * 
     * Set validity in dataStruct.
     * 
     * @param Validity
     * @return void
     */
    public void setValid( Validity valid ){
    	dataStruct[255] = (byte)( valid.getCode() & 0xff );
    }


    
    
    
 

    
    
    
    
	// open
    public static LabBat open(){
    	
    	// create new ProbTbl object
    	LabBat p = new LabBat();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
		p.maxrec = p.file.getMaxRecord();
		p.rec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "LabBat.getNext() file not open" );

        // set initial record number
    	if ( rec == null ){
    		rec = new Rec( 2 );
    	} else if ( rec.getRec() < 2 ){
    		rec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		rec.increment();
    	}
    	
    	// is this past maxrec
    	if (rec.getRec() > maxrec ) return false;
 
    	// read record
		file.getRecord( dataStruct, rec );
		return true;
    }	
    
        
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
    	file = null;
    	rec = null;
    	maxrec = 0;
    	return true;
    }

    
    
    


    
    
    
    
    
    
    
    
    


    public void dump(){
    	StructHelpers.dump( "LabBat", dataStruct, getRecordLength());
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
    	return String.format( "%s %s", this.getAbbr(), this.getDesc());
    }



    /**
     * fillListbox() - Static routine to fill a listbox with providers to select from.
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
    	LabBat labBat = new LabBat();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( labBat.dataStruct, rec );

            if (( labBat.getValid() == Validity.VALID ) && ( labBat.getStatus() == Status.ACTIVE )){
            	
				ZkTools.appendToListbox( lb, String.format( "%-10.10s %-40.40s", labBat.getAbbr(), labBat.getDesc()), new Rec( rec ));
            	++fnd;
            }
    	}
    	file.close();
    	
    	return ( fnd );
    }
    
    

    public static int fillListbox( Listbox lb, String loinc, String abbr, String desc, Status status ){
    	
    	if ( loinc != null ) loinc = loinc.trim();
    	if ( abbr != null ) abbr = abbr.trim().toUpperCase();
    	if ( desc != null ) desc = desc.trim();
    	
    	
    	LabBat v = new LabBat();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			// reset some things
		    v.noteText = null;
		    v.flgNoteTxt = false;
		    v.flgNoteTxtRead = false;
		    
			file.getRecord( v.dataStruct, rec );			
			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if (( status != null ) && ( status != v.getStatus())) continue;
			
			// if searching by cvx code, is this a match?
			if (( loinc != null ) && ( ! loinc.equals( v.getLOINC()))) continue;
			
			// if searching by abbreviation, is this a match?
			if (( abbr != null ) && ( ! abbr.equals( v.getAbbr()))) continue;
			
			// if searching by description, is this desc contained in either the short or full description?
			if (( desc != null ) && ( v.getDesc().toUpperCase().indexOf( desc ) < 0 )) continue;
			
			// add this item to the list
			ZkTools.appendToListbox( lb, desc, new Rec( rec ));
			++num;
		}
		
		file.close();
		
		return num;
    }
    
    
  
    
    public static Rec searchLoinc( String loinc ){ return search( loinc, null, null, null ); }
    public static Rec searchAbbr( String abbr ){ return search( null, abbr, null, null ); }
    public static Rec searchDesc( String desc ){ return search( null, null, desc, null ); }
    public static Rec searchSNOMED( String snomed ){ return search( null, null, null, snomed ); }
    
    public static Rec search( String loinc, String abbr, String desc, String snomed ){

    	Rec batRec = null;
    	
    	
    	if ( loinc != null ) loinc = loinc.trim();
    	if ( abbr != null ) abbr = abbr.trim().toUpperCase();
    	if ( desc != null ) desc = desc.trim();
    	if ( snomed != null ) snomed = snomed.trim();

    	
    	LabBat v = new LabBat();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			// reset some things
		    v.noteText = null;
		    v.flgNoteTxt = false;
		    v.flgNoteTxtRead = false;
		    
			file.getRecord( v.dataStruct, rec );			
			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if ( v.getStatus() != Status.ACTIVE ) continue;
			
			// if searching by cvx code, is this a match?
			if (( loinc != null ) && ( ! loinc.equals( v.getLOINC()))) continue;
			
			// if searching by abbreviation, is this a match?
			if (( abbr != null ) && ( ! abbr.equals( v.getAbbr()))) continue;
			
			// if searching by description, is this desc contained in either the short or full description?
			if (( desc != null ) && ( ! v.getDesc().equalsIgnoreCase( desc ))) continue;
			
			// if searching by cvx code, is this a match?
			if (( snomed != null ) && ( ! snomed.equals( v.getSNOMED()))) continue;
			
			// add this item to the list
			++num;
			batRec = new Rec( rec );
			break;
		}
		
		file.close();
		
		return batRec;
    }
    
    
  
	
}


	/**/


