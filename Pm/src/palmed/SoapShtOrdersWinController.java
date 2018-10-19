package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import usrlib.Date;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Validity;




public class SoapShtOrdersWinController extends GenericForwardComposer {
	
	Listbox ordersListbox;		// autowired
	
	Rec ptRec;					// pt record number
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			SystemHelpers.seriousError( "Error: SoapShtOrdersWinController() invalid or null ptRec" );
			ptRec = new Rec( 2 );		// default for testing
		}

		return;
	}
	
	

	
	public void onClick$btnPost( Event ev ){
		
		int i;
		
		for ( i=ordersListbox.getItemCount(); i > 0 ;--i ){
			
	
			// get item, get value (order record number or null if not yet posted)
			Listitem item = ordersListbox.getItemAtIndex(i-1);
			Reca reca = (Reca) item.getValue();
			
			// make sure this one not already posted
			if ( reca != null ) continue;
			
			// build item name
			String name = item.getLabel();
			
			// post item, create and store orders record
			Orders order = new Orders();
			order.setDate( Date.today());
			order.setPtRec( ptRec );
			order.setDesc( name );
			order.setOrderRec( new Rec()); //TODO
			order.setVisitReca( new Reca()); //TODO
			order.setStatus( Orders.Status.ORDERED );
			order.setPriority( Orders.Priority.ROUTINE );
			order.setValid( Validity.VALID );
			reca = order.postNew(ptRec);
			if ( reca == null ) SystemHelpers.seriousError( "SoapShtOrdersWinController - bad orders reca" );
			
			
			// mark item in listbox as posted (store reca in item listing)
			item.setValue( reca );
			item.setImage( "icon_check_mark.png" );

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.ORDERS_ADD, ptRec, Pm.getUserRec(), reca, null );
			
		}

		// Notify other windows of new orders
		Notifier.notify( ptRec, Notifier.Event.ORDERS );
		
		return;
	}

	
	
	public void onClick$btnDelete( Event ev ){
		
		int i;

		for ( i=ordersListbox.getSelectedCount(); i > 0 ;--i ){
			
	
			// get item, get value (order record number or null if not yet posted)
			Listitem item = ordersListbox.getSelectedItem();
			Reca reca = (Reca) item.getValue();
			
			// is this item already posted?
			if ( reca != null ){
				
				Orders order = new Orders( reca );
				order.setStatus( Orders.Status.CLOSED );
				order.setReason( Orders.Reason.PROVIDER_RECONSIDERED ); //TODO - user to select reason
				order.setDateSatisfied( Date.today());
				order.write( reca );
			}
				
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.ORDERS_DELETE, ptRec, Pm.getUserRec(), reca, null );
			
			ordersListbox.removeItemAt( ordersListbox.getSelectedIndex());
		}
			
		return;
	}

}
