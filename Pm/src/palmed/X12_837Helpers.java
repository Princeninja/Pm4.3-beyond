package palmed;

import usrlib.Date;
import usrlib.Name;
import usrlib.StringHelpers;
import usrlib.Time;

public class X12_837Helpers {

	
	public static String createISA( String senderID, String receiverID, Date date, Time time, String controlNum, String testInd ){
		
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "ISA*" );														// segment name
		sb.append( "  *" + "          *" );											// 1,2 - authorization info qualifier (2sp) and info (10sp)
		sb.append( "  *" + "          *" );											// 3,4 - security info qualifier (2sp) and info (10sp)
		sb.append( "ZZ*" );															// 5 - interchange ID qualifier (2sp) (tax ID????? pg 644)
		sb.append( StringHelpers.pad( senderID, 15 ) + "*" );						// 6 - interchange sender ID (15sp)
		sb.append( "ZZ*" );															// 7 - interchange ID qualifier  (tax ID????? pg 644)
		sb.append( StringHelpers.pad( receiverID, 15 ) + "*" );						// 8 - interchange receiver ID (15sp)
		sb.append( date.getPrintable(8).substring( 2, 8 ) + "*" );					// 9 - interchange date YYMMDD (6sp)
		sb.append( time.getPrintable(1).substring( 0, 4 ) + "*" );					// 10 - interchange time HHMM (4sp)
		sb.append( "^" + "*" );														// 11 - repitition seperator
		sb.append( "00501*" );														// 12 - interchange version control number (5sp)
		sb.append( StringHelpers.pad( StringHelpers.onlyDigits( controlNum ), 9 ) + "*" );	// 13 - interchange control number (9sp)
		sb.append( "0*" );															// 14 - acknowledgement requested
		sb.append( testInd + "*" );													// 15 - interchange usage indicator T=test data, P=production data
		sb.append( ":" );															// 16 - component seperator


		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	public static String createIEA( int numGroups, String controlNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "IEA*" );														// segment name and format
		sb.append( numGroups + "*" );												// 1 - number of functional groups
		sb.append( StringHelpers.pad( StringHelpers.onlyDigits( controlNum ), 9 ) );// 2 - interchange control number
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	
	
	
	
	public static String createGS( String senderCode, String receiverCode, Date date, Time time, String controlNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "GS*" );															// segment name 
		sb.append( "HC*" );															// 1 - functional identifier code
		sb.append( senderCode + "*" );												// 2 - application senders code  (?????)
		sb.append( receiverCode + "*" );											// 3 - application receivers code
		sb.append( date.getPrintable(8) + "*" );									// 4 - interchange date CCYYMMDD
		sb.append( time.getPrintable(1).substring( 0, 4 ) + "*" );					// 5 - interchange time HHMM
		sb.append( StringHelpers.pad( StringHelpers.onlyDigits( controlNum ), 9 ) + "*" );// 6 - group control number
		sb.append( "X*" );															// 7 - responsible agency code 'X' = X12
		sb.append( "005010X222A1" );												// 8 - version release industry identifier code
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	public static String createGE( int numSets, String controlNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "GE*" );														// segment name and format
		sb.append( numSets + "*" );												// 1 - number of transaction sets
		sb.append( StringHelpers.pad( StringHelpers.onlyDigits( controlNum ), 9 ) );// 2 - group control number
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	
	public static String createST( String controlNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "ST*837*" );														// segment name and format
		sb.append( controlNum + "*" );												// 2 - transaction set control number
		sb.append( "005010X222A1" );												// 3 - 
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	public static String createSE( int segments, String controlNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "SE*" );														// segment name and format
		sb.append( segments + "*" );												// 1 - number of segments
		sb.append( controlNum );													// 2 - transaction set control number
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	public static String createBHT( boolean reissue, String transNum, Date date, Time time ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "BHT*0019*" );														// segment name and format
		sb.append(( reissue ? "18": "00" ) + "*" );										// 2 - reissue vs new claims file
		sb.append( transNum + "*" );													// 3 - our claim file or transmission number
		sb.append( date.getPrintable(8) + "*" );										// 4 - date
		sb.append( time.getPrintable(0) + "*" );										// 5 - time
		sb.append( "CH" + "*" );														// 6 - transaction type code
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	

	
	// Create NM1
	
