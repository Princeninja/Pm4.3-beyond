package palmed;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;



//typedef struct {
//
//	LLHDR	hdr;			/*  history linked list header  */
//
//	DATE	Start;			/*  start date  */
//	DATE	Stop;			/*  stop date  */
//	RECORD	patientID;		/*  patient record number  */
//
//	char	problem[40];		/*  problem text  */
//
//	long	dgnID;			/*  diagnosis code ID  */	// now probTblRec - to link to record in ProbTbl
//	long	providerID;		/*  treating doctor  */
//	long	rdocID;			/*  referring doctor  */
//	RECA	EditReca;		/*  previous version  */
//	char	unused;			/*  unused field  */
//	char	Type;			/*  type:  A-acute, C-chronic  */
//	byte	Status;			/*  record status  */
//	byte	unused1;			/*  unused byte  */
//
//	} PROB;
//
///*	sizecheck( 80, sizeof( PROB ), "PROB" );*/

/*  current problems structure 

note:  if Status == 0 then presence of a Stop date
differentiates past from current problems.

Status: 'C' - current problem
	'P' - past problem
	'R' - removed problem
	'E' - a record superceeded by an edit
	NUL - current or past problem
*/



// from the C header file palmed/curmed.h

public class Prob implements LLItem {






	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    private static String fn_prob = "prob%02d.med";
    private static int reclen = 80;
    
    
    
    
	
    public Prob() {
    	allocateBuffer();
    }
	
