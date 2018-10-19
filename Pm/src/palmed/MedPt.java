/**
 * 
 */
package palmed;

import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;

/**
 * Class: MedPt
 * 
 * Medical record directory.  From PAL/MED Struct MedPt in info.h.
 * 
 * @author J. Richard Palen, MD
 *
 */

/*# CHANGELOG  */
/*# 02-09-2011 jrp - added vitalsReca to MedPt  */
/*# 02-09-2011 jrp - added ordersReca to MedPt  */
/*# 09-14-2011 jrp - added smokingReca to MedPt  */




/*  patient medical record structure  */

//typedef struct {
//
//0		RECSH	PcnRec;			/*  primary physician  */
//2		RECSH	PhyDefSet;		/*  physical default set  */
//
//4		RECA	DocReca;  		/*  document list  */
//8		RECA	SoapReca;		/*  soap note list  */
//12	RECA	MedsReca;		/*  medication records  */
//16	RECA	ParsReca;		/*  PAR records  */
//20	RECA	ProbReca;		/*  current problem list  */
//24	RECA	LabReca;		/*  lab reports record  */
//28	RECA	TrtReca;		/*  treatment notes record  */
//
//32	RECORD	HstRec;			/*  history record  */
//36	RECORD	FlwRec;			/*  follow list record  */
//40	RECORD	RdocRec;		/*  referring doctor record  */
//44	RECORD	MedAlertRec;		/*  Medical Alert Record  */
//48	RECA	ImmuneReca;		/*  immunizations list  */
//52	RECA	MrLogLast;		/*  medical record log last reca  */
//
//56	RECA	NurseReca;		/*  nursing notes  */
//60	RECA	XrayReca;		/*  xray reports  */
//64	RECA	PhyReca;		/*  physical exams  */
//68	RECORD	PharmRec;		/*  patient's preferred pharmacy  */
//72	RECORD	LabResultReca;		/*  (tabular) lab results  */
//	
//76	RECA	vitalsReca;		// vitals record link
//80	RECA	ordersReca;		// orders record link
//84	RECA	smokingReca;	// smoking status record link
//88	RECA	interventionsReca;	// interventions reca
//92	RECA	proceduresReca;	// proceduress
//96	RECA	drugsReca;		// drugs and alcohol hx reca
//100	RECA	hxFamilyItemReca;	// family history items reca
//104	RECA	hxFamilyReca;	// family history record
//108	RECA	hxGynReca;		// ob/gyn history record
//112	RECA	hxMiscReca;		// misc history record
//
//116	char	unused[8];		/*  unused field  */

//124	byte	flgNoProbs;		// flag no current problems
//125	byte	flgNoMeds;		// flag no current medications
//126	byte	flgNKDA;		// flag no known drug allergies
//127	byte	Confidential;		/*  confidential class  */
//
//	} MEDPT;
//
/*	sizecheck( 128, sizeof( MEDPT ), "MEDPT" );*/




public class MedPt {
	
	// fields
    public byte[] dataStruct;			// data buffer
    Rec medPtRec = null;				// this MedPt's record number
    
    private final static String fn_medpt = "medpt.med";		// file name
    private final static int recLength = 128;				// record length
    
    

	
	
    public MedPt() {
    	allocateBuffer();
    }
	
    public MedPt( Rec rec ){ 
    	
    	// allocate space
    	allocateBuffer();
    	
    	// read MedPt
    	read( rec );
    }

    public MedPt( int rec ){

        // allocate space
    	allocateBuffer();
    	
    	// read medpt.med
    	read( new Rec( rec ));
    }
    
    
    
    private boolean read( Rec rec ){
    	
    	// make sure we have valid rec
    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "MedPt.read() bad rec" );
   
