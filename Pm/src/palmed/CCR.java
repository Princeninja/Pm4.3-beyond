package palmed;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import usrlib.Address;
import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.XMLParseException;

public class CCR {

	
	public XMLElement root = null;		// root element
	private XMLElement body = null;		// body element
	private String authorID = "";

	
	
	
	// Constructor 
	
	public CCR(){

		return;
	}
	

	
	
	
	
	/**********************************************************************************************************************
	 * 
	 */
	
	
	// Initializes the root element
	public void setRoot(){
		
		this.root = new XMLElement();
		root.setName( "ContinuityOfCareRecord" );
		root.setAttribute( "xsi:schemaLocation", "urn:astm-org:CCR CCRV1.xsd" );
		root.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
		root.setAttribute( "xmlns", "urn:astm-org:CCR" );
		//root.setAttribute( "xmlns:ccr", "urn:astm-org:CCR" );
		//"xmlns", "urn:astm-org:CCR"
		return;
	}
		

	
	
	
	
	// Add complete header
	public void addHeader( String docID, palmed.Language lang, String version, usrlib.Date date, usrlib.Time time, String patientID, String authorID ){
		
		this.authorID = authorID;
		
		setDocumentObjID( docID );
		setLanguage( lang );
		setVersion( "V1.0" );
		addExactDateTime( root, null, date, time );
		setPatient( patientID );
		addToFrom( "From", authorID, "Personal Physician" );
		addToFrom( "To", "RecipientID", "Interested Party" );
		addPurpose( "Transfer of Care" );
		return;
	}

	
		
	
	
	
	
	
	

	/**************************************************************************************************
	 * 
	 * Header
	 * 
	 */
	
	// Set document ID (unique across all CCRs
	public XMLElement setDocumentObjID( String id ){
		
		XMLElement c = new XMLElement();
		c.setName( "CCRDocumentObjectID" );
		c.setContent( id );
		root.addChild( c );
		return c;
	}
		
	// Set language (Hardcode to English for now)
	public XMLElement setLanguage( palmed.Language lang ){
		
		XMLElement c = new XMLElement();
		c.setName( "Language" );

		XMLElement p = new XMLElement();
		p.setName( "Text" );
		p.setContent( lang.getLabel());
		c.addChild( p );

		p = new XMLElement();
		p.setName( "Code" );
		c.addChild( p );

		XMLElement q = new XMLElement();
		q.setName( "Value" );
		q.setContent( "en" );					// TODO - HARDCODED TO ENGLISH
		p.addChild( q );
		
		q = new XMLElement();
		q.setName( "CodingSystem" );
		q.setContent( "ISO-639-1" );			// TODO - HARDCODED TO ENGLISH
		p.addChild( q );
		
		root.addChild( c );
		return c;
	}

	// Set CCR version
	public XMLElement setVersion( String id ){
		
		XMLElement c = new XMLElement();
		c.setName( "Version" );
		c.setContent( "V1.0" );					// TODO - HARDCODED VERSION V1.0
		root.addChild( c );
		return c;
	}
		

	// Set Patient
	public XMLElement setPatient( String patientID ){
		
		XMLElement c = new XMLElement();
		c.setName( "Patient" );

		XMLElement p = new XMLElement();
		p.setName( "ActorID" );
		p.setContent( patientID );
		c.addChild( p );

		root.addChild( c );
		return c;
	}

	
	// Add To/From Stanza
	public XMLElement addToFrom( String toFrom, String actorID, String role ){
		
		XMLElement c = new XMLElement();
		c.setName( toFrom );

		XMLElement p = new XMLElement();
		p.setName( "ActorLink" );
		c.addChild( p );

		XMLElement q = new XMLElement();
		q.setName( "ActorID" );
		q.setContent( actorID );
		p.addChild( q );

		q = new XMLElement();
		q.setName( "ActorRole" );
		p.addChild( q );
		
		XMLElement r = new XMLElement();
		r.setName( "Text" );
		r.setContent( role );			
		q.addChild( r );
		
		root.addChild( c );
		return c;
	}

	
	
	
	
	
	
