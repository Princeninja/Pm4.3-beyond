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
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;
import usrlib.ZkTools;

public class LabResEditWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window labResEditWin;		// autowired - window	
	Reca labReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtTime;		// autowired
//	Textbox txtMisc;		// autowired
	Textbox txtNote;		// autowired
	
	Label	lblObs;		// autowired
	Label	lblBat;			// autowired
	
//	Label	lblMVX;			// autowired
//	Label	lblInfo;		// autowired
//	Label	lblCVXinfo;		// autowired
//	Label	lblMVXinfo;		// autowired
	
	Textbox	txtResult;	// autowired
	Textbox txtSource;		// autowired
	Textbox txtCondition;	// autowired
//	Textbox txtSite;		// autowired
	
	Listbox lboxAbnormalFlag;	// autowired
	Listbox lboxResultStatus;		// autowired
	Listbox lboxSpecimenSource;		// autowired
	Listbox lboxCondition;	// autowired
	Listbox lboxFacility;		// autowired
	Textbox txtSearch;		// autowired
	
	Tab tabBat;		// autowired
	Tab tabObs;				// autowired
	Tabpanel tpBat;	// autowired
	Tabpanel tpObs;			// autowired
	
	Listbox lboxObs;	// autowired
	Listbox lboxBat;		// autowired
	
	Listheader lhdrObs;// autowired
	Listheader lhdrBat;		// autowired
	Listheader lhdrObsAbbr;		// autowired
	Listheader lhdrObsDesc;		// autowired
	
	Checkbox cbMisc;		// autowired
	
	Button btnSearch;		// autowired
	Button btnSelect;		// autowired
	Row rowMed1;			// autowired
	Row rowMed2;			// autowired

	Rec	obsRec;				// observation selected
	Rec batRec;				// battery selected
	
	Label lblObsLOINC;
	Label lblObsInfo;
	Label lblBatLOINC;
	
	Class<? extends Notes> noteClass = NotesLab.class;
		
	
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
				try{ labReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "LabResAddWinController() operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "LabResAddWinController() bad ptRec" );

		// if editing, make sure we have a valid parReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( labReca ))) SystemHelpers.seriousError( "LabResAddWinController() bad immReca" );

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		

		
		
		// fill some selection listboxes
		LabFacility.fillListbox( lboxFacility, true );
		
		ZkTools.appendToListbox( lboxAbnormalFlag, "", null );
		for ( LabResult.Abnormal r : EnumSet.allOf(LabResult.Abnormal.class))
			ZkTools.appendToListbox( lboxAbnormalFlag, r.getLabel(), r );
		
		ZkTools.appendToListbox( lboxResultStatus, "", null );
		for ( LabResult.ResultStatus r : EnumSet.allOf(LabResult.ResultStatus.class))
			ZkTools.appendToListbox( lboxResultStatus, r.getLabel(), r );
		
		ZkTools.appendToListbox( lboxSpecimenSource, "", null );
		for ( LabResult.SpecimenSource r : EnumSet.allOf(LabResult.SpecimenSource.class))
			ZkTools.appendToListbox( lboxSpecimenSource, r.getLabel(), r );

		for ( LabResult.SpecimenCondition r : EnumSet.allOf(LabResult.SpecimenCondition.class))
			ZkTools.appendToListbox( lboxCondition, r.getLabel(), r );


		
		
		// refresh the search listboxes
		refreshObsList( null );
		refreshBatList( null );
		
		
		// Create a new Immunization entry
		if ( operation == EditPt.Operation.NEWPT ){

			// set date to today's date and time
			txtDate.setValue( Date.today().getPrintable(9));
			txtTime.setValue( Time.now().getPrintable());
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			LabResult lab = new LabResult( labReca );
				
/*			if ( imm.getFlgMisc()){
				// misc description
				cbMisc.setChecked( true );
				txtMisc.setValue( imm.getDesc());
				onCheck$cbMisc( null );
				
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				cbMisc.setChecked( false );

				lblVaccine.setValue( lab.getDesc());
				onCheck$cbMisc( null );
			}
*/			
			//TODO lblInfo
			obsRec = lab.getLabObsRec();			
			LabObsTbl obs = new LabObsTbl( obsRec );
			lblObs.setValue( obs.getDesc());
			lblObsLOINC.setValue( obs.getLOINC());
			lblObsInfo.setValue( obs.getTestType().getLabel());
			
			
			batRec = lab.getLabBatRec();
			if ( Rec.isValid( batRec )){
				LabBat bat = new LabBat( batRec );
				lblBat.setValue( bat.getDesc());
				lblBatLOINC.setValue( bat.getLOINC());
			}
			

			// Get info from data struct			
			txtDate.setValue( lab.getDate().getPrintable(9));
			txtTime.setValue( lab.getTime().getPrintable());
			
			txtResult.setValue( lab.getResult());
			
			ZkTools.setListboxSelectionByValue( lboxAbnormalFlag, lab.getAbnormalFlag());
			ZkTools.setListboxSelectionByValue( lboxResultStatus, lab.getResultStatus());
			ZkTools.setListboxSelectionByValue( lboxSpecimenSource, lab.getSpecimenSource());
			ZkTools.setListboxSelectionByValue( lboxCondition, lab.getSpecimenCondition());

			ZkTools.setListboxSelectionByValue( lboxFacility, lab.getLabFacilityRec());

			txtSource.setValue( lab.getSourceText());
			txtSource.setValue( lab.getConditionText());
			
			
			System.out.println( "noteClass=" + noteClass.getName());
			

			Notes note = null;
			//try { note = noteClass.newInstance(); } catch ( Exception e1 ){ e1.printStackTrace(); }
			note = Notes.newInstance( noteClass );
			note.read( lab.getResultNoteReca() );
			String txt = note.getNoteText();
			
			txtNote.setValue( txt );
		}


		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		LabResult lab;		// lab result info
		



		// TODO - VALIDATE DATA
		
		// verify valid date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return false;			
		}
	
		// verify valid time
		Time time = Time.fromString( txtTime.getValue());
		if (( time == null ) || ( ! time.isValid())){
			DialogHelpers.Messagebox( "Invalid time: " + txtTime.getValue() + "." );
			return false;			
		}
	
		// verify valid ptrec
		if ( ! Rec.isValid( ptRec )){
			DialogHelpers.Messagebox( "Invalid ptrec:" + ptRec.getRec());
			return false;
		}
		
		// verify medication selected/entered
