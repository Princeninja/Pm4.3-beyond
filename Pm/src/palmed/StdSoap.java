package palmed;


/**
 * STDSOAP - Soap Note Standard Dictations
 * 
 * 
 * C data structure
 * file - c/palmed/include/stdsoap.h
 * # 08-12-97 jrp - created
 * 
 * 	typedef struct {
 * 
 * 		DATE	modDate;		//  last modified date
 * 		char	Desc[40];		//  description
 * 		long	txtrec[4];		//  text records
 * 		char	unused[3];		//  unused
 * 		char	Status;			//  status flag
 * 
 * } STDSOAP;
 * 
 * sizecheck( 64, sizeof( STDSOAP ), "STDSOAP" );
 * 
 */



import palmed.HxMisc.BloodNotes;
//import palmed.Notes.NoteType;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;

public class StdSoap {
	
	private final static String fn_stdsoap = "";
	private final static String fn_stdtxt = "";
	private final static int recordLength = 64;
	
    private byte[] dataStruct;
    private Rec rec = null;				// this Rec
    private String noteText = null;		// temp holder for Note text

	public static final String fnsub = "hxmisc%02d.med";
	public static final String fnsubt = "hxmisct%02d.med";
	
    
//    class BloodNotes extends NoteTextHelper {
//    	@Override
//	    public String getFnsub(){ return fnsubt; }
//    	@Override
//    	public void setReca( Reca reca ){ setBloodNoteReca( reca ); }
 //   	@Override
//    	public Reca getReca(){ return getBloodNoteReca(); }
//    	@Override
//    	public Rec getRefRec(){ return getPtRec(); }
//    }
    
//    BloodNotes bloodNotes = null;



	/*  Note Status flags  */

	//#define	c_NOTE_STATUS_CURRENT	'C'
	//#define	c_NOTE_STATUS_REMOVED	'R'
	//#define	c_NOTE_STATUS_EDITED	'E'
	//
	//#define	s_NOTE_STATUS_CURRENT	"C"
	//#define	s_NOTE_STATUS_REMOVED	"R"
	//#define	s_NOTE_STATUS_EDITED	"E"




								

	    // Constructors
	    
	    public StdSoap(){
	    	allocateBuffer();
	    }
		
	    public StdSoap( Rec rec ){

	        // allocate space
	    	allocateBuffer();
	    	
	    	// read Note
	    	read( rec );
	    	
	    	// set local copy of rec
	    	this.rec = rec;
	    }

	    

		// read Note
	    
	    public void read( Rec rec ){
	    	
			if ( rec.getRec() < 2 ) return;		
			RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fn_stdsoap, Notes.getRecordLength());
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

	    
	    
	   
	    
	    
	    
	    
	    
	    
	    

	    // DATE Date;			// date, offset 0  
	    
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


	    
	    
	    
		// char	Desc[40];		// StdSoap description, offset 4
	    
	    /**
	     * getDesc()
	     * 
	     * Get StdSoap description from dataStruct.
	     * 
	     * @param none
	     * @return String
	     */
	    public String getDesc(){
	    	return StructHelpers.getString( dataStruct, 4, 40 ).trim();
	    }

	    /**
	     * setDesc()
	     * 
	     * Set StdSoap description in dataStruct.
	     * 
	     * @param String		description
	     * @return void
	     */
	    public void setDesc( String desc ){
	    	StructHelpers.setStringPadded(desc, dataStruct, 4, 40 );
	    	return ;
	    }


	    
	    
	    

	    
	    
		// RECORD txtrec;		// text record number, offset 44
	    
	    /**
	     * getTextRec()
	     * 
	     * Get text record from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getTextRec( int num ){
	    	// make sure num is in range
	    	if (( num < 0 ) || ( num > 3 )) num = 0;
	    	return Rec.fromInt( dataStruct, 44 + ( num * 4 ) );
	    }

	    /**
	     * setTextRec()
	     * 
	     * Set text record in dataStruct.
	     * 
	     * @param Rec		misc symptom record number
	     * @return void
	     */
	    public void setTextRec( Rec rec, int num ){
	    	// make sure num is in range
	    	if (( num < 0 ) || ( num > 3 )) num = 0;
	    	rec.toInt( dataStruct, 44 + ( num * 4 ));
	    }

	    
	    
	    
	    
	    
	    
	    
	    
	    
	    	    
	    


		// char	status;			// record status, offset 63
	    
	    /**
	     * getStatus()
	     * 
	     * Get status from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getStatus(){
	    	return (int) dataStruct[63];
	    }

	    /**
	     * setStatus()
	     * 
	     * Set status in dataStruct.
	     * 
	     * @param int		'status'
	     * @return void
	     */
	    public void setStatus( int num ){
	    	dataStruct[63] = (byte)( 0xff & num );
	    }


	    
	    

	    
	    
	    /**
	     * getText()
	     * 
	     * Get actual SOAP text from the soap text file.
	     * 
	     * @param num
	     * @return Rec
	     */
	    public String getText( int num ){
	    	
	    	// make sure num is in range
	    	if (( num < 0 ) || ( num > 3 )) num = 0;
	    	// read and return text
	    	return readText( getTextRec( num ));
	    }

	    
	    
	    
	    /**
	     * readText()
	     * 
	     * Read the actual StdSOAP text from the soap text file.
	     * 
	     * @param Rec
	     * @return String
	     */
	    public String readText( Rec txtrec ){
	    	
	    	// handle no text rec passed
	    	if (( txtrec == null ) || ( txtrec.getRec() < 2 )) return "";
	    	
	    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_stdtxt, 1, RandomFile.Mode.READONLY );
	    	
	    	byte[] hdrData = new byte[16];
	    	file.readBytes( hdrData, txtrec.getRec(), 16 );
	    	
	    	//this.lastedit = Rec.fromInt( hdrData, 0 );
	    	int txtlen = StructHelpers.getInt( hdrData, 4 );
	    	//this.ptrec = Rec.fromInt( hdrData, 8 );
	    	//this.date = new usrlib.Date( hdrData, 12 );

	    	// read text
	    	byte[] txtData = new byte[txtlen];
	    	file.readBytes( txtData, txtrec.getRec() + 16, txtlen );
	    	file.close();

	    	String text = new String( txtData, 0, txtlen );
	    	
	    	return text;
	    }
	    
	    
	    /**
	     * writeText()
	     * 
	     * Write new stdsoap text into the stdsoap text file.
	     * 
	     * @param String
	     * @return Rec
	     */
	    private Rec writeText( String text ){
	    	
	    	if ( text == null ) text = "";
	    	
	    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_stdtxt, 1, RandomFile.Mode.READWRITE );
	    	
	    	byte[] hdrData = StructHelpers.zeroBytes( new byte[16] );
	    	StructHelpers.setInt( text.length(), hdrData, 4 );
	    	
	    	int txtrec = (int) file.seekToEnd();
	    	file.writeBytes( hdrData, txtrec, 16 );
	    	
	    	file.writeBytes(hdrData, txtrec + 16, text.length());
	    	file.close();
	    		    	
	    	return new Rec( txtrec );
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
