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


public class EditPpoWinController extends GenericForwardComposer {
	
	EditPpo.Operation operation = null; //operation being performed
	
	Tabpanel tabpanel;
	
	Window editPpoWin; 	//autowired - window
	Rec ppoRec; 		// insurance carrier code
	Include pg2;		//autowired ,  second page of edit ( practice specific info)
	
	
	
	Button save;    //autowired - save button
	Button cancel;  //autowired - cancel button
	
	Label l_ppo; //autowired
	
	Textbox ppoDesc; //autowired
	Textbox ppoAbbr; //autowired
	Textbox ppoc1;   //autowired
	Textbox ppoc2;   //autowired 
	Textbox ppoc3;   //autowired
	Textbox ppoc4;   //autowired
	Textbox ppon1;   //autowired
	Textbox ppon2;   //autowired
	Textbox ppon3;   //autowired
	Textbox ppon4;   //autowired
	Textbox ppop1;   //autowired
	Textbox ppop2;   //autowired
	Textbox ppop3;   //autowired
	Textbox ppop4;   //autowired
	Textbox ppop5;   //autowired
	Textbox ppop6;   //autowired
	Textbox ppostreet; 			  //autowired
	Textbox ppoLine2;  			 //autowired
	Textbox ppocity;  			 //autowired
	Textbox ppostate;  			 //autowired
	Textbox ppozip;  			 //autowired
	Textbox InsCmpID; 			//autowired
	Textbox InsCmpSubID;		 //autowired
	Textbox EtsRecCode; 		//autowired
	Textbox HmoCopayAmount;		 //autowired
	Textbox HmoNoPmtSurcharge; 	//autowired
	Textbox HmoCopayPercent; 	//autowired
	Textbox NEIC; 				//autowired
	Textbox NEICsubID; 			//autowired
	Textbox MediGAP; 			//autowired
	Textbox NEICsubIDx; 		//autowired
	
	
	
