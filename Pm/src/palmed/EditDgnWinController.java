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

import usrlib.Address;
import usrlib.Date;
import usrlib.Decoders;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.ProgressBox;
import usrlib.Rec;
import usrlib.Validity;
import palmed.DgnWincontroller;

public class EditDgnWinController extends GenericForwardComposer {
	
	EditDgn.Operation operation = null; //operation to perform
	
	Tabpanel tabpanel;
	
	Window editDgnWin; //autowired -window 
	Rec dgnRec;  //diagnosis code
	
	Button save;	   //autowired - save button
	Button cancel; 	   //autowired - cancel button
	
	Label l_dgncode;   //autowired
	
	Textbox dgnDesc;   //autowired
	Textbox dgnAbbr;   //autowired
	Textbox dgnCode1;  //autowired
	Textbox dgnCode2;  //autowired
	Textbox dgnCode3;  //autowired
	Textbox dgnrv;  //autowired
	Textbox dgndc;  //autowired
	Textbox dgnwo;  //autowired
	Textbox dgnda;  //autowired
	
	public void doAfterCompose(Component component){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Execution exec = Executions.getCurrent();
		
		if ( exec != null ){
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (EditDgn.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ dgnRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
 // make sure we have an operation
 if ( operation == null ) SystemHelpers.seriousError( "EditDgnWinController() operation==null" );		
		
 if (( operation != null ) && ( operation == EditDgn.Operation.NEWDGN )){
			
			// Create a new Diagnosis Code
				System.out.println("New Code");			
			}
	
  else {	// operation == EditDgn.Operation.EDITDGN
	
	 System.out.println("edit Dgn Code:"+dgnRec);
	 //System.out.println(dgnRec.getRec());
	// make sure we have a Code
	if (( dgnRec == null ) || (dgnRec.getRec() < 2 )) SystemHelpers.seriousError("DgnEditWinController() has encountered bad rec");
	/*if (( dgnRec == null ) || dgnRec.getRec() < 2 ){
				System.out.println( "Error: Problem still exists , setting to pre-determined value" );
				dgnRec = new Rec( 1960 );		
			}*/
	
	//System.out.println( "EditDgnWinController() is editing dgnRec=" + dgnRec.getRec());
	//dgnRec = Rec;

	
	// Read patient info from Dgn (Dgn.java) 
	Dgn Dgn = new Dgn( dgnRec );

	
	System.out.println("The abbr of the code being edited is = "+ Dgn.getAbbr());


	// Get some Code values and set labels
	
	
	
	editDgnWin.setTitle( Dgn.getDesc());
	l_dgncode.setValue(Dgn.getAbbr());
	
	dgnDesc.setValue( Dgn.getDesc());
	dgnAbbr.setValue( Dgn.getAbbr());
	dgnCode1.setValue( Dgn.getCode(1));
	dgnCode2.setValue( Dgn.getCode(2));
	dgnCode3.setValue(Dgn.getCode(3));
	dgnrv.setValue((Dgn.getrv().getPrintable()));
	
			

 }
 
 return;
 
 
 }


	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Dgn	Dgn;		// Diagnosis Code directory
		
		
		System.out.println("Saving......");
		
		if (( dgnDesc.getValue().trim().length() < 1 )
				|| (( dgnDesc.getValue().trim().length() < 2 ) && ( dgnAbbr.getValue().trim().length() < 1 ))
				|| ( dgnCode1.getValue().trim().length() < 2 )){
			DialogHelpers.Messagebox( "You must enter a complete Diagnosis Code." );
			return false;
		}
		
		//desc, Abbr + code 1 &2 
		//String dgnDesc = new String.valueOf(dgnDesc).trim();
				

		
		
		//TODO Make sure code is unique
		// verify unique Code
	
				
		if (( operation != null ) && ( operation == EditDgn.Operation.NEWDGN )){

			// create a new Code
			System.out.println("Creating a new Diagnosis Code...");
			Dgn = new Dgn();
			
			// TODO use this for a restore button in DgnWincontroller (if need be)
			// set validity byte
			Dgn.setValid(Validity.VALID.getCode() & 0xff);
			System.out.println("The Validity code at time of save:"+Validity.VALID.getCode());
			
			
			// create/allocate a new Dgn record
			Dgn.setRec( dgnRec );
			
			//dgnRec = Dgn.writeNew();
			
			
			
		} else { 
			
			

			// make sure we have a Diagnosis Code
			if ((dgnRec == null) || (dgnRec.getRec() < 2)){
				System.out.println( "Error: EditDgnWinController().save() dgnRec=" + dgnRec.getRec());
				dgnRec = new Rec( 2 );		// default for testing
			}
	
			// Read diagnosis info from Dgn
			Dgn = new Dgn( dgnRec );
			Dgn = new Dgn( Dgn.getRec());
			
		}
	
			
		
		// Load data into Dgn		
		// set Code's Description, Abbreviation, Code 1 , 2 & 3
		Dgn.setDesc( dgnDesc.getValue());
		Dgn.setAbbr( dgnAbbr.getValue());
		//int set1 = Integer.parseInt(dgnSet1.getValue().trim());
		Dgn.setCode( 1,dgnCode1.getValue());
		Dgn.setCode( 2,dgnCode2.getValue());
		Dgn.setCode( 3,dgnCode3.getValue());
		if(!dgnrv.getValue().trim().isEmpty()){Dgn.setrv((dgnrv.getValue().trim()));}else{ Dgn.setrv("0");}
		
		
		//TODO Default Charge, write - off & Assignment
		
		
		
		System.out.println( "dump" );
		Dgn.dump();	
		
		// Save (write) records back to database
		if (( operation == EditDgn.Operation.NEWDGN ) || ( dgnRec == null ) || ( dgnRec.getRec() == 0 )){
			
			// new code
			dgnRec  = Dgn.writeNew();
			Dgn.setRec( dgnRec );
			
			System.out.println( "new code created dgnrec=" + dgnRec.getRec());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.DGNCODE_ADD, null, Dgn.getRec(), dgnRec, Dgn.getAbbr() );
			
			
			
		} else {	// EditDgn.Operation.EDITDGN
			
			// existing code
			Dgn.write();
			System.out.println( "edited code info written to, dgnrec: " + dgnRec.getRec());

			
		}
				
		return true;
	}
	
	public void onClose$editDgnWin( Event e ) throws InterruptedException{
		alert( "onCloset event");
		onClick$cancel( e );
		return;
	}
	
	
	
	public void onClick$save( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
	if ( Messagebox.show( "Are you sure you will like to save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editDgnWin, "Saving..." );

			if ( save()){
				//progBox.close();
				alert ("The Diagnosis Code has been saved!");
				closeWin();
				
				
			}
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$cancel( Event e ) throws InterruptedException  {

		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			closeWin();
		}
		
		return;
	}

	
	
	private void closeWin(){
		
		// close editDgnWin
		editDgnWin.detach();


		return;
	}
}
