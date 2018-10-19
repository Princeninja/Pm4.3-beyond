package palmed;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.HxObGyn.ObNotes;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.Time;

//from the C header file palmed/immune.h

//typedef struct {
//
// 0	LLHDR	hdr;			/*  history linked list header  */
//
// 8	DATE	Date;			/*  immunization date  */
// 12	TIME	time;			// NEW - time of administration (was DATE stopDate)
// 14	char	unused[2];		// NEW - unused
// 16	RECORD	PtRec;			/*  patient record number  */
//
// 20	char	Description[40];	/*  vaccine name  */
// 60	char	LotNumber[16];		// vaccine lot number
//
// 76	Rec		providerID;		/*  treating doctor  */
// 80	Date	expDate;		// expiration date
// 84	char	site[10];		// site (body part) of administration
//
// 94	byte	flgMisc;		// flg misc vaccine entered (not coded)
//								//    (note: a valid entry here (Y/N) indicates a new style record)
// 95	byte	Status;				/*  status or record  */
//
//	} IMMUNE;

/*	sizecheck( 96, sizeof( IMMUNE ), "IMMUNE" ); */


//NEW parallel record
// struct {
// 0	char	cvx[3];			// vaccine CVX code
// 3	char	mvx[3];			// vaccine MVX code
// 6	short	route;			// route of administration (Immunizations.Route)
// 8	short	cSite;			// codified site of administration (Immunizations.Site)
// 10	char	amount[6];		// administered amount
// 16	short	units;			// administererd amount units (Immunizations.Units)
// 18	short	infoSource;		// information source 'Source'
// 20	char	unused[2];		// unused
// 22	Reca	noteReca;		// note reca
//
// 26	char	unused[22];		// unused

//} IMMUNEX;
/*	sizecheck( 48, sizeof( IMMUNEX ), "IMMUNEX" ); */








public class Immunizations implements LLItem {

	public enum Status {
		
		CURRENT( "Current", 'C' ),	
		REMOVED( "Removed", 'R' ),
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

	public enum Site {
		
		// HL70163 - from http://www.cdc.gov/vaccines/programs/iis/stds/downloads/hl7guide-08-2010.pdf  Appendix A-A9
		
		NULL( "", "", 0 ),
		L_THIGH( "Left Thigh", "LT", 1 ),	
		R_THIGH( "Right Thigh", "RT", 2 ),
		L_UPPER_ARM( "Left Upper Arm", "LA", 3 ),
		R_UPPER_ARM( "Right Upper Arm", "RA", 4 ),
		L_DELTOID( "Left Deltoid", "LD", 5 ),
		R_DELTOID( "Right Deltoid", "RD", 6 ),
		L_GLUTEOUS( "Left Gluteous", "LG", 7 ),
		R_GLUTEOUS( "Right Gluteous", "RG", 8 ),
		L_VASTUS_LATERALIS( "Left Vastus Lateralis", "LVL", 9 ),
		R_VASTUS_LATERALIS( "Right Vastus Lateralis", "RVL", 10 ),
		L_LOWER_FOREARM( "Left Lower Forearm", "LLFA", 11 ),
		R_LOWER_FOREARM( "Right Lower Forearm", "RLFA", 12 ),

		
		UNSPECIFIED( "Unspecified", "", 99 );
		
		private String label;
		private String codedEntry;
		private int code;

		private static final Map<Integer, Site> lookup = new HashMap<Integer,Site>();
		private static final Map<String, Site> lookupCodedEntry = new HashMap<String,Site>();
		
		static {
			for ( Site r : EnumSet.allOf(Site.class)){
				lookup.put(r.getCode(), r );
				lookupCodedEntry.put(r.getCodedEntry(), r );
			}
		}


		Site ( String label, String codedEntry, int code ){
			this.label = label;
			this.codedEntry = codedEntry;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public String getCodedEntry(){ return this.codedEntry; }
		public int getCode(){ return this.code; }
		public static Site get( int code ){ return lookup.get( code ); }
		public static Site get( String codedEntry ){ return lookupCodedEntry.get( codedEntry ); }
		public static String getCodingSystem(){ return "HL70163"; }
	}

	
	
	public enum Route {
		
