package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.RecordStatus;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;

public class HxObGyn implements LLItem {

		
		// struct HxObGyn {
		//
		//0		LLHDR	llhdr;			// linked list header
		//8		Date	date;			// date of this update
		//12	Rec		ptRec;			// patient record number
		//
		//16	byte	gravida;			// gravida
		//17	byte	para;				// para
		//18	byte	aborta;				// aborta
		//19	byte	obHxUnavailable;	// flg ob history unavailable flag
		//20	byte	obComplications;	// flg ob complications
		//21	byte	unused[3];			// unused
		//24	Reca	obNoteReca;			// note
		//
		//28	byte	menstrualStatus;	// menstrual status code
		//29	byte	menarcheAge;		// age of menarche
		//30	byte	menopauseAge;		// age of menopause
		//31	byte	menstrualFlow;		// menstrual flow
		//32	byte	flgCramps;			// flg cramps with period
		//33	byte	flgPMS;				// flag pt has PMS symptoms
		//34	byte	menstrualHxUnavailable;// flag menstrual history unavailable
		//35	byte	unused;
		//36	Reca	gynNoteReca;		// note
	
		//40	byte	sexualActivity;		// sexual activity status
		//41	byte	sameSexEncounters;	// flg same sex encounters
		//42	byte	dyspareunia;		// flg dyspareunia
		//43	byte	contraception;		// contraception method
		//44	byte	sexualHxUnavailable;// flg sexual hx unavailable
		//45	byte	unused[3];
		//48	Reca	sexNoteReca;		// note
		//
		//52	byte	unused[11];
		//63	byte	status;				// record status
		//}
		//sizecheck( 64 )
		

	public enum MenstrualStatus {
		
