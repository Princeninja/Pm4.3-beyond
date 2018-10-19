package palmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;

//struct VaccineMVX {
//
// 0	char	mvx[3];			// three char MVX code (digits with trailing space)
// 3	byte	status;			// status flag - A-active, I-inactive, N-never active
// 4	Rec		noteRec;		// note record number
// 8	Date	updateDate;		// last update date
// 12	char	unused[4];
// 16	char	abbr[10];		// abbreviation
// 26	char	mfrName[60];	// manufacturer name
// 86	char	unused[41];
// 127	byte	valid;			// validity byte
//
// }
// sizecheck( 128 );
//
// Note this table basically comes from the CDC
// http://www2a.cdc.gov/nip/IIS/IISStandards/vaccines.asp?rpt=mvx


public class VaccineMVX {

	// fields
    private byte[] dataStruct;
    private Rec rec = null;					// this Rec
    
    private final static String fn_mvxTbl = "vacmvx.ovd";
    private final static int recLength = 128;	// record length
    
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read

    
    enum Status {
    	ACTIVE, INACTIVE, NEVER_ACTIVE
    }
    
    
    
    
    
	public VaccineMVX(){
		
    	allocateBuffer();
	}

	
	
    public VaccineMVX( Rec rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read probTable record
    	read( rec );
    	    	
    	// set local copy of rec
    	this.rec = rec;
    }


	// read record
    
    public void read( Rec rec ){
    			
    	if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "VaccineMVX.read() bad rec" ); return; }
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fn_mvxTbl, getRecordLength());
		this.rec = rec;
    }	
    

    
    
    
    public Rec writeNew(){
     	
    	// handle note text
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
    	
    	Rec rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_mvxTbl, getRecordLength());
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "VaccineMVX.writeNew() bad rec" );
    	this.rec = rec;
    	return rec;
    }
    
    
    public boolean write(){
    	return write( this.rec );
    }
    
    public boolean write( Rec rec ){
    	
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "VaccineMVX.write() bad rec" );
    	
    	if ( flgNoteTxt ){
    		Rec noteRec = this.writeNoteText( noteText );
    		this.setNoteRec( noteRec );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_mvxTbl, getRecordLength());
        this.rec = rec;
        return true;
    }



    
    
    
    



	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[getRecordLength()];
    }


   
    
    
    
    
    /**
     * getRec()
     * 
     * Get this entry's record number
     * 
     * @return Rec
     */
    public Rec getRec(){
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we iterate this table
    	return new Rec( this.rec.getRec());
    }
    
    
    
    
// 16	char	abbr[10];		// abbreviation
    
    /**
     * getAbbr()
     * 
     * Get abbreviation from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getAbbr(){
    	return StructHelpers.getString( dataStruct, 16, 10 ).trim();
    }

    /**
     * setAbbr()
     * 
     * Set abbreviation in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setAbbr( String s ){
    	StructHelpers.setStringPadded( s.toUpperCase(), dataStruct, 16, 10 );
    	return ;
    }



    
    
// 26	char	mfrName[60];		// manufacturer name
    
    /**
     * getMfrName()
     * 
     * Get manufacturer name from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getMfrName(){
    	return StructHelpers.getString( dataStruct, 26, 60 ).trim();
    }

    /**
     * setMfrName()
     * 
     * Set manufacturer name in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setMfrName( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 26, 60 );
    	return ;
    }

    /**
     * getDesc()
     * 
     * Static method to get a VaccineMVX description given a VaccineMVX record number
     * @param rec
     * @return String
     */
    public static String getMfrName( Rec rec ){    	
		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "VaccineMVX.getMfrName( rec ) bad rec" ); return null; }
		VaccineMVX p = new VaccineMVX( rec );
		return p.getMfrName();
    }

    public String getDesc() { return getMfrName(); }
    public static String getDesc( Rec rec ){ return getMfrName( rec ); }
    
    
    
    
 
    

    
// 0	char	mvx[3];		// vaccine MVX code *UNIQUE* in this file

    /**
     * getMVX()
     * 
     * Get MVX code from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getMVX(){
    	return StructHelpers.getString( dataStruct, 0, 3 ).trim();
    }

    /**
     * setMVX()
     * 
     * Set MVX code in dataStruct.
     * 
     * @param s
     * @return void
     */
    public void setMVX( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 0, 3 );
    	return ;
    }

    
    
