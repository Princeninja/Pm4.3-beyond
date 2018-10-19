package palmed;



//
//0	LLHDR	llhdr;		// linked list header
//8	Rec		ptrec;		// patient record number
//12	Date	date;		// date
//
//16	Reca	visitReca	// visit record number
//20	Rec		orderRec	// item ordered
//24	char	desc[40];	// text description
//64	Date	dateSatisfied;	// date item satisfied
//68	Reca	noteReca;	// text note
//72	Rec		pcnRec;		// provider record number
//
//76	char	inications[40];	// indications text - TEMP
//
//116	char	unused[8];	// unused space
//
//124	byte	priority;	// priority
//125	byte	status;		// flag item satisfied
//126	byte	reason;		// satisfied reason
//127	byte	valid;		// validity byte
//RecLength = 128;






import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.EditHelpers;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.Validity;

public class Orders implements LLItem {

	
	
	
	public enum Status {
		
		UNDEFINED( "", 0 ), 
		ORDERED( "Ordered", 1 ), 
		SCHEDULED( "Scheduled", 2 ), 
		IN_PROGRESS( "In Progress", 3 ),
		CLOSED( "Closed", 9 );
		
		private int code;
		private String label;
		
		private static final Map<Integer, Status> lookup = new HashMap<Integer,Status>();
		
		static {
			for ( Status r : EnumSet.allOf(Status.class))
				lookup.put(r.getCode(), r );
		}

		Status ( String label, int code ){
			this.label = label;
			this.code = code;
		}
		
		public int getCode(){ return this.code; }
		public String getLabel(){ return this.label; }
		public static Status get( int code ){ return lookup.get( code ); }
	}
	
	
	
	
	public enum Reason {
		
		UNSPECIFIED( "", 0 ),
		DONE( "Done", 1 ),
		PT_REFUSED( "Pt Refused", 2 ),
		PT_MISSED( "Pt Missed", 3 ),
		PAYER_DENIED( "Payer Denied", 4 ),
		PROVIDER_RECONSIDERED( "Provider Reconsidered", 5 );
		
		private int code;
		private String label;
		
		private static final Map<Integer, Reason> lookup = new HashMap<Integer,Reason>();
		
		static {
			for ( Reason r : EnumSet.allOf(Reason.class))
				lookup.put(r.getCode(), r );
		}

		Reason ( String label, int code ){
			this.label = label;
			this.code = code;
		}
		
		public int getCode(){ return this.code; }
		public String getLabel(){ return this.label; }
		public static Reason get( int code ){ return lookup.get( code ); }
	}
	
	
	public enum Priority {
		
		UNSPECIFIED( "", 0 ),
		ROUTINE( "Routine", 1 ),
		ASAP( "ASAP", 2 ),
		STAT( "STAT", 3 );
		
		private int code;
		private String label;
		
		private static final Map<Integer, Priority> lookup = new HashMap<Integer,Priority>();
		
		static {
			for ( Priority r : EnumSet.allOf(Priority.class))
				lookup.put(r.getCode(), r );
		}

		Priority ( String label, int code ){
			this.label = label;
			this.code = code;
		}
		
		public int getCode(){ return this.code; }
		public String getLabel(){ return this.label; }
		public static Priority get( int code ){ return lookup.get( code ); }
	}
	
	
	

	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
    private final static String fn_orders = "orders%02d.med";
    private final static int recLength = 128;	// record length
    
    private String noteText = null;		// additional note text
    private boolean flgNoteTxt = false;	// flag that note text has been set
    private boolean flgNoteTxtRead = false;	// flag that note text has been read
  
    
    
    
    
	public Orders(){
		
    	allocateBuffer();
	}

	
	
    public Orders( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read  record
    	read( reca );
    	
    	// set local copy of reca
    	this.reca = reca;
    }



