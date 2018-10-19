package palmed;

import palmed.HxMisc.BloodNotes;
import usrlib.Date;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.RecordStatus;





//typedef	struct {
//
// 0	LLHDR	hdr;			/*  linked list header  */
// 8	RECORD	PtRec;			/*  patient record number  */
// 12	DATE	Date;			/*  date  */
//
// 16	RECORD	txtrec;			/*  text record (offset)  */
// 20	USRT	txtlen;			/*  length of text record  */
// 22	RECSH	pcnrec;			/*  physician record  */
// 24	char	Desc[40];		/*  description  */
// 64	char	unused1[2];		/*  unused  */
// 66	RECSH	User;			/*  transcribing user  */
// 68	RECA	Signature;		/*  link to signature  */
// 72	byte	unused[6];
// 78	byte	edit;			/*  number of edits  */
// 79	char	Status;			/*  status flag  */
//
//	} SOAP;
//
//#define	SZ_SOAP		sizeof( SOAP )		/*  size of SOAP struct  */
//
///*	sizecheck( 80, sizeof( SOAP ), "SOAP" );
//*/






public class SoapNote implements LLItem {
	
	// static fields
	private final static String fn_soap = "soap%02d.med";
	private final static String fn_soaptxt = "soaptx%02d.med";
	private final static int recordLength = 80;
	
	final static String[] soapDot = {
		"S....", "O....", "A....", "P....", "N...."
	};

	final static String[] soapHdr = {
		"Subjective", "Objective", "Assessment", "Plan", "Annotation"
	};


	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    private int vol;
    private String soapText;	// temp holder for SOAP text
    
    
    class SNotes extends NoteTextLongHelper {
    	@Override
	    public String getFnsub(){ return fn_soaptxt; }
    	@Override
    	public void setRec( Rec rec ){ setTextRec(rec); }
    	@Override
    	public Rec getRec(){ return getTextRec(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    SNotes sNotes = null;

    

    

	
    public SoapNote() {
    	allocateBuffer();
    	this.sNotes = new SNotes();
    }
	
    public SoapNote( Reca reca ){

        // allocate space
    	allocateBuffer();
    	this.sNotes = new SNotes();
    	
    	this.read( reca );
    }


	// read SOAP Note
    
    public void read( Reca reca ){
    	
    	sNotes.resetReadStatus();
    	sNotes.setVol( reca.getYear());
    	
		if ( ! Reca.isValid( reca )){ SystemHelpers.seriousError( "bad soapReca" ); return;	}	
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_soap, getRecordLength());
    	this.reca = reca;
    }	


    
    
    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// write text notes records
    	sNotes.setVol( vol );
    	sNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fn_soap, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );
    	this.reca = reca;

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	
    	// write free text notes records
    	sNotes.setVol( reca.getYear());
    	sNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_soap, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getSoapReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setSoapReca(reca); }
				}
		);
		// this.reca should be set by the writeNew that LLPost.post should call
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


    
    
    

	// char	Desc[40];		// SOAP description, offset 16
    
    /**
     * getDesc()
     * 
     * Get SOAP description from dataStruct.
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
     * Set SOAP description in dataStruct.
     * 
     * @param String		description
     * @return void
     */
    public void setDesc( String desc ){
    	StructHelpers.setStringPadded(desc, dataStruct, 24, 40 );
    	return ;
    }


    
    
    

    
    
	// RECORD txtrec;		// text record number, offset 16
    // Reca textReca;		// NEW text Reca record number, offset 72
    
    // TODO NOTE (i wanted to do this - but the problem is it only leaves 24 bits for text file which limits
    //      it to a max of 16MB - way to small)   
    // - the new textReca will replace the old text record number.  We will keep the old around
    // for backward compatibility.  At some point after the old systems are retired, I can clean up
    // this code by writing a utility to move all the txtrec fields into new textReca fields.
    
    /**
     * getTextRec()
     * 
     * Get text record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getTextRec(){
    	System.out.println( "called getTextRec=" + Rec.fromInt( dataStruct, 16 ).getRecInt() );
    	return Rec.fromInt( dataStruct, 16 );
    }

//    public Reca getTextReca(){
//   	// if new reca is stored, return it, done
//    	Reca reca = Reca.fromReca( dataStruct, 72 );
//    	System.out.println( "in SoapNote.getTextReca() reca=" + reca.toString() );
//    	if ( Reca.isValid( reca )) return reca;
//    	
//    	// else build a reca from rec and date
//    	Rec rec = Rec.fromInt( dataStruct, 16 );
//    	if ( Rec.isValid( rec )){
//	    	int vol = this.getDate().getYear() % 100;
//	    	reca = new Reca( vol, rec.getRecInt());
//	    	System.out.println( "in SoapNote.getTextReca() built reca=" + reca.toString() );
//    	} else {
//    		reca = new Reca();
//	    	System.out.println( "in SoapNote.getTextReca() empty reca returned" );
//    	}
 //   	return reca;
//    }
    
    
    /**
     * setTextRec()
     * 
     * Set text record in dataStruct.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
    public void setTextRec( Rec rec ){
    	System.out.println( "called setTextRec=" + rec.getRecInt() );
    	rec.toInt( dataStruct, 16 );
    }

//    public void setTextReca( Reca reca ){
//   	// set reca into dataStruct
//    	reca.toReca( dataStruct, 72 );
//    	System.out.println( "in SoapNote.setTextReca() reca=" + reca.toString() );
//    	// zero out old rec
//    	new Rec( 0 ).toInt( dataStruct, 16 );
//    	return;
//    }

   
    
    
    
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
    	System.out.println( "called getTextLen=" + StructHelpers.getShort( dataStruct, 20 ) );
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
    	System.out.println( "called setTextLen=" + len );
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

    
    
    
    
    
	// RECA Signature;		// signature record number, offset 68
    
    /**
     * getSignatureReca()
     * 
     * Get signature record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getSignatureReca(){
    	return Reca.fromReca( dataStruct, 68 );
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
    	reca.toReca( dataStruct, 68 );
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * getText()
     * 
     * Get full SOAP text from the soap text file.
     * 
     * @param none
     * @return Rec
     */
    public String getText(){
    	System.out.println( "called getText" );
    	String txt = sNotes.getNoteText();
    	System.out.println( "  getText=" + txt );
    	return txt;
    }
    
    
    
    /**
     * setText() - set full text
     */
    public void setText( String txt ){
    	System.out.println( "called setText=" + txt );
    	sNotes.setNoteText( txt );
    	return;
    }
    
    
    
    
    /**
     * getSubjText() - get subjective section text
     * 
     */
    public String getSubjText(){
    	// get note text
    	System.out.println( "called getSubjText" );
    	String txt = getText();
    	System.out.println( "txt=" + txt );
    	
/*
    	// find end of subjective (first) part
    	int end = txt.indexOf( '\n' );
    	System.out.println( "end=" + end );
    	if ( end < 0 ) end = txt.length();
    	// return just subjective (first) part
    	return txt.substring( 0, end );
*/
    	String splits[] = txt.split( "\n", 5 );
    	if ( splits.length < 1 ) return "";
    	return splits[0];
    }
    
    /**
     * getObjText() - get objective section text
     * 
     */
    public String getObjText(){
    	System.out.println( "called getObjText" );
    	// get note text
    	String txt = getText();
    	// find start of obj (second) part
 /*   	int start = txt.indexOf( '\n' );
    	if ( start < 0 ) return "";
    	++start;
    	System.out.println( "start=" + start );
    	
    	// find end of obj (second) part
    	int end = txt.indexOf( '\n' );
    	if ( end < 0 ) end = txt.length();
    	
    	System.out.println( "end=" + end );

    	return txt.substring( start, txt.indexOf( '\n', start ));
*/
    	String splits[] = txt.split( "\n", 5 );
    	if ( splits.length < 2 ) return "";
    	return splits[1];
    }
    
    /**
     * getAssText() - get assessment section text
     * 
     */
    public String getAssText(){
    	System.out.println( "called getAssText" );
    	// get note text
    	String txt = getText();
/*    	int start = txt.indexOf( '\n' );
    	start = txt.indexOf( '\n', start + 1 ) + 1;
    	return txt.substring( start, txt.indexOf( '\n', start ));
*/
    	
    	String splits[] = txt.split( "\n", 5 );
    	if ( splits.length < 3 ) return "";
    	return splits[2];
    }
    
    /**
     * getPlanText() - get plan section text
     * 
     */
    public String getPlanText(){
    	System.out.println( "called getPlanText" );
    	// get note text
    	String txt = getText();
/*    	int start = txt.indexOf( '\n' );
    	start = txt.indexOf( '\n', start + 1 );
    	start = txt.indexOf( '\n', start + 1 ) + 1;
    	int end = txt.indexOf( '\n', start + 1 );
    	if ( end < start ) end = txt.length();
    	return txt.substring( start, end );
*/
    	
    	String splits[] = txt.split( "\n", 5 );
    	if ( splits.length < 4 ) return "";
    	return splits[3];
    }
    
    
    
    /**
     * setSubjText() - set subjective section text
     */
