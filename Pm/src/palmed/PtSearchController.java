package palmed;

import java.util.Vector;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Name;
import usrlib.Rec;
import usrlib.StringHelpers;



public class PtSearchController extends GenericForwardComposer {
	
	private Window ptSearchWin;		// autowired
	private Textbox firstname;		// autowired
	private Textbox middlename;		// autowired
	private Textbox lastname;		// autowired
	private Textbox ssn;			// autowired - search for patient SSN
	private Textbox ptnum;			// autowired - search for patient number
	private Tabbox tabbox;			// autowired
	private	Intbox ptrec;			// autowired - invisible component used to pass ptrec back to caller
	private Listbox multFndListbox;	// autowired - multiple listings found listbox
	private Groupbox multFndGroupbox; // autowired - multiple listings found groupbox
	private Listbox lastFndListbox;	// autowired - last found listbox
	private Textbox birthdate;		// autowired - search for birthdate
	private Radio rb_normal;		// autowired - normal search radiobutton
	private Radio rb_asubstring;	// autowired - anchored substring search radiobutton
	private Radio rb_usubstring;	// autowired - un-anchored substring search radiobutton
	private Radio rb_soundex;		// autowired - sounds-like search radiobutton
	
	
	
	
	public void doAfterCompose( Component comp ){
		try {
			super.doAfterCompose(comp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set initial value of ptrec to 0		
		ptrec.setValue(0);
		
		
		
		// fill last found button
		Vector<PtFoundList> ptList =   (Vector<PtFoundList>) SystemHelpers.getSessionAttribute( "PtFoundListVector" );
		//if ( ptList == null ) System.out.println( "ptList == null" );
		
		if (( ptList != null ) && ( ptList.size() > 0 )){
			
			//System.out.println( "Adding " + ptList.size() + " elements to listbox." );

			for ( int cnt = 0; cnt < ptList.size(); ++ cnt ){
				PtFoundList ptFnd = (PtFoundList) ptList.get(cnt);
			
				Listitem i;
				(i = new Listitem()).setParent( lastFndListbox );
				new Listcell( ptFnd.getName().getPrintableNameLFM()).setParent( i );
				new Listcell( ptFnd.getBirthdate().getPrintable(9)).setParent( i );
				new Listcell( ptFnd.getSSN()).setParent( i );
				i.setValue( ptFnd.getPtRec());		// set pt rec into listitem for later retrieval
			}
		}


	}
	
	public void onClick$exitBtn( Event event ){
		
		ptSearchWin.detach();
	}
	
	public void onClick$searchBtn( Event event ){
		
		int searchType = 0;		// search type: 0 - ptname, 1 - birthdate, 2 - SSN, 3 - pt number
		int searchFlags = 0;	// search flags: 0-normal, 1-anchored, 2-unanchored
		int	fnd = 0;			// number of patients found
		PtFinder ptFinder = null;	// pt finder
		PtFoundList ptFnd;		// patient found
		
		
		
		// Clear previous search results from mult listings found listbox
		int num = multFndListbox.getItemCount();
		
		if ( num > 0 ){
			for ( int cnt = num; cnt > 0; --cnt ){
				multFndListbox.removeItemAt(cnt - 1);
			}			
		}
		multFndGroupbox.getCaption().setLabel( "Patients Found" );

		
		
		// determine search type
		//     (get selected tab in search window)
		searchType = tabbox.getSelectedIndex();
		//System.out.println( "searchType=" + searchType );
		

		// determine search flags
		//     (from radio buttons)
		if ( rb_asubstring.isChecked()) searchFlags = 1;
		if ( rb_usubstring.isChecked()) searchFlags = 2;
		if ( rb_soundex.isChecked()) searchFlags = 3;

		

		
		
		// Did user enter some backdoor search?
		//    (%num just returns the patient with the given record number.  No checks, no nothing.)
		{
			String s = null;
			
			if ( lastname.getValue().startsWith( "%" )) s = lastname.getValue();
			if ( firstname.getValue().startsWith( "%" )) s = firstname.getValue();
			if ( middlename.getValue().startsWith( "%" )) s = middlename.getValue();
			
			if ( s != null ){
				s = s.substring( 1 ).trim();
				int rec = Integer.parseInt( s );
				//System.out.println( "PtSearch() backdoor  rec=" + rec );
				if ( rec > 1 ){
					ptrec.setValue( rec );
					ptSearchWin.onClose();
					return;
				}
			}
		}

		
		
		// Did user enter some backdoor search?
		//    ($num searches for the patient by social security number SSN.)
		{
			String s = null;
			
			if ( lastname.getValue().startsWith( "$" )) s = lastname.getValue();
			if ( firstname.getValue().startsWith( "$" )) s = firstname.getValue();
			if ( middlename.getValue().startsWith( "$" )) s = middlename.getValue();
			
			if ( s != null ){
				s = StringHelpers.onlyDigits( s );
				ssn.setValue( s );
				//System.out.println( "PtSearch() SSN  ssn=" + s );
				searchType = 2;
			}
		}

		
		// Did user enter some backdoor search?
		//    (#num searches for the patient by patient number fields.)
		{
			String s = null;
			
			if ( lastname.getValue().startsWith( "#" )) s = lastname.getValue();
			if ( firstname.getValue().startsWith( "#" )) s = firstname.getValue();
			if ( middlename.getValue().startsWith( "#" )) s = middlename.getValue();
			
			if ( s != null ){
				s = s.substring( 1 ).trim();
				ptnum.setValue( s );
				//System.out.println( "PtSearch() PtNum  num=" + s );
				searchType = 3;
			}
		}
	
		
		

		

		switch ( searchType ){
		
		case 0:				// search by pt name
		default:
			
			// make sure at least some part of the name was entered
			if (( lastname.getValue().trim().length() == 0 ) && ( firstname.getValue().trim().length() == 0 ) && ( middlename.getValue().trim().length() == 0 )){
				try {
					Messagebox.show( "No patient name entered.", "Patient Search", Messagebox.OK, null );
				} catch (InterruptedException e) { /* ignore */ }
				return;
			}
			
			// Call PtFinder to do the search
			Name name = new Name( firstname.getValue(), middlename.getValue(), lastname.getValue(), "" );
			ptFinder = new PtFinder();
			fnd = ptFinder.doSearchName( name, searchFlags );
			//System.out.println( "back from ptFinder.doSearch(), fnd = " + fnd );
			break;
			
			
			
			
		case 1:				// search by birthdate
			
			if (( birthdate.getValue().trim().length() == 0 )){
				try {
					Messagebox.show( "No patient birthdate entered.", "Patient Search", Messagebox.OK, null );
				} catch (Exception e) { /* ignore */ }
				return;
			}
			
			usrlib.Date srcDOB = new Date( birthdate.getText());
			
			if (( ! srcDOB.isValid())){
				try {
					Messagebox.show( "Invalid patient birthdate entered.", "Patient Search", Messagebox.OK, null );
				} catch (Exception e) { /* ignore */ }
				return;
			}
			
			// Call PtFinder to do the search
			ptFinder = new PtFinder();
			fnd = ptFinder.doSearchDOB( srcDOB );
			//System.out.println( "back from ptFinder.doSearchDOB(), fnd = " + fnd );
			break;
			
			
			
			
		case 2:				// search by SSN
			
			String srcSSN = StringHelpers.onlyDigits( ssn.getValue());
			
			if (( srcSSN.length() == 0 )){
				try {
					Messagebox.show( "No patient SSN entered.", "Patient Search", Messagebox.OK, null );
				} catch (Exception e) { /* ignore */ }
				return;
			}
			
			// Call PtFinder to do the search
			ptFinder = new PtFinder();
			fnd = ptFinder.doSearchSSN( srcSSN );
			//System.out.println( "back from ptFinder.doSearchSSN(), fnd = " + fnd );
			break;
			
			
			
			
		case 3:				// search by pt number
			
			String srcNum = ptnum.getValue().trim();
			
			if (( srcNum.length() == 0 )){
				try {
					Messagebox.show( "No patient number entered.", "Patient Search", Messagebox.OK, null );
				} catch (Exception e) { /* ignore */ }
				return;
			}
			
			// Call PtFinder to do the search
			ptFinder = new PtFinder();
			fnd = ptFinder.doSearchNum( srcNum, searchFlags );
			//System.out.println( "back from ptFinder.doSearchNum(), fnd = " + fnd );
			break;
		}
		
		
		
		// Handle no listing found
		if ( fnd == 0 ){
			try {
				switch ( searchType ){
				
				case 0:
				default:

					Messagebox.show( "A patient with that name was not found.  Please try again with a different name.", "Patient Search", Messagebox.OK, null );
					break;
					
				case 1:
					
					Messagebox.show( "A patient with that birthdate was not found.  Please try again with a different one.", "Patient Search", Messagebox.OK, null );
					break;
					
				case 2:
					
					Messagebox.show( "A patient with that Social Security Number was not found.  Please try again with a different one.", "Patient Search", Messagebox.OK, null );
					break;
					
				case 3:
					
					Messagebox.show( "A patient with that patient number was not found.  Please try again with a different one.", "Patient Search", Messagebox.OK, null );
					break;
				}
			} catch (Exception e) { /* ignore */ }
			return;
		
			
		// One patient found - select it, set return, and close window
		} else if ( fnd == 1 ){			
			
			Vector<PtFoundList> ptList = ptFinder.getPtList();
			ptFnd = (PtFoundList) ptList.firstElement();
			ptrec.setValue( ptFnd.getPtRec().getRec());
			addLastFound( ptFnd );
			ptSearchWin.onClose();
			return;
			
			
		// Multiple listings found
		} else {
	
			// Handle too many found
			if ( fnd > 200 ){
				
				DialogHelpers.Messagebox( "Too many listing found. (" + fnd + ")  Please enter more of the name and try again." );
				return;
			}
			

			// Drop down multiple listing found groupbox (if not already)
			multFndGroupbox.setOpen( true );
			multFndGroupbox.getCaption().setLabel( "Patients Found: (" + fnd + ")" );

			// get pt found list
			Vector<PtFoundList> ptList = ptFinder.getPtList();

			// add each patient to the list
			for ( int cnt = 0; cnt < ptList.size(); ++cnt ){
				
				ptFnd = (PtFoundList) ptList.get( cnt );
				
				Listitem i;
				(i = new Listitem()).setParent( multFndListbox );
				new Listcell( ptFnd.getName().getPrintableNameLFM()).setParent( i );
				new Listcell( ptFnd.getBirthdate().getPrintable(9)).setParent( i );
				new Listcell( ptFnd.getSSN()).setParent( i );
				new Listcell( String.format( "%06d", ptFnd.getPtRec().getRec())).setParent( i );
				i.setValue( ptFnd );		// set ptFnd item into listitem for later retrieval
			}
			
			return;
		}
	}

	
	
	public void onClick$selectBtn( Event event ){

		// make sure there are items in listbox
		int num = multFndListbox.getItemCount();		
		if ( num < 1 ) return;

		// make sure an item is selected
		if ( multFndListbox.getSelectedCount() < 1 ) return;
		
		PtFoundList ptFnd =  (PtFoundList) multFndListbox.getSelectedItem().getValue();
		
		ptrec.setValue( ptFnd.getPtRec().getRec());
		addLastFound( ptFnd );
		ptSearchWin.onClose();
		
	}
	
	
	
	public void onClick$lastFndBtn( Event event ){
		
		// make sure there are items in listbox
		int num = lastFndListbox.getItemCount();		
		if ( num < 1 ) return;

		// make sure an item is selected
		//  for this listbox - if no item selected, take first item (index 0)
		if ( lastFndListbox.getSelectedCount() < 1 )
			lastFndListbox.setSelectedIndex(0);
		
		Rec r =  (Rec) lastFndListbox.getSelectedItem().getValue();
		
		ptrec.setValue( r.getRec());
		//ptrec.setValue( ptRec.getRec());
		ptSearchWin.onClose();		
	}
	
	
	
	// Add PtFoundList item to session's last found list
	private void addLastFound( PtFoundList ptFnd ){
		
		// TODO - Make sure ptFnd is not already in list
		// TODO - if already in list, pull up to the top - index 0
		// TODO - limit size of list?
		
		Vector<PtFoundList> ptList =   (Vector<PtFoundList>) SystemHelpers.getSessionAttribute( "PtFoundListVector" );
		
		if ( ptList == null ){
			//System.out.println( "Creating new vector" );
			ptList = new Vector<PtFoundList>( 10, 10 );
			SystemHelpers.setSessionAttribute( "PtFoundListVector", ptList );
		}
		
		//System.out.println( "Adding element" );
		ptList.insertElementAt( ptFnd, 0 );
	}
	
	//public void onOK$Code( Event ev ){ onClick$searchBtn( ev ); }
	//modified to account for both protocols
	public void onOK( Event ev ){
		if ( multFndListbox.getSelectedCount() > 0 ){
			onClick$selectBtn( ev );
		} else {
			onClick$searchBtn( ev );
		}
		return;
	}
	
	public void onDoubleClick( Event ev ){  onClick$selectBtn( ev ); }
	
	public void onClear( Event ev ){
		onClick$exitBtn( ev );
		return;
	}
}
