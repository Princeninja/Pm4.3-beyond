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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class HxProceduresWinController extends GenericForwardComposer {

	private Listbox lboxProcedures;		// autowired
	private Radio r_all;				// autowired
	private Radio r_procedures;			// autowired
	private Radio r_surgeries;			// autowired
	private Radio r_illnesses;			// autowired
	private Radio r_injuries;			// autowired
	private Window proceduresWin;		// autowired
	
	
	private Rec	ptRec;		// patient record number

	
	

	
	
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
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );

	
		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){
	
		if ( lboxProcedures == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxProcedures );
		
		

		// populate list
		DirPt dirPt = new DirPt( ptRec );

		MedPt medPt = new MedPt( dirPt.getMedRec());
		Procedure proc = null;

		for ( Reca reca = medPt.getProceduresReca(); Reca.isValid( reca ); reca = proc.getLLHdr().getLast()){
			
			proc = new Procedure( reca );
			if ( proc.getStatus() != Procedure.Status.CURRENT ) continue;
				
			if ( r_procedures.isChecked() && ( proc.getType() != Procedure.Type.PROCEDURE )) continue;
			if ( r_surgeries.isChecked() && ( proc.getType() != Procedure.Type.SURGERY )) continue;
			if ( r_illnesses.isChecked() && ( proc.getType() != Procedure.Type.ILLNESS )) continue;
			if ( r_injuries.isChecked() && ( proc.getType() != Procedure.Type.INJURY )) continue;
			
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( lboxProcedures );
			i.setValue( reca );
			
			String str1 = proc.getDesc();
			String str2 = proc.getNoteTxt();
			if (( str2 != null ) && ( str2.length() > 0 )) str1 = str1 + " - " + str2;
			
			new Listcell( proc.getDate().getPrintable(9)).setParent( i );
			new Listcell( str1 ).setParent( i );
			new Listcell( proc.getType().getLabel()).setParent( i );
			new Listcell( proc.getCode()).setParent( i );
		}

		return;
	}
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$btnNew( Event ev ){
		
		if ( ProcedureEdit.enter( ptRec, (Component) proceduresWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem
	
	public void onClick$btnEdit( Event ev ){
	
		// was an item selected?
		if ( lboxProcedures.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) lboxProcedures.getSelectedItem().getValue();
		if ( ! Reca.isValid( reca )) return;

		// call edit routine
		if ( ProcedureEdit.edit( ptRec, reca, (Component) proceduresWin )){
			refreshList();
		}		
		return;
	}
	
	
	
	
	
	// Open dialog to remove an existing problem
	
	public void onClick$btnRemove( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( lboxProcedures.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) lboxProcedures.getSelectedItem().getValue();
		if ( ! Reca.isValid( reca )) return;

		// is user sure?
		if ( DialogHelpers.Optionbox( "Procedures History", "Really remove the selected item?", "Yes", "No", null ) != 1 ) return;
		
		// mark procedure removed
		Procedure.markRemoved( reca );
		
		// refresh list
		refreshList();	
		return;
	}
	
	
	public void onClick$btnPrint(){

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PROB_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		return;
	}
	
	public void onCheck$r_all( Event ev ){ refreshList(); }
	public void onCheck$r_procedures( Event ev ){ refreshList(); }
	public void onCheck$r_injuries( Event ev ){ refreshList(); }
	public void onCheck$r_illnesses( Event ev ){ refreshList(); }
	public void onCheck$r_surgeries( Event ev ){ refreshList(); }

	
	
}