	// Add Purpose Stanza
	public XMLElement addPurpose( String purpose ){
		
		XMLElement c = new XMLElement();
		c.setName( "Purpose" );

		XMLElement p = new XMLElement();
		p.setName( "Description" );
		c.addChild( p );

		XMLElement q = new XMLElement();
		q.setName( "Text" );
		q.setContent( purpose );
		p.addChild( q );

		root.addChild( c );
		return c;
	}

	
	
	
	
	
	
	
	
	
	/**************************************************************************************************
	 * 
	 * Footer
	 * 
	 */

		
	// Set Actors
	public XMLElement setActors( XMLElement parent ){
		
		XMLElement c = new XMLElement();
		c.setName( "Actors" );
		if ( parent != null ) parent.addChild( c );
		return c;
	}
	
		
	// Set Actor
	public XMLElement setActor( XMLElement parent, String objectID, String actorID ){

		XMLElement c = new XMLElement();
		c.setName( "Actor" );
		if ( parent != null ) parent.addChild( c );
		
		if (( objectID != null ) && ( objectID.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "ActorObjectID" );
			p.setContent( objectID );
			c.addChild( p );
		}
		
		if (( actorID != null ) && ( actorID.length() > 0 )){
			XMLElement p = new XMLElement();
			p.setName( "ActorID" );
			p.setContent( actorID );
			c.addChild( p );
		}
		
		return c;
	}
	

	
	
	
	
	// Set Person
	public XMLElement setPerson( XMLElement parent, Name name, palmed.Sex sex, Date bdate ){

		XMLElement c = new XMLElement();
		c.setName( "Person" );
		if ( parent != null ) parent.addChild( c );

		{
		XMLElement p = new XMLElement();
		p.setName( "Name" );
		c.addChild( p );
		
			XMLElement q = new XMLElement();
			q.setName( "BirthName" );
			p.addChild( q );
			
				XMLElement r = new XMLElement();
				r.setName( "Given" );
				r.setContent( name.getFirstName());
				q.addChild( r );
				
				r = new XMLElement();
				r.setName( "Given" );
				r.setContent( name.getMiddleName());
				q.addChild( r );
	
				r = new XMLElement();
				r.setName( "Family" );
				r.setContent( name.getLastName());
				q.addChild( r );

				String s = name.getSuffix();
				if ( s.trim().length() > 0 ){
					r = new XMLElement();
					r.setName( "Suffix" );
					r.setContent( s );
					q.addChild( r );
				}
		}
		
		if ( bdate != null ){
			XMLElement p = new XMLElement();
			p.setName( "DateOfBirth" );
			c.addChild( p );
			
			//XMLElement q = addDateTime( p, null, bdate, null );
			
			XMLElement q = new XMLElement();
			q.setName( "ExactDateTime" );
			q.setContent( bdate.getPrintable( 10 ) + "T00:00:00-06:00" );
			p.addChild( q );
		}
		
		if ( sex != null ){
			addCodedType( c, "Gender", sex.getLabel(), SNOMED.getSNOMED( sex ), "SNOMED" );
		}

		return c;
	}
	
	
	
	// Set Organization
	public XMLElement setOrganization( XMLElement parent, String name ){

		XMLElement c = new XMLElement();
		c.setName( "Organization" );
		if ( parent != null ) parent.addChild( c );
		
		{
			XMLElement p = new XMLElement();
			p.setName( "Name" );
			p.setContent( name );
			c.addChild( p );
		
		}
		
		return c;
	}
	
	
	
	// Set IDs
	public XMLElement setID( XMLElement parent, String id, String issuedBy, String role ){

		XMLElement p = new XMLElement();
		p.setName( "IDs" );
		if ( parent != null ) parent.addChild( p );
		
			XMLElement q = new XMLElement();
			q.setName( "ID" );
			q.setContent( id );
			p.addChild( q );
			
			q = new XMLElement();
			q.setName( "IssuedBy" );
			p.addChild( q );

			XMLElement r = new XMLElement();
			r.setName( "ActorID" );
			r.setContent( issuedBy );
			q.addChild( r );

			r = new XMLElement();
			r.setName( "ActorRole" );
			q.addChild( r );
			
				XMLElement s = new XMLElement();
				s.setName( "Text" );
				s.setContent( role );
				r.addChild(s );
			

			q = new XMLElement();
			q.setName( "Source" );
			p.addChild( q );
			

		return p;
	}
		
	

		
	// Set Address
	public XMLElement setAddress( XMLElement parent, Address address ){

		XMLElement p = new XMLElement();
		p.setName( "Address" );
		if ( parent != null ) parent.addChild( p );

		
			XMLElement q = new XMLElement();
			q.setName( "Line1" );
			q.setContent( address.getStreet());
			p.addChild( q );
			
			q = new XMLElement();
			q.setName( "City" );
			q.setContent( address.getCity());
			p.addChild( q );

			q = new XMLElement();
			q.setName( "State" );
			q.setContent( address.getState());
			p.addChild( q );

			q = new XMLElement();
			q.setName( "PostalCode" );
			q.setContent( address.getZip5());
			p.addChild( q );
	
		return p;
	}
		
	
	public XMLElement setTelephone( XMLElement parent, String phone ){

		XMLElement p = new XMLElement();
		p.setName( "Telephone" );
		if ( parent != null ) parent.addChild( p );

			XMLElement q = new XMLElement();
			q.setName( "Value" );
			q.setContent( phone );
			p.addChild( q );
			
		return p;
	}
					
					
			
	
	
	
	
	
	
	
	
	
	/**************************************************************************************************
	 * 
	 * BODY
	 * 
	 */
	
