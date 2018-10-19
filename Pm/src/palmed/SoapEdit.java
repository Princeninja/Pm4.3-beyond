package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class SoapEdit {

	public enum Operation {
		EDIT,
		NEW;
	}
	

	/**
	 * The Edit Call method.
	 */
	public static boolean edit( Rec ptRec, Reca soapReca, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		if ( ! Reca.isValid( soapReca )) return false;
		
		new SoapEdit( Operation.EDIT, ptRec, soapReca, parent );
		return true;
	}

	/**
	 * The New Call method.
	 */
	public static boolean add( Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ! Rec.isValid( ptRec )) return false;
		
		new SoapEdit( Operation.NEW, ptRec, null, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private SoapEdit( Operation operation, Rec ptRec, Reca soapReca, Component parent ) {
		super();
		createBox( operation, ptRec, soapReca, parent );
	}

	
	
	
	private void createBox( Operation operation, Rec ptRec, Reca soapReca, Component parent ) {

		
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", ptRec );
		myMap.put( "soapReca", soapReca );
		myMap.put( "operation", operation );
		

		// create new window
		//Executions.createComponents("soapedit.zul", parent, myMap );
		// create new window
		Window win = (Window) Executions.createComponents( "soapedit.zul", parent, myMap );
		try {
			win.doModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
