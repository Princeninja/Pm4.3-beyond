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


import usrlib.Rec;
import usrlib.Validity;
import usrlib.ZkTools;

public class LabObsTblEditWinController extends GenericForwardComposer {

	EditPt.Operation operation = null;	// operation to perform
	
	Window labObsTblEditWin;	// autowired - window	
	Rec labObsTblRec;		// vitals reca
	
	
	Textbox txtAbbr;			// autowired
	Textbox txtDesc;			// autowired
	Textbox txtUnitsText;		// autowired

	Textbox txtHigh;		// autowired
	Textbox txtLow;			// autowired
	Textbox txtMin;			// autowired
	Textbox txtMax;			// autowired
	Textbox txtSNOMED;		// autowired
	Textbox txtLOINC;		// autowired
	Textbox txtNormalRangeText;	// autowired
	Textbox txtResource;
	
	Listbox lboxDataType;
	Listbox lboxDecimalPlaces;
	Listbox lboxCodedUnits;
	Listbox lboxTestType;
	
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
				try{ labObsTblRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "LabObsTblEditWinController() operation==null" );
		

		
		for ( LabObsTbl.DataType r : EnumSet.allOf(LabObsTbl.DataType.class))
			ZkTools.appendToListbox( lboxDataType, r.getLabel(), r );
		
		for ( LabObsTbl.Units r : EnumSet.allOf(LabObsTbl.Units.class))
			ZkTools.appendToListbox( lboxCodedUnits, r.getLabel(), r );
		
		for ( int i = 0; i < 9; ++i )
			ZkTools.appendToListbox( lboxDecimalPlaces, String.valueOf( i ), new Integer( i ));

		for ( LabObsTbl.TestType r : EnumSet.allOf(LabObsTbl.TestType.class))
			ZkTools.appendToListbox( lboxTestType, r.getLabel(), r );
		

		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			System.out.println( "new LabObsTbl" );
			rbActive.setChecked( true );
		
			// Create a new entry
			
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			System.out.println( "edit LabObsTbl" );

			// Read ProbTbl entry 
			if ( ! Rec.isValid( labObsTblRec )) SystemHelpers.seriousError( "LabObsTblEditWinController() bad rec" );
			System.out.println( "rec=" + labObsTblRec.getRec());

			LabObsTbl p = new LabObsTbl( labObsTblRec );
			

			// Get probTbl info from data struct
			
			txtAbbr.setValue( p.getAbbr());
			txtDesc.setValue( p.getDesc());
			txtUnitsText.setValue( p.getUnitsText());
			
			txtNormalRangeText.setValue( p.getNormalRangeText());
			txtHigh.setValue( p.getNormalHigh());
			txtLow.setValue( p.getNormalLow());
			txtMin.setValue( p.getAbsoluteMin());
			txtMax.setValue( p.getAbsoluteMax());
			txtSNOMED.setValue( p.getSNOMED());
			txtLOINC.setValue( p.getLOINC());
			txtResource.setValue( p.getResource());
		
			ZkTools.setListboxSelectionByValue( lboxCodedUnits, p.getCodedUnits());
			ZkTools.setListboxSelectionByValue( lboxDataType, p.getDataType());
			ZkTools.setListboxSelectionByValue( lboxDecimalPlaces, new Integer( p.getDecimalPlaces()));
			ZkTools.setListboxSelectionByValue( lboxTestType, p.getTestType());

			rbActive.setChecked( p.getStatus() == LabObsTbl.Status.ACTIVE );
			rbInactive.setChecked( p.getStatus() != LabObsTbl.Status.ACTIVE );

		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		LabObsTbl p;		// LabObsTbl info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into LabObsTbl dataBuffer
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new probTbl record
			System.out.println( "Creating new LabObsTbl record..." );
			p = new LabObsTbl();
			
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( labObsTblRec == null ) || ( labObsTblRec.getRec() < 2 )) SystemHelpers.seriousError( "LabObsTblEditWinController.save() bad rec" );
	
			// Read probTbl
			p = new LabObsTbl( labObsTblRec );			
		}
	
		
		
		
		// Load data into prob dataBuffer

		p.setAbbr( txtAbbr.getValue());
		p.setDesc( txtDesc.getValue());
		p.setSNOMED( txtSNOMED.getValue());
		p.setLOINC( txtLOINC.getValue());
		p.setResource( txtResource.getValue());
		
		p.setUnitsText( txtUnitsText.getValue());
		
		p.setNormalRangeText(txtNormalRangeText.getValue());
		p.setNormalHigh(txtHigh.getValue());
		p.setNormalLow( txtLow.getValue());
		p.setAbsoluteMin( txtMin.getValue());
		p.setAbsoluteMax( txtMax.getValue());
		

		LabObsTbl.Units units = (LabObsTbl.Units) ZkTools.getListboxSelectionValue( lboxCodedUnits );
		if ( units == null ) units = LabObsTbl.Units.UNSPECIFIED;
		p.setCodedUnits( units );
		
		LabObsTbl.DataType dtype = (LabObsTbl.DataType) ZkTools.getListboxSelectionValue( lboxDataType );
		if ( dtype == null ) dtype = LabObsTbl.DataType.UNSPECIFIED;
		p.setDataType( dtype );
		
		LabObsTbl.TestType ttype = (LabObsTbl.TestType) ZkTools.getListboxSelectionValue( lboxTestType );
		if ( ttype == null ) ttype = LabObsTbl.TestType.UNSPECIFIED;
		p.setTestType( ttype );
		
		Integer ii = (Integer) ZkTools.getListboxSelectionValue( lboxDecimalPlaces );
		int i = ( ii == null) ? 0: ii;
		if ( i < 1 ) i = 0;
		if ( i > 9 ) i = 9;
		p.setDecimalPlaces( i );
		
		

		// set status to Active unless inactive checked
		p.setStatus( rbInactive.isChecked() ? LabObsTbl.Status.INACTIVE: LabObsTbl.Status.ACTIVE );
		p.setValid( Validity.VALID );
		
		
		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.NEWPT ){

			// new probTbl entry
			labObsTblRec = p.writeNew();
			System.out.println( "new LabObsTbl entry created rec=" + labObsTblRec.toString());
						
			
		} else {	// EditPt.Operation.EDITPT
			
			// existing prob
			p.write( labObsTblRec );
			System.out.println( "edited LabObsTbl info written, rec=" + labObsTblRec.toString());
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
		labObsTblEditWin.detach();
		return;
	}
	

}
