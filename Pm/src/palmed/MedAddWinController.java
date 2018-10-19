package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class MedAddWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window medAddWin;		// autowired - window	
	Reca cmedReca = null;	// vitals reca
	
	Textbox txtDate;		// autowired
	Textbox txtMiscMed;		// autowired
	Textbox txtAddlSig;		// autowired
	Textbox txtMsg;			// autowired
	Textbox	txtNumber;		// autowired
	Textbox	txtRefills;		// autowired
	
	Label	lblMed;			// autowired
	Label	lblCode;		// autowired
	Label	lblEtc;			// autowired
	
	Listbox medListbox;		// autowired
	
	Listbox lbRoute;		// autowired
	Listbox lbFreq;			// autowired
	Listbox lbDosage;		// autowired
	Listbox lbForm;			// autowired
	Listbox lbPrescriber;	// autowired
	Listbox lbProblem;		// autowired
	
	Textbox txtSearch;		// autowired
	
	Checkbox cbPRN;			// autowired
	Checkbox cbAcute;		// autowired
	Checkbox cbOutside;		// autowired
	Checkbox cbNMBU;		// autowired
	Checkbox cbDAW;			// autowired
	Checkbox cbMisc;		// autowired
	
	
	
		
	
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
				try{ cmedReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "MedAddWinController() operation==null" );
		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )) SystemHelpers.seriousError( "MedAddWinController() bad ptRec" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill some selection listboxes
		MedRoute.fillListbox( lbRoute );
		MedFreq.fillListbox( lbFreq );
		MedForm.fillListbox( lbForm );
		MedDosage.fillListbox( lbDosage );
		Prov.fillListbox( lbPrescriber, true );
		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

		
		
		

			// Create a new medication entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			if (( cmedReca == null ) || ( cmedReca.getRec() < 2 )) SystemHelpers.seriousError( "MedAddWinController() bad reca" );
			Cmed cmed = new Cmed( cmedReca );
				

			
			
			// Get info from data struct
/*			
			txtDate.setValue( cmed.getStartDate().getPrintable(9));
			
			Rec probTblRec = cmed.getProbTblRec();
			
			if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )){
				// misc description
				misc.setValue( prob.getMiscDesc());
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				lblMed.setValue( prob.getProbDesc());
				lblCode.setValue( prob.getCode());
			}
			
			
			// acute/chronic
			int type = cmed.getType();
			if ( type == 'A' ) rbAcute.setChecked( true );
			if ( type == 'C' ) rbChronic.setChecked( true );

			//TODO - history of???
*/		
			
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
		
		Cmed cmed;		// medication info
		



		// TODO - VALIDATE DATA
		
		// verify valid start date
		Date startDate = new Date( txtDate.getValue());
		if (( startDate == null ) || ( ! startDate.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return;			
		}
	
		// verify valid ptrec
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			DialogHelpers.Messagebox( "Invalid ptrec:" + ptRec.getRec());
		}
		
		// verify medication selected/entered
		if ( cbMisc.isChecked()){
			String s = txtMiscMed.getValue();
			if (( s == null ) || ( s.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid drug name: " + s + "." );
				return;
			}
			
		} else {
			
			String s = lblMed.getValue();
			String t = lblCode.getValue();
			
			if (( s == null ) || ( s.length() < 2 ) || ( t == null ) || ( t.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid drug name: " + s + " or code: " + t + "." );
				return;
			}
		}
		
		// verify provider selected
		Rec provRec = (Rec) ZkTools.getListboxSelectionValue( lbPrescriber );
		if ( provRec == null ) provRec = new Rec( 0 );
		
		// verify form, route, dosage, frequency
		

		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			System.out.println( "Editing existing cmed record, cmedReca=" + cmedReca.toString() + "." );
				
			// make sure we have a valid reca
			if (( cmedReca == null ) || ( cmedReca.getRec() < 2)) 
				SystemHelpers.seriousError( "CmedEditWinController.save() bad reca" );
		
			// Read cmed
			cmed = new Cmed( cmedReca );
			// validity/status byte should already be set
			//cmed.setStatus( 'C' );
				
		} else { 
			
			// create a new cmed record
			System.out.println( "Creating new cmed record..." );
			cmed = new Cmed();
			// set validity/status byte
			cmed.setStatus( Cmed.Status.CURRENT );			
			cmed.setPtRec( ptRec );
			cmed.setChecked( (byte) 0 );
		}
	
		
		// Set new med info into Cmed object
		
		cmed.setStartDate( startDate );

		if ( cbMisc.isChecked()){
			cmed.setDrugName( txtMiscMed.getValue());
			cmed.setFlgMisc( true );
		} else {
			cmed.setDrugName( lblMed.getValue());
			cmed.setDrugCode( lblCode.getValue());
			cmed.setFlgMisc( false );
		}
		
		int dosage = (Integer) ZkTools.getListboxSelectionValue( lbDosage );
		cmed.setDosage( dosage );
		
		int form = (Integer) ZkTools.getListboxSelectionValue( lbForm );
		cmed.setForm( form );
		
		int route = (Integer) ZkTools.getListboxSelectionValue( lbRoute );
		cmed.setRoute( route );
		
		Integer i = (Integer) ZkTools.getListboxSelectionValue( lbFreq );
		cmed.setSched(( i == null ) ? 0: i );
		
		cmed.setAddlSigTxt( txtAddlSig.getValue());
		
		cmed.setPRN( cbPRN.isChecked());
		cmed.setDAW( cbDAW.isChecked());
		cmed.setAcute( cbAcute.isChecked());
		
		cmed.setNumber( EditHelpers.parseInt( txtNumber.getValue()));
		cmed.setRefills( EditHelpers.parseInt( txtRefills.getValue()));
		
		//cmed.setProbReca(reca);
		//cmed.setProviderRec(rec);
		//cmed.setRdocRec(rec);
		//cmed.setVisitReca(reca);;

		cmed.setProviderRec( provRec );
		 

		
		
/*
		if (( probTblRec != null ) && ( probTblRec.getRec() > 1 )){
			
			cmed.setProbTblRec( probTblRec );
			cmed.setMiscDesc( "" );

		} else {
			
			cmed.setProbTblRec( new Rec( 0 ));
			cmed.setMiscDesc( misc.getValue());
		}
		
		byte type = 0;
		if ( rbAcute.isChecked()) type = 'A';
		if ( rbChronic.isChecked()) type = 'C';
		cmed.setType( type );
		
*/		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//cmed.setEdits( prob.getEdits() + 1 );
			
			// existing cmed
			cmed.write( cmedReca );
			System.out.println( "edited cmed info written, reca=" + cmedReca.toString());

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.MED_EDIT, ptRec, Pm.getUserRec(), cmedReca, null );
			
		} else {			// Edit.Operation.NEWPT
			
			// new cmed
			// write the new med rec
			cmed.postNew( ptRec );
			
			// unset 'No Medications' flag
			MedUtils.setNoMedsFlag( ptRec, false );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.MED_ADD, ptRec, Pm.getUserRec(), cmedReca, null );
			
			// done

