package palmed;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Enumeration;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.text.BasicTextEncryptor;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

import usrlib.Address;
import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;

public class PtCCR {

	
	public static String create( Rec ptRec ){
		return create( ptRec, new Rec( 2 ));
	}
	
	public static String create( Rec ptRec, Rec provRec ){
		
	
		if ( ! Rec.isValid( ptRec )) Clients.alert( "Bad ptRec" );
	
		// Set up PATIENT info
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );		
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		System.out.println( dirPt.getName().getPrintableNameLFM());
		
		String patientID = String.format( "PT%06d", ptRec.getRec());
		String authorID = "AuthorID_01";
		
		CCR ccr = new CCR();

		
		// establish root
		ccr.setRoot();
		
		// make unique CCR file ID
		String ccrID = String.format( "%06d-%s", ptRec.getRec(), usrlib.Date.today().getPrintable( 8 ) + usrlib.Time.now().getPrintable( 1 ));
		
		ccr.addHeader( ccrID, palmed.Language.ENGLISH, "1.0", usrlib.Date.today(), usrlib.Time.now(), patientID, authorID );
		ccr.setBody();
		
		// Element to be top of various main sections: Problems, Medications, Alerts, etc.
		XMLElement p = null;
		
		
		
		
		
		// Current problem list
		boolean flgProblems = false;
		
		for ( Reca reca = medPt.getProbReca(); reca.getRec() != 0; ){
			
			Prob prob = new Prob( reca );
			
			// for now just display current problems
			int display = 1;
			
			// get problem status byte
			int status = prob.getStatus();
			// if no status byte - look at stop date & set status (to support legacy code)
			if (( status == 0 ) && prob.getStopDate().isValid()) status = 'P';
			// is this type selected?
			if ((( status == 'C' ) && (( display & 1 ) != 0 ))
				|| (( status == 'P') && (( display & 2 ) != 0 ))){
				// ( status == 'R' ) - removed
				// ( status == 'E' ) - superceded by edit
				
				

				//String s = prob.getStopDate().getPrintable(9);
				//if ( s.equals( "00-00-0000" )) s = "";
				
				// set problem status
				//int stat = prob.getStatus();
				//if ( stat == 'C' ) s = "Active";
				//if ( stat == 'P' ) s = "Resolved";
				//if ( stat == 'R' ) s = "Removed";
				String desc = "";
				String code = "";
				String codeType = "";
				String probStatus = "";
				
				
				Rec pRec = prob.getProbTblRec();
				if ( Rec.isValid( pRec )){
					// read entry from ProbTbl
					desc = prob.getProbDesc();
					
					ProbTbl pTbl = new ProbTbl( pRec );					
					code = pTbl.getSNOMED();
					codeType = "SNOMED";
					
				} else {
					// misc entry
					desc = prob.getProbDesc();
					code = "";
					codeType = "";
					//new Listcell( /*Dgn.read( prob.getDgnRec()).getCode()*/ "V101.5" ).setParent( i );;
				}
				
				probStatus = ( prob.getStatus() == 'C'  ) ? "Active": "Inactive";
				
				// Note - problem type must be one of: Problem, Condition, Diagnosis, Symptom, Finding, Complaint, Functional Limitation
				//probType = ( prob.getType() == 'A' ) ? "Acute": "Chronic";
				String probType = "Problem";
				
				if ( ! flgProblems ){
					p = ccr.setProblems();
					flgProblems = true;
				}
				
				ccr.setProblem( p, reca, prob.getStartDate(), desc, code, codeType, probType, probStatus );
			}
			
			// get next reca in list	
			reca = prob.getLLHdr().getLast();			
		}


		
		
		// add social history
		p = ccr.setSocialHistory();
		ccr.setRace( p, ptRec, dirPt.getRace());
		

		

		
		
		// Allergies
		boolean flgAllergies = false;

