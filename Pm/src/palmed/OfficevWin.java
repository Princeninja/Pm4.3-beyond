package palmed;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;


public class OfficevWin {
	
	/**
	 * 
	 * The Call Method
	 */
	public static boolean show( Component parent ) {

		new OfficevWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private OfficevWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "Calling Office Visit");
		}
		
	
		Executions.createComponents( "office_visit.zul", parent, /*myMap*/ null );
	}


}
