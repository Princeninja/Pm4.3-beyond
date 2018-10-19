package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.LLHdr;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;

public class ProbTblEditWinController extends GenericForwardComposer {
	
	EditPt.Operation operation = null;	// operation to perform
	
	Window probTblEditWin;	// autowired - window	
	Rec probTblRec;		// vitals reca
	
	
	Textbox abbr;			// autowired
	Textbox desc;			// autowired
	Textbox snomed;			// autowired
	Textbox icd9;			// autowired
	Textbox icd10;			// autowired
	Textbox code4;			// autowired
	Textbox dgnrec;			// autowired
	Textbox txtProtocol;	// autowired
	Textbox txtResource;

	
	
		
	
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
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (EditPt.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ probTblRec = (Rec) myMap.get( "rec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "ProbTblEditWinController() operation==null" );
		
		
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){
			
			System.out.println( "new ProbTbl" );
			
			// Create a new entry
			
			
			//TODO - ?preselect default units?
	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
			System.out.println( "edit ProbTbl" );

			// Read ProbTbl entry 
			if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )) SystemHelpers.seriousError( "ProbTblEditWinController() bad rec" );
			System.out.println( "rec=" + probTblRec.getRec());

			ProbTbl p = new ProbTbl( probTblRec );
				

			System.out.println( "ProbTbl read" );
			System.out.println( "abbr=" + p.getAbbr());

			
			// Get probTbl info from data struct
			
			abbr.setValue( p.getAbbr());
			desc.setValue( p.getDesc());
			snomed.setValue( p.getSNOMED());
			icd9.setValue( p.getICD9());
			icd10.setValue( p.getICD10());
			code4.setValue( p.getCode4());
			txtResource.setValue( p.getResource());
			
			//TODO Rec dgnRec = p.getDgnRec();
			//if (( dgnRec != null ) && ( dgnRec.getRec() > 1 )){
				// get Dgn abbr
				//dgnabbr.setValue( Dgn( dgnRec ).getAbbr());
			//}
			
			//TODO Rec protocol = p.getProtocol();
			//if (( protocol != null ) && ( protocol.getRec() > 1 )){
				// get follow list protocol abbr
				//txtProtocol.setValue( Protocol( protocol ).getAbbr());
			//}
			
			
			//TODO set active/inactive
		

			
		}

		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		ProbTbl p;		// probTbl info
		
		
		// TODO - VALIDATE DATA
		
		// Load data into probTbl dataBuffer
		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if (( operation != null ) && ( operation == EditPt.Operation.NEWPT )){

			// create a new probTbl record
			System.out.println( "Creating new probTbl record..." );
			p = new ProbTbl();
			
			

			
		} else { 
			
			
			// make sure we have a valid reca
			if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )) SystemHelpers.seriousError( "ProbTblEditWinController.save() bad rec" );
	
			// Read probTbl
			p = new ProbTbl( probTblRec );			
		}
	
		
		
		
		// Load data into prob dataBuffer

		p.setAbbr( abbr.getValue());
		p.setDesc( desc.getValue());
		p.setSNOMED( snomed.getValue());
		p.setICD9( icd9.getValue());
		p.setICD10( icd10.getValue());
		p.setCode4( code4.getValue());
		p.setResource( txtResource.getValue());
		
//TODO		probTbl.setAbbr( dgn.getValue());
//TODO		probTbl.setAbbr( txtProtocol.getValue());


		
		//probTbl.setDgnRec( EditHelpers.getListboxSelection( bcbsPar ));

		p.setStatus( ProbTbl.Status.ACTIVE );
		
		
		
		// Save (write) records back to database
		if (( operation == EditPt.Operation.NEWPT ) || ( probTblRec == null ) || ( probTblRec.getRec() == 0 )){

			// new probTbl entry
			probTblRec = p.writeNew();
			System.out.println( "new probTbl entry created rec=" + probTblRec.toString());
						
			
		} else {	// EditPt.Operation.EDITPT
			
			
			// TODO - really handle edit flag
			//probTbl.setEdits( prob.getEdits() + 1 );
			
			// existing prob
			p.write();
			System.out.println( "edited probTbl info written, rec=" + p.getRec().toString());
		}
		//p.dump();
		
		return;
	}

	
	
//	public void onClose$vitalsNewWin( Event e ) throws InterruptedException{
//		alert( "onClose event");
//		onClick$cancel( e );
//		return;
//	}
	
	
	
	public void onClick$save( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			save();
			//progBox.close();
			closeWin();
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$cancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			closeWin();
		}
		
		return;
	}

	
	private void closeWin(){
		
		// close Win
		probTblEditWin.detach();
		return;
	}
	

}
