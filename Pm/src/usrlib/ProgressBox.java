/**
 * Progress Box
 * 
 * Displays a message centered on the screen until the close() is called.
 * 
 */

package usrlib;


import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;


public class ProgressBox {
	
	private Component parent;
	private Window progWin;
	private Label label;
	
	
	/*
	 * show( parent, msg )
	 * 
	 * Use this method to call/open the ProgressBox
	 */
	static public ProgressBox show( Component parent, String msg ){
		return new ProgressBox( parent, msg );
	}
	

	/*
	 * Private constructor
	 */
	private ProgressBox( Component parent, String msg ){
		
		//Executions.createComponentsDirectly(content, extension, parent, arg)
		System.out.println( "create progressBox( " + msg + " );" );
		
		// Create window
		progWin = new Window( "Progress", "none", false );
		progWin.setParent( parent );
		progWin.setWidth("350px");
		progWin.setHeight("150px");
		progWin.setPosition("center");
		progWin.doHighlighted();
		progWin.setVisible( true );
		
		progWin.setContentStyle( "center" );
		
		
		// Add text
		progWin.appendChild( label = new Label( msg ));


//		System.out.println( "progressBox: progWin=" + progWin.toString() );
//		Events.echoEvent("onLater", progWin, null);
		return;
	}
	
	
	
	/*
	 * setMsg()
	 * 
	 * Use this method to change the message.
	 */
	public void setMsg( String msg ){
		this.label.setValue(msg);
	}
	
	/*
	 * close()
	 * 
	 * Use this method to close the box.
	 */
	public void close(){
		System.out.println( "progressBox.close();" );
		progWin.detach();
	}

}
