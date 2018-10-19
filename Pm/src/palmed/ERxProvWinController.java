package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;

public class ERxProvWinController extends GenericForwardComposer {
	
	private Window eRxProvWin;		// autowired - medications window
	
	Tabpanel tabpanel;		// tab panel this window is in

	private Listbox lbProvider;			// autowired - provide selected
	private Button btnQuery;			// autowired - query button
//	private Button pending;		// autowired - process pending requests
	
	private Textbox txtResult;			// autowired
	
	
	private Button save;				// autowired
	private Button cancel;				// autowired
	
	
	private boolean demo = false;		// flag to edit demo data set instead of live
	

	
	
	

	public ERxProvWinController() {
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
		
/*		
		
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
		
		
*/

		
		System.out.println( "ERxProvWinController()" );
		
		// initialize some session type things
		palmed.SystemHelpers.initApp();
		
		palmed.Pm.setMedPath( palmed.PmConfig.getMedPath());
		palmed.Pm.setOvdPath( palmed.PmConfig.getOvdPath());
		palmed.Pm.setSchPath( palmed.PmConfig.getSchPath());
		
		// set profile listbox to 'Live' or 'Demo' data (demo = false)
		demo = Pm.getERxDemo();
		
		refresh();
		return;
	}
	
	
	
	
	
	public void refresh(){
		

		
		// Read config info
		ERxConfig eRxConfig = new ERxConfig();
		eRxConfig.read( demo );	
			
		
		txtResult.setValue( "" );
		
		
		Prov.fillListbox( lbProvider, true );
		


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
		//eRxConfig.setPartnerName( partnerName.getValue());
		//eRxConfig.setPartnerID( partnerID.getValue());
		//eRxConfig.setPartnerUsername( partnerUsername.getValue());
		//eRxConfig.setPartnerPassword( partnerPassword.getValue());
		//eRxConfig.setPartnerProduct( partnerProduct.getValue());
		//eRxConfig.setPartnerVersion( partnerVersion.getValue());
		
		

	

		
		// Save (write) records back to database
		eRxConfig.save( demo );
		System.out.println( "saved." );		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.ERX_CONFIG, null, Pm.getUserRec(), null, null );
		
		return;
	}

	
	
	
	
	
	
	
	public void onClick$btnQuery( Event e ) throws InterruptedException{

		// get selected provier
		if ( lbProvider.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No Provider Selected" );
			return;
		}
		
		Rec provRec = (Rec) EditHelpers.getListboxSelection( lbProvider );
		if ( ! provRec.isValid()) SystemHelpers.seriousError( "Invalid provider rec.");
		
		
		Newcrop.getProviderStatus( provRec );
		
		return;
	}
	
	
	
	public void onClick$cancel( Event e ) throws InterruptedException{		
		closeWin();
		return;
	}

	
	private void closeWin(){
		
		// close editPtWin
		eRxProvWin.detach();

		// close tab and tabpanel
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().detach();
			tabpanel.detach();
		}

		return;
	}
	

	
	


}
