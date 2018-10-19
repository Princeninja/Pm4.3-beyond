package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class ProbEdit {
	
	private Component parent = null;	// parent Component (calling window)
	private Window probEditWin;		// edit problems window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		
		new ProbEdit( EditPt.Operation.NEWPT, null, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "ProbEdit.enter() returning.");
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Reca reca, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		if (( reca == null ) || ( reca.getRec() < 2 )){ SystemHelpers.seriousError( "ProbEdit.edit() bad reca" ); return false; }
		
		new ProbEdit( EditPt.Operation.EDITPT, reca, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "ProbEdit.edit() returning.");
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ProbEdit( EditPt.Operation operation, Reca reca, Rec ptRec, Component parent ) {
		super();
		createBox( operation, reca, ptRec, parent );
	}

	private void createBox( EditPt.Operation operation, Reca reca, Rec ptRec, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( ptRec != null ) myMap.put( "ptRec", ptRec );
		if ( reca != null ) myMap.put( "reca", reca );
		
		// create new window
		probEditWin = (Window) Executions.createComponents( "probedit.zul", parent, myMap );
		
		try {
			probEditWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (Exception e) {
//			logger.fatal("", e);
	        System.out.println( "Exception" );

		}

		
		System.out.println( "ProbEdit.createBox() returning.");
		return;
	}


}
