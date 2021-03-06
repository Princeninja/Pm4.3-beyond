package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class LabResEdit {

	private Component parent = null;	// parent Component (calling window)
	private Window labResEditWin;		// edit  window (created window)
	

	
	
	// THESE ARE THE CALL METHODS
	
	/**
	 * The Call method.
	 */
	public static boolean add( Rec ptRec, Component parent ) {

		if ( ! Rec.isValid( ptRec )){ SystemHelpers.seriousError( "LabResEdit bad ptRec" ); return false; }
		new LabResEdit( EditPt.Operation.NEWPT, ptRec, null, parent );
		return true;
	}


	/**
	 * The Call method.
	 */
	public static boolean edit( Rec ptRec, Reca labReca, Component parent ) {

		if ( ! Rec.isValid( ptRec )){ SystemHelpers.seriousError( "LabResEdit bad ptRec" ); return false; }
		if ( ! Reca.isValid( labReca )){ SystemHelpers.seriousError( "LabResEdit bad labReca" ); return false; }

		new LabResEdit( EditPt.Operation.EDITPT, ptRec, labReca, parent );
		
		// TODO - return true only if refresh needed
		return true;
	}


	
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private LabResEdit( EditPt.Operation operation, Rec ptRec, Reca labReca, Component parent ) {
		super();
		createBox( operation, ptRec, labReca, parent );
	}

	private void createBox( EditPt.Operation operation, Rec ptRec, Reca labReca, Component parent ) {
				
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "operation", operation );
		if ( ptRec != null ) myMap.put( "ptRec", ptRec );
		if ( labReca != null ) myMap.put( "reca", labReca );

		// create new window
		labResEditWin = (Window) Executions.createComponents( "labresedit.zul", parent, myMap );
		
		try {
			labResEditWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (Exception e) {
//			logger.fatal("", e);
	        System.out.println( "Exception" );

		}

		
		System.out.println( "LabResEdit.createBox() returning.");
		return;
	}

}
