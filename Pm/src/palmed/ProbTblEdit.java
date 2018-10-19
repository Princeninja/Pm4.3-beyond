package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;


public class ProbTblEdit {
	
	private Component parent = null;	// parent Component (calling window)
	private Window probTblEditWin;		// edit problem table window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Component parent ) {

		new ProbTblEdit( EditPt.Operation.NEWPT, null, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec rec, Component parent ) {

		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "ProbTblEdit.edit() bad rec" ); return false; }
		
		new ProbTblEdit( EditPt.Operation.EDITPT, rec, parent );
		System.out.println("goes here");
		// TODO - return true only if refresh needed
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ProbTblEdit( EditPt.Operation operation, Rec rec, Component parent ) {
		super();
		createBox( operation, rec, parent );
	}

	private void createBox( EditPt.Operation operation, Rec rec, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );
		
		// create new window
		probTblEditWin = (Window) Executions.createComponents( "probtbledit.zul", parent, myMap );
		
		try {
			probTblEditWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (InterruptedException e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}

		
		System.out.println( "ProbTblEdit.createBox() returning.");
		return;
	}




}
