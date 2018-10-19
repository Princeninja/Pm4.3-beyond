package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;


public class CdmWin {


	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new CdmWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private CdmWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "INSTANCE OF TABPANEL");
		}
		
		// pass parameters to new window
		//Map<String, Object> myMap = new HashMap();
		//myMap.put( "ptRec", ptRec );
		//myMap.put( "operation", operation );
		

		// create new Audit Log window
		//editPtWin = (Window) 
		Executions.createComponents( "cdmwin.zul", parent, /*myMap*/ null );
	}

}