	// Set Body
	public XMLElement setBody(){
		
		XMLElement c = new XMLElement();
		c.setName( "Body" );
		this.body = c;
		this.root.addChild( c );
		return c;
	}
	
		

	
	/**************************************************************************************************
	 * 
	 * Problems
	 * 
	 */
	
	// Set Problems
	public XMLElement setProblems(){
		
		XMLElement c = new XMLElement();
		c.setName( "Problems" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set A Problem
	public XMLElement setProblem( XMLElement parent, Reca reca, usrlib.Date date, String desc, String code, String codeType, String probType, String status ){
		
		XMLElement c = new XMLElement();
		c.setName( "Problem" );
		parent.addChild( c );

		// add CCRDataObjectID
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "PROB" + reca.toString() );
		c.addChild( p );
		}
		
		// add date/time
		addDateTime( c, "Date", date, null );
			
		
		// add problem type
		{
		XMLElement p = new XMLElement();
		p.setName( "Type" );
		c.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( probType );
			p.addChild( q );
		}
			
		// add Description
		addCodedType( c, "Description", desc, code, codeType );

		
		
		// add status
		{
		XMLElement p = new XMLElement();
		p.setName( "Status" );
		c.addChild( p );
			
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( status );
			p.addChild( q );
		}
		
		
		// TODO add actor
		setSource( c, authorID );
		
		return c;
	}
	
	

	
	
	
	/**************************************************************************************************
	 * 
	 * Social History
	 * 
	 */
	
	// Set SocialHistory
	public XMLElement setSocialHistory(){
		
		XMLElement c = new XMLElement();
		c.setName( "SocialHistory" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set A SocialHistory
	public XMLElement setRace( XMLElement parent, Rec ptRec, palmed.Race race ){
		
		XMLElement c = new XMLElement();
		c.setName( "SocialHistoryElement" );
		parent.addChild( c );

		// add CCRDataObjectID
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "SHX" + ptRec.toString() );
		c.addChild( p );
		}
		
		// add Type
		{
		XMLElement p = new XMLElement();
		p.setName( "Type" );
		c.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( "Race" );
			p.addChild( q );
		}

		// add Description
		addCodedType( c, "Description", race.getLabel(), SNOMED.getSNOMED( race ), "SNOMED" );

		
		setSource( c, authorID);
		return c;
	}
	
	
	
	
	
	
	/**************************************************************************************************
	 * 
	 * Allergies
	 * 
	 */
	
	// Set Allergies
	public XMLElement setAllergies(){
		
		XMLElement c = new XMLElement();
		c.setName( "Alerts" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set An Allergy
	public XMLElement setAllergy( XMLElement parent, Reca reca, usrlib.Date date, String domainType, String domainCode, String domainSystem, String desc, String code, String codeType, String status, String severity ){
		
		XMLElement c = new XMLElement();
		c.setName( "Alert" );
		parent.addChild( c );

		// add CCRDataObjectID
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "PAR" + reca.toString() );
		c.addChild( p );
		}
		
		// add date/time
		addDateTime( c, "Date", date, null );
			
		// add Type
		addCodedType( c, "Type", domainType, domainCode, domainSystem );


		
		// add Description
		addCodedType( c, "Description", desc, code, codeType );
		

		// add status
		{
		XMLElement p = new XMLElement();
		p.setName( "Status" );
		c.addChild( p );
			
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( "Active" );
			p.addChild( q );
		}
		
		setSource( c, authorID );
		

		// TODO add Agent--Products--Product........
		
		
		// add Reaction
		{
		XMLElement p = new XMLElement();
		p.setName( "Reaction" );
		c.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Severity" );
			p.addChild( q );
			
				XMLElement r = new XMLElement();
				r.setName( "Text" );
				r.setContent( severity );
				q.addChild( r );
		}


		return c;
	}
	
	

	
	
