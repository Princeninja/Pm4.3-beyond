package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class ImmAdd {

	public enum Operation {
		EDITPT,
		NEWPT;
	}
	

	/**
	 * The Call method.
	 */
	public static boolean add( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		
		new ImmAdd( EditPt.Operation.NEWPT, ptRec, null, parent );
		return true;
	}

	/**
	 * The Call method.
	 */
	public static boolean edit( Rec ptRec, Reca immReca, Component parent ) {

		// if ptrec or ImmReca not valid, return
		if ( ! Rec.isValid( ptRec ) || ! Reca.isValid( immReca )) return false;
		
		new ImmAdd( EditPt.Operation.EDITPT, ptRec, immReca, parent );
		return true;
	}
	
	

	/**
	 * private constructor. So it can only be created with the static add() or edit()
	 * methods.
	 */
	private ImmAdd( EditPt.Operation operation, Rec ptRec, Reca immReca, Component parent ) {
		super();
		createBox( operation, ptRec, immReca, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec ptRec, Reca immReca, Component parent ) {

		
		
		// pass Immameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "operation", operation );
		if ( Reca.isValid( immReca )) myMap.put( "reca", immReca );

		// create new window
		Window win = (Window) Executions.createComponents( "immadd.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
