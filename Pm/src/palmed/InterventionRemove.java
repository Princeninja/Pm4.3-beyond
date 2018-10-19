package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class InterventionRemove {

	/**
	 * The Call method.
	 */
	public static boolean show( Rec ptRec, Reca ivReca, Component parent ) {

		// if ptrec or parReca not valid, return
		if ( ! Rec.isValid( ptRec ) || ! Reca.isValid(ivReca )) return false;
		
		new InterventionRemove( ptRec, ivReca, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private InterventionRemove( Rec ptRec, Reca ivReca, Component parent ) {
		super();
		createBox( ptRec, ivReca, parent );
	}

	
	
	
	private void createBox( Rec ptRec, Reca ivReca, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "reca", ivReca );
		

		// create new window
		Window win = (Window) Executions.createComponents( "intervrem.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			//ignore
		}
	}

}
