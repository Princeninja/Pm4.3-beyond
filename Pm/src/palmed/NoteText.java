/**
 * NoteText
 * 
 * This class is used to read/write the text part of SOAP notes, nursing notes, lab reports, etc.
 * 
 */


//typedef	struct {
//
//		RECORD	lastedit;		/*  offset of last edit  */
//		RECORD	txtlen;			/*  length of text  */
//		RECORD	ptrec;			/*  patient record number  */
//		DATE	date;			/*  date of note or edit  */
//
//	} NOTEHDR;
//
//#define	SZ_NOTEHDR	16	/*  size of NOTEHDR struct  */
///*	sizecheck( 16, sizeof( NOTEHDR ), "NOTEHDR" ); */


package palmed;

import usrlib.Date;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.StructHelpers;

public class NoteText {
		
	String fname = null;
	Date date = null;		// note date
	Rec ptrec = null;		// patient record number
	Rec lastedit = null;	// last edit record number
	int txtlen = 0;			// text length
	String text = null;		// text
	Reca reca = null;
	

	
	
	// Constructors
	
	
	
	/**
	 * Notes() - This constructor reads a text header and record and saves them so
	 * each of the parts can be retrieved later.
	 * 
	 * @param String fnsub
	 * @param Reca reca
	 */
	public NoteText( String fnsub, Reca reca ){
		
		// valid reca?
    	if ( ! Reca.isValid( reca )) return;
    	this.reca = reca;
    	
    	// read note header
    	this.fname = String.format( fnsub, reca.getYear());
    	byte[] hdrData = new byte[16];
    	RandomFile.readBytes( new Rec( reca.getRec()), 16, hdrData, Pm.getMedPath(), this.fname );
    	this.lastedit = Rec.fromInt( hdrData, 0 );
    	this.txtlen = StructHelpers.getInt( hdrData, 4 );
    	this.ptrec = Rec.fromInt( hdrData, 8 );
    	this.date = new usrlib.Date( hdrData, 12 );
    	System.out.println( "txtlen=" + txtlen + ",  ptrec=" + ptrec.toString() + ",  date=" + date.getPrintable());
    	
    	// read text
    	Rec txtrec = new Rec( reca.getRec() + 16 );
    	byte[] txtData = new byte[txtlen];
    	RandomFile.readBytes( txtrec, txtlen, txtData, Pm.getMedPath(), this.fname);
    	this.text = new String( txtData, 0, txtlen );
    	return;
	}
	
	
	
	
	/**
	 * getLastedit() - get rec offset of last edit (in this file)
	 * 
	 * @param none
	 * @return
	 */
	public Rec getLastedit(){
		return this.lastedit;
	}
	
	/**
	 * getDate() - get note Date
	 * 
	 * @param none
	 * @return Date
	 */
	public usrlib.Date getDate(){
		return this.date;
	}
	
	/**
	 * getPtRec() - get note patient record number
	 * 
	 * @param none
	 * @return Rec
	 */
	public Rec getPtRec(){
		return this.ptrec;
	}
	
	/**
	 * getTxtLen() - get note text length
	 * 
	 * @param none
	 * @return Date
	 */
	public int getTxtLen(){
		return this.txtlen;
	}
	
	/**
	 * getText() - get note Text
	 * 
	 * @param none
	 * @return Date
	 */
	public String getText(){
		return this.text;
	}
	

	
// Eventually could override this function in a sub-class for different kinds of notes
	
//	private String getFnsub(){
//		return "soaptx%02d.med";
//	}
//
	
	public static String readText( String fnsub, Reca reca ){
		return readText( fnsub, reca.getYear(), new Rec( reca.getRec()));
	}
	