/*		if ( cbMisc.isChecked()){
			String s = txtMisc.getValue();
			if (( s == null ) || ( s.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid vaccine name: " + s + "." );
				return false;
			}
			
		} else {
*/			
			String strObs = lblObs.getValue();
			String strBat = lblBat.getValue();
			
			if (( strObs == null ) || ( strObs.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid observation name: " + strObs + "." );
				return false;
			}
//		}
		
		
		
		
		
		
		
		System.out.println( "Saving..." );

		// Save edited information
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			//System.out.println( "Editing existing lab result record, labReca=" + labReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( labReca )) SystemHelpers.seriousError( "bad reca" );
		
			// Read immunization
			lab = new LabResult( labReca );

			
			
		// Credate new immunization
		} else { 
			
			// create a new par record
			System.out.println( "Creating new lab result record..." );
			lab = new LabResult();
			lab.setStatus( LabResult.Status.ACTIVE );			
			lab.setValid( usrlib.Validity.VALID );
			lab.setPtRec( ptRec );
		}
	
		
		// Set new info into Imm object
		
		lab.setDate( date );
		lab.setTime( time );

/*		if ( cbMisc.isChecked()){
			lab.setDesc( txtMisc.getValue());
			lab.setFlgMisc( true );
		} else {
*/
			//lab.setDesc( lblVaccine.getValue());
			lab.setLabObsRec( obsRec );
			lab.setLabBatRec( batRec );
