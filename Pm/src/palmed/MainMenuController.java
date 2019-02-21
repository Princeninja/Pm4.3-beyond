/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package palmed;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.mail.Flags;

import org.apache.commons.io.FileUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zk.ui.HtmlBasedComponent;

import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Page;
import org.zkoss.zkplus.theme.Themes;

import com.sun.mail.imap.protocol.FLAGS;


import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.XMLElement;
import usrlib.XMLParseException;
import palmed.PtChartController;
import usrlib.Dollar;

/**
 * 
 * Main menu controller. <br>
 * <br>
 * Added the buttons for expanding/closing the menu tree. Calls the menu
 * factory.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 * 
 */
public class MainMenuController extends GenericForwardComposer  {


	static final String ATTR_INDEX            = "index";
	
	
	// Autowired
	private Window mainMenuWindow; // autowire
	private Tree mainMenuTree;	// autowire
	private Window ptChartWin;
	

	private static String bgColor = "D6DCDE";
	private static String bgColorInner = "white";

	public void onCreate$mainMenuWindow(Event event) throws Exception {

		// doOnCreateCommon(mainMenuWindow, event); // wire vars
//		doOnCreateCommon(getMainMenuWindow(), event); // wire vars
		System.out.println( "MainMenuController.onCreate$mainMenuWindow()" );

		createMenu();
	}

	/**
	 * Creates the mainMenu. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void createMenu() throws InterruptedException {

		Toolbarbutton toolbarbutton;
		
		System.out.println( "MainMenuController.createMenu()" );


		final Groupbox gb = (Groupbox) getMainMenuWindow().getFellowIfAny("groupbox_menu");
		// gb.setHeight("500px");

		// Hbox for the expand/collapse buttons
		final Hbox hbox = new Hbox();
		hbox.setStyle("backgound-color: " + bgColorInner);
		hbox.setParent(gb);

		// ToolbarButton for expanding the menutree
		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuExpandAll");
		toolbarbutton.setImage("expand_icon.gif");
		toolbarbutton.setTooltiptext( "Expand All" /*Labels.getLabel("btnFolderExpand.tooltiptext")*/ );
		toolbarbutton.addEventListener("onClick", new EventListener() {
//			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuExpandAll(event);
			}
		});
		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuCollapseAll");
		toolbarbutton.setImage("collapse_icon.gif");
		toolbarbutton.setTooltiptext( "Collapse All" /*Labels.getLabel( "btnFolderCollapse.tooltiptext")*/ );
		toolbarbutton.addEventListener("onClick", new EventListener() {
//			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuCollapseAll(event);
			}
		});

		// toolbarbutton = new Toolbarbutton();
		// hbox.appendChild(toolbarbutton);
		// toolbarbutton.setId("btnMainMenuChange");
		//
		// toolbarbutton.setImage("/images/icons/menu_16x16.gif");
		// // toolbarbutton.setImage("/images/icons/combobox_16x16.gif");
		// toolbarbutton.setTooltiptext(Labels.getLabel("btnMainMenuChange.tooltiptext"));
		// toolbarbutton.addEventListener("onClick", new EventListener() {
		// @Override
		// public void onEvent(Event event) throws Exception {
		// onClick$btnMainMenuChange(event);
		// }
		// });

		// toolbarbutton = new Toolbarbutton();
		// hbox.appendChild(toolbarbutton);
		// toolbarbutton.setId("btnMainMenuDocumentation");
		//		
		// toolbarbutton.setImage("/images/icons/icon-pdf_16x16.png");
		// toolbarbutton.setTooltiptext(Labels.getLabel("btnMainMenuDocumentation.tooltiptext"));
		// toolbarbutton.addEventListener("onClick", new EventListener() {
		// @Override
		// public void onEvent(Event event) throws Exception {
		// //
		// Executions.getCurrent().sendRedirect("the_url_generated_of_report","_blank");
		// //
		// Executions.getCurrent().sendRedirect("http://sunet.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf",
		// // "_blank");
		//		
		// final String url1 =
		// "http://sunet.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf";
		// Clients.evalJavaScript("window.open('" + url1
		// +
		// "','','top=100,left=200,height=600,width=800,scrollbars=1,resizable=1')");
		// }
		// });

		Separator separator = createSeparator(false);
		separator.setWidth("97%");
		separator.setStyle("background-color: " + bgColorInner);
		separator.setBar(false);
		separator.setParent(gb);

		separator = createSeparator(false);
		separator.setWidth("97%");
		separator.setBar(true);
		separator.setParent(gb);

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

		// the menuTree
		final Tree tree = new Tree();
		// tree.setSizedByContent(true);
		tree.setStyle("overflow:auto;");
		tree.setParent(gb);

		// tree.setZclass("z-dottree");
		tree.setStyle("border: none");

		final Treechildren treechildren = new Treechildren();
		tree.appendChild(treechildren);

		// generate the treeMenu from the menuXMLFile
//		ZkossTreeMenuFactory.addMainMenu(treechildren);

		final Separator sep1 = new Separator();
		sep1.setWidth("97%");
		sep1.setBar(false);
		sep1.setParent(gb);

		/* as standard, call the welcome page */
