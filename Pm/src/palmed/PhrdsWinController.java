package palmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

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
import usrlib.Validity;

public class PhrdsWinController extends GenericForwardComposer {
	
	private Listbox phrdsListbox; //autowired - Pharmarcies Listbox
	private Radio r_active;     //autowired
	private Radio r_inactive;   //autowired
	private Radio r_all; 		//autowired
	private Window phrdsWin;		//autowired
	private Textbox srcstr;		//autowired
	

	public PhrdsWinController () {
		
	}
	
	public PhrdsWinController(char separator) {
		super(separator);
		
	}
	
	public PhrdsWinController(char separator, boolean ignoreZScript,
			 boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
	}
	
	
	public void doAFterCompose ( Component component ) {
			
		// Call superclass to do autowiring 
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return;
	
	}
	

	// Watch for radiobutton change 
	public void onCheck$r_active( Event ev ){
		refreshList();
	}
	
	public void onCheck$r_inactive( Event ev ) {
		refreshList();
	}
	
	public void onCheck$r_all( Event ev ) { 
		refreshList();
	}
	
	
	// Refresh Listbox when needed.
	public void refreshList() { onClick$search( null ); }
	

	public void refreshList( String searchString ){
		
		 // which kind of list to display
		  int display = 1;
		  if (r_active.isSelected()) display = 1;
		  if (r_inactive.isSelected()) display = 2;
		  if (r_all.isSelected()) display = 3;
		  
		  if ( phrdsListbox == null ) return;
			
			// remove all items
			for ( int i = phrdsListbox.getItemCount(); i > 0; --i ){
				phrdsListbox.removeItemAt( 0 );
			}
			
			
			// search string passed?
			String s3 = searchString;
			if ( s3 != null ) s3 = s3.toUpperCase();
			
			
			// populate list

			Phrds phrds = Phrds.open();
			
			while ( phrds.getNext()){
				String Pa2,Pa3,P12,P13;
				Pa2= phrds.getAddress().getPrintableAddress(2);
				Pa3= phrds.getAddress().getPrintableAddress(3);
				P12= phrds.getAddress().getPrintableAddress(1)+"," + Pa2;
				P13 = phrds.getAddress().getPrintableAddress(1)+"," + Pa3;
				
				int fnd = 1;
				
				// get phrds pharmacy validity 
				Phrds.Status status = phrds.isValid() ? Phrds.Status.ACTIVE: Phrds.Status.INACTIVE;
				// is this the type selected?
				if ((( status == Phrds.Status.ACTIVE) && (( display & 1 ) != 0 ))
						|| (( status == Phrds.Status.INACTIVE) && (( display & 2) != 0 ))){
				
					if (( searchString != null )
						&& (( phrds.getAbbr().toUpperCase().indexOf( s3 ) < 0 )
						&& ( phrds.getName().toUpperCase().indexOf( s3 ) < 0 )
						&& (phrds.getAddress().getPrintableAddress(1).toUpperCase().indexOf(s3)< 0)
						&& (Pa2.toUpperCase().indexOf(s3)<0)
						&& (Pa3.toUpperCase().indexOf(s3)< 0)
						&& (phrds.getContact().toUpperCase().indexOf(s3) < 0)
						
						)){					
						// this one doesn't match
						fnd = 0;
					}
			
					if ( fnd > 0 ){
						
						// create new Listitem and add cells to it
						Listitem i;
						(i = new Listitem()).setParent( phrdsListbox );
						i.setValue( phrds.getRec());
						
						Listcell Name = new Listcell( phrds.getName());
						Name.setStyle("border-right:2px dotted black;");
						Name.setParent( i );
						
						Listcell Abbr = new Listcell( phrds.getAbbr());
						Abbr.setStyle("border-right:2px dotted black;");
						Abbr.setParent( i );
						
						Listcell Contact = new Listcell(phrds.getContact());
						Contact.setStyle("border-right:2px dotted black;");
						Contact.setParent(i);
						
						if (phrds.getAddress().getPrintableAddress(1).trim().length() == 0 && Pa2.trim().length()== 0){
							Listcell Blank = new Listcell ("");
							Blank.setStyle("border-right:2px dotted black;");
							Blank.setParent(i);
							
							Listcell Blank2 = new Listcell ("");
							Blank2.setStyle("border-right:2px dotted black;");
							Blank2.setParent(i);
						
						}
											
						else if (Pa2.trim().length() ==0){
							Listcell A1 = new Listcell (phrds.getAddress().getPrintableAddress(1));
							A1.setStyle("border-right:2px dotted black;");
							A1.setParent( i );
							
							Listcell CSZ = new Listcell (Pa3);
							CSZ.setStyle("border-right:2px dotted black;");
							CSZ.setParent(i);
						}
					
						else{
							Listcell A12 = new Listcell (P12);
							A12.setStyle("border-right:2px dotted black;");
							A12.setParent(i);
							
						    Listcell CSZ = new Listcell (Pa3);
						    CSZ.setStyle("border-right:2px dotted black;");
						    CSZ.setParent(i);
						}
			    
					}
				}
			}		
			phrds.close();
			
			return;
		}
	
	public void onOK$srcstr( Event ev ){ onClick$search( ev ); }
	
    // search protocol to find Pharmacies.
	
	public void onClick$search( Event ev ) {
	
		String s = srcstr.getValue().trim();
		if ( s.length() <3 ){
			DialogHelpers.Messagebox( "Please enter at least three letters in the search field.");
			return;
		}
		refreshList(( s.length() < 1 ) ? null: s);
		return;
	}
	

	// Open dialog to enter new Pharmacy
	
	public void onClick$newphrds( Event ev ){
		
		if ( EditPhrds.enter( phrdsWin )){
			refreshList();
			System.out.println("new was clicked on");
			
		}
		return;
		
	}
	
	
// open dialog to edit existing pharmacy
	
	public void onClick$edit ( Event ev ){
		
		//was an item selected? 
		if ( phrdsListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}
		
		// get the selected item's rec 
		Rec rec = (Rec) phrdsListbox.getSelectedItem().getValue();
		System.out.println("rec: " + rec);
		
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// call edit routine 
		if ( EditPhrds.edit(rec, phrdsWin)) {
			refreshList();
			alert("That pharmacy has been edited" );
			Phrds phrds = new Phrds( rec );
			
			// log access
			AuditLogger.recordEntry(AuditLog.Action.PHRDS_EDIT, null, phrds.getRec(), rec, phrds.getAbbr());
		}
		return;
	 }

// Delete protocol to remove pharmacy
	
	public void onClick$delete ( Event ev ) throws InterruptedException{
		
		// was an item selected?
		if ( phrdsListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No pharmacy is selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
			
		}
	
		// is user sure?
		if ( Messagebox.show( "Do you really wish to delete this Pharmacy? ", "Delete Pharmacy?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
	
		//Final check 
		if ( Messagebox.show( "Are you sure you want to delete this pharmacy?", "Final check!", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES )return;
		
		// get selected carrier's rec 
		Rec rec = ( Rec ) phrdsListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// mark pharmacy inactive/hidden (delete)
		Phrds phrds = new Phrds ( rec );
		phrds.setHidden(); 
		alert("The pharmacy with the record number: "+phrdsListbox.getSelectedItem().getValue()+" has been deleted" );
		System.out.println("Pharmacy deleted...");
		phrds.write(rec);
		
		//log the access 
		AuditLogger.recordEntry(AuditLog.Action.PHRDS_DELETE, null, phrds.getRec(), rec, phrds.getAbbr());
		
		// refresh list 
		refreshList();
		
		return;
	} 
	
	
}
