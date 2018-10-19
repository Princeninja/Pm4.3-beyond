package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesEdit {

	public enum Operation {
		EDITPT,
		NEWPT;
	}
	

	/**
	 * The Call method.
	 */
	public static boolean add( Class<? extends Notes> noteClass, Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		
		new NotesEdit( noteClass, EditPt.Operation.NEWPT, ptRec, null, parent );
		return true;
	}

	/**
	 * The Call method.
	 */
	public static boolean edit( Class<? extends Notes> noteClass, Rec ptRec, Reca parReca, Component parent ) {

		// if ptrec or parReca not valid, return
		if ( ! Rec.isValid( ptRec ) || ! Reca.isValid( parReca )) return false;
		
		new NotesEdit( noteClass, EditPt.Operation.EDITPT, ptRec, parReca, parent );
		return true;
	}
	
	

	/**
	 * private constructor. So it can only be created with the static add() or edit()
	 * methods.
	 */
	private NotesEdit( Class<? extends Notes> noteClass, EditPt.Operation operation, Rec ptRec, Reca parReca, Component parent ) {
		super();
		createBox( noteClass, operation, ptRec, parReca, parent );
	}

	
	
	
	private void createBox( Class<? extends Notes> noteClass, EditPt.Operation operation, Rec ptRec, Reca parReca, Component parent ) {

		
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		myMap.put( "operation", operation );
		//myMap.put( "noteType", noteType );
		myMap.put( "noteClass", noteClass );

		if ( Reca.isValid( parReca )) myMap.put( "reca", parReca );

		// create new window
		Window win = (Window) Executions.createComponents( "notesedit.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
