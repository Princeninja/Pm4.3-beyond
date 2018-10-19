package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.RecordStatus;

public class HxFamily implements LLItem {

	// struct HxFamily {
	//
	//0		byte	unused[8];		// unused (LItem)
	//8		Date	date;			// date of this update
	//12	Rec		ptRec;			// patient record number
	//
	//16	byte	fatherStatus;	// father status (alive, deceased, unknown)
	//17	byte	flgPaternalHxUnavailable;	// flag that paternal family history is unavailable
	//
	//18	byte	motherStatus;	// mother status (alive, deceased, unknown)
	//19	byte	flgMaternalHxUnavailable;	// flag that maternal family history is unavailable
	//
	//20	USRT	fatherBorn;		// year father born
	//22	USRT	fatherDeceased;	// year father deceased
	//24	Reca	fatherCauseTextReca;		// father cause of death text Reca
	//
	//28	USRT	motherBorn;		// year mother born
	//30	USRT	motherDeceased;	// year mother deceased
	//32	Reca	motherCauseTextReca;		// mother cause of death text Reca
	//
	//36	byte	brothers;		// number of brothers
	//37	byte	sisters;		// number of sisters
	//38	byte	sons;			// number of sons
	//39	byte	daughters;		// number of daughters
	//40	USRT	kidsOldestBirthYear;		// birth year of oldest child
	//42	USRT	kidsYoungestBirthYear;		// birth year of youngest child
	//
	//44	byte	unused[19];		// unused
	//63	byte	status;			// status (valid, removed, etc)
	//}
	//sizecheck( 64 )
	

public enum ParentStatus {
	
	LIVING( 	"Living",		'L' ),	
	DECEASED( 	"Deceased",		'D' ),		
	UNKNOWN( 	"Unknown",		'U' ),
	UNSPECIFIED("Unspecified",	' ' );

	private String label;
	private int code;

	private static final Map<Integer, ParentStatus> lookup = new HashMap<Integer,ParentStatus>();
	
	static {
		for ( ParentStatus r : EnumSet.allOf(ParentStatus.class))
			lookup.put(r.getCode(), r );
	}


	ParentStatus ( String label, int code ){
		this.label = label;
		this.code = code & 0xff;
	}
	
	public String getLabel(){ return this.label; }
	public int getCode(){ return this.code; }
	public static ParentStatus get( int code ){ return lookup.get( code & 0xff ); }

	public boolean isLiving() { return (this.code == 'L'); }
	public boolean isDeceased() { return (this.code == 'D'); }
}




	
	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="hxfam%02d.med";
	public static final int recordLength = 64;
	
    private String fnsubTxt = "hxfamt%02d.med";
  
    //private String fatherCauseText = null;		// additional note text
    //private boolean flgFatherCauseText = false;	// flag that note text has been set
    //private boolean flgFatherCauseTextRead = false;	// flag that note text has been read
  //
    //private String motherCauseText = null;		// additional note text
    //private boolean flgMotherCauseText = false;	// flag that note text has been set
    //private boolean flgMotherCauseTextRead = false;	// flag that note text has been read
  
 
    
    
    // set up NoteTextHelper classes
    
