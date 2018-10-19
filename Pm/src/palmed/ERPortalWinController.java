package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

import palmed.PmLogin.PmLogin;

public class ERPortalWinController extends GenericForwardComposer {

	private Window ERPortalWin;		// autowired
	
	private boolean isLoggedIn = false;
	

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		

		
		
		// initialize some session type things
		SystemHelpers.initApp();
		Pm.setMedPath( PmConfig.getMedPath());
		Pm.setOvdPath( PmConfig.getOvdPath());
		Pm.setSchPath( PmConfig.getSchPath());
		
		
		// set up AuditLogger object
		AuditLogger.start();
		
		
		// echo onLater event to trigger login window
		Events.echoEvent( "onLater", ERPortalWin, "login" );	

	}
	
	
	// onLater event
	public void onLater$ERPortalWin( Event event ){
		
		if ( ! isLoggedIn ){

			PmLogin.show( ERPortalWin );
			isLoggedIn = true;
			
			//Events.echoEvent("onLater", pmAppWin, "test");	
			//System.out.println( "called postEvent(onLater, pmAppWin)" );
		}
		
		
		// detach this app window (could just hide it I guess)
		ERPortalWin.detach();
		
		// open ERSearch window
		Executions.createComponents( "ersearch.zul", null, null );

		return;
	}

}
