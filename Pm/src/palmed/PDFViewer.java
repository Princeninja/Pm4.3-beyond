package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;


import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;

public class PDFViewer {


	/**
	 * The Call method.
	 */
	public static boolean show( Component parent, String filename ) {

		new PDFViewer( parent, null, filename );
		return true;
	}

	

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent, Media media ) {
		
		new PDFViewer( parent, media, null );
		return true;
	}

	

	/**
	 * private constructor. So it can only be created with the static add() or edit()
	 * methods.
	 */
	private PDFViewer( Component parent, Media media, String filename ) {
		super();
		createBox( parent, media, filename );
	}

	
	
	
	private void createBox( Component parent, Media media, String filename ) {

		
		// upload the PDF file to view
		if ( media == null ){
		
			try {
				media = new AMedia( new File( "/u/med/resource/" + filename ), "application/pdf", null );
			} catch (FileNotFoundException e) {
				DialogHelpers.Messagebox( "PDF file not found: " + filename );
				return;
			}
		}
		
		
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading PDF file." );
			return;
		}
		
		if ( ! media.getContentType().equalsIgnoreCase( "application/pdf" )){
			DialogHelpers.Messagebox( "PDF/CCD file wrong content type: " + media.getContentType());
			return;
		}
		
			
		//String absPath = SystemHelpers.getRealPath(); // + "temp.xml";



			    
			    
		// Create viewer window
		Window win = (Window) Executions.createComponents( "pdfviewer.zul", parent, null );
		win.setTitle( "PDF Viewer" );
		
		// Set AMedia content in viewer window's iframe
		Iframe iframe = (Iframe) win.getFellow( "iframe" );
		if (( iframe == null ) || !( iframe instanceof Iframe ))
			SystemHelpers.seriousError( "could not get iframe" );
		//System.out.println( "MEDIA FORMAT=" + nmedia.getFormat());
		//System.out.println( "CONTENT TYPE=" + nmedia.getContentType());
		iframe.setContent( media );
		return;		
	}
}
