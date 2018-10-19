package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class UserWinController extends GenericForwardComposer {

	private Listbox userListbox;		// autowired
	private Radio rbActive;				// autowired
	private Radio rbInactive;			// autowired
	private Radio rbAll;				// autowired
	private Window userWin;				// autowired
	private Listheader status;			// autowired
	
	
	

	public UserWinController() {
		// TODO Auto-generated constructor stub
	}

	public UserWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public UserWinController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
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
/*		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { ; ignore };
			}
		}		
*/		
		
	
		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$rbActive( Event ev ){
		refreshList();
	}
	public void onCheck$rbInactive( Event ev ){
		refreshList();
	}
	public void onCheck$rbAll( Event ev ){
		refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){

		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( rbActive.isSelected()) display = 1;
		if ( rbInactive.isSelected()) display = 2;
		if ( rbAll.isSelected()) display = 3;

		// display status column header?
		status.setVisible(( display == 3 ) ? true: false );

		
		if ( userListbox == null ) return;
		
		// remove all items
		for ( int i = userListbox.getItemCount(); i > 0; --i ){
			userListbox.removeItemAt( 0 );
		}
		
		

		// populate list

		pmUser user = pmUser.open();
		
		while ( user.getNext()){
			
			// get user status byte
			//ProbTbl.Status status = user.getStatus();
			pmUser.Status status = user.isValid() ? pmUser.Status.ACTIVE: pmUser.Status.INACTIVE;
			
			// is this type selected?
			if ((( status == pmUser.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == pmUser.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				// ( status == probTbl.Status.REMOVED ) - removed
				// ( status == probTbl.SUPERCEDED ) - superceded by edit
				

		
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( userListbox );
				i.setValue( user.getRec());
				
				new Listcell( user.getUser()).setParent( i );
				new Listcell( user.getName().getPrintableNameLFM()).setParent( i );
				new Listcell( user.getRole().getDesc()).setParent( i );
				
				// set problem status
				String s = "Inactive";
				if ( status == pmUser.Status.ACTIVE ) s = "Active";
				new Listcell( s ).setParent( i );
				
				new Listcell( user.getSignonDate().getPrintable(9) + "  " + user.getSignonTime().getPrintable(5)).setParent( i );
			}
		}

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.USER_MANAGE, null, Pm.getUserRec(), null, null );
		

		user.close();

		return;
	}
	
	
	
	// Open dialog to enter new user
	
	public void onClick$btnNewUser( Event ev ){
		
		if ( UserEdit.enter( userWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing user
	
	public void onClick$btnEdit( Event ev ){
	
		// was an item selected?
		if ( userListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) userListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;

		// call edit routine
		if ( UserEdit.edit( rec, true, userWin )){
			refreshList();
		}		
		return;
	}
	
	
	
	
	
	// Open dialog to remove an existing user
	
	public void onClick$btnDelete( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( userListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No user selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// is user sure?
		if ( Messagebox.show( "Do you really wish to delete this user?", "Delete User", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES ) return;

		// get selected item's rec
		Rec rec = (Rec) userListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// mark user inactive/hidden
		pmUser user = new pmUser( rec );
		user.setHidden();
		user.write( rec );

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.USER_DELETE, null, Pm.getUserRec(), rec, user.getUser() );
		
		// refresh list
		refreshList();	
		return;
	}
	
	
	
	
	// Open dialog 
	
	public void onClick$btnPrint( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( userListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) userListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;

		// call edit routine
		//ProbRes.remove( reca, ptRec, probsWin );			
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.USER_PRINT, null, Pm.getUserRec(), null, null );
		
		refreshList();	
		return;
	}
	
	

}
