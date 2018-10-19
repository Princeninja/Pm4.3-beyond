package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;






	/**
	 * This class creates a modal window as a dialog in which the user <br>
	 * can input some text. By onClosing with <RETURN> or Button <send> this
	 * InputConfirmBox can return the message as a String value if not empty. <br>
	 * In this case the returnValue is the same as the inputValue.<br>
	 *
	 * @author bbruhns
	 * @author sgerth
	 */
	public class PtSearch {

		private int ptRec = 0;
		
		private Component parent = null;	// parent Component (calling window)
		private Window ptSearchWin;			// search window (created window)
		

		/**
		 * The Call method.
		 */
		public static int show() {
			return new PtSearch().getPtRec();
		}

		/**
		 * private constructor. So it can only be created with the static show()
		 * method.
		 */
		private PtSearch() {
			super();

//			textbox = new Textbox();

//			setParent(parent);

//			try {
//				userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			createBox();
		}

		private void createBox() {

			ptSearchWin = (Window) Executions.createComponents("ptsearch.zul", this.parent, null);
			//System.out.println( "back from creating components ptsearch.zul");
			
			ptSearchWin.addEventListener( "onClose", new EventListener() {
				
				public void onEvent(Event event) throws Exception {
					//System.out.println( "createBox() - in ptSearchWin() onClose EventListener");
					ptRec = ((Intbox)(ptSearchWin.getFellow( "ptrec" ))).getValue();
					ptSearchWin.onClose();
				}
			});



			try {
				ptSearchWin.doModal();
			} catch (SuspendNotAllowedException e) {
		        System.out.println( "SuspendNotAllowedException" );
			} catch (InterruptedException e) {
		        System.out.println( "InterruptedException" );
			}

			
			// Get ptrec from invisible intbox in ptSearchWin
			this.ptRec = (((Intbox) (ptSearchWin.getFellow( "ptrec", false ))).getValue());
			//System.out.println( "ptrec=" + ptRec );

		}
/*
		final class OnCloseListener implements EventListener {
//			@Override
			public void onEvent(Event event) throws Exception {
				ptSearchWin.onClose();
			}
		}
*/

		public int getPtRec() {
			return ptRec;
		}
	}

