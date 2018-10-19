package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class LabObsTblEdit {

	private Component parent = null;	// parent Component (calling window)
	private Window labObsTblEditWin;	// edit  window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Component parent ) {

		new LabObsTblEdit( EditPt.Operation.NEWPT, null, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec rec, Component parent ) {

		if ( ! Rec.isValid( rec )){ SystemHelpers.seriousError( "LabObsTblEdit.edit() bad rec" ); return false; }
		
		new LabObsTblEdit( EditPt.Operation.EDITPT, rec, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private LabObsTblEdit( EditPt.Operation operation, Rec rec, Component parent ) {
		super();
		createBox( operation, rec, parent );
	}

	private void createBox( EditPt.Operation operation, Rec rec, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );
		
		// create new window
		labObsTblEditWin = (Window) Executions.createComponents( "labobstbledit.zul", parent, myMap );
		
		try {
			labObsTblEditWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (Exception e) {
//			logger.fatal("", e);
	        System.out.println( "Exception" );

		}

		
		System.out.println( "LabObsTblEdit.createBox() returning.");
		return;
	}


}
