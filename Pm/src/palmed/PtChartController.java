package palmed;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zul.Image;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.ImageHelper;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;



public class PtChartController extends GenericForwardComposer {

	private Rec	ptRec = null;
	private Boolean soap = false;
	
	// Display fields at top of chart (ALL AUTOWIRED)
	public Window ptChartWin;		// autowired
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
	private Label ins1cmp;			// autowired
	private Label ins1id;			// autowired
	private Label ins1grp;			// autowired
	private Label ins2cmp;			// autowired
	private Label ins2id;			// autowired
	private Label ins2grp;			// autowired
		
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
	

	// Tabs and Tabpanels for main part of chart  (ALL AUTOWIRED)
	private Tab t_orders;			// autowired - tab
	private Tabpanel tp_orders;		// autowired - tabpanel
	private Tab t_problems;			// autowired - tab
	private Tabpanel tp_problems;	// autowired - tabpanel
	private Tab t_meds;				// autowired - tab
	private Tabpanel tp_meds;		// autowired - tabpanel
	private Tab t_soap;				// autowired - tab
	private Tabpanel tp_soap;		// autowired - tabpanel
	private Tab t_nurse;			// autowired - tab
	private Tabpanel tp_nurse;		// autowired - tabpanel
	private Tab t_treat;			// autowired - tab
	private Tabpanel tp_treat;		// autowired - tabpanel
	private Tab t_lab;				// autowired - tab
	private Tabpanel tp_lab;		// autowired - tabpanel
	private Tab t_labnotes;			// autowired - tab
	private Tabpanel tp_labnotes;	// autowired - tabpanel
	private Tab t_image;			// autowired - tab
	private Tabpanel tp_image;		// autowired - tabpanel
	private Tab t_docs;				// autowired - tab
	private Tabpanel tp_docs;		// autowired - tabpanel
	private Tab t_pars;				// autowired - tab
	private Tabpanel tp_pars;		// autowired - tabpanel
	private Tab t_immune;			// autowired - tab
	private Tabpanel tp_immune;		// autowired - tabpanel
	private Tab t_exam;				// autowired - tab
	private Tabpanel tp_exam;		// autowired - tabpanel
	private Tab t_follow;			// autowired - tab
	private Tabpanel tp_follow;		// autowired - tabpanel
	private Tab t_history;			// autowired - tab
	private Tabpanel tp_history;	// autowired - tabpanel
	private Tab t_vitals;			// autowired - tab
	private Tabpanel tp_vitals;		// autowired - tabpanel
	private Tab t_interv;			// autowired - tab
	private Tabpanel tp_interv;		// autowired - tabpanel
	
	
	
	// Windows for each tab opened in main part of chart  (NOT AUTOWIRED)	
	private Window ordersWin = null;		// orders Window
	private Window probsWin = null;			// problems Window
	private Window medsWin = null;			// meds Window
	private Window soapWin = null;			// soap Window
	private Window nurseWin = null;			// nurse Window
	private Window treatWin = null;			// treat Window
	private Window labWin = null;			// lab (new) Window
	private Window labnotesWin = null;		// lab notes (old) Window
	private Window imageWin = null;			// image Window
	private Window docsWin = null;			// documents Window
	private Window parsWin = null;			// pars Window
	private Window immuneWin = null;		// immunizations Window
	private Window examWin = null;			// exam Window
	private Window followWin = null;		// follow list Window
	private Window historyWin = null;			// hx (history) Window
	private Window vitalsWin = null;		// vitals Window
	private Window intervWin = null;		// interv Window

	
	
	