	// read record
	public void read(Reca reca) {
		if (( reca == null ) || ( reca.getRec() < 2 )){ SystemHelpers.seriousError( "Orders.read() bad reca" ); return; }
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_orders, getRecordLength());
	}

	
	
	
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Orders.write() bad reca" );

       	// handle note text
    	if ( flgNoteTxt ){
    		Reca noteReca = this.writeNoteText( noteText );
    		this.setNoteReca( noteReca );
    		flgNoteTxt = false;
    	}
    	
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_orders, getRecordLength());
        return true;
    }



	
	
	
	// write a new record
    public Reca writeNew(){
    	
       	// handle note text
    	if ( flgNoteTxt ){
    		Reca noteReca = this.writeNoteText( noteText );
    		this.setNoteReca( noteReca );
    		flgNoteTxt = false;
    	}
    	
    	Reca reca = RecaFile.newReca( Reca.todayVol(), this.dataStruct, Pm.getMedPath(), fn_orders, getRecordLength());
    	if (( reca == null ) || ( reca.getRec() < 2 )) SystemHelpers.seriousError( "Orders.writeNew() bad reca" );

    	return reca;
    }

    
    
    
    public Reca postNew( Rec ptRec ){
    	
		// new vitals
		Reca reca = LLPost.post( ptRec, this, 
			new LLPost.LLPost_Helper () {
				public Reca getReca( MedPt medPt ){ return medPt.getOrdersReca(); }
				public void setReca( MedPt medPt, Reca reca ){ medPt.setOrdersReca(reca); }
			}
		);
		return reca;
    }
    
    
    

    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[getRecordLength()];
    }

    public void setDataStruct(byte[] dataStruct) { this.dataStruct = dataStruct; }

    public byte[] getDataStruct() { return dataStruct; }

   

    
    
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


    
    
    
    // DATE date;			// document date, offset 12    
    
    /**
     * getDate()
     * 
     * Get date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDate(){
       	return StructHelpers.getDate( dataStruct, 12 );
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
        StructHelpers.setDate( date, dataStruct, 12 );
        return;
    }


    
    
    
	// RECORD PtRec;		// patient record number, offset 8
    
    /**
     * getPtRec()
     * 
     * Get patient record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPtRec(){
    	return Rec.fromInt( dataStruct, 8 );
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
    	ptRec.toInt( dataStruct, 8 );
    }


    
    
    

	// RECORD VisitReca;		// visit record number, offset 16
    
    /**
     * getVisitReca()
     * 
     * Get visit record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getVisitReca(){
    	return Reca.fromReca( dataStruct, 16 );
    }

    /**
     * setVisitReca()
     * 
     * Set visit record in dataStruct.
     * 
     * @param Reca		visit record number
     * @return void
     */
    public void setVisitReca( Reca reca ){
    	reca.toReca( dataStruct, 16 );
    }


    
    
    
	// RECORD OrderRec;		// ordered item record number, offset 20
    
    /**
     * getOrderRec()
     * 
     * Get ordered item record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getOrderRec(){
    	return Rec.fromInt( dataStruct, 20 );
    }

    /**
     * setOrderRec()
     * 
     * Set ordered item record in dataStruct.
     * 
     * @param Rec		ordered item record number
     * @return void
     */
    public void setOrderRec( Rec r ){
    	r.toInt( dataStruct, 20 );
    }


    
    
    
	// char	desc[40];		// text description, offset 24
    
    /**
     * getDesc()
     * 
     * Get text description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 24, 40 );
    }

    /**
     * setDesc()
     * 
     * Set text description in dataStruct 
     * 
     * @param String
     * @return void
     */
    public void setDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 24, 40 );
    	return ;
    }


    
    
    // DATE dateSatisfied;			// satisfied date, offset 64    
    
    /**
     * getDateSatisfied()
     * 
     * Get date satisfied from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getDateSatisfied(){
       	return StructHelpers.getDate( dataStruct, 64 );
    }

    /**
     * setDateSatisfied()
     * 
     * Set date in dataStruct.
     * 
     * @param usrlib.Date		dateSatisfied
     * @return void
     */
    public void setDateSatisfied( usrlib.Date dateSatisfied ){
        StructHelpers.setDate( dateSatisfied, dataStruct, 64 );
        return;
    }


    
    
    
  

	// RECORD NoteReca;		// note record number, offset 68
    
    /**
     * getNoteReca()
     * 
     * Get note record from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getNoteReca(){
    	return Reca.fromReca( dataStruct, 68 );
    }

    /**
     * setNoteReca()
     * 
     * Set note record in dataStruct.
     * 
     * @param Reca		note record number
     * @return void
     */
    public void setNoteReca( Reca reca ){
    	reca.toReca( dataStruct, 68 );
    }


    
    
	// RECORD pcnRec;		// provider record number, offset 72
    
    /**
     * getPcnRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPcnRec(){
    	return Rec.fromInt( dataStruct, 72 );
    }

    /**
     * setPcnRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec		provider record number
     * @return void
     */
    public void setPcnRec( Rec r ){
    	r.toInt( dataStruct, 72 );
    }


    
    
    
	// char	priority;			// Priority, offset 124
    
    /**
     * getPriority()
     * 
     * Get Priority from dataStruct.
     * 
     * @param none
     * @return Priority
     */
    public Priority getPriority(){
    	return Priority.get( (int)( dataStruct[124] & 0xff ));
    }

    /**
     * setPriority()
     * 
     * Set Priority in dataStruct.
     * 
     * @param Priority
     * @return void
     */
    public void setPriority( Priority s ){
    	dataStruct[124] = (byte)( s.getCode() & 0xff );
    }


 
 

  
	// char	flgStatus;			// satisfied status flag, offset 125
    
    /**
     * getStatus()
     * 
     * Get satisfied status from dataStruct.
     * 
     * @param none
     * @return Orders.Satisfied
     */
    public Orders.Status getStatus(){
    	return Orders.Status.get( (int)( dataStruct[125] & 0xff ));
    }

    /**
     * setStatus()
     * 
     * Set satisfied status in dataStruct.
     * 
     * @param Orders.Satisfied
     * @return void
     */
    public void setStatus( Orders.Status s ){
    	dataStruct[125] = (byte)( s.getCode() & 0xff );
    }


 
 

	// char	flgReason;			// Reason status flag, offset 126
    
    /**
     * getReason()
     * 
     * Get Reason status from dataStruct.
     * 
     * @param none
     * @return Orders.Reason
     */
    public Orders.Reason getReason(){
    	return Orders.Reason.get( (int)( dataStruct[126] & 0xff ));
    }

    /**
     * setReason()
     * 
     * Set Reason status in dataStruct.
     * 
     * @param Orders.Reason
     * @return void
     */
    public void setReason( Orders.Reason s ){
    	dataStruct[126] = (byte)( s.getCode() & 0xff );
    }


 
 



	// char	indications[40];			// TEMP - indications text, offset 126
    
    /**
     * getIndicationsText()
     * 
     * Get IndicationsText from dataStruct.
     * 
     * @param none
     * @return 
     */
    public String getIndicationsText(){
    	return StructHelpers.getString( dataStruct, 76, 40 );
    }

    /**
     * setIndicationsText()
     * 
     * Set IndicationsText in dataStruct.
     * 
     * @param String
     * @return void
     */
    public void setIndicationsText( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 76, 40);
    }


 
 



	
	
	
    // validity byte, offset 127   
    
    
    public Validity getValid(){
    	Validity valid = Validity.get( dataStruct[127] );
    	return ( valid == null ) ? Validity.INVALID: valid;
    }
    
    public void setValid( Validity valid ){
    	dataStruct[127] = (byte) (valid.getCode() & 0xff);
    }
    
    public boolean isValid(){
    	return ( getValid() == Validity.VALID );
    }
    



    public void dump(){
    	StructHelpers.dump( "Orders", dataStruct, getRecordLength());
    	return;
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
    		
    		Reca reca = this.getNoteReca();
    		if ( Reca.isValid(reca)){
    			this.noteText = this.readNoteText( reca );
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


    
    
    private final static String fnsubTxt = "ordert%02d.med";
    private final static int txtRecLength = 64;
    
    private String readNoteText( Reca reca ){
    	
    	// handle trivial case
    	if (( reca == null ) || ( ! reca.isValid())) return "";
    	   	
    	// read entry from text file
    	byte[] dataStruct = new byte[txtRecLength];
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsubTxt, txtRecLength );
		String s = StructHelpers.getString(dataStruct, 0, txtRecLength );
		return s.trim();
    }
    
    private Reca writeNoteText( String text ){
    	
     	// handle trivial case
    	if ( text == null ) return null;
    	
    	byte[] dataStruct = new byte[txtRecLength];
    	StructHelpers.setStringPadded( text, dataStruct, 0, txtRecLength );
    	Reca reca = RecaFile.newReca( Reca.todayVol(), dataStruct, Pm.getMedPath(), fnsubTxt, txtRecLength );
    	
    	return reca;
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
    	return String.format( "%s %s %s", this.getDate().getPrintable(9), this.getDesc(), this.getPriority().getLabel());
    }


}
