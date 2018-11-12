package palmed;

import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Validity;
import usrlib.ZkTools;

public class SoapShtSummaryWinController extends GenericForwardComposer {
	
	
	Window soapShtSummaryWin;
	Listbox lboxPtEd;			// autowired
	
	Rec ptRec;					// pt record number
	Rec provRec;				// provider record number
	
	
	
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
				try{ provRec = (Rec) myMap.get( "provRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )){
			SystemHelpers.seriousError( "invalid ptRec" );
			//ptRec = new Rec( 2 );		// default for testing
		}
		
		if ( ! Rec.isValid( provRec )) provRec = new Rec( 2 );

		
		refresh();
		return;
	}
	
	
	
	void refresh(){
		
		
		ZkTools.listboxClear( lboxPtEd );
		
		
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		// any problems that get pt ed??
		for ( Reca reca = medPt.getProbReca(); Reca.isValid( reca ); ){
			
			Prob prob = new Prob( reca );
			
			Rec pRec = prob.getProbTblRec();
			ProbTbl p = new ProbTbl( pRec );
			//System.out.println("problem is: "+ProbTbl.getResource( pRec ));
			
			if ( Rec.isValid( pRec )){
				String resource = ProbTbl.getResource( pRec );
				if ( resource.length() > 1 ){
					System.out.println("Problem desc: "+ProbTbl.getDesc(pRec));
					ZkTools.appendToListbox(lboxPtEd, "Problem: " + prob.getProbDesc(), resource );
				}
			}
			reca = prob.getLLHdr().getLast();
		}
		
		
		// any labs that get pt ed??
		// any problems that get pt ed??
		for ( Reca reca = medPt.getLabResultReca(); Reca.isValid( reca ); ){
			
			LabResult lab = new LabResult( reca );
			Rec pRec = lab.getLabObsRec();

			if ( Rec.isValid( pRec )){
				LabObsTbl obs = new LabObsTbl( pRec );			
				String resource = LabObsTbl.getResource( pRec );
				if ( resource.length() > 1 ){
					ZkTools.appendToListbox(lboxPtEd, "Lab: " + obs.getDesc(), resource );
				}
			}
			reca = lab.getLLHdr().getLast();
		}
		
		
		
		
		
		// any orders that get pt ed??
		for ( Reca reca = medPt.getOrdersReca(); Reca.isValid( reca ); ){
			
			Orders order = new Orders( reca );
			//Rec pRec = lab.getLabObsRec();

			if ( order.getDesc().indexOf( "CT" ) >= 0 ){
				ZkTools.appendToListbox(lboxPtEd, "Order: " + order.getDesc(), "ct_scan.pdf" );

			} else if ( order.getDesc().equalsIgnoreCase( "hgba1c" )){
				ZkTools.appendToListbox(lboxPtEd, "Order: " + order.getDesc(), "A1CNow.pdf" );

			}
			
			reca = order.getLLHdr().getLast();
		}
		
		
		
		
		
		
		
	}
	

	
	public void onClick$btnCCR( Event ev ){

		PtCCR.exportCCRtoPatient( ptRec );
		return;
	}

	
	
	public void onClick$btnPrintSummary( Event ev ){
		
		// build CCR output
		String ccrText = PtCCR.create( ptRec, provRec );
		Media media = new AMedia( "visit_summary.ccr", "text", "text/xml", ccrText );
		
		CCRView.show( soapShtSummaryWin, media, null );
		
		Intervention.postNew( ptRec, Date.today(), Intervention.Types.VISIT_SUMMARY, "Print" );
			
		return;
	}
	
	public void onClick$btnRefresh( Event ev ){
		refresh();
		return;
	}


	
	public void onClick$btnPrintPtEd( Event ev ){
		
		if ( lboxPtEd.getSelectedCount() < 1 ) {
			try {Messagebox.show( "No Problem is currently selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
			return;
	     }		
 
		
		String resource = (String) ZkTools.getListboxSelectionValue( lboxPtEd );
		if ( resource.length() > 0 ){
			PDFViewer.show( soapShtSummaryWin, resource );
		}
		
		
		// post intervention
		Intervention.postNew( ptRec, Date.today(), Intervention.Types.PATIENT_EDUCATION, resource );
		
		AuditLogger.recordEntry( AuditLog.Action.PT_EDUCATION_PRINT, ptRec, Pm.getUserRec(), null, resource );

		return;
	}
	

}
