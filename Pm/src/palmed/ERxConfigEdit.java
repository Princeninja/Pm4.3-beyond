package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;


public class ERxConfigEdit {


	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {
		
		new ERxConfigEdit( parent );
		return true;
	}

	
	
	/**
	 * ERxConfig() - private constructor
	 * 
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ERxConfigEdit( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox(Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "INSTANCE OF TABPANEL");
		}
		
		// pass parameters to new window
		//Map<String, Object> myMap = new HashMap();
		//myMap.put( "provRec", provRec );
		//myMap.put( "operation", operation );
		

		// create new editprov window
		//editPtWin = (Window) 
		Executions.createComponents( "erxconfig.zul", parent, null /*myMap*/ );
	}

	
	
	
}

/**/



