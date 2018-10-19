package palmed;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Address;
import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;

public class ERxConfigWinController extends GenericForwardComposer {

	private Window eRxConfigWin;		// autowired - medications window
	
	Tabpanel tabpanel;		// tab panel this window is in

	private Listbox profile;			// autowired - select live vs demo
//	private Button newrx;		// autowired - new prescription button
//	private Button pending;		// autowired - process pending requests
	
	private Textbox partnerName;		// autowired
	private Textbox partnerUsername;	// autowired
	private Textbox partnerPassword;	// autowired
	private Textbox partnerProduct;		// autowired
	private Textbox partnerVersion;		// autowired
	
	private Textbox clickThru;			// autowired
	private Textbox webServices;		// autowired
	
	private Textbox accountName;		// autowired
	private Textbox accountID;			// autowired
	private Textbox siteID;				// autowired
	private Textbox accountStreet;		// autowired
	private Textbox accountLine2;		// autowired
	private Textbox accountCity;		// autowired
	private Textbox accountState;		// autowired
	private Textbox accountZip;			// autowired
	private Textbox accountCountry;		// autowired
	private Textbox accountPhone;		// autowired
	private Textbox accountFax;			// autowired
	private Textbox accountEmail;		// autowired
	
	private Textbox locationName;		// autowired
	private Textbox locationID;			// autowired
	private Textbox locationStreet;		// autowired
	private Textbox locationLine2;		// autowired
	private Textbox locationCity;		// autowired
	private Textbox locationState;		// autowired
	private Textbox locationZip;		// autowired
	private Textbox locationCountry;	// autowired
	private Textbox locationPhone;		// autowired
	private Textbox locationFax;		// autowired
	private Textbox locationEmail;		// autowired
	private Textbox locationContact;	// autowired
	
	private Button save;				// autowired
	private Button cancel;				// autowired
	
	private Checkbox newcropManagedMeds;	// autowired
	private Checkbox newcropManagedPARs;	// autowired
	private Checkbox sendMeds;			// autowired
	private Checkbox sendPARs;			// autowired
	private Checkbox sendProbs;			// autowired
	private Checkbox cbPoll;			// autowired
	
	private boolean demo = false;		// flag to edit demo data set instead of live
	

	
	
	

	public ERxConfigWinController() {
		super();
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
		
		
		
		// Is window in a Tabpanel???
		// Get Tabpanel this window is placed in.
		// May have to go several layers deep due to enclosing window.
		if ( component instanceof Tabpanel ){
			tabpanel = (Tabpanel) component;
		} else if ( component.getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent();
		} else if ( component.getParent().getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent().getParent();
		}


		
		// Add close tabpanel event listener
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().addEventListener( "onClose", new EventListener(){ 
				public void onEvent( Event ev ) throws Exception { ev.stopPropagation(); onClick$cancel( ev ); }});
		}
		
		


		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

//		if ( exec != null ){
//			Map myMap = exec.getArg();
//			if ( myMap != null ){
//				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
//			}
//		}
		
		System.out.println( "ERxConfigWinController()" );
		
		
		// set profile listbox to 'Live' or 'Demo' data (demo = false)
		demo = Pm.getERxDemo();
		profile.setSelectedIndex( demo ? 1: 0 );
		
