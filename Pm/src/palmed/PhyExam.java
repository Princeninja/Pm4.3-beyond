package palmed;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.RecordStatus;
import usrlib.StructHelpers;

//typedef	struct {
//
//	0	LLHDR	hdr;			/*  linked list header  */
//	8	RECORD	PtRec;			/*  patient record number  */
//	12	DATE	Date;			/*  date  */
//
//	16	RECORD	TxtRec;			/*  text record (offset)  */
//	20	USRT	TxtLen;			/*  length of text record  */
//	22	RECSH	PcnRec;			/*  physician record  */
//	24	char	Desc[40];		/*  description  */
//	64	RECSH	SignUser;		/*  signoff user  */
//	66	RECSH	User;			/*  transcribing user  */
//	68	byte	SignFlag;		/*  signoff flag  */
//	69	byte	Type;			/*  physical exam type  */
//	70	byte	Edit;			/*  number of edits  */
//	71	char	Status;			/*  status byte  */
//
//	} PHYEX;
//
///*	sizecheck( 72, sizeof( PHYEX ), "PHYEX" );*/

/*  status bytes  */

//#define	c_PHYEX_STATUS_CURRENT	'C'
//#define	c_PHYEX_STATUS_REMOVED	'R'
//
//#define	s_PHYEX_STATUS_CURRENT	"C"
//#define	s_PHYEX_STATUS_REMOVED	"R"


//TODO Signature Reca etc



public class PhyExam implements LLItem {
	// static fields
	private final static String fn_phyex = "phy%02d.med";
	private final static String fn_phytxt = "phyt%02d.med";
	
	final static String[] soapDot = {
		"S....", "O....", "A....", "P....", "N...."
	};

	final static String[] soapHdr = {
		"Subjective", "Objective", "Assessment", "Plan", "Annotation"
	};


	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    private String text;		// temp holder for text
    private int vol;
    
