package palmed;

import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class ImmWinController extends GenericForwardComposer {

	Window immWin;					// autowired
	private Listbox listbox;		// autowired
	
	
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
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "Error: ImmWinController() bad ptRec" );

		
		refreshList();
		
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notifier.Event.IMMUNIZATIONS );
		return;
	}
	
	
	
	
	
	
	
	public void refreshList(){
		
		// clear listbox
		ZkTools.listboxClear( listbox );
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getImmuneReca();

		// finished if there are no entries to read
		if ( ! Reca.isValid( reca )) return;
		
		for ( ; reca.getRec() != 0; ){
			
			Immunizations imm = new Immunizations( reca );
			
			// display this one?
			if ( imm.getStatus() == Immunizations.Status.CURRENT ){
			
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( listbox );
				new Listcell( imm.getDate().getPrintable(9)).setParent( i );
				new Listcell( imm.getDesc()).setParent( i );
				new Listcell( imm.getCVX()).setParent( i );
				new Listcell( imm.getMVX()).setParent( i );
				new Listcell( imm.getLotNumber()).setParent( i );
				new Listcell( imm.getExpDate().getPrintable(9)).setParent( i );
				new Listcell( imm.getSiteTxt()).setParent( i );
				
				String s = imm.getAmount().trim();
				String u = imm.getUnits().getLabel();
				new Listcell( s+u ).setParent( i );
				
				s = imm.getNoteTxt();
				if (( s != null ) && ( s.length() > 0 )){
					new Listcell( "Note" ).setParent( i );
					i.setTooltiptext( s );
				} else {
					new Listcell( "" ).setParent( i );
				}
				
				// store reca in listitem's value
				i.setValue( reca );
			}
			
			
			// get next reca in list	
			reca = imm.getLLHdr().getLast();
		}


		return;
	}
	
	
	
	
	public void onClick$btnAdd(){
		
		ImmAdd.add( ptRec, immWin );
		Notifier.notify( ptRec, Notifier.Event.IMMUNIZATIONS );
		//refreshList();
		return;
	}
	
	public void onClick$btnEdit(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca immReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid( immReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		ImmAdd.edit( ptRec, immReca, immWin );
		Notifier.notify( ptRec, Notifier.Event.IMMUNIZATIONS );
		//refreshList();
		return;
	}
	
	
	
	
	
	
	
	public void onClick$btnRemove(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca immReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(immReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		//ImmRemove.show( ptRec, immReca, immWin );
		Notifier.notify( ptRec, Notifier.Event.IMMUNIZATIONS );
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
		sb.append( "<H3>Immunizations<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Date</td><td>Immunization</td><td>Severity</td><td>Domain</td><td>Reaction/Symptom</td></tr>" );
		
		for( int i = 0; i < listbox.getItemCount(); ++i ){
			Listitem li = listbox.getItemAtIndex( i );
			Reca immReca = (Reca) li.getValue();
			Immunizations imm = new Immunizations( immReca );
			sb.append( "<tr><td>" + imm.getDate().getPrintable(9) + "</td><td>" + imm.getDesc() + "</td></tr>" );
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
		iframe.setParent( immWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}
	
	
	public void onClick$btnExport() throws InterruptedException{

		if ( listbox.getSelectedCount() > 0 ){
			
			// export selected item only or complete immunization history?
			switch ( Messagebox.show( "Export selected entry?", "Export?", (Messagebox.YES + Messagebox.NO + Messagebox.CANCEL), Messagebox.QUESTION )){
			case Messagebox.YES:
				
				// Export this one entry
				Reca immReca = (Reca) listbox.getSelectedItem().getValue();
				if ( ! Reca.isValid(immReca)){
					DialogHelpers.Messagebox( "Invalid item selected." );
					return;
				}
				download( ImmExport.exportOne( ptRec, immReca ));
				break;
				
			case Messagebox.NO:
				
				if ( Messagebox.show( "Export complete immunization history?", "Export?",
						Messagebox.YES + Messagebox.NO + Messagebox.CANCEL, Messagebox.QUESTION )
					== Messagebox.YES ){
						
					// Export complete immunization history					
					download( ImmExport.exportAll( ptRec ));
					return;
				}
				// break intentionally left out
				
			case Messagebox.CANCEL:
				
				DialogHelpers.Messagebox( "Cancelled." );
				break;
			}
			
		
		} else {
			

			if ( Messagebox.show( "Export complete immunization history?", "Export?",
					Messagebox.YES + Messagebox.NO + Messagebox.CANCEL, Messagebox.QUESTION )
				== Messagebox.YES ){
					
				// Export complete immunization history
				
				download( ImmExport.exportAll( ptRec ));
				return;
				
			} else {
				
				DialogHelpers.Messagebox( "Cancelled." );
			}

			return;
		}
		

		return;
	}

	
	
	public void download( String hl7 ){
		
		Filedownload.save( hl7, "text/plain", "immune.txt" );
		DialogHelpers.Messagebox( "Finished" );
	}

}
