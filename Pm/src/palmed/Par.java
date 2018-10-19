package palmed;


import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;



//from the C header file palmed/par.h

//typedef struct {
//
//	LLHDR	hdr;			/*  history linked list header  */
//
//	DATE	Date;			/*  PAR date  */
//	RECORD	PtRec;			/*  patient record number  */
//
//	char	dname[40];		/*  drug name  */
//
//	RECA	MiscSymptomReca;	/*  misc symptom desc record #  */
//	char	Severity;			// NEW - symptom/reaction severity. mild, mod, severe
//	byte	Status;				/*  status or record  */
//	char	Symptoms;			/*  symptom flags  */
//	char	flgMisc;			// NEW - flag this is Misc drug//allergen, non Y/N value indicates old style
//
//	} PAR;

/*	sizecheck( 64, sizeof( PAR ), "PAR" ); */

//Status:
//	'C' - valid PAR
//	'R' - removed PAR
//	'E' - record superceeded by edited version



// NEW parallel record
//
// typedef struct {
//
//	long	compositeAllergyID;		// Newcrop's composite allergy ID
//	long	another code ID:		// another coding system ?FDB? code
//	DATE	StopDate;				// removed date
//
//	byte	domain;					// domain (drug, food, environment)
//	byte	reason;					// removed reason code
//
//	} PARX;
//
//	sizecheck( 64, sizeof( PARX ), "PARX" );



// Need drug code
// Allergy domain - to map to SNOMED
// Checked - sent to NewCrop



public class Par implements LLItem {
	
	
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

	public enum Severity {
		
		MILD( "Mild", 'L' ),	
		MODERATE( "Moderate", 'M' ),
		SEVERE( "Severe", 'S' ),
		UNSPECIFIED( "Unspecified", 'U' );
		
		private String label;
		private int code;

		private static final Map<Integer, Severity> lookup = new HashMap<Integer,Severity>();
		private static final Map<String, Severity> lookupLabel = new HashMap<String,Severity>();
		
