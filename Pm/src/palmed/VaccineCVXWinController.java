package palmed;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.ZkTools;

public class VaccineCVXWinController extends GenericForwardComposer {

	private Window cvxWin;				// autowired - (this window)
	private Listbox lboxVaccineCVX;		// autowired
	private Radio r_active;				// autowired
	private Radio r_all;				// autowired
	private Textbox srcstr;				// autowired
	private Listheader lhdrAbbr;		// autowired
	private Listheader lhdrCVX;			// autowired
	private Listheader lhdrStatus;		// autowired
	private Listheader lhdrShortDesc;	// autowired

	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
		//Execution exec = Executions.getCurrent();

		//if ( exec != null ){
		//	Map myMap = exec.getArg();
		//	if ( myMap != null ){
		//		try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
		//	}
		//}		
		
		
		lhdrAbbr.setSortAscending( new MyComparator( true, 0 ));
		lhdrAbbr.setSortDescending( new MyComparator( false, 0 ));
		lhdrCVX.setSortAscending( new MyComparator( true, 1 ));
		lhdrCVX.setSortDescending( new MyComparator( false, 1 ));
		lhdrStatus.setSortAscending( new MyComparator( true, 2 ));
		lhdrStatus.setSortDescending( new MyComparator( false, 2 ));
		lhdrShortDesc.setSortAscending( new MyComparator( true, 3 ));
		lhdrShortDesc.setSortDescending( new MyComparator( false, 3 ));

		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$r_active( Event ev ){
		refreshList();
	}
	public void onCheck$r_all( Event ev ){
		refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){ refreshList( null ); }
	
	public void refreshList( String searchString ){

		// which kind of list to display (from radio buttons)
		VaccineCVX.Status status = null;
		if ( r_active.isSelected()) status = VaccineCVX.Status.ACTIVE;

		
		if ( lboxVaccineCVX == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxVaccineCVX );
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		List<VaccineCVX.Entry> list = VaccineCVX.Search( null, null, s, status );
		if (( list == null ) || ( list.size() < 1 )) return;
		
		MyModel lm = new MyModel( list, true );
		lboxVaccineCVX.setModel( lm );
		
		lboxVaccineCVX.setItemRenderer( new CustomRenderer());
		
		return;
	}

	
	
	// Search
	
	public void onClick$btnSearch( Event ev ){

		String s = srcstr.getValue().trim();
		refreshList(( s.length() < 1 ) ? null: s );
		return;
	}
	
	
	
	// Open dialog to enter new vitals data
	
/*	public void onClick$btnNewb( Event ev ){
		
		if ( ProbTblEdit.enter( thisWin )){
			refreshList();
		}
		return;
	}
*/	
	
	
	// Open dialog to edit existing problem table entry
	
/*	public void onClick$btnEdit( Event ev ){
	
		// was an item selected?
		if ( lboxVaccineCVX.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) {  }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) lboxVaccineCVX.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;

		// call edit routine
		if ( ProbTblEdit.edit( rec, thisWin )){
			refreshList();
		}		
		return;
	}
*/	
	
	
	
	// Import from CVX flat file

	public void onClick$btnImport( Event ev ){
		
		Media media = null;
		
        try {
			media = Fileupload.get( "Select the CVX file to import.", "Import CVX" );
		} catch (Exception e) {
			DialogHelpers.Messagebox( "Error loading CVX file." );
			return;
		}

	
	
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading CVX file." );
			return;
		}
		
		if ( ! media.getContentType().equalsIgnoreCase( "text/plain" )){
			DialogHelpers.Messagebox( "CVX file wrong content type:" + media.getContentType());
			return;
		}
		
			
		// convert to string data
		String text = media.getStringData();

		if ( text.indexOf( "|" ) < 0 ){
			DialogHelpers.Messagebox( "Not a valid CVX file." );
			return;			
		}
		
		
		if ( VaccineCVX.importFromFlatFile( text ) == true ){
			DialogHelpers.Messagebox( "CVX Import done." );
			refreshList();
		}
		
		return;
	}
	


    public class MyModel extends ListModelList {
        public MyModel(List list, boolean flg) {
            super(list, flg);  // was true
        }
        public void sort(Comparator cmpr, boolean ascending) {
            Collections.sort(getInnerList() , cmpr);
            fireEvent(org.zkoss.zul.event.ListDataEvent.CONTENTS_CHANGED, -1, -1);
        }
    }

	
	private class CustomRenderer implements ListitemRenderer {
		
		//@Override
		public void render( Listitem item, Object data ) throws Exception {
			
			VaccineCVX.Entry v = (VaccineCVX.Entry) data;

			item.appendChild( new Listcell( v.getAbbr()) );
			item.appendChild( new Listcell( v.getCVX()) );
			item.appendChild( new Listcell( v.getStatus() == VaccineCVX.Status.ACTIVE ? "Active": "Inactive" ));
			item.appendChild( new Listcell( v.getShortDesc()) );
			
			String note = v.getNote();
			if (( note != null ) && ( note.length() > 0 )){
				item.appendChild( new Listcell( "Note" ));
				item.setTooltiptext( note );
			} else {
				item.appendChild( new Listcell( "" ));
			}
			
			item.appendChild( new Listcell( v.getFullDesc()) );

			return;			
		}

// ????? FROM GENE
//	
//		@Override
//		public void render(Listitem arg0, Object arg1, int arg2)
//				throws Exception {
//			 render(arg0,arg1);
//			
//		}
	
	}

	
	/** Comparators for Entry */
    public class MyComparator implements Comparator {
        private boolean ascending;
        private int columnIndex;
        public MyComparator (boolean ascending, int columnIndex) {
            this.ascending = ascending;
            this.columnIndex = columnIndex;
        }
        public int compare(Object o1, Object o2) {
            System.out.println("MyComparator : " + o1 + " vs " + o2);
            VaccineCVX.Entry e1 = (VaccineCVX.Entry )o1;
            VaccineCVX.Entry e2 = (VaccineCVX.Entry)o2;
            
            int v = 0;
            switch (columnIndex) {
                case 0:
                    v = e1.getAbbr().compareTo(e2.getAbbr());
                    break;
                case 1:		// sort CVX numerically
                    v = EditHelpers.parseInt( e1.getCVX()) - EditHelpers.parseInt( e2.getCVX());
                    break;
                case 2:
                    v = e1.getStatus().compareTo(e2.getStatus());
                    break;
                case 3:
                    v = e1.getShortDesc().toUpperCase().compareTo(e2.getShortDesc().toUpperCase());
                    break;
                case 4:
                	v = 0;
                	break;
                case 5:
                	v = 0;
                	break;
            }
            return ascending ? v: -v;
        }
    }

}
