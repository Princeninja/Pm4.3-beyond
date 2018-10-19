package palmed;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import palmed.Par.Domain;
import palmed.Par.Reason;
import palmed.Par.Severity;
import palmed.Par.Status;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;


/**
 * OLD PALMED HISTORY RECORD FORMAT
 *
 */

//from the C header file palmed/history.h

/*  history record structures  */

//typedef	struct {
//
// 0		DATE	update;			/*  last history update  */
//
// 4		RECORD	dsc1;			/*  first desc. paragraph  */
// 8		RECORD	dsc2;			/*  second desc. paragraph  */
//	
// 12		RECORD	extension[12];		/*  hst extension records  */
//
// 60		BYTE	mbdte[2];		/*  mother's birthdate  */
// 62		BYTE	medte[2];		/*  mother's expiration date  */
// 64		char	mexp[16];		/*  mother expired from  */
//
// 80		BYTE	fbdte[2];		/*  father's birthdate  */
// 82		BYTE	fedte[2];		/*  father's expiration date  */
// 84		char	fexp[16];		/*  father expired from  */
//
// 100		BYTE	brsr;			/*  # brothers and sisters  */
// 101		BYTE	sndt;			/*  # sons and daughters  */
// 102		BYTE	chcc;			/*  # kids home, in college  */
// 103		BYTE	btyp;			/*  blood type  */
//
// 104		USRT	pmhst[3];		/*  past medical hst flags  */
// 110		USRT	r1; /*immun;*/		/*  immunization flags  */
// 112		USRT	social;			/*  social history  */
// 114		USRT	grpr;			/*  grava para aborta  */
//
// 116		BYTE	menarche;		/*  menarche year  */
// 117		BYTE	menopause;		/*  menopause year  */
// 118		BYTE	mfbc;			/*  mens flow, brth cntrl  */
//
// 119		char	unused[9];		/*  unused area  */
//
//	} HSTMN;
//
//#define	SZ_HSTMN	sizeof ( HSTMN )	/*  size of HSTMN  */
//
//	sizecheck( 128, sizeof( HSTMN ), "HSTMN" );






		/*  history extension structure  */

//typedef	struct {
//
// 0		char	allergy[16];		/*  allergy  */
//
// 16		DATE	opdte;			/*  operation date  */
// 20		char	operation[16];		/*  operation  */
//
// 36		char	unused[92];		/*  unused area  */
//
//	} HSTEX;
//
//#define	SZ_HSTEX	sizeof( HSTEX )		/*  size of HSTEX  */
//
//	sizecheck( 128, sizeof( HSTEX ), "HSTEX" );







public class History {


	
	
	
	
	// fields
    private byte[] dataStruct;
    private byte[] dataStructX;
    private Rec rec;			// this Rec
    
	public static final String fnsub ="hist-mn.med";
	public static final String fnsubx ="hist-ex.med";
	public static final String fnsubn ="hist-nt.med";
	public static final int recordLength = 128;
	public static final int recordLengthX = 128;
	
    private String noteText = null;		// additional SIG text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    
    
	
    public History() {
    	allocateBuffer();
    }
	
    public History( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read record
    	if ( Rec.isValid( rec )){
    		this.read( rec );
    	}
    	
    	// set local copy of rec
    	this.rec = rec;
    }


	// read 
    
    public void read( Rec rec ){
    	
		if ( ! Rec.isValid( rec )) return;		
		RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
/*    public Rec writeNew(){
    	
    	Rec rec;		// rec
    	    	
    	
    	rec = RandomFile.newRec( this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "writeNew() bad rec" );
    	RandomFile.writeRec( rec, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());

    	return rec;
    }
*/    
    
    
/*    public boolean write( Rec rec ){
    	
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "write() bad rec" );

    	// handle addlSig text
    	if ( flgNoteTxt ){
    		Rec addlSigRec = this.writeNoteText( noteText );
    		this.setMiscSymptomRec( addlSigRec );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RandomFile.writeRec( rec, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        RandomFile.writeRec( rec, this.dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());
        return true;
    }
*/

    
    
    
    
    

	
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
       	return StructHelpers.getDate( dataStruct, 0 );
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
        StructHelpers.setDate( date, dataStruct, 0 );
        return;
    }


    
    
    

    
    
    



    
    
    

    
    
	// Rec	DescParaRec[2];		// descriptive paragraph records, offset 4
    
    /**
     * getDescParaRec()
     * 
     * Get descriptive paragraph record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getDescParaRec( int num ){
    	if ( num < 0 ) num = 0;
    	if ( num > 1 ) num = 1;
    	return Rec.fromInt( dataStruct, 4 + ( num * 4 ));
    }

    /**
     * setDescParaRec()
     * 
     * Set descriptive paragraph record in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setDescParaRec( int num, Rec rec ){
    	if ( num < 0 ) num = 0;
    	if ( num > 1 ) num = 1;
    	rec.toInt( dataStruct, 4 + ( num * 4 ));
    }
    
    
    
    
      
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	// Rec extension[12];		// history extension records, offset 12
    
    /**
     * getExtensionRec()
     * 
     * Get extension record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getExtensionRec( int num ){
    	if ( num < 0 ) num = 0;
    	if ( num > 11 ) num = 11;    	
    	return Rec.fromInt( dataStruct, 12 + ( num * 4 ));
    }

    /**
     * setExtensionRec()
     * 
     * Set extension record in dataStruct.
     * 
     * @param Rec
     * @return void
     */
    public void setExtensionRec( int num, Rec rec ){
    	if ( num < 0 ) num = 0;
    	if ( num > 11 ) num = 11;
    	rec.toInt( dataStruct, 12 + ( num * 4 ));
    }
    
    

    
    public List<String> getAllergies(){
    	
    	List<String> list = new ArrayList<String>();
    	
    	for ( int num = 0; num < 12; ++num ){
    		if ( Rec.isValid( getExtensionRec( num ))){
				RandomFile.readRec( getExtensionRec( num ), dataStructX, Pm.getMedPath(), fnsubx, getRecordLengthX());
    			String s = StructHelpers.getString( dataStructX, 0, 16 ).trim();
    			if (( s != null ) && ( s.length() > 0 )) list.add( s );
    		}
    	}
    	
    	return list;
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
    	return String.format( "%s", "History" );
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
    		
    		Rec rec = this.getDescParaRec( 0 );
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

    
    private String readNoteText( Rec rec ){
    	
    	// handle trivial case
    	if (( rec == null ) || ( ! rec.isValid())) return "";
    	   	
    	// read entry from text file
    	byte[] dataStruct = new byte[32];
		RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fnsubTxt, 32 );
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

