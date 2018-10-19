package palmed;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import palmed.Newcrop.StatusList;
import palmed.PmLogin.PmLogin;
import palmed.pmUser.Role;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.ZkTools;



public class ERxRefillsWinController extends GenericForwardComposer {

	private Listbox lbox;				// autowired
	private Radio rbActive;				// autowired
	private Radio rbInactive;			// autowired
	private Radio rbAll;				// autowired
	private Window erxWin;				// autowired
	private Listheader status;			// autowired
	
	private Button btnNewcrop; 
	
	Tabbox tabbox;
	Tabs tabs;
	Tabpanels tps;
	Tabpanel tpStatus;
	Tabpanel tpNCStatus;
	Radio rbProv;
	
	
	Rec provRec = null;
	pmUser.Role role = null;
	List<StatusList> list = null;
	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Rec supRec;
		
		
		// Get arguments from map
/*		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { ; ignore };
			}
		}		
*/		
		
		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Medical.PRESCRIPTIONS )){
			//System.out.println( "no Perms.Medical.PRESCRIPTIONS permission");
			SystemHelpers.msgAccessDenied();
			return;
		}

		
		
		// get user
		Rec userRec = Pm.getUserRec();
		if ( ! Rec.isValid( userRec )){
			SystemHelpers.msgAccessDenied();
			return;			
		}
		
		pmUser user = new pmUser( userRec );		
		role = user.getRole();
		

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
		

		
		// set radio button title to provider's abbr
		rbProv.setLabel( Prov.getAbbr( provRec ));
		
				
		// populate listbox
		refreshList( true, provRec );
		
		AuditLogger.recordEntry( AuditLog.Action.NEWCROP_STATUS, null, Pm.getUserRec(), null, null );
		
		btnNewcrop.setVisible(false);		
		return;
	}
	
	
	public void fillStatusListbox( Listbox lbox, List<Newcrop.StatusList> list, Rec provRec ){
		
		for ( int i = 0; i < list.size(); ++i ){
			
			Newcrop.StatusList sitem = list.get( i );
			
			// include this one in list?
			if (( provRec != null ) && ( ! provRec.equals( sitem.getProvRec()))) continue;
			
			
			Listitem litem = new Listitem();
			litem.setValue( sitem.getPtRec());
			
			Listcell lcell = new Listcell();
			Vbox vbox = new Vbox();
			Label lblPtName = new Label( sitem.getPtName());
			lblPtName.setParent( vbox );
			lblPtName.setStyle( "color:blue" );
			
			(new Label( "DOB: " + sitem.getPtDOB() + ", SSN:" + sitem.getPtSSN())).setParent( vbox );
			vbox.setParent( lcell );
			lcell.setParent( litem );
			
			new Listcell( sitem.getDate()).setParent( litem );
			new Listcell( sitem.getDrugInfo()).setParent( litem );
			new Listcell( Rec.isValid( sitem.getProvRec()) ? Prov.getAbbr( sitem.getProvRec()): "" ).setParent( litem );
			
			String status = sitem.getStatus();
			if ( status.equals( "P" )) status = "Pending";
			if ( status.equals( "C" )) status = "Complete";
			new Listcell( status ).setParent( litem );
			
			String subStatus = sitem.getSubStatus();
			String s = "";
			if ( subStatus.equals( "A")) s = "Staff Review";
			if ( subStatus.equals( "U" )) s = "Doctor Review";
			if ( subStatus.equals( "P" )) s = "Renewal Request";
			if ( subStatus.equals( "S" )) s = "In Process";
			if ( subStatus.equals( "O" )) s = "Outside Rx";
			if ( subStatus.equals( "D" )) s = "Drug Set";

			new Listcell( s ).setParent( litem );
			
			litem.setParent( lbox );
		}

	}
	
	
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$rbProv( Event ev ){
		refreshList( false, provRec );
	}
	public void onCheck$rbAllProv( Event ev ){
		refreshList( false, null );
	}
	public void onCheck$rbStaff( Event ev ){
		refreshList( false, null );
	}
	public void onCheck$rbAll( Event ev ){
		refreshList( false, null );
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList( boolean flgRead, Rec provRec ){
		
		// fill status listbox
		ZkTools.listboxClear( lbox );
		//if (( list == null ) || flgRead ) list = Newcrop.getStatusList();
		if ( list == null ) list = Newcrop.getStatusList();
		System.out.println("List size and content: "+list);
		
		if ( !(list == null)){
		fillStatusListbox( lbox, list, provRec ); }
				
		return;
	}
	
	
	
	
	// Handle button to process patient entry
	
	public void onClick$btnProcess( Event ev ){
		
		// is tab open?  only one patient tab at a time!  newcrop rules....
		if ( tabs.getFellowIfAny( "ptTab" ) != null ){
			DialogHelpers.Messagebox( "Patient tab already open.  Only one patient may be processed at a time.  (Newcrop rules.)  Finish and close the open tab prior to opening a new one." );
			((Tab) tabs.getFellow( "ptTab" )).setSelected( true );
			return;
		}
		
		// was an item selected?
		if ( lbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No patient selected." );
			return;
		}
		
		
		Rec ptRec = (Rec) ZkTools.getListboxSelectionValue( lbox );
		if ( ! Rec.isValid( ptRec )) return;
		
		DirPt dirPt = new DirPt( ptRec );
		String ptName = dirPt.getName().getPrintableNameLFM();
		
		
		Tab tab = new Tab( ptName );
		tab.setId( "ptTab" );
		tab.setParent( tabs );
		tab.setClosable( true );
		tab.setSelected( true );
		
		Tabpanel tp = new Tabpanel();
		tp.setParent( tps );
		
		
		
		
		// unique tag (key) to store/retrieve XML to/from Session
		Random random = new Random();
		String xmlTag = String.format( "tag%04d", random.nextInt( 10000 ));
		
				
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
		String xmlText = newcrop.toXMLString();
		//System.out.println(xmlText);

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_NEWRX, ptRec, Pm.getUserRec(), null, null );
		
			  
		// call newcrop window
		Newcrop.callNewcrop( xmlTag, xmlText, tp, null );

		return;
	}
	
	
	public void nursev(){
		
		   final Rec userRec = new Rec ( PmLogin.show(erxWin,"true"));
		   
		   System.out.println("new user rec2 is: "+userRec);
			
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
				
				if (!(btnNewcrop == null)){
					btnNewcrop.setVisible(true);}
								
						
			}else { 
							
			System.out.println("postha2");
				
		final Window HandP = new Window();	
		
		HandP.setParent(erxWin);
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
										
						btnNewcrop.setVisible(true);
																    
						HandP.detach();
						
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
	
		
	//choose roles for newcrop status
	public void onClick$btnProv ( Event ev ){
		
		nursev();
		
	}
	
	// Handle button to open newcrop status tab	
	public void onClick$btnNewcrop( Event ev ){ 
		
		/*int z = 1;
		while ( true )  {
			
			z ++;
			
			if ( !(Param[0] == null) ) { break; } 
		}*/
		
		// is tab open?  only one patient tab at a time!  newcrop rules....
		if ( tabs.getFellowIfAny( "ptTab" ) != null ){
			DialogHelpers.Messagebox( "Patient tab already open.  Only one patient may be processed at a time.  (Newcrop rules.)  Finish and close the open tab prior to opening a new one." );
			((Tab) tabs.getFellow( "ptTab" )).setSelected( true );
			return;
		}
		
		if ( tabs.getFellowIfAny( "tabNCStatus" ) != null ){
			((Tab) tabs.getFellowIfAny( "tabNCStatus" )).setSelected( true );
			return;
		}
		
		
		
		Tab tab = new Tab( "Newcrop Status" );
		tab.setId( "tabNCStatus" );
		tab.setParent( tabs );
		tab.setClosable( true );
		tab.setSelected( true );
		
		Tabpanel tp = new Tabpanel();
		tp.setParent( tps );
		
		
		
		// unique tag (key) to store/retrieve XML to/from Session
		Random random = new Random();
		String xmlTag = String.format( "tag%04d", random.nextInt( 10000 ));
		
		
		
	
		// Build XML
		Newcrop newcrop = new Newcrop();		
		newcrop.setXMLHeader();
		newcrop.setCredentials();
		newcrop.setUserRole( Newcrop.getUserType( role ), Newcrop.getRoleString( role ));
		newcrop.setDestination( "status" );
		newcrop.setAccount();
		newcrop.setLocation();
		newcrop.setProvider( provRec );
		String xmlText = newcrop.toXMLString();
		//System.out.println(xmlText);

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.NEWCROP_STATUS, null, Pm.getUserRec(), null, null );
		
			  
		// call newcrop window
		Newcrop.callNewcrop( xmlTag, xmlText, tp, null );
		btnNewcrop.setVisible(false);
		return;
		
		
	}
	
	
	
	// Open dialog 
	
	public void onClick$btnPrint( Event ev ) throws InterruptedException{
	
		// was an item selected?
		//if ( userListbox.getSelectedCount() < 1 ){
		//	try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
		//	return;
		//}

		// get selected item's rec
		//Rec rec = (Rec) userListbox.getSelectedItem().getValue();
		//if (( rec == null ) || ( rec.getRec() < 2 )) return;

		// call edit routine
		//ProbRes.remove( reca, ptRec, probsWin );			
		// log the access
		//AuditLogger.recordEntry( AuditLog.Action.USER_PRINT, null, Pm.getUserRec(), null, null );
		
		//refreshList();	
		return;
	}
	
	
}
