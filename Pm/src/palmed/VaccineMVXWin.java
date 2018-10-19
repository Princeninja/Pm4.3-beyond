package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;

public class VaccineMVXWin {

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new VaccineMVXWin( parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private VaccineMVXWin( Component parent ) {
		super();
		createBox( parent );
	}

	
	
	
	private void createBox( Component parent ) {

		Executions.createComponents( "vacmvx.zul", parent, /*myMap*/ null );
	}

}