	public void doAfterCompose( Component component){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//tie in the second zul file (editPpoinfo.zul)
		Components.wireVariables( pg2, this );
		Components.addForwards( pg2, this );
		
		
		Execution exec = Executions.getCurrent();
		
		if ( exec != null ){
			Map<String,Object>myMap = exec.getArg();
			if ( myMap != null ){
				System.out.println("Got here... ");
				try{ operation = (EditPpo.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /*ignore*/ };
				try{ ppoRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /*ignore */ };
			}
		}
	
	// make sure we have an operation 
	if ( operation == null ){ SystemHelpers.seriousError( "EditPpoWinController() operation==null" );}
	
	if (( operation != null ) && ( operation == EditPpo.Operation.NEWPPO )){
	
			// Create a new Insurance Carrier
				System.out.println("New Ins Carrier");
			}
	
	else { //operation == EditPpo.Operation.EDITPpo
		
		System.out.println("operation controller is: "+ operation );
		System.out.println("edit Ppo Carrier:"+ ppoRec);
		
		if (( ppoRec == null ) || (ppoRec.getRec() < 2 )) SystemHelpers.seriousError("PpoEditWinController() has encountered bad rec");
		
		Ppo Ppo = new Ppo( ppoRec );
		Ppoinfo Ppoinfo = new Ppoinfo( ppoRec ); // new Ppoinfo file using the same rec 
		
		System.out.println("The abbr of the carrier being edited is = "+ Ppo.getAbbr());
		
		// Get the information and set labels
		
		editPpoWin.setTitle( Ppo.getname());
		l_ppo.setValue(Ppo.getAbbr());
		
		ppoDesc.setValue( Ppo.getname());
		ppoAbbr.setValue( Ppo.getAbbr());
		ppoc1.setValue(Ppo.getContact(1)); 
		ppoc2.setValue(Ppo.getContact(2));   
		ppoc3.setValue(Ppo.getContact(3));   
		ppoc4.setValue(Ppo.getContact(4));   
		ppon1.setValue(Ppo.getnote(1));   
		ppon2.setValue(Ppo.getnote(2));   
		ppon3.setValue(Ppo.getnote(3));   
		ppon4.setValue(Ppo.getnote(4)); 
		ppop1.setValue(Ppo.getphone(1));   
		ppop2.setValue(Ppo.getphone(2));   
		ppop3.setValue(Ppo.getphone(3));   
		ppop4.setValue(Ppo.getphone(4));  
		ppop5.setValue(Ppo.getphone(5));  
		ppop6.setValue(Ppo.getphone(6));
		
		//Insurance address
		Address Ia = Ppo.getAddress();
		ppostreet.setValue(Ia.getStreet());
		ppoLine2.setValue(Ia.getLine2());
		ppocity.setValue(Ia.getCity());   
		ppostate.setValue(Ia.getState());
		ppozip.setValue(Ia.getZip_code());
		
		
		//information in second page form Ppoinfo
		InsCmpID.setValue(Ppoinfo.getInsCmpID());
		InsCmpSubID.setValue(Ppoinfo.getInsCmpSubID());
		EtsRecCode.setValue(Ppoinfo.getEtsRecCode());
		HmoCopayAmount.setValue(Ppoinfo.getHmoCopayAmount().getPrintable());
		HmoNoPmtSurcharge.setValue(Ppoinfo.getHmoNoPmtSurcharge().getPrintable());
		HmoCopayPercent.setValue(Integer.toString(Ppoinfo.getHmoCopayPercent()));
		NEIC.setValue(Ppoinfo.getNEIC());
		NEICsubID.setValue(Ppoinfo.getNEICsubID());
		MediGAP.setValue(Ppoinfo.getMediGAP());
		//NEICsubIDx.setValue(Ppoinfo.getN)
		
	}
	
	return;
		
	}
	
	/**
	 * Save 
	 * 
	 */
	
	public boolean save() {
		
		Ppo Ppo;     //Insurance carrier directory
		Ppoinfo Ppoinfo; // Practice Specific Info		
		
		System.out.println("Saving Ins Carrier.....");
		
		if (( ppoDesc.getValue().trim().length() < 1) 
				|| (( ppoDesc.getValue().trim().length() <2 ) && (ppoAbbr.getValue().trim().length() <1 )) 
				|| ( ppoc1.getValue().trim().length() < 2 )){
			DialogHelpers.Messagebox( "You need to enter a complete Insurance Carrier please. ");
			return false;
		}
		
		// TODO Make sure insurance carrier is unique 
		
		if (( operation != null ) && (operation == EditPpo.Operation.NEWPPO)){
			
			// Creating a new Insurance carrier
			System.out.println("Creating a new Insurance Code...");
			Ppo = new Ppo();
			Ppoinfo = new Ppoinfo();
			
			Ppo.setValid(Validity.VALID.getCode() & 0xff) ;
			Ppoinfo.setValid(Validity.VALID.getCode() & 0xff) ;
			System.out.println("The Validity code has been set to: "+Validity.VALID.getCode()+" at time of save.");
			
			// create/allocate a new Ppo record
			//Ppo.setRec( Ppo.writeNew());
						
			//ppoRec = Ppo.writeNew(); 
			Ppo.setRec( ppoRec );
			
			Ppoinfo.setRec( ppoRec );
			//Ppoinfo.setRec( Ppo.writeNew());
		
			//log the access 
			//AuditLogger.recordEntry(AuditLog.Action.PPO_ADD, null, Ppo.getRec(), ppoRec , Ppo.getAbbr());
			
		 			
		} else {
			
			// make sure we have an Insurance Carrier 
			if ((ppoRec == null) || (ppoRec.getRec() < 2)){
				System.out.println( "Error: EditPpoWinController().save() ppoRec=" + ppoRec.getRec() );
				//TODO add system helpers or Dialog helpers if need be
				ppoRec = new Rec(2);  // for testing purposes 
			}
			
			// Read insurance info from Ppo
			Ppo = new Ppo( ppoRec );
			Ppo = new Ppo( Ppo.getRec());
			Ppoinfo = new Ppoinfo( ppoRec );
			Ppoinfo = new Ppoinfo( Ppo.getRec());
			
		}
		
		// Load data into Ppo 
		// set Carriers, Name, Abbr, Contacts(1-4),(Notes 1-4), Phones (1-6) and addresses
		Ppo.setname( ppoDesc.getValue());
		Ppo.setAbbr( ppoAbbr.getValue());
		Ppo.setContact( 1, ppoc1.getValue());
		Ppo.setContact( 2, ppoc2.getValue());
		Ppo.setContact( 3, ppoc3.getValue());
		Ppo.setContact( 4, ppoc4.getValue());
		Ppo.setnote( 1, ppon1.getValue());
		Ppo.setnote( 2, ppon2.getValue());
		Ppo.setnote( 3, ppon3.getValue());
		Ppo.setnote( 4, ppon4.getValue());
		Ppo.setphone( 1, ppop1.getValue());
		Ppo.setphone( 2, ppop2.getValue());
		Ppo.setphone( 3, ppop3.getValue());
		Ppo.setphone( 4, ppop4.getValue());
		Ppo.setphone( 5, ppop5.getValue());
		Ppo.setphone( 6, ppop6.getValue());
		
		Ppo.setAddress( new Address( ppostreet.getValue(), ppoLine2.getValue(), ppocity.getValue(), ppostate.getValue(),
						ppozip.getValue(),"","", ""));
		
		//TODO add home and work if need be 
		
		//Set Practice Specific info
		Ppoinfo.setInsCmpID(  InsCmpID.getValue());
		Ppoinfo.setInsCmpSubID( InsCmpSubID.getValue());
		Ppoinfo.setEtsRecCode( EtsRecCode.getValue());
		if (!HmoCopayAmount.getValue().trim().isEmpty()){Ppoinfo.setHmoCopayAmount( HmoCopayAmount.getValue().trim());} else{ Ppoinfo.setHmoCopayAmount("0");}
		if (!HmoNoPmtSurcharge.getValue().trim().isEmpty()){Ppoinfo.setHmoNoPmtSurcharge( HmoNoPmtSurcharge.getValue().trim());} else{ Ppoinfo.setHmoNoPmtSurcharge("0");}
		if (!HmoCopayPercent.getValue().trim().isEmpty()){Ppoinfo.setHmoCopayPercent(Integer.parseInt(HmoCopayPercent.getValue()));} else{ Ppoinfo.setHmoCopayPercent(Integer.parseInt("0"));}
		Ppoinfo.setNEIC( NEIC.getValue());
		Ppoinfo.setNEICsubID( NEICsubID.getValue());
		Ppoinfo.setMediGAP( MediGAP.getValue());
		
		
		System.out.println("dumping..");
		Ppo.dump();
		Ppoinfo.dump();
		
		// Save (write) records back to the database 
		if (( operation == EditPpo.Operation.NEWPPO) || (ppoRec == null ) || ( ppoRec.getRec() == 0)){
			
			//new insurance carrier
			ppoRec = Ppo.writeNew();
			Ppo.setRec( ppoRec );
			Ppoinfo.write( ppoRec );
			System.out.println( " new insurance carrier created with ppoRec: "+ ppoRec.getRec());
			//log the access 
			AuditLogger.recordEntry(AuditLog.Action.PPO_ADD, null, Ppo.getRec(), ppoRec , Ppo.getAbbr());
			
			
		}else {  // EditPpo.Operation.EDITPPO

			//Existing insurance carrier 
			Ppo.write();
			Ppoinfo.write();
			System.out.println( "edited insurance carrier written to, ppoRec: "+ ppoRec.getRec());		
		}
		
		return true;
	}
	
	public void onClose$editPpoWin( Event e ) throws InterruptedException{
		alert( "onClose event");
		onClick$cancel( e );
		return;
	}
	
	public void onClick$save( Event e ) throws InterruptedException{
		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Are you sure you will like to save this Information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES){
			
			if ( save()){
				alert ("The Information entered has been saved successfully!");
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
		
		//close editPpoWin
		editPpoWin.detach();
		
		
		return;
		
	}
	
}
