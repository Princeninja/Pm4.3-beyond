package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesRemove {

	/**
	 * The Call method.
	 */
	public static boolean remove( Class<? extends Notes> noteClass, Rec ptRec, Reca noteReca, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		if ( ! Reca.isValid( noteReca )) return false;
		
		new NotesRemove( noteClass, ptRec, noteReca, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static remove() method.
	 */
	private NotesRemove( Class<? extends Notes> noteClass, Rec ptRec, Reca noteReca, Component parent ) {
		super();
		createBox( noteClass, ptRec, noteReca, parent );
	}

	
	


	
	
	private void createBox( Class<? extends Notes> noteClass, Rec ptRec, Reca noteReca, Component parent ) {

		
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "noteClass", noteClass );

		if ( Reca.isValid( noteReca )) myMap.put( "reca", noteReca );

		// create new window
		Window win = (Window) Executions.createComponents( "notesrem.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