/*			try {

				//TODO - lock file, assure atomic action, etc
				SystemHelpers.setLockStub();

				// read MedPt
				DirPt dirPt = new DirPt( ptRec );
				MedPt medPt = new MedPt( dirPt.getMedRec());
				
				// get old cmed record pointer from medPt
				Reca oldCmedReca = medPt.getMedsReca();
				
				// set LLHdr (linked-list header) with backward pointer and null next pointer in new cmed record
				cmed.setLLHdr( new LLHdr( new Reca().setNull(), oldCmedReca ));
				//cmed.dump();	
		
				// write new cmed record
				cmedReca = cmed.writeNew();
				System.out.println( "new cmed created reca=" + cmedReca.toString());
				
				// set new cmed reca in MedPt and write MedPt
				medPt.setMedsReca( cmedReca );
				medPt.write();
				
				// set forward pointer in oldProb
				if (( oldCmedReca != null ) && ( oldCmedReca.getRec() > 1 )){
					Cmed oldCmed = new Cmed();
					oldCmed.read( oldCmedReca );
					LLHdr llhdr = oldCmed.getLLHdr();
					llhdr.setNext( cmedReca );
					oldCmed.setLLHdr( llhdr );
					oldCmed.write( oldCmedReca );
				}
				
			} finally {
				
				//TODO - did the above execute properly??
				SystemHelpers.releaseLockStub();
			}
*/
		}
		
		//cmed.dump();		
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
		medAddWin.detach();
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


	
	public void onOK( Event ev ){ 		
		
		 onClick$btnSearch(  ev )	;	
		
	}
	
	// Search for matching medications in NCFull table
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		//loadProbTblList(( s.length() < 1 ) ? null: s );
		usrlib.ZkTools.listboxClear( medListbox );		
		if (( s != null ) && ( s.length() > 1 )) palmed.NCFull.search( medListbox, s );
		return;
	}
	
	
	public void onDoubleClick( Event ev ){  onClick$btnSelect( ev ); }
	
	// Select a matching medication from the NCFull table
	
	public void onClick$btnSelect( Event ev ){

		// make sure an item was selected
		if ( medListbox.getSelectedCount() < 1 ) return;
		// get selected item's Rec
		String drugName = medListbox.getSelectedItem().getLabel();
		String drugID = (String) medListbox.getSelectedItem().getValue();
		if (( drugName == null ) || ( drugID == null )) { SystemHelpers.seriousError( "MedAddWinController.onClick$select() bad drug data" ); return; }
		// copy to fields
		//ProbTbl p = new ProbTbl( pRec );
		lblMed.setValue( drugName);
		lblCode.setValue( drugID );
		
		NCFull nc = NCFull.readMedID(drugID);
		if ( nc == null ){
			System.out.println( "drugID " + drugID + " not found." );
			return;
		}
		
		System.out.println( "Form=" + nc.getMedForm()+ "----" + nc.getMedFormDesc());
		System.out.println( "Translated form=" + MedForm.fromNCFull( nc.getMedForm()));
		
		ZkTools.setListboxSelectionByValue( lbForm, MedForm.fromNCFull( nc.getMedForm()));
		ZkTools.setListboxSelectionByValue( lbRoute, MedRoute.fromNCFull( nc.getMedRouteID()));
		ZkTools.setListboxSelectionByLabel( lbDosage, "1" );
		
		System.out.println( "Route=" + nc.getMedRoute()+ "----" + nc.getMedRouteDesc() + " ID=" + nc.getMedRouteID());
		lblEtc.setValue( nc.getMedEtc());
		
		//probTblRec = pRec;
		//if ( cbDosage.getValue().length() < 1 ) cbDosage.setValue( "1" );
		return;
	}



}