		// HL70162 - from http://www.cdc.gov/vaccines/programs/iis/stds/downloads/hl7guide-08-2010.pdf  Appendix A-7
		
		NULL( "", "", 0 ),							// FDA NCIT codes
		INTRAMUSCULAR( "Intramuscular", "IM", 1 ),	//C28161
		SUBCUTANEOUS( "Subcutaneous", "SC", 2 ),	//C38299
		INTRADERMAL( "Intradermal", "ID", 3 ),		//C38238
		INTRAVENOUS( "Intravenous", "IV", 4 ),		//C38276
		ORAL( "Oral", "PO", 5 ),					//C38288
		NASAL( "Nasal", "NS", 6 ),					//C38284
		TRANSDERMAL( "Transdermal", "TD", 7 ),		//C38305
		PERCUTANEOUS( "Percutaneous", "", 8 ),		//C38676
		OTHER( "Other", "OTH", 98 ),
		
		UNSPECIFIED( "Unspecified", "", 99 );
		
		private String label;
		private String codedEntry;
		private int code;

		private static final Map<Integer, Route> lookup = new HashMap<Integer,Route>();
		private static final Map<String, Route> lookupCodedEntry = new HashMap<String,Route>();
		
		static {
			for ( Route r : EnumSet.allOf(Route.class)){
				lookup.put(r.getCode(), r );
				lookupCodedEntry.put(r.getLabel(), r );
			}
		}


		Route ( String label, String codedEntry, int code ){
			this.label = label;
			this.codedEntry = codedEntry;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public String getCodedEntry(){ return this.codedEntry; }
		public int getCode(){ return this.code; }
		public static Route get( int code ){ return lookup.get( code ); }
		public static Route get( String codedEntry ){ return lookupCodedEntry.get( codedEntry ); }
		public static String getCodingSystem(){ return "HL70162"; }
	}


	public enum Units {
		
		// ISO528,1977 - from  http://aurora.regenstrief.org/~gunther/oldhtml/tables.html#TABLES   Table 36
		
		NULL( "", "", 0 ),
		VIAL( "vial", "VL", 1 ),
		BOTTLE( "bottle", "BT", 2 ),	
		EACH( "each", "EA", 3 ),
		TABLET( "tablet", "TB", 4 ),
		MILLIGRAMS( "milligrams", "MG", 5 ),
		MILLILITERS( "milliliters", "ML", 6 ),
		MEQ( "milliequivalent", "MEQ", 7 ),
		OUNCES( "ounces", "OZ", 8 ),
		GRAMS( "grams", "GM", 9 ),
		KILOGRAMS( "kilograms", "KG", 10 ),
		POUNDS( "pounds", "LB", 11 ),
		SQUARE_CENTIMETERS( "square centimeters", "SC", 12 ),
		
		UNSPECIFIED( "Unspecified", "", 99 );
		
		private String label;
		private String codedEntry;
		private int code;

		private static final Map<Integer, Units> lookup = new HashMap<Integer,Units>();
		private static final Map<String, Units> lookupCodedEntry = new HashMap<String,Units>();
		
		static {
			for ( Units r : EnumSet.allOf(Units.class)){
				lookup.put(r.getCode(), r );
				lookupCodedEntry.put(r.getCodedEntry(), r );
			}
		}


		Units ( String label, String codedEntry, int code ){
			this.label = label;
			this.codedEntry = codedEntry;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public String getCodedEntry(){ return this.codedEntry; }
		public int getCode(){ return this.code; }
		public static Units get( int code ){ return lookup.get( code ); }
		public static Units get( String codedEntry ){ return lookupCodedEntry.get( codedEntry ); }
		public static String getCodingSystem(){ return "ISO+"; } // "ISO0528" or ISO2955.83??
	}



	
	public enum Source {
		
		// Table NIP001 for information source 
		// http://www.cdc.gov/vaccines/programs/iis/stds/downloads/hl7guide-08-2010.pdf  Appendix A-A34
		
		NULL( "", "", 0 ),
		NEW( "new immunization record", "00", 1 ),
		HX_UNSPEC( "source unspecified", "01", 2 ),	
		HX_PROV( "from other provider", "02", 3 ),
		HX_WRITTEN( "from parents written record", "03", 4 ),
		HX_RECALL( "from parents recall", "04", 5 ),
		HX_REGISTRY( "from other registry", "05", 6 ),
		HX_BIRTH( "from birth certificate", "06", 7 ),
		HX_SCHOOL( "from school record", "07", 8 ),
		HX_AGENCY( "from public agency", "08", 9 ),
		
