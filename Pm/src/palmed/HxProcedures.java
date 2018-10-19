package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class HxProcedures {
	
	Window win = null;
	

	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static Window show( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return null;
		
		HxProcedures hx = new HxProcedures( ptRec, parent );
		return hx.win;
	}


	private HxProcedures( Rec ptRec, Component parent ){
		this.win = createBox( ptRec, parent );
	}

	
	private Window createBox( Rec ptRec, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		if ( ptRec != null ) myMap.put( "ptRec", ptRec );
		
		// create new window
		this.win = (Window) Executions.createComponents( "hxproc.zul", parent, myMap );
		return this.win;
	}

}
