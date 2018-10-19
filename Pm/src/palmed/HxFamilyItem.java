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

public class HxFamilyItem implements LLItem {
		
		// struct HxFamilyItem {
		//
		//0		LLHDR	llhdr;			// linked list header
		//8		Date	date;			// date of this update
		//12	Rec		ptRec;			// patient record number
		//
		//16	Rec		probTblRec;		// record link to ProbTbl
		//20	Reca	noteReca;		// note reca
		//24	byte	memberCode;		// Member enum code
		//25	byte	unused[6];		// unused
		//31	byte	status;			// status (valid, removed, etc)
		//}
		//sizecheck( 32 )
		

	public enum Member {
		
		FATHER( 	"Father",		1, 1, 1 ),	
		MOTHER( 	"Mother",		2, 2, 2 ),		
		BROTHER( 	"Brother",		3, 3, 1 ),
		SISTER( 	"Sister",		4, 3, 2 ),
		SON( 		"Son",			5, 4, 1 ),
		DAUGHTER( 	"Daughter",		6, 4, 2 ),		
		MGF( 		"M.GrandFather",7, 2, 1 ),
		MGM( 		"M.GrandMother",8, 2, 2 ),
		PGF( 		"P.GrandFather",9, 1, 1 ),
		PGM( 		"P.GrandMother",10, 1, 2 ),
		MUNCLE( 	"M.Uncle",		11, 2, 1 ),
		MAUNT( 		"M.Aunt",		12, 2, 2 ),
		PUNCLE( 	"P.Uncle",		13, 1, 1 ),
		PAUNT( 		"P.Aunt",		14, 1, 2 ),
		PATERNAL( 	"Paternal",		15, 1, 0 ),
		MATERNAL( 	"Maternal",		16, 2, 0 ),
		SIBLINGS(	"Siblings",		17, 3, 0 ),
		UNSPECIFIED("Unspecified",	0, 0, 0 );
	
		private String label;
		private int code;
		private int side;
		private int sex;

		private static final Map<Integer, Member> lookup = new HashMap<Integer,Member>();
		
		static {
			for ( Member r : EnumSet.allOf(Member.class))
				lookup.put(r.getCode(), r );
		}


		Member ( String label, int code, int side, int sex ){
			this.label = label;
			this.code = code & 0xff;
			this.side = side;
			this.sex = sex;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Member get( int code ){ return lookup.get( code & 0xff ); }

		public boolean isPaternal() { return (this.side == 1); }
		public boolean isMaternal() { return (this.side == 2); }
		public boolean isSiblings() { return (this.side == 3); }
		public boolean isChildren() { return (this.side == 4); }
		public boolean isMale() { return (this.sex == 1); }
		public boolean isFemale() { return (this.sex == 2); }
	}



		
		
		// fields
	    private byte[] dataStruct;
	    private Reca reca;			// this Reca
	    
		public static final String fnsub ="hxfam2%02d.med";
		public static final int recordLength = 32;
		
	    private String fnsubTxt = "hxfam2t%02d.med";
	  
	  
	    
	    
	    // set up NoteTextHelper class to handle notes
	    
	    class HxNotes extends NoteTextHelper {
	    	@Override
		    public String getFnsub(){ return fnsubTxt; }
	    	@Override
	    	public void setReca( Reca reca ){ setNoteReca( reca ); }
	    	@Override
	    	public Reca getReca(){ return getNoteReca(); }
	    	@Override
	    	public Rec getRefRec(){ return getPtRec(); }
	    }
	    
	    HxNotes hxNotes = null;

    
	    
	    
	    
	    // Constructors
		
	    public HxFamilyItem() {
	    	allocateBuffer();
	    	this.hxNotes = new HxNotes();
	    }
		
	    public HxFamilyItem( Reca reca ){

	        // allocate space
	    	allocateBuffer();
	    	this.hxNotes = new HxNotes();
	    	
	    	// read record
	    	if ( Reca.isValid( reca )){
	    		this.read( reca );
	    	}
	    	
	    	// set local copy of reca
	    	this.reca = reca;
	    }


		// read record
	    
	    public void read( Reca reca ){
	    	
	    	hxNotes.resetReadStatus();				// haven't read note text record yet

			if ( ! Reca.isValid( reca )) return;		
			RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	   }	


	    
	    
	    public Reca writeNew(){
	    	
	    	Reca reca;		// reca
	    	
	    	int vol = Reca.todayVol();
	    	
	    	// handle note text
	    	hxNotes.write();
	    	
	    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "writeNew() bad reca" );

