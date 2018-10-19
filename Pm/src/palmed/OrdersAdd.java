package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class OrdersAdd {

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
		
		new OrdersAdd( EditPt.Operation.NEWPT, ptRec, null, parent );
		return true;
	}

	/**
	 * The Call method.
	 */
	public static boolean edit( Rec ptRec, Reca orderReca, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		
		new OrdersAdd( EditPt.Operation.EDITPT, ptRec, orderReca, parent );
		return true;
	}

	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private OrdersAdd( EditPt.Operation operation, Rec ptRec, Reca orderReca, Component parent ) {
		super();
		createBox( operation, ptRec, orderReca, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec ptRec, Reca orderReca, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		if ( Reca.isValid( orderReca )) myMap.put( "reca", orderReca );
		myMap.put( "operation", operation );
		

		// create new window
		Window win = (Window) Executions.createComponents( "ordersadd.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
