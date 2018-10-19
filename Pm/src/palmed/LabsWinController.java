package palmed;

import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class LabsWinController extends GenericForwardComposer {

	Window labsWin;					// autowired
	private Listbox listbox;		// autowired - pars listbox
	
	
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
			System.out.println( "Error: LabsWinController() ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}

		refreshList();
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notifier.Event.LAB );
		return;
	}
	
	
	
	
	
	
	
	public void refreshList(){
		
		// clear listbox
		ZkTools.listboxClear( listbox );
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getLabResultReca();

		// finished if there are no entries to read
		if ( ! Reca.isValid( reca )) return;
		
		for ( ; reca.getRec() != 0; ){
			
			LabResult lab = new LabResult( reca );
			
			// display this one?
			if ( lab.getStatus() == LabResult.Status.ACTIVE ){
			
				String strBat = "";
				Rec batRec = lab.getLabBatRec();
				if ( Rec.isValid( batRec )){
					LabBat bat = new LabBat( batRec );
					strBat = bat.getDesc();
				}
				
				String strObs = "";
				LabObsTbl obs = new LabObsTbl( lab.getLabObsRec());
				strObs = obs.getDesc();
				
				String strResult = lab.getResult();
				String strUnits = obs.getCodedUnits().getLabel();
				if (( strUnits == null ) || ( strUnits.length() < 1 )) strUnits = obs.getUnitsText();
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( listbox );
				new Listcell( lab.getDate().getPrintable(9)).setParent( i );
				
				new Listcell( strBat ).setParent( i );
				new Listcell( strObs ).setParent( i );
				new Listcell( strResult + " " + strUnits ).setParent( i );
				
				new Listcell( lab.getAbnormalFlag().getLabel()).setParent( i );
				new Listcell( lab.getResultStatus().getLabel()).setParent( i );
				
				String strCondition = lab.getSpecimenCondition().getLabel();
				if ( strCondition.length() < 1 ) strCondition = lab.getConditionText();
				new Listcell( strCondition ).setParent( i );
				
				// store PAR reca in listitem's value
				i.setValue( reca );
			}
			
			
			// get next reca in list	
			reca = lab.getLLHdr().getLast();
		}


		return;
	}
	
	
	
	
	public void onClick$btnAdd(){
		
		LabResEdit.add( ptRec, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		//refreshList();
		return;
	}
	
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
		//refreshList();
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
