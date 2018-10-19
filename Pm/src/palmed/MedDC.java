package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class MedDC {
	//private Rec ptRec = null;			// patient record number
	//private String operation = null;	// operation to perform ("newpt" vs "editpt")
	
	//private Component parent = null;	// parent Component (calling window)
	//private Window editPtWin;			// search window (created window)
	
	

	/**
	 * The Call method.
	 */
	public static boolean show( Rec ptRec, Reca cmedReca, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		if ( ! Reca.isValid( cmedReca )) return false;
		
		new MedDC( ptRec, cmedReca, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private MedDC( Rec ptRec, Reca cmedReca, Component parent ) {
		super();
		createBox( ptRec, cmedReca, parent );
	}

	
	
	
	private void createBox( Rec ptRec, Reca cmedReca, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "cmedReca", cmedReca );
		

		// create new editpt window
		//editPtWin = (Window) 
		Window win = (Window) Executions.createComponents( "meddc.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
