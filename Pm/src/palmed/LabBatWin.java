package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;

public class LabBatWin {

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new LabBatWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private LabBatWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		
		
		// pass parameters to new window
		//Map<String, Object> myMap = new HashMap();
		//myMap.put( "ptRec", ptRec );
		//myMap.put( "operation", operation );
		

		// create new editpt window
		//editPtWin = (Window) 
		Executions.createComponents( "labbat.zul", parent, /*myMap*/ null );
	}

}
