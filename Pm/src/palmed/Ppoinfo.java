package palmed;

	/* PPO practice specific info  

	//typedef struct {

	//RECORD	srvfee;		/*  service fee schedules  
	//RECORD	dgnfee;		/*  diagnosis fee schedules  
	//RECORD	form[2];	/*  insurance forms  

	//USRT	flags;		/*  primary, secondary flags  

	//byte    EtsBatch;       /*  flag to batch file ETS  
	//char    InsCmpID[5];    /*  insurance company ID (ETS)  
	//char    InsCmpSubID[4]; /*  insurance company sub ID (ETS)  
	//char    EtsRecCode[2];  /*  ETS record code  

	//DOLLAR	HmoCopayAmount;	/*  copay dollar amount  
	//DOLLAR	HmoNoPmtSurcharge;/*  surcharge for not paying copay at visit 
	//USRT	HmoCopayPercent;/*  copay percent  

	//char	NEIC[5];	/*  carrier's NEIC number 
	//char	NEICsubID[3];	/*  carrier's NEIC sub ID number  
	//char	MediGAP[6];	/*  MediGAP number 

	//char	unused;		// unused field
	//byte	ProvNum4;	// provider number for BA0.14 NSF field

	//char	NEICsubIDx;	// last char needed on NEIC sub ID field above
	//byte	ProvNum3;	// provider number to use per line item
	//byte	ProvNum2;	// provider number to use 
	//byte	SourceOfPmt;	// NSF Source of Payment indicator
	//byte	PosSet;		// place of service set for ecs
	//byte	ProvNum;	/*  which provider number to use  
	//byte	PlanType;	/*  plan type (G-MediGAP)  
	//byte	valid;		/*  validity byte  

	//} PPOINFO;

	///*sizecheck( 64, sizeof( PPOINFO ), "PPOINFO" ); */

import usrlib.*;

public class Ppoinfo {

		// fields
		private byte[] dataStruct;
		private Rec ppoRec = null;      // this Rec (same as that of Ppo)
		
		private final static String fn_ppoinfo = "ppoinfo.med";
		private final static int recLength = 64;   // record Length
		
		private RandomFile file = null;
		
		private Rec maxrec = null;
		
 		enum Status {
 			ACTIVE, INACTIVE, REMOVED
 		}
		
 		public Ppoinfo() {
 			
 			allocateBuffer();	
 		}

 		
 		public Ppoinfo( Rec rec ){
 			
 			// allocate space
 			allocateBuffer();
 			
 			// read ppoinfo record
 			read( rec );
 			
 			//set local copy of the rec
 			this.ppoRec = rec;				
 		}

 		// read ppoinfo
 		
 		public void read( Rec rec ){
 			
 			if (( rec == null ) || (rec.getRec() < 2 )){ SystemHelpers.seriousError( "Ppoinfo.read() bad rec" ); return;}
 			RandomFile.readRec(rec, dataStruct, Pm.getMedPath(), fn_ppoinfo, getRecordLength());
 		}

 		
 		public Rec writeNew(){
 			
 			Rec rec;    // ppoinfo rec
 			
 			rec = RandomFile.writeRec(null, this.dataStruct, Pm.getMedPath(), fn_ppoinfo, getRecordLength());
 			if (( rec == null ) || (rec.getRec() < 2 )) SystemHelpers.seriousError( "Ppoinfo.writeNew() bad rec" );
 			
 			return rec;
 		}

 		public boolean write(){
 			return write( ppoRec );
 		}

 		public boolean write( Rec rec ){
 			
 			if (( rec == null ) || (rec.getRec() < 2 )) SystemHelpers.seriousError( "Ppoinfo.write() bad rec" );
 			
 			// write record
 			if ( file != null){
 				file.putRecord(dataStruct,  ppoRec ); 
 			} else {
 				RandomFile.writeRec(rec, this.dataStruct, Pm.getMedPath(), fn_ppoinfo, getRecordLength());
 			}
 			return true;
 		}
 		
 		
 		/**
 		 * getRecordLength 
 		 */
 		
