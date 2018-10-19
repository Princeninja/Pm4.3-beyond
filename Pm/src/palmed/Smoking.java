package palmed;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.HxObGyn.ObNotes;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;

public class Smoking implements LLItem {
	
	// struct Smoking {
	//
	//0		LLHDR	llhdr;		// linked list header
	//8		Date	date;		// date of this update
	//12	Rec		ptRec;		// patient record number
	//
	//16	byte	smokingStatus;	// CDC NHIS Smoking Status Recode - 2004 version SMKSTAT2
	//17	byte	smokingPacksDay;// packs per day smoked
	//18	byte	smokingAgeStarted;	// age started cigarette smoking
	//19	byte	unused;			// unused
	//20	Date	smokingQuitDate;// date quit
	//
	//24	byte	otherProduct;	// type of tobacco product
	//25	byte	otherStatus;	// user status
	//26	byte	otherAgeStarted;// age started using
	//27	byte	otherUnused;
	//28	Date	otherQuitDate;	// date quit using
	//
	//32	Reca	smokingNoteReca;// smoking note reca
	//36	byte	flgAttemptingToQuit;	// flag attempting to quit smoking/tobacco
	//37	byte	flgMultipleAttempts;	// flag multiple previous attempts to quit smoking
	//38	Reca	otherNoteReca;	// other note reca
	//
	//42	byte	unused[6];
	//}
	//sizecheck( 48 )
	

	
	public enum Product {
		
		CIGARETTES( "Cigarettes", 'C' ),
		CIGARS( "Cigars", 'G' ),
		CHEWING( "Chewing", 'W' ),
		PIPE( "Pipe", 'P' ),
		
		OTHER( "Other", 'O' ),
		EMPTY( "Unspecified", 'U' );
		
		private static final Map<Character, Product> lookup = new HashMap<Character,Product>();
		
