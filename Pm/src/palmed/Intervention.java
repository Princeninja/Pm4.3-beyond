package palmed;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.HxObGyn.ObNotes;
import palmed.Par.Reason;
import palmed.Par.Severity;
import palmed.Par.Status;
import usrlib.LLHdr;



//typedef struct {
//
// 0	LLHDR	hdr;			/*  history linked list header  */
//
// 8	DATE	Date;			/*  PAR date  */
// 12	RECORD	PtRec;			/*  patient record number  */
//
// 16	short	type;			// type of intervention - from enum Types
// 18  char	unused[2];
//
// 20	Reca	noteTextReca;	// additional note text record number
//
// 24	char	unused[38];
//
// 62	byte	removedReason;		// removed reason code
// 63	byte	Status;				/*  status or record  */
//
//	} Interventions;

/*	sizecheck( 64, sizeof( Interventions ), "Interventions" ); */




import usrlib.Date;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;

public class Intervention implements LLItem {
	
	
	
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

	public enum Types {
		
		NONE( "", 0 ),
		SMOKING_CESSATION( "Smoking Cessation", 2 ),	
		DIETITIAN( "Dietitian", 3 ),
		BMI_HIGH( "BMI Low F/U Plan", 4 ),
		BMI_LOW( "BMI Low F/U Plan", 5 ),
		PHYSICAL_ACTIVITY( "Physical Activity Counseling", 6 ),
		NUTRITION_COUNSELING( "Nutrition Counseling", 7 ),
		REFERRAL( "Referral", 8 ),
		PATIENT_EDUCATION( "Patient Education Materials", 9 ),
		REMINDER_SENT( "Reminder Sent", 10 ),
		VISIT_SUMMARY( "Visit Summary to Patient", 11 ),
		SUMMARY_PROVIDER( "Clinical Summary to Care Provider", 12 ),
		PT_ECOPY_PROVIDED( "Patient provided EHR copy", 13 ),
		PT_ECOPY_REQUEST( "Patient request EHR copy", 14 ),
		PT_TRANSFER_IN( "Patient care transfered IN (to us)", 15 ),
		PT_TRANSFER_OUT( "Patient care transferred OUT (from us)", 16),
		MEDICATION_RECONCILIATION( "Medications Reconciled", 17 ),
		
		UNSPECIFIED( "Unspecified", 9999 );
		
		private String label;
		private int code;

		private static final Map<Integer, Types> lookup = new HashMap<Integer,Types>();
		private static final Map<String, Types> lookupLabel = new HashMap<String,Types>();
		
		static {
			for ( Types r : EnumSet.allOf(Types.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		Types ( String label, int code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Types get( int code ){ return lookup.get( code ); }
		public static Types get( String label ){ return lookupLabel.get( label ); }

	}


	public enum Reason {
		
		NONE( "", 0 ),
		ERROR( "Entered in Error", 1 ),	
		UNSPECIFIED( "Unspecified", 99 );
		
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
    private Reca reca;			// this Reca
    
	public static final String fnsub ="interv%02d.med";
	public static final int recordLength = 64;
	
    
    private String noteText = null;			// note text
    private boolean flgNoteTxt = false;		// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
    private Reca noteTextReca;				// this Reca

	
    
    
    
    
    // Set up NoteTextHelper class

   private String fnsubTxt = "intert%02d.med";
   
   class HxNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setNoteTextReca( reca ); }
    	@Override
    	public Reca getReca(){ return getNoteTextReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    HxNotes hxNotes = null;


    
    
    
    
    
    
    // Constructors
    public Intervention() {
    	allocateBuffer();
    	hxNotes = new HxNotes();
    }
	
    public Intervention( Reca reca ){

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


	// read PAR
    
    public void read( Reca reca ){
    	
    	hxNotes.resetReadStatus();				// haven't read note text record yet

    	if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	    	
    	// handle note text
    	hxNotes.write();
    	
    	Reca reca = RecaFile.newReca( Reca.todayVol(), dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

    	// handle note text
    	hxNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getInterventionsReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setInterventionsReca(reca); }
				}
		);
		return reca;
    }
    
    
    

	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recordLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recordLength];
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

    
    
    

	// RECA	noteTextReca;		// note text record number, offset 20
    
    /**
     * getNoteTextReca()
     * 
     * Get note text record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getNoteTextReca(){
    	return Reca.fromReca( dataStruct, 20 );
    }

    /**
     * setNoteTextReca()
     * 
     * Set note text record in dataStruct.
     * 
     * @param Reca		note text record number
     * @return void
     */
    public void setNoteTextReca( Reca reca ){
    	reca.toReca( dataStruct, 20 );
    }
    
    
  
    
    
    
	// short	type;			// intervention type according to Type enum, offset 16
    
    /**
     * getType()
     * 
     * Get type from dataStruct.
     * 
     * @param none
     * @return Type
     */
    public Types getType(){
    	return Types.get( StructHelpers.getUShort( dataStruct, 16 ));
    }

    /**
     * setType()
     * 
     * Set type in dataStruct.
     * 
     * @param Type
     * @return void
     */
    public void setType( Types type ){
    	if ( type == null ) type = Types.NONE;
    	StructHelpers.setUShort( type.getCode(), dataStruct, 16);
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
    public Reason getRemovedReason(){
    	Reason reason = Reason.get( dataStruct[62] & 0xff );
    	if ( reason == null ) reason = Reason.UNSPECIFIED;
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
    	dataStruct[62] = (byte)( reason.getCode() & 0xff );
    }


    
    

    
    
    
	// char	status;			// record status, offset 63
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	return Status.get( dataStruct[63] & 0xff );
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
    	dataStruct[63] = (byte)( status.getCode() & 0xff );
    }


    
	public static Reca postNew( Rec ptRec, Date date, Intervention.Types type, String note ){

		Intervention i = new Intervention();
		i.setDate( date );
		i.setPtRec( ptRec );
		i.setType( type );
		i.setStatus( Intervention.Status.CURRENT );
		i.setNoteText( note );
		Reca reca = i.postNew( ptRec );
		return reca;
	}



	
	
	
	
	
    /**
     * getNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getNoteText(){ return hxNotes.getNoteText(); }
      	
    /**
     * setNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setNoteText( String txt ){ hxNotes.setNoteText(txt); }
    

}
