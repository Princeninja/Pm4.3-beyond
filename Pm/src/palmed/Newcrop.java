package palmed;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.zkoss.zhtml.Input;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;

public class Newcrop {
	
	private ERxConfig eRx;
	private NCScript n = null;
	private XMLElement root = null;
	private XMLElement patient = null;
	
	
	
	
	public Newcrop(){

		// Get ERx Configuration
		eRx = new ERxConfig();
		if ( ! eRx.read( false )) System.out.println( "NCScript.setXMLHeader() could not read ERxConfig." );

	}
	
	
	
	
	public void setXMLHeader(){
		

		// Get ERx Configuration
		
		// Build XML
		root = NCScript.setHeader();
		return;
	}
	
	
	public void setCredentials(){
		// Set partner credentials
		//n.setCredentials( "palmed", "23f5c380-e15b-4aa9-aee5-ba999dcd7481", "48934091-d974-43c6-ad91-3fe7d5be6cfe", "PAL/MED", "V5.0" );		
		NCScript.setCredentials( root, eRx.getPartnerName(), eRx.getPartnerUsername(), eRx.getPartnerPassword(), 
				eRx.getPartnerProduct(), eRx.getPartnerVersion());				
		return;
	}
	
	
	public void setUserRole( String type, String role ){
		//n.setUserRole("LicensedPrescriber", "doctor");
		NCScript.setUserRole( root, type, role );
		return;
	}
	
	
		
		
	public void setDestination( String dest ){
		// Set Destination (landing page)
		//n.setDestination("compose");
		if (( dest != null ) && ( dest.trim().length() > 0 )) NCScript.setDestination( root, dest );
		return;
	}
	
	
		
		
		
	public void setAccount(){

		// Set Account info
		//n.setAccount("pm000001", "Riverside Family Practice", "rfp01", "3129 Blattner Dr", "", "Cape Girardeau", "MO", "63703", null, "US", "5733350166", "5733357942");
		NCScript.setAccount( root, eRx.getAccountID(), eRx.getAccountName(), eRx.getSiteID(), 
				eRx.getAccountStreet(), eRx.getAccountLine2(), eRx.getAccountCity(), eRx.getAccountState(), 
				eRx.getAccountZip().substring(0, 5), null, eRx.getAccountCountry(), 
				eRx.getAccountPhone(), eRx.getAccountFax());
		return;
	}
		
	public void setLocation(){

		// Set Location info
		//n.setLocation("rfp01", "Riverside Family Practice", "3129 Blattner Dr", "", "Cape Girardeau", "MO", "63703", null, "US", "5733350166", "5733357942", "5733350166");
		NCScript.setLocation( root, eRx.getLocationID(), eRx.getLocationName(), eRx.getLocationStreet(), eRx.getLocationLine2(), 
				eRx.getLocationCity(), eRx.getLocationState(), eRx.getLocationZip().substring( 0, 5 ), null, 
				eRx.getLocationCountry(), eRx.getLocationPhone(), eRx.getLocationFax(), eRx.getLocationContact());		
		return;
	}
	
	

	
	
	public void setProvider( Rec provRec ){
		
		if (( provRec == null ) || ( provRec.getRec() < 2 )) Clients.alert( "Bad provider rec" );

		Prov prov = new Prov( provRec );
		
		
		//n.setPrescriber("PROV0011", "Palen", "James", "Richard", "", "MD", "FP1275519", "1598902728", "MO", "2009030106" );		
		NCScript.setPrescriber( root, String.format( "PROV%04d", provRec.getRec()), 
				prov.getLastName(), prov.getFirstName(), prov.getMiddleName(), "", prov.getSuffix(), 
				prov.getDEA(), prov.getNPI(), prov.getLicenseState(), prov.getLicenseNum());		
		return;
	}
	
	
	
	public XMLElement setPatient( Rec ptRec ){
		
		if (( ptRec == null ) || ( ptRec.getRec() == 0 )) Clients.alert( "Bad ptRec" );
		
		// Set up PATIENT info
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );		
		usrlib.Name name = dirPt.getName();
		usrlib.Address add = dirPt.getAddress();
		
