package palmed;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import usrlib.Rec;



public class ProvSrcWinController extends GenericForwardComposer {
	
	private Window provSrcWin;		// autowired
	private Listbox provListbox;	// autowired
	private	Intbox provRec;			// autowired - invisible component used to pass provRec back to caller
	private Button exitBtn;			// autowired - exit button
	private Button selectBtn;		// autowired - select button
	
	
	public void doAfterCompose( Component comp ){
		try {
			super.doAfterCompose(comp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set initial value of provRec intbox to 0
		provRec.setValue(0);
		
				// fill listbox with providers
		Prov.fillListbox( provListbox, false );		
		return;
	}

	
	/**
	 *  Handle 'Exit' button
	 * @param event
	 */
	public void onClick$exitBtn( Event event ){
		
		provSrcWin.detach();
	}

	
	
	/**
	 *  Handle 'Select' button
	 * @param event
	 * 
	 */
	public void onClick$selectBtn( Event event ){
				
		// Handle no selection made
		if ( provListbox.getSelectedCount() < 1 ) {
			try { Messagebox.show( "No provider selected.  Please make a selection and press 'Select' again.", "Provider Select", Messagebox.OK, null ); }
				catch ( Exception e ) { /* ignore */ }			
			System.out.println( "no item selected");
			return;
		}
		
		// get rec number from listbox selection
		int rec = ((Rec) provListbox.getSelectedItem().getValue()).getRec();
		provRec.setValue( rec );
		System.out.println( "ProvSrcWinController().onClick$selectBtn --> rec=" + rec + "  selectedItem=" + provListbox.getSelectedItem().getLabel() );
		
		// close this window
		provSrcWin.detach();
		return;
	}

}

/**/