		static {
			for ( Severity r : EnumSet.allOf(Severity.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		Severity ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Severity get( int code ){ return lookup.get( code & 0xff ); }
		public static Severity get( String label ){ return lookupLabel.get( label ); }

	}

	
	// Reaction flags OR'd together to form Reaction/Symptom byte
	public static class Reactions {
		public final static int RASH = 1;
		public final static int SHOCK = 2;
		public final static int DYSPNEA = 4;
		public final static int NAUSEA = 8;
		public final static int ANEMIA = 16;
		public final static int UNSPECIFIED = 32;
		
		public static String getPrintable( int num ){
			StringBuilder sb = new StringBuilder( 50 );
			if (( num & RASH ) != 0 ) sb.append( "Rash, " );
			if (( num & SHOCK ) != 0 ) sb.append( "Shock, " );
			if (( num & DYSPNEA ) != 0 ) sb.append( "Dyspnea, " );
			if (( num & NAUSEA ) != 0 ) sb.append( "Nausea, " );
			if (( num & ANEMIA ) != 0 ) sb.append( "Anemia, " );
			if (( num & UNSPECIFIED ) != 0 ) sb.append( "Unspecified" );
			
			String s = sb.toString();
			if ( s.endsWith( ", " )) s = s.substring( 0, s.length() - 2 );
			return s;
		}
	}

	public enum Domain {
		
		DRUG( "Drug Allergy", 'D', "416098002" ),	
		FOOD( "Food Allergy", 'F', "414285001" ),
		ENVIRONMENT( "Environmental Allergy", 'E', "426232007" ),
		UNSPECIFIED( "Unspecified", 'U', "416098002" );		// hardcode this to drug allergy (most commonly entered)
		
		private String label;
		private int code;
		private String SNOMED;

		private static final Map<Integer, Domain> lookup = new HashMap<Integer,Domain>();
		
		static {
			for ( Domain r : EnumSet.allOf(Domain.class))
				lookup.put(r.getCode(), r );
		}


		Domain ( String label, int code, String SNOMED ){
			this.label = label;
			this.code = code & 0xff;
			this.SNOMED = SNOMED;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Domain get( int code ){ return lookup.get( code & 0xff ); }
		public String getSNOMED(){ return this.SNOMED; }
	}

	
	
	
	public enum Reason {
		
		ERROR( "Entered in Error", 'E' ),	
		UNSPECIFIED( "Unspecified", 'U' );
		
		private String label;
		private int code;

		private static final Map<Integer, Reason> lookup = new HashMap<Integer,Reason>();
		
		static {
			for ( Reason r : EnumSet.allOf(Reason.class))
				lookup.put(r.getCode(), r );
		}


		Reason ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Reason get( int code ){ return lookup.get( code & 0xff ); }

	}

	
	
	
	
	// fields
    private byte[] dataStruct;
    private byte[] dataStructX;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="par%02d.med";
	public static final String fnsubx ="parx%02d.med";
	public static final int recordLength = 64;
	public static final int recordLengthX = 64;
	
    private String noteText = null;		// additional SIG text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    
    
	
    public Par() {
    	allocateBuffer();
    }
	
    public Par( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read PAR
    
    public void read( Reca reca ){
    	
    	flgNoteTxtRead = false;				// haven't read note text record yet
    	
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
    	if ( flgNoteTxt ){
    		Reca addlSigReca = this.writeNoteText( noteText );
    		this.setMiscSymptomReca( addlSigReca );
    		flgNoteTxt = false;
    	}
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );
    	RecaFile.writeReca( reca, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

    	// handle addlSig text
    	if ( flgNoteTxt ){
    		Reca addlSigReca = this.writeNoteText( noteText );
    		this.setMiscSymptomReca( addlSigReca );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        RecaFile.writeReca( reca, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getParsReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setParsReca(reca); }
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


    
    
    
	// RECORD PtRec;		// patient record number, offset 12
    
    /**
     * getPtRec()
     * 
     * Get patient record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPtRec(){
    	return Rec.fromInt( dataStruct, 12 );
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
    	ptRec.toInt( dataStruct, 12 );
    }


    
    
    

	// char	dname[40];		// PAR description, offset 16
    
    /**
     * getParDesc()
     * 
     * Get PAR description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getParDesc(){
    	return StructHelpers.getString( dataStruct, 16, 40 ).trim();
    }

    /**
     * setParDesc()
     * 
     * Set PAR description in dataStruct.
     * 
     * @param String		description
     * @return void
     */
    public void setParDesc( String desc ){
    	StructHelpers.setStringPadded(desc, dataStruct, 16, 40 );
    	return ;
    }


    
    
    

    
    
	// RECA	MiscSymptomReca;		// misc symptom record number, offset 56
    
    /**
     * getMiscSymptomReca()
     * 
     * Get misc symptom record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Reca getMiscSymptomReca(){
    	return Reca.fromReca( dataStruct, 56 );
    }

    /**
     * setMiscSymptomReca()
     * 
     * Set misc symptom record in dataStruct.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
    public void setMiscSymptomReca( Reca reca ){
    	reca.toReca( dataStruct, 56 );
    }
    
    
    
    
 	// char	severity;			// NEW severity, offset 60
    
    /**
     * getSeverity()
     * 
     * Get Severity from dataStruct.
     * 
     * @param none
     * @return Severity
     */
    public Par.Severity getSeverity(){
    	Par.Severity severity = Par.Severity.get( dataStruct[60] & 0xff );
    	return ( severity == null ) ? Par.Severity.UNSPECIFIED: severity;
    }

    /**
     * setSeverity()
     * 
     * Set Severity in dataStruct.
     * 
     * @param Site
     * @return void
     */
    public void setSeverity( Severity severity ){
    	dataStruct[60] = (byte)( severity.getCode() & 0xff );
    }


    
    
 
    
    

	// char	status;			// record status, offset 61
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Par.Status getStatus(){
    	return Par.Status.get( dataStruct[61] & 0xff );
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Status status ){
    	dataStruct[61] = (byte)( status.getCode() & 0xff );
    }


    
    
    
	// char	Symptoms;			// symptoms, offset 62
    
    /**
     * getSymptoms()
     * 
     * Get symptoms from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getSymptoms(){
    	return (int)( dataStruct[62] & 0xff );
    }

    /**
     * setSymptoms()
     * 
     * Set symptoms in dataStruct.
     * 
     * @param int		'status'
     * @return void
     */
    public void setSymptoms( int num ){
    	dataStruct[62] = (byte)( 0xff & num );
    }

    
    public String getSymptomsText(){ return getSymptomsText( false ); }

    /**
     * getSymptomsText()
     *
     * Boolean flgNotes specifies whether to include the Misc Notes with the symptom.
     * 
     * @param flgNotes
     * @return
     */    
    public String getSymptomsText( boolean flgNotes ){
    	String s = Par.Reactions.getPrintable( getSymptoms());
    	if ( flgNotes ){
	    	String n = this.getNoteTxt();
	    	if ( s.length() > 0 && ( n.length() > 0 )) s += "; ";
	    	if ( n.length() > 0 ) s += n;
    	}
    	return s;
    }
    
    


 	// char	flgMisc;			// NEW flag this PAR as using a misc med/allergen, offset 63
    
    /**
     * getFlgMisc()
     * 
     * Get misc flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMisc(){
    	return ! ( dataStruct[63] == 'N' );
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
    	dataStruct[63] = (byte) (flg ? 'Y': 'N');
    }


    
    
    /**
     * isNewStyle()
     * 
     * Is this a 'new style' par entry?
     * (test flgMisc for Y/N value == new style)
     * 
     * @param none
     * @return boolean
     */
    public boolean isNewStyle(){
    	return (( dataStruct[63] == 'Y' ) || ( dataStruct[63] == 'N' ));
    }


 
    
    
    
    
    
    // get NewCrop CompositeID, PARX, offset 0
    
    /**
     * getCompositeID()
     * 
     * Get PAR composite ID from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getCompositeID(){
    	return StructHelpers.getInt( dataStructX, 0 );
    }

    /**
     * setCompositeID()
     * 
     * Set PAR composite ID in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setCompositeID( int id ){
    	StructHelpers.setInt( id, dataStructX, 0 );
    	return;
    }



    
    



    
   
    
    // get Newcrop Code, PARX, offset 4
    
    /**
     * getCode()
     * 
     * Get PAR code from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getCode(){
    	return StructHelpers.getInt( dataStructX, 4 );
    }

    /**
     * setCode()
     * 
     * Set PAR code in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setCode( int id ){
    	StructHelpers.setInt( id, dataStructX, 4 );
    	return;
    }


    
    

	// char	domain;			// record status, PARX, offset 12
    
    /**
     * getDomain()
     * 
     * Get domain from dataStruct.
     * 
     * @param none
     * @return Domain
     */
    public Par.Domain getDomain(){
    	Par.Domain domain = Par.Domain.get( dataStructX[12] & 0xff );
    	if ( domain == null ) domain = Par.Domain.UNSPECIFIED;
    	return domain;
    }

    /**
     * setDomain()
     * 
     * Set domain in dataStruct.
     * 
     * @param Domain
     * @return void
     */
    public void setDomain( Domain domain ){
    	dataStructX[12] = (byte)( domain.getCode() & 0xff );
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
    public Par.Reason getRemovedReason(){
    	Par.Reason reason = Par.Reason.get( dataStructX[13] & 0xff );
    	if ( reason == null ) reason = Par.Reason.UNSPECIFIED;
    	return reason;
    }

    /**
     * setRemovedReason()
     * 
     * Set Removed Reason in dataStruct.
     * 
     * @param Reason
     * @return void
     */
    public void setRemovedReason( Reason reason ){
    	dataStructX[13] = (byte)( reason.getCode() & 0xff );
    }


    
    


    // DATE StopDate;			// date, PARX, offset 8    
    
    /**
     * getStopDate()
     * 
     * Get Stop date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getStopDate(){
       	return StructHelpers.getDate( dataStructX, 8 );
    }

    /**
     * setStopDate()
     * 
     * Set Stop date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setStopDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStructX, 8 );
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
    	return String.format( "%s", this.getParDesc());
    }


    
    
    
    /**
     * getNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getNoteTxt(){
    	
    	// have we read note text record yet, if not - read it
    	if ( ! flgNoteTxtRead ){
    		
    		Reca reca = this.getMiscSymptomReca();
    		if ( Reca.isValid(reca)){
    			this.noteText = this.readNoteText( reca );
    			flgNoteTxtRead = true;
    		}
    	}
    	
    	return ( this.noteText == null ) ? "": this.noteText;
    }

    /**
     * setNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setNoteTxt( String txt ){
    	
    	// handle trivial cases
    	if ( txt == null ) return;
    	txt = txt.trim();
    	if ( txt.length() < 1 ) return;
    	
    	// is new and old the same?
    	if ( txt.equals( this.noteText )) return;
    	
    	// set addlSig text and mark to be written
    	this.flgNoteTxt = true;
    	this.noteText = txt;
    }


    
    
    private String fnsubTxt = "parsym%02d.med";
    //private String fnsubNdx = "medtxl%02d.med";

    
    private String readNoteText( Reca reca ){
    	
    	// handle trivial case
    	if (( reca == null ) || ( ! reca.isValid())) return "";
    	   	
    	// read entry from text file
    	byte[] dataStruct = new byte[32];
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsubTxt, 32 );
		String s = StructHelpers.getString(dataStruct, 0, 32 );
		return s.trim();
    }
    
    private Reca writeNoteText( String text ){
    	
     	// handle trivial case
    	if ( text == null ) return null;
    	
    	byte[] dataStruct = new byte[32];
    	StructHelpers.setStringPadded( text, dataStruct, 0, 32 );
    	Reca reca = RecaFile.newReca( Reca.todayVol(), dataStruct, Pm.getMedPath(), fnsubTxt, 32 );
    	
    	return reca;
    }

   
    
    
    
    

}