//    public void setSubjText(){
//    	return;
//    }
    
    
    
    
    
    
    // char unused			// unused field, offset 60;
    
    
    
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


    
    
    


	// byte	status;			// record status, offset 79

    /**
     * getStatus()
     * 
     * Get Status from dataStruct.
     * 
     * @param none
     * @return usrlib.RecordStatus
     */
    public usrlib.RecordStatus getStatus(){
    	return usrlib.RecordStatus.get( (char) dataStruct[79] );
    }

    /**
     * setStatus()
     * 
     * Set Status in dataStruct.
     * 
     * @param usrlib.RecordStatus
     * @return void
     */
    public void setStatus( usrlib.RecordStatus status ){
    	dataStruct[79] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }
    
    


    

    


    
    

    
    

    /**
     * writeText()
     * 
     * Write new soap text into the soap text file.
     * 
     * @param Rec		misc symptom record number
     * @return void
     */
    public Reca writeText( usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
    	int vol = Reca.isValid( this.reca ) ? reca.getYear(): ( Date.today().getYear() % 100 );
    	return NoteText.saveText( fn_soaptxt, vol, date, ptrec, lastedit, text );
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
    	return String.format( "%s %s", this.getDate().getPrintable(), this.getDesc());
    }

    
    
    /**
     * delete()
     * 
     * Delete a previously saved SOAP note.
     * 
     */
    public static void delete( Reca soapReca ){
    	if ( ! Reca.isValid( soapReca )){ SystemHelpers.seriousError( "bad soapReca" ); return; }
    	SoapNote soapnt = new SoapNote( soapReca );
    	soapnt.setStatus( RecordStatus.REMOVED );
    	soapnt.write( soapReca );
    	return;
    }

}
