package palmed;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;


import palmed.PhyExamWinController.Cbox;
import palmed.PhyExamWinController.Lateral;
import palmed.PhyExamWinController.Tbox;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.XMLElement;

/**
 * <p>Title: PAL/MED </p>
 * 
 * <p>Description: Xmlwindow class </p>
 * 
 * <p>Copyright: Copyright (c) 2019 </p>
 * 
 * <p>Company: PAL/MED </p>
 * 
 * @author Prince A Antigha
 * @version TBD
 */

public class Xmlwindow {
	
	private String MainChild, SecondChild, ThirdChild, SuperChild, AdoptedChild;
	private XMLElement e, emain, emainchild, xml = new XMLElement();
	private int  NosChildren;
	private Tab tab;
	private Tabs tabs = new Tabs();
	private Tabpanels tabpanels = new Tabpanels();
	final private List<Object> Findings = new ArrayList<Object>();
	private boolean moreChildren;
	private Hbox main = new Hbox();
	private Window build_XmlWin = new Window();
	private Button update;
	private File Mainfile;
	private Component SuperParent = null;
	private Xmlwindow xmlwindow = null; 
	
	
	public Window setXmlWin(Component parent, Xmlwindow xmlwindow){
		 
		 build_XmlWin.setParent(parent);
		 this.SuperParent = parent;
		 this.xmlwindow = xmlwindow;
		 
		 //build_XmlWin.setWidth("850px");
		 
		 return build_XmlWin;
		 
	 }

