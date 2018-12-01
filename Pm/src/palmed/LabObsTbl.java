package palmed;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.Listbox;

import palmed.Par.Reason;
import palmed.Par.Severity;
import palmed.Par.Status;
import palmed.VaccineCVX.Entry;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.Validity;
import usrlib.ZkTools;





/*  observation record structure  */
//
//typedef	struct {
//
// 0	char	abbr[10];			/*  code abbreviation  */
// 10	char	desc[40];			/*  code description  */
//
// 50	char	units[16];			/*  report units text */

// 66	byte	cUnits;				// coded units
// 67	byte	dataType;			// data type, type of units
// 68	byte	dplaces;			// decimal places - number of decimal places for decimal units
// 69	byte	testType;			// test type
//	
// 70	char	normalRangeText[20];// normal range text for display
// 90	char	normalHigh[10];		// normal high
// 100	char	normalLow[10];		// normal low
// 110	char	absoluteMin[10];	// absolute min
// 120	char	absoluteMax[10];	// absolute max
//	
// 130	RECORD	submission;			/*  submission requirements  */
// 134	RECORD	procedure;			/*  test procedure  */
// 138	RECORD	interpret;			/*  result interpretation  */
// 142	RECORD	ptEdRec;			// patient education record number
//	
// 146	char	resource[40];		// pt ed resource
// 186	char	unused[40];
// 									//  NEEDED TO USE THIS SPACE FOR MU FOR NOW!!  // 146	char	values[8][10];		/*  names for specific values */
// 226	char	SNOMED[10];			// SNOMED code
// 236	char	LOINC[10];			// LOINC code
//
// 246	char	unused[8];			/*  unused for now  */
//
// 254	byte	Status;				// status flag
// 255	byte	Valid;				/* validity byte  */
//
//} LABOBS;
//
//	sizecheck( 256, sizeof( LABOBS ), "LABOBS" );



public class LabObsTbl {

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


	
	public enum DataType {
		
		WHOLE_NUMBERS( "Whole Numbers", 1 ),	
		DECIMAL( "Decimals", 2 ),
		NORMAL_ABNORMAL( "Normal/Abnormal", 3 ),
		POSITIVE_NEGATIVE( "Positive/Negative", 4 ),
		DISCRETE_LABELS( "Discrete Labels", 5 ),
		
		
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, DataType> lookup = new HashMap<Integer,DataType>();
		private static final Map<String, DataType> lookupLabel = new HashMap<String,DataType>();
		
