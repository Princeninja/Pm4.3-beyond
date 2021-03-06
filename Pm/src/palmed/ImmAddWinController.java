package palmed;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.VaccineCVX.Entry;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;
import usrlib.ZkTools;





public class ImmAddWinController extends GenericForwardComposer {

	Rec ptRec = null;			// patient record
	EditPt.Operation operation = null;	// operation to perform
	
	Label ptname;			// autowired
	
	
	Window immAddWin;		// autowired - window	
	Reca immReca = null;	// par reca
	
	Textbox txtDate;		// autowired
	Textbox txtTime;		// autowired
	Textbox txtMisc;		// autowired
	Textbox txtNote;		// autowired
	
	Label	lblVaccine;		// autowired
	Label	lblCVX;			// autowired
	Label	lblMVX;			// autowired
	Label	lblInfo;		// autowired
	Label	lblCVXinfo;		// autowired
	Label	lblMVXinfo;		// autowired
	
	Textbox	txtLotNumber;	// autowired
	Textbox txtExpDate;		// autowired
	Textbox txtAmount;		// autowired
	Textbox txtSite;		// autowired
	
	Listbox lboxUnits;		// autowired
	Listbox lboxSite;		// autowired
	Listbox lboxRoute;		// autowired
	Listbox lboxProvider;	// autowired
	Listbox lboxSource;		// autowired
	Textbox txtSearch;		// autowired
	
	Tab tabShortcut;		// autowired
	Tab tabCVX;				// autowired
	Tab tabMVX;				// autowired
	Tabpanel tpShortcut;	// autowired
	Tabpanel tpCVX;			// autowired
	Tabpanel tpMVX;			// autowired
	
	Listbox lboxShortcut;	// autowired
	Listbox lboxCVX;		// autowired
	Listbox lboxMVX;		// autowired
	
	Listheader lhdrShortcut;// autowired
	Listheader lhdrCVX;		// autowired
	Listheader lhdrVac;		// autowired
	Listheader lhdrMVX;		// autowired
	Listheader lhdrMfr;		// autowired
	
	Checkbox cbMisc;		// autowired
	
