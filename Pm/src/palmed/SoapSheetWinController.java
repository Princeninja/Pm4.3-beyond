package palmed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.PhyExamWinController.Impressions;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.ImageHelper;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;

public class SoapSheetWinController extends GenericForwardComposer {

	
	private Rec	ptRec = null;
	private Reca soapReca = null;
	private String Vfile = null;
	private String Debug = null;
	Rec provRec = new Rec( 2 );
	
	// Display fields at top of chart (ALL AUTOWIRED)
	private Window soapShtWin;
	private Window ptChartWin;		// autowired
	private Label ptname;			// autowired - patient name
	private Label ptnum;			// autowired - patient number
	private Label ptssn;			// autowired - patient SSN
	private Label ptrec;			// autowired - pt record number
	private Label ptsex;			// autowired - pt sex
	private Label ptdob;			// autowired - pt birthdate
	private Label ptage;			// autowired - pt age
	private Label ptRace;			// autowired
	private Label ptEthnicity;		// autowired
	private Label ptLanguage;		// autowired
	
	private Image ptImage;			// autowired - pt's picture
	
	private Label address1;
	private Label address2;
	private Label address3;
	private Label home_ph;
	private Label work_ph;
	
	private Listbox meds;			// autowired - medications listbox
	private Listbox pars;			// autowired - pars listbox
	private Listbox problems;		// autowired - problems listbox
	
	private Listbox mrlog_listbox;	// autowired - MrLog listbox
	private Hbox ehbox;
	
	private Button save;
	private Button end;
	
	Listbox lboxProblems2;
	Listbox lboxAssess;
	Listbox lboxAlerts;
	Listbox lboxProviders;			// autowired - provider select box
	

	// Tabs and Tabpanels for main part of chart  (ALL AUTOWIRED)
	private Tab tabSheet;			// autowired - tab
	private Tabpanel tpSheet;		// autowired - tabpanel
	private Tab tabLast;			// autowired - tab
	private Tabpanel tpLast;		// autowired - tabpanel
	private Tab tabSubject;			// autowired - tab
	private Tabpanel tpSubject;		// autowired - tabpanel
	private Tab tabROS;				// autowired - tab
	private Tabpanel tpROS;			// autowired - tabpanel
	private Tab tabHx;				// autowired - tab
	private Tabpanel tpHx;			// autowired - tabpanel
	private Tab tabVitals;			// autowired - tab
	private Tabpanel tpVitals;		// autowired - tabpanel
	private Tab tabExam;			// autowired - tab
	private Tabpanel tpExam;		// autowired - tabpanel
	private Tab tabAssess;			// autowired - tab
	private Tabpanel tpAssess;		// autowired - tabpanel
	private Tab tabOrders;			// autowired - tab
	private Tabpanel tpOrders;		// autowired - tabpanel
	private Tab tabRX;				// autowired - tab
	private Tabpanel tpRX;			// autowired - tabpanel
	private Tab tabFU;				// autowired - tab
	private Tabpanel tpFU;			// autowired - tabpanel
	private Tab tabCharge;			// autowired - tab
	private Tabpanel tpCharge;		// autowired - tabpanel
	private Tab tabSummary;			// autowired - tab
	private Tabpanel tpSummary;		// autowired - tabpanel
	
	
	
	// Windows for each tab opened in main part of soap sheet  (NOT AUTOWIRED)	
	private Window sheetWin = null;			// Window
	private boolean lastWin = false;		// Window
	private Window subjectWin = null;		// Window
	private Window rosWin = null;			// Window
	private Window hxWin = null;			// Window
	private Window vitalsWin = null;		// Window
	private Window examWin = null;			// Window
	private Window assessWin = null;		// Window
	private Window ordersWin = null;		// Window
	private Window rxWin = null;			// Window
	private Window fuWin = null;			// Window
	private Window chargeWin = null;		// Window
	private Window summaryWin = null;		// Window
	
	
	private Groupbox gbROS;
	private Groupbox gbHx;
	private Groupbox gbVi;
	private Groupbox gbEx;
	private Groupbox gbOrders;
	private Groupbox gbRX;
	private Groupbox gbFU;
	private Groupbox gbCharge;
	
	private Textbox cc;
	private Textbox subj;
	private Textbox RosTextbox;
	private Textbox HxTextbox;
	private Textbox VitalsTextbox;
	private Textbox ExamTextbox;
	private Textbox OrdersTextbox;
	private Textbox RxTextbox;
	private Textbox FUTextbox;
	private Textbox ChargeTextbox;
	//private Textbox txtAssess;

	//private boolean newcropManagedMeds = false;	// flag that NewCrop will manage all meds
	//private boolean newcropManagedPARs = false;	// flag that NewCrop will manage all PARs
	
	
	private Div divOrders;
	
	private Textbox soapTextbox;			// autowired - last soap textbox
	private Textbox noteTextbox;			// autowired - last nursing notes
	private Textbox labTextbox;				// autowired - recent lab reports
	

	private Component parent;
	
	private MedPt	medPt;			// patient's medical record
	private DirPt  dirPt;
	private usrlib.Date lastVisitDate;		// last visit date (determine from last soap date for now)
	
	private String  Provider, CC , Subj,  ROS, Hx, Vitals, Exam, Assess, Orders, RX, FU, Charge, summary, Status;
	