		for ( Reca reca = medPt.getParsReca(); reca.getRec() != 0; ){
			
			Par par = new Par( reca );

			if ( par.getStatus() == Par.Status.CURRENT ){	
				
				String code = "";
				String codeType = "";
				
				Par.Domain domain = par.getDomain();
				if (( domain == null ) || ( domain == Par.Domain.UNSPECIFIED )) domain = Par.Domain.DRUG;
				
				int parCode = par.getCode();
				if ( parCode != 0 ){
					NCAllergies nc = NCAllergies.readMedID( String.valueOf( parCode ));
					if ( nc != null ){
						code = nc.getConceptID();
						codeType = "FDB";		// RxNorm??
					}
				}
				
				if ( ! flgAllergies ){
					p = ccr.setAllergies();
					flgAllergies = true;
				}
				
				ccr.setAllergy( p, reca, par.getDate(), domain.getLabel(), domain.getSNOMED(), "SNOMED CT",
						par.getParDesc(), code, codeType, "Active", par.getSeverity().getLabel());
			}
			
			// get next reca in list	
			reca = par.getLLHdr().getLast();
		}

		
		
		
		
		
		
		
		
		
		// Medications
		boolean flgMedications = false;
		
		for ( Reca reca = medPt.getMedsReca(); reca.getRec() != 0; ){
			
			Cmed cmed = new Cmed( reca );
			// current med?????
			if (( cmed.getStatus() == Cmed.Status.CURRENT )
				&& ( ! cmed.getStopDate().isValid())){

				if ( ! cmed.getFlgMisc()){
					
					NCFull nc = NCFull.readMedID( cmed.getDrugCode());
					
					
					if ( ! flgMedications ){
						p = ccr.setMedications();
						flgMedications = true;
					}
					
					ccr.setMedication( p, reca, cmed.getStartDate(), cmed.getDrugName(), cmed.getDrugCode(), "FDDC" /*RxNorm?*/, "Active",
							nc.getMedStrength(), nc.getMedStrengthUnits(), MedForm.getDesc( cmed.getForm()), 
							MedDosage.getDesc( cmed.getDosage()),  MedForm.getDesc( cmed.getForm()), MedRoute.getDesc(cmed.getRoute()), 
							MedFreq.getDesc(cmed.getSched()) );
					
				} else {
					
					if ( ! flgMedications ){
						p = ccr.setMedications();
						flgMedications = true;
					}
					
					ccr.setMedication( p, reca, cmed.getStartDate(), cmed.getDrugName(), cmed.getDrugCode(), "FDDC" /*RxNorm?*/, "Active", 
							"", "m", MedForm.getDesc( cmed.getForm()),
							MedDosage.getDesc( cmed.getDosage()), "", MedRoute.getDesc(cmed.getRoute()),
							MedFreq.getDesc(cmed.getSched()) );
				}
			}
						
			// get next reca in list	
			reca = cmed.getLLHdr().getLast();
		}

		
		
		
		// Lab Results
		boolean flgResults = false;
		
		for ( Reca reca = medPt.getLabResultReca(); reca.getRec() != 0; ){
			
			LabResult lab = new LabResult( reca );

			if ( lab.getStatus() == LabResult.Status.ACTIVE ){

					LabObsTbl obs = new LabObsTbl( lab.getLabObsRec());
					String units = obs.getCodedUnits().getLabel();
					if (( units == null ) || ( units.length() < 1 )) units = obs.getUnitsText();
					
					if ( ! flgResults ){
						p = ccr.setLabResults();
						flgResults = true;
					}
					
					ccr.setLabResult( p, reca, lab.getDate(), lab.getTime(), obs.getTestType().getLabel(), obs.getDesc(),
							obs.getLOINC(), "LOINC", lab.getResultStatus().getLabel(), lab.getResult(), units,
							obs.getNormalRangeText());
					
			}
						
			// get next reca in list	
			reca = lab.getLLHdr().getLast();
		}


		
		
		
		
		// Current problem list
		boolean flgProcedures = false;
		
		Procedure proc = null;
		
		for ( Reca reca = medPt.getProceduresReca(); reca.getRec() != 0; reca = proc.getLLHdr().getLast()){
			
			proc = new Procedure( reca );
			
			if ( proc.getStatus() != Procedure.Status.CURRENT ) continue;
				

			String desc = "";
			String code = "";
			String codeType = "";
			String procType = "";
			String procStatus = "Active";
			
			
			Rec pRec = proc.getProbTblRec();
			
			if ( Rec.isValid( pRec )){
				
				ProbTbl ptbl = new ProbTbl( pRec );
				desc = ptbl.getDesc();			
				
				// CCR wants ICD9-CM or CPT-4 codes - instead of SNOMED - for Procedures
				code = ptbl.getICD9();
				codeType = "ICD9-CM";
				
				if ( code.length() < 1 ){
					// try CPT-4
					code = ptbl.getCode4();
					codeType = "CPT-4";
				}
				
				if ( code.length() < 1 ){
					//code = ptbl.getSNOMED();
					//codeType = "SNOMED-CT";
				}
				
			} else {
				desc = proc.getDesc();
				code = null;
				codeType = null;
			}
			
			//procStatus = ( proc.getStatus() == 'C'  ) ? "Active": "Inactive";
			
			// Note - problem type must be one of: Problem, Condition, Diagnosis, Symptom, Finding, Complaint, Functional Limitation
			procType = ( proc.getType() != null ) ? proc.getType().getLabel(): "Other";
			
			if ( ! flgProcedures ){
				p = ccr.setProcedures();
				flgProcedures = true;
			}
			ccr.setProcedure( p, reca, proc.getDate(), desc, code, codeType, procType, procStatus );
		}



		
		
		
		// add footer
		XMLElement actors = ccr.setActors( ccr.root );
		
