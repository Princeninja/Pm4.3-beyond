package palmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.WildCardFilenameFilter;

public class EcsWinController extends GenericForwardComposer {
	
	
	private Listbox ecsYearListbox;		// autowired
	
	private Listbox ecsRptListbox;		// autowired
	private Textbox ecsRptTextbox;		// autowired
	private Html ecsRptHTML;				// autowired
	private Window ecsRptHtmlWin;		// autowired
	private Groupbox ecsRptHtmlGroupbox;

	private Listbox ecsNsfListbox;		// autowired
	private Textbox ecsNsfTextbox;		// autowired
	private Window ecsNsfHtmlWin;		// autowired
	private Html ecsNsfHTML;				// autowired
	private Groupbox ecsNsfHtmlGroupbox;

	
	private int textType = 0;			// text type currently displayed: 0-none, 1-text, 2-html
	private int NsfTextType = 0;			// text type currently displayed: 0-none, 1-text, 2-html
	
	
	

	public EcsWinController() {
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

		
		// fill report years listbox
		for ( int year = Date.today().getYear(); year > 2000; --year ){
			Listitem i = new Listitem( String.valueOf( year ));
			i.setValue( year );
			i.setParent( ecsYearListbox );
			
		}
		
		// select current year
		ecsYearListbox.setSelectedIndex( 0 );
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_ADD, null, Pm.getUserRec(), null, null );
		
		// populate list
		refreshRptList();
		refreshNsfList();

