package palmed;

import org.zkoss.zul.Listbox;

import usrlib.Address;
import usrlib.Name;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.Validity;
import usrlib.ZkTools;



/* Offset in Pcn


Abbr 0
Name 10
flags 50
home 52
office 140

License 228 [4][20]
	State 228 [2]
	Num 230 [14]
	Date 244  [4]
	
                
Specialty 308  [3][32]
NPI 404
Unused 414
Numbers 436  [16][16]
EtsProvID 692
EtsProvSubID 708

MedicareEffective 712
MedicareAppliedFor 716
MedicaidEffective 720
MedicaidAppliedFor 724

MedicalSchool 728
YearGraduated 744
Birthdate 746
DefSupPcnRec 750

CLIA 752
MedicareGroup 768
OtherNumbers 784

LastName 976
FirstName 996
MiddleInitial 1012
Suffix 1013

MedicareParProv 1019
MedicaidParProv 1020
BCBSParProv 1021
NeedsSupervisor 1022
Valid 1023
*/    



public class Prov {

	
	private final static String fn_pcn = "physin.ovd";
	private final static String fn_pcnsmry = "pcn%02d.med";

    public byte[] dataStruct;
    
    
    public enum Numbers {
    	FED_TAX(0),
    	STATE_TAX(1),
    	LOCAL_TAX(2),
    	SSN(3),
    	MEDICARE(4),
    	MEDICAID(5),
    	BLUE_CROSS(6),
    	BLUE_SHIELD(7),
    	DEA(8),
    	FAA(9),
    	MED_ED(10),
    	OTHER1(11),
    	OTHER2(12),
    	OTHER3(13),
    	OTHER4(14),
    	UPIN(15),
    	ETS_PROV_ID(16),
    	ETS_SUB_ID(17),
    	CLIA(18),
    	MEDICARE_GROUP(19),
    	
    	NPI(33);
    	
    	private int code;
    	
    	private Numbers( int code ){ this.code = code; }
    	public int getCode() { return ( this.code ); }
    }
    
    
	
	// CONSTRUCTORS
	
    public Prov() {
    	allocateBuffer();
    }
    
    public Prov( Rec rec ){
        // allocate space
    	allocateBuffer();

    	// read pt
    	read( rec );

    }
    
    
    
    
    
    
    private boolean read( Rec rec ){

    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Prov.read() bad rec" );

    	// read provider record
    	RandomFile.readRec( rec, this.dataStruct, Pm.getOvdPath(), fn_pcn, getRecordLength());
        return true;
    }
    
    
    
    
    
    
    
    public Rec newProv(){
    	
    	Rec rec;		// prov rec
    	
    	rec = RandomFile.writeRec( null, this.dataStruct, Pm.getOvdPath(), fn_pcn, getRecordLength());
    	return rec;
    }
    
    
    
    public boolean write( Rec rec ){
    	
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "Prov.write() bad rec" );

    	// write physin.ovd
        RandomFile.writeRec( rec, this.dataStruct, Pm.getOvdPath(), fn_pcn, getRecordLength());
        return true;
    }


    
    
    
    

    
    
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[1024];
    }

    public void setDataStruct(byte[] dataStruct) {
        this.dataStruct = dataStruct;
    }

    public byte[] getDataStruct() {
        return dataStruct;
    }

    /**
     * getRecordLength
     */
    public static int getRecordLength() {
        return ( 1024 );
    }

    
    
    
    
    
  
    
    
    
    
    /**
     * getAbbr()
     *
     * Gets the provider abbreviation.
     * 
     * @param none
     * @return String
     */
    public String getAbbr() {
        return new String( dataStruct, 0, 10 ).trim();
    }

    /**
     * setAbbr()
     *
     * Sets the provider abbreviation.
     * 
     * @param String
     * @return void
     */
    public void setAbbr( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 0, 10 );
    }

    /**
     * getAbbr()
     *
     * Static version reads provider record and gets the provider abbreviation.
     * 
     * @param rec
     * @return String
     */
    public static String getAbbr( Rec rec ) {
    	String str = "";
    	if (( rec != null ) && ( rec.getRec() > 1 ))
    		str = (new Prov( rec )).getAbbr();
        return str;
    }

    
    
    
    
    

    /**
     * getName()
     *
     * Gets the provider name.
     * 
     * @param none
     * @return String
     */
    public String getName() {
        return new String( dataStruct, 10, 40 ).trim();
    }

    /**
     * setName()
     *
     * Sets the provider name.
     * 
     * @param String
     * @return void
     */
    public void setName( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 10, 40 );
    }

    /**
     * getName()
     *
     *  Static version reads provider record and gets the provider name.
     * 
     * @param rec
     * @return String
     */
    public static String getName( Rec rec ) {
        return new Prov( rec ).getName();
    }

    
    
    
    
    