		static {
			for ( DataType r : EnumSet.allOf(DataType.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		DataType ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static DataType get( int code ){ return lookup.get( code & 0xff ); }
		public static DataType get( String label ){ return lookupLabel.get( label ); }

	}

	
	public enum Units {

		NONE( "", 1 ),	
		PERCENT( "%", 2 ),
		
		COUNT( "Count", 3 ),
		K( "K", 4 ),
		M( "M", 5 ),
		
		MMOL_L( "mmol/L", 10 ),
		MOSM_KG( "mosm/kg", 15 ),
		
		G_DL( "g/dL", 20 ),
		MG_DL( "mg/dL", 21 ),
		UG_DL( "ug/dL", 22 ),
		NG_DL( "ng/dL", 23 ),
		PG_DL( "pg/dL", 24 ),
		
		UG_ML( "ug/mL", 30 ),
		NG_ML( "ng/mL", 31 ),

		K_UL( "K/uL", 11 ),
		M_UL( "M/uL", 12 ),

		G_L( "g/L", 40 ),
		NG_L( "ng/L", 41 ),

		ML_MIN( "mL/min", 50 ),
		FL( "fL", 51 ),
		PG( "pg", 52 ),
		UUG( "uug", 53 ),			// same as PG, 52 ????
		
		IU_L( "IU/L", 60 ),
		UIU_ML( "uIU/mL", 61 ),
		U_L( "U/L", 62 ),
		
		UNSPECIFIED( "unspecified", 99 );
		
		
		

		
		
		
		private String label;
		private int code;

		private static final Map<Integer, Units> lookup = new HashMap<Integer,Units>();
		private static final Map<String, Units> lookupLabel = new HashMap<String,Units>();
		
		static {
			for ( Units r : EnumSet.allOf(Units.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		Units ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Units get( int code ){ return lookup.get( code & 0xff ); }
		public static Units get( String label ){ return lookupLabel.get( label ); }

	}

	
	public enum TestType {
		
		CHEMISTRY( "Chemistry", 1 ),	
		HEMATOLOGY( "Hematology", 2 ),
		COAGULATION( "Coagulation", 3 ),
		MICROBIOLOGY( "Microbiology", 4 ),
		SEROLOGY( "Serology", 5 ),
		VIROLOGY( "Virology", 6 ),
		TOXICOLOGY( "Toxicology", 7 ),
		PATHOLOGY( "Pathology", 8 ),
		
		OTHER( "Other", 98 ),
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, TestType> lookup = new HashMap<Integer,TestType>();
		private static final Map<String, TestType> lookupLabel = new HashMap<String,TestType>();
		
		static {
			for ( TestType r : EnumSet.allOf(TestType.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		TestType ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static TestType get( int code ){ return lookup.get( code & 0xff ); }
		public static TestType get( String label ){ return lookupLabel.get( label ); }

	}

	

	
	// fields
    private byte[] dataStruct;
    private Rec rec;			// this Rec
    
	public static final String fnsub ="labobstb.ovd";
	public static final int recordLength = 256;
	
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    RandomFile file;
    int	maxrec;
    
    

    
    
    
    public LabObsTbl() {
    	allocateBuffer();
    }
	
    public LabObsTbl( Rec rec ){

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


    
    
    

	// char	units[50];		// units text, offset 50
    
    /**
     * getUnitsText()
     * 
     * Get units text from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getUnitsText(){
    	return StructHelpers.getString( dataStruct, 50, 16 ).trim();
    }

    /**
     * setUnitsText()
     * 
     * Set units text in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setUnitsText( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 50, 16 );
    	return ;
    }


    
    
    

	// byte	cUnits;			// coded units, offset 66
    
    /**
     * getUnitsCode()
     * 
     * Get coded units from dataStruct.
     * 
     * @param none
     * @return Units
     */
    public Units getCodedUnits(){
    	Units units = Units.get( dataStruct[66] & 0xff );
    	if ( units == null ) units = Units.UNSPECIFIED;
    	return units;
    }

    /**
     * setUnitsCode()
     * 
     * Set coded units in dataStruct.
     * 
     * @param Units
     * @return void
     */
    public void setCodedUnits( Units units ){
    	dataStruct[66] = (byte)( units.getCode() & 0xff );
    }


    
    

	// byte	dataType;			// data type, offset 67
    
    /**
     * getDataType()
     * 
     * Get data type from dataStruct.
     * 
     * @param none
     * @return Units
     */
    public DataType getDataType(){
    	DataType type = DataType.get( dataStruct[67] & 0xff );
    	if ( type == null ) type = DataType.UNSPECIFIED;
    	return type;
    }

    /**
     * setDataType()
     * 
     * Set data type in dataStruct.
     * 
     * @param SpecimenSource
     * @return void
     */
    public void setDataType( DataType type ){
    	dataStruct[67] = (byte)( type.getCode() & 0xff );
    }


    
    

	// byte	testType;			// data type, offset 69
    
    /**
     * getTestType()
     * 
     * Get test type from dataStruct.
     * 
     * @param none
     * @return Units
     */
    public TestType getTestType(){
    	TestType type = TestType.get( dataStruct[69] & 0xff );
    	if ( type == null ) type = TestType.UNSPECIFIED;
    	return type;
    }

    /**
     * setTestType()
     * 
     * Set test type in dataStruct.
     * 
     * @param Abnormal
     * @return void
     */
    public void setTestType( TestType type ){
    	dataStruct[69] = (byte)( type.getCode() & 0xff );
    }


    
    

	// byte	dplaces;			// decimal places, offset 68
    
    /**
     * getDecimalPlaces()
     * 
     * Get decimal places from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getDecimalPlaces(){
    	return (int) dataStruct[68] & 0xff;
    }

    /**
     * setDecimalPlaces()
     * 
     * Set decimal places in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setDecimalPlaces( int i ){
    	dataStruct[68] = (byte)( i & 0xff );
    }


    
    

	// char	normalRangeText[72];		// normal range text, offset 70
    
    /**
     * getNormalRangeText()
     * 
     * Get Normal Range Text from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getNormalRangeText(){
    	return StructHelpers.getString( dataStruct, 70, 20 ).trim();
    }

    /**
     * setNormalRangeText()
     * 
     * Set Normal Range Text in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setNormalRangeText( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 70, 20 );
    	return ;
    }


    
    
    
	// char	normalRangeText[92];		// normal range text, offset 90
    
    /**
     * getNormalHigh()
     * 
     * Get Normal High from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getNormalHigh(){
    	return StructHelpers.getString( dataStruct, 90, 10 ).trim();
    }

    /**
     * setNormalHigh()
     * 
     * Set Normal High in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setNormalHigh( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 90, 10 );
    	return ;
    }


    
    
    


	// char	normalLow[102];		// normal low, offset 100
    
    /**
     * getNormalLow()
     * 
     * Get Normal Low from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getNormalLow(){
    	return StructHelpers.getString( dataStruct, 100, 10 ).trim();
    }

    /**
     * setNormalLow()
     * 
     * Set Normal Low in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setNormalLow( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 100, 10 );
    	return ;
    }


    
    
    
	// char	absoluteMin[112];		// absolute min, offset 110
    
    /**
     * getAbsoluteMin()
     * 
     * Get Absolute Min from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getAbsoluteMin(){
    	return StructHelpers.getString( dataStruct, 110, 10 ).trim();
    }

    /**
     * setAbsoluteMin()
     * 
     * Set AbsoluteMin in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setAbsoluteMin( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 110, 10 );
    	return ;
    }


    
    
    
	// char	absoluteMax[122];		// absolute max, offset 120
    
    /**
     * getAbsoluteMax()
     * 
     * Get Absolute Max from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getAbsoluteMax(){
    	return StructHelpers.getString( dataStruct, 120, 10 ).trim();
    }

    /**
     * setAbsoluteMax()
     * 
     * Set Absolute Max in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setAbsoluteMax( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 120, 10 );
    	return ;
    }


    
    
    
	// char	SNOMED[10];		// SNOMED, offset 226
    
    /**
     * getSNOMED()
     * 
     * Get SNOMED from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getSNOMED(){
    	return StructHelpers.getString( dataStruct, 226, 10 ).trim();
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
    	StructHelpers.setStringPadded( s, dataStruct, 226, 10 );
    	return ;
    }


    
    
    

	// char	LOINC[10];		// LOINC, offset 236
    
    /**
     * getLOINC()
     * 
     * Get LOINC from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getLOINC(){
    	return StructHelpers.getString( dataStruct, 236, 10 ).trim();
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
    	StructHelpers.setStringPadded( s, dataStruct, 236, 10 );
    	return ;
    }


    
    
    



	// char	resource[40];		// pt ed resources, offset 146
    
    /**
     * getResource()
     * 
     * Get pt ed resource from dataStruct.
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
     * @param String
     * @return void
     */
    public void setResource( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 146, 40 );
    	return ;
    }


    /**
     * getResource()
     * 
     * Static method to get a LabObsTbl pt ed resource given a record number
     * @param rec
     * @return String
     */
    public static String getResource( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "getResource( rec ) bad rec" ); return null; }
		LabObsTbl p = new LabObsTbl( rec );
		return p.getResource();
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

    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[255] = (byte)( Validity.HIDDEN.getCode() & 0xff );}
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[255] ) == Validity.HIDDEN ); }
    
    
    
    
 

    
    
    
    
	// open
    public static LabObsTbl open(){
    	
    	// create new ProbTbl object
    	LabObsTbl p = new LabObsTbl();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY );
		p.maxrec = p.file.getMaxRecord();
		p.rec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "LabObsTbl.getNext() file not open" );

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
    	LabObsTbl labObsTbl = new LabObsTbl();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( labObsTbl.dataStruct, rec );

            if (( labObsTbl.getValid() == Validity.VALID ) && ( labObsTbl.getStatus() == Status.ACTIVE )){
            	
				ZkTools.appendToListbox( lb, String.format( "%-10.10s %-40.40s", labObsTbl.getAbbr(), labObsTbl.getDesc()), new Rec( rec ));
            	++fnd;
            }
    	}
    	file.close();
    	
    	return ( fnd );
    }

    
    

    
    
    
    


