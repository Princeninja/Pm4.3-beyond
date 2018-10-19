package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Address;
import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;

public class ProvEditWinController extends GenericForwardComposer {

	Rec provRec = null;			// provider record
	EditPt.Operation operation = null;	// operation to perform
	
	Tabpanel tabpanel;		// tab panel this window is in
	

	Window provEditWin;		// autowired - window
	Include pg1;			// autowired
	Include pg2;			// autowired
	Include pg3;			// autowired
	
	
	Button save;			// autowired - save button
	Button cancel;			// autowired - cancel button
	
	Label l_provName;		// autowired
	
	Textbox provAbbr;		// autowired
	Textbox provName;		// autowired
	Textbox provFirst;		// autowired
	Textbox provMiddle;		// autowired
	Textbox provLast;		// autowired
	Textbox provSuffix;		// autowired
	Textbox provDOB;		// autowired
	Textbox provSSN;		// autowired
	
	Textbox homeStreet;		// autowired
	Textbox homeLine2;		// autowired
	Textbox homeCity;		// autowired
	Textbox homeState;		// autowired
	Textbox homeZip;		// autowired
	Textbox homeHome;		// autowired
	Textbox homeWork;		// autowired
	Textbox provCell;		// autowired
	Textbox provEmail;		// autowired
	
	Textbox officeStreet;	// autowired
	Textbox officeLine2;	// autowired
	Textbox officeCity;		// autowired
	Textbox officeState;	// autowired
	Textbox officeZip;		// autowired
	Textbox officeHome;		// autowired
	Textbox officeWork;		// autowired
	
	Textbox licState1;		// autowired
	Textbox licNum1;		// autowired
	Textbox licExp1;		// autowired
	Textbox bnddNum1;		// autowired
	Textbox bnddExp1;		// autowired
	Textbox licState2;		// autowired
	Textbox licNum2;		// autowired
	Textbox licExp2;		// autowired
	Textbox bnddNum2;		// autowired
	Textbox bnddExp2;		// autowired
	Textbox licState3;		// autowired
	Textbox licNum3;		// autowired
	Textbox licExp3;		// autowired
	Textbox bnddNum3;		// autowired
	Textbox bnddExp3;		// autowired
	Textbox licState4;		// autowired
	Textbox licNum4;		// autowired
	Textbox licExp4;		// autowired
	Textbox bnddNum4;		// autowired
	Textbox bnddExp4;		// autowired
	
	Textbox fedTaxID;		// autowired
	Textbox stateTaxID;		// autowired
	Textbox localTaxID;		// autowired

	Textbox provFAA;		// autowired
	Textbox provUPIN;		// autowired
	Textbox provNPI;		// autowired
	Textbox provDEA;		// autowired
	Textbox provCLIA;		// autowired
	Textbox etsProvID;		// autowired
	Textbox etsSubID;		// autowired
	
	Textbox mcareProv;		// autowired
	Textbox mcareGroup;		// autowired
	Textbox mcareEff;		// autowired
	Textbox mcareApp;		// autowired
	Listbox mcarePar;		// autowired
	
	Textbox mcaidProv;		// autowired
	Textbox mcaidEff;		// autowired
	Textbox mcaidApp;		// autowired
	Listbox mcaidPar;		// autowired

