package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;


/**
 * Class EditPt
 * 
 * @author Owner
 *
 */
public class EditPt {

	
	//private Rec ptRec = null;			// patient record number
	//private String operation = null;	// operation to perform ("newpt" vs "editpt")
	
	//private Component parent = null;	// parent Component (calling window)
	//private Window editPtWin;			// search window (created window)
	
	public enum Operation {
		EDITPT,
		NEWPT;
	}
	

	/**
	 * The Call method.
	 */
	public static boolean show( EditPt.Operation operation, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if (( operation == EditPt.Operation.EDITPT ) && ( ptRec.getRec() < 2 )) return false;
		
		new EditPt( operation, ptRec, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private EditPt( EditPt.Operation operation, Rec ptRec, Component parent ) {
		super();
		createBox( operation, ptRec, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec ptRec, Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "INSTANCE OF TABPANEL");
		}
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "operation", operation );
		

		// create new editpt window
		//editPtWin = (Window) 
		Executions.createComponents("editpt.zul", parent, myMap );
	}

}

/**/

