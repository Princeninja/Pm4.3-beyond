package palmed;

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

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class InterventionRemoveWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	Reca ivReca = null;		// cmed record
	
	Label ptname;			// autowired
	
	
	Window ivRemoveWin;		// autowired - window	
	
	Label lblInt;
	Label lblDate;		// autowired
	
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
				try{ ivReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );
		

		
		// Set ptname in window
		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill reasom selection listbox
		ZkTools.appendToListbox( lboxReason, "", Intervention.Reason.NONE );
		ZkTools.appendToListbox( lboxReason, "Entered in error", Intervention.Reason.ERROR );
		ZkTools.appendToListbox( lboxReason, "Other reason", Intervention.Reason.UNSPECIFIED );

		

			
		// Read info 
		if ( ! Reca.isValid( ivReca )) SystemHelpers.seriousError( "ParRemoveWinController() bad reca" );
		Intervention iv = new Intervention( ivReca );
				
		lblDate.setValue( iv.getDate().getPrintable(9));
		lblInt.setValue( iv.getType().getLabel());
		

		// verify PAR selected is a current PAR
		if ( iv.getStatus() != Intervention.Status.CURRENT ){
			DialogHelpers.Messagebox( "Item selected must be a 'Current'." );
			closeWin();
		}
		
		
		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 * Returns 'true' if successful, false if validation fails and should remain in DC window to fix.
	 * 
	 */	
	
	public boolean save(){
		
		// verify valid parReca
		if ( ! Reca.isValid( ivReca)) 
			SystemHelpers.seriousError( "bad reca" );
	
		// Read par
		Intervention iv = new Intervention( ivReca );
		
		// verify DC reason selected/entered
		if ( lboxReason.getSelectedCount() < 1 ){
				DialogHelpers.Messagebox( "No removed 'Reason' selected." );
				return false;
		}

		

		System.out.println( "Saving..." );
		
		// set validity/status byte and stop date
		iv.setStatus( Intervention.Status.REMOVED );			
		//iv.setRemovedReason( (Par.Reason) ZkTools.getListboxSelectionValue( lboxReason ) );
		
		// Save (write) records back to database
		iv.write( ivReca );
		
		//System.out.println( "edited par info written, reca=" + parReca.toString());
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.INTERVENTIONS_DELETE, ptRec, Pm.getUserRec(), ivReca, null );
		
		
		return true;
	}

	
	
	
	
		public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			if ( save()){
				closeWin();
			}
			//progBox.close();
			
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
		ivRemoveWin.detach();
		return;
	}
	
}