		PREMENARCHAL( "Pre-Menarchal", '1' ),
		MENSTRUATING( "Mensturating", '2' ),
		POSTMENOPAUSE( "Post-Menopause", '3' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, MenstrualStatus> lookup = new HashMap<Character,MenstrualStatus>();
		
		static {
			for ( MenstrualStatus r : EnumSet.allOf(MenstrualStatus.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		MenstrualStatus( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static MenstrualStatus get( char code ){ return lookup.get( code ); }
	}
	
	

	public enum MenstrualFlow {
		
		LIGHT( "Light", '1' ),
		MODERATE( "Moderate", '2' ),
		HEAVY( "Heavy", '3' ),
		UNKNOWN( "Unknown", '9' ),
		UNSPECIFIED( "Unspecified", '0' );
		
		private static final Map<Character, MenstrualFlow> lookup = new HashMap<Character,MenstrualFlow>();
		
		static {
			for ( MenstrualFlow r : EnumSet.allOf(MenstrualFlow.class))
				lookup.put(r.getCode(), r );
		}
		
		String label;
		char code;
		
		MenstrualFlow( String label, char code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public char getCode(){ return (char) this.code; }
		public static MenstrualFlow get( char code ){ return lookup.get( code ); }
	}
	
	

		public enum SexualActivityStatus {
			
			ACTIVE( "Active", 'A' ),
			NOT_ACTIVE( "Marijuana", 'N' ),
			UNKNOWN( "Unknown", '9' ),
			UNSPECIFIED( "Unspecified", '0' );
			
			private static final Map<Character, SexualActivityStatus> lookup = new HashMap<Character,SexualActivityStatus>();
			
			static {
				for ( SexualActivityStatus r : EnumSet.allOf(SexualActivityStatus.class))
					lookup.put(r.getCode(), r );
			}
			
			String label;
			char code;
			
			SexualActivityStatus( String label, char code ){
				this.label = label;
				this.code = code;
			}
			
			public String getLabel(){ return this.label; }
			public char getCode(){ return this.code; }
			public static SexualActivityStatus get( char code ){ return lookup.get( code ); }
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
		
		
		
		
		
		
		
		
		public enum ContraceptionMethod {
			
			NONE( "None", 'N' ),
			RHYTHM( "Rhythm", 'R' ),
			CONDOMS( "Condoms", 'C' ),
			PILL( "BC Pill", 'P' ),
			DEPO( "Depo Inj", 'D' ),
			IMPLANT( "Implant", 'I' ),
			IUD( "IUD", 'U' ),
			SPONGE( "Sponge", 'S' ),
			DIAPHRAGM( "Diaphragm", 'G' ),
			TUBAL( "Tubal", 'T' ),
			HYSTERECTOMY( "Hysterectomy", 'H' ),
			PARTNER( "PARTNER", 'M' ),
			UNKNOWN( "Unknown", '9' ),
			UNSPECIFIED( "Unspecified", '0' );
			
			private static final Map<Character, ContraceptionMethod> lookup = new HashMap<Character,ContraceptionMethod>();
			
			static {
				for ( ContraceptionMethod r : EnumSet.allOf(ContraceptionMethod.class))
					lookup.put(r.getCode(), r );
			}
			
			String label;
			char code;
			
			ContraceptionMethod( String label, char code ){
				this.label = label;
				this.code = code;
			}
			
			public String getLabel(){ return this.label; }
			public char getCode(){ return (char) this.code; }
			public static ContraceptionMethod get( char code ){ return lookup.get( code ); }
		}
		
		
		
		
		
		
		
		
		
		
		// fields
	    private byte[] dataStruct;
	    private Reca reca;			// this Reca
	    
		public static final String fnsub ="hxobgyn%02d.med";
		public static final String fnsubt ="hxobgynt%02d.med";
		public static final int recordLength = 64;
		
	    //private String menstrualNoteText = null;		// additional note text
	    //private boolean flgMenstrualNoteTxt = false;	// flag that note text has been set
	    //private boolean flgMenstrualNoteTxtRead = false;// flag that note text has been read
	  
	    //private String sexualNoteText = null;			// additional note text
	    //private boolean flgSexualNoteTxt = false;		// flag that note text has been set
	    //private boolean flgSexualNoteTxtRead = false;	// flag that note text has been read
	  
	    //private String obNoteText = null;				// additional note text
	    //private boolean flgObNoteTxt = false;			// flag that note text has been set
	    //private boolean flgObNoteTxtRead = false;		// flag that note text has been read
	  
	    
	    class ObNotes extends NoteTextHelper {
	    	@Override
		    public String getFnsub(){ return fnsubt; }
	    	@Override
	    	public void setReca( Reca reca ){ setObNoteReca( reca ); }
	    	@Override
	    	public Reca getReca(){ return getObNoteReca(); }
	    	@Override
	    	public Rec getRefRec(){ return getPtRec(); }
	    }
	    
	    ObNotes obNotes = null;

	    class MenstrualNotes extends NoteTextHelper {
	    	@Override
		    public String getFnsub(){ return fnsubt; }
	    	@Override
	    	public void setReca( Reca reca ){ setGynNoteReca( reca ); }
	    	@Override
	    	public Reca getReca(){ return getGynNoteReca(); }
	    	@Override
	    	public Rec getRefRec(){ return getPtRec(); }
	    }
	    
	    MenstrualNotes menstrualNotes = null;

	    class SexualNotes extends NoteTextHelper {
	    	@Override
		    public String getFnsub(){ return fnsubt; }
	    	@Override
	    	public void setReca( Reca reca ){ setSexualNoteReca( reca ); }
	    	@Override
	    	public Reca getReca(){ return getSexualNoteReca(); }
	    	@Override
	    	public Rec getRefRec(){ return getPtRec(); }
	    }
	    
	    SexualNotes sexualNotes = null;
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
		
	    public HxObGyn() {
	    	allocateBuffer();
	    	this.obNotes = new ObNotes();
	    	this.menstrualNotes = new MenstrualNotes();
	    	this.sexualNotes = new SexualNotes();
	    }
		
	    public HxObGyn( Reca reca ){

	        // allocate space
	    	allocateBuffer();
	    	this.obNotes = new ObNotes();
	    	this.menstrualNotes = new MenstrualNotes();
	    	this.sexualNotes = new SexualNotes();
	    	
	    	// read record
	    	if ( Reca.isValid( reca )){
	    		this.read( reca );
	    	}
	    	
	    	// set local copy of reca
	    	this.reca = reca;
	    	
	    	
	    }


		// read record
	    
	    public void read( Reca reca ){
	    	
	    	//flgMenstrualNoteTxtRead = false;			// haven't read note text record yet
	    	//flgSexualNoteTxtRead = false;				// haven't read note text record yet
	    	//flgObNoteTxtRead = false;					// haven't read note text record yet
	    	obNotes.resetReadStatus();
	    	menstrualNotes.resetReadStatus();
	    	sexualNotes.resetReadStatus();

			if ( ! Reca.isValid( reca )) return;		
			RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	   }	


	    
	    
	    public Reca writeNew(){
	    	
	    	Reca reca;		// reca
	    	
	    	int vol = Reca.todayVol();
	    	
	    	// handle note text
	    	//if ( flgMenstrualNoteTxt ){
	    	//	Reca reca1 = this.writeMenstrualNoteText( menstrualNoteText );
	    	//	this.setGynNoteReca( reca1 );
	    	//	flgMenstrualNoteTxt = false;
	    	//}

	    	//if ( flgSexualNoteTxt ){
	    	//	Reca reca2 = this.writeSexualNoteText( sexualNoteText );
	    	//	this.setSexualNoteReca( reca2 );
	    	//	flgSexualNoteTxt = false;
	    	//}
	    	
	    	//if ( flgObNoteTxt ){
	    	//	Reca reca2 = this.writeObNoteText( obNoteText );
	    	//	this.setObNoteReca( reca2 );
	    	//	flgObNoteTxt = false;
	    	//}
	    	
	    	// write free text notes records
	    	obNotes.write();
	    	menstrualNotes.write();
	    	sexualNotes.write();
	    	
	    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

	    	return reca;
	    }
	    
	    
	    
	    public boolean write( Reca reca ){
	    	
	    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "bad reca" );

	    	// handle note text
	    	//if ( flgMenstrualNoteTxt ){
	    	//	Reca reca1 = this.writeMenstrualNoteText( menstrualNoteText );
	    	//	this.setGynNoteReca( reca1 );
	    	//	flgMenstrualNoteTxt = false;
	    	//}

	    	//if ( flgSexualNoteTxt ){
	    	//	Reca reca2 = this.writeSexualNoteText( sexualNoteText );
	    	//	this.setSexualNoteReca( reca2 );
	    	//	flgSexualNoteTxt = false;
	    	//}
	    	
	    	//if ( flgObNoteTxt ){
	    	//	Reca reca2 = this.writeObNoteText( obNoteText );
	    	//	this.setObNoteReca( reca2 );
	    	//	flgObNoteTxt = false;
	    	//}
	    	
	    	// write free text notes records
	    	obNotes.write();
	    	menstrualNotes.write();
	    	sexualNotes.write();
	    	
	    	// write record
	        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	        return true;
	    }


	    
	    
	    public Reca postNew( Rec ptRec ){
	    	
			// new 
			Reca reca = LLPost.post( ptRec, this, 
					new LLPost.LLPost_Helper () {
						public Reca getReca( MedPt medPt ){ return medPt.getHxGynReca(); }
						public void setReca( MedPt medPt, Reca reca ){ medPt.setHxGynReca(reca); }
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


	    
	    
	    

		//16	byte	gravida;	// gravida

	    /**
	     * getGravida()
	     * 
	     * Get Gravida from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getGravida(){
	    	return (int)( dataStruct[16] & 0xff );
	    }

	    /**
	     * setGravida()
	     * 
	     * Set Gravida in dataStruct.
	     * 
	     * @param int
	     * @return void
	     */
	    public void setGravida( int num ){
	    	dataStruct[16] = (byte)( 0xff & num );
	    }
	    
	    
	    
	    
	    

		//17	byte	para;	// para

	    /**
	     * getPara()
	     * 
	     * Get Para from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getPara(){
	    	return (int)( dataStruct[17] & 0xff );
	    }

	    /**
	     * setPara()
	     * 
	     * Set Para in dataStruct.
	     * 
	     * @param int
	     * @return void
	     */
	    public void setPara( int num ){
	    	dataStruct[17] = (byte)( 0xff & num );
	    }
	    
	    
	    
	    
	    

		//18	byte	aborta;	// aborta

	    /**
	     * getAborta()
	     * 
	     * Get Aborta from dataStruct.
	     * 
	     * @param none
	     * @return int
	     */
	    public int getAborta(){
	    	return (int)( dataStruct[18] & 0xff );
	    }

	    /**
	     * setAborta()
	     * 
	     * Set Aborta in dataStruct.
	     * 
	     * @param int
	     * @return void
	     */
	    public void setAborta( int num ){
	    	dataStruct[18] = (byte)( 0xff & num );
	    }
	    
	    
	    
	    
	    

		//19	byte	flgObHxUnavailable;		// flag that OB history is unavailable
	    
	    /**
	     * getObHxUnavailable()
	     * 
	     * Get ObHxUnavailable from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getObHxUnavailable(){
	    	return FlagStatus.get( (char) dataStruct[19] );
	    }

	    /**
	     * setObHxUnavailable()
	     * 
	     * Set ObHxUnavailable in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setObHxUnavailable( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[19] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }

	    
	    

	    
		//20	byte	flgObComplications;		// flag that there were OB complications
	    
	    /**
	     * getObComplications()
	     * 
	     * Get ObComplications from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getObComplications(){
	    	return FlagStatus.get( (char) dataStruct[20] );
	    }

	    /**
	     * setObComplications()
	     * 
	     * Set ObComplications in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setObComplications( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[20] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
		//24	Reca	obNoteReca;	// note
	    
	    /**
	     * getObNoteReca()
	     * 
	     * Get note Reca from dataStruct.
	     * 
	     * @param none
	     * @return Reca
	     */
	    public Reca getObNoteReca(){
	    	return Reca.fromReca( dataStruct, 24 );
	    }

	    /**
	     * setObNoteReca()
	     * 
	     * Set note Reca record in dataStruct.
	     * 
	     * @param Reca
	     * @return void
	     */
	    public void setObNoteReca( Reca reca ){
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
	    public MenstrualStatus getMenstrualStatus(){
	    	return MenstrualStatus.get( (char) dataStruct[28] );
	    }

	    /**
	     * setMenstrualStatus()
	     * 
	     * Set MenstrualStatus in dataStruct.
	     * 
	     * @param BloodType
	     * @return void
	     */
	    public void setMenstrualStatus( MenstrualStatus status ){
	    	if ( status == null ) status = MenstrualStatus.UNSPECIFIED;
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
	    
	    
	    
	    
	    
		//31	byte	menstrualFlow;		// current menstrual flow status
	    
	    /**
	     * getMenstrualFlow()
	     * 
	     * Get Menstrual Flow from dataStruct.
	     * 
	     * @param none
	     * @return MenstrualFlow
	     */
	    public MenstrualFlow getMenstrualFlow(){
	    	return MenstrualFlow.get( (char) dataStruct[31] );
	    }

	    /**
	     * setMenstrualFlow()
	     * 
	     * Set MenstrualFlow in dataStruct.
	     * 
	     * @param MenstrualFlow
	     * @return void
	     */
	    public void setMenstrualFlow( MenstrualFlow status ){
	    	if ( status == null ) status = MenstrualFlow.UNSPECIFIED;
	    	dataStruct[31] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
		//32	byte	flgCramps;		// flag has cramps with period
	    
	    /**
	     * getFlgCramps()
	     * 
	     * Get FlgCramps Flow from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getFlgCramps(){
	    	return FlagStatus.get( (char) dataStruct[32] );
	    }

	    /**
	     * setFlgCramps()
	     * 
	     * Set FlgCramps in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setFlgCramps( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[32] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
		//33	byte	flgPMS;		// flag has symptoms of PMS
	    
	    /**
	     * getFlgPMS()
	     * 
	     * Get FlgPMS Flow from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getFlgPMS(){
	    	return FlagStatus.get( (char) dataStruct[33] );
	    }

	    /**
	     * setFlgPMS()
	     * 
	     * Set FlgPMS in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setFlgPMS( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[33] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
		//34	byte	flgMenstrualHxUnavailable;		// flag that menstrual history is unavailable
	    
	    /**
	     * getMenstrualHxUnavailable()
	     * 
	     * Get MenstrualHxUnavailable from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getMenstrualHxUnavailable(){
	    	return FlagStatus.get( (char) dataStruct[34] );
	    }

	    /**
	     * setMenstrualHxUnavailable()
	     * 
	     * Set MenstrualHxUnavailable in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setMenstrualHxUnavailable( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[34] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }

	    
	    

	    
	    
	    
	    //35	byte	unused;			// unused
	    
	    
	    	    
	    

		//36	Reca	gynNoteReca;	// note
	    
	    /**
	     * getGynNoteReca()
	     * 
	     * Get note Reca from dataStruct.
	     * 
	     * @param none
	     * @return Reca
	     */
	    public Reca getGynNoteReca(){
	    	return Reca.fromReca( dataStruct, 36 );
	    }

	    /**
	     * setGynNoteReca()
	     * 
	     * Set note Reca record in dataStruct.
	     * 
	     * @param Reca
	     * @return void
	     */
	    public void setGynNoteReca( Reca reca ){
	    	reca.toReca( dataStruct, 36 );
	    }
	    
	    
	    
	    
	    

	    
	    
	    
		//40	byte	sexualActivityStatus;		// sexual activity status

	    /**
	     * getSexualActivityStatus()
	     * 
	     * Get Sexual Activity Status from dataStruct.
	     * 
	     * @param none
	     * @return SexualActivity
	     */
	    public SexualActivityStatus getSexuallyActiveStatus(){
	    	return SexualActivityStatus.get( (char) dataStruct[40] );
	    }

	    /**
	     * setSexualActivityStatus()
	     * 
	     * Set Sexual Activity Status in dataStruct.
	     * 
	     * @param UserStatus
	     * @return void
	     */
	    public void setSexualActivityStatus( SexualActivityStatus status ){
	    	if ( status == null ) status = SexualActivityStatus.UNSPECIFIED;
	    	dataStruct[40] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }
	    
	    


		//41	byte	flgSameSexEncounters;		// flag has history of same sex encounters
	    
	    /**
	     * getFlgSameSexEncounters()
	     * 
	     * Get FlgSameSexEncounters from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getFlgSameSexEncounters(){
	    	return FlagStatus.get( (char) dataStruct[41] );
	    }

	    /**
	     * setFlgPMS()
	     * 
	     * Set FlgSameSexEncounters in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setFlgSameSexEncounters( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[41] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
	    
		//42	byte	flgDyspareunia;		// flag has dyspareunia
	    
	    /**
	     * getFlgDyspareunia()
	     * 
	     * Get FlgDyspareunia from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getFlgDyspareunia(){
	    	return FlagStatus.get( (char) dataStruct[42] );
	    }

	    /**
	     * setFlgDyspareunia()
	     * 
	     * Set FlgDyspareunia in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setFlgDyspareunia( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[42] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
	    
		//43	byte	contraception;		// contraception method
	    
	    /**
	     * getContraceptionMethod()
	     * 
	     * Get ContraceptionMethod from dataStruct.
	     * 
	     * @param none
	     * @return ContraceptionMethod
	     */
	    public ContraceptionMethod getContraceptionMethod(){
	    	return ContraceptionMethod.get( (char) dataStruct[43] );
	    }

	    /**
	     * setContraceptionMethod()
	     * 
	     * Set ContraceptionMethod in dataStruct.
	     * 
	     * @param ContraceptionMethod
	     * @return void
	     */
	    public void setContraceptionMethod( ContraceptionMethod status ){
	    	if ( status == null ) status = ContraceptionMethod.UNSPECIFIED;
	    	dataStruct[43] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }


	    
	    
	    
	    
		//44	byte	flgSexualHxUnavailable;		// flag that sexual history is unavailable
	    
	    /**
	     * getSexualHxUnavailable()
	     * 
	     * Get SexualHxUnavailable from dataStruct.
	     * 
	     * @param none
	     * @return FlagStatus
	     */
	    public FlagStatus getSexualHxUnavailable(){
	    	return FlagStatus.get( (char) dataStruct[44] );
	    }

	    /**
	     * setSexualHxUnavailable()
	     * 
	     * Set SexualHxUnavailable in dataStruct.
	     * 
	     * @param FlagStatus
	     * @return void
	     */
	    public void setSexualHxUnavailable( FlagStatus status ){
	    	if ( status == null ) status = FlagStatus.UNSPECIFIED;
	    	dataStruct[44] = (byte) (status.getCode() & 0xff);
	    	return ;
	    }

	    
	    

	    
		//48	Reca	sexNoteReca;	// note
	    
	    /**
	     * getSexualNoteReca()
	     * 
	     * Get sex note Reca from dataStruct.
	     * 
	     * @param none
	     * @return Reca
	     */
	    public Reca getSexualNoteReca(){
	    	return Reca.fromReca( dataStruct, 48 );
	    }

	    /**
	     * setSexualNoteReca()
	     * 
	     * Set sex note Reca record in dataStruct.
	     * 
	     * @param Reca
	     * @return void
	     */
	    public void setSexualNoteReca( Reca reca ){
	    	reca.toReca( dataStruct, 48 );
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
	    	return String.format( "%s", this.getMenstrualStatus().getLabel());
	    }


	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /**
	     * getMenstrualNoteTxt()
	     * 
	     * Get note text 
	     * 
	     * @param none
	     * @return String
	     */
/*	    public String getMenstrualNoteText(){
	    	
	    	// have we read note text record yet, if not - read it
	    	if ( ! flgMenstrualNoteTxtRead ){
	    		
	    		Reca reca = this.getGynNoteReca();
	    		if ( Reca.isValid(reca)){
	    			this.menstrualNoteText = this.readMenstrualNoteText( reca );
	    			flgMenstrualNoteTxtRead = true;
	    		}
	    	}
	    	
	    	return ( this.menstrualNoteText == null ) ? "": this.menstrualNoteText;
	    }
*/
	    /**
	     * setMenstrualNoteTxt()
	     * 
	     * Set note text 
	     * 
	     * @param String
	     * @return void
	     */
/*	    public void setMenstrualNoteText( String txt ){
	    	
	    	// does he have note saved already?  read it.
	    	if ( ! this.flgMenstrualNoteTxtRead && Reca.isValid( this.getGynNoteReca())){
	    		this.readMenstrualNoteText();
	    	}
	    	
	    	// handle trivial cases
	    	if (( txt == null ) || (( txt = txt.trim()).length() < 1 )){
	    		// if there is already a note
	    		if ( this.flgMenstrualNoteTxtRead && ( this.menstrualNoteText.length() > 0 )){
	    			// set note to no text
	    			this.menstrualNoteText = "";
	    			this.flgMenstrualNoteTxt = true;
	    		}
	    		return;
	    	}

	    	
	    	// are the new and old the same?
	    	//   if yes, nothing to do
	    	if ( txt.equals( this.menstrualNoteText )) return;
	    	
	    	// set addlSig text and mark to be written
	    	this.flgMenstrualNoteTxt = true;
	    	this.menstrualNoteText = txt;
	    }
*/

	    
	    
	    
	    
	    /**
	     * getSexualNoteTxt()
	     * 
	     * Get note text 
	     * 
	     * @param none
	     * @return String
	     */
/*	    public String getSexualNoteText(){
	    	
	    	// have we read note text record yet, if not - read it
	    	if ( ! flgSexualNoteTxtRead ){
	    		
	    		Reca reca = this.getSexualNoteReca();
	    		if ( Reca.isValid(reca)){
	    			this.sexualNoteText = this.readSexualNoteText( reca );
	    			flgSexualNoteTxtRead = true;
	    		}
	    	}
	    	
	    	return ( this.sexualNoteText == null ) ? "": this.sexualNoteText;
	    }
*/
	    /**
	     * setSexualNoteTxt()
	     * 
	     * Set note text 
	     * 
	     * @param String
	     * @return void
	     */
/*	    public void setSexualNoteText( String txt ){
	    	
	    	// does he have note saved already?  read it.
	    	if ( ! this.flgSexualNoteTxtRead && Reca.isValid( this.getSexualNoteReca())){
	    		this.readSexualNoteText();
	    	}
	    	
	    	// handle trivial cases
	    	if (( txt == null ) || (( txt = txt.trim()).length() < 1 )){
	    		// if there is already a note
	    		if ( this.flgSexualNoteTxtRead && ( this.sexualNoteText.length() > 0 )){
	    			// set note to no text
	    			this.sexualNoteText = "";
	    			this.flgSexualNoteTxt = true;
	    		}
	    		return;
	    	}

	    	// is new and old the same?
	    	if ( txt.equals( this.sexualNoteText )) return;
	    	
	    	// set addlSig text and mark to be written
	    	this.flgSexualNoteTxt = true;
	    	this.sexualNoteText = txt;
	    }
*/

	    
	    
	    
	    /**
	     * getObNoteTxt()
	     * 
	     * Get note text 
	     * 
	     * @param none
	     * @return String
	     */
	    public String getObNoteText(){
	    	return obNotes.getNoteText();
	    }
	    public String getMenstrualNoteText(){
	    	return menstrualNotes.getNoteText();
	    }
	    public String getSexualNoteText(){
	    	return sexualNotes.getNoteText();
	    }
	    
	    
	    	// have we read note text record yet, if not - read it
//	    	if ( ! flgObNoteTxtRead ){
//	    		
//	    		Reca reca = this.getObNoteReca();
//	    		if ( Reca.isValid(reca)){
//	    			this.obNoteText = this.readObNoteText( reca );
//	    			flgObNoteTxtRead = true;
//	    		}
//	    	}
//	    	
//	    	return ( this.obNoteText == null ) ? "": this.obNoteText;
//	    }

	    /**
	     * setObNoteTxt()
	     * 
	     * Set note text 
	     * 
	     * @param String
	     * @return void
	     */
		public void setObNoteText( String txt ){
			obNotes.setNoteText(txt);
		}
		
		public void setMenstrualNoteText( String txt ){
			menstrualNotes.setNoteText(txt);
		}
		
		public void setSexualNoteText( String txt ){
			sexualNotes.setNoteText(txt);
		}

	    	// does he have note saved already?  read it.
//	    	if ( ! this.flgObNoteTxtRead && Reca.isValid( this.getObNoteReca())){
//	    		this.readObNoteText();
//	    	}
//	    	
//	    	// handle trivial cases
//	    	if (( txt == null ) || (( txt = txt.trim()).length() < 1 )){
//	    		// if there is already a note
//	    		if ( this.flgObNoteTxtRead && ( this.obNoteText.length() > 0 )){
//	    			// set note to no text
//	    			this.obNoteText = "";
//	    			this.flgObNoteTxt = true;
//	    		}
//	    		return;
//	    	}
//
//	    	// is new and old the same?
//	    	if ( txt.equals( this.obNoteText )) return;
//	    	
//	    	// set addlSig text and mark to be written
//	    	this.flgObNoteTxt = true;
//	    	this.obNoteText = txt;
//	    }


	    
/*	    
	    private String fnsubTxt = "hxobgynt%02d.med";

	    
	    private String readNoteText( Reca reca ){
	    	
	    	// handle trivial case
	    	if (( reca == null ) || ( ! reca.isValid())) return "";
	    	// read entry from text file
	    	String s = NoteText.readText(fnsubTxt, reca.getYear(), new Rec(reca.getRec()));
	    	   	
			return s.trim();
	    }

	    private String readMenstrualNoteText() { return readMenstrualNoteText( this.getGynNoteReca()); }
	    private String readMenstrualNoteText( Reca reca ){ 
			this.menstrualNoteText = readNoteText( reca );
			flgMenstrualNoteTxtRead = true;
	    	return this.menstrualNoteText;
	    }
	    
	    private String readSexualNoteText() { return readSexualNoteText( this.getSexualNoteReca()); }
	    private String readSexualNoteText( Reca reca ){ 
			this.sexualNoteText = readNoteText( reca );
			flgSexualNoteTxtRead = true;
	    	return this.sexualNoteText;
	    }

	    
//	    private String readObNoteText() { return readObNoteText( this.getObNoteReca()); }
//	    private String readObNoteText( Reca reca ){ 
//			this.obNoteText = readNoteText( reca );
//			flgObNoteTxtRead = true;
//	    	return this.obNoteText;
//	    }

	    
	    
	    
	    
	    
	    private Reca writeNoteText( String text, Reca noteReca ){
	    	if ( text == null ) text = "";    	
	    	Reca reca = NoteText.saveText(fnsubTxt, Date.today(), this.getPtRec(), noteReca, text );
	    	return reca;
	    }

	    private Reca writeMenstrualNoteText( String text ){
	    	return writeNoteText( text, this.getGynNoteReca());
	    }

	    private Reca writeSexualNoteText( String text ){
	    	return writeNoteText( text, this.getSexualNoteReca());
	    }

//	    private Reca writeObNoteText( String text ){
//	    	return writeNoteText( text, this.getObNoteReca());
//	    }

*/	    	
		
		
	}

