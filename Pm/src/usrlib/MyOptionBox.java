
package usrlib;


import org.zkoss.zk.ui.event.EventListener; 
import org.zkoss.zul.Messagebox;   
/**  * Extended messagebox that can show multi-lined messages.
 *   * Lines can be breaked with the \n .
 */             


public class MyOptionBox extends Messagebox {
	
	private static final long serialVersionUID = 1L;
       
	// path of the messagebox zul-template
	//private static String _templ = "/WEB-INF/pages/util/multiLineMessageBox.zul";
	private static String _templ = "/msgxxxx.zul";

	public MyOptionBox(){
	}
	
	public static void doSetTemplate(){
		setTemplate(_templ);
	}
	
	
	
	public static int show( String message, String title, String option1, String option2, String option3, String icon ){
		
		return 0;
	}
	
	
	/**      
	 * Shows a message box and returns what button is pressed. A shortcut to
	 * show(message, null, OK, INFORMATION).
	 * 
	 * Simple MessageBox with customizable message and title.
	 * 
	 * @param message
	 *        The message to display.
	 * @param title      
	 *        The title to display.      
	 * @param icon      
	 *        The icon to display.
	 *        QUESTION = "z-msgbox z-msgbox-question";
	 *        EXCLAMATION = "z-msgbox z-msgbox-exclamation";
	 *        INFORMATION = "z-msgbox z-msgbox-imformation";
	 *        ERROR = "z-msgbox z-msgbox-error";
	 * @param buttons
	 *        MultiLineMessageBox.CANCEL
	 *        MultiLineMessageBox.YES
	 *        MultiLineMessageBox.NO
	 *        MultiLineMessageBox.ABORT
	 *        MultiLineMessageBox.RETRY
	 *        MultiLineMessageBox.IGNORE
	 * @param padding
	 *        true = Added an empty line before and after the message.
	 *        
	 *       
	 * @return
	 * @throws InterruptedException
	 */
	
	public static final int show(String message, String title, int buttons, String icon, boolean padding) throws InterruptedException {
		String msg = message;
		
		if (padding) {
			msg = "\n" + message + "\n\n";
		}
		
		if (icon.equals("QUESTION")) {
			icon = "z-msgbox z-msgbox-question";
			
		} else if (icon.equals("EXCLAMATION")) {
			icon = "z-msgbox z-msgbox-exclamation";
			
		} else if (icon.equals("INFORMATION")) {
			icon = "z-msgbox z-msgbox-imformation";
		} else if (icon.equals("ERROR")) {
			icon = "z-msgbox z-msgbox-error";
		}
		
		return show(msg, title, buttons, icon, 0, null);
	}
	
	
	/**
	 * Shows a message box and returns what button is pressed. A shortcut to
	 * show(message, null, OK, INFORMATION).
	 * 
	 * Simple MessageBox with customizable message and title.
	 * 
	 * @param message
	 * 		The message to display.
	 * @param title 
	 *      The title to display.
	 * @param icon
	 *      The icon to display.
	 *      QUESTION = "z-msgbox z-msgbox-question";
	 *      EXCLAMATION = "z-msgbox z-msgbox-exclamation";
	 *      INFORMATION = "z-msgbox z-msgbox-imformation";
	 *      ERROR = "z-msgbox z-msgbox-error";
	 * @param buttons
	 *      MultiLineMessageBox.CANCEL
	 *      MultiLineMessageBox.YES
	 *      MultiLineMessageBox.NO
	 *      MultiLineMessageBox.ABORT
	 *      MultiLineMessageBox.RETRY
	 *      MultiLineMessageBox.IGNORE
	 * @param padding
	 *      true = Added an empty line before and after the message.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static final int show(String message, String title, int buttons, String icon, boolean padding, EventListener listener)
		throws InterruptedException {
		
		String msg = message;
		
		if (padding) {
			msg = "\n" + message + "\n\n";
		}
		
		if (icon.equals("QUESTION")) {
			icon = "z-msgbox z-msgbox-question";
			
		} else if (icon.equals("EXCLAMATION")) {
			icon = "z-msgbox z-msgbox-exclamation";
			
		} else if (icon.equals("INFORMATION")) {
			icon = "z-msgbox z-msgbox-imformation";
			
		} else if (icon.equals("ERROR")) {
			icon = "z-msgbox z-msgbox-error";
		}
		
		return show(msg, title, buttons, icon, 0, listener);
	}
}


/**/