	/**************************************************************************************************
	 * 
	 * Medications
	 * 
	 */
	
	// Set Allergies
	public XMLElement setMedications(){
		
		XMLElement c = new XMLElement();
		c.setName( "Medications" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set A Medication
	public XMLElement setMedication( XMLElement parent, Reca reca, usrlib.Date date, String medName, String medCode, String codeType, String status,
			String strength, String strengthUnits, String form, String dose, String doseUnits, String route, String freq ){
		
		XMLElement c = new XMLElement();
		c.setName( "Medication" );
		parent.addChild( c );

		// add CCRDataObjectID
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "MED" + reca.toString() );
		c.addChild( p );
		}
		
		// add date/time
		addDateTime( c, "Start Date", date, null );
			
		// add status
		{
		XMLElement p = new XMLElement();
		p.setName( "Status" );
		c.addChild( p );
			
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( "Active" );
			p.addChild( q );
		}
		
		
		// TODO add source
		// TODO add actor
		
		setSource( c, authorID );
		

		// add Med
		{
		XMLElement p = new XMLElement();
		p.setName( "Product" );
		c.addChild( p );
		
			// add Med identifier (name, code)
			addCodedType( p, "ProductName", medName, medCode, codeType );

			//TODO - brand name
			
			// add Med strength
			if (( strength != null ) && ( strength.length() > 0 )){

				XMLElement q = new XMLElement();
				q.setName( "Strength" );
				p.addChild( q );
				
					XMLElement r = new XMLElement();
					r.setName( "Value" );
					r.setContent( strength );
					q.addChild( r );
					

					if (( strengthUnits != null ) && ( strengthUnits.length() > 0 )){
						
						r = new XMLElement();
						r.setName( "Units" );
						q.addChild( r );
						
							XMLElement s = new XMLElement();
							s.setName( "Unit" );
							s.setContent( strengthUnits );
							r.addChild( s );
					}
			}
			
			// add Med form
			if (( form != null ) && ( form.length() > 0 )){
				
				XMLElement q = new XMLElement();
				q.setName( "Form" );
				p.addChild( q );
				
					XMLElement r = new XMLElement();
					r.setName( "Text" );
					r.setContent( form );
					q.addChild( r );
			}
			
			
					
		}

		//TODO - Quantity
		
		
		// add Directions
		{
		XMLElement p = new XMLElement();
		p.setName( "Directions" );
		c.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Direction" );
			p.addChild( q );

				if (( dose != null ) && ( dose.length() > 0 )){
					
					XMLElement r = new XMLElement();
					r.setName( "Dose" );
					q.addChild( r );
					
						XMLElement s = new XMLElement();
						s.setName( "Value" );
						s.setContent( dose );
						r.addChild( s );

						if (( doseUnits != null ) && ( doseUnits.length() > 0 )){
							
							s = new XMLElement();
							s.setName( "Units" );
							r.addChild( s );
	
								XMLElement t = new XMLElement();
								t.setName( "Unit" );
								t.setContent( doseUnits );
								s.addChild( t );
						}
				}
				
				// Route
				if (( route != null ) && ( route.length() > 0 )){
					
					XMLElement r = new XMLElement();
					r.setName( "Route" );
					q.addChild( r );
					
						XMLElement s = new XMLElement();
						s.setName( "Text" );
						s.setContent( route );
						r.addChild( s );
				}
				
				// Frequency
				if (( freq != null ) && ( freq.length() > 0 )){
					
					XMLElement r = new XMLElement();
					r.setName( "Frequency" );
					q.addChild( r );
					
						XMLElement s = new XMLElement();
						s.setName( "Value" );
						s.setContent( freq );
						r.addChild( s );
				}
		}

		//TODO - PatientInstructions
		//TODO - FulfillmentInstructions
		//TODO - Refills
		//TODO - SeriesNumber
		//TODO - Consent
		//TODO - Reaction
		//TODO - FulfillmentHistory
		
		//addSource( c, authorID );
		return c;
	}
	
	

	
	
					
		
	/**************************************************************************************************
	 * 
	 * Lab Results
	 * 
	 */
	
