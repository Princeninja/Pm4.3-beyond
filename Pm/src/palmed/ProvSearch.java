package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import usrlib.Rec;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can input some text. By onClosing with <RETURN> or Button <send> this
 * InputConfirmBox can return the message as a String value if not empty. <br>
 * In this case the returnValue is the same as the inputValue.<br>
 *
 * @author jrpalen
 */

public class ProvSearch {
	
	private Rec provRec = null;
	
	private Component parent = null;	// parent Component (calling window)
	private Window searchWin;			// search window (created window)
	

	/**
	 * The Call method.
	 */
	public static Rec show() {
		return new ProvSearch().getProvRec();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private ProvSearch() {
		super();

//		textbox = new Textbox();

//		setParent(parent);

//		try {
//			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		createBox();
	}

	private void createBox() {

		searchWin = (Window) Executions.createComponents("provsrc.zul", this.parent, null);
		
		searchWin.addEventListener( "onClose", new EventListener() {
			
			public void onEvent(Event event) throws Exception {
				System.out.println( "createBox() - in ptSearchWin() onClose EventListener");
				provRec = new Rec(((Intbox)(searchWin.getFellow( "provRec" ))).getValue());
				searchWin.onClose();
			}
		});



		try {
			searchWin.doModal();
		} catch (SuspendNotAllowedException e) {
	        System.out.println( "SuspendNotAllowedException" );
		} catch (Exception e) {
	        System.out.println( "Exception" );
		}

		
		// Get ptrec from invisible intbox in ptSearchWin
		this.provRec = new Rec(((Intbox) (searchWin.getFellow( "provRec", false ))).getValue());
		System.out.println( "provRec=" + provRec.getRec());

	}
/*
	final class OnCloseListener implements EventListener {
//		@Override
		public void onEvent(Event event) throws Exception {
			ptSearchWin.onClose();
		}
	}
*/

	public Rec getProvRec() {
		return provRec;
	}

}
