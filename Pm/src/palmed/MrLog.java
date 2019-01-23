package palmed;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.Notes.Status;
import usrlib.Date;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;
import usrlib.Validity;


// from the C header file palmed/mr.h

public class MrLog implements LLItem {

	public enum Types {
		
		UNDEFINED( 0, "Undefined" ),
		SOAP_NOTE( 1, "SOAP Note" ),
		PROGRESS_NOTE( 2, "Progress Note" ),
		LAB_NOTE( 3, "Lab Note" ),
		NURSING_NOTE( 4, "Nursing Note" ),
		TREATMENT_NOTE( 5, "Treatment Note" ),
		PHYSICAL_EXAM( 6, "Physical Exam" ),
		NURSE_CALLBACK( 7, "Nurse Callback" ),
		DOCUMENT( 8, "Document" ),
		MISSED_APPOINTMENT( 9, "Missed Appointment" ),
		XRAY_REPORT( 10, "Xray Report" ),
		LAB_POST(11, "Lab Posting"),
		
		UNSPECIFIED( 10000, "Unspecified" );

		private static final Map<Integer, Types> lookup = new HashMap<Integer,Types>();
		
		static {
			for ( Types r : EnumSet.allOf(Types.class))
				lookup.put(r.getCode(), r );
		}

		private int code;
		private String desc;
		
		private Types( int code, String desc ){ 
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() { return code; }
		public String getDesc(){ return desc; }
		public static Types get( int code ){ return lookup.get( code & 0xff ); }
	}
	
	public enum Status {
		
		CURRENT( "Current", 'C' ),	
		REMOVED( "Removed", 'R' ),
		CHANGED( "Superceeded", 'E' );
		
		private String label;
		private int code;

		private static final Map<Integer, Status> lookup = new HashMap<Integer,Status>();
		
		static {
			for ( Status r : EnumSet.allOf(Status.class))
				lookup.put(r.getCode(), r );
		}


		Status ( String label, int code ){
			this.label = label;
			this.code = code & 0xff;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Status get( int code ){ return lookup.get( code & 0xff ); }

	}

	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
    // static members
    private static final String fnsub = "mrlog%02d.med";
    

    
    
	
    public MrLog() {
    	allocateBuffer();
    }
	
    public MrLog( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read log entry
    	if ( reca.getRec() > 1 ){
    		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), MrLog.fnsub, MrLog.getRecordLength());
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read MrLog entry
    
    public void read( Reca reca ){
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), MrLog.fnsub, MrLog.getRecordLength());
    }	



    
    
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	int vol;		// volume
    	
