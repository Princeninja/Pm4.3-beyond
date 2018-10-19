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

public class ProbTblWinController extends GenericForwardComposer {

	
	private Listbox probTblListbox;		// autowired - problem table listbox
	private Radio r_active;				// autowired
	private Radio r_inactive;			// autowired
	private Radio r_all;				// autowired
	private Window probTblWin;			// autowired - problem table window (this window)
	private Textbox srcstr;				// autowired
	
	
	

	public ProbTblWinController() {
		// TODO Auto-generated constructor stub
	}

	public ProbTblWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public ProbTblWinController(char separator, boolean ignoreZScript,
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
		
		srcstr.setFocus(true);
		// Get arguments from map
		//Execution exec = Executions.getCurrent();

		//if ( exec != null ){
		//	Map myMap = exec.getArg();
		//	if ( myMap != null ){
		//		try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
		//	}
		//}		
		
		
		
		// populate listbox
		//refreshList();
		
		return;
	}
	
	
	
	
	// Watch for radiobutton to change
	public void onCheck$r_active( Event ev ){
		//refreshList();
	}
	public void onCheck$r_inactive( Event ev ){
		//refreshList();
	}
	public void onCheck$r_all( Event ev ){
		//refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	public void refreshList(){ onClick$search( null ); }
	
	public void refreshList( String searchString ){

		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_active.isSelected()) display = 1;
		if ( r_inactive.isSelected()) display = 2;
		if ( r_all.isSelected()) display = 3;
		
		if ( probTblListbox == null ) return;
		
		// remove all items
		for ( int i = probTblListbox.getItemCount(); i > 0; --i ){
			probTblListbox.removeItemAt( 0 );
		}
		
		
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
					(i = new Listitem()).setParent( probTblListbox );
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
		
		if ( ProbTblEdit.enter( probTblWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem table entry
	
	public void onClick$edit( Event ev ){
	
		// was an item selected?
		if ( probTblListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) probTblListbox.getSelectedItem().getValue();
		System.out.println("rec: "+rec);
		System.out.println(probTblListbox.getSelectedIndex());
		System.out.println(probTblListbox.getSelectedItem().getLabel());
		System.out.println(probTblListbox.getSelectedItem().getValue());
		if (( rec == null ) || ( rec.getRec() < 2 )) return;

		// call edit routine
		if ( ProbTblEdit.edit( rec, probTblWin )){
			System.out.println("rec:* "+rec);
			refreshList();
		}		
		return;
	}
	
	
	
	
	// Import from SNOMED file

/*	public void onClick$btnImport( Event ev ){
		ProbTblImport.show( probTblWin.getParent() );
		return;
	}
*/	
	
	// Import from CVX flat file

	public void onClick$btnImport( Event ev ){
		
		Media media = null;
		
        try {
			media = Fileupload.get( "Select the SNOMED Core  file to import.", "Import CVX" );
		} catch (InterruptedException e) {
			DialogHelpers.Messagebox( "Error loading SNOMED file." );
			return;
		}

	
	
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading SNOMED file." );
			return;
		}
		
		if ( ! media.getContentType().equalsIgnoreCase( "text/plain" )){
			DialogHelpers.Messagebox( "SNOMED file wrong content type:" + media.getContentType());
			return;
		}
		
			
		// in memory vs string
		BufferedReader br = null;
		Reader r = null;
		
		if ( media.inMemory()){
		
			// convert to string data
			String text = media.getStringData();
			r = new StringReader( text );
			br = new BufferedReader( r );
			
		} else {
			r = media.getReaderData();
			br = new BufferedReader( r );
		}
		
		
		if ( ProbTblImport.importSnomed( br ) > 0 ){
			DialogHelpers.Messagebox( "SNOMED Core Concepts Import done." );
			refreshList();
		}
		
		try {
			r.close();
			br.close();
		} catch (IOException e) {
			// ignore
		}
		
		return;
	}
	


	

}
