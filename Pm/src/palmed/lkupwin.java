package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;


public class lkupwin {

	/**
	 * 
	 * The Call Method
	 */
	public static boolean show( Component parent ) {

		new lkupwin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private lkupwin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "Calling Look-up Application");
		}
		
	
		Executions.createComponents( "lkup.zul", parent, /*myMap*/ null );
	}

	
}
