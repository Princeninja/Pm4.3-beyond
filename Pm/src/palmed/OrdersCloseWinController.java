package palmed;

import java.util.EnumSet;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.Orders.Status;
import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class OrdersCloseWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	
	Label ptname;			// autowired
	
	
	Window ordersCloseWin;		// autowired - window	
	Reca orderReca = null;		// reca
	
	Label lblDate;			// autowired
	Label lblDesc;			// autowired
	Label lblNote;			// autowired
	Label lblIndications;	// autowired
	
	//Label	lblEtc;			// autowired
	
	Label lblStatus;		// autowired
	Label lblProvider;		// autowired
	Label lblPriority;		// autowired
	Textbox txtDateSatisfied;	// autowired
	
	Listbox lboxReason;		// autowired
	
	
	
	
		
	
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
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ orderReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "OrdersAddWinController() bad ptRec" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill some selection listboxes
		for ( Orders.Reason r : EnumSet.allOf(Orders.Reason.class))
			ZkTools.appendToListbox( lboxReason, r.getLabel(), r );



		
		// Read info 
		if ( ! Reca.isValid( orderReca )) SystemHelpers.seriousError( "MedAddWinController() bad reca" );
		Orders order = new Orders( orderReca );
		
		lblDate.setValue( order.getDate().getPrintable(9));
		lblDesc.setValue( order.getDesc());
		lblNote.setValue( order.getNoteTxt());
		lblIndications.setValue( order.getIndicationsText());
		
		lblPriority.setValue( order.getPriority().getLabel());
		
		txtDateSatisfied.setValue( Date.today().getPrintable(9));
		

		//ZkTools.setListboxSelectionByValue( lboxStatus, Orders.Status.ORDERED );
			
		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		Orders order;		// medication info
		



		// TODO - VALIDATE DATA
		
		if ( lboxReason.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "A Reason must be selected." );
			return;			
		}
	
		// verify valid close date
		Date date = new Date( txtDateSatisfied.getValue());
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtDateSatisfied.getValue() + "." );
			return;			
		}
		
		
		
		
		
		System.out.println( "Saving..." );
		
		// make sure we have a valid reca
		if ( ! Reca.isValid( orderReca )) SystemHelpers.seriousError( "OrderEditWinController.save() bad reca" );
	
		// Read cmed
		order = new Orders( orderReca );
		
		order.setStatus( Orders.Status.CLOSED );
		order.setReason( (Orders.Reason) ZkTools.getListboxSelectionValue( lboxReason ));
		order.setDateSatisfied( date );
		
		// existing order
		order.write( orderReca );
		System.out.println( "edited order info written, reca=" + orderReca.toString());

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.ORDERS_CLOSE, ptRec, Pm.getUserRec(), orderReca, order.getDesc());
		

		// Notify other windows of new orders
		Notifier.notify( ptRec, Notifier.Event.ORDERS );

		return;
	}

	
	
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			save();
			//progBox.close();
			closeWin();
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$btnCancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			closeWin();
		}		
		return;
	}

	
	private void closeWin(){		
		// close Win
		ordersCloseWin.detach();
		return;
	}
	
	
	

}
