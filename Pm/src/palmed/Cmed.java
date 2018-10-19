package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RecaFile;
import usrlib.StructHelpers;


// from the C header file palmed/curmed.h


/*  current medication structure  */

//typedef struct {
//
//0		LLHDR	hdr;			/*  history linked list header  */
//
//8		DATE	Start;			/*  med start date  */
//12	DATE	Stop;			/*  med stop date  */
//16	RECORD	PtRec;			/*  patient record number  */
//
//20	char	dname[40];		/*  drug name  */
//60	char	route[2];		/*  route of administration  */
//62	char	sched[4];		/*  administration schedule  */
//66	char	Dosage[10];		/*  prescribed dosage  */
//
//76	DATE	LastRefillDate;		/*  last refill date  */
//80	RECA	LabelTextReca;		/*  Addl SIG text Reca, (was label text record number)  */
//84	RECA	FromMed;		/*  continued from med record number  */
//
//88	long	providerID;		/*  prescribing doctor  */
//92	long	rdocID;			/*  prescribing rdoc  */
//96	long	probID;			/*  problem prescribed for  */
//
//100	short	Number;			/*  number of doses  */
//102	short	Refills;		/*  number of Refills  */
//
//104	long	visitID;		/*  visit id  */
//108	long	drugCode;		// NEW - drug code - FDB (8 digit numeric)
//112	short	routeCode;		// NEW - route of administration code
//114	short	freqCode;		// NEW - frequency code
//116	short	formCode;		// NEW - dosage form code (ie tablet, capsule, etc)
//118	short	dosageCode;		// NEW - dosage number code
//
//120	char	deaClass;		// NEW - dea class code
//121	char	flgAcute;		// NEW - flag this as an acute med (not chronic)
//122	char	flgDAW;			// NEW - flag to 'dispense as written'
//123	char	flgPRN;			// NEW - flag this as a PRN med
//124	char	status;			/*  status - c_MED_STATUS_xxxxx  */
//125	char	checked;		// S-sent to Newcrop, P-pulled back (likely paper/fax), E-sent electronically
//
//126	byte	Removed;		/*  removed reason  */
//127	byte	flgMisc;		/*  NEW - flag that this is a misc med (not coded), values 'Y' or 'N'
//								//        (also used to indicate new style med - a Y or N value indicates new style med  */
//
//	} CMED;

/*	sizecheck( 128, sizeof( CMED ), "CMED" );
*/




//TODO - handle additional sig text



public class Cmed implements LLItem {

	
	// fields
    private byte[] dataStruct;
    private Reca reca;			// this Reca
    private String addlSig = null;		// additional SIG text
    private boolean flgAddlSig = false;	// flag that addlSig text has been set
    private boolean flgAddlSigRead = false;	// flag that addlSig text has been read
    
	public static final String fnsub = "cmed%02d.med";
	public static final int recordLength = 128;
	
	
	public enum Status {
		
		CURRENT( "Current", 'C' ),	
		DISCONTINUED( "Discontinued", 'P' ),
		REMOVED( "Removed", 'R' ),
		CHANGED( "Changed", 'D' );
		
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

    
    
	
    public Cmed() {
    	allocateBuffer();
    }
	
    public Cmed( Reca reca ){

        // allocate space
    	allocateBuffer();
    	
    	// read current med
    	if ( Reca.isValid( reca ))
    		this.read( reca );
    	
    	// set local copy of reca
    	this.reca = reca;
    }


	// read current med
    
    public void read( Reca reca ){

    	flgAddlSigRead = false;				// haven't read addlSig text record yet
    	
		if ( ! Reca.isValid( reca )) return;		
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    }	


