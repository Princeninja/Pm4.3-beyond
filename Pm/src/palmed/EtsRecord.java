
/**
 *  ETS Record
 *  
 *  This class is the ETS format electronic claims record written out by the
 *  legacy PAL/MED character based financial side.
 *  
 *  @change 2015-09-15 jrp - created
 */



// C Data Structure
//
//	/*  ETS00 insurance company information sub-record  */
//
//typedef	struct {
//
//	char	Relation[2];		/*  relationship code  (to insured)  */
//	char	PolicyHolder[14];	/*  policy holder (employer) NR  */
//	char	GroupNumber[20];	/*  group #, policy #, or Medicaid #  */
//	char	InsuredID[15];		/*  insured ID or Medicare Number  */
//	char	PayorName[20];		/*  insurance co name  */
//	char	PayorAddr1[28];		/*  ins comp address #1  */
//	char	PayorAddr2[28];		/*  ins comp addr #2  */
//	char	PayorCity[15];		/*  ins city  */
//	char	PayorState[2];		/*  ins	state  */
//	char	PayorZip[9];		/*  ins zip  */
//	char	PayorID[5];		/*  insurance company number (NEIC #) */
//	char	PayorSubID[4];		/*  pay office #  */
//	char	Assignment;		/*  assignment indicator  */
//	char	PaymentTrigger;		/*  "X"	 */
//	char	CCTCode;		/*  cct code  NR  */
//	char	ClaimType;		/*  claim type (sec only)  */
//
//		} ETS00I;
// 166
//
//
//
//	/*  ETS 00 Record Type  */
//
//typedef	struct {
//
//	char	RecordType[2];		/*  transaction type code  */
//	char	BatchType[3];		/*  hardcode to "100"  */
//	char	SecondaryClaim;		/*  secondary claim ind y/n  */
//	char	spaces1[9];		/*    */
//	char	ETSVersion[2];		/*  ets version X.X fmt  */
//	char	ClaimNumber[5];		/*  provider's claim number  */
//	char	PtAccount[12];		/*  pt account number  */
//	char	spaces2[13];		/*    */
//	char	ProvTaxID[10];		/*  client tax ID, "0" in digit 10  */
//	char 	ProvSubID[4];		/*  internal phys number  */
//	char	ClaimCharges[8];	/*  total charges for this claim  */
//	char	ClaimPaid[8];		/*  amount paid by patient  */
//	char	ClaimBalance[8];	/*  total claim balance  */
//	char	NumberCharges[2];	/*  total number of charges on claim  */
//	char	spaces3[20];		/*    */
//107
//	char	PtLast[13];		/*  patient last name  */
//	char	PtFirst[10];		/*  patient first name  */
//	char	PtMiddle;		/*  patient middle initial  */
//	char	PtStreet[28];		/*  patient street address  */
//	char	PtAddress2[28];		/*  extra patient address line  */
//	char	PtCity[15];		/*  patient city address  */
//	char	PtState[2];		/*  patient state address  */
//	char	PtZip[9];		/*  patient zip code  */
//	char	PtPhone[10];		/*  patient phone  */	
// 223
//	char	Birthdate[6];		/*  patient's date of birth  */
//	char	MaritalStatus;		/*  marital status code  */
//	char	EmploymentStatus;	/*  employment status  */
//	char	Student;		/*  student status  */
//
//	char	Death;			/*  patient deceased D = deceased  */
//	char	Sex;			/*  patient's sex  */
//
//	char	WorkRelated;		/*  work related code  */
//	char	AccidentInd;		/*  accident ind (a,n, or space)  */
//	char	AccidentDate[6];	/*  date of onset or accident  */
//	char	AccidentState[2];	/*  accident state  */
// 244
//	char	InsLast[13];		/*  insured's last name  */		
//	char	InsFirst[10];		/*  insured's first name  */
//	char	InsMiddle;		/*  insured's middle initial  */
//	char	InsStreet[28];		/*  insured's street address  */
//	char	InsAddress2[28];	/*  insured's extra address line  */
//	char	InsCity[15];		/*  insured's city address  */
//	char	InsState[2];		/*  insured's state address  */
//	char	InsZip[9];		/*  insured's zip code	*/
// 350
//	char	InsBirthDate[6];	/*  insured's birthdate  */
//	char	spaces4[4];		/*    */
//	char	InsuredSex;		/*  insured's sex  */
//	char	spaces5[3];		/*    */
//	char	ReleaseInfo;		/*  release info cert  (Y/N)  */
//	char	Signature;		/*  patient signature on file code  */
// 366
//	ETS00I	Primary;		/*  primary insurance  */
// 532
//	ETS00I	Secondary;		/*  secondary insurance  */
// 698
//	char	SameSymptom;		/*  same or similar symptoms flag  */
//	char	FirstConsultDate[6];	/*  date physician first consulted  */
//
//	char	FirstSymptom;		/*  first symptom ind  */
//	char	FirstSymptomDate[6];	/*  first symptom date  */
//	char	Emergency;		/*  emergency indicator  Y/N  */
//	char	RetToWorkDate[6];	/*  return to work date  */
//	char	DisabFromDate[6];	/*  disability from date  */
//	char	DisabToDate[6];		/*  disability to date	*/
//	char	ParDisabFromDate[6];	/*  partial disability from date  */
//	char	ParDisabToDate[6];	/*  partial disability to date  */
//
//	char	RefPcnName[22];		/*  referring physician name  */
//	char	RefPcnNumber[13];	/*  referring physician number  */
//
//	char	HospitalName[15];	/*  hospital name  */
//	char	HospitalNumber[13];	/*  hospital number  */
//
//	char	RefPcnCity[13];		/*  referring physician city  */
//	char	RefPcnState[2];		/*  referring physician state  */
//
//	char	HospitalAddr[28];	/*  hospital address  */
//	char	HospitalCity[15];	/*  hospital city  */
//	char	HospitalState[2];	/*  hospital state	 */
//	char	HospitalZip[9];		/*  hospital zip code  */
//				
//	char	AdmitDate[6];		/*  hospital admission date  */
//	char	DischargeDate[6];	/*  hospital discharge date  */
//
//	char	OutsideLab;		/*  outside lab indicator  */
//	char	OutsideLabFee[7];	/*  outside lab charge	*/
//
//	char	EPSDT;			/*  epsdt indicator  */
//	char	FamilyPlanning;		/*  family plan indicator  */
//
//	char	PriorAuth[14];		/*  prior auth number  */
//	char	Liability;		/*  third party liability indicator  */
//	char	spaces7[2];		/*    */
//	char	PrintCode;		/*  print codes  */
//	char	spaces8[3];		/*    */
//	char	EtsRecCode[2];		/*  ETS	record code  */
//	char	spaces9[12];		/*    */
//	char	spaces10[55];		/*    */
//	char	HospitalArea;		/*  hospital area  */
//	char	RefPcnAddress[27];	/*  referring physician address  */
//	char	RefPcnZip[9];		/*  referring physician zip code  */
//	char	cr;			/*  CR  */
//
//		} ETS00;
//
//
//
//
//
//	/*  ETS 30 Record Type:  Detail Charge Info  */
//
//typedef	struct {
//
//	char	RecordType[2];		/*  hard code to "30"  */
//	char	DgnCode[4][5];		/*  diagnosis codes  */
//
//	char	LineItems[13][54];	/*  line items  */
//					/*    (done this way due to word  */
//					/*    alignment problems.)  */
//
//		/*  documentation area  */
//
//	char	documentation[234];	/*  free form text area  */
//	char	spaces1[3];		/*  spaces  */
//
//		/*  data field extensions  */
//
//	char	spaces2[3];		/*    */
//	char	SpecialSpace[17];	/*    */
//
//	char	spaces3[39];		/*    */
//	char	spaces4[2];		/*    */
//	char	EtsRecCode[2];		/*  ETS record code  */
//	char	cr;			/*  CR  */
//
//		} ETS30;
//
//
//
//
//
//	/*  ETS 30 Record Type Line Item Sub-Record  */
//
//typedef	struct {
//
//	char	SrvLocation[2];		/*  place of service  */
//	char	FromDate[6];		/*  from service date  */
//	char	ToDate[6];		/*  to service date  */
//	char	SrvType;		/*  type of service  */
//	char	Anesthesia[3];		/*  anesthesia units  */
//	char	Units[3];		/*  number of units  */
//	char	SrvCode[5];		/*  procedure code  */
//	char	SrvCodeMod[2][2];	/*  service code modifiers  */
//	char	DgnCodePtr[4];		/*  dgn code pointer to 1-4 of above  */
//	char	UniqueTOSCode[3];	/*  unique TOS code  */
//	char	ProvSubID[4];		/*  Physician sub ID  */
//	char	MedUnnecessary;		/*  medically unnecessary tag Y/N  */
//	char 	Documentation;		/*  documentation indicator  */
//	char	PartBBlood[4];		/*  medicare part B blood  */
//	char	Charge[7];		/*  charge for service  */
//
//	} ETS30L;
//
//
//
//
//	char	*fn_EtsClaims = "claims.ets";
//	char	*fn_EtsReport = "report.ets";
//	char	*fn_EtsClaimsArchive = "cl%.8s.ets";
//	char	*fn_EtsReportArchive = "rp%.8s.ets";
//
//
//


