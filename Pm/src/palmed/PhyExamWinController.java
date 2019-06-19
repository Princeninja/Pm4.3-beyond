package palmed;

import groovyjarjarcommonscli.CommandLine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.commons.io.FileUtils;
import org.python.antlr.ast.Print;
import org.python.apache.html.dom.HTMLBuilder;
import org.python.google.common.io.Files;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.LabelElement;

import usrlib.Date;
import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;

public class PhyExamWinController extends GenericForwardComposer {
	
	private final static String fn_config = "H&P.xml";
	
	//private Window HandP;
	private Listbox phyexListbox;		// autowired
	private Textbox phyexTextbox;		// autowired
	private Html phyexHTML;				// autowired
	private Window phyexHtmlWin;		// autowired
	private Groupbox phyexHtmlGroupbox;
	private Vbox MainVbox;
	private Tabbox Maintabbox;
	private Window phyexWin;
	
	
	private Rec	ptRec;		// patient record number
	private Reca phyReca = null ;
	
	//private String Vfile = null;
	//private String VisitString =null;
	//private File VisitFile = null;
	private Textbox ExamTextbox;
	private int textType = 0;			// text type currently displayed: 0-none, 1-text, 2-html
	
	
	

	public PhyExamWinController() {
		// TODO Auto-generated constructor stub
	}

	public PhyExamWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public PhyExamWinController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
		// TODO Auto-generated constructor stub
	}

	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Components.wireVariables( phyexHtmlWin, this );
