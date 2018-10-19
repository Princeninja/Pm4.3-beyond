package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import palmed.pmUser.Role;

import usrlib.XMLElement;
import usrlib.XMLParseException;


public class SoapShtExamWinController  {
	
	Textbox texta = null; 
	 
	 
	private final static String fn_config = "H&P.xml";
	//private Window win;
	
	public boolean read() {
		 
		 Window win = (Window) new Window();
		 //win.setParent(getWin());
		 
		 win.setClosable(true);
		 win.setId("H&P-exam");
		 //win.setBorder(false);
		 /*try {
			win.setMode("embedded");
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/
		 win.setVflex("1");
		 win.setHeight("1");
		 //win.doPopup();
		 
		XMLElement xml = new XMLElement();
		FileReader reader;
		
		
		try {
			reader = new FileReader(new File("C:"+File.separator +"Users"+File.separator +"program"+File.separator +"Heliosworkspace"+File.separator +"Pm"+File.separator +"WebContent"+File.separator +fn_config));
	    } catch (FileNotFoundException e) {
	    	System.out.println( "file not found:" + "the.." + File.separator + fn_config);
	    	return false;
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
	    
		XMLElement e;
		
		System.out.println( "root element=" + xml.getName());
		System.out.println( "num children=" + xml.countChildren());
		///**pulls up first page** System.out.println( "First page: " + xml.getChildByName( "PAGE" ) );
		// e = xml.getElementByPathName("EXAM");
		//System.out.println( xml.getChildByName("Heart"));
		// System.out.println( "Current Test: " + e.getDoubleAttribute("Type"));
		// System.out.println( "Current Test" + e.getAttribute("Type"));[PAGE/FINDING]
		// System.out.println( "Get Child 3: " + e.getChildByNumber( 4 ).getChildByNumber(0));
		 
		 String Name = xml.getName();
		 int NosChildren = xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		
		 Vbox mainVbox = new Vbox();
		 mainVbox.setVflex("1");
		 mainVbox.setHflex("1");
		 mainVbox.setParent(win);
		 
		 Tabbox Maintabbox = new Tabbox();
		 Maintabbox.setVflex("1");
		 Maintabbox.setHflex("1");
		 Maintabbox.setOrient("vertical");
		 Maintabbox.setParent(mainVbox);
		 
		 /*if( texta == null ){
		
			 System.out.println("jajabinks is loco");
		 
		 }*/
		 
		 
		 for ( int j=0; j< NosChildren ; j++){
		 XMLElement Childj = e.getChildByNumber( j );
		 
		 if ( Childj.getName().equals("PAGE")){
			// System.out.println("Creates a tab with the Label: "+Childj.getAttribute("label"));
			 Tab tab = new Tab();
			 tab.setLabel((String) Childj.getAttribute("label"));
			 //tab.setParent(Maintabbox);
			 
			 //System.out.println("Creates a tabpanel");
			 Tabpanel tabpanel = new Tabpanel();
			 tabpanel.setStyle("overflow:auto");
			 tabpanel.setVflex("1");
			 tabpanel.setHflex("1");
			 //tabpanel.setParent(tab);
		 }
		 
		 XMLElement Childjj = Childj.getChildByNumber(0);
		 
		 if (Childjj.getName().equals("SYSTEM")){
			 
			 for ( int k = 0; k < Childj.countChildren(); k++ ){
				 
				 XMLElement Childk = Childj.getChildByNumber(k);
				 Hbox hbox1 = new Hbox();
				 //hbox1.setParent(tabpanel);
				 
				 //System.out.println("Creates a groupbox-|");
				 Groupbox gbox = new Groupbox();
				 new Caption((String) Childk.getAttribute("ID")).setParent(gbox);
				 gbox.setParent(hbox1);
				 
				 //System.out.println("With caption: "+Childk.getAttribute("ID")+" and label: "+ Childk.getAttribute("label"));
				 new Label((String) Childk.getAttribute("label")).setParent(gbox);
				 //System.out.println("Creates a Vbox");
				 
				 
				 for( int l =0; l < Childk.countChildren(); l++){
				 XMLElement Childkk = Childk.getChildByNumber(l);
				 
				 if (Childkk.getName().equals("FINDING")){
					 if ( Childkk.getAttribute("Type").equals("checkbox")){
						 
						 System.out.println("Creates a "+Childkk.getAttribute("Type")+" named: "+Childkk.getAttribute("ID"));
						 
						 System.out.println(Childkk.getChildByNumber(0).getContent());
						 
						 Hbox hbox =  new Hbox();
						 Checkbox check = new Checkbox();
						 check.setLabel((String)Childkk.getAttribute("ID"));
						 check.setParent(hbox);
						 hbox.detach();
						
						 
					 }
					 else{
					System.out.println("Creates a "+Childkk.getAttribute("Type")+" named: "+Childkk.getAttribute("ID"));
					 
					  }
				 	}
				 } 
			 }
			 
		 }
		
		 
		 else if(Childjj.getName().equals("FINDING")){
		 for ( int i=0; i < Childj.countChildren(); i++ ){
			 
		 XMLElement Child11 = Childj.getChildByNumber(i);
		 if (Child11.getName().equals("FINDING")){
			 
			 
		// System.out.println("Creates a "+Child11.getAttribute("Type")+" named: "+Child11.getAttribute("ID"));
		 	}
		 
		 	}
		  } 
		 }
		 
		
		
		//win = (Window) Executions.createComponents(win, null, null);
		
		
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private Window getWin() {
		
		final Borderlayout bl = (Borderlayout) Path.getComponent("/pmMainWin/borderlayoutMain");
		final Center center = bl.getCenter();
		
		// TODO Auto-generated method stub
		return null;
	}












	public static boolean isDemo() {
		
		if( ! new File( Pm.getOvdPath() + "/" + fn_config ).exists()){
			System.out.println( "H&Pexam.isDemo(): fn_config does not exist" );
			return true;
		}
		
		System.out.println( "H&PExam.isDemo(). reading fn_congfig xml" );
		XMLElement xml = new XMLElement();
		FileReader reader;
		
		
		
		return false;
	}
	
	
	
}