package palmed;

import usrlib.Address;
import usrlib.Date;
import usrlib.Decoders;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;

public class EtsRecord {

	
	private final static String fn_claims = "claims.ets";

    public byte[] ets00record;
    public byte[] ets30record;
//    public byte[] info2Struct;
    
    public int claimNum = 0;
    private int numClaims = 0;
    private static int claimRecLength = 1025;
    
    private RandomFile file;
    private Rec maxrec;
    
    
    
	
	// CONSTRUCTORS
	
    public EtsRecord() {
    	allocateBuffer();
    }
    
    public EtsRecord( int claimNum ){

        // allocate space
    	allocateBuffer();

    	// read 
    	read( claimNum );
    	this.claimNum = claimNum;
    }
    
    
    
    // claims number from 1 
    
    private boolean read( int claimNum ){

        RandomFile file;

    	// read claims file
        file = new RandomFile( Pm.getMedPath(), fn_claims, claimRecLength, RandomFile.Mode.READONLY );
        file.getRecord( this.ets00record, (claimNum - 1) * 2 + 1);
        file.getRecord( this.ets30record, (claimNum - 1) * 2 + 2); 
        file.close();

        this.claimNum = claimNum;
        return true;
    }

    
    
    /**
     * getRecordLength
     */
    public static int getRecordLength() {
        return ( claimRecLength );
    }
    

    
    
