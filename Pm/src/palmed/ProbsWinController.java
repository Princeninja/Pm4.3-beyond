package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;

public class ProbsWinController extends GenericForwardComposer {
	
	private Listbox probs_listbox;		// autowired - problems listbox
	private Radio r_current;			// autowired
	private Radio r_resolved;			// autowired
	private Radio r_history;			// autowired
	private Listheader stop;			// autowired
	private Listheader status;			// autowired
	private Window probsWin;			// autowired - problem window (this window)
	private Groupbox gbNoProbs;			// autowired
	private Checkbox cbNoProbs;			// autowired
	
	
	private Rec	ptRec;		// patient record number

	
	

	public ProbsWinController() {
		// TODO Auto-generated constructor stub
	}

	
	
	
	
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
		if ( ptRec.getRec() < 2){
			System.out.println( "Error: ProbsWinController() ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}
	
		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$r_current( Event ev ){
		refreshList();
	}
	public void onCheck$r_resolved( Event ev ){
		refreshList();
	}
	public void onCheck$r_history( Event ev ){
		refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){

		boolean flgCurrent = false;

		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_current.isSelected()) display = 1;
		if ( r_resolved.isSelected()) display = 2;
		if ( r_history.isSelected()) display = 3;

		// display stop date column header?
		stop.setVisible((( display & 2 ) != 0 ) ? true: false );
		status.setVisible(( display == 3 ) ? true: false );

		
		if ( probs_listbox == null ) return;
		
		// remove all items
		for ( int i = probs_listbox.getItemCount(); i > 0; --i ){
			probs_listbox.removeItemAt( 0 );
		}
		
		

		// populate list
		DirPt dirPt = new DirPt( ptRec );

		MedPt medPt = new MedPt( dirPt.getMedRec());

		
		for ( Reca reca = medPt.getProbReca(); Reca.isValid( reca ); ){
			
			Prob prob = new Prob( reca );
			//String s = prob.toString();
			//System.out.println( "prob=" + s );
			
			// get problem status byte
			int status = prob.getStatus();
			// if no status byte - look at stop date & set status (to support legacy code)
			if (( status == 0 ) && prob.getStopDate().isValid()) status = 'P';

			// is this a 'Current Problem'?
			if ( status == 'C' /*Prob.Status.CURRENT*/ ) flgCurrent = true;

			
			// is this type selected?
			if ((( status == 'C' ) && (( display & 1 ) != 0 ))
				|| (( status == 'P') && (( display & 2 ) != 0 ))
				|| (( status == 'R' && ( display == 3 )))){
				// ( status == 'R' ) - removed
				// ( status == 'E' ) - superceded by edit
				
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( probs_listbox );
				i.setValue( reca );
				
				new Listcell( prob.getStartDate().getPrintable(9)).setParent( i );
				
				String s = prob.getStopDate().getPrintable(9);
				if ( s.equals( "00-00-0000" )) s = "";
				new Listcell( s ).setParent( i );
				
				// set problem status
				int stat = prob.getStatus();
				if ( stat == 'C' ) s = "Active";
				if ( stat == 'P' ) s = "Resolved";
				if ( stat == 'R' ) s = "Removed";
				new Listcell( s ).setParent( i );
				
				Rec pRec = prob.getProbTblRec();

				if ( Rec.isValid( pRec )){

					// read entry from ProbTbl
					ProbTbl probTbl = new ProbTbl( pRec );					
					new Listcell( probTbl.getDesc()).setParent( i );
					new Listcell( probTbl.getSNOMED()).setParent( i );;
					new Listcell( probTbl.getICD9()).setParent( i );;

				} else {
					// misc entry
					new Listcell( prob.getProbDesc()).setParent( i );
					new Listcell( "" ).setParent( i );
					new Listcell( "" ).setParent( i );
					//new Listcell( /*Dgn.read( prob.getDgnRec()).getCode()*/ "V101.5" ).setParent( i );;
				}
			}
			
			// get next reca in list	
			reca = prob.getLLHdr().getLast();			
		}

		
		// are there any 'Current Problems'?
		if ( flgCurrent ){
			gbNoProbs.setVisible( false );			
		} else {
			gbNoProbs.setVisible( true );
			cbNoProbs.setChecked( medPt.getNoProbsFlag());
		}

		return;
	}
	
	
	
	
	public void onCheck$cbNoProbs( Event ev ){

		ProbUtils.setNoProbsFlag( ptRec, cbNoProbs.isChecked());
		
		// notify that we have modified Probs
		Notifier.notify( ptRec, Notifier.Event.PROB );
		return;
	}
	
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$newprob( Event ev ){
		
		if ( ProbEdit.enter( ptRec, probsWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem
	
	public void onClick$edit( Event ev ){
	
		// was an item selected?
		if ( probs_listbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) probs_listbox.getSelectedItem().getValue();
		if (( reca == null ) || ( reca.getRec() < 2 )) return;

		// call edit routine
		if ( ProbEdit.edit( reca, ptRec, probsWin )){
			refreshList();
		}		
		return;
	}
	
	
	
	
	
	// Open dialog to resolve an existing problem
	
	public void onClick$btnResolved( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( probs_listbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) probs_listbox.getSelectedItem().getValue();
		if (( reca == null ) || ( reca.getRec() < 2 )) return;
		
		// call edit routine
		ProbRes.resolve( reca, ptRec, probsWin );
		refreshList();	
		return;
	}
	
	
	
	
	// Open dialog to remove an existing problem
	
	public void onClick$btnRemoved( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( probs_listbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) probs_listbox.getSelectedItem().getValue();
		if (( reca == null ) || ( reca.getRec() < 2 )) return;

		// call edit routine
		ProbRes.remove( reca, ptRec, probsWin );			
		refreshList();	
		return;
	}
	
	
	public void onClick$btnPrint(){

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PROB_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		return;
	}
	
	
	
	// Open dialog to export a problem to Public Health
	
	public void onClick$btnPubHealth( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( probs_listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}



		// export selected item only or complete immunization history?
		if ( Messagebox.show( "Export selected entry?", "Export?", (Messagebox.YES + Messagebox.NO ), Messagebox.QUESTION ) == Messagebox.YES ){
			
			// Export this one entry
			Reca probReca = (Reca) probs_listbox.getSelectedItem().getValue();
			if ( ! Reca.isValid(probReca)){
				DialogHelpers.Messagebox( "Invalid item selected." );
				return;
			}
			download( PubHealthExport.exportProb( ptRec, probReca ));
		}
		return;
	}
	
	
	public void download( String hl7 ){
		
		Filedownload.save( hl7, "text/plain", "problem.txt" );
		DialogHelpers.Messagebox( "Finished" );
	}

	
}

/**/

