package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.Rec;
import usrlib.ZkTools;

public class UserEditWinController extends GenericForwardComposer {

	Rec userRec = null;			// user record
	EditPt.Operation operation = null;	// operation to perform
	boolean flgPerms = false;
	String savUsername = null;		// saved username
	
	Window userEditWin;	// autowired - window	
	
	
	Label lblUserName;			// autowired
	
	Textbox txtLogin;			// autowired
	Textbox txtPassword1;		// autowired
	Textbox txtPassword2;		// autowired
	Textbox txtFirst;			// autowired
	Textbox	txtMiddle;			// autowired
	Textbox txtLast;			// autowired
	Textbox txtSuffix;			// autowired
	
	Listbox provListbox;		// autowired
	
	Radio	rbActive;	// autowired
	Radio	rbInactive;	// autowired
	
	Radio rbClerical;
	Radio rbManager;
	Radio rbAdmin;
	Radio rbNurse;
	Radio rbMidlevel;
	Radio rbPhysician;
	Radio rbEmergency;
	
	Checkbox cbMedView;
	Checkbox cbMedEdit;
	Checkbox cbMedEnter;
	Checkbox cbMedRx;
	Checkbox cbMedSoap;
	Checkbox cbMedRpt;
	Checkbox cbMedDB;
	Checkbox cbFinView;
	Checkbox cbFinPost;
	Checkbox cbFinCor;
	Checkbox cbFinDly;
	Checkbox cbFinRep;
	Checkbox cbUser;
	Checkbox cbProblem;
	Checkbox cbMaint;
	Checkbox cbBackup;
	Checkbox cbRemote;

		
	
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
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (EditPt.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ userRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
				try{ flgPerms = (Boolean) myMap.get( "flgPerms" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "UserEditWinController() operation==null" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		

		// fill provider listbox
		Prov.fillListbox( provListbox, true );
		
		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			lblUserName.setValue( "New User" );

			// Create a new user
			rbActive.setChecked( true );
			savUsername = "";
			

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read user info 
			if (( userRec == null ) || ( userRec.getRec() < 2 )) SystemHelpers.seriousError( "UserEditWinController() bad rec" );
			pmUser user = new pmUser( userRec );
				

			lblUserName.setValue( user.getName().getPrintableNameLFM());

			
			// Get user info from data struct
			txtLogin.setValue( user.getUser());
			txtPassword1.setValue( user.getPassword());
			txtPassword2.setValue( user.getPassword());
			
			txtFirst.setValue( user.getName().getFirstName());
			txtMiddle.setValue( user.getName().getMiddleName());
			txtLast.setValue( user.getName().getLastName());
			txtSuffix.setValue( user.getName().getSuffix());
			
			
			// save username to test against changes later
			savUsername = user.getUser();
			
			if ( flgPerms ){
				
				// active/inactive radiobuttons
				int type = user.isValid() ? 'A': 'I';
				if ( type == 'A' ) rbActive.setChecked( true );
				if ( type == 'I' ) rbInactive.setChecked( true );
	
				// select provider 
				Rec provRec = user.getProvRec();
				if ( Rec.isValid( provRec )){
					ZkTools.setListboxSelectionByValue( provListbox, provRec );
				}
				

			
				// set user role radiobutton
				System.out.println( "UserEditWinController.doAfterCompose() setting user role" );
				switch ( user.getRole()){
				
				case NURSE:
					rbNurse.setChecked(true);
					break;
				case MIDLEVEL:
					rbMidlevel.setChecked(true);
					break;
				case PHYSICIAN:
					rbPhysician.setChecked(true);
					break;
				case CLERICAL:
					rbClerical.setChecked(true);
					break;
				case MANAGER:
					rbManager.setChecked(true);
					break;
				case ADMINISTRATOR:
					rbAdmin.setChecked(true);
					break;
				case EMERGENCY:
					rbEmergency.setChecked(true);
					break;
				}
				
				
				
				// set permissions
				int medPerms = user.getMedPerms();
				if (( medPerms & Perms.Medical.VIEW.getCode()) != 0 ) cbMedView.setChecked( true );
				if (( medPerms & Perms.Medical.UPDATE.getCode()) != 0 ) cbMedEnter.setChecked( true );
				if (( medPerms & Perms.Medical.EDIT.getCode()) != 0 ) cbMedEdit.setChecked( true );
				if (( medPerms & Perms.Medical.PRESCRIPTIONS.getCode()) != 0 ) cbMedRx.setChecked( true );
				if (( medPerms & Perms.Medical.TRANSCRIPTION.getCode()) != 0 ) cbMedSoap.setChecked( true );
				if (( medPerms & Perms.Medical.PATIENT_REPORTS.getCode()) != 0 ) cbMedRpt.setChecked( true );
				if (( medPerms & Perms.Medical.DATABASE_SEARCH.getCode()) != 0 ) cbMedDB.setChecked( true );
				
				int finPerms = user.getFinPerms();
				if (( finPerms & Perms.Financial.VIEW.getCode()) != 0 ) cbFinView.setChecked( true );
				if (( finPerms & Perms.Financial.POST.getCode()) != 0 ) cbFinPost.setChecked( true );
				if (( finPerms & Perms.Financial.CORRECTIONS.getCode()) != 0 ) cbFinCor.setChecked( true );
				if (( finPerms & Perms.Financial.DAILY_REPORTS.getCode()) != 0 ) cbFinDly.setChecked( true );
				if (( finPerms & Perms.Financial.PRACTICE_REPORTS.getCode()) != 0 ) cbFinRep.setChecked( true );

				int sysPerms = user.getSysPerms();
				if (( sysPerms & Perms.Sys.USER_MANAGEMENT.getCode()) != 0 ) cbUser.setChecked( true );
				if (( sysPerms & Perms.Sys.PROBLEM_SOLVERS.getCode()) != 0 ) cbProblem.setChecked( true );
				if (( sysPerms & Perms.Sys.MAINTENANCE.getCode()) != 0 ) cbMaint.setChecked( true );
				if (( sysPerms & Perms.Sys.BACKUP.getCode()) != 0 ) cbBackup.setChecked( true );
				if (( sysPerms & Perms.Sys.REMOTE_ACCESS.getCode()) != 0 ) cbRemote.setChecked( true );

			}
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	public boolean save(){
		
		pmUser user;		// prob info
		
		
		// TODO - VALIDATE DATA
		
		String username = txtLogin.getValue().trim().toUpperCase();		
		if ( username.length() < 3 ){
			DialogHelpers.Messagebox( "User logon name must be at least 3 characters long." );
			return false;			
		}
		
		String first = txtFirst.getValue().trim();		
		String middle = txtMiddle.getValue().trim();		
		String last = txtLast.getValue().trim();		
		String suffix = txtSuffix.getValue().trim();	
		
		if (( first.length() < 2 ) || ( last.length() < 2 )){
			DialogHelpers.Messagebox( "User first and last names must be at least 3 characters long each." );
			return false;			
		}
		
		String password = txtPassword1.getValue().trim();		
		if ( password.length() < 5 ){
			DialogHelpers.Messagebox( "Password too short. Must be at least 5 characters long." );
			return false;			
		}
		
		if ( ! password.equals( txtPassword2.getValue().trim())){
			DialogHelpers.Messagebox( "Passwords do not match." );
			return false;			
		}
		
		
		
		// verify unique user name
		
		if ( ! username.equals( savUsername )){
			
			if ( pmUser.search( username ) != null ){
				DialogHelpers.Messagebox( "This logon name is already taken.  Please choose another." );
				return false;			
			}
		}
		
		
		
		
		System.out.println( "Saving..." );
		
		if ( operation == EditPt.Operation.NEWPT ){

			// create a new user record
			System.out.println( "Creating new user record..." );
			user = new pmUser();

			// set validity byte - by default
			user.setValid();
			//user.setStatus( 'C' );
			

		

			
		} else { 
			
			
			// make sure we have a valid rec
			if (( userRec == null ) || ( userRec.getRec() < 2)) SystemHelpers.seriousError( "UserEditWinController.save() bad rec" );
	
			// Read user
			user = new pmUser( userRec );
			// validity/status byte should already be set
			//user.setStatus( 'C' );

		}
	
		
		
		
		// Load data into user dataBuffer
		user.setUser( username );
		user.setPassword( password );
		user.setName( new Name( first, middle, last, suffix ));
		
		
		if ( flgPerms ){
				
			// set valid/active vs hidden/inactive user
			if ( rbActive.isChecked()) user.setValid();
			if ( rbInactive.isChecked()) user.setHidden();

			// get provider rec
			Rec provRec = (Rec) ZkTools.getListboxSelectionValue( provListbox );
			user.setProvRec( provRec );
			
			// set user role
			System.out.println( "UserEditWinController.save() setting user role" );
			user.setRole( pmUser.Role.UNSPECIFIED );
			if ( rbNurse.isChecked()) user.setRole( pmUser.Role.NURSE );
			if ( rbMidlevel.isChecked()) user.setRole( pmUser.Role.MIDLEVEL );
			if ( rbPhysician.isChecked()) user.setRole( pmUser.Role.PHYSICIAN );
			if ( rbClerical.isChecked()) user.setRole( pmUser.Role.CLERICAL );
			if ( rbManager.isChecked()) user.setRole( pmUser.Role.MANAGER );
			if ( rbAdmin.isChecked()) user.setRole( pmUser.Role.ADMINISTRATOR );
			if ( rbEmergency.isChecked()) user.setRole( pmUser.Role.EMERGENCY );

			
			
			// set user permissions
			
			int medPerms = 0;
			if ( cbMedView.isChecked()) medPerms = medPerms | Perms.Medical.VIEW.getCode();
			if ( cbMedEnter.isChecked()) medPerms = medPerms | Perms.Medical.UPDATE.getCode();
			if ( cbMedEdit.isChecked()) medPerms = medPerms | Perms.Medical.EDIT.getCode();
			if ( cbMedRx.isChecked()) medPerms = medPerms | Perms.Medical.PRESCRIPTIONS.getCode();
			if ( cbMedSoap.isChecked()) medPerms = medPerms | Perms.Medical.TRANSCRIPTION.getCode();
			if ( cbMedRpt.isChecked()) medPerms = medPerms | Perms.Medical.PATIENT_REPORTS.getCode();
			if ( cbMedDB.isChecked()) medPerms = medPerms | Perms.Medical.DATABASE_SEARCH.getCode();
			user.setMedPerms( medPerms );

			int finPerms = 0;
			if ( cbFinView.isChecked()) finPerms = finPerms | Perms.Financial.VIEW.getCode();
			if ( cbFinPost.isChecked()) finPerms = finPerms | Perms.Financial.POST.getCode();
			if ( cbFinCor.isChecked()) finPerms = finPerms | Perms.Financial.CORRECTIONS.getCode();
			if ( cbFinDly.isChecked()) finPerms = finPerms | Perms.Financial.DAILY_REPORTS.getCode();
			if ( cbFinRep.isChecked()) finPerms = finPerms | Perms.Financial.PRACTICE_REPORTS.getCode();
			user.setFinPerms( finPerms );
			
			int sysPerms = 0;
			if ( cbUser.isChecked()) sysPerms = sysPerms | Perms.Sys.USER_MANAGEMENT.getCode();
			if ( cbProblem.isChecked()) sysPerms = sysPerms | Perms.Sys.PROBLEM_SOLVERS.getCode();
			if ( cbMaint.isChecked()) sysPerms = sysPerms | Perms.Sys.MAINTENANCE.getCode();
			if ( cbBackup.isChecked()) sysPerms = sysPerms | Perms.Sys.BACKUP.getCode();
			if ( cbRemote.isChecked()) sysPerms = sysPerms | Perms.Sys.REMOTE_ACCESS.getCode();
			user.setSysPerms( sysPerms );
			
		}
		
		
		
		
		//prob.dump();	

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.NEWPT ){

			// new user
			user.newUser();
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.USER_ADD, null, Pm.getUserRec(), userRec, user.getUser());
			
			
		} else {	// EditPt.Operation.EDITPT
			
			
			// existing user
			user.write( userRec );
			System.out.println( "edited user info written, userRec=" + userRec.toString());

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.USER_EDIT, null, Pm.getUserRec(), userRec, user.getUser());
			
}
		
		return true;
	}

	
	
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			if ( save())
			//progBox.close();
				closeWin();
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$btnCancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			closeWin();
		}
		
		return;
	}

	
	private void closeWin(){
		
		// close Win
		userEditWin.detach();
		return;
	}
	
	
	
	

	
	// Open dialog to enter new vitals data
	
	public void onClick$btnApply( Event ev ){
		
		
		//TODO - read these permissions from an XML file
		
		if ( rbClerical.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( false );
			cbMedRx.setChecked( false );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( false );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( true );
			cbFinCor.setChecked( false );
			cbFinDly.setChecked( false );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( false );
			cbMaint.setChecked( false );
			cbBackup.setChecked( false );
			cbRemote.setChecked( false );			
			
		} else if ( rbManager.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( false );
			cbMedRx.setChecked( false );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( false );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( true );
			cbFinCor.setChecked( true );
			cbFinDly.setChecked( true );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( true );
			cbMaint.setChecked( true );
			cbBackup.setChecked( true );
			cbRemote.setChecked( false );			
			
		} else if ( rbAdmin.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( true );
			cbMedRx.setChecked( true );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( true );
			cbMedDB.setChecked( true );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( true );
			cbFinCor.setChecked( true );
			cbFinDly.setChecked( true );
			cbFinRep.setChecked( true );
			cbUser.setChecked( true );
			cbProblem.setChecked( true );
			cbMaint.setChecked( true );
			cbBackup.setChecked( true );
			cbRemote.setChecked( false );			
			
		} else if ( rbNurse.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( true );
			cbMedRx.setChecked( true );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( true );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( false );
			cbFinCor.setChecked( false );
			cbFinDly.setChecked( false );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( false );
			cbMaint.setChecked( false );
			cbBackup.setChecked( false );
			cbRemote.setChecked( false );			
			
		} else if ( rbMidlevel.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( true );
			cbMedRx.setChecked( true );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( true );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( false );
			cbFinCor.setChecked( false );
			cbFinDly.setChecked( false );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( false );
			cbMaint.setChecked( false );
			cbBackup.setChecked( false );
			cbRemote.setChecked( false );			
			
		} else if ( rbPhysician.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( true );
			cbMedEdit.setChecked( true );
			cbMedRx.setChecked( true );
			cbMedSoap.setChecked( true );
			cbMedRpt.setChecked( true );
			cbMedDB.setChecked( true );
			cbFinView.setChecked( true );
			cbFinPost.setChecked( true );
			cbFinCor.setChecked( true );
			cbFinDly.setChecked( true );
			cbFinRep.setChecked( true );
			cbUser.setChecked( true );
			cbProblem.setChecked( true );
			cbMaint.setChecked( true );
			cbBackup.setChecked( true );
			cbRemote.setChecked( false );			
			
		} else if ( rbEmergency.isChecked()){
			
			cbMedView.setChecked( true );
			cbMedEnter.setChecked( false );
			cbMedEdit.setChecked( false );
			cbMedRx.setChecked( false );
			cbMedSoap.setChecked( false );
			cbMedRpt.setChecked( false );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( false );
			cbFinPost.setChecked( false );
			cbFinCor.setChecked( false );
			cbFinDly.setChecked( false );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( false );
			cbMaint.setChecked( false );
			cbBackup.setChecked( false );
			cbRemote.setChecked( true );			
			
		} else {
			
			cbMedView.setChecked( false );
			cbMedEnter.setChecked( false );
			cbMedEdit.setChecked( false );
			cbMedRx.setChecked( false );
			cbMedSoap.setChecked( false );
			cbMedRpt.setChecked( false );
			cbMedDB.setChecked( false );
			cbFinView.setChecked( false );
			cbFinPost.setChecked( false );
			cbFinCor.setChecked( false );
			cbFinDly.setChecked( false );
			cbFinRep.setChecked( false );
			cbUser.setChecked( false );
			cbProblem.setChecked( false );
			cbMaint.setChecked( false );
			cbBackup.setChecked( false );			
			cbRemote.setChecked( false );			
		}

		return;
	}
	
	
	
	

}
