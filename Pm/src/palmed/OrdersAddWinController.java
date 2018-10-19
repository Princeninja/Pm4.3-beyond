package palmed;

import java.util.EnumSet;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.Orders.Status;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Validity;
import usrlib.ZkTools;

public class OrdersAddWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window ordersAddWin;		// autowired - window	
	Reca orderReca = null;		// reca
	
	Textbox txtDate;		// autowired
	Textbox txtDesc;		// autowired
	Textbox txtNote;		// autowired
	Textbox txtIndications;	// autowired
	
	//Label	lblEtc;			// autowired
	
	Listbox medListbox;		// autowired
	
	Listbox lboxStatus;		// autowired
	Listbox lboxProvider;	// autowired
	
	Radio rbRoutine;		// autowired
	Radio rbASAP;			// autowired
	Radio rbSTAT;			// autowired
	
	//Checkbox cbNMBU;		// autowired
	
	
	
		
	
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
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
				try{ orderReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "OrdersAddWinController() operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "OrdersAddWinController() bad ptRec" );
		

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		
		
		
		// fill some selection listboxes
		Prov.fillListbox( lboxProvider, true );
		
		for ( Status r : EnumSet.allOf(Status.class))
			ZkTools.appendToListbox( lboxStatus, r.getLabel(), r );
		
		
		if ( operation == EditPt.Operation.NEWPT ){

		
		
		

			// Create a new order entry
			
			// set date to today's date
			txtDate.setValue( Date.today().getPrintable(9));
			rbRoutine.setChecked( true );
			ZkTools.setListboxSelectionByValue( lboxStatus, Orders.Status.ORDERED );


	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			if ( ! Reca.isValid( orderReca )) SystemHelpers.seriousError( "MedAddWinController() bad reca" );
			Orders order = new Orders( orderReca );
			
			txtDate.setValue( order.getDate().getPrintable(9));
			txtDesc.setValue( order.getDesc());
			txtNote.setValue( order.getNoteTxt());
			txtIndications.setValue( order.getIndicationsText());
			
			Orders.Priority priority = order.getPriority();
			rbRoutine.setChecked( priority == Orders.Priority.ROUTINE );
			rbASAP.setChecked( priority == Orders.Priority.ASAP );
			rbSTAT.setChecked( priority == Orders.Priority.STAT );
			
			ZkTools.setListboxSelectionByValue( lboxStatus, order.getStatus());
			ZkTools.setListboxSelectionByValue( lboxProvider, order.getPcnRec());
			
			
			// Get info from data struct
/*			
			txtDate.setValue( cmed.getStartDate().getPrintable(9));
			
			Rec probTblRec = cmed.getProbTblRec();
			
			if (( probTblRec == null ) || ( probTblRec.getRec() < 2 )){
				// misc description
				misc.setValue( prob.getMiscDesc());
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				lblMed.setValue( prob.getProbDesc());
				lblCode.setValue( prob.getCode());
			}
			
			
			// acute/chronic
			int type = cmed.getType();
			if ( type == 'A' ) rbAcute.setChecked( true );
			if ( type == 'C' ) rbChronic.setChecked( true );

			//TODO - history of???
*/		
			
		}

		
		// load problem Master Table
		//loadProbTblList();
		
		

		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public void save(){
		
		Orders order;		// medication info
		



		// TODO - VALIDATE DATA
		
		// verify valid start date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + txtDate.getValue() + "." );
			return;			
		}
	
		// verify valid ptrec
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			DialogHelpers.Messagebox( "Invalid ptrec:" + ptRec.getRec());
		}
		
		// verify medication selected/entered
		String s = txtDesc.getValue();
		if (( s == null ) || ( s.length() < 1 )){
			DialogHelpers.Messagebox( "Order text must be entered" );
		}
		
/*		if ( cbMisc.isChecked()){
			String s = txtMiscMed.getValue();
			if (( s == null ) || ( s.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid drug name: " + s + "." );
				return;
			}
			
		} else {
			
			String s = lblMed.getValue();
			String t = lblCode.getValue();
			
			if (( s == null ) || ( s.length() < 2 ) || ( t == null ) || ( t.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid drug name: " + s + " or code: " + t + "." );
				return;
			}
		}
*/
		
		// verify provider selected
		Rec provRec = (Rec) ZkTools.getListboxSelectionValue( lboxProvider );
		if ( provRec == null ) provRec = new Rec( 0 );
		
		

		
		
		
		
		
		
		System.out.println( "Saving..." );
		
		if ( operation == EditPt.Operation.EDITPT ){

			System.out.println( "Editing existing order record, orderReca=" + orderReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( orderReca )) SystemHelpers.seriousError( "OrderEditWinController.save() bad reca" );
		
			// Read cmed
			order = new Orders( orderReca );
			// validity/status byte should already be set
			//cmed.setStatus( 'C' );
				
		} else { 
			
			// create a new orders record
			System.out.println( "Creating new order record..." );
			order = new Orders();
			
			order.setPtRec( ptRec );

			// set validity
			order.setValid( Validity.VALID );
		}
	
		
		// Set new med info into Cmed object
		
		order.setDate( date );
		order.setDesc( txtDesc.getValue());
		order.setNoteTxt( txtNote.getValue());
		order.setIndicationsText( txtIndications.getValue());
		order.setStatus( (Orders.Status) ZkTools.getListboxSelectionValue( lboxStatus ));			

		if ( rbRoutine.isChecked()) order.setPriority( Orders.Priority.ROUTINE );
		if ( rbASAP.isChecked()) order.setPriority( Orders.Priority.ASAP );
		if ( rbSTAT.isChecked()) order.setPriority( Orders.Priority.STAT );
		
		
		
		//cmed.setRdocRec(rec);
		//cmed.setVisitReca(reca);;

		order.setPcnRec( provRec );
		 

		
		