	private Component parent;
	
	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ soap = (Boolean) myMap.get( "soap" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		if ( soap ){
			
			t_orders.setVisible(false);
			t_meds.setVisible(false);
			t_nurse.setVisible(false);
			t_treat.setVisible(false);
			t_labnotes.setVisible(false);
			t_image.setVisible(false);
			//t_docs.setVisible(false);
			t_exam.setVisible(false);
			t_follow.setVisible(false);
			t_vitals.setVisible(false);
			t_interv.setVisible(false);
			
			
		}

		// make sure we have valid ptRec
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ptChartWinController() invalid ptRec" );
		//System.out.println( "ptRec=" + ptRec.getRec());
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		
	
		// Get some patient values and set labels
		ptrec.setValue( "" + ptRec.getRec());
		ptChartWin.setTitle( dirPt.getName().getPrintableNameLFM());
		
		
		ptssn.setValue( dirPt.getSSN());
		ptnum.setValue( dirPt.getPtNumber());
		ptsex.setValue( dirPt.getSex().getAbbr());
		ptdob.setValue( dirPt.getBirthdate().getPrintable( 9 ));
		ptage.setValue( Date.getPrintableAge( dirPt.getBirthdate()));
		
		ptRace.setValue( dirPt.getRace().getLabel());
		ptEthnicity.setValue( dirPt.getEthnicity().getLabel());
		ptLanguage.setValue( dirPt.getLanguage().getLabel());

		String Cname = "", Cname2 = "";
		
		if ((dirPt.getPpo(1).getRec() != 0) && (dirPt.getPpo(1) != null) ) {

			//Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(1) );
			Insure insure = new Insure ( dirPt.getPpoinfo(1));

			if ( !(dirPt.getPpo(1).getRec() < 2)){	
				Rec ppoRec = dirPt.getPpo(1);
				Ppo ppo = new Ppo ( ppoRec );
				 Cname = ppo.getAbbr();
			}else{Cname = insure.getCarrierName();  
				}
		ins1cmp.setValue(Cname); 
		ins1id.setValue(insure.getPolicyNumber());
		ins1grp.setValue(insure.getGroupNumber()); 
		
		} 
		if( Cname.length() < 1 ||  Cname == null ) {
		
		ins1cmp.setValue("CompanyName1");
		ins1id.setValue("idnumxxxxx");
		ins1grp.setValue("grpnumxxx");
			
		}
		
		if ((dirPt.getPpo(2).getRec() != 0) && (dirPt.getPpo(2) != null) ) {

			//Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(1) );
			Insure insure = new Insure ( dirPt.getPpoinfo(2));

			if ( !(dirPt.getPpo(2).getRec() < 2)){	
				Rec ppoRec = dirPt.getPpo(2);
				Ppo ppo = new Ppo ( ppoRec );
				Cname2 = ppo.getAbbr();
			}else{Cname2 = insure.getCarrierName();  
				}
		ins2cmp.setValue(Cname2); 
		ins2id.setValue(insure.getPolicyNumber());
		ins2grp.setValue(insure.getGroupNumber()); 
		
		}
		if( Cname2.length() < 1 || Cname2 == null){
			
		ins2cmp.setValue("CompanyName2");
		ins2id.setValue("idnumxxxxx");
		ins2grp.setValue("grpnumxxx");
				
			}
			
		
		
		
		// patient address
		{
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
		}

		
		
		//System.out.println( "chart created ptrec=" + ptRec.getRec());
		//System.out.println( "name=" + ptname.getValue());

	
		
		// Is window in a Tabpanel???
		// Get Tabpanel chart is to be placed in.
		// May have to go several layers deep due to enclosing window.
		
		Tabpanel tabpanel = null;

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
		}
		

		// MORE.......
		
		
		// Image
		{
			String filename = String.format( "f%07d.bmp", ptRec.getRec());
			if ( new File( Pm.getMedPath() + "/faces", filename ).exists()){
				ptImage.setContent( ImageHelper.showImage( Pm.getMedPath() + "/faces/" + filename, 125, 150 ));
			}
		}
		
		
		
		
		

		//ERxConfig eRx = new ERxConfig();
		//eRx.read();
		//newcropManagedMeds = eRx.getNewcropManagedMeds();
		//newcropManagedPARs = eRx.getNewcropManagedPARs();
		
