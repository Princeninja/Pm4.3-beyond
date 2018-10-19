package palmed;

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

import usrlib.Address;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Validity;
import usrlib.ZkTools;

public class LabFacilityEditWinController extends GenericForwardComposer {

	EditPt.Operation operation = null;	// operation to perform
	
	Window labFacilityEditWin;	// autowired - window	
	Rec labFacilityRec;		// vitals reca
	
	
	Textbox txtAbbr;			// autowired
	Textbox txtDesc;			// autowired
	Textbox txtStreet;			// autowired
	Textbox txtLine2;			// autowired
	Textbox txtCity;			// autowired
	Textbox txtState;			// autowired
	Textbox txtZip;				// autowired
	Textbox txtPhone1;			// autowired
	Textbox txtPhone2;			// autowired

	
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
				try{ labFacilityRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "LabFacilityEditWinController() operation==null" );
		

		


		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			System.out.println( "new LabFacility" );
			rbActive.setChecked( true );
		
			// Create a new entry
			
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			System.out.println( "edit LabFacility" );

			// Read ProbTbl entry 
			if ( ! Rec.isValid( labFacilityRec )) SystemHelpers.seriousError( "LabFacilityEditWinController() bad rec" );
			System.out.println( "rec=" + labFacilityRec.getRec());

			LabFacility p = new LabFacility( labFacilityRec );
			

			// Get probTbl info from data struct
			
			txtAbbr.setValue( p.getAbbr());
			txtDesc.setValue( p.getDesc());
			
			Address a = p.getAddress();
			txtStreet.setValue( a.getStreet());
			txtLine2.setValue( a.getLine2());
			txtCity.setValue( a.getCity());
			txtState.setValue( a.getState());
			txtZip.setValue( a.getZip5());
			txtPhone1.setValue( a.getHome_ph());
			txtPhone2.setValue( a.getWork_ph());
		
			
			rbActive.setChecked( p.getStatus() == LabFacility.Status.ACTIVE );
			rbInactive.setChecked( p.getStatus() != LabFacility.Status.ACTIVE );

			
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		LabFacility p;		// LabFacility info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into LabFacility dataBuffer
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new probTbl record
			System.out.println( "Creating new LabFacility record..." );
			p = new LabFacility();
			
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( labFacilityRec == null ) || ( labFacilityRec.getRec() < 2 )) SystemHelpers.seriousError( "LabFacilityEditWinController.save() bad rec" );
	
			// Read probTbl
			p = new LabFacility( labFacilityRec );			
		}
	
		
		
		
		// Load data into prob dataBuffer

		p.setAbbr( txtAbbr.getValue());
		p.setDesc( txtDesc.getValue());
		
		Address a = new Address();
		a.setStreet( txtStreet.getValue());
		a.setLine2( txtLine2.getValue());
		a.setCity( txtCity.getValue());
		a.setState( txtState.getValue());
		a.setZip_code( txtZip.getValue());
		a.setHome_ph( txtPhone1.getValue());
		a.setWork_ph( txtPhone2.getValue());
		p.setAddress( a );
		

		

		// set status to Active unless inactive checked
		p.setStatus( rbInactive.isChecked() ? LabFacility.Status.INACTIVE: LabFacility.Status.ACTIVE );
		p.setValid( Validity.VALID );
		
		
		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.NEWPT ){

			// new probTbl entry
			labFacilityRec = p.writeNew();
			System.out.println( "new LabFacility entry created rec=" + labFacilityRec.toString());
						
			
		} else {	// EditPt.Operation.EDITPT
			
			// existing prob
			p.write( labFacilityRec );
			System.out.println( "edited LabFacility info written, rec=" + labFacilityRec.toString());
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
		labFacilityEditWin.detach();
		return;
	}

	


}
