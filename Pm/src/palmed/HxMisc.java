package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;

public class HxMisc implements LLItem {

	
	// struct HxMisc {
	//
	//0		LLHDR	llhdr;			// linked list header
	//8		Date	date;			// date of this update
	//12	Rec		ptRec;			// patient record number
	//
	//16	byte	flgBloodTypeKnown;// flag blood type known
	//17	byte	bloodType;			// blood type enum code
	//18	byte	rhesusFactor;		// rhesus factor
	//19	byte	flgTransfusionReaction;	// flg pt had transfusion reaction
	//20	byte	flgWillDonate;		// flg will donate blood
	//21	byte	flgDeclinesBlood;	// flg declines blood products or transfusion
	//22	byte	unused[2];			// unused
	//24	Reca	bloodNoteReca;		// note
	//
	//
	//28	byte	unused[35];			// unused
	//
	//63	byte	status;				// record status
	//}
	//sizecheck( 64 )
	

	public enum BloodType {
		
		O( "O", '1' ),
		A( "A", '2' ),
		B( "B", '3' ),
		AB( "AB", '4' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, BloodType> lookup = new HashMap<Character,BloodType>();
		
		static {
			for ( BloodType r : EnumSet.allOf(BloodType.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		BloodType( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static BloodType get( char code ){ return lookup.get( code ); }
	}



		
		
	public enum RhesusFactor {
		
		POSITIVE( "+", '+' ),
		NEGATIVE( "-", '-' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, RhesusFactor> lookup = new HashMap<Character,RhesusFactor>();
		
		static {
			for ( RhesusFactor r : EnumSet.allOf(RhesusFactor.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		RhesusFactor( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static RhesusFactor get( char code ){ return lookup.get( code ); }
	}



		
		
	public enum FlagStatus {
		
		TRUE( "True", 'T' ),
		FALSE( "False", 'F' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, FlagStatus> lookup = new HashMap<Character,FlagStatus>();
		
		static {
			for ( FlagStatus r : EnumSet.allOf(FlagStatus.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		FlagStatus( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static FlagStatus get( char code ){ return lookup.get( code ); }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
	public static final String fnsub ="hxmisc%02d.med";
	public static final String fnsubt ="hxmisct%02d.med";
	public static final int recordLength = 64;
	
    
    class BloodNotes extends NoteTextHelper {
    	@Override
	    public String getFnsub(){ return fnsubt; }
    	@Override
    	public void setReca( Reca reca ){ setBloodNoteReca( reca ); }
    	@Override
    	public Reca getReca(){ return getBloodNoteReca(); }
    	@Override
    	public Rec getRefRec(){ return getPtRec(); }
    }
    
    BloodNotes bloodNotes = null;

    
    
    
    
    
    
    
    
    
    
	
    public HxMisc() {
    	allocateBuffer();
    	this.bloodNotes = new BloodNotes();
    }
	
    public HxMisc( Reca reca ){

        // allocate space
    	allocateBuffer();
    	this.bloodNotes = new BloodNotes();
   	
    	// read record
    	if ( Reca.isValid( reca )){
    		this.read( reca );
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    	
    	
    }


	// read record
    
    public void read( Reca reca ){
    	
    	bloodNotes.resetReadStatus();

		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
   }	


    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	
    	int vol = Reca.todayVol();
    	
    	// write free text notes records
    	bloodNotes.write();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

    	
    	// write free text notes records
    	bloodNotes.write();
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getHxMiscReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setHxMiscReca(reca); }
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


    
    
    

	//16	byte	flgBloodTypeKnown;	// flag blood type known

    /**
     * getFlgBloodTypeKnown()
     * 
     * Get FlgBloodTypeKnown from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgBloodTypeKnown(){
    	return (( dataStruct[16] & 0xff ) == 'Y' );
    }

    /**
     * setFlgBloodTypeKnown()
     * 
     * Set FlgBloodTypeKnown in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgBloodTypeKnown( boolean flg ){
    	dataStruct[16] = (byte)( 0xff & ( flg ? 'Y': 'N' ));
    }
    
    
    
    
    

	//17	byte	bloodType;	// blood type enum code

    /**
     * getBloodType()
     * 
     * Get BloodType from dataStruct.
     * 
     * @param none
     * @return BloodType
     */
    public BloodType getBloodType(){
    	return BloodType.get( (char) dataStruct[17] );
    }

    /**
     * setBloodType()
     * 
     * Set BloodType in dataStruct.
     * 
     * @param BloodType
     * @return void
     */
    public void setBloodType( BloodType type ){
    	dataStruct[17] = (byte)( 0xff & type.getCode() );
    }
    
    
    
    
    

	//18	byte	rhesusFactor;	// rhesus factor code

    /**
     * getRhesusFactor()
     * 
     * Get RhesusFactor from dataStruct.
     * 
     * @param none
     * @return RhesisFactor
     */
    public RhesusFactor getRhesusFactor(){
    	return RhesusFactor.get( (char) dataStruct[18] );
    }

    /**
     * setRhesusFactor()
     * 
     * Set RhesusFactor in dataStruct.
     * 
     * @param RhesusFactor
     * @return void
     */
    public void setRhesusFactor( RhesusFactor type ){
    	dataStruct[18] = (byte)( 0xff & type.getCode() );
    }
    
    
    
    
    

	//19	byte	flgTransfusionReaction;	// flag pt had transfusion reaction

    /**
     * getFlgTransfusionReaction()
     * 
     * Get FlgTransfusionReaction from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgTransfusionReaction(){
    	return (( dataStruct[19] & 0xff ) == 'Y' );
    }

    /**
     * setFlgTransfusionReaction()
     * 
     * Set FlgTransfusionReaction in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgTransfusionReaction( boolean flg ){
    	dataStruct[19] = (byte)( 0xff & ( flg ? 'Y': 'N' ));
    }
    
    
    
    
    

	//20	byte	flgWillDonateBlood;	// flag patient will donate blood

    /**
     * getFlgWillDonateBlood()
     * 
     * Get FlgWillDonateBlood from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgWillDonateBlood(){
    	return (( dataStruct[20] & 0xff ) == 'Y' );
    }

    /**
     * setFlgWillDonateBlood()
     * 
     * Set FlgWillDonateBlood in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgWillDonateBlood( boolean flg ){
    	dataStruct[20] = (byte)( 0xff & ( flg ? 'Y': 'N' ));
    }
    
    
    
    
    

	//21	byte	flgDeclinesBlood;	// flag pt declines blood products or transfusion

    /**
     * getFlgDeclinesBlood()
     * 
     * Get FlgDeclinesBlood from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgDeclinesBlood(){
    	return (( dataStruct[21] & 0xff ) == 'Y' );
    }

    /**
     * setFlgDeclinesBlood()
     * 
     * Set FlgDeclinesBlood in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgDeclinesBlood( boolean flg ){
    	dataStruct[21] = (byte)( 0xff & ( flg ? 'Y': 'N' ));
    }
    
    
    
    
    
    
	//24	Reca	bloodNoteReca;	// note
    
    /**
     * getBloodNoteReca()
     * 
     * Get note Reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getBloodNoteReca(){
    	return Reca.fromReca( dataStruct, 24 );
    }

    /**
     * setBloodNoteReca()
     * 
     * Set note Reca record in dataStruct.
     * 
     * @param Reca
     * @return void
     */
    public void setBloodNoteReca( Reca reca ){
    	reca.toReca( dataStruct, 24 );
    }
    
    
    
    
    

    
	//28	byte	menstrualStatus;		// current menstrual status
    
    /**
     * getMenstrualStatus()
     * 
     * Get Menstrual Status from dataStruct.
     * 
     * @param none
     * @return MenstrualStatus
     */
    public BloodType getMenstrualStatus(){
    	return BloodType.get( (char) dataStruct[28] );
    }

    /**
     * setMenstrualStatus()
     * 
     * Set MenstrualStatus in dataStruct.
     * 
     * @param BloodType
     * @return void
     */
    public void setMenstrualStatus( BloodType status ){
    	if ( status == null ) status = BloodType.UNSPECIFIED;
    	dataStruct[28] = (byte) (status.getCode() & 0xff);
    	return ;
    }


    
    
    
	//29	byte	menarcheAge;	// menarche age 

    /**
     * getMenarcheAge()
     * 
     * Get MenarcheAge from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getMenarcheAge(){
    	return (int)( dataStruct[29] & 0xff );
    }

    /**
     * setMenarcheAge()
     * 
     * Set MenarcheAge in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setMenarcheAge( int num ){
    	dataStruct[29] = (byte)( 0xff & num );
    }
    
    
    
    
    
	//30	byte	menopauseAge;	// menopause age 

    /**
     * getMenopauseAge()
     * 
     * Get MenopauseAge from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getMenopauseAge(){
    	return (int)( dataStruct[30] & 0xff );
    }

    /**
     * setMenopauseAge()
     * 
     * Set MenopauseAge in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setMenopauseAge( int num ){
    	dataStruct[30] = (byte)( 0xff & num );
    }
    
    
    
    
    
    
    
    
    

    
    
    
    
    
	//63	byte	status;		// record status

    /**
     * getStatus()
     * 
     * Get Status from dataStruct.
     * 
     * @param none
     * @return usrlib.RecordStatus
     */
    public usrlib.RecordStatus getStatus(){
    	return usrlib.RecordStatus.get( (char) dataStruct[63] );
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
    	dataStruct[63] = ( status != null ) ? (byte) (status.getCode() & 0xff): (byte) ' ';
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
    	return String.format( "%s %s", this.getBloodType().getLabel(), this.getRhesusFactor().getLabel());
    }


    
    
    
    
    
    
    
    
    
    
    

    
    
    
    /**
     * getBloodNoteTxt()
     * 
     * Get note text 
     * 
     * @param none
     * @return String
     */
    public String getBloodNoteText(){
    	return bloodNotes.getNoteText();
    }

    /**
     * setBloodNoteTxt()
     * 
     * Set note text 
     * 
     * @param String
     * @return void
     */
	public void setBloodNoteText( String txt ){
		bloodNotes.setNoteText(txt);
	}
	


    
	
	
}

