package palmed;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import palmed.SchCal.SchTm;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.ZkTools;

public class SchWinController extends GenericForwardComposer {


	private Listbox schListbox;			// autowired
	private Listbox lboxMonth;			// autowired
	private Listbox lboxDay;			// autowired
	private Listbox lboxYear;			// autowired
	//private Radio rbActive;			// autowired
	//private Radio rbInactive;			// autowired
	//private Radio rbAll;				// autowired
	private Window schWin;				// autowired
	//private Listheader status;		// autowired
	
	int month = 0;
	int day = 0;
	int year = 0;
	
	
	

	public SchWinController() {
		super();
	}


	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
/*		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { ; ignore };
			}
		}		
*/		
		
		
		// select current month and year
		ZkTools.setListboxSelectionByLabel(lboxMonth, String.valueOf( Date.today().getMonth()));
		ZkTools.setListboxSelectionByLabel(lboxDay, String.valueOf( Date.today().getDay()));
		ZkTools.setListboxSelectionByLabel(lboxYear, String.valueOf( Date.today().getYear()));
		
	
		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
/*	
	// Watch for radiobutton to change
	public void onCheck$rbActive( Event ev ){
		refreshList();
	}
	public void onCheck$rbInactive( Event ev ){
		refreshList();
	}
	public void onCheck$rbAll( Event ev ){
		refreshList();
	}
*/	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){

/*		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( rbActive.isSelected()) display = 1;
		if ( rbInactive.isSelected()) display = 2;
		if ( rbAll.isSelected()) display = 3;

		// display status column header?
		status.setVisible(( display == 3 ) ? true: false );
*/
		
		
		if ( schListbox == null ) return;
		
		// remove all items
		ZkTools.listboxClear( schListbox );
		
		
		
		// get month and year selections		
		if (( lboxMonth.getSelectedCount() < 1 ) || ( lboxYear.getSelectedCount() < 1 ) || ( lboxDay.getSelectedCount() < 1 )) return;
		month = EditHelpers.parseInt( lboxMonth.getSelectedItem().getLabel());
		day = EditHelpers.parseInt( lboxDay.getSelectedItem().getLabel());
		year = EditHelpers.parseInt( lboxYear.getSelectedItem().getLabel());
		
		// verify valid month, year
		if (( month < 1 ) || ( month > 12 )) month = 1;
		if (( day < 1 ) || ( day > 31 ))
		if (( year < 2011 ) || ( year > Date.today().getYear())) year = Date.today().getYear();
		
		// populate list
		//AuditLog.fillListbox( schListbox, month, year, /*userRec*/ null, /*ptRec*/ null, /*action*/ null );
		SchCal cal = new SchCal();
		
		SchSlot slot = null;
		                cal.new SchTm().read(0, 0, 0, 0, 0, 0).getSlot(0);
		return;
	}
	
	
	
	// Open dialog to enter new user
	
	public void onClick$btnRefresh( Event ev ){
		
		// get month and year selections
		
		if (( lboxMonth.getSelectedCount() < 1 ) || ( lboxYear.getSelectedCount() < 1 )){
			DialogHelpers.Messagebox( "A month, day, and a year must be selected." );
			return;
		}
		
		refreshList();

		return;
	}
	
	
	
	// Open dialog to print current listbox
	
	public void onClick$btnPrint( Event ev ){
	
		//System.out.println( "onClick$btnPrint" );
		
		
		// Generate HTML report text
		
		StringBuilder sb = new StringBuilder( 1000 );
		
		sb.append( "<HTML>" );
		sb.append( "<HEAD>" );
		
		sb.append( "<style type='text/css'>" );
		//sb.append( "font-size:large" );
		//sb.append( "body{font-size:100%;}" );
		//sb.append( "h3 {font-size:200%;}" );
		//sb.append( "p {font-size:100%;}" );
		//sb.append( "*{font-size:40%;}" );

		sb.append( "</style>" );
		
		sb.append( "</HEAD>" );
		sb.append( "<BODY>" );
		sb.append( "<H2>PAL/MED</H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "/<H3>" );
		sb.append( "<space/>");
				
		sb.append( "<H3>Audit Log: " + month + "/" + year + "</H3>" );
		sb.append( "<space/>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Date</td><td>Time</td><td>User</td><td>Patient</td><td>Action</td><td>Note</td></tr>" );
		
		for( int i = 0; i < schListbox.getItemCount(); ++i ){
			Listitem li = schListbox.getItemAtIndex( i );
			Rec logRec = (Rec) li.getValue();
			
			AuditLog log = AuditLog.read( month, year, logRec );
			
			sb.append( "<tr><td>" + log.getDate().getPrintable(9) + "</td>" );
			sb.append( "<td>" + log.getTime().getPrintable() + "</td>" );
			sb.append( "<td>" + pmUser.getUser( log.getUserRec()) + "</td>" );
			
			String s = Rec.isValid( log.getPtRec()) ? (new DirPt(log.getPtRec())).getName().getPrintableNameLFM(): "";
			sb.append( "<td>" + s + "</td>" );
			
			sb.append( "<td>" + log.getAction().getLabel() + "</td>" );
			sb.append( "<td>" + log.getNote() + "</td>" );
			sb.append( "</tr>" );
		}
		
		sb.append( "</table>" );
		sb.append( "END REPORT" );

		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		sb.append( "</BODY>" );
		sb.append( "</HTML>" );
		
		String html = sb.toString();
		
		//System.out.println( html );

		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );
		if ( amedia == null ) System.out.println( "AMedia==null" );

		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( schWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.AUDIT_PRINT, null, Pm.getUserRec(), null, null );
		
		return;
	}
	
	
	public void onSelect$lboxMonth(){
		refreshList();
	}
	
	public void onSelect$lboxYear(){
		refreshList();
	}
	
	
}
