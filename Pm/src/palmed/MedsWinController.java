package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import palmed.pmUser.Role;
import palmed.PmLogin.PmLogin;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.ZkTools;

public class MedsWinController extends GenericForwardComposer {
	
	private Window medsWin;				// autowired - medications window
	private Listbox medsListbox;		// autowired - medications listbox
	private Button newrx;				// autowired - new prescription button
	private Button pending;				// autowired - process pending requests
	private Radio r_current;			// autowired
	private Radio r_past;				// autowired
	private Radio r_history;			// autowired
	private Listheader stop;			// autowired
	private Listheader status;			// autowired
	private Groupbox gbNoMeds;			// autowired
	private Checkbox cbNoMeds;			// autowired
	
	
	private Rec	ptRec;		// patient record number
	private Rec provRec = null;
	pmUser.Role role = null;
	
	

	public MedsWinController() {
		// TODO Auto-generated constructor stub
	}

	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		//System.out.println( "MedsWinController() ptRec=" + ptRec.getRec());
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "MedsWinController.doAfterCompose() ptRec invalid," );


		
		// populate listbox
		refreshList();
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notifier.Event.MED );
 
		
		newrx.setVisible(false);
		return;
	}
	
	
	
	
	
	
	public void refreshList(){
		
		boolean flgCurrent = false;
		
		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_current.isSelected()) display = 1;
		if ( r_past.isSelected()) display = 2;
		if ( r_history.isSelected()) display = 3;

		// display stop date column header?
		stop.setVisible((( display & 2 ) != 0 ) ? true: false );
		status.setVisible(( display == 3 ) ? true: false );

		
		if ( medsListbox == null ) return;
		
		// clear listbox
		ZkTools.listboxClear( medsListbox );
		
		
		
		// populate Med list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getMedsReca();

		// finished if there are no entries to read
		//if ( ! Reca.isValid( reca )) return;

		for ( ; Reca.isValid( reca ); ){
			
			Cmed cmed = new Cmed( reca );
			//String s = cmed.toString();
			//System.out.println( "Cmed=" + s );
			
			// get problem status byte
			Cmed.Status status = cmed.getStatus();
			
			// is this a 'Current Med'?
			if ( status == Cmed.Status.CURRENT ) flgCurrent = true;
			
			// if no status byte - look at stop date & set status (to support legacy code)
			if ( status == null ){
				status = cmed.getStopDate().isValid() ? Cmed.Status.DISCONTINUED: Cmed.Status.CURRENT;
			}
			
			// is this type selected?
			if ((( status == Cmed.Status.CURRENT ) && (( display & 1 ) != 0 ))
				|| (( status == Cmed.Status.DISCONTINUED ) && (( display & 2 ) != 0 ))
				|| (( status == Cmed.Status.CHANGED ) && (( display & 2 ) != 0 ))){
				// ( status == 'R' ) - removed	//TODO
				
			
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( medsListbox );
				i.setValue( reca );		// store Cmed reca in listitem's value

				new Listcell( cmed.getStartDate().getPrintable(9)).setParent( i );
				String s = cmed.getStopDate().getPrintable(9);
				if ( s.equals( "00-00-0000" )) s = "";
				new Listcell( s ).setParent( i );


				
				Listcell lc = new Listcell( cmed.getDrugName());
				lc.setParent( i );
				String drugCode = cmed.getDrugCode();
				if ( drugCode.length() > 1 ){
					NCFull nc = NCFull.readMedID( drugCode );
					if ( nc != null )
						lc.setTooltiptext( nc.getMedEtc());
				}
				
				String doseString = MedDosage.getDesc( cmed.getDosage());
				int formID = cmed.getForm();
				if ( formID > 0 ) doseString += " " + MedForm.getDesc( formID );
				new Listcell( doseString ).setParent( i );
				
				new Listcell( MedRoute.getDesc( cmed.getRoute())).setParent( i );
				new Listcell( MedFreq.getDesc( cmed.getSched())).setParent( i );
				new Listcell( cmed.getPRN() ? "PRN": "" ).setParent( i );

				// set medication status
				s = status.getLabel();
				new Listcell( s ).setParent( i );
				
				new Listcell( cmed.getDrugCode()).setParent( i );
				
				String checked = "";
				int ch = cmed.getChecked();
				if ( ch == 1 ) checked = "S";
				if ( ch == 2 ) checked = "P";
				new Listcell( checked ).setParent( i );
				
				new Listcell( cmed.getAddlSigTxt()).setParent( i );

			}
			
			
			// get next reca in list	
			reca = cmed.getLLHdr().getLast();
		}
		

		// are there any 'Current Meds'?
		if ( flgCurrent ){
			gbNoMeds.setVisible( false );			
		} else {
			gbNoMeds.setVisible( true );
			cbNoMeds.setChecked( medPt.getNoMedsFlag());
		}


		return;
	}
	
	
	
	
	public void onCheck$cbNoMeds( Event ev ){
		
		MedUtils.setNoMedsFlag( ptRec, cbNoMeds.isChecked());

		// notify that we have modified Meds
		Notifier.notify( ptRec, Notifier.Event.MED );
		return;
	}
	
	
	
	

	
	// Watch for radiobutton to change
	public void onCheck$r_current( Event ev ){
		refreshList();
	}
	public void onCheck$r_past( Event ev ){
		refreshList();
	}
	public void onCheck$r_history( Event ev ){
		refreshList();
	}
	
	Boolean nursev = false;
	//Object syncObject = "";
	//static Object[] Newcroppram2 = new Object[]{null, null};
	
	//public Object[] nursev()
	
	public void nursev(){
		
		   final Rec userRec = new Rec ( PmLogin.show(medsWin,"true"));
		   
		    
		   System.out.println("new user rec is: "+userRec);
		   
		   // Object[] Newcroppram = new Object[]{null, null};
		   //final Object[] Newcroppram2 = new Object[]{null, null};
		   /*	
			// Does user have permission?
			if ( ! Perms.isRequestPermitted( Perms.Medical.PRESCRIPTIONS )){
				//System.out.println( "no Perms.Medical.PRESCRIPTIONS permission");
				SystemHelpers.msgAccessDenied();
				return;
			}

			
			
			// get user
			Rec userRec = Pm.getUserRec();*/
			if (( userRec == null ) || ( userRec.getRec() < 2 )){
				//System.out.println( "userRec not valid");
				SystemHelpers.msgAccessDenied();
				return;			
			}
			
			final pmUser user = new pmUser( userRec );		
			final pmUser.Role role2 = user.getRole();
			
			//System.out.println("preha");
			if (( role2 == pmUser.Role.PHYSICIAN ) || ( role2 == pmUser.Role.MIDLEVEL )){
				role = role2;
				provRec = user.getProvRec();
				
				if (!(newrx == null)){
				newrx.setVisible(true);}
								
						
			}else { 
					
							
			
			System.out.println("postha");
			
			//System.out.println("role is: "+role.name());
		   /*		if (( provRec == null ) || ( provRec.getRec() < 2 )){
					//System.out.println( "prov user's provRec not valid");
					SystemHelpers.msgAccessDenied();
					return;			
				}
							
			} else {
				
				// get provider to use
				if (( provRec = ProvSearch.show()).getRec() == 0 ){
					// No provider selected
					//System.out.println( "no provider selected");
					return;
				}			
			}
			

			
			// get supervising provider rec
			Prov prov = new Prov( provRec );
			
			if ( prov.getNeedsSupervisor() == true ){			
				// get supervising provider to use
				//System.out.println( "supervising provider required");
				if (( supRec = ProvSearch.show()).getRec() == 0 ){
					// No provider selected
					//System.out.println( "no supervising provider selected");
					return;
				}			
			}
			
		    */
			
			/*synchronized(syncObject){
				try {
			        // Calling wait() will block this thread until another thread
			        // calls notify() on the object.
					while (!nursev) {
					System.out.println("waiting");
			        syncObject.wait(); }
			    } catch (InterruptedException e1) {
			        // Happens if someone interrupts your thread.
			    }
			}*/
		
		
		final Window HandP = new Window();	
		
		HandP.setParent(medsWin);
		HandP.setPosition("center");
		
		HandP.setHeight("402px"); //reduced from 702px
		HandP.setWidth("513px");
		HandP.setVflex("1");
		HandP.setHflex("1");
		HandP.setBorder("normal");
				
		
				
		
		
		 Vbox MainVbox = new Vbox();
		 MainVbox.setHeight("357px"); //reduced from 667px
		 MainVbox.setWidth("491px");
		 MainVbox.setParent(HandP);
		 
		 
		
		 Hbox hbox00 = new Hbox();
		 hbox00.setParent( MainVbox );
		
		 Groupbox gbox00 = new Groupbox();
		 gbox00.setHflex("max");
		 gbox00.setParent(hbox00);
		 
		 new Caption ("Choose Prescribing Provider: ").setParent(gbox00);
		
		 Grid grid00 = new Grid();
		 grid00.setParent(gbox00);
		 
		 Rows rows00 = new Rows();
		 rows00.setParent(grid00);
		 			 
		 Row row01 = new Row();
		 row01.setParent(rows00);
		 
		 final Label l01 = new Label();
		 l01.setValue("Provider: ");
		 l01.setParent(row01);
		 
		 final Listbox li01 = new Listbox();
		 li01.setMold("select");
		 li01.setWidth("200px");
		 li01.setParent(row01);
		 
		 Prov.fillListbox(li01, true);
		 li01.removeItemAt(0);
		 
		 Row row002 = new Row();
		 row002.setParent(rows00);
		 
		 Label l002 = new Label();
		 l002.setValue("Provider Role: ");
		 l002.setParent(row002);
		 
		 final Listbox li02 = new Listbox();
		 li02.setMold("select");
		 li02.setWidth("200px");
		 li02.setParent(row002);
		 
		 Listitem neww = new Listitem ("Physician");
		 neww.setParent(li02);
		 
		 Listitem ns = new Listitem ("Nurse Practitioner");
		 ns.setParent(li02);
		 
		 
		 Hbox SecondHbox = new Hbox();
			SecondHbox.setHeight("26px");
			SecondHbox.setWidth("200px");
			SecondHbox.setPack("start");
			SecondHbox.setParent(HandP);
			
			Div end = new Div();
			end.setParent(SecondHbox);
			end.setStyle("text-align: right");
			
			
			
			Button Save = new Button();
				Save.setParent(end);
				Save.setLabel("Select");
				Save.setWidth("65px");
				Save.setHeight("23px");
				Save.addEventListener(Events.ON_CLICK, new EventListener(){
					
					@SuppressWarnings("static-access")
					public void onEvent(Event arg1) throws Exception {
						// TODO Auto - generated method stub
						
						if ( li01.getSelectedItem() == null ){
							try { Messagebox.show( "Please (Re-)Choose a Provider." ); } catch (InterruptedException e) { /*ignore*/ }
							return;
						}
						
						if ( li02.getSelectedItem() == null ){
							try { Messagebox.show( "Please (Re-)Choose a role." ); } catch (InterruptedException e) { /*ignore*/ }
							return;
						}
												
						provRec = (Rec) ZkTools.getListboxSelectionValue( li01 );
						//pmUser.Role role3 =  null;
						
						if ( ! Rec.isValid(provRec)) provRec = new Rec( 2 );
						
						if ( li02.getSelectedItem().getLabel().toString().trim().equals("Physician")){
							role = pmUser.Role.PHYSICIAN;	
							
						}
						else { role = pmUser.Role.MIDLEVEL; }
						
													
						File VisitFile = new File( Pm.getOvdPath() + File.separator + "NursePresc.txt");
						String VisitString = FileUtils.readFileToString(VisitFile);
						
						Date Tdate = usrlib.Date.today();
						java.util.Calendar calendar = GregorianCalendar.getInstance(); 
						 int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
						 int mins = calendar.get(java.util.Calendar.MINUTE);
							
						 //String timenow = (String.format("%02d", hour))+":"+ (String.format("%02d", mins));
						 final String timestrt = (String.format("%02d", hour)) + (String.format("%02d", mins));
						
						StringBuilder Log = new StringBuilder();
						Log.append(VisitString);
						Log.append(System.getProperty("line.separator"));
						Log.append(user.getName(userRec).getPrintableNameLFM().trim()+" used "+ li01.getSelectedItem().getLabel().toString().trim()+" prescription credentials at "+timestrt+" on "+Tdate.getPrintable()+".");
						Log.append(System.getProperty("line.separator"));
						
						String visitpath =  Pm.getOvdPath() + File.separator + "NursePresc.txt";
						File Visitfile = new File(visitpath);
							
						System.out.println("path: "+visitpath);
						
						
						try {
							FileUtils.writeStringToFile(Visitfile, Log.toString());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						newrx.setVisible(true);
																    
					    
						HandP.detach();
						//nursev = true;		
						
						
						//NursePresc.txt
						
						
						/*synchronized(syncObject) {
							System.out.println("notfied");
						    syncObject.notify();
						}*/	
									
					
					}
						 
				 });	
		
				Button Close = new Button();
				Close.setParent(end);
				Close.setLabel("Cancel");
				Close.setWidth("65px");
				Close.setHeight("23px");
				Close.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						// TODO Auto-generated method stub
						if ( Messagebox.show( "Do you wish to cancel this process? "," Cancel new prescription?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
						HandP.detach();
											
					}
					
				});	
		
				HandP.doOverlapped();
				//System.out.println("post overlap"); }
		
	   
	}
			
			
			
	}
	
	
	
	public void onClick$newprov( Event e)
	{
		nursev();
		
		
	}	
	
	public void onClick$newrx( Event e ) throws FileNotFoundException, MalformedURLException{
		
		
		Rec supRec = null;			// supervising provider rec
		
	
		// unique tag (key) to store/retrieve XML to/from Session
		Random random = new Random();
		String xmltag = String.format( "tag%04d", random.nextInt( 10000 ));
		
		
		
		// Build XML
		Newcrop newcrop = new Newcrop();
		
		newcrop.setXMLHeader();
		newcrop.setCredentials();
		newcrop.setUserRole( Newcrop.getUserType( role ), Newcrop.getRoleString( role ));
		newcrop.setDestination( "compose" );
		newcrop.setAccount();
		newcrop.setLocation();
		newcrop.setProvider( provRec );
		
		newcrop.setPatient( ptRec );
		newcrop.setPars( ptRec );
		newcrop.setMeds( ptRec, false );
		
				
		// print out XML file
		String xmlText = newcrop.toXMLString();
		System.out.println(xmlText);

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_NEWRX, ptRec, Pm.getUserRec(), null, null );
		
			  
		// call newcrop window
		//callNewcropWin( xmltag, xmlText );
		
		medsWin.setVisible( false );
		Newcrop.callNewcrop( xmltag, xmlText, medsWin.getParent(), new EventListener(){
			public void onEvent( Event ev ) throws Exception {
				onClick$return( ev );
			}});
		newrx.setVisible(false);
		return; 
	}
	

	
	
	
	
	
	// Add an outside medication
	public void onClick$btnAdd( Event e ){
		
		boolean changes = MedAdd.show( EditPt.Operation.NEWPT, ptRec, medsWin );

		// notify that we have modified PARs
		if ( changes ) Notifier.notify( ptRec, Notifier.Event.MED );
		return;
	}
	

	
	
	
	
	// Discontinue a medication
	public void onClick$btnDC( Event e ){
		
		if ( medsListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No medication selected." );
			return;
		}
		
		// get selected cmed Reca
		Reca cmedReca = (Reca) medsListbox.getSelectedItem().getValue();
		if ( ! Reca.isValid( cmedReca )) SystemHelpers.seriousError( "MedsWinController.onClick$btnDC: bad cmedReca" );
		
		boolean changes = MedDC.show( ptRec, cmedReca, medsWin );
		
		// notify that we have modified PARs
		if ( changes ) Notifier.notify( ptRec, Notifier.Event.MED );
		return;
	}
	
	
	
	
	
	
	public void onClick$pending( Event e ) throws FileNotFoundException, MalformedURLException{
		
		Rec provRec = null;
		Rec supRec = null;			// supervising provider rec
		
		
		
		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Medical.PRESCRIPTIONS )){
			//System.out.println( "no Perms.Medical.PRESCRIPTIONS permission");
			SystemHelpers.msgAccessDenied();
			return;
		}

		
		
		// get user
		Rec userRec = Pm.getUserRec();
		if (( userRec == null ) || ( userRec.getRec() < 2 )){
			//System.out.println( "userRec not valid");
			SystemHelpers.msgAccessDenied();
			return;			
		}
		
		pmUser user = new pmUser( userRec );		
		pmUser.Role role = user.getRole();
		

		if (( role == pmUser.Role.PHYSICIAN ) || ( role == pmUser.Role.MIDLEVEL )){
			
			provRec = user.getProvRec();
			if (( provRec == null ) || ( provRec.getRec() < 2 )){
				//System.out.println( "prov user's provRec not valid");
				SystemHelpers.msgAccessDenied();
				return;			
			}
						
		} else {
			
			// get provider to use
			if (( provRec = ProvSearch.show()).getRec() == 0 ){
				// No provider selected
				//System.out.println( "no provider selected");
				return;
			}			
		}
		

		
		// get supervising provider rec
		Prov prov = new Prov( provRec );
		
		if ( prov.getNeedsSupervisor() == true ){			
			// get supervising provider to use
			//System.out.println( "supervising provider required");
			if (( supRec = ProvSearch.show()).getRec() == 0 ){
				// No provider selected
				//System.out.println( "no supervising provider selected");
				return;
			}			
		}
		

		
		// unique tag (key) to store/retrieve XML to/from Session
		Random random = new Random();
		String xmltag = String.format( "tag%04d", random.nextInt( 10000 ));
		
		
		
		// Build XML		
		Newcrop newcrop = new Newcrop();
		newcrop.setXMLHeader();
		newcrop.setCredentials();
		newcrop.setUserRole( Newcrop.getUserType( role ), Newcrop.getRoleString( role ));
		newcrop.setDestination( "status" );
		newcrop.setAccount();
		newcrop.setLocation();
		newcrop.setProvider( provRec );
		newcrop.setPatient( ptRec );		

		// print out XML file
		String xmlText = newcrop.toXMLString();
		System.out.println(xmlText);

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_PENDING, ptRec, Pm.getUserRec(), null, null );
		
		

		// call newcrop window
		medsWin.setVisible( false );
		Newcrop.callNewcrop( xmltag, xmlText, medsWin.getParent(), new EventListener(){
			public void onEvent( Event ev ){
				medsWin.setVisible( true );
				return;
			}});
	}

	
	
	
	
	
	
	
	// 'Return' button event handler (Return button in Newcrop window)
	//  NOT AUTOWIRED
	public void onClick$return( Event ev ){

		// pull back and reconcile meds, allergies, etc		
		
		// get newcrop window
		Window newWin = (Window) ev.getTarget().getParent().getParent();
			// could have used getSpaceOwner() ????
		
		// close newcrop Iframe
		Iframe frame = (Iframe) newWin.getFellow( "frame" );
		if ( frame != null ) frame.detach();		
		
		// pull back meds
		newcrop_pullback_meds( ptRec );
		newcrop_pullback_allergies( ptRec );		
		
		// close newcrop display Window
		newWin.detach();

		// Un-hide medications window
		medsWin.setVisible( true );
		return;
	}
	
	
	
	
	
	
	
	
	
	


	
	
	
	void newcrop_pullback_meds( Rec ptRec ){
		
		boolean changes = false;			// flag that changes have been made to meds

			
		String sendXml = "";
		String url = null;		//"https://preproduction.newcropaccounts.com/V7/WebServices/Update1.asmx";
		String action = "https://secure.newcropaccounts.com/V7/webservices/GetPatientFullMedicationHistory6";
		String partnerName = null;	// "palmed";
		String name = null;		// "demo";
		String password = null;	// "demo";
		String account = null;	// "pm000001";
		String site = null;		// "rfp01";
		String patientID = String.format( "PT%06d", ptRec.getRec());
		
		
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
		j.setName( "GetPatientFullMedicationHistory6" );
		j.setAttribute( "xmlns", "https://secure.newcropaccounts.com/V7/webservices" );
		body.addChild( j );

		
		
		// add credentials and account info
		Newcrop.setSOAPCredentials( j, partnerName, name, password, account, site );
		Newcrop.setSOAPPatient( j, patientID );
		
		// add prescription history request
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
			p.setContent( "C" );					// only pull back completed meds
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
			p.setContent( "N" );					// only pull back 'current' or 'not archived' meds
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
		//System.out.println( "new root2=" + root2.getName());
		
		XMLElement c = root2.getElementByPathName( "NewDataSet/Table/DrugName" );
		if ( c != null ) System.out.println( c.getContent());
		
		Enumeration<XMLElement> en = root2.enumerateChildren();
		while (en.hasMoreElements()) {
			XMLElement z = (XMLElement) en.nextElement();
			if ( z == null ) break;
			if ( z.getName().equals( "Table" )){
				
				String strErxID = "";
				String drugName = "";
				String drugID = "";
				int dosage = 0;
				int form = 0;
				int route = 0;
				int freq = 0;
				boolean prn = false;
				boolean daw = false;
				boolean flgERx = false;
				int num = 0;
				int refills = 0;
				Reca eRxID = null;
				Date rxDate = null;
				XMLElement y;
				int deaClass = 0;
				
				
				
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
				
				y = z.getChildByName( "DeaClassCode" );
				if ( y != null ) deaClass = EditHelpers.parseInt(y.getContent());
				
				y = z.getChildByName( "PharmacyNCPDP" );
				if (( y != null ) && ( y.getContent() != null ) && ( y.getContent().length() > 0 )) flgERx = true;;
				
				


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
						cmed.setDEAClass( deaClass );

						newCmed.setChecked( (byte)( flgERx ? 3: 2 ));
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
	//System.out.println( "Med posted: " + drugName + " " + drugID + " dosageCode=" + dosage + " routeCode=" + route + " formCode=" + form + " freqCode=" + freq + "prn=" + prn );

	
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
						changes = true;
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

					cmed.setChecked( (byte)( flgERx ? 3: 2 ));	// mark whether sent electronically
					cmed.setStatus( Cmed.Status.CURRENT );
					cmed.setFlgMisc( false );
					//cmed.setAcute(flg);
					//cmed.setAddlSigTxt(txt);
					cmed.setDAW( daw );
					//cmed.setProbReca(reca);
					//cmed.setProviderRec(rec);
					//cmed.setRdocRec(rec);
					//cmed.setVisitReca(reca);;
					cmed.setDEAClass( deaClass );
					
					cmed.postNew(ptRec);
//System.out.println( "Med posted: " + drugName + " " + drugID + " dosageCode=" + dosage + " routeCode=" + route + " formCode=" + form + " freqCode=" + freq + "prn=" + prn );
					changes = true;
					
					// unset 'No Medications' flag
					MedUtils.setNoMedsFlag( ptRec, false );
				}
								


				
				
			}
		}
	 
		// notify that we have modified Meds
		if ( changes ) Notifier.notify( ptRec, Notifier.Event.MED );
		return;
	}
	

	void newcrop_pullback_allergies( Rec ptRec ){
		
		boolean changes = false;			// flg that changes have been made
		
		String sendXml = "";
		String url = null;		//"https://preproduction.newcropaccounts.com/V7/WebServices/Update1.asmx";
		String action = "https://secure.newcropaccounts.com/V7/webservices/GetPatientAllergyHistoryV3";
		String partnerName = null;	// "palmed";
		String name = null;		// "demo";
		String password = null;	// "demo";
		String account = null;	// "pm000001";
		String site = null;		// "rfp01";
		String patientID = String.format( "PT%06d", ptRec.getRec());
		
		
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
		j.setName( "GetPatientAllergyHistoryV3" );
		j.setAttribute( "xmlns", "https://secure.newcropaccounts.com/V7/webservices" );
		body.addChild( j );

		
		
		// add credentials and account info
		Newcrop.setSOAPCredentials( j, partnerName, name, password, account, site );
		Newcrop.setSOAPPatient( j, patientID );
		
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
		
		XMLElement c = root2.getElementByPathName( "NewDataSet/Table/AllergyName" );
		if ( c != null ) System.out.println( c.getContent());
		
		Enumeration<XMLElement> en = root2.enumerateChildren();
		while (en.hasMoreElements()) {
			XMLElement z = (XMLElement) en.nextElement();
			if ( z == null ) break;
			if ( z.getName().equals( "Table" )){
				
				String drugName = "";
				int compositeID = 0;
				String notes = "";
				String code = "";
				Par.Severity severity = null;
				
				
				XMLElement y = z.getChildByName( "AllergyName" );
				if ( y != null ) drugName = y.getContent();
				
				{
				String s = "";
				y = z.getChildByName( "CompositeAllergyID" );
				if ( y != null ) s = y.getContent();
				compositeID = EditHelpers.parseInt( s );
				}
				{
				String s = ""; 
				y = z.getChildByName( "AllergySeverityName" );
				if ( y != null ) s = y.getContent();
				if ( s.length() > 0 ) severity = Par.Severity.get( s );
				if ( severity == null ) severity = Par.Severity.UNSPECIFIED;
				}
				
				y = z.getChildByName( "AllergyNotes" );
				if ( y != null ) notes = y.getContent();
				
				y = z.getChildByName( "AllergyId" );
				if ( y != null ) code = y.getContent();
				
/*				y = z.getChildByName( "Refills" );
				if ( y != null ){
					String s = y.getContent();
					if (( s != null ) && ( s.length() > 0 ) && Character.isDigit( s.charAt(0)))
						refills = Integer.parseInt( s );
				}
*/				
				
			    //<AllergyType>1</AllergyType>
			    //<AllergySourceID>F</AllergySourceID>
			    //<AllergyId>476</AllergyId>
			    //<AllergyConceptId>1</AllergyConceptId>
			    //<ConceptType>GROUP</ConceptType>
			    //<Status>A</Status>
			    //<AllergySeverityTypeId>1</AllergySeverityTypeId>
			    //<ConceptID>476</ConceptID>
			    //<ConceptTypeId>1</ConceptTypeId>
				

				
				// now need to make sure this med not already recorded -- ARGHHH!
				// for each current meds
				//     read Cmed
				//     set Cmed info into NCScript in root element	
				boolean found = false;
				{
				DirPt dirPt = new DirPt( ptRec );
				MedPt medPt = new MedPt( dirPt.getMedRec());

				for ( Reca reca = medPt.getParsReca(); reca.getRec() != 0; ){
					
					Par par = new Par( reca );
					if ( par.getStatus() == Par.Status.CURRENT ){
	
						if ( par.getCompositeID() == compositeID ){
						// TODO - check severity, etc					
							found = true;
							break;
						}
					}
					
					// get next reca in list	
					reca = par.getLLHdr().getLast();
				}

				
				
				if ( ! found ){
								
					Par par = new Par();
					par.setDate( usrlib.Date.today());
					par.setPtRec( ptRec );
					par.setStatus( Par.Status.CURRENT );
				
					if ( compositeID > 0 ){
						par.setFlgMisc( false );
						par.setCompositeID( compositeID );
						
						NCAllergies nc = NCAllergies.readMedID( String.valueOf( compositeID ));
						if ( nc == null ){
							DialogHelpers.Messagebox( "Error looking up allergy id=" + compositeID );
							return;
						}
						
						par.setParDesc( drugName );
						
					} else {
						par.setFlgMisc( false );
						par.setParDesc( drugName );
						par.setCompositeID( 0 );
					}
					
					par.setSeverity( severity );
					par.setNoteTxt( notes );
					par.setCode( EditHelpers.parseInt( code ));
					
					par.postNew( ptRec );
					
					System.out.println( "Posting new allergy name=" + drugName + ", id=" + compositeID + ", severity=" + severity.getLabel());
					changes = true;
					
					// unset 'No Known Allergies' flag
					ParUtils.setNKDAFlag( ptRec, false );
				}
				}
			}
		}
		
		// notify that we have modified PARs
		if ( changes ) Notifier.notify( ptRec, Notifier.Event.PAR );
		return;
	}
	
	
	
	
	public void onClick$btnPrint(){
		
		//System.out.println( "onClick$btnPrint" );
		
		
		// Generate HTML report text
		
		StringBuilder sb = new StringBuilder( 1000 );
		
		sb.append( "<HTML>" );
		sb.append( "<HEAD>" );
		
		sb.append( "<style type='text/css'>" );
		//sb.append( "font-size:large" );
		//sb.append( "body{font-size:100%;}" );
		//sb.append( "h3 {font-size:200%;}" );
		//sb.append( "p {font-size:100%;}" );
		//sb.append( "*{font-size:40%;}" );

		sb.append( "</style>" );
		
		sb.append( "</HEAD>" );
		sb.append( "<BODY>" );
		sb.append( "<H2>PAL/MED<H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "<H3>" );
		sb.append( "<space>");
		
		DirPt dirpt = new DirPt( ptRec );
		
		sb.append( "<H3>Patient: " + dirpt.getName().getPrintableNameLFM());
		sb.append( "<H3>DOB: " + dirpt.getBirthdate().getPrintable(9));
		sb.append( "<space>" );
		sb.append( "<H3>Medications<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Start Date</td><td>Medication</td><td>SIG</td></tr>" );
		
		for( int i = 0; i < medsListbox.getItemCount(); ++i ){
			Listitem li = medsListbox.getItemAtIndex( i );
			Reca cmedReca = (Reca) li.getValue();
			Cmed cmed = new Cmed( cmedReca );
			sb.append( "<tr><td>" + cmed.getStartDate().getPrintable(9) + "</td><td>" + cmed.getDrugName() + "</td><td>" + MedSIG.getPrintableSIG(cmed) + "</td></tr>" );
		}
		
		sb.append( "</table>" );
		sb.append( "END REPORT" );

		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		sb.append( "</BODY>" );
		sb.append( "</HTML>" );
		
		String html = sb.toString();
		
		//System.out.println( html );

		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );
		if ( amedia == null ) System.out.println( "AMedia==null" );

		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( medsWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}


}

/**/