    private void allocateBuffer(){
        // allocate data structure space
        ets00record = new byte[1025];
        ets30record = new byte[1025];
    }


    
    
    
    
    
    
    
    
    
    
    
    /**
     * getRecordType
     *
     * ETS record type - should be '00' or '30'
     */
    public String getRecordType() {
        return new String( ets00record, 0, 2 ).trim();
    }


    /**
     * getBatchType
     *
     * ETS batch type - should be '100'
     */
    public String getBatchType() {
        return new String( ets00record, 2, 3 ).trim();
    }


    /**
     * getSecondaryClaim
     *
     * ETS secondary claim indicator - should be 'Y' or 'N'
     */
    public String getSecondaryClaim() {
        return new String( ets00record, 5, 1 ).trim();
    }


    /**
     * getETSVersion
     *
     * ETS version number X.X
     */
    public String getETSVersion() {
        return new String( ets00record, 15, 2 ).trim();
    }


    /**
     * getClaimNumber
     *
     * ETS claim number 
     */
    public String getClaimNumber() {
        return new String( ets00record, 17, 5 ).trim();
    }


    /**
     * getPtAccount
     *
     * ETS patient account number
     */
    public String getPtAccount() {
        return new String( ets00record, 22, 12 ).trim();
    }


    /**
     * getProvTaxID
     *
     * ETS provider tax ID  (with 0 in digit 10)
     */
    public String getProvTaxID() {
        return new String( ets00record, 47, 10 ).trim();
    }


    /**
     * getProvSubID
     *
     * ETS provider tax sub ID - internal physician number
     */
    public String getProvSubID() {
        return new String( ets00record, 57, 4 ).trim();
    }


    /**
     * getClaimCharges
     *
     * ETS total claim charges
     */
    public String getClaimCharges() {
        return new String( ets00record, 61, 8 ).trim();
    }


    /**
     * getClaimPaid
     *
     * ETS claim paid
     */
    public String getClaimPaid() {
        return new String( ets00record, 69, 8 ).trim();
    }


    /**
     * getClaimBalance
     *
     * ETS claim balance
     */
    public String getClaimBalance() {
        return new String( ets00record, 77, 8 ).trim();
    }


    /**
     * getNumberCharges
     *
     * ETS number charges in claim
     */
    public String getNumberCharges() {
        return new String( ets00record, 85, 2 ).trim();
    }




    
    /**
     * getPtLast
     *
     * ETS get pt last name
     */
    public String getPtLast() {
        return new String( ets00record, 107, 13 ).trim();
    }


    /**
     * getPtFirst
     *
     * ETS get pt first name
     */
    public String getPtFirst() {
        return new String( ets00record, 120, 10 ).trim();
    }


