package palmed;

import java.util.Vector;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import palmed.PmLogin.PmLogin;

import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;

public class ERSearchWinController extends GenericForwardComposer {

	private Window ERSearchWin;		// autowired
	private Textbox firstname;		// autowired
	private Textbox middlename;		// autowired
	private Textbox lastname;		// autowired
	private Textbox birthdate;		// autowired - search for birthdate
	private Textbox ssn;			// autowired - search for patient SSN
	private Checkbox agree;			// autowired
	
	private	Rec ptRec = null;
	
	

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
	}
	
	
	
	public void onClick$searchBtn( Event event ){
		
		
		// make sure the 'I Agree' checkbox is checked
		if ( agree.isChecked() != true ){
			try { Messagebox.show( "You must read and agree to the Disclaimer above first." ); } catch (Exception e) { ; }
			return;
		}
		

		
		// make sure at least some part of the name was entered
		if (( lastname.getValue().trim().length() == 0 ) && ( firstname.getValue().trim().length() == 0 ) && ( middlename.getValue().trim().length() == 0 )){
			try { Messagebox.show( "No patient name entered.", "Patient Search", Messagebox.OK, null );	} catch (Exception e) { /* ignore */ }
			return;
		}


		// make sure either a SSN or DOB was entered
		if (( birthdate.getValue().trim().length() == 0 ) && ( ssn.getValue().trim().length() == 0 )){
			try { Messagebox.show( "You must also enter either a birthdate or a Social Security Number." ); } catch (Exception e) { ; }
			return;
		}
		
		
		// Call PtFinder to do the search
		Name name = new Name( firstname.getValue(), middlename.getValue(), lastname.getValue(), "" );
		PtFinder ptFinder = new PtFinder();
		int fnd = ptFinder.doSearchName( name, 0 /*searchFlags*/ );
		System.out.println( "back from ptFinder.doSearch(), fnd = " + fnd );
		

		boolean match = false;

		// Patient not found
		if ( fnd > 0 ){

			// get pt found list
			Vector<PtFoundList> ptList = ptFinder.getPtList();
	
		
			// Look through found patients for one with birthdate or SSN
			for ( int cnt = 0; cnt < ptList.size(); ++cnt ){
					
				PtFoundList ptFnd = (PtFoundList) ptList.get( cnt );
				
				// verify matching birthdate
				if ( birthdate.getValue().trim().length() > 0 ){				
					System.out.println( "verifying matching birthdate" );
					if ( ptFnd.getBirthdate().compare( new usrlib.Date( birthdate.getValue().trim())) == 0 ) match = true;
				}
				
				// verify matching SSN
				if ( ssn.getValue().trim().length() > 0 ){				
					System.out.println( "verifying matching SSN" );
					if ( ptFnd.getSSN().equals( ssn.getValue().trim()) ) match = true;
				}
				
				if ( match == true ){
					ptRec = ptFnd.getPtRec();
					break;
				}
			}
		}
		
		if ( ! match ){
			
			// patient not found message
			try { Messagebox.show( "Patient not found.  Please update the info and try again." ); } catch (Exception e) { ; }
			return;
		}
		
			
		// open ER Viewer window
		ERSearchWin.detach();
		ERView.show( ptRec, null );
	
		return;
	}


}
