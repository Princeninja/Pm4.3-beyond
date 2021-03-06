package palmed;

import java.util.EnumSet;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;


import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class HxFamilyItemWinController extends GenericForwardComposer {

	private Listbox lboxFamHx;			// autowired
	private Listbox lboxMember;			// autowired
	private Listbox lboxProbTbl;			// autowired
	private Textbox txtNote;			// autowired
	private Textbox txtSearch;			// autowired
	
	
	private Checkbox cbPreselect;		// autowired
	
	private Rec	ptRec;		// patient record number

	
	

	
	
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
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "bad ptRec" );


		// populate family member listbox
		for ( HxFamilyItem.Member r : EnumSet.allOf(HxFamilyItem.Member.class))
			ZkTools.appendToListbox( lboxMember, r.getLabel(), r );
		
		// populate probTbl listbox
		cbPreselect.setChecked( true );
		doSearch( null );

		
		refreshList();
		
		return;
	}
	
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){
	
		if ( lboxFamHx == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxFamHx );
		
		

		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		HxFamilyItem fam = null;

		for ( Reca reca = medPt.getHxFamilyItemReca(); Reca.isValid( reca ); reca = fam.getLLHdr().getLast()){
			
			fam = new HxFamilyItem( reca );
			if ( fam.getStatus() != usrlib.RecordStatus.CURRENT ) continue;
				
			
			
			// create new Listitem and add cells to it
			Listitem i = new Listitem();
			i.setValue( reca );			
			i.appendChild( new Listcell( fam.getMember().getLabel()));
			String str1 = ProbTbl.getDesc( fam.getProbTblRec());
			String str2 = fam.getNoteText();
			if ( str2.length() > 0 ) str1 = str1 + " - " + str2;
			i.appendChild( new Listcell( str1 ));
			i.appendChild( new Listcell( fam.getNoteText()));
			lboxFamHx.appendChild( i );
		}

		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	// Remove an entry
	
	public void onClick$btnRemove( Event ev ) throws InterruptedException{
	
		// was an item selected?
		if ( lboxFamHx.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		// get item from listbox
		Reca reca = (Reca) usrlib.ZkTools.getListboxSelectionValue( lboxFamHx );
		if ( ! Reca.isValid( reca )) return;
		
		// is user sure?
		if ( DialogHelpers.Optionbox( "Family History", "Really remove the selected item?", "Yes", "No", null ) != 1 ) return;
		
		// mark item removed
		HxFamilyItem.markRemoved( reca );
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.HISTORY_EDIT, ptRec, Pm.getUserRec(), reca, null );

		// refresh list
		refreshList();	
		return;
	}
	
	
	
	
	
	
	// Add an entry
	
	public void onClick$btnAdd( Event ev ) throws InterruptedException{
		
		// was a family member selected?
		if ( lboxMember.getSelectedCount() < 1 ){
			try { Messagebox.show( "No family member selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}
		
		// was an item selected?
		if ( lboxProbTbl.getSelectedCount() < 1 ){
			try { Messagebox.show( "No history item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		
		// get info 
		HxFamilyItem.Member member = (HxFamilyItem.Member) usrlib.ZkTools.getListboxSelectionValue( lboxMember );
		Rec rec = (Rec) usrlib.ZkTools.getListboxSelectionValue( lboxProbTbl );
		String txtLabel = usrlib.ZkTools.getListboxSelectionLabel( lboxProbTbl );
		String note = txtNote.getValue();

		// save item
		Reca reca = HxFamilyItem.postNew( ptRec, member, rec, note );
		
		// append item to listbox
//		Listitem i = new Listitem();
//		i.appendChild( new Listcell( member.getLabel()));
//		i.appendChild( new Listcell( txtLabel + " " + note ));
//		lboxFamHx.appendChild( i );
		
		
		// get ready for next
		txtNote.setValue( "" );
		lboxMember.clearSelection();
		// note - we will leave the ProbTbl item selected between entries
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.HISTORY_NEW, ptRec, Pm.getUserRec(), reca, null );
		
		// refresh list
		refreshList();	
		return;
	}
	
	
	
	
	
	// Search ProbTbl List
	
	public void onClick$btnSearch( Event ev ) throws InterruptedException{
		
		// was an item selected?
		if ( cbPreselect.isChecked() ){
			doSearch( null );
		} else {
			
			String s = txtSearch.getValue().trim();
			if ( s.length() < 3 ){
				DialogHelpers.Messagebox( "Please enter at least 3 letters to search for." );
				return;
			}
			doSearch( s );
		}
		return;
	}
	
	public void onCheck$cbPreselect( Event ev ) throws InterruptedException{
		if ( cbPreselect.isChecked()){
			txtSearch.setValue("");
			doSearch( null );
		}
	}
	
	private void doSearch( String s ){
		usrlib.ZkTools.listboxClear( lboxProbTbl );		
		ProbTbl.fillListbox( lboxProbTbl, ( s == null ) ? "Family History": s );		
	}
	
}
