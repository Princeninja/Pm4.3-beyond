package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class ParRemove {
	

	/**
	 * The Call method.
	 */
	public static boolean show( Rec ptRec, Reca parReca, Component parent ) {

		// if ptrec or parReca not valid, return
		if ( ! Rec.isValid( ptRec ) || ! Reca.isValid( parReca )) return false;
		
		new ParRemove( ptRec, parReca, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ParRemove( Rec ptRec, Reca parReca, Component parent ) {
		super();
		createBox( ptRec, parReca, parent );
	}

	
	
	
	private void createBox( Rec ptRec, Reca parReca, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "reca", parReca );
		

		// create new window
		Window win = (Window) Executions.createComponents( "parrem.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