		UNSPECIFIED( "Unspecified", "01", 99 );
		
		private String label;
		private String codedEntry;
		private int code;

		private static final Map<Integer, Source> lookup = new HashMap<Integer,Source>();
		private static final Map<String, Source> lookupCodedEntry = new HashMap<String,Source>();
		
		static {
			for ( Source r : EnumSet.allOf(Source.class)){
				lookup.put(r.getCode(), r );
				lookupCodedEntry.put(r.getCodedEntry(), r );
			}
		}


		Source ( String label, String codedEntry, int code ){
			this.label = label;
			this.codedEntry = codedEntry;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public String getCodedEntry(){ return this.codedEntry; }
		public int getCode(){ return this.code; }
		public static Source get( int code ){ return lookup.get( code ); }
		public static Source get( String codedEntry ){ return lookupCodedEntry.get( codedEntry ); }
		public static String getCodingSystem(){ return "NIP001"; }
	}



	
	

	
	
	
	
	// fields
    private byte[] dataStruct;
    private byte[] dataStructX;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="imm%02d.med";
	public static final String fnsubx ="immx%02d.med";
	public static final int recordLength = 96;
	public static final int recordLengthX = 48;
	
  
    
    
    

    
    // Set up NoteTextHelper class to handle free text notes
    private String fnsubTxt = "immt%02d.med";

    class HxNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    HxNotes hxNotes = null;


    
	
    public Immunizations() {
    	allocateBuffer();
    	hxNotes = new HxNotes();
    }
	
    public Immunizations( Reca reca ){

        // allocate space
    	allocateBuffer();
    	hxNotes = new HxNotes();

    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read Immunization record
    
    public void read( Reca reca ){
    	
    	hxNotes.resetReadStatus();				// haven't read note text record yet
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
		if ( this.isNewStyle()){
			String fname = String.format( fnsubx, reca.getYear());
			if ( new File( Pm.getMedPath() + "/" + fname ).exists()){
				RecaFile.readReca( reca, dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());
			}
		}
   }	


    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// handle note text
    	hxNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );
    	RecaFile.writeReca( reca, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

    	// handle addlSig text
    	hxNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        RecaFile.writeReca( reca, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getImmuneReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setImmuneReca(reca); }
				}
		);
		return reca;
    }
    
    
    

	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recordLength ); }
    public static int getRecordLengthX() { return ( recordLengthX ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recordLength];
        dataStructX = new byte[recordLengthX];
    }

/*    public void setDataStruct(byte[] dataStruct) {
    	this.dataStruct = dataStruct;
    	this.dataStructx = dataStructx;
    }

    public byte[] getDataStruct() { return dataStruct; }
*/
   
    
    
    
    
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


    
    
    
    // DATE Date;			// date, offset 8    
    
    /**
     * getDate()
     * 
     * Get date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDate(){
       	return StructHelpers.getDate( dataStruct, 8 );
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
        StructHelpers.setDate( date, dataStruct, 8 );
        return;
    }


    
    // Time time;			// time of administration, offset 12    
    
    /**
     * getTime()
     * 
     * Get date from dataStruct.
     * 
     * @param none
     * @return usrlib.Time
     */
    public usrlib.Time getTime(){
       	return Time.fromBCD( dataStruct, 12, Time.BCDLEN.HHMM );
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
        time.toBCD( dataStruct, 12, Time.BCDLEN.HHMM );
        return;
    }


    
    
    
   
    
	// RECORD PtRec;		// patient record number, offset 16
    