	// Entity Identifier Code
	// 40 - Receiver
	// 41 - Submitter
	// 82 - Rendering provider
	// 85 - Billing provider
	// 87 - Pay-to Provider
	// PE - Payee
	// PR - Payer (2010BB)
	// IL - Insured or Subscriber (2010BA)
	// QC - Patient (2010CA)
	// 77 - Service Location (2310D???????)
	
	// Entity Type Qualifier
	// 1 - Person
	// 2 - Non-person entity
	
	// Identification Code Qualifier
	// 46 - Electronic Transmitter ID Number (ETIN)
	// XX - NPI
	// PI - Payor Identification
	// XV - CMS Plan ID
	// II - Standard Unique Health Identifier for each Individual in the US (req 2010BA if HIPAA IPI mandated use)
	// MI - Member Identification Number (subscribers ID number as assigned by payer)
	
	
	
	public static String createNM1( String code, String type, String orgName, String idQualifier, String idCode ){
		return createNM1( code, type, orgName, "", "", "", idQualifier, idCode );
	}
	public static String createNM1( String code, String type, Name name, String idQualifier, String idCode ){
		return createNM1( code, type, name.getLastName(), name.getFirstName(), name.getMiddleName(), name.getSuffix(), idQualifier, idCode );
	}
	
	public static String createNM1( String code, String type, String lastName, String firstName, String middleName, String suffix, String idQualifier, String idCode ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "NM1*" );													// segment name
		sb.append( code + "*" );												// 1 - Entity ID Code
		sb.append( type + "*" );												// 2 - Entity Type Qualifier
		sb.append( lastName + "*" );											// 3 - Name Last / Org Name
		sb.append( firstName + "*" );											// 4 - Name First
		sb.append( middleName + "*" );											// 5 - Name Middle
		sb.append( "*" );														// 6 - name prefix
		sb.append( suffix + "*" );												// 7 - name suffix
		sb.append( idQualifier + "*" ); 										// 8 - id code Qualifier
		sb.append( idCode + "*" );												// 9 - id code
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	
	
	public static String createN3( String addressLine1 ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "N3*" );														// segment name
		sb.append( addressLine1 + "*" );										// 1 -
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	public static String createN4( String addressCity, String addressState, String addressZip ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "N4*" );														// segment name
		sb.append( addressCity + "*" );											// 1 -
		sb.append( addressState + "*" );										// 2 -
		sb.append( addressZip + "*" );											// 3 -
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	
	
	
	
	
	
	// Create PER
	
	// Contact Function Code
	// IC - Information Contact
	
	// Communication Number Qualifier
	// EM - electronic mail
	// FX - fascimile
	// TE - telephone
	// EX - Telephone Extension
	
	
	
	public static String createPER( String code, Name name, String qualifier1, String number1, String qualifier2, String number2, String qualifier3, String number3 ){
		return createNM1( code, name.getPrintableNameFML(), qualifier1, number1, qualifier2, number2, qualifier3, number3 );
	}
	
