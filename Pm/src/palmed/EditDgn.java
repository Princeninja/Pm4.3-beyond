package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;

/**
 * 
 * 
 *   For test purposes it will be Diagnosis, later, it is inherited for Service Codes. 
 */
 

public class EditDgn {
		
	private Component parent = null;
	private Window editDgnWin;
	
	public enum Operation{
		EDITDGN,
		NEWDGN;
	}
	
	/**
	 * The Call method.
	 */
	public static boolean show(EditDgn.Operation operation, Rec rec, Component parent ) {

		// if rec not valid, return
		if (( operation == EditDgn.Operation.EDITDGN ) && ( rec.getRec() < 2 )) return false;
		
		new EditDgn( operation, rec, parent );
		return true;
	}
// THESE ARE THE CALL METHODS, modeled from ProbTblEdit.java
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Component parent ) {

		new EditDgn( EditDgn.Operation.NEWDGN, null, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec rec, Component parent ) {

		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "EditDgn.edit() bad rec" ); return false; }
		
		System.out.println("edit rec:"+rec);
		new EditDgn( EditDgn.Operation.EDITDGN, rec, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private EditDgn( EditDgn.Operation operation, Rec rec, Component parent ) {
		super();
		createBox( operation, rec, parent );
	}

	
	
	
	private void createBox( EditDgn.Operation operation, Rec rec, Component parent ) {

		
		if ( parent instanceof Tabpanel ){
			System.out.println( "Instance of Dgn edit");
		}
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );

		// create new editdgn window
		 
	editDgnWin = (Window) Executions.createComponents("editDgn.zul", parent, myMap );
	
	try {
		editDgnWin.doModal();
	} catch (SuspendNotAllowedException e) {
//		logger.fatal("", e);
        System.out.println( "SuspendNotAllowedException" );

	} catch (InterruptedException e) {
//		logger.fatal("", e);
        System.out.println( "InterruptedException" );

	}

	
	System.out.println( "returning from EditDgn.createBox(){Edit Win} .");
	return;
	
	}

	
	
}
