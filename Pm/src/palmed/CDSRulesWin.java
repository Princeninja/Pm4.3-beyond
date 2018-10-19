package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

public class CDSRulesWin {

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new CDSRulesWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private CDSRulesWin( Component parent ) {
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
		Executions.createComponents( "cdsrules.zul", parent, /*myMap*/ null );
	}


}
