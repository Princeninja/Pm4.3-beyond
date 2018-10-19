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

public class ParRemoveWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	Reca parReca = null;		// cmed record
	
	Label ptname;			// autowired
	
	
	Window parRemoveWin;		// autowired - window	
	
	Textbox txtDate;		// autowired
	
	Label	lblMed;			// autowired
	Label	lblCode;		// autowired
	Label	lblEtc;			// autowired
	Label	lblSymptoms;	// autowired
	Label	lblNote;		// autowired
	Label	lblStart;		// autowired
	
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
				try{ parReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ParRemoveWinController() bad ptRec" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill reasom selection listbox
		ZkTools.appendToListbox( lboxReason, "", Par.Reason.UNSPECIFIED );
		ZkTools.appendToListbox( lboxReason, "Entered in error", Par.Reason.ERROR );
		ZkTools.appendToListbox( lboxReason, "Other reason", Par.Reason.UNSPECIFIED );

		

		// set date to today's date
		txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		// Read info 
		if ( ! Reca.isValid( parReca )) SystemHelpers.seriousError( "ParRemoveWinController() bad reca" );
		Par par = new Par( parReca );
				
		lblStart.setValue( par.getDate().getPrintable(9));
		lblMed.setValue( par.getParDesc());
		
		String code = String.valueOf( par.getCompositeID());
		lblCode.setValue( code );
		
		if (( code != null ) && ( code.length() > 0 )){
			NCAllergies nc = NCAllergies.readMedID( code );
			if ( nc != null ){
				lblEtc.setValue( nc.getConceptID() + ", " + nc.getConceptType());
			}
		}
		
		lblSymptoms.setValue( par.getSymptomsText( false ));
		lblNote.setValue( par.getNoteTxt());
		

		// verify PAR selected is a current PAR
		if ( par.getStatus() != Par.Status.CURRENT ){
			DialogHelpers.Messagebox( "PAR selected must be a 'Current' PAR." );
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
		if ( ! Reca.isValid( parReca)) 
			SystemHelpers.seriousError( "ParRemoveWinController.save() bad reca" );
	
		// Read par
		Par par = new Par( parReca );
		
		// verify valid DC date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid()) || ( date.compare( par.getDate() ) < 0 )){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return false;			
		}
	
		// verify DC reason selected/entered
		if ( lboxReason.getSelectedCount() < 1 ){
				DialogHelpers.Messagebox( "No removed 'Reason' selected." );
				return false;
		}

		

		System.out.println( "Saving..." );
		
		// set validity/status byte and stop date
		par.setStatus( Par.Status.REMOVED );			
		par.setStopDate( date );
		par.setRemovedReason( (Par.Reason) ZkTools.getListboxSelectionValue( lboxReason ) );
		
		// Save (write) records back to database
		par.write( parReca );
		
		//System.out.println( "edited par info written, reca=" + parReca.toString());
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PAR_DELETE, ptRec, Pm.getUserRec(), parReca, null );
		
		
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
		parRemoveWin.detach();
		return;
	}
	
}
