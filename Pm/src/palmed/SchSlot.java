package palmed;

import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;



/*  a schedule slot  */

//typedef struct {
//
// 0	RECSH   aprec;                  /*  appointment record  */
// 2	RECSH   resource[4];            /*  resource scheduled  */
// 10	RECSH   provider[2];            /*  provider scheduled  */
// 14	unsigned short flags;           /*  flags  */
//
//	} SCHSLOT;
//
//      sizecheck( 16, sizeof( SCHSLOT ), "SCHSLOT" );




public class SchSlot {
	
	private static final int recordLength = 16;
	
	private Rec rec = null;
	private Rec maxrec = null;
	private RandomFile file = null;
	
	
    public byte[] dataStruct;
    
    
    

	
	//Constructors
	//
	
	public SchSlot(){
		super();
		allocateBuffer();
				this.rec = new Rec( 0 );
	}
	
/*	public SchSlot( Rec rec ){
		super();
		allocateBuffer();
		this.rec = new Rec( rec.getRec());
		read( rec );
	}
*/	
	
	
	
	
	
	
	
	
    private void allocateBuffer(){
        dataStruct = new byte[recordLength];
    }

    public void setDataStruct(byte[] dataStruct){
        this.dataStruct = dataStruct;
    }

    public byte[] getDataStruct(){
        return dataStruct;
    }

    /**
     * getRecordLength
     */
    public static int getRecordLength(){
        return ( recordLength );
    }

    
    
    
    
    
   // Rec aprec;			// appointment record, offset 0   
    
    /**
     * getApmtRec()
     * 
     * Get appointment record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getApmtRec(){
       	return Rec.fromUShort( dataStruct, 0 );
    }

    /**
     * setApmtRec()
     * 
     * Set appointment record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setApmtRec( Rec rec ){
        if ( Rec.isValid( rec )) rec.toUShort( dataStruct, 0 );
        return;
    }


    
    

    
    
    
    
    
    
	   // RECSH prov[2];			// get provider record, offset 10   
    
    /**
     * getProvRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProvRec( int num ){
       	return Rec.fromUShort( dataStruct, 10 + ( 2 * num));
    }

    public Rec getProvRec(){
    	return getProvRec( 0 );
    }
    
    
    /**
     * setProvRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setProvRec( int num, Rec rec ){
        if ( Rec.isValid( rec )) rec.toUShort( dataStruct, 10 + ( 2 * num));
        return;
    }
    
    public void setProvRec( Rec rec ){
    	setProvRec( 0, rec );
    }


    
    
    

    
   // RECSH resource[4];			// get provider record, offset 2   
    
    /**
     * getResRec()
     * 
     * Get resource record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getResRec( int num ){
       	return Rec.fromShort( dataStruct, 2 + ( 2 * num));
    }

    public Rec getResRec(){
    	return getResRec( 0 );
    }
    
    
    /**
     * setResRec()
     * 
     * Set resource record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setResRec( int num, Rec rec ){
        if ( Rec.isValid( rec )) rec.toShort( dataStruct, 2 + ( 2 * num));
        return;
    }
    
    public void setResRec( Rec rec ){
    	setResRec( 0, rec );
    }


    
    
    // unsigned short flags;			// flags, offset 14  
    
    /**
     * getFlags()
     * 
     * Get flags from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getFlags(){
       	
       	return StructHelpers.getUShort( dataStruct, 14 );
    }

    /**
     * setFlags()
     * 
     * Set flags in dataStruct.
     * 
     * @param int flags
     * @return void
     */
    public void setFlags( int flags ){
    	StructHelpers.setUShort( flags, dataStruct, 14 );
        return;
    }


    


 

    


}
