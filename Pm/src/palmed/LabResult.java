package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Listbox;

import palmed.LabObsTbl.DataType;
import palmed.LabObsTbl.TestType;
import palmed.LabResult.Status;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.Time;
import usrlib.Validity;
import usrlib.ZkTools;



/*  observation record structure  */
//
//typedef	struct {
//
// 0	LLHDR	llhdr;				// linked list header
// 8	Rec		ptRec;				// patient record number
// 12	Date	date;				// date of result
// 16	Time	time;				// time of result (2 byte BCD)
//
// 18	Reca	orderReca;			// the order this satisfies
// 22	Rec		labBatRec;			// the lab battery this is associated with
// 26	Rec		labObsRec;			// the lab observation table entry this is associated with
// 30	Reca	resultNoteReca;		// note text with result


// 34	byte	cUnits;				// coded units
// 35	byte	dataType;			// data type, type of units
// 36	byte	dplaces;			// decimal places - number of decimal places for decimal units
// 37	byte	sequence;			// sequence in order (for batteries)
//	
// 38	char	result[20];			// result
//
// 58	byte	abnormalFlag;		// abnormal result flag
// 59	byte	resultStatus;		// result status flag
//
// 60	char	unitsText[10];
// 70	char	normalRange[20];  // normal range text for display
//
// 90	byte	cSource;			// coded specimen source
// 91	byte	cCondition;			// coded specimen condition
// 92	byte	unused[2];
//
// 94	Reca	sourceTextReca;
// 98	Reca	conditionTextReca;
// 102	Rec		labFacilityRec;		// lab facility test was done at
//
// 106	char	unused[20];			/*  unused for now  */
//
// 126	byte	Status;				// status flag
// 127	byte	Valid;				/* validity byte  */
//
//} LABOBS;
//
//	sizecheck( 128, sizeof( LABRESULT ), "LABRESULT" );







public class LabResult implements LLItem {




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


	
	
	public enum SpecimenSource {
		
		NONE( "", 0 ),	
		VENOUS( "Venipuncture", 1 ),	
		ARTERIAL( "Arterial Blood", 2 ),
		ARTERIAL_CATHETER( "Arterial Catheter", 3 ),
		VENOUS_LINE( "Venous Line", 4 ),
		URINE( "Urine, clean catch", 5 ),
		URINE_CATH( "Urine, catheter", 6 ),
		SERUM( "Serum", 7 ),

		OTHER( "Other", 98 ),
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, SpecimenSource> lookup = new HashMap<Integer,SpecimenSource>();
		private static final Map<String, SpecimenSource> lookupLabel = new HashMap<String,SpecimenSource>();
		