    	vol = Reca.todayVol();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), MrLog.fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "MrLog.writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "MrLog.write() bad reca" );

    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), MrLog.fnsub, getRecordLength());
        return true;
    }


    

    public Reca postNew( Rec ptRec ){
    	
		// new vitals
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getMrLogReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setMrLogReca(reca); }
				}
		);
		return reca;
    }
    
    

    public static Reca postNew( Rec ptRec, Date date, String desc, MrLog.Types type, Reca linkReca ){
    	
		MrLog mrLog = new MrLog();
		mrLog.setPtRec( ptRec );
		mrLog.setDate( date );
		mrLog.setDesc( desc );
		//TODO
		mrLog.setType( type );
		if ( linkReca != null ) mrLog.setLinkReca( linkReca );
		mrLog.setStatus( Status.CURRENT );
		mrLog.setValid( Validity.VALID );
		
		// post new doc record
		return mrLog.postNew( ptRec );
    }

    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( 64 ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[80];
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


    
    
    

	// RECA LinkReca;		// record link, offset 16
    
    /**
     * getLinkReca()
     * 
     * Get Link reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getLinkReca(){
    	return Reca.fromReca( dataStruct, 16 );
    }

    /**
     * setEditReca()
     * 
     * Set Link reca in dataStruct.
     * 
     * @param Reca		Link reca
     * @return void
     */
    public void setLinkReca( Reca reca ){
    	reca.toReca( dataStruct, 16 );
    }

    
    
    
 
    
    
	// RECSH Type;			// type of entry (uses disk table), offset 20
    
    /**
     * getType()
     * 
     * Get Type from dataStruct.
     * 
     * @param none
     * @return MrLog.Types
     */
    public MrLog.Types getType(){
    	MrLog.Types type = MrLog.Types.get( StructHelpers.getShort( dataStruct, 20 ));
    	return ( type == null ) ? MrLog.Types.UNDEFINED: type;
    }

    /**
     * setType()
     * 
     * Set Type in dataStruct.
     * 
     * @param MrLog.Types
     * @return void
     */
    public void setType( MrLog.Types type ){
    	if ( type == null ) type = MrLog.Types.UNDEFINED;
    	StructHelpers.setShort( type.getCode(), dataStruct, 20 );
    }
    

    
    
    



    
	// USRT Flags;			// flags, offset 22
    
    /**
     * getFlags()
     * 
     * Get Flags from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public int getFlags(){
    	return StructHelpers.getUShort( dataStruct, 22 );
    }

    /**
     * setFlags()
     * 
     * Set Flags in dataStruct.
     * 
     * @param int		flags
     * @return void
     */
    public void setFlags( int flags ){
    	StructHelpers.setUShort( flags, dataStruct, 22 );
    }
    

    
    
    



    

	// char	Desc[30];		// description, offset 24
    
    /**
     * getDesc()
     * 
     * Get description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 24, 30 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set description in dataStruct.
     * 
     * @param String		desc
     * @return void
     */
    public void setDesc( String desc ){
    	StructHelpers.setStringPadded(desc, dataStruct, 24, 30 );
    	return ;
    }


    
    
    

    
    
	// char	unused[9];		// unused field, offset 54
    
    /**
     * Status - not really used yet but uses validity byte.  Made the validity private
     * so I enforce using the status from now on.
     * 
     */
	// char	status;			// record status, 
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return Status
     */
    public Status getStatus(){
    	//return Status.get( dataStruct[79] & 0xff );
    	return ( getValid() == Validity.VALID ) ? Status.CURRENT: Status.REMOVED;
    }

    /**
     * setStatus()
     * 
     * Set status in dataStruct.
     * 
     * @param Event
     * @return void
     */
    public void setStatus( Status status ){
    	//dataStruct[79] = (byte)( status.getCode() & 0xff );
    	setValid(( status == Status.REMOVED ) ? Validity.HIDDEN: Validity.VALID );
    }


    
    
   
    
    


	// char	Valid;		// validity byte, offset 63
    
    /**
     * getValid()
     * 
     * Get validity byte from dataStruct.
     * 
     * @param none
     * @return usrlib.Validity
     */
    private Validity getValid(){
    	return usrlib.Validity.get( dataStruct[63] );
    }

    /**
     * setValid()
     * 
     * Set validity byte in dataStruct.
     * 
     * @param usrlib.Validity		valid
     * @return void
     */
    private void setValid( usrlib.Validity valid ){
    	dataStruct[63] = (byte)( valid.getCode() & 0xff );
    	return ;
    }



    
    
    
    public String getTypeDesc(){
    	return this.getType().getDesc();
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
    	return String.format( "%s  %s %s", this.getDate().getPrintable(9), this.getTypeDesc(), this.getDesc());
    }

    
    
    
    
    
    
    public static boolean removeEntry( Rec ptRec, Date date, MrLog.Types type, Reca linkReca ){
		//System.out.println( "src ptrec=" + ptRec.toString() + ", mrLog.Type=" + type.getDesc() + ", reca=" + linkReca.toString());

    	boolean ret = false;
    	
    	if ( ! Rec.isValid( ptRec )) return false;
    	if ( ! Reca.isValid( linkReca )) return false;
    	if ( type == null ) return false;

    	DirPt dirPt = new DirPt( ptRec );
    	MedPt medPt = new MedPt( dirPt.getMedRec());
    	
    	MrLog mrLog = null;
    	
    	for ( Reca reca = medPt.getMrLogReca(); Reca.isValid( reca ); reca = mrLog.getLLHdr().getLast()){

    		mrLog = new MrLog( reca );
    		
    		//System.out.println( "ptrec=" + mrLog.getPtRec().toString() + ", mrLog.Type=" + mrLog.getType().getDesc() + ", reca=" + mrLog.getLinkReca().toString());
    		
    		if (( mrLog.getPtRec().equals( ptRec )) && ( mrLog.getType() == type ) && ( mrLog.getLinkReca().equals( linkReca ))){
    			//System.out.println( "match found" );
    			mrLog.setStatus( Status.REMOVED );
    			mrLog.write( reca );
    			ret = true;
    			break;
    		}
    	}
    	
		return ret;
    }

    
    
}


/**/

