package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class ParAdd {

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
		
		new ParAdd( EditPt.Operation.NEWPT, ptRec, null, parent );
		return true;
	}

	/**
	 * The Call method.
	 */
	public static boolean edit( Rec ptRec, Reca parReca, Component parent ) {

		// if ptrec or parReca not valid, return
		if ( ! Rec.isValid( ptRec ) || ! Reca.isValid( parReca )) return false;
		
		new ParAdd( EditPt.Operation.EDITPT, ptRec, parReca, parent );
		return true;
	}
	
	

	/**
	 * private constructor. So it can only be created with the static add() or edit()
	 * methods.
	 */
	private ParAdd( EditPt.Operation operation, Rec ptRec, Reca parReca, Component parent ) {
		super();
		createBox( operation, ptRec, parReca, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec ptRec, Reca parReca, Component parent ) {

		
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "operation", operation );
		if ( Reca.isValid( parReca )) myMap.put( "reca", parReca );

		// create new window
		Window win = (Window) Executions.createComponents( "paradd.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