	// Set Results
	public XMLElement setLabResults(){
		
		XMLElement c = new XMLElement();
		c.setName( "Results" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set A Lab Result
	public XMLElement setLabResult( XMLElement parent, Reca labReca, usrlib.Date date, usrlib.Time time, String labType, String desc,
			String code, String codeType, String status, String result, String units, String range  ){
		
		XMLElement c = new XMLElement();
		c.setName( "Result" );
		parent.addChild( c );

		// add object id
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "LAB" + labReca.toString());
		c.addChild( p );
		}
		
		// add date/time
		addDateTime( c, null, date, null );
		
		// add Type
		{
			XMLElement p = new XMLElement();
			p.setName( "Type" );
			c.addChild( p );
				
				XMLElement q = new XMLElement();
				q.setName( "Text" );
				q.setContent( labType );	// Chemistry, Hematology, etc.
				p.addChild( q );
		}
		
			
		// add lab test

		addCodedType( c, "Description", desc, code, codeType );
		
		
		
		// TODO add source
		setSource( c, authorID );
			

		// add Test
		{
		XMLElement p = new XMLElement();
		p.setName( "Test" );
		c.addChild( p );
			{
			XMLElement q = new XMLElement();
			q.setName( "CCRDataObjectID" );
			p.addChild( q );
			}

			addCodedType( p, "Description", desc, code, codeType );
			
			
			// add Result Status
			if (( status != null ) && ( status.length() > 0 )){

				XMLElement q = new XMLElement();
				q.setName( "Status" );
				p.addChild( q );
				
					XMLElement r = new XMLElement();
					r.setName( "Text" );
					r.setContent( status );
					q.addChild( r );
			}
			{		
			XMLElement q = new XMLElement();
			q.setName( "Source" );
			p.addChild( q );
			}
			{
			XMLElement q = new XMLElement();
			q.setName( "TestResult" );
			p.addChild( q );

				XMLElement r = new XMLElement();
				r.setName( "Value" );
				r.setContent( result );
				q.addChild( r );
				
				r = new XMLElement();
				r.setName( "Units" );
				q.addChild( r );
				
					XMLElement s = new XMLElement();
					s.setName( "Unit" );
					s.setContent( units );
					r.addChild( s );
			}


			
		// add normal range
		if (( range != null ) && ( range.length() > 0 )){
			
			XMLElement q = new XMLElement();
			q.setName( "NormalResult" );
			p.addChild( q );
			
				XMLElement r = new XMLElement();
				r.setName( "Normal" );
				q.addChild( r );
				
				XMLElement s = new XMLElement();
				s.setName( "Value" );
				s.setContent( range );
				r.addChild( s );

				s = new XMLElement();
				s.setName( "Units" );
				r.addChild( s );

					XMLElement t = new XMLElement();
					t.setName( "Unit" );
					t.setContent( units );
					s.addChild( t );
	
				s = new XMLElement();
				s.setName( "Source" );
				r.addChild( s );
			}
		}
			
		return c;
	}
	
	

	
	
	
	
	
	/**************************************************************************************************
	 * 
	 * Procedures
	 * 
	 */
	
	// Set Problems
	public XMLElement setProcedures(){
		
		XMLElement c = new XMLElement();
		c.setName( "Procedures" );
		body.addChild( c );
		
		return c;
	}
	
		

	// Set A Problem
	public XMLElement setProcedure( XMLElement parent, Reca reca, usrlib.Date date, String desc, String code, String codeType, String procType, String status ){
		
		XMLElement c = new XMLElement();
		c.setName( "Procedure" );
		parent.addChild( c );

		// add CCRDataObjectID
		{
		XMLElement p = new XMLElement();
		p.setName( "CCRDataObjectID" );
		p.setContent( "PROC" + reca.toString() );
		c.addChild( p );
		}
		
		// add date/time
		addDateTime( c, "Date", date, null );
			
		
		// add problem type
		{
		XMLElement p = new XMLElement();
		p.setName( "Type" );
		c.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( procType );
			p.addChild( q );
		}
			
		// add Description
		addCodedType( c, "Description", desc, code, codeType );

		
		
		// add status
		{
		XMLElement p = new XMLElement();
		p.setName( "Status" );
		c.addChild( p );
			
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( status );
			p.addChild( q );
		}
		
		
		// add source
		setSource( c, authorID );
		
		// TODO location
		// TODO substance
		// TODO method
		// TODO position
		// TODO site
		
		return c;
	}
	
	

	
	

	
	
	
	
	
	
	
	
	
	
	
					
