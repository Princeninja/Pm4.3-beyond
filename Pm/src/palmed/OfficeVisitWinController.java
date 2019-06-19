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
import org.zkoss.util.media.AMedia;
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
import org.zkoss.zul.Iframe;
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
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;

public class OfficeVisitWinController extends GenericForwardComposer {
	
	private Window officevWin;
	
	private org.zkoss.zul.North dashboard;
	
	private Radio r_new;
	private Radio r_triage;
	private Radio r_examrm;
	private Radio r_end;
	private Radio r_done;
	private Radio r_all;
	
	private Textbox srcstr;
	
	private Listbox officevListbox;
	
	boolean openISS = false;
	
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
		System.out.println("Refreshed for Pateints at Review Visits");
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
	
	//List of visit folders
	List<String> Filez = new ArrayList<String>();
	
	List<String> DFilez = new ArrayList<String>();
	List<String> Statuses = new ArrayList<String>();
	
	//refresh the visit Listbox
	private void refreshVisits(){ refreshVisits( null );  } 
	
		private void refreshVisits( String searchString ){
		
		Filez.clear();
		DFilez.clear();
		Statuses.clear();
		
		boolean All = false ;
		
		String display = null;
		if ( r_new.isSelected()) display = "QNewQ" ;
		if ( r_triage.isSelected()) display = "QTriageQ";
		if ( r_examrm.isSelected()) display = "QExam RoomQ";
		if ( r_end.isSelected()) display = "QReview VisitQ";
		if ( r_done.isSelected()) display = "QEndQ";
		if ( r_all.isSelected()) All = true ;
		ZkTools.listboxClear(officevListbox);
		
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		File visits = new File( Pm.getOvdPath() + File.separator + "OfficeVisits");
		

		File[] matchingfolders = visits.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visits, String name){
				return new File(visits, name).isDirectory();
			}
			
		});
						
		
		System.out.println("number of folders : " + matchingfolders.length);
		for ( int i = 0; i < matchingfolders.length; i++ ){
			
			String visitfolder = matchingfolders[i].getPath();
			
			File visitfolderf = new File((String)visitfolder);
			
			File[] matchingfile = visitfolderf.listFiles(new FilenameFilter() { 
				
				public boolean accept(File visitfolderf, String name){
					return name.startsWith("Visit")&& name.endsWith("xml");
				}
				
			});
			
			System.out.println("mf is: "+ matchingfile.length );
			if (  matchingfile.length > 0 || !(matchingfile == null)){
				
			File visit = new File(matchingfile[0].getPath());
			
			XMLElement xml = new XMLElement();
			FileReader reader = null;
			
			System.out.println("visit is: "+ matchingfile[0].getPath());
			try {
				reader = new FileReader( visit );
		    } catch (FileNotFoundException e) {
		    	System.out.println( "the.." + matchingfile[0].getPath() + " file was not found:" );
		    	
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
			
			 new XMLElement();
			 XMLElement Info = new XMLElement();
			 new XMLElement();
			 
			 
			 e.getChildByName("VISIT");
			 Info = e.getChildByName("INFO");
			 e.getChildByName("TODO");
			 
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
				
				 Statuses.add(status);	
				 Filez.add(visitfolder);
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
					
					status = deleteachar(status, 0);
					status = deleteachar(status, status.length()-1);
					 
					
					Listcell Abbr = new Listcell( status );
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
		 li01.setSelectedItem(li01.getItemAtIndex(0));
		 
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
						
						//TODO 
						boolean Visitfolder = new File(Pm.getOvdPath()+File.separator+"OfficeVisits"+File.separator+"Visit"+timestrt+ptRec.getRec()).mkdir();
						String Visitfolderstr = "Visit"+timestrt+ptRec.getRec();
						
						if ( Visitfolder ){
						
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
						VisitString = VisitString.replace("QStatusQ", "Q"+li02.getSelectedItem().getLabel().toString().trim()+"Q");
						VisitString = VisitString.replace("QFreeQ", Debugpath );
						
						String visitpath = Pm.getOvdPath()+File.separator+"OfficeVisits"+ File.separator+ Visitfolderstr + File.separator+ "Visit"+timestrt+ptRec.getRec()+"XXXX"+".xml";
						File Visitfile = new File(visitpath);
						new File(Pm.getOvdPath()+Debugpath);
						System.out.println(VisitString);						
						System.out.println("path: "+visitpath);
						
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
						}else { alert("Error creating Visit folder, Please re-try!");}
						
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
		String Vfilem = Filez.get(no);
		
		File visitfolderf = new File(Vfilem);
		
		File[] matchingfile = visitfolderf.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visitfolderf, String name){
				return name.startsWith("Visit")&& name.endsWith("xml");
			}
			
		});
		
		final String Vfile = matchingfile[0].getPath();
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
	
		//System.out.println("visit is: "+ visit);
		try {
			reader = new FileReader( Vfile );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + Vfile + " file was not found:" );
	    	
	    }
		
	    System.out.println("Edit file is: "+ Vfile);
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
		 new XMLElement();
		 
		 		 
			
		 Info = e.getChildByName("INFO");
		 String Status = Info.getChildByName("Status").getContent().trim();
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
			 Status = deleteachar(Status, 0);
			 Status = deleteachar(Status, Status.length()-1);
			 final String fStatus = Status;
			 t01.setText(fStatus);
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
							putData = putData.replace("Q"+fStatus+"Q", "Q"+li02.getSelectedItem().getLabel().toString().trim()+"Q");
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
	
	
	public void onDoubleClick( Event ev ){  
		
		if ( !openISS ){
		if ( !(officevListbox.getSelectedCount() < 1) ){
						
		onClick$view ( ev ); } } }
	
	public void onClick$view( Event ev ){
		
		openISS = true;
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
		
		//TODO add review window 
		
		if (( rec == null ) || ( rec.getRec() < 2 ))return;
		
		if ( !Statuses.get(no).equals("QReview VisitQ")){
		if	( SoapSheetWin.show( Debug, Vfile, rec, officevWin ));{
			
			refreshVisits();
			openISS = false;			
			 ptRec =  rec ;	
		
	}}else{
		
				
		File visitfolderf = new File(Vfile);
		
		File[] matchingfile = visitfolderf.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visitfolderf, String name){
				return name.startsWith("Visit")&& name.endsWith("xml");
			}
			
		});
		
		final String Vfilef = matchingfile[0].getPath();
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
	
		try {
			reader = new FileReader( Vfilef );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + Vfilef  + " file was not found:" );
	    	
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
		
		
		 XMLElement Endgame = new XMLElement();		 
		 
		 Endgame = e.getChildByName("ENDGAME");
		 
		final Window HandP = new Window();	
		
		HandP.setParent(officevWin);
		HandP.setPosition("center");
		
		HandP.setHeight("702px"); //reduced from 702px
		HandP.setWidth("813px");
		HandP.setVflex("1");
		HandP.setHflex("1");
		HandP.setBorder("normal");
		HandP.setClosable(true);
				
		HandP.doOverlapped();
		
		 Vbox MainVbox = new Vbox();
		 MainVbox.setHeight("600px"); //reduced from 667px
		 MainVbox.setWidth("791px");
		 MainVbox.setParent(HandP);
		 
		 Tabbox Maintabbox = new Tabbox();
		 //Maintabbox.setVflex("1");
		 Maintabbox.setHflex("1");
		 Maintabbox.setHeight("585px"); //reduced from 667px
		 Maintabbox.setParent(MainVbox);
		 
		 Tabs tabs1 = new Tabs();
		 Maintabbox.appendChild(tabs1);
		 
		 Tabpanels tabpanels1 = new Tabpanels();
		 Maintabbox.appendChild(tabpanels1);
		 
		 Tab tab00 = new Tab();
		 tab00.setLabel("SOAP-Review");
		 tab00.setParent(tabs1);
		
		 Tabpanel tabpanel06 = new Tabpanel();
		 tabpanel06.setVflex("1");
		 tabpanel06.setHflex("1");
		 tabpanel06.setParent(tabpanels1);
		 tabpanel06.setStyle("overflow:auto");
		 
		 Groupbox hbox06 = new Groupbox();
		 hbox06.setHeight("585px");
		 hbox06.setParent(tabpanel06);
		
		 Groupbox gbox06 = new Groupbox();
		 gbox06.setParent(hbox06);
		 
		 final Textbox Fhx = new Textbox();
		 Fhx.setRows(4);
		 Fhx.setCols(100);
		 Fhx.setParent(gbox06);
		 Fhx.setText( Endgame.getChildByName("Subj").getContent().trim() );
		 new Caption("Subjective:").setParent(gbox06);
		
		 Groupbox gbox07 = new Groupbox();
		 gbox07.setParent(hbox06);
		 
		 final Textbox Shx = new Textbox();
		 Shx.setRows(4);
		 Shx.setCols(100);
		 Shx.setParent(gbox07);
		 Shx.setText( Endgame.getChildByName("Obj").getContent().trim() );
		 
		 new Caption("Objective:").setParent(gbox07);
		 
		 Groupbox gbox09 = new Groupbox();
		 gbox09.setParent(hbox06);
		 gbox09.setStyle("overflow:auto");
		 
		 final Textbox Bhx = new Textbox();
		 Bhx.setRows(4);
		 Bhx.setCols(100);
		 Bhx.setParent(gbox09);
		 Bhx.setStyle("overflow:auto");
		 
		 new Caption("Assessment:").setParent(gbox09);
		 Bhx.setText( Endgame.getChildByName("Assess").getContent().trim() );
		 
		 Groupbox gbox10 = new Groupbox();
		 gbox10.setParent(hbox06);
		 gbox10.setStyle("overflow:auto");
			
			final Textbox Sghx = new Textbox();
			Sghx.setRows(4);
			Sghx.setCols(100);
			Sghx.setParent(gbox10);
			Sghx.setStyle("overflow:auto");
			
			new Caption("Plan:").setParent(gbox10); 
		
			Sghx.setText( Endgame.getChildByName("Plan").getContent().trim() );
			
		Tab tab01 = new Tab();
		tab01.setLabel("Misc");
		tab01.setParent(tabs1);
		
		Tabpanel tabpanel00 = new Tabpanel();
		 tabpanel00.setVflex("1");
		 tabpanel00.setHflex("1");
		 tabpanel00.setParent(tabpanels1);
		 
		 Hbox hbox00 = new Hbox();
		 hbox00.setParent(tabpanel00);
		
		 Groupbox gbox00 = new Groupbox();
		 gbox00.setHflex("max");
		 gbox00.setParent(hbox00);
		 
		 new Caption ("Patient Metrics: ").setParent(gbox00);
		
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
		 //t00.setText(Tdate.getPrintable());
		 
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
		 l02.setValue("Time Spent: ");
		 l02.setParent(row02);
		 
		 final Textbox t02 = new Textbox();
		 t02.setCols(30);
		 t02.setParent(row02);
		
		
		 
			final int prec = Integer.parseInt(Endgame.getChildByName("Provrec").getContent().trim());
			
			final Rec provRec = new Rec();
			
			final String date = Endgame.getChildByName("date").getContent().trim();
			final String desc = Endgame.getChildByName("desc").getContent().trim();
			
		 
		 Hbox SecondHbox = new Hbox();
			SecondHbox.setHeight("26px");
			SecondHbox.setWidth("340px");
			SecondHbox.setPack("start");
			SecondHbox.setParent(HandP);
			
			Vbox SnC = new Vbox();
			SnC.setParent(SecondHbox);
			SnC.setPack("start");
			SnC.setAlign("start");
			
			final XMLElement Endgamef = Endgame; 

			Button SR = new Button();
			SR.setParent(SnC);
			SR.setLabel("Save");
			SR.setWidth("65px");
			SR.setHeight("23px");
			SR.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					if ( Messagebox.show( "Save content to ISS-Review? ","Save ISS-Review?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
					 
					String putData = "";
					File CurrentFile = new File ( Vfilef );
					try {
						 putData = FileUtils.readFileToString( CurrentFile );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
												
					putData = putData.replace(Endgamef.getChildByName("Subj").getContent().trim(), Fhx.getText().trim());
				
					putData = putData.replace(Endgamef.getChildByName("Obj").getContent().trim(), Shx.getText().trim());
					
					putData = putData.replace(Endgamef.getChildByName("Assess").getContent().trim(), Bhx.getText().trim());
					
					putData = putData.replace(Endgamef.getChildByName("Plan").getContent().trim(), Sghx.getText().trim());
					
											
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
			Close.setParent(SnC);
			Close.setLabel("Cancel");
			Close.setWidth("65px");
			Close.setHeight("23px");
			Close.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					if ( Messagebox.show( "Do you wish to close this ISS-Review? "," Close the ISS-Review?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
					
					//Final check
					if ( Messagebox.show( "Any information entered wont be saved, close?", "Verify close?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES )return;
					
					HandP.detach();
					refreshVisits();
										
				}
				
			});	
			
			Hbox ThirdHbox = new Hbox();
			ThirdHbox.setHeight("26px");
			ThirdHbox.setWidth("340px");
			ThirdHbox.setPack("start");
			ThirdHbox.setParent(SecondHbox);
			
			Div end = new Div();
			end.setParent(ThirdHbox);
			end.setStyle("text-align: right");
			
			XMLElement Info = new XMLElement();		 
			 
			Info = e.getChildByName("INFO");
			
			String soapstat = Info.getChildByName("Soap").getContent().trim();
			
			
			
			Button Save = new Button();
				Save.setParent(end);
				Save.setLabel("Create Soap");
				Save.setWidth("105px");
				Save.setHeight("23px");
				if ( soapstat.equalsIgnoreCase("QCreatedQ")){ Save.setDisabled(true); }
				Save.addEventListener(Events.ON_CLICK, new EventListener(){
					
					
					public void onEvent(Event arg1) throws Exception {
						// TODO Auto - generated method stub
						
						if ( Messagebox.show( "Are you sure you wish to create the SOAP? "," Create SOAP ? ", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;  
					
					
						SoapNote soapnt = null;
						Reca soapReca = null;
						
						
						
						provRec.setRec(prec);
						
						if ( !(provRec == null)){
						soapnt = new SoapNote();
						soapnt.setPtRec( ptRec );
						soapnt.setStatus( usrlib.RecordStatus.CURRENT );					
						
						//TODO pull the below variables 
						
						soapnt.setProvRec( provRec );
						
						usrlib.Date ddate = new usrlib.Date();
						ddate.fromString(date);
						
						soapnt.setDate( ddate );
						soapnt.setDesc( desc );
						
						soapnt.setText( Fhx.getText().trim() + '\n' + Shx.getText().trim() + '\n' + Bhx.getText().trim() + '\n' + Sghx.getText().trim() + '\n' );
						
						
							System.out.println("soapreca before new save: "+soapReca);
							soapReca = soapnt.postNew( ptRec );
							
							// post to MR Log
							//MrLog.postNew( ptRec, date, desc, MrLog.Types.SOAP_NOTE, soapReca );
							
							String putData = "";
							File CurrentFile = new File ( Vfilef );
							try {
								 putData = FileUtils.readFileToString( CurrentFile );
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
														
							putData = putData.replace("QNoneQ", "QCreatedQ");						
												
							
							try {
								FileUtils.writeStringToFile(CurrentFile, putData);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							// log the access
							AuditLogger.recordEntry( AuditLog.Action.SOAP_ADD, ptRec, Pm.getUserRec(), null, null );
						
						}
						
						HandP.detach();
						refreshVisits();
						
					}
					
				});
				
								
				
				XMLElement Summary = new XMLElement();		 
				 
				Summary = e.getChildByName("SUMM");
				final String CCF;
				final String AssessF;
				final String FUP, FUP2, FUW,  SRVI, SRVI2, Charges, Charges2, PROCEED, LabsB;
				
				
				CCF = Summary.getChildByName("CCF").getContent().trim();
				AssessF = Summary.getChildByName("AssesmentF").getContent().trim();
				FUP = Summary.getChildByName("FUP").getContent().trim();
				FUP2 = Summary.getChildByName("FUP2").getContent().trim();
				FUW = Summary.getChildByName("FUW").getContent().trim();
				SRVI = Summary.getChildByName("SRVI").getContent().trim();
				SRVI2 = Summary.getChildByName("SRVI2").getContent().trim();
				Charges = Summary.getChildByName("Charges").getContent().trim();
				Charges2 = Summary.getChildByName("Charges2").getContent().trim();
				PROCEED = Summary.getChildByName("PROCEED").getContent().trim();
				LabsB =Summary.getChildByName("LabsB").getContent().trim();
											
								
				Button Print = new Button();
				Print.setParent(end);
				Print.setLabel("Print Visit-Summary");
				Print.setWidth("145px");
				Print.setHeight("23px");
				Print.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						
						if ( Messagebox.show( "Do you wish to print the Visit Summary? "," Print Visit Summary?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
						//HandP.detach();
						
						XMLElement Info = new XMLElement();		 
						 
						Info = e.getChildByName("INFO");
						
						System.out.println("name: "+ Info.getChildByName("Name").getContent().trim() );
						
						
						File htmlSummaryFile = new File( Pm.getOvdPath() + File.separator +"VisitSummary.html");
						String htmlString = "";
						try {
							 htmlString = FileUtils.readFileToString(htmlSummaryFile);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						DirPt dirPt = new DirPt( ptRec );
						
						StringBuilder sb = new StringBuilder();
						
						sb.append( "<style type='text/css'>" );
						sb.append("@page"+ System.getProperty("line.separator") );
						sb.append("{"+ System.getProperty("line.separator") );
						sb.append("    size: auto;"+ System.getProperty("line.separator") );
						//sb.append("    margin-bottom: 1.225cm;"+ System.getProperty("line.separator") );
						sb.append("    margin: 0mm;"+ System.getProperty("line.separator") );
						sb.append("}"+ System.getProperty("line.separator") );
						sb.append(" body { margin: 0.875cm; }"+ System.getProperty("line.separator") );//0.875cm
						sb.append(" div { line-height: normal; }"+ System.getProperty("line.separator") );
						sb.append( "</style>" );
						sb.append( System.getProperty("line.separator") );
						
						htmlString = htmlString.replace("QHEADERQ", sb.toString() );
						
							
						htmlString = htmlString.replace("QVisitDateQ", date );
						htmlString = htmlString.replace("QProviderNameQ", Info.getChildByName("ProviderName").getContent().trim() );
						htmlString = htmlString.replace("%s", Info.getChildByName("Name").getContent().trim() );
						htmlString = htmlString.replace("QnameQ", Info.getChildByName("Name").getContent().trim() );
						htmlString = htmlString.replace("QdobQ", dirPt.getBirthdate().getPrintable() );
						htmlString = htmlString.replace("QCCQ", CCF );
						htmlString = htmlString.replace("QAssessQ", AssessF );
						htmlString = htmlString.replace("QInterventQ", FUP );
						htmlString = htmlString.replace("QInstructQ", FUP2 );
						htmlString = htmlString.replace("QFollowUpWithQ", FUW );
						htmlString = htmlString.replace("QRTCNumberQ", SRVI );
						htmlString = htmlString.replace("QRTCPeriodQ", SRVI2 );
						htmlString = htmlString.replace("QNewPatientQ", Charges );
						htmlString = htmlString.replace("QLevelQ", Charges2 );
						htmlString = htmlString.replace("QproceeduresQ", PROCEED );
						htmlString = htmlString.replace("QLabsBilledQ", LabsB );
						
						StringBuilder sb2 = new StringBuilder();
						
						sb2.append( "<script type='text/javascript' defer='true' >" );
						sb2.append( "window.focus();window.print();window.close();" );
						sb2.append( "</script>" );
						
						htmlString = htmlString.replace("QFOOTERQ", sb2.toString() );
						
						// Wrap HTML in AMedia object
						AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", htmlString );
						
						// Create Iframe and pass it amedia object (HTML report)
						Iframe iframe = new Iframe();
						iframe.setHeight( "1px" );
						iframe.setWidth( "1px" );
						iframe.setContent( amedia );
						iframe.setParent( officevWin );
						
					}
					
				});	
				
				refreshVisits();
				openISS = false;			
				 ptRec =  rec ;	
		
	}
		
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