 		public static int getRecordLength() { return ( recLength ); } 
 		
 		private void allocateBuffer(){
 			//allocate data structure space
 			dataStruct = new byte[getRecordLength()];
 		}
 		
 		public void setDataStruct(byte[] dataStruct) {this.dataStruct = dataStruct; }
 		
 		public byte[] getDataStruct() { return dataStruct; }
 		
 		
 		
 		
 		//RECORD	srvfee;		/*  service fee schedules  
 		//RECORD	dgnfee;		/*  diagnosis fee schedules  
 		//RECORD	form[2];	/*  insurance forms  
 		

 		//USRT	flags;		/*  primary, secondary flags , offset 16
 		/**
 		 * getFlags()
 		 * 
 		 * Get code flags from dataStruct
 		 * 
 		 * @param none
 		 * @return int
 		 */
 		public int getFlags(){
 			return StructHelpers.getShort(dataStruct, 16 );
 		}
 		
 		/**
 		 * setFlags()
 		 * 
 		 * Set code flags in dataStruct
 		 * 
 		 * @param flags - code flags
 		 * @return void
 		 */
 		public void setFlags( int flags ){
 			StructHelpers.setShort( flags, dataStruct, 16);
 		}

 		//byte    EtsBatch;       /*  flag to batch file ETS  
 		
 		
 		//char    InsCmpID[5];    /*  insurance company ID (ETS) 
 		
 		/**
 		 * getInsCmpID()
 		 * 
 		 * Get InsCmpID from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getInsCmpID(){
 			return StructHelpers.getString(dataStruct, 19, 5).trim();
 		}
 		
 		/**
 		 * setInsCmpID()
 		 * 
 		 * Set the InsCmpID in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setInsCmpID( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 19, 5);
 			return;
 		}
 		
 		//char    InsCmpSubID[4]; /*  insurance company sub ID (ETS) 
 		
 		/**
 		 * getInsCmpSubID()
 		 * 
 		 * Get InsCmpSubID from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getInsCmpSubID(){
 			return StructHelpers.getString(dataStruct, 24, 4).trim();
 		}
 		
 		/**
 		 * setInsCmpSubID()
 		 * 
 		 * Set the InsCmpSubID in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setInsCmpSubID( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 24, 4);
 			return;
 		}
 		
 		//char    EtsRecCode[2];  /*  ETS record code  
 		
 		/**
 		 * getEtsRecCode()
 		 * 
 		 * Get EtsRecCode from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getEtsRecCode(){
 			return StructHelpers.getString(dataStruct, 28, 2).trim();
 		}
 		
 		/**
 		 * setEtsRecCode()
 		 * 
 		 * Set the EtsRecCode in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setEtsRecCode( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 28, 2);
 			return;
 		}
 		
 		
 		//DOLLAR	HmoCopayAmount;	/*  copay dollar amount  
 		
 		 /**
 	     * getHmoCopayAmount()
 	     * 
 	     * Get the HmoCopayAmount
 	     * 
 	     * @param none
 	     * @return Dollar
 	     */

 	    public Dollar getHmoCopayAmount(){
 	    	return new Dollar (dataStruct, 30);
 	    }
 	    
 	    /**
 	     * setHmoCopayAmount()
 	     * 
 	     * Set the Dollar HmoCopayAmount in dataStruct.
 	     * 
 	     * @param HmoCopayAmount - String HmoCopayAmount
 	     * @return none
 	     */
 	    public void setHmoCopayAmount( String HmoCopayAmount ){
 	    	Dollar dollar = new Dollar(HmoCopayAmount);
 	    	dollar.toBCD( dataStruct, 30 );
 	    } 
 	    
 		//DOLLAR	HmoNoPmtSurcharge;/*  surcharge for not paying copay at visit
 	    
 	   /**
 	     * HmoNoPmtSurcharge()
 	     * 
 	     * Get the HmoNoPmtSurcharge
 	     * 
 	     * @param none
 	     * @return Dollar
 	     */

