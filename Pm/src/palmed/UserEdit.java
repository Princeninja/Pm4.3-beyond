package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class UserEdit {

	private Component parent = null;	// parent Component (calling window)
	private Window userEditWin;			// user edit window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Component parent ) {

		new UserEdit( EditPt.Operation.NEWPT, null, true, parent );
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec rec, boolean flgPerms, Component parent ) {

		// if ptrec not valid, return
		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "UserEdit.edit() bad rec" ); return false; }
		
		new UserEdit( EditPt.Operation.EDITPT, rec, flgPerms, parent );
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private UserEdit( EditPt.Operation operation, Rec rec, boolean flgPerms, Component parent ) {
		super();
		createBox( operation, rec, flgPerms, parent );
	}

	private void createBox( EditPt.Operation operation, Rec rec, boolean flgPerms, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );
		myMap.put( "flgPerms", flgPerms );
		
		// create new window
		userEditWin = (Window) Executions.createComponents( "useredit.zul", parent, myMap );
		
		try {
			userEditWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (InterruptedException e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}

		
		//System.out.println( "UserEdit.createBox() returning.");
		return;
	}


}
