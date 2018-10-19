package palmed;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listitem;

import palmed.EditDgn.Operation;

import usrlib.Address;
import usrlib.Date;
import usrlib.Decoders;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.ProgressBox;
import usrlib.Rec;
import usrlib.Validity;

public class EditPhrdsWinController extends GenericForwardComposer {

	EditPpo.Operation operation = null; // the operation being performed 
	
	Tabpanel tabpanel; 
	
	Window editPhrdsWin; //autowired - window
	Rec phrdsRec; // Pharmarcy code 
	
	Button save; 		//autowired - save button
	Button cancel;		//autowired - cancel button
	
	Label l_phrds;      //autowired 
	
    Textbox phrdsName;  //autowired
    Textbox phrdsAbbr;  //autowired
    Textbox phrdsc;     //autowired
    Textbox phrdsstreet;  //autowired
    Textbox phrdsLine2;  //autowired
    Textbox phrdscity;  //autowired
    Textbox phrdsstate;  //autowired
    Textbox phrdszip;  //autowired
    
public void doAfterCompose( Component component){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Execution exec = Executions.getCurrent();
		
		if ( exec != null ){
			Map<String,Object>myMap = exec.getArg();
			if ( myMap != null ){
				//System.out.println("Got here... ");
				try{ operation = (EditPpo.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /*ignore*/ };
				try{ phrdsRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /*ignore */ };
			}
		}
  
		// make sure we have an operation 
		if ( operation == null ){ SystemHelpers.seriousError( "EditPhrdsWinController() operation==null" );}
		
		if (( operation != null ) && ( operation == EditPpo.Operation.NEWPPO )){
		
				// Create a new Pharmacy
					System.out.println("New Pharmacy");
				}
		
		else { //operation == EditPpo.Operation.EDITPpo
			
			System.out.println("operation controller is: "+ operation );
			System.out.println("edit Phrds pharmacy:"+ phrdsRec);
			
			if (( phrdsRec == null ) || (phrdsRec.getRec() < 2 )) SystemHelpers.seriousError("PhrdsEditWinController() has encountered bad rec");
			
			Phrds Phrds = new Phrds( phrdsRec );
			
			System.out.println("The abbr of the pharmacy being edited is = "+ Phrds.getAbbr());
			
			// Get the information and set labels
			
			editPhrdsWin.setTitle( Phrds.getName());
			l_phrds.setValue(Phrds.getAbbr());
			
			phrdsName.setValue( Phrds.getName());
			phrdsAbbr.setValue( Phrds.getAbbr());
			phrdsc.setValue(Phrds.getContact()); 
			
			
			//Pharmacy address
			Address Pa = Phrds.getAddress();
			phrdsstreet.setValue(Pa.getStreet());
			phrdsLine2.setValue(Pa.getLine2());
			phrdscity.setValue(Pa.getCity());   
			phrdsstate.setValue(Pa.getState());
			phrdszip.setValue(Pa.getZip_code());
				
		}
		return;
		
		}	

/**
 * Save 
 * 
 */

public boolean save() {
	
	Phrds Phrds;     //Pharmarcy directory
	
	System.out.println("Saving Pharmarcy.....");
	
	if (( phrdsName.getValue().trim().length() < 1) 
			|| (( phrdsName.getValue().trim().length() <2 ) && (phrdsAbbr.getValue().trim().length() <1 )) 
			|| ( phrdsc.getValue().trim().length() < 2 )){
		DialogHelpers.Messagebox( "You need to enter a complete Pharmarcy please. ");
		return false;
	}
	
	// TODO Make sure pharmacy is unique 
	
	if (( operation != null ) && (operation == EditPpo.Operation.NEWPPO)){
		
		// Creating a new Pharmacy
		System.out.println("Creating a new pharmacy...");
		Phrds = new Phrds();
		
		Phrds.setValid(Validity.VALID.getCode() & 0xff) ;
		System.out.println("The Validity  has been set to: "+Validity.VALID.getCode()+" at the time of save.");
		
		// create/allocate a new Phrds record
		Phrds.setRec( phrdsRec );
		
		//phrdsRec = Phrds.writeNew();
		
		
	} else {
		
		// make sure we have a Pharmacy
		if ((phrdsRec == null) || (phrdsRec.getRec() < 2)){
			System.out.println( "Error: EditPhrdsWinController().save() ppoRec=" + phrdsRec.getRec() );
			//TODO add system helpers or Dialog helpers if need be
			phrdsRec = new Rec(2);  // for testing purposes 
		}
		
		// Read pharmacy info from Phrds
		Phrds = new Phrds( phrdsRec );
		Phrds = new Phrds( Phrds.getRec());
	}
	
	// Load data into Phrds 
	// set Pharmacy's , Name, Abbr, Contact and addresses
	Phrds.setName( phrdsName.getValue());
	Phrds.setAbbr( phrdsAbbr.getValue());
	Phrds.setContact(phrdsc.getValue());
		
	Phrds.setAddress( new Address( phrdsstreet.getValue(), phrdsLine2.getValue(), phrdscity.getValue(), phrdsstate.getValue(),
					phrdszip.getValue(),"","", ""));
	
	//TODO add home and work if need be 
	
	System.out.println("dumping..");
	Phrds.dump();
	
	// Save (write) records back to the database 
	if (( operation == EditPpo.Operation.NEWPPO) || (phrdsRec == null ) || ( phrdsRec.getRec() == 0)){
		
		//new pharmacy 
		phrdsRec = Phrds.writeNew();
		Phrds.setRec( phrdsRec );
		System.out.println( " new pharmacy created with phrdsRec: "+ phrdsRec.getRec());
		
		//log the access
		AuditLogger.recordEntry(AuditLog.Action.PHRDS_ADD, null, Phrds.getRec(), phrdsRec , Phrds.getAbbr());
	 			
		
	}else {  // EditPpo.Operation.EDITPPO
		
		//Existing pharmacy
		Phrds.write();
		System.out.println( "edited pharmacy written to, phrdsRec: "+ phrdsRec.getRec());		
	}
	
	return true;
}

public void onClose$editPhrdsWin( Event e ) throws InterruptedException{
	alert( "onClose event");
	onClick$cancel( e );
	return;
}

public void onClick$save( Event e ) throws InterruptedException{
	//TODO - defaults for yes/no
	
	if ( Messagebox.show( "Are you sure you will like to save this Information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES){
		
		if ( save()){
			alert ("The Information entered has been saved!");
			closeWin();
		}
		
	}else {
		
		if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES){
			closeWin();
		}
	}
	
	return;
} 

public void onClick$cancel( Event e ) throws InterruptedException{
	
	if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES){
		 closeWin();
	}
	
	return;
}

private void closeWin(){
	
	//close editPhrdsWin
	editPhrdsWin.detach();
	
	
	return;
	
}

}