	public static String readText( String fnsub, int vol, Rec txtRec ){
		
    	if ( ! Rec.isValid( txtRec )) return "";
    	String fname = String.format( fnsub, vol );    	
    	//System.out.println( "Notes  vol=" + vol + ", txtRec=" + txtRec.getRec());
    	

    	// read note header
    	byte[] hdrData = new byte[16];
    	RandomFile.readBytes( txtRec, 16, hdrData, Pm.getMedPath(), fname );

    	//System.out.println( "ptrec=" + Rec.fromInt(hdrData, 8));
    	//System.out.println( "date=" + new Date(hdrData, 12).getPrintable(9));

    	int txtlen = StructHelpers.getInt( hdrData, 4 );
    	txtRec.increment( 16 );
    	System.out.println( "txtlen=" + txtlen );
    	
    	//System.out.println( "txtlen=" + txtlen + ", txtrec=" + txtRec.getRec());
    	String s = "";
    	
    	if ( txtlen > 0 ){
	    	byte[] txtData = new byte[txtlen];
	    	RandomFile.readBytes( txtRec, txtlen, txtData, Pm.getMedPath(), fname);
	    	//String s = StructHelpers.getString( txtData, 0, txtlen );
	    	s = new String( txtData, 0, txtlen );
    	}
    	
    	//System.out.println( "text=" + s );
       	return s;
 	}
	
	
	
	public static Reca saveText( String fnsub, usrlib.Date date, Rec ptrec, Reca lastedit, String text ){
		
		int vol = Reca.todayVol();
		
    	//System.out.println( "in NoteText.saveText()" );
		
		// make sure there is a text pointer (though it could be "", and that there is a patient record
		if (( text == null ) || ( ! Rec.isValid( ptrec ))) return null;
        int txtlen = text.length();
    	System.out.println( "txtlen=" + txtlen );

    	String fname = String.format( fnsub, vol );
        RandomFile file = RandomFile.open( Pm.getMedPath(), fname, 1, RandomFile.Mode.READWRITE );

       
        // find end of file
        int rec = (int) file.length();
        //file.seek()
        
        // set data in header
    	byte[] hdrData = new byte[16];
    	lastedit.toReca(hdrData, 0);
    	StructHelpers.setInt( txtlen, hdrData, 4 );
    	ptrec.toInt( hdrData, 8 );
    	date.toBCD( hdrData, 12 );
    	
    	// write header
    	file.writeBytes( hdrData, rec, 16 );
    	
    	//set text in text buffer
    	byte[] txtData = new byte[txtlen];
    	StructHelpers.setStringNoPad( text, txtData, 0, txtlen );

    	// write text
    	if ( txtlen > 0 ) file.writeBytes( txtData, rec + 16, txtlen );
    	
    	// write delimeter
    	hdrData[0] = 0x1e;
    	file.writeBytes( hdrData, rec + 16 + txtlen, 1 );
    	
    	// close file
    	file.close();   

		return new Reca( vol, rec );
	}
	
	
	public static Reca saveText( String fnsub, int vol, usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
				
		// make sure there is a text pointer (though it could be "", and that there is a patient record
		if (( text == null ) || ( ptrec == null ) || ( ptrec.getRec() < 2 )) return null;
        int txtlen = text.length();
    	System.out.println( "txtlen=" + txtlen );

    	String fname = String.format( fnsub, vol );
        RandomFile file = RandomFile.open( Pm.getMedPath(), fname, 1, RandomFile.Mode.READWRITE );

        
        // find end of file
        int rec = (int) file.length();
        //file.seek()
        
        // set data in header
    	byte[] hdrData = new byte[16];
    	lastedit.toInt( hdrData, 0 );
    	StructHelpers.setInt( txtlen, hdrData, 4 );
    	ptrec.toInt( hdrData, 8 );
    	date.toBCD( hdrData, 12 );
    	
    	// write header
    	file.writeBytes( hdrData, rec, 16 );
    	
    	//set text in text buffer
    	byte[] txtData = new byte[txtlen];
    	StructHelpers.setStringNoPad( text, txtData, 0, txtlen );

    	// write text
    	if ( txtlen > 0 ) file.writeBytes( txtData, rec + 16, txtlen );
    	
    	// write delimeter
    	hdrData[0] = 0x1e;
    	file.writeBytes( hdrData, rec + 16 + txtlen, 1 );
    	
    	// close file
    	file.close();   

		return new Reca( vol, rec );
	}

}