		static {
			for ( Product r : EnumSet.allOf(Product.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		Product( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return this.code; }
		public static Product get( char code ){ return lookup.get( code ); }
	}
	
	
	public enum Recode {
		
		EVERY_DAY( "Current every day smoker", '1' ),
		SOME_DAY( "Current some day smoker", '2' ),
		FORMER( "Former Smoker", '3' ),
		NEVER( "Never Smoked", '4' ),
		SMOKER( "Smoker, current status unknown", '5' ),
		UNKNOWN( "Unknown if ever smoked", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, Recode> lookup = new HashMap<Character,Recode>();
		
		static {
			for ( Recode r : EnumSet.allOf(Recode.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		Recode( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static Recode get( char code ){ return lookup.get( code ); }
	}
	
	
	
	public enum UserStatus {		// Basically the same as Recode
		
		EVERY_DAY( "Current every day user", '1' ),
		SOME_DAY( "Current some day user", '2' ),
		FORMER( "Former User", '3' ),
		NEVER( "Never Used", '4' ),
		SMOKER( "User, current status unknown", '5' ),
		UNKNOWN( "Unknown if ever used", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, UserStatus> lookup = new HashMap<Character,UserStatus>();
		
		static {
			for ( UserStatus r : EnumSet.allOf(UserStatus.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		UserStatus( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return this.code; }
		public static UserStatus get( char code ){ return lookup.get( code ); }
	}
	
	
	
	public enum PacksDay {		// Packs per day smoked
		
		PACKS_NONE( "", '0', 0 ),
		PACKS_HALF( "1/2", '1', 0.5 ),
		PACKS_1( "1", '2', 1 ),
		PACKS_1_5( "1.5", '3', 1.5 ),
		PACKS_2( "2", '4', 2 ),
		PACKS_2_5( "2.5", '5', 2.5 ),
		PACKS_3( "3", '6', 3 ),
		PACKS_MORE( "3+", '7', 3.5 );
		
		private static final Map<Character, PacksDay> lookup = new HashMap<Character,PacksDay>();
		
		static {
			for ( PacksDay r : EnumSet.allOf(PacksDay.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		double multiplier;
		
		PacksDay( String label, char code, double multiplier ){
			this.label = label;
			this.code = code;
			this.multiplier = multiplier;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return this.code; }
		public double getMultiplier(){ return this.multiplier; }
		public static PacksDay get( char code ){ return lookup.get( code ); }
	}
	
	
	
	
	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="smoke%02d.med";
	public static final int recordLength = 48;
	
    private String smokingNoteText = null;		// additional note text
    private boolean flgSmokingNoteTxt = false;	// flag that note text has been set
    private boolean flgSmokingNoteTxtRead = false;	// flag that note text has been read
    
    private String otherNoteText = null;		// additional note text
    private boolean flgOtherNoteTxt = false;	// flag that note text has been set
    private boolean flgOtherNoteTxtRead = false;	// flag that note text has been read
    
    
    private String fnsubTxt = "smoket%02d.med";

    
    class SmokingNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setSmokingNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getSmokingNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    SmokingNotes smokingNotes = null;

    class OtherNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setOtherNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getOtherNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    OtherNotes otherNotes = null;

    
    
    
    
    // Constructors
    
    public Smoking() {
    	allocateBuffer();
    	smokingNotes = new SmokingNotes();
    	otherNotes = new OtherNotes();
    }
	
    public Smoking( Reca reca ){

        // allocate space
    	allocateBuffer();
    	smokingNotes = new SmokingNotes();
    	otherNotes = new OtherNotes();
    	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read record
    
    public void read( Reca reca ){
    	
    	smokingNotes.resetReadStatus();				// haven't read note text record yet
    	otherNotes.resetReadStatus();				// haven't read note text record yet
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// handle note text
    	smokingNotes.write();
    	otherNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Smoking.writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Smoking.write() bad reca" );

    	// handle addlSig text
    	smokingNotes.write();
    	otherNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getSmokingReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setSmokingReca(reca); }
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


    
    
    

	//16	byte	smokingStatus;	// CDC NHIS Smoking Status Recode - 2004 version SMKSTAT2
    
    /**
     * getSmokingStatus()
     * 
     * Get Smoking Status from dataStruct.
     * 
     * @param none
     * @return String
     */
    public Recode getSmokingStatus(){
    	return Recode.get( (char) dataStruct[16] );
    }

    /**
     * setSmokingStatus()
     * 
     * Set SmokingStatus in dataStruct.
     * 
     * @param String		description
     * @return void
     */
    public void setSmokingStatus( Recode status ){
    	dataStruct[16] = (status != null) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }


    
	//17	byte	smokingPacksDay;// packs per day smoked
    
    /**
     * getSmokingPacksDay()
     * 
     * Get Smoking Packs per Day from dataStruct.
     * 
     * @param none
     * @return PacksDay
     */
    public PacksDay getSmokingPacksDay(){
    	return PacksDay.get( (char) dataStruct[17] );
    }

    /**
     * setSmokingPacksDay()
     * 
     * Set Smoking Packs per Day in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setSmokingPacksDay( PacksDay packs ){
    	dataStruct[17] = (packs != null) ? (byte)(packs.getCode() & 0xff): (byte) ' ';
    }
    
    
    
    


	//18	byte	smokingAgeStarted;	// age started cigarette smoking

    /**
     * getSmokingAgeStarted()
     * 
     * Get Smoking AgeStarted from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getSmokingAgeStarted(){
    	return (int)( dataStruct[18] & 0xff );
    }

    /**
     * setSmokingAgeStarted()
     * 
     * Set Smoking AgeStarted in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setSmokingAgeStarted( int num ){
    	dataStruct[18] = (byte)( 0xff & num );
    }
    
    
    
    
    
//19	byte	unused;			// unused
    
    
    
    
    
	//20	Date	smokingQuitDate;// date quit
    
   /**
     * getSmokingQuitDate()
     * 
     * Get Smoking Quit date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getSmokingQuitDate(){
       	return StructHelpers.getDate( dataStruct, 20 );
    }

    /**
     * setSmokingQuitDate()
     * 
     * Set Smoking Quit date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setSmokingQuitDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 20 );
        return;
    }


    
    


	//24	byte	otherType;		// type of tobacco product
    
    /**
     * getOtherProduct()
     * 
     * Get OtherProduct from dataStruct.
     * 
     * @param none
     * @return String
     */
    public Product getOtherProduct(){
    	return Product.get( (char) dataStruct[24] );
    }

    /**
     * setOtherProduct()
     * 
     * Set OtherProduct in dataStruct.
     * 
     * @param Product
     * @return void
     */
    public void setOtherProduct( Product product ){
    	dataStruct[24] = (product != null) ? (byte) (product.getCode() & 0xff): (byte) ' ';
    	return ;
    }

    
    
    
	//25	byte	otherStatus;	// user status

    /**
     * getOtherStatus()
     * 
     * Get Other Status from dataStruct.
     * 
     * @param none
     * @return String
     */
    public UserStatus getOtherStatus(){
    	return UserStatus.get( (char) dataStruct[25] );
    }

    /**
     * setOtherStatus()
     * 
     * Set Other user Status in dataStruct.
     * 
     * @param String		description
     * @return void
     */
    public void setOtherStatus( UserStatus status ){
    	dataStruct[25] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }
    
    


    
    //26	byte	otherAgeStarted;// age started using

    /**
     * getOtherAgeStarted()
     * 
     * Get Other Age Started from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getOtherAgeStarted(){
    	return (int)( dataStruct[26] & 0xff );
    }

    /**
     * setOtherAgeStarted()
     * 
     * Set Other AgeStarted in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setOtherAgeStarted( int num ){
    	dataStruct[26] = (byte)( 0xff & num );
    }
    
    
    
        
    
    
    
	//28	Date	otherQuitDate;	// date quit using
   
   /**
     * getOtherQuitDate()
     * 
     * Get Other Quit date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getOtherQuitDate(){
       	return StructHelpers.getDate( dataStruct, 28 );
    }

    /**
     * setOtherQuitDate()
     * 
     * Set Other Quit date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setOtherQuitDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 28 );
        return;
    }


    

    
    
	//32	Reca	smokingNoteReca;	// note
    
    /**
     * getSmokingNoteReca()
     * 
     * Get smoking note Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getSmokingNoteReca(){
    	return Reca.fromReca( dataStruct, 32 );
    }

    /**
     * setSmokingNoteReca()
     * 
     * Set smoking note Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setSmokingNoteReca( Reca reca ){
    	if ( reca == null ) reca = new Reca();
    	reca.toReca( dataStruct, 32 );
    }
    
    
    
    
    

	//38	Reca	otherNoteReca;	// note
    
    /**
     * getOtherNoteReca()
     * 
     * Get other note Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getOtherNoteReca(){
    	return Reca.fromReca( dataStruct, 38 );
    }

    /**
     * setOtherNoteReca()
     * 
     * Set other note Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setOtherNoteReca( Reca reca ){
    	if ( reca == null ) reca = new Reca();
    	reca.toReca( dataStruct, 38 );
    }
    
    
    
    
    

    
    
    

    
    
	//36	byte	flgAttemptingToQuit;	// flag attempting to quit smoking/tobacco
    
    /**
     * getFlgAttemptingToQuit()
     * 
     * Get FlgAttemptingToQuit from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgAttemptingToQuit(){
    	return ( dataStruct[36] == 'Y' );
    }

    /**
     * setFlgAttemptingToQuit()
     * 
     * Set FlgAttemptingToQuit in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgAttemptingToQuit( boolean flg ){
    	dataStruct[36] = (byte) (flg ? 'Y': 'N');
    }

    
    
    
	//37	byte	flgMultipleAttempts;	// flag multiple previous attempts to quit smoking
    
    /**
     * getFlgMultipleAttempts()
     * 
     * Get FlgMultipleAttempts flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMultipleAttempts(){
    	return ( dataStruct[37] == 'Y' );
    }

    /**
     * setFlgMultipleAttempts()
     * 
     * Set FlgMultipleAttempts in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgMultipleAttempts( boolean flg ){
    	dataStruct[37] = (byte) (flg ? 'Y': 'N');
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
    	return String.format( "%s", this.getSmokingStatus().getLabel());
    }


    
    
  
    
    
    
    
    
    
    /**
     * getSmokingNoteTxt()
     * 
     * Get smoking note text 
     * 
     * @param none
     * @return String
     */
    public String getSmokingNoteText(){ return smokingNotes.getNoteText(); }
    	
    /**
     * setSmokkingNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setSmokingNoteText( String txt ){ smokingNotes.setNoteText(txt); }
    	

    
    
    
    

    
    
    
    
    
    /**
     * getOtherNoteTxt()
     * 
     * Get other smoking note text 
     * 
     * @param none
     * @return String
     */
    public String getOtherNoteText(){ return otherNotes.getNoteText(); }

    /**
     * setOtherNoteTxt()
     * 
     * Set other note text 
     * 
     * @param String
     * @return void
     */
    public void setOtherNoteText( String txt ){ otherNotes.setNoteText(txt); }
    	


    
    
    
    

    
    
    
    
    
    
   
    
    
    
    

    

  
    
    
    
    public boolean isSmoker(){
    	char recode = getSmokingStatus().getCode();
    	return (( recode == '1' ) || ( recode == '2' ) || ( recode == '5' ));
    }
	
	
	
	
}