	public XMLElement addCodedType( XMLElement parent, String elementName, String desc, String code, String codingSystem ){
		
		// add Type

		XMLElement p = new XMLElement();
		p.setName( elementName );
		if ( parent != null ) parent.addChild( p );
		
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( desc );
			p.addChild( q );
			
			// add code
			if ( code != null ){
				
				q = new XMLElement();
				q.setName( "Code" );
				p.addChild( q );
				
					XMLElement r = new XMLElement();
					r.setName( "Value" );
					r.setContent( code );
					q.addChild( r );
					
					r = new XMLElement();
					r.setName( "CodingSystem" );
					r.setContent( codingSystem );
					q.addChild( r );
			}
			
		return p;
	}
		
	
	
		

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
//***********************************************************************************************************************************************
		

		
		
	public static CCR read( String str ){
		
		CCR ccr = new CCR();
		
		XMLElement xml = new XMLElement();
/*		FileReader reader;
		
		try {
			reader = new FileReader( Pm.getOvdPath() + fn_config );
		} catch (FileNotFoundException e) {
			System.out.println( "file not found:" + fn_config );
			return false;
		}
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		xml.parseString( str );
		
		
		XMLElement e;
		
		
		System.out.println( "root element=" + xml.getName());
		System.out.println( "num children=" + xml.countChildren());

		
		Enumeration<XMLElement> children = xml.enumerateChildren();
		
		while ( children.hasMoreElements()){
			XMLElement e2 = (XMLElement) children.nextElement();
			
			//System.out.println( e2.getName());

			if ( e2.getName().equalsIgnoreCase( "CCRDocumentObjectID" )){

				; // ignore
				
			} else if ( e2.getName().equalsIgnoreCase( "Language" )){
				
				; // ignore
				
			} else if ( e2.getName().equalsIgnoreCase( "Version" )){
				
				; // ignore
				
			} else if ( e2.getName().equalsIgnoreCase( "DateTime" )){
				
				processDateTime( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Patient" )){
				
				processPatient( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Actors" )){
				
				; // ignore
				
			} else if ( e2.getName().equalsIgnoreCase( "Body" )){
				
				processBody( e2 );
			}
			
			
		}
		
		
		
		return ccr;
	}
		

	
	
	
	
	static void processDateTime( XMLElement e ){

		Date date = null;
		String strDate = null;
		String strType = null;
		
		System.out.println( "processDateTime" );
	
		// get ExactDateTime
		{
		XMLElement e1 = e.getChildByName( "ExactDateTime" );
		if ( e1 != null ){
			String str = e1.getContent();
			XMLElement e2 = e1.getChildByName( "Text" );
			if ( e2 != null ) str = e2.getContent();
			if ( str == null ) System.out.println( "CCR ERROR: null date info" );
			if ( str != null ){
				strDate = str;
				System.out.println( str );
			}
		}
		}
		
		// get ApproximateDateTime
		{
		XMLElement e1 = e.getChildByName( "ApproximateDateTime" );
		if ( e1 != null ){
			String str = e1.getContent();
			XMLElement e2 = e1.getChildByName( "Text" );
			if ( e2 != null ) str = e2.getContent();
			if ( str == null ) System.out.println( "CCR ERROR: null date info" );
			if ( str != null ){
				strDate = str;
				System.out.println( str );
			}
		}
		}

		// get Type of DateTime
		{
		XMLElement e1 = e.getChildByName("Type");
		if ( e1 != null ){
			String str = e1.getContent();
			XMLElement e2 = e1.getChildByName("Text");
			if ( e2 != null ) str = e2.getContent();
			if ( str == null ) System.out.println( "CCR ERROR: null date type info" );
			if ( str != null ){
				strType = str;
				System.out.println( str );
			}
		}
		}
		
		return;
	}

	
	
	
	static void processCode( XMLElement e ){
			
		XMLElement e2;
		
		// get problem code value
		e2 = e.getChildByName( "Value" );
		if ( e2 != null ) System.out.println( "CodeValue: " + e2.getContent());
		
		e2 = e.getChildByName( "CodingSystem" );
		if ( e2 != null ) System.out.println( "CodeType: " + e2.getContent());
		return;
	}

	
	
	
				
	static void processPatient( XMLElement e ){
		
		System.out.println( "processPatient" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processBody( XMLElement e ){
		
		System.out.println( "processBody" );
		
		Enumeration<XMLElement> children = e.enumerateChildren();
		
		while ( children.hasMoreElements()){
			
			XMLElement e2 = (XMLElement) children.nextElement();
			
			System.out.println( e2.getName());

			if ( e2.getName().equalsIgnoreCase( "FunctionalStatus" )){

				processFunctionalStatus( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Problems" )){
				
				processProblems( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "SocialHistory" )){
				
				processSocialHistory( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Alerts" )){
				
				processAlerts( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Medications" )){
				
				processMedications( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Immunizations" )){
				
				processImmunizations( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "VitalSigns" )){
				
				processVitalSigns( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Results" )){
				
				processResults( e2 );
				
			} else if ( e2.getName().equalsIgnoreCase( "Procedures" )){
				
				processProcedures( e2 );
			}
			
		}

		return;
	}
		
		
		
	static void processFunctionalStatus( XMLElement e ){
		
		System.out.println( "processFunctionalStatus" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}
	
	
	
	//**********************************************************************************************
	
	static void processProblems( XMLElement e ){
		
		System.out.println( "processProblems" );

		// loop for each of the Problem entities
		Enumeration<XMLElement> children = e.enumerateChildren();
		
		while ( children.hasMoreElements()){
			
			XMLElement e1 = (XMLElement) children.nextElement();			
			if (( e1 != null ) && ( e1.getName().equals( "Problem" )))
				processProblem( e1 );
		}

		return;
	}


	static void processProblem( XMLElement e ){
		
		XMLElement e1;
		
		System.out.println( "processProblem" );
		
		// CCRDataObjectID - ignore
		
		
		// get problem Date
		e1 = e.getChildByName( "DateTime" );
		if ( e1 != null ) processDateTime( e1 );
		
		// Type - ignore
		
		// get problem description
		e1 = e.getChildByName( "Description" );
		if ( e1 != null ) processProblemDescription( e1 );
		

		// Status
		e1 = e.getChildByName( "Status" );
		if ( e1 != null ) e1 = e1.getChildByName( "Text" );
		if ( e1 != null ) System.out.println( "Status: " + e1.getContent());
		
		// Source - ignore

		return;
	}
	
	static void processProblemDescription( XMLElement e ){
		
		XMLElement e1;
		

		// I'm doing this part in a loop because there may be multiple <Code> entities
		// for a given problem.
		
		Enumeration<XMLElement> children = e.enumerateChildren();
		
		while ( children.hasMoreElements()){
			
			e1 = (XMLElement) children.nextElement();
			
			System.out.println( e1.getName());

			if ( e1.getName().equalsIgnoreCase( "Text" )){

				// get problem description text
				System.out.println( "Prob: " + e1.getContent());
				
			} else if ( e1.getName().equalsIgnoreCase( "Code" )){
				// process code entity
				processCode( e1 );
			}
		}
		
		return;
	}

	static void processSocialHistory( XMLElement e ){
		
		System.out.println( "processSocialHistory" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processAlerts( XMLElement e ){
		
		System.out.println( "processAlerts" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processMedications( XMLElement e ){
		
		System.out.println( "processMedications" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processImmunizations( XMLElement e ){
		
		System.out.println( "processImmunizations" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processVitalSigns( XMLElement e ){
		
		System.out.println( "processVitalSigns" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processResults( XMLElement e ){
		
		System.out.println( "processResults" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}

	static void processProcedures( XMLElement e ){
		
		System.out.println( "processProcedures" );
		
		XMLElement e1 = e.getChildByName( "ActorID" );
		if ( e1 != null ) System.out.println( e1.getContent());
		
		//XMLElement e2 = e.getChildByName("Type");
		//if ( e2!= null ) e2 = e2.getChildByName("Text");
		//if ( e2 != null ) System.out.println( e2.getContent());
		
	}


	
			
			
		
		
		
		

		
		
	
	
//*********************************************************************************************************	
	
	
	
	// Add date and time
	public XMLElement addDateTime( XMLElement parent, String type, usrlib.Date date, usrlib.Time time ){
		
		String timezone = "0600"; // TODO - HARDCODED TIMEZONE OFFSET
		String datetime = "";
		if (( date != null ) && date.isValid()) datetime = date.getPrintable( 9 /*10*/ );
		if (( time != null ) && time.isValid()) datetime = datetime + "T" + time.getPrintable( 1 ) + "-" + timezone;

		
		
