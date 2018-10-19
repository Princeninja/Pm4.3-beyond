package palmed;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.text.BasicTextEncryptor;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;

public class CCRView {
	
	

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent ) {

		new CCRView( parent, null, null, false );
		return true;
	}

	
	/**
	 * The Call method.
	 */
	public static boolean showEncrypted( Component parent ) {

		new CCRView( parent, null, null, true );
		return true;
	}

	

	/**
	 * The Call method.
	 */
	public static boolean show( Component parent, Media media, String xslFile ) {
		
		new CCRView( parent, media, xslFile, false );
		return true;
	}

	

	/**
	 * private constructor. So it can only be created with the static add() or edit()
	 * methods.
	 */
	private CCRView( Component parent, Media media, String xslFilename, boolean encrypted ) {
		super();
		createBox( parent, media, xslFilename, encrypted );
	}

	
	
	
	private void createBox( Component parent, Media media, String xslFilename, boolean encrypted ) {

		String title = null;
		String htmlStr = null;
		
		// upload the CCR file to view
		if ( media == null ){
		
	        try {
				media = Fileupload.get( "Select the CCR/CCD file to upload.", "Upload CCR/CCD" );
			} catch (Exception e) {
				DialogHelpers.Messagebox( "Error loading CCR/CCD file." );
				return;
			}
		}
		
		
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading CCR/CCD file." );
			return;
		}
		
		String xmlStr = "";
		
		if ( encrypted ){
			
			String password = DialogHelpers.Textbox( "CCR Viewer", "Please enter the document's password." );
			if ( password == null ) return;
			
			// media content type text/plain
			String encryptedText = media.getStringData();
			
			

			
			
			BasicTextEncryptor textDecryptor = new BasicTextEncryptor();
			textDecryptor.setPassword( password );
			xmlStr = textDecryptor.decrypt( encryptedText );
			
			
			StandardStringDigester digester = new StandardStringDigester();
			digester.setAlgorithm( "SHA-1" );		
			digester.setSaltSizeBytes( 0 );
			
			String digest = digester.digest( xmlStr );
			
			DialogHelpers.Messagebox( "SHA-1 Digest: " + digest );

			
			if (( xmlStr.indexOf( "ContinuityOfCareRecord") < 0 )
				&& ( xmlStr.indexOf( "<ClinicalDocument") < 0 )){
				
				DialogHelpers.Messagebox( "Invalid password or incorrect file type." );
				return;
			}
					
			
		} else {
			
			if ( ! media.getContentType().equalsIgnoreCase( "text/xml" )){
				DialogHelpers.Messagebox( "CCR/CCD file wrong content type:" + media.getContentType());
				return;
			}
				
			// convert to string data
			xmlStr = media.getStringData();
		}
		
		
		
		// what type of file?
		if ( xmlStr.indexOf( "ContinuityOfCareRecord") >= 0 ){
			
			// CCR File
			if ( xslFilename == null ) xslFilename = "ccr.xsl";
			title = "View CCR File";
			
		} else if ( xmlStr.indexOf( "<ClinicalDocument") >= 0 ){
			
			// CCD File
			if ( xslFilename == null ) xslFilename = "cda.xsl";
			title = "View CCD File";

			
			
			
		} else {
			// not valid CCR or CCD file
			DialogHelpers.Messagebox( "Not a valid CCR or CCD file." );
			return;
		}
			

			
		String absPath = SystemHelpers.getRealPath(); // + "temp.xml";


		// Create streams
	    StringReader fr = new StringReader( xmlStr );
	    StringWriter fw = new StringWriter( xmlStr.length());
	    
		try {
		
			TransformerFactory tFactory = TransformerFactory.newInstance();
		
			Transformer transformer = tFactory.newTransformer( new javax.xml.transform.stream.StreamSource(absPath + xslFilename ));
		
		    transformer.transform
		      (new javax.xml.transform.stream.StreamSource( fr ),
		       new javax.xml.transform.stream.StreamResult( fw ));
		    //new FileOutputStream(absPath+"howto.html")
		    
			htmlStr = fw.toString();		
			//System.out.println( htmlStr );

			fr.close();
			fw.close();
					
		    
		} catch (Exception e){
			DialogHelpers.Messagebox( "Error processing XML." );
			return;
		}


			    
			    
		// Create viewer window
		Window win = (Window) Executions.createComponents( "ccrview.zul", parent, null );
		win.setTitle( title );
		
		// Wrap html in AMedia
		AMedia nmedia = new AMedia( "ccr.html", "html", "text/html", htmlStr );

		// Set AMedia content in viewer window's iframe
		Iframe iframe = (Iframe) win.getFellow( "iframe" );
		if (( iframe == null ) || !( iframe instanceof Iframe ))
			SystemHelpers.seriousError( "could not get iframe" );
		//System.out.println( "MEDIA FORMAT=" + nmedia.getFormat());
		//System.out.println( "CONTENT TYPE=" + nmedia.getContentType());
		iframe.setContent( nmedia );
		return;		
	}
}



/*  ANOTHER WAY TO HAVE DONE IT - ONE OF MY EARLIER TRIES.....
 * 
 * 

		Media media = null;
		
        try {
			media = Fileupload.get( "Select the CCR file to upload.", "Upload CCR" );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		if (media != null) { 
			
			String str = media.getStringData();

			int split = str.indexOf( "?>" );
			if ( split < 0 ){
				DialogHelpers.Messagebox( "Incorrectly formed CCR file." );
				return;
			}
			split += 2;
			StringBuilder sb = new StringBuilder( str.length() + 40 );
			sb.append( str.substring( 0, split ));
			sb.append( "\n<?xml-stylesheet type='text/xsl' href='ccr.xsl'?>\n" );;
			sb.append( str.substring( split, str.length()));
			str = sb.toString();
			
			System.out.println( str );


			   String buf,temp;
			   String fname = null;
			   
			    // Create streams
			    StringReader fr = new StringReader ( str );
			    FileWriter fw;
			    
			    String absPath = SystemHelpers.getRealPath(); // + "temp.xml";

			    // create temp file
			    File file = null;
				try {
					file = File.createTempFile( "ccr", ".xml", new File( absPath ));
				    fname = file.getName();
				    System.out.println( "Fname=" + fname );
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			    
			    
				try {
					fw = new FileWriter ( file );
			    BufferedReader br=new BufferedReader(fr);  //wrap basic object
			    BufferedWriter bw=new BufferedWriter(fw);  //wrap basic object
			    while ((buf=br.readLine()) != null)
			    {
			      bw.write(buf+"\n");
			    }
			    br.close(); bw.close();
			    fr.close(); fw.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
			    
			// pass parameters to new window
			Map<String, Object> myMap = new HashMap();
			myMap.put( "ptRec", (Object)(ptRec ));		
			//myMap.put( "text", str );
			
			
			myMap.put( "src", fname );
			
			Component comp = Executions.createComponents( "ccrview.zul", this.parent, myMap );
			
//			Iframe iframe = (Iframe) comp.getFellow( "iframe" );
//			if (( iframe == null ) || !( iframe instanceof Iframe ))
//				SystemHelpers.seriousError( "could not get iframe" );
//			System.out.println( "MEDIA FORMAT=" + media.getFormat());
//			System.out.println( "CONTENT TYPE=" + media.getContentType());
			
//			iframe.setContent( media );
//			iframe.setSrc( "temp.xml")
		}

*/


