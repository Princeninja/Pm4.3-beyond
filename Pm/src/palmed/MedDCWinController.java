package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class MedDCWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	Reca cmedReca = null;		// cmed record
	
	Label ptname;			// autowired
	
	
	Window medDCWin;		// autowired - window	
	
	Textbox txtDate;		// autowired
	
	Label	lblMed;			// autowired
	Label	lblCode;		// autowired
	Label	lblEtc;			// autowired
	Label	lblSig;			// autowired
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
				try{ cmedReca = (Reca) myMap.get( "cmedReca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "MedAddWinController() bad ptRec" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill some selection listboxes
//TODO Reason codes
		lboxReason.appendItem( "", "" );
		lboxReason.appendItem( "Provider discontinued", "1" );
		lboxReason.appendItem( "Patient discontinued", "2" );
		lboxReason.appendItem( "Adverse event", "3" );
		lboxReason.appendItem( "Patient cost", "4" );
		lboxReason.appendItem( "Insurance not covered", "5" );
		lboxReason.appendItem( "Entered in error", "6" );
		lboxReason.appendItem( "Allergy/Contraindicated", "7" );
		lboxReason.appendItem( "Another provider discontinued", "8" );

		

		// set date to today's date
		txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		// Read info 
		if ( ! Reca.isValid( cmedReca )) SystemHelpers.seriousError( "MedDCWinController() bad reca" );
		Cmed cmed = new Cmed( cmedReca );
				
		lblStart.setValue( cmed.getStartDate().getPrintable(9));
		lblMed.setValue( cmed.getDrugName());
		
		String code = cmed.getDrugCode();
		lblCode.setValue( code );
		
		if (( code != null ) && ( code.length() > 0 )){
			NCFull nc = NCFull.readMedID( cmed.getDrugCode());
			if ( nc != null ){
				lblEtc.setValue( nc.getMedEtc());
			}
		}
		
		lblSig.setValue( MedSIG.getPrintableSIG( cmed ));


		// verify medication selected is a current medication
		if ( cmed.getStatus() != Cmed.Status.CURRENT ){
			DialogHelpers.Messagebox( "Medication selected must be a 'Current' medication." );
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
		
		// verify valid cmedReca
		if ( ! Reca.isValid( cmedReca)) 
			SystemHelpers.seriousError( "MedDCWinController.save() bad reca" );
	
		// Read cmed
		Cmed cmed = new Cmed( cmedReca );
		
		
		// verify valid DC date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid()) || ( date.compare( cmed.getStartDate() ) < 0 )){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return false;			
		}
	
		// verify DC reason selected/entered
		if ( lboxReason.getSelectedCount() < 1 ){
				DialogHelpers.Messagebox( "No discontinue 'Reason' selected." );
				return false;
		}

		

		System.out.println( "Saving..." );
		
		// set validity/status byte and stop date
		cmed.setStatus( Cmed.Status.DISCONTINUED );			
		cmed.setStopDate( date );

		// Save (write) records back to database
		cmed.write( cmedReca );
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_DC, ptRec, Pm.getUserRec(), cmedReca, null );
		
		System.out.println( "edited cmed info written, reca=" + cmedReca.toString());
		
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
		medDCWin.detach();
		return;
	}
	
}
