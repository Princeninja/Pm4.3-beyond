package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.North;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;

public class OfficeVisitWinController extends GenericForwardComposer {
	
	private Window officevWin;
	
	private Listbox todo;
	private Menuitem btnCurrentT;
	private Menuitem btnCompletedT;
	private Menuitem btnCreateT;
	private Menuitem btnNotes;
	
	
	private org.zkoss.zul.North dashboard;
	
	private Button newvst;
	private Button modify;
	
	private Radio r_new;
	private Radio r_triage;
	private Radio r_examrm;
	private Radio r_end;
	private Radio r_done;
	private Radio r_all;
	
	private Button search;
	private Textbox srcstr;
	
	private Listbox officevListbox;
	
	private Button view;
	private Button remove;
	
	

	private Rec ptRec = null;
	
	public OfficeVisitWinController() {
		//TODO Auto-generated constructor sub
		
	}
	
	public OfficeVisitWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public OfficeVisitWinController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
		// TODO Auto-generated constructor stub
	}

	
	public void doAfterCompose (Component comp){
		try {
			super.doAfterCompose(comp);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		dashboard.setOpen(false);		
		
		refreshVisits();
       
		
		return;
	}

	// Watch for radiobutton to change
	public void onCheck$r_new( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for New visits");
	}
	
	// Watch for radiobutton to change
	public void onCheck$r_triage( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for Patients in triage");
	}
	
	// Watch for radiobutton to change
	public void onCheck$r_examrm( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for Patients in Exam Room");
	}
	
	// Watch for radiobutton to change
	public void onCheck$r_end( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for Pateints at the Post Visits");
	}
	// Watch for radiobutton to change
	public void onCheck$r_done( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for Pateints at the End of their Visits");
	}
	// Watch for radiobutton to change
	public void onCheck$r_all( Event ev ){
		refreshVisits();
		System.out.println("Refreshed for All Visits");
	}
	
	List<String> Filez = new ArrayList<String>();
	List<String> DFilez = new ArrayList<String>();
	
	//refresh the visit Listbox
	private void refreshVisits(){ refreshVisits( null );  } 
	
		private void refreshVisits( String searchString ){
		
		Filez.clear();
		DFilez.clear();
		
		boolean All = false ;
		
		String display = null;
		if ( r_new.isSelected()) display = "New" ;
		if ( r_triage.isSelected()) display = "Triage";
		if ( r_examrm.isSelected()) display = "Exam Room";
		if ( r_end.isSelected()) display = "Post Visit";
		if ( r_done.isSelected()) display = "End";
		if ( r_all.isSelected()) All = true ;
		ZkTools.listboxClear(officevListbox);
		
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		File visits = new File( Pm.getOvdPath() + File.separator + "OfficeVisits");
		
		File[] matchingfiles = visits.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visits, String name){
				return name.startsWith("Visit")&& name.endsWith("xml");
			}
			
		});
		
		
		System.out.println("number of files : " + matchingfiles.length);
		for ( int i = 0; i < matchingfiles.length; i++ ){
			
			String visit = matchingfiles[i].getPath();
			
			
			XMLElement xml = new XMLElement();
			FileReader reader = null;
			
			System.out.println("visit is: "+ visit);
			try {
				reader = new FileReader( visit );
		    } catch (FileNotFoundException e) {
		    	System.out.println( "the.." + visit + " file was not found:" );
		    	
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
			 
			 if ( NosChildren != 0 || Name != null){
			
			 e = xml.getElementByPathName(Name);
			
			 XMLElement Visit = new XMLElement();
			 XMLElement Info = new XMLElement();
			 XMLElement Todo = new XMLElement();
			 
			 
			 Visit = e.getChildByName("VISIT");
			 Info = e.getChildByName("INFO");
			 Todo = e.getChildByName("TODO");
			 
			 int fnd = 1;
			 
			 String status = Info.getChildByName("Status").getContent().trim();
			 System.out.println("status is: "+status);
			 System.out.println("second caser: "+ display );
			 
			 if ( All || display.trim().equalsIgnoreCase(status.trim())){
				
				 if (( searchString != null )
							&& (( Info.getChildByName("Name").getContent().toUpperCase().indexOf( s ) < 0 )
							&& ( Info.getChildByName("Status").getContent().toUpperCase().indexOf( s ) < 0 )
							&& ( Info.getChildByName("Type").getContent().toUpperCase().indexOf( s ) < 0 )
							&& ( Info.getChildByName("ProviderName").getContent().toUpperCase().indexOf( s ) < 0 )
							)){					
							// this one doesn't match
							fnd = 0;
						}
				
				if ( fnd > 0 ){
					
				 Filez.add(visit);
				 DFilez.add(Info.getChildByName("FreeText").getContent().trim());
				 System.out.println("HERE>.");
				// create new List item and add cells to it
					Listitem j;
					(j = new Listitem()).setParent( officevListbox );
					Rec ptrec = new Rec(Integer.parseInt(Info.getChildByName("PtRec").getContent().trim()));
					j.setValue( ptrec );
					
					Listcell Desc = new Listcell( Info.getChildByName("Name").getContent());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(j);
					
					Listcell Abbr = new Listcell( Info.getChildByName("Status").getContent() );
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent(j);
					
					Listcell C0 = new Listcell( Info.getChildByName("Type").getContent() );
					C0.setStyle("border-right:2px dotted black;");
					C0.setParent(j);
					
					Listcell C1 = new Listcell( Info.getChildByName("ProviderName").getContent() );
					C1.setStyle("border-right:2px dotted black;");
					C1.setParent(j);
					
					java.util.Calendar calendar = GregorianCalendar.getInstance(); 
					int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
					int mins = calendar.get(java.util.Calendar.MINUTE);
					//int seconds = calendar.get(java.util.Calendar.SECOND);
					System.out.println("hour,mins "+hour +","+ mins);
					//(hour+24)%24)
					String timenow = (String.format("%02d", hour))+ (String.format("%02d", mins));
					String starttime = Info.getChildByName("Start").getContent().trim();
					
					String hrs1 = timenow.substring(0, 2);
				    String min1 = timenow.substring(2, 4);
				    String hrs2 = starttime.substring(0, 2);
				    String min2 = starttime.substring(2, 4);

				    int minDiff = 0 ; int hrsDiff = 0 ;
				    // int difference = t2 - t1;
				    if (Integer.parseInt(timenow) < Integer.parseInt(starttime)) {
				         minDiff = Integer.parseInt(min2) - Integer.parseInt(min1);
				         hrsDiff = Integer.parseInt(hrs2) - Integer.parseInt(hrs1);
				        if (minDiff < 0) {
				            minDiff += 60;
				            hrsDiff--;
				        }

				        System.out.println("The difference between times is " + hrsDiff + " hours " + minDiff + " minutes.");

				    } else {
				         minDiff = Integer.parseInt(min1) - Integer.parseInt(min2);
				         hrsDiff = Integer.parseInt(hrs1) - Integer.parseInt(hrs2);
				        if (minDiff < 0) {
				            minDiff += 60;
				            hrsDiff--;
				        }

				        System.out.println("The difference between times is " + hrsDiff + " hours " + minDiff + " minutes.");
				    }
					/*int tn = Integer.parseInt(timenow);
					int st = Integer.parseInt(starttime);
					
					int fh = st/100;
					int fm = st % 100;
					
					int sh = tn/100;
					int sm = tn % 100; 
					
					SimpleDateFormat format = new SimpleDateFormat("hhmm");
					Date current = null, start = null, str2 = null;
					try {
						current = format.parse(timenow);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try {
						start = format.parse(starttime);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					DateTime jcurrent = new DateTime(current);
					DateTime jstart = new DateTime(start);
					
					int hours = 0;
					int minutes = 0;
					if ( current == null || start == null ){
					      hours = 0; minutes = 0; }
					else {	
						
					hours = Math.abs(Hours.hoursBetween(jcurrent, jstart).getHours());
					minutes = Minutes.minutesBetween(jcurrent, jstart).getMinutes(); 
					System.out.println(hours + "hours" + minutes + "minutes" );  
					
					minutes = Math.abs(minutes % 60);
					
				
					
					}
					
					/*long duration = 0 , duramin = 0;
					duration = sh - fh;
					duramin = sm - fm;*/
					
					Listcell C2 = new Listcell(  hrsDiff + " hrs " + minDiff + " mins." );
					C2.setStyle("border-right:2px dotted black;");
					C2.setParent(j);
					
					Listcell C3 = new Listcell( Info.getChildByName("Priority").getContent() );
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(j);
				}				 
				 
			 }
			 
			
			
		}
		
		 }
		System.out.println("New List size:"+ Filez.size());
		return;
		
	}

	//-- Search for matching entries in corresponding listbox
	public void onOK$srcstr( Event ev) { onClick$search( ev ); }
	
	public void onClick$search( Event ev ){

		String s = srcstr.getValue().trim();
		if ( s.length() == 0 ) s = null;
		
		
			ZkTools.listboxClear( officevListbox );		
			refreshVisits( s );
			
		return;
	}
	
	public void onClick$newvst( Event ev ){ 
		
		final String fn_config = "Officev.xml";
		
		ptRec = new Rec( PtSearch.show()); 
		if ( ptRec.getRec() < 2 ){
			return;
		}			
		
	
		final Window HandP = new Window();	
		
		HandP.setParent(officevWin);
		HandP.setPosition("center");
		
		HandP.setHeight("602px"); //reduced from 702px
		HandP.setWidth("813px");
		HandP.setVflex("1");
		HandP.setHflex("1");
		HandP.setBorder("normal");
				
		HandP.doOverlapped();
				
		
		
		 Vbox MainVbox = new Vbox();
		 MainVbox.setHeight("557px"); //reduced from 667px
		 MainVbox.setWidth("791px");
		 MainVbox.setParent(HandP);
		 
		 
		 /*Maintabbox = new Tabbox();
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
		 tabpanel00.setParent(tabpanels1); */
		 
		 Hbox hbox00 = new Hbox();
		 hbox00.setParent( MainVbox );
		
		 Groupbox gbox00 = new Groupbox();
		 gbox00.setHflex("max");
		 gbox00.setParent(hbox00);
		 
		 new Caption ("Patient Info: ").setParent(gbox00);
		
		 Grid grid00 = new Grid();
		 grid00.setParent(gbox00);
		 
		 Rows rows00 = new Rows();
		 rows00.setParent(grid00);
		 
		 Row row00 = new Row();
		 row00.setParent(rows00);
		 
		 		 
		 Label l00 = new Label();
		 l00.setValue("Visit Start time: ");
		 l00.setParent(row00);
		 
		 final Textbox t00 = new Textbox();
		 t00.setCols(10);
		 t00.setParent(row00);
		 java.util.Calendar calendar = GregorianCalendar.getInstance(); 
		 int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		 int mins = calendar.get(java.util.Calendar.MINUTE);
			
		 String timenow = (String.format("%02d", hour))+":"+ (String.format("%02d", mins));
		 final String timestrt = (String.format("%02d", hour)) + (String.format("%02d", mins));
				
		 t00.setText( timenow );
		 
		/* tab00.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					
					t00.setFocus(true);
					
				}
				 
				
				 
			 });*/
		 
		 Row row01 = new Row();
		 row01.setParent(rows00);
		 
		 final Label l01 = new Label();
		 l01.setValue("Provider: ");
		 l01.setParent(row01);
		 
		 final Listbox li01 = new Listbox();
		 li01.setMold("select");
		 li01.setWidth("300px");
		 li01.setParent(row01);
		 
		 Prov.fillListbox(li01, true);
		 li01.removeItemAt(0);
		 
		 Row row002 = new Row();
		 row002.setParent(rows00);
		 
		 Label l002 = new Label();
		 l002.setValue("Patient Status: ");
		 l002.setParent(row002);
		 
		 final Listbox li02 = new Listbox();
		 li02.setMold("select");
		 li02.setWidth("300px");
		 li02.setParent(row002);
		 
		 Listitem neww = new Listitem ("New");
		 neww.setParent(li02);
		 
		 Listitem ns = new Listitem ("Triage");
		 ns.setParent(li02);
		 
		 Listitem er = new Listitem ("Exam Room");
		 er.setParent(li02);
		 
		 Listitem pv = new Listitem ("Post Visit");
		 pv.setParent(li02);
		 
		 Listitem env = new Listitem ("End");
		 env.setParent(li02);		 
		 
		 Row row02 = new Row();
		 row02.setParent(rows00);
		 
		 Label l02 = new Label();
		 l02.setValue("Type: ");
		 l02.setParent(row02);
		 
		 final Textbox t02 = new Textbox();
		 t02.setCols(30);
		 t02.setParent(row02);
		 
		 Row row03 = new Row();
		 row03.setParent(rows00);
		 
		 Label l03 = new Label();
		 l03.setValue("Priority: ");
		 l03.setParent(row03);
		 
		 final Textbox t03 = new Textbox();
		 t03.setCols(30);
		 t03.setParent(row03);
		 
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
						
						if ( li01.getSelectedItem() == null ){
							try { Messagebox.show( "Please (Re-)Choose a Provider." ); } catch (InterruptedException e) { /*ignore*/ }
							return;
						}
						
						if ( li02.getSelectedItem() == null ){
							try { Messagebox.show( "Please (Re-)Choose a Status." ); } catch (InterruptedException e) { /*ignore*/ }
							return;
						}
						
						File VisitFile = new File( Pm.getOvdPath() + File.separator + fn_config);
						String VisitString = FileUtils.readFileToString(VisitFile);
						
						DirPt dirPt = new DirPt( ptRec );
						String nameLFM = dirPt.getName().getPrintableNameLFM();
						String Debugpath = "/"+"OfficeVisits"+"/"+"Visit"+timestrt+ptRec.getRec()+"XXXX"+".txt";
						
						VisitString = VisitString.replace("QnameQ", nameLFM );
						VisitString = VisitString.replace("QPtRecQ", Integer.toString(ptRec.getRec()));
						VisitString = VisitString.replace("QStartQ", timestrt);
						VisitString = VisitString.replace("QTypeQ", t02.getText());
						VisitString = VisitString.replace("QPriQ", t03.getText());
						VisitString = VisitString.replace("QProviderQ", Integer.toString(li01.getSelectedItem().getIndex()).toString().trim());
						VisitString = VisitString.replace("QProviderNameQ", li01.getSelectedItem().getLabel().toString().trim());
						VisitString = VisitString.replace("QStatusQ", li02.getSelectedItem().getLabel().toString().trim());
						VisitString = VisitString.replace("QFreeQ", Debugpath );
						
						String visitpath = Pm.getOvdPath()+File.separator+"OfficeVisits"+File.separator+"Visit"+timestrt+ptRec.getRec()+"XXXX"+".xml";
						File Visitfile = new File(visitpath);
						File Debugfile = new File(Pm.getOvdPath()+Debugpath);
						System.out.println(VisitString);						
						System.out.println("path: "+visitpath);
						
						String Start = "Test file, file was created at: "+ timestrt;
						try {
							FileUtils.writeStringToFile(Visitfile, VisitString);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						/*try {
							FileUtils.writeStringToFile(Debugfile, Start);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
						
						HandP.detach();
												
						//SoapSheetWin.show(visitpath, ptRec, officevWin );
						refreshVisits();
							
						
						
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
						if ( Messagebox.show( "Do you wish to close this Office Visit? "," Close the Office Visit?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
						HandP.detach();
											
					}
					
				});	
		
		
	}
	
	
	public void onClick$edit ( Event ev ){ 
		
		if ( officevListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No Visit is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}
		//get the file path
		int no = officevListbox.getSelectedIndex();
		final String Vfile = Filez.get(no);
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
	
		//System.out.println("visit is: "+ visit);
		try {
			reader = new FileReader( Vfile );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + Vfile + " file was not found:" );
	    	
	    }
		
	    //System.out.println("Edit file is: "+Vfile);
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
		 final String Status = Info.getChildByName("Status").getContent().trim();
		 final String Type  = Info.getChildByName("Type").getContent().trim();
		 final String Pri = Info.getChildByName("Priority").getContent().trim();
		 
		 final Window HandP = new Window();	
			
			HandP.setParent(officevWin);
			HandP.setPosition("center");
			
			HandP.setHeight("602px"); //reduced from 702px
			HandP.setWidth("813px");
			HandP.setVflex("1");
			HandP.setHflex("1");
			HandP.setBorder("normal");
					
			HandP.doOverlapped();
					
			
			
			 Vbox MainVbox = new Vbox();
			 MainVbox.setHeight("557px"); //reduced from 667px
			 MainVbox.setWidth("791px");
			 MainVbox.setParent(HandP);
			 
			 
			 
			 Hbox hbox00 = new Hbox();
			 hbox00.setParent( MainVbox );
			
			 Groupbox gbox00 = new Groupbox();
			 gbox00.setHflex("max");
			 gbox00.setParent(hbox00);
			 
			 new Caption ("Patient Info: ").setParent(gbox00);
			
			 Grid grid00 = new Grid();
			 grid00.setParent(gbox00);
			 
			 Rows rows00 = new Rows();
			 rows00.setParent(grid00);
			 
			 Row row01 = new Row();
			 row01.setParent(rows00);
			 
			 Label l01 = new Label();
			 l01.setValue(" Previous Status: ");
			 l01.setParent(row01);
			 
			 final Textbox t01 = new Textbox();
			 t01.setCols(30);
			 t01.setParent(row01);
			 t01.setText(Status);
			 t01.setReadonly(true);
			
			 Row row002 = new Row();
			 row002.setParent(rows00);
			 
			 Label l002 = new Label();
			 l002.setValue("New Patient Status: ");
			 l002.setParent(row002);
			 
			 final Listbox li02 = new Listbox();
			 li02.setMold("select");
			 li02.setWidth("300px");
			 li02.setParent(row002);
			 
			 Listitem neww = new Listitem ("New");
			 neww.setParent(li02);
			 
			 Listitem ns = new Listitem ("Triage");
			 ns.setParent(li02);
			 
			 Listitem er = new Listitem ("Exam Room");
			 er.setParent(li02);
			 
			 Listitem pv = new Listitem ("Post Visit");
			 pv.setParent(li02);
			 
			 Listitem env = new Listitem ("End");
			 env.setParent(li02);		
			 
			 Row row020 = new Row();
			 row020.setParent(rows00);
			 
			 Label l020 = new Label();
			 l020.setValue("Previous Type: ");
			 l020.setParent(row020);
			 
			 final Textbox t020 = new Textbox();
			 t020.setCols(30);
			 t020.setParent(row020);
			 t020.setText(Type);
			 t020.setReadonly(true);
			 
			 Row row02 = new Row();
			 row02.setParent(rows00);
			 
			 Label l02 = new Label();
			 l02.setValue("New Type(if applicable): ");
			 l02.setParent(row02);
			 
			 final Textbox t02 = new Textbox();
			 t02.setCols(30);
			 t02.setParent(row02);
			 
			 Row row030 = new Row();
			 row030.setParent(rows00);
			 
			 Label l030 = new Label();
			 l030.setValue("Priority: ");
			 l030.setParent(row030);
			 
			 final Textbox t030 = new Textbox();
			 t030.setCols(30);
			 t030.setParent(row030);
			 t030.setText(Pri);
			 t030.setReadonly(true);
			 
			 
			 Row row03 = new Row();
			 row03.setParent(rows00);
			 
			 Label l03 = new Label();
			 l03.setValue("New Priority(if applicable): ");
			 l03.setParent(row03);
			 
			 final Textbox t03 = new Textbox();
			 t03.setCols(30);
			 t03.setParent(row03);
			 
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
							
							if ( !(li02.getSelectedItem() == null) ){		
							putData = putData.replace(Status, li02.getSelectedItem().getLabel().toString().trim());
							}
							
							if ( !(t02.getText().length() <= 0 ) && !Type.equalsIgnoreCase("") ) {
							putData = putData.replace(Type,  t02.getText());
							
							}
							
							if ( !(t03.getText().length() <= 0  && !Pri.equalsIgnoreCase("") )) {
								putData = putData.replace(Pri,  t03.getText());
								
								}
							
							System.out.println("putdata info: "+putData.toString());
							
							try {
								FileUtils.writeStringToFile(CurrentFile, putData);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						
							HandP.detach();
							refreshVisits();
								
							
							
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
							if ( Messagebox.show( "Do you wish to close this Office Visit? "," Close the Office Visit?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
							HandP.detach();
												
						}
						
					});	
			
	}
		
	public void onDoubleClick( Event ev ){  onClick$view ( ev ); }
	
	public void onClick$view( Event ev ){
		
		if ( officevListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No Visit is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		int no = officevListbox.getSelectedIndex();
		String Vfile = Filez.get(no);
		String Debug = DFilez.get(no);
		
		// get selected item's rec
		Rec rec = (Rec) officevListbox.getSelectedItem().getValue();
		
		System.out.println("rec is: "+rec);
		//alert("The Path is:"+Pm.getOvdPath()+Debug);
		
		 java.util.Calendar calendar = GregorianCalendar.getInstance(); 
		 int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		 int mins = calendar.get(java.util.Calendar.MINUTE);
		 int secs = calendar.get(java.util.Calendar.SECOND);
			
		 String timenow = (String.format("%02d", hour))+":"+ (String.format("%02d", mins))+(String.format("%02d", secs));
		 //System.out.println("timenow: "+timenow);
		 
		StringBuilder Strupdate = new StringBuilder();
		Strupdate.append(System.getProperty("line.separator"));
		String Strup = "Before the ISS call routine: "+ timenow;
		Strupdate.append(Strup);
		Strupdate.append(System.getProperty("line.separator"));
		
		/*System.out.println("DEBUG is: "+Debug);
		try {
		    Files.write(Paths.get(Pm.getOvdPath()+Debug), Strupdate.toString().getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}*/
		
		if (( rec == null ) || ( rec.getRec() < 2 ))return;
		
		if	( SoapSheetWin.show( Debug, Vfile, rec, officevWin ));{
			
			refreshVisits();
			
			//alert("The code with record number: "+dgnListbox.getSelectedItem().getValue()+" has been modified" );
			 ptRec =  rec ;
			
			// log the access
			//AuditLogger.recordEntry( AuditLog.Action.DGNCODE_EDIT, null, dgn.getRec(), rec, dgn.getAbbr() );
			
	}
		
		return;
	}
		
	
	
}
