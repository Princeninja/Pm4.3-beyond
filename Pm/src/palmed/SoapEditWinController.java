package palmed;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listbox;



import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.ProgressBox;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;
import usrlib.ZkTools;

public class SoapEditWinController extends GenericForwardComposer {

	Rec ptRec = null;		// patient record
	Reca soapReca = null;	// soap record
	
	SoapEdit.Operation operation = null;	// operation to perform
	
	Tabpanel tabpanel;		// tab panel this window is in
	//to make a bak

	Window soapEditWin;		// autowired - window
	
	Button btnSubj;// autowired
	Button btnObj;// autowired
	Button btnAssess;// autowired
	Button btnPlan;// autowired

	Button btnApply; // autowired
	
	Button btnSave;			// autowired - save button
	Button btnCancel;		// autowired - cancel button
	
	Label l_ptName;			// autowired

	
	
	boolean flgChanged = false;
	
	Textbox txtDate;				// autowired
	Listbox lbProvider;				// autowired
	Listbox stdDictListbox;		 	// autowired
	
	Textbox txtDesc;				// autowired
	Textbox txtSubj;				// autowired
	Textbox txtObj;					// autowired
	Textbox txtAss;					// autowired
	Textbox txtPlan;				// autowired

	Textbox txtTemp;				// autowired
	Textbox txtHR;					// autowired
	Textbox txtResp;				// autowired
	Textbox txtSystolic;			// autowired
	Textbox txtDiastolic;			// autowired
	Textbox txtHeight;				// autowired
	Textbox txtWeight;				// autowired
	Textbox txtPO2;					// autowired
	Textbox txtHead;				// autowired

	
	

	
	
	

	
	
	
	public void doAfterCompose( Component component ){
	
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
		
		
		
		// Is window in a Tabpanel???
		// Get Tabpanel this window is placed in.
		// May have to go several layers deep due to enclosing window.
		if ( component instanceof Tabpanel ){
			tabpanel = (Tabpanel) component;
		} else if ( component.getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent();
		} else if ( component.getParent().getParent() instanceof Tabpanel ){
			tabpanel = (Tabpanel) component.getParent().getParent();
		}


		
		// Add close tabpanel event listener
		if ( tabpanel != null ){
			tabpanel.getLinkedTab().addEventListener( "onClose", new EventListener(){ 
				public void onEvent( Event ev ) throws Exception { ev.stopPropagation(); onClick$btnCancel( ev ); }});
		}
		
		
		
		// Get arguments from map
		Execution exec = Executions.getCurrent();
	
		if ( exec != null ){
			Map<String,Object> myMap = exec.getArg();
			if ( myMap != null ){
				try{ operation = (SoapEdit.Operation) myMap.get( "operation" ); } catch ( Exception e ) { /* ignore */ };
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ soapReca = (Reca) myMap.get( "soapReca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );
		if (( operation == SoapEdit.Operation.EDIT ) && ( ! Reca.isValid( soapReca ))) SystemHelpers.seriousError( "bad soapReca" );
		
		
		// Fill some listboxes
		Prov.fillListbox( lbProvider, true );

		// Fill standard dictations
		StdDictSoap.fillListbox(stdDictListbox, StdDictSoap.DisplayRecords.Active,false);

		// set some defaults
		txtDate.setValue( Date.today().getPrintable());
		
		
		
		refresh();
		
		return;
	}
		

		
	public void refresh(){
	
	
		if (( operation != SoapEdit.Operation.EDIT ) || ( ! Reca.isValid( soapReca ))) return;
		
		if ( Reca.isValid( soapReca )){
			
			SoapNote soapnt = new SoapNote( soapReca );

			System.out.println("The Reca is : "+soapReca);
			
			txtDate.setValue( soapnt.getDate().getPrintable(9));
			txtDesc.setValue( soapnt.getDesc());
			
			ZkTools.setListboxSelectionByValue( lbProvider, soapnt.getProvRec());
			
			txtSubj.setValue( soapnt.getSubjText());
			txtObj.setValue( soapnt.getObjText());
			txtAss.setValue( soapnt.getAssText());
			txtPlan.setValue( soapnt.getPlanText());
	
			//TODO vitals
			
		}

		
	
		
		
		btnSave.setDisabled( true );
		return;
	}
	
	
	
	
	

	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec=" );
		
		usrlib.Date date = new Date().fromString( txtDate.getValue());
		if ( ! date.isValid()){ DialogHelpers.Messagebox( "You must enter a valid date." ); return; }
		
		Rec provRec = (Rec) ZkTools.getListboxSelectionValue( lbProvider );
		if ( ! Rec.isValid( provRec )){ DialogHelpers.Messagebox( "You must select a provider." ); return; }

		String desc = txtDesc.getValue();
		String subj = txtSubj.getValue();
		String obj = txtObj.getValue();
		String ass = txtAss.getValue();
		String plan = txtPlan.getValue();
		
		
		
		// get vitals info
		boolean flgVitals = false;
		
		double temp = EditHelpers.parseDouble( txtTemp.getValue());
		if ( temp > 0 ) temp = UnitHelpers.getCelcius( temp );
		
		int hr = EditHelpers.parseInt( txtHR.getValue());
		int resp = EditHelpers.parseInt( txtResp.getValue());
		int sbp = EditHelpers.parseInt( txtSystolic.getValue());
		int dbp = EditHelpers.parseInt( txtDiastolic.getValue());
		int pO2 = EditHelpers.parseInt( txtPO2.getValue());
		
		double ht = EditHelpers.parseDouble( txtHeight.getValue());
		if ( ht > 0 ) ht = UnitHelpers.getCentimeters( ht );
		
		double wt = EditHelpers.parseDouble( txtWeight.getValue());
		if ( wt > 0 ) wt = UnitHelpers.getGramsFromPounds( wt );
		
		double hc = EditHelpers.parseDouble( txtHead.getValue());
		if ( hc > 0 ) hc = UnitHelpers.getCentimeters( hc );

		if (( temp > 0 ) || ( hr > 0 ) || ( resp > 0 ) || ( sbp > 0 ) || ( dbp > 0 ) || ( pO2 > 0 )
				|| ( ht > 0 ) || ( wt > 0 ) || ( hc > 0 )){
				flgVitals = true;
		}
		
		SoapNote soapnt = null;
		
		if ( operation == SoapEdit.Operation.EDIT ){			
			soapnt = new SoapNote( soapReca );
			soapnt.setNumEdits( soapnt.getNumEdits() + 1 );
		} else {			
			soapnt = new SoapNote();
			soapnt.setPtRec( ptRec );
			soapnt.setStatus( usrlib.RecordStatus.CURRENT );
		}
		
		soapnt.setProvRec( provRec );
		soapnt.setDate( date );
		soapnt.setDesc( desc );
		
		soapnt.setText( subj + '\n' + obj + '\n' + ass + '\n' + plan + '\n' );
		
		if ( operation == SoapEdit.Operation.EDIT ){
			// write edited SOAP note
			soapnt.write( soapReca );
			//TODO - modify MrLog if needed
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SOAP_EDIT, ptRec, Pm.getUserRec(), null, null );
			
		} else {
			// post new SOAP note
			System.out.println("soapreca before new save: "+soapReca);
			soapReca = soapnt.postNew( ptRec );
			// post to MR Log
			MrLog.postNew( ptRec, date, desc, MrLog.Types.SOAP_NOTE, soapReca );
			
			// post vitals
			if ( flgVitals ) Vitals.postNew( ptRec, date, temp, hr, resp, sbp, dbp, pO2, ht, (int) wt, hc );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SOAP_ADD, ptRec, Pm.getUserRec(), null, null );
		}
		

		
		btnSave.setDisabled( true );		
		return;
	}

	
	public void onClick$btnApply(Event e) throws InterruptedException {

		fillTextBox(txtSubj, 1);
		fillTextBox(txtObj, 2);
		fillTextBox(txtAss, 3);
		fillTextBox(txtPlan, 4);

		btnSave.setDisabled(false);
		btnSave.setFocus(true);

	}