/*
    /**
     * getName
     *
    public Name getName() {
        return new Name( dataStruct, 0 );
    }
    
    /**
     * setName() - Set name into DirPt struct
     * 
     * @param Name name
     * @return void
     *
    public void setName( Name name ) {
    	name.toData( dataStruct, 0 );
    }
    
*/
    
    
    

    
    /**
     * getProvNumber
     *
     * @param index int
     * note - handles < 0 or > 16 as 0
     */
    public String getProvNumber(int index) {

    	if (( index < 0 ) || ( index > 33 )) index = 0;
    	
    	if ( index == 16 ) return StructHelpers.getString( dataStruct, 692, 16 ).trim();	// ETS Provider ID
    	if ( index == 17 ) return StructHelpers.getString( dataStruct, 708, 4 ).trim();		// ETS Provider Sub ID
    	if ( index == 18 ) return StructHelpers.getString( dataStruct, 752, 16 ).trim();	// CLIA #
    	if ( index == 19 ) return StructHelpers.getString( dataStruct, 768, 16 ).trim();	// Medicare Group #
    	if ( index == 33 ) return StructHelpers.getString( dataStruct, 404, 10 ).trim();	// NPI #
    	
    	if (( index >= 0 ) && ( index < 16 ))
            return StructHelpers.getString( dataStruct, 436+(index*16), 16 ).trim();
    		
    	if (( index >= 20 ) && ( index < 32 ))
            return StructHelpers.getString( dataStruct, 784+((index-20)*16), 16 ).trim();

    	return "";
    }
    
    

    /**
     * getProvNumber
     *
     * Always returns the first prov number
     */
    public String getProvNumber() {
        return getProvNumber(1);
    }
    
    public String getProvNumber( Prov.Numbers num ){ return( getProvNumber( num.getCode())); }
    
    /**]
     * setProvNumber() - Set the provider numbers into data struct
     * 
     */
    public void setProvNumber( int index, String s ){
  
    	if ( index == 16 ){ StructHelpers.setStringPadded(s, dataStruct, 692, 16 ); return; }	// ETS Provider ID
    	if ( index == 17 ){ StructHelpers.setStringPadded(s, dataStruct, 708, 4 ); return; }	// ETS Provider Sub ID
    	if ( index == 18 ){ StructHelpers.setStringPadded(s, dataStruct, 752, 16 ); return; }	// CLIA #
    	if ( index == 19 ){ StructHelpers.setStringPadded(s, dataStruct, 768, 16 ); return; }	// Medicare Group #
    	if ( index == 33 ){ StructHelpers.setStringPadded(s, dataStruct, 404, 10 ); return; }	// NPI #
    	
    	if ( index < 0 ) index = 0;
    	if ( index > 15 ) index = 0;

    	StructHelpers.setStringPadded(s, dataStruct, 436+(index*16), 16 );
    	return;
    }
    
    public void setProvNumber( Prov.Numbers num, String s ){ setProvNumber( num.getCode(), s ); }
    
    
    
    
    
    
    

    /**
     * getNPI
     *
     * @param none
     */
    public String getNPI() {

        return StructHelpers.getString( dataStruct, 404, 10 ).trim();
    }

   /**]
     * setNPI() - Set the provider numbers into data struct
     * 
     */
    public void setNPI( String s ){

    	StructHelpers.setStringPadded(s, dataStruct, 404, 10 );
    	return;
    }
    
    
    
    /**
     * getDEA
     *
     * @param none
     */
    public String getDEA() { return this.getProvNumber( Numbers.DEA ); }

   /**]
     * setDEA() - Set the provider numbers into data struct
     * 
     */
    public void setDEA( String s ){ this.setProvNumber( Numbers.DEA, s); }
    
    
    
    
    
    
    
    
    

    /**
     * getBirthdate
     */
    public usrlib.Date getBirthdate() {
        return ( new usrlib.Date( dataStruct, 746 ));
    }
    
    /**
     * setBirthdate
     */
    public void setBirthdate( usrlib.Date date ){
    	date.toBCD( dataStruct, 746 );
    }
    
    
    
    
    

    
    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[1023] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[1023] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[1023] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[1023] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[1023] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[1023] = (byte)( valid.getCode() & 0xff ); }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    
    
    /**
     * getMedicareParProv() - Return Medicare Participating Provider status as "Y", "N", or " "
     * 
     * @param none
     * @return String
     */
    public String getMedicareParProv() {
    	String s = " ";
    	if ( dataStruct[1019] == ' ' ) s = " ";
    	if ( dataStruct[1019] == 'Y' ) s = "Y";
    	if ( dataStruct[1019] == 'N' ) s = "N";
    	return s;
    }
   
    /**
     * setMedicareParProv() - Sets the Medicare Participating Provider status as "Y", "N", or " "
     * 
     * @param String s
     * @return
     */
    public void setMedicareParProv( String s ){
    	dataStruct[1019] = ' ';
    	if ( s.equals( "Y" )) dataStruct[1019] = 'Y';
    	if ( s.equals( "N" )) dataStruct[1019] = 'N';
    	return;
    }
    
   
    
    
    
    
    /**
     * getMedicaidParProv() - Return Medicaid Participating Provider status as "Y", "N", or " "
     * 
     * @param none
     * @return String
     */
    public String getMedicaidParProv() {
    	String s = " ";
    	if ( dataStruct[1020] == ' ' ) s = " ";
    	if ( dataStruct[1020] == 'Y' ) s = "Y";
    	if ( dataStruct[1020] == 'N' ) s = "N";
    	return s;
    }
   
    /**
     * setMedicaidParProv() - Sets the Medicaid Participating Provider status as "Y", "N", or " "
     * 
     * @param String s
     * @return
     */
    public void setMedicaidParProv( String s ){
    	dataStruct[1020] = ' ';
    	if ( s.equals( "Y" )) dataStruct[1020] = 'Y';
    	if ( s.equals( "N" )) dataStruct[1020] = 'N';
    	return;
    }
    
   
    
    
    
    
    /**
     * getBCBSParProv() - Return BCBS Participating Provider status as "Y", "N", or " "
     * 
     * @param none
     * @return String
     */
    public String getBCBSParProv() {
    	String s = " ";
    	if ( dataStruct[1021] == ' ' ) s = " ";
    	if ( dataStruct[1021] == 'Y' ) s = "Y";
    	if ( dataStruct[1021] == 'N' ) s = "N";
    	return s;
    }
   
    /**
     * setBCBSParProv() - Sets the Medicaid Participating Provider status as "Y", "N", or " "
     * 
     * @param String s
     * @return
     */
    public void setBCBSParProv( String s ){
    	dataStruct[1021] = ' ';
    	if ( s.equals( "Y" )) dataStruct[1021] = 'Y';
    	if ( s.equals( "N" )) dataStruct[1021] = 'N';
    	return;
    }
    
   
    
    
    
    
    /**
     * getNeedsSupervisor() - Return NeedsSupervisor status as true or false
     * 
     * @param none
     * @return boolean
     */
    public boolean getNeedsSupervisor() {
    	return ( dataStruct[1022] == 'Y' ) ? true: false;
    }
   
    /**
     * setNeedsSupervisor() - Sets the NeedsSupervisor status as true="Y", or false="N"
     * 
     * @param boolean
     * @return
     */
    public void setNeedsSupervisor( boolean needs ){
    	dataStruct[1022] = (byte)( needs ? 'Y': 'N');
    	return;
    }
    
   
    
    
    
    
    
    
    
    
    
    /**
     * getHomeAddress()
     * 
     *  Returns an Address object read from the provider information.
     *  
     *  @param none
     *  @return Address
     */
        public Address getHomeAddress() {
    	return new Address( dataStruct, 52 );
    }
        
    /**
     * setHomeAddress() - Set an address into the provider information.
     * 
     * @param Address
     * @return void
     */
     public void setHomeAddress( Address address ){
    	 address.toData( dataStruct, 52 );
    	 return;
     }

     
     
     
    
     /**
      * getOfficeAddress()
      * 
      *  Returns an Address object read from the provider information.
      *  
      *  @param none
      *  @return Address
      */
         public Address getOfficeAddress() {
     	return new Address( dataStruct, 140 );
     }
         
     /**
      * setOfficeAddress() - Set an address into the provider information.
      * 
      * @param Address
      * @return void
      */
      public void setOfficeAddress( Address address ){
     	 address.toData( dataStruct, 140 );
     	 return;
      }

      
      
      
     
    
      /**
       * getLastName()
       *
       * Gets the provider's last name
       * 
       * @param none
       * @return String
       */
      public String getLastName() {
          return new String( dataStruct, 976, 20 ).trim();
      }

      /**
       * setLastName()
       *
       * sets the provider's last name
       * 
       * @param none
       * @return String
       */
      public void setLastName( String s ) {
          StructHelpers.setStringPadded( s, dataStruct, 976, 20 );
      }

   
    
    /**
     * getFirstName()
     *
     * Gets the provider's first name
     * 
     * @param none
     * @return String
     */
    public String getFirstName() {
        return new String( dataStruct, 996, 16 ).trim();
    }

    /**
     * setFirstName()
     *
     * sets the provider's first name
     * 
     * @param none
     * @return String
     */
    public void setFirstName( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 996, 16 );
    }

 
  

 
    
    
    
    /**
     * getMiddleName()
     *
     * Gets the provider's middle name (really just middle initial for now)
     * 
     * @param none
     * @return String
     */
    public String getMiddleName() {
        return new String( dataStruct, 1012, 1 ).trim();
    }

    /**
     * setMiddleName()
     *
     * sets the provider's middle name (really just middle initial for now)
     * 
     * @param none
     * @return String
     */
    public void setMiddleName( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 1012, 1 );
    }

 
  





    /**
     * getSuffix()
     *
     * Gets the provider's middle initial
     * 
     * @param none
     * @return String
     */
    public String getSuffix() {
        return new String( dataStruct, 1013, 6 ).trim();
    }

    /**
     * setSuffix()
     *
     * sets the provider's middle initial
     * 
     * @param none
     * @return String
     */
    public void setSuffix( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 1013, 6 );
    }

 
  


    
    /**
     * getLicenseState()
     *
     * Gets the provider's State License State
     * 
     * Pass index from 1 thru 4.
     * 
     * @param none
     * @return String
     */
    public String getLicenseState( int index ) {
    	
    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
        return new String( dataStruct, 228 + (index * 20), 2 ).trim();
    }
    public String getLicenseState() { return getLicenseState( 1 ); }
    
    /**
     * setLicenseState()
     *
     * sets the provider's State License State
     * 
     * @param none
     * @return String
     */
    public void setLicenseState( int index, String s ) {
    	
    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
        StructHelpers.setStringPadded( s, dataStruct, 228 + (index * 20), 2 );
    }
    public void setLicenseState( String s ){ setLicenseState( 1, s ); }
    
 
  


    
   

    /**
     * getLicenseNum()
     *
     * Gets the provider's State License Number
     * 
     * Pass index from 1 thru 4.
     * 
     * @param none
     * @return String
     */
    public String getLicenseNum( int index ) {
    	
    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
        return new String( dataStruct, 230 + (index * 20), 14 ).trim();
    }
    public String getLicenseNum() { return getLicenseNum( 1 ); }
    
    /**
     * setLicenseNum()
     *
     * sets the provider's State License Number
     * 
     * @param none
     * @return String
     */
    public void setLicenseNum( int index, String s ) {
    	
    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
        StructHelpers.setStringPadded( s, dataStruct, 230 + (index * 20), 14 );
    }
    public void setLicenseNum( String s ){ setLicenseNum( 1, s ); }
    
 
  


    
   
    /**
     * getLicenseExpDate
     */
    public usrlib.Date getLicenseExpDate( int index ) {

    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
        return ( new usrlib.Date( dataStruct, 244 + (index * 20) ));
    }
    public usrlib.Date getLicenseExpDate() { return getLicenseExpDate( 1 ); }


    /**
     * setLicenseExpDate
     */
    public void setLicenseExpDate( int index, usrlib.Date date ){

    	// make sure index is in range
    	if ( index < 1 ) index = 1;
    	if ( index > 4 ) index = 1;
    	--index;
    	
    	date.toBCD( dataStruct, 244 + (index * 20) );
    }
    public void setLicenseExpDate( usrlib.Date date ){ setLicenseExpDate( 1, date ); }


    
    
    
    /**
     * getEtsProvID()
     *
     * Gets the provider's Ets Prov ID
     * 
     * @param none
     * @return String
     */
    public String getEtsProvID() {
        return new String( dataStruct, 692, 16 ).trim();
    }

    /**
     * setEtsProvID()
     *
     * sets the provider's Ets Prov ID
     * 
     * @param none
     * @return String
     */
    public void setEtsProvID( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 692, 16 );
    }

 
  


    /**
     * getEtsSubID()
     *
     * Gets the provider's Ets Prov Sub ID
     * 
     * @param none
     * @return String
     */
    public String getEtsSubID() {
        return new String( dataStruct, 708, 4 ).trim();
    }

    /**
     * setEtsSubID()
     *
     * sets the provider's Ets Prov Sub ID
     * 
     * @param none
     * @return String
     */
    public void setEtsSubID( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 708, 4 );
    }

 
  





    /**
     * getCLIA()
     *
     * Gets the provider's CLIA number
     * 
     * @param none
     * @return String
     */
    public String getCLIA() {
        return new String( dataStruct, 752, 16 ).trim();
    }

    /**
     * setCLIA()
     *
     * sets the provider's CLIA number
     * 
     * @param none
     * @return String
     */
    public void setCLIA( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 752, 16 );
    }

 
  





    /**
     * getFlags()
     *
     * Gets the provider's flags
     * 
     * @param none
     * @return int
     */
    public int getFlags(){
        return (int) StructHelpers.getShort( dataStruct, 50 );
    }

    /**
     * setFlags()
     *
     * Sets the provider's flags
     * 
     * @param int
     * @return void
     */
    public void setFlags( int flags ) {
        StructHelpers.setShort( flags, dataStruct, 50 );
        return;
    }

    
    
    /**
     * fillListbox() - Static routine to fill a listbox with providers to select from.
     * 
     * Returns int number found.
     * 
     * @param Listbox
     * @return int
     */
    public static int fillListbox( Listbox lb, boolean blank ){
    	
    	int rec;			// record number
    	int	maxrec;			// max record number
    	int fnd = 0;		// number found
    	
 
    	// create blank entry if asked for
    	if ( blank ) ZkTools.appendToListbox( lb, "", null );
    	
    	
    	// create new blank Prov instance
    	Prov prov = new Prov();
    	
    	
    	RandomFile file = RandomFile.open( Pm.getOvdPath(), fn_pcn, getRecordLength(), RandomFile.Mode.READONLY );
    	maxrec = file.getMaxRecord();
    	
    	for ( rec = 2; rec <= maxrec; ++rec ){
    		
            file.getRecord( prov.dataStruct, rec );

            if ( prov.isValid()){
            	
				ZkTools.appendToListbox( lb, String.format( "%-10.10s %-40.40s", prov.getAbbr(), prov.getName()), new Rec( rec ));
            	++fnd;
            }
    	}
    	file.close();
    	
    	return ( fnd );
    }

    
    
    public void dump(){ 
    	StructHelpers.dump( "Prov", dataStruct, getRecordLength() );
        return;
    }


}

/**/
