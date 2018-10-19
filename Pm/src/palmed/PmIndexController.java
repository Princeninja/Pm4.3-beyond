/**
 * 
 */
package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import usrlib.ProgressBox;

/**
 * @author Owner
 *
 */


public class PmIndexController extends GenericForwardComposer {
	
	private Window pmIndexWin;
	private Textbox username;
	private Textbox password;
	private Label loggedIn;
	private Timer indexTimer;
	
	private ProgressBox progBox;
	
	private boolean isLoggedIn = false;
	

	public void doAfterCompose(){
		Events.echoEvent("onLater", pmIndexWin, null);	
	}
/*	public void onCreate$logWin(){
		try {
			logWin.doModal();
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
	// onLater event
	public void onLater$pmIndexWin( Event event ){
		
	}
	
	// onClick event from "logon" button
	public void onClick$logon( Event event ){
		
		pmUserLogon logon = new pmUserLogon( username.getValue(), password.getValue());
		if ( ! logon.doSearch()){
			alert( "Not Found");
		} else {
			
			// TODO - Get date and time of last login
			
			// TODO - Set date and time of new login
			
			// Set some session attributes to maintain 'logged in' status in other windows
			session.setAttribute( "user", username.getValue());
			isLoggedIn = true;
			
			
			// Display logged in message for a few seconds
			loggedIn.setValue( username.getValue() + " logged In.  Date: " + "<date>" + "  Time: " + "<time>" + "." );
			Events.echoEvent("onLater", pmIndexWin, null);		
			
			//Timer timer = new Timer(3000);
			//progBox = new ProgressBox( "Logging In..." );
			Clients.showBusy("Logging In");
			
			indexTimer.setDelay(4000);
			indexTimer.start();
		}
	}
	
	public void onTimer$indexTimer( Event event){
		if ( isLoggedIn == true ){
			//progBox.close();
			Clients.showBusy( null );
//			Events.echoEvent("onLater", pmIndexWin, null);
			

			
/*			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/	
			Executions.sendRedirect("main.zul");
		}

	}
	

	
	// onClick event from "exit" button
	public void onClick$exit( Event event ){
		alert( "Exit" );
		// close login window
		pmIndexWin.detach();
	}
		
}

