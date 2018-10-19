package palmed;

import java.io.*;
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
import org.zkoss.zul.Vbox;
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

public class EditSrvWinController extends GenericForwardComposer {

	EditDgn.Operation operation = null; //operation to perform 
	
	Tabpanel tabpanel;
	
	Window editSrvWin; //autowired - window
	Rec srvRec;  //service code 
	
	Button save;      //autowired - save button
	Button cancel;    //autowired - cancel button
	
	Label l_srvcode; //autowired label
	
	Textbox srvDesc; //autowired textbox
	Textbox srvAbbr; //autowired
	Textbox srvCode1; //autowired
	Textbox srvCode2; //autowired
	Textbox srvCode3; //autowired
	Textbox srvrv; //autowired
	Textbox srvdc; //autowired
	Textbox srvwo; //autowired
	Textbox srvda; //autowired
	
	Vbox feebox; //autowired
	Textbox srvedate; //autowired
	Textbox srvfee; //autowired
	Textbox srvadj; //autowired
	Textbox srvasn; //autowired
	
	
	
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
				try{ srvRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
	
		// make sure we have an operation
		 if ( operation == null ) SystemHelpers.seriousError( "EditSrvWinController() operation == null" );		
				
		 if (( operation != null ) && ( operation == EditDgn.Operation.NEWDGN )){
					
					// Create a new Service Code
						System.out.println("New Code");		
						srvDesc.setFocus(true);
						feebox.setVisible(true);
						//srvrv.setConstraint("no empty: Please enter a relative value!");
					}
			
		  else {	// operation == EditDgn.Operation.EDITDGN
			
			 System.out.println("edit Srv Code:"+srvRec);
			
			// make sure we have a Code
			if (( srvRec == null ) || (srvRec.getRec() < 2 )) SystemHelpers.seriousError("EditSrvWinController() has encountered bad rec");
			
						
			// Read patient info from Srv (Srv.java) 
			Srv Srv = new Srv( srvRec );

			
			System.out.println("The abbr of the code being edited is = "+ Srv.getAbbr());

			// Get some Code values and set label			
			editSrvWin.setTitle( Srv.getDesc());
			l_srvcode.setValue(Srv.getAbbr());
			
			srvDesc.setValue( Srv.getDesc());
			srvAbbr.setValue( Srv.getAbbr());
			srvCode1.setValue( Srv.getCode(1));
			srvCode2.setValue( Srv.getCode(2));
			srvCode3.setValue(Srv.getCode(3));
			srvrv.setValue(Srv.getrv().getPrintable());
			
		 }
		 
		 return;
		 
		 }
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Srv	Srv;		// Service Code directory
		Fee	Fee;		// Fee Schedule 
		
		
		System.out.println("Saving......");
		
		if (( srvDesc.getValue().trim().length() < 1 )
				|| (( srvDesc.getValue().trim().length() < 2 ) && ( srvAbbr.getValue().trim().length() < 1 ))
				|| ( srvCode1.getValue().trim().length() < 2 )){
			DialogHelpers.Messagebox( "You must enter a complete Service Code." );
			return false;
		}
		
		//desc, Abbr + code 1 &2 
		//String srvDesc = new String.valueOf(srvDesc).trim();
				

		
		
		//TODO Make sure code is unique
		// verify unique Code
	
				
		if (( operation != null ) && ( operation == EditDgn.Operation.NEWDGN )){

			// create a new Code
			System.out.println("Creating a new Service Code...");
			Srv = new Srv();
			
			
			// set validity byte
			Srv.setValid(Validity.VALID.getCode() & 0xff);
			System.out.println("The Validity code at time of save:"+Validity.VALID.getCode());
			
			
			// create/allocate a new Srv record
			//srvRec = Srv.writeNew();
			
			Srv.setRec( srvRec );
			
			

			
		} else { 
			
			

			// make sure we have a Diagnosis Code
			if ((srvRec == null) || (srvRec.getRec() < 2)){
				System.out.println( "Error: EditSrvWinController().save() srvRec=" + srvRec.getRec());
				srvRec = new Rec( 2 );		// default for testing
			}
	
			// Read diagnosis info from Dgn
			Srv = new Srv( srvRec );
			Srv = new Srv( Srv.getRec());
			
		}
	
			
		
		// Load data into Srv	
		// set Code's Description, Abbreviation, Code 1 , 2 & 3
		Srv.setDesc( srvDesc.getValue());
		Srv.setAbbr( srvAbbr.getValue());
		//int set1 = Integer.parseInt(srvSet1.getValue().trim());
		Srv.setCode( 1,srvCode1.getValue());
		Srv.setCode( 2,srvCode2.getValue());
		Srv.setCode( 3,srvCode3.getValue());
		if (!srvrv.getValue().trim().isEmpty()){Srv.setrv(srvrv.getValue().trim());}else{ Srv.setrv("0");}
		
		
		//TODO Default Charge, write - off & Assignment
		
		
		
		System.out.println( "dump" );
		Srv.dump();	
		
		// Save (write) records back to database
		if (( operation == EditDgn.Operation.NEWDGN ) || ( srvRec == null ) || ( srvRec.getRec() == 0 )){
			
			// new code
			srvRec  = Srv.writeNew();
			Srv.setRec(srvRec);
			
			for (int i = 0; i < 100; i++ ){
				
				String fn_fees = "cdesrv"+ String.format("%02d", i) +".ovd";
				File file = new File( Pm.getOvdPath(), fn_fees );
				
				if( file.exists()){
				
				Fee = new Fee();
				Fee.setValid(Validity.VALID.getCode() & 0xff);
				Fee.setRec( srvRec );
				
				Fee.setEffectivedate(new Date(srvedate.getValue().trim()));
				if (!srvfee.getValue().trim().isEmpty()){ Fee.setFee(srvfee.getValue().trim());}else{ Fee.setFee("0");}
				if (!srvadj.getValue().trim().isEmpty()){ Fee.setAdj(srvadj.getValue().trim());}else{ Fee.setAdj("0");}
				if (!srvasn.getValue().trim().isEmpty()){ Fee.setAsn(srvasn.getValue().trim());}else{ Fee.setAsn("0");}
				
				Fee.dump();
				
				Fee.write(srvRec, i); }
				
				else { System.out.println("the file: "+fn_fees+" doesn't exist, therefore no fee schedule was created.");}
				
			}
			System.out.println( "new code created srvrec=" + srvRec.getRec());
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SRVCODE_ADD, null, Srv.getRec(), srvRec, Srv.getAbbr() );
			
			
			
		} else {	// EditDgn.Operation.EDITDGN
			
			// existing code
			Srv.write();
			System.out.println( "edited code info written to, srvrec: " + srvRec.getRec());

			
		}
				
		return true;
	}
	
	
	public void onClose$editSrvWin( Event e ) throws InterruptedException{
		alert( "onCloset event");
		onClick$cancel( e );
		return;
	}
	
	
	
	public void onClick$save( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
	if ( Messagebox.show( "Are you sure you will like to save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editSrvWin, "Saving..." );

			if ( save()){
				//progBox.close();
				alert ("The Service Code has been saved!");
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
		editSrvWin.detach();


		return;
	}
}	
	
	
	