	public static String createPER( String code, String name, String qualifier1, String number1, String qualifier2, String number2, String qualifier3, String number3 ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PER*" );													// segment name
		sb.append( code + "*" );												// 1 - Contact Function Code
		sb.append( name + "*" );												// 2 - Name
		sb.append( qualifier1 + "*" );											// 3 - Communication Number Qualifier 1
		sb.append( number1 + "*" );												// 4 - Communication Number 1 (phone number?)
		sb.append( qualifier2 + "*" );											// 5 - Communication Number Qualifier 2
		sb.append( number2 + "*" );												// 6 - Communication Number 2 (phone number?)
		sb.append( qualifier3 + "*" );											// 7 - Communication Number Qualifier 3
		sb.append( number3 + "*" );												// 8 - Communication Number 3 (phone number?)
		sb.append( "*" );														// 9 - not used
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	
	
	
	
	
	

	// Create PRV
	
	// Provider Code
	// BI - Billing
	// PE - Rendering
	
	// Reference ID Qualifier
	// PXC - Health Care Provider Taxonomy Code
	
	
	public static String createPRV( String code, String idQualifier, String id ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PRV*" );													// segment name
		sb.append( code + "*" );												// 1 - Provider Code
		sb.append( idQualifier + "*" );											// 2 - Reference ID Qualifier
		sb.append( id + "*" );													// 3 - Reference Identification
		sb.append( "*" );														// 4 - state or province code (not used)
		sb.append( "*" );														// 5 - provider specialty information (not used)
		sb.append( "*" );														// 6 - provider organization code (not used)
		sb.append( "*" );														// 7 - Communication Number 3 (phone number?)
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	
	
	

	// Create REF
	
	// Reference ID Qualifier
	// EI - Employer's ID Number (EIN)
	// SY - Social Security Number (SSN)
	// 0B - State License Number
	// 1G - Provider UPIN Number
	// 2U - Payer ID Number
	// FY - Claim Office Number
	// NF - NAIC Code
	// G2 - Provider Commercial Number
	// LU - Location Number
	// 4N - Special Payment Reference Number (Service Authorization Exception Code)
	// 9F - Referral Number
	// G1 - Prior Authorization Number
	// F8 - Original Reference Number (Payer Claim Control Number)
	// X4 - CLIA number
	// EA - Medical Record ID Number
	// 1J - Facility ID Number (NPI number for Care Plan Oversight)
	
	
	public static String createREF( String idQualifier, String id ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "REF*" );													// segment name
		sb.append( idQualifier + "*" );											// 1 - Reference ID Qualifier
		sb.append( id );														// 2 - Reference Identification
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	



	// Create DTP - Date
	
	// Date Time Qualifier
	// 431 - Onset of Current Symptoms or Illness
	// 454 - Initial Treatment Date
	// 304 - Last Visit or Consultation Date
	// 453 - Acute Manifestation of a Chronic Condition
	// 439 - Accident Date
	// 484 - Last Menstrual Period
	// 455 - Last Xray Date
	// 471 - Hearing and Vision Prescription Date
	// 314 - Disability Dates (range)
	// 360 - Initial Disability Period Start
	// 361 - Initial Disability Period End
	// 297 - Initial Disability Period Last Day Worked
	// 296 - Initial Disability Period Return to Work Date
	// 435 - Admission Date
	// 096 - Discharge Date
	// 090 - Assumed Care Date - Report Start
	// 091 - Relinquished Care Date - Report End
	
	// 472 - Service Date (?????)
	
	public static String createDTP( String qualifier, Date date ){
		return createDTP( qualifier, date.getPrintable(8), "D8" );
	}
	public static String createDTP( String qualifier, String dateString ){
		return createDTP( qualifier, dateString, "D8" );
	}
	public static String createDTP( String qualifier, Date startDate, Date endDate ){
		return createDTP( qualifier, startDate.getPrintable(8) + "-" + endDate.getPrintable(8), "RD8" );
	}
	
	private static String createDTP( String qualifier, String dateString, String formatQualifier ){
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "DTP*" );													// segment name
		sb.append( qualifier + "*" );											// 1 - Reference ID Qualifier
		sb.append( formatQualifier + "*" );										// 2 - Date Time Period Format Qualifier (D8 - CCYYMMDD)
		sb.append( dateString );												// 3 - date time period
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	



	// Create HL
	
	// Hierarchical Level Code
	// 20 - Billing Provider (??)
	// 22 - Subscriber
	// 23 - Dependent (2000C)

	public static String createHL( int id, int parentId, String levelCode, boolean childCode ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "HL*" );													// segment name
		sb.append( id + "*" );												// 1 - HL segment ID
		sb.append( parentId + "*" );										// 2 - parent HL segment ID
		sb.append( levelCode + "*" );										// 3 - Hierarchical Level Code
		sb.append( (childCode ? "0": "1") + "*" );							// 4 - Hierarchical Child Code
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	






	// Create SBR - Subscriber Information
	
	// Payer Responsibility Sequence Number Code
	// P - Primary
	// S - Secondary
	// T - Tertiary
	// A - Payer #4
	// B - Payer #5
	// C - Payer #6
	// D - Payer #7
	// E - Payer #8
	// F - Payer #9
	// G - Payer #10
	// H - Payer #11
	// U - Unknown
	
	// Individual Relationship Code
	// 18 - Self (req if patient is subscriber)
	
	// Insurance Type Code
	// 12 - Medicare Secondary Working Aged Beneficiary or Spouse with Employer Group Health Plan
	// 13 - Medicare Secondary End-Stage Renal Disease Beneficiary in Mandated Coordination Period with an Employer's Group Health Plan
	// 14 - Medicare Secondary, No-fault Insurance including Auto is Primary
	// 15 - Medicare Secondary Worker's Compensation
	// 16 - Medicare Secondary Public Health Service or Other Federal Agency
	// 41 - Medicare Secondary Black Lung
	// 42 - Medicare Secondary VA
	// 43 - Medicare Secondary Disabled Beneficiary Under Age 65 with Large Group Health Plan
	// 47 - Medicare Secondary, Other Liability Insurance is Primary
	
	
	
	public static String createSBR( String payerSeqCode, String relationshipCode, String referenceID, String name, String typeCode, String claimCode ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "SBR*" );													// segment name
		sb.append( payerSeqCode + "*" );										// 1 - Payer Responsibility Sequence Number Code
		sb.append( relationshipCode + "*" );									// 2 - Individual relationship code
		sb.append( referenceID + "*" );											// 3 - Reference ID (subscriber group or policy number)
		sb.append( name + "*" );												// 4 - Name (req when SBR03 not used and name is available)
		sb.append( typeCode + "*" );											// 5 - Insurance Type Code (req when dest payer is Medicare and Medicare is not primary
		sb.append( "*" );														// 6 - Coordination of Benefits Code (not used)
		sb.append( "*" );														// 7 - Response Code (not used)
		sb.append( "*" );														// 8 - Employment Status Code (not used)
		sb.append( "*" );														// 9 - Claim Filing Indicator Code (not used after HIPAA date)
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	




	// Create PAT - Patient Information
	
	// Individual Relationship Code
	// 01 - Spouse
	// 19 - Child
	// 20 - Employee
	// 21 - Unknown
	// 39 - Organ Donor
	// 40 - Cadaver Donor
	// 53 - Life Partner
	// G8 - Other Relationship
	

	public static String createPAT( String relationshipCode, Date deathDate, boolean pregnant ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PAT*" );													// segment name
		sb.append( relationshipCode + "*" );									// 1 - Individual Relationship Code
		
		if (( deathDate != null ) || ( pregnant )){
			sb.append( "*" );														// 2 - Patient Location Code (not used)
			sb.append( "*" );														// 3 - Employment Status Code (not used)
			sb.append( "*" );														// 4 - Student Status Code (not used)
	
			if ( deathDate!= null ){
				sb.append( "D8*" );														// 5 - Date Time Period Format Qualifier (D8 - CCYYMMDD)
				if ( deathDate != null ){ sb.append( deathDate.getPrintable(8));} sb.append( "*" );	// 6 - Date Time Period (Patient Death Date)
			} else {
				sb.append( "**" );														// 7 - Unit or Basis for Measurement Code (for DME only)
			}
	
			sb.append( "*" );														// 7 - Unit or Basis for Measurement Code (for DME only)
			sb.append( "*" );														// 8 - Weight (for DME only)
			if ( pregnant  ){ sb.append( "Y" ); } sb.append( "*" );					// 9 - Yes/No Condition or Response Code (Pregnancy Indicator)
		}
		
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	






	// Create DMG - Subscriber Demographic Information

	public static String createDMG( Date birthDate, String gender ){
		
		String str = "";
		if ( birthDate != null )str = birthDate.getPrintable(8);
		return createDMG( str, gender );
	}
	
	
	public static String createDMG( String birthDate, String gender ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "DMG*" );													// segment name
		sb.append( "D8*" );														// 1 - Date Time Period Format Qualifier (D8 - CCYYMMDD)
		sb.append( birthDate ); sb.append( "*" );								// 2 - Date Time Period (Patient Birth Date)
		sb.append( gender + "*" );												// 3 - Gender Code (M, F, or U)
		sb.append( "*" );														// 4 - Marital Status Code (not used)
		sb.append( "*" );														// 5 - Composite Race or Ethnicity Information (not used)
		sb.append( "*" );														// 6 - Citizenship Status Code (not used)
		sb.append( "*" );														// 7 - Country Code (not used)
		sb.append( "*" );														// 8 - Basis of Verification Code (not used)
		sb.append( "*" );														// 9 - Quantity (not used)
		sb.append( "*" );														// 10 - Code List Qualifier Code (not used)
		sb.append( "*" );														// 11 - Industry Code (not used)
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	




	// Create CLM - Claim Information
	
	// Accept Assignment Code
	// A - Assigned
	// B - Accept Assignment on Clinical Lab Services Only
	// C - Not Assigned
	
	// Assign Benefits Code
	// Y - Yes
	// N - No
	// W - Not applicable or patient refuses to assign
	
	// Release Code
	// I - Informed Consent to Release Medical Information
	// Y - Provider has a signed statement permitting release
	
	// Special Programs Code
	// 02 - Physically Handicapped Children's Program (Medicaid only)
	// 03 - Special Federal Funding (Medicaid only)
	// 05 - Disability (Medicaid only)
	// 09 - Second Opinion or Surgery (Medicaid only)
	
	// Delay Reason Code
	// 1 - Proof of Eligibility Unknown or Unavailable
	// 2 - Litigation
	// 3 - Authorization Delays
	// 4 - Delay in Certifying Provider
	// 5 - Delay in Supplying Billing Forms
	// 6 - Delay in Delivery of Custom Made Appliances
	// 7 - Third Party Processing Delay
	// 8 - Delay in Eligibility Determination
	// 9 - Original Claim Rejected or Denied Due to a Reason Unrelated to the Billing Limitation Rules
	// 10 - Administration Delay in Prior Approval Process
	// 11 - Other
	// 15 - Natural Disaster
	

	public static String createCLM( String claimNumber, String amount, String facilityCode, String facilityValue, String claimFreqTypeCode, 
			String providerSign, String assignCode, String assignBenefits, String releaseCode, String signatureSourceCode,
			String relatedCauses, String specialProgramsCode, String delayReasonCode ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "CLM*" );													// segment name
		sb.append( claimNumber + "*" );											// 1 - Claim Submitter's Identifier (our claim #)
		sb.append( amount + "*" );												// 2 - Monetary Amount (Total Claim Charge Amount)
		sb.append( "*" );														// 3 - Claim Filing Indicator Code (not used)
		sb.append( "*" );														// 4 - Non-Institutional Claim Type Code (not used)
		sb.append( facilityCode + ":" + facilityValue + ":" + claimFreqTypeCode + "*" );// 5 - Health Care Service Location Information
		sb.append( providerSign + "*" );										// 6 - Provider Signature Indicator Y/N
		sb.append( assignCode + "*" );											// 7 - Provider Accept Assignment Code (or Plan Participation)
		sb.append( assignBenefits + "*" );										// 8 - Benefits Assignment Certification Indicator
		sb.append( releaseCode + "*" );											// 9 - Release of Information Code
		sb.append( signatureSourceCode + "*" );									// 10 - Patient Signature Source Code
		sb.append( relatedCauses + "*" );										// 11 - Related Causes Information (multiple sub-fields)
		sb.append( specialProgramsCode + "*" );									// 12 - Special Program Code Code
		sb.append( "*" );														// 13 - Yes/No Condition or Response Code (not used)
		sb.append( "*" );														// 14 - Level of Service Code (not used)
		sb.append( "*" );														// 15 - Yes/No Condition or Response Code (not used)
		sb.append( "*" );														// 16 - Provider Agreement Code (not used)
		sb.append( "*" );														// 17 - Claim Status Code (not used)
		sb.append( "*" );														// 18 - Yes/No Condition or Response Code (not used)
		sb.append( "*" );														// 19 - Claim Submission Reason Code (not used)
		sb.append( delayReasonCode + "*" );										// 20 - Delay Reason Code (not used)
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	





	// Create AMT - Monetary Amount
	
	// Amount Qualifier Code
	// F5 - Patient Amount Paid
	
	
	public static String createAMT( String amtQualifier, String amount ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "AMT*" );													// segment name
		sb.append( amtQualifier + "*" );										// 1 - Amount Qualifier Code
		sb.append( amount );													// 2 - Monetary Amount
																				// 3 - Credit / Debit Flag Code
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	



	// Create NTE - Note
	
	// Note Reference Code
	// ADD - Additional Information
	// CER - Certification Narrative
	// DCP - Goals, Rehabilitation Potential, or Discharge Plans
	// DGN - Diagnosis Description
	// TPO - Third Party Organization Notes
	
	
	public static String createNTE( String code, String desc ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "NTE*" );													// segment name
		sb.append( code + "*" );												// 1 - Note Reference Code
		sb.append( desc );														// 2 - Description
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	// Create HI - Healthcare Diagnosis Codes
	
	public static String createHI( boolean icd10, String code1 ){
		return createHI( icd10, code1, null, null, null, null, null );
	}
	public static String createHI( boolean icd10, String code1, String code2 ){
		return createHI( icd10, code1, code2, null, null, null, null );
	}	
	public static String createHI( boolean icd10, String code1, String code2, String code3 ){
		return createHI( icd10, code1, code2, code3, null, null, null );
	}	
	public static String createHI( boolean icd10, String code1, String code2, String code3, String code4 ){
		return createHI( icd10, code1, code2, code3, code4, null, null );
	}	
	public static String createHI( boolean icd10, String code1, String code2, String code3, String code4, String code5 ){
		return createHI( icd10, code1, code2, code3, code4, code5, null );
	}
	
	
	public static String createHI( boolean icd10, String code1, String code2, String code3, String code4, String code5, String code6 ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "HI*" );														// segment name
		sb.append(( icd10 ? "ABK": "BK" ) + ":" + code1 + "*" );				// 1 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Primary Code (diagnosis code)
		
		if (( code2 != null ) && ( code2.length() > 0 ))
			sb.append(( icd10 ? "ABF": "BF" ) + ":" + code2 + "*" );			// 2 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Code (diagnosis code)
		
		if (( code3 != null ) && ( code3.length() > 0 ))
			sb.append(( icd10 ? "ABF": "BF" ) + ":" + code3 + "*" );			// 3 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Code (diagnosis code)
		
		if (( code4 != null ) && ( code4.length() > 0 ))
			sb.append(( icd10 ? "ABF": "BF" ) + ":" + code4 + "*" );			// 4 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Code (diagnosis code)
		
		if (( code5 != null ) && ( code5.length() > 0 ))
			sb.append(( icd10 ? "ABF": "BF" ) + ":" + code5 + "*" );			// 5 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Code (diagnosis code)
		
		if (( code6 != null ) && ( code6.length() > 0 ))
			sb.append(( icd10 ? "ABF": "BF" ) + ":" + code6 + "*" );			// 6 - Code List Qualifier Code (BK=icd9, ABK=icd10) AND Code (diagnosis code)
		
		
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	// Create LX
	
	// Service Line Counter
	
	public static String createLX( int lineNum ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "LX*"  );													// segment name
		sb.append( "" + lineNum );												// 1 - line number
		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

	// Create SV1 - Professional Service Line Item
	
	
	public static String createSV1( String code, String mod1, String mod2, String mod3, String mod4, String desc, String charge, 
			int quantity, String posCode, int dgnPtr1, int dgnPtr2, int dgnPtr3, int dgnPtr4, boolean emergency, boolean epsdt, boolean familyPlanning ){
			
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "SV1*" );													// segment name
		sb.append( "HC:" + code );												// 1 - "HC" code type AND cpt code
		if (( mod1 != null ) && ( mod1.length() > 0 )) sb.append( ":" + mod1 );	//      append modifier
		if (( mod2 != null ) && ( mod2.length() > 0 )) sb.append( ":" + mod2 );	//      append modifier
		if (( mod3 != null ) && ( mod3.length() > 0 )) sb.append( ":" + mod3 );	//      append modifier
		if (( mod4 != null ) && ( mod4.length() > 0 )) sb.append( ":" + mod4 );	//      append modifier
		if (( desc != null ) && ( desc.length() > 0 )) sb.append( ":" + desc );	//      append description
		sb.append( "*" );

		sb.append( charge + "*" );												// 2 - line item charge amount
		sb.append( "UN" + "*" );												// 3 - units "UN" vs minutes "MJ" (for anesthesia)
		sb.append( "" + quantity + "*" );										// 4 - quantity
		sb.append( posCode + "*" );												// 5 - place of service code
		sb.append( "*" );														// 6 - service type code (not used)
		
		sb.append( dgnPtr1 );													// 7 - diagnosis code pointer
		if ( dgnPtr2 > 0 ) sb.append( ":" + dgnPtr2 );							//      diagnosis code pointer
		if ( dgnPtr3 > 0 ) sb.append( ":" + dgnPtr3 );							//      diagnosis code pointer
		if ( dgnPtr4 > 0 ) sb.append( ":" + dgnPtr4 );							//      diagnosis code pointer
		//sb.append( "*" );														// 
		//sb.append( "*" );														// 8 - monetary amount (not used)
		//sb.append(( emergency ? "Y": "" ) + "*" );								// 9 - Emergency Indicator
		//sb.append( "*" );														// 10 - multiple procedure code (not used)
		//sb.append(( epsdt ? "Y": "" ) + "*" );									// 11 - EPSDT Indicator
		//sb.append(( familyPlanning ? "Y": "" ) + "*" );							// 12 - family planning Indicator

		sb.append( "~" );

		String s = sb.toString();
		return s;
	}
	
	
	

/*	public static int countSegments( StringBuilder sb ){
		
		int start = 0;
		int next = 0;
		int segments = 0;
		
		while (( next = sb.indexOf( "~", start )) > 0 ){
			start = next+1;
			++segments;
		}
		
		return segments;
	}
*/


}
