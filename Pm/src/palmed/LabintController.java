package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Var;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import usrlib.ZkTools;



public class LabintController extends GenericForwardComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String fn_config = "HL7Config.xml";
	private final static String fn_config1 = "Compendium0.xml";
	private final static String fn_config2 = "AOE Questions0.xml";
	
	
	private Window Labintwin;
	private Label PtName;
	
	private Listbox lboxProv;
	private Listbox lboxLabs;
	private Listbox lboxDiagnosis;
	
	private Checkbox Stat;
		
	//private Textbox NteNotes;
	
	private Textbox txtSearch;
	private Textbox txtSearch1;
	
	private Tab tabPanel;
	private Tab tabTest;
	
	private Listbox lboxPanel;
	private Listbox  lboxTest;
	private Listbox lboxDiag;
	
	private String ReqStr = "" ;
	
	private Rec ptRec = null;

	public LabintController() {
		//TODO Auto-generated constructor sub
		
	}
	
	public LabintController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public LabintController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
		// TODO Auto-generated constructor stub
	}

	
	@SuppressWarnings("unchecked")
	public void doAfterCompose (Component comp){
		try {
			super.doAfterCompose(comp);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		txtSearch.setFocus(true);
		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Rec> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// make sure we have valid ptRec
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "LabintController() invalid ptRec" );
			
        refreshinterface();	
				
		Prov.fillListbox(lboxProv, true);
		lboxProv.removeItemAt(0);
		
		refreshTestList( null );
		refreshPanelList( null );
		refreshDiagnosisList( null );
		
		return;
	}
	
	public void refreshinterface(){
		

		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
				
		// Get some patient values and set labels
		PtName.setValue( dirPt.getName().getPrintableNameLFM());
		
		ZkTools.listboxClear(lboxLabs);
		ZkTools.listboxClear(lboxDiagnosis);
		Abbr.clear();
		Desc.clear();
		Code.clear();
		CodeDesc.clear();
		AbbList.clear();
		AbbListi.clear();
		txtSearch.setValue("");
		txtSearch1.setValue("");
		//NteNotes.setValue("");
		ZkTools.setListboxSelectionByLabel(lboxProv, null);
		Stat.setChecked(false);
		ReqStr = "";
		
		refreshTestList( null );
		refreshPanelList( null );
		refreshDiagnosisList( null );
		
	}
	
	
	public void onClick$PatientSel (Event ev) throws InterruptedException{
		
		// is user sure?
		if ( Messagebox.show( "All current information entred will be deleted!", "Change Patient?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES ) return;

		
		
		ptRec = new Rec( PtSearch.show()); 
		if ( ptRec.getRec() < 2 ){
			return;
		}			
	    refreshinterface();
	    
	}
	
	public void refreshDiagnosisList	( String searchString ){

		if ( lboxDiag == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxDiag );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();		
		
		/*
		 // populate list
		Dgn.fillListbox( lboxDiag, null, null, searchString, Dgn.Status.ACTIVE );}
		
		else{Dgn.fillListbox( lboxDiag, false);} */
		
		// populate list

		Dgn dgn = Dgn.open();
		
		while ( dgn.getNext()){
			
			int fnd = 1;
			
			// get dgn code  validity byte
			Dgn.Status status = dgn.isValid() ? Dgn.Status.ACTIVE: Dgn.Status.INACTIVE;
			// is this type selected?
			if (( status == Dgn.Status.ACTIVE ) /*|| ( status == Dgn.Status.INACTIVE )*/){
			
				if (( searchString != null )
					&& (//( dgn.getAbbr().toUpperCase().indexOf( s ) < 0 )
					 ( dgn.getDesc().toUpperCase().indexOf( s ) < 0 )
					//&& (dgn.getCode(1).toUpperCase().indexOf( s ) < 0 )
					//&& (dgn.getCode(2).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(3).toUpperCase().indexOf( s ) < 0 )
					//&& (dgn.getrv().getPrintable().toUpperCase().indexOf(s)<0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new List item and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( lboxDiag );
					i.setValue( dgn.getRec());
					
					Listcell Desc = new Listcell( dgn.getDesc());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(i);
					
					Listcell C3 = new Listcell(dgn.getCode(3));
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(i);
					C3.setVisible(false);
								    
				}
			}	
		}
		
		
		dgn.close();
		
		return;
	}

   	
	public void refreshTestList( String searchString ){
		
		if ( lboxTest == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxTest );
		
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		

		LabObsTbl lab = LabObsTbl.open();
		int display = 1;
		while ( lab.getNext()){
			
			int fnd = 1;			
			
			// get problem status byte
			LabObsTbl.Status status = lab.getStatus();
			// is this type selected?
			if ((( status == LabObsTbl.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == LabObsTbl.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				
				

				if (( searchString != null )
					&& ( lab.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( lab.getDesc().toUpperCase().indexOf( s ) < 0 )
					){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( lboxTest );
					i.setValue( new Rec( lab.getRec().getRec()));
					
					String Panel = lab.getAbbr()+", "+lab.getDesc();
					new Listcell( Panel ).setParent( i );
					
				}
			}
		}
		
		lab.close();
		
		/*if ( lboxTest == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxTest );
		
		// search string passed?
		String s = searchString;
		if ( s != null ){ s = s.toUpperCase();		
		
		// populate list
		LabObsTbl.fillListbox( lboxTest, null, null, searchString, LabObsTbl.Status.ACTIVE );}
		
		else{LabObsTbl.fillListbox( lboxTest, false);}*/
		
		return;
	} 

		

    
	public void refreshPanelList( String searchString ){

		if ( lboxPanel == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxPanel );
		
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
			
		// populate list
		//LabBat.fillListbox( lboxPanel, null, null, searchString, LabBat.Status.ACTIVE );}
		
		//else{LabBat.fillListbox( lboxPanel, false );
		//} 
		
		// populate list

		LabBat lab = LabBat.open();
		int display = 1;
		while ( lab.getNext()){
			
			int fnd = 1;			
			
			// get problem status byte
			LabBat.Status status = lab.getStatus();
			// is this type selected?
			if ((( status == LabBat.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == LabBat.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				// ( status == probTbl.Status.REMOVED ) - removed
				// ( status == probTbl.SUPERCEDED ) - superceded by edit
				

				if (( searchString != null )
					&& ( lab.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( lab.getDesc().toUpperCase().indexOf( s ) < 0 )
					){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( lboxPanel );
					i.setValue( new Rec( lab.getRec().getRec()));
					
					String Panel = lab.getAbbr()+", "+lab.getDesc();
					new Listcell( Panel ).setParent( i );
					
				}
			}
		}
		
		lab.close();
		
		return;
	}
	//-- Search for matching entries in corresponding listbox
	public void onOK$txtSearch1( Event ev) { onClick$btnSearch1( ev ); }
	
	public void onClick$btnSearch1( Event ev ){

		String s = txtSearch1.getValue().trim();
		if ( s.length() == 0 ) s = null;
		
		
			ZkTools.listboxClear( lboxDiag );		
			refreshDiagnosisList( s );
			
		return;
	}
	

	
	// Search for matching entries in corresponding listbox
	
	public void onOK$txtSearch( Event ev) { onClick$btnSearch( ev ); }
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		if ( s.length() == 0 ) s = null;
		
		if ( tabPanel.isSelected()){

			ZkTools.listboxClear( lboxPanel );		
			refreshPanelList( s );
			
		} else if ( tabTest.isSelected()){
			
			ZkTools.listboxClear( lboxTest );		
			refreshTestList( s );
		}
		
		return;
	}
	
	List<String> Abbr = new ArrayList<String>();
	List<String> Desc = new ArrayList<String>();
	List<String> Code = new ArrayList<String>();
	List<String> CodeDesc = new ArrayList<String>();
	
	final List<Object> AbbList = new ArrayList<Object>();
	final List<Integer> AbbListi = new ArrayList<Integer>();	
	
	// Select a an entry from the tests search tables	
	public void onClick$btnSelect( Event ev ){
		
		boolean Duplicate = false;
		boolean AOE = false;
		String Abbrev = null;
		if ( tabTest.isSelected()){
			// make sure an item was selected
			if ( lboxTest.getSelectedCount() < 1 ) return;
			Rec obsRec = (Rec) ZkTools.getListboxSelectionValue( lboxTest );
			LabObsTbl obs = new LabObsTbl( obsRec );
						
			for (int i=0; i < lboxLabs.getItemCount(); i++){
				
				String item = lboxLabs.getItemAtIndex(i).getLabel().toString();
				int pnos = item.indexOf(",");
				
				String Abb = "";
				if(pnos != -1){
				  Abb = lboxLabs.getItemAtIndex(i).getLabel().substring(0, pnos);
			    }
				
				if ( Abb.contains(obs.getAbbr()) ) { Duplicate = true; }
		
		    }
			
			if ( !Duplicate ){
			Listitem lab = new Listitem();
			lab.setLabel(obs.getAbbr()+","+obs.getDesc());
			Abbrev = obs.getAbbr();
			Abbr.add(Abbrev);
			Desc.add(obs.getDesc());		
			lab.setParent(lboxLabs);  }
			
			else { try {Messagebox.show("That Test has already been selected!");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
				
			}

			
		} else if ( tabPanel.isSelected()){
			
			// make sure an item was selected
			if ( lboxPanel.getSelectedCount() < 1 ) return;
			Rec batRec = (Rec) ZkTools.getListboxSelectionValue( lboxPanel );
			LabBat bat = new LabBat( batRec );
			
		 for (int i=0; i < lboxLabs.getItemCount(); i++){
				
				String item = lboxLabs.getItemAtIndex(i).getLabel().toString();
				int pnos = item.indexOf(",");
				
				String Abb = "";
				if(pnos != -1){
				  Abb = lboxLabs.getItemAtIndex(i).getLabel().substring(0, pnos);
			    }
				
				if ( Abb.contains(bat.getAbbr()) ) { Duplicate = true; }
		
		    }
			
			if ( !Duplicate ){ 
			Listitem lab = new Listitem();
			lab.setLabel(bat.getAbbr()+","+bat.getDesc());
			Abbrev = bat.getAbbr();
			Abbr.add( Abbrev );
			Desc.add(bat.getDesc());	
			lab.setParent(lboxLabs); }
			
			else { try {Messagebox.show("That Panel has already been selected!");  } catch  (InterruptedException e) { /*ignore*/ }
			return;	}
		}
		
		
		List<Object> FF = new  ArrayList<Object>();
		
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
			
		try {
			reader = new FileReader( Pm.getOvdPath() + File.separator + fn_config1 );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config1 + "file was not found:" );
	    	
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
		 //System.out.println(Name+" Name is config1");
		 final int NosChildren = xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		
		for ( int i = 0; i < NosChildren; i++){
		
		 // 11/6 contains versus equals
		 //if ( e.getChildByNumber(i).getChildByNumber(0).getContent().contains(Abbrev) ){
		  if ( e.getChildByNumber(i).getChildByNumber(0).getContent().equalsIgnoreCase(Abbrev) ){
			  
		 if ( e.getChildByNumber(i).getChildByName("HAS_AOE").getContent().equalsIgnoreCase("TRUE") ){
			 
			 AOE = true;
		 }}}
		
		if (AOE){ ModalQuestions( Abbrev, FF ); AbbListi.add(lboxLabs.getItemCount()); }
		
		//AbbList.add(FFTxts);
		
		return;
	}
	

	
	private void ModalQuestions( String Abbrev, final List<Object> FF  ) {
		// TODO Auto-generated method stub
				
		 final Window MQuestions = new Window();	
			
		 MQuestions.setParent(Labintwin);
		 MQuestions.setPosition("center");	
		 //MQuestions.setHeight("302px"); 
		 MQuestions.setWidth("813px");
		 MQuestions.setVflex("min");
		 MQuestions.setHflex("1");
		 MQuestions.setBorder("normal");
		
		 
		 Groupbox gboxdd =  new Groupbox();
		 gboxdd.setHflex("max");
		 gboxdd.setParent(MQuestions);
		 
		 Grid grid00 = new Grid();
		 grid00.setParent(gboxdd);
		 
		 Rows rows00 = new Rows();
		 rows00.setParent(grid00);
		 
		  
		 
		XMLElement xml = new XMLElement();
		FileReader reader = null;
			
		try {
			reader = new FileReader( Pm.getOvdPath() + File.separator + fn_config2 );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config2 + "file was not found:" );
	    	
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
		 //System.out.println(Name+": Name is config2");
		 final int NosChildren = xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		 
		 String Caption = "";
		 String Res_Code = "";
		 String Label = "";
		 int MaxV = 0; 
		 int size = 50;
		
		 for ( int i = 0; i < NosChildren; i++){
			 
			 //11/6 Contains vs equals
			 // if ( e.getChildByNumber(i).getChildByNumber(0).getContent().contains(Abbrev) ){
			 if ( e.getChildByNumber(i).getChildByNumber(0).getContent().equalsIgnoreCase(Abbrev) ){
				 
				 //System.out.println("rescode alpha: "+ e.getChildByNumber(i).getChildByNumber(1).getContent());
				 	Caption = e.getChildByNumber(i).getChildByNumber(0).getContent();
				 	Res_Code = e.getChildByNumber(i).getChildByNumber(1).getContent();
				 	MaxV = Integer.parseInt(e.getChildByNumber(i).getChildByName("MAX_VALUE").getContent());
				    if ( MaxV > 0 ) { size = MaxV; }
				 	
				 if ( e.getChildByNumber(i).getChildByName("FIELD_TYPE").getContent().contains("DD") ){
					 
					 Label = e.getChildByNumber(i).getChildByNumber(2).getContent(); 
 					 
					 String rowname = "row0"+ Integer.toString(i);
					 Row row00 = new Row();
					 row00.setParent(rows00);
					 row00.setId(rowname);
					 
					 		 
					 Label l00 = new Label();
					 l00.setValue(Label+": ");
					 l00.setParent(row00);					 
					 
					//System.out.println("Abbrev, Rescode:"+ Abbrev +","+ Res_Code);
					 DropDown DD = new DropDown(Abbrev, Res_Code, row00);
					 FF.add(DD);
					 //DropDown( Abbrev, Res_Code, row00 ); }
				 }
				 
				 if ( e.getChildByNumber(i).getChildByName("FIELD_TYPE").getContent().contains("ST") ){ 
					 
					
					 Label = e.getChildByNumber(i).getChildByNumber(2).getContent(); 
					 
					 String rowname = "row0"+ Integer.toString(i);
					 Row row00 = new Row();
					 row00.setParent(rows00);
					 row00.setId(rowname);
					 
					 		 
					 Label l00 = new Label();
					 l00.setValue(Label+": ");
					 l00.setParent(row00);
					 
					// System.out.println("Abbrev, Rescode:"+ Abbrev +","+ Res_Code);
					 ST StrT =  new ST();
					 StrT.StringT( row00, Res_Code, size );
					 //System.out.println("Rescode:"+ Res_Code);
					 FF.add(StrT);
					 //StringT( row00 ); 
					 }
				 
					 
				 
				 if ( e.getChildByNumber(i).getChildByName("FIELD_TYPE").getContent().contains("FT") ){ 
					 
					
					 Label = e.getChildByNumber(i).getChildByNumber(2).getContent(); 
					 
					 String rowname = "row0"+ Integer.toString(i);
					 Row row00 = new Row();
					 row00.setParent(rows00);
					 row00.setId(rowname);
					 
					 		 
					 Label l00 = new Label();
					 l00.setValue(Label+": ");
					 l00.setParent(row00);
					 
					 //System.out.println("Abbrev, Rescode:"+ Abbrev +","+ Res_Code);
					 FT FrT = new FT();
					 FrT.FreeText(row00, Res_Code, size);
					 //System.out.println("Rescode:"+ Res_Code);
					 FF.add(FrT);
					 //FreeText( row00 ); 
					 
					 }
				 
			 }
			 
			 
		 }
		
		 new Caption (Caption+": ").setParent(gboxdd);	 
		
		 Hbox SecondHbox = new Hbox();
			SecondHbox.setHeight("26px");
			SecondHbox.setWidth("200px");
			SecondHbox.setPack("start");
			SecondHbox.setParent(MQuestions);
			
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
						
						if ( Messagebox.show( "Save all the information entered? "," Save! ", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
						
						ListStr FFT = new ListStr();
						
						if (FF.size() >0 ){
						for ( int i =0; i < FF.size(); i++ ){
														
							String[] Str =  getStrs(FF.get(i));
							
							//if ( Str[2].toString().length() > 0 ) {
							FFT.add(Str); 	
						 							
						}}
						AbbList.add(FFT);
						
						
						//System.out.println("List size pre modal: "+ AbbList.size());
						MQuestions.detach();
						FF.clear();
						
					}});
		
			
			Button Close = new Button();
			Close.setParent(end);
			Close.setLabel("Cancel");
			Close.setWidth("65px");
			Close.setHeight("23px");
			Close.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					if ( Messagebox.show( "Canceling will remove the Panel/Test. "," Cancel ?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
					
					int nos = lboxLabs.getItemCount()-1;
					  
					 //System.out.println("nos is:"+nos);
					lboxLabs.removeItemAt(nos);
					Abbr.remove(nos);
					Desc.remove(nos);					
					MQuestions.detach();
					FF.clear();
																
					}
					
				});	
				
			
			MQuestions.doOverlapped();
			/*try {
				MQuestions.doModal();
			} catch (SuspendNotAllowedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		
		
		
	}

	

	

	

	//Duplicate the last entered diganosis code
	  public void onClick$btnduplicate ( Event ev ){
		  
		  if ( lboxDiagnosis.getItemCount() < 1 ){
				try {Messagebox.show( "There has to be at least 1 Diagnosis Code added. ");  } catch  (InterruptedException e) { /*ignore*/ }
				return;
			}
		  
		  int nos = lboxDiagnosis.getItemCount()-1;
		  
		  String Diagbox, Codestr, CodeDescstr;
		  		  
		  Codestr = Code.get(nos);
		  Code.add(Codestr);
		  
		  CodeDescstr = CodeDesc.get(nos);
		  CodeDesc.add(CodeDescstr);
		  
		  Diagbox = CodeDescstr+","+Codestr;
		  Listitem diag = new Listitem();
		  diag.setLabel(Diagbox);
		  diag.setParent(lboxDiagnosis);
		  
		  //System.out.println(Code.size()+","+CodeDesc.size());
		  
		  
	  }	
  
	// Select a an entry from the tests search tables	
	public void onClick$btnSelect1( Event ev ){
		
		boolean Duplicate = false;
							
		// make sure an item was selected
		if ( lboxDiag.getSelectedCount() < 1 ) return;
		Rec dgnRec = (Rec) ZkTools.getListboxSelectionValue( lboxDiag );
		Dgn dgn = new Dgn( dgnRec );
		Listitem diag = new Listitem();
		diag.setLabel(dgn.getDesc()+","+dgn.getCode(3));
		Code.add(dgn.getCode(3));
		CodeDesc.add(dgn.getDesc());
			
			
		for (int i=0; i < lboxDiagnosis.getItemCount(); i++){
			
			String item = lboxDiagnosis.getItemAtIndex(i).getLabel().toString();
			int pnos = item.indexOf(",");
			
			String Abb = "";
			if(pnos != -1){
				Abb = lboxDiagnosis.getItemAtIndex(i).getLabel().substring(0, pnos);
		    }
		
			if ( Abb.contains(dgn.getDesc()) ) { Duplicate = true; }
		
		    }
			
			if ( !Duplicate ){ diag.setParent(lboxDiagnosis); }
			else { try {Messagebox.show("That Diagnosis Code has already been selected!");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
				
			}

		return;
	}
	
  public void onClick$btndelete ( Event ev ){
	  
	  //int selected = lboxLabs.getSelectedCount();
	  // System.out.println("selected is: "+ selected );
	  if ( lboxLabs.getSelectedCount() < 1 ){
			try {Messagebox.show( "No Lab is currently selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		}
	  
	  int nos = lboxLabs.getSelectedItem().getIndex();
	  
	  //System.out.println("nos is:"+nos);
	  lboxLabs.removeItemAt(nos);
	  Abbr.remove(nos);
	  Desc.remove(nos);
	  if ( AbbList.size() >= nos+1){
	  AbbList.remove(nos); }
	  
	 // System.out.println("List size is: "+lboxLabs.getItemCount());
	  //System.out.println("Lists size are: "+Abbr.size()+","+Desc.size());
	  //System.out.println("item at 0 "+Abbr.get());
	  
  }	
  
  public void onClick$btndelete1 ( Event ev ){
	  
	  if ( lboxDiagnosis.getSelectedCount() < 1 ){
			try {Messagebox.show( "No Diagnosis is currently selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		}
	  
	  int nos = lboxDiagnosis.getSelectedItem().getIndex();
	  
	  lboxDiagnosis.removeItemAt(nos);
	  Code.remove(nos);
	  CodeDesc.remove(nos);
	
  }	

	
   public void onClick$btnSubmit( Event ev ) throws InterruptedException{
	   
		// is user sure?
		if ( Messagebox.show( "Do you really wish to complete and save this Lab Request?", "Save Lab Request?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES ) return;

	   
	   submit();
	   	   
   }
   
   
   public void onClick$btnPrint( Event ev ){
	   
	   if ( ReqStr.length()<= 1) {
		   
		   try {Messagebox.show( "Please complete and save a Lab request first. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		   
	   }
	   
	   else{
	   // Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", ReqStr );
		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		//System.out.println(amedia.getContentType());
		//System.out.println("amedia is: "+amedia.getStringData());
		iframe.setParent( Labintwin );
		//Labintwin.appendChild(iframe);
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

   private void submit() {
	  if (!( lboxProv.getSelectedIndex() >= 0 )){
			try {Messagebox.show( "Please select a Provider. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		}
	   
	   if ( lboxLabs.getItemCount() < 0 ){
			try {Messagebox.show( "No Lab has been selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		}
	   
	   if ( lboxLabs.getItemCount() != lboxDiagnosis.getItemCount() ){
			try {Messagebox.show( "Please make sure number of Tests equal that of Diagnoses. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
		}

		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		
		Name name = dirPt.getName();
		String namelfm = name.getLastName()+"^"+name.getFirstName()+"^"+name.getMiddleName();
		//String ptssn = dirPt.getSSNDSH();
		String ssn = dirPt.getSSN(false);
		//String ptnum = dirPt.getPtNumber();
		String ptsex = dirPt.getSex().getAbbr();
		String ptdob = dirPt.getBirthdate().getPrintable( 8 );
		Date.getPrintableAge( dirPt.getBirthdate());
		
		dirPt.getRace().getLabel();
		dirPt.getEthnicity().getLabel();
		dirPt.getLanguage().getLabel();

		
		// patient address
		
		String s2, s3 ,s4, s5 = null;
		String address1 = dirPt.getAddress().getPrintableAddress(1);
		String address2 = s2 = dirPt.getAddress().getPrintableAddress(2);
		s3 = "";
		if ( s2.trim().length() == 0 ){
			address2 = s3 ;
		} else {
		}
		
	    s4 = address1+"^"+address2;
	    s5 = address1+"-"+address2;
		
	    String work_ph = dirPt.getAddress().getWork_ph();
	    //System.out.println("work ph: "+work_ph);
	    if (work_ph.length() == 8 ){
	    	work_ph = "573"+ work_ph;
	    	work_ph = "("+work_ph.substring(0,3)+")"+ work_ph.substring(3, work_ph.length());
	 	    
	    } 
	   
		String home_ph = dirPt.getAddress().getHome_ph();
		if ( home_ph.length() == 8){		  
	    	home_ph = "573"+ home_ph;
	    	home_ph = "("+home_ph.substring(0,3)+")"+ home_ph.substring(3, home_ph.length());
			   
	    } 
			
		//number of inusrance info
		int n = 0;
		
		if (!(dirPt.getPpo(1).getRec() < 2) && (dirPt.getPpo(1) != null) ) { n = 1 ;}
		//Ppo ppo1 = new Ppo ( dirPt.getPpo(1));
		//System.out.println("ins1 name: "+ppo1.getname());
		//System.out.println("ins1 abbr: "+ppo1.getAbbr());
		if (!(dirPt.getPpo(2).getRec() < 2) && (dirPt.getPpo(2) != null) ) { n = 2 ;}
		//System.out.println("n2: "+ n);
		if (!(dirPt.getPpo(3).getRec() < 2) && (dirPt.getPpo(3) != null) ) { n = 3 ;} 
		//System.out.println("rec: "+ dirPt.getPpo(1).getRec()+","+ dirPt.getPpo(2).getRec()+ ","+ dirPt.getPpo(3).getRec());
		//System.out.println("n3: "+ n);
		
		
		if ( n != 0 ) { 
			Ppo ppo = new Ppo ( dirPt.getPpo(1));
			if (ppo.getname().length()<2) { n = 0;}
		}	 
		
		if ( n > 1) { 
			
			Ppo ppo = new Ppo ( dirPt.getPpo(2));
			if (ppo.getname().length()<2) { n = 1;}
		}
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
		
		
		try {
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
		
		File LabReqFile = new File( Pm.getOvdPath() + File.separator +"LabReq.txt");
		String labreqString = "";
		try {
			labreqString = FileUtils.readFileToString(LabReqFile);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		    
		final XMLElement e;
				
		 String Name = xml.getName();
		 xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		 
		 StringBuilder Lab = new StringBuilder();
		 
		 String pipe = e.getChildByNumber(0).getChildByNumber(1).getContent();
		 //System.out.println("pipe is: "+pipe);
		 
		 java.util.Calendar calendar = GregorianCalendar.getInstance();
		 int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		 int mins = calendar.get(java.util.Calendar.MINUTE);
		 int seconds = calendar.get(java.util.Calendar.SECOND);
		 
		 //System.out.println("hr: "+hour+"mins: "+mins+"seconds: "+seconds); 
		 
		 Date today = usrlib.Date.today();
		 
		 String date = today.getPrintable(8)+Integer.toString(hour)+Integer.toString(mins)+ String.format("%02d", seconds);
		 String datecode = "RFM"+date;
			 
		 XMLElement MSH = e.getChildByNumber(0);
		 Lab.append(MSH.getChildByNumber(0).getContent()+pipe+MSH.getChildByNumber(2).getContent()+pipe+MSH.getChildByNumber(3).getContent()+pipe+
				    MSH.getChildByNumber(4).getContent()+pipe+MSH.getChildByNumber(5).getContent()+pipe+MSH.getChildByNumber(6).getContent()
				    +pipe+date+pipe+pipe+MSH.getChildByNumber(7).getContent()+pipe+datecode+pipe+MSH.getChildByNumber(8).getContent()+pipe+MSH.getChildByNumber(9).getContent()+System.getProperty("line.separator"));
		
		 String Payt ="";
		 if (n == 0){
		          Payt = "P"; }	     							//no insurance
	     else { Payt = "T"; }//insurance
	    	 
	    XMLElement PID = e.getChildByNumber(1);
	    Lab.append(PID.getChildByNumber(0).getContent()+StringUtils.repeat(pipe,3)+ptRec.toString()+pipe+pipe+namelfm
	    		   +pipe+pipe+ptdob+pipe+ptsex+StringUtils.repeat(pipe,3)+s4+"^"+dirPt.getAddress().getCity()+"^"+dirPt.getAddress().getState()+"^"+dirPt.getAddress().getZip_code()
	    		   +StringUtils.repeat(pipe,2)+home_ph.toString()+pipe+work_ph+StringUtils.repeat(pipe,4)+"^^^"+Payt+pipe+ssn+StringUtils.repeat(pipe,3)+System.getProperty("line.separator"));
	   
	    labreqString = labreqString.replace("$PatientID", "PatientID: "+ptRec.toString());
	    labreqString = labreqString.replace("$LName", name.getLastName());
	    labreqString = labreqString.replace("$FName", name.getFirstName());
	    labreqString = labreqString.replace("$Middle", name.getMiddleName());
	    labreqString = labreqString.replace("$BirthDate",  dirPt.getBirthdate().getPrintable(10));
	    labreqString = labreqString.replace("$Sex", ptsex);
	    labreqString = labreqString.replace("$PTAddress1", s5);
	    labreqString = labreqString.replace("$PTAddress2", "");
	    labreqString = labreqString.replace("$PTCity", dirPt.getAddress().getCity());
	    labreqString = labreqString.replace("$PTSt", dirPt.getAddress().getState());
	    labreqString = labreqString.replace("$PTZip", dirPt.getAddress().getZip_code());
	    labreqString = labreqString.replace("$PTPhone1", home_ph.toString());
	    labreqString = labreqString.replace("$PTPhone2", work_ph);
	    labreqString = labreqString.replace("$SSN", ssn);
	    
	    
	    
		
	    XMLElement IN = e.getChildByNumber(2);
	    String in = IN.getChildByNumber(0).getContent();
	    String i17 = IN.getChildByNumber(1).getContent();
		
	    String Instxt = "No Insurance";
	    StringBuilder InsSB = new StringBuilder();
	    if (n != 0){
	    for (int j=1; j<n+1; j++) {
	    	//System.out.println("j :"+j);
	    	Lab.append(Insure(j , ptRec, pipe, namelfm, dirPt.getBirthdate().getPrintable(10), in, i17,InsSB));
	    	
	    }}
	    
	    if ( InsSB.toString().length() > 0 ) { Instxt = InsSB.toString();}
	    labreqString = labreqString.replace("$Insurance", Instxt);
	    //System.out.println("Insurance txt"+ Instxt);
	    
		XMLElement ORC = e.getChildByNumber(3);
		Lab.append(ORC.getChildByNumber(0).getContent()+pipe+ORC.getChildByNumber(1).getContent()+pipe+datecode+pipe+ORC.getChildByNumber(2).getContent()+System.getProperty("line.separator"));
		
		labreqString = labreqString.replace("$ORC2", datecode);
		
		//if (NteNotes.getValue().length() != 0){
			
			//XMLElement NTE = e.getChildByNumber(4);
			//Lab.append(NTE.getChildByNumber(0).getContent()+pipe+NTE.getChildByNumber(1).getContent()+pipe+pipe+NteNotes.getValue()+System.getProperty("line.separator"));
		 //}
		
		XMLElement OBR = e.getChildByNumber(5);
		XMLElement DGN = e.getChildByNumber(6);
		XMLElement OBX = e.getChildByNumber(7);
		
		//String OBXtxt = "";
		//StringBuilder OBXSB = new StringBuilder();
		int x = 0; 
		StringBuilder OBRSB = new StringBuilder();
		
		for ( int i=0; i<lboxLabs.getItemCount(); i++ ){
			
			List<String[]> StrList = null;
			
			//System.out.println("size is:"+ AbbListi.size());
			//System.out.println("list is is:"+  AbbList.toString());
			
			int q = -1;
			for ( int r = 0; r<AbbList.size(); r++ ){
			  
			 if ( AbbListi.get(r) == i+1) { q = r; }
			  	 
			}
			
			//System.out.println("r n q :"+q);
			
			if ( q > -1 ){
				
			 StrList = OBXList (AbbList.get(q));	
			 
			}
			
			String stat = "R";
			
			if ( Stat.isChecked() == true ) { stat = "S"; }
			
			Rec ProvRec = (Rec) lboxProv.getSelectedItem().getValue();
			Prov provider = new Prov( ProvRec );
			String Provider = provider.getNPI()+"^"+provider.getLastName()+"^"+provider.getFirstName();
			
			Lab.append(OBR.getChildByNumber(0).getContent()+pipe+Integer.toString(i+1)+pipe+datecode+pipe+pipe+Abbr.get(i)+"^"+Desc.get(i)
					  +pipe+stat+StringUtils.repeat(pipe,11)+Provider+System.getProperty("line.separator"));
			
			
			if (x > 0 ){
				
				OBRSB.append(System.getProperty("line.separator"));
			}
			
			OBRSB.append("OBR - "+Integer.toString(i+1)+"  "+Abbr.get(i)+"		"+ Desc.get(i)+ "	"+ stat+ " "+ provider.getNPI()+ " "+ provider.getLastName()+ "  "+ provider.getFirstName() );
			
			/*labreqString = labreqString.replace("$TestAbbrev",Abbr.get(i) );
			labreqString = labreqString.replace("$TestDescr", Desc.get(i) );
			labreqString = labreqString.replace("$Priority", stat );
			labreqString = labreqString.replace("$ProviderID", provider.getNPI());
			labreqString = labreqString.replace("$DocLast", provider.getLastName());
			labreqString = labreqString.replace("$DocFirst", provider.getFirstName()); */
			
			Lab.append(DGN.getChildByNumber(0).getContent()+pipe+Integer.toString(i+1)+pipe+DGN.getChildByNumber(1).getContent()+pipe+Code.get(i)+pipe+CodeDesc.get(i)
					  +System.getProperty("line.separator"));
			
			//labreqString = labreqString.replace("$DGN", CodeDesc.get(i)+"-"+Code.get(i));
			OBRSB.append(System.getProperty("line.separator"));
			OBRSB.append(CodeDesc.get(i)+"-"+Code.get(i));
			OBRSB.append(System.getProperty("line.separator"));
			int y = x +1;
						
			int k = 0;
			String Str2 = "";
			String Type = "", Label ="", Content ="";
			
			if ( StrList != null ) {
			for ( int j=0; j< StrList.size(); j++){
			
			
			//if ( Str[2].toString().length() > 0 ) {
			String[] Str =  StrList.get(j);
			
			//Type,Label,Content
			if ( Str != null ){
			Type = Str[0];
			Label = Str[1];
			Content = Str[2];
			Str2 = Content;
			/*System.out.println("j is:"+j);
			System.out.println(" Beta Str[0]:"+ Str[0]);
			System.out.println(" Beta Str[1]:"+ Str[1]);
			System.out.println(" Beta Str[2]:"+ Str[2]);*/
			
			}
			
			
			
			
			
			//System.out.println("pre k: "+k);
			//System.out.println("Str2 length"+Str2.length());
			if ( Str2.length() > 0 ) {
			
			int l =  k + 1;
			k = l ; 
			//System.out.println("post k: "+k);
			Lab.append(OBX.getChildByNumber(0).getContent()+pipe+Integer.toString(k)+pipe+Type+pipe+Label+pipe+pipe+Content+ System.getProperty("line.separator")); 
			OBRSB.append(Label);
			OBRSB.append(System.getProperty("line.separator"));
			OBRSB.append(Content);
			OBRSB.append(System.getProperty("line.separator")); 			
			
			
			}
					
			}
			
			//if ( OBXSB.toString().length() > 0 ) { OBXtxt = OBXSB.toString(); }
			//OBRSB.append(OBXtxt);
			//OBRSB.append(System.getProperty("line.separator")); 
			
			}
			
			x = y;
		}
	    
		
		labreqString = labreqString.replace("$OBX", OBRSB.toString());
		
		labreqString = labreqString.replace("*Done*", "End");
		
		String path =  e.getChildByNumber(8).getContent();
		String truepath = path +datecode+".txt";
		//String labreqpath = path+"LabReq"+datecode+".txt";
		
		System.out.println("path: "+truepath);
		//System.out.println("labreq path: "+labreqpath);
		
		File LabRequest = new File(truepath);
		//File LabReq = new File(labreqpath);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<HTML>" );
		sb.append( "<HEAD>" );
		
		sb.append( "<style type='text/css'>" );
		sb.append("@page"+ System.getProperty("line.separator") );
		sb.append("{"+ System.getProperty("line.separator") );
		sb.append("    size: auto;"+ System.getProperty("line.separator") );
		sb.append("    margin: 0mm;"+ System.getProperty("line.separator") );
		sb.append("}"+ System.getProperty("line.separator") );
		sb.append(" body { margin: 0.875cm; }"+ System.getProperty("line.separator") );
		sb.append(" div { line-height: normal; }"+ System.getProperty("line.separator") );
		sb.append( "</style>" );
		sb.append( System.getProperty("line.separator") );
		
		String forhtml = "<div><p>"+labreqString+"</p></div>";
		forhtml = forhtml.replace( System.getProperty("line.separator"), "<br>");
		sb.append( forhtml );
		
		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		sb.append( "</BODY>" );
		sb.append( "</HTML>" );
		
		String html = sb.toString();
		
		ReqStr = html;
		//System.out.println("Req Str: "+ReqStr);
		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );
		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		//System.out.println(amedia.getContentType());
		//System.out.println("amedia is: "+amedia.getStringData());
		iframe.setParent( Labintwin );
		//Labintwin.appendChild(iframe);
		
		
		
		try {
			FileUtils.writeStringToFile(LabRequest, Lab.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		try {
			FileUtils.writeStringToFile(LabReq, labreqString);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} */
		
		//System.out.println(forhtml);
		//System.out.println(labreqString);		
		//System.out.println(Lab.toString());
		 
		 
    }

   private String Insure(int n, Rec ptRec, String pipe, String name, String ptdob, String in, String i17, StringBuilder InsSB ) {
	   	
	   	DirPt dirPt = new DirPt( ptRec );
	   
	   	Ppo ppo = new Ppo ( dirPt.getPpo(n));
		
		Ppoinfo ppoinfo = new Ppoinfo ( dirPt.getPpo(n));
		
		Insure insure = new Insure ( dirPt.getPpoinfo(n) );
		
		//abbr used to be neic
		//String neic = ppoinfo.getNEIC();
		
		String abbr = ppo.getAbbr();
		String coname = ppo.getname();
		String address = ppo.getAddress().getPrintableAddress(4);
		
		String insnum = insure.getGroupNumber();
		String policynum = insure.getPolicyNumber();
		
		StringBuilder IN = new StringBuilder();
		
		IN.append(in+Integer.toString(n)+pipe+Integer.toString(n)+pipe+pipe+abbr+pipe+coname+pipe+address+pipe+pipe+pipe+insnum+StringUtils.repeat(pipe,8)+name+pipe+
				i17+pipe+ptdob+pipe+dirPt.getAddress().getPrintableAddress(4)+StringUtils.repeat(pipe,3)+Integer.toString(n)+StringUtils.repeat(pipe,14)+policynum+System.getProperty("line.separator"));
		
		StringBuilder INtxt = new StringBuilder();
		INtxt.append( Integer.toString(n)+": "+ coname );
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append( ppo.getAddress().getPrintableAddress(0) );
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append( insnum );
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append( name +"               Bithdate: "+ptdob);
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append(dirPt.getAddress().getPrintableAddress(0));
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append("Priority: "+ Integer.toString(n) );
		INtxt.append(System.getProperty("line.separator"));
		INtxt.append("Policy Number: " +policynum );
		INtxt.append(System.getProperty("line.separator"));
		InsSB.append(INtxt.toString());
		
		return IN.toString();
	}	

   private List<String[]> OBXList(Object I) {
	   
	   List<String[]> OBXList =  new ArrayList<String[]>();
	   
	   if (I instanceof ListStr) { OBXList = ( (ListStr) I).FFT(); }
	   
	   else { OBXList = null ; }
	   
	return OBXList;}
   
   private String[] getStrs(Object O){
	    
	    String Type = "", Label = "", Content = "";
		
	    String[] getStrs = new String[]{"","",""};
		
		if (O instanceof FT) { Content = ( (FT) O).getText();
								Type = "FT";
								Label = ( (FT) O).Label; }
		else if (O instanceof ST) { Content = ( (ST) O).getText();
									Type = "ST";
									Label = ( (ST) O).Label; }
		else if (O instanceof DropDown ) { Content = ( (DropDown) O).getContent();
											Type = "ST";
											Label = ( (DropDown) O).Label;
											//System.out.println("T,L,C:"+ Type+","+Label+","+Content);
											}
		
		
		
		//System.out.println("T,L,C:"+ Type+","+Label+","+Content);
				
		getStrs = new String[]{Type.toString(),Label.toString(),Content.toString()};
		//System.out.println("At get STrs: "+getStrs.toString());
		//System.out.println("At get STrsUNS: "+getStrs[0]);
		//System.out.println("At get STrsU: "+getStrs[0].toString());
		return getStrs ;
		
	 }

 class ListStr {
	 
	List<String[]> FFTxts; 
	
	ListStr () {
		
		FFTxts = new ArrayList<String[]>();
		
	}
	void add( String[] Str ) {
		
		FFTxts.add(Str);
	
		
	}
	
	String[] getStrs( int i ) {
		
		return FFTxts.get(i);
		
	}
	
	List<String[]> FFT() {
		
		return FFTxts;
	}
	 
 }

 class FT {
	 
	 Row row;
	 Textbox FT;
	 String Label;
	 
	  void FreeText( Row row, String Label, int size ) {
			
		   this.Label = Label;
			
			FT = new Textbox();
			FT.setMaxlength(size);
			FT.setParent(row);
			
		}
	  
	  String getText() {
		  
		  //System.out.println(FT.getText().trim());
		  return FT.getText(); 
	  }
	 
 }
 
 class ST {
	 
	 Row row;
	 Textbox StringT;
	 String Label;
	 
	  void StringT( Row row, String Label, int size ) {
			
		   this.Label = Label;
			
			StringT = new Textbox();
			StringT.setMaxlength(size);
			StringT.setParent(row);
			
		}
	  
	  String getText() {
		  
		  //System.out.println(StringT.getText().trim());
		  return StringT.getText();
		  
	  }
	  
	 
 }
 
 class DropDown {
	 private final static String fn_config3 = "AOE List Items0.xml";	 
	 
	 Listbox Li01;
	 String Content;
	 String Label;
	 
	 DropDown( String Abbrev, String Res_Code, Row row) {
			// Use result_code
			this.Label = Res_Code;
			XMLElement xml = new XMLElement();
			FileReader reader = null;
				
			try {
				reader = new FileReader( Pm.getOvdPath() + File.separator + fn_config3 );
		    } catch (FileNotFoundException e) {
		    	System.out.println( "the.." + File.separator + fn_config3 + "file was not found:" );
		    	
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
			 //System.out.println(Name+": Name is config3");
			 final int NosChildren = xml.countChildren();
			 
			 e = xml.getElementByPathName(Name);
			
			 Li01 = new Listbox();
			 Li01.setMold("select");
			 Li01.setWidth("300px");
			 Li01.setParent(row);
			 
			 for ( int i =0; i < NosChildren; i++ ){ 
				
				 // Contains vs equals
			  if ( e.getChildByNumber(i).getChildByNumber(0).getContent().equalsIgnoreCase( Abbrev ) && e.getChildByNumber(i).getChildByNumber(1).getContent().contains( Res_Code ) ) {
				  
				  XMLElement Item = new XMLElement();
				  Item = e.getChildByNumber(i);
				  int NosoC = Item.countChildren();
				  
				  for ( int j=0; j < NosoC; j++ ){
					  
					  if ( Item.getChildByNumber(j).getName().contains("OPTION_TEXT")){
						  
						  Listitem con = new Listitem ( Item.getChildByName("OPTION_TEXT").getContent() );
						  con.setParent( Li01 );
						  
					  }
				  }
				  
			  }					 
			 }
			
		  if ( Li01.getItemCount() < 2 ) {  
			  
			  Listitem blank = new Listitem ( "" );
			  blank.setParent( Li01 );
			  
		  } 	 
			
		}
	 
	 String getContent(){
		
		 //System.out.println("SI: "+ Li01.getSelectedIndex());
		 if( Li01.getSelectedIndex() >= 0 ){
		 Content = Li01.getSelectedItem().getLabel().toString().trim(); }
		 
		 else { Content = ""; } 
		 //System.out.println("Content at content: "+ Content);
		 return Content;
		 
	 }
	 
 }
}
