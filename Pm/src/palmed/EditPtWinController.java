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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listitem;

import palmed.PhyExamWinController.Impressions;

import usrlib.Address;
import usrlib.Date;
import usrlib.Decoders;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Name;
import usrlib.ProgressBox;
import usrlib.Rec;

public class EditPtWinController extends GenericForwardComposer {

	Rec ptRec = null;		// patient record
	Rec ppoRec = null; Rec ppoRec2 = null; Rec ppoRec3 = null; Rec ppoRecO,ppoRec2O,ppoRec3O = null; //Insurance records
	EditPt.Operation operation = null;	// operation to perform

	Tabpanel tabpanel;		// tab panel this window is in


	Window editPtWin;		// autowired - window
	Include pg1;		 	// autowired
	Include pg2;			// autowired
	Include pg3;			// autowired
	Include pg4;			// autowired

	Button save;			// autowired - save button
	Button cancel;			// autowired - cancel button
	Button InsSel;          // autowired - Choose Insurance
	Button InsSel1;
	Button InsSel2;

	Label l_ptName;			// autowired

	Textbox ptFirst;		// autowired
	Textbox ptMiddle;		// autowired
	Textbox ptLast;		// autowired
	Textbox ptSuffix;		// autowired
	Textbox ptDOB;		// autowired
	Textbox ptSSN;		// autowired
	Textbox ptNum1;		// autowired
	Textbox ptNum2;		// autowired
	Textbox ptNum3;		// autowired

	Listbox ptSex;		// autowired
	Listbox ptRace;		// autowired
	Listbox ptMarital;	// autowired
	Listbox ptEthnicity;// autowired
	Listbox ptLanguage;	// autowired

	Textbox ptStreet;		// autowired
	Textbox ptLine2;		// autowired
	Textbox ptCity;		// autowired
	Textbox ptState;		// autowired
	Textbox ptZip;		// autowired
	Textbox ptHome;		// autowired
	Textbox ptWork;		// autowired
	Textbox ptCell;		// autowired
	Textbox ptEmail;		// autowired
	Textbox ptOccupation;		// autowired
	Textbox ptProv;		// autowired
	Textbox ptRdoc;		// autowired
	Textbox ptEmpGrp;		// autowired
	Textbox ptPhyDef;		// autowired
	Textbox ptNH;		// autowired
	Textbox	email;		// autowired

	Rec NokRec;         //autowired
	Textbox nokFirst;   //autowired
	Textbox nokMiddle;  //autowired
	Textbox nokLast;    //autowired
	Textbox nokSuffix;  //autowired

	Textbox nokDOB;    //autowired
	Textbox nokSSN;    //autowired

	Listbox nokSex;   //autowired

	Textbox nokStreet;  //autowired
	Textbox nokZip;     //autowired
	Textbox nokHome;    //autowired
	Textbox nokWork;    //autowired
	Textbox nokCell;    //autowired
	Textbox nokEmail;   //autowired



	Textbox pin;		// autowired
	Checkbox allowPortal;	// autowired
	Checkbox allowEmail;	// autowired


	Textbox insCmp1;       // autowired
	Listbox insMisc1;      // autowired
	Textbox insNum1;       // autowired
	Textbox insGrp1;       // autowired
	Textbox insEffDate1;   //autowired
	Textbox insDeduct1;    // autowired
	Textbox insCopayAmt1;  // autowired
	Textbox insCopayPct1;  // autowired
	Listbox insPRN1;       // autowired    
	Listbox insAA1;       // autowired
	Listbox insSOF1;      // autowired

	Textbox insCmp2;       // autowired
	Listbox insMisc2;      // autowired
	Textbox insNum2;       // autowired
	Textbox insGrp2;       // autowired
	Textbox insEffDate2;   //autowired
	Textbox insDeduct2;    // autowired
	Textbox insCopayAmt2;  // autowired
	Textbox insCopayPct2;  // autowired
	Listbox insPRN2;       // autowired    
	Listbox insAA2;       // autowired
	Listbox insSOF2;      // autowired