//	char	snomed[20];		// SNOMED code, offset 50

    /**
     * getSNOMED()
     * 
     * Get SNOMED code from dataStruct.
     * 
     * @param none
     * @return String
     */
//    public String getSNOMED(){
//    	return StructHelpers.getString( dataStruct, 50, 20 ).trim();
//    }

    /**
     * setSNOMED()
     * 
     * Set SNOMED code in dataStruct.
     * 
     * @param s
     * @return void
     */
//    public void setSNOMED( String s ){
//    	StructHelpers.setStringPadded( s, dataStruct, 50, 20 );
//    	return ;
//    }

    
    
    
    
   
    


    
    
// 8	Date	updateDate;		// last update date, offset 8
    
    /**
     * getUpdateDate()
     * 
     * Get last update date from dataStruct.
     * 
     * @return Date
     */
    public Date getUpdateDate(){
    	return StructHelpers.getDate( dataStruct, 8 );
    }

    /**
     * setUpdateDate()
     * 
     * Set last update date in dataStruct.
     * 
     * @param date
     * @return void
     */
    public void setUpdateDate( Date date ){
    	StructHelpers.setDate( date, dataStruct, 8 );
    	return ;
    }


    
    
    
   
// 4	Rec	noteRec;		// note record number, offset 4
    
    /**
     * getNoteRec()
     * 
     * Get note rec from dataStruct.
     * 
     * @return Rec
     */
    public Rec getNoteRec(){
    	return Rec.fromInt( dataStruct, 4 );
    }

    /**
     * setNoteRec()
     * 
     * Set note rec in dataStruct.
     * 
     * @param rec
     * @return void
     */
    public void setNoteRec( Rec rec ){
    	rec.toInt( dataStruct, 4 );
    	return ;
    }


    
    

    
    

   
	// 3	byte status;			// status byte
    
    /**
     * getStatus()
     * 
     * Get status byte from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	if ( dataStruct[3] == 'A' ) return Status.ACTIVE;
    	if ( dataStruct[3] == 'I' ) return Status.INACTIVE;
    	if ( dataStruct[3] == 'N' ) return Status.NEVER_ACTIVE;
    	return Status.INACTIVE;
    }

    /**
     * setStatus()
     * 
     * Set status byte in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Status status ){
    	dataStruct[3] = 'I';
    	if ( status == Status.ACTIVE ) dataStruct[3] = 'A';
    	if ( status == Status.INACTIVE ) dataStruct[3] = 'I';
    	if ( status == Status.NEVER_ACTIVE ) dataStruct[3] = 'N';
    	return;
    }


 
 

	// 127	byte valid;			// validity byte
    
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return Validity
     */
    public usrlib.Validity getValid(){
    	usrlib.Validity valid = usrlib.Validity.get( dataStruct[127] );
    	return ( valid == null ) ? usrlib.Validity.INVALID: valid;
    }

    /**
     * setValid()
     * 
     * Set validity byte in dataStruct.
     * 
     * @param Validity
     * @return void
     */
    public void setValid( usrlib.Validity valid ){
    	dataStruct[127] = (byte) valid.getCode();
    	return;
    }


 
 




    public void dump(){
    	StructHelpers.dump( "VaccineMVX", dataStruct, getRecordLength());
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
    	return String.format( "%s %s %s", this.getAbbr(), this.getMVX(), this.getMfrName());
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
    		
    		Rec rec = this.getNoteRec();
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


    
    
    private String fnsubTxt = "vacmvxt.ovd";
    private int txtRecLength = 320;
    
    private String readNoteText( Rec rec ){
    	
    	// handle trivial case
    	if ( ! Rec.isValid( rec )) return "";
    	   	
    	// read entry from text file
    	byte[] dataStruct = new byte[txtRecLength];
		RandomFile.readRec( rec, dataStruct, Pm.getOvdPath(), fnsubTxt, txtRecLength );
		String s = StructHelpers.getString(dataStruct, 0, txtRecLength );
		return s.trim();
    }
    
    private Rec writeNoteText( String text ){
    	
     	// handle trivial case
    	if (( text == null ) || ( text.trim().length() < 1 )) return null;
    	
    	byte[] dataStruct = new byte[txtRecLength];
    	StructHelpers.setStringPadded( text, dataStruct, 0, txtRecLength );
    	Rec rec = RandomFile.writeRec( null, dataStruct, Pm.getOvdPath(), fnsubTxt, txtRecLength );
    	
    	return rec;
    }

 
    
    
    
    // TODO lookup mvx number and return name/description
    public static String getName( String mvx ){
    	return mvx;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    public class Entry {
    	String mvx;
    	String abbr;
    	String mfrName;
    	String note;
    	Status status;
    	Rec rec;
    	
    	Entry( String mvx, String abbr, String mfrName, Status status, String note, Rec rec ){
    		this.mvx = mvx;
    		this.abbr = abbr;
    		this.mfrName = mfrName;
    		this.status = status;
    		this.note = (( note != null ) && ( note.length() > 0 )) ? note: null;
    		this.rec = rec;
    	}
    	
    	public String getMVX(){ return this.mvx; }
    	public String getAbbr(){ return this.abbr; }
    	public String getMfrName(){ return this.mfrName; }
    	public String getNote(){ return this.note; }
    	public Status getStatus(){ return this.status; }
    	public Rec getRec(){ return this.rec; }
    }
    
    
    public static List<Entry> Search( String mvx, String abbr, String desc, Status status ){
    	
    	if ( mvx != null ) mvx = mvx.trim();
    	if ( abbr != null ) abbr = abbr.trim().toUpperCase();
    	if ( desc != null ) desc = desc.trim();
    	
    	List<Entry> list = new ArrayList<Entry>();
    	
    	VaccineMVX v = new VaccineMVX();
    	
    	// open file, read maxrec, and set current rec to null
		RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_mvxTbl, getRecordLength(), RandomFile.Mode.READONLY );
		int maxrec = file.getMaxRecord();
		
		for ( int rec = 2; rec <= maxrec; ++rec ){
	
			// reset some things
		    v.noteText = null;
		    v.flgNoteTxt = false;
		    v.flgNoteTxtRead = false;
		    
			file.getRecord( v.dataStruct, rec );
			
			if ( v.getValid() != usrlib.Validity.VALID ) continue;
			
			// if searching by status
			if (( status != null ) && ( status != v.getStatus())) continue;
			
			// if searching by MVX code, is this a match?
			if (( mvx != null ) && ( ! mvx.equals( v.getMVX()))) continue;
			
			// if searching by abbreviation, is this a match?
			if (( abbr != null ) && ( ! abbr.equals( v.getAbbr()))) continue;
			
			// if searching by description, is this desc contained in either the short or full description?
			if (( desc != null ) && ( v.getMfrName().toUpperCase().indexOf( desc ) < 0 )) continue;
			
			// add this item to the list
			list.add( v.new Entry( v.getMVX(), v.getAbbr(), v.getMfrName(), v.getStatus(), v.getNoteTxt(), new Rec( rec )));
		}
		
		return list;
    }
    
    
    
    public static boolean importFromFlatFile( String text ){
    	
    	
/*    	FileReader fr;
    	
		try {
			fr = new FileReader( new File( fname ));
		} catch (FileNotFoundException e) {
			DialogHelpers.Messagebox( "Error opening file: " + fname );
			return false;
		}
*/
    	
    	StringReader sr = new StringReader( text );
    	BufferedReader br = new BufferedReader( sr );
    	
    	String line = null;
    	

    	try {
			while (( line = br.readLine()) != null ){
				
				System.out.println( line );
				
				String[] tokens = line.split( "\\|" );
				if ( tokens.length != 5 ){
					System.out.println( "Incomplete line. num=" + tokens.length );
					continue;
				}
				
				String mvx = tokens[0].trim();

				Status status = Status.ACTIVE;
				if ( tokens[3].startsWith( "I" )) status = Status.INACTIVE;
				if ( tokens[3].startsWith( "N" )) status = Status.NEVER_ACTIVE;
				
				//tokens[5] ????
				
				Date updateDate = new Date( tokens[4] );
				
				VaccineMVX v = new VaccineMVX();
				v.setMVX( mvx );
				v.setAbbr( mvx );
				v.setMfrName( tokens[1] );
				if ( tokens[2].length() > 1 ) v.setNoteTxt( tokens[2] );
				v.setStatus( status );
				v.setValid( usrlib.Validity.VALID );
				v.setUpdateDate( updateDate );
				v.writeNew();
				
			}
			
		} catch (IOException e1) {
			DialogHelpers.Messagebox( "Error reading MVX file" );
			return false;
		}
    	

    	try {
			br.close();
	    	sr.close();
		} catch (IOException e) {
			// ignore
		}
    	
    	return true;
    }
}