	private boolean noAssessment = false;
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println( "in doAfterCompose()" );
		

		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ Vfile = (String) myMap.get( "Vfile" ); } catch ( Exception e ) { /* ignore */ };
				try{ Debug = (String) myMap.get( "Debug" ); } catch ( Exception e ) { /* ignore */ };
				
			}
		}
		
		if ( ptRec == null ) System.out.println( "ptRec == null" );
		if ( Vfile != null ) {System.out.println("Vfile is : "+ Vfile);}
		else {System.out.println( "Vfile == null" );}
		if (Debug == null ) System.out.println( "Debug == null");
		
		/* java.util.Calendar calendar = GregorianCalendar.getInstance(); 
		 int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		 int mins = calendar.get(java.util.Calendar.MINUTE);
		 int secs = calendar.get(java.util.Calendar.SECOND);
			
		 String timenow = (String.format("%02d", hour))+":"+ (String.format("%02d", mins))+(String.format("%02d", secs));
		 //System.out.println("timenow: "+timenow);
		 
		StringBuilder Strupdate = new StringBuilder();
		Strupdate.append(System.getProperty("line.separator"));
		String Strup = "At do compose for ISS: "+ timenow;
		Strupdate.append(Strup);
		Strupdate.append(System.getProperty("line.separator"));
		
		try {
		    Files.write(Paths.get(Pm.getOvdPath()+Debug), Strupdate.toString().getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}* */
		
		
		//TODO protocol for user lock tabpanels
		// tpSheet.setVisible(false);		
		 //tabLast.setVisible(false);		
		 tpLast.setVisible(false);	
		 tabLast.setDisabled(true);
		 tpLast.setVisible(false);
		 tabROS.setVisible(false);			
		 //tpHx.setVisible(false);			
		 tabVitals.setVisible(false);		
		 tabExam.setVisible(false);		
		 tabAssess.setVisible(false);		
		 tabOrders.setVisible(false);		
		 //tpRX.setVisible(false);			
		 tabFU.setVisible(false);			
		 tabCharge.setVisible(false);		
		 tabSummary.setVisible(false);	
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
	
		//System.out.println("visit is: "+ visit);
		try {
			reader = new FileReader( Vfile );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + Vfile + " file was not found:" );
	    	
	    }
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e ) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		final XMLElement e;
				
		 String Name = xml.getName();
		 final int NosChildren = xml.countChildren();
		 //System.out.println("Number of children: "+NosChildren +","+Name);
		 
		 e = xml.getElementByPathName(Name);
		
		
		 XMLElement Info = new XMLElement();
		 XMLElement Todo = new XMLElement();
		 
		 		 
			
		 Info = e.getChildByName("INFO");
		 //Todo = e.getChildByName("TODO");
		 Status = Info.getChildByName("Status").getContent().trim();
		 System.out.println("Status is: "+Status);
		 
		 if ( Status.equals("Exam Room")){
			
			 soapShtWin.setPosition("center,left");
			 
			 tabLast.setDisabled(false);
			 tabLast.setSelected(true);
			 //onSelect$tabLast(null);
			 
				tabExam.setVisible(true);
				Exam = Info.getChildByName("Exam").getContent().trim();
				 if ( !Exam.equals("QExamQ")){ ExamTextbox.setText(Exam); } else { ExamTextbox.setText(""); }

		 }else{ tabSubject.setSelected(true); }
		 
		 if (  Status.equals("Triage") ){
		 
			 	tabOrders.setVisible(true);
			 	Orders = Info.getChildByName("Orders").getContent().trim();
			 	if ( !Orders.equals("QOrdersQ")){ OrdersTextbox.setText(Orders); } else { OrdersTextbox.setText(""); }

		 }
		 
		 if ( Status.equals("Exam Room") || Status.equals("Triage") ){
				
			 	tabAssess.setVisible(true); 
			 	 Assess = Info.getChildByName("Assesment").getContent().trim();
			 	if ( Assess.equals("QAssessQ") || Assess.length() < 3) { noAssessment = true; }

			 	 
				tabVitals.setVisible(true);
				Vitals = Info.getChildByName("Vitals").getContent().trim();
				if ( !Vitals.equals("QVitalsQ")){  VitalsTextbox.setText(Vitals); } else {  VitalsTextbox.setText(""); }
				 
				
				tabROS.setVisible(true); 
				ROS = Info.getChildByName("ROS").getContent().trim();
				if ( !ROS.equals("QROSQ")){ RosTextbox.setText(ROS); } else { RosTextbox.setText(""); }

		 }
		 
		 
		 if ( Status.equals("Post Visit")){
			 	
			 	tabFU.setVisible(true);
			 	FU = Info.getChildByName("FU").getContent().trim();
			 	if ( !FU.equals("QFUQ")){ FUTextbox.setText(FU); } else { FUTextbox.setText(""); }

			 	
			    tabSummary.setVisible(true);
			    summary = Info.getChildByName("Summary").getContent().trim();
			    
			 	tabCharge.setVisible(true);
			 	Charge = Info.getChildByName("Charge").getContent().trim();
			 	if ( !Charge.equals("QChargeQ")){ ChargeTextbox.setText(Charge); } else { ChargeTextbox.setText(""); }
				
		 }
		 
		 if ( Status.equals("End")){
			 
			 end.setVisible(true);
			 soapShtWin.setPosition("center,left");
			 
			 tabLast.setDisabled(false);
			 tpLast.setVisible(true);
			 tabLast.setVisible(true);
						 
			 tabExam.setVisible(true);
			 Exam = Info.getChildByName("Exam").getContent().trim();
				 if ( !Exam.equals("QExamQ")){ ExamTextbox.setText(Exam); } else { ExamTextbox.setText(""); }

			tabOrders.setVisible(true);
			Orders = Info.getChildByName("Orders").getContent().trim();
			 	if ( !Orders.equals("QOrdersQ")){ OrdersTextbox.setText(Orders); } else { OrdersTextbox.setText(""); }

			tabAssess.setVisible(true); 
			Assess = Info.getChildByName("Assesment").getContent().trim();
			if ( Assess.equals("QAssessQ") || Assess.length() < 3) { noAssessment = true; }

			 	 
			tabVitals.setVisible(true);
			Vitals = Info.getChildByName("Vitals").getContent().trim();
			if ( !Vitals.equals("QVitalsQ")){  VitalsTextbox.setText(Vitals); } else {  VitalsTextbox.setText(""); }
				 
				
			tabROS.setVisible(true); 
			ROS = Info.getChildByName("ROS").getContent().trim();
			if ( !ROS.equals("QROSQ")){ RosTextbox.setText(ROS); } else { RosTextbox.setText(""); }
	 
			tabFU.setVisible(true);
		 	FU = Info.getChildByName("FU").getContent().trim();
		 	if ( !FU.equals("QFUQ")){ FUTextbox.setText(FU); } else { FUTextbox.setText(""); }

		 	
		    tabSummary.setVisible(true);
		    summary = Info.getChildByName("Summary").getContent().trim();
		    
		 	tabCharge.setVisible(true);
		 	Charge = Info.getChildByName("Charge").getContent().trim();
		 	if ( !Charge.equals("QChargeQ")){ ChargeTextbox.setText(Charge); } else { ChargeTextbox.setText(""); }
	
			 
		 }
		 
		 Provider = Info.getChildByName("Provider").getContent().trim();
		 CC = Info.getChildByName("CC").getContent().trim();
		 Subj = Info.getChildByName("HPI").getContent().trim();
		 Hx = Info.getChildByName("Hx").getContent().trim();
		 RX = Info.getChildByName("RX").getContent().trim();
		 
		 
		 
		 
		 //System.out.println("Exam is: "+ Exam);
		 //System.out.println("CC is: "+CC);
		 
		 
		 if ( !CC.equals("QCCQ")){ cc.setText(CC); } else { cc.setText(""); }
		 
		 if ( !Subj.equals("QHPIQ")){ subj.setText(Subj); } else { subj.setText(""); }
		 		 
		 
		 if ( !Hx.equals("QHxQ")){ HxTextbox.setText(Hx); } else { HxTextbox.setText(""); }
			
		 		 
		 if ( !RX.equals("QRXQ")){ RxTextbox.setText(RX); } else { RxTextbox.setText(""); }

		
		 
		 
		soapShtWin.setClosable(true);
		
		
		/* java.util.Calendar calendar00 = GregorianCalendar.getInstance(); 
		 int hour00 = calendar00.get(java.util.Calendar.HOUR_OF_DAY);
		 int mins00 = calendar00.get(java.util.Calendar.MINUTE);
		 int secs00 = calendar.get(java.util.Calendar.SECOND);
			
		 String timenow00 = (String.format("%02d", hour00))+":"+ (String.format("%02d", mins00))+(String.format("%02d", secs00));
		 //System.out.println("timenow: "+timenow);
		 
		StringBuilder Strupdate00 = new StringBuilder();
		Strupdate00.append(System.getProperty("line.separator"));
		String Strup00 = "Xml file parsed and all data obtained: "+ timenow00;
		Strupdate00.append(Strup00);
		Strupdate00.append(System.getProperty("line.separator"));
		
		
		try {
		    Files.write(Paths.get(Pm.getOvdPath()+Debug), Strupdate00.toString().getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e00) {
		    //exception handling left as an exercise for the reader
		} */
		
		
		
		//ehbox.setVisible(false);
		// DEBUG
		// Normally would not get here without args and ptRec set 
		//   -- BUT I needed to be able to for easy testing
		//TODO - take out this hard code ptRec
		if (( ptRec == null ) || ( ptRec.getRec() == 0 )) ptRec = new Rec( 7072 );
		System.out.println( "ptRec=" + ptRec.getRec());
		
		// Read patient info DIRPT and INFOPT
		dirPt = new DirPt( ptRec );
		
	
		// Get some patient values and set labels
		//ptrec.setValue( "" + ptRec.getRec());
		ptname.setValue( dirPt.getName().getPrintableNameLFM());
		
		
		//ptssn.setValue( dirPt.getSSN());
		//ptnum.setValue( dirPt.getPtNumber());
		ptsex.setValue( dirPt.getSex().getAbbr());
		//ptdob.setValue( dirPt.getBirthdate().getPrintable( 9 ));
		ptage.setValue( Date.getPrintableAge( dirPt.getBirthdate()));
		
		//ptRace.setValue( dirPt.getRace().getLabel());
		//ptEthnicity.setValue( dirPt.getEthnicity().getLabel());
		//ptLanguage.setValue( dirPt.getLanguage().getLabel());

		
		// patient address
		/*{
		String s2, s3;
		address1.setValue( dirPt.getAddress().getPrintableAddress(1));
		address2.setValue( s2 = dirPt.getAddress().getPrintableAddress(2));
		s3 = dirPt.getAddress().getPrintableAddress(3);
		if ( s2.trim().length() == 0 ){
			address2.setValue( s3 );
			address3.setValue( "" );
		} else {
			address3.setValue( s3 );
		}
	
		work_ph.setValue( dirPt.getAddress().getWork_ph());
		home_ph.setValue( dirPt.getAddress().getHome_ph());
		}*/

		
		
		// Fill some listboxes
		Prov.fillListbox( lboxProviders, true );
		ZkTools.setListboxSelectionByValue( lboxProviders, new Rec( 2 ));
		lboxProviders.setSelectedItem(lboxProviders.getItemAtIndex(Integer.parseInt(Provider)+1));
		
		
		// Is window in a Tabpanel???
		// Get Tabpanel chart is to be placed in.
		// May have to go several layers deep due to enclosing window.
		
		/*Tabpanel tabpanel = null;

		if ( component instanceof Tabpanel ){
			tabpanel = (Tabpanel) component;
		} else if ( component.getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent();
		} else if ( component.getParent().getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent().getParent();
		}

		
		// Set title of Tabpanel to Patient's name
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().setLabel( dirPt.getName().getPrintableNameLFM());
			tabpanel.getLinkedTab().setImage( "ptchart_verysmall.png" );
		} */
		

		// MORE.......
		
		
		// Image
