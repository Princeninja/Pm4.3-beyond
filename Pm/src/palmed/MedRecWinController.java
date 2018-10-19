package palmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class MedRecWinController extends GenericForwardComposer {

	//Window medRecWin;		// i'll try to do this without a window id so I can have more than 1 up
	Listbox medsListbox;	// autowired
	Rec ptRec;
	String text;
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ text = (String) myMap.get( "text" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		//System.out.println( "MedsWinController() ptRec=" + ptRec.getRec());
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "MedRecWinController.doAfterCompose() ptRec invalid," );

		refreshList();
		return;
	}
	
	
	
	public void refreshList(){
		
		if (( text == null ) || ( text.length() < 1 )) return;
		
		ZkTools.listboxClear( medsListbox );
		
		StringReader sr = new StringReader( text );
		BufferedReader br = new BufferedReader( sr );
		String line;
		
		try {
			while (( line = br.readLine()) != null ){
				line = line.trim();
				if ( line.length() > 0 ){
					ZkTools.appendToListbox( medsListbox, line, null );
				}
			}
		} catch (IOException e) {
			// ignore
		}
		
	}

	
	
	
	
	public void onClick$btnDelete( Event ev ){

		if ( medsListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		medsListbox.removeItemAt( medsListbox.getSelectedIndex());
		return;
	}
	
	
	
	public void onClick$btnReload( Event ev ){

		refreshList();
		return;
	}
	
	
	
	
	
	
	public void onClick$btnClose( Event ev ){
		((Component) ev.getTarget().getSpaceOwner()).detach();
		return;
	}
	
	
	
	public void onClick$btnPrint( Event ev ){
		
		System.out.println( "onClick$btnPrint" );
		
		
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
		sb.append( "<H2>PAL/MED<H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "<H3>" );
		sb.append( "<space>");
		
		DirPt dirpt = new DirPt( ptRec );
		
		sb.append( "<H3>Patient: " + dirpt.getName().getPrintableNameLFM());
		sb.append( "<H3>DOB: " + dirpt.getBirthdate().getPrintable(9));
		sb.append( "<space>" );
		sb.append( "<H3>Medication Reconciliation List<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Medication</td></tr>" );
		
		for( int i = 0; i < medsListbox.getItemCount(); ++i ){
			Listitem li = medsListbox.getItemAtIndex( i );
			sb.append( "<tr><td>" + li.getLabel() + "</td></tr>" );
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
		iframe.setParent( ((Component) ev.getTarget().getSpaceOwner()) );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.MED_PRINT, ptRec, Pm.getUserRec(), null, "Medication Reconciliation List" );
		return;
	}

}
