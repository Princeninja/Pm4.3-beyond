package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

public class PpoSrch {

	private int ppoRec = 0;
	
	private Component parent = null;	// parent Component (calling window)
	private Window ppoSrchWin;			// search window (created window)
	

	/**
	 * The Call method.
	 */
	public static int show() {
		return new PpoSrch().getPpoRec();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private PpoSrch() {
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

		ppoSrchWin = (Window) Executions.createComponents("pposrch.zul", this.parent, null);
		//System.out.println( "back from creating components ptsearch.zul");
		
		ppoSrchWin.addEventListener( "onClose", new EventListener() {
			
			public void onEvent(Event event) throws Exception {
				//System.out.println( "createBox() - in ptSearchWin() onClose EventListener");
				ppoRec = ((Intbox)(ppoSrchWin.getFellow( "ppoRec" ))).getValue();
				ppoSrchWin.onClose();
			}
		});



		try {
			ppoSrchWin.doModal();
			ppoSrchWin.setPosition("center");
		} catch (SuspendNotAllowedException e) {
	        System.out.println( "SuspendNotAllowedException" );
		} catch (InterruptedException e) {
	        System.out.println( "InterruptedException" );
		}

		
		// Get pporec from invisible intbox in ppoSrchWin
		this.ppoRec = (((Intbox) (ppoSrchWin.getFellow( "ppoRec", false ))).getValue());
		
	}


	public int getPpoRec() {
		return ppoRec;
	}
	
	
}