//		showPage("/WEB-INF/pages/welcome.zul", "Start");

	}

	
	
	public void onSelect$mainMenuTree( Event event ) throws Exception{

		Treeitem i = mainMenuTree.getSelectedItem();
		String itemID = mainMenuTree.getSelectedItem().getId();
		
		if ( itemID.equals( "lkupd" )){
			
			Ldgn();
			
		}else if ( itemID.equals( "testbtn" )){
			
			test();			
			
		}else if ( itemID.equals( "ofvisit" )){
			
			OfficeVisit();			
			
		}else if ( itemID.equals("labint")){
			
			Labint();
			
		}else if ( itemID.equals( "ptchart" )){
			
			createPtChart();
			
		} else if ( itemID.equals( "soapsht" )){
				
				createSoapSheetWin();
				
		} else if ( itemID.equals( "ptledger")){

			createLedger();

		} else if ( itemID.equals( "rxstatus")){

			createRxStatus();

		} else if ( itemID.equals( "editpt")){

			createEditPt();

		} else if ( itemID.equals( "newpt")){

			createNewPt();
			
		} else if ( itemID.equals( "editprov")){
			
			createEditProv();
			
		} else if ( itemID.equals( "newprov" )){
			
			createNewProv();

		} else if ( itemID.equals( "srvcode" )){
			
			alert( "srvcde" );

		}else if( itemID.equals("dgndc")){
			
			   createDgnWin();
			  
		}else if ( itemID.equals("srvcde")){
			
				createSrvWin();
				//System.out.println("Modyfying Service Codes");
				
		} else if( itemID.equals("ppoIns")){
			
			 createPpoWin();
		} else if ( itemID.equals("phrdsphar")){
				
			createPhrdsWin();
		}else if ( itemID.equals( "ProbTbl" )){
			
			createProbTblWin();
			
			
		} else if ( itemID.equals( "foxnews" )){
			
			createBrowser( "http://www.foxnews.com" );
			
		} else if ( itemID.equals( "erxconfig" )){
			
			createERxConfig();
			
		} else if ( itemID.equals( "erxprov" )){
			
			createERxProvWin();
			
		} else if ( itemID.equals( "userwin" )){
			
			createUserWin();
			
		} else if ( itemID.equals( "useredit" )){
			
			createUserEditWin();
			
		} else if ( itemID.equals( "ecs" )){
			
			createEcsWin();
			
		} else if ( itemID.equals( "pmimport" )){
			
			createPmImportWin();
			
		} else if ( itemID.equals( "audit" )){
			
			createAuditWin();
			
		} else if ( itemID.equals( "vaccvx" )){
			
			createVaccineCVXWin();
			
		} else if ( itemID.equals( "vacmvx" )){
			
			createVaccineMVXWin();
			
		} else if ( itemID.equals( "labobstbl" )){
			
			createLabObsTblWin();
			
		} else if ( itemID.equals( "labbat" )){
			
			createLabBatWin();
			
		} else if ( itemID.equals( "labfac" )){
			
			createLabFacilityWin();
			
		} else if ( itemID.equals( "labimport" )){
			
			createLabImport();
			
		} else if ( itemID.equals( "cdsRules" )){
			
			createCDSRulesWin();
			
		} else if ( itemID.equals( "reminders" )){
			
			createRemindersWin();
			
		} else if ( itemID.equals( "listpt" )){
			
			createPtListWin();
			
		} else if ( itemID.equals( "cqm" )){
			
			createCQMWin();
			
		} else if ( itemID.equals( "amc" )){
			
			createAMCWin();
			
		} else if ( itemID.equals( "xfrVitals" )){
			
			createXfrVitals();
			
		} else if ( itemID.equals( "xfrPARs" )){
			
			createXfrPARs();
			
		} else if ( itemID.equals( "scheduler" )){
			
			createSchWin();
			
		} else if ( itemID.equals( "cdmDashboard" )){
			
			createCdmDashboard();
			
		} else if (itemID.equals("stdDict")) {

			createStdDictWin();

		} else if ( itemID.equals( "about" )){
			
			createAboutWin();
			
		}else if ( itemID.equals( "changelog" )){
			
			createchangelogWin();
			
		}else if ( itemID.equals( "comings" )){
			
			createcomingsWin();
			
		}else if ( itemID.equals( "FAQ" )){
			
			createFAQWin();
			
		}else if ( itemID.equals( "laberr" )){
			
			createlaberrWin();
			
		}else if ( itemID.equals( "labprg" )){
			
			createlabprgWin();
			
		}else if ( itemID.equals( "labimportbatch" )){
			
			createlabimpbatchWin();
			
		}else {
			
			// if empty, assume category - toggle open state
			if ( ! i.isEmpty()){
				i.setOpen( i.isOpen() ? false: true );
			} else {
			
				alert( "Not yet supported: " + itemID );
			}
		}

		
		// clear the selection so we can choose again
		mainMenuTree.clearSelection();
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
		   for(byte b: a)
		      sb.append(String.format("%#x", b & 0xFF));
		   return sb.toString();
		}
	
	
	
	/**
	 * Test Function
	 * 
	 */
	
	public void test() throws Exception {
		
		
		SoapShtExamWinController SSEW;
		
		//SSEW = new SoapShtExamWinController();
		System.out.println(Themes.getThemes());
		//if ( ! SSEW.read()) System.out.println("file.read isn't working");
		/*
		int b = 3;
		String ba = "$-9,9h99fe.94578";
		
		Dollar fee = new Dollar(ba);
		Dollar price = new Dollar(b);
		
		fee.absolute();
		
		int bab = 0;
		bab = fee.integer();
		
		fee.getDollar();
		
		byte[] bcd = new byte[4];
		
		fee.toBCD(bcd);
		
		int cha = 20;
		String cha2= String.format("%02d", cha);
		String mom = "cdsrv"+cha2+".ovd";
		
		//Dollar neg = new Dollar(feerb.negative().toString());
		
		
		System.out.println("String in form:"+ mom);
		System.out.println("The negative value of the dollar: "+ price.negative());
		//System.out.println("The zero value of the dollar: "+ price.zero() );
		System.out.println("The integer is: " + bab  );
		System.out.println("What?: " + fee.mult(price) );
		System.out.println("The dollar amount is: "+ price.getPrintable() );
		System.out.println("The core dollar is: " + fee);
		System.out.println("the core byte is:"+ bcd);
		System.out.println("the fancy byte is:"+ javax.xml.bind.DatatypeConverter.printHexBinary(bcd) );
		System.out.println("the fancier  byte:"+ byteArrayToHex(bcd) );
		Dollar feerb = new Dollar(bcd);
		System.out.println("the dollar reborn is: "+feerb.getPrintable());
		System.out.println("the dollar in style:[1]"+ feerb.format(8|1));
		System.out.println("the dollar in style:[2]"+ feerb.format(1|2));
		System.out.println("the dollar in style:[3]"+ feerb.format(1|4|16));
		//System.out.println("the dollar in style:[4]"+ );
		//System.out.println("The other byte is: " + byteArrayToHex(bcd2));
		//System.out.println("New negative dollar:" + neg.getPrintable());
		
		
		String start = "1-1-2016";
		Date dateStart = new Date( start );
		
		dateStart.compare(dateStart);
		System.out.println("The date is: "+ dateStart);
		*/
		
	}
	
	

	/**
	 * Creates a seperator. <br>
	 * 
	 * @param withBar
	 * <br>
	 *            true=with Bar <br>
	 *            false = without Bar <br>
	 * @return
	 */
	private static Separator createSeparator(boolean withBar) {

		final Separator sep = new Separator();
		sep.setStyle("backgound-color: " + bgColorInner);
		sep.setBar(withBar);

		return sep;
	}

	public final class GuestBookListener implements EventListener {
//		@Override
		public void onEvent(Event event) throws Exception {

			showPage("/WEB-INF/pages/guestbook/guestBookList.zul", "Guestbook");
		}
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout. Checks if the tab is opened before. If yes than it selects
	 * this tab.
	 * 
	 * @param zulFilePathName
	 *            The ZulFile Name with path.
	 * @param tabName
	 *            The tab name.
	 * @throws InterruptedException
	 */
	private void showPage(String zulFilePathName, String tabName) throws InterruptedException {
		System.out.println( "MainMenuController.showPage()" );

		try {
			// TODO get the parameter for working with tabs from the application
			// params
			final int workWithTabs = 1;

			{



				// Check if the tab is already open.

				Tab checkTab = tabExists( tabName.trim());
				

				if (checkTab == null) {
					System.out.println( "MainMenuController.showPage() creating new tab" );

					// Create new tab, set Id, set title, make closeable, and set parent
					final Tab tab = createTab( tabName.trim(), tabName.trim());
					

					// Create new tabpanel, set size, style, and parent
					final Tabpanel tabpanel = createTabpanel();
					
					
					/*
					 * create the page and put it in the tabs area
					 */
					Executions.createComponents(zulFilePathName, tabpanel, null);
					tab.setSelected(true);

				}
			}

		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
		System.out.println( "returning" );
	}

	
	/*
	 * Get Tabbox
	 */
	public Tabbox getTabbox(){
		final Borderlayout bl = (Borderlayout) Path.getComponent("/pmMainWin/borderlayoutMain");
		final Center center = bl.getCenter();
		Tabbox tabs = (Tabbox) center.getFellow("divCenter").getFellow("tabBoxIndexCenter");
		return tabs;
	}
	
	/*
	 * Get tabs
	 */
	public Tabs getTabs(){
		final Borderlayout bl = (Borderlayout) Path.getComponent("/pmMainWin/borderlayoutMain");
		final Center center = bl.getCenter();
		Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter");
		return tabs;
	}
	
	/*
	 * Does Tab 'tabname' already exist?
	 */
	public Tab tabExists( String tabName ){

		Tab checkTab = null;
		try {
			checkTab = (Tab) getTabs().getFellow( "tab_" + tabName.trim());

		} catch (final ComponentNotFoundException ex) { /* Ignore  */ }

		return checkTab;
	}
	
	/*
	 * Make new tab
	 * Create new tab, set Id, set title, make closeable, **new( make it draggable and droppable)**, and set parent
	 */
	
	public Tab createTab( String id,  String title){
		final Tab tab = new Tab();
		//final Tabpanel tabpanel =  new Tabpanel();
		
		tab.setId( "tab_" + id);
		tab.setLabel(title);
	    tab.setDraggable("true");
		tab.setDroppable("true");
		tab.setClosable(true);
		//tabpanel.setDraggable("true");
		//tabpanel.setDroppable("true");
		tab.setParent( getTabs());
		
				
		tab.addEventListener(Events.ON_DROP, new EventListener(){
			@SuppressWarnings("unchecked")
			public void onEvent(Event event) {
				//int index = tab.getIndex();
			/*	//DropEvent dropEvent = (DropEvent)event;
				//Tab tab =(Tab)dropEvent.getDragged();
				Component tabs = tab.getParent();
				tabs.removeChild(tab);
				tabs.appendChild(tab);
				Tabpanels panels = ((Tabbox) tabs.getParent()).getTabpanels(); 
				Tabpanel panel = (Tabpanel) panels.getChildren().get(index);
				System.out.println("This is the index:"+index);
				panels.removeChild((Component) panel);
				panels.appendChild((Component) panel);
				tab.setSelected(true);*/
				DropEvent dropEvent = (DropEvent)event;
			    Tab dragged = (Tab)dropEvent.getDragged();
			    Tab target =  (Tab)dropEvent.getTarget();
			    if (dragged.getIndex() > target.getIndex()) {
			        Tabpanel drag = dragged.getLinkedPanel();
			        Tabpanel drop = target.getLinkedPanel();
			        Tabpanels panels = (Tabpanels)drag.getParent();
			        panels.getChildren().remove(drag);
			        panels.getChildren().add(panels.getChildren().indexOf(drop),drag);
			        dragged.getParent().insertBefore(dragged, target);
			        //panels.appendChild((Tabpanels)panels);
			        panels.invalidate();// strange behavior when I don't do this
			      } else {
			        Tabpanel drag = dragged.getLinkedPanel();
			        Tabpanel drop = target.getLinkedPanel();
			        Tabpanels panels = (Tabpanels)drag.getParent();
			        panels.getChildren().remove(drag);
			        panels.getChildren().add(panels.getChildren().indexOf(drop)+1,drag);
			        Tabs tabs = (Tabs)dragged.getParent();
			        tabs.getChildren().remove(dragged);
			        tabs.getChildren().add(tabs.getChildren().indexOf(target)+1,dragged);
			        //panels.appendChild((Tabs)tabs);
			        panels.invalidate(); // strange behavior when I don't do this
			      }
			     dragged.setSelected(true);
			}
			});
		
		return tab;		
	}
	
	
	/*protected void move(Component dragged) {
		// TODO Auto-generated method stub
	           self.Parent.insertBefore(dragged, self);
		}*/
	

	/*
	 * Create new tabpanel, set size, style, and parent
	*/
	public Tabpanel createTabpanel( ){
		
		final Tabpanels tabpanels = (Tabpanels) getTabs().getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);
		
		return tabpanel;
	}

	
	
	
	
	
	public Window getMainMenuWindow() {
		return this.mainMenuWindow;
	}

	public void setMainMenuWindow(Window mainMenuWindow) {
		this.mainMenuWindow = mainMenuWindow;
	}

	public void onClick$btnMainMenuExpandAll(Event event) throws Exception {

		doCollapseExpandAll(getMainMenuWindow(), true);
	}

	public void onClick$btnMainMenuCollapseAll(Event event) throws Exception {

		doCollapseExpandAll(getMainMenuWindow(), false);
	}

	public void onClick$btnMainMenuChange(Event event) throws Exception {

		// correct the desktop height
		final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
		cb.setChecked(false);

		// UserWorkspace.getInstance().setTreeMenu(false);

		// get an instance of the borderlayout defined in the index.zul-file
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched west layout area
		final West west = bl.getWest();
		west.setVisible(false);

		final North north = bl.getNorth();
		north.setFlex(true); // that's important !!!!

		final Div div = (Div) north.getFellow("divDropDownMenu");

		final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
		menuBar.setVisible(true);

		// generate the menu from the menuXMLFile
//		ZkossDropDownMenuFactory.addDropDownMenu(menuBar);

		final Menuitem changeToTreeMenu = new Menuitem();
		changeToTreeMenu.setLabel(Labels.getLabel("menu_Item_backToTree"));
		changeToTreeMenu.setImage("/images/icons/refresh2_yellow_16x16.gif");
		changeToTreeMenu.setParent(menuBar);
		changeToTreeMenu.addEventListener("onClick", new EventListener() {
//			@Override
			public void onEvent(Event event) throws Exception {
				// get an instance of the borderlayout defined in the
				// index.zul-file
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				// get an instance of the searched west layout area
				final West west = bl.getWest();
				west.setVisible(true);

				final North north = bl.getNorth();

				final Div div = (Div) north.getFellow("divDropDownMenu");

				final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
				menuBar.getChildren().clear();
				menuBar.setVisible(false);
				north.setFlex(false); // that's important !!!!

				// correct the desktop height
				final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
				cb.setChecked(true);

				// UserWorkspace.getInstance().setTreeMenu(true);

				// Refresh the whole page for setting correct sizes of the
				// components
				final Window win = (Window) Path.getComponent("/outerIndexWindow");
				win.invalidate();

			}
		});

		// Guestbook
		final Menuitem guestBookMenu = new Menuitem();
		guestBookMenu.setLabel("ZK Guestbook");
		guestBookMenu.addEventListener("onClick", new GuestBookListener());
		guestBookMenu.setParent(menuBar);

		// Refresh the whole page for setting correct sizes of the
		// components
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		win.invalidate();
	}

	private void doCollapseExpandAll(Component component, boolean aufklappen) {
		if (component instanceof Treeitem) {
			final Treeitem treeitem = (Treeitem) component;
			treeitem.setOpen(aufklappen);
		}
		final Collection<?> com = component.getChildren();
		if (com != null) {
			for (final Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
				doCollapseExpandAll((Component) iterator.next(), aufklappen);

			}
		}
	}
	
	/**
	 * Create Diagnostics look-up 
	 */
	
	public void Ldgn(){
		showLkupWin();
	}
	
	public void showLkupWin(){
		
		/*Window win = (Window) Executions.createComponents( "lkup.zul", null, null );
			try {
				win.doModal();
			} catch (Exception e) {
				// ignore
			}
			return; */
	
	Tab	tab;				// tab
	Tabpanel tabpanel;		// tabpanel


	// Make tab_panel name, see if it exists
	//   -- if it exists, select it quietly and return
	
	String tabname = "lkupwin";		
	if (( tab = tabExists( tabname )) != null ){
		tab.setSelected(true);
		return;
	}
	
	// Create new tab and tabpanel
	tab = createTab( tabname, "Look-up Application");
	tabpanel = createTabpanel();
	tabpanel.setWidth( "100%" );
	tabpanel.setHeight( "100%" );
	
	// Create dgntab
	lkupwin.show( tabpanel );
	
	// Select this tabpanel
	tab.setSelected(true);	
	}
	
	/**
	 * Create Office Visit
	 */
	
	public void OfficeVisit(){
		showOfficevWin();
	}
	
	public void showOfficevWin(){
		
		
	
	Tab	tab;				// tab
	Tabpanel tabpanel;		// tabpanel


	// Make tab_panel name, see if it exists
	//   -- if it exists, select it quietly and return
	
	String tabname = "OfficevWin";		
	if (( tab = tabExists( tabname )) != null ){
		tab.setSelected(true);
		return;
	}
	
	// Create new tab and tabpanel
	tab = createTab( tabname, "Office Visits");
	tabpanel = createTabpanel();
	tabpanel.setWidth( "100%" );
	tabpanel.setHeight( "100%" );
	
	// Create officewintab
	OfficevWin.show( tabpanel );
	
	// Select this tabpanel
	tab.setSelected(true);	
	}
	
	/**
	 * Create Lab interface
	 */
	public void Labint() {
		showLabintWin( null );
	}
	
	public void showLabintWin( Rec ptRec){
	
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel
		
		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Medical.VIEW )){
			SystemHelpers.msgAccessDenied();
			return;
		}


		
		// If no particular patient specified, search for a patient
		if ( ! Rec.isValid( ptRec )){
			ptRec = new Rec( PtSearch.show());
			if ( ptRec.getRec() < 2 ){
				// No patient found
				return;
			}			
		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "Labintwin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Lab Interface");
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create labtab
		Labintwin.show( ptRec, tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
		
	}
	
	
	
	/**
	 * Create PtChart
	 */
	
	
	public void createPtChart(){
		showPtChart( null );
	}
	
	public void createPtChart( Rec ptRec ){
		showPtChart( ptRec );
	}
	
	public void showPtChart( Rec ptRec ){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		
		
		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Medical.VIEW )){
			SystemHelpers.msgAccessDenied();
			return;
		}


		
		// If no particular patient specified, search for a patient
		if ( ! Rec.isValid( ptRec )){
			ptRec = new Rec( PtSearch.show());
			if ( ptRec.getRec() < 2 ){
				// No patient found
				return;
			}			
		}
		
		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "ptchart_" + ptRec.getRec();		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "PtChart" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		PtChart.show( ptRec, tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	
	
	
	/**
	 * Create SoapSheetWin
	 */
	
	
	public void createSoapSheetWin(){
		showSoapSheetWin( null );
	}
	
	public void createSoapSheetWin( Rec ptRec ){
		showSoapSheetWin( ptRec );
	}
	
	public void showSoapSheetWin( Rec ptRec ){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		
		
		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Medical.VIEW )){
			SystemHelpers.msgAccessDenied();
			return;
		}


		// If no particular patient specified, search for a patient
		if ( ! Rec.isValid( ptRec )){
			ptRec = new Rec( PtSearch.show());
			if ( ! Rec.isValid( ptRec )){
				// No patient found
				return;
			}			
		}
		
		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "soapsht_" + ptRec.getRec();		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "SoapSheet" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create SoapSheet
		//SoapSheetWin.show( ptRec, tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	
	
	
	/**
	 * Create Ledger
	 */

	public void createLedger(){
		showLedger( null );
	}
	
	public void createLedger( Rec ptrec ){
		showLedger( ptrec );
	}
	
	public void showLedger( Rec rpRec ){
		
		//TODO -- need to really handle rpRec instead of ptRec
		
			
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel

			
			// Does user have permission?
			if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW)){
				SystemHelpers.msgAccessDenied();
				return;
			}


			
			// If no particular patient specified, search for a patient
			if ( ! Rec.isValid( rpRec )){
				rpRec = new Rec( PtSearch.show());
				if ( ! Rec.isValid( rpRec )){
					// No patient found
					return;
				}			
			}
			
			// Make tab_panel name, see if it exists
			//   -- if it exists, select it quietly and return			
			String tabname = "rpledger_" + rpRec;			
			if (( tab = tabExists( tabname )) != null ){
				tab.setSelected(true);
				return;
			}
			
			// Create new tab and tabpanel
			tab = createTab( tabname, "RpLedger" );
			tabpanel = createTabpanel();
			
			// Create RpLedger
			RpLedger.show( rpRec, tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
			
	}

	
	
	
	
	/**
	 * Create About Window
	 */

	public void createAboutWin(){
		showAboutWin();
	}
	
	public void showAboutWin(){
		
			Window win = (Window) Executions.createComponents( "about.zul", null, null );
			try {
				win.doModal();
			} catch (Exception e) {
				// ignore
			}
			return;
	}

	
	

	/**
	 * Create Change_log Window
	 */

	public void createchangelogWin(){
		showchangelogWin();
	}
	
	public void showchangelogWin(){
		
			Window win = (Window) Executions.createComponents( "change_log.zul", null, null );
			try {
				win.doModal();
			} catch (Exception e) {
				// ignore
			}
			return;
	}

	
	

	/**
	 * Create Coming Soon Window
	 */

	public void createcomingsWin(){
		showcomingsWin();
	}
	
	public void showcomingsWin(){
		
			Window win = (Window) Executions.createComponents( "coming_soon.zul", null, null );
			try {
				win.doModal();
			} catch (Exception e) {
				// ignore
			}
			return;
	}
	
	/**
	 * Create Coming Soon Window
	 */

	public void createFAQWin(){
		showFAQWin();
	}
	
	public void showFAQWin(){
		
			Window win = (Window) Executions.createComponents( "faq.zul", null, null );
			try {
				win.doModal();
			} catch (Exception e) {
				// ignore
			}
			return;
	}

	//createlabimpbtchWin(); 
	
	/**
	 * Import a batch of Lab Reports
	 * @throws InterruptedException 
	 */

	public void createlabimpbatchWin() throws InterruptedException{
		showimpbatchWin();
	}
	
	public void showimpbatchWin() throws InterruptedException{
		
		if ( Messagebox.show( "Import All the Labs in The Designated Folder?", "Import?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			
			String fn_config = "HL7Config.xml";
			
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
			
			final XMLElement e;
			
			 String Name = xml.getName();
			 //xml.countChildren();
			 
			 e = xml.getElementByPathName(Name);
			 
			String path =  e.getChildByNumber(9).getContent();
			String Destination = e.getChildByNumber(10).getContent();
			
			File labs = new File( path );
			
			File[] matchingfiles = labs.listFiles(new FilenameFilter() { 
				
				public boolean accept(File labs, String name){
					return  name.endsWith(".txt");
				}
				
			});
			
			if ( labs.exists()) {
			int mflength = matchingfiles.length;
			System.out.println("Number of labs: "+mflength);
			
			// The Labs at the time of Import and their names
			ArrayList<String> Labslist = new ArrayList<String>();
			ArrayList<String> Labnames = new ArrayList<String>();
			
			//TODO import protocol (Send the path too) , that method archives the lab
			
			if ( mflength > 0 ) { 
				
				for ( int i = 0; i < mflength; i++){
				
				//System.out.println("i is: "+ i);
				String labpath = matchingfiles[ i ].getPath();
				String labname = matchingfiles[ i ].getName();
				
				//System.out.println("Alpha lab path and name: "+ labpath+ ", "+ labname);
				
				Labslist.add(labpath);
				Labnames.add(labname);
				
								
				}
			
			
			//Labslist size
			int ll = Labslist.size();
			
			for ( int i = 0; i < ll; i++){
				
				String Filename = Labslist.get(i);
				//System.out.println("Imports: "+Filename);
				LabImport.show(Filename);
				
			}
			
			//Track if export folder exists.
			boolean exportstat = true;
			
			for ( int j = 0; j < ll; j++){ 
				
				String labfile = Labslist.get(j);
				String labname = Labnames.get(j);
				
				File LabFile = new File(labfile);
				File newLabFile = new File (Destination+labname);
				
				if ( !newLabFile.exists()){
					
					alert("Error Finding Designated Export Folder");
					exportstat= false;
					break;
				}
				
				//System.out.println("Labfile is: "+(j+1)+", "+ LabFile+" new lab file is: "+newLabFile.getPath());
				try {
					FileUtils.copyFile(LabFile, newLabFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				LabFile.delete();
				
			}
			
			if ( exportstat ){
			alert(Labnames.size()+" Labs have been imported and archived to: "+ Destination
					+ ". Check lab report for import details.");
			}else{
				
				alert(Labnames.size()+" Labs have been imported but were not archived to: "+ Destination
						+ " as it doesnt exist. Check lab report for import details.");
			}						
			
		  }else {
			  
			  alert("No Lab files were found in: "+path);
		  } 
			  
			  
			
		}else{ alert("Error finding Designated Import Folder"); }
		}
		return;
			
	}

	
	
	/**
	 * Purge Lab Report
	 * @throws InterruptedException 
	 */

	public void createlabprgWin() throws InterruptedException{
		showprgWin();
	}
	
	public void showprgWin() throws InterruptedException{
		
		if ( Messagebox.show( "Purge Lab Report ?", "Purge?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			
			File errrpt = new File( Pm.getOvdPath() + File.separator + "LabErr");
			
			File[] matchingfiles = errrpt.listFiles(new FilenameFilter() { 
				
				public boolean accept(File errrpts, String name){
					return name.startsWith("lab")&& name.endsWith(".html");
				}
				
			});
			
			int mflength = matchingfiles.length;
			
			if ( mflength > 0 ) { 
				
				String errr = matchingfiles[ mflength-1 ].getPath();
				
				
				File errfile = new File(errr);
				
				errfile.delete();
				
				alert("Lab report file purged!");
				
			}
			
			
		}		
		return;
			
	}

	
	
	
	
	/**
	 * Print Most Recent Lab(Error) Report
	 */

	public void createlaberrWin(){
		showlaberrWin();
	}
	
	public void showlaberrWin(){
		StringBuilder sb = new StringBuilder();
		
		File errrpt = new File( Pm.getOvdPath() + File.separator + "LabErr");
		
		File[] matchingfiles = errrpt.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visits, String name){
				return name.startsWith("lab")&& name.endsWith(".html");
			}
			
		});
		
		int mflength = matchingfiles.length;
		System.out.println("number of files : " + mflength  );
		
		if ( mflength > 0 ) { 
			
			String errr = matchingfiles[ mflength-1 ].getPath();
			String putData = "";
			
			File errfile = new File(errr);
			
			try {
				 putData = FileUtils.readFileToString( errfile );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		//String toperrhtml = putData.toString();
		//sb.append( toperrhtml );
		
		/*sb.append(System.getProperty("line.separator"));
		sb.append("</body>");
		sb.append(System.getProperty("line.separator"));
		sb.append("</HTML>");*/
		
		sb.append("<p>ZCC</p>");
		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		/*sb.append( "</BODY>" );
		sb.append( "</HTML>" );*/
		
		//String html = sb.toString();
		System.out.println("sb String is: "+sb.toString());
		putData = putData.replace("<p>ZCC</p>", sb.toString());
		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", putData );
		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( mainMenuWindow );
		
	}
		
			return;
	}
	
	/**
	 * Create Browser Window
	 */

	public void createBrowser(){
		showBrowser( null );
	}
	
	public void createBrowser( String weburl ){
		showBrowser( weburl );
	}
	
	public void showBrowser( String weburl ){
			
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			
			// If no particular weburl specified, ask user for one
			if (( weburl == null ) || ( weburl.trim().length() < 1 )){
				if (( weburl = DialogHelpers.Textbox( "Browser", "Enter the URL to open." )).length() < 1 ){
					// No website entered
					return;
				}			
			}
			//System.out.println( "showBrowser() weburl=" + weburl );
			
			// Make tab_panel name, see if it exists
			//   -- if it exists, select it quietly and return			
			String tabname = "browser_" + weburl;			
			if (( tab = tabExists( tabname )) != null ){
				tab.setSelected(true);
				return;
			}
			
			// Create new tab and tabpanel
			tab = createTab( tabname, "Web" );
			tabpanel = createTabpanel();
			
			// Create RpLedger
			Map<String, String> myMap = new HashMap<String, String>();
			myMap.put("webUrl", weburl);
			
			Executions.createComponents( "browser.zul", tabpanel, myMap );
			
			// Select this tabpanel
			tab.setSelected(true);
	}

	
	
	
	
	

	
	
	public void createRxStatus(){ showRxStatus(); }
	
	public void showRxStatus(){
		
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			
			// Does user have permission?
			if ( ! Perms.isRequestPermitted( Perms.Medical.PRESCRIPTIONS )){
				SystemHelpers.msgAccessDenied();
				return;
			}

			// Make tab_panel name, see if it exists
			//   -- if it exists, select it quietly and return
			String tabname = "rxstatus";			
			if (( tab = tabExists( tabname )) != null ){
				tab.setSelected(true);
				return;
			}
			
			// Create new tab and tabpanel
			tab = createTab( tabname, "RxStatus" );
			tabpanel = createTabpanel();
			tabpanel.setWidth( "100%" );
			tabpanel.setHeight( "100%" );
			
			ERxRefills.show( tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
	}
	
	
	
	/**
	 * Edit Patient
	 */

	public void createNewPt (){
		showEditPt( EditPt.Operation.NEWPT, 0 );
	}

	public void createEditPt(){
		showEditPt( EditPt.Operation.EDITPT, 0 );
	}
	
	public void createEditPt( int ptrec ){
		showEditPt( EditPt.Operation.EDITPT, ptrec );
	}
	
	public void showEditPt( EditPt.Operation Operation, int ptRec ){
		
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			String tabname;			// tab name
			String title;			// tab title

			

			System.out.println( "showEditPt() Operation=" + Operation.name() + "  ptRec=" + ptRec );
			
			if ( Operation == EditPt.Operation.NEWPT ){
				
				// Create a new patient
				
				tabname = "newpt";
				title = "New Patient";
				//TODO - make tab name unique
				
				
			} else {	// Operation == EditPt.Operation.EDITPT
			
				// If no particular patient specified, search for a patient
				if (  ptRec == 0 ){
					if (( ptRec = PtSearch.show()) == 0 ){
						// No patient found
						return;
					}			
				}
				
				// Make tab_panel name, see if it exists
				//   -- if it exists, select it quietly and return
				
				tabname = "editpt_" + ptRec;
				
				if (( tab = tabExists( tabname )) != null ){
					tab.setSelected(true);
					return;
				}
				
				title = "Edit Pt";
			}
			
			// Create new tab and tabpanel
			tab = createTab( tabname, title );
			tabpanel = createTabpanel();
			
			// Create EditPt window
			EditPt.show( Operation, new Rec( ptRec ), tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
			return;
	}


	
	

	
	/**
	 * Edit Provider
	 */

	public void createNewProv(){
		showEditProv( EditPt.Operation.NEWPT, new Rec( 0 ));
	}

	public void createEditProv(){
		showEditProv( EditPt.Operation.EDITPT, new Rec( 0 ));
	}
	
	public void createEditProv( Rec provRec ){
		showEditProv( EditPt.Operation.EDITPT, provRec );
	}
	
	public void showEditProv( EditPt.Operation Operation, Rec provRec ){
		
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			String tabname;			// tab name
			String title;			// tab title
			

			

			System.out.println( "showEditProv() Operation=" + Operation.name() + "  provRec=" + provRec );
			
			if ( Operation == EditPt.Operation.NEWPT ){
				
				// Create a new provider				
				tabname = "newprov";
				title = "New Provider";
				//TODO - make tab name unique
				
				
			} else {	// Operation == EditPt.Operation.EDITPT
			
				// If no particular patient specified, search for a patient
				if (( provRec == null ) || ( provRec.getRec() == 0 )){
					if (( provRec = ProvSearch.show()).getRec() == 0 ){
						// No provider selected
						return;
					}			
				}
				
				// Make tab_panel name, see if it exists
				//   -- if it exists, select it quietly and return
				
				tabname = "editprov_" + provRec.getRec();				
				if (( tab = tabExists( tabname )) != null ){
					tab.setSelected(true);
					return;
				}
				
				title = "Edit Provider";
			}
			
			// Create new tab and tabpanel
			System.out.println( "showEditProv() creating new tab provRec=" + provRec.getRec() );
			tab = createTab( tabname, title );
			tabpanel = createTabpanel();
			
			// Create EditPt window
			ProvEdit.show( Operation, provRec, tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
			return;
	}

	
	
	
	
	
	/**
	 * ERx Config
	 */
	public void createERxConfig(){ showERxConfig(); }
	
	public void showERxConfig( ){
		
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			String tabname;			// tab name
			String title;			// tab title
			

				
			// Make tab_panel name, see if it exists
			//   -- if it exists, select it quietly and return
			
			tabname = "erxconfig";			
			if (( tab = tabExists( tabname )) != null ){
				tab.setSelected(true);
				return;
			}
			
			title = "eRx Config";
			
			// Create new tab and tabpanel
			tab = createTab( tabname, title );
			tabpanel = createTabpanel();
			
			// Create EditPt window
			ERxConfigEdit.show( tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
			return;
	}

	
	
	
	/**
	 * ERx Config
	 */
	public void createERxProvWin(){ showERxProvWin(); }
	
	public void showERxProvWin( ){
		
			Tab	tab;				// tab
			Tabpanel tabpanel;		// tabpanel
			String tabname;			// tab name
			String title;			// tab title
			

				
			// Make tab_panel name, see if it exists
			//   -- if it exists, select it quietly and return
			
			tabname = "erxprov";			
			if (( tab = tabExists( tabname )) != null ){
				tab.setSelected(true);
				return;
			}
			
			title = "eRx Providers";
			
			// Create new tab and tabpanel
			tab = createTab( tabname, title );
			tabpanel = createTabpanel();
			
			// Create EditPt window
			ERxProvWin.show( tabpanel );
			
			// Select this tabpanel
			tab.setSelected(true);
			return;
	}

	/**
	 * Create DgnWin
	 * 
	 * Create window to manage and Add new Diagnosis Codes
	 * 
	 */
	public void createDgnWin(){ showDgnWin(); }
	
	public void showDgnWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "DgnWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Dgn Codes" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create Dgn Home page
		DgnWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	/**
	 * Create SrvWin
	 * 
	 * Create window to manage and Add new Service Codes
	 * 
	 */
	public void createSrvWin(){ showSrvWin(); }
	
	public void showSrvWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "SrvWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Service Codes" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create Srv Home page
		SrvWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	/**
	 * Create PpoWin
	 * 
	 * Create window to manage and Add new Insurance Carriers
	 * 
	 */
	public void createPpoWin(){ showPpoWin(); }
	
	public void showPpoWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "PpoWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Insurance Carriers" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create Dgn Home page
		PpoWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	/**
	 * Create PhrdsWin
	 * 
	 * Create window to manage and Add new Pharmacies
	 * 
	 */
	public void createPhrdsWin(){ showPhrdsWin(); }
	
	public void showPhrdsWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "PhrdsWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Pharmacies" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create Dgn Home page
		PhrdsWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	
	
	
	/**
	 * Create ProbTblWin
	 * 
	 * Create window to manage the Problem List Master Table
	 * 
	 */
	public void createProbTblWin(){ showProbTblWin(); }
	
	public void showProbTblWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "probTblWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "ProbTbl" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		ProbTblWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	/**
	 * Create LabObsTblWin
	 * 
	 * Create window to manage the Lab Observation Master Table
	 * 
	 */
	public void createLabObsTblWin(){ showLabObsTblWin(); }
	
	public void showLabObsTblWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "labObsTblWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		System.out.println( "showLabObsTblWin() creating new tab" );
		tab = createTab( tabname, "LabObsTbl" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		LabObsTblWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	/**
	 * Create LabBatWin
	 * 
	 * Create window to manage the Lab Batch Table
	 * 
	 */
	public void createLabBatWin(){ showLabBatWin(); }
	
	public void showLabBatWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "labBatWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		System.out.println( "showLabBatWin() creating new tab" );
		tab = createTab( tabname, "LabBat" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		LabBatWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	/**
	 * Create LabFacilityWin
	 * 
	 * Create window to manage the Lab Facility Table
	 * 
	 */
	public void createLabFacilityWin(){ showLabFacilityWin(); }
	
	public void showLabFacilityWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "labFacilityWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		System.out.println( "showLabFacilityWin() creating new tab" );
		tab = createTab( tabname, "LabFacility" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		LabFacilityWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	
	/**
	 * Create LabImport
	 * 
	 * Create window to manage the Lab Import
	 * 
	 */
	public void createLabImport(){ showLabImportWin(); }
	
	public void showLabImportWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "labImportWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "LabImport" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		LabImport.show( tabpanel, tab );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	/**
	 * Create CDSRulesWin
	 * 
	 * Create window to manage the CDS Rules Table
	 * 
	 */
	public void createCDSRulesWin(){ showCDSRulesWin(); }
	
	public void showCDSRulesWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "cdsRulesWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "CDSRules" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		CDSRulesWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	

	
	/**
	 * Create Reminders
	 * 
	 * Create window to manage the Reminders
	 * 
	 */
	public void createRemindersWin(){ showRemindersWin(); }
	
	public void showRemindersWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "remindersWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Reminders" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		RemindersWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	

	
	
	/**
	 * Create Pt Lists
	 * 
	 * Create window to manage the Pt Lists
	 * 
	 */
	public void createPtListWin(){ showPtListWin(); }
	
	public void showPtListWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "ptlistWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "PtList" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		PtListWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	

	
	
	/**
	 * Create CQM
	 * 
	 * Create window to manage the Clinical Quality Measures
	 * 
	 */
	public void createCQMWin(){ showCQMWin(); }
	
	public void showCQMWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return	
		String tabname = "cqmWin";	
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "CQM" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		CQMWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	

	
	
	/**
	 * Create AMC
	 * 
	 * Create window to manage the Automated Measures Calculations
	 * 
	 */
	public void createAMCWin(){ showAMCWin(); }
	
	public void showAMCWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel


		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "amcWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Measures" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		AMCWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	

	
	
	
	
	/**
	 * Create UserEditWin
	 * 
	 * Create window to manage the current user's info
	 * 
	 */
	public void createUserEditWin(){ showUserEditWin(); }
	
	public void showUserEditWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

/*
		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return
		
		String tabname = "probTblWin";
		
		if (( tab = tabExists( tabname )) != null ){
			System.out.println( tabname + " exists");
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		System.out.println( "showProbTblWin() creating new tab" );
		tab = createTab( tabname, "ProbTbl" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
*/		
		// Get user record number from session attribute
		Rec userRec = (Rec) SystemHelpers.getSessionAttribute( "userrec" );
		// make sure record number is valid
		if ( ! Rec.isValid( userRec )){ SystemHelpers.seriousError( "showUserEditWin() bad userRec" ); return; }
		// Create UserEdit window
		UserEdit.edit( userRec, false, null );
		
		// Select this tabpanel
		//tab.setSelected(true);	
	}
	
	/**
	 * Create UserWin
	 * 
	 * Create window to manage the User
	 * 
	 */
	public void createUserWin(){ showUserWin(); }
	
	public void showUserWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Sys.USER_MANAGEMENT )){
			SystemHelpers.msgAccessDenied();
			return;
		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "userWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Users" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create PtChart
		UserWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	/**
	 * Create AuditWin
	 * 
	 * Create window to view the Audit Log
	 * 
	 */
	public void createAuditWin(){ showAuditWin(); }
	
	public void showAuditWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Sys.USER_MANAGEMENT )){
			SystemHelpers.msgAccessDenied();
			return;
		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "auditWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Audit Log" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create new Window
		AuditWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	/**
	 * Create SchWin
	 * 
	 * Create appointment scheduler window
	 * 
	 */
	public void createSchWin(){ showSchWin(); }
	
	public void showSchWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW ) && ! Perms.isRequestPermitted( Perms.Medical.VIEW )){
			SystemHelpers.msgAccessDenied();
			return;
		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "schWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Scheduler" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create new Window
		SchWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	/**
	 * Create EcsWin
	 * 
	 * Create window to manage Electronic Claims
	 * 
	 */
	public void createEcsWin(){ showEcsWin(); }
	
	public void showEcsWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
			SystemHelpers.msgAccessDenied();
			return;
		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "ecsWin";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "ECS" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create window
		EcsWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	
	/**
	 * Create PmImportWin
	 * 
	 * Create window to Import Documents
	 * 
	 */
	public void createPmImportWin(){ showPmImportWin(); }
	
	public void showPmImportWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "pmimport";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "PmImport" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create window
		PmImportWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	
	
	
	

	/**
	 * Create VaccineCVXWin
	 * 
	 * Create window to View/Edit Vaccine CVX Records
	 * 
	 */
	public void createVaccineCVXWin(){ showVaccineCVXWin(); }
	
	public void showVaccineCVXWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "vaccvx";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Vaccine CVX" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create window
		VaccineCVXWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	

	
	/**
	 * Create VaccineMVXWin
	 * 
	 * Create window to View/Edit Vaccine MVX Records
	 * 
	 */
	public void createVaccineMVXWin(){ showVaccineMVXWin(); }
	
	public void showVaccineMVXWin(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "vacmvx";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "Vaccine MVX" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		// Create window
		VaccineMVXWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}
	


	
	/**
	 * Create CdmDashboard Win
	 * 
	 * Create window to View CDM Dashboard
	 * 
	 */
	public void createCdmDashboard(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "cdmDashboard";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "CDM Dashboard" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		
		
		// Create window
		CdmWin.show( tabpanel );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}

	

	/**
	 * Create StddictWin
	 * 
	 * Create window to manage standard dictations
	 * 
	 */
	public void createStdDictWin() {
		showStdDictWin();
	}

	public void showStdDictWin() {

		Tab tab; // tab
		Tabpanel tabpanel; // tabpanel

		// Does user have permission?
		if (!Perms.isRequestPermitted(Perms.Sys.MAINTENANCE)) {
			SystemHelpers.msgAccessDenied();
			return;
		}

		// Make tab_panel name, see if it exists
		// -- if it exists, select it quietly and return
		String tabname = "stdDictWin";
		if ((tab = tabExists(tabname)) != null) {
			tab.setSelected(true);
			return;
		}

		// Create new tab and tabpanel
		tab = createTab(tabname, "Standard Dictations");
		tabpanel = createTabpanel();
		tabpanel.setWidth("100%");
		tabpanel.setHeight("100%");

		StdDictWin.show(tabpanel);

		// Select this tabpanel
		tab.setSelected(true);
	}





	/**
	 * Create XfrVitals Win
	 * 
	 * Create window to View/Edit Vaccine MVX Records
	 * 
	 */
	public void createXfrVitals(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "xfrVitals";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "XFR Vitals" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		String start = DialogHelpers.Textbox("Transfer PARs", "Enter a starting date.");
		if (( start == null ) || ( start.length() < 1 )) start = "1-1-1982";
		Date dateStart = new Date( start );
		
		String end = DialogHelpers.Textbox("Transfer PARs", "Enter an end date.");
		if (( end == null ) || ( end.length() < 1 )) end = Date.today().getPrintable();
		Date dateEnd = new Date( end );
		
		DialogHelpers.Messagebox( "Start.  Transferring from " + dateStart.getPrintable() + " to " + dateEnd.getPrintable() +"." );
		
		// Create window
		TransferVitals.transfer( dateStart, dateEnd );
		
		DialogHelpers.Messagebox( "Done" );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}

	
	/**
	 * Create XfrPARs Win
	 * 
	 * Create window to View/Edit Vaccine MVX Records
	 * 
	 */
	public void createXfrPARs(){
		
		Tab	tab;				// tab
		Tabpanel tabpanel;		// tabpanel

		// Does user have permission?
//		if ( ! Perms.isRequestPermitted( Perms.Financial.VIEW )){
//			SystemHelpers.msgAccessDenied();
//			return;
//		}

		// Make tab_panel name, see if it exists
		//   -- if it exists, select it quietly and return		
		String tabname = "xfrPARs";		
		if (( tab = tabExists( tabname )) != null ){
			tab.setSelected(true);
			return;
		}
		
		// Create new tab and tabpanel
		tab = createTab( tabname, "XFR PARs" );
		tabpanel = createTabpanel();
		tabpanel.setWidth( "100%" );
		tabpanel.setHeight( "100%" );
		
		String start = DialogHelpers.Textbox("Transfer PARs", "Enter a starting date.");
		if (( start == null ) || ( start.length() < 1 )) start = "1-1-1982";
		
		String end = DialogHelpers.Textbox("Transfer PARs", "Enter an end date.");
		if (( end == null ) || ( end.length() < 1 )) end = Date.today().getPrintable();
		
		DialogHelpers.Messagebox( "Start" );
		
		
		
		// Create window
		TransferPars.transfer( new Date( start ), new Date( end ));
		
		DialogHelpers.Messagebox( "Done" );
		
		// Select this tabpanel
		tab.setSelected(true);	
	}

	
}

/**/