    class FatherCauseText extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setFatherCauseTextReca( reca ); }
    	@Override
    	public Reca getReca(){ return getFatherCauseTextReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    FatherCauseText fatherCauseText = null;


    class MotherCauseText extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setMotherCauseTextReca( reca ); }
    	@Override
    	public Reca getReca(){ return getMotherCauseTextReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    MotherCauseText motherCauseText = null;


    
    
    
    
    
    // Constructors
	
    public HxFamily() {
    	allocateBuffer();
    	fatherCauseText = new FatherCauseText();
    	motherCauseText = new MotherCauseText();
    }
	
    public HxFamily( Reca reca ){

        // allocate space
    	allocateBuffer();
    	fatherCauseText = new FatherCauseText();
    	motherCauseText = new MotherCauseText();
   	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read record
    
    public void read( Reca reca ){
    	
    	fatherCauseText.resetReadStatus();				// haven't read note text record yet
    	motherCauseText.resetReadStatus();				// haven't read note text record yet

		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// handle note text
    	fatherCauseText.write();
    	motherCauseText.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	// handle note text
    	fatherCauseText.write();
    	motherCauseText.write();    	
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getHxFamilyReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setHxFamilyReca(reca); }
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


    
    
    

	//16	byte	fatherStatus;		// father's ParentStatus (living/deceased) code
    
    /**
     * getFatherStatus()
     * 
     * Get FatherStatus from dataStruct.
     * 
     * @param none
     * @return ParentStatus
     */
    public ParentStatus getFatherStatus(){
    	return ParentStatus.get( (char) dataStruct[16] );
    }

    /**
     * setFatherStatus()
     * 
     * Set FatherStatus in dataStruct.
     * 
     * @param ParentStatus
     * @return void
     */
    public void setFatherStatus( ParentStatus status ){
    	if ( status == null ) status = ParentStatus.UNSPECIFIED;
    	dataStruct[16] = (byte) (status.getCode() & 0xff);
    	return ;
    }


    

    
    
	//17	byte	flgPaternalHxUnavailable;		// flg Paternal Hx Unavailable
    
    /**
     * getFlgPaternalHxUnavailable()
     * 
     * Get FlgPaternalHxUnavailable from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgPaternalHxUnavailable(){
    	return ( dataStruct[17] == 'Y'  );
    }

    /**
     * setFlgPaternalHxUnavailable()
     * 
     * Set FlgPaternalHxUnavailable in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgPaternalHxUnavailable( boolean status ){
    	dataStruct[17] = (byte) (( status ? 'Y': ' ' ) & 0xff);
    	return ;
    }


    

    
    

	//18	byte	motherStatus;		// mother's ParentStatus (living/deceased) code
    
    /**
     * getMotherStatus()
     * 
     * Get MotherStatus from dataStruct.
     * 
     * @param none
     * @return ParentStatus
     */
    public ParentStatus getMotherStatus(){
    	return ParentStatus.get( (char) dataStruct[18] );
    }

    /**
     * setMotherStatus()
     * 
     * Set MotherStatus in dataStruct.
     * 
     * @param ParentStatus
     * @return void
     */
    public void setMotherStatus( ParentStatus status ){
    	if ( status == null ) status = ParentStatus.UNSPECIFIED;
    	dataStruct[18] = (byte) (status.getCode() & 0xff);
    	return ;
    }


    

    
    

	//19	byte	flgMaternalHxUnavailable;		// flg Maternal Hx Unavailable
    
    /**
     * getFlgMaternalHxUnavailable()
     * 
     * Get FlgMaternalHxUnavailable from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMaternalHxUnavailable(){
    	return ( dataStruct[19] == 'Y'  );
    }

    /**
     * setFlgMaternalHxUnavailable()
     * 
     * Set FlgMaternalHxUnavailable in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgMaternalHxUnavailable( boolean status ){
    	dataStruct[19] = (byte) (( status ? 'Y': ' ' ) & 0xff);
    	return ;
    }


    

    
    

	//20	USRT	fatherBorn;	// birth year of father
    
    /**
     * getYearFatherBorn()
     * 
     * Get birth year of father from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getYearFatherBorn(){
    	return StructHelpers.getUShort( dataStruct, 20 );
    }

    /**
     * setYearFatherBorn()
     * 
     * Set birth year of father in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setYearFatherBorn( int num ){
    	StructHelpers.setUShort( num, dataStruct, 20 );
    }
    
    
    
    
    

    
  
    
	//22	USRT	fatherDeceased;	// year father deceased
    
    /**
     * getYearFatherDeceased()
     * 
     * Get year father deceased from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getYearFatherDeceased(){
    	return StructHelpers.getUShort( dataStruct, 22 );
    }

    /**
     * setYearFatherDeceased()
     * 
     * Set year father deceased in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setYearFatherDeceased( int num ){
    	StructHelpers.setUShort( num, dataStruct, 22 );
    }
    
    
    
    
    

    
  
    
	//28	USRT	motherBorn;	// birth year of mother
    
    /**
     * getYearMotherBorn()
     * 
     * Get birth year of mother from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getYearMotherBorn(){
    	return StructHelpers.getUShort( dataStruct, 28 );
    }

    /**
     * setYearMotherBorn()
     * 
     * Set birth year of mother in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setYearMotherBorn( int num ){
    	StructHelpers.setUShort( num, dataStruct, 28 );
    }
    
    
    
    
    

    
  
    
	//30	USRT	motherDeceased;	// year mother deceased
    
    /**
     * getYearMotherDeceased()
     * 
     * Get year mother deceased from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getYearMotherDeceased(){
    	return StructHelpers.getUShort( dataStruct, 30 );
    }

    /**
     * setYearMotherDeceased()
     * 
     * Set year mother deceased in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setYearMotherDeceased( int num ){
    	StructHelpers.setUShort( num, dataStruct, 30 );
    }
    
    
    
    
    

    
  
    
	//36	byte	brothers;	// number of brothers
    
    /**
     * getNumBrothers()
     * 
     * Get number of brothers from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumBrothers(){
    	return StructHelpers.getByte( dataStruct, 36 );
    }

    /**
     * setNumBrothers()
     * 
     * Set number of brothers in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setNumBrothers( int num ){
    	StructHelpers.setByte( num, dataStruct, 36 );
    }
    
    
    
    
    

    
    
	//37	byte	sisters;	// number of sisters
    
    /**
     * getNumSisters()
     * 
     * Get number of sisters from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumSisters(){
    	return StructHelpers.getByte( dataStruct, 37 );
    }

    /**
     * setNumSisters()
     * 
     * Set number of sisters in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setNumSisters( int num ){
    	StructHelpers.setByte( num, dataStruct, 37 );
    }
    
    
    
    
    

    
    
	//38	byte	sons;	// number of sons
    
    /**
     * getNumSons()
     * 
     * Get number of sons from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumSons(){
    	return StructHelpers.getByte( dataStruct, 38 );
    }

    /**
     * setNumSons()
     * 
     * Set number of sons in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setNumSons( int num ){
    	StructHelpers.setByte( num, dataStruct, 38 );
    }
    
    
    
    
    

    
    
	//39	byte	daughters;	// number of daughters
    
    /**
     * getNumDaughters()
     * 
     * Get number of daughters from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumDaughters(){
    	return StructHelpers.getByte( dataStruct, 39 );
    }

    /**
     * setNumDaughters()
     * 
     * Set number of daughters in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setNumDaughters( int num ){
    	StructHelpers.setByte( num, dataStruct, 39 );
    }
    
    
    
    
    

    
  
    
	//40	USRT	kidsOldestBirthYear;	// birth year of oldest child
    
    /**
     * getKidsOldestBirthYear()
     * 
     * Get birth year of oldest child from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getKidsOldestBirthYear(){
    	return StructHelpers.getUShort( dataStruct, 40 );
    }

    /**
     * setKidsOldestBirthYear()
     * 
     * Set birth year of oldest child in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setKidsOldestBirthYear( int num ){
    	StructHelpers.setUShort( num, dataStruct, 40 );
    }
    
    
    
    
    

    
  
	//42	USRT	kidsYoungestBirthYear;	// birth year of oldest child
    
    /**
     * getKidsYoungestBirthYear()
     * 
     * Get birth year of youngest child from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getKidsYoungestBirthYear(){
    	return StructHelpers.getUShort( dataStruct, 42 );
    }

    /**
     * setKidsYoungestBirthYear()
     * 
     * Set birth year of youngest child in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setKidsYoungestBirthYear( int num ){
    	StructHelpers.setUShort( num, dataStruct, 42 );
    }
    
    
    
    
    

    
  
    
	//24	Reca	fatherCauseTextReca;	// father cause of death text Reca
    
    /**
     * getFatherCauseTextReca()
     * 
     * Get father cause of death text Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getFatherCauseTextReca(){
    	return Reca.fromReca( dataStruct, 24 );
    }

    /**
     * setFatherCauseTextReca()
     * 
     * Set father cause of death text Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setFatherCauseTextReca( Reca reca ){
    	reca.toReca( dataStruct, 24 );
    }
    
    
    
    
    

    
	//32	Reca	motherCauseTextReca;	// mother cause of death text Reca
    
    /**
     * getMotherCauseTextReca()
     * 
     * Get mother cause of death text Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getMotherCauseTextReca(){
    	return Reca.fromReca( dataStruct, 32 );
    }

    /**
     * setMotherCauseTextReca()
     * 
     * Set mother cause of death text Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setMotherCauseTextReca( Reca reca ){
    	reca.toReca( dataStruct, 32 );
    }
    
    
    
    
    

    
    
    
    
    
    
    

    
    
    
	//31	byte	status;		// record status

    /**
     * getStatus()
     * 
     * Get Status from dataStruct.
     * 
     * @param none
     * @return usrlib.RecordStatus
     */
    public usrlib.RecordStatus getStatus(){
    	return usrlib.RecordStatus.get( (char) dataStruct[31] );
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
    	dataStruct[31] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
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
    	return ""; //String.format( "%s %s %s", this.getMember().getLabel(), "" );
    }


    
    
    
    
    
    
    
    
    
    
    
  
    
    
    
    
    
    
    /**
     * getFatherCauseText()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getFatherCauseText(){ return fatherCauseText.getNoteText(); }
    	
    /**
     * setFatherCauseText()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setFatherCauseText( String txt ){ fatherCauseText.setNoteText(txt); }
    
    	

    
    
    
    
    /**
     * getMotherCauseText()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getMotherCauseText(){ return motherCauseText.getNoteText(); }
    	
    /**
     * setMotherCauseText()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setMotherCauseText( String txt ){ motherCauseText.setNoteText(txt); }
    	


    
    

    

    	
	

    
    
    /**
     * Mark history item record removed
     * 
     * @param reca
     * @return
     */
    //public static boolean markRemoved( Reca reca ){	    	
    //	if ( ! Reca.isValid( reca )) { SystemHelpers.seriousError( "HxFamily.markRemoved() bad reca" ); return false; }
	//	HxFamilyItem fam = new HxFamilyItem( reca );
	//	fam.setStatus( HxFamilyItem.Status.REMOVED );
	//	fam.write( reca );
	//	return true;
    //}

    /**
     * Save new family history item record and link to patient.
     * 
     * @param ptRec
     * @param member
     * @param probTblRec
     * @param note
     * @return Reca
     */
    //public static Reca postNew( Rec ptRec, Member member, Rec probTblRec, String note ){	    	
	//	// save item
	//	HxFamilyItem fam = new HxFamilyItem();
	//	fam.setPtRec( ptRec );
	//	fam.setDate( Date.today());
	//	fam.setMember( member );
	//	fam.setProbTblRec( probTblRec );
	//	fam.setNoteText( note );
	//	fam.setStatus( HxFamilyItem.Status.CURRENT );
	//	Reca reca = fam.postNew( ptRec );
	//	return reca;
    //}
	

}

