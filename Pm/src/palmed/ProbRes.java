package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class ProbRes {

	private Component parent = null;	// parent Component (calling window)
	private Window probResWin;		// edit problems window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean resolve( Reca reca, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		
		new ProbRes( EditPt.Operation.EDITPT, reca, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "ProbRes.resolve() returning.");
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean remove( Reca reca, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		if (( reca == null ) || ( reca.getRec() < 2 )){ SystemHelpers.seriousError( "ProbRes.remove() bad reca" ); return false; }
		
		new ProbRes( EditPt.Operation.NEWPT, reca, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "ProbRes.remove() returning.");
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ProbRes( EditPt.Operation operation, Reca reca, Rec ptRec, Component parent ) {
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
		probResWin = (Window) Executions.createComponents( "probres.zul", parent, myMap );
		
		try {
			probResWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (Exception e) {
//			logger.fatal("", e);
	        System.out.println( "Exception" );

		}

		
		System.out.println( "ProbRes.createBox() returning.");
		return;
	}


}