		patient = NCScript.setPatient( root, String.format( "PT%06d", ptRec.getRec()), name.getLastName(), name.getFirstName(), name.getMiddleName(), null, name.getSuffix(),
				dirPt.getPtNumber(), dirPt.getSSN( false ), "", 
				add.getStreet(), add.getLine2(), add.getCity(), add.getState(), add.getZip5(), add.getZipPlus4(), "US", add.getHome_ph(),
				dirPt.getBirthdate().getPrintable(8), dirPt.getSex().getAbbr());

		return patient;
	}

	

	public void setPars( Rec ptRec ){

		// for each PAR/allergy
		//     read PAR
		//     set PAR/allergy info into NCScript in 'Patient' element
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());

		// Format requires us to send codified allergies first and then the freeform allergies
		// loop through all of the codified allergies
		for ( Reca reca = medPt.getParsReca(); reca.getRec() != 0; ){

			Par par = new Par( reca );		
			if ( par.getStatus() == Par.Status.CURRENT ){
				if ( ! par.getFlgMisc()){				
					NCScript.setCodifiedAllergy( patient, reca.toString(), String.valueOf( par.getCompositeID()), par.getSeverity().getLabel(), par.getSymptomsText( true ));
				}
			}
			// get next reca in list	
			reca = par.getLLHdr().getLast();
		}

		// loop through the freeform allergies
		for ( Reca reca = medPt.getParsReca(); reca.getRec() != 0; ){

			Par par = new Par( reca );		
			if ( par.getStatus() == Par.Status.CURRENT ){
				if ( par.getFlgMisc()){				
					NCScript.setFreeFormAllergy( patient, reca.toString(), par.getParDesc(), par.getSeverity().getLabel(), par.getSymptomsText( true ));
				}
			}
			// get next reca in list	
			reca = par.getLLHdr().getLast();
		}

		return;
	}
	
	
	public void setMeds( Rec ptRec, boolean mark ){
		
		// for each current meds
		//     read Cmed
		//     set Cmed info into NCScript in root element		
		DirPt dirPt = new DirPt( ptRec );

		for ( Reca reca = ( new MedPt( dirPt.getMedRec())).getMedsReca(); reca.getRec() != 0; ){
			
			Cmed cmed = new Cmed( reca );
			// don't send past med or med that has been sent to or came from newcrop
			if ( ! cmed.getStopDate().isValid() ){ //&& ( cmed.getChecked() != 1 ) && ( cmed.getChecked() != 2 )){
				
				if (( ! cmed.isNewStyle()) || cmed.getFlgMisc()){
					
					NCScript.addOutsideRxUncoded( root, reca.toString(), cmed.getStartDate().getPrintable(8), "", cmed.getDrugName(), cmed.getNumber(), cmed.getRefills(), "reconcile", cmed.getAddlSigTxt(),
							0 /*actionID*/, cmed.getDosage(), cmed.getForm(), cmed.getRoute(), cmed.getSched(), cmed.getPRN(), cmed.getDAW(), "" );
					;
				} else {
				
					NCScript.addOutsideRxCoded( root, reca.toString(), cmed.getStartDate().getPrintable(8), "", cmed.getDrugCode(), cmed.getNumber(), cmed.getRefills(), "reconcile", cmed.getAddlSigTxt(),
							0 /*actionID*/, cmed.getDosage(), cmed.getForm(), cmed.getRoute(), cmed.getSched(), cmed.getPRN(), cmed.getDAW(), "" );
				}
				
				// mark this med as transmitted to newcrop
				//if ( mark ){
				//	cmed.setChecked( (byte) 1 );
				//	cmed.write(reca);
				//}
			}
			
			// get next reca in list	
			reca = cmed.getLLHdr().getLast();
		}

//		n.addOutsideRx("2001cd", "20070215", "Dr. Bob", "Crestor 5mg tab", "30", "1 daily", "0", "reconcile" );
//		n.addOutsideRx("78883", "20101205", "Dr. Rick", "Voltaren gel", "5", "Rub 4g into a.a. 2-4x/day", "3", null);

		return;
	}
	
	
	
	
	
	public String toXMLString(){
		return ( root.toString());
	}

	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * 
	 *  SOAP Routines
	 *  
	 *  
	 *  
	 *  
	 */
	
	
	
	public static XMLElement setSOAPEnvelope( XMLElement root ){
		
		root.setName( "soap@Envelope" );
		root.setAttribute( "xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/" );
		root.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
		root.setAttribute( "xmlns:xsd", "http://www.w3.org/2001/XMLSchema" );

		XMLElement body = new XMLElement();
		body.setName( "soap@Body" );
		root.addChild(body);
		
		return body;
	}
	
		
		

	public static XMLElement setSOAPCredentials( XMLElement root, String partnerName, String name, String password, String accountID, String siteID ){
		
		// add credentials
		{
		XMLElement c = new XMLElement();
		c.setName( "credentials" );
		
		{
		XMLElement p = new XMLElement();
		p.setName( "PartnerName" );
		p.setContent( partnerName );
		c.addChild( p );
		}				
		{
		XMLElement p = new XMLElement();
		p.setName( "Name" );
		p.setContent( name );
		c.addChild( p );
		}
		{
		XMLElement p = new XMLElement();
		p.setName( "Password" );
		p.setContent( password );
		c.addChild( p );
		}
		
		root.addChild( c );
		}
		
		// add account request
		{
		XMLElement c = new XMLElement();
		c.setName( "accountRequest" );
		
		{
		XMLElement p = new XMLElement();
		p.setName( "AccountId" );
		p.setContent( accountID );
		c.addChild( p );
		}				
		{
		XMLElement p = new XMLElement();
		p.setName( "SiteId" );
		p.setContent(siteID );
		c.addChild( p );
		}
		
		root.addChild( c );
		}
		
		return root;
	}


	public static XMLElement setSOAPPatient( XMLElement root, String patientID ){
	
		// add patient request
		{
		XMLElement c = new XMLElement();
		c.setName( "patientRequest" );
	
		{
		XMLElement p = new XMLElement();
		p.setName( "PatientId" );
		p.setContent( patientID );
		c.addChild( p );
		}				
		
		root.addChild( c );
		}
		
		return root;
	}
	
	
	
	public static String toSOAPXmlString( XMLElement root ){
		
		String xmlText = root.toString();
		xmlText = xmlText.replace( '@', ':' );		// because my simple XML class won't handle <SOAP:Envelope> etc.
		return xmlText;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public static String getUserType( pmUser.Role role ){
		
		String s = "None";
		if ( role == pmUser.Role.PHYSICIAN ) s = "LicensedPrescriber";
		if ( role == pmUser.Role.MIDLEVEL ) s = "MidlevelPrescriber";
		if ( role == pmUser.Role.NURSE ) s = "Staff";
		if ( role == pmUser.Role.ADMINISTRATOR ) s = "Staff";
		if ( role == pmUser.Role.MANAGER ) s = "Staff";
		if ( role == pmUser.Role.CLERICAL ) s = "Staff";		
		return s;
	}

	public static String getRoleString( pmUser.Role role ){
		
		String s = "None";
		if ( role == pmUser.Role.PHYSICIAN ) s = "doctor";
		if ( role == pmUser.Role.MIDLEVEL ) s = "midlevelPrescriber";
		if ( role == pmUser.Role.NURSE ) s = "nurse";
		if ( role == pmUser.Role.ADMINISTRATOR ) s = "manager";
		if ( role == pmUser.Role.MANAGER ) s = "manager";
		if ( role == pmUser.Role.CLERICAL ) s = "admin";		
		return s;
	}
	
	
	
	
	
	
	
	
	
	public static Window callNewcrop( String xmlTag, String xmlText, Component parent, EventListener onReturn ){
		
		ERxConfig eRx = new ERxConfig();
		if ( ! eRx.read()) System.out.println( "couldnt read ERxConfig." );
		String page = eRx.getClickThru();
		System.out.println( "page=" + page );
		
		
		//alert( "Remember:  Med changes made in NewCrop are not yet carried back to PAL/MED.  You need to update med lists in both screens for now.");
		
		// hide medications window
		//medsWin.setVisible( false );
		
		// Store XML text and unique tag to current Session
		Sessions.getCurrent().setAttribute( xmlTag, xmlText);
		Sessions.getCurrent().setAttribute( "clickthru", page );
		

		String newZUL =
			"<window id='" + xmlTag + "Win' border='none' height='800px' width='1200px' >" +
			"<vbox vflex= '1' hflex= '1' >" +
			"<button id='return' label='Return' />" +
			"<iframe id='frame'  style='height:750px;width:1075px' src='clickthru.zul?xmlTag=" 
			+ xmlTag + "' />" +
			"</vbox>" +
			"</window>";
		
		//height='500px' width='900px'
		//style='height:500px;width:900px'
		//"style='width:1000px; height:600px;border:3px inset;'"		
		//onClick='newWin.close();' 
			
		// create newcrop window and iframe
		Window newWin = (Window) Executions.createComponentsDirectly( newZUL, null, parent, null );
		//newWin.setHeight( "450px" );
		//newWin.setVflex( "true" );

		
		
		// add Event Listener to handle return button
		Button b = (Button) newWin.getFellow("return");		
		if ( onReturn != null ) b.addEventListener( "onClick", onReturn );

		//vflex= \"max\" hflex=\"max\" 
		//b.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$return( ev ); }
		//private void onClick$return(Event ev) {
		//	// TODO Auto-generated method stub
		//	
		//}});
		
		Events.postEvent( "onSize", newWin, null );
		// Note:  this is another way I could have set the iframe contents
		// set frame content
		//Iframe frame;
		//frame = ( Iframe) newWin.getFellow( "frame" );
		//frame.setContent( new org.zkoss.util.media.AMedia( new URL( "http://www.foxnews.com/index.html" ), "text/html", "UTF-8" ));
		//frame.setSrc( "clickthru3.zul" );
		

		return newWin;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*******************************************************************************************************************************
	 * Use GetAccountStatusDetail to get list of patients and prescriptions that need review.
	 */
	
	
	
	class StatusList {
		Rec ptRec;
		Rec provRec;
		String ptName;
		String ptDOB;
		String ptSSN;
		String drugInfo;
		String status;
		String subStatus;
		String date;
		
		StatusList( Rec ptRec, Rec provRec, String ptName, String ptDOB, String ptSSN, String drugInfo, String status, String subStatus, String date ){
			this.ptRec = ptRec;
			this.provRec = provRec;
			this.ptName = ptName;
			this.ptDOB = ptDOB;
			this.ptSSN = ptSSN;
			this.drugInfo = drugInfo;
			this.status = status;
			this.subStatus = subStatus;
			this.date = date;
		}
		
		public Rec getPtRec(){ return ptRec; }
		public Rec getProvRec(){ return provRec; }
		public String getPtName(){ return ptName; }
		public String getPtDOB(){ return ptDOB; }
		public String getPtSSN(){ return ptSSN; }
		public String getDrugInfo(){ return drugInfo; }
		public String getStatus(){ return status; }
		public String getSubStatus(){ return subStatus; }
		public String getDate(){ return date; }

		
	}
	
	
	public static List<StatusList> getStatusList(){
		
		Newcrop nc = new Newcrop();
		
		
		List<StatusList> list = new ArrayList<StatusList>( 12 );
		
		//String url = "https://preproduction.newcropaccounts.com/V7/WebServices/Update1.asmx";
		String action = "https://secure.newcropaccounts.com/V7/webservices/GetAccountStatusDetail";
		
		
		//ERxConfig erx = new ERxConfig();
		//erx.read();
		String url = nc.eRx.getWebServices();
		String partnerName = nc.eRx.getPartnerName();
		String name = nc.eRx.getPartnerUsername();
		String password = nc.eRx.getPartnerPassword();
		String account = nc.eRx.getAccountID();
		String site = nc.eRx.getSiteID();
		
		
		XMLElement root = new XMLElement();		
		XMLElement body = Newcrop.setSOAPEnvelope( root );
		
		
			
		XMLElement j = new XMLElement();
		j.setName( "GetAccountStatusDetail" );
		j.setAttribute( "xmlns", "https://secure.newcropaccounts.com/V7/webservices" );
		body.addChild( j );

		
		
		// add credentials and account info
		Newcrop.setSOAPCredentials( j, partnerName, name, password, account, site );
		//Newcrop.setSOAPPatient( j, patientID );
		
		{
			XMLElement p = new XMLElement();
			p.setName( "locationId" );
			p.setContent( "rfp01" );
			j.addChild( p );
		}				
		
		{
			XMLElement p = new XMLElement();
			p.setName( "licensedPrescriberId" );
			p.setContent( "prov0011" );
			j.addChild( p );
		}				
		
		{
			XMLElement p = new XMLElement();
			p.setName( "statusSectionType" );
			p.setContent( "AllDoctorReview" );
			j.addChild( p );
		}				
		
	
		// convert XMLElements to XML text string
		String xmlText = Newcrop.toSOAPXmlString( root );
		// print out XML file
		System.out.println(xmlText);

		
		
		String back = "";

		
		try {
			back = usrlib.SOAPClient4XG.send( url, action,  xmlText );
			System.out.println( "back is: "+back );
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		

			int i;
			i = back.indexOf( "XmlResponse" );
			if ( i == -1 ) return null;
			
			int k;
			k = back.indexOf( "XmlResponse", i + 1 );
			if ( k == -1 ) return null;
			
			back = new String( usrlib.Base64.decode( back.substring( i + 12, k - 2 )));
			System.out.println( back );


			
		XMLElement root2 = new XMLElement();
		root2.parseString( back );
		System.out.println( "new root2=" + root2.getName());
		
		Enumeration<XMLElement> en = root2.enumerateChildren();
		
		int count = 0;
		
		while (en.hasMoreElements()) {
			XMLElement z = (XMLElement) en.nextElement();
			if ( z == null ) break;
			if ( z.getName().equals( "StatusDetail" )){
				
				XMLElement y;
				
				String patientID = "";
				y = z.getChildByName( "ExternalPatientId" );
				if ( y != null ) patientID = y.getContent();
				Rec ptRec = new Rec ( EditHelpers.parseInt( patientID.substring( 2, 8 )));
				
				String ptFirstName = "";
				y = z.getChildByName( "PatientFirstName" );
				if ( y != null ) ptFirstName = y.getContent();
				
				String ptMiddleName = "";
				y = z.getChildByName( "PatientMiddleName" );
				if ( y != null ) ptMiddleName = y.getContent();
				
				String ptLastName = "";
				y = z.getChildByName( "PatientLastName" );
				if ( y != null ) ptLastName = y.getContent();
				
				String ptName = ptLastName + ", " + ptFirstName + " " + ptMiddleName;
				
				String ptDOB = "";
				y = z.getChildByName( "PatientDOB" );
				if ( y != null ) ptDOB = y.getContent();
				if (( ptDOB != null ) && ( ptDOB.length() > 1 )) ptDOB = (new Date( ptDOB )).getPrintable(9);
				
				String ptSSN = "";
				
				String drugInfo = "";
				y = z.getChildByName( "DrugInfo" );
				if ( y != null ) drugInfo = y.getContent();
				
				String providerID = "";
				y = z.getChildByName( "ExternalDoctorId" );
				if ( y != null ) providerID = y.getContent();
				Rec provRec = new Rec( EditHelpers.parseInt( providerID.substring( 4, 8 )));
				
				String s = "";
				Date rxDate = null;
				y = z.getChildByName( "PrescriptionDate" );
				if ( y != null ) s = y.getContent();
				if (( s != null ) && ( s.length() > 1 )){
					String[] tok = s.split( " " );
					rxDate = new Date( tok[0] );
				}
				
				String rxStatus = "";
				y = z.getChildByName( "PrescriptionStatus" );
				if ( y != null ) rxStatus = y.getContent();
				
				String rxSubStatus = "";
				y = z.getChildByName( "PrescriptionSubStatus" );
				if ( y != null ) rxSubStatus = y.getContent();
				
				StatusList item = nc.new StatusList( ptRec, provRec, ptName, ptDOB, ptSSN, drugInfo, rxStatus, rxSubStatus, rxDate.getPrintable(9));
				list.add( item );
			}

		}
		System.out.println("pre List is:"+list.toString());
		return list;
	}
	

	
	
	
	
	
	
	
	public static List<StatusList> getProviderStatus( Rec provRec ){
		
		Newcrop nc = new Newcrop();
		
		
		List<StatusList> list = new ArrayList<StatusList>( 12 );
		
		//String url = "https://preproduction.newcropaccounts.com/V7/WebServices/Update1.asmx";
		String action = "https://secure.newcropaccounts.com/V7/webservices/GetAccountStatusDetail";
		
		
		//ERxConfig erx = new ERxConfig();
		//erx.read();
		String url = nc.eRx.getWebServices();
		String partnerName = nc.eRx.getPartnerName();
		String name = nc.eRx.getPartnerUsername();
		String password = nc.eRx.getPartnerPassword();
		String account = nc.eRx.getAccountID();
		String site = nc.eRx.getSiteID();
		
		String provID = String.format( "PROV%04d", provRec.getRec());
		
		
		
		XMLElement root = new XMLElement();		
		XMLElement body = Newcrop.setSOAPEnvelope( root );
		
		
			
		XMLElement j = new XMLElement();
		j.setName( "LicensedPrescriberStatus" );
		j.setAttribute( "xmlns", "https://secure.newcropaccounts.com/V7/webservices" );
		body.addChild( j );

		
		
		// add credentials and account info
		Newcrop.setSOAPCredentials( j, partnerName, name, password, account, site );
		//Newcrop.setSOAPPatient( j, patientID );
		
		{
			XMLElement p = new XMLElement();
			p.setName( "licensedPrescriberId" );
			p.setContent( provID );
			j.addChild( p );
		}				
		
		{
			XMLElement p = new XMLElement();
			p.setName( "locationId" );
			p.setContent( "rfp01" );
			j.addChild( p );
		}				
				
	
		// convert XMLElements to XML text string
		String xmlText = Newcrop.toSOAPXmlString( root );
		// print out XML file
		System.out.println(xmlText);

		
		
		String back = "";

		
		try {
			back = usrlib.SOAPClient4XG.send( url, action,  xmlText );
			System.out.println( back );
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		

			int i;
			i = back.indexOf( "XmlResponse" );
			if ( i == -1 ) return null;
			
			int k;
			k = back.indexOf( "XmlResponse", i + 1 );
			if ( k == -1 ) return null;
			
			back = new String( usrlib.Base64.decode( back.substring( i + 12, k - 2 )));
			System.out.println( back );


			
		XMLElement root2 = new XMLElement();
		root2.parseString( back );
		System.out.println( "new root2=" + root2.getName());
		
		Enumeration<XMLElement> en = root2.enumerateChildren();
		
		int count = 0;
		
		while (en.hasMoreElements()) {
			XMLElement z = (XMLElement) en.nextElement();
			if ( z == null ) break;
			if ( z.getName().equals( "StatusDetail" )){
				
				XMLElement y;
				
				String patientID = "";
				y = z.getChildByName( "ExternalPatientId" );
				if ( y != null ) patientID = y.getContent();
				Rec ptRec = new Rec ( EditHelpers.parseInt( patientID.substring( 2, 8 )));
				
				String ptFirstName = "";
				y = z.getChildByName( "PatientFirstName" );
				if ( y != null ) ptFirstName = y.getContent();
				
				String ptMiddleName = "";
				y = z.getChildByName( "PatientMiddleName" );
				if ( y != null ) ptMiddleName = y.getContent();
				
				String ptLastName = "";
				y = z.getChildByName( "PatientLastName" );
				if ( y != null ) ptLastName = y.getContent();
				
				String ptName = ptLastName + ", " + ptFirstName + " " + ptMiddleName;
				
				String ptDOB = "";
				y = z.getChildByName( "PatientDOB" );
				if ( y != null ) ptDOB = y.getContent();
				if (( ptDOB != null ) && ( ptDOB.length() > 1 )) ptDOB = (new Date( ptDOB )).getPrintable(9);
				
				String ptSSN = "";
				
				String drugInfo = "";
				y = z.getChildByName( "DrugInfo" );
				if ( y != null ) drugInfo = y.getContent();
				
				String providerID = "";
				y = z.getChildByName( "ExternalDoctorId" );
				if ( y != null ) providerID = y.getContent();
				//Rec provRec = new Rec( EditHelpers.parseInt( providerID.substring( 4, 8 )));
				
				String s = "";
				Date rxDate = null;
				y = z.getChildByName( "PrescriptionDate" );
				if ( y != null ) s = y.getContent();
				if (( s != null ) && ( s.length() > 1 )){
					String[] tok = s.split( " " );
					rxDate = new Date( tok[0] );
				}
				
				String rxStatus = "";
				y = z.getChildByName( "PrescriptionStatus" );
				if ( y != null ) rxStatus = y.getContent();
				
				String rxSubStatus = "";
				y = z.getChildByName( "PrescriptionSubStatus" );
				if ( y != null ) rxSubStatus = y.getContent();
				
				StatusList item = nc.new StatusList( ptRec, provRec, ptName, ptDOB, ptSSN, drugInfo, rxStatus, rxSubStatus, rxDate.getPrintable(9));
				list.add( item );
			}

		}
		
		return list;
	}
	

	
}

	/**/

