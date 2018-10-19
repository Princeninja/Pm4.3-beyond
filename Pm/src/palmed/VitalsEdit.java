package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class VitalsEdit {

	private Component parent = null;	// parent Component (calling window)
	private Window vitalsNewWin;		// new vitals window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		
		new VitalsEdit( EditPt.Operation.NEWPT, null, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "VitalsEdit.enter() returning.");
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Reca reca, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		if (( reca == null ) || ( reca.getRec() < 2 )){ SystemHelpers.seriousError( "VitalsEdit.edit() bad reca" ); return false; }
		
		new VitalsEdit( EditPt.Operation.EDITPT, reca, ptRec, parent );
		
		// TODO - return true only if refresh needed
		System.out.println( "VitalsEdit.edit() returning.");
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private VitalsEdit( EditPt.Operation operation, Reca reca, Rec ptRec, Component parent ) {
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
		vitalsNewWin = (Window) Executions.createComponents( "vitals_new.zul", parent, myMap );
		
		try {
			vitalsNewWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (Exception e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}

		
		System.out.println( "VitalsEdit.createBox() returning.");
		return;
	}

}
