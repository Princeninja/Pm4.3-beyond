package palmed;

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
import usrlib.ZkTools;

public class ParAddWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window parAddWin;		// autowired - window	
	Reca parReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtMisc;		// autowired
	Textbox txtNote;		// autowired
	
	Label	lblMed;			// autowired
	Label	lblCode;		// autowired
	Label	lblEtc;			// autowired
	
	Listbox parListbox;		// autowired
	
	Textbox txtSearch;		// autowired
	
	Checkbox cbRash;		// autowired
	Checkbox cbShock;		// autowired
	Checkbox cbDyspnea;		// autowired
	Checkbox cbNausea;		// autowired
	Checkbox cbAnemia;		// autowired
	Checkbox cbUnspecified;	// autowired
	Checkbox cbMisc;		// autowired
	
	Radio rbMild;			// autowired
	Radio rbModerate;		// autowired
	Radio rbSevere;			// autowired
	
	Radio rbDrug;			// autowired
	Radio rbFood;			// autowired
	Radio rbEnvironment;	// autowired

	Row rowMed1;
	Row rowMed2;
	Button btnSelect;
	Button btnSearch;
	Listheader name;
		
	
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
				try{ parReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "ParAddWinController() operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ParAddWinController() bad ptRec" );

		// if editing, make sure we have a valid parReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( parReca ))) SystemHelpers.seriousError( "ParAddWinController() bad parReca" );

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		if ( operation == EditPt.Operation.NEWPT ){

		
		
		

			// Create a new PAR entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			Par par = new Par( parReca );
				

			
			
			// Get info from data struct
			
			txtDate.setValue( par.getDate().getPrintable(9));
			
			
			if ( par.getFlgMisc()){
				// misc description
				cbMisc.setChecked( true );
				txtMisc.setValue( par.getParDesc());
				
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				cbMisc.setChecked( false );
				lblMed.setValue( par.getParDesc());
				lblCode.setValue( String.valueOf( par.getCompositeID()));
			}
			
			// set controls based on cbMisc
			onCheck$cbMisc( null );
			
			int symptoms = par.getSymptoms();
			cbRash.setChecked(( symptoms & Par.Reactions.RASH ) != 0 );
			cbShock.setChecked(( symptoms & Par.Reactions.SHOCK ) != 0 );
			cbDyspnea.setChecked(( symptoms & Par.Reactions.DYSPNEA ) != 0 );
			cbNausea.setChecked(( symptoms & Par.Reactions.NAUSEA ) != 0 );
			cbAnemia.setChecked(( symptoms & Par.Reactions.ANEMIA ) != 0 );
			cbUnspecified.setChecked(( symptoms & Par.Reactions.UNSPECIFIED ) != 0 );
			
			Par.Severity severity = par.getSeverity();
			rbMild.setChecked( severity == Par.Severity.MILD );
			rbModerate.setChecked( severity == Par.Severity.MODERATE );
			rbSevere.setChecked( severity == Par.Severity.SEVERE );
			
			Par.Domain domain = par.getDomain();
			rbDrug.setChecked( domain == Par.Domain.DRUG );
			rbFood.setChecked( domain == Par.Domain.FOOD );
			rbEnvironment.setChecked( domain == Par.Domain.ENVIRONMENT );
			
			txtNote.setValue( par.getNoteTxt());		
		}

		
		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Par par;		// medication info
		



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
				DialogHelpers.Messagebox( "Invalid drug name: " + s + "." );
				return false;
			}
			
		} else {
			
			String s = lblMed.getValue();
			String t = lblCode.getValue();
			
			if (( s == null ) || ( s.length() < 2 ) || ( t == null ) || ( t.length() < 1 )){
				DialogHelpers.Messagebox( "Invalid drug name: " + s + " or code: " + t + "." );
				return false;
			}
		}
		
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			System.out.println( "Editing existing Par record, parReca=" + parReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( parReca )) 
				SystemHelpers.seriousError( "ParAddWinController.save() bad reca" );
		
			// Read par
			par = new Par( parReca );
			// validity/status byte should already be set
			//cmed.setStatus( 'C' );
				
		} else { 
			
			// create a new par record
			System.out.println( "Creating new Par record..." );
			par = new Par();
			// set validity/status byte
			par.setStatus( Par.Status.CURRENT );			
			par.setPtRec( ptRec );
		}
	
		
		// Set new info into Par object
		
		par.setDate( date );

		if ( cbMisc.isChecked()){
			par.setParDesc( txtMisc.getValue());
			par.setFlgMisc( true );
		} else {
			par.setParDesc( lblMed.getValue());
			par.setCompositeID( EditHelpers.parseInt( lblCode.getValue()));
			par.setFlgMisc( false );
		}
		
		int reaction = 0;
		if ( cbRash.isChecked()) reaction |= Par.Reactions.RASH;
		if ( cbShock.isChecked()) reaction |= Par.Reactions.SHOCK;
		if ( cbDyspnea.isChecked()) reaction |= Par.Reactions.DYSPNEA;
		if ( cbNausea.isChecked()) reaction |= Par.Reactions.NAUSEA;
		if ( cbAnemia.isChecked()) reaction |= Par.Reactions.ANEMIA;
		if ( cbUnspecified.isChecked()) reaction |= Par.Reactions.UNSPECIFIED;
		par.setSymptoms( reaction );

		par.setSeverity( Par.Severity.UNSPECIFIED );
		if ( rbMild.isChecked()) par.setSeverity( Par.Severity.MILD );
		if ( rbModerate.isChecked()) par.setSeverity( Par.Severity.MODERATE );
		if ( rbSevere.isChecked()) par.setSeverity( Par.Severity.SEVERE );

		par.setDomain( Par.Domain.UNSPECIFIED );
		if ( rbDrug.isChecked()) par.setDomain( Par.Domain.DRUG );
		if ( rbFood.isChecked()) par.setDomain( Par.Domain.FOOD );
		if ( rbEnvironment.isChecked()) par.setDomain( Par.Domain.ENVIRONMENT );

		par.setNoteTxt( txtNote.getValue());
		
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//par.setEdits( prob.getEdits() + 1 );
			
			// existing Par
			par.write( parReca );
			//System.out.println( "edited par info written, reca=" + parReca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PAR_EDIT, ptRec, Pm.getUserRec(), parReca, null );
			

		} else {			// Edit.Operation.NEWPT
			
			// new par
			// write the new par rec
			Reca reca = par.postNew( ptRec );
			//System.out.println( "new par info written, reca=" + reca.toString());
			
			// unset NKDA flag if set
			ParUtils.setNKDAFlag( ptRec, false );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PAR_ADD, ptRec, Pm.getUserRec(), parReca, null );
			
			// done

		}
		
		//par.dump();		
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
		parAddWin.detach();
		return;
	}
	
	
	