	Button btnSearch;		// autowired
	Button btnSelect;		// autowired
	Row rowMed1;			// autowired
	Row rowMed2;			// autowired

	
	
		
	
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
				try{ immReca = (Reca) myMap.get( "reca" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		// Debugging
		//ptRec = new Rec( 2 );
		//operation = EditPt.Operation.NEWPT;

		
		// make sure we have an operation
		if ( operation == null ) SystemHelpers.seriousError( "ImmAddWinController() operation==null" );
		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ImmAddWinController() bad ptRec" );

		// if editing, make sure we have a valid parReca
		if (( operation == EditPt.Operation.EDITPT ) && ( ! Reca.isValid( immReca ))) SystemHelpers.seriousError( "ImmAddWinController() bad immReca" );

		
		// Set ptname in window
//		ptname.setValue( new DirPt( ptRec ).getName().getPrintableNameLFM());
		

		
		
		// fill some selection listboxes
		Prov.fillListbox( lboxProvider, true );
		
		for ( Immunizations.Units r : EnumSet.allOf(Immunizations.Units.class))
			ZkTools.appendToListbox( lboxUnits, r.getLabel(), r );
		
		for ( Immunizations.Site r : EnumSet.allOf(Immunizations.Site.class))
			ZkTools.appendToListbox( lboxSite, r.getLabel(), r );
		
		for ( Immunizations.Route r : EnumSet.allOf(Immunizations.Route.class))
			ZkTools.appendToListbox( lboxRoute, r.getLabel(), r );

		for ( Immunizations.Source r : EnumSet.allOf(Immunizations.Source.class))
			ZkTools.appendToListbox( lboxSource, r.getLabel(), r );

		
		
		// some listbox sort functions
		lhdrCVX.setSortAscending( new MyCVXComparator( true, 0 ));
		lhdrCVX.setSortDescending( new MyCVXComparator( false, 0 ));
		lhdrVac.setSortAscending( new MyCVXComparator( true, 1 ));
		lhdrVac.setSortDescending( new MyCVXComparator( false,1 ));
		lhdrMVX.setSortAscending( new MyMVXComparator( true, 0 ));
		lhdrMVX.setSortDescending( new MyMVXComparator( false, 0 ));
		lhdrMfr.setSortAscending( new MyMVXComparator( true, 1 ));
		lhdrMfr.setSortDescending( new MyMVXComparator( false, 1 ));

		
		
		// refresh the search listboxes
		refreshCVXList( null );
		refreshMVXList( null );
		refreshShortcuts( null );
		
		
		// Create a new Immunization entry
		if ( operation == EditPt.Operation.NEWPT ){

			// set date to today's date and time
			txtDate.setValue( Date.today().getPrintable(9));
			txtTime.setValue( Time.now().getPrintable());
			

	

			
		} else {	// operation == EditPt.Operation.EDITPT
		
				
			// Read info 
			Immunizations imm = new Immunizations( immReca );
				
			if ( imm.getFlgMisc()){
				// misc description
				cbMisc.setChecked( true );
				txtMisc.setValue( imm.getDesc());
				onCheck$cbMisc( null );
				
			} else {
				//cmed.getDesc
				//TODO - set problem listbox selection?????
				cbMisc.setChecked( false );
				lblVaccine.setValue( imm.getDesc());
				onCheck$cbMisc( null );
			}
			
			//TODO lblInfo
						
			lblCVX.setValue( imm.getCVX());
			lblMVX.setValue( imm.getMVX());

			// Get info from data struct			
			txtDate.setValue( imm.getDate().getPrintable(9));
			txtTime.setValue( imm.getTime().getPrintable());
			
			txtLotNumber.setValue( imm.getLotNumber());
			txtExpDate.setValue( imm.getExpDate().isValid() ? imm.getExpDate().getPrintable(9): "" );
			txtAmount.setValue( imm.getAmount());
			
			ZkTools.setListboxSelectionByValue( lboxUnits, imm.getUnits());
			ZkTools.setListboxSelectionByValue( lboxSite, imm.getSite());
			ZkTools.setListboxSelectionByValue( lboxRoute, imm.getRoute());
			ZkTools.setListboxSelectionByValue( lboxSource, imm.getInfoSource());

			ZkTools.setListboxSelectionByValue( lboxProvider, imm.getProvRec());

			txtSite.setValue( imm.getSiteTxt());
			txtNote.setValue( imm.getNoteTxt());
		}


		return;
	}
	
	
	
	
	
	
	/**
	 * Save
	 * 
	 */
	
	
	public boolean save(){
		
		Immunizations imm;		// immunization info
		



		// TODO - VALIDATE DATA
		
		// verify valid date
		Date date = new Date( txtDate.getValue());
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid vaccine date: " + txtDate.getValue() + "." );
			return false;			
		}
	
		// verify valid time
		Time time = Time.fromString( txtTime.getValue());
		if (( time == null ) || ( ! time.isValid())){
			DialogHelpers.Messagebox( "Invalid vaccine time: " + txtTime.getValue() + "." );
			return false;			
		}
	
		// verify valid ptrec
		if ( ! Rec.isValid( ptRec )){
			DialogHelpers.Messagebox( "Invalid ptrec:" + ptRec.getRec());
			return false;
		}
		
		// verify medication selected/entered
		if ( cbMisc.isChecked()){
			String s = txtMisc.getValue();
			if (( s == null ) || ( s.length() < 2 )){
				DialogHelpers.Messagebox( "Invalid vaccine name: " + s + "." );
				return false;
			}
			
		} else {
			
			String vac = lblVaccine.getValue();
			String cvx = lblCVX.getValue();
			String mvx = lblMVX.getValue();
			
			if (( vac == null ) || ( vac.length() < 2 ) || ( cvx == null ) || ( cvx.length() < 1 ) || ( mvx == null ) || ( mvx.length() < 1 )){
				DialogHelpers.Messagebox( "Invalid vaccine name: " + vac + " or CVX: " + cvx + " or MVX: " + mvx + "." );
				return false;
			}
		}
		
		
		
		
		
		
		
		System.out.println( "Saving..." );

		// Save edited information
		if (( operation != null ) && ( operation == EditPt.Operation.EDITPT )){

			System.out.println( "Editing existing Immunizations record, immReca=" + immReca.toString() + "." );
				
			// make sure we have a valid reca
			if ( ! Reca.isValid( immReca )) SystemHelpers.seriousError( "ImmAddWinController.save() bad reca" );
		
			// Read immunization
			imm = new Immunizations( immReca );

			
			
		// Credate new immunization
		} else { 
			
			// create a new par record
			System.out.println( "Creating new Immunizations record..." );
			imm = new Immunizations();
			imm.setStatus( Immunizations.Status.CURRENT );			
			//imm.setValid( usrlib.Validity.VALID );
			imm.setPtRec( ptRec );
		}
	
		
		// Set new info into Imm object
		
		imm.setDate( date );
		imm.setTime( time );

		if ( cbMisc.isChecked()){
			imm.setDesc( txtMisc.getValue());
			imm.setFlgMisc( true );
		} else {
			imm.setDesc( lblVaccine.getValue());
			imm.setCVX( lblCVX.getValue());
			imm.setMVX( lblMVX.getValue());
			imm.setFlgMisc( false );
		}
		

		imm.setLotNumber( txtLotNumber.getValue());
		imm.setExpDate( new Date( txtExpDate.getValue()));
		imm.setAmount( txtAmount.getValue());
		
		imm.setUnits( (Immunizations.Units) ZkTools.getListboxSelectionValue( lboxUnits ));
		imm.setSite( (Immunizations.Site) ZkTools.getListboxSelectionValue( lboxSite ));
		imm.setRoute( (Immunizations.Route) ZkTools.getListboxSelectionValue( lboxRoute ));
		imm.setInfoSource( (Immunizations.Source) ZkTools.getListboxSelectionValue( lboxSource ));
		imm.setProvRec( (Rec) ZkTools.getListboxSelectionValue( lboxProvider ));

		imm.setSiteTxt( txtSite.getValue());
		imm.setNoteTxt( txtNote.getValue());

		
		
		

		
		// Save (write) records back to database
		if ( operation == EditPt.Operation.EDITPT ){
			
			// TODO - really handle edit flag
			//par.setEdits( prob.getEdits() + 1 );
			
			// existing Immunizations
			imm.write( immReca );
			System.out.println( "edited immunization info written, reca=" + immReca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_EDIT, ptRec, Pm.getUserRec(), immReca, null );
			

		} else {			// Edit.Operation.NEWPT
			
			// new par
			// write the new par rec
			Reca reca = imm.postNew( ptRec );
			System.out.println( "new immunization info written, reca=" + reca.toString());
			
			// log the access
			AuditLogger.recordEntry( AuditLog.Action.IMMUNIZATIONS_ADD, ptRec, Pm.getUserRec(), immReca, null );
			
			// done

		}
		
		//par.dump();		
		return true;
	}

	
	
	
	
	
	public void onClick$btnSave( Event e ) throws InterruptedException{

		//TODO - defaults for yes/no
		
		if ( Messagebox.show( "Save this information?", "Save?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) == Messagebox.YES ){
			
			//ProgressBox progBox = ProgressBox.show(editProvWin, "Saving..." );

			if ( save()){
				closeWin();
			}
			//progBox.close();
			
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
		immAddWin.detach();
		return;
	}
	
	
	public void onCheck$cbMisc( Event ev ){
		
		boolean flg = cbMisc.isChecked();
		
		rowMed1.setVisible( ! flg );
		rowMed2.setVisible( ! flg );
		tabShortcut.setDisabled( flg );
		tabCVX.setDisabled( flg );
		tabMVX.setDisabled( flg );
		btnSelect.setDisabled( flg );
		btnSearch.setDisabled( flg );
		lboxShortcut.setDisabled( flg );
		lboxCVX.setDisabled( flg );
		lboxMVX.setDisabled( flg );
		txtMisc.setDisabled( ! flg );
		txtSearch.setDisabled( flg );		
		return;
	}

	
	
	
	
	
	/*******************************************************************************************************
	 * 
	 * CVX Listbox handling
	 */
	
	
	public void refreshCVXList( String searchString ){

		// which kind of list to display (from radio buttons)
		VaccineCVX.Status status = VaccineCVX.Status.ACTIVE;
		
		if ( lboxCVX == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxCVX );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		List<VaccineCVX.Entry> list = VaccineCVX.Search( null, null, s, status );
		if (( list == null ) || ( list.size() < 1 )) return;
		
		MyCVXModel lm = new MyCVXModel( list, true );
		lboxCVX.setModel( lm );
		
		lboxCVX.setItemRenderer( new CustomCVXRenderer());

		return;
	}

    public class MyCVXModel extends ListModelList {
        public MyCVXModel(List list, boolean flg) {
            super(list, flg);  // was true
        }
        public void sort(Comparator cmpr, boolean ascending) {
            Collections.sort(getInnerList() , cmpr);
            fireEvent(org.zkoss.zul.event.ListDataEvent.CONTENTS_CHANGED, -1, -1);
        }
    }

	
	private class CustomCVXRenderer implements ListitemRenderer {
		
		public void render( Listitem item, Object data ) throws Exception {
			
			VaccineCVX.Entry v = (VaccineCVX.Entry) data;
			item.appendChild( new Listcell( v.getCVX()) );
			item.appendChild( new Listcell( v.getShortDesc()) );
			
			String s = "Desc: " + v.getFullDesc();			
			String note = v.getNote();
			if (( note != null ) && ( note.length() > 0 )){
				s += ";\n\nNote: " + note;
			}			
			item.setTooltiptext( s );
			return;			
		}

// ?? FROM GENE
//		
//		@Override
//		public void render(Listitem arg0, Object arg1, int arg2)
//				throws Exception {
//			render(arg0, arg1);
//
//		}

	}

	
	/** Comparators for Entry */
    public class MyCVXComparator implements Comparator {
        private boolean ascending;
        private int columnIndex;
        public MyCVXComparator (boolean ascending, int columnIndex) {
            this.ascending = ascending;
            this.columnIndex = columnIndex;
        }
        public int compare( Object o1, Object o2 ){
            VaccineCVX.Entry e1 = (VaccineCVX.Entry) o1;
            VaccineCVX.Entry e2 = (VaccineCVX.Entry) o2;
            
            int v = 0;
            switch (columnIndex) {
                case 0:		// sort CVX numerically
                    v = EditHelpers.parseInt( e1.getCVX()) - EditHelpers.parseInt( e2.getCVX());
                    break;
                case 1:
                    v = e1.getShortDesc().toUpperCase().compareTo(e2.getShortDesc().toUpperCase());
                    break;
            }
            return ascending ? v: -v;
        }
    }

	
	

    
	/*******************************************************************************************************
	 * 
	 * MVX Listbox handling
	 */
	
	
	public void refreshMVXList( String searchString ){

		// which kind of list to display (from radio buttons)
		VaccineMVX.Status status = VaccineMVX.Status.ACTIVE;
		
		if ( lboxMVX == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxMVX );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		List<VaccineMVX.Entry> list = VaccineMVX.Search( null, null, s, status );
		if (( list == null ) || ( list.size() < 1 )) return;
		
		MyMVXModel lm = new MyMVXModel( list, true );
		lboxMVX.setModel( lm );
		
		lboxMVX.setItemRenderer( new CustomMVXRenderer());

		return;
	}

    public class MyMVXModel extends ListModelList {
        public MyMVXModel(List list, boolean flg) {
            super(list, flg);  // was true
        }
        public void sort(Comparator cmpr, boolean ascending) {
            Collections.sort(getInnerList() , cmpr);
            fireEvent(org.zkoss.zul.event.ListDataEvent.CONTENTS_CHANGED, -1, -1);
        }
    }

	
	private class CustomMVXRenderer implements ListitemRenderer {
		
		public void render( Listitem item, Object data ) throws Exception {
			
			VaccineMVX.Entry v = (VaccineMVX.Entry) data;
			item.appendChild( new Listcell( v.getMVX()) );
			item.appendChild( new Listcell( v.getMfrName()) );
			
			String note = v.getNote();
			if (( note != null ) && ( note.length() > 0 )){
				item.setTooltiptext( "Note: " + note );
			}			
			return;			
		}

// ?? FROM GENE		
//
//		@Override
//		public void render(Listitem item, Object data, int arg2)
//				throws Exception {
//			render(item, data);
//
//		}

	}

	
	/** Comparators for Entry */
    public class MyMVXComparator implements Comparator {
        private boolean ascending;
        private int columnIndex;
        public MyMVXComparator (boolean ascending, int columnIndex) {
            this.ascending = ascending;
            this.columnIndex = columnIndex;
        }
        public int compare( Object o1, Object o2 ){
            VaccineMVX.Entry e1 = (VaccineMVX.Entry) o1;
            VaccineMVX.Entry e2 = (VaccineMVX.Entry) o2;
            
            int v = 0;
            switch (columnIndex) {
                case 0:		// sort CVX numerically
                    v = EditHelpers.parseInt( e1.getMVX()) - EditHelpers.parseInt( e2.getMVX());
                    break;
                case 1:
                    v = e1.getMfrName().toUpperCase().compareTo(e2.getMfrName().toUpperCase());
                    break;
            }
            return ascending ? v: -v;
        }
    }

	
	public void refreshShortcuts( String s ){
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
				(i = new Listitem()).setImment( medListbox );
				i.setValue( probTbl.getRec());
				
				//new Listcell( probTbl.getAbbr()).setImment( i );
				new Listcell( probTbl.getDesc()).setImment( i );
				//new Listcell( probTbl.getSNOMED()).setImment( i );
				//new Listcell( probTbl.getICD9()).setImment( i );
				//new Listcell( probTbl.getICD10()).setImment( i );
				//new Listcell( probTbl.getCode4()).setImment( i );
				//new Listcell( //*Dgn.read( prob.getDgnRec()).getCode() //"V101.5" ).setImment( i );;
			}
		}
		
		probTbl.close();
		

		return;
	}
*/



	
	// Search for matching entries in corresponding listbox
	