		// add recipient
		{
		XMLElement actor = ccr.setActor( actors, "RecipientID", null );
		ccr.setOrganization( actor, "Any Interested Person/Organization" );
		ccr.setSource( actor );
		}
		
		// add patient
		{
		XMLElement actor = ccr.setActor( actors, patientID, null );
		ccr.setPerson( actor, dirPt.getName(), dirPt.getSex(), dirPt.getBirthdate());
		ccr.setID( actor, patientID, authorID, "Medical Record Number Generator" );
		ccr.setAddress( actor, dirPt.getAddress());
		ccr.setTelephone( actor, dirPt.getAddress().getHome_ph());
		ccr.setSource( actor );
		}
		
		// add practice/clinic
		
		{
		Prov prov = new Prov( provRec );
		Name name = new Name( prov.getFirstName(), prov.getMiddleName(), prov.getLastName(), prov.getSuffix() );
		
		Address add = prov.getOfficeAddress();
		//Address add = new Address( "3129 Blattner Dr", "", "Cape Girardeau", "MO", "63703", "573-335-0166", "573-335-7942", "" );
		
		XMLElement actor = ccr.setActor( actors, authorID, null );		
		XMLElement q  = ccr.setPerson( actor, name, null, null );		
		ccr.setAddress( actor, add );		
		ccr.setTelephone( actor, add.getHome_ph());
		ccr.setSource( actor );
		}
		
		
		// print out XML file
		System.out.println( ccr.toString());
		


