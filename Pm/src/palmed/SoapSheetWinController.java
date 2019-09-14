package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import palmed.PhyExamWinController.Impressions;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;

public class SoapSheetWinController extends GenericForwardComposer {

	
	private Rec	ptRec = null;
	private Reca soapReca = null;
	private String Vfilef, Vfile = null;
	private String Debug = null;
	Rec provRec = new Rec( 2 );
	
	// Display fields at top of chart (ALL AUTOWIRED)
	private Window soapShtWin;
	private Component Component = null;
	private Label ptname;			// autowired - patient name
	private Label ptsex;			// autowired - pt sex
	private Label ptage;			// autowired - pt age
	private Label ptrace;			// autowired - pt age
	private Listbox meds;			// autowired - medications listbox
	private Listbox pars;			// autowired - pars listbox
	private Listbox problems;		// autowired - problems listbox
	
	private Listbox mrlog_listbox;	// autowired - MrLog listbox
	private Button end;
	
	Listbox lboxProblems2;
	Listbox lboxAssess;
	Listbox lboxAlerts;
	Listbox lboxProviders;			// autowired - provider select box
	
	private Tab tabSheet;			// autowired - tab
	private Tab tabLast;			// autowired - tab
	private Tabpanel tpLast;		// autowired - tabpanel
	private Tab tabSubject;			// autowired - tab
	private Tab tabROS;				// autowired - tab
	private Tab tabVitals;			// autowired - tab
	private Tab tabExam;			// autowired - tab
	private Tab tabAssess;			// autowired - tab
	private Tabpanel tpAssess;		// autowired - tabpanel
	private Tab tabOrders;			// autowired - tab
	private Tab tabFU;				// autowired - tab
	private Tab tabCharge;			// autowired - tab
	private Tab tabSummary;			// autowired - tab
	private Tabpanel tpSummary;		// autowired - tabpanel
	
	
	
	private boolean lastWin = false;		// Window
	private Window rosWin = null;			// Window
	private Window hxWin = null;			// Window
	private Window vitalsWin = null;		// Window
	private Window examWin = null;			// Window
	private Window assessWin = null;		// Window
	private Window ordersWin = null;		// Window
	private Window rxWin = null;			// Window
	private Window FUWin = null;			// Window
	private Window chargeWin = null;		// Window
	private Window summaryWin = null;		// Window
	private int tabint = 0;   			//Window-tab
	
	
	private Groupbox gbROS;
	private Groupbox gbHx;
	private Groupbox gbVi;
	private Groupbox gbEx;
	private Groupbox gbOrders;
	private Groupbox gbRX;
	private Groupbox gbxFU;
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
	
	
	private Textbox soapTextbox;			// autowired - last soap textbox
	private Textbox noteTextbox;			// autowired - last nursing notes
	private Textbox labTextbox;				// autowired - recent lab reports
	

	private MedPt	medPt;			// patient's medical record
	private DirPt  dirPt;
	private usrlib.Date lastVisitDate;		// last visit date (determine from last soap date for now)
	
	private String  Provider, CC , Subj,  ROS, Hx, Vitals, Exam, Assess, Orders, RX, FU, Charge, summary, Status, fStatus;
	private XMLElement Info3 = null;
	
	private boolean noAssessment = false;
	
	private Toolbarbutton ptchartbtn,docsbtn, labibtn ; 
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println( "in doAfterCompose()" );
		