	private void fillTextBox(Textbox txtBox, int num) {

		if (stdDictListbox.getSelectedCount() < 1) {
			try { Messagebox.show("No item selected."); } catch (Exception ex) { /* ignore */ }
			return;
		}



		String currentText = txtBox.getValue();
		Set<Listitem> selections = stdDictListbox.getSelectedItems();
		
		for ( Listitem li : selections ){
			
    		Rec rec = (Rec) li.getValue();
    		if ((rec == null) || (rec.getRec() < 2)) {
    			continue;
    		}
    		StdDictSoap stdDict = new StdDictSoap(rec);
    		String textToAdd = stdDict.getText(num).trim();
    		
    		currentText += textToAdd;currentText += "  ";  //"\n";
        }

		// get selected item's reca
//		Rec rec = (Rec) stdDictListbox.getSelectedItem().getValue();
//
//
//		if ((rec == null) || (rec.getRec() < 2)) {
//			return;
//		}
//		StdDictSoap stdDict = new StdDictSoap(rec);
//		String textToAdd = stdDict.getText(num).trim();
//		String currentText = txtBox.getValue();
//		currentText += textToAdd;

		txtBox.setValue(currentText);
	}
	

	
	
	public void onClose$soapEditWin( Event e ) throws InterruptedException{
		alert( "onCloset event");
		onClick$btnCancel( e );
		return;
	}
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			ProgressBox progBox = ProgressBox.show( soapEditWin, "Saving..." );

			save();
			progBox.close();
			closeWin();
			
		} else {

			if ( Messagebox.show( "Continue editing?", "Continue?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.NO ){
				closeWin();
			}
		}

		return;
	}
	
	
	
	public void onClick$btnCancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			closeWin();
		}
		
		return;
	}

	
	private void closeWin(){
		
		// close editPtWin
		soapEditWin.detach();

		// close tab and tabpanel
		//if ( tabpanel != null ){
		//	tabpanel.getLinkedTab().detach();
		//	tabpanel.detach();
		//}
		//refresh();
		return;
	}
	
	
	
	
	
	
	
	public void onChange$soapEditWin( Event ev ){
		flgChanged = true;
		btnSave.setDisabled( false );	
	}

	
	
	
	
//	public void onCheck$cbObHxUnavailable$pgOb( Event ev ){ doCheckObHxUnavailable(); }
//	private void doCheckObHxUnavailable(){
//
//		boolean status = cbObHxUnavailable.isChecked();
//		rowGravida.setVisible( ! status );
//		rowPara.setVisible( ! status );
//		rowAborta.setVisible( ! status );
//		rowComplications.setVisible( ! status );
//		return;
//	}
	
	

}