    /**
     * getPtMiddle
     *
     * ETS get pt middle name
     */
    public String getPtMiddle() {
        return new String( ets00record, 130, 1 ).trim();
    }




    /**
     * getName
     */
//    public Name getName() {
//        return new Name( ets00record, 0 );
//    }
    

    
    /**
     * getPtStreet
     *
     * ETS pt street address
     */
    public String getPtStreet() {
        return new String( ets00record, 131, 28 ).trim();
    }


    /**
     * getPtAddress2
     *
     * ETS pt 2nd address line
     */
    public String getPtAddress2() {
        return new String( ets00record, 159, 28 ).trim();
    }


    /**
     * getPtCity
     *
     * ETS get pt address city
     */
    public String getPtCity() {
        return new String( ets00record, 187, 15 ).trim();
    }


    /**
     * getPtState
     *
     * ETS get pt address state
     */
    public String getPtState() {
        return new String( ets00record, 202, 2 ).trim();
    }


    /**
     * getPtZip
     *
     * ETS get pt address zip code
     */
    public String getPtZip() {
        return new String( ets00record, 204, 9 ).trim();
    }


    /**
     * getPtPhone
     *
     * ETS get pt phone number
     */
    public String getPtPhone() {
        return new String( ets00record, 213, 10 ).trim();
    }


    /**
     * getPtBirthdate
     *
     * ETS get pt birthdate
     */
    public String getPtBirthdate() {
        return new String( ets00record, 223, 6 );
    }


    /**
     * getPtMaritalStatus
     *
     * ETS get pt marital status
     */
    public String getPtMaritalStatus() {
        return new String( ets00record, 229, 1 ).trim();
    }


    /**
     * getPtEmploymentStatus
     *
     * ETS get pt employment status
     */
    public String getPtEmploymentStatus() {
        return new String( ets00record, 230, 1 ).trim();
    }


    /**
     * getPtStudentStatus
     *
     * ETS get pt student status
     */
    public String getPtStudentStatus() {
        return new String( ets00record, 231, 1 ).trim();
    }


    /**
     * getPtDeath
     *
     * ETS get pt death indicator
     */
    public String getPtDeath() {
        return new String( ets00record, 232, 1 ).trim();
    }


    /**
     * getPtSex
     *
     * ETS get pt sex
     */
    public String getPtSex() {
        return new String( ets00record, 233, 1 ).trim();
    }


    /**
     * getWorkRelated
     *
     * ETS get work related indicator
     */
    public String getWorkRelated() {
        return new String( ets00record, 234, 1 ).trim();
    }


    /**
     * getAccidentInd
     *
     * ETS accident indicator
     */
    public String getAccidentInd() {
        return new String( ets00record, 235, 1 ).trim();
    }


    /**
     * getAccidentDate
     *
     * ETS accident date
     */
    public String getAccidentDate() {
        return new String( ets00record, 236, 6 ).trim();
    }


    /**
     * getAccidentState
     *
     * ETS accident state
     */
    public String getAccidentState() {
        return new String( ets00record, 242, 2 ).trim();
    }



    
    
    
    
    
    
    
    
    /**
     * getInsLast
     *
     * ETS get insured last name
     */
    public String getInsLast() {
        return new String( ets00record, 244, 13 ).trim();
    }


    /**
     * getInsFirst
     *
     * ETS get insured first name
     */
    public String getInsFirst() {
        return new String( ets00record, 257, 10 ).trim();
    }


    /**
     * getInsMiddle
     *
     * ETS get insured middle name
     */
    public String getInsMiddle() {
        return new String( ets00record, 267, 1 ).trim();
    }




    /**
     * getName
     */
//    public Name getInsName() {
//        return new Name( ets00record, 0 );
//    }
    

    
    /**
     * getInsStreet
     *
     * ETS insured street address
     */
    public String getInsStreet() {
        return new String( ets00record, 268, 28 ).trim();
    }


    /**
     * getInsAddress2
     *
     * ETS insured 2nd address line
     */
    public String getInsAddress2() {
        return new String( ets00record, 296, 28 ).trim();
    }


    /**
     * getInsCity
     *
     * ETS get insured address city
     */
    public String getInsCity() {
        return new String( ets00record, 324, 15 ).trim();
    }


    /**
     * getInsState
     *
     * ETS get insured address state
     */
    public String getInsState() {
        return new String( ets00record, 339, 2 ).trim();
    }


    /**
     * getInsZip
     *
     * ETS get insured address zip code
     */
    public String getInsZip() {
        return new String( ets00record, 341, 9 ).trim();
    }



