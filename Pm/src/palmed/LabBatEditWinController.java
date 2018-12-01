package palmed;

import java.util.EnumSet;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Validity;
import usrlib.ZkTools;

public class LabBatEditWinController extends GenericForwardComposer {

	EditPt.Operation operation = null;	// operation to perform
	
	Window labBatEditWin;	// autowired - window	
	Rec labBatRec;		// vitals reca
	
	
	Textbox txtAbbr;			// autowired
	Textbox txtDesc;			// autowired
	Textbox txtUnitsText;		// autowired

	Textbox txtSNOMED;		// autowired
	Textbox txtLOINC;		// autowired
	
	Listbox lboxSelected;
	Listbox lboxLabObsTbl;
	
	Radio rbActive;
	Radio rbInactive;
		
	
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
				try{ labBatRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "LabBatEditWinController() operation==null" );
		

		
		//for ( LabBat.DataType r : EnumSet.allOf(LabBat.DataType.class))
		//	ZkTools.appendToListbox( lboxDataType, r.getLabel(), r );
		LabObsTbl.fillListbox( lboxLabObsTbl, false );
		


		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			System.out.println( "new LabBat" );
			rbActive.setChecked( true );
		
			// Create a new entry
			
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			System.out.println( "edit LabBat" );

			// Read ProbTbl entry 
			if ( ! Rec.isValid( labBatRec )) SystemHelpers.seriousError( "LabBatEditWinController() bad rec" );
			System.out.println( "rec=" + labBatRec.getRec());

			LabBat p = new LabBat( labBatRec );
			

			// Get probTbl info from data struct
			
			txtAbbr.setValue( p.getAbbr());
			txtDesc.setValue( p.getDesc());
			
			txtSNOMED.setValue( p.getSNOMED());
			txtLOINC.setValue( p.getLOINC());
		
			//ZkTools.setListboxSelectionByValue( lboxCodedUnits, p.getCodedUnits());
			
			rbActive.setChecked( p.getStatus() == LabBat.Status.ACTIVE );
			rbInactive.setChecked( p.getStatus() != LabBat.Status.ACTIVE );

			
			Rec[] recs = p.getObsRecs();
			for ( int i = 0; i < 32; ++i ){
				if ( Rec.isValid( recs[i] )){
					LabObsTbl l = new LabObsTbl( recs[i] );
					String label = l.getDesc();
					ZkTools.appendToListbox( lboxSelected, label, new Rec( recs[i].getRec()));
				}
			}

			
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		LabBat p;		// LabBat info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into LabBat dataBuffer
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new probTbl record
			System.out.println( "Creating new LabBat record..." );
			p = new LabBat();
			
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( labBatRec == null ) || ( labBatRec.getRec() < 2 )) SystemHelpers.seriousError( "LabBatEditWinController.save() bad rec" );
	
			// Read probTbl
			p = new LabBat( labBatRec );			
		}
	
		
		
		
		// Load data into prob dataBuffer

		p.setAbbr( txtAbbr.getValue());
		p.setDesc( txtDesc.getValue());
		p.setSNOMED( txtSNOMED.getValue());
		p.setLOINC( txtLOINC.getValue());
		

		//LabBat.Units units = (LabBat.Units) ZkTools.getListboxSelectionValue( lboxCodedUnits );
		//if ( units == null ) units = LabBat.Units.UNSPECIFIED;
		//p.setCodedUnits( units );
		
		Rec[] recs = new Rec[32];
		int j = 0;
		int max = lboxSelected.getItemCount() > 32 ? 32: lboxSelected.getItemCount();
		for ( int i = 0; i < max; ++i ){
			Rec rec = (Rec) lboxSelected.getItemAtIndex( i ).getValue();
			if ( Rec.isValid( rec )){
				recs[j++] = rec;
			}
		}		
		for ( ; j < 32; ++j ) recs[j] = new Rec( 0 );
		p.setObsRecs( recs );
		

		// set status to Active unless inactive checked
		p.setStatus( rbInactive.isChecked() ? LabBat.Status.INACTIVE: LabBat.Status.ACTIVE );
		p.setValid( Validity.VALID );
		
		
		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.NEWPT ){

			// new probTbl entry
	
			labBatRec = p.writeNew();
			System.out.println( "new LabBat entry created rec=" + labBatRec.toString());
						
			
		} else {	// EditPt.Operation.EDITPT
			
			// existing prob
			p.write( labBatRec );
			System.out.println( "edited LabBat info written, rec=" + labBatRec.toString());
		}
		//p.dump();
		
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
		labBatEditWin.detach();
		return;
	}

	
	public void onClick$btnSelect( Event e ) throws InterruptedException{
		
		if ( lboxLabObsTbl.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		if ( lboxSelected.getItemCount() > 31 ){			
			DialogHelpers.Messagebox( "Maximum number of 32 items are selected." );
			return;
		}

		String label = ZkTools.getListboxSelectionLabel( lboxLabObsTbl );
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxLabObsTbl );
		
		// make sure this one not already in listbox
		if ( ZkTools.getListboxItemIndexByValue( lboxSelected, rec ) >= 0 ){			
			DialogHelpers.Messagebox( "Item already selected." );
			return;
		}
		
		ZkTools.appendToListbox( lboxSelected, label, rec );
		
		return;
	}


}
