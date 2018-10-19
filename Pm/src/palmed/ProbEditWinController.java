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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class ProbEditWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window probEditWin;	// autowired - window	
	Reca probReca = null;		// vitals reca
	Rec probTblRec = null;		// rec link to ProbTbl
	
	Textbox date;		// autowired
	Textbox misc;		// autowired
	Label	lblProblem;	// autowired
	Label	lblCode;	// autowired
	Listbox probTblListbox;	// autowired
	Textbox srcstr;		// autowired
	Radio	rbAcute;	// autowired
	Radio	rbChronic;	// autowired
	
	
	
		
	
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
		
		
		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			// Create a new set of vitals
			
			// set date to today's date
			date.setValue( Date.today().getPrintable(9));
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read vitals info 
			if (( probReca == null ) || ( probReca.getRec() < 2 )) SystemHelpers.seriousError( "ProbEditWinController() bad reca" );
			Prob prob = new Prob( probReca );
				

			
			
			// Get prob info from data struct
			
			date.setValue( prob.getStartDate().getPrintable(9));
			
			Rec probTblRec = prob.getProbTblRec();
			
			if ( ! Rec.isValid( probTblRec )){
				// misc description
				misc.setValue( prob.getMiscDesc());
			} else {
				//prob.getDesc
				//TODO - set problem listbox selection?????
				ProbTbl probTbl = new ProbTbl( probTblRec );
				
				lblProblem.setValue( probTbl.getDesc());
				String code = "SNOMED: " + probTbl.getSNOMED();
				String icd9 = probTbl.getICD9();
				if ( icd9.length() > 1 ){
					code = code + "\nICD-9: " + icd9;
				}
				lblCode.setValue( code );
			}
			
			
			// acute/chronic
			int type = prob.getType();
			if ( type == 'A' ) rbAcute.setChecked( true );
			if ( type == 'C' ) rbChronic.setChecked( true );

			//TODO - history of???
		
			
		}

		
		// load problem Master Table
		//loadProbTblList();
		
		

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
		
		Date pdate = new Date( date.getValue());
		if (( pdate == null ) || ( ! pdate.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + date.getValue() + "." );
			return;			
		}
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new prob record
			System.out.println( "Creating new prob record..." );
			prob = new Prob();
			// set validity/status byte
			prob.setStatus( 'C' );
		
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( probReca == null ) || ( probReca.getRec() < 2)) SystemHelpers.seriousError( "ProbEditWinController.save() bad reca" );
	
			// Read prob
			prob = new Prob( probReca );
			// validity/status byte should already be set
			//prob.setStatus( 'C' );

		}
	
		
		
		
		// Load data into prob dataBuffer

		prob.setPtRec( ptRec );
		prob.setStartDate( pdate );

		if (( probTblRec != null ) && ( probTblRec.getRec() > 1 )){
			
			prob.setProbTblRec( probTblRec );
			prob.setMiscDesc( "" );

		} else {
			
			prob.setProbTblRec( new Rec( 0 ));
			prob.setMiscDesc( misc.getValue());
		}
		
		byte type = 0;
		if ( rbAcute.isChecked()) type = 'A';
		if ( rbChronic.isChecked()) type = 'C';
		prob.setType( type );
		
		
		//prob.dump();	

		
		// Save (write) records back to database
		if (( operation == EditPt.Operation.NEWPT ) || ( probReca == null ) || ( probReca.getRec() == 0 )){
			
			// new prob

			// write new prob record
			probReca = prob.postNew( ptRec );
			
			// unset 'No Problems' flag
			ProbUtils.setNoProbsFlag( ptRec, false );
			
			//System.out.println( "new prob created probReca=" + probReca.toString());
				
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROB_ADD, ptRec, Pm.getUserRec(), probReca, null );
			
			
			
		} else {	// EditPt.Operation.EDITPT
			
			
			// TODO - really handle edit flag
			//prob.setEdits( prob.getEdits() + 1 );
			
			// existing prob
			prob.write( probReca );
			//System.out.println( "edited prob info written, probReca=" + probReca.toString());

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROB_EDIT, ptRec, Pm.getUserRec(), probReca, null );
			

		}
		//prob.dump();
		
		return;
	}

	
	
//	public void onClose$vitalsNewWin( Event e ) throws InterruptedException{
//		alert( "onClose event");
//		onClick$cancel( e );
//		return;
//	}
	
	
	
	public void onClick$save( Event e ) throws InterruptedException{

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
	
	
	
	public void onClick$cancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			closeWin();
		}
		
		return;
	}

	
	private void closeWin(){
		
		// close Win
		probEditWin.detach();
		return;
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void loadProbTblList(){ loadProbTblList( null ); }
	
	public void loadProbTblList( String searchString ){

		if ( probTblListbox == null ) return;
		
		// remove all items
		ZkTools.listboxClear( probTblListbox );
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		ProbTbl probTbl = ProbTbl.open();
		
		while ( probTbl.getNext()){
			
			int fnd = 1;
			

			// is active ?
			if ( ProbTbl.Status.ACTIVE != probTbl.getStatus()) continue;
			
			if (( searchString != null )
				&& (( probTbl.getAbbr().toUpperCase().indexOf( s ) < 0 )
				&& ( probTbl.getDesc().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getSNOMED().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getICD9().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getICD10().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getCode4().toUpperCase().indexOf( s ) < 0 )
				)){					
				// this one doesn't match
				fnd = 0;
			}
	
			if ( fnd > 0 ){
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( probTblListbox );
				i.setValue( probTbl.getRec());
				
				//new Listcell( probTbl.getAbbr()).setParent( i );
				new Listcell( probTbl.getDesc()).setParent( i );
				//new Listcell( probTbl.getSNOMED()).setParent( i );
				//new Listcell( probTbl.getICD9()).setParent( i );
				//new Listcell( probTbl.getICD10()).setParent( i );
				//new Listcell( probTbl.getCode4()).setParent( i );
				//new Listcell( /*Dgn.read( prob.getDgnRec()).getCode()*/ "V101.5" ).setParent( i );;
			}
		}
		
		probTbl.close();
		

		return;
	}

	
	// Open dialog to enter new vitals data
	
	public void onClick$search( Event ev ){

		String s = srcstr.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least three letters in the search field." );
			return;
		}
		loadProbTblList(( s.length() < 1 ) ? null: s );
		return;
	}

	public void onOK$srcstr( Event ev ){ onClick$search( ev ); }
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$select( Event ev ){

		// make sure an item was selected
		if ( probTblListbox.getSelectedCount() < 1 ) return;
		// get selected item's Rec
		Rec pRec = (Rec) probTblListbox.getSelectedItem().getValue();
		if ( ! Rec.isValid( pRec )) { SystemHelpers.seriousError( "ProbEditWinController.onClick$select() bad pRec" ); return; }
		// copy to fields
		ProbTbl p = new ProbTbl( pRec );
		lblProblem.setValue( p.getDesc());
		
		String code = "SNOMED: " + p.getSNOMED();
		String icd9 = p.getICD9();
		if ( icd9.length() > 1 ){
			code = code + "\nICD-9: " + icd9;
		}
		lblCode.setValue( code );
		
		probTblRec = pRec;
		return;
	}	
	

}