    class PhyNotes extends NoteTextLongHelper {
    	@Override
    	public String getFnsub(){ return fn_phytxt; }
    	@Override
    	public void setRec( Rec rec ){ setTextRec(rec); }
    	@Override
    	public Rec getRec(){ return getTextRec(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    PhyNotes pNotes = null;
    
    
	
    public PhyExam() {
    	allocateBuffer();
    	this.pNotes = new PhyNotes();
    }
	
    public PhyExam( Reca reca ){

        // allocate space
    	allocateBuffer();
    	this.pNotes = new PhyNotes();
    	
    	/*// read Note
    	if ( reca.getRec() > 1 ){
    		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_phyex, PhyExam.getRecordLength());
    	}
    	
    	// set local copy of reca
    	this.reca = reca;*/
    	
    	this.read(reca);
    }


	// read Note
    
    public void read( Reca reca ){
    	
    	pNotes.resetReadStatus();
    	pNotes.setVol( reca.getYear());
    	
		//if ( reca.getRec() < 2 ) return;	
    	if ( ! Reca.isValid( reca )){ SystemHelpers.seriousError( "bad phyReca" ); return; }
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_phyex, PhyExam.getRecordLength());
		this.reca = reca;
		
    }	

    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// write text notes records
    	pNotes.setVol( vol );
    	pNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fn_phyex, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );
    	this.reca = reca;

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	// write free text notes records
    	pNotes.setVol( reca.getYear());
    	pNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_phyex, getRecordLength());
        return true;
        
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getPhyReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setPhyReca(reca); }
				}
		);
		// this.reca should be set by the writeNew that LLPost.post should call
		return reca;
    }
	
    
    
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( 72 ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[72];
    }

    public void setDataStruct(byte[] dataStruct) { this.dataStruct = dataStruct; }

    public byte[] getDataStruct() { return dataStruct; }

   
    
    
    
    
    
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


    
    
    

	// char	Desc[40];		// description, offset 24
    
    /**
     * getDesc()
     * 
     * Get description from dataStruct.
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
     * Set description in dataStruct.
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

    
    
    
	// RECSH pcnrec;		// physician record, offset 22
    
    /**
     * getProvRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProvRec(){
    	return new Rec( StructHelpers.getShort( dataStruct, 22 ));
    }

    /**
     * setProvRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec rec
     * @return void
     */
    public void setProvRec( Rec rec ){
    	StructHelpers.setShort( rec.getRec(), dataStruct, 22 );
    }

    
    
    
    
    
	// RECSH UserRec;		// transcribing user record, offset 66
    
    /**
     * getUserRec()
     * 
     * Get user record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getUserRec(){
    	return new Rec( StructHelpers.getShort( dataStruct, 66 ));
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
    	StructHelpers.setShort( rec.getRec(), dataStruct, 66 );
    }

    
    
    
    
    
	// RECA Signature;		// signature record number, offset 68
    
//    /**
//     * getSignatureReca()
//     * 
//     * Get signature record from dataStruct.
//     * 
//     * @param none
//     * @return Reca
//     */
//    public Reca getSignatureReca(){
//    	return Reca.fromReca( dataStruct, 68 );
//    }
//
//    /**
//     * setSignatureReca()
//     * 
//     * Set signature record in dataStruct.
//     * 
//     * @param Reca		signature record number
//     * @return void
//     */
//    public void setSignatureReca( Reca reca ){
//    	reca.toReca( dataStruct, 68 );
//    }
    
    
    
    
    
    // char unused			// unused field, offset 60;
    
    
    
	// byte type;			// physical exam type????, offset 69
    
    /**
     * getType()
     * 
     * Get type from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getType(){
    	return (int) dataStruct[69];
    }

    /**
     * setType()
     * 
     * Set type in dataStruct.
     * 
     * @param int		'type'
     * @return void
     */
    public void setType( int num ){
    	dataStruct[69] = (byte)( 0xff & num );
    }


    
    
    


    
    
	// byte edit;			// number of edits, offset 70
    
    /**
     * getNumEdits()
     * 
     * Get number of edits from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumEdits(){
    	return (int) dataStruct[70];
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
    	dataStruct[70] = (byte)( 0xff & num );
    }


    
    
    


	// char	status;			// record status, offset 71
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getStatus(){
    	return (int) dataStruct[71];
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param int		'status'
     * @return void
     */
    public void setStatus( RecordStatus status ){
    	dataStruct[71] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }


    
 // public void setVol()
    
    public void setVol( int vol ){
    	this.vol = vol;
    }
    
    

    
    
    /**
     * getText()
     * 
     * Get actual text from the note text file.
     * 
     * @param none
     * @return Rec
     */
    public String getText(){
    	
    	// handle no text
    	if (( this.reca == null ) || ( this.reca.getRec() < 2 )) return "";
    	
    	return NoteText.readText( fn_phytxt, this.reca.getYear(), this.getTextRec());
    	//Rec txtrec = this.getTextRec();
    	//String fname = new String().format( fn_soaptxt, this.reca.getYear());
    	//byte[] data = new byte[this.getTextLen()];
    	//RandomFile.readBytes( new Rec( getTextRec().getRec() + 16 ), getTextLen(), data, Pm.getMedPath(), fname);
    	//File file = new File();
    	//return new String( data, 0, getTextLen());
    	
    	/*System.out.println( "called getText" );
    	String txt = pNotes.getNoteText();
    	System.out.println( " getText=" + txt );
    	return txt; */
    	
    }
    
    
    public void setText( String txt ){
    	System.out.println( "called setText=" + txt );
    	pNotes.setNoteText( txt );
    	return;
    	
    }
    
    
    /**
     * writeText()
     * 
     * Write new note text into the note text file.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
   /* public Rec writeText( usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
    	return NoteTextLong.saveTextLong( fn_phytxt, vol, date, ptrec, lastedit, text );
    }*/

    public Reca writeText( usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
    	int vol = Reca.isValid( this.reca ) ? reca.getYear(): ( Date.today().getYear() % 100 );
    	return NoteText.saveText( fn_phytxt, vol, date, ptrec, lastedit, text );
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

	


}