		// build Med list and register Notifier callback
		refreshMedList();
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshMedList();
				return true;
			}
		}, ptRec, Notifier.Event.MED );
		
		// build Prob list and register Notifier callback
		refreshProbList();
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshProbList();
				return true;
			}
		}, ptRec, Notifier.Event.PROB );
		
		// build Par list and register Notifier callback
		refreshParList();
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshParList();
				return true;
			}
		}, ptRec, Notifier.Event.PAR );
		
		
		// build patient care log and register Notifier callback
		build_carelog();
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				build_carelog();
				return true;
			}
		}, ptRec, Notifier.Event.CARELOG );
		

		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PT_CHART, ptRec, Pm.getUserRec(), null, null );
		
		return;
	}
	
	
	
	
	public void refreshMedList(){
		MedUtils.fillListbox( meds, ptRec );
		return;
	}
		

	public void refreshProbList(){
		ProbUtils.fillListbox( problems, ptRec );
		return;
	}
	
	
	public void refreshParList(){
		ParUtils.fillListbox( pars, ptRec );
		return;
	}
	
	
	
	
	
	private void build_carelog(){
	
		//System.out.println( "ptchart - refreshing carelog");
		ZkTools.listboxClear( mrlog_listbox );
		
		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		// populate MrLog listbox 
		MrLog mrLog = null;
		
		for ( Reca reca = medPt.getMrLogReca(); reca.getRec() != 0; reca = mrLog.getLLHdr().getLast()){
			
			mrLog = new MrLog( reca );
			if ( mrLog.getStatus() != MrLog.Status.CURRENT ) continue;
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( mrlog_listbox );
			new Listcell( mrLog.getDate().getPrintable(9)).setParent( i );
			new Listcell( mrLog.getTypeDesc() + ":  " + mrLog.getDesc()).setParent( i );
		}

		return;
	}
	
	
	
	
	
	// Meds Tab

	public void onSelect$t_meds( Event e ){
		if ( medsWin != null ){ 
			medsWin.onClose();}
			medsWin = build_medsWin();
	}

	private Window build_medsWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "newcropManagedMeds", true );

		// create new meds window in tabpanel
		//ptChartWin = (Window) Executions.createComponents("ptchart.zul", parent, myMap );
		//System.out.println( "createBox() back from creating components ptchart.zul");
		return (Window) Executions.createComponents("meds.zul", tp_meds, myMap );
	}
	
	
	
	
	
	// Problems Tab

	public void onSelect$t_problems( Event e ){
		if ( probsWin == null ) probsWin = build_probsWin();
	}

	private Window build_probsWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("probs.zul", tp_problems, myMap );
	}
	
	
	
	
	
	// PARs/Allergies Tab

	public void onSelect$t_pars( Event e ){
		if ( parsWin == null ) parsWin = build_parsWin();
	}

	private Window build_parsWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("pars.zul", tp_pars, myMap );
	}
	
	
	
	
	// SOAPs/Progress Notes Tab

	public void onSelect$t_soap( Event e ){
		if ( soapWin == null ) soapWin = build_soapWin();
	}

	private Window build_soapWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("soapnt.zul", tp_soap, myMap );
	}
	
	
	
	// Docs Tab

	public void onSelect$t_docs( Event e ){
		if ( docsWin == null ) docsWin = build_docsWin();
	}

	private Window build_docsWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("docs.zul", tp_docs, myMap );
	}
	
	
	

	// Lab (new) Tab

	public void onSelect$t_lab( Event e ){
		if ( labWin == null ) labWin = build_labWin();
	}

	private Window build_labWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		return (Window) Executions.createComponents("labs.zul", tp_lab, myMap );
	}
	
	
	

	
	
	
	
	// Lab Notes  (old) Tab
	public void onSelect$t_labnotes( Event e ){
		if ( labnotesWin == null ) labnotesWin = NotesLab.createWin( ptRec, tp_labnotes );
	}

	// Nursing Notes Tab
	public void onSelect$t_nurse( Event e ){
		if ( nurseWin == null ) nurseWin = NotesNursing.createWin( ptRec, tp_nurse );
	}
	
	// Treatment Notes Tab
	public void onSelect$t_treat( Event e ){
		if ( treatWin == null ) treatWin = NotesTreatment.createWin( ptRec, tp_treat );
	}

	// Imaging Reports Tab
	public void onSelect$t_image( Event e ){
		if ( imageWin == null ) imageWin = NotesXray.createWin( ptRec, tp_image );
	}

	
	
	
	
	

	
	// Physical Exam Reports Tab

	public void onSelect$t_exam( Event e ){
		if ( examWin == null ) examWin = build_phyexWin();
	}

	private Window build_phyexWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		return (Window) Executions.createComponents("phyexam.zul", tp_exam, myMap );
	}
	
	
	

	
	
	// vitals Tab

	public void onSelect$t_vitals( Event e ){
		if ( vitalsWin == null ) vitalsWin = build_vitalsWin();
	}

	private Window build_vitalsWin(){
		
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		return (Window) Executions.createComponents("vitals.zul", tp_vitals, myMap );
	}
	
	
	
	
	// Orders Tab

	public void onSelect$t_orders( Event e ){
		if ( ordersWin == null ) ordersWin = build_ordersWin();
	}

	private Window build_ordersWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("orders.zul", tp_orders, myMap );
	}
	
	
	
	

	

	// History Tab

	public void onSelect$t_history( Event e ){
		if ( historyWin == null ) historyWin = build_historyWin();
	}

	private Window build_historyWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("history.zul", tp_history, myMap );
	}
	
	
	
	

	

	// Immunizations Tab

	public void onSelect$t_immune( Event e ){
		if ( immuneWin == null ) immuneWin = build_immuneWin();
	}

	private Window build_immuneWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("immwin.zul", tp_immune, myMap );
	}
	
	
	
	
	// Interventions Tab

	public void onSelect$t_interv( Event e ){
		if ( intervWin == null ) intervWin = build_intervWin();
	}

	private Window build_intervWin(){
		
		// pass parameters to new window
		HashMap<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));		
		return (Window) Executions.createComponents("interv.zul", tp_interv, myMap );
	}
	
	
	

	

	
	
	
	
	
	
	
	
	public void onClick$ptImage( Event event ){
		alert( "Pt picture clicked!" );
	}
	

	/**
	 * Create and Download CCR export file. 
	 */
	public void onClick$btnExportCCR( Event ev ){
		PtCCR.exportCCR( ptRec );		
	}
	
	
