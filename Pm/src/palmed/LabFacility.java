package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Listbox;

import usrlib.Address;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;
import usrlib.ZkTools;

/*  lab facility structure  */
//
//typedef	struct {
//
// 0	char	abbr[10];			/*  code abbreviation  */
// 10	char	desc[40];			/*  code description  */
//
// 50	char	Address;			// SNOMED code
//
// 138	char	unused[116];		/*  unused for now  */
//
// 254	byte	Status;				// status flag
// 255	byte	Valid;				/* validity byte  */
//
//} LABFacility;
//
//	sizecheck( 256, sizeof( LABFacility ), "LABFacility" );


public class LabFacility {

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
    
	public static final String fnsub ="labfac.ovd";
	public static final int recordLength = 256;
	
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    RandomFile file;
    int	maxrec;
    
    

    
    
    
    public LabFacility() {
    	allocateBuffer();
    }
	
    public LabFacility( Rec rec ){

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


    
    
    

    

    
    
    
    
    
	// Address	Address;		// Address struct, offset 50
    
    /**
     * getAddress()
     * 
     * Get Address from dataStruct.
     * 
     * @param none
     * @return Address
     */
    public Address getAddress(){
    	return new Address( dataStruct, 50 );
    }

    /**
     * setAddress()
     * 
     * Set Address in dataStruct.
     * 
     * @param Address
     * @return void
     */
    public void setAddress( Address addr ){
    	addr.toData( dataStruct, 50 );
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
    public static LabFacility open(){
    	
    	// create new object
    	LabFacility p = new LabFacility();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
		p.maxrec = p.file.getMaxRecord();
		p.rec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "getNext() file not open" );

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
    	StructHelpers.dump( "LabFacility", dataStruct, getRecordLength());
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
    	
    	
    	// create new blank LabFacility instance
    	LabFacility labFacility = new LabFacility();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( labFacility.dataStruct, rec );

            if (( labFacility.getValid() == Validity.VALID ) && ( labFacility.getStatus() == Status.ACTIVE )){
            	
				ZkTools.appendToListbox( lb, String.format( "%-10.10s %-40.40s", labFacility.getAbbr(), labFacility.getDesc()), new Rec( rec ));
            	++fnd;
            }
    	}
    	file.close();
    	
    	return ( fnd );
    }

    
    
    
    public static Rec search( String strSearch ){
    	
    	int rec;			// record number
    	int	maxrec;			// max record number
    	int fnd = 0;		// number found
    	
    	Rec facRec = null;
    	
    	// create new blank LabFacility instance
    	LabFacility fac = new LabFacility();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY  );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( fac.dataStruct, rec );

            if (( fac.getValid() != Validity.VALID ) || ( fac.getStatus() != Status.ACTIVE )) continue;

            if (( fac.getAbbr().equalsIgnoreCase( strSearch )) || ( fac.getDesc().startsWith( strSearch )) ){
            	// match found
            	facRec = new Rec( rec );
            	break;
            }
    	}
    	file.close();
    	
    	return facRec;
    }


}
