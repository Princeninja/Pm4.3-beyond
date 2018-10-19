/**
 * 
 */
package palmed;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.theme.Themes;
import org.zkoss.zul.Window;

import palmed.PmLogin.PmLogin;

/**
 * @author Owner
 *
 */
public class PmAppController extends GenericForwardComposer {

	private Window pmAppWin;			// application window
	private boolean isLoggedIn = false;	// flag application logged in
	
		
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//PmLogin.show();
		
		//Window pmLoginWin = (Window) Executions.createComponents("login.zul", self, null);

		//Executions.wait(pmLoginWin );
		
		//alert( "Testing" );
		//Executions.sendRedirect("main.zul");
		System.out.println( "in doAfterCompose()" );

		Events.echoEvent("onLater", pmAppWin, "test");	
		System.out.println( "called postEvent(onLater, pmAppWin)" );
		
		
		
		//Library.setProperty("org.zkoss.theme.preferred", "silvertail");
		//Executions.getCurrent().sendRedirect(null);
		
		
		
		// initialize some session type things
		SystemHelpers.initApp();
		
		Pm.setMedPath( PmConfig.getMedPath());
		Pm.setOvdPath( PmConfig.getOvdPath());
		Pm.setSchPath( PmConfig.getSchPath());
		
		// set up Notifier object
		Notifier.getInstance();
		
		// set up AuditLogger object
		AuditLogger.start();
		
		// set ERxDemo flag as "true" or "false"
		Pm.setERxDemo();
		

	}
	
	
	
	public void onOpen$pmAppWin( Event evt ){
		System.out.println( "called onOpen$pmAppWin()" );

		//alert( "TESTING!");
	}

	// onLater event
	public void onLater$pmAppWin( Event event ){
		System.out.println( "called onLater$pmAppWin" );
		
		if ( ! isLoggedIn ){

			System.out.println( "calling pmLogin.show()" );
			
			PmLogin.show(pmAppWin);
			isLoggedIn = true;
			System.out.println( "back from pmLogin.show()" );
			
			Events.echoEvent("onLater", pmAppWin, "test");	
			System.out.println( "called postEvent(onLater, pmAppWin)" );

		} else {
			
			Component	parent;
			
			// Detach Splash Screen 'pmAppWin'
			parent = pmAppWin.getParent();
			pmAppWin.detach();
			//pmAppWin.setVisible( false );

			// Call 'PmMain' to really start application
			System.out.println( "calling main.zul " );
			//Executions.sendRedirect("main.zul");
			PmMain.show( parent );
			System.out.println( "back from main.zul" );

		}
	}

	
}
