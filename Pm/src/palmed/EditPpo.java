package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;

/**
 * This class creates its own enum for operation 
 **/

public class EditPpo {

	//private Component parent = null;
	private Window EditPpoWin;
	
	
	public enum Operation{
		EDITPPO,
		NEWPPO;
	}
	
	/**
	 * The main Call method.
	 */
	public static boolean show(EditPpo.Operation operation, Rec rec, Component parent ) {

		// if rec not valid, return
		if (( operation == EditPpo.Operation.EDITPPO ) && ( rec.getRec() < 2 )) return false;
		
		new EditPpo( operation, rec, parent );
		return true;
	}
	// The call methods: 
	
	/**
	 * The Call Method
	 */
	
	public static boolean enter( Component parent ) {
		
		System.out.println("new called");
		new EditPpo( EditPpo.Operation.NEWPPO, null, parent );
		
		//TODO - return true only if refresh is needed 
		return true;
	}
	
	/**
	 * The Call Method
	 */
	
	public static boolean edit( Rec rec, Component parent ) {
		
		if (( rec == null ) || (rec.getRec() < 2 )) { SystemHelpers.seriousError( "EditPpo.edit() bad rec" ); return false; }
		
		new EditPpo( EditPpo.Operation.EDITPPO, rec, parent );
		System.out.println("rec ppo.java  is: "+ rec );
		//TODO - return true only if refresh needed 
		return true;
	}
	
	/**
	 private constructor. So it can only be created with the static show() method.
	  */
	private EditPpo (EditPpo.Operation operation, Rec rec, Component parent ) {
		super();
		System.out.println("operation in Editppo() is : "+ operation);
		createBox( operation, rec, parent );
		
	}

	private void createBox(EditPpo.Operation operation, Rec rec, Component parent) {
		
		System.out.println("createBox' operation is: "+ operation);
		//pass parameters to new window
		Map<String, Object>myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( rec != null ) myMap.put( "rec", rec );
		
		// create new window
		EditPpoWin = (Window) Executions.createComponents( "editPpo.zul", parent, myMap);
		
		try {
			EditPpoWin.doModal();
		} catch (SuspendNotAllowedException e) {
			//logger.fatal("", e);
			System.out.println( "SuspendNotAllowedException" );
		} catch (InterruptedException e) {
			// logger.fatal("", e);
			System.out.println( "InterruptedException" );
			
		}
		System.out.println( "returning from EditPpo.createBox(){the Edit Win} .");
		return;
		
		}
	}
		
	
	