/*		{
			String filename = String.format( "f%07d.bmp", ptRec.getRec());
			if ( new File( Pm.getMedPath() + "/faces", filename ).exists()){
				ptImage.setContent( ImageHelper.showImage( Pm.getMedPath() + "/faces/" + filename, 125, 150 ));
			}
		}
*/		
		
		
		
		
		// build patient care log
		//build_carelog();
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PT_SOAPSHT, ptRec, Pm.getUserRec(), null, null );
		

		return;
	}
	
	
	
	
	
	
	
	
	
	private void refreshMeds(){
		MedUtils.fillListbox( meds, ptRec );
		return;
	}
	
	
	private void refreshProblems(){
		ProbUtils.fillListbox( problems, ptRec );
		return;
	}
	
	
	// populate PARs and allergies
	private void refreshPARs(){
		ParUtils.fillListbox( pars, ptRec );
		return;
	}
	

	
	
	private void refreshAlerts(){
		
		ZkTools.listboxClear( lboxAlerts );
		
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		
		// vitals based alerts
		{
		Reca reca = medPt.getVitalsReca();
		if ( Reca.isValid( reca )){
			Vitals v = new Vitals( reca );
			double w = v.getWeight();
			double h = v.getHeight();
			if (( w > 0 ) && ( h > 0 )){
				double bmi = ClinicalCalculations.computeBMI( w, h );
				if ( bmi >= 30 ) ZkTools.appendToListbox( lboxAlerts, "BMI greater than 30.  Consider ordering nutritional counselling.", null );
			}
		}
		}
		
		
		// HgbA1c?
		boolean flgDiabetes = false;
		boolean flgHgbA1c = false;
		boolean flgLDL = false;
		boolean flgStatin = false;
		
		for ( Reca reca = medPt.getProbReca(); Reca.isValid( reca ); ){
			
			Prob prob = new Prob( reca );
			
			if ( prob.getProbDesc().startsWith( "Diabetes" )){
				flgDiabetes = true;
				break;
			}
				
			reca = prob.getLLHdr().getLast();
		}
		
		for ( Reca reca = medPt.getLabResultReca(); Reca.isValid( reca ); ){
			
			LabResult lab = new LabResult( reca );
			Rec orec = lab.getLabObsRec();
			LabObsTbl obs = new LabObsTbl( orec );
			
			if ( flgDiabetes && obs.getDesc().startsWith( "HgbA1c" )){
				double r = EditHelpers.parseDouble( lab.getResult());
				if ( r >= 9.0 ){
					ZkTools.appendToListbox( lboxAlerts, "Diabetic with HgbA1c >= 9.0.  Consider ordering diabetic teaching.", null );
				}
				if ( lab.getDate().compare( Date.today()) > 90 ){
					ZkTools.appendToListbox( lboxAlerts, "Diabetic without recent HgbA1c.  Consider getting HgbA1c today.", null );
				}
				flgHgbA1c = true;
				break;
			}

			if ( obs.getDesc().startsWith( "LDL")){
				if ( EditHelpers.parseInt( lab.getResult()) > 130 ){
					flgLDL = true;
				}
			}
			
			reca = lab.getLLHdr().getLast();
		}	
		
		if ( flgDiabetes && ! flgHgbA1c ){
			ZkTools.appendToListbox( lboxAlerts, "Diabetic without recent HgbA1c.  Consider getting HgbA1c today.", null );
		}
		
		for ( Reca reca = medPt.getMedsReca(); Reca.isValid( reca ); ){
			
			Cmed med = new Cmed( reca );
			String code = med.getDrugCode();
			NCFull nc = NCFull.readMedID( code );
			if ( nc != null ){
				String etc = nc.getMedEtc();
				if ( etc.toLowerCase().indexOf( "statin" ) > 0 ){
					flgStatin=true;
				}
				break;
			}

			reca = med.getLLHdr().getLast();
		}			
		
		if ( flgLDL && ! flgStatin ){
			ZkTools.appendToListbox( lboxAlerts, "LDL greater than 130.  Patient not on a statin.  Consider prescribing a statin if not contraindicated.", null );
		}
		
		
		return;
	}
	
	
	
	private void build_carelog(){
	
		// populate MrLog listbox 
		for ( Reca reca = medPt.getMrLogReca(); Reca.isValid( reca ); ){
			
			MrLog mrLog = new MrLog( reca );
			//System.out.println( "MRLOG=" + reca.toString() + "  " + mrLog.toString());
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( mrlog_listbox );
			new Listcell( mrLog.getDate().getPrintable(9)).setParent( i );
			new Listcell( mrLog.getTypeDesc() + ":  " + mrLog.getDesc()).setParent( i );
			
			//mrlog_listbox.appendItem( mrLog.getDate().getPrintable(9) + "  " + mrLog.getDesc(), "" );	
			reca = mrLog.getLLHdr().getLast();
		}

	}
	
	
	//Sheet Tab
	
	public void onSelect$tabSheet( Event e ){ 
		

		// get patient's medical record
		medPt = new MedPt( dirPt.getMedRec());
		

		ERxConfig eRx = new ERxConfig();
		eRx.read();
		//newcropManagedMeds = eRx.getNewcropManagedMeds();
		//newcropManagedPARs = eRx.getNewcropManagedPARs();
		
		

		
		
		
		
		
		refreshProblems();
		
		// register Prob list callback
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshProblems();
				return true;
			}
		}, ptRec, Notifier.Event.PROB );
		
		
		refreshMeds();
		
		// register Med list callback
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshMeds();
				return true;
			}
		}, ptRec, Notifier.Event.MED );
		
		
		refreshPARs();
		
		// register PAR list callback
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshPARs();
				return true;
			}
		}, ptRec, Notifier.Event.PAR );
		
		
		
		
		
		refreshAlerts();
		
		// Register Alert list for callbacks
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshAlerts();
				return true;
			}
		}, ptRec, Notifier.Event.PROB );
		
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshAlerts();
				return true;
			}
		}, ptRec, Notifier.Event.MED );

		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshAlerts();
				return true;
			}
		}, ptRec, Notifier.Event.LAB );
		
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshAlerts();
				return true;
			}
		}, ptRec, Notifier.Event.VITALS );

		
		
		
		
	}
	
	
	// Exam Tab

	public void onSelect$tabExam( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room")){
			
		if ( examWin == null ) examWin = build_examWin();}
		//System.out.println("window?");
	}

	private Window build_examWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		//myMap.put("Vfile", Vfile);
		myMap.put("Textbox", ExamTextbox);
		//System.out.println("here exam");
		return (Window) Executions.createComponents("phyexam.zul", gbEx, myMap );
		
	}
	 List<Object> Imps = new ArrayList<Object>();
	 Textbox Te30 = new Textbox();
	 Textbox t30 = new Textbox();
	// Assesment Tab
	
	public void onSelect$tabAssess( Event e )
	
	{ if ( Status.equals("Exam Room") || Status.equals("Triage") ){
		
		if ( assessWin == null ) assessWin = build_AssessWin();}
		//System.out.println("window?");
	}
	
	
	
	private Window build_AssessWin(){
		
		Window build_AssessWin = new Window();
		
		build_AssessWin.setParent(tpAssess);
		System.out.println("condition?: "+ noAssessment);
		if( noAssessment == true  ) {
		
		 Hbox hbox03 = new Hbox();
		 hbox03.setParent(build_AssessWin);
		
		 Groupbox gbox03 = new Groupbox();
		 gbox03.setWidth("380px");
		 gbox03.setParent(hbox03);
		 
		 new Caption ("Assessments: ").setParent(gbox03);
		
		 Hbox ButtonHbox00 = new Hbox();
		 ButtonHbox00.setParent(gbox03);
		 ButtonHbox00.setWidth(gbox03.getWidth());
		 ButtonHbox00.setPack("end");
		 
		 Button Add = new Button();
		 
		 Add.setParent(ButtonHbox00);
		 Add.setLabel("Add Assessment");
		 Add.setMold("trendy");
		 
		 Button Remove = new Button();
		 
		 Remove.setParent(ButtonHbox00);
		 Remove.setLabel("Remove Assessment:");
		 Remove.setMold("trendy");
		 
		 final Listbox Impslist = new Listbox();
		 Impslist.setParent(ButtonHbox00);
		 Impslist.setRows(1);
		 Impslist.setMold("select");
		 
		 		 
		 Grid grid01 = new Grid();
		 grid01.setParent(gbox03);
		 
		 
		 Columns Columns00 = new Columns();
		 Columns00.setParent(grid01);
		 
		 Column Column00 = new Column();
		 Column00.setWidth("23px");
		 Column00.setParent(Columns00);
		 
		 Column Column01 = new Column();
		 Column01.setParent(Columns00);
		 
		 final Rows rows01 = new Rows();
		 rows01.setParent(grid01);
		 
		 Row row10 = new Row();
		 row10.setParent(rows01);
		 		 
		 
		 Label l10 = new Label();
		 l10.setValue("1 ");
		 l10.setParent(row10);
		 
		 t30 = new Textbox();
		 t30.setRows(2);
		 t30.setCols(50);
		 t30.setParent(row10);
		 
		 tabAssess.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				t30.setFocus(true);
				
			}
			 
		 });
		 
		 Imps = new ArrayList<Object>();
		 final PhyExamWinController PhyEx = new PhyExamWinController();					 
		 
		 Add.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					
					 Impressions Imp = PhyEx.new Impressions(rows01);
					 
					 int i = 2;
					 String num = Integer.toString(Imps.size()+i);  
					 
					 Imp.createImpression(num);
					 Imps.add(Imp);		
					 
					 Listitem jj = new Listitem();
					 jj.setLabel(num);
					 jj.setParent(Impslist);
					 
					 Impslist.setSelectedItem(Impslist.getItemAtIndex(0));
					
				}});
		 	 	 
		 		 
		 Remove.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				
				if ( Impslist.getSelectedCount() < 1 ){
					try {Messagebox.show( "No Assessment is currently available to remove. ");  } catch  (InterruptedException e) { /*ignore*/ }
				return;
				}
				
				int nos = Integer.parseInt(Impslist.getSelectedItem().getLabel().toString().trim()) - 2;
								
				Object O = Imps.get(nos);
												
				((Impressions) O).deleteImpression();
				
				if ( Imps.size() > nos +1 ){
				
				Object I = Imps.get(nos+1);
				((Impressions) I).newlbl(Impslist.getSelectedItem().getLabel().toString().trim());
								
				if ( Imps.size() > (nos+2) ){
					
					for ( int i=0; i < (Imps.size()-(nos+2)); i++) {
						
						int s = Integer.parseInt(Impslist.getSelectedItem().getLabel().toString().trim())+i;
												
						Object J = Imps.get(s);
												
						int sn = s+1;
						
						((Impressions) J).newlbl(Integer.toString(sn));
												
					}
					
				}
				
				}
							
				
				if ( Impslist.getItemCount() > (nos + 1)){
										
					Impslist.removeItemAt(Impslist.getItemCount()-1);
			
				}
				
				 else if( Impslist.getItemCount() <= (nos+1)) { 
								
					Impslist.removeItemAt(nos);	
				
				}
				
				Impslist.setSelectedItem(Impslist.getItemAtIndex(Impslist.getItemCount()-1));
				Impslist.setFocus(true);
				Imps.remove(O);
				
			
			}});
				 
		 Groupbox gbox05 = new Groupbox();
		 gbox05.setParent(hbox03);
		 gbox05.setMold("3d");
				 
		 new Caption("Current Problems: ").setParent(gbox05);
		 
		 final Listbox li30 = new Listbox();
		 li30.setRows(8);
		 li30.setParent(gbox05);
		 li30.setWidth("353px");
		 
		 ProbUtils.fillListbox(li30, ptRec);
		 
		 Hbox ButtonHbox01 = new Hbox();
		 ButtonHbox01.setParent(gbox05);
		 ButtonHbox01.setWidth("353px");
		 ButtonHbox01.setPack("center");
		 ButtonHbox01.setStyle("border: 1px solid teal");
		 		 
		 Button Add01 = new Button();
		 Add01.setLabel("Add Problem");
		 Add01.setParent(ButtonHbox01);
		 Add01.setMold("trendy");
		 Add01.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				
				if ( li30.getSelectedCount() < 1 ){
				try {Messagebox.show( "No Problem is currently selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
					return;
			}		
				
				String Problem =  li30.getSelectedItem().getLabel().toString();
				
				boolean Duplicate  = false;
				
				if ( t30.getValue().contains(Problem) ){ Duplicate = true; }
				
				for ( int i=0; i < Imps.size(); i++ ){
					
				 if (((Impressions)  Imps.get(i)).text().contains(Problem)){ Duplicate = true ;}
					
				}
				
							
				if ( !Duplicate ){
				
				if ( t30.getValue().length() == 0 || t30.getValue().equals(" ") ){
					
					t30.setValue(Problem);
				}
				
								
				
				else {
				
				Impressions Imp01 = PhyEx.new Impressions(rows01);
				
				int i = 2;
				String num = Integer.toString(Imps.size()+i);  
				 
				Imp01.createImpression(num,Problem);
				Imps.add(Imp01);		
				 
				Listitem jj = new Listitem();
				jj.setLabel(num);
				jj.setParent(Impslist);
				
				}
				}
				
				else { try {Messagebox.show("An Impression with that problem already exists!");  } catch  (InterruptedException e) { /*ignore*/ }
			//	return;
				
				}
				
				
			}}); }
		 
		else if ( !noAssessment == true ) {
			
		Hbox hbox03 = new Hbox();
		hbox03.setParent(build_AssessWin);
			
		 Groupbox gbox06 = new Groupbox();
		 gbox06.setParent(hbox03);
		 gbox06.setMold("3d");
			 
		 new Caption("Assessment-Text: ").setParent(gbox06);
		 
		 Te30 = new Textbox();
		 Te30.setRows(8);
		 Te30.setParent(gbox06);
		 Te30.setWidth("353px"); 
		 Te30.setText(Assess);
		 
		}
		 
		return build_AssessWin;
		
	
		}
	
	
	// ROS Tab

	public void onSelect$tabROS( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room") || Status.equals("Triage") ){
	
		if ( rosWin == null ) rosWin = build_rosWin();}
	}

	private Window build_rosWin(){
		
		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));	
		//return (Window) Executions.createComponents("phyexam.zul", gbROS, myMap );
		return (Window) Executions.createComponents("soap_ros.zul", gbROS, myMap );
	}
	
	
	
	
	
	
	// Vitals Tab

	public void onSelect$tabVitals( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room") || Status.equals("Triage")){
			
		if ( vitalsWin == null ) vitalsWin = build_vitalsWin(); }
	}

	private Window build_vitalsWin(){
		
		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		
		//System.out.println( "in build_vitalsWin" );

		// create new vitals window in tabpanel
		return (Window) Executions.createComponents("vitals.zul", gbVi, myMap );
		
	}
	
	
	
	
	
	
	
	// RX Tab

	public void onSelect$tabRX( Event e ){
		if ( rxWin == null ) rxWin = build_rxWin();
	}

	private Window build_rxWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		//myMap.put( "newcropManagedMeds", true );

		// create new meds window in tabpanel
		//ptChartWin = (Window) Executions.createComponents("ptchart.zul", parent, myMap );
		//System.out.println( "createBox() back from creating components ptchart.zul");
		return (Window) Executions.createComponents("meds.zul", gbRX, myMap );
	}

	
	
	
	
	
	
	
	// Last Tab

	public void onSelect$tabLast( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room")){
			
		if ( lastWin == false ) lastWin = build_lastWin(); }
	}

	private boolean build_lastWin(){
		
		// pass parameters to new window
		//Map<String, Rec> myMap = new HashMap();
		//myMap.put( "ptRec", (Rec)(ptRec ));		
		//return (Window) Executions.createComponents("pars.zul", tp_pars, myMap );
		// populate last progress note
		
		lastVisitDate = new Date();
		
		soapReca = medPt.getSoapReca();
		
		if (( soapReca != null ) && ( soapReca.getRec() > 1 )){
			
			SoapNote soap = new SoapNote( soapReca );
			
			// set last visit date
			lastVisitDate = soap.getDate();

			String txt = soap.getText();
			//System.out.println( "Soap text ==>" + txt );
			
			int o = txt.indexOf( '\n' );
			String sub = txt.substring( 0, o );
			
			int a = txt.indexOf( '\n', o+1 );
			String obj = txt.substring( o+1, a );
			
			int p = txt.indexOf( '\n', a+1 );
			String ass = txt.substring( a+1, p );
			
			String plan = txt.substring( p+1, txt.length());
			
			soapTextbox.setValue( soap.getDate().getPrintable(9) + "  " + new Prov( soap.getProvRec()).getName() + "\n" + SoapNote.soapDot[0] + sub + "\r\n" + SoapNote.soapDot[1] + obj + "\r\n" + 
					SoapNote.soapDot[2] + ass + "\r\n" + SoapNote.soapDot[3] + plan );
		}

		
		
		
		// populate recent nursing/treatment notes (since last visit)
		{
		String txt = "";
		
		{
		Reca nurseReca = medPt.getNurseReca();

		if (( nurseReca != null ) && ( nurseReca.getRec() > 1 )){

			while (( nurseReca != null ) && ( nurseReca.getRec() > 1 )){
				
				Notes note = new NotesNursing( nurseReca );
				if ( note.getDate().compare( lastVisitDate ) >= 0 ){
					// add to text
					txt = txt.concat( note.getDate().getPrintable(9) + "  " + note.getNoteText() + "\n" );					
				}
				
				nurseReca = note.getLLHdr().getLast();
			}
		}
		}
		
		{
		Reca trtReca = medPt.getTrtReca();

		if (( trtReca != null ) && ( trtReca.getRec() > 1 )){

			while (( trtReca != null ) && ( trtReca.getRec() > 1 )){
				
				Notes note = new NotesTreatment( trtReca );
				if ( note.getDate().compare( lastVisitDate ) >= 0 ){
					// add to text
					txt = txt.concat( note.getDate().getPrintable(9) + "  " + note.getNoteText() + "\n" );					
				}
				
				trtReca = note.getLLHdr().getLast();
			}
		}
		}
		noteTextbox.setValue( txt );
		}
		
		
		
		// populate recent lab reports (since last visit)
		{
		String txt = "";
		
		{
		Reca labReca = medPt.getLabReca();

		if (( labReca != null ) && ( labReca.getRec() > 1 )){

			while (( labReca != null ) && ( labReca.getRec() > 1 )){
				
				Notes note = new NotesLab( labReca );
				if ( note.getDate().compare( lastVisitDate ) >= 0 ){
					// add to text
					txt = txt.concat( note.getDate().getPrintable(9) + "  " + note.getNoteText() + "\n" );					
				}
				
				labReca = note.getLLHdr().getLast();
			}
		}
		}
		
		{
		for ( Reca reca = medPt.getLabResultReca(); Reca.isValid( reca ); ){
			
			LabResult lab = new LabResult( reca );
			if ( lab.getDate().compare( lastVisitDate ) >= 0 ){				
				Rec orec = lab.getLabObsRec();
				if ( Rec.isValid( orec )){
					LabObsTbl obs = new LabObsTbl( orec );
					String s = String.format( "%s %15.15s %s %s\n", lab.getDate().getPrintable(9), obs.getDesc(), lab.getResult(), obs.getCodedUnits().getLabel());
					txt = txt.concat( s );					
				}
			}
			
			reca = lab.getLLHdr().getLast();
		}
		}
		
		labTextbox.setValue( txt );
		}
		
		
		return true;
	}
	
		
	
	
	
	

	
	// Orders Tab

	public void onSelect$tabOrders( Event e ){
		if ( ordersWin == null ) ordersWin = build_ordersWin();
	}

	private Window build_ordersWin(){
		
		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("soap_orders.zul", gbOrders, myMap );
	}
	

	
	
	
	// Charge Tab

	public void onSelect$tabCharge( Event e ){
		if ( Status.equals("End") || Status.equals("Post Visit")){
			
		if ( chargeWin == null ) chargeWin = build_chargeWin(); }
	}

	private Window build_chargeWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));

		// create new charge window in tabpanel
		return (Window) Executions.createComponents("soap_charge.zul", gbCharge, myMap );
	}
	
	
	
	// Hx Tab

	public void onSelect$tabHx( Event e ){
		if ( hxWin == null ) hxWin = build_hxWin();
	}

	private Window build_hxWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));

		// create new Hx window in tabpanel
		return (Window) Executions.createComponents("history.zul", gbHx, myMap );
	}
	
	
	
	
	
	
	
	

	
	
	
	// Summary Tab

	public void onSelect$tabSummary( Event e ){
		if ( Status.equals("End") || Status.equals("Post Visit")){
			
		if ( summaryWin == null ) summaryWin = build_summaryWin(); }
	}

	private Window build_summaryWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "provRec", (Rec)(provRec ));

		// create new charge window in tabpanel
		return (Window) Executions.createComponents("soap_summary.zul", tpSummary, myMap );
	}
	
	public void onClose$soapShtWin( Event e ) throws InterruptedException{
		//alert( "onCloset event");
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			closeWin();
		}
		return;
	}
	
	
	
	public void onClick$save( Event ev ){
		
		try {
			if ( Messagebox.show( "Do you wish to save and close this Soap? "," Save the changes?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String putData = "";
		File CurrentFile = new File ( Vfile );
		try {
			 putData = FileUtils.readFileToString( CurrentFile );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("putdata info: "+putData.toString());
		//System.out.println(CC+" ,"+Subj+" ,"+Exam+" ,"+Asses+" ,"+ROS+Vitals+" ,"+Charge);
		if ( !(CC == null) && CC.length()>1 ){
		putData = putData.replace(CC.trim(), cc.getText());}
		if ( !(Subj == null) && Subj.length()>1 ){
		putData = putData.replace(Subj.trim(), subj.getText());}
		System.out.println("Exam is: "+Exam);
		if ( !(Exam == null) && Exam.length()>1 ){
		putData = putData.replace(Exam.trim(), ExamTextbox.getText());}
		
		//System.out.println("Te30: "+t30.getText());
		StringBuilder Impressions = new StringBuilder();
		if ( noAssessment ){
		Impressions.append("1:"+t30.getText());
		Impressions.append(System.getProperty("line.separator"));
		
		for ( int j = 0; j < Imps.size() ; j++ ){
			
			Impressions.append(((Impressions) Imps.get(j)).getTextN());
			Impressions.append(System.getProperty("line.separator"));
		}
		//System.out.println("Asses is: "+Assess);}
		
		putData = putData.replace(Assess.trim(), Impressions.toString());}
		else if ( !(Assess == null) && !(noAssessment)  ) {
			putData = putData.replace(Assess.trim(), Te30.getText().trim());}
		
		if ( !(ROS == null) && ROS.length()>1 ){
		putData = putData.replace(ROS.trim(), RosTextbox.getText());}
		
		if ( !(Vitals == null) && Vitals.length()>1 ){
		putData = putData.replace(Vitals.trim(), VitalsTextbox.getText());}
		
		if ( !(FU == null) && FU.length()>1 ){
		putData = putData.replace(FU.trim(), FUTextbox.getText());}
		
		if ( !(Charge == null) && Charge.length()>1){
		putData = putData.replace(Charge.trim(), ChargeTextbox.getText());}
		
		if ( !(Orders == null) && Orders.length()>1){
		putData = putData.replace(Orders.trim(), OrdersTextbox.getText());}
		
		if ( !(RX == null) && RX.length()>1 ){
		putData = putData.replace(RX.trim(), RxTextbox.getText());}
		
		if ( !(Hx == null) && Hx.length()>1){
		putData = putData.replace(Hx.trim(), HxTextbox.getText());}
        
		System.out.println("putdata info: "+putData.toString());
		
		try {
			FileUtils.writeStringToFile(CurrentFile, putData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*try{
            String verify, putData;
          
            FileWriter fw = new FileWriter(Vfile);
            BufferedWriter bw = new BufferedWriter(fw);
           
            FileReader fr = new FileReader(Vfile);
            BufferedReader br = new BufferedReader(fr);

            while( (verify=br.readLine()) != null ){ //***editted
                       //**deleted**verify = br.readLine();**
                if(verify != null){ //***edited
                    putData = verify.replaceAll(CC, cc.getText()).replace(Subj, subj.getText())
                    .replace(Exam, ExamTextbox.getText()).replace(Asses, txtAssess.getText());
                    
                    bw.write(putData);
                }
            }
            br.close();


        }catch(IOException ex){
        ex.printStackTrace();
        }*/
		
		soapShtWin.detach();
	
	}
	
	public void onClick$cancel( Event ev ) throws InterruptedException{ onClose$soapShtWin(ev); }
	
	public void onClick$end( Event ev ){
		
		try {
			if ( Messagebox.show( "Do you wish to end this vist? ","Create Soap?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec=" );
		
		usrlib.Date date = usrlib.Date.today();
		//if ( ! date.isValid()){ DialogHelpers.Messagebox( "You must enter a valid date." ); return; }
		
		//Rec provRec = (Rec) ZkTools.getListboxSelectionValue( lboxProviders );
		if ( ! Rec.isValid( provRec )){ DialogHelpers.Messagebox( "You must select a provider." ); return; }

		String desc = cc.getText().trim();
		String subj = this.subj.getText().trim()+RosTextbox.getText().trim()+HxTextbox.getText().trim();
		String obj = VitalsTextbox.getText().trim()+ExamTextbox.getText().trim();
		String ass = "";
		if ( noAssessment ){
		StringBuilder Impressions = new StringBuilder();
		
		Impressions.append("1:"+t30.getText());
		Impressions.append(System.getProperty("line.separator"));
		
		for ( int j = 0; j < Imps.size() ; j++ ){
			
			Impressions.append(((Impressions) Imps.get(j)).getTextN());
			Impressions.append(System.getProperty("line.separator"));
		}
		
		
		 ass = Impressions.toString();}
		else {  ass = Te30.getText().trim(); }
		String plan = OrdersTextbox.getText().trim()+RxTextbox.getText().trim()+FUTextbox.getText().trim();
		
		
		
		/*// get vitals info
		boolean flgVitals = false;
		
		double temp = EditHelpers.parseDouble( txtTemp.getValue());
		if ( temp > 0 ) temp = UnitHelpers.getCelcius( temp );
		
		int hr = EditHelpers.parseInt( txtHR.getValue());
		int resp = EditHelpers.parseInt( txtResp.getValue());
		int sbp = EditHelpers.parseInt( txtSystolic.getValue());
		int dbp = EditHelpers.parseInt( txtDiastolic.getValue());
		int pO2 = EditHelpers.parseInt( txtPO2.getValue());
		
		double ht = EditHelpers.parseDouble( txtHeight.getValue());
		if ( ht > 0 ) ht = UnitHelpers.getCentimeters( ht );
		
		double wt = EditHelpers.parseDouble( txtWeight.getValue());
		if ( wt > 0 ) wt = UnitHelpers.getGramsFromPounds( wt );
		
		double hc = EditHelpers.parseDouble( txtHead.getValue());
		if ( hc > 0 ) hc = UnitHelpers.getCentimeters( hc );

		if (( temp > 0 ) || ( hr > 0 ) || ( resp > 0 ) || ( sbp > 0 ) || ( dbp > 0 ) || ( pO2 > 0 )
				|| ( ht > 0 ) || ( wt > 0 ) || ( hc > 0 )){
				flgVitals = true;
		} */
		
		SoapNote soapnt = null;
		
		//if ( operation == SoapEdit.Operation.EDIT ){			
		//	soapnt = new SoapNote( soapReca );
		//	soapnt.setNumEdits( soapnt.getNumEdits() + 1 );
		//} else {			
			soapnt = new SoapNote();
			soapnt.setPtRec( ptRec );
			soapnt.setStatus( usrlib.RecordStatus.CURRENT );
		//}
		
		soapnt.setProvRec( provRec );
		soapnt.setDate( date );
		soapnt.setDesc( desc );
		
		soapnt.setText( subj + '\n' + obj + '\n' + ass + '\n' + plan + '\n' );
		
		/*if ( operation == SoapEdit.Operation.EDIT ){
			// write edited SOAP note
			soapnt.write( soapReca );
			//TODO - modify MrLog if needed
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SOAP_EDIT, ptRec, Pm.getUserRec(), null, null );
			
		} else {*/
			// post new SOAP note
			System.out.println("soapreca before new save: "+soapReca);
			soapReca = soapnt.postNew( ptRec );
			// post to MR Log
			MrLog.postNew( ptRec, date, desc, MrLog.Types.SOAP_NOTE, soapReca );
			
			// post vitals
			//if ( flgVitals ) Vitals.postNew( ptRec, date, temp, hr, resp, sbp, dbp, pO2, ht, (int) wt, hc );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SOAP_ADD, ptRec, Pm.getUserRec(), null, null );
		//}
		
		File CurrentFile = new File ( Vfile );
		System.out.println("Current file: "+ CurrentFile.getAbsolutePath());
		CurrentFile.delete();
		
		//OfficeVisitWinController Ovwc = new OfficeVisitWinController();
		//Ovwc.onCheck$r_new(ev);
		
		soapShtWin.detach();
		
		
	}
	
/*	
	// Docs Tab

	public void onSelect$t_docs( Event e ){
		if ( docsWin == null ) docsWin = build_docsWin();
	}

	private Window build_docsWin(){
		
		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("docs.zul", tp_docs, myMap );
	}
	

	// Lab Notes Tab

	public void onSelect$t_labnotes( Event e ){
		if ( labnotesWin == null ) labnotesWin = build_labnotesWin();
	}

	private Window build_labnotesWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteType", Notes.NoteType.NOTE_TYPE_LAB );
		return (Window) Executions.createComponents("notes.zul", tp_labnotes, myMap );
	}
	
	
	

	
	
	// Nursing Notes Tab

	public void onSelect$t_nurse( Event e ){
		if ( nurseWin == null ) nurseWin = build_nurseWin();
	}

	private Window build_nurseWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteType", Notes.NoteType.NOTE_TYPE_NURSING );
		return (Window) Executions.createComponents("notes.zul", tp_nurse, myMap );
	}
	
	
	

	
	// Treatment Notes Tab

	public void onSelect$t_treat( Event e ){
		if ( treatWin == null ) treatWin = build_treatWin();
	}

	private Window build_treatWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteType", Notes.NoteType.NOTE_TYPE_TREATMENT );
		return (Window) Executions.createComponents("notes.zul", tp_treat, myMap );
	}
	
	
	

	// Imaging Reports Tab

	public void onSelect$t_image( Event e ){
		if ( imageWin == null ) imageWin = build_imageWin();
	}

	private Window build_imageWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteType", Notes.NoteType.NOTE_TYPE_XRAY );
		return (Window) Executions.createComponents("notes.zul", tp_image, myMap );
	}
	
	
	

	
	// Physical Exam Reports Tab

	public void onSelect$t_exam( Event e ){
		if ( examWin == null ) examWin = build_phyexWin();
	}

	private Window build_phyexWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		return (Window) Executions.createComponents("phyexam.zul", tp_exam, myMap );
	}
	
	
	

	
	
	// vitals Tab

	public void onSelect$tabVitals( Event e ){
		if ( vitalsWin == null ) vitalsWin = build_vitalsWin();
	}

	private Window build_vitalsWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));
		return (Window) Executions.createComponents("soap_vitals.zul", tpVitals, myMap );
	}
	
	
	
*/
	
	public void onClick$btnSaveFU( Event ev ){
		
		
		return;
	}
	
	
	
	
	
	public void onClick$ptImage( Event event ){
		alert( "Pt picture clicked!" );
	}
	
	
	


	public void onSelect$lboxProviders( Event e ){
		provRec = (Rec) ZkTools.getListboxSelectionValue( lboxProviders );
		if ( ! Rec.isValid(provRec)) provRec = new Rec( 2 );
	}

	private void closeWin() {
		
		soapShtWin.detach();
		return;
	}



}
