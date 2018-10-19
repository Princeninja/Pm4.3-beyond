package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

public class PpoWin {

	/**
	 * The Call method
	 * */
	
	public static boolean show ( Component parent ){
		
		new PpoWin( parent );
		return true;
	}	
	
	/***
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	
	private PpoWin( Component parent ){
		super();
		createBox( parent );
		
	}

	private void createBox( Component parent ) {
		
		if (parent instanceof Tabpanel ) {
			System.out.println( "INSTANCE OF TABPANEL" );
		}
		
		Executions.createComponents( "Ppowin.zul", parent, null);
	}
	

	
}