 	    public Dollar getHmoNoPmtSurcharge(){
 	    	return new Dollar (dataStruct, 34);
 	    }
 	    
 	    /**
 	     * setHmoNoPmtSurcharge()
 	     * 
 	     * Set the Dollar HmoNoPmtSurcharge in dataStruct.
 	     * 
 	     * @param HmoNoPmtSurcharge - String HmoNoPmtSurcharge
 	     * @return none
 	     */
 	    public void setHmoNoPmtSurcharge( String HmoNoPmtSurcharge ){
 	    	Dollar dollar = new Dollar(HmoNoPmtSurcharge);
 	    	dollar.toBCD( dataStruct, 34 );
 	    } 
 	    
 		//USRT	HmoCopayPercent;/*  copay percent  
 	    
 	   /**
 		 * getHmoCopayPercent()
 		 * 
 		 * Get HmoCopayPercent from dataStruct
 		 * 
 		 * @param none
 		 * @return int
 		 */
 		public int getHmoCopayPercent(){
 			return StructHelpers.getShort(dataStruct, 38 );
 		}
 		
 		/**
 		 * setHmoCopayPercent()
 		 * 
 		 * Set HmoCopayPercent in dataStruct
 		 * 
 		 * @param HmoCopayPercent 
 		 * @return void
 		 */
 		public void setHmoCopayPercent( int HmoCopayPercent ){
 			StructHelpers.setShort( HmoCopayPercent, dataStruct, 38 );
 		}
 		
 	    //char	NEIC[5];	/*  carrier's NEIC number 

 		/**
 		 * getNEIC()
 		 * 
 		 * Get NEIC from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getNEIC(){
 			return StructHelpers.getString(dataStruct, 40, 5).trim();
 		}
 		
 		/**
 		 * setNEIC()
 		 * 
 		 * Set the NEIC in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setNEIC( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 40, 5);
 			return;
 		}
 		
 	    //char	NEICsubID[3];	/*  carrier's NEIC sub ID number  
 		
 		/**
 		 * getNEICsubID()
 		 * 
 		 * Get NEICsubID from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getNEICsubID(){
 			return StructHelpers.getString(dataStruct, 45, 3).trim();
 		}
 		
 		/**
 		 * setNEICsubID()
 		 * 
 		 * Set the NEICsubID in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setNEICsubID( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 45, 3);
 			return;
 		}
 		
 	    //char	MediGAP[6];	/*  MediGAP number 

 		/**
 		 * getMediGAP()
 		 * 
 		 * Get MediGAP from dataStruct
 		 * 
 		 * @return String
 		 */
 		public String getMediGAP(){
 			return StructHelpers.getString(dataStruct, 48, 6).trim();
 		}
 		
 		/**
 		 * setMediGAP()
 		 * 
 		 * Set the MediGAP in dataStruct
 		 * 
 		 * @param s
 		 * @return void
 		 */
 		public void setMediGAP( String s){
 			StructHelpers.setStringPadded(s, dataStruct, 48, 6);
 			return;
 		}
 		
 	    //char	unused;		// unused field
 		//byte	ProvNum4;	// provider number for BA0.14 NSF field

 		//char	NEICsubIDx;	// last char needed on NEIC sub ID field above
 	    
 		//byte	ProvNum3;	// provider number to use per line item
 		//byte	ProvNum2;	// provider number to use 
 		//byte	SourceOfPmt;	// NSF Source of Payment indicator
 		//byte	PosSet;		// place of service set for ecs
 		//byte	ProvNum;	/*  which provider number to use  
 		//byte	PlanType;	/*  plan type (G-MediGAP)  
 		
 	    //byte	valid;		/*  validity byte  offset 63
 		
 		/**
 	     * getValid()
 	     * 
 	     * Get validity byte from dataStruct.
 	     * 
 	     * @param none
 	     * @return int
 	     */
 	    public int getValid(){
 	    	return (int) dataStruct[63];
 	    }

