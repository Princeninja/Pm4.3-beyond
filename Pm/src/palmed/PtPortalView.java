package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class PtPortalView {
	
	
	private Component parent = null;	// parent Component (calling window)
	private Window ptPortalViewWin;		// PtPortalView window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean show( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		
		new PtPortalView( ptRec, parent );
		return true;
	}



	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private PtPortalView( Rec ptRec, Component parent ) {
		super();
		createBox( ptRec, parent );
	}

	private void createBox( Rec ptRec, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		
		// create new window
		ptPortalViewWin = (Window) Executions.createComponents( "ptportalv.zul", parent, myMap );
		return;
	}



}
