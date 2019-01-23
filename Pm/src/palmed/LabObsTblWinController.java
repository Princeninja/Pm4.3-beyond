package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.ZkTools;

public class LabObsTblWinController extends GenericForwardComposer {

	private Listbox labObsTblListbox;	// autowired
	private Radio r_active;				// autowired
	private Radio r_inactive;			// autowired
	private Radio r_all;				// autowired
	private Window labObsTblWin;		// autowired
	private Textbox srcstr;				// autowired
	
	
	

	
	
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
		
		
		
		// populate listbox
		refreshList();
		
		return;
	}
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$r_active( Event ev ){
		refreshList();
	}
	public void onCheck$r_inactive( Event ev ){
		refreshList();
	}
	public void onCheck$r_all( Event ev ){
		refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){ refreshList( null ); }
	
	public void refreshList( String searchString ){

		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_active.isSelected()) display = 1;
		if ( r_inactive.isSelected()) display = 2;
		if ( r_all.isSelected()) display = 3;
		
		if ( labObsTblListbox == null ) return;
		
		// remove all items
		ZkTools.listboxClear( labObsTblListbox );
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		LabObsTbl lab = LabObsTbl.open();
		
		while ( lab.getNext()){
			
			int fnd = 1;
			
			// get problem status byte
			LabObsTbl.Status status = lab.getStatus();
			// is this type selected?
			if ((( status == LabObsTbl.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == LabObsTbl.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				// ( status == probTbl.Status.REMOVED ) - removed
				// ( status == probTbl.SUPERCEDED ) - superceded by edit
				

				if (( searchString != null )
					&& ( lab.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( lab.getDesc().toUpperCase().indexOf( s ) < 0 )
					){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( labObsTblListbox );
					i.setValue( new Rec( lab.getRec().getRec()));
					
					new Listcell( lab.getAbbr()).setParent( i );
					new Listcell( lab.getDesc()).setParent( i );
				}
			}
		}
		
		lab.close();
		

		return;
	}

	
	public void onOK$srcstr( Event ev ){ onClick$btnSearch( ev ); }
	
	
	// Open dialog to enter new entry
	
	public void onClick$btnSearch( Event ev){

		String s = srcstr.getValue().trim();
		//if ( s.length() < 3 ){
		//	DialogHelpers.Messagebox( "Please enter at least three letters in search field." );
		//	return;
		//}
		refreshList(( s.length() < 1 ) ? null: s );
		return;
	}
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$btnNew( Event ev ){
		
		if ( LabObsTblEdit.enter( labObsTblWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem table entry
	
	public void onClick$btnEdit( Event ev ){
	
		// was an item selected?
		if ( labObsTblListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) labObsTblListbox.getSelectedItem().getValue();
		if ( ! Rec.isValid( rec )) return;

		System.out.println("obs rec is: "+rec);
		// call edit routine
		if ( LabObsTblEdit.edit( rec, labObsTblWin )){
			refreshList();
		}		
		return;
	}
	
	
	
}
