package palmed;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.Par.Status;
import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;


//typedef struct {
//
//	0	LLHDR	Hdr;			/*  link list header  */
//	8	RECORD	PtRec;			/*  patient record number  */
//	12	DATE	Date;			/*  date of note  */
//
//	16	RECORD	txtrec;			/*  text record (offset)  */
//	20	USRT	txtlen;			/*  length of text record  */
//
//	22	RECSH	UserRec;		/*  entered by  */
//
//	24	char	Desc[40];		/*  description  */
//	64	RECA	Signature;		/*  signature link reca  */
//
//	68	byte	unused[9];		/*  unused field  */
//	77	byte	Type;			/*  type of note  */
//	78	byte	edit;			/*  note edited flag  */
//	79	char	Status;			/*  Status flag  */
//
//	} NOTE;
//
///*	sizecheck( 80, sizeof( NOTE ), "NOTE" );*/


/*  Note Status flags  */

//#define	c_NOTE_STATUS_CURRENT	'C'
//#define	c_NOTE_STATUS_REMOVED	'R'
//#define	c_NOTE_STATUS_EDITED	'E'
//
//#define	s_NOTE_STATUS_CURRENT	"C"
//#define	s_NOTE_STATUS_REMOVED	"R"
//#define	s_NOTE_STATUS_EDITED	"E"




	/*  note type ids, Note.Type field  */

//#define NOTE_TYPE_LAB           1L      /*  misc type lab report  */
//#define NOTE_TYPE_NURSING       2L      /*  nursing note  */
//#define NOTE_TYPE_TREATMENT     3L      /*  treatment notes  */
//#define NOTE_TYPE_XRAY          4L      /*  xray reports  */
//#define NOTE_TYPE_TELEPHONE     5L      /*  telephone notes  */
//#define NOTE_TYPE_SOAP          6L      /*  soap note  */
//#define NOTE_TYPE_PHYSICAL      7L      /*  history & physical exam  */
//
//#if NOTNOW
//#define	NOTE_TYPE_LAB		1	/*  misc type lab report  */
//#define	NOTE_TYPE_NURSING	2	/*  nursing note  */
//#define	NOTE_TYPE_TREATMENT	3	/*  treatment notes  */
//#define	NOTE_TYPE_TELEPHONE	4	/*  telephone notes  */
//#define	NOTE_TYPE_XRAY		5	/*  xray reports  */
//#endif
							

	

public abstract class Notes implements LLItem {
	
/*	static enum NoteType {
		
		NOTE_TYPE_LAB( 1, "lab", "Lab Notes" ),	
		NOTE_TYPE_NURSING( 2, "nur", "Nursing Notes" ),	
		NOTE_TYPE_TREATMENT( 3, "trt", "Treatment Notes" ), 
		NOTE_TYPE_XRAY( 4, "xry", "Xray Reports" );
		
		private int code;
		private String fnsub;
		private String title;
		
		private NoteType( int code, String fnsub, String title ){ 
			this.code = code;
			this.fnsub = fnsub;
			this.title = title;
		}
		
		public int getCode() { return code; }
		public String getFnsub(){ return fnsub; }
		public String getTitle(){ return title; }
	};
*/
	
	
	static public enum Status {
		
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


	// static fields
	//private final static String fn_note = "nt%s%02d.med";
	//private final static String fn_notetxt = "nt%st%02d.med";
	

//	final static String[] noteHdr = {
//		"Lab Reports", "Nursing Notes", "Treatment Notes", "Xray Reports"
//	};


	// fields
    private byte[] dataStruct;
    private Reca reca = null;			// this Reca
    //private NoteType noteType = null;	// note type	
    private final static int recordLength = 80;		// record length
    
    private String noteText = null;		// note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    private Class<? extends Notes> noteClass;	// implementing class

    
    // Constructors
    
    public Notes( Class<? extends Notes> noteClass ){
    	this.noteClass = noteClass;
    	allocateBuffer();
    }
	
    public Notes( Class<? extends Notes> noteClass, Reca reca ){

    	// set note type
    	this.noteClass = noteClass;
    	
    	allocateBuffer();    	
    	read( reca );   	
    	this.reca = reca;
    }

    

	// read Note
    
    public void read( Reca reca ){
    	
    	flgNoteTxtRead = false;				// haven't read note text record yet
    	
		if ( ! Reca.isValid( reca )) return;		
		this.reca = reca;
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), getFname(), getRecordLength());
    }	


   public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// handle note text
    	if ( flgNoteTxt ){
    		Rec textRec = this.writeNoteText( vol, noteText );
    		this.setTextRec( textRec );
    		flgNoteTxt = false;
    	}
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), getFname(), getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

    	// handle addlSig text
    	if ( flgNoteTxt ){
    		Rec textRec = this.writeNoteText( reca.getYear(), noteText );
    		this.setTextRec( textRec );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), getFname(), getRecordLength());
        return true;
    }


    
