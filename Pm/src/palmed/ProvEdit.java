package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

import usrlib.Rec;

public class ProvEdit {

	public enum Operation {
		EDITPT,
		NEWPT;
	}
	

	/**
	 * The Call method.
	 */
	public static boolean show( EditPt.Operation operation, Rec provRec, Component parent ) {

		// if ptrec not valid, return
		if (( operation == EditPt.Operation.EDITPT ) && ( provRec.getRec() < 2 )) return false;
		
		new ProvEdit( operation, provRec, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ProvEdit( EditPt.Operation operation, Rec provRec, Component parent ) {
		super();
		createBox( operation, provRec, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec provRec, Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "INSTANCE OF TABPANEL");
		}
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "provRec", provRec );
		myMap.put( "operation", operation );
		

		// create new editprov window
		//editPtWin = (Window) 
		Executions.createComponents("provedit.zul", parent, myMap );
	}

}

/**/


