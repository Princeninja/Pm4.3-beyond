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


public class PpoWinController extends GenericForwardComposer {

	private Listbox ppoListbox; //autowired - Insurance carriers Listbox
	private Radio r_active;     //autowired
	private Radio r_inactive;   //autowired
	private Radio r_all; 		//autowired
	private Window ppoWin;		//autowired
	private Textbox srcstr;		//autowired
	
	
	public PpoWinController () {
		
	}
	
	public PpoWinController(char separator) {
		super(separator);
		
	}
	
	public PpoWinController(char separator, boolean ignoreZScript,
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
	
	static void join (StringBuilder SB, String str) {
		if ((str != null) && (!(str.length() == 0))){
			if (SB.length() > 0 ){
				SB.append('~');
			}
			SB.append(str);
		}
	}
	
	// Refresh Listbox when needed.
	public void refreshList() { onClick$search( null ); }
	
	public void refreshList( String searchString ){
		
		 // which kind of list to display
		  int display = 1;
		  if (r_active.isSelected()) display = 1;
		  if (r_inactive.isSelected()) display = 2;
		  if (r_all.isSelected()) display = 3;
		  
		
				
		if ( ppoListbox == null ) return;
		
		// remove all items
		for ( int i = ppoListbox.getItemCount(); i > 0; --i ){
			ppoListbox.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s2 = searchString;
		if ( s2 != null ) s2 = s2.toUpperCase();
		
		
		// populate list

		Ppo ppo = Ppo.open();
		
		while ( ppo.getNext()){
			String A2,A3,A12,A13,Contacts2,Notes2;
			String Phones2;
			A2 = ppo.getAddress().getPrintableAddress(2);
			A3 = ppo.getAddress().getPrintableAddress(3);
			A12 = ppo.getAddress().getPrintableAddress(1)+","+ A2;
			A13 = ppo.getAddress().getPrintableAddress(1)+","+ A3;
			String C1,C2,C3,C4,C11,C22,C33,C44;
			C1= ppo.getContact(1);
			C2= ppo.getContact(2);
			C3= ppo.getContact(3);
			C4= ppo.getContact(4);
			if (C1.trim().length() == 0){
				C11="";
			}
			else {
				C11=C1;
			}
			if (C2.trim().length() == 0){
				C22="";
			}
			else {
				C22=C2;
			}
			if (C3.trim().length() == 0){
				C33="";
			}
			else {
				C33=C3;
			}
			if (C4.trim().length() == 0){
				C44="";
			}
			else {
				C44=C4;
			}
			StringBuilder Contacts = new StringBuilder(C11);
			join(Contacts, C22);
			join(Contacts, C33);
			join(Contacts, C44);
			
			Contacts2 = Contacts.toString();
			
			String N1,N2,N3,N4,N11,N22,N33,N44;
			N1= ppo.getnote(1);
			N2= ppo.getnote(2);
			N3= ppo.getnote(3);
			N4= ppo.getnote(4);
			if (N1.trim().length() == 0){
				N11="";
			}
			else {
				N11=N1;
			}
			if (N2.trim().length() == 0){
				N22="";
			}
			else {
				N22=N2;
			}
			if (N3.trim().length() == 0){
				N33="";
			}
			else {
				N33=N3;
			}
			if (N4.trim().length() == 0){
				N44="";
			}
			else {
				N44=N4;
			}
			
				
			StringBuilder Notes = new StringBuilder(N11);
			join(Notes, N22);
			join(Notes, N33);
			join(Notes, N44);
			
			Notes2 = Notes.toString();
			
			String P1,P2,P3,P4,P5,P6,P11,P22,P33,P44,P55,P66;
			P1= ppo.getphone(1);
			P2= ppo.getphone(2);
			P3= ppo.getphone(3);
			P4= ppo.getphone(4);
			P5= ppo.getphone(5);
			P6= ppo.getphone(6);
			
			if (P1.trim().length() < 1){
				P11="";
			}
			else {
				P11=P1;
			}
			if (P2.trim().length() < 1){
				P22="";
			}
			else {
				P22=P2;
			}
			if (P3.trim().length() < 1){
				P33="";
			}
			else {
				P33=P3;
			}
			if (P4.trim().length() < 1){
				P44="";
			}
			else {
				P44=P4;
			}
			if (P5.trim().length() < 1){
				P55="";
			}
			else {
				P55=P5;
			}
			if (P6.trim().length() < 1){
				P66="";
			}
			else {
				P66=P6;
			}
						
			StringBuilder Phones = new StringBuilder(P11);
			join(Phones, P22);
			join(Phones, P33);
			join(Phones, P44);
			join(Phones, P55);
			join(Phones, P66);
			
			Phones2 = Phones.toString();
			
			int fnd = 1;
			
			// get ppo carrier validity
			Ppo.Status status = ppo.isValid() ? Ppo.Status.ACTIVE: Ppo.Status.INACTIVE;
			// is this type selected?
			if ((( status == Ppo.Status.ACTIVE) && (( display & 1 ) != 0 )) 
					|| (( status == Ppo.Status.INACTIVE) && (( display & 2) != 0 ))){
			
				if (( searchString != null )
					&& (( ppo.getAbbr().toUpperCase().indexOf( s2 ) < 0 )
					&& ( ppo.getname().toUpperCase().indexOf( s2 ) < 0 )
					&& (ppo.getContact(1).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(2).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(3).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(4).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getnote(1).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(2).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(3).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(4).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(1).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(2).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(3).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(4).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(5).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(6).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getAddress().getPrintableAddress(1).toUpperCase().indexOf(s2) < 0)
					&& (A2.toUpperCase().indexOf(s2) < 0)
					&& (A3.toUpperCase().indexOf(s2) < 0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( ppoListbox );
					i.setValue( ppo.getRec());
					
					
					Listcell Desc = new Listcell( ppo.getname());
					Desc.setStyle("border-left:2px solid dotted; border-right:2px dotted black;");
					Desc.setParent( i );
					
					Listcell Abbr = new Listcell( ppo.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent( i );
					
					Listcell C = new Listcell(Contacts2);
				
					C.setStyle("border-right:2px dotted black;"); 
					C.setParent(i);
					
					Listcell N = new Listcell (Notes2);
					N.setStyle("border-right:2px dotted black;");
					N.setParent(i);
					
					Listcell P = new Listcell (Phones2);
					P.setStyle("border-right:2px dotted black;");
					P.setParent(i);
					
					if(ppo.getAddress().getPrintableAddress(1).trim().length()== 0 && A2.trim().length() == 0)
					{
						Listcell Blank =  new Listcell ("");
						Blank.setStyle("border-right:2px dotted black;");
						Blank.setParent(i);
						
						Listcell Blank2 = new Listcell ("");
						Blank2.setStyle("border-right:2px dotted black;");
						Blank2.setParent(i);
					}
					
					else if (A2.trim().length() == 0){ 
						Listcell Addr =  new Listcell (ppo.getAddress().getPrintableAddress(1));
						Addr.setStyle("border-right:2px dotted black;");
						Addr.setParent(i);
						
						Listcell CSZ = new Listcell (A3);
						CSZ.setStyle("border-right:2px dotted black;");
						CSZ.setParent(i);
							}
					
					else {
						Listcell Addr = new Listcell (A12);
						Addr.setStyle("border-right:2px dotted black;");
						Addr.setParent(i);
						
						
						Listcell CSZ = new Listcell (A3);
						CSZ.setStyle("border-right:2px dotted black;");
						CSZ.setParent(i);
						
						}		
					}
			}
		}
				
		ppo.close();
		
		return;
	}
	
	public void onOK$srcstr( Event ev) { onClick$search( ev ); }
	
	// search protocol to find Insurance Carriers.
	
	public void onClick$search( Event ev ) {
	
		String s = srcstr.getValue().trim();
		if ( s.length() <3 ){
			DialogHelpers.Messagebox( "Please enter at least three letters in the search field. ");
			return;
		}
		refreshList(( s.length() < 1 ) ? null: s);
		return;
	}
	
	// Open dialog to enter new Insurance carriers
	
	public void onClick$newppo( Event ev ){
		
		if ( EditPpo.enter( ppoWin )){
			refreshList();
			System.out.println("new was clicked");
			
			
			/*Ppo ppo = new Ppo();
			Rec rec = ppo.getRec();
			// log access
			AuditLogger.recordEntry(AuditLog.Action.PPO_ADD, null, ppo.getRec(), rec , ppo.getAbbr());
		     */
		}
		return;
		
	}
	
	// open dialog to edit existing code
	
	public void onClick$edit ( Event ev ){
		
		//was an item selected? 
		if ( ppoListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item is currently selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}
		
		// get the selected item's rec 
		Rec rec = (Rec) ppoListbox.getSelectedItem().getValue();
		System.out.println("rec: " + rec);
		
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// call edit routine 
		if ( EditPpo.edit(rec, ppoWin)) {
			refreshList();
			//alert("That Insurance carrier has been edited" );
			Ppo ppo = new Ppo( rec );
			
			// log access
			AuditLogger.recordEntry(AuditLog.Action.PPO_EDIT, null, ppo.getRec(), rec, ppo.getAbbr());
		}
		
		return;
		
	 }
	
	// Delete protocol to remove Insurance carrier 
	
	public void onClick$delete ( Event ev ) throws InterruptedException{
		
		// was an item selected?
		if ( ppoListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No carrier is selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
			
		}
	
		// is user sure?
		if ( Messagebox.show( "Do you really wish to delete this Insurance Carrier? ", "Delete Insurance Carrier?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
	
		//Final check 
		if ( Messagebox.show( "Are you sure you want to delete this carrier?", "Final check!", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES )return;
		
		// get selected carrier's rec 
		Rec rec = ( Rec ) ppoListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// mark carrier inactive/hidden (delete)
		
		Ppo ppo = new Ppo( rec );
		ppo.setHidden(); 
		alert("The insurance carrier with record number: "+ppoListbox.getSelectedItem().getValue()+" has been deleted" );
		//System.out.println("Ins Carrier deleted.");
		ppo.write(rec);
		
		//log the access 
		AuditLogger.recordEntry(AuditLog.Action.PPO_DELETE, null, ppo.getRec(), rec, ppo.getAbbr());
		
		// refresh list 
		refreshList();
		
		return;
	} 
	
	// Restore protocol to restore a previously deleted carrier 
	
	public void onClick$restore ( Event ev ) throws InterruptedException{
		
		// was an item selected?
		if ( ppoListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No carrier is selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
			
		}
	
		// is user sure?
		if ( Messagebox.show( "Do you really wish to restore this Insurance Carrier? ", "Restore Insurance Carrier?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION ) != Messagebox.YES ) return;
	
		// get selected carrier's rec 
		Rec rec = ( Rec ) ppoListbox.getSelectedItem().getValue();
		if (( rec == null ) || ( rec.getRec() < 2 )) return;
		
		// restore the carrier 
		Ppo ppo = new Ppo( rec );
		//System.out.println("the validity code was: "+ ppo.getValid());
		ppo.setValid(2);
		//System.out.println("the validity code now is: "+ ppo.getValid());
		//(Validity.VALID.getCode() & 0xff) ;
		ppo.write(rec);
		
		
		alert("The insurance carrier with record number: "+ppoListbox.getSelectedItem().getValue()+" has been restored" );
		//System.out.println("Ins Carrier restored.");
		
		//log the access 
		AuditLogger.recordEntry(AuditLog.Action.PPO_RESTORE, null, ppo.getRec(), rec, ppo.getAbbr());
		
		// refresh list 
		refreshList();
		
		return;
		
	}
	
	
	
	

}
	
		
		

	
	