/*    public Reca postNew( Rec ptRec ){
    	
    	Reca reca = null;
    	System.out.println( "this class type=" + this.getClass().getName());

			reca = LLPost.post( ptRec, this, 
					new LLPost.LLPost_Helper () {
						public Reca getReca( MedPt medPt ){ return getMedPtReca( medPt); }
						public void setReca( MedPt medPt, Reca reca ){ setMedPtReca( medPt, reca ); }
					}
			);
		return reca;
    }
*/
    abstract public Reca postNew( Rec ptRec );
    
    
    
    

	


    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recordLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recordLength];
    }


    // Get file names
    public String getFname() { return ( "nt" + Notes.getFnsub( noteClass ) + "%02d.med" ); }
    public String getTextFname() { return ( "nt" + Notes.getFnsub( noteClass ) + "t%02d.med" ); }
    
    
   
    
    
    
    
    
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


    
    
    
	// char	Desc[40];		// Note description, offset 24
    
    /**
     * getDesc()
     * 
     * Get Note description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 24, 40 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set Note description in dataStruct.
     * 
     * @param String		description
     * @return void
     */
    public void setDesc( String desc ){
    	StructHelpers.setStringPadded(desc, dataStruct, 24, 40 );
    	return ;
    }


    
    
    

    
    
	// RECORD txtrec;		// text record number, offset 16
    
    /**
     * getTextRec()
     * 
     * Get text record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getTextRec(){
    	return Rec.fromInt( dataStruct, 16 );
    }

    /**
     * setTextRec()
     * 
     * Set text record in dataStruct.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
    public void setTextRec( Rec rec ){
    	rec.toInt( dataStruct, 16 );
    }

    
    
    
    
	// USRT txtlen;		// text length, offset 20
    
    /**
     * getTextLen()
     * 
     * Get text record from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getTextLen(){
    	return StructHelpers.getShort( dataStruct, 20 );
    }

    /**
     * setTextLen()
     * 
     * Set text length in dataStruct.
     * 
     * @param int len
     * @return void
     */
    public void setTextLen( int len ){
    	StructHelpers.setShort( len, dataStruct, 20 );
    }

    
    
    
	// RECSH UserRec;		// user record, offset 22
    
    /**
     * getUserRec()
     * 
     * Get user record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getUserRec(){
    	return new Rec( StructHelpers.getShort( dataStruct, 22 ));
    }

    /**
     * setUserRec()
     * 
     * Set user record in dataStruct.
     * 
     * @param Rec rec
     * @return void
     */
    public void setUserRec( Rec rec ){
    	StructHelpers.setShort( rec.getRec(), dataStruct, 22 );
    }

    
    
    
    
    
	// RECA Signature;		// signature record number, offset 64
    
    /**
     * getSignatureReca()
     * 
     * Get signature record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getSignatureReca(){
    	return Reca.fromReca( dataStruct, 64 );
    }

    /**
     * setSignatureReca()
     * 
     * Set signature record in dataStruct.
     * 
     * @param Reca		signature record number
     * @return void
     */
    public void setSignatureReca( Reca reca ){
    	reca.toReca( dataStruct, 64 );
    }
    
    
    
    
    
    // char unused[9]			// unused field, offset 68;
    
    
    
    
	// byte	Type;			// record type, offset 77
    
    /**
     * getType()
     * 
     * Get type from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getType(){
    	return (int) dataStruct[77];
    }

    /**
     * setType()
     * 
     * Set status in dataStruct.
     * 
     * @param int		'type'
     * @return void
     */
    public void setType( int num ){
    	dataStruct[77] = (byte)( 0xff & num );
    }


    
    

    
    
	// byte edit;			// number of edits, offset 78
    
    /**
     * getNumEdits()
     * 
     * Get number of edits from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumEdits(){
    	return (int) dataStruct[78];
    }

    /**
     * setNumEdits()
     * 
     * Set number of edits in dataStruct.
     * 
     * @param int		'edits'
     * @return void
     */
    public void setNumEdits( int num ){
    	dataStruct[78] = (byte)( 0xff & num );
    }


    
    
    


    
	// char	status;			// record status, offset 79
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	return Status.get( dataStruct[79] & 0xff );
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
    	dataStruct[79] = (byte)( status.getCode() & 0xff );
    }


    
    
   

    
    
    /**
     * getText()
     * 
     * Get actual SOAP text from the soap text file.
     * 
     * @param none
     * @return Rec
     */