		static {
			for ( SpecimenSource r : EnumSet.allOf(SpecimenSource.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		SpecimenSource ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static SpecimenSource get( int code ){ return lookup.get( code & 0xff ); }
		public static SpecimenSource get( String label ){ return lookupLabel.get( label ); }

	}

	

	public enum SpecimenCondition {
		
		NONE( "", 0 ),	
		GOOD( "Good", 1 ),	
		HEMOLYZED( "Hemolyzed", 2 ),
		SPOILED( "Spoiled", 3 ),
		POSITIVE_NEGATIVE( "Positive/Negative", 4 ),

		OTHER( "Other", 98 ),
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, SpecimenCondition> lookup = new HashMap<Integer,SpecimenCondition>();
		private static final Map<String, SpecimenCondition> lookupLabel = new HashMap<String,SpecimenCondition>();
		
		static {
			for ( SpecimenCondition r : EnumSet.allOf(SpecimenCondition.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		SpecimenCondition ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static SpecimenCondition get( int code ){ return lookup.get( code & 0xff ); }
		public static SpecimenCondition get( String label ){ return lookupLabel.get( label ); }

	}

	

		
	public enum Abnormal {
		
		NONE( "", 0 ),
		NORMAL( "Normal", 1 ),	
		ABNORMAL( "Abnormal", 2 ),
		CRITICAL( "Critical", 3 ),
		CRITICAL_HIGH( "Critical High", 4 ),
		CRITICAL_LOW( "Critical Low", 5 ),		
		
		OTHER( "Other", 98 ),				
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, Abnormal> lookup = new HashMap<Integer,Abnormal>();
		private static final Map<String, Abnormal> lookupLabel = new HashMap<String,Abnormal>();
		
		static {
			for ( Abnormal r : EnumSet.allOf(Abnormal.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		Abnormal ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Abnormal get( int code ){ return lookup.get( code & 0xff ); }
		public static Abnormal get( String label ){ return lookupLabel.get( label ); }

	}

	
	public enum ResultStatus {
		
		NONE( "", 0 ),
		FINAL( "Final Results", 1 ),	
		PENDING( "Pending", 2 ),
		CANCELLED( "Cancelled", 3 ),
		INCOMPLETE( "Incomplete", 4 ),
		IN_PROGRESS( "In Progress", 5 ),
		CORRECTED( "Corrected Results", 6 ),
		PRELIMINARY( "Preliminary Results", 7 ),
		
		OTHER( "Other", 98 ),
		UNSPECIFIED( "Unspecified", 99 );
		
		
		
		private String label;
		private int code;

		private static final Map<Integer, ResultStatus> lookup = new HashMap<Integer,ResultStatus>();
		private static final Map<String, ResultStatus> lookupLabel = new HashMap<String,ResultStatus>();
		
		static {
			for ( ResultStatus r : EnumSet.allOf(ResultStatus.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		ResultStatus ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static ResultStatus get( int code ){ return lookup.get( code & 0xff ); }
		public static ResultStatus get( String label ){ return lookupLabel.get( label ); }

	}

	



	

	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="labres%02d.med";
	public static final int recordLength = 128;
	
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    RandomFile file;
    int	maxrec;
    
    

    
    
    
    public LabResult() {
    	allocateBuffer();
    }
	
    public LabResult( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read 
    
    public void read( Reca reca ){
    	
    	flgNoteTxtRead = false;				// haven't read note text record yet
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	
     	
/*    	// handle note text
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
*/
    	
    	Reca reca = RecaFile.newReca( Reca.todayVol(), dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

/*    	// handle addlSig text
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
*/
    	
    	// write record
        RecaFile.writeReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }    
    
    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getLabResultReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setLabResultReca(reca); }
				}
		);
		return reca;
    }
    
    


	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recordLength ); }
    
    private void allocateBuffer(){
        dataStruct = new byte[recordLength];
    }

    public Reca getReca() { return reca; }
    
    
    
    
   
    
    
    
    
    // LLHDR hdr;		// linked list header
    
    /**
     * getLLHdr()
     * 
     * Get Linked list header LLHDR from dataStruct.
     * 
     * @param none
     * @return LLHdr
     */
    public LLHdr getLLHdr(){
    	return LLHdr.fromLLHdr(dataStruct, 0);
    }

    /**
     * setLLHdr()
     * 
     * Set Linked list header LLHDR from dataStruct.
     * 
     * @param LLHDR		linked list header
     * @return void
     */
    public void setLLHdr( LLHdr llhdr ){
    	llhdr.toLLHdr(dataStruct, 0);
    	return ;
    }


    
    
    
    // DATE Date;			// date, offset 12    
    
    /**
     * getDate()
     * 
     * Get date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDate(){
       	return StructHelpers.getDate( dataStruct, 12 );
    }

    /**
     * setDate()
     * 
     * Set date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 12 );
        return;
    }


    
    
    
	// RECORD PtRec;		// patient record number, offset 8
    
    /**
     * getPtRec()
     * 
     * Get patient record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPtRec(){
    	return Rec.fromInt( dataStruct, 8 );
    }

    /**
     * setPtRec()
     * 
     * Set patient record in dataStruct.
     * 
     * @param Rec		patient record number
     * @return void
     */
    public void setPtRec( Rec ptRec ){
    	ptRec.toInt( dataStruct, 8 );
    }


    
    
    

    // Time time;			// time, offset 16   
    
    /**
     * getTime()
     * 
     * Get time from dataStruct.
     * 
     * @param none
     * @return usrlib.Time
     */
    public usrlib.Time getTime(){
       	return Time.fromBCD( dataStruct, 16, Time.BCDLEN.HHMM );
    }

    /**
     * setTime()
     * 
     * Set time in dataStruct.
     * 
     * @param usrlib.Time	
     * @return void
     */
    public void setTime( usrlib.Time time ){
        if ( time != null ) time.toBCD( dataStruct, 16, Time.BCDLEN.HHMM );
        return;
    }


    
    
    

    
    
    
    
    
    



    
    
    


	// Rec	labObsRec;		// lab Obs Rec, offset 26
    
    /**
     * getLabObsRec()
     * 
     * Get a single LabObsTbl Rec from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getLabObsRec(){
    	return Rec.fromInt( dataStruct, 26 );
    }

    /**
     * setLabObsRec()
     * 
     * Set a single LabObsTbl Rec in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setLabObsRec( Rec rec ){
     	rec.toInt( dataStruct, 26 );
    	return ;
    }


    
    
    
	// Rec	labBatRec;		// lab Bat Rec, offset 22
    
    /**
     * getLabBatRec()
     * 
     * Get a Lab Bat Rec from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getLabBatRec(){
    	return Rec.fromInt( dataStruct, 22 );
    }

    /**
     * setLabBatRec()
     * 
     * Set a Lab Bat Rec in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setLabBatRec( Rec rec ){
    	if ( rec == null ) rec = new Rec( 0 );
     	rec.toInt( dataStruct, 22 );
    	return ;
    }


    
    
    
	// byte	cSource;			// coded Specimen Source, offset 90
    
    /**
     * getSpecimenSource()
     * 
     * Get Specimen ource from dataStruct.
     * 
     * @param none
     * @return SpecimenSource
     */
    public SpecimenSource getSpecimenSource(){
    	SpecimenSource type = SpecimenSource.get( dataStruct[90] & 0xff );
    	if ( type == null ) type = SpecimenSource.UNSPECIFIED;
    	return type;
    }

    /**
     * setSpecimenSource()
     * 
     * Set Specimen Source in dataStruct.
     * 
     * @param SpecimenSource
     * @return void
     */
    public void setSpecimenSource( SpecimenSource type ){
    	if ( type == null ) type = SpecimenSource.NONE;
    	dataStruct[90] = (byte)( type.getCode() & 0xff );
    }


    
    

	// byte	cCondition;			// coded Specimen Condition, offset 91
    
    /**
     * getSpecimenCondition()
     * 
     * Get Specimen Condition from dataStruct.
     * 
     * @param none
     * @return SpecimenCondition
     */
    public SpecimenCondition getSpecimenCondition(){
    	SpecimenCondition type = SpecimenCondition.get( dataStruct[91] & 0xff );
    	if ( type == null ) type = SpecimenCondition.UNSPECIFIED;
    	return type;
    }

    /**
     * setSpecimenCondition()
     * 
     * Set Specimen Condition in dataStruct.
     * 
     * @param SpecimenCondition
     * @return void
     */
    public void setSpecimenCondition( SpecimenCondition type ){
    	if ( type == null ) type = SpecimenCondition.NONE;
    	dataStruct[91] = (byte)( type.getCode() & 0xff );
    }


    
    
	// byte	AbnormalFlag;			// coded AbnormalFlag, offset 58
    
    /**
     * getAbnormalFlag()
     * 
     * Get AbnormalFlag from dataStruct.
     * 
     * @param none
     * @return Abnormal
     */
    public Abnormal getAbnormalFlag(){
    	Abnormal type = Abnormal.get( dataStruct[58] & 0xff );
    	if ( type == null ) type = Abnormal.UNSPECIFIED;
    	return type;
    }

    /**
     * setAbnormalFlag()
     * 
     * Set AbnormalFlag in dataStruct.
     * 
     * @param Abnormal
     * @return void
     */
    public void setAbnormalFlag( Abnormal type ){
    	if ( type == null ) type = Abnormal.NONE;
    	dataStruct[58] = (byte)( type.getCode() & 0xff );
    }


    
    

	// byte	ResultStatus;			// coded Result Status (final, pending, etc), offset 59
    
    /**
     * getResultStatus()
     * 
     * Get ResultStatus from dataStruct.
     * 
     * @param none
     * @return ResultStatus
     */
    public ResultStatus getResultStatus(){
    	ResultStatus type = ResultStatus.get( dataStruct[59] & 0xff );
    	if ( type == null ) type = ResultStatus.UNSPECIFIED;
    	return type;
    }

    /**
     * setResultStatus()
     * 
     * Set Result Status in dataStruct.
     * 
     * @param ResultStatus
     * @return void
     */
    public void setResultStatus( ResultStatus type ){
    	if ( type == null ) type = ResultStatus.NONE;
    	dataStruct[59] = (byte)( type.getCode() & 0xff );
    }


    
    


	// Rec	labFacilityRec;		// lab facility rec, offset 102
    
    /**
     * getLabFacilityRec()
     * 
     * Get a LabFacility Rec from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getLabFacilityRec(){
    	return Rec.fromInt( dataStruct, 102 );
    }

    /**
     * setLabFacilityRec()
     * 
     * Set a single LabFacility Rec in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setLabFacilityRec( Rec rec ){
    	if ( rec == null ) rec = new Rec( 0 );
     	rec.toInt( dataStruct, 102 );
    	return ;
    }


    
    
    



	// Reca	sourceTextReca;		// specimen source text Reca, offset 94
    
    /**
     * getSourceTextReca()
     * 
     * Get a specimen Source text Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getSourceTextReca(){
    	return Reca.fromReca( dataStruct, 94 );
    }

    /**
     * setSourceTextReca()
     * 
     * Set a specimen Source text Reca in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setSourceTextReca( Reca reca ){
     	reca.toReca( dataStruct, 94 );
    	return ;
    }


    public void setSourceText( String s ){
    	return;
    }
    
    public String getSourceText(){
    	return "";
    }
    
   
    
    

	// Reca	conditionTextReca;		// specimen condition text Reca, offset 98
    
    /**
     * getConditionTextReca()
     * 
     * Get a specimen Condition text Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getConditionTextReca(){
    	return Reca.fromReca( dataStruct, 98 );
    }

    /**
     * setConditionTextReca()
     * 
     * Set a specimen Condition text Reca in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setConditionTextReca( Reca reca ){
     	reca.toReca( dataStruct, 98 );
    	return ;
    }


    public void setConditionText( String s ){
    	return;
    }
    
    public String getConditionText(){
    	return "";
    }
    
    

	// Reca	resultNoteReca;		// ResultNote text Reca, offset 30
    
    /**
     * getResultNoteReca()
     * 
     * Get a result note text Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getResultNoteReca(){
    	return Reca.fromReca( dataStruct, 30 );
    }

    /**
     * setResultNoteReca()
     * 
     * Set a ResultNote text Reca in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setResultNoteReca( Reca reca ){
     	reca.toReca( dataStruct, 30 );
    	return ;
    }


    public void setResultNoteText( String s ){
    	return;
    }
    
    public String getResultNoteText(){
    	return "";
    }
    
   
    
    
	// Reca	orderReca;		// order Reca, offset 18
    
    /**
     * getOrderReca()
     * 
     * Get a Order Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getOrderReca(){
    	return Reca.fromReca( dataStruct, 18 );
    }

    /**
     * setOrderReca()
     * 
     * Set a Order Reca in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setOrderReca( Reca reca ){
     	reca.toReca( dataStruct, 18 );
    	return ;
    }


    
    
    
    
    
    
	// char	result[20];		// result text, offset 38
    
    /**
     * getResult()
     * 
     * Get result from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getResult(){
    	return StructHelpers.getString( dataStruct, 38, 20 ).trim();
    }

    /**
     * setResult()
     * 
     * Set result in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setResult( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 38, 20 );
    	return ;
    }


    
    
    

	// char	unitsText[10];		// result units text, offset 60
    
    /**
     * getUnitsText()
     * 
     * Get UnitsText from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getUnitsText(){
    	return StructHelpers.getString( dataStruct, 60, 10 ).trim();
    }

    /**
     * setUnitsText()
     * 
     * Set UnitsText in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setUnitsText( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 60, 10 );
    	return ;
    }


    
    
    

    
  
 
    
	// char	status;			// record status, offset 126
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	return Status.get( dataStruct[126] & 0xff );
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
    	dataStruct[126] = (byte)( status.getCode() & 0xff );
    }


    
    
    
    
	// char	valid;			// validity byte, offset 127
    
    /**
     * getValid()
     * 
     * Get validity from dataStruct.
     * 
     * @param none
     * @return Validity
     */
    public Validity getValid(){
    	return Validity.get( dataStruct[127] & 0xff );
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
    	dataStruct[127] = (byte)( valid.getCode() & 0xff );
    }


    
    
    
 

    
    
    
    
/**	// open
    public static LabResult open(){
    	
    	// create new ProbTbl object
    	LabResult p = new LabResult();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fnsub, getRecordLength(), "r" );
		p.maxrec = p.file.getMaxRecord();
		p.rec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "LabResult.getNext() file not open" );

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
*/
    
    
    


    
    
    
    
    
    
    
    
    


    public void dump(){
    	StructHelpers.dump( "LabResult", dataStruct, getRecordLength());
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
    	return String.format( "%s %s", this.getDate().getPrintable(9), "lab result" );
    }



	
}


	/**/



