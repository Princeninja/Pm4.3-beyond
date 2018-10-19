package palmed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import usrlib.Address;
import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Time;

public class Ecs5010Compiler {
	
	int numSegments = 0;
	int levelID = 0;
	int providerLevelID = 0;
	int subscriberLevelID = 0;
	int patientLevelID = 0;
	
	
	// configurables
	boolean dgnCodeRec = true;					// record numbers passed for diagnosis codes
	String testInd = "T";						// T=test, P=production
	int dgnCodeSet = 3;							// diagnosis code set to use
	static String senderID = "3060";			// ecs clearinghouse account number (ISA06)
	static String receiverID = "CLAIMMD";		// ecs clearinghouse receiver (ISA08)
	static String senderCode = "3060";			// ecs clearinghouse sender code (GS02)
	static String receiverCode = "CLAIMMD";		// ecs clearinghouse receiver (GS03)
	


	static Rec pctRec = new Rec( 2 );
	static Practice pct = new Practice( pctRec );
	
	static String orgName = pct.getName();
	static String orgNPI = pct.getNPI();
	static String contactName = pct.getContactName();
	static String contactPhone = pct.getAddress().getHome_ph();
	static String contactFax = pct.getAddress().getHome_ph();
	static String contactEmail = pct.getEmail();
	
	static String ecsName = "CLAIMMD";
	static String ecsNum = "3060";
	
	
	static EtsRecord etsRecord = null;
	
	
	
