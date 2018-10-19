package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;

public class NotesRemoveWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	Class<? extends Notes> noteClass = null;
	
	
	
	Window notesRemoveWin;		// autowired - window	
	Reca noteReca = null;		// par reca
	
	Label lblDate;				// autowired
	Label lblDesc;				// autowired
	Label lblNote;				// autowired
	
	Label lblPtName;			// autowired
	
	
	
		
	
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
				try{ noteReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
				try{ noteClass = (Class<? extends Notes>) myMap.get( "noteClass" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have a note type
		if ( noteClass == null ) SystemHelpers.seriousError( "noteClass==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );

		// make sure we have a valid noteReca
		if ( ! Reca.isValid( noteReca )) SystemHelpers.seriousError( "bad noteReca" );

		
		// set window title
		notesRemoveWin.setTitle( "Remove " + Notes.getTitle( noteClass ));
		
		// Set ptname in window
		lblPtName.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
				
		// Read info
		Notes note = Notes.newInstance( noteClass );
		//try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
		note.read( noteReca );
		
		lblDate.setValue( note.getDate().getPrintable(9));		
		lblDesc.setValue( note.getDesc());
		lblNote.setValue( note.getNoteText());		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean delete(){
		
		Notes note = null;		// medication info
		

		
		//System.out.println( "deleting..." );
		
		// make sure we have a valid reca
		if ( ! Reca.isValid( noteReca )) SystemHelpers.seriousError( "bad reca" );
	
		// remove note (just mark note status as removed)
		try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
		note.read( noteReca );
		
		note.setStatus( Notes.Status.REMOVED );
		note.write( noteReca );
		
		// remove entry from carelog
		MrLog.removeEntry( note.getPtRec(), note.getDate(), Notes.getMrLogTypes( noteClass ), noteReca );
		
		
		// log the access
		AuditLogger.recordEntry( Notes.getAuditLogActionDelete( noteClass ), ptRec, Pm.getUserRec(), noteReca, null );		
		
	    // Notify listeners 
		Notifier.notify( ptRec, Notes.getNotifierEvent( noteClass ));
		Notifier.notify( ptRec, Notifier.Event.CARELOG );

		return true;
	}

	
	
	
	
	
	public void onClick$btnDelete( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Are you sure that you want to delete this information?", "Delete?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){

			delete();
		}

		closeWin();
		return;
	}
	
	
	
	public void onClick$btnCancel( Event e ){		
		closeWin();
		return;
	}

	
	private void closeWin(){		
		notesRemoveWin.detach();
		return;
	}
	
		
}
