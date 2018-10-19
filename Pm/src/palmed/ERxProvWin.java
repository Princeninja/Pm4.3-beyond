package palmed;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;


public class ERxProvWin {

	private Window ERxProvWin;			// user edit window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ){

		new ERxProvWin( parent );
		return true;
	}



	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ERxProvWin( Component parent ) {
		super();
		createBox( parent );
	}

	private void createBox( Component parent ) {
				
		// pass parameters to new window
		//Map<String, Object> myMap = new HashMap();
		////myMap.put( "operation", operation );
		//if ( rec != null ) myMap.put( "rec", rec );
		//myMap.put( "flgPerms", flgPerms );
		
		// create new window
		ERxProvWin = (Window) Executions.createComponents( "erxprov.zul", parent, null /*myMap*/ );
		
/*		try {
			ERxProvWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (InterruptedException e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}
*/
		return;
	}
}