 	    /**
 	     * setValid()
 	     * 
 	     * Set validity byte in dataStruct.
 	     * 
 	     * @param int		'valid'
 	     * @return void
 	     */
 	    public void setValid( int valid ){
 	    	dataStruct[63] = (byte)( 0xff & valid );
 	    }


 	    /**
 	     * isValid  
 	     * */
 	    public boolean isValid() { return ( Validity.get( (int) dataStruct[63] ) == Validity.VALID ); }

 	    /**
 	     * setValid()
 	     * 
 	     *  */
 	    public void setValid(){ dataStruct[63] = (byte)( Validity.VALID.getCode() & 0xff ); }
 	    
 	    /**
 	     * setHidden()
 	     * 
 	     * */
 	    public void setHidden(){ dataStruct[63] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
 	    
 	    /**
 	     *isHidden() 
 	     */
 	    public boolean isHidden() { return ( Validity.get( (int) dataStruct[63] ) == Validity.HIDDEN ); }
 	    
 	     /**
 	      * getValidityByte()
 	      * 
 	      */
 	    public Validity getValidity(){ return Validity.get( (int) dataStruct[63] ); }
 	    
 	    /**
 	     * setValidityByte()
 	     */
 	    public void setValidity( Validity valid ){ dataStruct[63] = (byte)( valid.getCode() & 0xff ); }
 	    
 		
 	    
 	    
 	    public void dump(){
 			StructHelpers.dump( "Ppoinfo", dataStruct, getRecordLength());
 			return; 			
 		}
 		
 	    
 		/**
 		 * toString()
 		 * 
 		 * Get a printable String representation of this entry
 		 * 
 		 * @param none
 		 * @return String
 		 */
 		
 	     public String toString(){
 	    	 return String.format( "%s %s %s %s %s %s ", this.getInsCmpID(),this.getInsCmpSubID(),this.getEtsRecCode(),this.getNEIC(),this.getNEICsubID(),this.getMediGAP());
 	     }
 	    
 		
 	     
 	     //open
 	     public static Ppoinfo open(){
 	    	 
 	    	 // create new Ppoinfo object
 	    	 Ppoinfo ppoinfo = new Ppoinfo();
 	    	 
 	    	 //open file, read maxrec, and set current rec to null 
 	    	 ppoinfo.file = RandomFile.open( Pm.getMedPath() , fn_ppoinfo, getRecordLength(), RandomFile.Mode.READWRITE );
 	    	 ppoinfo.maxrec =  new Rec( ppoinfo.file.getMaxRecord());
 	    	 ppoinfo.ppoRec = null;
 	    	 
 	    	 return ppoinfo;
 	     }
 		
 		
 		// getNext()
 	    public boolean getNext(){
 	    	
 	    	// is file open?
 	    	if ( file == null ) SystemHelpers.seriousError( "Ppoinfo.getNext() file not open" );
 	    	
 	    	// set initial record number
 	    	if ( ppoRec == null ){
 	    		ppoRec = new Rec( 2 );
 	      	}else if ( ppoRec.getRec() < 2 ){
 	      		ppoRec.setRec( 2 );
 	      		
 	      		//else, increment record number
 	      	} else {
 	      		ppoRec.increment();
 	      	}
 	    	
 	    	// is this past maxrec?
 	    	if ( ppoRec.getRec() > maxrec.getRec()) return false;
 	    	
 	    	// read record
 	    	file.getRecord( dataStruct, ppoRec );
 	    	return true;
 	    }
 		
 	    
 	    public Rec getRec(){
 	    	return new Rec( ppoRec.getRec());
 	    }
 	    
 	    public void setRec( Rec rec ){ ppoRec = rec;}
 	    
 	    // close()
 	    public boolean close(){
 	    	if ( file != null ) file.close();
 	    	file = null;
 	    	ppoRec = null;
 	    	maxrec = null;
 	    	return true;
 	    }
 	
 	    
 	    
}


