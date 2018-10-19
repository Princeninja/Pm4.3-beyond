package palmed;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;


/*  document structure  */

//typedef struct {
//
//	LLHDR	hdr;			/*  history linked list header  */
//
//	RECORD	PtRec;			/*  patient record number  */
//	DATE	Date;			/*  date  */
//
//	char	desc[40];		/*  description  */
//	char	unused[20];		/*  unused space  */
//
//	long	doctypeID;		/*  document type id  */
//	long	problemID;		/*  problem id  */
//	long	visitID;		/*  visit id  */
//	RECA	documentID;		/*  document number (this recs reca)  */
//
//	char	unused1[3];		/*  unused field  */
//	byte	Status;			/*  validity byte  */
//
//	} DOC;

/*	sizecheck( 96, sizeof( DOC ), "DOC" );
*/



public class Docs implements LLItem {



	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    
    private final String fn_docs = "doc%02d.med";
    
    
    
    
	
    public Docs() {
    	allocateBuffer();
    }
	
    public Docs( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read current problem
    	if ( Reca.isValid( reca )){
    		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_docs, Docs.getRecordLength());
    	}
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read current problem
    
    public void read( Reca reca ){
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fn_docs, getRecordLength());
    }	


	
    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	int vol;		// volume
    	
    	vol = Reca.todayVol();
    	
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fn_docs, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Docs.writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Docs.write() bad reca" );

    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fn_docs, getRecordLength());
        return true;
    }


    

    public Reca postNew( Rec ptRec ){
    	
		// new vitals
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getDocReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setDocReca(reca); }
				}
		);
		return reca;
    }
    
    

    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( 96 ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[96];
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
     * Set sdate in dataStruct.
     * 
     * @param usrlib.Date		start date
     * @return void
     */
    public void setDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 12 );
        return;
    }


    
    
    
	// DATE AcquiredDate;			// scanned or acquired date, offset 56
    
    /**
     * getAcquiredDate()
     * 
     * Get acquired date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getAcquiredDate(){
    	return StructHelpers.getDate( dataStruct, 56 );
    }

    /**
     * setAcquiredDate()
     * 
     * Set acquired date in dataStruct.
     * 
     * @param usrlib.Date		acquired date
     * @return void
     */
    public void setAcquiredDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 56 );
    	return ;
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


    
    
    

	// char	desc[40];		// document description, offset 16
    
    /**
     * getDesc()
     * 
     * Get document description from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDesc(){
    	return StructHelpers.getString( dataStruct, 16, 40 ).trim();
    }

    /**
     * setDesc()
     * 
     * Set document description in dataStruct.
     * 
     * @param String		document description
     * @return void
     */
    public void setDesc( String s ){
    	StructHelpers.setStringPadded( s, dataStruct, 16, 40 );
    	return ;
    }


    
    
    

    
    
	// long	doctypeID;		// document type ID, offset 76
    
    /**
     * getDocTypeID()
     * 
     * Get document type ID from dataStruct.
     * 
     * @param none
     * @return DocTypes.DocTypeIDs
     */
    public DocTypes getDocTypeID(){
    	return DocTypes.get( StructHelpers.getInt( dataStruct, 76 ));
    }

    /**
     * setDocTypeID()
     * 
     * Set document type ID in dataStruct.
     * 
     * @param DocTypes.DocTypeIDs		document type ID
     * @return void
     */
    public void setDocTypeID( DocTypes id ){
    	StructHelpers.setInt( id.getCode(), dataStruct, 76);
    }
    
    
    
    
    
    
    
    

	// long	documentID;		// document ID (this Reca), offset 88
    
    /**
     * getDocumentID()
     * 
     * Get document ID (Reca) from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getDocumentID(){
    	return Reca.fromReca( dataStruct, 88 );
    }

    /**
     * setDocumentID()
     * 
     * Set document ID (Reca) in dataStruct.
     * 
     * @param Reca		provider record number
     * @return void
     */
    public void setDocumentID( Rec rec ){
    	reca.toReca( dataStruct, 88 );
    }


    
    
    


    
    
    



	// byte	Status;			// record status, offset 95
    
    /**
     * getStatus()
     * 
     * Get status from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getStatus(){
    	return (int) dataStruct[95];
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
    	dataStruct[95] = (byte)( 0xff & num );
    }


 
 //TODO validity byte (status above)   
    
    



    
    
    
    /**
     * toString()
     * 
     * Get a printable String representation of this entry.
     * 
     * @param none
     * @return void
     */
  
    public String toString(){
    	return String.format( "%s", this.getDesc());
    }

}