	Textbox insCmp3;       // autowired
	Listbox insMisc3;      // autowired
	Textbox insNum3;       // autowired
	Textbox insGrp3;       // autowired
	Textbox insEffDate3;   //autowired
	Textbox insDeduct3;    // autowired
	Textbox insCopayAmt3;  // autowired
	Textbox insCopayPct3;  // autowired
	Listbox insPRN3;       // autowired    
	Listbox insAA3;       // autowired
	Listbox insSOF3;      // autowired

	String Cname, Cname2, Cname3;

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

		Components.wireVariables( pg4, this );
		Components.addForwards( pg4, this );

		Components.wireVariables( pg3, this );
		Components.addForwards( pg3, this );

		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (EditPt.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
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



		// Fill some listboxes
		EditHelpers.loadSexListbox( ptSex );
		loadRaceListbox( ptRace );
		loadLanguageListbox( ptLanguage );
		loadEthnicityListbox( ptEthnicity );









		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// Create a new patient

			// Set title of Tabpanel to "New Patient"
			if ( tabpanel != null ){
				tabpanel.getLinkedTab().setLabel( "New Patient" );
				//tabpanel.getLinkedTab().setImage( "ptchart_verysmall.png" );
			}



		} else {	// operation == EditPt.Operation.EDITPT

			// make sure we have a patient
			if (( ptRec == null ) || ptRec.getRec() < 2 ){
				System.out.println( "Error: EditPtWinController() invalid ptRec, setting to 2" );
				ptRec = new Rec( 2 );		// default for testing
			}
			System.out.println( "EditPtWinController() ptRec=" + ptRec.getRec());


			// Read patient info DIRPT and INFOPT
			DirPt dirPt = new DirPt( ptRec );


			// Set title of Tabpanel to Patient's name
			if ( tabpanel != null ){
				tabpanel.getLinkedTab().setLabel( "Edit: " + dirPt.getName().getPrintableNameLFM());
				//tabpanel.getLinkedTab().setImage( "ptchart_verysmall.png" );
			}


			// Get some patient values and set labels
			//ptrec.setValue( "" + ptRec.getRec());
			Name n = dirPt.getName();
			editPtWin.setTitle( n.getPrintableNameLFM());

			ptFirst.setValue( n.getFirstName());
			ptMiddle.setValue( n.getMiddleName());
			ptLast.setValue( n.getLastName());
			ptSuffix.setValue( n.getSuffix());
			updatePtName();


			ptSSN.setValue( dirPt.getSSN());
			ptNum1.setValue( dirPt.getPtNumber( 1 ));
			ptNum2.setValue( dirPt.getPtNumber( 2 ));
			ptNum3.setValue( dirPt.getPtNumber( 3 ));

			ptDOB.setValue( dirPt.getBirthdate().getPrintable( 9 ));


			// patient address
			{
				Address a = dirPt.getAddress();
				ptStreet.setValue( a.getStreet());
				ptLine2.setValue( a.getLine2());
				ptCity.setValue( a.getCity());
				ptState.setValue( a.getState());
				ptZip.setValue( a.getZip_code());
				ptHome.setValue( a.getHome_ph());
				ptWork.setValue( a.getWork_ph());
			}

			EditHelpers.setListboxSelection( ptSex, dirPt.getSex().getAbbr());
			EditHelpers.setListboxSelection( ptRace, dirPt.getRace());
			EditHelpers.setListboxSelection( ptMarital, dirPt.getMarital());

			EditHelpers.setListboxSelection( ptEthnicity, dirPt.getEthnicity());
			EditHelpers.setListboxSelection( ptLanguage, dirPt.getLanguage());


			if ((dirPt.getPpo(1).getRec() != 0) && (dirPt.getPpo(1) != null) ) {

				//Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(1) );
				Insure insure = new Insure ( dirPt.getPpoinfo(1));

				if ( !(dirPt.getPpo(1).getRec() < 2)){	
					ppoRec = dirPt.getPpo(1);
					ppoRecO = dirPt.getPpo(1);
					Ppo ppo = new Ppo ( ppoRec );
					Cname = ppo.getAbbr(); insMisc1.setSelectedIndex(2); 
				}else{Cname = insure.getCarrierName();  insMisc1.setSelectedIndex(1); 
				//System.out.println("cname is:"+ insure.getCarrierName());
				//System.out.println("ppo: "+ Cname);
				}
				insCmp1.setValue(Cname);       
				insNum1.setValue(insure.getPolicyNumber());       
				insGrp1.setValue(insure.getGroupNumber());       
				insEffDate1.setText(insure.getEffectiveDate().getPrintable(9));   
				insDeduct1.setValue(insure.getDeductible().getPrintable());    
				insCopayAmt1.setValue(insure.getCopayAmt().getPrintable());  
				insCopayPct1.setValue(Integer.toString(insure.getCopayPct()));  
				//System.out.println("R,AA,Sig:"+insure.getRelation()+","+insure.getAssignment()+","+insure.getSignature());

				if (insure.getRelation() == 80){
					insPRN1.setSelectedIndex(1); }
				else{if (insure.getRelation() == 82){
					insPRN1.setSelectedIndex(2); }
				else{if (insure.getRelation() == 78){
					insPRN1.setSelectedIndex(3); }
				else{if (insure.getRelation() == 32){
					insPRN1.setSelectedIndex(0); }
				else{
					insPRN1.setSelectedIndex(0); }}}}

				if (insure.getAssignment() == 32 ){
					insAA1.setSelectedIndex(0);       
				}else{if (insure.getAssignment() == 89 ){
					insAA1.setSelectedIndex(1);
				}else{if (insure.getAssignment() == 78 ){
					insAA1.setSelectedIndex(2);       
				}else{
					insAA1.setSelectedIndex(0);       
				}}}

				if (insure.getSignature() == 32 ){
					insSOF1.setSelectedIndex(0);       
				}else{if (insure.getSignature() == 89 ){
					insSOF1.setSelectedIndex(1);
				}else{if (insure.getSignature() == 78 ){
					insSOF1.setSelectedIndex(2);       
				}else{
					insSOF1.setSelectedIndex(0);       
				}}}


			}

			if ((dirPt.getPpo(2).getRec() != 0) && (dirPt.getPpo(2) != null) ) {

				//Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(2) );
				Insure insure = new Insure ( dirPt.getPpoinfo(2));
				//System.out.println("rec 2 is:"+dirPt.getPpo(2));

				if (!(dirPt.getPpo(2).getRec() < 2)){	
					ppoRec2 = dirPt.getPpo(2);
					ppoRec2O = dirPt.getPpo(2);
					Ppo ppo = new Ppo ( ppoRec2 );
					Cname2 = ppo.getAbbr(); 
					insMisc2.setSelectedIndex(2);  
				}else{Cname2 = insure.getCarrierName(); insMisc2.setSelectedIndex(1);  
				}
				insCmp2.setValue(Cname2);  				
				insNum2.setValue(insure.getPolicyNumber());       
				insGrp2.setValue(insure.getGroupNumber());       
				insEffDate2.setText(insure.getEffectiveDate().getPrintable(9));   
				insDeduct2.setValue(insure.getDeductible().getPrintable());    
				insCopayAmt2.setValue(insure.getCopayAmt().getPrintable());  
				insCopayPct2.setValue(Integer.toString(insure.getCopayPct()));  
				//System.out.println("R,AA,Sig:"+insure.getEffectiveDate().getPrintable()+insure.getRelation()+","+insure.getAssignment()+","+insure.getSignature());

				if (insure.getRelation() == 80){
					insPRN2.setSelectedIndex(1); }
				else{if (insure.getRelation() == 82){
					insPRN2.setSelectedIndex(2); }
				else{if (insure.getRelation() == 78){
					insPRN2.setSelectedIndex(3); }
				else{if (insure.getRelation() == 32){
					insPRN2.setSelectedIndex(0); }
				else{
					insPRN2.setSelectedIndex(0); }}}}

				if (insure.getAssignment() == 32 ){
					insAA2.setSelectedIndex(0);}       
				else{if (insure.getAssignment() == 89 ){
					insAA2.setSelectedIndex(1);}
				else{if (insure.getAssignment() == 78 ){
					insAA2.setSelectedIndex(2);       
				}else{
					insAA2.setSelectedIndex(0);       
				}}}

				if (insure.getSignature() == 32 ){
					insSOF2.setSelectedIndex(0);       
				}else{ if (insure.getSignature() == 89 ){
					insSOF2.setSelectedIndex(1);
				} else{ if (insure.getSignature() == 78 ){
					insSOF2.setSelectedIndex(2);       
				}else{
					insSOF2.setSelectedIndex(0);       
				}}}
			}

			if ((dirPt.getPpo(3).getRec() != 0) && (dirPt.getPpo(3) != null) ) {

				//Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(3) );
				Insure insure = new Insure ( dirPt.getPpoinfo(3));

				if (!(dirPt.getPpo(3).getRec() < 2) ){	
					ppoRec3 = dirPt.getPpo(3);
					ppoRec3O = dirPt.getPpo(3);
					Ppo ppo = new Ppo ( ppoRec3 ); 
					System.out.println("pporec3-1: "+ppoRec3+","+ insure.getCarrierName());
					Cname3 = ppo.getAbbr(); insMisc3.setSelectedIndex(2); 
				}else{Cname3 = insure.getCarrierName(); insMisc3.setSelectedIndex(1); 
				}
				insCmp3.setValue( Cname3 );      

				insNum3.setValue(insure.getPolicyNumber());       
				insGrp3.setValue(insure.getGroupNumber());  
				insEffDate3.setText(insure.getEffectiveDate().getPrintable(9));   
				insDeduct3.setValue(insure.getDeductible().getPrintable());    
				insCopayAmt3.setValue(insure.getCopayAmt().getPrintable());  
				insCopayPct3.setValue(Integer.toString(insure.getCopayPct()));  
				//System.out.println("R,AA,Sig:"+insure.getEffectiveDate().getPrintable()+insure.getRelation()+","+insure.getAssignment()+","+insure.getSignature());

				if (insure.getRelation() == 80){
					insPRN3.setSelectedIndex(1); }
				else{ if (insure.getRelation() == 82){
					insPRN3.setSelectedIndex(2); }
				else{ if (insure.getRelation() == 78){
					insPRN3.setSelectedIndex(3); }
				else{ if (insure.getRelation() == 32){
					insPRN3.setSelectedIndex(0); }
				else{
					insPRN3.setSelectedIndex(0); } }}}

				if (insure.getAssignment() == 32 ){
					insAA3.setSelectedIndex(0);       
				}else{ if (insure.getAssignment() == 89 ){
					insAA3.setSelectedIndex(1);
				}else{ if (insure.getAssignment() == 78 ){
					insAA3.setSelectedIndex(2);       
				}else{
					insAA3.setSelectedIndex(0);       
				}}}

				if (insure.getSignature() == 32 ){
					insSOF3.setSelectedIndex(0);       
				}else{ if (insure.getSignature() == 89 ){
					insSOF3.setSelectedIndex(1);
				}else{ if (insure.getSignature() == 78 ){
					insSOF3.setSelectedIndex(2);       
				}else{
					insSOF3.setSelectedIndex(0);       
				}}}


			}

			//		EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());
			//		EditHelpers.setListboxSelection( ptRelationRP, dirPt.getRelationRP());



			// set patient electronic info
			email.setValue( dirPt.getEmail());
			allowEmail.setChecked( dirPt.getAllowEmail());

			pin.setValue( dirPt.getPIN());
			allowPortal.setChecked( dirPt.getAllowPtPortal());

		}

