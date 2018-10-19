package palmed;


import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;






	/**
	 *
	 */
	public class PtChart {

		private Rec ptRec = null;			// patient record number
		
		private Component parent = null;	// parent Component (calling window)
		private Window ptChartWin;			// search window (created window)
		

		/**
		 * The Call method.
		 */
		public static boolean show( Rec ptRec, Component parent ) {

			// if ptrec not valid, return
			if ( ptRec.getRec() < 2 ) return false;
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PT_CHART, ptRec, Pm.getUserRec(), null, null );
			
			// open the chart
			new PtChart( ptRec, parent );
			return true;
		}

		/**
		 * private constructor. So it can only be created with the static show()
		 * method.
		 */
		private PtChart( Rec ptRec, Component parent ) {
			super();

			createBox( ptRec, parent );
		}

		private void createBox( Rec ptRec, Component parent ) {

			
			// pass parameters to new window
			Map<String, Rec> myMap = new HashMap();
			myMap.put( "ptRec", ptRec );
			
			System.out.println( "in ptchart  ptrec=" + ptRec.getRec());

			// create new ptchart window
			ptChartWin = (Window) Executions.createComponents("ptchart.zul", parent, myMap );
		}
	}