	public void onClick$btnSearch( Event ev ){

		String s = txtSearch.getValue().trim();
		if ( s.length() == 0 ) s = null;
		
		if ( tabShortcut.isSelected()){

			ZkTools.listboxClear( lboxShortcut );		
			refreshShortcuts( s );
			
		} else if ( tabCVX.isSelected()){
			
			ZkTools.listboxClear( lboxCVX );		
			refreshCVXList( s );
			
		} else if ( tabMVX.isSelected()){
			
			ZkTools.listboxClear( lboxMVX );		
			refreshMVXList( s );			
		}
		
		return;
	}
	
	
	
	// Select a an entry from the search tables	
	public void onClick$btnSelect( Event ev ){

		if ( tabShortcut.isSelected()){

			// make sure an item was selected
			if ( lboxShortcut.getSelectedCount() < 1 ) return;
			//VaccineCVX.Entry e = (VaccineCVX.Entry) ZkTools.getListboxSelectionValue( lboxShortcut );
			// populate multiple fields
			
		} else if ( tabCVX.isSelected()){
			
			// make sure an item was selected
			if ( lboxCVX.getSelectedCount() < 1 ) return;
			Object[] list = ((ListModelList) lboxCVX.getListModel()).getSelection().toArray();
			if ( list.length != 1 ) SystemHelpers.seriousError( "list.length != 1");
			VaccineCVX.Entry e = (VaccineCVX.Entry) list[0];

			// this didn't work due to ListModel stuff?
			//VaccineCVX.Entry e = (VaccineCVX.Entry) ZkTools.getListboxSelectionValue( lboxCVX );

			lblCVX.setValue( e.getCVX());
			lblVaccine.setValue( e.getShortDesc());
			lblCVXinfo.setValue( e.getFullDesc());
			
		} else if ( tabMVX.isSelected()){
			
			// make sure an item was selected
			if ( lboxMVX.getSelectedCount() < 1 ) return;
			Object[] list = ((ListModelList) lboxMVX.getListModel()).getSelection().toArray();
			if ( list.length != 1 ) SystemHelpers.seriousError( "list.length != 1");
			VaccineMVX.Entry e = (VaccineMVX.Entry) list[0];

			//VaccineMVX.Entry e = (VaccineMVX.Entry) ZkTools.getListboxSelectionValue( lboxMVX );
			
			lblMVX.setValue( e.getMVX());
			lblMVXinfo.setValue( e.getMfrName());
			//lblVaccine.setValue( e.getMfrName());
		}

		return;
	}


}
