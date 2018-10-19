package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

public class TenkeyComponentWinController extends GenericForwardComposer {
	
	Window tenkeyWin;		// autowired
	Component parent;		// parent container
	
	public void doAfterCompose( Component component ){

		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parent = tenkeyWin.getParent();
	}
	

	public void onClick$b1( Event ev ){
		
		System.out.println( "clicked button 1" );
		if ( parent == null ) System.out.println( "parent=null"  );

	}
}
