package palmed;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;


public class CCRViewWinController extends GenericForwardComposer {
	
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
		Media media = iframe.getContent();
		if ( media == null ) System.out.println( "media=null" );
		
		String str = media.getStringData();
		
		// add javascript to print the report
		StringBuilder sb = new StringBuilder( str.length() + 80 );

		
		// filter <style>......</style>
		//TODO - for some reason the style part will not let anything print... ZK bug or style problem?
		int split = str.indexOf( "<style");
		if ( split < 0 ) System.out.println( "NO STYLE????" );
		
		sb.append( str.substring( 0, split ));		
		
		int next = str.indexOf( "</style>" );
		next += 8;
		split = str.indexOf( "</head>");
		if ( split < 0 ) System.out.println( "NO HEAD????" );
		
		sb.append( str.substring( next, split ));		
		sb.append( "<script type='text/javascript' defer='true' >\n" );
		sb.append( "window.focus();window.print();window.close();\n" );
		sb.append( "</script>\n" );
		
		sb.append( str.substring( split ));
		String html = sb.toString();
		
		//System.out.println( html );

		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );

		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( (Component) ev.getTarget().getSpaceOwner() );

		return;
	}

}