	public File MainFile(String xmlpath, Rec ptRec, final String Type, String folder) {
		
		File MainFile = null;
			//new File("");
		boolean filefound = false;
		System.out.println("folder is: "+ folder+","+ Type );
		
		File xmls = new File(folder);
		
		File[] matchingfiles = xmls.listFiles(new FilenameFilter() { 
			
			public boolean accept(File xmls, String name){
				return name.startsWith(Type)&& name.endsWith("xml");
			}
			
		});
				
		for ( int i = 0; i < matchingfiles.length; i++ ){
			
			if ( matchingfiles[i].getPath().contains(Type) && (matchingfiles[i].getPath().contains(Integer.toString(ptRec.getRec())))){
				
				MainFile = matchingfiles[i];
				filefound = true;
			}
		}
		
	 if ( !filefound ){
		File XmlFile0 = new File( xmlpath );
		String XmlString="";
		try {
			XmlString = FileUtils.readFileToString(XmlFile0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String uniquepath = folder + File.separator+Type+ptRec.getRec()+".xml";
		File Xmlfile = new File(uniquepath);
		
		try {
			FileUtils.writeStringToFile(Xmlfile, XmlString);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MainFile = Xmlfile;
	 }
	 
	 	Mainfile = MainFile;
	 	
		return MainFile;}
	
	
	public void initializexml ( String[] Tabboxms, String MainChild, String SecondChild,String ThirdChild, XMLElement xml,
								boolean morechildren, String SuperChild, String ContentChild, boolean standalone, String AdoptedChild,
								final String StatusChild){
		
			
		if ( morechildren ) {
			
			
			main.setParent(build_XmlWin);
			//main.setWidth("850px");
			
			moreChildren = true;		
			
		}
		
		
		 Vbox SecondaryVbox = new Vbox();
		 SecondaryVbox.setVflex("1");
		 SecondaryVbox.setHflex("1");
		 //SecondaryVbox.setWidth("850px");
		 SecondaryVbox.setParent(build_XmlWin);
		 
	 	 Tabbox Secondarytabbox = new Tabbox();
	 	 //Secondarytabbox.setHflex("min");
	 	 Secondarytabbox.setHeight(Tabboxms[0]); //reduced from 625px
	 	 Secondarytabbox.setWidth(Tabboxms[1]);
	 	 Secondarytabbox.setOrient("vertical");
		 
		 SecondaryVbox.appendChild(Secondarytabbox);
		 //System.out.println("Vbox and tabbox width: "+ SecondaryVbox.getWidth()+", "+ Secondarytabbox.getWidth());
		 
		this.xml = xml;
		this.MainChild = MainChild;
		this.SecondChild = SecondChild;
		this.ThirdChild = ThirdChild;
		this.Statuschild = StatusChild;
		this.AdoptedChild = AdoptedChild;
		Secondarytabbox.appendChild(tabs);
		tabs.setWidth("85px");		
		Secondarytabbox.appendChild(tabpanels);
		//tabpanels.setHflex("max");
		 
		if ( !(xml==null)){
			
			String Name = xml.getName();
			
			
			 if ( moreChildren) {
				 				
				emain = xml.getElementByPathName(Name);
				System.out.println("emain is: "+emain.getName());
				
				emainchild = emain.getChildByName(SuperChild); this.SuperChild = SuperChild;
				System.out.println("emainchild is: "+emainchild.getName());
				
				e = emain.getChildByName(ContentChild); 
				System.out.println("e is: "+e.getName());
				
				NosChildren = e.countChildren();
				
			 }else{			
			 
			 NosChildren = xml.countChildren();
			 
			 e = xml.getElementByPathName(Name);
		   }
		}
		
		
		if ( standalone ) {
			
			Hbox endbox = new Hbox();
			endbox.setHeight("26px");
			endbox.setWidth("200px");
			endbox.setPack("start");
			endbox.setParent(build_XmlWin);
			
			Div end = new Div();
			end.setParent(endbox);
			end.setStyle("text-align: right");
			
			update = new Button();
				update.setParent(endbox);
				update.setLabel("Update");
				update.setWidth("65px");
				update.setHeight("23px");
				update.addEventListener(Events.ON_CLICK, new EventListener(){
					
					public void onEvent(Event arg1) throws Exception {	
						
						update();
						
					}
					
				});
		 }
		
	}
	
	public void refreshXW (XMLElement newxml) {
		
		//build_XmlWin.detach();
		
		System.out.println("this is the parent:" + this.SuperParent );
		
		this.xmlwindow.setXmlWin(this.SuperParent, this.xmlwindow);
		//this.xmlwindow.initializexml("85px", this.MainChild, this.SecondChild, this.ThirdChild, newxml, this.moreChildren, 
				//this.SuperChild, this.ContentChild, this.standAlone, this.AdoptedChild, this.Statuschild );
		this.xmlwindow.createxml();		
		
	}
	
	
	public void createxml(){
		
		 if ( moreChildren ){	
			 
			//Creates a groupbox
			 Groupbox gbox = new Groupbox();
			 new Caption(SuperChild).setParent(gbox);
			 gbox.setParent(main);
			 //gbox.setWidth("825px");
			 gbox.setVflex("min");
			 gbox.setHflex("min");
			 
			 Hbox main2 = new Hbox();
			 main2.setParent(gbox);
			 
			 List<Vbox> Vboxes = new ArrayList<Vbox>(); 
			 
			 for ( int i = 1; i<emainchild.countChildren()+1; i++){		 
			 
			 if ( (i == 1) || ((i&3) == 0) ){
				 //System.out.println("i is: "+ i);
				 Vbox vboxs = new Vbox();
				 vboxs.setParent(main2);
				 Vboxes.add(vboxs);
			 }
			 
			 //System.out.println("Vbox size is: "+Vboxes.size());
			 
			 Cbox gen = new Cbox(emainchild.getChildByNumber(i-1), Vboxes.get(Vboxes.size()-1));
			 gen.createCbox();
			 Findings.add(gen);
			 
		 } 
	   }
			
		 for ( int j=0; j< NosChildren ; j++){
		 XMLElement Childj = e.getChildByNumber( j );
		 
		 //System.out.println("Childjj name is: "+Childj.getName());
		 
		 if ( Childj.getName().equals(MainChild)){
			 //Creates a tab with the Label: Childj.getAttribute("label")
			 tab = new Tab();
			 tab.setLabel((String) Childj.getAttribute("label"));
			 tab.setParent(tabs);
			 //tab.setStyle("overflow:auto");
			 Findings.add(tab);
						 
			 //Creates a tabpanel
			 Tabpanel tabpanel = new Tabpanel();
			 tabpanel.setStyle("overflow:auto");
			 tabpanel.setVflex("1");
			 tabpanel.setHflex("max");
			 //tabpanel.setWidth("800px");
			 tabpanel.setParent(tabpanels);
			
		   XMLElement Childjj; 
		 
		 
			 
		 Childjj = Childj.getChildByNumber(0);
		 //System.out.println("Window,Hboxmain, tab, tabpanel is: "+ build_XmlWin.getWidth()+", "+ main.getWidth()+", "+tab.getWidth()+", "+tabpanel.getWidth());
		 			 		 		 		 
		 if ( (Childjj.getName().equals(SecondChild)) || (Childjj.getName().equals(AdoptedChild)) ){
			 
			 Hbox  hboxA1 = new Hbox();
			 
			 if ( Childjj.getName().equals(AdoptedChild) ){
			 
			 hboxA1.setParent(tabpanel);
			 
			 Space spacebecka = new Space();
			 spacebecka.setParent(tabpanel); }
			 
			 Vbox Vbox1 = new Vbox();
			 Vbox1.setParent(tabpanel); 
			 			 
			 
			 for ( int k = 0; k < Childj.countChildren(); k++ ){
				 
				 XMLElement Childk = Childj.getChildByNumber(k);
				 
				 
				if(Childj.getChildByNumber(k).getName().equals(AdoptedChild)){
					
					 Cbox gen = new Cbox(Childk, hboxA1);
					 gen.createCbox();
					 Findings.add(gen);
					
				}else if(Childj.getChildByNumber(k).getName().equals(SecondChild)){
				//Creates a groupbox
				 Groupbox gbox = new Groupbox();
				 new Caption((String) Childk.getAttribute("ID")).setParent(gbox);
				 gbox.setParent(Vbox1);
				 gbox.setWidth("305px");
				 //gbox.setWidth("max");
				
								 
				 for( int l =0; l < Childk.countChildren(); l++){
				 XMLElement Childkk = Childk.getChildByNumber(l);
				 
				 if (Childkk.getName().equals(ThirdChild)){
					 if (!Childkk.toString().contains("Misc")){ 
					 if ( Childkk.getAttribute("Type").equals("checkbox")){
						
												
						Cbox gen = new Cbox(Childkk, gbox);
						gen.createCbox();
						Findings.add(gen);
						 
											 
					}else if ( Childkk.getAttribute("Type").equals("binary-text") ){
						
						//Creates a binary-text 
						BinarY Bi0 = new BinarY(Childkk, gbox);
						Bi0.createBinarY( "+", "- ", 40,1);
						Findings.add(Bi0);					
					
					
					}else if( Childkk.getAttribute("Type").equals("lateral") ){
						
						//Creates a "+Childkk.getAttribute("Type")+" named: "+Childkk.getAttribute("ID")
						Lateral lat0 = new Lateral(Childkk, gbox);
						lat0.createLateral("L", "R");
						Findings.add(lat0);
						
						
											 
					 }else if( Childkk.getAttribute("Type").equals("text-check")){
						 
						 //Creates a text-check
						 Cbox tgen = new Cbox(Childkk, gbox);
						 tgen.createCbox(40, 3, "",  true);
						 Findings.add(tgen);
						 
						 /* Separator sep = new Separator();
						 sep.setParent(gbox);
						 
						 Tbox text01 = new Tbox(Childkk, gbox);
						 text01.createTbox(40, 3);
						 Findings.add(text01);*/
						 
					
					 }else if( Childkk.getAttribute("Type").equals("lateral-text")){
						 
						 Lateral lat01 = new Lateral(Childkk, gbox);
						 lat01.createLateral("L", "R", 40, 3,"", true);
						 Findings.add(lat01);
						 
						/*						 
						 Separator sep = new Separator();
						 sep.setParent(gbox);
						 
						 Tbox text02 = new Tbox(Childkk, gbox);
						 text02.createTbox(40, 3);
						 Findings.add(text02); */
						 
						 
					   }
					 else{
						 DialogHelpers.Messagebox("The type:"+ Childkk.getAttribute("Type")+" isn't availabe in this version of the software!");
					  }
					 
					 }else if (Childkk.toString().contains("Misc")){
						 
						if (Childkk.getAttribute("Type").equals("lateral") && Childkk.getAttribute("Misc").equals("U/L")){
							
							Lateral lat02 = new Lateral(Childkk, gbox);
							lat02.createLateral("U", "L");
							Findings.add(lat02);
							
							
						}else if (Childkk.getAttribute("Type").equals("checkbox") && Childkk.getAttribute("Misc").equals("textbox/r/s/'/6'")){
							
							Cbox misc1  = new Cbox(Childkk, gbox);	
							misc1.createCbox( 4, 1, "/6",false);
							Findings.add(misc1);
							
							
						}else if (Childkk.getAttribute("Type").equals("lateral") && Childkk.getAttribute("Misc").equals("textbox/r/s")){
							
							Lateral lat03 = new Lateral(Childkk, gbox);
							lat03.createLateral("L", "R", 2, 1, "",false);
							Findings.add(lat03);
							
													
							
						}else if (Childkk.getAttribute("Type").equals("checkbox") && Childkk.getAttribute("Misc").equals("textbox/r/s")){
							
							Cbox misc2 = new Cbox(Childkk, gbox);
							misc2.createCbox( 2, 1, "", false);
							Findings.add(misc2);
							
						}else if (Childkk.getAttribute("Type").equals("lateral") && Childkk.getAttribute("Misc").equals("textbox/r/s/'/5'")){
							
							Lateral lat04 = new Lateral(Childkk, gbox);
							lat04.createLateral("L", "R", 2, 1, "/5",false);
							Findings.add(lat04);
							
							
							
							
						}else if (Childkk.getAttribute("Type").equals("lateral") && Childkk.getAttribute("Misc").equals("+/-")){
							
							Lateral lat05 = new Lateral(Childkk, gbox);
							lat05.createLateral("+", "-");
							Findings.add(lat05);
							
							
							
						}else if (Childkk.getAttribute("Type").equals("lateral-text") && Childkk.getAttribute("Misc").equals("+/-")){
							
							Lateral lat06 = new Lateral(Childkk, gbox);
							lat06.createLateral("+", "-",40,3,"", true);
							Findings.add(lat06);
							
							/*
							Tbox text03 = new Tbox(Childkk, gbox);
							text03.createTbox(40, 3);
							Findings.add(text03);*/
							
							
						}else if (Childkk.getAttribute("Type").equals("binary-text") && Childkk.getAttribute("Misc").equals("/")){
							
							BinarY Bi0 = new BinarY(Childkk, gbox);
							Bi0.createBinarY( "", "", 40,1);
							Findings.add(Bi0);
							
							
							
						}else {  DialogHelpers.Messagebox("No provision exists for: "+Childkk.getAttribute("Misc")+" in this version");} 
						
						 
					 }
					 
				   }
				 }
			 	}  
			 }
		 	 
		 }
		
		
		 else if(Childjj.getName().equals(ThirdChild)){
			 
		 Groupbox gbox1 = new Groupbox();
		 gbox1.setParent(tabpanel);
		 new Caption((String)Childj.getAttribute("label")).setParent(gbox1);
		 		
		 
		 for ( int i=0; i < Childj.countChildren(); i++ ){
			 
		 XMLElement Child11 = Childj.getChildByNumber(i);
		 if (Child11.getAttribute("Type").equals("checkbox")){
		
			 
		 Cbox gen1 = new Cbox(Child11, gbox1);
		 gen1.createCbox();
		 Findings.add(gen1);
		 
		 if ( i % 4 ==0 ){	 
			 Separator Sep = new Separator();
			 Sep.setParent(gbox1);
		 }
		 			 
		 	 
		 //Creates a "+Child11.getAttribute("Type")+" named: "+Child11.getAttribute("ID")
		 	 }else if ( Child11.getAttribute("Type").equals("text-check")){
		 		
		 		 Cbox tgen = new Cbox(Child11, gbox1);
		 		 tgen.createCbox(80, 4, "", true );
		 		 Findings.add(tgen);
		 		 
		 		 /*
		 		 Separator sep = new Separator();
				 sep.setParent(gbox1);
				 
				 
				 Tbox text04 = new Tbox(Child11, gbox1);
				 text04.createTbox(80, 4);
				 Findings.add(text04);*/
				 
				 
		 	  }else if ( Child11.getAttribute("Type").equals("binary-text") ){
					
		 		  if ( i % 4 ==0 ){	 
					 Separator Sep = new Separator();
					 Sep.setParent(gbox1);
		 		  }
		 		  
					//Creates a binary-text 
					BinarY Bi0 = new BinarY(Child11, gbox1);
					Bi0.createBinarY( "+", "- ", 40,1);
					Findings.add(Bi0);					
				
				
				}else if ( Child11.getAttribute("Type").equals("text-check/s")){
			 		
			 		 Cbox tvit = new Cbox(Child11, gbox1);
			 		 tvit.createCbox(60, 1, "",false);
			 		 Findings.add(tvit);					 										 
					 
			 	  }else { DialogHelpers.Messagebox("This Type: "+ Child11.getAttribute("Type")+" is not available in this version.");} 
		        
		 	}
		    
		 }else { DialogHelpers.Messagebox("Provision for \"child\" named "+ Childjj.getName()+" hasn't been made in this version.");}
		
		 }
	}
		 
	}
	
	public String examtext( boolean plain ) {
		
		String examtext = "";
		
		StringBuilder PhysicalExam = new StringBuilder();
		
		for (int i=0; i < Findings.size(); i++){
		
		 String Physicalcomp = getfstatus(Findings.get(i));
		 
		 if (Physicalcomp == null ){ Physicalcomp = ""; }
		 PhysicalExam.append(Physicalcomp);	
		 
		 if (Findings.get(i) instanceof Tab){ TabsList.add(((Tab) Findings.get(i)).getLabel().trim());}
		
		}
		
		String PhysicalEx = PhysicalExam.toString();
		
		for (int i=0; i < 4; i++){
		PhysicalEx = deleteachar(PhysicalEx, 0);}
		
		PhysicalEx = PhysicalEx+"</p>";
		
		String PhysicalExamFinal = null;
										
		String tabcont =  "<p align=\"justify\"><b>%s: </b></p>";
																							
		/*PhysicalExamFinal = PhysicalEx.replace(String.format(tabcont, TabsList.get(0)), "").replace(String.format(tabcont, TabsList.get(1)), "")						
										.replace(String.format(tabcont, TabsList.get(2)), "").replace(String.format(tabcont, TabsList.get(3)), "")	
										.replace(String.format(tabcont, TabsList.get(4)), "").replace(String.format(tabcont, TabsList.get(5)), "")
										.replace(String.format(tabcont, TabsList.get(6)), "").replace(String.format(tabcont, TabsList.get(7)), "")
										.replace(String.format(tabcont, TabsList.get(8)), "").replace(String.format(tabcont, TabsList.get(9)), "")
										.replace(String.format(tabcont, TabsList.get(10)), "").replace(String.format(tabcont, TabsList.get(11)), ""); */
		
		PhysicalExamFinal = PhysicalEx;
		
		for ( int j=0; j < TabsList.size(); j++ ){
			
			PhysicalExamFinal = PhysicalExamFinal.replace(String.format(tabcont, TabsList.get(j)), "");
		}
		
		if (plain) {			
			
			String SoapExam =  PhysicalExamFinal.replace("<p align=\"justify\">", "").replace("<b>", "").replace("</b>", "")
			.replace("<p>", "").replace("</p>", "");
			
			examtext = SoapExam;
			
			
		}else { examtext = PhysicalExamFinal;}
		
		System.out.println("XmlText is: "+ examtext);
		return examtext;
	}
	
	private String Statuschild; 
	
	private void update() {
		
		
		
		for (int i=0; i < Findings.size(); i++){
			
			setupdatestatus(Findings.get(i));
		}
		
		//System.out.println( xml.toString());
		
		try {
			FileUtils.writeStringToFile(Mainfile, xml.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//refreshXW( xml );
		
	}
	
	
	private void setupdatestatus(Object O) {
		
		if (O instanceof Cbox){  ((Cbox) O).setStatus(); }
		
		else if (O instanceof Tbox){ ((Tbox) O).setText(); }
		
		else if ( O instanceof Lateral){ ((Lateral) O).setStatus();}
		
		else if ( O instanceof BinarY){ ((BinarY) O).setStatus(); }
			
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
		
		else if ( O instanceof BinarY) { status = ((BinarY) O).getStatus();} 
		return status;
	}
	
	
	public class BinarY {
		
		XMLElement Childkk;		
		Component parent;
		Vbox vbox;
		Hbox hbox;
		Groupbox gboxM = null;
		Checkbox boxL,boxR;
		Textbox text = null;
		String info, textstr;
		boolean textin;  // if needed
		boolean checked;
		
		BinarY( XMLElement Childkk, Component parent ){
			
			this.Childkk = Childkk;
			this.parent = parent;
			
			if ( parent instanceof Groupbox){
				
				this.gboxM = (Groupbox) parent;
			}
			
		}
		
		
		void createBinarY ( String left, String right, final int cols, final int rows){
			
			vbox = new Vbox();
			vbox.setParent(parent);
			
			hbox = new Hbox();
			hbox.setParent(vbox);
			
			boxL = new Checkbox();
			boxL.setLabel(left);
			boxL.setParent(hbox);
						
			boxR = new Checkbox();
			boxR.setLabel( right+ (String) Childkk.getAttribute("ID") );
			boxR.setParent(hbox);
			
			String status = "";
			if ( !(Childkk.getChildByName(Statuschild)== null)){
				
				status = Childkk.getChildByName(Statuschild).getContent().trim();
			}
			
			if ( status.contains("Left")) { boxL.setChecked(true); }
			else if ( status.contains("Right")){ boxR.setChecked(true); }
			
			
			if ( boxL.isChecked() ){
				
				
				Hbox hbox2 = new Hbox();
				hbox2.setParent(vbox);
				
				text = new Textbox();
				text.setCols(cols);
				text.setRows(rows);
				text.setParent(hbox2);
				
				if ( !(gboxM == null) ){
					
					gboxM.setWidth("min");
				}
				
				if ( !(Childkk.getChildByName("textfield")== null)){
					
					textin = true ;
					text.setText(Childkk.getChildByName("textfield").getContent().trim());
				}				
			}else {
				
				boxL.addEventListener(Events.ON_CHECK, new EventListener(){
					
					public void onEvent(Event arg1) throws Exception {	
						if ( !checked ){
						checked = true; 
						
						Hbox hbox2 = new Hbox();
						hbox2.setParent(vbox);
						
						text = new Textbox();
						text.setCols(cols);
						text.setRows(rows);
						text.setParent(hbox2);
						
						if ( !(gboxM == null) ){
							
							gboxM.setWidth("min");
							
						}
					}else {
						
						checked = false;
						
						text.setVisible(false);
						
					}
						
						
					}
					
				});				
			}
			
			
		}
		
		
		String getStatus() {
			String status = null;
			if(text == null ){
				
				if (boxL.isChecked() == true ){ status = ""; }
				else if  (boxR.isChecked() == true ){ status = info ; }
				
			}
			
			else {
				
				Label textf = new Label();
				textf.setValue(text.getText().trim()+".  ");
				textf.setStyle("color:red");
				// font-weight:bold"
						
				if (boxL.isChecked() == true ){ status =  textf.getValue().toUpperCase(); }
				else if (boxR.isChecked() == true ){ status = info ;}
				 
			}
		return status;
		}
		
		
		
		
		
		public void setStatus() {
		
			
			if(text == null ){
				String status = "";
				
				
				if (boxL.isChecked() == true ){ status = "Left"; }
				else if  (boxR.isChecked() == true ){ status = "Right"; }
				else { status = ".....";  }
				
				Childkk.getChildByName(Statuschild).setContent(status);
				
			}
			
			else {
				
				String status = "";
								
				if (boxL.isChecked() == true ){ status = "Left"; }
				else if  (boxR.isChecked() == true ){ status = "Right"; }
				else { status = "....."; }
				
				Childkk.getChildByName(Statuschild).setContent(status);
				
				if ( textin == false ){
				XMLElement textfield = new XMLElement();
				textfield.setName("textfield");
				textfield.setContent(text.getText().trim());
				
				Childkk.addChild(textfield); 
				
				}else {  Childkk.getChildByName("textfield").setContent( text.getText() );  }
				
			}	
			
		}
		
		
	}
	
	public class Lateral{
		
		XMLElement Childkk;
		Groupbox gbox;
		Component parent;
		Hbox hbox;
		Checkbox boxL,boxM,boxR;
		Textbox text = null;
		Label lbl;
		String info;
		boolean textin;
		
		Lateral(XMLElement Childkk, Component parent){
			
			this.Childkk = Childkk;
			//this.gbox = gbox;
			this.parent = parent;
			
		}
		
		
		

		void createLateral(String left, String right){
			
			hbox = new Hbox();
			hbox.setParent(parent);
			
			boxL = new Checkbox();
			boxL.setLabel(left);
			boxL.setParent(hbox);
			
			boxR = new Checkbox();
			boxR.setLabel(right);
			boxR.setParent(hbox);
			
			boxM = new Checkbox();
			boxM.setLabel((String) Childkk.getAttribute("ID"));
			boxM.setParent(hbox);
			
			String status = "";
			if ( !(Childkk.getChildByName(Statuschild)== null)){
				
				status = Childkk.getChildByName(Statuschild).getContent().trim();
			}
			
			if ( status.contains("Bi-lateral")) { boxM.setChecked(true); }
			else if ( status.contains("Left")){ boxL.setChecked(true); } 
			else if ( status.contains("Right") ) { boxR.setChecked(true);   }
			
			
			this.info = Childkk.getChildByNumber(0).getContent();
			
			
		}
		

		void createLateral(String left, String right, int Cols, int Rows, String label, boolean sepa ){
			
			hbox = new Hbox();
			hbox.setParent(parent);
			
			boxL = new Checkbox();
			boxL.setLabel(left);
			boxL.setParent(hbox);
			
			boxR = new Checkbox();
			boxR.setLabel(right);
			boxR.setParent(hbox);
			
			boxM = new Checkbox();
			boxM.setLabel((String) Childkk.getAttribute("ID"));
			boxM.setParent(hbox);
			
			String status = "";
			if ( !(Childkk.getChildByName(Statuschild)== null)){
				
				status = Childkk.getChildByName(Statuschild).getContent().trim();
			}
			
			if ( status.contains("Bi-lateral")) { boxM.setChecked(true); }
			else if ( status.contains("Left")){ boxL.setChecked(true); } 
			else if ( status.contains("Right") ) { boxR.setChecked(true);   }
			
			if ( sepa ){
				
				Separator sep = new Separator();
				sep.setParent( parent );
			
			}
			
			text = new Textbox();
			text.setCols(Cols);
			text.setRows(Rows);
			text.setParent(parent);
			
			if ( !(Childkk.getChildByName("textfield")== null)){
				
				textin = true ;
				text.setText(Childkk.getChildByName("textfield").getContent().trim());
			}
			
			lbl =  new Label();
			lbl.setValue(label);
			lbl.setParent(hbox);
		
			this.info = Childkk.getChildByNumber(0).getContent();
			
			
		}
		
		String getStatus() {
			String status = null;
			if(text == null ){
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = String.format(info, "Bi-lateral").trim(); }
				else if (boxL.isChecked() == true ){ status = String.format(info, "Left").trim(); }
				else if  (boxR.isChecked() == true ){ status = String.format(info, "Right").trim(); }
				
			}
			
			else {
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = String.format(info, "Bi-lateral").trim()+" "+ text.getText().trim()+lbl.getValue().trim()+".  ";}
				else if (boxL.isChecked() == true ){ status = String.format(info, "Left").trim()+" "+ text.getText().trim()+ lbl.getValue().trim()+".  ";}
				else if (boxR.isChecked() == true ){ status = String.format(info, "Right").trim()+" "+ text.getText().trim()+lbl.getValue().trim()+".  ";}
				 
			}
		return status;
		}
		
		
		public void setStatus() {
		
			
			if(text == null ){
				String status = "";
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = "Bi-lateral"; }
				else if (boxL.isChecked() == true ){ status = "Left"; }
				else if  (boxR.isChecked() == true ){ status = "Right"; }
				else { status = ".....";  }
				
				Childkk.getChildByName(Statuschild).setContent(status);
				
			}
			
			else {
				
				String status = "";
				
				if (boxM.isChecked() == true || (boxL.isChecked() == true && boxR.isChecked() == true)){ status = "Bi-lateral"; }
				else if (boxL.isChecked() == true ){ status = "Left"; }
				else if  (boxR.isChecked() == true ){ status = "Right"; }
				else { status = "....."; }
				
				Childkk.getChildByName(Statuschild).setContent(status);
				
				if ( textin == false ){
				XMLElement textfield = new XMLElement();
				textfield.setName("textfield");
				textfield.setContent(text.getText().trim());
				
				Childkk.addChild(textfield); 
				
				}else {  Childkk.getChildByName("textfield").setContent( text.getText() );  }
				
			}	
			
		}

		
		
	}
	
	
	public class Cbox {
		
		XMLElement Childkk;
		Groupbox gbox;
		Component parent;
		Hbox hbox;
		Checkbox check;
		Textbox text = null;
		Label lbl;
		String Slabel,info,OGlabel;
		boolean checked,textin = false;
		
		Cbox(XMLElement Childkk, Component parent){
			
		this.Childkk = Childkk;
		
		//this.gbox = gbox;
		this.parent = parent;
		
		}
		
	

		void createCbox(){
			
			hbox = new Hbox();
			hbox.setParent(parent);
			
			check = new Checkbox();
			OGlabel = (String)Childkk.getAttribute("ID");
			check.setLabel( OGlabel );
			check.setParent(hbox);
			
			//System.out.println("status content is: "+Childkk.getChildByName(Statuschild).getContent());
			
			if ( !( Childkk.getChildByName(Statuschild) == null) ) {
				if( Childkk.getChildByName(Statuschild).getContent().length() > 0 ){
			if ( Childkk.getChildByName(Statuschild).getContent().equalsIgnoreCase("checked")){
				
				check.setChecked(true);
				checked = true;
				
			}}}
			
			if ( !(Childkk.getChildByName("TEXT") == null  ) ) {
			this.info = Childkk.getChildByName("TEXT").getContent(); }
			
		}
		
		void createCbox(int Cols, int Rows , String label, boolean sepa ){
			
			hbox =  new Hbox();
			hbox.setParent(parent);
			
			
			check = new Checkbox();
			OGlabel = (String)Childkk.getAttribute("ID");
			check.setLabel( OGlabel );
			check.setParent(hbox);
			
			if ( Childkk.getChildByName(Statuschild).getContent().equalsIgnoreCase("checked")){
				
				check.setChecked(true);
				checked = true;
			}
		
			if ( sepa ){
			Separator sep = new Separator();
			sep.setParent(parent); }
			
			text = new Textbox();
			text.setParent(parent);
			text.setCols(Cols);
			text.setRows(Rows);
			//System.out.println(OGlabel+" here isnt null text");
			
			if ( !(Childkk.getChildByName("textfield") == null )){
				
				textin = true;
				text.setText(Childkk.getChildByName("textfield").getContent().trim());
			}
			
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
			
			}else { 			
						
				if (check.isChecked() == true ){
										
					status = info.trim()+" "+text.getText().trim()+Slabel+".";
				}
			}	
			return status;
				
		}
		
		public void setStatus() {
			
			if(text == null){
				if (check.isChecked() == true && !checked ){
					
					//System.out.println("here, checked");
					//System.out.println("old content is: "+Childkk.getChildByName(Statuschild).getContent());
					Childkk.getChildByName(Statuschild).setContent("checked");
					//System.out.println("new bae 1 is: "+Childkk.getChildByName(Statuschild).getContent()+OGlabel);
				
				}else if ( checked && !check.isChecked()){
					
					Childkk.getChildByName(Statuschild).setContent(".....");
					//System.out.println("new bae 1.5 blank"+OGlabel);
				}
				
				}else { 			
							
					if (check.isChecked() == true && !checked ){
						
						Childkk.getChildByName(Statuschild).setContent("checked");
						
						
						if ( textin == false ){
						XMLElement textfield = new XMLElement();
						textfield.setName("textfield");
						textfield.setContent(text.getText().trim());
						Childkk.addChild( textfield );
						}else { Childkk.getChildByName("textfield").setContent( text.getText()) ; } 
						
						
						
						
						
						System.out.println("new bae 2.0");
					}
					
					else if ( checked && !check.isChecked() ) {
						
						
						Childkk.getChildByName(Statuschild).setContent(".....");
						//System.out.println("new bae 2.2 is checked with textbox");
						
						if ( textin ){
						if ( !Childkk.getChildByName("textfield").getContent().contentEquals(text.getText())){
							
							Childkk.getChildByName("textfield").setContent( text.getText() );
						}
						}else {
							
							XMLElement textfield = new XMLElement();
							textfield.setName("textfield");
							textfield.setContent(text.getText().trim());
							Childkk.addChild( textfield );							
						}						
					}else {
						//System.out.println("solo text");
						if ( textin == false ){
							XMLElement textfield = new XMLElement();
							textfield.setName("textfield");
							textfield.setContent(text.getText().trim());
							Childkk.addChild( textfield );
						}else { Childkk.getChildByName("textfield").setContent( text.getText()) ; } 						
					}
				}	
			
			
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
			
			//System.out.println(text.getText().trim());
			return text.getText();
			
		}
		
		public void setText() {
			
			
		}
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
