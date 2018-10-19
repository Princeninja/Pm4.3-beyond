package palmed;

import java.util.EnumSet;
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
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.Procedure.Type;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class ProcedureEditWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window procedureEditWin;		// autowired - window	
	Reca procReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtMisc;		// autowired
	Textbox txtNote;		// autowired
	
	Label	lblProcedure;	// autowired
	Label	lblCode;		// autowired
	
	Listbox lboxTypes;		// autowired
	Listbox lboxProbTbl;	// autowired
	Listbox lboxRdoc;		// autowired
	
	Textbox txtSearch;		// autowired
	
	Checkbox cbMisc;		// autowired
	

	Row rowMed1;
	Row rowMed2;
	Button btnSelect;
	Button btnSearch;
	Listhead lhdr;
		
	Rec probTblRec = null;
	
	
	
	
	
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
				try{ procReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );

		// if editing, make sure we have a valid parReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( procReca ))) SystemHelpers.seriousError( "bad Reca" );

		
		// Set ptname in window
		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		// fill listboxes
		for ( Procedure.Type r : EnumSet.allOf(Procedure.Type.class))
			ZkTools.appendToListbox( lboxTypes, r.getLabel(), r );
		
		//Rdoc.fillListbox( lboxRdoc );
		
		
		if ( operation == EditPt.Operation.NEWPT ){

		
		
		

			// Create a new PAR entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			Procedure proc = new Procedure( procReca );
				

			
			
			// Get info from data struct
			
			txtDate.setValue( proc.getDate().getPrintable(9));
			
			
			if ( proc.getFlgMisc()){
				// misc description
				cbMisc.setChecked( true );
				txtMisc.setValue( proc.getMiscDesc());
				
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				cbMisc.setChecked( false );
				lblProcedure.setValue( proc.getDesc());
				lblCode.setValue( proc.getCode());
				probTblRec = proc.getProbTblRec();
			}
			
			// set controls based on cbMisc
			onCheck$cbMisc( null );
			
			ZkTools.setListboxSelectionByValue( lboxTypes, proc.getType());
			if ( Rec.isValid( proc.getRdocRec())) ZkTools.setListboxSelectionByValue( lboxRdoc, proc.getRdocRec());
			
			txtNote.setValue( proc.getNoteTxt());		
		}

		
		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Procedure proc;	
		



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
		
		// verify medication selected/entered
		if ( cbMisc.isChecked()){
			String s = txtMisc.getValue();
			if (( s == null ) || ( s.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid description: " + s + "." );
				return false;
			}
			
		} else {
			
			if ( ! Rec.isValid( probTblRec )){
				DialogHelpers.Messagebox( "No item selected." );
				return false;
			}
		}
		
		// verify type entered
		if ( lboxTypes.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "Please select a type: procedure, surgery, illness, or injury." );
			return false;
		}
		
		
		
		
		Procedure.Type type = (Procedure.Type) ZkTools.getListboxSelectionValue( lboxTypes );
		String note = txtNote.getValue().trim();
		Rec rdocRec = ( lboxRdoc.getSelectedCount() > 0 ) ? (Rec) ZkTools.getListboxSelectionValue( lboxRdoc ): new Rec();

		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			System.out.println( "Editing existing record, procReca=" + procReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( procReca )) SystemHelpers.seriousError( "bad reca" );
		
			// Read par
			proc = new Procedure( procReca );
			// ptRec should already be set - proc.setPtRec( ptRec );
			proc.setDate( date );
			// validity/status byte should already be set - cmed.setStatus( Procedure.Status.CURRENT );
			proc.setType( type );
			proc.setNoteTxt( note );
			proc.setRdocRec( rdocRec );
				
			if ( cbMisc.isChecked()){
				proc.setMiscDesc( txtMisc.getValue());
				proc.setFlgMisc( true );
			} else {
				proc.setMiscDesc( lblProcedure.getValue());
				proc.setProbTblRec( probTblRec );
				proc.setFlgMisc( false );
			}
			proc.write( procReca );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROCEDURES_EDIT, ptRec, Pm.getUserRec(), procReca, null );
			
			
			
			
			
		} else { 
			
			// post new procedure record
			if ( cbMisc.isChecked()){
				Procedure.postNewMisc( ptRec, date, type, txtMisc.getValue(), note, rdocRec );
			} else {
				Procedure.postNew( ptRec, date, type, probTblRec, lblProcedure.getValue(), note, rdocRec );
			}
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROCEDURES_ADD, ptRec, Pm.getUserRec(), procReca, null );
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
		procedureEditWin.detach();
		return;
	}
	
	
	


	
	
	

	
	// Search for matching entries in ProbTbl
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "Please enter at least 3 letters to search for." );
			return;
		}
		
		usrlib.ZkTools.listboxClear( lboxProbTbl );		
		ProbTbl.fillListbox( lboxProbTbl, s );
		return;
	}
	
	public void onOK$srcstr( Event ev ){ onClick$btnSearch( ev ); }
	
	
	// Select a matching medication/appergen from the NCAllergies table
	
	public void onClick$btnSelect( Event ev ){

		// make sure an item was selected
		if ( lboxProbTbl.getSelectedCount() < 1 ) return;
		
		// get selected item's Rec
		probTblRec = (Rec) lboxProbTbl.getSelectedItem().getValue();
		String drugName = lboxProbTbl.getSelectedItem().getLabel();
		String drugID = ProbTbl.getSNOMED( probTblRec );
		if (( drugName == null ) || ( drugID == null )) { SystemHelpers.seriousError( "bad probTbl data" ); return; }

		// copy to fields		
		

		lblProcedure.setValue( drugName );
		lblCode.setValue( drugID );
		
		return;
	}

	public void onCheck$cbMisc( Event ev ){
		
		boolean flg = cbMisc.isChecked();
		
		rowMed1.setVisible( ! flg );
		rowMed2.setVisible( ! flg );
		btnSelect.setDisabled( flg );
		btnSearch.setDisabled( flg );
		lboxProbTbl.setDisabled( flg );
		lhdr.setVisible( ! flg );
		txtMisc.setDisabled( ! flg );
		txtSearch.setDisabled( flg );		
		return;
	}
	


}