		refresh();
		return;
	}
	
	
	
	
	
	public void refresh(){
		

		
		// Read config info
		ERxConfig eRxConfig = new ERxConfig();
		eRxConfig.read( demo );	
			
		
		// Load Partner data into ERxConfig			
		partnerName.setValue( eRxConfig.getPartnerName());
		//partnerID.setValue( eRxConfig.getPartnerID());
		partnerUsername.setValue( eRxConfig.getPartnerUsername());
		partnerPassword.setValue( eRxConfig.getPartnerPassword());
		partnerProduct.setValue( eRxConfig.getPartnerProduct());
		partnerVersion.setValue( eRxConfig.getPartnerVersion());
		
		
		// Load Account data into ERxConfig			
		accountName.setValue( eRxConfig.getAccountName());
		accountID.setValue( eRxConfig.getAccountID());
		siteID.setValue( eRxConfig.getSiteID());
		accountStreet.setValue( eRxConfig.getAccountStreet());
		accountLine2.setValue( eRxConfig.getAccountLine2());
		accountCity.setValue( eRxConfig.getAccountCity());
		accountState.setValue( eRxConfig.getAccountState());
		accountZip.setValue( eRxConfig.getAccountZip());
		accountCountry.setValue( eRxConfig.getAccountCountry());
		accountPhone.setValue( eRxConfig.getAccountPhone());
		accountFax.setValue( eRxConfig.getAccountFax());
		accountEmail.setValue( eRxConfig.getAccountEmail());

	
		// Load Location data into ERxConfig			
		locationName.setValue( eRxConfig.getLocationName());
		locationID.setValue( eRxConfig.getLocationID());
		siteID.setValue( eRxConfig.getSiteID());
		locationStreet.setValue( eRxConfig.getLocationStreet());
		locationLine2.setValue( eRxConfig.getLocationLine2());
		locationCity.setValue( eRxConfig.getLocationCity());
		locationState.setValue( eRxConfig.getLocationState());
		locationZip.setValue( eRxConfig.getLocationZip());
		locationCountry.setValue( eRxConfig.getLocationCountry());
		locationPhone.setValue( eRxConfig.getLocationPhone());
		locationFax.setValue( eRxConfig.getLocationFax());
		locationEmail.setValue( eRxConfig.getLocationEmail());
		locationContact.setValue( eRxConfig.getLocationContact());


		// Load other config data
		clickThru.setValue( eRxConfig.getClickThru());
		webServices.setValue( eRxConfig.getWebServices());

		// Checkboxes
		newcropManagedMeds.setChecked( eRxConfig.getNewcropManagedMeds());
		newcropManagedPARs.setChecked( eRxConfig.getNewcropManagedPARs());
		sendMeds.setChecked( eRxConfig.getSendMeds());
		sendPARs.setChecked( eRxConfig.getSendPARs());
		sendProbs.setChecked( eRxConfig.getSendProbs());
		cbPoll.setChecked( eRxConfig.getPollStatus());
		
		


		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		Prov	prov;		// proivider info
		
		
		System.out.println( "Saving..." );
		
			
			
		System.out.println( "ERxConfigWinController().save() " );


	
		// Read provider info
		ERxConfig eRxConfig = new ERxConfig();
			
			
		
		// Load Partner data into ERxConfig			
		eRxConfig.setPartnerName( partnerName.getValue());
		//eRxConfig.setPartnerID( partnerID.getValue());
		eRxConfig.setPartnerUsername( partnerUsername.getValue());
		eRxConfig.setPartnerPassword( partnerPassword.getValue());
		eRxConfig.setPartnerProduct( partnerProduct.getValue());
		eRxConfig.setPartnerVersion( partnerVersion.getValue());
		
		
		// Load Account data into ERxConfig			
		eRxConfig.setAccountName( accountName.getValue());
		eRxConfig.setAccountID( accountID.getValue());
		eRxConfig.setSiteID( siteID.getValue());
		eRxConfig.setAccountStreet( accountStreet.getValue());
		eRxConfig.setAccountLine2( accountLine2.getValue());
		eRxConfig.setAccountCity( accountCity.getValue());
		eRxConfig.setAccountState( accountState.getValue());
		eRxConfig.setAccountZip( accountZip.getValue());
		eRxConfig.setAccountCountry( accountCountry.getValue());
		eRxConfig.setAccountPhone( accountPhone.getValue());
		eRxConfig.setAccountFax( accountFax.getValue());
		eRxConfig.setAccountEmail( accountEmail.getValue());

	
		// Load Location data into ERxConfig			
		eRxConfig.setLocationName( locationName.getValue());
		eRxConfig.setLocationID( locationID.getValue());
		eRxConfig.setSiteID( siteID.getValue());
		eRxConfig.setLocationStreet( locationStreet.getValue());
		eRxConfig.setLocationLine2( locationLine2.getValue());
		eRxConfig.setLocationCity( locationCity.getValue());
		eRxConfig.setLocationState( locationState.getValue());
		eRxConfig.setLocationZip( locationZip.getValue());
		eRxConfig.setLocationCountry( locationCountry.getValue());
		eRxConfig.setLocationPhone( locationPhone.getValue());
		eRxConfig.setLocationFax( locationFax.getValue());
		eRxConfig.setLocationEmail( locationEmail.getValue());
		eRxConfig.setLocationContact( locationContact.getValue());


		// Load other config data
		eRxConfig.setClickThru( clickThru.getValue());
		eRxConfig.setWebServices( webServices.getValue());

		// Checkboxes
		eRxConfig.setNewcropManagedMeds( newcropManagedMeds.isChecked( ));
		eRxConfig.setNewcropManagedPARs( newcropManagedPARs.isChecked( ));
		eRxConfig.setSendMeds( sendMeds.isChecked( ));
		eRxConfig.setSendPARs( sendPARs.isChecked( ));
		eRxConfig.setSendProbs( sendProbs.isChecked( ));
		eRxConfig.setPollStatus( cbPoll.isChecked( ));

		

		
		// Save (write) records back to database
		eRxConfig.save( demo );
		System.out.println( "saved." );		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.ERX_CONFIG, null, Pm.getUserRec(), null, null );
		
		return;
	}

	
	
	// handle demo vs live listbox selection
	public void onSelect$profile( Event e ){
		demo = profile.getSelectedItem().getValue().equals( "true" ) ? true: false;
		refresh();
		return;
	}
	
	
	
	
	
	public void onClick$save( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			save();
			//progBox.close();
			closeWin();
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$cancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			closeWin();
		}
		
		return;
	}

	
	private void closeWin(){
		
		// close editPtWin
		eRxConfigWin.detach();

		// close tab and tabpanel
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().detach();
			tabpanel.detach();
		}

		return;
	}
	

	
	


}

/**/