    /**
     * getInsBirthdate
     *
     * ETS get insured birthdate
     */
    public String getInsBirthdate() {
        return new String( ets00record, 350, 6 ).trim();
    }


    /**
     * getInsSex
     *
     * ETS get insured sex
     */
    public String getInsSex() {
        return new String( ets00record, 360, 1 ).trim();
    }


    /**
     * getReleaseInfo
     *
     * ETS get release info flag
     */
    public String getReleaseInfo() {
        return new String( ets00record, 364, 1 ).trim();
    }


    /**
     * getSignature
     *
     * ETS get signature on file flag
     */
    public String getSignature() {
        return new String( ets00record, 365, 1 ).trim();
    }




    /**
     * getSameSymptom
     *
     * ETS get same symptom flag
     */
    public String getSameSymptom() {
        return new String( ets00record, 698, 1 ).trim();
    }


    /**
     * getFirstConsultDate
     *
     * ETS get first consult date
     */
    public String getFirstConsultDate() {
        return new String( ets00record, 699, 6 ).trim();
    }



    /**
     * getFirstSymptom
     *
     * ETS get first symptom indicator
     */
    public String getFirstSymptom() {
        return new String( ets00record, 705, 1 ).trim();
    }



    /**
     * getFirstSymptomDate
     *
     * ETS get first symptom date
     */
    public String getFirstSymptomDate() {
        return new String( ets00record, 706, 6 ).trim();
    }


    /**
     * getEmergency
     *
     * ETS get emergency indicator
     */
    public String getEmergency() {
        return new String( ets00record, 712, 1 ).trim();
    }


    /**
     * getReturnToWorkDate
     *
     * ETS get return to work date
     */
    public String getReturnToWorkDate() {
        return new String( ets00record, 713, 6 ).trim();
    }


    /**
     * getDisabledFromDate
     *
     * ETS get disabled from date
     */
    public String getDisabledFromDate() {
        return new String( ets00record, 719, 6 ).trim();
    }


    /**
     * getDisabledToDate
     *
     * ETS get disabled to date
     */
    public String getDisabledToDate() {
        return new String( ets00record, 725, 6 ).trim();
    }


    /**
     * getParDisabledFromDate
     *
     * ETS get partial disabled from date
     */
    public String getParDisabledFromDate() {
        return new String( ets00record, 731, 6 ).trim();
    }


    /**
     * getParDisabledToDate
     *
     * ETS get partial disabled to date
     */
    public String getParDisabledToDate() {
        return new String( ets00record, 737, 6 ).trim();
    }


    /**
     * getRefProvName
     *
     * ETS get referring provider name
     */
    public String getRefProvName() {
        return new String( ets00record, 743, 22 ).trim();
    }


    /**
     * getRefProvNumber
     *
     * ETS get referring provider number
     */
    public String getRefProvNum() {
        return new String( ets00record, 765, 13 ).trim();
    }


    /**
     * getRefProvRec
     *
     * ETS get referring provider number
     * (The lost version of PAL/MED financial had apparently sent the record number in this space prepended by a #.)
     */
    public Rec getRefProvRec() {
        String str = new String( ets00record, 765, 13 ).trim();
        if ( str.charAt(0) != '#' ) return null;
        int num = EditHelpers.parseInt( str.substring( 1, 6 ).trim());
        return new Rec( num );
    }


    /**
     * getHospitalName
     *
     * ETS get hospital name
     */
    public String getHospitalName() {
        return new String( ets00record, 778, 15 ).trim();
    }


    /**
     * getHospitalNumber
     *
     * ETS get hospital number
     */
    public String getHospitalNum() {
        return new String( ets00record, 793, 13 ).trim();
    }





    /**
     * getRefProvCity
     *
     * ETS get referring provider address city
     */
    public String getRefProvCity() {
        return new String( ets00record, 806, 13 ).trim();
    }


    /**
     * getRefProvState
     *
     * ETS get referring provider address state
     */
    public String getRefProvState() {
        return new String( ets00record, 819, 2 ).trim();
    }






    /**
     * getHospitalStreet
     *
     * ETS Hospital street address
     */
    public String getHospitalStreet() {
        return new String( ets00record, 821, 28 ).trim();
    }


   /**
     * getHospitalCity
     *
     * ETS get Hospital address city
     */
    public String getHospitalCity() {
        return new String( ets00record, 849, 15 ).trim();
    }