		XMLElement c = new XMLElement();
		c.setName( "DateTime" );
		parent.addChild( c );

			if ( type != null ){
				
				XMLElement p = new XMLElement();
				p.setName( "Type" );
				c.addChild( p );
		
					// add text
					XMLElement q = new XMLElement();
					q.setName( "Text" );
					q.setContent( type );
					p.addChild( q );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "ApproximateDateTime" );
			c.addChild( p );
	
			// add text
			XMLElement q = new XMLElement();
			q.setName( "Text" );
			q.setContent( datetime );
			p.addChild( q );
			}

		return c;
	}

	// Add date and time
	public XMLElement addExactDateTime( XMLElement parent, String type, usrlib.Date date, usrlib.Time time ){
		
		String timezone = "06:00"; // TODO - HARDCODED TIMEZONE OFFSET
		String datetime = "";
		if (( date != null ) && date.isValid()) datetime = date.getPrintable( 10 );
		if (( time != null ) && time.isValid()) datetime = datetime + "T" + time.getPrintable( 2 ) + "-" + timezone;

		
		
		XMLElement c = new XMLElement();
		c.setName( "DateTime" );
		parent.addChild( c );

			if ( type != null ){
				
				XMLElement p = new XMLElement();
				p.setName( "Type" );
				c.addChild( p );
		
					// add text
					XMLElement q = new XMLElement();
					q.setName( "Text" );
					q.setContent( type );
					p.addChild( q );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "ExactDateTime" );
			p.setContent( datetime );
			c.addChild( p );
	
			}

		return c;
	}


		
	// Add source
	public XMLElement setSource( XMLElement parent ){ return setSource( parent, null, null ); }

	public XMLElement setSource( XMLElement parent, String name ){ return setSource( parent, name, null ); }

	public XMLElement setSource( XMLElement parent, String name, String role ){
		
		XMLElement c = new XMLElement();
		c.setName( "Source" );
		if ( parent != null ) parent.addChild( c );
		
			if ( name != null ){
				XMLElement p = new XMLElement();
				p.setName( "Actor" );
				c.addChild( p );
		
				XMLElement q = new XMLElement();
				q.setName( "ActorID" );
				q.setContent( name );
				p.addChild( q );
	
				if ( role != null ){
					q = new XMLElement();
					q.setName( "ActorRole" );
					p.addChild( q );
	
						XMLElement r = new XMLElement();
						r.setName( "Text" );
						r.setContent( role );
						q.addChild( r );
				}
			}
		return c;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void makeTest(){
		
		CCR ccr = new CCR();

		
		// establish root
		ccr.setRoot();
		ccr.addHeader( "4948MKP", palmed.Language.ENGLISH, "1.0", usrlib.Date.today(), usrlib.Time.now(), "Palen, Madison K", "AuthorID_01" );
		ccr.setBody();
		
		XMLElement p = ccr.setSocialHistory();
		ccr.setRace( p, new Rec(0), palmed.Race.CAUCASIAN );
		
		p = ccr.setProblems();
		ccr.setProblem( p, new Reca(), usrlib.Date.today(), "DM-2", "234234234", "SNOMED", "", "Active" );
		ccr.setProblem( p, new Reca(), usrlib.Date.today(), "HTN", "45646456", "SNOMED", "Chronic", "Active" );
		ccr.setProblem( p, new Reca(), usrlib.Date.today(), "COPD", "789789789", "SNOMED", "", "Active" );

		p = ccr.setAllergies();
		ccr.setAllergy( p, new Reca(), usrlib.Date.today(), "Drug Allergy", "416098002", "SNOMED CT", "Amoxicillin", "23232323", "RxNorm", "Active", "Severe" );
		
		p = ccr.setMedications();
		ccr.setMedication( p, new Reca(), usrlib.Date.today(), "metorpolol", "234234", "RxNorm", "Active", "25", "mg", "tablet", "1", "tablet", "Oral", "BID" );
		ccr.setMedication( p, new Reca(), usrlib.Date.today(), "lipitor", "324324", "RxNorm", "Active", "40", "mg", "tablet", "1", "tablet", "Oral", "QHS" );
		ccr.setMedication( p, new Reca(), usrlib.Date.today(), "norvasc", "567567", "RxNorm", "Active", "5", "mg", "tablet", "1", "tablet", "Oral", "daily" );


		//ccr.addFooter( "234234", new Name( "palen", "Madison", "K", "" ), palmed.Sex.FEMALE, new Date( "10-16-1990" ), new Address(), "RFP" );

		
		// print out XML file
		System.out.println( ccr.toString());
		
		return;
	}
	
	public String toString(){
		return root.toString();
	}

}
