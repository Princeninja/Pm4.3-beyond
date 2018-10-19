package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class StatusBarWinController extends GenericForwardComposer {
	
	Window statusBarWin;		// autowired
	Label lblNCStatus = null;
	
	NCStatusChecker nc = null;
	
	
	

	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		setupNewcrop();
		return;
	}
	
	
	
	
	
	
	
	public void setupNewcrop(){
		
		
		// do we poll Newcrop?
		ERxConfig conf = new ERxConfig();
		conf.read();
		if ( ! conf.getPollStatus()) return;
		
		
		// set up labels in status bar
		( new Label( "ERx Status:" )).setParent( statusBarWin );
		lblNCStatus = new Label( "" );
		lblNCStatus.setParent( statusBarWin );
		
		
		// register Par list callback
		Notifier.registerCallback( new NotifierCallback(){
			public boolean callback( Rec ptRec, Notifier.Event event ){
				refreshNewcrop();
				return true;
			}
		}, null, Notifier.Event.NC_STATUS );

		// start newcrop status checker (poll timer)
		nc = NCStatusChecker.start( statusBarWin );
		
	}
	
	
	public void refreshNewcrop(){
		lblNCStatus.setValue( String.valueOf( nc.getCount()) );
	}
}