    /**
     * getHospitalState
     *
     * ETS get Hospital address state
     */
    public String getHospitalState() {
        return new String( ets00record, 864, 2 ).trim();
    }


    /**
     * getHospitalZip
     *
     * ETS get Hospital address zip code
     */
    public String getHospitalZip() {
        return new String( ets00record, 866, 9 ).trim();
    }


    /**
     * getAdmitDate
     *
     * ETS get hospital admit date
     */
    public String getAdmitDate() {
        return new String( ets00record, 875, 6 ).trim();
    }


    /**
     * getDischargeDate
     *
     * ETS get hospital discharge date
     */
    public String getDischargeDate() {
        return new String( ets00record, 881, 6 ).trim();
    }


    /**
     * getOutsideLab
     *
     * ETS get outside lab indicator
     */
    public String getOutsideLab() {
        return new String( ets00record, 887, 1 ).trim();
    }


    /**
     * getOutsideLabFee
     *
     * ETS get outside lab fee
     */
    public String getOutsideLabFee() {
        return new String( ets00record, 888, 7 ).trim();
    }


    /**
     * getEPSDT
     *
     * ETS get EPSDT indicator
     */
    public String getEPSDT() {
        return new String( ets00record, 895, 1 ).trim();
    }


    /**
     * getFamilyPlanning
     *
     * ETS get family planning indicator
     */
    public String getFamilyPlanning() {
        return new String( ets00record, 896, 1 ).trim();
    }


    /**
     * getPriorAuth
     *
     * ETS get prior auth number
     */
    public String getPriorAuth() {
        return new String( ets00record, 897, 14 ).trim();
    }


    /**
     * getLiability
     *
     * ETS get third party liability indicator
     */
    public String getLiability() {
        return new String( ets00record, 911, 1 ).trim();
    }


    /**
     * getPrintCode
     *
     * ETS get print code
     */
    public String getPrintCode() {
        return new String( ets00record, 914, 1 ).trim();
    }


    /**
     * getEtsRecCode
     *
     * ETS get Ets record code
     */
    public String getEtsRecCode() {
        return new String( ets00record, 918, 2 ).trim();
    }


    /**
     * getHospitalArea
     *
     * ETS get hospital area
     */
    public String getHospitalArea() {
        return new String( ets00record, 987, 1 ).trim();
    }


    /**
     * getRefProvAddress
     *
     * ETS get referring provider address
     */
    public String getRefProvAddress() {
        return new String( ets00record, 988, 27 ).trim();
    }


    /**
     * getRefProvZip
     *
     * ETS get referring provider zip code
     */
    public String getRefProvZip() {
        return new String( ets00record, 1015, 9 ).trim();
    }



    /**
     * getProvRec
     *
     * ETS get provider record number
     * (This is in spaces9[12] in the 00 record.  Done in the lost version of the old PAL/MED.
     * 
     */
    public Rec getProvRec() {
        String str = new String( ets00record, 920, 6 ).trim();
        if ( str.charAt(0) != '#' ) return null;
        return new Rec( EditHelpers.parseInt( str.substring( 1 )));        
    }



    /**
     * getPPORec
     *
     * ETS get PPO record number
     * (This is in spaces9[12] in the 00 record.  Done in the lost version of the old PAL/MED.
     * 
     */
    public Rec getPPORec( int num ) {
    	int offset = ( num == 2 ) ? 944: 932;
        String str = new String( ets00record, offset, 6 ).trim();
        if ( str.charAt(0) != '#' ) return null;
        return new Rec( EditHelpers.parseInt( str.substring( 1 )));        
    }





    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // ETS 30 Record
    //
    //
    //
    //
    
    
    
    /**
     * get30RecordType
     *
     * ETS get ETS 30 record type - should be '30'
     */
    public String get30RecordType() {
        return new String( ets30record, 0, 2 ).trim();
    }


    /**
     * getDiagnosisCode
     *
     * ETS get claim diagnosis codes
     */
    public String getDiagnosisCode( int num ) {
    	if (( num < 1 ) || ( num > 4 )) SystemHelpers.seriousError( "diagnosis code num out of range" );
        return new String( ets30record, 2 + ((num - 1) * 5), 5 ).trim();
    }


    /**
     * getDocumentation
     *
     * ETS get documentation text
     */
    public String getDocumentation() {
        return new String( ets30record, 724, 234 ).trim();
    }


    /**
     * getSpecialSpace
     *
     * ETS get special space
     */
    public String getSpecialSpace() {
        return new String( ets30record, 964, 17 ).trim();
    }


