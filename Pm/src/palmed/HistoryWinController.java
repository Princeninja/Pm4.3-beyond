package palmed;

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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.Drugs;
import palmed.HxObGyn.ContraceptionMethod;
import palmed.HxObGyn.MenstrualFlow;
import palmed.Smoking;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.ProgressBox;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;





public class HistoryWinController extends GenericForwardComposer {

	Rec ptRec = null;		// patient record
	EditPt.Operation operation = null;	// operation to perform
	Sex ptSex = null;		// patient's sex
	
	Tabpanel tabpanel;		// tab panel this window is in
	

	Window historyWin;		// autowired - window
	
	Include pg1;			// autowired
	Include pg2;			// autowired
	Include pg3;			// autowired
	Include pg4;			// autowired
	
	Button btnSave;			// autowired - save button
	Button btnCancel;		// autowired - cancel button
	
	Label l_ptName;			// autowired

	
	
	
	
	
	Tab t_procedures;				// autowired
	Tabpanel tp_procedures;			// autowired
	Window procWin = null;
	
	
	
	
	/* 
	 * OB History Page 
	 */
	
	Tab t_ob;						// autowired
	Tabpanel tp_ob;					// autowired
	Include pgOb;					// autowired
	boolean flgObGynChanged = false;
	Checkbox cbObHxUnavailable;		// autowired
	Checkbox cbEverPregnant;		// autowired
	Textbox txtGravida;				// autowired
	Textbox txtPara;				// autowired
	Textbox txtAborta;				// autowired
	Checkbox cbObComplications;		// autowired
	Textbox txtObNotes;				// autowired
	Row rowGravida;					// autowired
	Row rowPara;					// autowired
	Row rowAborta;					// autowired
	Row rowComplications;			// autowired
	Row rowObNotes;					// autowired
	

	
	
	
	/* 
	 * Gyn History Page 
	 */
	
	Tab t_gyn;						// autowired
	Tabpanel tp_gyn;				// autowired
	Include pgGyn;					// autowired
	Checkbox cbMenstrualHxUnavailable;// autowired
	Radio rbPreMenarchal;			// autowired
	Radio rbMenstruating;			// autowired
	Radio rbPostMenopause;			// autowired
	Radio rbMenstrualUnknown;		// autowired
	Textbox txtAgeMenarche;			// autowired
	Textbox txtAgeMenopause;		// autowired
	Listbox lbFlow;					// autowired
	Checkbox cbMenstrualCramps;		// autowired
	Checkbox cbPMSSymptoms;			// autowired
	Textbox txtMenstrualNotes;		// autowired
	Row rowMenstrualStatus;			// autowired
	Row rowMenarche;				// autowired
	Row rowMenopause;				// autowired
	Row rowFlow;					// autowired
	Row rowCramps;					// autowired
	Row rowPMS;						// autowired
	Row rowMenstrualNotes;			// autowired

	
	
	Checkbox cbSexualHxUnavailable;	// autowired
	Radio rbSexuallyActive;			// autowired
	Radio rbNotSexuallyActive;		// autowired
	Radio rbSexuallyUnknown;		// autowired
	Listbox lbContraception;		// autowired
	Checkbox cbDyspareunia;			// autowired
	Checkbox cbSameSexEncounters;	// autowired
	Textbox txtSexualNotes;			// autowired
	Row rowSexualStatus;			// autowired
	Row rowContraception;			// autowired
	Row rowDyspareunia;				// autowired
	Row rowSameSexEncounters;		// autowired
	Row rowSexualNotes;				// autowired


	
	
	
	/*
	 * Smoking / Tobacco Use Page
	 */
	Listbox lboxSmokingStatus;		// autowired
	Textbox txtSmokingAgeStarted;	// autowired
	Listbox lboxSmokingPacksDay;	// autowired
	Textbox txtSmokingQuitDate;		// autowired
	Checkbox cbAttemptingToQuit;	// autowired
	Checkbox cbMultipleAttemptsToQuit;	// autowired
	Textbox txtNotes;				// autowired
	Label lblRecode;				// autowired
	Grid divSmokingDetails;			// autowired
	Row rowSmokingQuitDate;			// autowired
	
	
	Listbox lboxOtherProduct;		// autowired
	Listbox lboxOtherStatus;		// autowired
	Textbox txtOtherAgeStarted;		// autowired
	Textbox txtOtherQuitDate;		// autowired
	Textbox txtNotes2;				// autowired
	
	Checkbox cbOtherAttemptingToQuit;	// autowired
	Div divTobaccoDetails;			// autowired
	Row rowTobaccoQuitDate;			// autowired
	boolean flgTobaccoChanged = false;
	
	
	
	
	/*
	 * Drinking (EtOH) / Drug use page
	 */
	
	Include pgEtOH;					// autowired
	Listbox lboxDrinkingStatus;		// autowired
	Textbox txtDrinkingAgeStarted;	// autowired
	Textbox txtDrinkingQuitDate;	// autowired
	Textbox txtDrinkingNotes;		// autowired
	Row rowDrinkingAgeStarted;		// autowired
	Row rowDrinkingQuitDate;		// autowired
	
	boolean flgDrinkingChanged = false;
	
	Include pgDrugs;				// autowired
	Div divCurrentDrugs;			// autowired
	Div divPastDrugs;				// autowired
	Listbox lboxDrugsStatus;		// autowired
	Textbox txtDrugsAgeStarted;		// autowired
	Textbox txtDrugsQuitDate;		// autowired
	Textbox txtDrugsNotes;			// autowired
	boolean flgDrugsChanged = false;

	
	
	
	
	
	/* 
	 * Family History Page 
	 */
	
	Tab t_family;					// autowired
	Tabpanel tp_family;				// autowired
	Include pgHxFamily;				// autowired
	Window familyWin = null;
	boolean flgHxFamilyChanged = false;
	

	Radio rbFatherAlive;			// autowired
	Radio rbFatherDeceased;			// autowired
	Radio rbFatherUnknown;			// autowired
	Textbox txtFatherYearBorn;		// autowired
	Textbox txtFatherYearDeceased;	// autowired
	Textbox txtFatherCauseOfDeath;	// autowired
	Checkbox cbPaternalHxUnavailable;// autowired
	
	Radio rbMotherAlive;			// autowired
	Radio rbMotherDeceased;			// autowired
	Radio rbMotherUnknown;			// autowired
	Textbox txtMotherYearBorn;		// autowired
	Textbox txtMotherYearDeceased;	// autowired
	Textbox txtMotherCauseOfDeath;	// autowired
	Checkbox cbMaternalHxUnavailable;// autowired
	
	Textbox txtNumBrothers;			// autowired
	Textbox txtNumSisters;			// autowired
	Textbox txtNumSons;				// autowired
	Textbox txtNumDaughters;		// autowired
	Textbox txtKidsAgeYoungest;		// autowired
	Textbox txtKidsAgeOldest;		// autowired
	Row rowKidsAgeYoungest;			// autowired
	Row rowKidsAgeOldest;			// autowired

	
	
	
	Tab t_family2;					// autowired
	Tabpanel tp_family2;			// autowired
	Window family2Win = null;
	

