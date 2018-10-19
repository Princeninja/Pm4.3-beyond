package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vbox;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.XMLElement;

import java.util.Enumeration;
//import java.util.Timer;
//import java.util.TimerTask;

public class NCStatusChecker {

	//NCStatusChecker instance = null;
	Timer timer = null;
	Component parent;
	
	
	int count = 1;
	
	
	private NCStatusChecker(){
		
	}
	
	
	
	public static NCStatusChecker start( Component comp ){
		
		// if already started, just return
		//if ( instance != null ) return;
		
		// create instance
		NCStatusChecker nc = new NCStatusChecker();
		//instance = nc;
		
		nc.parent = comp;
		nc.setupTimer();		// java.util.Timer - not ZK Timer
		return nc;
	}
		
		
	public void setupTimer(){
		
		// refresh now
		refresh();
		
		
		// start timer
		timer = new Timer();		// java.util.Timer -- not ZK's Timer
		timer.setParent( parent );
		//timer.setPage( parent.getPage() );
		timer.setDelay( 1800000 );	// every half hour
		timer.setRepeats( true );
//		timer.scheduleAtFixedRate( new RefreshTask(), 5000, 1800000 );	// every half hour
		timer.addEventListener( Events.ON_TIMER, new EventListener(){
			public void onEvent(Event ev) throws Exception {
				System.out.println( "onTimer event");
				refresh();
			}
		});
		timer.setRunning( true );
		System.out.println( "Timer set")
;
	}
	
	
	
	// call newcrop to get status info
//	class RefreshTask extends TimerTask {
//		public void run(){	
//			check();
//			return;
//		}
//	}
	
	
	public void refresh(){
		check();
	}
	
	public void check(){
		
		
		
		
		String sendXml = "";
		String url = "https://preproduction.newcropaccounts.com/V7/WebServices/Update1.asmx";
		String action = "https://secure.newcropaccounts.com/V7/webservices/GetAccountStatusDetail";
		String partnerName = null;	// "palmed";
		String name = null;		// "demo";
		String password = null;	// "demo";
		String account = null;	// "pm000001";
		String site = null;		// "rfp01";
		//String patientID = String.format( "PT%06d", ptRec.getRec());
		
		
		ERxConfig erx = new ERxConfig();
		erx.read();
		url = erx.getWebServices();
		partnerName = erx.getPartnerName();
		name = erx.getPartnerUsername();
		password = erx.getPartnerPassword();
		account = erx.getAccountID();
		site = erx.getSiteID();
		
		
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
			p.setContent( "DrReview" );
			j.addChild( p );
		}				
		
/*		// add prescription history request
		{
		XMLElement c = new XMLElement();
		c.setName( "prescriptionHistoryRequest" );
			{
			XMLElement p = new XMLElement();
			p.setName( "StartHistory" );
			p.setContent( "2004-01-01T00:00:00" );
			c.addChild( p );
			}				
			{
			XMLElement p = new XMLElement();
			p.setName( "EndHistory" );
			p.setContent( "2012-01-01T00:00:00" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "PrescriptionStatus" );
			p.setContent( "%" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "PrescriptionSubStatus" );
			p.setContent( "%" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "PrescriptionArchiveStatus" );
			p.setContent( "N" );
			c.addChild( p );
			}
			
			j.addChild( c );
		}
		
		{
			XMLElement c = new XMLElement();
			c.setName( "includeSchema" );
			c.setContent( "N" );			
			j.addChild( c );
		}
		
*/
	
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
			if ( i == -1 ) return;
			
			int k;
			k = back.indexOf( "XmlResponse", i + 1 );
			if ( k == -1 ) return;
			
			back = new String( usrlib.Base64.decode( back.substring( i + 12, k - 2 )));
			System.out.println( back );


			
		XMLElement root2 = new XMLElement();
		root2.parseString( back );
		System.out.println( "new root2=" + root2.getName());
		
		Enumeration<XMLElement> en = root2.enumerateChildren();
		
		count = 0;
		
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
				
				
				String drugInfo = "";
				y = z.getChildByName( "DrugInfo" );
				if ( y != null ) drugInfo = y.getContent();
				
				String providerID = "";
				y = z.getChildByName( "ExternalDoctorId" );
				if ( y != null ) providerID = y.getContent();
				Rec provRec = new Rec( EditHelpers.parseInt( providerID.substring( 4, 8 )));
				
				String s = "";
				y = z.getChildByName( "PrescriptionDate" );
				if ( y != null ) s = y.getContent();
				Date rxDate = new Date( s );
				
				String rxStatus = "";
				y = z.getChildByName( "PrescriptionStatus" );
				if ( y != null ) rxStatus = y.getContent();
				
				String rxSubStatus = "";
				y = z.getChildByName( "PrescriptionSubStatus" );
				if ( y != null ) rxSubStatus = y.getContent();
				

				
					
				System.out.println( "Rx:" + rxDate.getPrintable() + "  " + ptName + "  " + drugInfo + "  " + providerID + "  " + rxStatus + " " + rxSubStatus );
				++count;
			}
		}
		

		Notifier.notify( null, Notifier.Event.NC_STATUS );
		System.out.println( "refresh");
		return;
	}
	
	
	public void checkNow(){
		//if ( instance == null ) return;
		check();
		return;
	}
	
	public int getCount(){
		//if ( instance == null ) return 0;
		System.out.println( "get count");
		return count;
	}
	
	
	
	
	
	
	
}
