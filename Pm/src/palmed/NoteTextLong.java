package palmed;

import usrlib.Date;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;

public class NoteTextLong {

	String fname = null;
	Date date = null;		// note date
	Rec ptrec = null;		// patient record number
	Rec lastedit = null;	// last edit record number
	int txtlen = 0;			// text length
	String text = null;		// text
	
	Rec rec = null;
	int vol = 0;
	

	
	
	// Constructors
	
	
	
	/**
	 * Notes() - This constructor 
	 * 
	 */
	private NoteTextLong(){
	}
	
	
	
	
	/**
	 * Notes() - This constructor reads a text header and record and saves them so
	 * each of the parts can be retrieved later.
	 * 
	 * @param String fnsub
	 * @param Reca reca
	 */
	public static NoteTextLong read( String fnsub, int vol, Rec rec ){

    	System.out.println( "in NoteTextLong.read() vol=" + vol + ",  rec=" + rec.toString() );
		
		// valid reca?
    	if ( ! Rec.isValid( rec )) return null;
    	
    	NoteTextLong note = new NoteTextLong();
    	note.rec = rec;
    	note.vol = vol;
    	
    	// read note header
    	note.fname = String.format( fnsub, vol );
    	byte[] hdrData = new byte[16];
    	RandomFile.readBytes( rec, 16, hdrData, Pm.getMedPath(), note.fname );
    	
    	// decode header info
    	note.lastedit = Rec.fromInt( hdrData, 0 );
    	note.txtlen = StructHelpers.getInt( hdrData, 4 );
    	note.ptrec = Rec.fromInt( hdrData, 8 );
    	note.date = new usrlib.Date( hdrData, 12 );
    	System.out.println( "txtlen=" + note.txtlen + ",  ptrec=" + note.ptrec.toString() + ",  date=" + note.date.getPrintable());
    	
    	// read text
    	Rec txtrec = new Rec( rec.getRec() + 16 );
    	byte[] txtData = new byte[note.txtlen];
    	RandomFile.readBytes( txtrec, note.txtlen, txtData, Pm.getMedPath(), note.fname);
    	note.text = new String( txtData, 0, note.txtlen );
    	return note;
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
	

	
	
	
	public static String readText( String fnsub, int vol, Rec txtRec ){
		NoteTextLong note = NoteTextLong.read( fnsub, vol, txtRec );
		return note.getText();
	}
	
	
	
	
	
	
	public static Rec saveTextLong( String fnsub, int vol, usrlib.Date date, Rec ptrec, Rec lastedit, String text ){
		
		// make sure there is a text pointer (though it could be "", and that there is a patient record
		if (( text == null ) || ! Rec.isValid( ptrec )) return null;
		
        int txtlen = text.length();

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
    	System.out.println( "in NoteText.saveText() rec=" + rec + " vol=" + vol );

		return new Rec( rec );
	}
}
