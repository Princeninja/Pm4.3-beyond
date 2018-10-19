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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;
import usrlib.ZkTools;

public class CDSRulesEditWinController extends GenericForwardComposer {

	EditPt.Operation operation = null;	// operation to perform
	
	
	
	Window cdsRulesEditWin;		// autowired - window	
	Rec cdsRec = null;	// par reca
	
	Textbox txtDesc;			// autowired x
	Textbox txtSearchProb;		// autowired x
	Textbox txtSearchMed;		// autowired x
	Textbox txtSearchLab;		// autowired x
	Textbox txtMsg;				// autowired x
	Textbox txtNote;		// autowired
	
	Label	lblObs;		// autowired
	Label	lblBat;			// autowired
	
//	Label	lblMVX;			// autowired
//	Label	lblInfo;		// autowired
//	Label	lblCVXinfo;		// autowired
//	Label	lblMVXinfo;		// autowired
	
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
	
	Listbox lboxRace;			// autowired x
	Listbox lboxEthnicity;		// autowired x
	Listbox lboxProb;
	Listbox lboxLab;
	Listbox lboxMed;
	Listbox lboxProbSel;
	Listbox lboxMedSel;
	Listbox lboxLabSel;
	
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
	
	Checkbox cbProbs;
	Radio rbHasDx;
	Radio rbNoDx;
	
	Checkbox cbLabs;
	Radio rbLabLess;
	Radio rbLabGreater;
	Radio rbNoneWithin;
	Textbox txtPeriod;
	Textbox txtResult;
	
	Radio rbMedNoRx;
	Radio rbMedOn;
	Checkbox cbMeds;
	
	Checkbox cbBMI;
	Radio rbBMIgreater;
	Radio rbBMIless;
	Textbox txtBMI;
	
	Checkbox cbMsg;
	Radio rbSOAP;
	
	
	
	
		
	
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
				try{ cdsRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "LabResAddWinController() operation==null" );
		
		// if editing, make sure we have a valid parReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Rec.isValid( cdsRec ))) SystemHelpers.seriousError( "bad rec" );

		

		
		
		// fill some selection listboxes
		LabFacility.fillListbox( lboxFacility, true );
		
		ZkTools.appendToListbox( lboxRace, "", null );
		for ( palmed.Race r : EnumSet.allOf(palmed.Race.class))
			ZkTools.appendToListbox( lboxRace, r.getLabel(), r );
		
		ZkTools.appendToListbox( lboxEthnicity, "", null );
		for ( palmed.Ethnicity r : EnumSet.allOf(palmed.Ethnicity.class))
			ZkTools.appendToListbox( lboxEthnicity, r.getLabel(), r );
		
		
		
		ZkTools.appendToListbox( lboxSpecimenSource, "", null );
		for ( LabResult.SpecimenSource r : EnumSet.allOf(LabResult.SpecimenSource.class))
			ZkTools.appendToListbox( lboxSpecimenSource, r.getLabel(), r );

		for ( LabResult.SpecimenCondition r : EnumSet.allOf(LabResult.SpecimenCondition.class))
			ZkTools.appendToListbox( lboxCondition, r.getLabel(), r );


		
		