    /**
     * getPtRec()
     * 
     * Get patient record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPtRec(){
    	return Rec.fromInt( dataStruct, 16 );
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
    	ptRec.toInt( dataStruct, 16 );
    }


    
    
    

	// char	desc[40];		// description, offset 20
    
    /**
     * getDesc()
     * 
     * Get description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 20, 40 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set description in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setDesc( String desc ){
    	StructHelpers.setStringPadded(desc, dataStruct, 20, 40 );
    	return ;
    }


    
    
    
	// char	LotNumber[16];		// lot number, offset 60
    
    /**
     * getLotNumber()
     * 
     * Get lot number from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getLotNumber(){
    	return StructHelpers.getString( dataStruct, 60, 16 ).trim();
    }

    /**
     * setLotNumber()
     * 
     * Set lot number in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setLotNumber( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 60, 16 );
    	return ;
    }


    
    
    

    // DATE expDate;			// expiration date, offset 80    
    
    /**
     * getExpDate()
     * 
     * Get expiration date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getExpDate(){
       	return StructHelpers.getDate( dataStruct, 80 );
    }

    /**
     * setExpDate()
     * 
     * Set expiration date in dataStruct.
     * 
     * @param usrlib.Date
     * @return void
     */
    public void setExpDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 80 );
        return;
    }


    
    
    

	// char	site[10];		// site of administration, offset 84
    
    /**
     * getSiteTxt()
     * 
     * Get site from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getSiteTxt(){
    	return StructHelpers.getString( dataStruct, 84, 10 ).trim();
    }

    /**
     * setSiteTxt()
     * 
     * Set site in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setSiteTxt( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 84, 10 );
    	return ;
    }


    
    
	// RECORD provRec;		// provider id (record number), offset 76
    
    /**
     * getProvRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProvRec(){
    	return Rec.fromInt( dataStruct, 76 );
    }

    /**
     * setProvRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setProvRec( Rec rec ){
    	if ( rec == null ) rec = new Rec( 0 );
    	rec.toInt( dataStruct, 76 );
    }


    
    
    
  
    
    
	// RECA	noteReca;		// note record number, IMMUNEX, offset 22
    
    /**
     * getNoteReca()
     * 
     * Get note record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getNoteReca(){
    	return Reca.fromReca( dataStructX, 22 );
    }

    /**
     * setNoteReca()
     * 
     * Set note record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setNoteReca( Reca reca ){
    	reca.toReca( dataStructX, 22 );
    }
    
    
    
    


    
    
 
    
    

	// char	status;			// record status, offset 95
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Immunizations.Status getStatus(){
    	return Immunizations.Status.get( dataStruct[95] & 0xff );
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Immunizations.Status status ){
    	dataStruct[95] = (byte)( status.getCode() & 0xff );
    }


    
    
    

 	// char	flgMisc;			// NEW flag this Imm as using a misc vaccine, offset 94
    
    /**
     * getFlgMisc()
     * 
     * Get misc flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMisc(){
    	return ! ( dataStruct[94] == 'N' );
    }

    /**
     * setFlgMisc()
     * 
     * Set misc in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgMisc( boolean flg ){
    	dataStruct[94] = (byte) (flg ? 'Y': 'N');
    }


    
    
    /**
     * isNewStyle()
     * 
     * Is this a 'new style' entry?
     * (test flgMisc for Y/N value == new style)
     * 
     * @param none
     * @return boolean
     */
    public boolean isNewStyle(){
    	return (( dataStruct[94] == 'Y' ) || ( dataStruct[94] == 'N' ));
    }


 
    
    
    
    
    
    // char	cvx[3], IMMUNEX, offset 0
    
    /**
     * getCVX()
     * 
     * Get CVX code from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getCVX(){
    	return StructHelpers.getString( dataStructX, 0, 3 ).trim();
    }

    /**
     * setCVX()
     * 
     * Set CVX code in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setCVX( String s ){
    	StructHelpers.setStringPadded( s, dataStructX, 0, 3 );
    	return;
    }



    
    


    // char	mvx[3], IMMUNEX, offset 3
    
    /**
     * getMVX()
     * 
     * Get MVX code from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getMVX(){
    	return StructHelpers.getString( dataStructX, 3, 3 ).trim();
    }

    /**
     * setMVX()
     * 
     * Set MVX code in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setMVX( String s ){
    	StructHelpers.setStringPadded( s, dataStructX, 3, 3 );
    	return;
    }



    
    
    // char	amount[6], IMMUNEX, offset 10
    
    /**
     * getAmount()
     * 
     * Get Amount from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getAmount(){
    	return StructHelpers.getString( dataStructX, 10, 6 ).trim();
    }

    /**
     * setAmount()
     * 
     * Set Amount in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setAmount( String s ){
    	StructHelpers.setStringPadded( s, dataStructX, 10, 6 );
    	return;
    }



    
    

    // short	units, IMMUNEX, offset 16
    
    /**
     * getUnits()
     * 
     * Get Units from dataStruct.
     * 
     * @param none
     * @return Immunizations.Units
     */
    public Immunizations.Units getUnits(){
    	return Immunizations.Units.get( StructHelpers.getShort( dataStructX, 16 ));
    }

    /**
     * setUnits()
     * 
     * Set Units in dataStruct.
     * 
     * @param Immunizations.Units
     * @return void
     */
    public void setUnits( Immunizations.Units units ){
    	StructHelpers.setShort( (units == null) ? 0: units.getCode(), dataStructX, 16 );
    	return;
    }



    
    

    // short	units, IMMUNEX, offset 18
    
    /**
     * getInfoSource()
     * 
     * Get information source from dataStruct.
     * 
     * @param none
     * @return Immunizations.Source
     */
    public Immunizations.Source getInfoSource(){
    	return Immunizations.Source.get( StructHelpers.getShort( dataStructX, 18 ));
    }

    /**
     * setSource()
     * 
     * Set information source in dataStruct.
     * 
     * @param Immunizations.Source
     * @return void
     */
    public void setInfoSource( Immunizations.Source source ){
    	StructHelpers.setShort( (source == null) ? 0: source.getCode(), dataStructX, 18 );
    	return;
    }



    
    



    
   
    

	// short cSite;			// immunization site (coded), IMMUNEX, offset 8
    
    /**
     * getSite()
     * 
     * Get Site from dataStruct.
     * 
     * @param none
     * @return Site
     */
    public Immunizations.Site getSite(){
    	Immunizations.Site site = Immunizations.Site.get( StructHelpers.getShort(dataStructX, 8 ) );
    	if ( site == null ) site = Immunizations.Site.UNSPECIFIED;
    	return site;
    }

    /**
     * setSite()
     * 
     * Set site in dataStruct.
     * 
     * @param Site
     * @return void
     */
    public void setSite( Immunizations.Site site ){
    	StructHelpers.setShort( (site == null) ? 0: site.getCode(), dataStructX, 8 );
    }


    
    

	// short route;			// immunization route (coded), IMMUNEX, offset 6
    
    /**
     * getRoute()
     * 
     * Get route from dataStruct.
     * 
     * @param none
     * @return Route
     */
    public Immunizations.Route getRoute(){
    	Immunizations.Route route = Immunizations.Route.get( StructHelpers.getShort( dataStructX, 6 ) );
    	if ( route == null ) route = Immunizations.Route.UNSPECIFIED;
    	return route;
    }

    /**
     * setRoute()
     * 
     * Set route in dataStruct.
     * 
     * @param Route
     * @return void
     */
    public void setRoute( Immunizations.Route route ){
    	StructHelpers.setShort( (route == null) ? 0: route.getCode(), dataStructX, 6 );
    }


    
    


    
	// char	reason;			// removed reason code, PARX, offset 13
    
    /**
     * getRemovedReason()
     * 
     * Get Removed Reason from dataStruct.
     * 
     * @param none
     * @return Reason
     */
/*    public Par.Reason getRemovedReason(){
    	Par.Reason reason = Par.Reason.get( dataStructX[13] & 0xff );
    	if ( reason == null ) reason = Par.Reason.UNSPECIFIED;
    	return reason;
    }
*/
    /**
     * setRemovedReason()
     * 
     * Set Removed Reason in dataStruct.
     * 
     * @param Reason
     * @return void
     */
/*    public void setRemovedReason( Reason reason ){
    	dataStructX[13] = (byte)( reason.getCode() & 0xff );
    }
*/

    
    



    
    
    

    
    
    /**
     * toString()
     * 
     * Get a printable String representation of this entry.
     * 
     * @param none
     * @return void
     */
  
    public String toString(){
    	return String.format( "%s %s", this.getDate().getPrintable(9), this.getDesc());
    }


    
    
    
    /**
     * getNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getNoteTxt(){ return hxNotes.getNoteText(); }
    	
    /**
     * setNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setNoteTxt( String txt ){ hxNotes.setNoteText(txt); }
    

    
    

   
    
    
    
    

}