		Component = component;
		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ Vfilef = (String) myMap.get( "Vfile" ); } catch ( Exception e ) { /* ignore */ };
				try{ Debug = (String) myMap.get( "Debug" ); } catch ( Exception e ) { /* ignore */ };
				
			}
		}
		
		if ( ptRec == null ) System.out.println( "ptRec == null" );
		if ( Vfilef != null ) {System.out.println("Vfile is : "+ Vfilef);}
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
		 tabLast.setDisabled(false);
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
		
		File visitfolderf = new File(Vfilef);
		
		File[] matchingfile = visitfolderf.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visitfolderf, String name){
				return name.startsWith("Visit")&& name.endsWith("xml");
			}
			
		});
		
		Vfile = matchingfile[0].getPath();
		
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
		 xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		
		
		 XMLElement Info = new XMLElement();
					
		 Info = e.getChildByName("INFO");
		 Info3 = Info;
		 //Todo = e.getChildByName("TODO");
		 Status = Info.getChildByName("Status").getContent().trim();
		 
		 
		 Status = deleteachar(Status, 0);
		
		 Status = deleteachar(Status, Status.length()-1);
		 
		 fStatus = Status;
		 
		 System.out.println("Status is: "+Status);
		 
		 if ( Status.equals("Exam Room")){
			
			 //soapShtWin.setPosition("center");
			 
			 soapShtWin.setPosition("left_top");
			 
			 tabLast.setDisabled(false);
			 tabSheet.setSelected(true);
			 //Event ev = null;
			 //onSelect$tabSheet(ev);
			 //onSelect$tabLast(null);
			 
				tabExam.setVisible(true);
				//Exam = Info.getChildByName("Exam").getContent().trim();
				// if ( !Exam.equals("QExamQ")){ ExamTextbox.setText(Exam); } else { ExamTextbox.setText(""); }

		 }else{ 
			 
			 soapShtWin.setPosition("left_top");
			 
			 tabSubject.setSelected(true); }
		 
		 if (  Status.equals("Triage") ){
		 
			 	tabVitals.setVisible(true);
				Vitals = Info.getChildByName("Vitals").getContent().trim();
				if ( !Vitals.equals("QVitalsQ")){  VitalsTextbox.setText(Vitals); } else {  VitalsTextbox.setText(""); }
				 
			 	
		 }
		 
		 if ( Status.equals("Exam Room") ){
				
			 	tabAssess.setVisible(true); 
			 	 Assess = Info.getChildByName("Assesment").getContent().trim();
			 	if ( Assess.equals("QAssessQ") || Assess.length() < 3) { noAssessment = true; }

			 	tabOrders.setVisible(true);
			 	Orders = Info.getChildByName("Orders").getContent().trim();
			 	if ( !Orders.equals("QOrdersQ")){ OrdersTextbox.setText(Orders); } else { OrdersTextbox.setText(""); }

			 	
				tabVitals.setVisible(true);
				Vitals = Info.getChildByName("Vitals").getContent().trim();
				if ( !Vitals.equals("QVitalsQ")){  VitalsTextbox.setText(Vitals); } else {  VitalsTextbox.setText(""); }
				 
				
				tabROS.setVisible(true); 
				//ROS = Info.getChildByName("ROS").getContent().trim();
				//if ( !ROS.equals("QROSQ")){ RosTextbox.setText(ROS); } else { RosTextbox.setText(""); }

		 }
		 
		 
		/* if ( Status.equals("Post Visit")){
			 	
			 	tabFU.setVisible(true);
			 	FU = Info.getChildByName("FU").getContent().trim();
			 	if ( !FU.equals("QFUQ")){ FUTextbox.setText(FU); } else { FUTextbox.setText(""); }

			 	
			    tabSummary.setVisible(true);
			    summary = Info.getChildByName("Summary").getContent().trim();
			    
			 	tabCharge.setVisible(true);
			 	Charge = Info.getChildByName("Charge").getContent().trim();
			 	if ( !Charge.equals("QChargeQ")){ ChargeTextbox.setText(Charge); } else { ChargeTextbox.setText(""); }
				
		 }*/
		 
		 if ( Status.equals("End")){
			 
			 end.setVisible(true);
			 soapShtWin.setPosition("left_top");
			 
			 tabLast.setDisabled(false);
			 tpLast.setVisible(true);
			 tabLast.setVisible(true);
						 
			 tabExam.setVisible(true);
			 //Exam = Info.getChildByName("Exam").getContent().trim();
			// if ( !Exam.equals("QExamQ")){ ExamTextbox.setText(Exam); } else { ExamTextbox.setText(""); }

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
			//ROS = Info.getChildByName("ROS").getContent().trim();
			//if ( !ROS.equals("QROSQ")){ RosTextbox.setText(ROS); } else { RosTextbox.setText(""); }
	 
			tabFU.setVisible(true);
		 	//FU = Info.getChildByName("FU").getContent().trim();
		 	//if ( !FU.equals("QFUQ")){ FUTextbox.setText(FU); } else { FUTextbox.setText(""); }

		 	
		    tabSummary.setVisible(true);
		    summary = Info.getChildByName("Summary").getContent().trim();
		    
		   		    
		 	tabCharge.setVisible(true);
		 	/*Charge = Info.getChildByName("Charge").getContent().trim();
		 	if ( !Charge.equals("QChargeQ")){ ChargeTextbox.setText(Charge); } else { ChargeTextbox.setText(""); }*/
	
			 
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
		 		 
		 
		// if ( !Hx.equals("QHxQ")){ HxTextbox.setText(Hx); } else { HxTextbox.setText(""); }
			
		 		 
		 if ( !RX.equals("QRXQ")){ RxTextbox.setText(RX); } else { RxTextbox.setText(""); }

		
		 
		 
		soapShtWin.setClosable(true);
		//soapShtWin.setMinimizable(true);
		soapShtWin.setSizable(true);
		soapShtWin.setMaximizable(true);
		
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
		
		ptrace.setValue( dirPt.getRace().getLabel());
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
			
		// get patient's medical record
		medPt = new MedPt( dirPt.getMedRec());
		

		ERxConfig eRx = new ERxConfig();
		eRx.read();
				
		
		
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

		
		
		
		 if ( Status.equals("Exam Room")){
			 
			 tabSheet.setSelected(true);
			 			 
		 }
		
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
			//TODO use different method for speed test else move whole method.
			//med.getDrugName();
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
	
	
	
	/*private void build_carelog(){
	
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
	*/
	
	//Sheet Tab
	
	/*public void onSelect$tabSheet( Event e ){ 
		

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
		
		
		
		
		
		//refreshAlerts();
		
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
	*/
	
	// Exam Tab

	public void onSelect$tabExam( Event e ){
		
		whichtab();
		tabint = 5;
				
		soapShtWin.setPosition("left,top");
		
		if ( Status.equals("End") || Status.equals("Exam Room")){
			
		if ( examWin == null ) examWin = build_examWin();}
		//System.out.println("window?");
	}
	
	Xmlwindow exam = null;
	private Window build_examWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap();
		myMap.put( "ptRec", ptRec );
		//myMap.put("Vfile", Vfile);
		myMap.put("Textbox", ExamTextbox);
		//System.out.println("here exam");
		//return (Window) Executions.createComponents("phyexam.zul", gbEx, myMap );
	
		Window build_examWin = new Window();
		exam = new Xmlwindow();
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
		
		String fn_config = "H&P.xml";
		String fullpath = Pm.getOvdPath() + File.separator + fn_config;
						
		try {
			reader = new FileReader( exam.MainFile(fullpath, ptRec, "Exam", Vfilef).getAbsolutePath() );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config+ "file was not found:" );
	    	
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
		
		build_examWin =  exam.setXmlWin(gbEx, exam);
		String[] Tabboxms = new String[] {"550px", "875px"};
		exam.initializexml( Tabboxms, "PAGE", "SYSTEM", "FINDING",  xml, false, "","",true,"","CODE");
		exam.createxml();	
		
		
		return build_examWin;
		
		
	}

	boolean boolass = true;
	Button Bass;
	// Assesment Tab
	
	public void onSelect$tabAssess( Event e ){
		
		whichtab();
		boolass = true;
		if ( Status.equals("Exam Room") || Status.equals("Triage") || Status.equals("End") ){
		
		if ( assessWin == null ) assessWin = build_AssessWin();}
		//System.out.println("window?");
	}
	
	Assessment assess = new Assessment();
	
	private Window build_AssessWin(){
							
		Window build_AssessWin = new Window();
		build_AssessWin = assess.setAssessvar(tpAssess,0);
		
		assess.createassessment(noAssessment, ptRec, Assess, true);
		
		assess.setfocus(tabAssess);
		
		return build_AssessWin;
			
		}
	
	/* public void onDoubleClick( Event ev ){ 
		 
		 System.out.println("At doubleclick");
		Bass.getEventHandler(Events.ON_CLICK);
		 
		 
	 }*/
	 
	 
	// FU Tab

	public void onSelect$tabFU( Event e ){
		
		whichtab();
		tabint = 9;
		if ( Status.equals("End") || Status.equals("Exam Room") || Status.equals("Triage") ){
	
		if ( FUWin == null ) FUWin = build_FUWin();}
	}

	
	Xmlwindow FUF = null;
	private Window build_FUWin(){
					
		Window build_FUWin = new Window();
		FUF = new Xmlwindow();
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
		
		String fn_config = "FUF.xml";
		String fullpath = Pm.getOvdPath() + File.separator + fn_config;
						
		try {
			reader = new FileReader( FUF.MainFile(fullpath, ptRec, "FU", Vfilef).getAbsolutePath() );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config+ "file was not found:" );
	    	
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
		
		build_FUWin =  FUF.setXmlWin(gbxFU, FUF);
		String[] Tabboxms = new String[] {"525px", "850px"};
		FUF.initializexml( Tabboxms, "Follow_Box", "", "Follow_Plan_Item",  xml, false, "","",true,"","Status");
		FUF.createxml();	
		
		
		return build_FUWin;	
	
	}
	
		
	
	// ROS Tab

	public void onSelect$tabROS( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room") || Status.equals("Triage") ){
		
		whichtab();
		tabint = 4;
		if ( rosWin == null ) rosWin = build_rosWin();}
	}

	
	Xmlwindow rosF = null;
	private Window build_rosWin(){
		
		// pass parameters to new window
		Map<String, Rec> myMap = new HashMap();
		myMap.put( "ptRec", (Rec)(ptRec ));	
		//return (Window) Executions.createComponents("phyexam.zul", gbROS, myMap );
		//return (Window) Executions.createComponents("soap_ros.zul", gbROS, myMap );
		
		
		Window build_rosWin = new Window();
		rosF = new Xmlwindow();
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
		String fn_config = "SoapROSF.xml";
		String fullpath = Pm.getOvdPath() + File.separator + fn_config;
		
		
		try {
			reader = new FileReader( rosF.MainFile(fullpath, ptRec, "ROS", Vfilef).getAbsolutePath() );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config+ "file was not found:" );
	    	
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
		
		
	
		
		build_rosWin =  rosF.setXmlWin(gbROS, rosF);
		String[] Tabboxms = new String[] {"485px","875px"};
		rosF.initializexml(Tabboxms, "ROS_tab", "ROS_findingBox", "ROS_findingBoxSub", xml, true, "ROS_General", "ROS_Tabs", true,"ROS_finding","Status");
		rosF.createxml();	
		
		
		return build_rosWin;
	}
	
	
	
	
	
	
	// Vitals Tab

	public void onSelect$tabVitals( Event e ){
		if ( Status.equals("End") || Status.equals("Exam Room") || Status.equals("Triage")){
		
		whichtab();	
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
		whichtab();
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

	
	
	
	
	
	//Sheet tab select 
	public void onSelect$tabSheet( Event e ){
		
		whichtab();}
	
	
	
	// Last Tab
	public void onSelect$tabLast( Event e ){
		
		whichtab();
		if ( lastWin == false ) lastWin = build_lastWin(); }
	

	private boolean build_lastWin(){
		
		// pass parameters to new window
		//Map<String, Rec> myMap = new HashMap();
		//myMap.put( "ptRec", (Rec)(ptRec ));		
		//return (Window) Executions.createComponents("pars.zul", tp_pars, myMap );
		// populate last progress note
		
		lastVisitDate = new Date();
		
		if (!( medPt.getSoapReca() == null) ){
		soapReca = medPt.getSoapReca(); }
		
		if (( soapReca != null ) && ( soapReca.getRec() > 1 )){
			
			SoapNote soap = new SoapNote( soapReca );
			
			// set last visit date
			lastVisitDate = soap.getDate();

			String txt = soap.getText();
			//System.out.println( "Soap text ==>" + txt );
			
		
			String sub = "";
			int o = txt.indexOf( '\n' );
			
			if ( o > 0 ){
				
			//System.out.println("length and index: "+ txt.length()+", "+ o);
					
			sub = txt.substring( 0, o ); 
						
			int a = txt.indexOf( '\n', o+1 );
			
			String obj = txt.substring( o+1, a );
			
			int p = txt.indexOf( '\n', a+1 );
			
			String ass = txt.substring( a+1, p );
			
			String plan = txt.substring( p+1, txt.length());
			
			soapTextbox.setValue( soap.getDate().getPrintable(9) + "  " + new Prov( soap.getProvRec()).getName() + "\n" + SoapNote.soapDot[0] + sub + "\r\n" + SoapNote.soapDot[1] + obj + "\r\n" + 
					SoapNote.soapDot[2] + ass + "\r\n" + SoapNote.soapDot[3] + plan ); }
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
		whichtab();
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
		
		whichtab();
		tabint = 10;
		if ( Status.equals("End") ){
			
		if ( chargeWin == null ) chargeWin = build_chargeWin(); }
	}

	Xmlwindow ChargeW = null;
	
	private Window build_chargeWin(){
		
		Window build_chargeWin = new Window();
		ChargeW = new Xmlwindow();
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
		
		String fn_config = "ChargesF.xml";
		String fullpath = Pm.getOvdPath() + File.separator + fn_config;
						
		try {
			reader = new FileReader( ChargeW.MainFile(fullpath, ptRec, "Charge", Vfilef).getAbsolutePath() );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config+ "file was not found:" );
	    	
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
		
		build_chargeWin =  ChargeW.setXmlWin(gbCharge, ChargeW);
		String[] Tabboxms = new String[] {"525px", "850px"};
		ChargeW.initializexml( Tabboxms, "CHG_TAB", "CHG_TAB_BOX", "CHG_TAB_BOX_sub",  xml, false, "","",true,"","Status");
		ChargeW.createxml();	
		
		
		return build_chargeWin;	
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
			
		whichtab();	
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
		closeWin();
		/*if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			closeWin();
		}*/
		return;
	}
	
	public void onClick$labibtn( Event ev ){ 
		
		final Window PtChartwin = new Window();	
		
		//labintWin
		PtChartwin.setParent(Component);
		PtChartwin.setPosition("center");
		
		PtChartwin.setHeight("922px");
		PtChartwin.setWidth("1013px");
		PtChartwin.setVflex("1");
		PtChartwin.setHflex("1");
		PtChartwin.setBorder("normal");
		
		PtChartwin.setTitle("Labint-Window");
		PtChartwin.setClosable(true);
		PtChartwin.setMaximizable(true);
		
		//PtChartwin.doPopup();
				
		 Vbox  MVbox = new Vbox();
		 MVbox.setHeight("857px");
		 MVbox.setWidth("991px");
		 MVbox.setParent(PtChartwin);
		 
		 Groupbox PtGroupbox = new Groupbox();
		 PtGroupbox.setHflex( "1" );
		 PtGroupbox.setVflex( "1" );
		 PtGroupbox.setStyle("overflow:auto");
		 PtGroupbox.setMold("3d");
		 PtGroupbox.setParent( MVbox );
		 
		 Labintwin.show( ptRec, PtGroupbox );
		 

			Hbox SecondHbox = new Hbox();
			SecondHbox.setHeight("26px");
			SecondHbox.setWidth("950px");
			SecondHbox.setPack("end");
			SecondHbox.setParent(PtChartwin);
			
			
			
			Div end = new Div();
			end.setParent(SecondHbox);
			end.setStyle("text-align: right");
			end.setAlign("end");
			
			//SecondHbox.setAlign("end");
					
			Button Close = new Button();
			Close.setParent(end);
			Close.setLabel("Close");
			Close.setWidth("105px");
			Close.setHeight("23px");
			Close.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					
					//if ( Messagebox.show( "Do you wish to close the PtChart? "," Close the PtChart ?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
					PtChartwin.detach();
					
					
				}
				
			});	
			
			
			try {
				PtChartwin.doModal();
			} catch (SuspendNotAllowedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 
		 
		
	
	}
	
	public void onClick$docsbtn( Event ev ){ 
		
		
		Window PtChartwin = new Window();	
		
		
		//labintWin
		PtChartwin.setParent(Component);
		PtChartwin.setPosition("center");
		
		PtChartwin.setHeight("922px");
		PtChartwin.setWidth("1013px");
		PtChartwin.setVflex("1");
		PtChartwin.setHflex("1");
		PtChartwin.setBorder("normal");
		
		PtChartwin.setTitle("Docs-Window");
		PtChartwin.setClosable(true);
		PtChartwin.setMaximizable(true);
		
		//PtChartwin.doPopup();
				
		 Vbox  MVbox = new Vbox();
		 MVbox.setHeight("857px");
		 MVbox.setWidth("991px");
		 MVbox.setParent(PtChartwin);
		 
		 Groupbox PtGroupbox = new Groupbox();
		 PtGroupbox.setHflex( "1" );
		 PtGroupbox.setVflex( "1" );
		 PtGroupbox.setStyle("overflow:auto");
		 PtGroupbox.setMold("3d");
		 PtGroupbox.setParent( MVbox );
		 
		 HashMap<String, Object> myMap = new HashMap<String, Object>();
		 myMap.put( "ptRec", (Rec)(ptRec ));
		
		 //Window PtChartwin2 = new Window();
		 PtChartwin = (Window) Executions.createComponents("docs.zul", PtGroupbox, myMap );
		
		
			
			
			try {
				PtChartwin.doModal();
			} catch (SuspendNotAllowedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	
	public void onClick$ptchartbtn( Event ev ){ 
		
		final Window PtChartwin = new Window();	
		
		//soapShtWin
		PtChartwin.setParent(Component);
		PtChartwin.setPosition("center");
		
		PtChartwin.setHeight("922px");
		PtChartwin.setWidth("1013px");
		PtChartwin.setVflex("1");
		PtChartwin.setHflex("1");
		PtChartwin.setBorder("normal");
		
		PtChartwin.setTitle("PtChart-Window");
		PtChartwin.setClosable(true);
		PtChartwin.setMaximizable(true);
		
		//PtChartwin.doPopup();
				
		 Vbox  MVbox = new Vbox();
		 MVbox.setHeight("857px");
		 MVbox.setWidth("991px");
		 MVbox.setParent(PtChartwin);
		 
		 Groupbox PtGroupbox = new Groupbox();
		 PtGroupbox.setHflex( "1" );
		 PtGroupbox.setVflex( "1" );
		 PtGroupbox.setStyle("overflow:auto");
		 PtGroupbox.setMold("3d");
		 PtGroupbox.setParent( MVbox );
		 
		// Create PtChart
		PtChart.show( ptRec, PtGroupbox, true );
		
		Hbox SecondHbox = new Hbox();
		SecondHbox.setHeight("26px");
		SecondHbox.setWidth("950px");
		SecondHbox.setPack("end");
		SecondHbox.setParent(PtChartwin);
		
		
		
		Div end = new Div();
		end.setParent(SecondHbox);
		end.setStyle("text-align: right");
		end.setAlign("end");
		
		//SecondHbox.setAlign("end");
				
		Button Close = new Button();
		Close.setParent(end);
		Close.setLabel("Close PtChart");
		Close.setWidth("105px");
		Close.setHeight("23px");
		Close.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				
				//if ( Messagebox.show( "Do you wish to close the PtChart? "," Close the PtChart ?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
				PtChartwin.detach();
				
				
			}
			
		});	
		
		
		try {
			PtChartwin.doModal();
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void onClick$save( Event ev ){
		
		save();
		
	}
	
	private void selecttabs(){
		
		if (assess == null ) {build_AssessWin();}
		if (exam == null ) {build_examWin(); }
		if (ROS == null ) {build_rosWin();}
		if (FUF == null ) {build_FUWin();}
		//build_hxWin();
		if (RxTextbox == null ) {build_rxWin();}
		if (VitalsTextbox == null ) {build_vitalsWin(); }
		if (OrdersTextbox == null ) {build_ordersWin();}
		
		
	}
	
	private void whichtab() {
		
		if ( tabint == 4) { rosF.update();     }
		
		if ( tabint == 5) { exam.update();    }
		
		if ( tabint == 9) { FUF.update();   }
		
		if ( tabint == 10) { ChargeW.update(); }
		
	
		
		
	}
		
	public void save(){	
	  /*	try {
			if ( Messagebox.show( "Do you wish to save and close this Soap? "," Save the changes?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
		String putData = "";
		File CurrentFile = new File ( Vfile );
		try {
			 putData = FileUtils.readFileToString( CurrentFile );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		selecttabs();
		
		//System.out.println("putdata info: "+putData.toString());
		System.out.println(CC+" ,"+Subj+" ,"+Exam+" ,"+Vitals+" ,"+Charge);
		if ( !(CC == null) && cc.getText().length()>1 ){
		putData = putData.replace(CC.trim(), cc.getText());}
		if ( !(Subj == null) && subj.getText().length()>1 ){
		putData = putData.replace(Subj.trim(), subj.getText());}
		
		 rosF.update(); 
		 exam.update(); 
		 FUF.update();
		 ChargeW.update(); 		
		
		System.out.println("noAssessment in save is: "+noAssessment);
		if ( !(Assess == null)) { putData = putData.replace(Assess.trim(),assess.getimpressions(noAssessment) );  }
		
		
		
		if ( !(Vitals == null) && VitalsTextbox.getText().length()>1 ){
		putData = putData.replace(Vitals.trim(), VitalsTextbox.getText());}
		
		
		if ( !(Orders == null) && OrdersTextbox.getText().length()>1){
		putData = putData.replace(Orders.trim(), OrdersTextbox.getText());}
		
		if ( !(RX == null) && RxTextbox.getText().length()>1 ){
		putData = putData.replace(RX.trim(), RxTextbox.getText());}
	
        
		System.out.println("putdata info: "+putData.toString());
		
		try {
			FileUtils.writeStringToFile(CurrentFile, putData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Info3.getChildByName("ViewStatus").setContent("QClosedQ");
		soapShtWin.detach();
	
	}
	
	public void onClick$cancel( Event ev ) throws InterruptedException{ onClose$soapShtWin(ev); }
	
	public void onClick$end( Event ev ){
		
		
		try {
			if ( Messagebox.show( "Do you wish to end this vist? ","Go to Review?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		save();
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec=" );
		
		usrlib.Date date = usrlib.Date.today();
		//if ( ! date.isValid()){ DialogHelpers.Messagebox( "You must enter a valid date." ); return; }
		
		//Rec provRec = (Rec) ZkTools.getListboxSelectionValue( lboxProviders );
		if ( ! Rec.isValid( provRec )){ DialogHelpers.Messagebox( "You must select a provider." ); return; }
		
		String ros = "  ", examstr = "   ", FUstr = "  ",chargestr = "  ", FUpstr="  ";
		if ( !(rosF == null) ) { ros = rosF.examtext(true); 
		}else { build_rosWin(); ros = rosF.examtext(true); }
		if ( !(exam == null) ) { examstr = exam.examtext(true); 
		}else { build_examWin(); examstr = exam.examtext(true); }
		if ( !(FUF == null) ) { FUstr = FUF.examtext(true); FUpstr = FUF.examtext(true);
		}else { build_FUWin(); FUstr = FUF.examtext(true);  FUpstr = FUF.examtext(true);}
		if ( !(ChargeW == null) ) { chargestr = ChargeW.examtext(true); 
		}else { build_chargeWin(); chargestr = ChargeW.examtext(true); }
		
		if ( !(assess == null) ) {
		}else { build_AssessWin(); }
		
		
		String desc = cc.getText().trim();
		String subj = this.subj.getText().trim() +". "+  ros ;
		String obj = VitalsTextbox.getText().trim()+".  "+ examstr;
		String ass = "";
		ass = assess.getimpressions(noAssessment);
		
		System.out.println("assessment is: "+ ass);
		
		String assf = ass.trim();
		assf = assf.replace("\n", "").replace(System.getProperty("line.separator"), "").replace("\r", "");
		
		
		
		String orders, rxstr;
		orders = OrdersTextbox.getText().trim();
		rxstr = RxTextbox.getText().trim();
		
		
		String ordersf, rxstrf, fustrf, subjf, fupstrf, chargestrf; 
		
		if (orders.substring(orders.length()-1).equals(".")){
			
			ordersf = orders+"  ";
		}else { ordersf = orders+".  "; }
		
		if (rxstr.substring(rxstr.length()-1).equals(".")){
			
			rxstrf = rxstr+"  ";
		}else { rxstrf = rxstr+".  "; }
		
		
		System.out.println("FUstr is: "+ FUstr);
		System.out.println("exam is: "+ examstr);
		System.out.println("ros is: "+ ros);
		System.out.println("assessmentf is: "+ assf);

		if (FUstr.substring(FUstr.length()-1).equals(".")){
	
			fustrf = FUstr+"  ";
		}else { fustrf = FUstr+".  "; }
		
		if (subj.substring(subj.length()-1).equals(".")){
			
			subjf = subj+"  ";
		}else { subjf = subj+".  "; }
		
		if (FUpstr.substring(FUpstr.length()-1).equals(".")){
			
			fupstrf = FUpstr+"  ";
		}else { fupstrf = FUpstr+".  "; }
		
		if (chargestr.substring(chargestr.length()-1).equals(".")){
			
			chargestrf = chargestr+"  ";
		}else { chargestrf = chargestr+".  "; }
		
		
		String plan = ordersf+ rxstrf + fustrf ;
		
		
		String putData = "";
		File CurrentFile = new File ( Vfile );
		try {
			 putData = FileUtils.readFileToString( CurrentFile );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		putData = putData.replace( "Q"+Status+"Q", "QReview VisitQ");
		
		putData = putData.replace( "QdescQ", desc);
		
		putData = putData.replace("QProvrecQ", provRec.toString());
		putData = putData.replace( "QdateQ", date.getPrintable());
		putData = putData.replace( "QdescQ", desc);
				
		
		putData = putData.replace( "QSubjQ", subjf);
		
		putData = putData.replace( "QObjQ", obj);
		
		putData = putData.replace( "QPAssessPQ", assf);
		
		putData = putData.replace( "QPPlanPQ", plan);
		
		putData = putData.replace( "QCCFQ", desc);
		
		putData = putData.replace( "QAssessFQ", assf);
		
		putData = putData.replace( "QFUPQ", fupstrf);
		
		putData = putData.replace( "QChargesQ", chargestrf );
		
			
		
		try {
			FileUtils.writeStringToFile(CurrentFile, putData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		boolean ready = false;
		if ( ready ){
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
		
		soapnt.setText( subjf + '\n' + obj + '\n' + assf + '\n' + plan + '\n' );
		
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
		
		//File CurrentFile = new File ( Vfile );
		//System.out.println("Current file: "+ CurrentFile.getAbsolutePath());
		//CurrentFile.delete();
		
		//OfficeVisitWinController Ovwc = new OfficeVisitWinController();
		//Ovwc.onCheck$r_new(ev);
		}
		Info3.getChildByName("ViewStatus").setContent("QClosedQ");
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
		
		System.out.println("name of file"+Info3.getName());
		
		Info3.getChildByName("ViewStatus").setContent("QClosedQ");
		soapShtWin.detach();
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

}
