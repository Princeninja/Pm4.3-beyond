package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.pmUser.Role;
import usrlib.*;


/**
 * <p>Title: PAL/MED</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 1983 - 2005</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */
public class DirPt {
	
	private final static String fn_dirpt = "dir-pt.med";
	private final static String fn_infopt = "info-pt.med";
	private final static String fn_infopt2 = "info-pt2.med";
	private final static String fn_infonok = "info-nk.med";

    public byte[] dataStruct;
    public byte[] infoDataStruct;
    public byte[] info2Struct;
    public byte[] nkdataStruct;
    private boolean read_info2 = false;
    private boolean write_info2 = false;
    
    private Rec ptRec = null;
    
    private RandomFile file;
    private RandomFile file2;
    private RandomFile file3;
    private Rec maxrec;
    
    
    
	
	// CONSTRUCTORS
	
    public DirPt() {
    	allocateBuffer();
    }
    
    public DirPt( Rec ptRec ){
        // allocate space
    	allocateBuffer();

    	// read pt
    	readPt( ptRec );
    	this.ptRec = ptRec;
    }
    
    public DirPt( int ptrec ){

        // allocate space
    	allocateBuffer();

    	// read pt
    	readPt( new Rec( ptrec ));
    	this.ptRec = new Rec( ptrec );
    }
    
    
    
    
    
    private boolean readPt( Rec ptRec ){

        RandomFile file;

    	// read dir-pt.med
        file = new RandomFile( Pm.getMedPath(), fn_dirpt, DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file.getRecord( this.dataStruct, ptRec.getRec());
        file.close();

        // read info-pt.med
        file = new RandomFile( Pm.getMedPath(), fn_infopt, DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );
        file.getRecord( this.infoDataStruct, ptRec.getRec());
        file.close();
        
        // read info-nk.med
        file = new RandomFile( Pm.getMedPath(), fn_infonok, DirPt.getInfoNokRecordLength(), RandomFile.Mode.READONLY );
        file.getRecord( this.nkdataStruct, ptRec.getRec());
        file.close();
        
        // flag that we need to read this
        this.read_info2 = false;

        this.ptRec = ptRec;
        return true;
    }


    private boolean readInfo2(){

    	// read info-pt.med
	    RandomFile file = new RandomFile( Pm.getMedPath(), fn_infopt2, DirPt.getInfo2RecordLength(), RandomFile.Mode.READONLY );
	    file.getRecord( this.info2Struct, ptRec.getRec());
	    file.close();
	    read_info2 = true;
	    return true;
	}
   
    
    
    
    
    
    public Rec newPt(){
    	
    	Rec rec;		// patient rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getMedPath(), fn_dirpt, DirPt.getRecordLength());
    	RandomFile.writeRec( rec, this.infoDataStruct, Pm.getMedPath(), fn_infopt, DirPt.getInfoRecordLength());
    	RandomFile.writeRec( rec, this.nkdataStruct, Pm.getMedPath(), fn_infonok, DirPt.getInfoNokRecordLength());
    	RandomFile.writeRec( rec, this.info2Struct, Pm.getMedPath(), fn_infopt2, DirPt.getInfo2RecordLength());
    	this.ptRec = rec;
    	return rec;
    }
    
    
    
    public boolean writePt( Rec ptRec ){
    	
    	writeDirPt( ptRec );
    	writeInfoPt( ptRec );
    	writeInfoNok( ptRec );
    	if ( write_info2 ) writeInfo2();
    	this.ptRec = ptRec;
    	return true;
    }

    public boolean writeDirPt( Rec ptRec ){

    	// write dir-pt.med
        RandomFile.writeRec( ptRec, this.dataStruct, Pm.getMedPath(), fn_dirpt, DirPt.getRecordLength());
    	this.ptRec = ptRec;
        return true;
    }


    public boolean writeInfoPt( Rec ptRec ){

        // write info-pt.med
        RandomFile.writeRec( ptRec, this.infoDataStruct, Pm.getMedPath(), fn_infopt, DirPt.getInfoRecordLength());
    	this.ptRec = ptRec;
        return true;
    }
    
    public boolean writeInfoNok( Rec ptRec ){

        // write info-nok.med
        RandomFile.writeRec( ptRec, this.nkdataStruct, Pm.getMedPath(), fn_infonok, DirPt.getInfoNokRecordLength());
    	this.ptRec = ptRec;
        return true;
    }
    
    public boolean writeInfo2(){

        // write info-pt.med
        RandomFile.writeRec( ptRec, this.info2Struct, Pm.getMedPath(), fn_infopt2, DirPt.getInfo2RecordLength());
        return true;
    }
    
    
    
    /**
     * getRecordLength
     */
    public static int getRecordLength() {
        return ( 176 );
    }

    /**
     * getInfoRecordLength
     */
    public static int getInfoRecordLength() {
        return ( 160 );
    }
    
    /**
     * getInfoNokRecordLength
     */
    public static int getInfoNokRecordLength() {
        return ( 128 );
    }

    /**
     * getInfo2RecordLength
     */
    public static int getInfo2RecordLength() {
        return ( 128 );
    }

    
    
    
    
    
   
    

    
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[176];
        infoDataStruct = new byte[160];
        nkdataStruct = new byte[128];
        info2Struct = new byte[128];
    }

