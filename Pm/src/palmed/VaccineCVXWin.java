package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;

public class VaccineCVXWin {

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new VaccineCVXWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private VaccineCVXWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		Executions.createComponents( "vaccvx.zul", parent, /*myMap*/ null );
	}


}
