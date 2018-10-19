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

public class CDSRulesWinController extends GenericForwardComposer {

	private Listbox lboxRules;			// autowired
	private Radio r_active;				// autowired
	private Radio r_inactive;			// autowired
	private Radio r_all;				// autowired
	private Window cdsRulesWin;			// autowired
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
		//refreshList();
	}
	public void onCheck$r_inactive( Event ev ){
		//refreshList();
	}
	public void onCheck$r_all( Event ev ){
		//refreshList();
	}
	
	
	
	
	
	// Refresh listbox when needed
	
	public void refreshList(){

		// which kind of list to display (from radio buttons)
		int display = 1;
		if ( r_active.isSelected()) display = 1;
		if ( r_inactive.isSelected()) display = 2;
		if ( r_all.isSelected()) display = 3;
		
		if ( lboxRules == null ) return;
		
		// remove all items
		ZkTools.listboxClear( lboxRules );
		
		
		
		if ( ! r_inactive.isSelected()){
			
			// populate list
			Listitem i = new Listitem();
			i.setParent( lboxRules );
			i.setValue( new Rec( 2 ));
			
			new Listcell( "Diabetics without a HgbA1c in last 3 months, suggest HgbA1c" ).setParent( i );
			new Listcell( "Active" ).setParent( i );
			
			i = new Listitem();
			i.setParent( lboxRules );
			i.setValue( new Rec( 3 ));
			new Listcell( "Diabetics with HgbA1c > 9.0, suggest diabetic teaching" ).setParent( i );
			new Listcell( "Active" ).setParent( i );
			
			i = new Listitem();
			i.setParent( lboxRules );
			i.setValue( new Rec( 4 ));
			new Listcell( "LDL > 130 and not on a statin, suggest adding a statin" ).setParent( i );
			new Listcell( "Active" ).setParent( i );
			
			i = new Listitem();
			i.setParent( lboxRules );
			i.setValue( new Rec( 5 ));
			new Listcell( "BMI > 30, consider nutritional counselling" ).setParent( i );
			new Listcell( "Active" ).setParent( i );
		
		}
		
		

		return;
	}

	
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$create( Event ev ){
		
		if ( CDSRulesEdit.enter( cdsRulesWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing problem table entry
	
	public void onClick$edit( Event ev ){
	
		// was an item selected?
		if ( lboxRules.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (Exception e) { /*ignore*/ }
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) lboxRules.getSelectedItem().getValue();
		if ( ! Rec.isValid( rec )) return;

		// call edit routine
		if ( CDSRulesEdit.edit( rec, cdsRulesWin )){
			refreshList();
		}		
		return;
	}
	
	
	
	

}
