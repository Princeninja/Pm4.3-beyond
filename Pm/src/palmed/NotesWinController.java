package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class NotesWinController extends GenericForwardComposer {

	private Window notesWin;			// autowired
	private Listbox notesListbox;		// autowired
	private Textbox noteTextbox;		// autowired
	
	private Rec	ptRec;		// patient record number
	//private Notes.NoteType noteType;	// note type
	Class<? extends Notes> noteClass;
	

	
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
//				try{ noteType = (Notes.NoteType) myMap.get( "noteType" ); } catch ( Exception e ) { /* ignore */ };
				try{ noteClass = (Class<? extends Notes>) myMap.get( "noteClass" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have a note class (or noteType)
		if ( noteClass == null ) SystemHelpers.seriousError( "noteClass==null" );
		System.out.println( "noteClass=" + noteClass.getName());
		
/*		
		try {
			noteType = (NoteType) noteClass.getMethod( "getNoteType", null ).invoke( null );
		} catch ( Exception e ){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( noteType == null ) SystemHelpers.seriousError( "noteType==null" );
*/
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptrec" );

		
		
		refreshList();
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notes.getNotifierEvent( noteClass ));
		return;
	}
	
	
	
	
	
	
	public void refreshList(){
		
		
		ZkTools.listboxClear( notesListbox );
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = null;
				
		reca = Notes.getMedPtReca( noteClass, medPt );
		
		
		Notes note = null;
		
		for ( ; reca.getRec() != 0; reca = note.getLLHdr().getLast()){
			
			note = Notes.newInstance( noteClass );
			
			//try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
			note.read( reca );
			if ( note.getStatus() != Notes.Status.CURRENT ) continue;
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( notesListbox );
			new Listcell( note.getDate().getPrintable(9)).setParent( i );
			new Listcell( note.getDesc()).setParent( i );
			new Listcell( pmUser.getUser( note.getUserRec())).setParent( i );
			i.setValue( reca );		// set reca into listitem for later retrieval
		}

		return;
	}
	
	
	
	public void onSelect$notesListbox( Event e ){
		
		Notes note = null;
		//try { note = noteClass.newInstance(); } catch ( Exception e1 ){ e1.printStackTrace(); }
		note = Notes.newInstance( noteClass );
		note.read( (Reca) notesListbox.getSelectedItem().getValue());
		String txt = note.getNoteText();		
		noteTextbox.setValue( txt );
		return;
	}


	public void onClick$btnAdd(){
		
		NotesEdit.add( noteClass, ptRec, notesWin );
		return;
	}
	
	public void onClick$btnEdit(){
		
		if ( notesListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca noteReca = (Reca) notesListbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(noteReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		NotesEdit.edit( noteClass, ptRec, noteReca, notesWin );
		return;
	}
	
	public void onClick$btnDelete(){
		
		if ( notesListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca noteReca = (Reca) notesListbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(noteReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		NotesRemove.remove( noteClass, ptRec, noteReca, notesWin );
		return;
	}
	
	
	

}