	// Constructor
	public Ecs5010Compiler(){
		
	}
	
	
	
	
	public boolean compile(){
		
		boolean	first_claim = true;
		String tempStr = null;

		Rec claimProvRec = null;
		Rec lastClaimProvRec = null;

		String controlNum = "9999";
		String transNum = "999999";

		// Compile claims
		
		Ecs5010Compiler.etsRecord = EtsRecord.open();

		if (( etsRecord == null )){
			System.out.println( "Could not open file" );
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		
			
		while ( etsRecord.getNext()){
			
			// validate
			
			
			
			// if first claim
			if ( first_claim ){
				// open output file
				// write header info
				
				tempStr = addTransactionSetHeader( senderID, receiverID, senderCode, receiverCode, controlNum );
				sb.append( tempStr );
				
				tempStr = addBHT( transNum );
				sb.append( tempStr );
				
				// Loop 1000A
				tempStr = addSubmitter();
				sb.append( tempStr );

				// Loop 1000B
				tempStr = addReceiver();
				sb.append( tempStr );

				first_claim = false;
			}
			
			
			
			// get provider
			//   same provider??
			//claimProv = etsRecord.getProvTaxID() + etsRecord.getProvSubID();
			claimProvRec = etsRecord.getProvRec();

			//System.out.println( "PROVIDER REC=" + claimProvRec.getRecInt());
			//System.out.println( "PPO 1 REC=" + etsRecord.getPPORec(1).getRecInt());
			
			if (( lastClaimProvRec == null ) || ! claimProvRec.equals( lastClaimProvRec )){
				//String temp1Str = ( lastClaimProvRec == null ) ? "null": lastClaimProvRec.toString();
				//System.out.println( "PROVIDER RECS NOT EQUAL curr=" + claimProvRec.getRecInt() + " last=" + temp1Str );
				
				// close this level and new 2000 loop??
				//if ( lastClaimProvRec == null ){	
				//}
				
				lastClaimProvRec = claimProvRec;
				
				// loop 2000A
				tempStr = addProviderHL();
				sb.append( tempStr );
				
				// loop 2010AA
				tempStr = addBillingProvider( claimProvRec );
				sb.append( tempStr );
				
//				// loop 2010AB
//				tempStr = addPayToProvider( claimProvRec );
//				sb.append( tempStr );
				
			}
			
			
			
			
			// loop 2000B
			tempStr = addSubscriberHL( true );
			sb.append( tempStr );
			
			// loop 2010BA
			tempStr = addSubscriber();
			sb.append( tempStr );

			// loop 2010BB
			tempStr = addPayer( 1 );
			sb.append( tempStr );

			// loop 2000C
			tempStr = addPatientHL();
			sb.append( tempStr );
			
			tempStr = addPatient();
			sb.append( tempStr );
			
			// loop 2300 Claim Level
			tempStr = addClaimLevel( claimProvRec );
			sb.append( tempStr );
			
			
			
			
			
			
			
			
			// claim diagnosis codes
			tempStr = addDiagnosisCodes();
			sb.append( tempStr );
			
			
			// loop 2310B
			tempStr = addRenderingProvider( claimProvRec );
			sb.append( tempStr );
			
			
			
			
			// Loop 2310D Service Location
			tempStr = addServiceLocation();
			sb.append( tempStr );
			
			
			
			
			
			// Loop 2400 Service Line
			tempStr = addServiceLines();
			sb.append( tempStr );
			
		
			
			
			

			
			
			System.out.println( "Claim " + etsRecord.claimNum + ":  " + etsRecord.getPtLast() + ", " + etsRecord.getPtFirst() + ", " + fixDollar( etsRecord.getClaimCharges()));
		}
			
		etsRecord.close();
		
		
		tempStr = addTransactionSetTrailer( controlNum );
		sb.append( tempStr );
		
		
		
		
		// write new claims file
		
		String fname = "/u/margie/claims.5010";
		
		try {
			BufferedWriter out = new BufferedWriter( new FileWriter( fname ));
			out.write( sb.toString() );
			out.close();
			} catch ( Exception e) { 
				DialogHelpers.Messagebox( "Error writing claims output file:" + fname );
				return false;
			}
	
			
		// archive claims.ets
		File file = new File( "/u/med/claims.ets" );
		String newFname = "/u/med/cl" + Date.today().getPrintable(8) + "_" + Time.now().getPrintable(2)+ ".ets";
		if ( ! file.renameTo( new File( newFname ))){
			DialogHelpers.Messagebox( "Error writing claims ARCHIVE file:" + newFname );
			file.delete();
		}
		
		
		//System.out.println( sb.toString());
		
		
		return true;

	}



	String	addTransactionSetHeader( String senderID, String receiverID, String senderCode, String receiverCode, String controlNum ){
		
		// String testInd - test production indicator T=test P=production
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		tempStr = X12_837Helpers.createISA( senderID, receiverID, Date.today(), Time.now(), controlNum, testInd );
		sb.append( tempStr );
		//++this.numSegments;

		tempStr = X12_837Helpers.createGS( senderCode, receiverCode, Date.today(), Time.now(), controlNum );
		sb.append( tempStr );
		//++this.numSegments;

		tempStr = X12_837Helpers.createST( controlNum );
		sb.append( tempStr );
		++this.numSegments;
		
		return sb.toString();
	}
	
	
	
	
	
	
	
	String	addTransactionSetTrailer( String controlNum ){
		return addTransactionSetTrailer( this.numSegments+1, controlNum );
	}
	
	String	addTransactionSetTrailer( int numSegments, String controlNum ){
				
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		tempStr = X12_837Helpers.createSE( numSegments, controlNum );
		sb.append( tempStr );
		++this.numSegments;
		
		tempStr = X12_837Helpers.createGE( 1, controlNum );
		sb.append( tempStr );
		//++this.numSegments;

		tempStr = X12_837Helpers.createIEA( 1, controlNum );
		sb.append( tempStr );
		//++this.numSegments;

		return sb.toString();
	}
	
	

	String	addBHT( String transNum ){
		
		String str;
		str = X12_837Helpers.createBHT( false, transNum, Date.today(), Time.now());
		++this.numSegments;
		return str;
	}
	
	

	String	addSubmitter(){
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		// Loop 1000A
		tempStr = X12_837Helpers.createNM1( "41", "2", orgName, "XX", orgNPI );
		sb.append( tempStr );
		++this.numSegments;
		tempStr = X12_837Helpers.createPER( "IC", contactName, "TE", contactPhone, "FX", contactFax, "EM", contactEmail );
		sb.append( tempStr );
		++this.numSegments;
		return sb.toString();
	}
	
	
	String	addReceiver(){
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		// Loop 1000B
		tempStr = X12_837Helpers.createNM1( "40", "2", ecsName, "46", ecsNum );
		sb.append( tempStr );
		++this.numSegments;
		tempStr = X12_837Helpers.createPER( "IC", contactName, "TE", contactPhone, "FX", contactFax, "EM", contactEmail );
		sb.append( tempStr );
		++this.numSegments;
		return sb.toString();
	}
	
	
	String addBillingProvider( Rec provRec ){
		
		// look up provider and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;	
		
		String pctName = "Riverside Family Medicine";
		String pctNPI = "1255610150";
		
		
		// find provider info
		Prov prov = new Prov( provRec );
		Address address = prov.getOfficeAddress();
		String provSpecialtyTaxonomy = prov.getProvNumber( 31 );

		System.out.println( "PROVREC=" + provRec.getRecInt() + ", NAME=" + prov.getFirstName() + " " + prov.getMiddleName() + " " + prov.getLastName());
		
		
		// add provider specialty info
//		tempStr = X12_837Helpers.createPRV( "BI", "PXC", provSpecialtyTaxonomy );
		tempStr = X12_837Helpers.createPRV( "BI", "", "" );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createNM1( "85", "2", pctName, "XX", pctNPI );
		sb.append( tempStr );
		++this.numSegments;

		// Loop 2010AA
//		tempStr = X12_837Helpers.createNM1( "85", "2", prov.getLastName(), prov.getFirstName(), prov.getMiddleName(), prov.getSuffix(), "XX", prov.getNPI() );
//		sb.append( tempStr );
//		++this.numSegments;

		tempStr = X12_837Helpers.createN3( address.getStreet() );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( address.getCity(), address.getState(), address.getZip5() );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createREF( "EI", prov.getProvNumber(Prov.Numbers.FED_TAX) );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	
	
	
	String addRenderingProvider( Rec provRec ){
		
		// look up provider and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;	
		
		
		
		// find provider info
		Prov prov = new Prov( provRec );
		Address address = prov.getOfficeAddress();
		String provSpecialtyTaxonomy = prov.getProvNumber( 31 );

		System.out.println( "PROVREC=" + provRec.getRecInt() + ", NAME=" + prov.getFirstName() + " " + prov.getMiddleName() + " " + prov.getLastName());
		
		
		// add provider specialty info
//		tempStr = X12_837Helpers.createPRV( "PE", "PXC", provSpecialtyTaxonomy );
//		sb.append( tempStr );
//		++this.numSegments;

		// Loop 2310B
		tempStr = X12_837Helpers.createNM1( "82", "1", prov.getLastName(), prov.getFirstName(), prov.getMiddleName(), prov.getSuffix(), "XX", prov.getNPI() );
		sb.append( tempStr );
		++this.numSegments;

//		tempStr = X12_837Helpers.createN3( address.getStreet() );
//		sb.append( tempStr );
//		++this.numSegments;

//		tempStr = X12_837Helpers.createN4( address.getCity(), address.getState(), address.getZip5() );
//		sb.append( tempStr );
//		++this.numSegments;

		tempStr = X12_837Helpers.createREF( "EI", prov.getProvNumber(Prov.Numbers.FED_TAX) );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	
	
	
	String addPayToProvider( Rec provRec ){
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String pctName = "Riverside Family Medicine";
		String pctNPI = "1255610150";
		
		
		// find practice info
		// NOTE - USE PROVIDER FOR NOW
		Prov prov = new Prov( provRec );
		Address address = prov.getOfficeAddress();
		
		
		// Loop 2010AB
		tempStr = X12_837Helpers.createNM1( "87", "2", pctName, "XX", pctNPI );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN3( address.getStreet() );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( address.getCity(), address.getState(), address.getZip5() );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createREF( "EI", prov.getProvNumber(Prov.Numbers.FED_TAX) );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	
	
	
	String addSubscriber( ){
		
		// look up provider and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String insLastName = etsRecord.getInsLast();
		String insFirstName = etsRecord.getInsFirst();
		String insMiddleName = etsRecord.getInsMiddle();
		String insSuffixName = "";
		String insLine1 = etsRecord.getInsStreet();
		String insCity = etsRecord.getInsCity();
		String insState = etsRecord.getInsState();
		String insZip = etsRecord.getInsZip();
		
		String insBirthdate = fixDate( etsRecord.getInsBirthdate());
		String insSex = etsRecord.getInsSex();
		
		String insRelation = etsRecord.getInsRelation( 1 );							// TEMP
		String insGroupNum = etsRecord.getInsGroupNumber( 1 );		
		String insPolicyNum = etsRecord.getInsInsuredID( 1 );
		
		System.out.println( "Subscriber:" );
		System.out.println( "    name " + insLastName + ", " + insFirstName + " " + insMiddleName );
		System.out.println( "    DOB " + insBirthdate );

		
		// find insured info
		
		
		// add provider specialty info
		tempStr = X12_837Helpers.createSBR( "P", (insRelation == "P" ? "18": ""), insGroupNum, "", "", "" );
		sb.append( tempStr );
		++this.numSegments;

		// Loop 2010AA
		tempStr = X12_837Helpers.createNM1( "IL", "1", insLastName, insFirstName, insMiddleName, insSuffixName, "MI", insPolicyNum );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN3( insLine1 );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( insCity, insState, insZip );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createDMG( insBirthdate, insSex );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	

	
	
	String addPatient( ){
		
		// look up patient and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String ptLastName = etsRecord.getPtLast();
		String ptFirstName = etsRecord.getPtFirst();
		String ptMiddleName = etsRecord.getPtMiddle();
		String ptSuffixName = "";
		String ptLine1 = etsRecord.getPtStreet();
		String ptCity = etsRecord.getPtCity();
		String ptState = etsRecord.getPtState();
		String ptZip = etsRecord.getPtZip();
		
		String ptBirthdate = fixDate( etsRecord.getPtBirthdate());
		String ptSex = etsRecord.getPtSex();
		//String ptSSN = etsRecord.getPtSSN();
		
		String insRelation = fixInsRelation( etsRecord.getInsRelation( 1 ));							// TEMP
		
		//String insGroupNum = etsRecord.getInsGroupNumber( 1 );		
		//String insPolicyNum = etsRecord.getInsInsuredID( 1 );
		
		System.out.println( "Patient:" );
		System.out.println( "    name " + ptLastName + ", " + ptFirstName + " " + ptMiddleName );
		System.out.println( "    DOB " + ptBirthdate );

		
		// find insured info
		
		
		// add provider specialty info
		tempStr = X12_837Helpers.createPAT( insRelation, null, false );
		sb.append( tempStr );
		++this.numSegments;

		// Loop 2010AA
		tempStr = X12_837Helpers.createNM1( "QC", "1", ptLastName, ptFirstName, ptMiddleName, ptSuffixName, "", "" );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN3( ptLine1 );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( ptCity, ptState, ptZip );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createDMG( ptBirthdate, ptSex );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	
	

	
	
	String addPayer( int num ){
		
		// look up provider and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String payerName = etsRecord.getInsPayorName( num );
		String payerID = etsRecord.getInsPayorID( num );
		String payerSubID = etsRecord.getInsPayorSubID( num );
		String payerLine1 = etsRecord.getInsAddress1( num );
		String payerCity = etsRecord.getInsCity( num );
		String payerState = etsRecord.getInsState( num );
		String payerZip = etsRecord.getInsZip( num );
		//String insGroupNum = etsRecord.getInsGroupNumber(1);		
		//String insPolicyNum = etsRecord.getInsInsuredID( 1 );
		
		
		
		
		// find insured info
		
		
		// Loop 2010BB
		tempStr = X12_837Helpers.createNM1( "PR", "2", payerName, "PI", payerID );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN3( payerLine1 );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( payerCity, payerState, payerZip );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
	}
	
	


	String addClaimLevel( Rec provRec ){
		
		// look up provider and get info
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String claimNum = etsRecord.getClaimNumber();
		String claimCharges = fixDollar( etsRecord.getClaimCharges());
		String claimFacilityCode = "";
		String claimFacilityValue = "";
		String claimFreqTypeCode = "";
		String provSign = "Y";
		
		String assignCode = etsRecord.getInsAssignment( 1 );
		if ( assignCode == "Y" ) assignCode = "A";
		
		String assignBenefits = "Y";
		String releaseCode = "Y";
		String signSourceCode = "";

		
		// get CLIA number
		Prov prov = new Prov( provRec );
		String provCLIA = prov.getProvNumber( Prov.Numbers.CLIA );
		

		
		
		// find insured info
		
		
		// Loop 2300 Claim Level
		
		
		tempStr = X12_837Helpers.createCLM( claimNum, claimCharges, claimFacilityCode, claimFacilityValue, claimFreqTypeCode, 
				provSign, assignCode, assignBenefits, releaseCode, signSourceCode, "", "", "" );
		sb.append( tempStr );
		++this.numSegments;
		
		tempStr = X12_837Helpers.createREF( "X4", provCLIA );
		sb.append( tempStr );
		++this.numSegments;
		
		
		// onset date, admit date, discharge date, etc.
		
		Date date = new Date();
		
		if ( date.fromString( etsRecord.getAdmitDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "435", date.getPrintable( 8 ));
			System.out.println( "Admit Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		
		if ( date.fromString( etsRecord.getDischargeDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "096", date.getPrintable( 8 ));
			System.out.println( "Discharge Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		
		if ( date.fromString( etsRecord.getFirstSymptomDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "431", date.getPrintable( 8 ));
			System.out.println( "FirstSymptom Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		
		if ( date.fromString( etsRecord.getAccidentDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "439", date.getPrintable( 8 ));
			System.out.println( "Accident Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		
		if ( date.fromString( etsRecord.getDisabledFromDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "360", date.getPrintable( 8 ));
			System.out.println( "Disabled From Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		
		if ( date.fromString( etsRecord.getDisabledToDate()).isValid() ){			
			tempStr = X12_837Helpers.createDTP( "361", date.getPrintable( 8 ));
			System.out.println( "Disabled To Date " + tempStr );
			sb.append( tempStr );
			++this.numSegments;
		}
		

		

		return sb.toString();
	}
	
	
	
	
	
	
	
	String addDiagnosisCodes(){
		
		// boolean codepass - config that codes are passed as record numbers
		// int dgnCodeSet - diagnosis code set to use
		
		String code1 = etsRecord.getDiagnosisCode(1);			
		String code2 = etsRecord.getDiagnosisCode(2);
		String code3 = etsRecord.getDiagnosisCode(3);
		String code4 = etsRecord.getDiagnosisCode(4);

		System.out.println( "Diagnoses: " );
		System.out.println( "    code1 " + code1 );
		System.out.println( "    code2 " + code2 );
		System.out.println( "    code3 " + code3 );
		System.out.println( "    code4 " + code4 );

		if ( dgnCodeRec ){
			
			Rec rec;
			
			rec = new Rec( EditHelpers.parseInt( code1 ));
			if ( rec.isValid()) code1 = (new Dgn( rec )).getCode( dgnCodeSet );
			
			rec = new Rec( EditHelpers.parseInt( code2 ));
			if ( rec.isValid()) code2 = (new Dgn( rec )).getCode( dgnCodeSet );
			
			rec = new Rec( EditHelpers.parseInt( code3 ));
			if ( rec.isValid()) code3 = (new Dgn( rec )).getCode( dgnCodeSet );
			
			rec = new Rec( EditHelpers.parseInt( code4 ));
			if ( rec.isValid()) code4 = (new Dgn( rec )).getCode( dgnCodeSet );			
		
			System.out.println( "TRANSLATED Diagnoses: " );
			System.out.println( "    code1 " + code1 );
			System.out.println( "    code2 " + code2 );
			System.out.println( "    code3 " + code3 );
			System.out.println( "    code4 " + code4 );
		}
		
		String str = X12_837Helpers.createHI( true, code1, code2, code3, code4 );
		++this.numSegments;
		
		return str;
	}
	


	
	String addServiceLocation(){
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		String srvLocationName = etsRecord.getHospitalName();
		String srvLocationID = etsRecord.getHospitalNum();

		String srvLocationLine1 = etsRecord.getHospitalStreet();
		String srvLocationCity = etsRecord.getHospitalCity();
		String srvLocationState = etsRecord.getHospitalState();
		String srvLocationZip = etsRecord.getHospitalZip();

		
		
		
		
		// Loop 2010BB
		tempStr = X12_837Helpers.createNM1( "77", "2", srvLocationName, "XX", srvLocationID );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN3( srvLocationLine1 );
		sb.append( tempStr );
		++this.numSegments;

		tempStr = X12_837Helpers.createN4( srvLocationCity, srvLocationState, srvLocationZip );
		sb.append( tempStr );
		++this.numSegments;

		return sb.toString();
}
	

	
	
	
	
	
	String addServiceLines(){
		
		StringBuilder sb = new StringBuilder();
		String tempStr;
		
		
		
		for ( int lineNum = 1; lineNum <= 13; ++lineNum ){
			
			String code = etsRecord.getLineSrvCode( lineNum );
			
			// is service line valid??
			if (( code == null ) || ( code.length() < 1 )) break;
			
			
			
			String mod1 = etsRecord.getLineSrvMod1( lineNum );
			String mod2 = etsRecord.getLineSrvMod2( lineNum );
			String desc = null;  // etsRecord.getLine( lineNum );
			String charge = fixDollar( etsRecord.getLineCharge( lineNum ));
			int quantity = EditHelpers.parseInt( etsRecord.getLineUnits( lineNum ));
			String posCode = etsRecord.getLineSrvLocation( lineNum );
			String fromDate = fixDate( etsRecord.getLineFromDate( lineNum ));
			
			System.out.println( "Service: " );
			System.out.println( "    code " + code );
			System.out.println( "    date " + fromDate );
			System.out.println( "    mod1 " + mod1 );
			System.out.println( "    mod2 " + mod2 );
			System.out.println( "    charge " + charge );
			System.out.println( "    qty " + quantity );
			System.out.println( "    pos " + posCode );
			System.out.println( "    desc " + desc );

			String dgnPtrString = etsRecord.getLineDgnCodePtr( lineNum );
			int dgnPtr1 = EditHelpers.parseInt( dgnPtrString.substring( 0,1 ));
			int dgnPtr2 = EditHelpers.parseInt( dgnPtrString.substring( 1,2 ));
			int dgnPtr3 = EditHelpers.parseInt( dgnPtrString.substring( 2,3 ));
			int dgnPtr4 = EditHelpers.parseInt( dgnPtrString.substring( 3,4 ));
			System.out.println( "    dgnPtrStr " + dgnPtrString );

			boolean emergency = ( etsRecord.getEmergency() == "Y" );
			boolean epsdt = ( etsRecord.getEPSDT() == "Y" );
			boolean familyPlanning = ( etsRecord.getFamilyPlanning() == "Y" );

			tempStr = X12_837Helpers.createLX( lineNum );
			sb.append( tempStr );
			++this.numSegments;
		
			tempStr = X12_837Helpers.createSV1( code, mod1, mod2, null, null, desc, charge, 
					quantity, posCode, dgnPtr1, dgnPtr2, dgnPtr3, dgnPtr4, emergency, epsdt, familyPlanning );
			sb.append( tempStr );
			++this.numSegments;
		
			tempStr = X12_837Helpers.createDTP( "472", fromDate );
			sb.append( tempStr );
			++this.numSegments;
		
		}
		
		return sb.toString();
	}
	

		static String fixInsRelation( String s ){
			return s;
		}

		static String fixDollar( String s ){
			double amt = EditHelpers.parseDouble( s ) / 100;
			s = String.format( "%3.2f", amt );
			return s;
		}

		static String fixDate( String s ){
			
			if ( s.length() == 6 ){
				if ( s == "000000" ){
					s = "00000000";
				} else {
					int currentYear = Date.today().getYear() % 100;
					int year = Integer.parseInt( s.substring( 4, 6 ));
					year = year + (( year > currentYear ) ? 1900: 2000 );
					s = "" + year + s.substring( 0, 4 );
				}
			} else {
				SystemHelpers.seriousError( "Date different size than expected" );
			}
			return s;
		}
		
		
		
		
		
		
		
		
		
		
		String addProviderHL(){
			providerLevelID = ++this.levelID;
			return addHierarchicalLevel( providerLevelID, 0, "20", true );
		}
		
		String addSubscriberHL( boolean childCode ){
			subscriberLevelID = ++this.levelID;
			return addHierarchicalLevel( subscriberLevelID, providerLevelID, "22", childCode );
		}
		
		String addPatientHL(){
			patientLevelID = ++this.levelID;
			return addHierarchicalLevel( patientLevelID, subscriberLevelID, "23", true );
		}
		
 
		String addHierarchicalLevel( int levelID, int parentID, String levelCode, boolean childCode ){
			
			String str;
			str = X12_837Helpers.createHL( levelID, parentID, levelCode, childCode );
			++this.numSegments;
			return str;
	}
		

		
		

		
	public boolean fixDgn(){
		int cnt = 0;
		Dgn dgn = Dgn.open();
		
		System.out.println( "Processing diagnosis codes...." );
		while ( dgn.getNext()){
			
			dgn.setCode( 2, String.format( "%05d", dgn.getRec().getRecInt()));
			dgn.write();
			++cnt;
		}
		
		dgn.close();
		System.out.printf( "Processed %d diagnosis codes....", cnt );
		return ( true );
	}
		

}