    public void setDataStruct(byte[] dataStruct) {
        this.dataStruct = dataStruct;
    }

    public byte[] getDataStruct() {
        return dataStruct;
    }



    /**
     * getName
     */
    public Name getName() {
        return new Name( dataStruct, 0 );
    }
    
    /**
     * setName() - Set name into DirPt struct
     * 
     * @param Name name
     * @return void
     */
    public void setName( Name name ) {
    	name.toData( dataStruct, 0 );
    }

    
    
    
    
    
    /**
     * getPtNumber
     *
     * @param index int       // index for pt numbers 1, 2, or 3
     * note - handles 0 or > 3 as 1
     */
    public String getPtNumber(int index) {

        switch ( index ){
        default:
        case 1:
            return new String( dataStruct, 64, 12 ).trim();
        case 2:
            return new String( dataStruct, 76, 12 ).trim();
        case 3:
            return new String( dataStruct, 88, 12 ).trim();
        }
    }

    /**
     * getPtNumber
     *
     * Always returns the first pt number
     */
    public String getPtNumber() {
        return new String( dataStruct, 64, 12 ).trim();
    }

    /**
     * setPtNumber
     *
     * @param String s
     * @param index int       // index for pt numbers 1, 2, or 3
     * note - handles 0 or > 3 as 1
     * @return void
     */
    public void setPtNumber( String s, int index ){

        switch ( index ){
        default:
        case 1:
            StructHelpers.setStringPadded( s, dataStruct, 64, 12 );
            break;
         case 2:
            StructHelpers.setStringPadded( s, dataStruct, 76, 12 );
            break;
        case 3:
            StructHelpers.setStringPadded( s, dataStruct, 88, 12 );
            break;
        }
    }

       
    
    
    
    /**
     * getBirthdate
     */
    public Date getBirthdate() {
        byte bcd[] = new byte[4];
        bcd[0] = dataStruct[100];
        bcd[1] = dataStruct[101];
        bcd[2] = dataStruct[102];
        bcd[3] = dataStruct[103];
        Date birthdate = new Date( bcd );
        return ( birthdate );
    }
    
    public void setBirthdate( usrlib.Date date ){
    	date.toBCD( dataStruct, 100 );
    }
    
    
    
    /**
     * get patient creation date
     */
    public Date getPCdate() {
        byte bcd[] = new byte[4];
        bcd[0] = dataStruct[104];
        bcd[1] = dataStruct[105];
        bcd[2] = dataStruct[106];
        bcd[3] = dataStruct[107];
        Date birthdate = new Date( bcd );
        return ( birthdate );
    }
    
    public void setPCdate( usrlib.Date date ){
    	date.toBCD( dataStruct, 104 );
    }
    
    
    
    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[127] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[127] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[127] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[127] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[127] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[127] = (byte)( valid.getCode() & 0xff ); }
    
