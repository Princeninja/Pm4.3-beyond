package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.HxFamilyItem.Member;
import palmed.Par.Severity;
import palmed.Par.Status;
import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StackTraceInfo;
import usrlib.StructHelpers;


//NEW

//typedef struct {
//
// 0	LLHDR	hdr;				/*  history linked list header  */
//
// 8	DATE	Date;				/*  start date  */
// 12	RECORD	ptRec;				/*  patient record number  */
//
// 16	char	desc[40];			/*  problem text  */
//
// 56	long	probTblRec;			/*  probTblRec - to link to record in ProbTbl
//
// 60	byte	unused[56];
//
// 116	Rec		rdocID;				/*  referring doctor  */
// 120 	Reca	noteReca;			// note text reca
//
// 124	byte	unused1;			/*  unused byte  */
// 125	byte	flgMisc;			// flag as Misc Y/N
// 126	byte	Type;				/*  type:  S-surgical, I-illness, J-injury, P-procedure  */
// 127	byte	Status;				/*  record status  */
//
//	} PROCEDURE;
//
//*	sizecheck( 128, sizeof( PROCEDURE ), "PROCEDURE" );*/



public class Procedure implements LLItem {

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


	public enum Type {
		
		NONE( "", ' ' ),
		PROCEDURE( "Prodedure", 'P' ),	
		SURGERY( "Surgery", 'S' ),
		ILLNESS( "Illness", 'I' ),
		INJURY( "Injury", 'J' ),
		UNSPECIFIED( "Unspecified", 'U' );
		
		private String label;
		private int code;

		private static final Map<Integer, Type> lookup = new HashMap<Integer,Type>();
		private static final Map<String, Type> lookupLabel = new HashMap<String,Type>();
		
		static {
			for ( Type r : EnumSet.allOf(Type.class)){
				lookup.put(r.getCode(), r );
				lookupLabel.put(r.getLabel(), r );
			}
		}


		Type ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Type get( int code ){ return lookup.get( code & 0xff ); }
		public static Type get( String label ){ return lookupLabel.get( label ); }
	}


	
	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    private static String fn_procedure = "proc%02d.med";
    private static int reclen = 128;
    
    private String noteText = null;		// additional SIG text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read

    
    
	
    public Procedure() {
    	allocateBuffer();
    }
	
    public Procedure( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read record
   		read( reca );
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read record
    
    public void read( Reca reca ){
    	
    	flgNoteTxtRead = false;				// haven't read note text record yet
    	
    	if ( ! Reca.isValid( reca )){ SystemHelpers.seriousError( "read() bad reca" ); return; }
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_procedure, getRecordLength());
    }	

    
    // Write new record
    public Reca writeNew(){
    	
    	// handle note text
    	if ( flgNoteTxt ){
    		Reca noteReca = this.writeNoteText( noteText );
    		this.setNoteReca( noteReca );
    		flgNoteTxt = false;
    	}
    	
    	Reca reca = RecaFile.newReca( Reca.todayVol(), this.dataStruct, Pm.getMedPath(), fn_procedure, getRecordLength());
    	if (( reca == null ) || ( reca.getRec() < 2 )) SystemHelpers.seriousError( "Prob.writeNew() bad reca" );
    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

       	// handle addlSig text
    	if ( flgNoteTxt ){
    		Reca noteReca = this.writeNoteText( noteText );
    		this.setNoteReca( noteReca );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_procedure, getRecordLength());
        return true;
    }


    

