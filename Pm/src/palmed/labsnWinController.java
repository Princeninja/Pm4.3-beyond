package palmed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
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

public class labsnWinController extends GenericForwardComposer {

	Window labsWin;					// autowired
	Window labsnWin;					// autowired
	private Listbox listbox;		// autowired - pars listbox
	private Listbox listboxn;		// autowired - pars listbox
	private Groupbox gbObs;         // autowired -Groupbox
	
	
	private Rec	ptRec;		// patient record number
	private List<String[]> obsstrList; 
	private String BatStr, date;

	
	
	

	
	@SuppressWarnings("unchecked")
	public void doAfterCompose( Component component ){
		//System.out.println("here?");
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
			//System.out.println("here with component:"+component);
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
				try{ obsstrList = (List<String[]>) myMap.get( "obsstrList" ); } catch ( Exception e ) { /* ignore */ };
				try{ BatStr = (String) myMap.get( "BatStr" ); } catch ( Exception e ) { /* ignore */ };
				try{ date = (String) myMap.get( "date" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ptRec.getRec() < 2){
			System.out.println( "Error: LabsWinController() ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}

		refreshList(BatStr);
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList(BatStr);
					return false;
				}}, ptRec, Notifier.Event.LAB );
		return;
	}
	
	
	
	
	public void refreshList( String BatStr ){
		
		// clear listbox
		ZkTools.listboxClear( listbox );
		
		
		
		for ( int j=0; j< obsstrList.size(); j++ ){
			
				if ( obsstrList.get(j)[5].equals(BatStr) && obsstrList.get(j)[6].equals(date)){				
				
				String [] obs = obsstrList.get(j);
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( listbox );
				if ( obs[2].contains("Abnormal") || obs[2].contains("High") || obs[2].contains("Low")  ){
					//System.out.println("AB here:");
									
					Listcell Date = new Listcell( obs[0] );
					//Date.setStyle("color:red;");
					Date.setParent( i );
					
					new Listcell( obs[1] ).setParent( i );
					new Listcell( obs[7] ).setParent( i );
					new Listcell( obs[2] ).setParent( i );
					new Listcell( obs[3] ).setParent( i );
					
					i.setStyle("color:red");
				}
				else {
				new Listcell( obs[0] ).setParent( i );
				new Listcell( obs[1] ).setParent( i );
				new Listcell( obs[7] ).setParent( i );
				new Listcell( obs[2] ).setParent( i );
				new Listcell( obs[3] ).setParent( i ); 
				//i.setStyle("color:blue;");
				}
				
			
				Reca obsreca = new Reca(); 
				obsreca = Reca.fromString(obs[4]);
				// store PAR reca in listitem's value
				i.setValue( obsreca );
				
				LabResult lab = new LabResult( obsreca );
				
				Class<? extends Notes> noteClass = NotesLab.class;
				
				Notes note = null;
				note = Notes.newInstance( noteClass );
				note.read( lab.getResultNoteReca() );
				String Notestat = "";
				
				if (note.getNoteText().length() > 1) { Notestat = "Yes";}
				else { Notestat = "No"; }
				
				new Listcell( Notestat ).setParent( i );
				
			}
			
			
		}
		


		return;
	}
	
	
	
	public void onClick$btnAdd(){
		
		LabResEdit.add( ptRec, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		refreshList(BatStr);
		return;
	}
	
	public void onDoubleClick( Event ev ){  onClick$btnEdit(); }
	
	public void onClick$btnEdit(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca labReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(labReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		LabResEdit.edit( ptRec, labReca, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		refreshList(BatStr);
		return;
	}
	
	
	public void onClick$btnCancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave this Battery View ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			closeWin();
		}		
		return;
	}
		
	private void closeWin(){		
		// close Win
		
		//LabsWinController lwc = new LabsWinController();
		//lwc.onnull(labsnWin);
		labsnWin.detach();
		
		return;
	}
	
	public void onClick$btnRemove(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca labReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(labReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		LabResRemove.show( ptRec, labReca, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
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
		sb.append( "<H3>PARs/Allergies<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Start Date</td><td>Medication</td><td>Severity</td><td>Domain</td><td>Reaction/Symptom</td></tr>" );
		
		for( int i = 0; i < listbox.getItemCount(); ++i ){
			Listitem li = listbox.getItemAtIndex( i );
			Reca parReca = (Reca) li.getValue();
			Par par = new Par( parReca );
			sb.append( "<tr><td>" + par.getDate().getPrintable(9) + "</td><td>" + par.getParDesc() + "</td><td>" + par.getSeverity().getLabel() + "</td><td>" +
					par.getDomain().getLabel() + "</td><td>" + par.getSymptomsText( true ) + "</td></tr>" );
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
		iframe.setParent( labsWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PAR_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}

}

	
	
	
	
	
	
	
	
	
	
	

