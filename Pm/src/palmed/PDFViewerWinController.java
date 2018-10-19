package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;

public class PDFViewerWinController extends GenericForwardComposer {

	Iframe iframe;
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
	
	
	
	
	public void onClick$btnClose( Event ev ){
		System.out.println( "onClick$btnClose" );
		((Component) ev.getTarget().getSpaceOwner()).detach();
		return;
	}
	
	
	
	public void onClick$btnPrint( Event ev ){

		// get the HTML from the iframe
		//Media media = iframe.getContent();
		//if ( media == null ) System.out.println( "media=null" );
		Clients.print();
		
		

		return;
	}
}
