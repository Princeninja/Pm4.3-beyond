package palmed;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class SoapNoteWinController extends GenericForwardComposer {
	
	private Listbox soapsListbox;		// autowired
	private Textbox soapTextbox;		// autowired

	Window soapWin;						// this window
	Button btnEdit;						// autowired
	Button btnNew;						// autowired
	Button btnDelete;					// autowired
	Button btnPrint;					// autowired

	
	
	private Rec	ptRec;		// patient record number

	
	

	public SoapNoteWinController() {
		// TODO Auto-generated constructor stub
	}

	public SoapNoteWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public SoapNoteWinController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
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

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )){ DialogHelpers.Messagebox( "bad ptrec" ); return; }
		
		// populate SOAP note listbox
		refreshList();

		return;
	}
	
	
	
	
	void refreshList(){
		
		Reca soapReca = null;
		
		// is item currently selected?  Save it.
		if ( soapsListbox.getSelectedCount() > 0 ){
			soapReca = (Reca) ZkTools.getListboxSelectionValue( soapsListbox );
		}
		
		// empty list
		ZkTools.listboxClear( soapsListbox );
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );

		for ( Reca reca = ( new MedPt( dirPt.getMedRec())).getSoapReca(); reca.getRec() != 0; ){
			
			SoapNote soap = new SoapNote( reca );
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( soapsListbox );
			new Listcell( soap.getDate().getPrintable(9)).setParent( i );
			new Listcell( soap.getDesc()).setParent( i );
			new Listcell( Prov.getAbbr( soap.getProvRec())).setParent( i );
			new Listcell(( soap.getNumEdits() == 0 ) ? "": "E" );
			new Listcell(( soap.getSignatureReca().getRec() < 1 ) ? "": "S" );
			i.setValue( reca );		// set reca into listitem for later retrieval

			
			// get next reca in list	
			reca = soap.getLLHdr().getLast();
		}

		// re-select the item
		if ( Reca.isValid( soapReca )){
			if ( ZkTools.setListboxSelectionByValue( soapsListbox, soapReca ))
				displaySelected();
		}
		
		
		return;
	}
	
	
	public void onSelect$soapsListbox( Event e ){
		displaySelected();
		return;
	}
	
	public void displaySelected(){

		SoapNote soap = new SoapNote( (Reca) ZkTools.getListboxSelectionValue( soapsListbox ));
		
		String sub = soap.getSubjText();
		String obj = soap.getObjText();
		String ass = soap.getAssText();
		String plan = soap.getPlanText();
		
		soapTextbox.setValue( SoapNote.soapDot[0] + sub + "\r\n" + SoapNote.soapDot[1] + obj + "\r\n" + 
				SoapNote.soapDot[2] + ass + "\r\n" + SoapNote.soapDot[3] + plan );

		return;
	}

	
	
	public void onClick$btnEdit( Event e ){
		
		Reca soapReca = (Reca) ZkTools.getListboxSelectionValue(soapsListbox);
		if ( ! Reca.isValid( soapReca )){ DialogHelpers.Messagebox( "No SOAP Note selected." ); return; }
		SoapEdit.edit( ptRec, soapReca, soapWin );
		refreshList();
	}

	public void onClick$btnNew( Event e ){
		
		SoapEdit.add( ptRec, soapWin );
		refreshList();
	}

	public void onClick$btnDelete( Event e ){
		
		Reca soapReca = (Reca) ZkTools.getListboxSelectionValue(soapsListbox);
		if ( ! Reca.isValid( soapReca )){ DialogHelpers.Messagebox( "No SOAP Note selected." ); return; }
		
		DialogHelpers.Optionbox( "Delete SOAP Note", "Are you sure you wish to delete this SOAP Note?", "Yes", "No", null );
		
		// delete SOAP Note
		SoapNote soapnt = new SoapNote( soapReca );
		Date date = soapnt.getDate();
		SoapNote.delete( soapReca );
		
		// delete MR Log entry corresponding
		MrLog.removeEntry( ptRec, date, MrLog.Types.SOAP_NOTE, soapReca);
		
		refreshList();
	}

}
