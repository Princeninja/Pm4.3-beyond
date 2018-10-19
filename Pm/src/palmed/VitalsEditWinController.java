package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;

public class VitalsEditWinController extends GenericForwardComposer {
	
	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	

	Window vitalsEditWin;	// autowired - window	
	Reca vitalsReca;		// vitals reca
	
	
	Button save;		// autowired - save button
	Button cancel;		// autowired - cancel button

	Textbox date;		// autowired
	Textbox temp;		// autowired
	Textbox pulse;		// autowired
	Textbox resp;		// autowired
	Textbox sbp;		// autowired
	Textbox dbp;		// autowired
	Textbox height;		// autowired
	Textbox weight;		// autowired
	Textbox head;		// autowired
	
	Textbox pO2;		// autowired
	Label	bmi;		// autowired
	
	//TODO temp - oral vs axillary, etc
	//TODO note
	//TODO on O2
	
	
	
		
	
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
				try{ vitalsReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "VitalsEditWinController() operation==null" );
		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )) SystemHelpers.seriousError( "VitalsEditWinController() bad ptRec" );
		

		
		// Set ptname in window
		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			// Create a new set of vitals
			
			// set date to today's date
			date.setValue( Date.today().getPrintable(9));
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read vitals info 
			if (( vitalsReca == null ) || ( vitalsReca.getRec() < 2 )) SystemHelpers.seriousError( "VitalsEditWinController() bad reca" );
			Vitals vitals = new Vitals( vitalsReca );
				
			String s;
			double d;
			int i;
			
			
			// Get vitals from data struct
			// Convert units if necessary
			// TODO - set appropriate units type listboxes
			// Don't set data if ZERO - that is a non-entered value
			
			date.setValue( vitals.getDate().getPrintable(9));
			
			d = vitals.getTemp();
			if ( d > 1 ){
				if ( true ) d = UnitHelpers.getFahrenheit( d );
				s = ( d == 0 ) ? "": String.format( "%4.1f", d );
				temp.setValue( s );
			}
			
			i = vitals.getPulse();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			pulse.setValue( s );
			
			i = vitals.getResp();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			resp.setValue( s );
			
			i = vitals.getSBP();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			sbp.setValue( s );
			
			i = vitals.getDBP();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			dbp.setValue( s );
			
			d = vitals.getHeight();
			if ( true ) d = UnitHelpers.getInches( d );
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			height.setValue( s );
			
			d = (double) vitals.getWeight();
			if ( true ) d = UnitHelpers.getPoundsFromGrams( d );
			//if ( true ) d = UnitHelpers.getKilogramsFromGrams( d );
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			weight.setValue( s );
			
			d = vitals.getHead();
			if ( true ) d = UnitHelpers.getInches( d );
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			head.setValue( s );
			
			i = vitals.getPO2();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			pO2.setValue( s );
			
			
			// update BMI label
			updateBMI();
			
			
				//TODO set metric/US listboxes appropriately
		

			
			//EditHelpers.setListboxSelection( ptSex, dirPt.getSex());
			//EditHelpers.setListboxSelection( ptRace, dirPt.getRace());
			//EditHelpers.setListboxSelection( ptMarital, dirPt.getMarital());
			//EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());
			//EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());

	
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		Vitals	vitals;		// vitals info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into Vitals dataBuffer
		
		Date vdate = new Date( date.getValue());
		if (( vdate == null ) || ( ! vdate.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + date.getValue() + "." );
			return;			
		}
		
		
		String str = temp.getValue();
		double t = 0;
		if (( str != null ) && ( str.trim().length() > 0 )){
			t = EditHelpers.parseDouble( str );
			if ( true ) t = UnitHelpers.getCelcius( t );
			if (( t != 0 ) && (( t < 30 ) || ( t > 50 ))){
				DialogHelpers.Messagebox( "Temperature out of range." );
				return;
			}
		}
		
		int p = EditHelpers.parseInt( pulse.getValue());
		if (( p < 0 ) || ( p > 400 )){			
			DialogHelpers.Messagebox( "Pulse out of range." );
			return;
		}
		
		int r = EditHelpers.parseInt( resp.getValue());
		if (( r < 0 ) || ( r > 100 )){			
			DialogHelpers.Messagebox( "Respirations out of range." );
			return;
		}
		
		int s = EditHelpers.parseInt( sbp.getValue());
		if (( s < 0 ) || ( s > 300 )){			
			DialogHelpers.Messagebox( "SBP out of range." );
			return;
		}
		
		int d = EditHelpers.parseInt( dbp.getValue());
		if (( d < 0 ) || ( d > 200 )){			
			DialogHelpers.Messagebox( "DBP out of range." );
			return;
		}
		
		double h = EditHelpers.parseDouble( height.getValue());
		if ( true ) h = UnitHelpers.getCentimeters( h );
		if (( h != 0 ) && (( h < 0 ) || ( h > 300 ))){			
			DialogHelpers.Messagebox( "Height out of range." );
			return;
		}
		
		double w = EditHelpers.parseDouble( weight.getValue());
		if ( true ) w = UnitHelpers.getGramsFromPounds( w );
		// if ( true ) w = UnitHelpers.getGramsFromKilograms( w );
		if (( w != 0 ) && (( w < 500 ) || ( w > 500000 ))){			
			DialogHelpers.Messagebox( "Weight out of range." );
			return;
		}
		
		double c = EditHelpers.parseDouble( head.getValue());
		if ( true ) c = UnitHelpers.getCentimeters( c );
		if (( c != 0 ) && (( c < 0 ) || ( c > 400 ))){			
			DialogHelpers.Messagebox( "Head circumference out of range." );
			return;
		}
		
		int o = EditHelpers.parseInt( pO2.getValue());
		if (( o != 0 ) && (( o < 30 ) || ( o > 100 ))){			
			DialogHelpers.Messagebox( "Pulse 02 out of range." );
			return;
		}
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new vitals record
			System.out.println( "Creating new vitals record..." );
			vitals = new Vitals();
			
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( vitalsReca == null ) || ( vitalsReca.getRec() < 2)) SystemHelpers.seriousError( "VitalsEditWinController.save() bad reca" );
	
			// Read vitals
			vitals = new Vitals( vitalsReca );			
		}
	
		
		
		
		// Load data into Vitals dataBuffer
		// Note: Filtering ZERO or non-entered data is handled in the vitals setters

		vitals.setPtRec( ptRec );
		vitals.setDate( vdate );
		vitals.setStatus( Vitals.Status.CURRENT );
		
		vitals.setTemp( t );		
		vitals.setPulse( p );
		vitals.setResp( r );
		vitals.setSBP( s );
		vitals.setDBP( d );
		vitals.setHeight( h );
		vitals.setWeight( (int) w );
		vitals.setHead( c );
		vitals.setPO2( o );

		
		//prov.setBCBSParProv( EditHelpers.getListboxSelection( bcbsPar ));

		
		// Save (write) records back to database
		if (( operation == EditPt.Operation.NEWPT ) || ( ! Reca.isValid( vitalsReca ))){
			
			// new vitals
			vitalsReca = vitals.postNew( ptRec );

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.VITALS_ADD, ptRec, Pm.getUserRec(), vitalsReca, null );
			

			
			
		} else {	// EditPt.Operation.EDITPT
			
			
			// TODO - really handle edit flag
			vitals.setEdits( vitals.getEdits() + 1 );
			
			// existing vitals
			vitals.write( vitalsReca );
			//System.out.println( "edited vitals info written, vitalsReca=" + vitalsReca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.VITALS_EDIT, ptRec, Pm.getUserRec(), vitalsReca, null );
			
		}
		//vitals.dump();
		
		
		Notifier.notify( ptRec, Notifier.Event.VITALS );
		
		
		// process BMI rule
		if ( ClinicalCalculations.computeBMI( w, h ) > 30 ){
			DialogHelpers.Messagebox( "BMI greater than 30.  Consider ordering nutritional counselling." );
		}
		
		return;
	}

	
	
	public void onClose$vitalsNewWin( Event e ) throws InterruptedException{
		alert( "onClose event");
		onClick$cancel( e );
		return;
	}
	
	
	
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
		vitalsEditWin.detach();
		return;
	}
	
	
	
	public void onChange$height( Event ev ){
		updateBMI();
	}
	
	public void onChange$weight( Event ev ){
		updateBMI();
	}
	
	// update BMI
	
	public void updateBMI(){
		
		double h = EditHelpers.parseDouble( height.getValue());
		if ( true ) h = UnitHelpers.getCentimeters( h );
		
		double w = EditHelpers.parseDouble( weight.getValue());
		if ( true ) w = UnitHelpers.getGramsFromPounds( w );

		if (( h != 0 ) && ( w != 0 )){
			bmi.setValue( String.format( "%4.1f kg/m2", ClinicalCalculations.computeBMI( w, h )));
		}
	}

}