    	// read record
    	medPtRec = rec;
		return RandomFile.readRec( rec, dataStruct, Pm.getMedPath(), fn_medpt, getRecordLength());
    }
    
    public boolean write() { return write( medPtRec ); }
    
    
    public boolean write( Rec rec ){

    	if ( ! Rec.isValid( rec )) SystemHelpers.seriousError( "MedPt.write() bad rec" );
    	
    	// write med-pt.med
        RandomFile.writeRec( rec, this.dataStruct, Pm.getMedPath(), fn_medpt, getRecordLength());
        //System.out.println( "writing out MedPt, rec=" + rec.toString());
        return true;
    }


    public Rec createNewRecord(){

    	// create med-pt.med
        medPtRec = RandomFile.writeRec( null, this.dataStruct, Pm.getMedPath(), fn_medpt, getRecordLength());
        return medPtRec;
    }





	
    /**
     * getRecordLength
     */
    public static int getRecordLength() { return ( recLength ); }
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recLength];
    }

    public void setDataStruct(byte[] dataStruct) { this.dataStruct = dataStruct; }

    public byte[] getDataStruct() { return dataStruct; }

   
    
    
    // RECSH PcnRec		// primary provider rec, offset 0

    /**
     * <p>getPcnRec()</p>
     * 
     * <p>Get patient's preferred provider record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	provider record number
     */
    
    public Rec getPcnRec() { return Rec.fromShort( dataStruct, 0 ); }
    
    /**
     * <p>setPcnRec()</p>
     * 
     * <p>Set patient's preferred provider record number into the MedPt data struct.</p>
     * 
     * @param int rec		provider record number
     * @return none
     */
    
    public void setPcnRec( int rec ) { new Rec( rec ).toShort( dataStruct, 0 ); }
    
    /**
     * <p>setPcnRec()</p>
     * 
     * <p>Set patient's preferred provider record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		provider record number
     * @return none
     */
    
    public void setPcnRec( Rec rec ) { rec.toShort( dataStruct, 0 ); }
    
   
	
    
    
    
    // RECSH PhyDefSet	// physical default set, offset 2

    /**
     * <p>getPhyDefSet()</p>
     * 
     * <p>Get patient's preferred provider record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	provider record number
     */
    
    public Rec getPhyDefSet() { return Rec.fromShort( dataStruct, 2 ); }
    
    /**
     * <p>setPcnRec()</p>
     * 
     * <p>Set patient's preferred provider record number into the MedPt data struct.</p>
     * 
     * @param int rec		provider record number
     * @return none
     */
    
    public void setPhyDefSet( int rec ) { new Rec( rec ).toShort( dataStruct, 2 ); }
    
    /**
     * <p>setPcnRec()</p>
     * 
     * <p>Set patient's preferred provider record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		provider record number
     * @return none
     */
    
    public void setPhyDefSet( Rec rec ) { rec.toShort( dataStruct, 2 ); }
    
   
    
   
	
    
    
    
    // RECA DocReca	// document list, offset 4

    /**
     * <p>getDocReca()</p>
     * 
     * <p>Get patient's document list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	document list reca record number
     */
    
    public Reca getDocReca() { return Reca.fromReca( dataStruct, 4 ); }
    
    /**
     * <p>setDocReca()</p>
     * 
     * <p>Set patient's document list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		document list reca record number
     * @return none
     */
    
    public void setDocReca( Reca reca ) { reca.toReca(dataStruct, 4); }
    


	

	
    // RECA SoapReca	// soap note list, offset 8

    /**
     * <p>getSoapReca()</p>
     * 
     * <p>Get patient's soap note list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	soap note list reca record number
     */
    
    public Reca getSoapReca() { return Reca.fromReca( dataStruct, 8 ); }
    
    /**
     * <p>setSoapReca()</p>
     * 
     * <p>Set patient's soap note list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		soap note list reca record number
     * @return none
     */
    
    public void setSoapReca( Reca reca ) { reca.toReca(dataStruct, 8); }
    


	

	
    // RECA MedsReca	// medication list, offset 12

    /**
     * <p>getMedsReca()</p>
     * 
     * <p>Get patient's medications list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	medications list reca record number
     */
    
    public Reca getMedsReca() { return Reca.fromReca( dataStruct, 12 ); }
    
    /**
     * <p>setMedsReca()</p>
     * 
     * <p>Set patient's medications list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		medications list reca record number
     * @return none
     */
    
    public void setMedsReca( Reca reca ) { reca.toReca(dataStruct, 12); }
    


	

    // RECA ParsReca	// prior adverse reactions list, offset 16

    /**
     * <p>getParsReca()</p>
     * 
     * <p>Get patient's prior adverse reactions (PARs) list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	PARs list reca record number
     */
    
    public Reca getParsReca() { return Reca.fromReca( dataStruct, 16 ); }
    
    /**
     * <p>setParsReca()</p>
     * 
     * <p>Set patient's prior adverse reactions (PARs) list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		PARs list reca record number
     * @return none
     */
    
    public void setParsReca( Reca reca ) { reca.toReca(dataStruct, 16); }
    


	

    // RECA ProbReca	// document list, offset 20

    /**
     * <p>getProbReca()</p>
     * 
     * <p>Get patient's problem list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	problem list reca record number
     */
    
    public Reca getProbReca() { return Reca.fromReca( dataStruct, 20 ); }
    
    /**
     * <p>setProbReca()</p>
     * 
     * <p>Set patient's problem list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		problem list reca record number
     * @return none
     */
    
    public void setProbReca( Reca reca ) { reca.toReca(dataStruct, 20 ); }
    


	

	
    // RECA LabReca	// document list, offset 24

    /**
     * <p>getLabReca()</p>
     * 
     * <p>Get patient's lab notes list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	lab notes list reca record number
     */
    
    public Reca getLabReca() { return Reca.fromReca( dataStruct, 24 ); }
    
    /**
     * <p>setLabReca()</p>
     * 
     * <p>Set patient's lab notes list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		lab notes list reca record number
     * @return none
     */
    
    public void setLabReca( Reca reca ) { reca.toReca(dataStruct, 24); }
    


	

	
    // RECA TrtReca	// treatment notes list, offset 28

    /**
     * <p>getTrtReca()</p>
     * 
     * <p>Get patient's treatment notes list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	treatment notes list reca record number
     */
    
    public Reca getTrtReca() { return Reca.fromReca( dataStruct, 28 ); }
    
    /**
     * <p>setTrtReca()</p>
     * 
     * <p>Set patient's treatment notes list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		treatment notes list reca record number
     * @return none
     */
    
    public void setTrtReca( Reca reca ) { reca.toReca(dataStruct, 28 ); }
    


	

	
    // RECA DocReca	// immunizations list, offset 48

    /**
     * <p>getImmuneReca()</p>
     * 
     * <p>Get patient's Immunizations list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	immunizations list reca record number
     */
    
    public Reca getImmuneReca() { return Reca.fromReca( dataStruct, 48 ); }
    
    /**
     * <p>setImmuneReca()</p>
     * 
     * <p>Set patient's immunizations list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		immunizations list reca record number
     * @return none
     */
    
    public void setImmuneReca( Reca reca ) { reca.toReca(dataStruct, 48 ); }
    


	

	

    // RECA MrLogLast	// MR Log list, offset 52

    /**
     * <p>getMrLogReca()</p>
     * 
     * <p>Get patient's MR log list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	MR log list reca record number
     */
    
    public Reca getMrLogReca() { return Reca.fromReca( dataStruct, 52 ); }
    
    /**
     * <p>setMrLogReca()</p>
     * 
     * <p>Set patient's MR log list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		MR log list reca record number
     * @return none
     */
    
    public void setMrLogReca( Reca reca ) { reca.toReca(dataStruct, 52 ); }
    


	

	
    // RECA NurseReca	// nursing list, offset 56

    /**
     * <p>getNurseReca()</p>
     * 
     * <p>Get patient's nursing note list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	nursing note list reca record number
     */
    
    public Reca getNurseReca() { return Reca.fromReca( dataStruct, 56 ); }
    
    /**
     * <p>setNurseReca()</p>
     * 
     * <p>Set patient's nursing note list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		nursing note list reca record number
     * @return none
     */
    
    public void setNurseReca( Reca reca ) { reca.toReca(dataStruct, 56 ); }
    


	

	
	
    // RECA XrayReca	// xray list, offset 60

    /**
     * <p>getXrayReca()</p>
     * 
     * <p>Get patient's xray list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	xray list reca record number
     */
    
    public Reca getXrayReca() { return Reca.fromReca( dataStruct, 60 ); }
    
    /**
     * <p>setXrayReca()</p>
     * 
     * <p>Set patient's xray list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		xray list reca record number
     * @return none
     */
    
    public void setXrayReca( Reca reca ) { reca.toReca(dataStruct, 60 ); }
    


	

	
    // RECA PhyReca	// physical exam list, offset 64

    /**
     * <p>getPhyReca()</p>
     * 
     * <p>Get patient's physical exam list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	physical exam list reca record number
     */
    
    public Reca getPhyReca() { return Reca.fromReca( dataStruct, 64 ); }
    
    /**
     * <p>setPhyReca()</p>
     * 
     * <p>Set patient's physical exam list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		physical exam list reca record number
     * @return none
     */
    
    public void setPhyReca( Reca reca ) { reca.toReca(dataStruct, 64); }
    


	

	

    
    // RECORD HstRec		// history rec, offset 32

    /**
     * <p>getHstRec()</p>
     * 
     * <p>Get patient's history record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	history record number
     */
    
    public Rec getHstRec() { return Rec.fromInt( dataStruct, 32 ); }
    
    /**
     * <p>setHstRec()</p>
     * 
     * <p>Set patient's history record number into the MedPt data struct.</p>
     * 
     * @param int rec		history record number
     * @return none
     */
    
    public void setHstRec( int rec ) { new Rec( rec ).toInt( dataStruct, 32 ); }
    
    /**
     * <p>setPcnRec()</p>
     * 
     * <p>Set patient's history record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		history record number
     * @return none
     */
    
    public void setHstRec( Rec rec ) { rec.toInt( dataStruct, 32 ); }
    
   
	
    
    
    

    // RECORD FlwRec		// follow list rec, offset 36

    /**
     * <p>getPcnRec()</p>
     * 
     * <p>Get patient's follow list record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	follow list record number
     */
    
    public Rec getFlwRec() { return Rec.fromInt( dataStruct, 36 ); }
    
    /**
     * <p>setFlwRec()</p>
     * 
     * <p>Set patient's follow list record number into the MedPt data struct.</p>
     * 
     * @param int rec		follow list record number
     * @return none
     */
    
    public void setFlwRec( int rec ) { new Rec( rec ).toInt( dataStruct, 36 ); }
    
    /**
     * <p>setFlwRec()</p>
     * 
     * <p>Set patient's follow list record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		follow list number
     * @return none
     */
    
    public void setFlwRec( Rec rec ) { rec.toInt( dataStruct, 36 ); }
    
   
	
    
    
    

    // RECORD RdocRec		// Rdoc rec, offset 40

    /**
     * <p>getRdocRec()</p>
     * 
     * <p>Get patient's Rdoc record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	Rdoc record number
     */
    
    public Rec getRdocRec() { return Rec.fromInt( dataStruct, 40 ); }
    
    /**
     * <p>setRdocRec()</p>
     * 
     * <p>Set patient's rdoc record number into the MedPt data struct.</p>
     * 
     * @param int rec		rdoc record number
     * @return none
     */
    
    public void setRdocRec( int rec ) { new Rec( rec ).toInt( dataStruct, 40 ); }
    
    /**
     * <p>setRdocRec()</p>
     * 
     * <p>Set patient's rdoc record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		rdoc number
     * @return none
     */
    
    public void setRdocRec( Rec rec ) { rec.toInt( dataStruct, 40 ); }
    
   
	
    
    
    
    // RECORD MedAlertRec		// medical alert rec, offset 44

    /**
     * <p>getMedAlertRec()</p>
     * 
     * <p>Get patient's medical alert record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Rec rec	medical alert record number
     */
    
    public Rec getMedAlertRec() { return Rec.fromInt( dataStruct, 44 ); }
    
    /**
     * <p>setMedAlertRec()</p>
     * 
     * <p>Set patient's medical alert record number into the MedPt data struct.</p>
     * 
     * @param int rec		medical alert record number
     * @return none
     */
    
    public void setMedAlertRec( int rec ) { new Rec( rec ).toInt( dataStruct, 44 ); }
    
    /**
     * <p>setMedAlertRec()</p>
     * 
     * <p>Set patient's medical alert record number into the MedPt data struct.</p>
     * 
     * @param Rec rec		medical alert number
     * @return none
     */
    
    public void setMedAlertRec( Rec rec ) { rec.toInt( dataStruct, 44 ); }
    
   

    
    
    // RECA LabResultReca	// document list, offset 72

    /**
     * <p>getLabResultReca()</p>
     * 
     * <p>Get patient's lab results list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	lab results list reca record number
     */
    
    public Reca getLabResultReca() { return Reca.fromReca( dataStruct, 72 ); }
    
    /**
     * <p>setLabResultReca()</p>
     * 
     * <p>Set patient's lab results list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		lab results list reca record number
     * @return none
     */
    
    public void setLabResultReca( Reca reca ) { reca.toReca(dataStruct, 72 ); }
    


	


    
    
    
    
    
    /**NEW**/    
    // RECA vitalsReca	// vitals, offset 76

    /**
     * <p>getVitalsReca()</p>
     * 
     * <p>Get patient's vital signs list RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	vital signs list reca record number
     */
    
    public Reca getVitalsReca() { return Reca.fromReca( dataStruct, 76 ); }
    
    /**
     * <p>setVitalsReca()</p>
     * 
     * <p>Set patient's vital signs list RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		vital signs list reca record number
     * @return none
     */
    
    public void setVitalsReca( Reca reca ) { reca.toReca(dataStruct, 76); }
    


	

	


    /**NEW**/    
    // RECA ordersReca	// orders, offset 80

    /**
     * <p>getOrdersReca()</p>
     * 
     * <p>Get patient's orders RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	orders reca record number
     */
    
    public Reca getOrdersReca() { return Reca.fromReca( dataStruct, 80 ); }
    
    /**
     * <p>setOrdersReca()</p>
     * 
     * <p>Set patient's orders RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		orders reca record number
     * @return none
     */
    
    public void setOrdersReca( Reca reca ) { reca.toReca(dataStruct, 80); }
    

    
    
    
    
    /**NEW**/    
    // RECA smokingReca	// orders, offset 84

    /**
     * <p>getSmokingReca()</p>
     * 
     * <p>Get patient's smoking status RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getSmokingReca() { return Reca.fromReca( dataStruct, 84 ); }
    
    /**
     * <p>setSmokingReca()</p>
     * 
     * <p>Set patient's smoking status RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setSmokingReca( Reca reca ) { reca.toReca(dataStruct, 84); }
    

	

	


    
    
    /**NEW**/    
    // RECA interventionsReca	// interventions, offset 88

    /**
     * <p>getInterventionsReca()</p>
     * 
     * <p>Get Interventions RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getInterventionsReca() { return Reca.fromReca( dataStruct, 88 ); }
    
    /**
     * <p>setInterventionsReca()</p>
     * 
     * <p>Set Interventions RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setInterventionsReca( Reca reca ) { reca.toReca(dataStruct, 88); }
    

	

	


    
    
    /**NEW**/    
    // RECA proceduresReca	// procedures, offset 92

    /**
     * <p>getProceduresReca()</p>
     * 
     * <p>Get Procedures RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getProceduresReca() { return Reca.fromReca( dataStruct, 92 ); }
    
    /**
     * <p>setProceduresReca()</p>
     * 
     * <p>Set Procedures RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setProceduresReca( Reca reca ) { reca.toReca(dataStruct, 92 ); }
    

	
    
    
    
    

    /**NEW**/    
    // RECA drugsReca	// drugs and alcohol reca, offset 96

    /**
     * <p>getDrugsReca()</p>
     * 
     * <p>Get Drugs RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getDrugsReca() { return Reca.fromReca( dataStruct, 96 ); }
    
    /**
     * <p>setDrugsReca()</p>
     * 
     * <p>Set Drugs RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setDrugsReca( Reca reca ) { reca.toReca(dataStruct, 96 ); }
    

	
    
    
    
    

    /**NEW**/    
    // RECA hxFamilyItemReca	// hx family 2 reca, offset 100

    /**
     * <p>getHxFamily2Reca()</p>
     * 
     * <p>Get HxFamily2 RECA record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getHxFamilyItemReca() { return Reca.fromReca( dataStruct, 100 ); }
    
    /**
     * <p>setHxFamily2Reca()</p>
     * 
     * <p>Set HxFamily2 RECA record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setHxFamilyItemReca( Reca reca ) { reca.toReca(dataStruct, 100 ); }
    

	
    
    
    
    
    /**NEW**/    
    // Reca hxFamilyRec	// hx family reca, offset 104

    /**
     * <p>getHxFamilyReca()</p>
     * 
     * <p>Get HxFamily Reca record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	rec record number
     */
    
    public Reca getHxFamilyReca() { return Reca.fromReca( dataStruct, 104 ); }
    
    /**
     * <p>setHxFamilyReca()</p>
     * 
     * <p>Set HxFamily Reca record number into the MedPt data struct.</p>
     * 
     * @param Rec reca		reca record number
     * @return none
     */
    
    public void setHxFamilyReca( Reca reca ) { reca.toReca(dataStruct, 104 ); }
    

	
    
    
    
    

    /**NEW**/    
    // Rec hxGynReca	// hx OB/GYN reca, offset 108

    /**
     * <p>getHxGynReca()</p>
     * 
     * <p>Get HxGynReca record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getHxGynReca() { return Reca.fromReca( dataStruct, 108 ); }
    
    /**
     * <p>setHxGynReca()</p>
     * 
     * <p>Set HxGyn Reca record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setHxGynReca( Reca reca ) { reca.toReca(dataStruct, 108 ); }
    

	
    
    
    
    

    /**NEW**/    
    // Rec hxMiscReca	// hx Misc reca, offset 112

    /**
     * <p>getHxMiscReca()</p>
     * 
     * <p>Get HxMiscReca record number from the MedPt data struct.</p>
     * 
     * @param none
     * @return Reca reca	reca record number
     */
    
    public Reca getHxMiscReca() { return Reca.fromReca( dataStruct, 112 ); }
    
    /**
     * <p>setHxMiscReca()</p>
     * 
     * <p>Set HxMisc Reca record number into the MedPt data struct.</p>
     * 
     * @param Reca reca		reca record number
     * @return none
     */
    
    public void setHxMiscReca( Reca reca ) { reca.toReca(dataStruct, 112 ); }
    

	
    
    
    
    

    // byte flgNoProbs;		// flg no current problems, offset 124

    /**
     * <p>getNoProbsFlag()</p>
     * 
     * <p>Get 'No Current Problems' flag from the MedPt data struct.</p>
     * 
     * @param none
     * @return boolean
     */
    
    public boolean getNoProbsFlag() { return (( dataStruct[124] & 0xff) == 'Y' ); }
    
    /**
     * <p>setNoProbsFlag()</p>
     * 
     * <p>Set 'No Current Problems' flag into the MedPt data struct.</p>
     * 
     * @param boolean
     * @return none
     */
    
    public void setNoProbsFlag( boolean flg ) { dataStruct[124] = (byte) (flg ? 'Y': 'N'); }
    
   
	



    
    
    // byte flgNoMedss;		// flg no current medications, offset 125

    /**
     * <p>getNoMedsFlag()</p>
     * 
     * <p>Get 'No Current Medications' flag from the MedPt data struct.</p>
     * 
     * @param none
     * @return boolean
     */
    
    public boolean getNoMedsFlag() { return (( dataStruct[125] & 0xff) == 'Y' ); }
    
    /**
     * <p>setNoMedsFlag()</p>
     * 
     * <p>Set 'No Current Medications' flag into the MedPt data struct.</p>
     * 
     * @param boolean
     * @return none
     */
    
    public void setNoMedsFlag( boolean flg ) { dataStruct[125] = (byte) (flg ? 'Y': 'N'); }
    
   
	



    
    // byte flgNKDA;		// flg no known drug allergies, offset 126

    /**
     * <p>getNKDAFlag()</p>
     * 
     * <p>Get 'No Known Drug Allergies' flag from the MedPt data struct.</p>
     * 
     * @param none
     * @return boolean
     */
    
    public boolean getNKDAFlag() { return (( dataStruct[126] & 0xff) == 'Y' ); }
    
    /**
     * <p>setNKDAFlag()</p>
     * 
     * <p>Set 'No Known Drug Allergies' flag into the MedPt data struct.</p>
     * 
     * @param boolean
     * @return none
     */
    
    public void setNKDAFlag( boolean flg ) { dataStruct[126] = (byte) (flg ? 'Y': 'N'); }
    
   
	



    
    
   
  
    
    // byte Confidential		// confidential class, offset 127

    /**
     * <p>getConfidentialClass()</p>
     * 
     * <p>Get patient's confidential class from the MedPt data struct.</p>
     * 
     * @param none
     * @return int		confidential class
     */
    
    public int getConfidentialClass() { return (int)( dataStruct[127] ); }
    
    /**
     * <p>setConfidentialClass()</p>
     * 
     * <p>Set patient's follow list record number into the MedPt data struct.</p>
     * 
     * @param int cclass		confidential class
     * @return none
     */
    
    public void setConfidentialClass( int cclass ) { dataStruct[127] = (byte)( cclass ); }
    
   
	
	
}


/**/
