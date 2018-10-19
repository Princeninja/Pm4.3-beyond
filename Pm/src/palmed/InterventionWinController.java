package palmed;

import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.StringHelpers;
import usrlib.ZkTools;

public class InterventionWinController extends GenericForwardComposer {

	Window ivWin;			// autowired
	private Listbox listbox;		// autowired - intervention listbox
	Radio r_current;
	Radio r_past;
	Radio r_history;
	
	
	private Rec	ptRec;		// patient record number


	
	

	
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
			}
		}
		

		
		// make sure we have a patient
		if ( ptRec.getRec() < 2){
			System.out.println( "bad ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}

		refreshList();
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notifier.Event.INTERVENTION );
		return;
	}
	
	
	
	
	
	
	
	public void refreshList(){
		
		// clear listbox
		ZkTools.listboxClear( listbox );
		
		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_current.isSelected()) display = 1;
		if ( r_past.isSelected()) display = 2;
		if ( r_history.isSelected()) display = 3;
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getInterventionsReca();

		// finished if there are no entries to read
		if ( ! Reca.isValid( reca )) return;
		
		for ( ; reca.getRec() != 0; ){
			
			Intervention iv = new Intervention( reca );
			
			// display this one?
			// is this type selected?
			Intervention.Status status = iv.getStatus();
			
			if ((( status == Intervention.Status.CURRENT ) && (( display & 1 ) != 0 ))
				|| (( status == Intervention.Status.REMOVED ) && (( display & 2 ) != 0 ))
				|| (( status == Intervention.Status.CHANGED ) && (( display & 2 ) != 0 ))){
			
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( listbox );
				new Listcell( iv.getDate().getPrintable(9)).setParent( i );
				new Listcell( iv.getType().getLabel()).setParent( i );
				new Listcell( StringHelpers.truncate( iv.getNoteText(), 30 )).setParent( i );
				new Listcell( iv.getStatus().getLabel()).setParent( i );
				
				// store Intervention reca in listitem's value
				i.setValue( reca );
			}
			
			
			// get next reca in list	
			reca = iv.getLLHdr().getLast();
		}


		return;
	}
	
	
	
	
	public void onClick$btnAdd(){
		
		InterventionAdd.add( ptRec, ivWin );
		Notifier.notify( ptRec, Notifier.Event.INTERVENTION );
		//refreshList();
		return;
	}
	
	public void onClick$btnEdit(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca ivReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(ivReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		InterventionAdd.edit( ptRec, ivReca, ivWin );
		Notifier.notify( ptRec, Notifier.Event.INTERVENTION );
		//refreshList();
		return;
	}
	
	
	
	// Watch for radiobutton to change
	public void onCheck$r_current( Event ev ){
		refreshList();
	}
	public void onCheck$r_past( Event ev ){
		refreshList();
	}
	public void onCheck$r_history( Event ev ){
		refreshList();
	}
	
	

	
	
	
	
	public void onClick$btnRemove(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca ivReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(ivReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		InterventionRemove.show( ptRec, ivReca, ivWin );
		Notifier.notify( ptRec, Notifier.Event.PAR );
		//refreshList();
		return;
	}
	
	
	public void onClick$btnPrint(){
		
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
		sb.append( "<H2>PAL/MED<H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "<H3>" );
		sb.append( "<space>");
		
		DirPt dirpt = new DirPt( ptRec );
		
		sb.append( "<H3>Patient: " + dirpt.getName().getPrintableNameLFM());
		sb.append( "<H3>DOB: " + dirpt.getBirthdate().getPrintable(9));
		sb.append( "<space>" );
		sb.append( "<H3>Interventions<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Start Date</td><td>Intervention</td></tr>" );
		
		for( int i = 0; i < listbox.getItemCount(); ++i ){
			Listitem li = listbox.getItemAtIndex( i );
			Reca ivReca = (Reca) li.getValue();
			Intervention iv = new Intervention( ivReca );
			sb.append( "<tr><td>" + iv.getDate().getPrintable(9) + "</td><td>" + iv.getType().getLabel() + "</td></tr>" );
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
		iframe.setParent( ivWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.INTERVENTIONS_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}



}
