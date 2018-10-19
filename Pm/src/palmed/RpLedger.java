package palmed;


import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;







	public class RpLedger {

		private int rpRec = 0;
		
		private Component parent = null;	// parent Component (calling window)
		private Window RpLedgerWin;			// search window (created window)
		

		/**
		 * The Call method.
		 */
		public static boolean show( Rec rpRec, Component parent ) {

			// if ptrec not valid, return
			if ( ! Rec.isValid( rpRec )) return false;
			
			new RpLedger( rpRec, parent );
			return true;
		}

		/**
		 * private constructor. So it can only be created with the static show()
		 * method.
		 */
		private RpLedger( Rec rpRec, Component parent ) {
			super();

			createBox( rpRec, parent );
		}

		private void createBox( Rec rpRec, Component parent ) {

			System.out.println( "in rpledger createBox()");
			
			if ( parent instanceof Tabpanel ){
				System.out.println( "INSTANCE OF TABPANEL");

			}
			
			// pass parameters to new window
			Map<String, Rec> myMap = new HashMap();
			myMap.put( "rpRec", rpRec );
			

			// create new ptchart window
			RpLedgerWin = (Window) Executions.createComponents("rpledger.zul", parent, myMap );
		}
	}