		// refresh the search listboxes
		refreshLabList( null );
		//refreshProbTblList( null );
		//refreshMedList( null );

		
		// Create a new Immunization entry
		if ( operation == EditPt.Operation.NEWPT ){

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			if ( cdsRec.getRec() == 2 ){
				
				txtDesc.setValue( "Diabetics without a HgbA1c in last 3 months, suggest HgbA1c" );
				txtMsg.setValue( "Diabetic without recent HgbA1c.  Consider getting HgbA1c today." );
				ZkTools.appendToListbox( lboxProbSel, "Diabetes Mellitus type 2", null );
				ZkTools.appendToListbox( lboxLabSel, "HgbA1c", null );
				txtPeriod.setValue( "90" );
				rbHasDx.setChecked( true );
				cbLabs.setChecked( true );
				cbProbs.setChecked( true );
				rbNoneWithin.setChecked( true );
				cbMsg.setChecked( true );
				rbSOAP.setChecked( true );
			}
			if ( cdsRec.getRec() == 3 ){
				
				txtDesc.setValue( "Diabetic with HgbA1c >= 9.0, suggest diabetic teaching" );
				txtMsg.setValue( "Diabetic with HgbA1c >= 9.0.  Consider ordering diabetic teaching." );
				ZkTools.appendToListbox( lboxProbSel, "Diabetes Mellitus type 2", null );
				ZkTools.appendToListbox( lboxLabSel, "HgbA1c", null );
				rbHasDx.setChecked( true );
				txtResult.setValue( "9.0" );
				rbLabGreater.setChecked( true );
				cbLabs.setChecked( true );
				cbProbs.setChecked( true );
				cbMsg.setChecked( true );
				rbSOAP.setChecked( true );
			}
			if ( cdsRec.getRec() == 4 ){
				
				txtDesc.setValue( "LDL > 130 and not on a statin, suggest adding a statin" );
				txtMsg.setValue( "LDL greater than 130.  Patient not on a statin.  Consider prescribing a statin if not contraindicated." );
				ZkTools.appendToListbox( lboxLabSel, "LDL Cholesterol", null );
				ZkTools.appendToListbox( lboxMedSel, "HMG CoA Reductase Inhibitors (statins)", null );
				txtResult.setValue( "130" );
				rbLabGreater.setChecked( true );
				rbMedNoRx.setChecked( true );
				cbMeds.setChecked( true );
				cbLabs.setChecked( true );
				cbMsg.setChecked( true );
				rbSOAP.setChecked( true );
			}
			if ( cdsRec.getRec() == 5 ){
				
				txtDesc.setValue( "BMI > 30, consider nutritional counselling" );
				txtMsg.setValue( "BMI greater than 30.  Consider ordering nutritional counselling." );
				txtBMI.setValue( "30" );
				rbBMIgreater.setChecked( true );
				cbBMI.setChecked( true );
				cbMsg.setChecked( true );
				rbSOAP.setChecked( true );
			}

	

				
			// Read info 
			//LabResult lab = new LabResult( cdsRec );
				
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
			

			// Get info from data struct			
			//txtDesc.setValue( );
			
			
			//ZkTools.setListboxSelectionByValue( lboxAbnormalFlag, lab.getAbnormalFlag());
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
		String desc = txtDesc.getValue();
		if (( desc == null ) || ( desc.length() < 1 )){
			DialogHelpers.Messagebox( "Invalid description." );
			return false;			
		}
	
		
		
		
		
		
		
		System.out.println( "Saving..." );

		// Save edited information
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){


			// make sure we have a valid reca
			//if ( ! Rec.isValid( cdsRec )) SystemHelpers.seriousError( "bad rec" );
		
			// Read immunization
			//lab = new LabResult( cdsRec );

			
			
		// Credate new immunization
		} else { 
			
			// create a new par record
			//System.out.println( "Creating new lab result record..." );
			//lab = new LabResult();
			//lab.setStatus( LabResult.Status.ACTIVE );			
			//lab.setValid( usrlib.Validity.VALID );
		}
	
		
		// Set new info into Imm object

		//lab.setResult( txtResult.getValue());
		
		//lab.setAbnormalFlag( (LabResult.Abnormal) ZkTools.getListboxSelectionValue( lboxAbnormalFlag ));

		
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//par.setEdits( prob.getEdits() + 1 );
			
			// existing Immunizations
			//lab.write( cdsRec );
			
			// log the access
			//AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_EDIT, ptRec, Pm.getUserRec(), cdsRec, null );
			

		} else {			// Edit.Operation.NEWPT
			
			// new par
			// write the new par rec
			//Reca reca = lab.postNew( ptRec );
			
			// log the access
			//AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_ADD, ptRec, Pm.getUserRec(), cdsRec, null );
			
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
		cdsRulesEditWin.detach();
		return;
	}
	
	
	
	
	
	
	
	public void refreshLabList( String searchString ){

		if ( lboxLab == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxLab );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();		
		
		// populate list
		//LabObsTbl.fillListbox( lboxObs, null, null, searchString, LabObsTbl.Status.ACTIVE );
		LabObsTbl.fillListbox( lboxLab, false);
		return;
	}

	
	
	

    
	public void refreshProbList( String searchString ){

		if ( lboxProb == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxProb );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		ProbTbl.fillListbox( lboxProb, searchString );
		return;
	}




	public void refreshMedList( String searchString ){

		if ( lboxMed == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxMed );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		//LabBat.fillListbox( lboxBat, null, null, searchString, LabBat.Status.ACTIVE );
		NCFull.search( lboxMed, searchString );
		return;
	}





	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchProb( Event ev ){

		String s = txtSearchProb.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least 3 chars." );
			return;
		}
		
		refreshProbList( s );
		return;
	}
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchMed( Event ev ){

		String s = txtSearchMed.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least 3 chars." );
			return;
		}
		
		refreshMedList( s );
		return;
	}
	
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchLab( Event ev ){

		String s = txtSearchLab.getValue().trim();
		if ( s.length() < 1 ){
			DialogHelpers.Messagebox( "You must enter at least 1 chars." );
			return;
		}
		
		refreshLabList( s );
		return;
	}
	
	
	
	

	// Select a an entry from the search tables	
	public void onClick$btnSelectProb( Event ev ){

		// make sure an item was selected
		if ( lboxProb.getSelectedCount() < 1 ) return;
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxProb );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxProb );
		ZkTools.appendToListbox( lboxProbSel, str, rec );
		return;
	}

	// Select a an entry from the search tables	
	public void onClick$btnSelectMed( Event ev ){

		// make sure an item was selected
		if ( lboxMed.getSelectedCount() < 1 ) return;
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxMed );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxMed );
		ZkTools.appendToListbox( lboxMedSel, str, rec );
		return;
	}
	// Select a an entry from the search tables	
	public void onClick$btnSelectLab( Event ev ){

		// make sure an item was selected
		if ( lboxLab.getSelectedCount() < 1 ) return;
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxLab );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxLab );
		ZkTools.appendToListbox( lboxLabSel, str, rec );
		return;
	}

}