		return ccr.toString();
	}
	
	
	
	
	public static boolean exportCCR( Rec ptRec ){
		
		boolean flgEncrypt = false;
		boolean flgPatient = false;
	
		// encrypted or plain text?
		int enc = DialogHelpers.Optionbox( "Export CCR", "Would you like this file encrypted or saved/sent as plain text?", "Encrypted", "Plain", "Cancel" );
		if (( enc != 1 ) && ( enc != 2 )) return false;
		flgEncrypt = ( enc == 1 ) ? true: false;
		
		// who is receiving this CCR output
		int who = DialogHelpers.Optionbox( "Export CCR", "Is this CCR being provided to the patient or to another healthcare provider?", "Patient", "Other Provider", "Cancel" );
		if ( who == 3 ) return false;
		flgPatient = ( who == 1 ) ? true: false;
		
		return exportCCR( ptRec, flgEncrypt, flgPatient );
	}
			

	
	public static boolean exportCCRtoPatient( Rec ptRec ){
		
		boolean flgEncrypt = false;
	
		// encrypted or plain text?
		int enc = DialogHelpers.Optionbox( "Export CCR", "Would you like this file encrypted or saved/sent as plain text?", "Encrypted", "Plain", "Cancel" );
		if (( enc != 1 ) && ( enc != 2 )) return false;
		flgEncrypt = ( enc == 1 ) ? true: false;

		return exportCCR( ptRec, flgEncrypt, true );
	}
			

	
	
	
	public static boolean exportCCRtoProvider( Rec ptRec ){
		
		boolean flgEncrypt = false;
	
		// encrypted or plain text?
		int enc = DialogHelpers.Optionbox( "Export CCR", "Would you like this file encrypted or saved/sent as plain text?", "Encrypted", "Plain", "Cancel" );
		if (( enc != 1 ) && ( enc != 2 )) return false;
		flgEncrypt = ( enc == 1 ) ? true: false;

		return exportCCR( ptRec, flgEncrypt, false );
	}
			

	
	
	
	public static boolean exportCCR( Rec ptRec, boolean flgEncrypt, boolean flgPatient ){
		
		String contentType = "text/xml";
		String outText = null;
		int function = 0;			// 1-save, 2-email
		
		
		
		// do we save/download or email file?
		function = DialogHelpers.Optionbox( "Export CCR", "Would you like to save this file, or send as an email?", "Save", "E-mail", "Cancel" );
		if (( function != 1 ) && ( function != 2 )) return false;

		

		// build CCR output
		String ccrText = PtCCR.create( ptRec );
		outText = ccrText;

		// compute SHA-1 digest and write to output file
		StandardStringDigester digester = new StandardStringDigester();
		digester.setAlgorithm( "SHA-1" );
		digester.setSaltSizeBytes( 0 );				
		digester.setIterations( 1 );
		digester.setStringOutputType( "base64" );
		//System.out.println( "Iterations=" + digester..getIterations());
		String digest = digester.digest( ccrText );
		
		
		int keyNum = 1;
		
		if ( flgEncrypt ){
			
			DirPt dirPt = new DirPt( ptRec );
			String password = dirPt.getSSN();
			if ( password.length() < 6 ){
				password = dirPt.getBirthdate().getPrintable(9);
				keyNum = 2;
				if ( password.length() < 6 ){
					// get another key ??
					DialogHelpers.Messagebox( "Patient has no SSN and no birthdate to use as a document password." );
					return false;
				}
			}
	
			DialogHelpers.Messagebox( "File encrypted using patient's " + ((keyNum == 1) ? "Social Security Number": "birthdate" ) + " as the password." );
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword( password );
			String encryptedText = textEncryptor.encrypt( ccrText );
			
			outText = encryptedText;
			contentType = "text/plain";
		}
		
		
		// save to file or email?
		
		switch ( function ){
		
		case 1:	// save as file
			DialogHelpers.Messagebox( "SHA-1 Message Digest: " + digest );
			
	        Filedownload.save( outText, contentType, "ptccr.xml");
			Intervention.postNew( ptRec, Date.today(), ( flgPatient ) ? Intervention.Types.PT_ECOPY_PROVIDED: Intervention.Types.PT_TRANSFER_OUT, "CCR" );
			break;
			
		case 2:	// send as email
			
			String fname = "ptccr.xml";
			String fname1 = "ptccr.sha1";
			String addr = "";
			
			try {
				// write ccr to server file
				BufferedWriter out = new BufferedWriter( new FileWriter( fname ));
				out.write( outText );
				out.close();
				} catch ( Exception e) { 
					DialogHelpers.Messagebox( "Error writing CCR output file." );
					return false;
				}
				

				try {
					// write ccr to server file
					BufferedWriter out = new BufferedWriter( new FileWriter( fname1 ));
					out.write( digest );
					out.close();
				} catch ( Exception e) { 
						DialogHelpers.Messagebox( "Error writing SHA-1 output file." );
						return false;
				}

				
				// get email address to send to
				addr = DialogHelpers.Textbox( "Send CCR", "Enter the email address to send this CCR to." );
				if ( addr == null ) return false;
				
				System.out.println( "Sending email" );
				System.out.println( "address=" + addr );
				System.out.println( "fname=" + fname );

				
				// send email
				String addresses[] = new String[1];
				addresses[0] = addr;
				String fnames[] = new String[2];
				fnames[0] = fname;
				fnames[1] = fname1;
				
				String msgTxt = "Here is a patient CCR and verification SHA-1 message digest.";
				if ( flgEncrypt ) msgTxt = msgTxt + "  Decrypt file with patient's " + ((keyNum == 1) ? "Social Security Number": "birthdate" ) + " as a password.";
				
				if ( Email.send( addresses, "Patient CCR", msgTxt, fnames )){
					System.out.println( "back from sending email" );
					DialogHelpers.Messagebox( "Email sent." );
					Intervention.postNew( ptRec, Date.today(), ( flgPatient ) ? Intervention.Types.PT_ECOPY_PROVIDED: Intervention.Types.PT_TRANSFER_OUT, flgEncrypt ? "Encrypted CCR": "CCR" );
					
				} else {
					DialogHelpers.Messagebox( "Email failed." );
				}


		default:	// cancel
			return false;			
		}
		
		return true;
 	}
	
	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public XMLElement parse( String ccrString ){
		



		
	XMLElement root = new XMLElement();
	root.parseString( ccrString );
	//System.out.println( "new root2=" + root2.getName());
	
	XMLElement c;
	
	c = root.getElementByPathName( "DateTime/ExactDateTime" );
	if ( c != null ) System.out.println( "Date: " + c.getContent());
	
	c = root.getElementByPathName( "Patient/ActorID" );
	if ( c != null ) System.out.println( "Pt Name: " + c.getContent());

	XMLElement body = root.getElementByPathName( "Body" );
	if ( c == null ) System.out.println( "Warning - No body." );
	
	
	Enumeration<XMLElement> en = root.enumerateChildren();
	while (en.hasMoreElements()) {
		XMLElement z = (XMLElement) en.nextElement();
		if ( z == null ) break;
		
		if ( z.getName().equals( "Medications" )){
/*			
			String strErxID = "";
			String drugName = "";
			String drugID = "";
			int dosage = 0;
			int form = 0;
			int route = 0;
			int freq = 0;
			boolean prn = false;
			boolean daw = false;
			int num = 0;
			int refills = 0;
			Reca eRxID = null;
			Date rxDate = null;
			XMLElement y;
			
			
			
			y = z.getChildByName( "ExternalPrescriptionID" );
			if ( y != null ) strErxID = y.getContent();
			if ( strErxID.length() > 2 ){
				eRxID = Reca.fromString( strErxID );
			}
			
			y = z.getChildByName( "DrugInfo" );
			if ( y != null ) drugName = y.getContent();
			
			y = z.getChildByName( "DrugID" );
			if ( y != null ) drugID = y.getContent();
			
			//y = z.getChildByName( "DosageNumberDescription" );
			y = z.getChildByName( "DosageNumberTypeID" );
			if ( y != null ) dosage = EditHelpers.parseInt( y.getContent());
			
			//y = z.getChildByName( "DosageForm" );
			y = z.getChildByName( "DosageFormTypeId" );
			if ( y != null ) form = EditHelpers.parseInt(y.getContent());
			
			//y = z.getChildByName( "Route" );
			y = z.getChildByName( "DosageRouteTypeId" );
			if ( y != null ) route = EditHelpers.parseInt( y.getContent());
			
			//y = z.getChildByName( "DosageFrequencyDescription" );
			y = z.getChildByName( "DosageFrequencyTypeID" );
			if ( y != null ) freq = EditHelpers.parseInt( y.getContent());
			
			y = z.getChildByName( "Dispense" );
			if ( y != null ){
				String s = y.getContent();
				if (( s != null ) && ( s.length() > 0 ) && Character.isDigit( s.charAt(0)))
					num = EditHelpers.parseInt( s );
			}
			
			y = z.getChildByName( "Refills" );
			if ( y != null ){
				String s = y.getContent();
				if (( s != null ) && ( s.length() > 0 ) && Character.isDigit( s.charAt(0)))
					refills = EditHelpers.parseInt( s );
			}
			
			y = z.getChildByName( "TakeAsNeeded" );
			if ( y != null ) prn = y.getContent().toUpperCase().equals( "Y" );
			
			y = z.getChildByName( "DispenseAsWritten" );
			if ( y != null ) daw = y.getContent().toUpperCase().equals( "Y" );
			
			y = z.getChildByName( "PrescriptionDate" );
			if ( y != null ){
				String s = y.getContent().substring( 0, 10 );
				rxDate = new Date( s );
			}
			
			


			// get patient id - make sure the same

			
			
			
			
			

			// if there is an eRxID (ExternalPrescriptionID) - this stems from an existing med				
			if ( Reca.isValid( eRxID )){
			
				// read a current med entry
				Cmed cmed = new Cmed( eRxID );
				int flgChange = 0;
				
				
				// any changes?
				// at least update refilled date
				
				// is this a refill?
				// were there changes?
				
				// get date
				// get drug id - make sure the same
				
				// is drug ID the same
				if ( ! drugID.equals( cmed.getDrugCode())){
					// different drug ID
					System.out.println( "different drug ID!!!!!!!!!!!!!!");
					break;
				}
				
				
				
				
				
				// are there any changes in the SIG?
				if ( dosage != cmed.getDosage()){
					cmed.setDosage( dosage );
					flgChange = 2;		// likely major
				}
				
				if ( form != cmed.getForm()){
					cmed.setForm( form );
					flgChange = 1;		// minor
				}
				
				if ( route != cmed.getRoute()){
					cmed.setRoute( route );
					flgChange = 1;		// minor
				}
				
				if ( freq != cmed.getSched()){
					cmed.setSched( freq );
					flgChange = 2;		// likely major	
				}
					

				
				// major changes: DC old med, create new med entry
				if ( flgChange == 2 ){


					Cmed newCmed = new Cmed();
					newCmed.setStartDate(( rxDate == null ) ? usrlib.Date.today(): rxDate );
					newCmed.setDrugName( drugName );
					newCmed.setDrugCode( drugID );
					newCmed.setPtRec( ptRec );
					
					newCmed.setDosage( dosage );
					newCmed.setRoute( route );
					newCmed.setForm( form );
					newCmed.setSched( freq );

					newCmed.setNumber( num );
					newCmed.setRefills( refills );
					newCmed.setPRN( prn );

					newCmed.setChecked( (byte) 2 );
					newCmed.setStatus( Cmed.Status.CURRENT );
					newCmed.setFlgMisc( false );
					//cmed.setAcute(flg);
					//cmed.setAddlSigTxt(txt);	//TODO
					newCmed.setDAW( daw );
					newCmed.setFromMedReca( eRxID );
					cmed.setProbReca( cmed.getProbReca());
					//cmed.setProviderRec(rec);
					//cmed.setRdocRec(rec);
					//cmed.setVisitReca(reca);;
					
					newCmed.postNew( ptRec );
System.out.println( "Med posted: " + drugName + " " + drugID + " dosageCode=" + dosage + " routeCode=" + route + " formCode=" + form + " freqCode=" + freq + "prn=" + prn );


					// mark previous cmed entry as changed
					cmed.setStopDate(( rxDate == null ) ? usrlib.Date.today(): rxDate );
					cmed.setStatus( Cmed.Status.CHANGED );
					cmed.write( eRxID );
					
					
					
					
					
					
				// minor changes, just update this med info
				} else if ( flgChange == 1 ){
					
					// is this a refill (updated start date)
					if ( rxDate.compare( cmed.getStartDate()) > 0 ){							
						cmed.setLastRefillDate( rxDate );
					}
					cmed.write( eRxID );
				}
				
				
					

				
				
			// no eRxID - this is most likely a new prescription that needs to be recorded
			} else {
				
				Cmed cmed = new Cmed();
				cmed.setStartDate(( rxDate == null ) ? usrlib.Date.today(): rxDate );
				cmed.setDrugName( drugName );
				cmed.setDrugCode( drugID );
				cmed.setPtRec( ptRec );
				
				cmed.setDosage( dosage );
				cmed.setRoute( route );
				cmed.setForm( form );
				cmed.setSched( freq );

				cmed.setNumber( num );
				cmed.setRefills( refills );
				cmed.setPRN( prn );

				cmed.setChecked( (byte) 2 );
				cmed.setStatus( Cmed.Status.CURRENT );
				cmed.setFlgMisc( false );
				//cmed.setAcute(flg);
				//cmed.setAddlSigTxt(txt);
				cmed.setDAW( daw );
				//cmed.setProbReca(reca);
				//cmed.setProviderRec(rec);
				//cmed.setRdocRec(rec);
				//cmed.setVisitReca(reca);;
				
				cmed.postNew(ptRec);
System.out.println( "Med posted: " + drugName + " " + drugID + " dosageCode=" + dosage + " routeCode=" + route + " formCode=" + form + " freqCode=" + freq + "prn=" + prn );
			}
							

*/
			
			
		} else if ( z.getName().equals( "Problems" )){
			
		} else if ( z.getName().equals( "AdvanceDirectives" )){
			
		} else if ( z.getName().equals( "Support" )){
			
		} else if ( z.getName().equals( "FunctionalStatus" )){
			
		} else if ( z.getName().equals( "FamilyHistory" )){
			
		} else if ( z.getName().equals( "SocialHistory" )){
			
		} else if ( z.getName().equals( "Alerts" )){
			
		} else if ( z.getName().equals( "MedicalEquipment" )){
			
		} else if ( z.getName().equals( "Immunizations" )){
			
		} else if ( z.getName().equals( "VitalSigns" )){
			
		} else if ( z.getName().equals( "Results" )){
			
		} else if ( z.getName().equals( "Procedures" )){
			
		} else if ( z.getName().equals( "Encounters" )){
			
		} else if ( z.getName().equals( "PlanOfCare" )){
			
		} else if ( z.getName().equals( "HealthCareProviders" )){
			
		}
	}
	
	
	return body;
 

		
	}
}