	/* 
	 * Blood History Page 
	 */
	
	Tab t_blood;					// autowired
	Tabpanel tp_blood;				// autowired
	Include pgBlood;				// autowired
	boolean flgHxMiscChanged = false;
	Listbox lbBloodType;			// autowired
	Listbox lbRhesusFactor;			// autowired
	Checkbox cbBloodTypeKnown;		// autowired
	Checkbox cbReaction;			// autowired
	Checkbox cbDonate;				// autowired
	Checkbox cbDeclinesBlood;		// autowired
	Textbox txtBloodNotes;			// autowired
	Row rowBloodType;				// autowired
	

	
	
	

	
	
	
	public void doAfterCompose( Component component ){
	
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// autowire included file variables and event handlers
		//Components.wireVariables( pg1, this );
		//Components.addForwards( pg1, this );

		Components.wireVariables( pg4, this );
		Components.addForwards( pg4, this );

		Components.wireVariables( pgDrugs, this );
		Components.addForwards( pgDrugs, this );

		Components.wireVariables( pgEtOH, this );
		Components.addForwards( pgEtOH, this );

		Components.wireVariables( pgHxFamily, this );
		Components.addForwards( pgHxFamily, this );

		Components.wireVariables( pgOb, this );
		Components.addForwards( pgOb, this );

		Components.wireVariables( pgGyn, this );
		Components.addForwards( pgGyn, this );

		Components.wireVariables( pgBlood, this );
		Components.addForwards( pgBlood, this );

	
		
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
				public void onEvent( Event ev ) throws Exception { ev.stopPropagation(); onClick$btnCancel( ev ); }});
		}
		
		
		
		// Fill some listboxes
		ZkTools.appendToListbox( lbContraception, "", HxObGyn.ContraceptionMethod.UNSPECIFIED );
		for ( HxObGyn.ContraceptionMethod r : EnumSet.allOf(HxObGyn.ContraceptionMethod.class))
			ZkTools.appendToListbox( lbContraception, r.getLabel(), r );

		ZkTools.appendToListbox( lbFlow, "", HxObGyn.MenstrualFlow.UNSPECIFIED );
		for ( HxObGyn.MenstrualFlow r : EnumSet.allOf(HxObGyn.MenstrualFlow.class))
			ZkTools.appendToListbox( lbFlow, r.getLabel(), r );

		ZkTools.appendToListbox( lboxSmokingStatus, "", null );
		for ( Smoking.Recode r : EnumSet.allOf(Smoking.Recode.class))
			ZkTools.appendToListbox( lboxSmokingStatus, r.getLabel(), r );

		ZkTools.appendToListbox( lboxSmokingPacksDay, "", null );
		for ( Smoking.PacksDay r : EnumSet.allOf(Smoking.PacksDay.class))
			ZkTools.appendToListbox( lboxSmokingPacksDay, r.getLabel(), r );

		ZkTools.appendToListbox( lboxOtherProduct, "", null );
		for ( Smoking.Product r : EnumSet.allOf(Smoking.Product.class))
			ZkTools.appendToListbox( lboxOtherProduct, r.getLabel(), r );
		
		ZkTools.appendToListbox( lboxOtherStatus, "", null );
		for ( Smoking.UserStatus r : EnumSet.allOf(Smoking.UserStatus.class))
			ZkTools.appendToListbox( lboxOtherStatus, r.getLabel(), r );
		
		
		
		
		ZkTools.appendToListbox( lboxDrinkingStatus, "", null );
		for ( Drugs.DrinkingStatus r : EnumSet.allOf(Drugs.DrinkingStatus.class))
			ZkTools.appendToListbox( lboxDrinkingStatus, r.getLabel(), r );
		
		ZkTools.appendToListbox( lboxDrugsStatus, "", null );
		for ( Drugs.UserStatus r : EnumSet.allOf(Drugs.UserStatus.class))
			ZkTools.appendToListbox( lboxDrugsStatus, r.getLabel(), r );
		
		ZkTools.appendToListbox( lbBloodType, "", HxMisc.RhesusFactor.UNSPECIFIED );
		for ( HxMisc.BloodType r : EnumSet.allOf(HxMisc.BloodType.class))
			if (( r != HxMisc.BloodType.UNKNOWN ) && ( r != HxMisc.BloodType.UNSPECIFIED ))
				ZkTools.appendToListbox( lbBloodType, r.getLabel(), r );
		
		ZkTools.appendToListbox( lbRhesusFactor, "", HxMisc.RhesusFactor.UNSPECIFIED );
		for ( HxMisc.RhesusFactor r : EnumSet.allOf(HxMisc.RhesusFactor.class))
			if (( r != HxMisc.RhesusFactor.UNKNOWN ) && ( r != HxMisc.RhesusFactor.UNSPECIFIED ))
				ZkTools.appendToListbox( lbRhesusFactor, r.getLabel(), r );
		
		
		
		
		
		
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );
		
		// set class var patient sex
		ptSex = new DirPt( ptRec ).getSex();
		
		
		
		// if pt not female, hide Ob & Gyn history
		if ( ptSex != Sex.FEMALE ){
			t_ob.setVisible( false );
			t_gyn.setVisible( false );
		}

		
		// refresh history pages
		if ( ptSex == Sex.FEMALE ) refreshObGyn();
		refreshHxFamily();
		refreshpg4();
		refreshHxMisc();
		
		onClick$t_procedures( null );
		
		return;
	}
		
		
		
		
	public void refreshpg4(){
	
	
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());

		Reca smokingReca =  medPt.getSmokingReca();
		
		if ( Reca.isValid( smokingReca )){
			
			Smoking smoking = new Smoking( smokingReca );
			//System.out.println( "HistoryWinController()  reading smokingReca=" + smokingReca.toString());

			Smoking.Recode recode = smoking.getSmokingStatus();
			if ( recode != null ){
				ZkTools.setListboxSelectionByValue( lboxSmokingStatus, recode );
				lblRecode.setValue( "Recode=" + recode.getCode());
			}
			
			int age = smoking.getSmokingAgeStarted();
			txtSmokingAgeStarted.setValue(( age > 0 ) ? String.valueOf( age ): "" );
			
			ZkTools.setListboxSelectionByValue( lboxSmokingPacksDay, smoking.getSmokingPacksDay());
			
			Date quitDate = smoking.getSmokingQuitDate();
			txtSmokingQuitDate.setValue( quitDate.getPrintable(9));
			
			cbAttemptingToQuit.setChecked( smoking.getFlgAttemptingToQuit());
			cbMultipleAttemptsToQuit.setChecked( smoking.getFlgMultipleAttempts());
			
			txtNotes.setValue( smoking.getSmokingNoteText());

			ZkTools.setListboxSelectionByValue( lboxOtherProduct, smoking.getOtherProduct());
			ZkTools.setListboxSelectionByValue( lboxOtherStatus, smoking.getOtherStatus());
			
			int otherAge = smoking.getOtherAgeStarted();
			txtOtherAgeStarted.setValue(( otherAge > 0 ) ? String.valueOf( otherAge ): "" );
			
			Date otherDate = smoking.getOtherQuitDate();
			txtOtherQuitDate.setValue( otherDate.getPrintable(9) );
			txtNotes2.setValue( smoking.getOtherNoteText());
		}

		
	
		
		Reca drugsReca =  medPt.getDrugsReca();
		
		if ( Reca.isValid( drugsReca )){
			
			Drugs drugs = new Drugs( drugsReca );

			Drugs.DrinkingStatus status = drugs.getDrinkingStatus();
			if ( status != null ){
				ZkTools.setListboxSelectionByValue( lboxDrinkingStatus, status );
			}
			
			int age = drugs.getDrinkingAgeStarted();
			txtDrinkingAgeStarted.setValue(( age > 0 ) ? String.valueOf( age ): "" );
						
			Date quitDate = drugs.getDrinkingQuitDate();
			txtDrinkingQuitDate.setValue( quitDate.getPrintable(9) );
						
			txtDrinkingNotes.setValue( drugs.getDrinkingNoteText());

			
			
			Drugs.UserStatus dstatus = drugs.getDrugsStatus();
			if ( dstatus != null ){
				ZkTools.setListboxSelectionByValue( lboxDrugsStatus, dstatus );
			}
			
			
			// read current drugs
			if (( dstatus == Drugs.UserStatus.CURRENT ) || ( dstatus == Drugs.UserStatus.CURRENT_HEAVY ) 
					|| ( dstatus == Drugs.UserStatus.CURRENT_OCCASIONAL )){
				
				String str = drugs.getCurrentDrugsList();
				
				Drugs.DrugsOfAbuse doa;
				for ( int i = 0, n = str.length(); i < n; ++i ){
					doa = Drugs.DrugsOfAbuse.get( str.charAt( i ));
					if ( doa != null && ( doa != Drugs.DrugsOfAbuse.EMPTY )){
						((Checkbox)(pgDrugs.getFellow( "cbCur" + doa.getCode()))).setChecked( true );
					}
				}
			}
			
			// read past drugs 
			if (( dstatus == Drugs.UserStatus.CURRENT ) || ( dstatus == Drugs.UserStatus.CURRENT_HEAVY ) 
					|| ( dstatus == Drugs.UserStatus.CURRENT_OCCASIONAL ) 
					|| ( dstatus == Drugs.UserStatus.FORMER ) || ( dstatus == Drugs.UserStatus.FORMER_HEAVY )){
				
				String str = drugs.getPastDrugsList();
				
				Drugs.DrugsOfAbuse doa;
				for ( int i = 0, n = str.length(); i < n; ++i ){
					doa = Drugs.DrugsOfAbuse.get( str.charAt( i ));
					if ( doa != null && ( doa != Drugs.DrugsOfAbuse.EMPTY )){
						((Checkbox)(pgDrugs.getFellow( "cbPast" + doa.getCode()))).setChecked( true );
					}
				}
			}
			
			txtDrugsNotes.setValue( drugs.getDrugsNoteText());
		}

		
	
		
		flgTobaccoChanged = false;
		flgDrinkingChanged = false;
		flgDrugsChanged = false;
		
		doSelectDrugsStatus();
		doSelectDrinkingStatus();
		
		btnSave.setDisabled( true );
		return;
	}
	
	
	
	public void refreshHxFamily(){
		
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca hxFamilyReca =  medPt.getHxFamilyReca();
		
		if ( Reca.isValid( hxFamilyReca )){
			
			HxFamily hxfam = new HxFamily( hxFamilyReca );
			
			int num;
			
			
			HxFamily.ParentStatus status = hxfam.getFatherStatus();
			switch ( status ){
			
			case LIVING:
				rbFatherAlive.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtFatherYearBorn, hxfam.getYearFatherBorn());
				txtFatherYearDeceased.setValue( "" );
				txtFatherCauseOfDeath.setValue( "" );
				break;
			
			case DECEASED:
				rbFatherDeceased.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtFatherYearBorn, hxfam.getYearFatherBorn());
				ZkTools.setTextboxIntIfNotZero( txtFatherYearDeceased, hxfam.getYearFatherDeceased());
				txtFatherCauseOfDeath.setValue( hxfam.getFatherCauseText());
				break;
			
			case UNKNOWN:
			default:
				rbFatherUnknown.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtFatherYearBorn, hxfam.getYearFatherBorn());
				txtFatherYearDeceased.setValue( "" );
				txtFatherCauseOfDeath.setValue( "" );
				break;
			}
			

			switch ( hxfam.getMotherStatus() ){
			
			case LIVING:
				rbMotherAlive.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtMotherYearBorn, hxfam.getYearMotherBorn());
				txtMotherYearDeceased.setValue( "" );
				txtMotherCauseOfDeath.setValue( "" );
				break;
			
			case DECEASED:
				rbMotherDeceased.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtMotherYearBorn, hxfam.getYearMotherBorn());
				ZkTools.setTextboxIntIfNotZero( txtMotherYearDeceased, hxfam.getYearMotherDeceased());
				txtMotherCauseOfDeath.setValue( hxfam.getMotherCauseText());
				break;
			
			case UNKNOWN:
			default:
				rbMotherUnknown.setChecked( true );
				ZkTools.setTextboxIntIfNotZero( txtMotherYearBorn, hxfam.getYearMotherBorn());
				txtMotherYearDeceased.setValue( "" );
				txtMotherCauseOfDeath.setValue( "" );
				break;
			}
			
			
			cbPaternalHxUnavailable.setChecked( hxfam.getFlgPaternalHxUnavailable());
			cbMaternalHxUnavailable.setChecked( hxfam.getFlgMaternalHxUnavailable());
			
			
			ZkTools.setTextboxIntIfNotZero( txtNumBrothers, hxfam.getNumBrothers());
			ZkTools.setTextboxIntIfNotZero( txtNumSisters, hxfam.getNumSisters());
			ZkTools.setTextboxIntIfNotZero( txtNumSons, hxfam.getNumSons());
			ZkTools.setTextboxIntIfNotZero( txtNumDaughters, hxfam.getNumDaughters());

			int earlyYear = hxfam.getKidsOldestBirthYear();
			int lateYear = hxfam.getKidsYoungestBirthYear();
			
			if ( earlyYear > 0 ){
				int ageOldest = Date.today().getYear() - earlyYear;
				ZkTools.setTextboxIntIfNotZero( txtKidsAgeOldest, ageOldest);
			}
			
			if ( lateYear > 0 ){
				int ageYoungest = Date.today().getYear() - lateYear;
				ZkTools.setTextboxIntIfNotZero( txtKidsAgeYoungest, ageYoungest );
			}
			
		}

		flgHxFamilyChanged = false;
		return;
	}
	
	
	
	
	
	
	public void refreshObGyn(){
		
		
		// Read patient info DIRPT and MEDPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca obgynReca =  medPt.getHxGynReca();
		
		if ( Reca.isValid( obgynReca )){
			
			HxObGyn obgyn = new HxObGyn( obgynReca );
			
			int num;
			
			
			
			// OB History

			if ( obgyn.getObHxUnavailable() == HxObGyn.FlagStatus.TRUE ){
				
				cbObHxUnavailable.setChecked( true );
				txtGravida.setValue( "" );
				txtPara.setValue( "" );
				txtAborta.setValue( "" );
				cbObComplications.setChecked( false );
				
			} else {
				
				cbObHxUnavailable.setChecked( false );
				txtGravida.setValue( String.valueOf( obgyn.getGravida()));
				txtPara.setValue( String.valueOf( obgyn.getPara()));
				txtAborta.setValue( String.valueOf( obgyn.getAborta()));				
				cbObComplications.setChecked( obgyn.getObComplications() == HxObGyn.FlagStatus.TRUE );
			}
			
			txtObNotes.setValue( obgyn.getObNoteText());
			
			
			
			// GYN History
			if ( obgyn.getMenstrualHxUnavailable() == HxObGyn.FlagStatus.TRUE ){
				
				cbMenstrualHxUnavailable.setChecked( true );
				rbMenstrualUnknown.setChecked( true );
				txtAgeMenarche.setValue( "" );
				txtAgeMenopause.setValue( "" );
				ZkTools.setListboxSelectionByValue( lbFlow, HxObGyn.MenstrualFlow.UNSPECIFIED );
				cbMenstrualCramps.setChecked( false );
				cbPMSSymptoms.setChecked( false );

			} else {
			
				cbMenstrualHxUnavailable.setChecked( false );
				HxObGyn.MenstrualStatus status = obgyn.getMenstrualStatus();
				switch ( status ){
				
				case PREMENARCHAL:
					rbPreMenarchal.setChecked( true );
					txtAgeMenarche.setValue( "" );
					txtAgeMenopause.setValue( "" );
					cbMenstrualCramps.setChecked( false );
					cbPMSSymptoms.setChecked( false );
					ZkTools.setListboxSelectionByValue( lbFlow, HxObGyn.MenstrualFlow.UNSPECIFIED );
					break;
				
				case MENSTRUATING:
					rbMenstruating.setChecked( true );
					ZkTools.setTextboxIntIfNotZero( txtAgeMenarche, obgyn.getMenarcheAge());
					txtAgeMenopause.setValue( "" );
					ZkTools.setListboxSelectionByValue( lbFlow, obgyn.getMenstrualFlow());
					cbMenstrualCramps.setChecked( obgyn.getFlgCramps() == HxObGyn.FlagStatus.TRUE );
					cbPMSSymptoms.setChecked( obgyn.getFlgPMS() == HxObGyn.FlagStatus.TRUE );
					break;
				
				case POSTMENOPAUSE:
					rbPostMenopause.setChecked( true );
					ZkTools.setTextboxIntIfNotZero( txtAgeMenarche, obgyn.getMenarcheAge());
					ZkTools.setTextboxIntIfNotZero( txtAgeMenopause, obgyn.getMenopauseAge());
					ZkTools.setListboxSelectionByValue( lbFlow, HxObGyn.MenstrualFlow.UNSPECIFIED );
					cbMenstrualCramps.setChecked( obgyn.getFlgCramps() == HxObGyn.FlagStatus.TRUE );
					cbPMSSymptoms.setChecked( obgyn.getFlgPMS() == HxObGyn.FlagStatus.TRUE );
					break;
				
				case UNKNOWN:
				default:
					rbMenstrualUnknown.setChecked( true );
					txtAgeMenarche.setValue( "" );
					txtAgeMenopause.setValue( "" );
					ZkTools.setListboxSelectionByValue( lbFlow, HxObGyn.MenstrualFlow.UNSPECIFIED );
					cbMenstrualCramps.setChecked( obgyn.getFlgCramps() == HxObGyn.FlagStatus.TRUE );
					cbPMSSymptoms.setChecked( obgyn.getFlgPMS() == HxObGyn.FlagStatus.TRUE );
					break;
				}
			}

			txtMenstrualNotes.setValue( obgyn.getMenstrualNoteText());
			
			
			
			// Sexual History
			
			if ( obgyn.getSexualHxUnavailable() == HxObGyn.FlagStatus.TRUE ){
				
				cbSexualHxUnavailable.setChecked( true );
				cbDyspareunia.setChecked( false );
				cbSameSexEncounters.setChecked( false );
				ZkTools.setListboxSelectionByValue( lbContraception, HxObGyn.ContraceptionMethod.UNSPECIFIED );
				rbSexuallyUnknown.setChecked( true );

			} else {

				switch ( obgyn.getSexuallyActiveStatus()){
				case ACTIVE:
					rbSexuallyActive.setChecked( true );
					break;
				case NOT_ACTIVE:
					rbNotSexuallyActive.setChecked( true );
					break;
				case UNKNOWN:
				default:
					rbSexuallyUnknown.setChecked( true );
					break;
				}
				
				cbSexualHxUnavailable.setChecked( false );
				cbDyspareunia.setChecked( obgyn.getFlgDyspareunia() == HxObGyn.FlagStatus.TRUE );
				cbSameSexEncounters.setChecked( obgyn.getFlgSameSexEncounters() == HxObGyn.FlagStatus.TRUE );
				ZkTools.setListboxSelectionByValue( lbContraception, obgyn.getContraceptionMethod());
			}
			
			txtSexualNotes.setValue( obgyn.getSexualNoteText());
					
		}

		flgObGynChanged = false;
		doCheckObHxUnavailable();
		doCheckMenstrualHxUnavailable();
		doCheckSexualHxUnavailable();
		return;
	}
	
	

	
	
	
	public void refreshHxMisc(){
		
		
		// Read patient info DIRPT and MEDPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca hxmiscReca =  medPt.getHxMiscReca();
		
		if ( Reca.isValid( hxmiscReca )){
			
			HxMisc hxmisc = new HxMisc( hxmiscReca );
			
			
			// Blood History

			if ( hxmisc.getFlgBloodTypeKnown()){
				cbBloodTypeKnown.setChecked( true );
				ZkTools.setListboxSelectionByValue( lbBloodType, hxmisc.getBloodType());
				ZkTools.setListboxSelectionByValue( lbRhesusFactor, hxmisc.getRhesusFactor());
			}
			
			cbReaction.setChecked( hxmisc.getFlgTransfusionReaction());
			cbDonate.setChecked( hxmisc.getFlgWillDonateBlood());
			cbDeclinesBlood.setChecked( hxmisc.getFlgDeclinesBlood());
			

			txtBloodNotes.setValue( hxmisc.getBloodNoteText());
		}
			

		flgHxMiscChanged = false;
		doCheckBloodTypeKnown();
		return;
	}
	
	

	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec=" );

		if ( flgTobaccoChanged ) saveTobaccoChanges();
		if ( flgDrugsChanged || flgDrinkingChanged ) saveDrugsChanges();
		if ( flgHxFamilyChanged ) saveHxFamilyChanges();
		if ( flgObGynChanged && ( ptSex == Sex.FEMALE )) saveObGynChanges();
		if ( flgHxMiscChanged ) saveHxMiscChanges();
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.HISTORY_EDIT, ptRec, Pm.getUserRec(), null, null );

		
		btnSave.setDisabled( true );
		
		return;
	}

	
	
	
	
	
	
	
	
	
	public void saveTobaccoChanges(){
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
			
	
			
		Reca smokingReca = medPt.getSmokingReca();
		
		Smoking smoking = new Smoking( smokingReca );
		smoking.setPtRec( ptRec );
		smoking.setDate( Date.today());

		
		// Smoking
		Smoking.Recode recode = (Smoking.Recode) ZkTools.getListboxSelectionValue( lboxSmokingStatus );

		smoking.setSmokingStatus( recode );

		// if unknown, unspecified, or never smoked - don't need to record anything else
		if (! (( recode == Smoking.Recode.NEVER ) || ( recode == Smoking.Recode.UNKNOWN ) || ( recode == Smoking.Recode.UNSPECIFIED ))){

			// record quit date only if former smoker
			Date quit =  new Date( txtSmokingQuitDate.getValue());
			smoking.setSmokingQuitDate( ( recode == Smoking.Recode.FORMER ) ? quit: (new Date()));

			// record this info if ever smoked
			smoking.setSmokingAgeStarted( EditHelpers.parseInt( txtSmokingAgeStarted.getValue()));
			smoking.setSmokingPacksDay((Smoking.PacksDay) ZkTools.getListboxSelectionValue( lboxSmokingPacksDay ));
			
			smoking.setFlgAttemptingToQuit( cbAttemptingToQuit.isChecked());
			smoking.setFlgMultipleAttempts( cbMultipleAttemptsToQuit.isChecked());
			
		} else {
			
			smoking.setSmokingAgeStarted( 0 );
			smoking.setSmokingPacksDay( Smoking.PacksDay.PACKS_NONE );
			smoking.setFlgAttemptingToQuit( false );
			smoking.setFlgMultipleAttempts( false );
			smoking.setSmokingQuitDate( new Date());
		}
		
		smoking.setSmokingNoteText( txtNotes.getValue());	

		
		
		
		
		// Other tobacco products		
		Smoking.Product prod = (Smoking.Product) ZkTools.getListboxSelectionValue( lboxOtherProduct );
		
		if (( prod != null ) && ( prod != Smoking.Product.EMPTY )){
			
			smoking.setOtherProduct( prod );
			
			Smoking.UserStatus status = (Smoking.UserStatus) ZkTools.getListboxSelectionValue( lboxOtherStatus );
			smoking.setOtherStatus( status );
			
			smoking.setOtherAgeStarted( EditHelpers.parseInt( txtOtherAgeStarted.getValue()));
			
			Date quit = new Date( txtOtherQuitDate.getValue());
			smoking.setOtherQuitDate( ( status == Smoking.UserStatus.FORMER ) ? quit: (new Date()));
			
		} else {
			
			smoking.setOtherProduct( Smoking.Product.EMPTY );
			smoking.setOtherStatus( Smoking.UserStatus.UNSPECIFIED );
			smoking.setOtherAgeStarted( 0 );
			smoking.setOtherQuitDate( new Date());
		}
		
		smoking.setOtherNoteText( txtNotes2.getValue());	

		
		
		
		
		
		
		
		// save new record
		if ( Reca.isValid( smokingReca )){
			smoking.write( smokingReca );

		} else {
			smoking.postNew(ptRec);
		}

		return;
	}
	
	
	
	
	
	
	
	
	
	public void saveDrugsChanges(){
		
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
			
	
			
		Reca drugsReca = medPt.getDrugsReca();
		
		Drugs drugs = new Drugs( drugsReca );
		drugs.setPtRec( ptRec );
		drugs.setDate( Date.today());
		
		
		
		// Smoking
		Drugs.DrinkingStatus status = (Drugs.DrinkingStatus) ZkTools.getListboxSelectionValue( lboxDrinkingStatus );
		drugs.setDrinkingStatus( status );

		// if unknown, unspecified, or never smoked - don't need to record anything else
		if (! (( status == Drugs.DrinkingStatus.NEVER ) || ( status == Drugs.DrinkingStatus.UNKNOWN ) 
				|| ( status == Drugs.DrinkingStatus.UNSPECIFIED ) || ( status == Drugs.DrinkingStatus.NO_SIG ))){

			// record quit date only if former drinker
			Date quit =  new Date( txtDrinkingQuitDate.getValue());
			drugs.setDrinkingQuitDate( (( status == Drugs.DrinkingStatus.FORMER ) || ( status == Drugs.DrinkingStatus.FORMER_HEAVY )) ? quit: (new Date()));

			// record this info if ever drank
			drugs.setDrinkingAgeStarted( EditHelpers.parseInt( txtDrinkingAgeStarted.getValue()));
			//drugs.setDrinkingProduct((Drugs.DrinkingProduct) ZkTools.getListboxSelectionValue( lboxDrinkingProduct ));
			
			//drugs.setFlgAttemptingToQuit( cbAttemptingToQuit.isChecked());
			
		} else {
			
			drugs.setDrinkingAgeStarted( 0 );
			//drugs.setDrinkingProduct( Drugs.DrinkingProduct );
			//drugs.setFlgAttemptingToQuit( false );
			drugs.setDrinkingQuitDate( new Date());
		}
		
		drugs.setDrinkingNoteText( txtDrinkingNotes.getValue());	

		
		
		
		
		// Other tobacco products		
		Drugs.UserStatus dstatus = (Drugs.UserStatus) ZkTools.getListboxSelectionValue( lboxDrugsStatus );
		drugs.setDrugsStatus( dstatus );

		if (( dstatus != null ) && /*( dstatus != Drugs.UserStatus.EMPTY ) &&*/ ( dstatus != Drugs.UserStatus.NEVER )
				&& ( dstatus != Drugs.UserStatus.NO_SIG ) && ( dstatus != Drugs.UserStatus.UNKNOWN ) && ( dstatus != Drugs.UserStatus.UNSPECIFIED )){
			
			
			//Smoking.UserStatus status = (Smoking.UserStatus) ZkTools.getListboxSelectionValue( lboxOtherStatus );
			//drugs.setOtherStatus( status );
			
			//drugs.setOtherAgeStarted( EditHelpers.parseInt( txtOtherAgeStarted.getValue()));
			
			//Date quit = new Date( txtOtherQuitDate.getValue());
			//drugs.setOtherQuitDate( ( status == Smoking.UserStatus.FORMER ) ? quit: (new Date()));
			
			if (( dstatus == Drugs.UserStatus.CURRENT ) || ( dstatus == Drugs.UserStatus.CURRENT_HEAVY ) 
					|| ( dstatus == Drugs.UserStatus.CURRENT_OCCASIONAL )){
				
				String str = "";
				for ( Drugs.DrugsOfAbuse r : EnumSet.allOf(Drugs.DrugsOfAbuse.class)){
					Checkbox cb;
					try { cb = ((Checkbox)(pgDrugs.getFellow( "cbCur" + r.getCode())));
						if (( cb != null ) && cb.isChecked()) str += r.getCode();
					} catch ( Exception e ){}					
				}
				drugs.setCurrentDrugsList( str );

			}
			
			
			if (( dstatus == Drugs.UserStatus.CURRENT ) || ( dstatus == Drugs.UserStatus.CURRENT_HEAVY ) 
				|| ( dstatus == Drugs.UserStatus.CURRENT_OCCASIONAL )
				|| ( dstatus == Drugs.UserStatus.FORMER ) || ( dstatus == Drugs.UserStatus.FORMER_HEAVY )){
				
				String str = "";
				for ( Drugs.DrugsOfAbuse r : EnumSet.allOf(Drugs.DrugsOfAbuse.class)){
					Checkbox cb;
					try { cb = ((Checkbox)(pgDrugs.getFellow( "cbPast" + r.getCode())));
						if (( cb != null ) && cb.isChecked()) str += r.getCode();
					} catch ( Exception e ){}					
				}
				
				drugs.setPastDrugsList( str );
			}

		} else {
			
			//drugs.setOtherProduct( Smoking.Product.EMPTY );
			//drugs.setOtherStatus( Smoking.UserStatus.UNSPECIFIED );
			//drugs.setOtherAgeStarted( 0 );
			//drugs.setOtherQuitDate( new Date());
		}
		
		drugs.setDrugsNoteText( txtDrugsNotes.getValue());	

		
		
		// save new record
		if ( Reca.isValid( drugsReca )){
			drugs.write( drugsReca );

		} else {
			drugs.postNew(ptRec);
		}

		return;
	}
	
	
	
	
	
	
	
	
	
	public void saveHxFamilyChanges(){
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		Reca hxFamilyReca = medPt.getHxFamilyReca();

		// read current family hx record if present
		HxFamily hxfam = new HxFamily( hxFamilyReca );
		
		// set some things
		hxfam.setPtRec( ptRec );
		hxfam.setDate( Date.today());
		hxfam.setStatus( usrlib.RecordStatus.CURRENT );

		hxfam.setFlgPaternalHxUnavailable( cbPaternalHxUnavailable.isChecked());
		hxfam.setFlgMaternalHxUnavailable( cbMaternalHxUnavailable.isChecked());
		
		if ( rbFatherAlive.isChecked()){
			
			hxfam.setFatherStatus( HxFamily.ParentStatus.LIVING );
			
			int year = EditHelpers.parseInt( txtFatherYearBorn.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearFatherBorn( year );
			hxfam.setYearFatherDeceased( 0 );
			hxfam.setFatherCauseText( "" );
			
		} else if ( rbFatherDeceased.isChecked()){
			
			hxfam.setFatherStatus( HxFamily.ParentStatus.DECEASED );
			
			int year = EditHelpers.parseInt( txtFatherYearBorn.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearFatherBorn( year );
			//System.out.println("year of pop birth:"+year);
			
			year = EditHelpers.parseInt( txtFatherYearDeceased.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearFatherDeceased( year );
			//System.out.println("year of death for pops:"+ year);
			
			hxfam.setFatherCauseText( txtFatherCauseOfDeath.getValue());
			
		} else {  /* rbFatherUnknown.isChecked() */
			
			hxfam.setFatherStatus( HxFamily.ParentStatus.UNKNOWN );
			hxfam.setYearFatherBorn( 0 );
			hxfam.setYearFatherDeceased( 0 );
			hxfam.setFatherCauseText( "" );

		}
		
		
		
		
		
		if ( rbMotherAlive.isChecked()){
			
			hxfam.setMotherStatus( HxFamily.ParentStatus.LIVING );
			
			int year = EditHelpers.parseInt( txtMotherYearBorn.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearMotherBorn( year );
			hxfam.setYearMotherDeceased( 0 );
			hxfam.setMotherCauseText( "" );
			
		} else if ( rbMotherDeceased.isChecked()){
			
			hxfam.setMotherStatus( HxFamily.ParentStatus.DECEASED );
			
			int year = EditHelpers.parseInt( txtMotherYearBorn.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearMotherBorn( year );
			
			year = EditHelpers.parseInt( txtMotherYearDeceased.getValue());
			if (( year != 0 ) && ( year < 100 )) year += 1900;
			hxfam.setYearMotherDeceased( year );

			hxfam.setMotherCauseText( txtMotherCauseOfDeath.getValue());
			
		} else {  /* rbMotherUnknown.isChecked() */
			
			hxfam.setMotherStatus( HxFamily.ParentStatus.UNKNOWN );
			hxfam.setYearMotherBorn( 0 );
			hxfam.setYearMotherDeceased( 0 );
			hxfam.setMotherCauseText( "" );

		}
		
		
		
		
		
		hxfam.setNumBrothers( EditHelpers.parseInt( txtNumBrothers.getValue()));
		hxfam.setNumSisters( EditHelpers.parseInt( txtNumSisters.getValue()));
		hxfam.setNumSons( EditHelpers.parseInt( txtNumSons.getValue()));
		hxfam.setNumDaughters( EditHelpers.parseInt( txtNumDaughters.getValue()));
		
		
		int age = EditHelpers.parseInt( txtKidsAgeYoungest.getValue());
		if ( age > 0 ){
			hxfam.setKidsYoungestBirthYear( Date.today().getYear() - age );
		}
		
		age = EditHelpers.parseInt( txtKidsAgeOldest.getValue());
		if ( age > 0 ){
			hxfam.setKidsOldestBirthYear( Date.today().getYear() - age );
		}
		
		
		
		
		
		
		// save new record
		if ( Reca.isValid( hxFamilyReca )){
			hxfam.write( hxFamilyReca );

		} else {
			hxfam.postNew(ptRec);
		}

		return;
	}
	
	
	
	
	
	
	
	
	

	public void saveObGynChanges(){
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		Reca obgynReca = medPt.getHxGynReca();
		
		// read current family hx record if present
		HxObGyn obgyn = new HxObGyn( obgynReca );
		
		// set some things
		obgyn.setPtRec( ptRec );
		obgyn.setDate( Date.today());
		obgyn.setStatus( usrlib.RecordStatus.CURRENT );

		
		
		// OB History

		if ( cbObHxUnavailable.isChecked()){
			
			obgyn.setObHxUnavailable( HxObGyn.FlagStatus.TRUE );
			obgyn.setGravida( 0 );
			obgyn.setPara( 0 );
			obgyn.setAborta( 0 );
			obgyn.setObComplications( HxObGyn.FlagStatus.UNSPECIFIED );
			
		} else {
			
			obgyn.setObHxUnavailable( HxObGyn.FlagStatus.FALSE );
			obgyn.setGravida( EditHelpers.parseInt( txtGravida.getValue()));
			obgyn.setPara( EditHelpers.parseInt( txtPara.getValue()));
			obgyn.setAborta( EditHelpers.parseInt( txtAborta.getValue()));
			obgyn.setObComplications( cbObComplications.isChecked() ? HxObGyn.FlagStatus.TRUE: HxObGyn.FlagStatus.FALSE );
		}

		obgyn.setObNoteText( txtObNotes.getValue());
		
		
		// Menstrual History
		if ( cbMenstrualHxUnavailable.isChecked()){
			
			obgyn.setMenstrualHxUnavailable( HxObGyn.FlagStatus.TRUE );
			obgyn.setMenstrualStatus( HxObGyn.MenstrualStatus.UNSPECIFIED );
			obgyn.setMenarcheAge( 0 );
			obgyn.setMenopauseAge( 0 );
			obgyn.setFlgCramps( HxObGyn.FlagStatus.UNSPECIFIED );
			obgyn.setFlgPMS( HxObGyn.FlagStatus.UNSPECIFIED );
			obgyn.setMenstrualFlow( HxObGyn.MenstrualFlow.UNSPECIFIED );
			
		} else {

			obgyn.setMenstrualHxUnavailable( HxObGyn.FlagStatus.FALSE );

			if ( rbPreMenarchal.isChecked()){
				obgyn.setMenstrualStatus( HxObGyn.MenstrualStatus.PREMENARCHAL );
			} else if ( rbMenstruating.isChecked()){
				obgyn.setMenstrualStatus( HxObGyn.MenstrualStatus.MENSTRUATING );
			} else if ( rbPostMenopause.isChecked()){
				obgyn.setMenstrualStatus( HxObGyn.MenstrualStatus.POSTMENOPAUSE );
			} else /* rbMenstrualUnknown.isChecked()) */ {
				obgyn.setMenstrualStatus( HxObGyn.MenstrualStatus.UNKNOWN );
			}
			
			obgyn.setMenarcheAge( EditHelpers.parseInt( txtAgeMenarche.getValue()));
			obgyn.setMenopauseAge( EditHelpers.parseInt( txtAgeMenopause.getValue()));
			
			obgyn.setFlgCramps( cbMenstrualCramps.isChecked() ? HxObGyn.FlagStatus.TRUE: HxObGyn.FlagStatus.FALSE );
			obgyn.setFlgPMS( cbPMSSymptoms.isChecked() ? HxObGyn.FlagStatus.TRUE: HxObGyn.FlagStatus.FALSE );
			obgyn.setMenstrualFlow( (HxObGyn.MenstrualFlow) ZkTools.getListboxSelectionValue( lbFlow ));
		}

		obgyn.setMenstrualNoteText( txtMenstrualNotes.getValue());
		
		
		// Sexual History
		if ( cbSexualHxUnavailable.isChecked()){
			
			obgyn.setSexualHxUnavailable( HxObGyn.FlagStatus.TRUE );
			obgyn.setFlgDyspareunia( HxObGyn.FlagStatus.UNSPECIFIED );
			obgyn.setFlgSameSexEncounters( HxObGyn.FlagStatus.UNSPECIFIED );
			obgyn.setContraceptionMethod( HxObGyn.ContraceptionMethod.UNSPECIFIED );
			
		} else {
			
			obgyn.setSexualActivityStatus( HxObGyn.SexualActivityStatus.UNKNOWN );
			if ( rbSexuallyActive.isChecked()) obgyn.setSexualActivityStatus( HxObGyn.SexualActivityStatus.ACTIVE );
			if ( rbNotSexuallyActive.isChecked()) obgyn.setSexualActivityStatus( HxObGyn.SexualActivityStatus.NOT_ACTIVE );
			obgyn.setSexualHxUnavailable( HxObGyn.FlagStatus.FALSE );
			obgyn.setFlgDyspareunia( cbDyspareunia.isChecked() ? HxObGyn.FlagStatus.TRUE: HxObGyn.FlagStatus.FALSE );
			obgyn.setFlgSameSexEncounters( cbSameSexEncounters.isChecked() ? HxObGyn.FlagStatus.TRUE: HxObGyn.FlagStatus.FALSE );
			obgyn.setContraceptionMethod( (ContraceptionMethod) ZkTools.getListboxSelectionValue( lbContraception ));
		}
		
		obgyn.setSexualNoteText( txtSexualNotes.getValue());

		
		
		
		// save new record
		if ( Reca.isValid( obgynReca )){
			obgyn.write( obgynReca );

		} else {
			obgyn.postNew( ptRec );
		}

		return;
	}
	
	
	
	
	
	
	
	public void saveHxMiscChanges(){
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		Reca hxmiscReca = medPt.getHxMiscReca();
		
		// read current misc hx record if present
		HxMisc hxmisc = new HxMisc( hxmiscReca );
		
		// set some things
		hxmisc.setPtRec( ptRec );
		hxmisc.setDate( Date.today());
		hxmisc.setStatus( usrlib.RecordStatus.CURRENT );

		
		hxmisc.setFlgWillDonateBlood( cbDonate.isChecked());
		hxmisc.setFlgTransfusionReaction( cbReaction.isChecked());
		hxmisc.setFlgDeclinesBlood( cbDeclinesBlood.isChecked());

		
		HxMisc.BloodType type = (HxMisc.BloodType) ZkTools.getListboxSelectionValue( lbBloodType );
		HxMisc.RhesusFactor factor = (HxMisc.RhesusFactor) ZkTools.getListboxSelectionValue( lbRhesusFactor );
		
		if ( cbBloodTypeKnown.isChecked() && ( type != null ) && ( factor != null )){
			hxmisc.setFlgBloodTypeKnown( true );			
			hxmisc.setBloodType( type );
			hxmisc.setRhesusFactor( factor );			
		} else {
			hxmisc.setFlgBloodTypeKnown( false );			
			hxmisc.setBloodType( HxMisc.BloodType.UNSPECIFIED );
			hxmisc.setRhesusFactor( HxMisc.RhesusFactor.UNSPECIFIED );
		}

		hxmisc.setBloodNoteText( txtBloodNotes.getValue());

		
		
		
		// save new record
		if ( Reca.isValid( hxmiscReca )){
			hxmisc.write( hxmiscReca );
		} else {
			hxmisc.postNew( ptRec );
		}

		return;
	}
	
	
	
	

	

	
	
	public void onClose$historyWin( Event e ) throws InterruptedException{
		alert( "onCloset event");
		onClick$btnCancel( e );
		return;
	}
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			ProgressBox progBox = ProgressBox.show(historyWin, "Saving..." );

			save();
			progBox.close();
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
		
		// close editPtWin
		//historyWin.detach();

		// close tab and tabpanel
		//if ( tabpanel != null ){
		//	tabpanel.getLinkedTab().detach();
		//	tabpanel.detach();
		//}
		refreshpg4();
		return;
	}
	
	
	
	
	
	
	
	public void onChange$pg4( Event ev ){
		flgTobaccoChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChangeDrugs$pgDrugs( Event ev ){
		flgDrugsChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChangeDrinking$pgEtOH( Event ev ){
		flgDrinkingChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChange$pgHxFamily( Event ev ){
		flgHxFamilyChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChange$pgOb( Event ev ){
		flgObGynChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChange$pgGyn( Event ev ){
		flgObGynChanged = true;
		btnSave.setDisabled( false );	
	}

	public void onChange$pgBlood( Event ev ){
		flgHxMiscChanged = true;
		btnSave.setDisabled( false );	
	}

	
	
	
	
	public void onCheck$cbObHxUnavailable$pgOb( Event ev ){ doCheckObHxUnavailable(); }
	private void doCheckObHxUnavailable(){

		boolean status = cbObHxUnavailable.isChecked();
		rowGravida.setVisible( ! status );
		rowPara.setVisible( ! status );
		rowAborta.setVisible( ! status );
		rowComplications.setVisible( ! status );
		return;
	}
	
	
	public void onCheck$cbMenstrualHxUnavailable$pgGyn( Event ev ){ doCheckMenstrualHxUnavailable(); }
	private void doCheckMenstrualHxUnavailable(){

		boolean status = cbMenstrualHxUnavailable.isChecked();
		rowMenstrualStatus.setVisible( ! status );
		rowMenarche.setVisible( ! status );
		rowMenopause.setVisible( ! status );
		rowFlow.setVisible( ! status );
		rowCramps.setVisible( ! status );
		rowPMS.setVisible( ! status );
		return;
	}
	
	
	public void onCheck$cbSexualHxUnavailable$pgGyn( Event ev ){ doCheckSexualHxUnavailable(); }
	private void doCheckSexualHxUnavailable(){

		boolean status = cbSexualHxUnavailable.isChecked();
		rowSexualStatus.setVisible( ! status );
		rowContraception.setVisible( ! status );
		rowDyspareunia.setVisible( ! status );
		rowSameSexEncounters.setVisible( ! status );
		return;
	}
	
	public void onCheck$cbBloodTypeKnown$pgBlood( Event ev ){ doCheckBloodTypeKnown(); }
	private void doCheckBloodTypeKnown(){

		boolean status = cbBloodTypeKnown.isChecked();
		rowBloodType.setVisible( status );
		return;
	}
	
	
	

	public void onSelect$lboxSmokingStatus$pg4( Event ev ){

		// set smoking recode label
		Smoking.Recode recode = (Smoking.Recode) usrlib.ZkTools.getListboxSelectionValue( lboxSmokingStatus );
		if ( recode == null ) recode = Smoking.Recode.UNSPECIFIED;
		lblRecode.setValue( "Recode:" + recode.getCode());
		char r = recode.getCode();
		
		// hide some fields
		divSmokingDetails.setVisible( ! (( r == '4' ) || ( r == '9' ) || ( r == '0' )));		
		rowSmokingQuitDate.setVisible(( recode == palmed.Smoking.Recode.FORMER ));
		return;
	}
	

	public void onSelect$lboxOtherProduct$pg4( Event ev ){ onSelect$lboxOtherStatus$pg4( ev ); }

	public void onSelect$lboxOtherStatus$pg4( Event ev ){

		Smoking.UserStatus status = (Smoking.UserStatus) usrlib.ZkTools.getListboxSelectionValue( lboxOtherStatus );
		if ( status == null ) status = Smoking.UserStatus.UNSPECIFIED;
		divTobaccoDetails.setVisible( ! (( status == Smoking.UserStatus.NEVER ) || ( status == Smoking.UserStatus.UNKNOWN ) || ( status == Smoking.UserStatus.UNSPECIFIED )));

		rowTobaccoQuitDate.setVisible(( status == Smoking.UserStatus.FORMER ));
		return;
	}

	
	public void onSelect$lboxDrugsStatus$pgDrugs( Event ev ){ doSelectDrugsStatus(); }
	public void doSelectDrugsStatus(){
		
		//System.out.println( "in onSelect$lboxDrugsStatus$pgDrugs" );
		palmed.Drugs.UserStatus status = (palmed.Drugs.UserStatus) usrlib.ZkTools.getListboxSelectionValue( lboxDrugsStatus );
		if ( status == null ) status = palmed.Drugs.UserStatus.UNSPECIFIED;
		
		switch ( status ){
		
		case NEVER:
		case NO_SIG:
		case UNKNOWN:
		case UNSPECIFIED:
			divCurrentDrugs.setVisible( false );
			divPastDrugs.setVisible( false );
			break;
			
		case CURRENT_OCCASIONAL:
		case CURRENT:
		case CURRENT_HEAVY:
			divCurrentDrugs.setVisible( true );
			divPastDrugs.setVisible( true );
			break;

		case FORMER:
		case FORMER_HEAVY:
			divCurrentDrugs.setVisible( false );
			divPastDrugs.setVisible( true );
			//rowSmokingQuitDate.setVisible(( recode == palmed.Smoking.Recode.FORMER ));
			break;
		}
		
		return;
	}

	
	public void onSelect$lboxDrinkingStatus$pgEtOH( Event ev ){ doSelectDrinkingStatus(); }
	public void doSelectDrinkingStatus(){
		
		palmed.Drugs.DrinkingStatus status = (palmed.Drugs.DrinkingStatus) usrlib.ZkTools.getListboxSelectionValue( lboxDrinkingStatus );
		if ( status == null ) status = palmed.Drugs.DrinkingStatus.UNSPECIFIED;
		
		switch ( status ){
		
		case NEVER:
		case NO_SIG:
		case UNKNOWN:
		case UNSPECIFIED:
			rowDrinkingQuitDate.setVisible( false );
			rowDrinkingAgeStarted.setVisible( false );
			break;
			
		case CURRENT_RARE:
		case CURRENT_SOCIAL:
		case CURRENT_DAILY:
		case CURRENT_HEAVY:
			rowDrinkingQuitDate.setVisible( false );
			rowDrinkingAgeStarted.setVisible( true );
			break;

		case FORMER:
		case FORMER_HEAVY:
			rowDrinkingQuitDate.setVisible( true );
			rowDrinkingAgeStarted.setVisible( true );
			break;
		}
		
		return;
	}

	
	public void onClick$t_procedures( Event ev ){
		if ( procWin != null ) return;
		procWin = HxProcedures.show( ptRec, tp_procedures );
	}

	public void onClick$t_family2( Event ev ){
		if ( family2Win != null ) return;
		family2Win = HxFamilyItemWin.show( ptRec, tp_family2 );
	}


}
