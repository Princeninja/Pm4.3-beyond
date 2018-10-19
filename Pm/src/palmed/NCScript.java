package palmed;

import usrlib.XMLElement;

public class NCScript {
			
	//private XMLElement root;		// root element

	
	
	
	// Constructor - initializes the root element
	
	public static XMLElement setHeader(){

		XMLElement root = new XMLElement();
		
		root.setName( "NCScript" );
		root.setAttribute( "xmlns", "http://secure.newcropaccounts.com/interfaceV7" );
		root.setAttribute( "xmlns:NCStandard", "http://secure.newcropaccounts.com/interfaceV7:NCStandard" );
		root.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
		return root;
	}
	
	
		
		
		
	// Set credentials
	//public XMLElement setCredentials( String partnerName, String name, String password, String product, String version ){
	//	return setCredentials( this.root, partnerName, name, password, product, version );
	//}

	public static XMLElement setCredentials( XMLElement r, String partnerName, String name, String password, String product, String version ){
		
		XMLElement c = new XMLElement();
		c.setName( "Credentials" );
		
		if ( partnerName.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "partnerName" );
			p.setContent( partnerName );
			c.addChild( p );
		}
		if ( name.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "name" );
			p.setContent( name );
			c.addChild( p );
		}
		if ( password.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "password" );
			p.setContent( password );
			c.addChild( p );
		}
		if ( product.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "productName" );
			p.setContent( product );
			c.addChild( p );
		}
		if ( version.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "productVersion" );
			p.setContent( version );
			c.addChild( p );
		}
		
		r.addChild( c );
		return c;
	}
		
		
		
		
		// Set User Role
	public static XMLElement setUserRole( XMLElement r, String user, String role ){

		XMLElement c = new XMLElement();
		c.setName( "UserRole" );
		
		if ( user.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "user" );
			p.setContent( user );
			c.addChild( p );
		}
		if ( role.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "role" );
			p.setContent( role );
			c.addChild( p );
		}
		
		r.addChild( c );
		return c;
	}
		
		
		
	
		// Set Destination (Landing Page)
	public static XMLElement setDestination( XMLElement r, String page ){
		
		XMLElement c = new XMLElement();
		c.setName( "Destination" );
		
		{  // required
			XMLElement p = new XMLElement();
			p.setName( "requestedPage" );
			p.setContent( page );
			c.addChild( p );
		}
		
		r.addChild( c );
		return c;
	}
		
		
		
		
		
		// Account Name, ID, address
	public static XMLElement setAccount( XMLElement r, String accountID, String accountName, String siteID, String address1, String address2, String city, String state, String zip, String zip4, String country, String phone, String fax ){

		XMLElement c = new XMLElement();
		c.setName( "Account" );
		c.setAttribute( "ID", accountID );	// required
		
		{	// required
			XMLElement p = new XMLElement();
			p.setName( "accountName" );
			p.setContent( accountName );
			c.addChild( p );
		}
		{	// required
			XMLElement p = new XMLElement();
			p.setName( "siteID" );
			p.setContent( siteID );
			c.addChild( p );
		}
		{
			XMLElement p = new XMLElement();
			p.setName( "AccountAddress" );
			c.addChild( p );
			
			if ( address1.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "address1" );
				q.setContent( address1 );
				p.addChild( q );
			}
			if ( address2.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "address2" );
				q.setContent( address2 );
				p.addChild( q );
			}
			if ( city.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "city" );
				q.setContent( city );
				p.addChild( q );
			}
			if ( state.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "state" );
				q.setContent( state );
				p.addChild( q );
			}
			if ( zip.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "zip" );
				q.setContent( zip );
				p.addChild( q );
			}
			if (( zip4 != null ) && ( zip4.length() > 0 )){
				XMLElement q = new XMLElement();
				q.setName( "zip4" );
				q.setContent( zip4 );
				p.addChild( q );
			}
			if ( country.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "country" );
				q.setContent( country );
				p.addChild( q );
			}
		}
		
		if ( phone.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "accountPrimaryPhoneNumber" );
			p.setContent( phone );
			c.addChild( p );
		}
		if ( fax.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "accountPrimaryFaxNumber" );
			p.setContent( fax );
			c.addChild( p );
		}

		r.addChild( c );
		return c;
	}
			
			
			
		

		
		
		// Location Name, ID, address
	public static XMLElement setLocation( XMLElement r, String locationID, String locationName, String address1, String address2, String city, String state, String zip, String zip4, String country, String phone, String fax, String contact ){

		XMLElement c = new XMLElement();
		c.setName( "Location" );
		if (( locationID != null ) && ( locationID.length() > 0 )) c.setAttribute( "ID", locationID );
		
		{	// required
			XMLElement p = new XMLElement();
			p.setName( "locationName" );
			p.setContent( locationName );
			c.addChild( p );
		}
		{
			XMLElement p = new XMLElement();
			p.setName( "LocationAddress" );
			c.addChild( p );
			
			if ( address1.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "address1" );
				q.setContent( address1 );
				p.addChild( q );
			}
			if ( address2.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "address2" );
				q.setContent( address2 );
				p.addChild( q );
			}
			if ( city.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "city" );
				q.setContent( city );
				p.addChild( q );
			}
			if ( state.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "state" );
				q.setContent( state );
				p.addChild( q );
			}
			if ( zip.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "zip" );
				q.setContent( zip );
				p.addChild( q );
			}
			if (( zip4 != null ) && ( zip4.length() > 0 )){
				XMLElement q = new XMLElement();
				q.setName( "zip4" );
				q.setContent( zip4 );
				p.addChild( q );
			}
			if ( country.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "country" );
				q.setContent( country );
				p.addChild( q );
			}
		}
		
		if ( phone.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "primaryPhoneNumber" );
			p.setContent( phone );
			c.addChild( p );
		}
		if ( fax.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "primaryFaxNumber" );
			p.setContent( fax );
			c.addChild( p );
		}
		if ( contact.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "pharmacyContactNumber" );
			p.setContent( contact );
			c.addChild( p );
		}

		r.addChild( c );
		return c;
	}
			
			
			
		

		
		// Licensed Prescriber
	public static XMLElement setPrescriber( XMLElement r, String prescriberID, String last, String first, String middle, String prefix, String suffix, 
			String DEA, String npi, String licenseState, String licenseNum ){

		XMLElement c = new XMLElement();
		c.setName( "LicensedPrescriber" );
		if (( prescriberID != null ) && ( prescriberID.length() > 0 )) c.setAttribute( "ID", prescriberID );

		{
			XMLElement p = new XMLElement();
			p.setName( "LicensedPrescriberName" );
			c.addChild( p );
			
			if ( last.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "last" );
				q.setContent( last );
				p.addChild( q );
			}
			if ( first.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "first" );
				q.setContent( first );
				p.addChild( q );
			}
			if ( middle.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "middle" );
				q.setContent( middle );
				p.addChild( q );
			}
			if ( prefix.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "prefix" );
				q.setContent( prefix );
				p.addChild( q );
			}
			if ( suffix.length() > 0 ){
				XMLElement q = new XMLElement();
				q.setName( "suffix" );
				q.setContent( suffix );
				p.addChild( q );
			}
		}
		
		if ( DEA.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "dea" );
			p.setContent( DEA );
			c.addChild( p );
		}
		if ( licenseState.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "licenseState" );
			p.setContent( licenseState );
			c.addChild( p );
		}
		if ( licenseNum.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "licenseNumber" );
			p.setContent( licenseNum );
			c.addChild( p );
		}
		if ( npi.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "npi" );
			p.setContent( npi );
			c.addChild( p );
		}

		r.addChild( c );
		return c;
		}
			
			
			
		
		
		
		
		// Patient Name, ID, Address, stats
	public static XMLElement setPatient( XMLElement r, String patientID, String last, String first, String middle, String prefix, String suffix, String mrNum, String SSN, String memo, String address1, String address2, String city, String state, String zip, String zip4, String country, String phone, String DOB, String gender ){

			XMLElement c = new XMLElement();
			c.setName( "Patient" );
			if (( patientID != null ) && ( patientID.length() > 0 )) c.setAttribute( "ID", patientID );
			
			{
				XMLElement p = new XMLElement();
				p.setName( "PatientName" );
				c.addChild( p );
				
				if ( last.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "last" );
					q.setContent( last );
					p.addChild( q );
				}
				if ( first.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "first" );
					q.setContent( first );
					p.addChild( q );
				}
				/*if ( middle.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "middle" );
					q.setContent( middle );
					p.addChild( q );
				}
				if ( suffix.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "prefix" );
					q.setContent( prefix );
					p.addChild( q );
				}
				if ( suffix.islength() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "suffix" );
					q.setContent( suffix );
					p.addChild( q );
				}*/
			}
			
			if ( mrNum.length() > 0 ){
				XMLElement p = new XMLElement();
				p.setName( "medicalRecordNumber" );
				p.setContent( mrNum );
				c.addChild( p );
			}
			if ( SSN.length() > 0 ){
				XMLElement p = new XMLElement();
				p.setName( "socialSecurityNumber" );
				p.setContent( SSN );
				c.addChild( p );
			}
			if ( memo.length() > 0 ){
				XMLElement p = new XMLElement();
				p.setName( "memo" );
				p.setContent( memo );
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "PatientAddress" );
				c.addChild( p );
				
				{  // required
					XMLElement q = new XMLElement();
					q.setName( "address1" );
					q.setContent( address1 );
					p.addChild( q );
				}
				if ( address2.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "address2" );
					q.setContent( address2 );
					p.addChild( q );
				}
				if ( city.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "city" );
					q.setContent( city );
					p.addChild( q );
				}
				if ( state.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "state" );
					q.setContent( state );
					p.addChild( q );
				}
				if ( zip.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "zip" );
					q.setContent( zip );
					p.addChild( q );
				}
				if (( zip4 != null ) && ( zip4.length() > 0 )){
					XMLElement q = new XMLElement();
					q.setName( "zip4" );
					q.setContent( zip4 );
					p.addChild( q );
				}
				if ( country.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "country" );
					q.setContent( country );
					p.addChild( q );
				}
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "PatientContact" );
				c.addChild( p );
				
				if ( phone.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "homeTelephone" );
					q.setContent( phone );
					p.addChild( q );
				}
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "PatientCharacteristics" );
				c.addChild( p );
				
				if ( DOB.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "dob" );
					q.setContent( DOB );
					p.addChild( q );
				}
				if ( gender.length() > 0 ){
					XMLElement q = new XMLElement();
					q.setName( "gender" );
					q.setContent( gender );
					p.addChild( q );
				}

			}

			r.addChild( c );
			return c;
		}
				
				
				
		
		
	
	
	// Set Freeform Allergy
	public static XMLElement setFreeFormAllergy( XMLElement parent, String allergyID, String drug, String severity, String comment ){

		XMLElement c = new XMLElement();
		c.setName( "PatientFreeformAllergy" );
		if (( allergyID != null ) && ( allergyID.length() > 0 )) c.setAttribute( "ID", allergyID );

		
		{  // required
			XMLElement p = new XMLElement();
			p.setName( "allergyName" );
			p.setContent( drug );
			c.addChild( p );
		}
		if (( severity != null ) && ( severity.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "allergySeverityTypeID" );
			p.setContent( severity );
			c.addChild( p );
		}
		if (( comment != null ) && ( comment.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "allergyComment" );
			p.setContent( comment );
			c.addChild( p );
		}

		parent.addChild( c );
		return c;
	}
			
			
			
		
		
		
		

	// Set Codified Allergy
	public static XMLElement setCodifiedAllergy( XMLElement parent, String allergyID, String compositeID, String severity, String comment ){

		XMLElement c = new XMLElement();
		c.setName( "PatientAllergies" );
		//if (( allergyID != null ) && ( allergyID.length() > 0 )) c.setAttribute( "ID", allergyID );

		
		{  // required
			XMLElement p = new XMLElement();
			p.setName( "allergyID" );
			p.setContent( compositeID );
			c.addChild( p );
		}
		{  // required
			XMLElement p = new XMLElement();
			p.setName( "allergyTypeID" );
			p.setContent( "FDB" );
			c.addChild( p );
		}
		if (( severity != null ) && ( severity.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "allergySeverityTypeID" );
			p.setContent( severity );
			c.addChild( p );
		}
		if (( comment != null ) && ( comment.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "allergyComment" );
			comment = comment.replaceAll( "[^\\p{Alnum}]", "" );
			p.setContent( comment );
			c.addChild( p );
		}

		parent.addChild( c );
		return c;
	}
			
			
			
		
		
		
		

		
		// New Prescriptions
	
	
	
	

	// Outside med coded and codified sig
	public static XMLElement addOutsideRxCoded( XMLElement r, String erxID, String date, String prov, String drugID, int num, int refills, String type, String sig,
			int actionID, int dosageID, int formID, int routeID, int freqID, boolean flgPRN, boolean flgDAW, String pharmMsg ){

		XMLElement c = addElement( r, "OutsidePrescription", null );
		
		if ( erxID.length() > 0 ) addElement( c, "externalId", erxID );
		if ( date.length() > 0 ) addElement( c, "date", date );
		if ( prov.length() > 0 ) addElement( c, "doctorName", prov );
		if ( num > 0 ) addElement( c, "dispenseNumber", num );
		if ( sig.length() > 0 )addElement( c, "sig", sig );
		if ( refills >= 0 ) addElement( c, "refillCount", refills );
		
		addElement( c, "substitution", flgDAW ? "DispenseAsWritten": "SubstitutionAllowed" );
		if (( pharmMsg != null ) && ( pharmMsg.length() > 0 )) addElement( c, "pharmacistMessage", pharmMsg );

		addElement( c, "drugIdentifier", drugID );		// required
		addElement( c, "drugIdentifierType", "FDB" );

		if (( type != null ) && ( type.length() > 0 )) addElement( c, "prescriptionType", type );

		addCodifiedSig( c, actionID, dosageID, formID, routeID, freqID );		
		addElement( c, "prn", flgPRN ? "Yes": "No" );
		
		return c;
	}

	// Outside med Uncoded and codified sig
	public static XMLElement addOutsideRxUncoded( XMLElement r, String erxID, String date, String prov, String drugName, int num, int refills, String type, String sig,
			int actionID, int dosageID, int formID, int routeID, int freqID, boolean flgPRN, boolean flgDAW, String pharmMsg ){

		XMLElement c = addElement( r, "OutsidePrescription", null );
		
		if ( erxID.length() > 0 ) addElement( c, "externalId", erxID );
		if ( date.length() > 0 ) addElement( c, "date", date );
		if ( prov.length() > 0 ) addElement( c, "doctorName", prov );

		
		drugName = cleanDrugName( drugName );	// needed for some older data where drugs contain other stuff
		addElement( c, "drug", drugName );		// required
		
		if ( num > 0 ) addElement( c, "dispenseNumber", num );
		
		sig = cleanString( sig );				// needed for some older data where the 'note text' or 'addl sig' data corrupted
		if ( sig.length() > 0 )addElement( c, "sig", sig );
		if ( refills >= 0 ) addElement( c, "refillCount", refills );
		
		addElement( c, "substitution", flgDAW ? "DispenseAsWritten": "SubstitutionAllowed" );
		if (( pharmMsg != null ) && ( pharmMsg.length() > 0 )) addElement( c, "pharmacistMessage", pharmMsg );
		
		if (( type != null ) && ( type.length() > 0 )) addElement( c, "prescriptionType", type );

		addCodifiedSig( c, actionID, dosageID, formID, routeID, freqID );
		addElement( c, "prn", flgPRN ? "Yes": "No" );
		
		return c;
	}
	
	
	
	
	
	// Add Codified Sig	
	public static XMLElement addCodifiedSig( XMLElement parent, int actionID, int dosageID, int formID, int routeID, int freqID ){
	
		XMLElement p = addElement( parent, "codifiedSigType", null );		

			addElement( p, "ActionType", actionID );
			addElement( p, "NumberType", dosageID );
			addElement( p, "FormType", formID );
			addElement( p, "RouteType", routeID );
			addElement( p, "FrequencyType", freqID );
		
		return p;
	}
	
	
	
	
	
	
	
	
	// Add XML Elements
	public static XMLElement addElement( XMLElement parent, String name, String content ){
		
		XMLElement q = new XMLElement();
		q.setName( name );
		if (( content != null ) && ( content.length() > 0 ))
			q.setContent( content );
		parent.addChild( q );
		return q;
	}
	
	public static XMLElement addElement( XMLElement parent, String name, int content ){
		
		XMLElement q = new XMLElement();
		q.setName( name );
		q.setContent( String.valueOf( content ));
		parent.addChild( q );
		return q;
	}
	
	
	
	
	
	
	
	
	// Outside med with misc med	
	public static XMLElement addOutsideRx( XMLElement r, String erxID, String date, String prov, String drug, String num, String sig, String refills, String type ){

		XMLElement c = new XMLElement();
		c.setName( "OutsidePrescription" );
		
		if ( erxID.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "externalId" );
			p.setContent( erxID );
			c.addChild( p );
		}
		if ( date.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "date" );
			p.setContent( date );
			c.addChild( p );
		}
		if ( prov.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "doctorName" );
			p.setContent( prov );
			c.addChild( p );
		}
		{ // Required
			XMLElement p = new XMLElement();
			p.setName( "drug" );
			p.setContent( drug );
			c.addChild( p );
		}
		if ( num.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "dispenseNumber" );
			p.setContent( num );
			c.addChild( p );
		}
		if ( sig.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "sig" );
			p.setContent( sig );
			c.addChild( p );
		}
		if ( refills.length() > 0 ){
			XMLElement p = new XMLElement();
			p.setName( "refillCount" );
			p.setContent( refills );
			c.addChild( p );
		}
		if (( type != null ) && ( type.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "prescriptionType" );
			p.setContent( type );
			c.addChild( p );
		}
		
		r.addChild( c );
		return c;
	}
		
		
		

	public static void makeTest(){
		
		NCScript n = new NCScript();

		
		// Credentials
		XMLElement root = setHeader();
		n.setCredentials( root, "demo", "demo", "demo", "PAL/MED", "V5.0" );		
		n.setUserRole( root, "demo", "doctor");
		n.setDestination( root, "ws-rx-send");
		n.setAccount( root, "demo", "Cust Account Name", "demo", "address1", "address2", "city", "state", "zip", "zip4", "US", "5555551212", "5555551313");
		n.setLocation( root, "DEMOLOC1", "Family Practice Center", "address1", "address2", "city", "state", "zip", "zip4", "US", "5555551212", "5555551313", "contact");
		n.setPrescriber( root, "DEMOLP1", "Jones", "Doctor", "J", "Dr.", "Jr", "DEA20", "12345678", "tx", "12345678" );		
		XMLElement p = n.setPatient( root, "DEMOPT900800400", "Thomas", "Frank", null, null, null, "123456", "555443333", "Picks up meds at VA.", "address1", "address2", "city", "state", "zip", "zip4", "US", "phone", "19700115", "M" );

		n.addOutsideRx( root, "2001cd", "20070215", "Dr. Bob", "Crestor 5mg tab", "30", "1 daily", "0", "reconcile" );
		n.addOutsideRx( root, "78883", "20101205", "Dr. Rick", "Voltaren gel", "5", "Rub 4g into a.a. 2-4x/day", "3", null);
		n.addOutsideRxCoded( root, "78883", "20101205", "Dr. Rick", "160064", 30, 1, "reconcile", "Use as directed",
				0, 2, 1, 1, 1, true, true, "Take with food" );

		
		// print out XML file
		System.out.println( root.toString());
		
		return;
	}
	
	//public String toString(){
	//	return root.toString();
	//}
	
	
	
	// Clean up drug names (from old style data)
	private static String cleanDrugName( String drugName ){
		
		if ( drugName.indexOf( '*' ) >= 0 ){
			// truncate drug name at '*'
			int end = drugName.indexOf( '*' );
			drugName = drugName.substring( 0, end );
		}
		
		return drugName.replaceAll( "[^\\p{Alnum}]", " " ).trim();
	}
	
	
	private static String cleanString( String s ){
		return s.replaceAll( "[^\\p{Alnum}\\p{Space}]", "" );
	}

}

/**/
