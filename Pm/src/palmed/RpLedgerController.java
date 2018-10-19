package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class RpLedgerController extends GenericForwardComposer {
	
	private Rec	rpRec = null;
	
	private Window rpLedgerWin;		// autowired
	private Label rpname;			// autowired - patient name
	private Label rprec;			// autowired - pt record number
	
	private Component parent;
	
	
	public void doAfterCompose( Component component ){
		
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println( "in RpLedgerController.doAfterCompose()" );
		Execution exec=Executions.getCurrent();
		Map myMap = exec.getArg();
		rpRec = (Rec) myMap.get( "rpRec" );
		
		rprec.setValue( rpRec.toString());
		rpname.setValue( new DirPt( rpRec ).getName().getPrintableNameLFM());
		System.out.println( "chart created rprec=" + rpRec.toString());
		System.out.println( "name=" + rpname.getValue());

		
		// Get Tabpanel chart is to be placed in.
		// May have to go several layers deep due to enclosing window.
		
		Tabpanel tabpanel = null;

		if ( component instanceof Tabpanel ){
			tabpanel = (Tabpanel) component;
		} else if ( component.getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent();
		} else if ( component.getParent().getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent().getParent();
		}
		
		// Set title of Tabpanel to Patient's name
		tabpanel.getLinkedTab().setLabel( rpname.getValue());
		tabpanel.getLinkedTab().setImage( "ledger_verysmall.png" );

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.LEDGER_ACCESS, null, Pm.getUserRec(), rpRec, null );
		
	}

}