    public Prob( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read current problem
   		read( reca );
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read current problem
    
    public void read( Reca reca ){
    	
    	if (( reca == null ) || ( reca.getRec() < 2 )){ SystemHelpers.seriousError( "Prob.read() bad reca" ); return; }
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_prob, getRecordLength());
    }	

    
    // Write new problem
    public Reca writeNew(){
    	
    	Reca reca;		// prob reca
    	int vol;		// volume
    	
    	vol = Reca.todayVol();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fn_prob, getRecordLength());
    	if (( reca == null ) || ( reca.getRec() < 2 )) SystemHelpers.seriousError( "Prob.writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if (( reca == null ) || ( reca.getRec() < 2 )) SystemHelpers.seriousError( "Prob.write() bad reca" );

    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_prob, getRecordLength());
        return true;
    }


    

    
    
   public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getProbReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setProbReca(reca); }
				}
		);
		return reca;
    }
    
    
    

	



	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( reclen ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[reclen];
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


    
    
    
    // DATE Start;			// start date, offset 8    
    
    /**
     * getStartDate()
     * 
     * Get start date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getStartDate(){
       	return StructHelpers.getDate( dataStruct, 8 );
    }

    /**
     * setStartDate()
     * 
     * Set start date in dataStruct.
     * 
     * @param usrlib.Date		start date
     * @return void
     */
    public void setStartDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 8 );
        return;
    }


    
    
    
	// DATE Stop;			// stop date, offset 12
    
    /**
     * getStopDate()
     * 
     * Get stop date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getStopDate(){
    	return StructHelpers.getDate( dataStruct, 12 );
    }

    /**
     * setStopDate()
     * 
     * Set stop date in dataStruct.
     * 
     * @param usrlib.Date		stop date
     * @return void
     */
    public void setStopDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 12 );
    	return ;
    }


    
    
    
	// RECORD PtRec;		// patient record number, offset 16
    
    /**
     * getPtRec()
     * 
     * Get patient record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getPtRec(){
    	return Rec.fromInt( dataStruct, 16 );
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
    	ptRec.toInt( dataStruct, 16 );
    }


    
    
    

    
    /**
     * getProbDesc()
     * 
     * Get problem description.  If there is a value in DgnRec, read Dgn and return
     * its description.  If not, return the text in the misc desc field problem[].
     * 
     * @param none
     * @return String
     */
    public String getProbDesc(){
    	
    	String s = "";
    	Rec probTblRec = getProbTblRec();
    	
    	if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )){
    		s = getMiscDesc();
    	} else {
    		return ProbTbl.getDesc( probTblRec );
    	}
    	return s;
    }

    
    

    
    /**
     * getCode()
     * 
     * Get problem code.  If there is a value in ProbTblRec (DgnRec), read ProbTbl and return
     * its code.  If not, return empty string (un-coded).
     * 
     * @param none
     * @return String
     */
    public String getCode(){
    	
    	String s = "";
    	Rec probTblRec = getProbTblRec();
    	
    	if (( probTblRec != null ) && ( probTblRec.getRec() > 1 )){
    		s = ProbTbl.getICD9( probTblRec );
    	}
    	return s;
    }


    
    
    
   
    
	// char	problem[40];		// problem description, offset 20

    /**
     * getMiscDesc()
     * 
     * Get problem misc description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getMiscDesc(){
    	return StructHelpers.getString( dataStruct, 20, 40 ).trim();
    }

    /**
     * setMiscDesc()
     * 
     * Set problem misc description in dataStruct.
     * 
     * @param String		desc
     * @return void
     */
    public void setMiscDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 20, 40 );
    	return ;
    }


    
    
    

    
    
    
	// long	probTblRec;		// ProbTbl entry record number, offset 60
    
    /**
     * getProbTblRec()
     * 
     * Get probTbl record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProbTblRec(){
    	return Rec.fromInt( dataStruct, 60 );
    }

    /**
     * setProbTblRec()
     * 
     * Set ProbTbl record in dataStruct.
     * 
     * @param Rec		diagnosis record number
     * @return void
     */
    public void setProbTblRec( Rec rec ){
    	rec.toInt( dataStruct, 60 );
    }
    
    
    
    
    
    

	// long	providerID;		// prescribing doctor, offset 64
    
    /**
     * getProviderRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProviderRec(){
    	return Rec.fromInt( dataStruct, 64 );
    }

    /**
     * setProviderRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec		provider record number
     * @return void
     */
    public void setProviderRec( Rec rec ){
    	rec.toInt( dataStruct, 64 );
    }


    
    
    


	// long	rdocID;			// prescribing rdoc, offset 68
    
    /**
     * getRdocRec()
     * 
     * Get Rdoc record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getRdocRec(){
    	return Rec.fromInt( dataStruct, 68 );
    }

    /**
     * setRdocRec()
     * 
     * Set Rdoc record in dataStruct.
     * 
     * @param Rec		Rdoc record number
     * @return void
     */
    public void setRdocRec( Rec rec ){
    	rec.toInt( dataStruct, 68);
    }
    

    
    
    


	// RECA EditReca;		// previous version, offset 72
    
    /**
     * getEditReca()
     * 
     * Get Edit reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getEditReca(){
    	return Reca.fromReca( dataStruct, 72 );
    }

    /**
     * setEditReca()
     * 
     * Set Edit reca in dataStruct.
     * 
     * @param Reca		Visit reca
     * @return void
     */
    public void setEditReca( Reca reca ){
    	reca.toReca( dataStruct, 72 );
    }

    
    
    
    
    

	// char	unused;		// unused field, offset 76
    
    
    
    
    


	// char	Type;		// type of problem, offset 77
    
    /**
     * getType()
     * 
     * Get Type (acute vs chronic) from dataStruct.
     * 
     * @param none
     * @return String
     */
    public byte getType(){
    	return dataStruct[77];
    }

    /**
     * setType()
     * 
     * Set Type (acute vs chronic) in dataStruct.
     * 
     * @param String		type
     * @return void
     */
    public void setType( byte c ){
    	dataStruct[77] = (byte) (c & 0xff);
    	return ;
    }


    
    
    



	// char	status;			// record status, offset 78
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getStatus(){
    	return (int)( dataStruct[78] & 0xff );
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
    	dataStruct[78] = (byte)( 0xff & num );
    }


    
    
    

	// char	unused1;		// unused field, offset 79
    
    
    
    
    
    /**
     * markResolved()
     * 
     * Mark this problem as resolved. (Set status to 'P' for 'Past'.)
     * 
     * @param reca
     * @param date
     * @return boolean
     */
    public static boolean markResolved( Reca reca, usrlib.Date date ){
 
    	// make sure valid reca
    	if (( reca == null ) || ( reca.getRec() < 2 )) { SystemHelpers.seriousError( "Prob.markResolved() bad reca" ); return false; }

    	// update and write record
        Prob p = new Prob( reca );
        p.setStatus( 'P' );
        p.setStopDate( date );
        p.write(reca);
       return true;
    }



    
    
    
    /**
     * markRemoved()
     * 
     * Mark this problem as removed. (Set status to 'R' for 'Removed'.)
     * 
     * @param reca
     * @param date
     * @param exp
     * @return boolean
     */
    public static boolean markRemoved( Reca reca, usrlib.Date date, String exp ){
 
    	// make sure valid reca
    	if (( reca == null ) || ( reca.getRec() < 2 )) { SystemHelpers.seriousError( "Prob.markRemoved() bad reca" ); return false; }

    	// update record status and stop date
        Prob p = new Prob( reca );
        p.setStatus( 'R' );
        p.setStopDate( date );
        
        // Save explanation
        if (( exp != null ) && ( exp.length() > 0 )){
            // TODO - save explanation
        	;
        }
        
        // write record
        p.write(reca);

        return true;
    }

    public static boolean markRemoved( Reca reca, usrlib.Date date ){ return markRemoved( reca, date, null ); }
    


    
    
    
    /**
     * toString()
     * 
     * Get a printable String representation of this entry.
     * 
     * @param none
     * @return void
     */
  
    public String toString(){
    	return String.format( "%s", this.getProbDesc());
    }

}


/**/