    public Reca writeNew(){
    	
    	Reca reca;		// reca
    	int vol;		// volume

    	// set volume based on current year;
    	vol = Reca.todayVol();
    	
    	// handle addlSig text
    	if ( flgAddlSig ){
    		Reca addlSigReca = this.writeAddlSigText( addlSig );
    		this.setLabelReca( addlSigReca );
    		flgAddlSig = false;
    	}
 
    	// write new cmed record
    	reca = RecaFile.newReca( vol, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Cmed.writeNew() bad reca" );

    	return reca;
    }
    
    
    
    public boolean write( Reca reca ){
    	
    	if ( ! Reca.isValid( reca )) SystemHelpers.seriousError( "Cmed.write() bad reca" );

    	// handle addlSig text
    	if ( flgAddlSig ){
    		Reca addlSigReca = this.writeAddlSigText( addlSig );
    		this.setLabelReca( addlSigReca );
    		flgAddlSig = false;
    	}
 
    	// write record
        RecaFile.writeReca( reca, this.dataStruct, Pm.getMedPath(), fnsub, getRecordLength());
        return true;
    }


    
    
    public Reca postNew( Rec ptRec ){
    	
		// new 
		Reca reca = LLPost.post( ptRec, this, 
				new LLPost.LLPost_Helper () {
					public Reca getReca( MedPt medPt ){ return medPt.getMedsReca(); }
					public void setReca( MedPt medPt, Reca reca ){ medPt.setMedsReca(reca); }
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
     * Get med start date from dataStruct.
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
     * Set med start date in dataStruct.
     * 
     * @param usrlib.Date		med start date
     * @return void
     */
    public void setStartDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 8 );
        return;
    }


    
    
    
	// DATE Stop;			// med stop date, offset 12
    
    /**
     * getStopDate()
     * 
     * Get med stop date from dataStruct.
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
     * Set med stop date in dataStruct.
     * 
     * @param usrlib.Date		med stop date
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


    
    
    

	// char	dname[40];		// drug name, offset 20
    
    /**
     * getDrugName()
     * 
     * Get drug name from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDrugName(){
    	if ( this.isNewStyle()){   		
    		return StructHelpers.getString( dataStruct, 20, 40 ).trim();
    	} else {
    		// if old style, include med strength (old dosage) in the name
    		return ( StructHelpers.getString( dataStruct, 20, 40 ).trim() + " " + StructHelpers.getString( dataStruct, 66, 10 ).trim()).trim();
    	}
    }

    /**
     * setDrugName()
     * 
     * Set drug name in dataStruct.
     * 
     * @param String		drug name
     * @return void
     */
    public void setDrugName( String name ){
    	StructHelpers.setStringPadded(name, dataStruct, 20, 40 );
    	return ;
    }


    
    
    

	// char	route[2];		// OLD - route of administration, offset 60
    // short route;			// NEW - route of administration code, offset 112
    /**
     * getRoute()
     * 
     * Get route from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getRoute(){
    	if ( this.isNewStyle()){
    		return StructHelpers.getUShort( dataStruct, 112 );
    	} else {
    		// for old style meds, get route code by decoding older MediSpan route text
    		return MedRoute.fromMediSpan( StructHelpers.getString( dataStruct, 60, 2 ).trim());
    	}
    }

    /**
     * setRoute()
     * 
     * Set drug name in dataStruct.
     * 
     * @param int routeCode
     * @return void
     */
    public void setRoute( int route ){
    	// set new style CodifiedSig route code in data struct
    	StructHelpers.setShort( route, dataStruct, 112 );   	
    	// for now also set the old-style MediSpan route in the text fields - just to support old char based
    	StructHelpers.setStringPadded( MedRoute.toMediSpan( route ), dataStruct, 60, 2 );
    	return ;
    }


    
    
    


	// char	sched[4];		// OLD - administration schedule, offset 62
    // short freqCode;		// NEW - frequency, offset 114
    
    /**
     * getSched()
     * 
     * Get schedule from dataStruct.
     * 
     * @param none
     * @return String
     */
    public int getSched(){
    	if ( this.isNewStyle()){   		
    		return StructHelpers.getUShort( dataStruct, 114 );
    	} else {
    		// for old style meds, get Sched/Freq code by decoding old schedule text field
    		return MedFreq.fromText( StructHelpers.getString( dataStruct, 62, 4 ).trim());
    	}
    }

    /**
     * setSched()
     * 
     * Set frequency code in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setSched( int freq ){
    	StructHelpers.setShort( freq, dataStruct, 114 );
    	return ;
    }


    
    
    


	// char	Dosage[10];		// prescribed dosage, offset 66

    /**
     * getDosage()
     * 
     * Get dosage from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getDosage(){
    	//return ;
    	if ( isNewStyle()){
    		return StructHelpers.getUShort(dataStruct, 118 );
    	} else {
    		return MedDosage.parseFromOldDrugName( StructHelpers.getString( dataStruct, 20, 40 ).trim());
    	}
    }

    /**
     * setDosage()
     * 
     * Set dosage in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setDosage( int dosageCode ){
    	// set dosageCode in new style 
    	StructHelpers.setUShort( dosageCode, dataStruct, 118 );
    	// also set dosageCode as text in old style Dosage field to support char based system
    	StructHelpers.setStringPadded( MedDosage.getDesc( dosageCode), dataStruct, 66, 10 );
    	return ;
    }


    
    
    
    // short formCode;			// NEW - dosage form code, offset 116
    /**
     * getForm()
     * 
     * Get dosage form code from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getForm(){
    	if ( this.isNewStyle()){
    		return StructHelpers.getUShort( dataStruct, 116 );
    	} else {
    		// for old style meds, return 0
    		return 0;
    	}
    }

    /**
     * setForm()
     * 
     * Set dosage form code in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setForm( int form ){
    	// set new style CodifiedSig dosage form code in data struct
    	StructHelpers.setShort( form, dataStruct, 116 );   	
    	return ;
    }


    
    
    



	// DATE	LastRefillDate;	// last refill date, offset 76
    
    /**
     * getLastRefillDate()
     * 
     * Get last refill date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getLastRefillDate(){
    	return StructHelpers.getDate( dataStruct, 76 );
    }

    /**
     * setLastRefillDate()
     * 
     * Set last refill date in dataStruct.
     * 
     * @param usrlib.Date last refill date
     * @return void
     */
    public void setLastRefillDate( usrlib.Date date ){
    	StructHelpers.setDate( date, dataStruct, 76 );
    	return ;
    }


    
    
    


	// RECA	LabelTextReca;	// additional Sig text (was label text) record number, offset 80
    
    /**
     * getAddlSigReca()
     * 
     * Get additional sig text reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getLabelReca(){
    	return Reca.fromReca( dataStruct, 80 );
    }

    /**
     * setAddlSigReca()
     * 
     * Set additional sig text reca in dataStruct.
     * 
     * @param Reca		additional sig text recorda
     * @return void
     */
    public void setLabelReca( Reca reca ){
    	reca.toReca( dataStruct, 80 );
    }


    
    
    


	// RECA	FromMed;		// continued from med record number, offset 84

    /**
     * getFromMedReca()
     * 
     * Get 'from med' reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getFromMedReca(){
    	return Reca.fromReca( dataStruct, 84 );
    }

    /**
     * setFromMedReca()
     * 
     * Set 'from med' reca in dataStruct.
     * 
     * @param Reca		'from med' reca
     * @return void
     */
    public void setFromMedReca( Reca reca ){
    	reca.toReca( dataStruct, 84 );
    }


    
    
    


	// long	providerID;		// prescribing doctor, offset 88
    
    /**
     * getProviderRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProviderRec(){
    	return Rec.fromInt( dataStruct, 88 );
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
    	rec.toInt( dataStruct, 88 );
    }


    
    
    


	// long	rdocID;			// prescribing rdoc, offset 92
    
    /**
     * getRdocRec()
     * 
     * Get Rdoc record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getRdocRec(){
    	return Rec.fromInt( dataStruct, 92 );
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
    	rec.toInt( dataStruct, 92 );
    }


    
    
    


	// long	probID;			// problem prescribed for, offset 96
    
    /**
     * getProbReca()
     * 
     * Get problem reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getProbReca(){
    	return Reca.fromReca( dataStruct, 96 );
    }

    /**
     * setProbReca()
     * 
     * Set pproblem reca in dataStruct.
     * 
     * @param Reca		prob reca
     * @return void
     */
    public void setProbReca( Reca reca ){
    	reca.toReca( dataStruct, 96 );
    }


    
    
    


	// short	Number;		// number of doses, offset 100
    
    /**
     * getNumber()
     * 
     * Get 'number to fill' from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getNumber(){
    	return StructHelpers.getUShort( dataStruct, 100 );
    }

    /**
     * setNumber()
     * 
     * Set 'number to fill' in dataStruct.
     * 
     * @param int		'number to fill'
     * @return void
     */
    public void setNumber( int num ){
    	StructHelpers.setUShort( num, dataStruct, 100 );
    }


    
    
    


	// short	Refills;	// number of Refills, offset 102

    /**
     * getRefills()
     * 
     * Get 'Refills' from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getRefills(){
    	return StructHelpers.getUShort( dataStruct, 102 );
    }

    /**
     * setRefills()
     * 
     * Set 'Refills' in dataStruct.
     * 
     * @param int		'Refills'
     * @return void
     */
    public void setRefills( int num ){
    	StructHelpers.setUShort( num, dataStruct, 102 );
    }


    
    
    


	// long	visitID;		// visit id, offset 104
    
    /**
     * getVisitReca()
     * 
     * Get Visit reca from dataStruct.
     * 
     * @param none
     * @return Reca
     */
    public Reca getVisitReca(){
    	return Reca.fromReca( dataStruct, 104 );
    }

    /**
     * setVisitReca()
     * 
     * Set Visit reca in dataStruct.
     * 
     * @param Reca		Visit reca
     * @return void
     */
    public void setVisitReca( Reca reca ){
    	reca.toReca( dataStruct, 104 );
    }


    
    
	// long	drugCode;		// NEW - FDB drug code, offset 108
    
    /**
     * getDrugCode()
     * 
     * Get drug code from dataStruct.
     * 
     * @param none
     * @return String
     */
    public String getDrugCode(){
    	int code = StructHelpers.getLong32( dataStruct, 108 );
    	return ( code == 0 ) ? "": String.valueOf( code );
    }

    /**
     * setDrugCode()
     * 
     * Set drug code in dataStruct.
     * 
     * @param String		drug code
     * @return void
     */
    public void setDrugCode( String s ){
    	int code = EditHelpers.parseInt( s );
    	StructHelpers.setLong32( code, dataStruct, 108 );
    	return;
    }


    
    
    





	// char	unused[16];		// unused field, offset 108
    
    
    
    
    
	// char	flgAcute;		// NEW - flag this as an acute (not chronic) med, offset 121
    
    /**
     * getAcute()
     * 
     * Get med Acute flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getAcute(){
    	return ( dataStruct[121] == 'Y' );
    }

    /**
     * setAcute()
     * 
     * Set Acute flag in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setAcute( boolean flg ){
    	dataStruct[121] = (byte) (flg ? 'Y': 'N');
    }


    
    
    

	// char	flgDAW;			// NEW - flag to 'dispense as written' or no substitutions, offset 122
    
    /**
     * getDAW()
     * 
     * Get med DAW flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getDAW(){
    	return ( dataStruct[122] == 'Y' );
    }

    /**
     * setDAW()
     * 
     * Set DAW flag in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setDAW( boolean flg ){
    	dataStruct[122] = (byte) (flg ? 'Y': 'N');
    }


    
    
    


  

	// char	flgPRN;			// NEW - flag this as a PRN med, offset 123
    
    /**
     * getPRN()
     * 
     * Get med PRN flag from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getPRN(){
    	
    	if ( this.isNewStyle()){   		
    		return ( dataStruct[123] == 'Y' );
    	} else {
    		// if old style, see if Schedule/Freq text field contains PRN
    		String s = StructHelpers.getString( dataStruct, 62, 4 ).trim();
    		String q = StructHelpers.getString( dataStruct, 66, 10 ).trim();
    		if ( s.length() > 0 ) return false;
    		return s.endsWith("P") || ( s.indexOf( "PRN" ) >= 0 ) || ( q.indexOf( "PRN" ) >= 0 );
    	}
    }

    /**
     * setPRN()
     * 
     * Set med PRN flag in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setPRN( boolean flg ){
    	dataStruct[123] = (byte) (flg ? 'Y': 'N');
    }


    
    
    


	// char	status;			// status - c_MED_STATUS_xxxxx, offset 124
    
    /**
     * getStatus()
     * 
     * Get Med status from dataStruct.
     * 
     * @param none
     * @return int
     */
    public Cmed.Status getStatus(){
    	return Cmed.Status.get( dataStruct[124] & 0xff );
    }

    /**
     * setStatus()
     * 
     * Set Med status in dataStruct.
     * 
     * @param int		'status'
     * @return void
     */
    public void setStatus( Status status ){
    	dataStruct[124] = (byte) (status.getCode() & 0xff);
    }


    
    
    



	// char	checked;		// interactions checked (Y,N,' '), offset 125
    // CHANGED - now used to signify codified with NewCrop
    
    /**
     * getChecked()
     * 
     * Get Interactions Checked status from dataStruct.
     * 
     * @param none
     * @return byte
     */
    public byte getChecked(){
    	return dataStruct[125];
    }

    /**
     * setChecked()
     * 
     * Set Interactions Checked status in dataStruct.
     * 
     * @param byte		'status'
     * @return void
     */
    public void setChecked( byte status ){
    	dataStruct[125] = (byte)(0xff & status);
    }


    



	// byte	Removed;		// removed reason, offset 126
    
    /**
     * getRemoved()
     * 
     * Get Med Removed status from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getRemoved(){
    	return (int) dataStruct[126];
    }

    /**
     * setRemoved()
     * 
     * Set Med Removed status in dataStruct.
     * 
     * @param int		'Removed status'
     * @return void
     */
    public void setRemoved( int num ){
    	dataStruct[126] = (byte)( 0xff & num );
    }


    
    
	// byte	flgMisc;		// NEW - flag this as misc med entry 'Y' or 'N'
    						// always set - use this to tell if new style med
    
    /**
     * getFlgMisc()
     * 
     * Get Misc Med status from dataStruct.
     * 
     * @param none
     * @return boolean
     */
    public boolean getFlgMisc(){
    	return ! ( dataStruct[127] == 'N' );
    }

    /**
     * setFlgMisc()
     * 
     * Set Misc Med status in dataStruct.
     * 
     * @param boolean
     * @return void
     */
    public void setFlgMisc( boolean flg ){
    	dataStruct[127] = (byte) (flg ? 'Y': 'N');
    }

    
     /**
     * isNewStyle()
     * 
     * Is this a 'new style' med entry?
     * (test flgMisc for Y/N value == new style)
     * 
     * @param none
     * @return boolean
     */
    public boolean isNewStyle(){
    	return (( dataStruct[127] == 'Y' ) || ( dataStruct[127] == 'N' ));
    }


   
    
    
    
	// char	deaClass;		// NEW - dea class of this med, offset 120
    
    /**
     * getDEAClass()
     * 
     * Get DEA Class number from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getDEAClass(){
    	return ( dataStruct[120] );
    }

    /**
     * setDEAClass()
     * 
     * Set DEA Class number in dataStruct.
     * 
     * @param int
     * @return void
     */
    public void setDEAClass( int num ){
    	dataStruct[120] = (byte) (num & 0xff);
    }


    
    
    

    
    
    
    
    
	// RECA	LabelTextReca;	// label text record number, offset 80
    
    /**
     * getAddlSig()
     * 
     * Get additional SIG text 
     * 
     * @param none
     * @return String
     */
    public String getAddlSigTxt(){
    	
    	String s = "";
    	String t = null;
    	
    	if ( ! isNewStyle()){
    		// get old style SIG
    		t = StructHelpers.getString( dataStruct, 60, 2 ).trim();
    		if ( t.length() > 0 ) s = s + t + " ";
    		t = StructHelpers.getString( dataStruct, 66, 10 ).trim();
    		if ( t.length() > 0 ) s = s + t + " ";
    		s = s + StructHelpers.getString( dataStruct, 62, 4 ).trim();
    		s = s.trim();
    	}
  
    	// have we read addlSig text record yet, if not - read it
    	if ( ! flgAddlSigRead ){
    		
    		Reca reca = this.getLabelReca();
    		if ( Reca.isValid(reca)){
    			this.addlSig = this.readAddlSigText( reca );
    			flgAddlSigRead = true;
    		}
    	}
    	
    	if (( this.addlSig != null ) && ( this.addlSig.length() > 0 )) s = s + this.addlSig;
    	
    	return s;
    }

    /**
     * setAddlSig()
     * 
     * Set additional SIG text 
     * 
     * @param String
     * @return void
     */
    public void setAddlSigTxt( String txt ){
    	
    	// handle trivial cases
    	if ( txt == null ) return;
    	txt = txt.trim();
    	if ( txt.length() < 1 ) return;
    	
    	// is new and old the same?
    	if ( txt.equals( this.addlSig )) return;
    	
    	// set addlSig text and mark to be written
    	this.flgAddlSig = true;
    	this.addlSig = txt;
    }


    
    
    
    
    
    
    
    
    
    /**
     * toString()
     * 
     * Get a printable String representation of this entry.
     * 
     * @param int		'Removed status'
     * @return void
     */
  
    public String toString(){
    	return String.format( "%s  %s %s", this.getDrugName(), this.getDosage(), this.getSched());
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private String fnsubTxt = "medtxt%02d.med";
    private String fnsubNdx = "medtxl%02d.med";

    
    private String readAddlSigText( Reca reca ){
    	
    	// handle trivial case
    	if (( reca == null ) || ( ! reca.isValid())) return "";
		//System.out.println( "Reading additional sig text: reca=" + reca.toString());

    	
    	// read entry from text index
    	byte[] dataStruct = new byte[8];
		RecaFile.readReca( reca, dataStruct, Pm.getMedPath(), fnsubNdx, 8 );
		
		int txtoff = StructHelpers.getInt( dataStruct, 0 );
		int txtlen = StructHelpers.getInt( dataStruct, 4 );
		//System.out.println( "text offset=" + txtoff + ", len=" + txtlen );
		
		// are numbers valid
		if (( txtoff < 0 ) || ( txtlen < 1 )) return "";
		
		String s = NoteText.readText(fnsubTxt, reca.getYear(), new Rec( txtoff ));
		s.replaceAll("[^\\p{Alnum}\\p{Punct}\\p{Blank}]", "" );
		System.out.println( "text=" + s );
		return s;
    }
    
    private Reca writeAddlSigText( String text ){
    	
    	int vol = Reca.todayVol();
    	
    	// handle trivial case
    	if ( text == null ) return null;
    	
    	text = text.trim();
    	int txtlen = text.length();
    	if ( txtlen < 1 ) return null;
    	
    	
    	// save text
    	Reca reca = NoteText.saveText( fnsubTxt, vol, getStartDate(), getPtRec(), new Rec( 0 ), text );
 
    	
    	// write index entry (not sure why I did this - I did this in the old version)
//TODO - fix this
        
        // set data in header
    	byte[] hdrData = new byte[8];
    	StructHelpers.setInt( reca.getRec(), hdrData, 0 );
    	StructHelpers.setInt( txtlen, hdrData, 4 );
    	
    	// write header
    	reca = RecaFile.newReca( vol, hdrData, Pm.getMedPath(), fnsubNdx, 8 );
    	
    	return reca;
    }

}


/**/