/*    public String getText(){
    	
    	// handle no text
    	if (( this.reca == null ) || ( this.reca.getRec() < 2 )) return "";
    	
    	return NoteText.readText( getTextFname(), this.reca.getYear(), this.getTextRec());
    	//Rec txtrec = this.getTextRec();
    	//String fname = new String().format( fn_soaptxt, this.reca.getYear());
    	//byte[] data = new byte[this.getTextLen()];
    	//RandomFile.readBytes( new Rec( getTextRec().getRec() + 16 ), getTextLen(), data, Pm.getMedPath(), fname);
    	//File file = new File();
    	//return new String( data, 0, getTextLen());
    }
*/
    
    
    /**
     * writeText()
     * 
     * Write new soap text into the soap text file.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
/*    public Reca writeText( int vol, usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
    	return NoteText.saveText( getTextFname(), vol, date, ptrec, lastedit, text );
    }
*/
    
    
    
    /**
     * getNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getNoteText(){
    	
    	// have we read note text record yet, if not - read it
    	if ( ! flgNoteTxtRead ){
    		
    		Rec rec = this.getTextRec();
    		if ( Rec.isValid(rec)){
    			this.noteText = this.readNoteText( rec );
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
    public void setNoteText( String txt ){
    	
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


    
    
    
    private String readNoteText( Rec rec ){
    	
    	// handle trivial case
    	if ( ! Rec.isValid( rec )) return "";    	   	
    	return NoteText.readText( getTextFname(), this.reca.getYear(), this.getTextRec());
    }
    
    private Rec writeNoteText( int vol, String text ){
    	
     	// handle trivial case
    	if ( text == null ) return null;
    	Rec rec = NoteTextLong.saveTextLong( getTextFname(), vol, getDate(), getPtRec(), this.getTextRec(), text );
    	return rec;
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
    	return String.format( "%s", this.getDesc());
    }


    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     *  These use reflection to get to the static methods in the subclasses
     * 
     *
     */
    
    
    public static Notes newInstance( Class<? extends Notes> noteClass ){
    	Notes notes = null;
    	
    	try { 
    		notes = noteClass.newInstance(); 
		} catch ( Exception e ){
			e.printStackTrace();
		}
    	
		return notes;
    }
    
    
    public static String getTitle( Class<? extends Notes> noteClass ){   	
		String title = "Notes";
		try {
			title = (String) noteClass.getMethod( "getTitle", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return title;    	
    }

    public static String getFnsub( Class<? extends Notes> noteClass ){
		String fnsub = "xxx";
		try {
			fnsub = (String) noteClass.getMethod( "getFnsub", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return fnsub;    	
    }


    public static MrLog.Types getMrLogTypes( Class<? extends Notes> noteClass ){
		MrLog.Types type = MrLog.Types.UNDEFINED;
		try {
			type = (MrLog.Types) noteClass.getMethod( "getMrLogTypes", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return type;    	
    }
    
	public static Notifier.Event getNotifierEvent( Class<? extends Notes> noteClass ){		
		Notifier.Event event = Notifier.Event.MISC;
		try {
			event = (Notifier.Event) noteClass.getMethod( "getNotifierEvent", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return event;
	}

	public static AuditLog.Action getAuditLogActionNew( Class<? extends Notes> noteClass ){
		AuditLog.Action action = AuditLog.Action.UNDEFINED;
		try {
			action = (AuditLog.Action) noteClass.getMethod( "getAuditLogActionNew", (Class<?>[]) null  ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return action;
	}
	public static AuditLog.Action getAuditLogActionEdit( Class<? extends Notes> noteClass ){
		AuditLog.Action action = AuditLog.Action.UNDEFINED;
		try {
			action = (AuditLog.Action) noteClass.getMethod( "getAuditLogActionEdit", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return action;
	}

	public static AuditLog.Action getAuditLogActionDelete( Class<? extends Notes> noteClass ){
		AuditLog.Action action = AuditLog.Action.UNDEFINED;
		try {
			action = (AuditLog.Action) noteClass.getMethod( "getAuditLogActionDelete", (Class<?>[]) null ).invoke( null, (Object[]) null );
		} catch ( Exception e ){
			e.printStackTrace();
		}
		return action;
	}
	
    
	
	
	
	public static Reca getMedPtReca( Class<? extends Notes> noteClass, MedPt medPt ){
		
		Reca reca = null;
		
		try {
			if ( noteClass.getMethod( "getMedPtReca", MedPt.class ) == null ) System.out.println( "noteClass.getMethod() == null" );
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			reca = (Reca) noteClass.getMethod( "getMedPtReca", MedPt.class ).invoke( null, medPt );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reca;
	}
	
	
	public static void setMedPtReca( Class<? extends Notes> noteClass, MedPt medPt, Reca reca ){
		
		Class<?> args1[] = { MedPt.class, Reca.class };

		
		try {
			noteClass.getMethod( "setMedPtReca", args1 ).invoke( null, medPt, reca );
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return;
	}
	
	
	
	
	// Note - implementing class needs to implement these!!!!!!
	//    (not allowed to be abstract by Java <= 1.6
	
	//abstract public static Reca getMedPtReca( MedPt medpt );
	//abstract public static void setMedPtReca( MedPt medpt, Reca reca );
	//abstract public static MrLog.Types getMrLogTypes();
	//abstract public static Notifier.Event getNotifierEvent();
	//abstract public static AuditLog.Action getAuditLogActionNew();
	//abstract public static AuditLog.Action getAuditLogActionEdit();
	//abstract public static AuditLog.Action getAuditLogActionDelete();
	//abstract public static String getTitle();
	//abstract public static String getFn
}
