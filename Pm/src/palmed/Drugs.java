package palmed;

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



public class Drugs implements LLItem {
	
	// struct Substance {
	//
	//0		LLHDR	llhdr;			// linked list header
	//8		Date	date;			// date of this update
	//12	Rec		ptRec;			// patient record number
	//
	//16	byte	drinkingStatus;		// DrinkingStatus enum code
	//17	byte	drinkingProduct;	// drinking product
	//18	byte	drinkingAgeStarted;	// age started drinking
	//19	byte	unused;				// unused
	//20	Date	drinkingQuitDate;	// date quit
	//24	Reca	drinkingNoteReca;	// note
	//
	//28	byte	drugsStatus;		// DrugStatus enum code
	//29	byte	drugsAgeStarted;	// age started using
	//30	byte	currentDrugs[12];	// array of current drug products used
	//42	byte	previousDrugs[12];	// array of previous drug products used
	//54	Date	drugsQuitDate;		// date quit using
	//58	Reca	drugsNoteReca;		// note
	//62	byte	flgHxIVDrugUse
	//
	//63	byte	unused[1];
	//}
	//sizecheck( 64 )
	

	public enum DrinkingStatus {
		
		NEVER( "Never used", '1' ),
		NO_SIG( "No significant use history", '2' ),
		FORMER( "Former User", '3' ),
		FORMER_HEAVY( "Former Heavy User", '4' ),
		CURRENT_RARE( "Current Rare User", '5' ),
		CURRENT_SOCIAL( "Current Social Drinker", '6' ),
		CURRENT_DAILY( "Current Everyday Drinker", '7' ),
		CURRENT_HEAVY( "Current Everyday Heavy User", '8' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, DrinkingStatus> lookup = new HashMap<Character,DrinkingStatus>();
		
		static {
			for ( DrinkingStatus r : EnumSet.allOf(DrinkingStatus.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		DrinkingStatus( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static DrinkingStatus get( char code ){ return lookup.get( code ); }
	}
	
	

	public enum DrugsOfAbuse {
		
		EMPTY( "None", ' ' ),
		MARIJUANA( "Marijuana", 'M' ),
		COCAINE( "Cocaine", 'C' ),
		HEROIN( "Heroin", 'H' ),
		METH( "Methamphetamine", 'A' ),
		LSD( "LSD", 'L' ),
		ECSTACY( "Ecstacy/MDMA", 'E' ),
		PCP( "PCP", 'P' ),
		OPIOIDS( "Narcotics/Opioids", 'N' ),
		BZD( "Benzodiazepines", 'B' ),
		INHALE( "Inhalants", 'I' ),
		BATH( "Bath Salts", 'S' ),
		OTHER( "Other", 'O' ),
		UNSPECIFIED( "Unspecified", 'U' );
		
		private static final Map<Character, DrugsOfAbuse> lookup = new HashMap<Character,DrugsOfAbuse>();
		
		static {
			for ( DrugsOfAbuse r : EnumSet.allOf(DrugsOfAbuse.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		DrugsOfAbuse( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return this.code; }
		public static DrugsOfAbuse get( char code ){ return lookup.get( code ); }
	}
	
	
	public enum UserStatus {
		
		NEVER( "Never used", '1' ),
		NO_SIG( "No significant use history", '2' ),
		FORMER( "Former User", '3' ),
		FORMER_HEAVY( "Former Heavy User", '4' ),
		CURRENT_OCCASIONAL( "Current Occasional User", '5' ),
		CURRENT( "Current User", '6' ),
		CURRENT_HEAVY( "Current Heavy User", '7' ),
		UNKNOWN( "Unknown", '9' ),
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
		public char getCode(){ return (char) this.code; }
		public static UserStatus get( char code ){ return lookup.get( code ); }
	}
	
	
	
	
	
	
	
	
	
	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="drugs%02d.med";
	public static final int recordLength = 64;
	
  
    
    
    // Set up NoteTextHelper classes to handle free note text
 
    private String fnsubTxt = "drugst%02d.med";

    class DrinkingNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setDrinkingNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getDrinkingNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    DrinkingNotes drinkingNotes = null;

    class DrugsNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubTxt; }
    	@Override
    	public void setReca( Reca reca ){ setDrugsNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getDrugsNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    DrugsNotes drugsNotes = null;
   

    
    
    
    
    
    // Constructors
    
    public Drugs() {
    	allocateBuffer();
    	drinkingNotes = new DrinkingNotes();
    	drugsNotes = new DrugsNotes();
    }
	
    public Drugs( Reca reca ){

        // allocate space
    	allocateBuffer();
    	drinkingNotes = new DrinkingNotes();
    	drugsNotes = new DrugsNotes();
    	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read record
    
    public void read( Reca reca ){
    	
		if ( ! Reca.isValid( reca )) return;		

		drinkingNotes.resetReadStatus();				// haven't read note text record yet
    	drugsNotes.resetReadStatus();					// haven't read note text record yet

		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// handle note text
    	drinkingNotes.write();
    	drugsNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

    	// handle addlSig text
    	drinkingNotes.write();
    	drugsNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getDrugsReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setDrugsReca(reca); }
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


    
    
    

	//16	byte	drinkingStatus;		// current drinking status
    
    /**
     * getDrinkingStatus()
     * 
     * Get Drinking Status from dataStruct.
     * 
     * @param none
     * @return DrinkingStatus
     */
    public DrinkingStatus getDrinkingStatus(){
    	return DrinkingStatus.get( (char) dataStruct[16] );
    }

    /**
     * setDrinkingStatus()
     * 
     * Set DrinkingStatus in dataStruct.
     * 
     * @param DrinkingStatus
     * @return void
     */
    public void setDrinkingStatus( DrinkingStatus status ){
    	dataStruct[16] = (status != null) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }


    
	//17	byte	drinkingProduct;	// type of alcohol consumed
    
    /**
     * getSmokingPacksDay()
     * 
     * Get DrinkingProduct from dataStruct.
     * 
     * @param none
     * @return DrinkingProduct
     */
//    public DrinkingProduct getDrinkingProduct(){
//    	return DrinkingProduct.get( (char) dataStruct[17] );
 //   }

    /**
     * setDrinkingProduct()
     * 
     * Set DrinkingProduct in dataStruct.
     * 
     * @param DrinkingProduct
     * @return void
     */
//    public void setDrinkingProduct( DrinkingProduct packs ){
//    	dataStruct[17] = (packs != null) ? (byte)(packs.getCode() & 0xff): (byte) ' ';
//    }
    
    
    
    


	//18	byte	drinkingAgeStarted;	// age started drinking

    /**
     * getDrinkingAgeStarted()
     * 
     * Get Drinking AgeStarted from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getDrinkingAgeStarted(){
    	return (int)( dataStruct[18] & 0xff );
    }

    /**
     * setDrinkingAgeStarted()
     * 
     * Set Drinking AgeStarted in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setDrinkingAgeStarted( int num ){
    	dataStruct[18] = (byte)( 0xff & num );
    }
    
    
    
    
    
//19	byte	unused;			// unused
    
    
    
    
    
	//20	Date	drinkingQuitDate;// date quit
    
   /**
     * getDrinkingQuitDate()
     * 
     * Get Drinking Quit date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDrinkingQuitDate(){
       	return StructHelpers.getDate( dataStruct, 20 );
    }

    /**
     * setDrinkingQuitDate()
     * 
     * Set Drinking Quit date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setDrinkingQuitDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 20 );
        return;
    }


    
    

	//24	Reca	drinkingNoteReca;	// note
    
    /**
     * getDrinkingNoteReca()
     * 
     * Get note Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getDrinkingNoteReca(){
    	return Reca.fromReca( dataStruct, 24 );
    }

    /**
     * setDrinkingNoteReca()
     * 
     * Set note Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setDrinkingNoteReca( Reca reca ){
    	reca.toReca( dataStruct, 24 );
    }
    
    
    
    
    

    
    
    
	//28	byte	drugStatus;		// drugs of abuse user status

    /**
     * getDrugsStatus()
     * 
     * Get Drugs Status from dataStruct.
     * 
     * @param none
     * @return UserStatus
     */
    public UserStatus getDrugsStatus(){
    	return UserStatus.get( (char) dataStruct[28] );
    }

    /**
     * setDrugsStatus()
     * 
     * Set Drugs user Status in dataStruct.
     * 
     * @param UserStatus
     * @return void
     */
    public void setDrugsStatus( UserStatus status ){
    	dataStruct[28] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
    	return ;
    }
    
    


	//30	char	currentDrugs[8]	// list of current drugs using
    
    /**
      * getCurrentDrugsList()
      * 
      * Get current Drugs list from dataStruct.
      * 
      * @param none
      * @return String
      */
     public String getCurrentDrugsList(){
        	return StructHelpers.getString( dataStruct, 30, 12 ).trim();
     }

     /**
      * setCurrentDrugsList()
      * 
      * Set current Drugs list in dataStruct.
      * 
      * @param String
      * @return void
      */
     public void setCurrentDrugsList( String str ){
         StructHelpers.setStringPadded( str, dataStruct, 30, 12 );
         return;
     }


     

     
     
   
 	//42	char	pastDrugs[8]	// list of past drugs using
     
     /**
       * getPastDrugsList()
       * 
       * Get past Drugs list from dataStruct.
       * 
       * @param none
       * @return String
       */
      public String getPastDrugsList(){
         	return StructHelpers.getString( dataStruct, 42, 12 ).trim();
      }

      /**
       * setPastDrugsList()
       * 
       * Set past Drugs list in dataStruct.
       * 
       * @param String
       * @return void
       */
      public void setPastDrugsList( String str ){
          StructHelpers.setStringPadded( str, dataStruct, 42, 12 );
          return;
      }


      

      
      
    
    //29	byte	DrugsAgeStarted;// Drugs age started using

    /**
     * getDrugsAgeStarted()
     * 
     * Get Other Age Started from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getDrugsAgeStarted(){
    	return (int)( dataStruct[29] & 0xff );
    }

    /**
     * setDrugsAgeStarted()
     * 
     * Set Drugs AgeStarted in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setDrugsAgeStarted( int num ){
    	dataStruct[29] = (byte)( 0xff & num );
    }
    
    
    
        
    
    
    
	//54	Date	DrugsQuitDate;	// date quit using
   
   /**
     * getDrugsQuitDate()
     * 
     * Get Drugs Quit date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDrugsQuitDate(){
       	return StructHelpers.getDate( dataStruct, 54 );
    }

    /**
     * setDrugsQuitDate()
     * 
     * Set Drugs Quit date in dataStruct.
     * 
     * @param usrlib.Date		date
     * @return void
     */
    public void setDrugsQuitDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 54 );
        return;
    }


    

    
    
	//58	Reca	drugsNoteReca;	// note
    
    /**
     * getDrugsNoteReca()
     * 
     * Get drugs note Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getDrugsNoteReca(){
    	return Reca.fromReca( dataStruct, 58 );
    }

    /**
     * setDrugsNoteReca()
     * 
     * Set drugs note Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setDrugsNoteReca( Reca reca ){
    	reca.toReca( dataStruct, 58 );
    }
    
    
    
    
    

    
    
    
	//62	byte	flgHxIVDrugUse;	// flag history of IV drug use
    
    /**
     * getFlgHxIVDrugUse()
     * 
     * Get FlgHxIVDrugUse from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgHxIVDrugUse(){
    	return ( dataStruct[62] == 'Y' );
    }

    /**
     * setFlgHxIVDrugUse()
     * 
     * Set FlgHxIVDrugUse in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgHxIVDrugUse( boolean flg ){
    	dataStruct[62] = (byte) (flg ? 'Y': 'N');
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
    	return String.format( "%s", this.getDrinkingStatus().getLabel());
    }


    
    
    
    
    
    
    
    
    
    
    
    /**
     * getDrinkingNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getDrinkingNoteText(){ return drinkingNotes.getNoteText(); }
    	
    /**
     * setDrinkingNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setDrinkingNoteText( String txt ){ drinkingNotes.setNoteText(txt); }
    	


    
    
    
    
    /**
     * getDrugsNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getDrugsNoteText(){ return drugsNotes.getNoteText(); }

    /**
     * setDrugsNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
    public void setDrugsNoteText( String txt ){ drugsNotes.setNoteText(txt); }
    	

    	
	
	
}