//		Components.addForwards( phyexHtmlWin, this );

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map<String, Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				//try{ phyReca = (Reca) myMap.get( "phyReca"); } catch ( Exception e ){ /*ignore */ }
				//try{ Vfile = (String) myMap.get( "Vfile" ); } catch ( Exception e ) { /* ignore */ };
				try{ ExamTextbox = (Textbox) myMap.get( "Textbox" ); } catch ( Exception e ) { /* ignore */ };
				
			}
		}
		
	    /*  VisitFile = new File( Vfile );
		try {
			 VisitString = FileUtils.readFileToString(VisitFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			System.out.println( "Error: PhyExamWinController() invalid or null ptRec" );
			ptRec = new Rec( 2 );		// default for testing
		}

		//System.out.println( "NotesWinController() ptRec=" + ptRec.getRec());
		
		
		
//		if ( phyexHTML == null ) System.out.println( "phyexHTML not autowired" );
//		if ( phyexHtmlWin == null ) System.out.println( "phyexHtmlWin not autowired" );
//		if ( phyexTextbox == null ) System.out.println( "phyexTextbox not autowired" );

		
		// populate listbox
		refreshList();

		return;
	}
	
	
	
	
	public void refreshList(){
		
		// empty list
		ZkTools.listboxClear(phyexListbox);
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getPhyReca();
		System.out.println( "pre reca"+reca );
		
		for ( ; reca.getRec() != 0; ){
			
			PhyExam phyex = new PhyExam( reca );
			//String s = soap.toString();
			
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( phyexListbox );
			new Listcell( phyex.getDate().getPrintable(9)).setParent( i );
			new Listcell( phyex.getDesc()).setParent( i );
			new Listcell( pmUser.getUser( phyex.getUserRec())).setParent( i );
			i.setValue( reca );		// set reca into listitem for later retrieval
			
			
			// get next reca in list	
			reca = phyex.getLLHdr().getLast();
			//<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		}


		return;
	}
	
	public void onClick$edit (Event ev ){
		
	  // System.out.println("Edit was clicked");
	  
		//make sure a H&P is selected 
	  if ( phyexListbox.getSelectedCount() < 1 ){
		  try { Messagebox.show( "No item is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
	  }
	   	
	   PhyExam phyex = new PhyExam((Reca) phyexListbox.getSelectedItem().getValue());	
	
	   DirPt dirPt = new DirPt( ptRec );
	   MedPt medPt = new MedPt( dirPt.getMedRec());
	   phyReca = medPt.getPhyReca();
	   
	   final Window EHandP = new Window();	
		
		EHandP.setParent(phyexWin);
		EHandP.setPosition("center");
		
		EHandP.setHeight("702px");
		EHandP.setWidth("813px");
		EHandP.setVflex("1");
		EHandP.setHflex("1");
		EHandP.setBorder("normal");
		
		EHandP.doOverlapped();

		
		 Vbox  MVbox = new Vbox();
		 MVbox.setHeight("657px");
		 MVbox.setWidth("791px");
		 MVbox.setParent(EHandP);
		 
		 
		 Groupbox HtmlGroupbox = new Groupbox();
		 HtmlGroupbox.setHflex( "1" );
		 HtmlGroupbox.setVflex( "1" );
		 HtmlGroupbox.setStyle("overflow:auto");
		 HtmlGroupbox.setMold("3d");
		 HtmlGroupbox.setParent( MVbox );
		
		
		final Textbox Editbox = new Textbox();	
		Editbox.setRows(5);
		Editbox.setWidth("750px");
		Editbox.setHeight("600px");
		Editbox.setStyle("overflow:auto");
		Editbox.setText(phyex.getText());
		Editbox.setParent(HtmlGroupbox);
		
		
		Hbox SecondHbox = new Hbox();
		SecondHbox.setHeight("26px");
		SecondHbox.setWidth("200px");
		SecondHbox.setPack("start");
		SecondHbox.setParent(EHandP);
		
		Div end = new Div();
		end.setParent(SecondHbox);
		end.setStyle("text-align: right");
		
		Button Save = new Button();
			Save.setParent(end);
			Save.setLabel("Save");
			Save.setWidth("65px");
			Save.setHeight("23px");
			Save.addEventListener(Events.ON_CLICK, new EventListener(){
				
				public void onEvent(Event arg1) throws Exception {
					
					if ( Messagebox.show( "Are you sure you wish to save this H+P? "," Save the H+P ? ", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;  
					
					PhyExam phyexam = new PhyExam( phyReca );
					
					phyexam.setNumEdits( phyexam.getNumEdits() + 1);
					System.out.println("edits num are:"+ phyexam.getNumEdits());
					phyexam.setText( Editbox.getText() );															
					phyexam.write( phyReca );					
																
					// log the access 
					AuditLogger.recordEntry(AuditLog.Action.PHYEXAM_EDIT, ptRec, Pm.getUserRec(), null, null);
					
					refreshList();
					
					EHandP.detach();
				}
			});
		
		Button Close = new Button();
		Close.setParent(end);
		Close.setLabel("Cancel");
		Close.setWidth("65px");
		Close.setHeight("23px");
		Close.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if ( Messagebox.show( "Do you wish to close this H+P? "," Close the H+P ?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
				EHandP.detach();
				//HandP.setVisible(false);
				
			}
			
		});	
		
		
	}
	
	
	public void onSelect$phyexListbox( Event e ){
		
		PhyExam phyex = new PhyExam((Reca) phyexListbox.getSelectedItem().getValue());
		String txt = phyex.getText();
		
		int newType = txt.startsWith( "<HTML>" ) ? 2: 1;
		//System.out.println("new type is: "+ newType+"+"+textType);
		// if different type, remove old components
		if ( newType != textType ){
			
			// textType == 0 - nothing to remove
			
			if ( textType == 1 ){
				
				phyexTextbox.detach();
				phyexTextbox = null;
				
			} else if ( textType == 2 ){
				

				phyexHTML.detach();
				phyexHtmlGroupbox.detach();
				phyexHTML = null;
				phyexHtmlGroupbox = null;
			}
		}
		
		
		// if different type, make new components
		if ( newType != textType ){
			
			if ( newType == 1 ){
				
				
				phyexTextbox = new Textbox();
				phyexTextbox.setRows( 2 );
				phyexTextbox.setVflex( "1" );
				phyexTextbox.setHflex( "1" );
				phyexTextbox.setParent( phyexHtmlWin );
				
			} else {
				
				
				phyexHtmlGroupbox = new Groupbox();
				phyexHtmlGroupbox.setHflex( "1" );
				phyexHtmlGroupbox.setVflex( "1" );
				phyexHtmlGroupbox.setStyle("overflow:auto");
				phyexHtmlGroupbox.setMold("3d");
				phyexHtmlGroupbox.setParent( phyexHtmlWin );
				
				phyexHTML = new Html();
				phyexHTML.setVflex( "1" );	
				phyexHTML.setParent( phyexHtmlGroupbox );
				
			}
		}


		// set content		
		if ( newType == 1 ){
			
			phyexTextbox.setValue( txt );
			

		} else {
			
			
			phyexHTML.setContent( txt );
			
		}
		
		textType = newType;
			
	}
	
	public class Lateral{
		
		XMLElement Childkk;
		Groupbox gbox;
		Hbox hbox;
		Checkbox boxL,boxM,boxR;
		Textbox text = null;
		Label lbl;
		String info, left, right;
		
		Lateral(XMLElement Childkk, Groupbox gbox){
			
			this.Childkk = Childkk;
			this.gbox = gbox;
			
		}
		
		
		void createLateral(String left, String right){
			
			hbox = new Hbox();
			hbox.setParent(gbox);
			
			boxL = new Checkbox();
			boxL.setLabel(left);
			boxL.setParent(hbox);
			
			boxR = new Checkbox();
			boxR.setLabel(right);
			boxR.setParent(hbox);
			
			boxM = new Checkbox();
			boxM.setLabel((String) Childkk.getAttribute("ID"));
			boxM.setParent(hbox);
			
			this.info = Childkk.getChildByNumber(0).getContent();
			
			
		}
		

		void createLateral(String left, String right, int Cols, int Rows, String label){
			
			hbox = new Hbox();
			hbox.setParent(gbox);
			
			boxL = new Checkbox();
			boxL.setLabel(left);
			boxL.setParent(hbox);
			
			boxR = new Checkbox();
			boxR.setLabel(right);
			boxR.setParent(hbox);
			
			boxM = new Checkbox();
			boxM.setLabel((String) Childkk.getAttribute("ID"));
			boxM.setParent(hbox);
			
			text = new Textbox();
			text.setCols(Cols);
			text.setRows(Rows);
			text.setParent(hbox);
			
			lbl =  new Label();
			lbl.setValue(label);
			lbl.setParent(hbox);
		
			this.info = Childkk.getChildByNumber(0).getContent();
			
			
		}
		
		String getStatus() {
			String status = null;
			if(text == null ){
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = String.format(info, "Bi-lateral").trim(); }
				else if (boxR.isChecked() == true ){ status = String.format(info, "Left").trim(); }
				else if  (boxL.isChecked() == true ){ status = String.format(info, "Right").trim(); }
				
			}
			
			else {
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = String.format(info, "Bi-lateral").trim()+" "+ text.getText().trim()+lbl.getValue().trim()+".";}
				else if (boxR.isChecked() == true ){ status = String.format(info, "Left").trim()+" "+ text.getText().trim()+ lbl.getValue().trim()+".";}
				else if (boxL.isChecked() == true ){ status = String.format(info, "Right").trim()+" "+ text.getText().trim()+lbl.getValue().trim()+".";}
				 
			}
		return status;
		}
				
	}
	
	public void onClick$newnote ( Event ev ){
					
		final Window HandP = new Window();	
		
		HandP.setParent(phyexWin);
		HandP.setPosition("center");
		
		HandP.setHeight("602px"); //reduced from 702px
		HandP.setWidth("813px");
		HandP.setVflex("1");
		HandP.setHflex("1");
		HandP.setBorder("normal");
				
		HandP.doOverlapped();
		
		//Tab tab02 = new Tab();
			
		//if ( ExamTextbox != null){
			//System.out.println("Here");
			//tab02.setFocus(true); }
					
				
		 MainVbox = new Vbox();
		 MainVbox.setHeight("557px"); //reduced from 667px
		 MainVbox.setWidth("791px");
		 MainVbox.setParent(HandP);
		 
		 
		 Maintabbox = new Tabbox();
		 //Maintabbox.setVflex("1");
		 Maintabbox.setHflex("1");
		 Maintabbox.setHeight("557px"); //reduced from 667px
		 Maintabbox.setParent(MainVbox);
		 
		 Tabs tabs1 = new Tabs();
		 Maintabbox.appendChild(tabs1);
		 
		 Tabpanels tabpanels1 = new Tabpanels();
		 Maintabbox.appendChild(tabpanels1);
		 
		 Tab tab00 = new Tab();
		 tab00.setLabel("General");
		 tab00.setParent(tabs1);
			 
		 
		 Tabpanel tabpanel00 = new Tabpanel();
		 tabpanel00.setVflex("1");
		 tabpanel00.setHflex("1");
		 tabpanel00.setParent(tabpanels1);
		 
		 Hbox hbox00 = new Hbox();
		 hbox00.setParent(tabpanel00);
		
		 Groupbox gbox00 = new Groupbox();
		 gbox00.setHflex("max");
		 gbox00.setParent(hbox00);
		 
		 new Caption ("General Info: ").setParent(gbox00);
		
		 Grid grid00 = new Grid();
		 grid00.setParent(gbox00);
		 
		 Rows rows00 = new Rows();
		 rows00.setParent(grid00);
		 
		 Row row00 = new Row();
		 row00.setParent(rows00);
		 
		 		 
		 Label l00 = new Label();
		 l00.setValue("Date Patient Seen: ");
		 l00.setParent(row00);
		 
		 final Textbox t00 = new Textbox();
		 t00.setCols(10);
		 t00.setParent(row00);
		 Date Tdate = usrlib.Date.today();
		 t00.setText(Tdate.getPrintable());
		 
		 tab00.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					
					t00.setFocus(true);
					
				}
				 
				
				 
			 });
		 
		 Row row01 = new Row();
		 row01.setParent(rows00);
		 
		 Label l01 = new Label();
		 l01.setValue("Provider Abbreviation: ");
		 l01.setParent(row01);
		 
		 final Listbox li01 = new Listbox();
		 li01.setMold("select");
		 li01.setWidth("300px");
		 li01.setParent(row01);
		 
		 Prov.fillListbox(li01, true);
		 li01.removeItemAt(0);
		 
		 Row row02 = new Row();
		 row02.setParent(rows00);
		 
		 Label l02 = new Label();
		 l02.setValue("Problem or Description: ");
		 l02.setParent(row02);
		 
		 final Textbox t02 = new Textbox();
		 t02.setCols(30);
		 t02.setParent(row02);
		 
		 
		 
		 Tab tab01 = new Tab();
		 tab01.setLabel("CC/HPI");
		 tab01.setParent(tabs1);
		 
		 Tabpanel tabpanel01 = new Tabpanel();
		 tabpanel01.setVflex("1");
		 tabpanel01.setHflex("1");
		 tabpanel01.setParent(tabpanels1);
		 
		 
		 Hbox hbox01 = new Hbox();
		 hbox01.setParent(tabpanel01);
		 
		 Groupbox gbox0010 = new Groupbox();
		 gbox0010.setParent(hbox01);
		 
		 Groupbox gbox01 = new Groupbox();
		 gbox01.setParent(gbox0010);
		 
		 new Caption("Chief Complaint: ").setParent(gbox01);
		 
		 final Textbox t10 = new Textbox();
		 t10.setRows(1);
		 t10.setCols(40);
		 t10.setParent(gbox01);
		 
		 tab01.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				
				t10.setFocus(true);
			}	 
			 
			 
		 });
		 
		 Groupbox gbox02 = new Groupbox();
		 gbox02.setParent(gbox0010);
		 
		 new Caption("Present Illness: ").setParent(gbox02);
		 
		 final Textbox t11 = new Textbox();
		 t11.setRows(8);
		 t11.setCols(60);
		 t11.setParent(gbox02);
		 
		 Sex ptSex = new DirPt( ptRec ).getSex();
			String pronoun, pronoun1 ;
			if ( ptSex == Sex.FEMALE ) { pronoun = "She"; pronoun1 = "Her";}
			else { pronoun = "He"; pronoun1 = "His";}
			
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());

		String Lname = dirPt.getName().getLastName().toLowerCase().substring(0, 1).toUpperCase() + dirPt.getName().getLastName().toLowerCase().substring(1);
		
		final java.util.Calendar calendar = java.util.Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		//System.out.println("year is: "+ year);
		
		 Tab tab06 = new Tab();
		 tab06.setLabel("History");
		 tab06.setParent(tabs1);
		 
		 Tabpanel tabpanel06 = new Tabpanel();
		 tabpanel06.setVflex("1");
		 tabpanel06.setHflex("1");
		 tabpanel06.setParent(tabpanels1);
		 tabpanel06.setStyle("overflow:auto");
		 
		 Groupbox hbox06 = new Groupbox();
		 //hbox06.setClosable(true);
		 hbox06.setParent(tabpanel06);
		 Reca hxFamilyReca = medPt.getHxFamilyReca(); 
			String Familyhx = null;
			
			int sons = 0 ;
			int daughters = 0;
			
			String smokehx= "unknown" , drinkhx= "unknown" ;
			
			if ( Reca.isValid( hxFamilyReca )){
				
				HxFamily  hxfam = new HxFamily( hxFamilyReca );
				
				String fatherhx = null;
				
				HxFamily.ParentStatus status = hxfam.getFatherStatus();
				switch ( status ){
				
				case LIVING: 
					//Date fbday00 = new Date(hxfam.getYearFatherBorn());
					int fbday00 = hxfam.getYearFatherBorn();
				    //System.out.println("the year is: "+ year );
					fatherhx = Lname+ "'s father was born in "+ fbday00 +" and is alive at "+(year - fbday00)+" .";
					break;
					
				case DECEASED:
					int fbday01 = hxfam.getYearFatherBorn();
					int fdday00 = hxfam.getYearFatherDeceased();
					//System.out.println("date of father birth: "+ hxfam.getYearFatherBorn()+","+fdday00);
					fatherhx = Lname + "'s father is deceased and was "+(fdday00 - fbday01 )+" years old. The cause of death was "+hxfam.getFatherCauseText()+".";
				    break;
				    
				case UNKNOWN:
					int fbday02 = hxfam.getYearFatherBorn();
				    fatherhx = Lname + "'s father's status is unknown and he was born in the year "+fbday02 +".";
				    break;
				}
				
				String motherhx = null;	
				
				switch ( hxfam.getMotherStatus() ){
				
				case LIVING:
					int mbday00 = hxfam.getYearMotherBorn();
					//System.out.println("date of mother birth: "+ mbday00 +"," + year);
					motherhx = Lname + "'s mother was born in "+mbday00+ " and is approximately "+(year - mbday00)+ " years old.";
				    break;
				
				case DECEASED:
				    int mbday01 = hxfam.getYearMotherBorn();
				    int mdday00 = hxfam.getYearMotherDeceased();
				    motherhx = Lname + "'s mother is deceased and was about "+ (mdday00 - mbday01)+" years old. The cause of death was "+hxfam.getMotherCauseText()+".";
					break;
				
				case UNKNOWN:
					int mbday02 = hxfam.getYearMotherBorn();
					motherhx = Lname + "'s mother's status is unknown and she was born in "+ mbday02+".";
				    break;
				} 
				
				Familyhx = fatherhx+" "+motherhx+" "+Lname + " has "+hxfam.getNumBrothers()+" brothers and "+hxfam.getNumSisters()+" sisters."+ Lname + " has "+hxfam.getNumSons()+" sons and "+hxfam.getNumDaughters()+" daughters.";   
				
			
			sons = hxfam.getNumSons();
			daughters = hxfam.getNumDaughters();
			
			}
					
			//TODO use the class for the other options. 
			
			String Socialhx;
			
			//Verify
			Socialhx = Lname +"'s smoking pattern is "+smokehx +". "+ pronoun1 +" drinking pattern is " + drinkhx+". " + pronoun + " is a " + usrlib.Date.getAgeYears(dirPt.getBirthdate()) + " year old " + dirPt.getRace().toString().toLowerCase()+ ", whose ethnicity is " + dirPt.getEthnicity().toString().toLowerCase() + ". "+ pronoun + " is "+ dirPt.getMaritaltxt() + " and has "+ sons + " sons and  "+ daughters +" daughters.";   
			
		
		 if ( Familyhx == null ){ Familyhx = " No Family History is currently available "; }
		 
		 Groupbox gbox06 = new Groupbox();
		 gbox06.setParent(hbox06);
		 
		 final Textbox Fhx = new Textbox();
		 Fhx.setRows(4);
		 Fhx.setCols(50);
		 Fhx.setParent(gbox06);
		 Fhx.setText(Familyhx);
		 
		 tab06.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				Fhx.setFocus(true);
				
			}
			 
		 });
		 
		 new Caption("Family History").setParent(gbox06);
		 
		 Groupbox gbox07 = new Groupbox();
		 gbox07.setParent(hbox06);
		 
		 final Textbox Shx = new Textbox();
		 Shx.setRows(4);
		 Shx.setCols(50);
		 Shx.setParent(gbox07);
		 Shx.setText(Socialhx);
		 
		 new Caption("Social History").setParent(gbox07);
		 
		 Reca obgynReca = medPt.getHxGynReca();
		 //System.out.println("the obgyn reca is: "+obgynReca);
		 
		 Groupbox gbox08 = new Groupbox();
		 gbox08.setParent(hbox06);
		 
		 final Textbox Ghx = new Textbox();
		 Ghx.setRows(4);
		 Ghx.setCols(50);
		 Ghx.setParent(gbox08);
		 gbox08.setVisible(false);
		 
		 new Caption("Gynecological History").setParent(gbox08);
		 
		 if ( ptSex == Sex.FEMALE ){	
				
				HxObGyn obgyn = new HxObGyn( obgynReca );  
				
				String OB = null;
				String Gyn = null;
				
				if ( obgyn.getObHxUnavailable() != HxObGyn.FlagStatus.TRUE ){
					
					String comp = null;
					if (  obgyn.getObComplications() == HxObGyn.FlagStatus.TRUE ){ comp = " She has had complications with pregnancy. " ;}
					else { comp = "She hasn't had any previous complications with pregnancy. "; }  
					
					OB = pronoun+" has a gravida of "+String.valueOf( obgyn.getGravida())+", "
					+"a para of "+String.valueOf(obgyn.getPara())+"and an aborta of "+ String.valueOf( obgyn.getAborta())+"."+
					comp + obgyn.getObNoteText();
					
					
				}else { OB = pronoun+" OB histroy isn't available."; }
				
				if ( obgyn.getMenstrualHxUnavailable() != HxObGyn.FlagStatus.TRUE ){
					
					String Gyna = null;
					
					HxObGyn.MenstrualStatus status = obgyn.getMenstrualStatus();
					//System.out.println(status);
					if ( status != null ){
					switch ( status ){
					
					case PREMENARCHAL:
						Gyna = " "+pronoun +" is Pre-Menarchal and her Menstrual flow is unspecified.";
					    break;
					
					case MENSTRUATING:
					default:	
						Gyna = " She is menstruating. Her Menarche Age was "+obgyn.getMenarcheAge()+". Her menstrual flow is "+
						obgyn.getMenstrualFlow().toString().toLowerCase() +", her cramps status is "+obgyn.getFlgCramps().toString().toLowerCase()+". She has pms symptoms.";
					    break;
					    
					case POSTMENOPAUSE:	
						Gyna = " She is post menopause. Her Menarche Age was  "+obgyn.getMenarcheAge()+". Her menopause age was: "+
						obgyn.getMenopauseAge()+"Her menstrual flow is unspecified and her cramps and pms statuses are"+obgyn.getFlgCramps()
						+obgyn.getFlgPMS();
						break;
					
					case UNKNOWN:
						Gyna = "Her menstrual status is unkown.";
						break;
						
					}}
				Gyn = Gyna + obgyn.getMenstrualNoteText();
					
					
				}else { Gyn = "Her Gyn history isn't available. "; }
				
				String Sexhx = null;						
				if ( obgyn.getSexualHxUnavailable() != HxObGyn.FlagStatus.TRUE ){
					
					String shxa = " Her sexual activity is unknown.";
					
					if ( obgyn.getSexuallyActiveStatus() != null ){
					switch ( obgyn.getSexuallyActiveStatus()) {
					case ACTIVE:
						shxa = " She is sexually active.";
						break;
					case NOT_ACTIVE:
						shxa = " She is not sexually active.";
						break;
					//case 
					}
				}
					String Dys = "";
					if ((obgyn.getFlgDyspareunia() == HxObGyn.FlagStatus.TRUE) )  {Dys = "She has Dyspareunia."; }
					String Sse = "";
					if (obgyn.getFlgSameSexEncounters() == HxObGyn.FlagStatus.TRUE) { Sse = "She has same sex encounters."; }
					System.out.println("shxa is: "+ shxa);
					System.out.println("contraceptice: "+ obgyn.getContraceptionMethod() );
					System.out.println("dys is : "+Dys);
					if ( obgyn.getContraceptionMethod() != null ){
					Sexhx = shxa + "She uses "+obgyn.getContraceptionMethod().toString().toLowerCase()+" for birth control."+Dys+
					Sse+obgyn.getSexualNoteText(); }
					
					else {Sexhx = shxa + "No information on her birth control."+Dys+
						Sse+obgyn.getSexualNoteText();}
					
				}else { Sexhx = "Sexual history is not available. ";}
				
				StringBuilder Gyno = new StringBuilder();
				Gyno.append(OB+Gyn+Sexhx);
				Gyno.append(System.getProperty("line.separator"));
				 
			gbox08.setVisible(true);
			Ghx.setText(Gyno.toString()); 
			
		  }
		 
		 Groupbox gbox09 = new Groupbox();
		 gbox09.setParent(hbox06);
		 gbox09.setStyle("overflow:auto");
		 
		 final Textbox Bhx = new Textbox();
		 Bhx.setRows(4);
		 Bhx.setCols(50);
		 Bhx.setParent(gbox09);
		 Bhx.setStyle("overflow:auto");
		 
		 new Caption("Blood Information").setParent(gbox09);
		 
		 Reca hxmiscReca = medPt.getHxMiscReca();
			
			StringBuilder Bloodinfo = new StringBuilder();
											
			if ( Reca.isValid( hxmiscReca )){
				
				HxMisc hxmisc = new HxMisc( hxmiscReca );
				
				Bloodinfo.append(Lname+"'s blood type is "+hxmisc.getBloodType().toString());
				Bloodinfo.append(" "+hxmisc.getRhesusFactor().toString().toLowerCase()+". ");
				
				if (hxmisc.getFlgDeclinesBlood() != false){ Bloodinfo.append(pronoun+" will not accept a blood transfusion. ");}
				else {Bloodinfo.append(pronoun+" is willing to accept a blood  transfusion. ");}
				
				if (hxmisc.getFlgWillDonateBlood() == false) { Bloodinfo.append(pronoun+" is not willing to donate blood. "+hxmisc.getBloodNoteText()); }
				else { Bloodinfo.append( pronoun + "is willing to  donate blood. "+hxmisc.getBloodNoteText()); }
				
				if (hxmisc.getFlgTransfusionReaction() == true){ Bloodinfo.append(Lname+"had a previous reaction to a blood transfusion.");}
				else { Bloodinfo.append(Lname+" hasn't had any reactions to a blood transfusion.");}
				
			}
		Bhx.setText(Bloodinfo.toString());
		
		Groupbox gbox10 = new Groupbox();
		gbox10.setParent(hbox06);
		gbox10.setStyle("overflow:auto");
		
		final Textbox Sghx = new Textbox();
		Sghx.setRows(4);
		Sghx.setCols(50);
		Sghx.setParent(gbox10);
		Sghx.setStyle("overflow:auto");
		
		new Caption("Surgical History").setParent(gbox10);
		
         Tab tab02 = new Tab();
				 				
		 tab02.setLabel("Physical Exam");
		 tab02.setParent(tabs1);
		 
		 if ( ExamTextbox != null ){	
			 System.out.println("Here");
			 tab02.setFocus(true); }
		 
		 Tabpanel tabpanel02 = new Tabpanel();
		 tabpanel02.setVflex("1");
		 tabpanel02.setHflex("1");
		 tabpanel02.setParent(tabpanels1);
		 
		 if ( ExamTextbox != null ){	
			 System.out.println("Here");
			 tabpanel02.setFocus(true); }
		 	 
		 
		 final Xmlwindow physicalexam = new Xmlwindow();
		 physicalexam.setXmlWin(tabpanel02, physicalexam);
		 
		 XMLElement xml = new XMLElement();
		 FileReader reader = null;
			
		 
			try {
				//reader = new FileReader(new File("C:"+File.separator +"Users"+File.separator +"program"+File.separator +"Heliosworkspace"+File.separator +"Pm"+File.separator +"WebContent"+File.separator +fn_config));
				reader = new FileReader( Pm.getOvdPath() + File.separator + fn_config );
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
		    
				String Name = xml.getName();
			 xml.countChildren();
			 
			 xml.getElementByPathName(Name);
		 String[] Tabboxms = new String[] {"550px", "875px"};
		 physicalexam.initializexml( Tabboxms , "PAGE", "SYSTEM", "FINDING",  xml, false, "","",false,"","CODE");
		 physicalexam.createxml();
		 
		 	  
		 Tab tab04 = new Tab();
		 tab04.setLabel("Impressions");
		 tab04.setParent(tabs1);
		 
		 Tabpanel tabpanel04 = new Tabpanel();
		 tabpanel04.setVflex("1");
		 tabpanel04.setHflex("1");
		 tabpanel04.setParent(tabpanels1);
		 tabpanel04.setStyle("overflow:auto");
		 
		 final Assessment impressions = new Assessment();
		 
		 impressions.setAssessvar(tabpanel04, 1);
		 
		 impressions.createassessment(true, ptRec, "", false);
		 
		 impressions.setfocus(tab04);
		 
		 
		 
		 Tab tab05 = new Tab();
		 tab05.setLabel("Plan");
		 tab05.setParent(tabs1);
		 
		 Tabpanel tabpanel05 = new Tabpanel();
		 tabpanel05.setVflex("1");
		 tabpanel05.setHflex("1");
		 tabpanel05.setParent(tabpanels1);
		 
		 Hbox hbox04 = new Hbox();
		 hbox04.setParent(tabpanel05);
		 
		 Groupbox gbox04 = new Groupbox();
		 gbox04.setParent(hbox04);
		 
		 new Caption("Plan: ").setParent(gbox04);
		 
		 final Textbox t40 = new Textbox();
		 t40.setRows(8);
		 t40.setCols(60);
		 t40.setParent(gbox04);
		 
		 tab05.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
					t40.setFocus(true);	
			}
		 
		 });
		 
		Hbox SecondHbox = new Hbox();
		SecondHbox.setHeight("26px");
		SecondHbox.setWidth("200px");
		SecondHbox.setPack("start");
		SecondHbox.setParent(HandP);
		
		Div end = new Div();
		end.setParent(SecondHbox);
		end.setStyle("text-align: right");
		
		Button Save = new Button();
			Save.setParent(end);
			Save.setLabel("Save");
			Save.setWidth("65px");
			Save.setHeight("23px");
			Save.addEventListener(Events.ON_CLICK, new EventListener(){
				
				public void onEvent(Event arg1) throws Exception {
					// TODO Auto - generated method stub
					if ( ExamTextbox == null){
					if ( Messagebox.show( "Are you sure you wish to save this H+P? "," Save the H+P ? ", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;  
					
					
					if (!( li01.getSelectedIndex() >= 0 )){
						try {Messagebox.show( "Please select a Provider. ");  } catch  (InterruptedException e) { /*ignore*/ }
						return;
					}}
					
					/*for (int i=0; i < Findings.size(); i++){
						
						getfstatus(Findings.get(i));
						
					}*/
					
					
					/*for ( int j = 0; j < Imps.size() ; j++ ){
						
						((Impressions) Imps.get(j)).getText();
						
					}*/
															
					//File htmlTemplateFile = new File("C:"+File.separator +"Users"+File.separator +"program"+File.separator +"Heliosworkspace"+File.separator +"Pm"+File.separator +"WebContent"+File.separator +"H&P.html");
					File htmlTemplateFile = new File( Pm.getOvdPath() + File.separator +"H&P.html");
					String htmlString = FileUtils.readFileToString(htmlTemplateFile);
					Sex ptSex = new DirPt( ptRec ).getSex();
					if ( ptSex == Sex.FEMALE ) {}
					else {}
					
					DirPt dirPt = new DirPt( ptRec );
					String nameLFM = dirPt.getName().getPrintableNameLFM();
					
					String Lname = dirPt.getName().getLastName().toLowerCase().substring(0, 1).toUpperCase() + dirPt.getName().getLastName().toLowerCase().substring(1);
					
					String title = "History and Physical: "+nameLFM;
					String date = t00.getText();
					
					htmlString = htmlString.replace("History and Physical: %s", title);
					htmlString = htmlString.replace("</b> date </td>", "</b> "+date+" </td>");
					if ( ExamTextbox == null){
					htmlString = htmlString.replace("provider </td>", li01.getSelectedItem().getLabel().toString().trim()+" </td>");}
					htmlString = htmlString.replace("patient </td>", nameLFM+" </td>");
					htmlString = htmlString.replace("dob </td>", dirPt.getBirthdate().getPrintable()+" </td>");
					htmlString = htmlString.replace("</b> sex", "</b> "+dirPt.getSex().toString().toLowerCase().substring(0, 1).toUpperCase() + dirPt.getSex().toString().toLowerCase().substring(1));
					htmlString = htmlString.replace("</b> race", "</b> "+dirPt.getRace().toString().toLowerCase().substring(0, 1).toUpperCase() + dirPt.getRace().toString().toLowerCase().substring(1));
					htmlString = htmlString.replace("<p>chief complaint", "<p>"+t10.getText());
					htmlString = htmlString.replace("<p>illness", "<p>"+t11.getText());
										
					Tablebuilder TB00 = new Tablebuilder();
					Listbox li30 = new Listbox();
					ProbUtils.fillListbox( li30, ptRec );
					String rows00 = TB00.rows(TB00, li30);				
					String Table00 = TB00.createtable(2, rows00);
					htmlString = htmlString.replace("TableCP", Table00);
					
					Tablebuilder TB01 = new Tablebuilder();					
					Listbox li40 = new Listbox();
					MedUtils.fillListbox(li40, ptRec);
					String rows01 = TB01.rows(TB01, li40);
					String Table01 = TB01.createtable(2, rows01);
					htmlString = htmlString.replace("TableCM", Table01);
					
					Tablebuilder TB02 = new Tablebuilder();
					Listbox li41 = new Listbox();
					ParUtils.fillListbox(li41, ptRec);
					String rows02 = TB02.rows(TB02, li41);
					String Table02 = TB02.createtable(2, rows02);
					htmlString = htmlString.replace("TableAP", Table02);
					
					
					Tablebuilder TB03 = new Tablebuilder();
					Listbox li42 = new Listbox();
							
					MedPt medPt = new MedPt( dirPt.getMedRec());
					Procedure proc = null;

					for ( Reca reca = medPt.getProceduresReca(); Reca.isValid( reca ); reca = proc.getLLHdr().getLast()){
					proc = new Procedure( reca );
					
					if ( proc.getStatus() != Procedure.Status.CURRENT ) continue;
					
					Listitem i;
					(i = new Listitem()).setParent(li42);
					i.setValue( reca );
					
					String str1 = proc.getDesc();
					String str2 = proc.getNoteTxt();
					if (( str2 != null ) && ( str2.length() > 0 )) str1 = str1 + " - " + str2;
					
					new Listcell(str1+proc.getDate().getPrintable(9)).setParent( i );
					
					}
					String rows03 = TB03.rows(TB03, li42);
					String Table03 = TB03.createtable(2, rows03);
					htmlString = htmlString.replace("TableOII", Table03);
					
				   StringBuilder GynoHx = new StringBuilder();
				   GynoHx.append(System.getProperty("line.separator"));
				   GynoHx.append("<h4>Gynecologic History</h4>");
				   GynoHx.append(Ghx.getText());
				   
				   if (ptSex == Sex.FEMALE) {htmlString = htmlString.replace("Gynott", GynoHx.toString());}
				   else {htmlString = htmlString.replace("Gynott", "");}
					
					Tablebuilder TB04 = new Tablebuilder();
					Listbox li43 = new Listbox();
					Reca reca = medPt.getImmuneReca();
					
					if ( Reca.isValid( reca )) {
					
						for ( ; reca.getRec() !=0; ){
						
						Immunizations imm = new Immunizations( reca );
						if ( imm.getStatus() == Immunizations.Status.CURRENT ){
							
							Listitem i;
							(i = new Listitem()).setParent( li43 );
							new Listcell ( imm.getDesc()).setParent( i );
							
							i.setValue( reca );
						
						}	
						reca = imm.getLLHdr().getLast();
					}
					
						String rows04 = TB04.rows(TB04, li43);
						String Table04 = TB04.createtable(2, rows04);
						htmlString = htmlString.replace("TableImm", Table04);	
					}
					
					else{
					htmlString = htmlString.replace("TableImm", Lname+" has no immunization record. " ); }
										
					if (Bhx.getText().length()==0){
						StringBuilder Bloodtxt = new StringBuilder();
						Bloodtxt.append("<h5>Blood Information</h5>");
						Bloodtxt.append("No current  information for "+Lname+".");
						htmlString = htmlString.replace("Blood informationsz", Bloodtxt.toString() ); }
					
					else{
					StringBuilder Bloodtxt = new StringBuilder();
					Bloodtxt.append("<h5>Blood Information</h5>");
					Bloodtxt.append(Bhx.getText());
					htmlString = htmlString.replace("Blood informationsz", Bloodtxt.toString());}
					
					if (Sghx.getText().length()==0){
						StringBuilder Surgeries = new StringBuilder();
						Surgeries.append("<h5>Surgical History</h5>");			
						Surgeries.append("No surgical history currently available for "+Lname+".");					
						htmlString = htmlString.replace("Surgery Informationsz", Surgeries.toString()); }
					
					else{
						StringBuilder Surgeries = new StringBuilder();
						Surgeries.append("<h5>Surgical History</h5>");			
						Surgeries.append(Sghx.getText());					
						htmlString = htmlString.replace("Surgery Informationsz", Surgeries.toString()); }	 
								
					htmlString = htmlString.replace("family historysz", Fhx.getText());
					
					//TODO add years of education + occupation
								
					htmlString = htmlString.replace("social historysz", Shx.getText());
			 										
					htmlString = htmlString.replace("physical examsz", physicalexam.examtext(false));
										
					htmlString = htmlString.replace("impressionssz", impressions.getimpressions(true));
										
					htmlString = htmlString.replace("plansz", t40.getText());
					if ( ExamTextbox == null){					
					htmlString = htmlString.replace("Doctorsz", li01.getSelectedItem().getLabel().toString().trim() );}
					//File newHtmlFile = new File("C:"+File.separator +"Users"+File.separator +"program"+File.separator +"Desktop"+File.separator +"TestHtml.html");
					//FileUtils.writeStringToFile(newHtmlFile, htmlString);
				   if ( ExamTextbox == null){				
					PhyExam phyexam = new PhyExam();
					phyexam.setPtRec(ptRec);
					phyexam.setStatus( usrlib.RecordStatus.CURRENT );
					
					phyexam.setProvRec((Rec) ZkTools.getListboxSelectionValue( li01 ));
					
					Date current = new Date(date);
					phyexam.setDate(current);
					phyexam.setDesc(title);
					
					phyexam.setTextLen(htmlString.toString().length());
					phyexam.setUserRec(Pm.getUserRec());
					
					phyexam.setText( htmlString );
					Reca PhyexReca = null;
					
					PhyexReca = phyexam.postNew(ptRec);					
															
					// post vitals
					MrLog.postNew(ptRec, current, title, MrLog.Types.PHYSICAL_EXAM, PhyexReca);
					
					// log the access 
					AuditLogger.recordEntry(AuditLog.Action.PHYEXAM_ADD, ptRec, Pm.getUserRec(), null, null);									
					refreshList();
				}
					HandP.detach();
					if ( ExamTextbox == null){
					alert("H+P save routine complete!");}
				}
				
			});
			
			Button Close = new Button();
			Close.setParent(end);
			Close.setLabel("Cancel");
			Close.setWidth("65px");
			Close.setHeight("23px");
			Close.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					if ( Messagebox.show( "Do you wish to close this H+P? "," Close the H+P ?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
					HandP.detach();
										
				}
				
			});	
			
		
		
		return;
		
	}
	
	public void onClick$print (Event ev) {
		
		
		PhyExam phyex = new PhyExam((Reca) phyexListbox.getSelectedItem().getValue());
		
		String filename = "HnP"+String.valueOf(phyex.getTextLen())+".html";
		
		File HnP = new File("/tmp/"+filename);
		try {
			FileUtils.writeStringToFile(HnP, phyex.getText());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//"C:"+ File.separator +"u"+ File.separator +"med"+ File.separator + "printers"+ File.separator + 
		//for medtest
		//"/usr/bin/sh", "-c", "/usr/bin/cat /tmp/"+filename+" | /u/medtest/printers/laser.html.sh 2>/tmp/html.err"
		
		String[] Shellscpt = new String[]{"/usr/bin/sh", "-c", "/usr/bin/cat /tmp/"+filename+" | /u/med/printers/laser.html.sh"};
		ProcessBuilder pr = new ProcessBuilder(Shellscpt);
				
		Process p = null;
				
		try {
			p = pr.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		InputStream stdout = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader( stdout ));
		
		String line = "";
		 try {
			while ((line = reader.readLine()) != null)
			 {
			   System.out.println(line+" the line contents");
			    
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		//Clients.print();
		
		HnP.delete();
		
		alert("The H+P has printed!");
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
	
	public class Tablebuilder {
		
	//Tablebuilder{}	
	
	// creates a table using number of columns and the content of the rows	
	public String createtable(int cols, String rows){
				
		StringBuilder TableBuilder = new StringBuilder("<table width=\"80%\">");
		TableBuilder.append(System.getProperty("line.separator"));
		TableBuilder.append("<colgroup>");
		TableBuilder.append(System.getProperty("line.separator"));
		for (int i=0; i<cols; i++){
			TableBuilder.append("<col>");
			TableBuilder.append(System.getProperty("line.separator"));
		}
		TableBuilder.append("</colgroup>");
		TableBuilder.append(System.getProperty("line.separator"));
		TableBuilder.append(rows);
		TableBuilder.append("</table><br>");
		TableBuilder.append(System.getProperty("line.separator"));
		
		return TableBuilder.toString();
			
	}	
		
	//creates a row with two contents 
	public String[] newrow(String rowcontent1, String rowcontent2){
		
		String row1 = "<tr><td>"+rowcontent1+"</td>"; 
		
		String row2 = "<td>"+rowcontent2+"</td></tr>";
			
		return new String[]{row1, row2};
		
	}
	
	//Creates rows from all items in a listbox (2 a row)
	public String rows ( Tablebuilder TB, Listbox lbox ){
		
		List<String> rows = new ArrayList<String>();
		
		//int limit = 8;
		//if (lbox.getItemCount() < limit) { limit = lbox.getItemCount(); }
		
		List<String> contents  = new ArrayList<String>();
		
		for ( int i=0; i < lbox.getItemCount() ; i++ ){
			
			String content1 = lbox.getItemAtIndex(i).getLabel().toString();
			contents.add(content1);
		}		
		
		for ( int i = 1; i < contents.size()+ 2; i++ ){
		
		if ( i%2 == 0){	
			
		String c1 = contents.get(i-2);
		String c2 = null;
		if (contents.size()> i-1){
		c2 = contents.get(i-1);
		
		}		
		else { c2 = ""; }
					
		String[] row = TB.newrow( c1 , c2 );
			
			rows.add(row[0]);
			rows.add(row[1]);
		  }
		}
	
		StringBuilder rowss = new StringBuilder();
		
		for (int i=0; i < rows.size(); i++){			
			rowss.append(rows.get(i));
			rowss.append(System.getProperty("line.separator"));		
		}
		
		String rowsstr = rowss.toString();
		
		return rowsstr;
		
	}
	
	}	
	
	
	public class Impressions {
		
	Rows rows;
	Row row00;
	Label l00;
	Textbox t00;
	String id;
	
	Impressions(Rows rows){
		
		this.rows = rows;
	}	
		
	
	public void createImpression(String n){
		
		 row00 = new Row();
		 row00.setParent(rows);
			
		 id = (n);
		 l00 = new Label();
		 l00.setValue(id);
		 l00.setParent(row00);
		 		 
		 t00 = new Textbox();
		 t00.setRows(2);
		 t00.setCols(50);
		 t00.setParent(row00);
		 t00.setFocus(true);
		 
	}
	
	public void createImpression(String n, String Problem){
		
		row00 = new Row();
		row00.setParent(rows);
		
		id = n;
		l00 = new Label();
		l00.setValue(id);
		l00.setParent(row00);
		
		t00 = new Textbox();
		t00.setRows(2);
		t00.setCols(50);
		t00.setParent(row00);
		t00.setValue(Problem);	
		
	}
	
	public void deleteImpression(){
				
		row00.setVisible(false);
	}
	
	public void newlbl(String trim) {
		
		id = trim;
		l00.setValue(trim);
	}
	
	String text() {
				
		return t00.getValue();
		
	}
	public String getText(){
		
		String getText = "<p>"+id+":"+t00.getValue()+"</p>";
		
		return getText;
	}	
	
	public String getTextN(){
		
		String getText = id+":"+t00.getValue();
		
		return getText;
	}
	
  }
	
	//List of the tabs
	List<String> TabsList = new ArrayList<String>();
	
	
	//Gets the status of the object and the text if active. 
	public String getfstatus(Object O){
		String status = null;
		if (O instanceof Cbox){ status = ((Cbox) O).getStatus(); }
		
		else if (O instanceof Tbox){ status =((Tbox) O).getText(); }
		
		else if ( O instanceof Lateral){ status = ((Lateral) O).getStatus();}
		
		else if ( O instanceof Tab){ status = "</p><p align=\"justify\">"+"<b>"+((Tab) O).getLabel().trim()+": "+"</b>"; }
		return status;
	}

	public class Cbox {
		
		XMLElement Childkk;
		Groupbox gbox;
		Hbox hbox;
		Checkbox check;
		Textbox text = null;
		Label lbl;
		String Slabel,info;
		
		Cbox(XMLElement Childkk, Groupbox gbox){
			
		this.Childkk = Childkk;
		this.gbox = gbox;
		
		}
		
		void createCbox(){
			
			hbox = new Hbox();
			hbox.setParent(gbox);
			
			check = new Checkbox();
			check.setLabel((String)Childkk.getAttribute("ID"));
			check.setParent(hbox);
			
			this.info = Childkk.getChildByNumber(0).getContent();
			
		}
		
		void createCbox(int Cols, int Rows , String label){
			
			hbox =  new Hbox();
			hbox.setParent(gbox);
			
			
			check = new Checkbox();
			check.setLabel((String)Childkk.getAttribute("ID"));
			check.setParent(hbox);
			
			
			text = new Textbox();
			text.setParent(hbox);
			text.setCols(Cols);
			text.setRows(Rows);
			
			lbl = new Label();
			lbl.setParent(hbox);
			lbl.setValue(label);
			Slabel = label;
			
			this.info = Childkk.getChildByNumber(0).getContent();
			
			return;
		
		}
		
		
		String getStatus() {
			String status = null;
			if(text == null){
			if (check.isChecked() == true ){
				
				status = info;
				
			}
			
			else if(check.isChecked() == false) {
								
			}
			}else { 			
						
				if (check.isChecked() == true ){
										
					status = info.trim()+" "+text.getText().trim()+Slabel+".";
				}
				
				else if(check.isChecked() == false) {
									
				}
			}	
			return status;
				
		}
				
		
		void getLabel() {
			
			System.out.println(check.getLabel());
		}
			
				
	}	
	
	
	public class Tbox {
		
		XMLElement Childkk;
		Groupbox gbox;
		Textbox text;
		
		
		Tbox(XMLElement Childkk, Groupbox gbox){
			
			this.Childkk = Childkk;
			this.gbox =  gbox;		
		}
			
		void createTbox(int Cols, int Rows) {
			
			text = new Textbox();
			text.setCols(Cols);
			text.setRows(Rows);
			text.setParent(gbox);
		
		}
				
		String getText() {
			
			System.out.println(text.getText().trim());
			return text.getText();
			
		}
	}
	
	}


