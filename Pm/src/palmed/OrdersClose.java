package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class OrdersClose {

	

	/**
	 * The Call method.
	 */
	public static boolean show( Rec ptRec, Reca orderReca, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		if ( ! Reca.isValid( orderReca )) return false;
		
		new OrdersClose( ptRec, orderReca, parent );
		return true;
	}


	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private OrdersClose( Rec ptRec, Reca orderReca, Component parent ) {
		super();
		createBox( ptRec, orderReca, parent );
	}

	
	
	
	private void createBox( Rec ptRec, Reca orderReca, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "reca", orderReca );
		

		// create new window
		Window win = (Window) Executions.createComponents( "orderscls.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