// 	RECORD	Ppo;			/*  ppo records   */
    
    /**
     * getPpo
     *
     * @param index int       // index for Ppo 1, 2, or 3
     * note - handles 0 or > 3 as 1
     */
    public Rec getPpo(int index) {

        switch ( index ){
        default:
        case 1:
            return  Rec.fromInt( dataStruct, 128 );
        case 2:
            return Rec.fromInt( dataStruct, 132 );
        case 3:
            return Rec.fromInt( dataStruct, 136 );
        }
    }


    /**
     * setPpo
     *
     * @param Rec rec
     * @param index int       // index for Ppo numbers 1, 2, or 3
     * note - handles 0 or > 3 as 1
     * @return void
     */
    public void setPpo( Rec rec, int index ){

        switch ( index ){
        default:
        case 1:
            rec.toInt( dataStruct, 128 );
            break;
         case 2:
        	 rec.toInt( dataStruct, 132 );
            break;
        case 3:
        	rec.toInt( dataStruct, 136 );
            break;
        }
    } 
    
// 	RECORD	Ppoinfo;			/*  ppo info  */
    
    /**
     * getPpoinfo
     *
     * @param index int       // index for Ppoinfo 1, 2, or 3
     * note - handles 0 or > 3 as 1
     */
    public Rec getPpoinfo(int index) {

        switch ( index ){
        default:
        case 1:
            return  Rec.fromInt( dataStruct, 140 );
        case 2:
            return Rec.fromInt( dataStruct, 144 );
        case 3:
            return Rec.fromInt( dataStruct, 148 );
        }
    }


    /**
     * setPpoinfo
     *
     * @param Rec rec
     * @param index int       // index for Ppoinfo numbers 1, 2, or 3
     * note - handles 0 or > 3 as 1
     * @return void
     */
    public void setPpoinfo( Rec rec, int index ){

        switch ( index ){
        default:
        case 1:
            rec.toInt( dataStruct, 140 );
            break;
         case 2:
        	 rec.toInt( dataStruct, 144 );
            break;
        case 3:
        	rec.toInt( dataStruct, 148 );
            break;
        }
    } 

    
    /**
     * getSSN - Returns SSN as string WITH dashes
     * 
     * @param none
     * @return String ssn
     */
    public String getSSNDSH() {
    	return Decoders.fromBCDSSNDSH(infoDataStruct, 88);
    }
    
    /**
     * getSSN - Returns SSN as string WITH dashes
     * 
     * @param none
     * @return String ssn
     */
    public String getSSN() {
    	return Decoders.fromBCDSSN(infoDataStruct, 88, true );
    }

    /**
     * getSSN - Returns SSN as string with/without dashes
     * 
     * @param dashes		flag to put in dashes
     * @return String ssn
     */
    public String getSSN( boolean dashes ) {
    	return Decoders.fromBCDSSN(infoDataStruct, 88, dashes );
    }
    
    /**
     * setSSN - Set SSN into DirPt
     * 
     * @param String ssn - SSN string to decode and store into DirPt
     * @return void
     */
    public void setSSN( String ssn ){
    	
    	Decoders.toBCDSSN( ssn, infoDataStruct, 88 );
    	return;
    }
    
    
    
    
    
    /**
     * Return patient sex enum - with some validation
     * 
     * @param none
     * @return enum sex "MALE", "FEMALE", "UNKNOWN"
     */ 
    public palmed.Sex getSex() {
    	palmed.Sex s = palmed.Sex.UNKNOWN;
    	if ( dataStruct[126] == 'M' ) s = palmed.Sex.MALE;
    	if ( dataStruct[126] == 'F' ) s = palmed.Sex.FEMALE;
    	return s;
    }
    
    /**
     * setSex() - Set sex into DirPt
     * 
     * @param sex - enum Sex "MALE" or "FEMALE"
     * @return void
     */
    public void setSex( palmed.Sex sex ){
    	dataStruct[126] = ' ';
    	if ( sex == palmed.Sex.MALE ) dataStruct[126] = 'M';
    	if ( sex == palmed.Sex.FEMALE) dataStruct[126] = 'F';
    	return;
    }
    
    
    
    
    
    /**
     * getRace() - Return patient race
     * 
     * @param none
     * @return palmed.Race
     */
    public palmed.Race getRace() {
    	Race r = palmed.Race.get( "" + (char) infoDataStruct[128] );
    	if ( r == null ) r = palmed.Race.UNSPECIFIED;
    	return r;
    }
    
    /**
     * setRace() - Set patient race
     * 
     * @param Race race
     * @return void
     */
    public void setRace( palmed.Race race ){
    	infoDataStruct[128] = (byte) ( race.getAbbr().charAt( 0 ) & 0xff );    	
    	if ( race == palmed.Race.UNSPECIFIED ) infoDataStruct[128] = ' ';
    	return;
    }
    
    
   
    
    /**
     *  getMarital - Return patient marital status in String
     *  
     *  @param none
     * @return String 
     */
    public String getMarital() {
    	String s = "?";
    	if ( infoDataStruct[129] == ' ' ) s = " "; 
    	if ( infoDataStruct[129] == 'M' ) s = "M"; 
    	if ( infoDataStruct[129] == 'D' ) s = "D"; 
    	if ( infoDataStruct[129] == 'W' ) s = "W"; 
    	if ( infoDataStruct[129] == 'X' ) s = "X"; 
    	if ( infoDataStruct[129] == 'S' ) s = "S"; 
    	return s;
    }
    
    public String getMaritaltxt(){
    	
    	String st = "?";
    	
    	if ( infoDataStruct[129] == ' ' )  st = " ";
    	if ( infoDataStruct[129] == 'M' )  st = "Married";
    	if ( infoDataStruct[129] == 'D' )  st = "Divorced";
    	if ( infoDataStruct[129] == 'W' )  st = "Widowed";
    	if ( infoDataStruct[129] == 'X' )  st = "Separated";
    	if ( infoDataStruct[129] == 'S' )  st = "Single";
    
    	return st;
    	
    }
    
    /**
     * setMarital() - Sets the patient marital status
     * 
     * @param String marital
     * @return
     */
    public void setMarital( String marital ){
    	infoDataStruct[129] = ' ';
    	if ( marital.equals( "M" )) infoDataStruct[129] = 'M';
    	if ( marital.equals( "D" )) infoDataStruct[129] = 'D';
    	if ( marital.equals( "W" )) infoDataStruct[129] = 'W';
    	if ( marital.equals( "X" )) infoDataStruct[129] = 'X';
    	if ( marital.equals( "S" )) infoDataStruct[129] = 'S';
    	return;
    }
    
   
    
    
    
    
    
    
    /**
     * getRelationRP() - Return patient relationship to RP in String
     * 
     * @param none
     * @return String
     */
    public String getRelationRP() {
    	String s = "?";
    	if ( infoDataStruct[130] == ' ' ) s = " ";
    	if ( infoDataStruct[130] == 'P' ) s = "P";
    	return s;
    }
   
    /**
     * setRelationRP() - Sets the patient relation to RP
     * 
     * @param String relation
     * @return
     */
    public void setRelationRP( String relation ){
    	infoDataStruct[130] = ' ';
    	if ( relation.equals( "P" )) infoDataStruct[130] = 'P';
    	return;
    }
    
   
    
    
    
    
    /**
     * getRelationNOK() - Return patient relationship to NOK in String
     * 
     * @param none
     * @return String
     */
    public String getRelationNOK() {
    	String s = "?";
    	if ( infoDataStruct[131] == ' ' ) s = " ";
    	if ( infoDataStruct[131] == 'P' ) s = "P";
    	return s;
    }
    
    /**
     * setRelationNOK() - Sets the patient relation to NOK
     * 
     * @param String relation
     * @return
     */
    public void setRelationNOK( String relation ){
    	infoDataStruct[131] = ' ';
    	if ( relation.equals( "P" )) infoDataStruct[131] = 'P';
    	return;
    }
    
   
    
    
    
    /**
     * Return patient Language enum - with some validation
     * 
     * @param none
     * @return enum Language UNSPECIFIED, ENGLISH, SPANISH, FRENCH
     */ 
    public palmed.Language getLanguage() {
    	palmed.Language l = palmed.Language.get( infoDataStruct[132] & 0xff );
    	if ( l == null ) l = palmed.Language.UNSPECIFIED;
    	return l;
    }
    
    /**
     * setLanguage() - Set Language into DirPt
     * 
     * @param Language - enum Language UNSPECIFIED, ENGLISH, SPANISH, FRENCH
     * @return void
     */
    public void setLanguage( palmed.Language lang ){
    	infoDataStruct[132] = (byte) lang.getCode();
    	return;
    }
    
    
    

    
    /**
     * Return patient Ethnicity enum - with some validation
     * 
     * @param none
     * @return enum Ethnicity UNSPECIFIED, ENGLISH, SPANISH, FRENCH
     */ 
    public palmed.Ethnicity getEthnicity() {
    	palmed.Ethnicity e = palmed.Ethnicity.get( infoDataStruct[133] & 0xff );
    	if ( e == null ) e = palmed.Ethnicity.UNSPECIFIED;
    	return e;
    }
    
    /**
     * setEthnicity() - Set Ethnicity into DirPt
     * 
     * @param Ethnicity - enum Ethnicity UNSPECIFIED, ENGLISH, SPANISH, FRENCH
     * @return void
     */
    public void setEthnicity( palmed.Ethnicity e ){
    	infoDataStruct[133] = (byte) e.getCode();
    	return;
    }
    
    
    

    

    
    // Return Unused field from info-pt.med
    public String getUnused() {
    	return new String( infoDataStruct, 132, 28 ).trim();
    }

    /**
     * getAddress()
     * 
     *  Returns an Address object read from the Info-pt file.
     *  
     *  @param none
     *  @return Address
     */
        public Address getAddress() {
    	return new Address( infoDataStruct, 0 );
    }
        
    /**
     * setAddress() - Set an address into infpPt
     * 
     * @param Address
     * @return void
     */
     public void setAddress( Address address ){
    	 address.toData( infoDataStruct, 0 );
    	 return;
     }

     
     
     
    
    /**
     * getOccupation()
     *
     * Gets the patient's occupation from info-pt.med
     * 
     * @param none
     * @return String
     */
    public String getOccupation() {
        return new String( infoDataStruct, 93, 20 ).trim();
    }

    /**
     * setOccupation()
     *
     * Sets the patient's occupation into InfoPt
     * 
     * @param String
     * @return void
     */
    public void setOccupation( String s ) {
        StructHelpers.setStringPadded( s, infoDataStruct, 93, 20 );
    }
     
    
    /**
     * getNOKFirstName()
     *
     * Gets the patient's NOK first name from info-pt.med
     * 
     * @param none
     * @return String
     */
    public String getNOKFirstName() {
        return new String( infoDataStruct, 113, 10 ).trim();
    }
    
    /**
     * setNOKFirstName()
     *
     * Sets the patient's NOK first name in info-pt.med
     * 
     * @param Sring
     * @return void
     */
    public void setNOKFirstName( String s ) {
        StructHelpers.setStringPadded( s, infoDataStruct, 113, 10 );   }
    
  
    /**
     * getNOKMiddleInitial()
     *
     * Gets the patient's NOK middle initial from info-pt.med
     * 
     * @param none
     * @return String
     */
    public String getNOKMiddleInitial() {
        return new String( infoDataStruct, 123, 1 ).trim();
    }

    /**
     * setNOKMiddleInitial()
     *
     * Sets the patient's NOK middle initial to info-pt.med
     * 
     * @param String
     * @return void
     */
    public void setNOKMiddleInitial(String s) {
       StructHelpers.setString(s, infoDataStruct, 123, 1 );
    }
    
    
    /**
     * getNokRec()
     *
     * Gets the patient's Nok record number
     * 
     * @param none
     * @return Rec
     */
    public Rec getNokRec(){
        return Rec.fromInt( dataStruct, 112 );
    }

    /**
     * setNokRec()
     *
     * Sets the patient's Nok record number
     * 
     * @param Rec
     * @return void
     */
    public void setNokRec( Rec rec ) {
        rec.toInt( dataStruct, 112 );
        return;
    }

    
    
    /**
     * getMedRec()
     *
     * Gets the patient's MedPt record number
     * 
     * @param none
     * @return Rec
     */
    public Rec getMedRec(){
        return Rec.fromInt( dataStruct, 120 );
    }

    /**
     * setMedRec()
     *
     * Sets the patient's MedPt record number
     * 
     * @param Rec
     * @return void
     */
    public void setMedRec( Rec rec ) {
        rec.toInt( dataStruct, 120 );
        return;
    }

    // INFONK content
    /*  INFO-NK structure definitions  

    typedef	struct {
    		ADDRESS	address;		/*  nok address & phones  */

    //		char	nk_lst[15];		/*  nok last name  */
    //		char	ssn[5];			/*  nok social security no  */
    //		char	unused[20];		/*  unused space  */

    //	} INFONK;
    
    // #define	SZ_NKINFO	sizeof( INFONK )	/*  size of INFONK  */

    // sizecheck( 128, sizeof( INFONK ), "INFONK" );
    
    /**
     * getAddress()
     * 
     *  Returns an Address object read from the Info-nk file.
     *  
     *  @param none
     *  @return Address
     */
        public Address getNokAddress() {
    	return new Address( nkdataStruct, 0 );
    }
        
    /**
     * setAddress() - Set an address into infpPt
     * 
     * @param Address
     * @return void
     */
     public void setNokAddress( Address address ){
    	 address.toData( nkdataStruct, 0 );
    	 return;
     }
    
     /**
      * getNOKLastName()
      *
      * Gets the patient's NOK last name from info-nk.med
      * 
      * @param none
      * @return String
      */
     public String getNOKLastName() {
         return new String( nkdataStruct, 88, 15 ).trim();
     }
     
     /**
      * setNOKLastName()
      *
      * Sets the patient's NOK last name in info-nk.med
      * 
      * @param Sring
      * @return void
      */
     public void setNOKLastName( String s ) {
         StructHelpers.setStringPadded( s, nkdataStruct, 88, 15 );   }
     
   
     /**
      * getSSN - Returns SSN as string WITH dashes
      * 
      * @param none
      * @return String ssn
      */
     public String getNokSSNDSH() {
     	return Decoders.fromBCDSSNDSH(nkdataStruct, 103);
     }
     
     /**
      * getSSN - Returns SSN as string WITH dashes
      * 
      * @param none
      * @return String ssn
      */
     public String getNokSSN() {
     	return Decoders.fromBCDSSN(nkdataStruct, 103, true );
     }

     /**
      * getSSN - Returns SSN as string with/without dashes
      * 
      * @param dashes		flag to put in dashes
      * @return String ssn
      */
     public String getNokSSN( boolean dashes ) {
     	return Decoders.fromBCDSSN(nkdataStruct, 103, dashes );
     }
     
     /**
      * setSSN - Set SSN into INFONK
      * 
      * @param String ssn - SSN string to decode and store into infonk
      * @return void
      */
     public void setNokSSN( String ssn ){
     	
     	Decoders.toBCDSSN( ssn, nkdataStruct, 103 );
     	return;
     }
     
     // Return Unused field from infonk.med
     public String getNokUnused() {
     	return new String( nkdataStruct, 108, 20 ).trim();
     }
    
    
    
    //****
    
    //*****************************************************************************************
    //*****************************************************************************************
    //*****************************************************************************************

    /**
     * getEmail()
     *
     * Gets the patient's Email address from InfoPt2
     * 
     * @param none
     * @return String
     */
    public String getEmail(){
    	if ( ! read_info2 ) readInfo2();
        return new String( info2Struct, 0, 40 ).trim();
    }
    
    /**
     * setEmail()
     *
     * Sets the patient's Email address into InfoPt2
     * 
     * @param String
     * @return void
     */
    public void setEmail( String s ) {
        StructHelpers.setStringPadded( s, info2Struct, 0, 40 );
    	write_info2 = true;
    }
    
    
    
    /**
     * getAllowEmail()
     *
     * Reads InfoPt2 to see if patient email communication is allowed.
     * 
     * @param none
     * @return boolean
     */
    public boolean getAllowEmail(){
    	if ( ! read_info2 ) readInfo2();
        return ( info2Struct[45] == ((byte)( 'Y' ))) ? true: false;
    }
    
    /**
     * setAllowEmail()
     *
     * Sets the patient email communication permission into InfoPt2
     * 
     * @param boolean
     * @return void
     */
    public void setAllowEmail( boolean b ) {
        info2Struct[45] = (byte) (( b ) ? 'Y': 'N');
    	write_info2 = true;
        return;
    }
    
    
 
    
    
    /**
     * getPIN()
     *
     * Gets the patient's PIN from InfoPt2
     * 
     * @param none
     * @return String
     */
    public String getPIN(){
    	if ( ! read_info2 ) readInfo2();
        return new String( info2Struct, 40, 4 ).trim();
    }
    
    /**
     * setPIN()
     *
     * Sets the patient's PIN into InfoPt2
     * 
     * @param String
     * @return void
     */
    public void setPIN( String s ) {
        StructHelpers.setStringPadded( s, info2Struct, 40, 4 );
    	write_info2 = true;
    }
    
    
    
    

    /**
     * getAllowPtPortal()
     *
     * Reads InfoPt2 to see if patient portal access is allowed.
     * 
     * @param none
     * @return boolean
     */
    public boolean getAllowPtPortal(){
    	if ( ! read_info2 ) readInfo2();
        return ( info2Struct[44] == ((byte)( 'Y' ))) ? true: false;
    }
    
    /**
     * setAllowPtPortal()
     *
     * Sets the patient portal access permission into InfoPt2
     * 
     * @param boolean
     * @return void
     */
    public void setAllowPtPortal( boolean b ) {
        info2Struct[44] = (byte) (( b ) ? 'Y': 'N');
    	write_info2 = true;
        return;
    }
    
    
 
    
    
    
	// open
    public static DirPt open(){
    	
    	// create new ProbTbl object
    	DirPt dirpt = new DirPt();

    	// open file, read maxrec, and set current rec to null
		dirpt.file = RandomFile.open( Pm.getMedPath(), fn_dirpt, getRecordLength(), RandomFile.Mode.READWRITE  );
		dirpt.maxrec = new Rec( dirpt.file.getMaxRecord());
		dirpt.ptRec = null;

        dirpt.file2 = new RandomFile( Pm.getMedPath(), fn_infopt, getInfoRecordLength(), RandomFile.Mode.READWRITE );
        dirpt.file3 = new RandomFile( Pm.getMedPath(), fn_infonok, getInfoNokRecordLength(), RandomFile.Mode.READWRITE );
        
		return dirpt;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "DirPt.getNext() file not open" );

        // set initial record number
    	if ( ptRec == null ){
    		ptRec = new Rec( 2 );
    	} else if ( ptRec.getRec() < 2 ){
    		ptRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		ptRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( ptRec.getRec() > maxrec.getRec()) return false;
    	
    	// reset some things
    	read_info2 = false;
    	
    	//System.out.println( ptRec.toString());
 
    	// read record
		file.getRecord( dataStruct, ptRec );
        file2.getRecord( infoDataStruct, ptRec);
        file3.getRecord( nkdataStruct, ptRec);
		return true;
    }	
    
    public Rec getRec(){ return ptRec; }
    public void setRec( Rec rec ){ ptRec = rec; }
    
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
    	if ( file2 != null ) file2.close();
    	if ( file3 != null ) file3.close();
       	file = null;
       	file2 = null;
       	file3 = null;
    	ptRec = null;
    	maxrec = null;
    	return true;
    }

    
    
    


    
    
    //*****************************************************************************************
    //*****************************************************************************************
    //*****************************************************************************************
    
    public void dump(){ 
    	StructHelpers.dump( "DirPt", dataStruct, getRecordLength() );
    	StructHelpers.dump( "InfoPt", infoDataStruct, getInfoRecordLength() );
    	StructHelpers.dump( "InfoNok", nkdataStruct, getInfoNokRecordLength() );
        return;
    }


}