	    	return reca;
	    }
	    
	    
	    
	    public boolean write( Reca reca ){
	    	
	    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "write() bad reca" );

	    	// handle addlSig text
	    	hxNotes.write();
	    	
	    	// write record
	        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
	        return true;
	    }


	    
	    
	    public Reca postNew( Rec ptRec ){
	    	
			// new 
			Reca reca = LLPost.post( ptRec, this, 
					new LLPost.LLPost_Helper () {
						public Reca getReca( MedPt medPt ){ return medPt.getHxFamilyItemReca(); }
						public void setReca( MedPt medPt, Reca reca ){ medPt.setHxFamilyItemReca(reca); }
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


	    
	    
	    

		//24	byte	memberCode;		// member enum code
	    
	    /**
	     * getMember()
	     * 
	     * Get Member from dataStruct.
	     * 
	     * @param none
	     * @return Member
	     */
	    public Member getMember(){
	    	return Member.get( (char) dataStruct[24] );
	    }

	    /**
	     * setMember()
	     * 
	     * Set Member in dataStruct.
	     * 
	     * @param Member
	     * @return void
	     */
	    public void setMember( Member member ){
	    	if ( member == null ) member = Member.UNSPECIFIED;
	    	dataStruct[24] = (byte) (member.getCode() & 0xff);
	    	return ;
	    }


	    

	    
	    

		//20	Reca	noteReca;	// note
	    
	    /**
	     * getNoteReca()
	     * 
	     * Get note Reca from dataStruct.
	     * 
	     * @param none
	     * @return Reca
	     */
	    public Reca getNoteReca(){
	    	return Reca.fromReca( dataStruct, 20 );
	    }

	    /**
	     * setNoteReca()
	     * 
	     * Set note Reca record in dataStruct.
	     * 
	     * @param Reca
	     * @return void
	     */
	    public void setNoteReca( Reca reca ){
	    	reca.toReca( dataStruct, 20 );
	    }
	    
	    
	    
	    
	    

	    
	    
	    
		//16	Rec		probTblRec;		// link to ProbTbl
	    
	    /**
	     * getProbTblRec()
	     * 
	     * Get ProbTbl Rec from dataStruct.
	     * 
	     * @param none
	     * @return Rec
	     */
	    public Rec getProbTblRec(){
	    	return Rec.fromInt( dataStruct, 16 );
	    }

	    /**
	     * setProbTblReca()
	     * 
	     * Set ProbTbl record in dataStruct.
	     * 
	     * @param Rec
	     * @return void
	     */
	    public void setProbTblRec( Rec rec ){
	    	rec.toInt( dataStruct, 16 );
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
	    	return String.format( "%s %s %s", this.getMember().getLabel(), ProbTbl.getDesc( this.getProbTblRec()), this.getNoteText());
	    }


	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /**
	     * getNoteTxt()
	     * 
	     * Get note text 
	     * 
	     * @param none
	     * @return String
	     */
	    public String getNoteText(){ return hxNotes.getNoteText(); }
	    	
	    /**
	     * setNoteTxt()
	     * 
	     * Set note text 
	     * 
	     * @param String
	     * @return void
	     */
	    public void setNoteText( String txt ){ hxNotes.setNoteText( txt ); }
	    	
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /**
	     * Mark history item record removed
	     * 
	     * @param reca
	     * @return
	     */
	    public static boolean markRemoved( Reca reca ){	    	
	    	if ( ! Reca.isValid( reca )) { SystemHelpers.seriousError( "HxFamily.markRemoved() bad reca" ); return false; }
			HxFamilyItem fam = new HxFamilyItem( reca );
			fam.setStatus( usrlib.RecordStatus.REMOVED );
			fam.write( reca );
			return true;
	    }

	    /**
	     * Save new family history item record and link to patient.
	     * 
	     * @param ptRec
	     * @param member
	     * @param probTblRec
	     * @param note
	     * @return Reca
	     */
	    public static Reca postNew( Rec ptRec, Member member, Rec probTblRec, String note ){	    	
			// save item
			HxFamilyItem fam = new HxFamilyItem();
			fam.setPtRec( ptRec );
			fam.setDate( Date.today());
			fam.setMember( member );
			fam.setProbTblRec( probTblRec );
			fam.setNoteText( note );
			fam.setStatus( usrlib.RecordStatus.CURRENT );
			Reca reca = fam.postNew( ptRec );
			return reca;
	    }
    	

}
