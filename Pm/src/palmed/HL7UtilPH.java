package palmed;

import usrlib.Address;
import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.StringHelpers;
import usrlib.Time;

public class HL7UtilPH {

	public static String tz= "-0600";
	public static String defaultAreaCode = "573";

	
	
	
	
	
	/**
	 * MSH - Message Header Segment
	 * 
	 * 
	 * @param date
	 * @param time
	 * @param controlID
	 * @return
	 */
	public static String createMSH( Date date, Time time, String controlID, String facilityNPI  ){
		
		String strDateTime = null;
		String msgType = "ADT^A03^ADT_A03";
		
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "MSH|^~\\&" );												// 1,2 - segment name, separator, and encoding characters
		sb.append( "|" );														// 3 - sending application
		sb.append( "|" ); sb.append( String.format( "%s^%s^%s", "Facility_NPI", facilityNPI, "NPI" ));	// 4 - sending facility  ??? table 301??
		sb.append( "|" );														// 5 - receiving application
		sb.append( "|" );														// 6 - receiving facility
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz )); 		// 7 - date/time of message
		sb.append( "|" );														// 8 - security
		sb.append( "|" ); sb.append( msgType );									// 9 - message type (ie. VXU)
		sb.append( "|" ); sb.append( controlID );								// 10 - message control ID
		sb.append( "|" ); sb.append( "P" );										// 11 - process control ID
		sb.append( "|" ); sb.append( "2.5.1" );									// 12 - version ID
		sb.append( "|" );														// 13 - sequence number
		sb.append( "|" );														// 14 - continuation pointer
		sb.append( "|" );														// 15 - accept acknowledgement type
		sb.append( "|" );														// 16 - application acknowledgement type
		sb.append( "|" );														// 17 - country code
		sb.append( "|" );														// 18 - character set
		sb.append( "|" );														// 19 - principle language
		sb.append( "|" );														// 20 - alternate character set
		sb.append( "|" );														// 21 - message profile identifier
		sb.append( "\n" );

		String s = sb.toString();
		return s;
	}

	
	
	
	
	
	
	
	
	/**
	 * EVN - Event Type Segmentt
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createEVN( Date date, Time time, String facilityName, String facilityNPI ){
		

		
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "EVN" );														// segment name
		sb.append( "|" );														// 1 - event type code
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 2 - recorded date/time
		sb.append( "|" );														// 3 - 
		sb.append( "|" );														// 4 - 
		sb.append( "|" ); 														// 5 - 
		sb.append( "|" );														// 6 - 
		sb.append( "|" ); sb.append( String.format( "%s^%s^%s", facilityName, facilityNPI, "NPI" ));	// 7 - event facility
		sb.append( "\n" );



		String s = sb.toString();
		return s;
	}



	
	
	
	
	

	
	

	/**
	 * PID - Patient ID Segment
	 * 
	 * 
	 * @param ptRec
	 * @param name
	 * @param bdate
	 * @param sex
	 * @param add
	 * @return
	 */
	public static String createPID( Rec ptRec, Name name, Date bdate, Sex sex, palmed.Race race, palmed.Ethnicity ethnic, Address add  ){
		
		String strDateTime = null;

		// XPN - Extended Person Name
		String ptname = String.format( "%s^%s^%s^%s^^^L", name.getLastName(), name.getFirstName(), name.getMiddleName(), name.getSuffix());
		String ptnum = String.format( "PT%06d", ptRec.getRec());
		
		
		// XAD - Extended Address Record
		// address types HL7 0190 C-current/temp, P-perm, M-mailing, B-business, O-office, H-home, etc
		String addressType = "H";
		String ptAddress = String.format( "%s^%s^%s^%s^%s^^%s", add.getStreet(), add.getLine2(), add.getCity(), add.getState(), add.getZip5(), addressType );

		//String ptPhone = String.format( "^%s^%s", "WPN", "PH", )
		//String ptPhone = String.format( "^%s^%s", "NET", "Internet", )

		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PID" );														// segment name
		sb.append( "|" ); sb.append( "1" );										// 1 - set ID
		sb.append( "|" );														// 2 - NULL - patient ID
		sb.append( "|" ); sb.append( formatCX( ptnum, "PAL/MED", "MR" ));		// 3R - Patient identifier list
		sb.append( "|" );														// 4 - NULL - alternate patient ID
		sb.append( "|" ); sb.append( ptname );									// 5R - patient name
		sb.append( "|" );														// 6 - mother's maiden name
		sb.append( "|" ); sb.append( formatDate( bdate )); 						// 7R - date/time of birth
		sb.append( "|" ); sb.append( sex.getAbbr());							// 8 - administrative sex
		sb.append( "|" ); 														// 9 - NULL, UNUSED - patient alias
		sb.append( "|" ); if ( race != null ) sb.append( formatCE( convertRace( race ), race.getLabel(), "HL70005" ));	// 10 - race
		sb.append( "|" ); sb.append( ptAddress );								// 11 - patient address
		sb.append( "|" ); 														// 12 - NULL - country code
		sb.append( "|" ); //sb.append( formatPhoneNumber( add.getHome_ph(), "PRN"));// 13 - phone number - home         *********PHIN doesnt want!!!!
		sb.append( "|" );														// 14 - phone number - business
		sb.append( "|" );														// 15 - primary language
		sb.append( "|" );														// 16 - marital status
		sb.append( "|" );														// 17 - religion
		sb.append( "|" );														// 18 - patient account number
		sb.append( "|" );														// 19 - SSN
		sb.append( "|" );														// 20 - DL number
		sb.append( "|" );														// 21 - Mothers identifier	
		sb.append( "|" ); if ( ethnic != null ) sb.append( formatCE( convertEthnic( ethnic ), ethnic.getLabel(), "HL70189" ));	// 22 - ethnic group
		sb.append( "|" );														// 23 - birth place
		sb.append( "|" );														// 24 - multiple birth indicator
		sb.append( "|" );														// 25 - birth order		
		sb.append( "|" );														// 26 - citizenship
		sb.append( "|" );														// 27 - veterans military status
		sb.append( "|" );														// 28 - nationality
		sb.append( "|" );														// 29 - patient death date and time
		sb.append( "|" );														// 30 - patient death indicator
		//sb.append( "|" );														// 31 - identity unknown indicator
		//sb.append( "|" );														// 32 - identity reliability code
		//sb.append( "|" );														// 33 - last update date/time
		//sb.append( "|" );														// 34 - last update facility
		//sb.append( "|" );														// 35 - species code
		//sb.append( "|" );														// 36 - breed code
		//sb.append( "|" );														// 37 - strain
		//sb.append( "|" );														// 38 - production class code
		//sb.append( "|" );														// 34 - tribal citizenship
		sb.append( "\n" );



		String s = sb.toString();
		return s;
	}



	
	
	
	/**
	 * PV1 - Patient Visit PV1 segment
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createPV1( Date date, Time time, Rec ptRec, String ptClass ){
		
		// Table 0007 Admission Type - http://www.hl7.org/special/committees/vocab/V26_Appendix_A.pdf
		// A-accident C-elective E-emergency L-labor and delivery N-newborn birth R-routine U-urgent
		
		String ptnum = String.format( "PT%06d", ptRec.getRec());
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PV1" );														// segment name
		sb.append( "|" );														// 1 - set ID
		sb.append( "|" ); sb.append( ptClass );									// 2R - patient class (syndromic surveillance) Table 0004
		sb.append( "|" );														// 3 - assigned patient location
		sb.append( "|" ); sb.append( "R" ); // hardcode to Routine				// 4 - admission type - table 0007
		sb.append( "|" ); 														// 5 - 
		sb.append( "|" );														// 6 - 
		sb.append( "|" ); 								 						// 7 - 
		sb.append( "|" ); 														// 8 - 
		sb.append( "|" ); 														// 9 - 
		sb.append( "|" ); sb.append( "MED" ); // hardcode to medical service	// 10 - hospital service - table 0069
		sb.append( "|" ); 														// 11 - 
		sb.append( "|" ); 														// 12 - 
		sb.append( "|" );														// 13 - 
		sb.append( "|" );														// 14 - admit source - table 0023
		sb.append( "|" );														// 15 - 
		sb.append( "|" ); 														// 16 - 
		sb.append( "|" ); 														// 17 - 
		sb.append( "|" );														// 18 - 
		sb.append( "|" ); sb.append( formatCX( ptnum, "PAL/MED", "MR" ));		// 19 - visit number - unique identifier for pt visit
		sb.append( "|" );														// 20 - 
		sb.append( "|" );														// 21 - 
		sb.append( "|" ); 														// 22 - 
		sb.append( "|" ); 														// 23 - 
		sb.append( "|" ); 														// 24 - 
		sb.append( "|" );														// 25 - 
		sb.append( "|" );														// 26 - 
		sb.append( "|" );														// 27 - 
		sb.append( "|" ); 														// 28 - 
		sb.append( "|" ); 														// 29 - 
		sb.append( "|" );														// 30 - 
		sb.append( "|" );														// 31 - 
		sb.append( "|" );														// 32 - 
		sb.append( "|" );														// 33 - 
		sb.append( "|" ); 														// 34 - 
		sb.append( "|" ); 														// 35 - 
		sb.append( "|" ); sb.append( "01"); // hardcode to discharged home		// 36RE - discharge disposition - NUBC UB04 field 17  (http://www.whainfocenter.com/wipop_07/Appendix_VI.pdf)
		sb.append( "|" );														// 37 - 
		sb.append( "|" );														// 38 - 
		sb.append( "|" );														// 39 - 
		sb.append( "|" ); 														// 40 - 
		sb.append( "|" ); 														// 41 - 
		sb.append( "|" );														// 42 - 
		sb.append( "|" );														// 43 - 
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 44 - admit date/time (time of patient presentation)
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 45 - discharge date/time 
		sb.append( "|" ); 														// 46 - 
		sb.append( "|" ); 														// 47 - 
		sb.append( "|" ); 														// 48 - 
		sb.append( "|" );														// 49 - 
		sb.append( "|" );														// 50 - 
		sb.append( "|" );														// 51 - 
		sb.append( "|" ); 														// 52 - 
		sb.append( "\n" );



		String s = sb.toString();
		return s;
	}



	
	
	
	
	
	
	
	/**
	 * PV2 - Patient Visit PV2 segment
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createPV2( Date date, Time time, Rec ptRec ){
		
		String ptnum = String.format( "PT%06d", ptRec.getRec());
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "PV2" );														// segment name
		sb.append( "|" );														// 1 - 
		sb.append( "|" );														// 2 - 
		sb.append( "|" );														// 3RE - admit reason CE
		sb.append( "|" ); 														// 4 - 
		sb.append( "|" ); 														// 5 - 
		sb.append( "|" );														// 6 - 
		sb.append( "|" ); 								 						// 7 - 
		sb.append( "|" ); 														// 8 - 
		sb.append( "|" ); 														// 9 - 
		sb.append( "|" ); 														// 10 - 
		sb.append( "|" ); 														// 11 - 
		sb.append( "|" ); 														// 12 - 
		sb.append( "|" );														// 13 - 
		sb.append( "|" );														// 14 - 
		sb.append( "|" );														// 15 - 
		sb.append( "|" ); 														// 16 - 
		sb.append( "|" ); 														// 17 - 
		sb.append( "|" );														// 18 - 
		sb.append( "|" ); 														// 19 - 
		sb.append( "|" );														// 20 - 
		sb.append( "|" );														// 21 - 
		sb.append( "|" ); 														// 22 - 
		sb.append( "|" ); 														// 23 - 
		sb.append( "|" ); 														// 24 - 
		sb.append( "|" );														// 25 - 
		sb.append( "|" );														// 26 - 
		sb.append( "|" );														// 27 - 
		sb.append( "|" ); 														// 28 - 
		sb.append( "|" ); 														// 29 - 
		sb.append( "|" );														// 30 - 
		sb.append( "|" );														// 31 - 
		sb.append( "|" );														// 32 - 
		sb.append( "|" );														// 33 - 
		sb.append( "|" ); 														// 34 - 
		sb.append( "|" ); 														// 35 - 
		sb.append( "|" ); 														// 36 - 
		sb.append( "|" );														// 37 - 
		sb.append( "|" );														// 38 - 
		sb.append( "|" );														// 39 - 
		sb.append( "|" ); 														// 40 - 
		sb.append( "|" ); 														// 41 - 
		sb.append( "|" );														// 42 - 
		sb.append( "|" );														// 43 - 
		sb.append( "|" ); 														// 44 - 
		sb.append( "|" ); 											;			// 45 - 
		sb.append( "|" ); 														// 46 - 
		sb.append( "|" ); 														// 47 - 
		sb.append( "|" ); 														// 48 - 
		sb.append( "|" );														// 49 - 
		sb.append( "\n" );

		String s = sb.toString();
		return s;
	}



	
	
	
	
	
	
	
	
	
	
	/**
	 * OBX - Observation/Result Segment
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createOBX( int count, Rec ptRec, Date date, Time time  ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "OBX" );														// segment name
		sb.append( "|" ); sb.append( String.valueOf( count ));					// 1 - set ID
		sb.append( "|" ); 														// 2R - value type - table 0125
		sb.append( "|" ); 														// 3R - observation identifier (syndromic surveillance)
		sb.append( "|" );														// 4 - 
		sb.append( "|" ); 										;				// 5 - observation value
		sb.append( "|" ); 														// 6 - units
		sb.append( "|" ); 														// 7 - 
		sb.append( "|" ); 														// 8 - 
		sb.append( "|" ); 														// 9 - 
		sb.append( "|" ); 														// 10 - 
		sb.append( "|" ); sb.append( "F" );										// 11 - observation result status
		sb.append( "|" ); 														// 12 - 
		sb.append( "|" );														// 13 - 
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 14 - date/time of observation
		sb.append( "|" ); 														// 15 - 
		sb.append( "|" ); 														// 16 - 
		sb.append( "|" ); 														// 17 - 
		sb.append( "|" );														// 18 - 
		sb.append( "|" );														// 19 - 
		sb.append( "\n" );

		String s = sb.toString();
		return s;
	}



	
	
	
	
	
	
	
	
	
	
	
	/**
	 * DG1 - Diagnosis Segment
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createDG1( int count, Rec ptRec, Date date, Time time, String dgn, String desc, String coding  ){
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "DG1" );														// segment name
		sb.append( "|" ); sb.append( String.valueOf( count ) );					// 1 - set ID
		sb.append( "|" ); 														// 2 - 
		sb.append( "|" ); sb.append( formatCE( dgn, desc, coding ));			// 3R - diagnosis code
		sb.append( "|" );														// 4 - 
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 5 - diagnosis date/time
		sb.append( "|" ); sb.append( "F" );										// 6 - diagnosis type A-admitting, W-working, F-final
		sb.append( "|" ); 														// 7 - 
		sb.append( "|" ); 														// 8 - 
		sb.append( "|" ); 														// 9 - 
		sb.append( "|" ); 														// 10 - 
		sb.append( "|" ); sb.append( "F" );										// 11 -
		sb.append( "|" ); 														// 12 - 
		sb.append( "|" );														// 13 - 
		sb.append( "|" ); 														// 14 -
		sb.append( "|" ); 														// 15 - 
		sb.append( "|" ); 														// 16 - 
		sb.append( "|" ); 														// 17 - 
		sb.append( "|" );														// 18 - 
		sb.append( "|" );														// 19 - 
		sb.append( "\n" );

		String s = sb.toString();
		return s;
	}



	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * RXA - Pharmacy/Treatment Administration
	 * 
	 * 
	 * @param date
	 * @param ptRec
	 * @return
	 */
	public static String createRXA( Date date, Time time, Rec ptRec, String cvx, String desc, String mvx, String mfr, 
			String amt, Immunizations.Units units, Immunizations.Source source, String lot, Date edate,
			Name name, String npi, String degree, Address address  ){
		

		// RXA-9, table NIP001 
		// 00-new imm record, 01+ historical	
		
		// set amount to 999 if none entered, and then don't send units
		if (( amt == null ) || ( amt.length() < 1 )){
			amt="999";
			units = null;
		}
		
		
		StringBuilder sb = new StringBuilder( 128 );
		
		sb.append( "RXA" );														// segment name
		sb.append( "|" ); sb.append( "0" );										// 1R - give Sub-ID Counter (constrains to '0')
		sb.append( "|" ); sb.append( "1" );										// 2R - administration Sub-ID counter (constrains to '1')
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 3R - date/time start of administration
		sb.append( "|" ); sb.append( formatDateTime( date, time, tz ));			// 4 - date/time end of administration
		sb.append( "|" ); sb.append( formatCE( cvx, desc, "CVX" ));				// 5R - administered code (CVX)
		sb.append( "|" ); sb.append( amt );										// 6R - administered amount
		sb.append( "|" );
			if ( units != null )
				sb.append( formatCE( units.getCodedEntry(), units.getLabel(), units.getCodingSystem()));	// 7 - administered units
			
		sb.append( "|" ); 														// 8 - administered dosage form
		sb.append( "|" ); sb.append( formatCE( source.getCodedEntry(), source.getLabel(), source.getCodingSystem()) );	// 9 - administration notes
		sb.append( "|" ); sb.append( formatXCN( name, npi, degree ));			// 10 - administering provider
		sb.append( "|" ); sb.append( formatLA2( address ));						// 11 - administered at location
		sb.append( "|" ); 														// 12 - administered per (time unit)
		sb.append( "|" );														// 13 - administered strength
		sb.append( "|" );														// 14 - administered strength units
		sb.append( "|" ); sb.append( lot );								// 15 - substance lot number
		sb.append( "|" ); sb.append( formatDate( edate ));					// 16 - substance expiration date
		sb.append( "|" ); sb.append( formatCE( mvx, mfr, "MVX"));				// 17 - manufacturer name MVX
		sb.append( "|" );														// 18 - substance/treatment refusal reason
		sb.append( "|" );														// 19 - indication
		sb.append( "|" );														// 20 - completion status
		sb.append( "|" ); sb.append( "A" );										// 21 - action code - RXA
		sb.append( "|" );														// 22 - system entry date/time
		sb.append( "|" );														// 23 - administered drug strength volume
		sb.append( "|" );														// 24 - administered drug strength volume units
		sb.append( "|" );														// 25 - administered barcode identifier
		sb.append( "|" );														// 26 - pharmacy order type
		sb.append( "\n" );



		String s = sb.toString();
		return s;
	}



	
	
	
	
	
	
	public static String formatLA2( Address add ){
		return String.format( "^^^^^^^^%s^%s^%s^%s^%s^^%s", add.getStreet(), add.getLine2(), add.getCity(), add.getState(), add.getZip5(), "O" 	);
	}
	
	
	public static String formatXCN( Name name, String npi, String degree ){
		
		return String.format( "%s^%s^%s^%s^%s^%s^%s^^^L^^^%s^^", npi, 
				name.getLastName(), name.getFirstName(), name.getMiddleName(), name.getSuffix(), "", degree, "NPI" );
	}
	
	public static String formatCE( String x, String y, String z ){
		return String.format( "%s^%s^%s", x, y, z );
	}
	
	public static String formatCX( String id, String assn, String type ){
		return String.format( "%s^^^%s^%s", id, assn, type );
	}
	
	
	
	
	
	public static String formatPhoneNumber( String phone, String type ){
		
		// XTN - Extended Telecommunications Record
		// telecommunication codes from HL7 0201:  PRN-home phone, WPN-work phone
		// codes from HL7 202: PH-phone, Internet-email, etc
		
		phone = StringHelpers.onlyDigits( phone );
		if ( phone.length() < 7 ) return "";
		
		String area = "";
		String local = "";
		
		if ( phone.length() > 8 ){
			area = phone.substring( 0, 3 );
			local = phone.substring( 3, phone.length());
		} else {
			area = defaultAreaCode;
			local = phone;
		}
		
		return String.format( "^%s^%s^^^%s^%s", type, "PH", area, local );
	}
	
	public static String formatDateTime( Date date, Time time, String timezone ){
		// YYYYMMDDHHMMSS-ZZZZ
		return date.getPrintable( 8 ) + time.getPrintable( 1 ) + timezone;
	}

	public static String formatDate( Date date ){
		// YYYYMMDD
		return date.getPrintable( 8 );
	}
	
	
	public static String convertRace( palmed.Race race ){
		if (( race == null)) return null;
		
		// From HL7 table 0005 Race
		String s = "2131-1";	// Other Race
		if ( race == Race.CAUCASIAN ){
			s = "2106-3";
		} else if ( race == Race.BLACK ){
			s = "2054-5";
		} else if ( race == Race.ASIAN ){
			s = "2028-9";
		} else if ( race == Race.NATIVE ){
			s = "1002-5";
		} else if ( race == Race.HAWAIIAN ){
			s = "2076-8";
		}
		return s;
	}
	public static String convertEthnic( palmed.Ethnicity ethnic ){
		if (( ethnic == null)) return null;
		
		// From HL7 table 0189 Ethnic group
		String s = "2186-5";	// not hispanic or latino
		if ( ethnic == Ethnicity.HISPANIC ){
			s = "2135-2";
		}
		return s;
	}


}
