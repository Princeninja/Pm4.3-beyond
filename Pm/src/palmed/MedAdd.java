package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class MedAdd {

	
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
		
		new MedAdd( operation, ptRec, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private MedAdd( EditPt.Operation operation, Rec ptRec, Component parent ) {
		super();
		createBox( operation, ptRec, parent );
	}

	
	
	
	private void createBox( EditPt.Operation operation, Rec ptRec, Component parent ) {

		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "operation", operation );
		

		// create new window
		Window win = (Window) Executions.createComponents( "medadd.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
