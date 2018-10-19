package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

public class StdDictWin {
	
	
	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new StdDictWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private StdDictWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "INSTANCE OF TABPANEL");
		}
		
		Executions.createComponents( "stddictwin.zul", parent, /*myMap*/ null );
	}


}
