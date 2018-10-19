package palmed;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;

public class NotesEditWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	//Notes.NoteType noteType = null;
	Class<? extends Notes> noteClass;
	
	
	Window notesEditWin;		// autowired - window	
	Reca noteReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtDesc;		// autowired
	Textbox txtNote;		// autowired
	
	Label lblPtName;		// autowired
	
	
	
		
	
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
				try{ noteReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
				try{ noteClass = (Class<? extends Notes>) myMap.get( "noteClass" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "operation==null" );
		
		// make sure we have a note class
		if ( noteClass == null ) SystemHelpers.seriousError( "noteClass==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );

		// if editing, make sure we have a valid noteReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( noteReca ))) SystemHelpers.seriousError( "bad noteReca" );

		
		// set window title
		notesEditWin.setTitle((( operation == EditPt.Operation.NEWPT ) ? "Enter ": "Edit " ) + Notes.getTitle( noteClass ));
		
		// Set ptname in window
		lblPtName.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		if ( operation == EditPt.Operation.NEWPT ){

		
		
		

			// Create a new PAR entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info
			Notes note = Notes.newInstance( noteClass );
			note.read( noteReca );
			
			
			// Get info from data struct
			
			txtDate.setValue( note.getDate().getPrintable(9));
			
			txtDesc.setValue( note.getDesc());
			txtNote.setValue( note.getNoteText());		
		}

		
		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Notes note = null;		// medication info
		



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
		
		
		// verify some desc entered
		String desc =  txtDesc.getValue().trim();
		if ( desc.length() < 1 ){
			DialogHelpers.Messagebox( "No description entered." );
			return false;
		}
		
		// verify some text entered
		if ( txtNote.getValue().trim().length() < 1 ){
			DialogHelpers.Messagebox( "No note text entered." );
			return false;
		}
		
		
		
		
		//System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

				
			// make sure we have a valid reca
			if ( ! Reca.isValid( noteReca )) SystemHelpers.seriousError( "bad reca" );
		
			// Read note
			note = Notes.newInstance( noteClass );
			note.read( noteReca );
			// validity/status byte should already be set
			note.setNumEdits( note.getNumEdits() + 1 );
			

		} else { 
			
			// create a new par record
			note = Notes.newInstance( noteClass );
			//try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
			
			note.setPtRec( ptRec );
			note.setUserRec( Pm.getUserRec());
			note.setStatus( Notes.Status.CURRENT );
			note.setNumEdits( 0 );
		}
	
		
		// Set new info into Par object
		
		note.setDate( date );
		note.setDesc( desc );
		note.setNoteText( txtNote.getValue().trim());
		
		// set type
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//par.setEdits( prob.getEdits() + 1 );
			
			// existing note
			note.write( noteReca );
			// log the action
			AuditLogger.recordEntry( Notes.getAuditLogActionEdit( noteClass ), ptRec, Pm.getUserRec(), noteReca, null );
			
			
		} else {			// Edit.Operation.NEWPT
			
			// write the new note rec
			noteReca = note.postNew( ptRec );		

			// post to MrLog
		    MrLog.postNew( ptRec, date, desc, Notes.getMrLogTypes( noteClass ), noteReca );
			// log the action
			AuditLogger.recordEntry( Notes.getAuditLogActionNew( noteClass ), ptRec, Pm.getUserRec(), noteReca, null );

		}
		
		
		
	    // Notify listeners 
		Notifier.notify( ptRec, Notes.getNotifierEvent( noteClass ));
		Notifier.notify( ptRec, Notifier.Event.CARELOG );

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
		notesEditWin.detach();
		return;
	}
	
	
	
	
	
}