    /**
     * get30EtsRecCode
     *
     * ETS get ETS 30 record code
     */
    public String get30EtsRecCode() {
        return new String( ets30record, 1022, 2 ).trim();
    }



    
    
    
    
    
    
    
    
    
    //
    // Get Insurance company info from ETS00I records
    //
    //
    //
    //
    
    /**
     * getInsRelation
     *
     * ETS get insured relationship code
     */
    public String getInsRelation( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 0 + ((num-1) * 166), 2 ).trim();
    }


    /**
     * getInsPolicyHolder
     *
     * ETS get insured policy holder (employer)
     */
    public String getInsPolicyHolder( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 2 + ((num-1) * 166), 14 ).trim();
    }


    /**
     * getInsGroupNumber
     *
     * ETS get insured group number
     */
    public String getInsGroupNumber( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 16 + ((num-1) * 166), 20 ).trim();
    }

   
    /**
     * getInsInsuredID
     *
     * ETS get insured insured ID
     */
    public String getInsInsuredID( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 36 + ((num-1) * 166), 15 ).trim();
    }

   
    /**
     * getInsPayorName
     *
     * ETS get insured payor name
     */
    public String getInsPayorName( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 51 + ((num-1) * 166), 20 ).trim();
    }


    /**
     * getInsAddress1
     *
     * ETS get insured payor address line 1
     */
    public String getInsAddress1( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 71 + ((num-1) * 166), 28 ).trim();
    }


    /**
     * getInsAddress2
     *
     * ETS get insured payor address line 2
     */
    public String getInsAddress2( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 99 + ((num-1) * 166), 28 ).trim();
    }


    /**
     * getInsCity
     *
     * ETS get insured payor city
     */
    public String getInsCity( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 127 + ((num-1) * 166), 15 ).trim();
    }


    /**
     * getInsState
     *
     * ETS get insured payor state
     */
    public String getInsState( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 142 + ((num-1) * 166), 2 ).trim();
    }


    /**
     * getInsZip
     *
     * ETS get insured payor zip code
     */
    public String getInsZip( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 144 + ((num-1) * 166), 9 ).trim();
    }


    /**
     * getInsPayorID
     *
     * ETS get insured payor ID
     */
    public String getInsPayorID( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 153 + ((num-1) * 166), 5 ).trim();
    }



    /**
     * getInsPayorSubID
     *
     * ETS get insured payor Sub ID
     */
    public String getInsPayorSubID( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 158 + ((num-1) * 166), 4 ).trim();
    }


    /**
     * getInsAssignment
     *
     * ETS get insured assignment indicator
     */
    public String getInsAssignment( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 162 + ((num-1) * 166), 1 ).trim();
    }


    /**
     * getInsPaymentTrigger
     *
     * ETS get insured payment trigger - 'X'
     */
    public String getInsPaymentTrigger( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 163 + ((num-1) * 166), 1 ).trim();
    }


    /**
     * getInsCCTCode
     *
     * ETS get insured CCT Code
     */
    public String getInsCCTCode( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 164 + ((num-1) * 166), 1 ).trim();
    }


    /**
     * getInsClaimType
     *
     * ETS get insured claim type
     */
    public String getInsClaimType( int num ) {
    	if (( num < 1 ) || ( num > 2 ))  SystemHelpers.seriousError( "ins num out of range" );
        return new String( ets00record, 366 + 165 + ((num-1) * 166), 5 ).trim();
    }



    
    
    
    
    
    
    
    //
    // Get Claim Line Item info from ETS30L records
    //
    //
    //
    //
    
    
    /**
     * getLineItemOffset
     *
     * ETS get line item offset
     */
    private int getLineItemOffset( int num ) {
    	if (( num < 1 ) || ( num > 13 ))  SystemHelpers.seriousError( "line item num out of range" );
        return 22 + ((num-1) * 54);
    }



    /**
     * getLineSrvLocation
     *
     * ETS get line item service location code
     */
    public String getLineSrvLocation( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 0, 2 ).trim();
    }


    /**
     * getLineFromDate
     *
     * ETS get line item service from date
     */
    public String getLineFromDate( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 2, 6 ).trim();
    }


    /**
     * getLineToDate
     *
     * ETS get line item service to date
     */
    public String getLineToDate( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 8, 6 ).trim();
    }


    /**
     * getLineSrvType
     *
     * ETS get line item service type
     */
    public String getLineSrvType( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 14, 1 ).trim();
    }


    /**
     * getLineAnesthesia
     *
     * ETS get line item anesthesia units
     */
    public String getLineAnesthesia( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 15, 3 ).trim();
    }


    /**
     * getLineUnits
     *
     * ETS get line item service units
     */
    public String getLineUnits( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 18, 3 ).trim();
    }


    /**
     * getLineSrvCode
     *
     * ETS get line item service code
     */
    public String getLineSrvCode( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 21, 5 ).trim();
    }


    /**
     * getLineSrvMod1
     *
     * ETS get line item service code modifier 1
     */
    public String getLineSrvMod1( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 26, 2 ).trim();
    }

    /**
     * getLineSrvMod2
     *
     * ETS get line item service code modifier 2
     */
    public String getLineSrvMod2( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 28, 2 ).trim();
    }


    /**
     * getLineDgnCodePtr
     *
     * ETS get line item diagnosis code pointer 1-4
     */
    public String getLineDgnCodePtr( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 30, 4 );
    }


    /**
     * getLineUniqueTOSCode
     *
     * ETS get line item unique TOS code ??
     */
    public String getLineUniqueTOSCode( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 34, 3 ).trim();
    }


    /**
     * getLineProvSubID
     *
     * ETS get line item provider sub ID
     */
    public String getLineProvSubID( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 37, 4 ).trim();
    }


    /**
     * getLineMedUnnecessary
     *
     * ETS get line item service medically unnecessary
     */
    public String getLineMedUnnecessary( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 41, 1 ).trim();
    }


    /**
     * getLineDocumentation
     *
     * ETS get line item service documentation indicator
     */
    public String getLineDocumentation( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 42, 1 ).trim();
    }


    /**
     * getLinePartBBlood
     *
     * ETS get line item part B blood
     */
    public String getLinePartBBlood( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 43, 4 ).trim();
    }


    /**
     * getLineCharge
     *
     * ETS get line item charge
     */
    public String getLineCharge( int num ) {
        return new String( ets30record, getLineItemOffset(num) + 47, 7 ).trim();
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * getRelationRP() - Return patient relationship to RP in String
     * 
     * @param none
     * @return String
     */
//    public String getRelationRP() {
//    	String s = "?";
//    	if ( infoDataStruct[130] == ' ' ) s = " ";
//    	if ( infoDataStruct[130] == 'P' ) s = "P";
//    	return s;
//    }
   
    /**
     * setRelationRP() - Sets the patient relation to RP
     * 
     * @param String relation
     * @return
     */
//    public void setRelationRP( String relation ){
//    	infoDataStruct[130] = ' ';
//    	if ( relation.equals( "P" )) infoDataStruct[130] = 'P';
//    	return;
//    }
    
   
    
    
    
    
    
 
    
    
    
    
    


    


    
    
    
    
    
    //*****************************************************************************************
    //*****************************************************************************************
    //*****************************************************************************************

    
    
 
    
    
    
	// open
    public static EtsRecord open(){
    	
    	// create new ProbTbl object
    	EtsRecord etsRecord = new EtsRecord();

    	// open file, read maxrec, and set current rec to null
		etsRecord.file = RandomFile.open( Pm.getMedPath(), fn_claims, claimRecLength, RandomFile.Mode.READWRITE  );
		etsRecord.numClaims = (int) (etsRecord.file.length() / claimRecLength / 2);
    	System.out.println( "number of claims = " + etsRecord.numClaims );
		etsRecord.claimNum = 0;

		return etsRecord;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "DirPt.getNext() file not open" );

        // set initial record number
    	if ( claimNum <= 0 ){
    		claimNum = 1;
    		
    	// else, increment record number
    	} else {
    		claimNum += 1;
    	}
    	
    	// is this past max claim?
    	if ( claimNum > numClaims ) return false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
    	int rec = ((claimNum - 1) * 2 ) + 1;
    	System.out.println( "rec = " + rec );
    	file.getRecord( ets00record, rec );
        file.getRecord( ets30record, rec + 1 );
		return true;
    }	
    
    //public Rec getRec(){ return ptRec; }
    //public void setRec( Rec rec ){ ptRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
       	file = null;
    	claimNum = 0;
    	numClaims = 0;
    	return true;
    }

    
    
    


    
    
    //*****************************************************************************************
    //*****************************************************************************************
    //*****************************************************************************************
    
    public void dump(){ 
    	StructHelpers.dump( "Ets00Record", ets00record, getRecordLength() );
    	StructHelpers.dump( "Ets30Record", ets30record, getRecordLength() );
        return;
    }


}
