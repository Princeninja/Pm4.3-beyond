package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

import usrlib.Rec;

public class Labintwin {
	
	private Rec ptRec = null;

	/**
	 * 
	 * The Call Method
	 */
	public static boolean show(Rec ptRec, Component parent ) {

		new Labintwin( ptRec, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private Labintwin( Rec ptRec,Component parent ) {
		super();
		createBox( ptRec, parent );
	}

	
	
	
	private void createBox( Rec ptRec, Component parent ) {

		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		
		if ( parent instanceof Tabpanel ){
			System.out.println( "Calling Lab interface, the rec is:"+ ptRec.getRec());
		}
		
	
		Executions.createComponents( "labinterface.zul", parent, myMap );
	}
	

	
}
