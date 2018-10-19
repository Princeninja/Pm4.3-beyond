package palmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.pmUser.Status;

import usrlib.DialogHelpers;
import usrlib.Rec;


public class DgnWincontroller extends GenericForwardComposer{

	
	private Listbox dgnListbox;		// autowired - diagnosis code  listbox
	private Radio r_active;			// autowired
	private Radio r_inactive;		// autowired
	private Radio r_all;			// autowired
	private Window dgnWin;			// autowired - Diagnosis codes  window (this window)
	private Textbox srcstr;			// autowired
	
	
	public DgnWincontroller() {
		// TODO Auto-generated constructor stub
	}

	public DgnWincontroller(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public DgnWincontroller(char separator, boolean ignoreZScript,
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
		
		return;
	}
	
	
	// Watch for radiobutton to change
	public void onCheck$r_active( Event ev ){
		refreshList();
		//System.out.println("Refreshed for active codes");
	}
	public void onCheck$r_inactive( Event ev ){
		refreshList();
		//System.out.println("Refreshed for inactive codes");
		
	}
	public void onCheck$r_all( Event ev ){
		refreshList();
		//System.out.println("Refreshed for all codes");
	}	
	
	
	
	// Refresh Listbox when needed
	public void refreshList(){ onClick$search( null ); }
	
		public void refreshList( String searchString ){
		
		// which kind of list to display (from radio b uttons)
		int display = 1;
		if ( r_active.isSelected()) display = 1;
		if ( r_inactive.isSelected()) display = 2;
		if ( r_all.isSelected()) display = 3;
				
		if ( dgnListbox == null ) return;
		
		// remove all items
		for ( int i = dgnListbox.getItemCount(); i > 0; --i ){
			dgnListbox.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		Dgn dgn = Dgn.open();
		
		while ( dgn.getNext()){
			
			int fnd = 1;
			
			// get the dgn code and the validity byte
			Dgn.Status status = dgn.isValid() ? Dgn.Status.ACTIVE: Dgn.Status.INACTIVE;
			// is this type selected?
			System.out.println("status is: "+status);
			System.out.println("display is: "+display);
			System.out.println("&1, &2:" + (display &1) +", "+(display & 2));
			if ((( status == Dgn.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == Dgn.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				
			
				if (( searchString != null )
					&& (( dgn.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( dgn.getDesc().toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(1).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(2).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(3).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getrv().getPrintable().toUpperCase().indexOf(s)<0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new List item and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( dgnListbox );
					i.setValue( dgn.getRec());
					
					Listcell Desc = new Listcell( dgn.getDesc());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(i);
					
					Listcell Abbr = new Listcell( dgn.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent(i);
					
					Listcell C1 = new Listcell(dgn.getCode(1));
					C1.setStyle("border-right:2px dotted black;");
					C1.setParent(i);
					
					Listcell C2 = new Listcell(dgn.getCode(2));
					C2.setStyle("border-right:2px dotted black;");
					C2.setParent(i);
					
					Listcell C3 = new Listcell(dgn.getCode(3));
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(i);
					
					Listcell rv = new Listcell(dgn.getrv().getPrintable());
					rv.setStyle("border-right:2px dotted black;");
					rv.setParent(i);
					
				}
			}
		}
		
		
		dgn.close();
		

		return;
	}

		public void onOK$srcstr( Event ev ){ onClick$search( ev ); }
		
		
		// Open search protocol to find diagnosis code 
		
		public void onClick$search( Event ev){

			String s = srcstr.getValue().trim();
			if ( s.length() < 3 ){
				DialogHelpers.Messagebox( "Please enter at least three letters in search field." );
				return;
			}
			refreshList(( s.length() < 1 ) ? null: s );
			return;
		}
	
		// Open dialog to enter new code
		
		public void onClick$newdgn( Event ev ){
			
			if ( EditDgn.enter( dgnWin )){
				refreshList();
				
				/*Dgn dgn = new Dgn();
				Rec rec = dgn.getRec();
				
				
				//log access
				AuditLogger.recordEntry(AuditLog.Action.DGNCODE_ADD, null, dgn.getRec(), rec , dgn.getAbbr());
				*/
			}
			return;
		}
		

		// Open dialog to edit existing code
		
		public void onClick$edit( Event ev ){
		
			// was an item selected?
			if ( dgnListbox.getSelectedCount() < 1 ){
				try { Messagebox.show( "No item is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
				return;
			}

			// get selected item's rec
			Rec rec = (Rec) dgnListbox.getSelectedItem().getValue();
			System.out.println("rec: " + rec);
			/*System.out.println(dgnListbox2.getSelectedIndex());
			System.out.println(dgnListbox2.getSelectedItem().getLabel());
			System.out.println(dgnListbox2.getSelectedItem().getValue());
			System.out.println(dgnListbox2.getSelectedItem().getValue().toString());
			System.out.println(dgnListbox2.getSelectedItem());*/
			
			if (( rec == null ) || ( rec.getRec() < 2 ))return;
			
			// call edit routine
			if ( EditDgn.edit( rec, dgnWin )){
				refreshList();
				//alert("The code with record number: "+dgnListbox.getSelectedItem().getValue()+" has been modified" );
				Dgn dgn = new Dgn( rec );
				
				// log the access
				AuditLogger.recordEntry( AuditLog.Action.DGNCODE_EDIT, null, dgn.getRec(), rec, dgn.getAbbr() );
				
			}
			
			return;
		}
		
		// Delete Protocol to remove a code and log access
		
		public void onClick$delete( Event ev ) throws InterruptedException{
			
			// was an item selected?
			if ( dgnListbox.getSelectedCount() < 1 ){
				try { Messagebox.show( "No code is selected." ); } catch (InterruptedException e) { /*ignore*/ }
				return;
			}

			// is user sure?
			if ( Messagebox.show( "Do you really wish to delete this code?", "Delete Code?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES ) return;

			//Final check
			if ( Messagebox.show( "Are you sure you wish to preceed with the deletion?", "Final Warning!", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES )return;
			
			// get selected codes's rec
			Rec rec = (Rec) dgnListbox.getSelectedItem().getValue();
			if (( rec == null ) || ( rec.getRec() < 2 )) return;
			
			// mark code inactive/hidden (delete)
			
			Dgn dgn = new Dgn( rec );
			dgn.setHidden();
			alert("The code with recrod number: "+dgnListbox.getSelectedItem().getValue()+ " has been deleted" );
			System.out.println("Code deleted.");
			dgn.write( rec );

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.DGNCODE_DELETE, null, dgn.getRec(), rec, dgn.getAbbr() );
			
			// refresh list
			refreshList();	
			
			return;
		}
			
	
}	