		InsSel.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub

				ppoRec = new Rec( PpoSrch.show()); 
				if ( ppoRec.getRec() < 2 ){
					return;
				}
				insCmp1.setValue(""); 
				Ppo ppo = new Ppo ( ppoRec );
				insCmp1.setValue(ppo.getAbbr()); 
				insMisc1.setSelectedIndex(2);

			}});

		InsSel1.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub

				ppoRec2 = new Rec( PpoSrch.show()); 
				if ( ppoRec2.getRec() < 2 ){
					return;
				}
				insCmp2.setValue("");
				Ppo ppo = new Ppo ( ppoRec2 );
				insCmp2.setValue(ppo.getAbbr()); 
				insMisc2.setSelectedIndex(2);
			}});

		InsSel2.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub

				ppoRec3 = new Rec( PpoSrch.show()); 
				if ( ppoRec3.getRec() < 2 ){
					return;
				}
				insCmp3.setValue(""); 
				Ppo ppo = new Ppo ( ppoRec3 );
				insCmp3.setValue(ppo.getAbbr());
				insMisc3.setSelectedIndex(2);
			}});


		return;

	}

	/**
	 * Deletes a character from a string .
	 * 
	 * @param str
	 * @param index
	 * @return
	 */	
	private static String deleteachar(String str, int index){
		return str.substring(0,index)+ str.substring(index+1);		
	}


	/**
	 * Save
	 * 
	 */


	public boolean save(){

		DirPt	dirPt;		// patient directory
		MedPt	medPt;		// patient medical record
		Insure  insure1 = null;     // Insurance records
		Insure  insure2 = null;
		Insure  insure3 = null;

		//TODO valid flag
		//TODO follow list
		//TODO NOK
		//TODO RP
		//TODO create ledger
		//TODO HippaRecordCreate
		//TODO mdx
		//TODO SSN index
		//TODO dly_newpt

		if (( ptFirst.getValue().trim().length() < 1 )
				|| (( ptFirst.getValue().trim().length() < 2 ) && ( ptMiddle.getValue().trim().length() < 1 ))
				|| ( ptLast.getValue().trim().length() < 2 )){
			DialogHelpers.Messagebox( "You must enter a complete name." );
			return false;
		}

		Name ptName = new Name( ptFirst.getValue(), ptMiddle.getValue(), ptLast.getValue(), ptSuffix.getValue());

		Date bdate = new Date( ptDOB.getValue());

		if ( ! bdate.isValid()){			
			DialogHelpers.Messagebox( "You must enter a birthdate." );
			return false;
		}




		// verify unique patient
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			PtFinder ptfnd = new PtFinder();
			if ( ptfnd.doSearchName( ptName, bdate ) > 0 ){
				DialogHelpers.Messagebox( "Patient exists.  You must enter a unique patient name/birthdate." );
				return false;

			}
		}


		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new patient

			dirPt = new DirPt();
			medPt = new MedPt();
			insure1 = new Insure();
			if((insCmp2.getValue().length()> 0) && (insNum2.getValue().length()>0) ){ insure2= new Insure();}
			if((insCmp3.getValue().length()> 0) && (insNum3.getValue().length()>0) ){ insure3= new Insure();}

			// create/allocate a new MedPt record
			medPt = new MedPt();
			dirPt.setMedRec( medPt.createNewRecord());
			dirPt.setPpoinfo(insure1.writeNew(), 1);
			if((insCmp2.getValue().length()> 0) && (insNum2.getValue().length()>0) ){dirPt.setPpoinfo(insure2.writeNew(), 2);}
			if((insCmp3.getValue().length()> 0) && (insNum3.getValue().length()>0) ){dirPt.setPpoinfo(insure3.writeNew(), 3);}


		} else { 



			// make sure we have a patient
			if ( ptRec.getRec() < 2){
				System.out.println( "Error: EditPtWinController().save() ptRec=" + ptRec.getRec());
				ptRec = new Rec( 2 );		// default for testing
			}

			// Read patient info DIRPT and INFOPT
			dirPt = new DirPt( ptRec );
			medPt = new MedPt( dirPt.getMedRec());

			if ((dirPt.getPpoinfo(1).getRec() != 0) && (dirPt.getPpoinfo(1) != null) ) {	
				insure1 = new Insure ( dirPt.getPpoinfo(1));  

			}else{if((insCmp1.getValue().length()> 0) && (insNum1.getValue().length()>0) ){ insure1= new Insure();
			dirPt.setPpoinfo(insure1.writeNew(), 1);
			}}

			if ((dirPt.getPpoinfo(2).getRec() != 0) && (dirPt.getPpoinfo(2) != null) ) {
				insure2 = new Insure ( dirPt.getPpoinfo(2));

			}else{if((insCmp2.getValue().length()> 0) && (insNum2.getValue().length()>0) ){ insure2= new Insure();
			dirPt.setPpoinfo(insure2.writeNew(), 2);
			}}



			if ((dirPt.getPpoinfo(3).getRec() != 0) && (dirPt.getPpoinfo(3) != null) ) {
				insure3 = new Insure ( dirPt.getPpoinfo(3));  

			}
			else{if((insCmp3.getValue().length()> 0) && (insNum3.getValue().length()>0) ){ insure3= new Insure();
			dirPt.setPpoinfo(insure3.writeNew(), 3);
			}}


		}


		// Load data into DirPt		
		// set patient's Name, birthdate, sex
		dirPt.setName( new Name( ptFirst.getValue(), ptMiddle.getValue(), ptLast.getValue(), ptSuffix.getValue()));

		String strSex = (String) EditHelpers.getListboxSelection( ptSex );
		if ( strSex == null ) strSex = "";
		dirPt.setSex( palmed.Sex.get( strSex ));

		dirPt.setBirthdate( new Date( ptDOB.getValue()));

		dirPt.setPtNumber( ptNum1.getValue(), 1 );
		dirPt.setPtNumber( ptNum2.getValue(), 2 );
		dirPt.setPtNumber( ptNum3.getValue(), 3 );




		// Load info into InfoPt
		// patient's address, SSN, race, marital, relations
		dirPt.setAddress( new Address( ptStreet.getValue(), ptLine2.getValue(), ptCity.getValue(), ptState.getValue(),
				ptZip.getValue(), ptHome.getValue(), ptWork.getValue(), "" ));
		dirPt.setSSN( ptSSN.getValue());

		String marital = (String) EditHelpers.getListboxSelection( ptMarital );
		if ( marital == null ) marital = "";
		dirPt.setMarital( marital );

		// race
		palmed.Race r = (palmed.Race) EditHelpers.getListboxSelection( ptRace );
		if ( r == null ) r = palmed.Race.UNSPECIFIED;
		dirPt.setRace( r );

		// ethnicity
		palmed.Ethnicity e = (palmed.Ethnicity ) EditHelpers.getListboxSelection( ptEthnicity );
		if ( e == null ) e = palmed.Ethnicity.UNSPECIFIED;
		dirPt.setEthnicity( e );

		// language
		palmed.Language l = (palmed.Language ) EditHelpers.getListboxSelection( ptLanguage );
		if ( l == null ) l = palmed.Language.UNSPECIFIED;
		dirPt.setLanguage( l );


		// set patient electronic info
		dirPt.setEmail( email.getValue().trim());
		dirPt.setAllowEmail( allowEmail.isChecked());

		dirPt.setPIN( pin.getValue().trim());
		dirPt.setAllowPtPortal( allowPortal.isChecked());


		//Set Insurance info 
		Boolean nulll = false;

		if ( ppoRecO == null && ppoRec == null && insCmp1.getValue()== Cname ){

			nulll = true;
			//System.out.println("true");
		}
		if (  !nulll ){
			Object rec, recO = null;
			if ( ppoRec == null) { rec = ppoRec; }else { rec = ppoRec.getRec();}
			if ( ppoRecO == null) { recO = ppoRecO; }else { recO = ppoRecO.getRec();}

			if ( rec == recO ){
				//System.out.println("here");
				if (  insCmp1.getValue() != Cname ){
					insure1.setCarrierName(insCmp1.getValue());
					Rec rec1 = new Rec (1);
					insure1.setPpoRec(rec1);
					dirPt.setPpo(rec1, 1);
				}}else{
					System.out.println("pporec is: "+ ppoRec);	
					insure1.setPpoRec(ppoRec);
					dirPt.setPpo(ppoRec, 1);
					insure1.setCarrierName("");}
			insure1.setPolicyNumber(insNum1.getValue());
			insure1.setGroupNumber(insGrp1.getValue());
			Date Effdate = new Date(insEffDate1.getValue());
			System.out.println(Effdate.getPrintable());
			insure1.setEffectiveDate(Effdate);
			insure1.setDeductible(insDeduct1.getValue());
			insure1.setCopayAmt(insCopayAmt1.getValue());
			insure1.setCopayPct(Integer.parseInt(insCopayPct1.getValue()));
			insure1.setRelation(Integer.parseInt(insPRN1.getSelectedItem().getValue().toString()));
			insure1.setAssignment(Integer.parseInt(insAA1.getSelectedItem().getValue().toString()));
			insure1.setSignature(Integer.parseInt(insSOF1.getSelectedItem().getValue().toString()));

		}

		Boolean nulll2 = false;

		if ( ppoRec2O == null && ppoRec2 == null && insCmp2.getValue()== Cname2 ){

			nulll2 = true;
			System.out.println("true");
		}

		if (  insure2!= null && !nulll2 ){
			System.out.println("pporec2: "+ ppoRec2+","+ppoRec2O);
			System.out.println("cname,value: "+ Cname2+","+insCmp2.getValue());
			Object rec2, rec2O = null;
			if ( ppoRec2 == null) { rec2 = ppoRec2; }else { rec2 = ppoRec2.getRec();}
			if ( ppoRec2O == null) { rec2O = ppoRec2O; }else { rec2O = ppoRec2O.getRec();}

			if ( rec2 == rec2O ){
				System.out.println("here");
				if (  insCmp2.getValue() != Cname2 ){			
					System.out.println("if");
					insure2.setCarrierName(insCmp2.getValue());
					Rec rec22 = new Rec (1);
					insure2.setPpoRec(rec22);
					dirPt.setPpo(rec22, 2);
				}}else{
					System.out.println("else");	
					insure2.setPpoRec(ppoRec2);
					dirPt.setPpo(ppoRec2, 2);
					insure2.setCarrierName("");}
			System.out.println("pporec final: "+ppoRec2);
			insure2.setPolicyNumber(insNum2.getValue());
			insure2.setGroupNumber(insGrp2.getValue());
			Date Effdate2 = new Date(insEffDate2.getValue());
			//System.out.println(Effdate2.getPrintable());
			insure2.setEffectiveDate(Effdate2);
			insure2.setDeductible(insDeduct2.getValue());
			insure2.setCopayAmt(insCopayAmt2.getValue());
			insure2.setCopayPct(Integer.parseInt(insCopayPct2.getValue()));
			insure2.setRelation(Integer.parseInt(insPRN2.getSelectedItem().getValue().toString()));
			insure2.setAssignment(Integer.parseInt(insAA2.getSelectedItem().getValue().toString()));
			insure2.setSignature(Integer.parseInt(insSOF2.getSelectedItem().getValue().toString()));


		}

		Boolean nulll3 = false;

		if ( ppoRec3O == null && ppoRec3 == null && insCmp3.getValue()== Cname3 ){

			nulll3 = true;
			//System.out.println("true");
		}

		if (  insure3 != null && !nulll3 ){

			Object rec3, rec3O = null;
			if ( ppoRec3 == null) { rec3 = ppoRec3; }else { rec3 = ppoRec3.getRec();}
			if ( ppoRec3O == null) { rec3O = ppoRec3O; }else { rec3O = ppoRec3O.getRec();}

			if ( rec3 == rec3O ){
				//System.out.println("here");
				if (  insCmp3.getValue() != Cname3 ){
					insure3.setCarrierName(insCmp3.getValue());
					Rec rec33 = new Rec (1);
					insure3.setPpoRec(rec33);
					dirPt.setPpo(rec33, 3);
				}}else{
					//System.out.println("pporec3:"+ppoRec3);	
					insure3.setPpoRec(ppoRec3);
					dirPt.setPpo(ppoRec3, 3);
					insure3.setCarrierName("");}
			insure3.setPolicyNumber(insNum3.getValue());
			insure3.setGroupNumber(insGrp3.getValue());
			Date Effdate3 = new Date(insEffDate3.getValue());
			//System.out.println(Effdate3.getPrintable());
			insure3.setEffectiveDate(Effdate3);
			insure3.setDeductible(insDeduct3.getValue());
			insure3.setCopayAmt(insCopayAmt3.getValue());
			insure3.setCopayPct(Integer.parseInt(insCopayPct3.getValue()));
			insure3.setRelation(Integer.parseInt(insPRN3.getSelectedItem().getValue().toString()));
			insure3.setAssignment(Integer.parseInt(insAA3.getSelectedItem().getValue().toString()));
			insure3.setSignature(Integer.parseInt(insSOF3.getSelectedItem().getValue().toString()));


		}

		// set validity byte
		dirPt.setValid();
		insure1.setValid();
		if (  insure2!= null){
			insure2.setValid();}
		if (  insure3!= null){
			insure3.setValid();}




		System.out.println( "dump" );
		dirPt.dump(); 
		insure1.dump();
		if ( insure2!= null){
			insure2.dump();}
		if ( insure3!= null){
			insure3.dump();}


		// Save (write) records back to database
		if (( operation == EditPt.Operation.NEWPT ) || ( ptRec == null ) || ( ptRec.getRec() == 0 )){

			// new patient
			ptRec = dirPt.newPt();
			medPt.write( dirPt.getMedRec());
			System.out.println( "new patient created ptrec=" + ptRec.getRec());
			insure1.write(dirPt.getPpoinfo(1));
			if ( insure2!= null){
				insure2.write(dirPt.getPpoinfo(2));}
			if (  insure3!= null){
				insure3.write(dirPt.getPpoinfo(3));}
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PT_NEW, ptRec, Pm.getUserRec(), null, null );

		} else {	// EditPt.Operation.EDITPT

			// existing patient
			dirPt.writePt( ptRec );
			medPt.write( dirPt.getMedRec());
			System.out.println( "edited patient info written, ptrec=" + ptRec.getRec());
			insure1.write(dirPt.getPpoinfo(1));
			if (  insure2!= null){
				insure2.write(dirPt.getPpoinfo(2));}
			if (  insure3!= null){
				insure3.write(dirPt.getPpoinfo(3));}

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.PT_EDIT, ptRec, Pm.getUserRec(), null, null );
		}


		return true;
	}



	public void onClose$editPtWin( Event e ) throws InterruptedException{
		alert( "onCloset event");
		onClick$cancel( e );
		return;
	}



	public void onClick$save( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no

		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){

			//ProgressBox progBox = ProgressBox.show(editPtWin, "Saving..." );

			if ( save()){
				//progBox.close();
				closeWin();
			}

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
		editPtWin.detach();

		// close tab and tabpanel
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().detach();
			tabpanel.detach();
		}

		return;
	}


	public void onChange$ptFirst$pg1( Event e ){ updatePtName(); }
	public void onChange$ptMiddle$pg1( Event e ){ updatePtName(); }
	public void onChange$ptLast$pg1( Event e ){ updatePtName(); }
	public void onChange$ptSuffix$pg1( Event e ){ updatePtName(); }


	private void updatePtName(){

		l_ptName.setValue( ptFirst.getValue() + " " + ptMiddle.getValue() + " " + ptLast.getValue() + " " + ptSuffix.getValue());
		return;
	}


	public static void loadRaceListbox( Listbox lb ){

		Listitem i;

		i = lb.appendItem( "", null);
		i.setValue( palmed.Race.UNSPECIFIED );

		for (  Race r : EnumSet.allOf(Race.class)){
			i = lb.appendItem( r.getLabel(), null);
			i.setValue( r );
		}		

		return;
	}

	public static void loadLanguageListbox( Listbox lb ){

		Listitem i;

		i = lb.appendItem( "", null);
		i.setValue( palmed.Language.UNSPECIFIED );

		for (  Language l : EnumSet.allOf(Language.class)){
			i = lb.appendItem( l.getLabel(), null);
			i.setValue( l );
		}		

		return;
	}

	public static void loadEthnicityListbox( Listbox lb ){

		Listitem i;

		i = lb.appendItem( "", null);
		i.setValue( palmed.Ethnicity.UNSPECIFIED );

		for (  Ethnicity e : EnumSet.allOf(Ethnicity.class)){
			i = lb.appendItem( e.getLabel(), null);
			i.setValue( e );
		}		

		return;
	}



}

/**/