/*	public void exportCCR( Event ev, boolean flgEncrypt ){
		
		String contentType = "text/xml";
		String outText = null;
		
		

		// build CCR output
		String ccrText = PtCCR.create( ptRec );
		outText = ccrText;

		// compute SHA-1 digest and write to output file
		StandardStringDigester digester = new StandardStringDigester();
		digester.setAlgorithm( "SHA-1" );
		digester.setSaltSizeBytes( 0 );				
		String digest = digester.digest( ccrText );
		
		

		if ( flgEncrypt ){
			
			DirPt dirPt = new DirPt( ptRec );
			String password = dirPt.getSSN();
			if ( password.length() < 6 ){
				password = dirPt.getBirthdate().getPrintable(9);
				if ( password.length() < 6 ){
					// get another key ??
					DialogHelpers.Messagebox( "Patient has no SSN and no birthdate to use as a document password." );
					return;
				}
			}
	
			
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword( password );
			String encryptedText = textEncryptor.encrypt( ccrText );
			
			outText = encryptedText;
			contentType = "text/plain";
		}
		
		
		// who is receiving this CCR output
		int who = DialogHelpers.Optionbox( "Export CCR", "Is this CCR being provided to the patient or to another healthcare provider?", "Patient", "Other Provider", "Cancel" );
		if ( who == 3 ) return;
		
		
		// save to file or email?
		
		switch ( DialogHelpers.Optionbox( "CCR", "Would you like to save this file, or send as an email?", "Save", "E-mail", "Cancel" )){
		
		case 1:	// save as file
	        Filedownload.save( outText, contentType, "ptccr.xml");
			Intervention.postNew( ptRec, Date.today(), ( who == 1 ) ? Intervention.Types.PT_ECOPY_PROVIDED: Intervention.Types.PT_TRANSFER_OUT, "CCR" );
			break;
			
		case 2:	// send as email
			
			String fname = "ptccr.xml";
			String fname1 = "ptccr.sha1";
			String addr = "";
			
			try {
				// write ccr to server file
				BufferedWriter out = new BufferedWriter( new FileWriter( fname ));
				out.write( outText );
				out.close();
				} catch ( Exception e) { 
					DialogHelpers.Messagebox( "Error writing CCR output file." );
					return;
				}
				

				try {
					// write ccr to server file
					BufferedWriter out = new BufferedWriter( new FileWriter( fname1 ));
					out.write( digest );
					out.close();
				} catch ( Exception e) { 
						DialogHelpers.Messagebox( "Error writing SHA-1 output file." );
						return;
				}

				
				// get email address to send to
				addr = DialogHelpers.Textbox( "Send CCR", "Enter the email address to send this CCR to." );
				if ( addr == null ) return;
				
				System.out.println( "Sending email" );
				System.out.println( "address=" + addr );
				System.out.println( "fname=" + fname );

				
				// send email
				String addresses[] = new String[1];
				addresses[0] = addr;
				String fnames[] = new String[2];
				fnames[0] = fname;
				fnames[1] = fname1;
				
				if ( Email.send( addresses, "Patient CCR", "Here is a patient CCR and verification SHA-1 message digest.", fnames )){
					System.out.println( "back from sending email" );
					DialogHelpers.Messagebox( "Email sent." );
					Intervention.postNew( ptRec, Date.today(), ( who == 1 ) ? Intervention.Types.PT_ECOPY_PROVIDED: Intervention.Types.PT_TRANSFER_OUT, flgEncrypt ? "Encrypted CCR": "CCR" );
					
				} else {
					DialogHelpers.Messagebox( "Email failed." );
				}


		default:	// cancel
			break;			
		}
 	}
*/
	

	
	
	
	
	
	
	
	/**
	 * Upload CCR import file. 
	 */
	public void onClick$btnImportCCR( Event ev ){
		
		CCRView.show( this.parent );
		return;
	}

	/**
	 * Upload CCR import file. 
	 */
	public void onClick$btnImportEncCCR( Event ev ){
		
		CCRView.showEncrypted( this.parent );
		return;
	}

	

	
	/**
	 * import medications and view list 
	 */
	public void onClick$btnMedReconciliation( Event ev ){

		Media media = null;
		
        try {
			media = Fileupload.get( "Select the Medication, CCR, or CCD file to upload.", "Upload Medications" );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading file." );
			return;
		}

		// get string/text data
		String str = media.getStringData();
		if ( str == null ){
			DialogHelpers.Messagebox( "File wrong content type - not text." );
			return;			
		}
		
		// is this a CCR or CCD file?
		if ( media.getContentType().equalsIgnoreCase( "text/xml" )){
			
			if ( str.indexOf( "ContinuityOfCareRecord" ) >= 0 ){
				
				System.out.println( "CCR file." );
				CCRView.show( ptChartWin, media, "ccrmeds.xsl" );
				
			} else if ( str.indexOf( "ClinicalDocument" ) >= 0 ){
				
				System.out.println( "CCD file." );
				CCRView.show( ptChartWin, media, "cdameds.xsl" );
				
			} else {
				
				DialogHelpers.Messagebox( "Unknown XML content type." );
				return;
			}

		
		} else {	// assume a text list of meds

			// pass parameters to new window
			Map<String, Object> myMap = new HashMap<String, Object>();
			myMap.put( "ptRec", (Object)(ptRec ));		
			myMap.put( "text", str );
			
			Component comp = Executions.createComponents( "medrec.zul", this.parent, myMap );
			//Iframe iframe = (Iframe) comp.getFellow( "iframe" );
			//if (( iframe == null ) || !( iframe instanceof Iframe ))
			//	SystemHelpers.seriousError( "could not get iframe" );
			//iframe.setContent( media );

			//Messagebox.show( str );
		}
		
		Intervention.postNew( ptRec, Date.today(), Intervention.Types.MEDICATION_RECONCILIATION, "" );
		
		return;
 	}
	
	

	
}