		return;
	}
	
	
	
	
	public void refreshRptList(){

		// clear listbox
		usrlib.ZkTools.listboxClear( ecsRptListbox );		
		
		// get selected year
		int year = (Integer) ecsYearListbox.getSelectedItem().getValue();
		
		// build file name filter
		String filter = String.format( "rp*%02d_*_*.ecs.Z", year % 100 );
		System.out.println( "filename filter=" + filter );
		
		// populate list
		
		File file = new File( /*"c:/Windows" ); */  Pm.getMedPath() + "/ecs/webmd/oldecs" );
		File list[] = file.listFiles( new WildCardFilenameFilter( filter ));
		
		if ( list != null ){
			
			for ( int cnt = 0; cnt < list.length; ++cnt ){
				
				//String s = soap.toString();
				System.out.println( list[cnt] );
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( ecsRptListbox );
				new Listcell( list[cnt].getName() ).setParent( i );
				new Listcell( "" + list[cnt].length() ).setParent( i );
				i.setValue( list[cnt] );		// set list[] (File) into listitem for later retrieval
				
				
			}
		}

		return;
	}
	
	public void refreshNsfList(){

		// clear listbox
		usrlib.ZkTools.listboxClear( ecsNsfListbox );		
		
		// get selected year
		int year = (Integer) ecsYearListbox.getSelectedItem().getValue();
		
		// build file name filter
		String filter = String.format( "ns*%02d_*.ecs.Z", year % 100 );
		System.out.println( "filename filter=" + filter );
		
		// populate list
		
		File file = new File( /*"c:/Windows" ); */  Pm.getMedPath() + "/ecs/webmd/oldecs" );
		File list[] = file.listFiles( new WildCardFilenameFilter( filter ));
		
		if ( list != null ){
			
			for ( int cnt = 0; cnt < list.length; ++cnt ){
				
				//String s = soap.toString();
				System.out.println( list[cnt] );
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( ecsNsfListbox );
				new Listcell( list[cnt].getName() ).setParent( i );
				new Listcell( "" + list[cnt].length() ).setParent( i );
				i.setValue( list[cnt] );		// set list[] (File) into listitem for later retrieval
				
				
			}
		}

		return;
	}
	
	public void onSelect$ecsYearListbox( Event e ){

		refreshRptList();
		refreshNsfList();
		return;
	}
	
	public void onSelect$ecsRptListbox( Event e ){

		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		
		// get selected item, get file name
		File f = (File) ecsRptListbox.getSelectedItem().getValue();
		
		// read file
		//String txt = phyex.getText();
		System.out.println( "File ==>" + f.getAbsolutePath() );
		
		if ( f.getName().endsWith( ".Z" )){
			
			try {
			Process p=Runtime.getRuntime().exec( "/usr/bin/zcat " + f.getAbsolutePath()); 
			//p.waitFor(); 
			reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
			} catch ( Exception e1 ) { System.out.println( "Exception" ); }
			
		} else {
			try {
			reader=new BufferedReader(new FileReader( f )); 
			} catch ( Exception e1 ) { System.out.println( "Exception" ); }
		}


		try {
		String line=reader.readLine(); 
		sb.append( line );
		sb.append( '\n' );
		
		while(line!=null){ 
			System.out.println(line); 
			line=reader.readLine(); 
			sb.append( line );
			sb.append( '\n' );
		} 

		} 
		catch( Exception e1) {} 

		System.out.println("Done"); 
		String txt = sb.toString();
	
	

		// what type of report?
		int newType = txt.startsWith( "<HTML>" ) ? 2: 1;

		// if different type, remove old components
		if ( newType != textType ){
			
			// textType == 0 - nothing to remove
			
			if ( textType == 1 ){
				
				ecsRptTextbox.detach();
				ecsRptTextbox = null;
				
			} else if ( textType == 2 ){
				

				ecsRptHTML.detach();
				ecsRptHtmlGroupbox.detach();
				ecsRptHTML = null;
				ecsRptHtmlGroupbox = null;
			}
		}
		
		
		// if different type, make new components
		if ( newType != textType ){
			
			if ( newType == 1 ){
				
				ecsRptTextbox = new Textbox();
				ecsRptTextbox.setRows( 2 );
				ecsRptTextbox.setVflex( "1" );
				ecsRptTextbox.setHflex( "1" );
				ecsRptTextbox.setStyle( "font-family: monospace" );
				//ecsRptTextbox.setStyle( "text-wrap: none" );
				ecsRptTextbox.setParent( ecsRptHtmlWin );
				
			} else {
				
				ecsRptHtmlGroupbox = new Groupbox();
				ecsRptHtmlGroupbox.setHflex( "1" );
				ecsRptHtmlGroupbox.setVflex( "1" );
				ecsRptHtmlGroupbox.setStyle( "overflow: auto" );
				ecsRptHtmlGroupbox.setParent( ecsRptHtmlWin );
				
				ecsRptHTML = new Html();
				ecsRptHTML.setHflex( "1" );
				ecsRptHTML.setVflex( "1" );	
				ecsRptHTML.setParent( ecsRptHtmlGroupbox );
			}
		}


		// set content		
		if ( newType == 1 ){
			
			ecsRptTextbox.setValue( txt );

		} else {
			
			ecsRptHTML.setContent( txt );
		}
		
		textType = newType;
		

		return;
	}
	
	public void onSelect$ecsNsfListbox( Event e ){

		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		
		// get selected item, get file name
		File f = (File) ecsNsfListbox.getSelectedItem().getValue();
		
		// read file
		//String txt = phyex.getText();
		System.out.println( "File ==>" + f.getAbsolutePath() );
		
		if ( f.getName().endsWith( ".Z" )){
			
			try {
			Process p=Runtime.getRuntime().exec( "/usr/bin/zcat " + f.getAbsolutePath()); 
			//p.waitFor(); 
			reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
			} catch ( Exception e1 ) { System.out.println( "Exception" ); }
			
		} else {
			try {
			reader=new BufferedReader(new FileReader( f )); 
			} catch ( Exception e1 ) { System.out.println( "Exception" ); }
		}


		try {
		String line=reader.readLine(); 
		sb.append( line );
		sb.append( '\n' );
		
		while(line!=null){ 
			System.out.println(line); 
			line=reader.readLine(); 
			sb.append( line );
			sb.append( '\n' );
		} 

		} 
		catch( Exception e1) {} 

		System.out.println("Done"); 
		String txt = sb.toString();
	
	

		// what type of report?
		int newType = txt.startsWith( "<HTML>" ) ? 2: 1;

		// if different type, remove old components
		if ( newType != NsfTextType ){
			
			// textType == 0 - nothing to remove
			
			if ( NsfTextType == 1 ){
				
				ecsNsfTextbox.detach();
				ecsNsfTextbox = null;
				
			} else if ( NsfTextType == 2 ){
				

				ecsNsfHTML.detach();
				ecsNsfHtmlGroupbox.detach();
				ecsNsfHTML = null;
				ecsNsfHtmlGroupbox = null;
			}
		}
		
		
		// if different type, make new components
		if ( newType != NsfTextType ){
			
			if ( newType == 1 ){
				
				ecsNsfTextbox = new Textbox();
				ecsNsfTextbox.setRows( 2 );
				ecsNsfTextbox.setVflex( "1" );
				ecsNsfTextbox.setHflex( "1" );
				ecsNsfTextbox.setStyle( "font-family: monospace; text-wrap: none" );
				//ecsNsfTextbox.setStyle( "text-wrap: none" );
				//ecsNsfTextbox.setStyle( "overflow: auto" );
				ecsNsfTextbox.setParent( ecsNsfHtmlWin );
				
			} else {
				
				ecsNsfHtmlGroupbox = new Groupbox();
				ecsNsfHtmlGroupbox.setHflex( "1" );
				ecsNsfHtmlGroupbox.setVflex( "1" );
				ecsNsfHtmlGroupbox.setStyle( "overflow: auto" );
				ecsNsfHtmlGroupbox.setParent( ecsNsfHtmlWin );
				
				ecsNsfHTML = new Html();
				ecsNsfHTML.setHflex( "1" );
				ecsNsfHTML.setVflex( "1" );	
				ecsNsfHTML.setParent( ecsNsfHtmlGroupbox );
			}
		}


		// set content		
		if ( newType == 1 ){
			
			ecsNsfTextbox.setValue( txt );

		} else {
			
			ecsNsfHTML.setContent( txt );
		}
		
		NsfTextType = newType;
		

		return;
	}
	
	public void onClick$btnPrint(){

		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_ADD, null, Pm.getUserRec(), null, null );

			return;
	}
	
	

	public void onClick$btnCompile(){

		// log the access
		//AuditLogger.recordEntry( AuditLog.Action.MED_ADD, null, Pm.getUserRec(), null, null );
		
		
		
		// Compile claims
		new Ecs5010Compiler().compile();
		

			return;
	}
	
	
	public void onClick$btnIndexDgn(){

		// log the access
		//AuditLogger.recordEntry( AuditLog.Action.MED_ADD, null, Pm.getUserRec(), null, null );
		
		
		
		// Compile claims
		new Ecs5010Compiler().fixDgn();
		DialogHelpers.Messagebox( "Diagnoses Indexed" );

			return;
	}
	
	


}