    public void dump(){
    	StructHelpers.dump( "LabObsTbl", dataStruct, getRecordLength());
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



    
    
    public static int fillListbox( Listbox lb, String loinc, String abbr, String desc, Status status ){
    	
    	if ( loinc != null ) loinc = loinc.trim();
    	if ( abbr != null ) abbr = abbr.trim().toLowerCase();
    	if ( desc != null ) desc = desc.trim().toLowerCase();
    	
    	
    	LabObsTbl v = new LabObsTbl();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			// reset some things
		    v.noteText = null;
		    v.flgNoteTxt = false;
		    v.flgNoteTxtRead = false;
		    
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
    
    
    public static Rec searchLoinc( String loinc ){ return search( loinc, null, null, null ); }
    public static Rec searchAbbr( String abbr ){ return search( null, abbr, null, null ); }
    public static Rec searchDesc( String desc ){ return search( null, null, desc, null ); }
    public static Rec searchSNOMED( String snomed ){ return search( null, null, null, snomed ); }
    
    public static Rec search( String loinc, String abbr, String desc, String snomed ){
   
    	if ( loinc != null ) loinc = loinc.trim();
    	if ( abbr != null ) abbr = abbr.trim().toUpperCase();
    	if ( desc != null ) desc = desc.trim();
    	if ( snomed != null ) snomed = snomed.trim();
    	
    	Rec obsRec = null;
    	
    	
    	LabObsTbl v = new LabObsTbl();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), RandomFile.Mode.READONLY );
		int maxrec = file.getMaxRecord();
		
		int num = 0;
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			// reset some things
		    v.noteText = null;
		    v.flgNoteTxt = false;
		    v.flgNoteTxtRead = false;
		    
			file.getRecord( v.dataStruct, rec );			
/*			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if (( status != null ) && ( status != v.getStatus())) continue;
*/			
			// if searching by loinc code, is this a match?
			if (( loinc != null ) && ( ! loinc.equals( v.getLOINC()))) continue;
			
			// if searching by snomed code, is this a match?
			if (( snomed != null ) && ( ! snomed.equals( v.getSNOMED()))) continue;
			
			// if searching by abbreviation, is this a match?
			if (( abbr != null ) && ( ! abbr.equals( v.getAbbr()))) continue;
			
			// if searching by description, is this desc contained in either the short or full description?
			if (( desc != null ) && ( ! v.getDesc().equalsIgnoreCase( desc ))) continue;
			
			// found one
			obsRec = new Rec( rec );
			++num;
		}
		file.close();
		
		return obsRec;
    }

	
    
    
    

	
}


	/**/