/*	
	// Refresh listbox when needed
	public void loadMedTblList(){ loadMedTblList( null ); }
	
	public void loadMedTblList( String searchString ){
		System.out.println( "in loadlist" );

		if ( medListbox == null ) return;
		System.out.println( "in loadlist2" );
		
		// remove all items
		for ( int i = medListbox.getItemCount(); i > 0; --i ){
			medListbox.removeItemAt( 0 );
		}
		
		
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
				(i = new Listitem()).setParent( medListbox );
				i.setValue( probTbl.getRec());
				
				//new Listcell( probTbl.getAbbr()).setParent( i );
				new Listcell( probTbl.getDesc()).setParent( i );
				//new Listcell( probTbl.getSNOMED()).setParent( i );
				//new Listcell( probTbl.getICD9()).setParent( i );
				//new Listcell( probTbl.getICD10()).setParent( i );
				//new Listcell( probTbl.getCode4()).setParent( i );
				//new Listcell( //*Dgn.read( prob.getDgnRec()).getCode() //"V101.5" ).setParent( i );;
			}
		}
		
		probTbl.close();
		

		return;
	}
*/



	
	// Search for matching medications/allergen in NCAllergies table
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		//loadProbTblList(( s.length() < 1 ) ? null: s );
		usrlib.ZkTools.listboxClear( parListbox );		
		if (( s != null ) && ( s.length() > 1 )) palmed.NCAllergies.search( parListbox, s );
		return;
	}
	
	
	
	// Select a matching medication/appergen from the NCAllergies table
	
	public void onClick$btnSelect( Event ev ){

		// make sure an item was selected
		if ( parListbox.getSelectedCount() < 1 ) return;
		
		// get selected item's Rec
		String drugName = parListbox.getSelectedItem().getLabel();
		String drugID = (String) parListbox.getSelectedItem().getValue();
		if (( drugName == null ) || ( drugID == null )) { SystemHelpers.seriousError( "ParAddWinController.onClick$select() bad drug data" ); return; }

		// copy to fields		
		
		NCAllergies nc = NCAllergies.readMedID(drugID);
		if ( nc == null ){
			System.out.println( "drugID " + drugID + " not found." );
			return;
		}

		lblMed.setValue( drugName );
		lblCode.setValue( drugID );
		lblEtc.setValue( nc.getConceptID() + ", Type: " + nc.getConceptType() );
		
		return;
	}

	public void onCheck$cbMisc( Event ev ){
		
		boolean flg = cbMisc.isChecked();
		
		rowMed1.setVisible( ! flg );
		rowMed2.setVisible( ! flg );
		btnSelect.setDisabled( flg );
		btnSearch.setDisabled( flg );
		parListbox.setDisabled( flg );
		name.setVisible( ! flg );
		txtMisc.setDisabled( ! flg );
		txtSearch.setDisabled( flg );		
		return;
	}
	


}
