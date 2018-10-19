package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;

public class ProbResWinController extends GenericForwardComposer {
	
	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired

	Window probResWin;		// autowired - window	
	Reca probReca = null;	// vitals reca	
	
	Label lblStartDate;		// autowired
	Label	lblProblem;		// autowired
	Label	lblType;		// autowired
	Label	lblCode;		// autowired
	Label	lblAction;		// autowired
	Textbox txtExp;			// autowired
	Textbox txtStopDate;	// autowired
	
	
		
	
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
				try{ probReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "ProbEditWinController() operation==null" );
		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )) SystemHelpers.seriousError( "ProbEditWinController() bad ptRec" );
		

		
		// Set ptname in window
		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		
				
		// Read Prob info 
		if (( probReca == null ) || ( probReca.getRec() < 2 )) SystemHelpers.seriousError( "ProbEditWinController() bad reca" );
		Prob prob = new Prob( probReca );
			

		
		
		// Get prob info from data struct
		lblStartDate.setValue( prob.getStartDate().getPrintable(9));
		
		int type = prob.getType();
		if ( type == 'A' ) lblType.setValue( "Acute" );
		if ( type == 'C' ) lblType.setValue( "Chronic" );

	
		Rec probTblRec = prob.getProbTblRec();
		
		if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )){
			// misc description
			lblProblem.setValue( prob.getMiscDesc());
			
		} else {
			//prob.getDesc
			//TODO - set problem listbox selection?????
			lblProblem.setValue( prob.getProbDesc());
			lblCode.setValue( prob.getCode());
		}
			
		// set today's date as default
		txtStopDate.setValue( Date.today().getPrintable(9));
		
		lblAction.setValue(( operation == EditPt.Operation.NEWPT ) ? "REMOVE": "RESOLVE" );
			
		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	public void save(){
		
		Prob prob;		// prob info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into prob dataBuffer
		
		Date pdate = new Date( txtStopDate.getValue());
		if (( pdate == null ) || ( ! pdate.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtStopDate.getValue() + "." );
			return;			
		}
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if ( operation == EditPt.Operation.NEWPT ){

			// mark problem record as removed
			System.out.println( "Removing prob..." );
			Prob.markRemoved( probReca, pdate );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROB_DELETE, ptRec, Pm.getUserRec(), probReca, null );
			
		} else { 
			
			// mark problem record as removed
			System.out.println( "Resolving prob..." );
			Prob.markResolved( probReca, pdate );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROB_RESOLVE, ptRec, Pm.getUserRec(), probReca, null );
			
		}
		
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
		probResWin.detach();
		return;
	}
	

}
