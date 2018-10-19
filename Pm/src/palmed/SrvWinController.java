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

import usrlib.DialogHelpers;
import usrlib.Rec;

public class SrvWinController extends GenericForwardComposer {

	private Listbox srvListbox;		// autowired - Service code  listbox
	private Radio r_active;			// autowired
	private Radio r_inactive;		// autowired
	private Radio r_all;			// autowired
	private Window srvWin;			// autowired - Service codes  window (this window)
	private Textbox srcstr;			// autowired
	
	

	public SrvWinController() {
		// TODO Auto-generated constructor stub
	}

	public SrvWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public SrvWinController(char separator, boolean ignoreZScript,
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
		
		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_active.isSelected()) display = 1;
		if ( r_inactive.isSelected()) display = 2;
		if ( r_all.isSelected()) display = 3;

		
		if ( srvListbox == null ) return;
		
		// remove all items
		for ( int i = srvListbox.getItemCount(); i > 0; --i ){
			srvListbox.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s1 = searchString;
		if ( s1 != null ) s1 = s1.toUpperCase();
		
		
		// populate list

		Srv srv = Srv.open();
		
		while ( srv.getNext()){
			
			int fnd = 1;
			
			// get the srv code and the validity byte
			Srv.Status status = srv.isValid() ? Srv.Status.ACTIVE: Srv.Status.INACTIVE;
			// is this type selected?
			if ((( status == Srv.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == Srv.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
							
				if (( searchString != null )
					&& (( srv.getAbbr().toUpperCase().indexOf( s1 ) < 0 )
					&& ( srv.getDesc().toUpperCase().indexOf( s1 ) < 0 )
					&& (srv.getCode(1).toUpperCase().indexOf( s1) <0 )
					&& (srv.getCode(2).toUpperCase().indexOf( s1) <0 )
					&& (srv.getCode(3).toUpperCase().indexOf( s1) <0 )
					&& (srv.getrv().getPrintable().toUpperCase().indexOf(s1)<0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( srvListbox );
					i.setValue( srv.getRec());
										
					Listcell Abbr = new Listcell(srv.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent(i);
					
					Listcell Desc = new Listcell(srv.getDesc());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(i);
					
					Listcell C1 = new Listcell(srv.getCode(1));
					C1.setStyle("border-right:2px dotted black;");
					C1.setParent(i);
					
					Listcell C2 = new Listcell(srv.getCode(2));
					C2.setStyle("border-right:2px dotted black;");
					C2.setParent(i);
					
					Listcell C3 = new Listcell(srv.getCode(3));
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(i);
					
					Listcell rv = new Listcell(srv.getrv().getPrintable());
					rv.setStyle("border-right:2px dotted black;");
					rv.setParent(i);
			}
		}
	}
		
		
		srv.close();
		

		return;
	}

		public void onOK$srcstr( Event ev ){ onClick$search( ev ); }
		
		
		// Open search protocol to find service code 
		
		public void onClick$search( Event ev){

			String s = srcstr.getValue().trim();
			if ( s.length() < 3 ){
				DialogHelpers.Messagebox( "Please enter at least three letters in the search field." );
				return;
			}
			refreshList(( s.length() < 1 ) ? null: s );
			return;
		}
	
		
		// Open dialog to enter new code
		
		public void onClick$newsrv ( Event ev ){
			
			if ( EditSrv.enter( srvWin )){
				refreshList();
		}
			return;
		}
		
		
		// Open dialog to edit existing code
		
		public void onClick$edit( Event ev ){
		
			// was an item selected?
			if ( srvListbox.getSelectedCount() < 1 ){
				try { Messagebox.show( "No item is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
				return;
			}

			// get selected item's rec
			Rec rec = (Rec) srvListbox.getSelectedItem().getValue();
			System.out.println("rec: " + rec);
						
			if (( rec == null ) || ( rec.getRec() < 2 ))return;
			
			// call edit routine
			if ( EditSrv.edit( rec, srvWin )){
				refreshList();
				//alert("The code with record number: "+srvListbox.getSelectedItem().getValue()+" has been modified" );
				Srv srv = new Srv( rec );
				
				// log the access
				AuditLogger.recordEntry( AuditLog.Action.SRVCODE_EDIT, null, srv.getRec(), rec, srv.getAbbr() );
				
			}
			
			return;
		}
		

// Delete Protocol to remove a code and log access
		
		public void onClick$delete( Event ev ) throws InterruptedException{
			
			// was an item selected?
			if ( srvListbox.getSelectedCount() < 1 ){
				try { Messagebox.show( "No code is selected." ); } catch (InterruptedException e) { /*ignore*/ }
				return;
			}

			// is user sure?
			if ( Messagebox.show( "Do you really wish to delete this code?", "Delete Code?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES ) return;

			//Final check
			if ( Messagebox.show( "Are you sure you wish to preceed with the deletion?", "Final Warning!", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES )return;
			
			// get selected codes's rec
			Rec rec = (Rec) srvListbox.getSelectedItem().getValue();
			if (( rec == null ) || ( rec.getRec() < 2 )) return;
			
			// mark code inactive/hidden (delete)
			
			Srv srv = new Srv( rec );
			srv.setHidden();
			alert("The code with recrod number: "+srvListbox.getSelectedItem().getValue()+ " has been deleted" );
			System.out.println("Code deleted.");
			srv.write( rec );

			// log the access
			AuditLogger.recordEntry( AuditLog.Action.SRVCODE_DELETE, null, srv.getRec(), rec, srv.getAbbr() );
			
			// refresh list
			refreshList();	
			
			return;
		}
		
	// Restore code protocol  
		
		public void onClick$restore ( Event ev ) throws InterruptedException{
			
			// was an item selected?
			if ( srvListbox.getSelectedCount() < 1 ){
				try { Messagebox.show( "No code is selected." ); } catch (InterruptedException e) { /*ignore*/ }
				return;
				
			}
		
			// is user sure?
			if ( Messagebox.show( "Do you really wish to restore this code? ", "Restore code?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
		
			// get selected carrier's rec 
			Rec rec = ( Rec ) srvListbox.getSelectedItem().getValue();
			if (( rec == null ) || ( rec.getRec() < 2 )) return;
			
			// restore the code
			Srv srv = new Srv( rec );
			System.out.println("the validity code was: "+ srv.getValid());
			srv.setValid(2);
			System.out.println("the validity code now is: "+ srv.getValid());
			//(Validity.VALID.getCode() & 0xff) ;
			srv.write(rec);
			
			
			alert("The code with record number: "+srvListbox.getSelectedItem().getValue()+" has been restored" );
			System.out.println("Service code "+ srv.getAbbr()+" has been restored.");
			
			//log the access 
			AuditLogger.recordEntry(AuditLog.Action.SRVCODE_RESTORE, null, srv.getRec(), rec, srv.getAbbr());
			
			// refresh list 
			refreshList();
			
			return;
			
		}
			
		
}
