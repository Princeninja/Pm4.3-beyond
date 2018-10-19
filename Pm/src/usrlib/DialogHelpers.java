package usrlib;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * DialogHelpers - A class to provide some easy to use, common dialogs.
 * 
 * @author Owner
 *
 */
public class DialogHelpers {

	private String reply = null;
	private int result = 0;
	private Window win;
	
	
	/**
	 * Messagebox()
	 * 
	 * (Really just another interface to ZK's Messagebox.show() so all my
	 * code doesn't have to catch this exception.)
	 * 
	 * @param s
	 */
	public static void Messagebox( String s ){
		try {
			org.zkoss.zul.Messagebox.show( s );
		} catch ( InterruptedException e ){
			/* ignore */
			//TODO - handle exception
		}
	}
	
	
	
	
	/**
	 * Textbox()
	 * 
	 * - if cancel is pressed, returns a null string
	 * 
	 * @param title
	 * @param promptMsg
	 * @return
	 */
	
	public static String Textbox( String title, String promptMsg ){
		
		DialogHelpers dlg = new DialogHelpers();
		return dlg.createTextbox( title, promptMsg );
	}

	public String createTextbox( String title, String prompt ){
		
		String newZUL =
			"<window id='newWin' title='Prompt' border='normal' width='400px' height='200px' >" +
			"<vbox vflex='1' hflex='1' >" +
			"<label id='lbl' />" +
			"<textbox id='txt' maxlength='40' />" +
			"<hbox>" + 
			"<button id='return' label='OK' width='80px' />" +
			"<button id='cancel' label='Cancel' width='80px' />" +
			"</hbox>" +
			"</vbox>" +
			"</window>";
		
		//"style='width:1000px; height:600px;border:3px inset;'"		
		//onClick='newWin.close();' 
			
		reply = "";
		
		// create newcrop window and iframe
		win = (Window) Executions.createComponentsDirectly( newZUL, null, null /*medsWin.getParent()*/, null );
		//newWin.setHeight( "450px" );
		if ( title != null ) win.setTitle( title );
		if ( prompt != null ) ((Label) win.getFellow( "lbl" )).setValue( prompt );
		//newWin.setVflex( "true" );
		
		// add Event Listener to handle return button
		Button b1 = (Button) win.getFellow("return");		
		Button b2 = (Button) win.getFellow("cancel");		
		b1.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$return( ev ); }});
		b2.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$cancel( ev ); }});
		
		Events.postEvent( "onSize", win, null );
		
		try {
			win.doModal();
		} catch (SuspendNotAllowedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return reply;
	}
	
	public void onClick$return( Event ev ){
		Textbox txt = (Textbox) win.getFellow( "txt" );
		reply = txt.getValue().trim();
		win.detach();
		
	}

	
	public void onClick$cancel( Event ev ){
		reply = null;
		win.detach();
		
	}

	
	
	
	
	
	public static int Optionbox( String title, String promptMsg, String opt1, String opt2, String opt3 ){
		
		DialogHelpers dlg = new DialogHelpers();
		return dlg.createOptionbox( title, promptMsg, opt1, opt2, opt3 );
	}


	public int createOptionbox( String title, String promptMsg, String opt1, String opt2, String opt3 ){
		
		String newZUL =
			"<window  title='Prompt' border='normal' width='400px' height='200px' >" +
			"<borderlayout>" +
			"<center >" +
			"<vbox align='left' pack='center' vflex='1' hflex='1' >" +
			"<hbox><div class='z-msgbox z-msgbox-question' /><space /><label id='lbl' /></hbox>" +
			"<separator />" +
			"</vbox>" +
			"</center >" +
			"<south >" +
			"<hbox id='hbox' hflex='1' align='center' pack='center' ></hbox>" +
			"</south>" +
			"</borderlayout>" +
			"</window>";
		
/*		"<window id='newWin' title='Prompt' border='normal' width='400px' height='200px' >" +
		"<north>" +
	
		//"<vbox vflex='1' hflex='1' align='center' pack='center' >" +
		"<hbox><label id='lbl' /></hbox>" +
		"</north>" +
		"<south >" +
		"<hbox id='hbox' align='center' pack='center' ></hbox>" +
		"</south>" +
		"</borderlayout>" +
		"</window>";
*/
		//"style='width:1000px; height:600px;border:3px inset;'"		
		//onClick='newWin.close();' 
		
			
		reply = "";
		Button btn1 = null;
		Button btn2 = null;
		Button btn3 = null;
		
		// create newcrop window and iframe
		win = (Window) Executions.createComponentsDirectly( newZUL, null, null /*medsWin.getParent()*/, null );
		//newWin.setHeight( "450px" );
		
		// set title and prompt
		if ( title != null ) win.setTitle( title );
		if ( promptMsg != null ) ((Label) win.getFellow( "lbl" )).setValue( promptMsg );
		
		// set buttons
		if ( opt1 != null ){
			(btn1 = new Button( opt1 )).setParent( win.getFellow( "hbox" ));
			btn1.setWidth( "80px" );
		}
		if ( opt2 != null ){
			(btn2 = new Button( opt2 )).setParent( win.getFellow( "hbox" ));
			btn2.setWidth( "80px" );
		}
		if ( opt3 != null ){
			(btn3 = new Button( opt3 )).setParent( win.getFellow( "hbox" ));
			btn3.setWidth( "80px" );
		}
		
		//newWin.setVflex( "true" );
		
		// add Event Listener to handle return button
		//Button b = (Button) newWin.getFellow("return");		
		//b.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$return( ev ); }});
		if ( btn1 != null ) btn1.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$btn( ev, 1 ); }});
		if ( btn2 != null ) btn2.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$btn( ev, 2 ); }});
		if ( btn3 != null ) btn3.addEventListener( "onClick", new EventListener(){ public void onEvent( Event ev ) throws Exception { onClick$btn( ev, 3 ); }});
		
		Events.postEvent( "onSize", win, null );
		
		try {
			win.doModal();
		} catch (Exception e) {
			// ignore
		}		

		return result;
	}
	
	
	
	public void onClick$btn( Event ev, int btnNum ){
		result = btnNum;
		win.detach();
	}
}