	Textbox provBC;			// autowired
	Textbox provBS;			// autowired
	Listbox bcbsPar;		// autowired
	
	
	
	
		
	
	public void doAfterCompose( Component component ){
	
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// autowire included file variables and event handlers
		Components.wireVariables( pg1, this );
		Components.addForwards( pg1, this );

		Components.wireVariables( pg2, this );
		Components.addForwards( pg2, this );

		Components.wireVariables( pg3, this );
		Components.addForwards( pg3, this );

	
		
		// Get arguments from map
		Execution exec = Executions.getCurrent();
	
		if ( exec != null ){
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (EditPt.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ provRec = (Rec) myMap.get( "provRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		
		
		// Is window in a Tabpanel???
		// Get Tabpanel this window is placed in.
		// May have to go several layers deep due to enclosing window.
		if ( component instanceof Tabpanel ){
			tabpanel = (Tabpanel) component;
		} else if ( component.getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent();
		} else if ( component.getParent().getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent().getParent();
		}


		
		// Add close tabpanel event listener
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().addEventListener( "onClose", new EventListener(){ 
				public void onEvent( Event ev ) throws Exception { ev.stopPropagation(); onClick$cancel( ev ); }});
		}
		
		
		
		// Initialize some items
		EditHelpers.loadYNListbox( mcarePar );
		EditHelpers.loadYNListbox( mcaidPar );
		EditHelpers.loadYNListbox( bcbsPar );

		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			// Create a new provider
			
			// Set title of Tabpanel to "New Provider"
			if ( tabpanel != null ){
				tabpanel.getLinkedTab().setLabel( "New Provider" );
				//tabpanel.getLinkedTab().setImage( "ptchart_verysmall.png" );
			}
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			// make sure we have a provider
			if (( provRec == null ) || provRec.getRec() < 2 ){
				System.out.println( "Error: ProvEditWinController() invalid provRec, setting to 2" );
				provRec = new Rec( 2 );		// default for testing
			}
			System.out.println( "EditProvWinController() provRec=" + provRec.getRec());
			
	
			// Read provider info Prov (Pcn)
			Prov prov = new Prov( provRec );
	
			
			// Set title of Tabpanel to Patient's name
			if ( tabpanel != null ){
				tabpanel.getLinkedTab().setLabel( "Edit: " + prov.getName());
				//tabpanel.getLinkedTab().setImage( "ptchart_verysmall.png" );
			}
	
		
			// Get some patient values and set labels
			//ptrec.setValue( "" + ptRec.getRec());
			provEditWin.setTitle( prov.getName());
	
			provAbbr.setValue( prov.getAbbr());
			provName.setValue( prov.getName());
			provFirst.setValue( prov.getFirstName());
			provMiddle.setValue( prov.getMiddleName());
			provLast.setValue( prov.getLastName());
			provSuffix.setValue( prov.getSuffix());
			updateProvName();
			
			


			fedTaxID.setValue( prov.getProvNumber( Prov.Numbers.FED_TAX ));
			stateTaxID.setValue( prov.getProvNumber( Prov.Numbers.STATE_TAX ));
			localTaxID.setValue( prov.getProvNumber( Prov.Numbers.LOCAL_TAX ));

			provDEA.setValue( prov.getProvNumber( Prov.Numbers.DEA ));
			provFAA.setValue( prov.getProvNumber( Prov.Numbers.FAA ));
			provCLIA.setValue( prov.getCLIA());
			provNPI.setValue( prov.getNPI());

			etsProvID.setValue( prov.getEtsProvID());
			etsSubID.setValue(prov.getEtsSubID());
			
			mcareProv.setValue( prov.getProvNumber( Prov.Numbers.MEDICARE ));
			mcareGroup.setValue( prov.getProvNumber( Prov.Numbers.MEDICARE_GROUP ));
//TODO			mcareEff.setValue( prov.getMedicareEff().getPrintable(9));
//TODO			mcareApp.setValue( prov.getMedicareApp().getPrintable(9));
			EditHelpers.setListboxSelection( mcarePar, prov.getMedicareParProv());
		
			mcaidProv.setValue( prov.getProvNumber( Prov.Numbers.MEDICAID ));
//TODO			mcaidEff.setValue( prov.getMedicaidEff().getPrintable(9));
//TODO			mcaidApp.setValue( prov.getMedicaidApp().getPrintable(9));
			EditHelpers.setListboxSelection( mcaidPar, prov.getMedicaidParProv());

			provBC.setValue( prov.getProvNumber( Prov.Numbers.BLUE_CROSS ));
			provBS.setValue( prov.getProvNumber( Prov.Numbers.BLUE_SHIELD ));
//TODO			bcbsPar;
			EditHelpers.setListboxSelection( bcbsPar, prov.getBCBSParProv());


	
			provDOB.setValue( prov.getBirthdate().getPrintable( 9 ));
			provSSN.setValue( prov.getProvNumber( Prov.Numbers.SSN ));
			//provSSN.setValue( prov.getSSN());

			
			// provier home address
			{
			Address a = prov.getHomeAddress();
			homeStreet.setValue( a.getStreet());
			homeLine2.setValue( a.getLine2());
			homeCity.setValue( a.getCity());
			homeState.setValue( a.getState());
			homeZip.setValue( a.getZip_code());
			homeHome.setValue( a.getHome_ph());
			homeWork.setValue( a.getWork_ph());
			}
			
			// provier office address
			{
			Address a = prov.getOfficeAddress();
			officeStreet.setValue( a.getStreet());
			officeLine2.setValue( a.getLine2());
			officeCity.setValue( a.getCity());
			officeState.setValue( a.getState());
			officeZip.setValue( a.getZip_code());
			officeHome.setValue( a.getHome_ph());
			officeWork.setValue( a.getWork_ph());
			}
			
			// state license numbers
			licState1.setValue( prov.getLicenseState(1));
			licNum1.setValue( prov.getLicenseNum(1));
			licExp1.setValue( prov.getLicenseExpDate(1).getPrintable(9));
			//bnddNum1.setValue( prov.getBNDD(1));
			//bnddExp1.setValue( prov.getBNDDExp(1));
			
			licState2.setValue( prov.getLicenseState(2));
			licNum2.setValue( prov.getLicenseNum(2));
			licExp2.setValue( prov.getLicenseExpDate(2).getPrintable(9));
			//bnddNum2.setValue( prov.getBNDD(2));
			//bnddExp2.setValue( prov.getBNDDExp(2));
			
			licState3.setValue( prov.getLicenseState(3));
			licNum3.setValue( prov.getLicenseNum(3));
			licExp3.setValue( prov.getLicenseExpDate(3).getPrintable(9));
			//bnddNum3.setValue( prov.getBNDD(3));
			//bnddExp3.setValue( prov.getBNDDExp(3));
			
			licState4.setValue( prov.getLicenseState(4));
			licNum4.setValue( prov.getLicenseNum(4));
			licExp4.setValue( prov.getLicenseExpDate(4).getPrintable(9));
			//bnddNum4.setValue( prov.getBNDD(4));
			//bnddExp4.setValue( prov.getBNDDExp(4));
			
			
			
			
			
			//EditHelpers.setListboxSelection( ptSex, dirPt.getSex());
			//EditHelpers.setListboxSelection( ptRace, dirPt.getRace());
			//EditHelpers.setListboxSelection( ptMarital, dirPt.getMarital());
	
	//		EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());
	//		EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());

		
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		Prov	prov;		// proivider info
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new patient
			System.out.println( "Creating new provider..." );
			prov = new Prov();
			
			

			
		} else { 
			
			
			System.out.println( "EditProvWinController().save() provRec=" + provRec.getRec());

			// make sure we have a patient
			if ( provRec.getRec() < 2) provRec = new Rec( 2 );		// default for testing

	
			// Read provider info
			prov = new Prov( provRec );
			
		}
	
		System.out.println( "Here0" );
			
		
		// Load data into Prov		
		// set patient's Name, birthdate, sex
		prov.setAbbr( provAbbr.getValue());
		prov.setName( provName.getValue());
		prov.setFirstName( provFirst.getValue());
		prov.setMiddleName( provMiddle.getValue());
		prov.setLastName( provLast.getValue());

		
		prov.setBirthdate( new Date( provDOB.getValue()));
		prov.setProvNumber( Prov.Numbers.SSN, provSSN.getValue());
		//dirPt.setSSN( ptSSN.getValue());
		
		System.out.println( "Here1" );
		
		
		// Load info into InfoPt
		// patient's address, SSN, race, marital, relations
		prov.setHomeAddress( new Address( homeStreet.getValue(), homeLine2.getValue(), homeCity.getValue(), homeState.getValue(),
				homeZip.getValue(), homeHome.getValue(), homeWork.getValue(), "" ));
		prov.setOfficeAddress( new Address( officeStreet.getValue(), officeLine2.getValue(), officeCity.getValue(), officeState.getValue(),
				officeZip.getValue(), officeHome.getValue(), officeWork.getValue(), "" ));


		System.out.println( "Here2" );
		

		prov.setProvNumber( Prov.Numbers.FED_TAX, fedTaxID.getValue());
		prov.setProvNumber( Prov.Numbers.STATE_TAX, stateTaxID.getValue());
		prov.setProvNumber( Prov.Numbers.LOCAL_TAX, localTaxID.getValue());

		prov.setProvNumber( Prov.Numbers.DEA, provDEA.getValue());
		prov.setProvNumber( Prov.Numbers.FAA, provFAA.getValue());
		prov.setCLIA( provCLIA.getValue());
		prov.setNPI( provNPI.getValue());

		System.out.println( "Here3" );
		
		// state license numbers
		prov.setLicenseState(1, licState1.getValue());
		prov.setLicenseNum(1, licNum1.getValue());
		prov.setLicenseExpDate(1, new Date( licExp1.getValue()));
		//prov.setBNDD(1, bnddNum1.getValue());
		//prov.setBNDDExp(1, bnddExp1.getValue());
		
		prov.setLicenseState(2, licState2.getValue());
		prov.setLicenseNum(2, licNum2.getValue());
		prov.setLicenseExpDate(2, new Date( licExp2.getValue()));
		//prov.setBNDD(2, bnddNum2.getValue());
		//prov.setBNDDExp(2, bnddExp2.getValue());
		
		prov.setLicenseState(3, licState3.getValue());
		prov.setLicenseNum(3, licNum3.getValue());
		prov.setLicenseExpDate(3, new Date( licExp3.getValue()));
		//prov.setBNDD(3, bnddNum3.getValue());
		//prov.setBNDDExp(3, bnddExp3.getValue());
		
		prov.setLicenseState(4, licState4.getValue());
		prov.setLicenseNum(4, licNum4.getValue());
		prov.setLicenseExpDate(4, new Date( licExp4.getValue()));
		//prov.setBNDD(4, bnddNum4.getValue());
		//prov.setBNDDExp(4, bnddExp4.getValue());
		
		System.out.println( "Here4" );
		
		prov.setProvNumber( Prov.Numbers.FED_TAX, fedTaxID.getValue());
		prov.setProvNumber( Prov.Numbers.STATE_TAX, stateTaxID.getValue());
		prov.setProvNumber( Prov.Numbers.LOCAL_TAX, localTaxID.getValue());

		prov.setEtsProvID( etsProvID.getValue());
		prov.setEtsSubID( etsSubID.getValue());
		
		prov.setProvNumber( Prov.Numbers.MEDICARE, mcareProv.getValue());
		prov.setProvNumber( Prov.Numbers.MEDICARE_GROUP, mcareGroup.getValue());
//TODO			prov.setMedicareEff( new Date( mcareEff.getValue()));
//TODO			prov.setMedicareApp( new Date( mcareApp.getValue());
		prov.setMedicareParProv( (String) EditHelpers.getListboxSelection( mcarePar ));
		
		prov.setProvNumber( Prov.Numbers.MEDICAID, mcaidProv.getValue());
//TODO			prov.setMedicaidEff( new Date( mcaidEff.getValue());
//TODO			prov.setMedicaidApp( new Date( mcaidApp.getValue());
		prov.setMedicaidParProv( (String) EditHelpers.getListboxSelection( mcaidPar ));

		prov.setProvNumber( Prov.Numbers.BLUE_CROSS, provBC.getValue());
		prov.setProvNumber( Prov.Numbers.BLUE_SHIELD, provBS.getValue());
		prov.setBCBSParProv( (String) EditHelpers.getListboxSelection( bcbsPar ));

		
		// set validity byte
		prov.setValid();
		
		
		System.out.println( "Here5" );
		

		
		System.out.println( "dump" );
		prov.dump();	
		System.out.println( "Here6" );
		
		// Save (write) records back to database
		if (( operation == EditPt.Operation.NEWPT ) || ( provRec == null ) || ( provRec.getRec() == 0 )){
			
			// new patient
			provRec = prov.newProv();
			System.out.println( "new provider created provrec=" + provRec.getRec());
			
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROV_ADD, null, Pm.getUserRec(), provRec, null );
				
			
		} else {	// EditPt.Operation.EDITPT
			
			// existing patient
			prov.write( provRec );
			System.out.println( "edited provider info written, provrec=" + provRec.getRec());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PROV_EDIT, null, Pm.getUserRec(), provRec, null );
				
		}
		
		
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
		
		// close editPtWin
		provEditWin.detach();

		// close tab and tabpanel
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().detach();
			tabpanel.detach();
		}

		return;
	}
	
	
	public void onChange$provName$pg1( Event e ){ updateProvName(); }
	//public void onChange$ptMiddle$pg1( Event e ){ updatePtName(); }
	//public void onChange$ptLast$pg1( Event e ){ updatePtName(); }
	//public void onChange$ptSuffix$pg1( Event e ){ updatePtName(); }
	
	
	private void updateProvName(){
		
		l_provName.setValue( provName.getValue());
		return;
	}
	


}

/**/

