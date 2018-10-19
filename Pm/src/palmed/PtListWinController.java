package palmed;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class PtListWinController extends GenericForwardComposer {

	private Listbox lbox;		// autowired
	private Window ptListWin;			// autowired
	private Textbox srcstr;				// autowired
	
	Radio rbAgeLess;
	Radio rbAgeGreater;
	Textbox txtAge;
	
	Radio rbSexMale;
	Radio rbSexFemale;
	Radio rbSexNone;

	Tab tabDemo;
	Tab tabProb;
	Tab tabMed;
	Tab tabAllergy;
	Tab tabLab;
	
	Textbox txtSearchProb;
	Textbox txtSearchMed;
	Textbox txtSearchLab;
	Textbox txtSearchAllergy;
	
	Listbox lboxProb;
	Listbox lboxMed;
	Listbox lboxLab;
	Listbox lboxAllergy;
	Label lblProb;
	Label lblMed;
	Label lblLab;
	Label lblAllergy;
	
	Textbox txtResult;
	Radio rbResultGreater;
	Radio rbResultLess;
	Radio rbResultAny;
	Radio rbProbInc;
	Radio rbProbDNI;
	Radio rbMedInc;
	Radio rbMedDNI;
	Radio rbAllergyInc;
	Radio rbAllergyDNI;
	
	
	Button btnSearch;
	Button btnDrill;
	Button btnReset;
	
	Listhead listHead;
	
	
	
	Rec probRec;
	String probStr = "";
	Rec labRec;
	String labStr = "";
	int parID;
	String parStr = "";
	String medID;
	String medStr = "";;
	Listheader lhdr4;
	
	int level = 0;
	List colList = null;
	
	double thisResult;
	

	
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
		
		rbSexNone.setChecked( true );
		rbAgeGreater.setChecked( true );
		
		// populate listbox
		//refreshList();
		
		return;
	}
	
	
	
	
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){ onClick$search( null ); }
	
	public void refreshList( String searchString ){

		// which kind of list to display (from radio buttons)
		int display = 1;
		
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		ProbTbl probTbl = ProbTbl.open();
		
		while ( probTbl.getNext()){
			
			int fnd = 1;
			
			// get problem status byte
			ProbTbl.Status status = probTbl.getStatus();
			// is this type selected?
			if ((( status == ProbTbl.Status.ACTIVE ) && (( display & 1 ) != 0 ))
				|| (( status == ProbTbl.Status.INACTIVE ) && (( display & 2 ) != 0 ))){
				// ( status == probTbl.Status.REMOVED ) - removed
				// ( status == probTbl.SUPERCEDED ) - superceded by edit
				

				if (( searchString != null )
					&& (( probTbl.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( probTbl.getDesc().toUpperCase().indexOf( s ) < 0 )
					&& ( probTbl.getSNOMED().toUpperCase().indexOf( s ) < 0 )
					&& ( probTbl.getICD9().toUpperCase().indexOf( s ) < 0 )
					&& ( probTbl.getICD10().toUpperCase().indexOf( s ) < 0 )
					&& ( probTbl.getCode4().toUpperCase().indexOf( s ) < 0 ))
					){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( lbox );
					i.setValue( probTbl.getRec());
					
					new Listcell( probTbl.getAbbr()).setParent( i );
					new Listcell( probTbl.getDesc()).setParent( i );
					new Listcell( probTbl.getSNOMED()).setParent( i );
					new Listcell( probTbl.getICD9()).setParent( i );
					new Listcell( probTbl.getICD10()).setParent( i );
					new Listcell( probTbl.getCode4()).setParent( i );
					new Listcell( /*Dgn.read( prob.getDgnRec()).getCode()*/ "V101.5" ).setParent( i );;
				}
			}
		}
		
		probTbl.close();
		

		return;
	}

	
	public void onOK$srcstr( Event ev ){ onClick$search( ev ); }
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$search( Event ev){

		String s = srcstr.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "Please enter at least three letters in search field." );
			return;
		}
		refreshList(( s.length() < 1 ) ? null: s );
		return;
	}
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$newprob( Event ev ){
		
//		if ( ProbTblEdit.enter( probTblWin )){
//			refreshList();
//		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem table entry
	
	public void onClick$btnSearch( Event ev ){

		
		//ZkTools.listboxClear( lbox );
	
		// is an item selected?
		if ( tabDemo.isSelected()){
			
		} else if ( tabProb.isSelected()){
			if ( ! Rec.isValid( probRec )){
				DialogHelpers.Messagebox( "You need to select a problem first." );
				return;
			}
		} if ( tabMed.isSelected()){
			if (( medID == null ) || ( medID.length() < 1 )){
				DialogHelpers.Messagebox( "You need to select a medication first." );
				return;
			}
		} if ( tabLab.isSelected()){
			if ( ! Rec.isValid( labRec )){
				DialogHelpers.Messagebox( "You need to select a lab test first." );
				return;
			}
		} if ( tabAllergy.isSelected()){
			if ( parID <= 0 ){
				DialogHelpers.Messagebox( "You need to select an allergy first." );
				return;
			}
		}
		
		
		
	
		if ( level == 0 ){
			btnSearch.setDisabled( true );
			btnDrill.setDisabled( false );
			btnReset.setDisabled( false );
		}
		
	
			
		// do initial search
		if ( level == 0 ){
		
			if ( tabDemo.isSelected()){
				
			} else if ( tabProb.isSelected()){
				new Listheader( "Prob" ).setParent( listHead );
			} if ( tabMed.isSelected()){
				new Listheader( "Med" ).setParent( listHead );
			} if ( tabLab.isSelected()){
				new Listheader( labStr ).setParent( listHead );
			} if ( tabAllergy.isSelected()){
				new Listheader( "Allergy" ).setParent( listHead );
			}
			
			
		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( 1 ));	//TEMP - change to limit lists for testing
		
		while( dirPt.getNext()){
			

			Rec ptRec = dirPt.getRec();
			
			if ( ! doLogic( ptRec, dirPt )) continue;
			
			
			// include this entry
			Listitem i = new Listitem();
			i.setValue( new Rec( dirPt.getRec().getRec()));
			i.setParent( lbox );
			
			new Listcell( dirPt.getName().getPrintableNameLFM()).setParent( i );
			new Listcell( Date.getPrintableAge( dirPt.getBirthdate())).setParent( i );
			new Listcell( dirPt.getSex().getAbbr()).setParent( i );
			
			if ( tabDemo.isSelected()){
				
			} else if ( tabProb.isSelected()){
				new Listcell( probStr ).setParent( i );
			} if ( tabMed.isSelected()){
				new Listcell( medStr ).setParent( i );
			} if ( tabLab.isSelected()){
				new Listcell( String.format( "%.1f", thisResult )).setParent( i );
			} if ( tabAllergy.isSelected()){
				new Listcell( parStr ).setParent( i );
			}
					
		}
		
		dirPt.close();
		
		
		
		
		} else {
			
			Listheader lh = null;
		
			if ( tabDemo.isSelected()){
				
			} else if ( tabProb.isSelected()){
				lh = new Listheader( "Prob" );

			} else if ( tabMed.isSelected()){
				lh = new Listheader( "Med" );

			} else if ( tabAllergy.isSelected()){
				lh = new Listheader( "Allergy " + level );

			} else if ( tabLab.isSelected()){
				lh = new Listheader( labStr );
			}
			if ( lh != null ){
				lh.setWidth( "50px" );
				lh.setParent( listHead );
				lh.setSort( "auto" );
			}

			
			for ( int i = lbox.getItemCount(); i > 0; ){
				Rec ptRec = (Rec) lbox.getItemAtIndex( --i ).getValue();
			
				DirPt dirPt = new DirPt( ptRec );
				
				if ( ! doLogic( ptRec, dirPt )){
					lbox.removeItemAt( i );
					continue;
				}
				
				if ( tabDemo.isSelected()){
					
				} else if ( tabProb.isSelected()){
					new Listcell( probStr ).setParent( lbox.getItemAtIndex(i));
				} if ( tabMed.isSelected()){
					new Listcell( medStr ).setParent( lbox.getItemAtIndex(i));
				} if ( tabLab.isSelected()){
					new Listcell( String.format( "%.1f", thisResult )).setParent( lbox.getItemAtIndex(i));
				} if ( tabAllergy.isSelected()){
					new Listcell( parStr ).setParent( lbox.getItemAtIndex(i));
				}
				
				
			}
		}
		
		
		++level;
	
		return;
	}
	
	
	
	
	
	
	
	boolean doLogic( Rec ptRec, DirPt dirPt ){
	
		boolean flgFound = false;
		
		
		
		
		if ( tabDemo.isSelected()){
			
			palmed.Sex sex = null;
			int age = 0;
			boolean greater = true;

			sex = null;
			if ( rbSexMale.isChecked()) sex = palmed.Sex.MALE;
			if ( rbSexFemale.isChecked()) sex = palmed.Sex.FEMALE;
			
			age = EditHelpers.parseInt( txtAge.getValue());
			greater = ( rbAgeGreater.isChecked()) ? true: false;
			
			flgFound = true;
			
			if (( sex != null ) && ( dirPt.getSex() != sex )) flgFound = false;
			if ( age > 0 ){
				int ptage = Date.getAgeYears( dirPt.getBirthdate());
				if ( greater && ( ptage < age )) flgFound = false;
				if ( ! greater && ( ptage >= age )) flgFound = false;
			}
			
		}
		
		
		
		
		
		if ( tabProb.isSelected()){
			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			for ( Reca reca = medpt.getProbReca(); Reca.isValid( reca ); ){
				Prob p = new Prob( reca );
				if (( rbProbInc.isChecked() && ( probRec.equals( p.getProbTblRec()))
						|| ( rbProbDNI.isChecked() && ( ! probRec.equals( p.getProbTblRec()))))){
					flgFound = true;
					break;
				}
				reca = p.getLLHdr().getLast();
			}
		}
	
		
		
		
		if ( tabMed.isSelected()){
			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			for ( Reca reca = medpt.getMedsReca(); Reca.isValid( reca ); ){
				Cmed p = new Cmed( reca );
				if (( rbMedInc.isChecked() && ( medID.equals( p.getDrugCode()))
						|| ( rbMedDNI.isChecked() && ( ! medID.equals( p.getDrugCode()))))){
					flgFound = true;
					break;
				}
				reca = p.getLLHdr().getLast();
			}
		}
		
		
		
		
		if ( tabLab.isSelected()){
			
			double result = EditHelpers.parseDouble( txtResult.getValue());

			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			for ( Reca reca = medpt.getLabResultReca(); Reca.isValid( reca ); ){
				LabResult p = new LabResult( reca );
				thisResult = EditHelpers.parseDouble( p.getResult());
				if ( labRec.equals( p.getLabObsRec())){
					//TODO - within date range?
					if (( rbResultGreater.isChecked() && ( thisResult >= result ))
						|| ( rbResultLess.isChecked() && ( thisResult < result ))
						|| ( rbResultAny.isChecked())){
						flgFound = true;
						break;							
					}
				}
				reca = p.getLLHdr().getLast();
			}
		}
		
		
		
		
		if ( tabAllergy.isSelected()){
			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			for ( Reca reca = medpt.getParsReca(); Reca.isValid( reca ); ){
				Par p = new Par( reca );
				if (( rbAllergyInc.isChecked() && ( parID == p.getCompositeID()))
					|| rbAllergyDNI.isChecked() && ( parID != p.getCompositeID())){
					flgFound = true;
					break;
				}
				reca = p.getLLHdr().getLast();
			}
		}
		
		return flgFound;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchProb( Event ev ){

		String s = txtSearchProb.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least 3 chars." );
			return;
		}
		
		refreshProbList( s );
		return;
	}
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchMed( Event ev ){

		String s = txtSearchMed.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least 3 chars." );
			return;
		}
		
		refreshMedList( s );
		return;
	}
	
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchLab( Event ev ){

		String s = txtSearchLab.getValue().trim();
		if ( s.length() < 1 ){
			DialogHelpers.Messagebox( "You must enter at least 1 chars." );
			return;
		}
		
		refreshLabList( s );
		return;
	}
	
	
	// Search for matching entries in corresponding listbox	
	public void onClick$btnSearchAllergy( Event ev ){

		String s = txtSearchAllergy.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "You must enter at least 3 chars." );
			return;
		}
		
		refreshAllergyList( s );
		return;
	}
	
	

	
	
	
	
	
	
	
	
	

	// Select a an entry from the search tables	
	public void onClick$btnSelectProb( Event ev ){

		// make sure an item was selected
		if ( lboxProb.getSelectedCount() < 1 ) return;
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxProb );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxProb );
		lblProb.setValue( str );
		probRec = rec;
		probStr= str;
		return;
	}

	// Select a an entry from the search tables	
	public void onClick$btnSelectMed( Event ev ){

		// make sure an item was selected
		if ( lboxMed.getSelectedCount() < 1 ) return;
		String id = (String) ZkTools.getListboxSelectionValue( lboxMed );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxMed );
		lblMed.setValue( str );
		medID = id;
		medStr = str;
		return;
	}
	// Select a an entry from the search tables	
	public void onClick$btnSelectLab( Event ev ){

		// make sure an item was selected
		if ( lboxLab.getSelectedCount() < 1 ) return;
		Rec rec = (Rec) ZkTools.getListboxSelectionValue( lboxLab );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxLab );
		lblLab.setValue( str );
		labRec = rec;
		labStr = str;
		return;
	}
	// Select a an entry from the search tables	
	public void onClick$btnSelectAllergy( Event ev ){

		// make sure an item was selected
		if ( lboxAllergy.getSelectedCount() < 1 ) return;
		String id = (String) ZkTools.getListboxSelectionValue( lboxAllergy );
		String str = (String) ZkTools.getListboxSelectionLabel( lboxAllergy );
		lblAllergy.setValue( str );
		parID = EditHelpers.parseInt( id );
		parStr = str;
		return;
	}


	
	
	
	
	
	
	
	
	
	
	
	public void refreshLabList( String searchString ){

		if ( lboxLab == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxLab );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();		
		
		// populate list
		//LabObsTbl.fillListbox( lboxLab, null, searchString, searchString, LabObsTbl.Status.ACTIVE );
		LabObsTbl.fillListbox( lboxLab, false);
		return;
	}

	
	
	

    
	public void refreshProbList( String searchString ){

		if ( lboxProb == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxProb );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		ProbTbl.fillListbox( lboxProb, searchString );
		return;
	}




	public void refreshMedList( String searchString ){

		if ( lboxMed == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxMed );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		//LabBat.fillListbox( lboxBat, null, null, searchString, LabBat.Status.ACTIVE );
		NCFull.search( lboxMed, searchString );
		return;
	}



	public void refreshAllergyList( String searchString ){

		if ( lboxAllergy == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxAllergy );
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list
		//LabBat.fillListbox( lboxBat, null, null, searchString, LabBat.Status.ACTIVE );
		NCAllergies.search( lboxAllergy, searchString );
		return;
	}




	public void onClick$btnReset( Event ev ){
		

		ZkTools.listboxClear( lbox );
		level = 0;
		colList = null;
		
		btnSearch.setDisabled( false );
		btnDrill.setDisabled( true );
		btnReset.setDisabled( true );
		
		List list = listHead.getChildren();
		for ( int i = list.size(); i > 0; ){
			Listheader lh = ((Listheader) list.get(--i));
			if ( lh.getColumnIndex() >= 3 ) lh.detach();
		}
		
		return;
	}
	
	
	public void onClick$btnDrill( Event ev ){
		
		onClick$btnSearch( ev );
		
		return;
	}
	
	
	
	public void onClick$btnPrint( Event ev ){
		Clients.print();
		return;
	}

	
}