/*
		if (( probTblRec != null ) && ( probTblRec.getRec() > 1 )){
			
			cmed.setProbTblRec( probTblRec );
			cmed.setMiscDesc( "" );

		} else {
			
			cmed.setProbTblRec( new Rec( 0 ));
			cmed.setMiscDesc( misc.getValue());
		}
*/		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// existing order
			order.write( orderReca );
			System.out.println( "edited order info written, reca=" + orderReca.toString());

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.ORDERS_EDIT, ptRec, Pm.getUserRec(), orderReca, order.getDesc());
			
		} else {			// Edit.Operation.NEWPT
			
			// write the new order rec
			order.postNew( ptRec );
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.ORDERS_ADD, ptRec, Pm.getUserRec(), orderReca, order.getDesc());
			
			// done

		}

		// Notify other windows of new orders
		Notifier.notify( ptRec, Notifier.Event.PAR );

		return;
	}

	
	
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

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
	
	
	
	public void onClick$btnCancel( Event e ) throws InterruptedException{
		
		if ( Messagebox.show( "Leave without saving ?", "Leave?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){			
			closeWin();
		}		
		return;
	}

	
	private void closeWin(){		
		// close Win
		ordersAddWin.detach();
		return;
	}
	
	
	
/*	
	// Refresh listbox when needed
	public void loadMedTblList(){ loadMedTblList( null ); }
	
	public void loadMedTblList( String searchString ){
		System.out.println( "in loadlist" );

		if ( medListbox == null ) return;
		System.out.println( "in loadlist2" );
		
		// remove all items
		for ( int i = medListbox.getItemCount(); i > 0; --i ){
			medListbox.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		ProbTbl probTbl = ProbTbl.open();
		
		while ( probTbl.getNext()){
			
			int fnd = 1;
			

			// is active ?
			if ( ProbTbl.Status.ACTIVE != probTbl.getStatus()) continue;
			
			if (( searchString != null )
				&& (( probTbl.getAbbr().toUpperCase().indexOf( s ) < 0 )
				&& ( probTbl.getDesc().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getSNOMED().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getICD9().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getICD10().toUpperCase().indexOf( s ) < 0 )
				//&& ( probTbl.getCode4().toUpperCase().indexOf( s ) < 0 )
				)){					
				// this one doesn't match
				fnd = 0;
			}
	
			if ( fnd > 0 ){
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( medListbox );
				i.setValue( probTbl.getRec());
				
				//new Listcell( probTbl.getAbbr()).setParent( i );
				new Listcell( probTbl.getDesc()).setParent( i );
				//new Listcell( probTbl.getSNOMED()).setParent( i );
				//new Listcell( probTbl.getICD9()).setParent( i );
				//new Listcell( probTbl.getICD10()).setParent( i );
				//new Listcell( probTbl.getCode4()).setParent( i );
				//new Listcell( //*Dgn.read( prob.getDgnRec()).getCode() //"V101.5" ).setParent( i );;
			}
		}
		
		probTbl.close();
		

		return;
	}
*/



/*	
	// Search for matching medications in NCFull table
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		//loadProbTblList(( s.length() < 1 ) ? null: s );
		usrlib.ZkTools.listboxClear( medListbox );		
		if (( s != null ) && ( s.length() > 1 )) palmed.NCFull.search( medListbox, s );
		return;
	}
	
	
	
	// Select a matching medication from the NCFull table
	
	public void onClick$btnSelect( Event ev ){

		// make sure an item was selected
		if ( medListbox.getSelectedCount() < 1 ) return;
		// get selected item's Rec
		String drugName = medListbox.getSelectedItem().getLabel();
		String drugID = (String) medListbox.getSelectedItem().getValue();
		if (( drugName == null ) || ( drugID == null )) { SystemHelpers.seriousError( "MedAddWinController.onClick$select() bad drug data" ); return; }
		// copy to fields
		//ProbTbl p = new ProbTbl( pRec );
		lblMed.setValue( drugName);
		lblCode.setValue( drugID );
		
		NCFull nc = NCFull.readMedID(drugID);
		if ( nc == null ){
			System.out.println( "drugID " + drugID + " not found." );
			return;
		}
		
		System.out.println( "Form=" + nc.getMedForm()+ "----" + nc.getMedFormDesc());
		System.out.println( "Translated form=" + MedForm.fromNCFull( nc.getMedForm()));
		
		ZkTools.setListboxSelectionByValue( lbForm, MedForm.fromNCFull( nc.getMedForm()));
		ZkTools.setListboxSelectionByValue( lbRoute, MedRoute.fromNCFull( nc.getMedRouteID()));
		ZkTools.setListboxSelectionByLabel( lbDosage, "1" );
		
		System.out.println( "Route=" + nc.getMedRoute()+ "----" + nc.getMedRouteDesc() + " ID=" + nc.getMedRouteID());
		lblEtc.setValue( nc.getMedEtc());
		
		//probTblRec = pRec;
		//if ( cbDosage.getValue().length() < 1 ) cbDosage.setValue( "1" );
		return;
	}
*/
	
}
