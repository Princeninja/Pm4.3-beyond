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
 * This uses the enum from EditDgn.java 
 * @author program
 *
 */

public class EditSrv {

	private Window editSrvWin;
	
	/**
	 * The Call method.
	 */
	public static boolean enter( Component parent ) {

		new EditSrv( EditDgn.Operation.NEWDGN, null, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec rec, Component parent ) {

		if (( rec == null ) || ( rec.getRec() < 2 )){ SystemHelpers.seriousError( "EditSrv.edit() bad rec" ); return false; }
		
		System.out.println("edit rec:"+rec);
		new EditSrv( EditDgn.Operation.EDITDGN, rec, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private EditSrv( EditDgn.Operation operation, Rec rec, Component parent ) {
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
		 
	editSrvWin = (Window) Executions.createComponents("editsrv.zul", parent, myMap );
	
	try {
		editSrvWin.doModal();
	} catch (SuspendNotAllowedException e) {
//		logger.fatal("", e);
        System.out.println( "SuspendNotAllowedException" );

	} catch (InterruptedException e) {
//		logger.fatal("", e);
        System.out.println( "InterruptedException" );

	}

	
	System.out.println( "returning from EditSrv.createBox(){Edit Win} .");
	return;
	
	}

	
	
	
	
	
	
}
