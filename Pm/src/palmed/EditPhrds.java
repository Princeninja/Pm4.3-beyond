package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;

/**
 * This class uses the enum from EditPpo operation 
 **/

public class EditPhrds {
		
	//private Component parent = null;
	private Window EditPhrdsWin;
	
	/**
	 * The Call Method
	 */
	
	public static boolean enter( Component parent ) {
		
		System.out.println("new called");
		new EditPhrds( EditPpo.Operation.NEWPPO, null, parent );
		
		//TODO - return true only if refresh is needed 
		return true;
	}
	
	/**
	 * The Call Method
	 */
	
	public static boolean edit( Rec rec, Component parent ) {
		
		if (( rec == null ) || (rec.getRec() < 2 )) { SystemHelpers.seriousError( "EditPhrds.edit() bad rec" ); return false; }
		
		new EditPhrds( EditPpo.Operation.EDITPPO, rec, parent );
		System.out.println("rec phrds.java  is: "+ rec );
		//TODO - return true only if refresh needed 
		return true;
	}
	
	/**
	 private constructor. So it can only be created with the static show() method.
	  */
	private EditPhrds (EditPpo.Operation operation, Rec rec, Component parent ) {
		super();
		System.out.println("operation in Editphrds() is : "+ operation);
		createBox( operation, rec, parent );
		
	}

	private void createBox(EditPpo.Operation operation, Rec rec, Component parent) {
		
		System.out.println("createBox' operation is: "+ operation);
		//pass parameters to new window
		Map<String, Object>myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );
		
		// create new window
		EditPhrdsWin = (Window) Executions.createComponents( "editphrds.zul", parent, myMap);
		
		try {
			EditPhrdsWin.doModal();
		} catch (SuspendNotAllowedException e) {
			//logger.fatal("", e);
			System.out.println( "SuspendNotAllowedException" );
		} catch (InterruptedException e) {
			// logger.fatal("", e);
			System.out.println( "InterruptedException" );
			
		}
		System.out.println( "returning from EditPhrds.createBox(){the Edit Win} .");
		return;
		
		}
	
}
