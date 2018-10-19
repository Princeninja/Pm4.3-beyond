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

import palmed.Intervention.Types;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class InterventionAddWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window ivAddWin;		// autowired - window	
	Reca ivReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtNote;		// autowired
	
	Listbox lboxInt;		// autowired
	
	
		
	
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
				try{ operation = (EditPt.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ ivReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );

		// if editing, make sure we have a valid ivReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( ivReca ))) SystemHelpers.seriousError( "bad ivReca" );

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		for ( Intervention.Types r : EnumSet.allOf(Intervention.Types.class))
			ZkTools.appendToListbox( lboxInt, r.getLabel(), r.getCode());
		

		
		
		
		if ( operation == EditPt.Operation.NEWPT ){

		
		
		

			// Create a new PAR entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			Intervention iv = new Intervention( ivReca );
				

			
			
			// Get info from data struct
			
			txtDate.setValue( iv.getDate().getPrintable(9));
			ZkTools.setListboxSelectionByValue(lboxInt, iv.getType().getCode());
			
			
		
			txtNote.setValue( iv.getNoteText());		
		}

		
		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Intervention iv;		// medication info
		



		// TODO - VALIDATE DATA
		
		// verify valid start date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return false;			
		}
	
		// verify valid ptrec
		if ( ! Rec.isValid( ptRec )){
			DialogHelpers.Messagebox( "Invalid ptrec:" + ptRec.getRec());
			return false;
		}
		
		// verify intervention selected
		if ( lboxInt.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No intervention selected." );
		}
		
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			System.out.println( "Editing existing Par record, ivReca=" + ivReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( ivReca )) 
				SystemHelpers.seriousError( "InterventionWinController.save() bad reca" );
		
			// Read par
			iv = new Intervention( ivReca );
			// validity/status byte should already be set
			//cmed.setStatus( 'C' );
				
		} else { 
			
			// create a new par record
			System.out.println( "Creating new Intervention record..." );
			iv = new Intervention();
			// set validity/status byte
			iv.setStatus( Intervention.Status.CURRENT );			
			iv.setPtRec( ptRec );
		}
	
		
		// Set new info into Par object
		
		iv.setDate( date );
		iv.setType( Types.get( (Integer) ZkTools.getListboxSelectionValue( lboxInt )));
		iv.setNoteText( txtNote.getValue());
		
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			
			// existing 
			iv.write( ivReca );
			System.out.println( "edited par info written, reca=" + ivReca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.INTERVENTIONS_EDIT, ptRec, Pm.getUserRec(), ivReca, null );
			

		} else {			// Edit.Operation.NEWPT
			
			// new par
			// write the new par rec
			Reca reca = iv.postNew( ptRec );
			System.out.println( "new info written, reca=" + reca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.INTERVENTIONS_ADD, ptRec, Pm.getUserRec(), ivReca, null );
			
			// done

		}
		
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
		ivAddWin.detach();
		return;
	}
	
	
	




}