/*			lab.setFlgMisc( false );
		}
*/	

		lab.setResult( txtResult.getValue());
		
		lab.setAbnormalFlag( (LabResult.Abnormal) ZkTools.getListboxSelectionValue( lboxAbnormalFlag ));
		lab.setResultStatus( (LabResult.ResultStatus) ZkTools.getListboxSelectionValue( lboxResultStatus ));
		lab.setSpecimenSource( (LabResult.SpecimenSource) ZkTools.getListboxSelectionValue( lboxSpecimenSource ));
		lab.setSpecimenCondition( (LabResult.SpecimenCondition) ZkTools.getListboxSelectionValue( lboxCondition ));
		lab.setLabFacilityRec( (Rec) ZkTools.getListboxSelectionValue( lboxFacility ));

		lab.setSourceText( txtSource.getValue());
		lab.setConditionText( txtCondition.getValue());
		
		Notes note = Notes.newInstance( noteClass );
		
		if (lab.getResultNoteReca().isValid()){
			
			
			note.read( lab.getResultNoteReca() );
			
			note.setNumEdits( note.getNumEdits() + 1 );
			
			note.setDate( date );
			note.setNoteText( txtNote.getValue().trim());
			
			note.write( lab.getResultNoteReca() );
			// log the action
			AuditLogger.recordEntry( Notes.getAuditLogActionEdit( noteClass ), ptRec, Pm.getUserRec(), lab.getResultNoteReca(), null );
			
			
		}
		else { 
			
			note = Notes.newInstance( noteClass );
			//try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
			
			note.setPtRec( ptRec );
			note.setUserRec( Pm.getUserRec());
			note.setStatus( Notes.Status.CURRENT );
			note.setNumEdits( 0 );
			
			note.setDate( date );
			note.setNoteText( txtNote.getValue().trim());
			
			lab.setResultNoteReca(note.postNew(ptRec));
			
			// post to MrLog
		    MrLog.postNew( ptRec, date, "", Notes.getMrLogTypes( noteClass ), lab.getResultNoteReca() );
			// log the action
			AuditLogger.recordEntry( Notes.getAuditLogActionNew( noteClass ), ptRec, Pm.getUserRec(), lab.getResultNoteReca(), null );
			
		}
		
		  // Notify listeners 
		Notifier.notify( ptRec, Notes.getNotifierEvent( noteClass ));
		Notifier.notify( ptRec, Notifier.Event.CARELOG );

		//lab.setResultNoteText( txtNote.getValue());

		
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//par.setEdits( prob.getEdits() + 1 );
			
			// existing Immunizations
			lab.write( labReca );
			System.out.println( "edited immunization info written, reca=" + labReca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_EDIT, ptRec, Pm.getUserRec(), labReca, null );
			

		} else {			// Edit.Operation.NEWPT
			
			// new par
			// write the new par rec
			Reca reca = lab.postNew( ptRec );
			System.out.println( "new immunization info written, reca=" + reca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_ADD, ptRec, Pm.getUserRec(), labReca, null );
			
			// done

		}

		Notifier.notify( ptRec, Notifier.Event.LAB );

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
		labResEditWin.detach();
		return;
	}
	
	
/*	public void onCheck$cbMisc( Event ev ){
		
		boolean flg = cbMisc.isChecked();
		
		rowMed1.setVisible( ! flg );
		rowMed2.setVisible( ! flg );
		tabShortcut.setDisabled( flg );
		tabCVX.setDisabled( flg );
		tabMVX.setDisabled( flg );
		btnSelect.setDisabled( flg );
		btnSearch.setDisabled( flg );
		lboxShortcut.setDisabled( flg );
		lboxCVX.setDisabled( flg );
		lboxMVX.setDisabled( flg );
		txtMisc.setDisabled( ! flg );
		txtSearch.setDisabled( flg );		
		return;
	}
*/
	
	
	
	
	
	public void refreshObsList( String searchString ){

		if ( lboxObs == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxObs );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();		
		
		// populate list
		//LabObsTbl.fillListbox( lboxObs, null, null, searchString, LabObsTbl.Status.ACTIVE );
		LabObsTbl.fillListbox( lboxObs, false);
		return;
	}

	
	
	

    
	public void refreshBatList( String searchString ){

		if ( lboxBat == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxBat );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		//LabBat.fillListbox( lboxBat, null, null, searchString, LabBat.Status.ACTIVE );
		LabBat.fillListbox( lboxBat, false );
		return;
	}





	
	// Search for matching entries in corresponding listbox
	public void onOK$txtSearch( Event ev ){ onClick$btnSearch( ev ); }
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		//if ( s.length() == 0 ) s = null;
		
		if ( tabObs.isSelected()){

			ZkTools.listboxClear( lboxObs );		
			//refreshObsList( s );
			LabObsTblWinController searchtool = new LabObsTblWinController();
			searchtool.refreshList(( s.length() < 1 ) ? null: s, lboxObs ,"Yes");
			
		} else if ( tabBat.isSelected()){
			if ( s.length() == 0 ) s = null;
			ZkTools.listboxClear( lboxBat );		
			refreshBatList( s );
		}
		
		return;
	}
	
	
	
	// Select a an entry from the search tables	
	public void onClick$btnSelect( Event ev ){

		if ( tabObs.isSelected()){
			
			// make sure an item was selected
			if ( lboxObs.getSelectedCount() < 1 ) return;
			obsRec = (Rec) ZkTools.getListboxSelectionValue( lboxObs );
			LabObsTbl obs = new LabObsTbl( obsRec );
			lblObs.setValue( obs.getDesc());
			lblObsLOINC.setValue( obs.getLOINC());
			lblObsInfo.setValue( obs.getTestType().getLabel());

			
		} else if ( tabBat.isSelected()){
			
			// make sure an item was selected
			if ( lboxBat.getSelectedCount() < 1 ) return;
			batRec = (Rec) ZkTools.getListboxSelectionValue( lboxBat );
			LabBat bat = new LabBat( batRec );
			lblBat.setValue( bat.getDesc());
			lblBatLOINC.setValue( bat.getLOINC());
		}

		return;
	}

}