   public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getProceduresReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setProceduresReca(reca); }
				}
		);
		return reca;
    }
    
    
    

	


	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( reclen ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[reclen];
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
     * Get start date from dataStruct.
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
     * @param usrlib.Date		start date
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


    
    
    

 
   
    /**
     * getDesc()
     * 
     * Get description.  If there is a value in DgnRec, read Dgn and return
     * its description.  If not, return the text in the misc desc field problem[].
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	
    	String s = "";
    	Rec probTblRec = getProbTblRec();
    	
    	if ( ! Rec.isValid( probTblRec )){
    		s = getMiscDesc();
    	} else {
    		return ProbTbl.getDesc( probTblRec );
    	}
    	return s;
    }

    
    

    
    /**
     * getCode()
     * 
     * Get problem code.  If there is a value in ProbTblRec (DgnRec), read ProbTbl and return
     * its code.  If not, return empty string (un-coded).
     * 
     * @param none
     * @return String
     */
    public String getCode(){
    	
    	String s = "";
    	Rec probTblRec = getProbTblRec();
    	
    	if ( Rec.isValid( probTblRec )){
    		s = ProbTbl.getSNOMED( probTblRec );
    	}
    	return s;
    }


    
    
    
   
    
	// char	desc[40];		// description, offset 16

    /**
     * getMiscDesc()
     * 
     * Get problem misc description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getMiscDesc(){
    	return StructHelpers.getString( dataStruct, 16, 40 ).trim();
    }

    /**
     * setMiscDesc()
     * 
     * Set problem misc description in dataStruct.
     * 
     * @param String		desc
     * @return void
     */
    public void setMiscDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 16, 40 );
    	return ;
    }


    
    
    

    
    
    
	// long	probTblRec;		// ProbTbl entry record number, offset 56
    
    /**
     * getProbTblRec()
     * 
     * Get probTbl record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProbTblRec(){
    	return Rec.fromInt( dataStruct, 56 );
    }

    /**
     * setProbTblRec()
     * 
     * Set ProbTbl record in dataStruct.
     * 
     * @param Rec		diagnosis record number
     * @return void
     */
    public void setProbTblRec( Rec rec ){
    	rec.toInt( dataStruct, 56 );
    }
    
    
    
    
    
    

	// long	rdocRec;			// performing rdoc, offset 116
    
    /**
     * getRdocRec()
     * 
     * Get Rdoc record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getRdocRec(){
    	return Rec.fromInt( dataStruct, 116 );
    }

    /**
     * setRdocRec()
     * 
     * Set Rdoc record in dataStruct.
     * 
     * @param Rec		Rdoc record number
     * @return void
     */
    public void setRdocRec( Rec rec ){
    	rec.toInt( dataStruct, 116 );
    }
    

    
    
    

	// RECA	NoteReca;		// note text record number, offset 120
    
    /**
     * getNoteReca()
     * 
     * Get note text record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Reca getNoteReca(){
    	return Reca.fromReca( dataStruct, 120 );
    }

    /**
     * setNoteReca()
     * 
     * Set note text record in dataStruct.
     * 
     * @param Rec		note text record number
     * @return void
     */
    public void setNoteReca( Reca reca ){
    	reca.toReca( dataStruct, 120 );
    }
    
    
    
    

 	// char	flgMisc;			// flag as misc entry, offset 125
    
    /**
     * getFlgMisc()
     * 
     * Get misc flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMisc(){
    	return ! ( dataStruct[125] == 'N' );
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
    	dataStruct[125] = (byte) (flg ? 'Y': 'N');
    }


    
    

    
    


 	// char	type;			// NEW type, offset 126
    
    /**
     * getType()
     * 
     * Get Type from dataStruct.
     * 
     * @param none
     * @return Type
     */
    public Procedure.Type getType(){
    	Procedure.Type type = Procedure.Type.get( dataStruct[126] & 0xff );
    	return ( type == null ) ? Procedure.Type.UNSPECIFIED: type;
    }

    /**
     * setType()
     * 
     * Set Type in dataStruct.
     * 
     * @param Type
     * @return void
     */
    public void setType( Type type ){
    	dataStruct[126] = (byte)( type.getCode() & 0xff );
    }


    
    
 
    

    
    
    

	// char	status;			// record status, offset 127
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Procedure.Status getStatus(){
    	return Procedure.Status.get( dataStruct[127] & 0xff );
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Procedure.Status status ){
    	dataStruct[127] = (byte)( status.getCode() & 0xff );
    }


    
    
    

    
    
    

	// char	unused1;		// unused field, offset 79
    
    
    
    
    
    
    
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
    		
    		Reca reca = this.getNoteReca();
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


    
    
    private String fnsubTxt = "proct%02d.med";
    private int txtRecLen = 80;
    
    private String readNoteText( Reca reca ){
    	
    	// handle trivial case
    	if (( reca == null ) || ( ! reca.isValid())) return "";
    	   	
    	// read entry from text file
    	byte[] dataStruct = new byte[txtRecLen];
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsubTxt, txtRecLen );
		String s = StructHelpers.getString(dataStruct, 0, txtRecLen );
		return s.trim();
    }
    
    private Reca writeNoteText( String text ){
    	
     	// handle trivial case
    	if ( text == null ) return null;
    	
    	byte[] dataStruct = new byte[txtRecLen];
    	StructHelpers.setStringPadded( text, dataStruct, 0, txtRecLen );
    	Reca reca = RecaFile.newReca( Reca.todayVol(), dataStruct, Pm.getMedPath(), fnsubTxt, txtRecLen );
    	
    	return reca;
    }

   
    
    
    
    /**
     * Mark this problem as removed. (Set status to 'R' for 'Removed'.)
     * 
     * @param reca
     * @return boolean
     */
    public static boolean markRemoved( Reca reca ){ 
    	System.out.println( StackTraceInfo.getCurrentClassName() + "." + StackTraceInfo.getCurrentMethodName() + "() hello world" );
    	if ( ! Reca.isValid( reca )) { SystemHelpers.seriousError( StackTraceInfo.getCurrentClassName() + "." + StackTraceInfo.getCurrentMethodName() + "() bad reca" ); return false; }
		Procedure fam = new Procedure( reca );
		fam.setStatus( Procedure.Status.REMOVED );
		fam.write( reca );
		return true;
    }
    


    /**
     * Save new procedure item record and link to patient.
     * 
     * @param ptRec
     * @param date
     * @param type
     * @param probTblRec
     * @param desc
     * @param note
     * @param rdocRec
     * @return Reca
     */
    public static Reca postNew( Rec ptRec, Date date, Procedure.Type type, Rec probTblRec, String desc, String note, Rec rdocRec ){	    	
		// save item
		Procedure proc = new Procedure();
		proc.setPtRec( ptRec );
		proc.setDate( date );
		proc.setFlgMisc( false );
		proc.setProbTblRec( probTblRec );
		proc.setMiscDesc( desc );
		proc.setNoteTxt( note );
		proc.setRdocRec( rdocRec );
		proc.setStatus( Procedure.Status.CURRENT );
		Reca reca = proc.postNew( ptRec );
		return reca;
    }
	
   
    
    

    /**
     * Save new MISC procedure item record and link to patient.
     * 
     * @param ptRec
     * @param date
     * @param type
     * @param probTblRec
     * @param desc
     * @param note
     * @param rdocRec
     * @return Reca
     */
    public static Reca postNewMisc( Rec ptRec, Date date, Procedure.Type type, String desc, String note, Rec rdocRec ){	    	
		// save item
		Procedure proc = new Procedure();
		proc.setPtRec( ptRec );
		proc.setDate( date );
		proc.setProbTblRec( new Rec());
		proc.setFlgMisc( true );
		proc.setMiscDesc( desc );
		proc.setNoteTxt( note );
		proc.setRdocRec( rdocRec );
		proc.setStatus( Procedure.Status.CURRENT );
		Reca reca = proc.postNew( ptRec );
		return reca;
    }
	
   
    
    

}


/**/

