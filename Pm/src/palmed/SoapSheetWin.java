package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class SoapSheetWin {

	private Rec ptRec = null;			// patient record number
	
	private Component parent = null;	// parent Component (calling window)
	private Window soapShtWin;			// soap sheet window (created window)
	

	/**
	 * The Call method.
	 */
	public static boolean show( String Debug,String Vfile,Rec ptRec, Component parent ) {

		// if ptrec not valid, return
		if ( ptRec.getRec() < 2 ) return false;
		
		new SoapSheetWin( Debug, Vfile, ptRec, parent );
		return true;
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private SoapSheetWin(String Debug, String Vfile, Rec ptRec, Component parent ) {
		super();

		createBox( Debug, Vfile, ptRec, parent );
	}

	private void createBox(String Debug, String Vfile,  Rec ptRec, Component parent ) {
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", ptRec );
		myMap.put("Vfile", Vfile);
		myMap.put("Debug", Debug);

		// create new soap sheet window
		soapShtWin = (Window) Executions.createComponents("soapsht.zul", parent, myMap );
		
		/*soapShtWin.setPosition("center");
		soapShtWin.setHeight("602px"); //reduced from 702px
		soapShtWin.setWidth("813px");
		soapShtWin.setVflex("1");
		soapShtWin.setHflex("1");
		soapShtWin.setBorder("normal");*/
		
		
		try {
			soapShtWin.doModal();
		} catch (SuspendNotAllowedException e) {
			//logger.fatal("", e);
			System.out.println( "SuspendNotAllowedException" );
		} catch (InterruptedException e) {
			// logger.fatal("", e);
			System.out.println( "InterruptedException" );
			
		}
		System.out.println( "returning from SoapSheetWin.createBox(){the SoapSheet Win} .");
		return;
	}

}
