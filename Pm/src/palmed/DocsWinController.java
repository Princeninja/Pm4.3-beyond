package palmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import usrlib.Rec;
import usrlib.Reca;

public class DocsWinController extends GenericForwardComposer {

	private Listbox docsListbox;		// autowired
	private Iframe docsIframe;			// autowired
	
	
	
	private Rec	ptRec;		// patient record number

	
	

	public DocsWinController() {
		// TODO Auto-generated constructor stub
	}

	public DocsWinController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public DocsWinController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
		// TODO Auto-generated constructor stub
	}

	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			System.out.println( "Error: DocsWinController() invalid or null ptRec" );
			ptRec = new Rec( 2 );		// default for testing
		}

		//System.out.println( "DocsWinController() ptRec=" + ptRec.getRec());
		
		
		
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );

		for ( Reca reca = ( new MedPt( dirPt.getMedRec())).getDocReca(); reca.getRec() != 0; ){
			
			Docs doc = new Docs( reca );
			//String s = doc.toString();
			//System.out.println( "doc=" + s );
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( docsListbox );
			new Listcell( doc.getDate().getPrintable(9)).setParent( i );
			new Listcell( doc.getDesc()).setParent( i );
			new Listcell( doc.getDocTypeID().getAbbr()).setParent( i );
			i.setValue( reca );		// set reca into listitem for later retrieval
			
			
			// get next reca in list	
			reca = doc.getLLHdr().getLast();
			

		}




	}
	
	
	
	public void onSelect$docsListbox( Event e ) throws InterruptedException{
		
		boolean newFname = false;
		String fname;
		int	ret;				// process return value
		
		Reca reca = (Reca) docsListbox.getSelectedItem().getValue();
		
		Docs doc = new Docs( reca );
		
		// get document type
		// get document filename
		fname = String.format( "%s/docs/%02d/d%07d.%s", Pm.getMedPath(), reca.getYear(), reca.getRec(), doc.getDocTypeID().getExt());
		System.out.println( "fname=" + fname );

		
		// Does file exist?
		//   If not, is it .Z compressed?
		//      If so, uncompress it.
		if ( ! new File( "", fname ).exists()){
			if ( new File( "", fname + ".Z" ).exists()){
			
				String oldFname = fname;
				
				// Make new temp file name
				// TODO - a truly random name would be soooo much better
				if ( SystemHelpers.isWinOS()){
					//fname = String.format( "\\u\\tmp\\d%07d.%s", reca.getRec(), doc.getDocTypeExt());
					fname = String.format( "/u/tmp/d%07d.%s", reca.getRec(), doc.getDocTypeID().getExt());
				} else {
					fname = String.format( "/usr/tmp/d%07d.%s", reca.getRec(), doc.getDocTypeID().getExt());
				}
				
				// call OS to uncompress file 
				//   (likely must be on UNIX if we still have .Z compressed files  ( OR TESTING ON WIN :))
				System.out.println( "Calling OS to uncompress .Z file" );
				
				try { 
					
					String cmd;
					
					if ( SystemHelpers.isWinOS() ){
						
						System.out.println( "Windows" );
						//cmd = "\\u\\tmp\\cp " + oldFname + ".Z " + fname + ".Z";
						cmd = "/u/tmp/cp " + oldFname + ".Z " + fname + ".Z";
						System.out.println( "Cmd=" + cmd );
						Process p=Runtime.getRuntime().exec( cmd ); 
						ret = p.waitFor(); 
						System.out.println( "ret=" + ret );
						BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
						String line=reader.readLine(); 
	
						while( line != null ){ 
							System.out.println(line); 
							line=reader.readLine(); 
						} 
						//cmd = "\\u\\tmp\\compress -d " + fname + ".Z";
						cmd = "/u/tmp/compress -d " + fname + ".Z";
						System.out.println( "Cmd=" + cmd );
						Process p2=Runtime.getRuntime().exec( cmd ); 
						//ret = p.waitFor(); 
						System.out.println( "ret=" + ret );
						
						/*	?do I even need to read this ???
						BufferedReader reader2=new BufferedReader(new InputStreamReader(p2.getInputStream())); 
						String line2=reader.readLine(); 
	
						while( line2 != null ){ 
							System.out.println(line2); 
							line2=reader2.readLine(); 
						} 
						 */
						
					} else {
						
						System.out.println( "Unix" );
						//cmd = "/bin/zcat " + oldFname + ".Z  >" + fname;
						cmd = "/bin/cp " + oldFname + ".Z  " + fname + ".Z";
						System.out.println( "Cmd=" + cmd );
						Process p=Runtime.getRuntime().exec( cmd ); 
						ret = p.waitFor(); 
						System.out.println( "ret=" + ret );
						
						cmd = "/bin/uncompress " + fname + ".Z";
						System.out.println( "Cmd=" + cmd );
						p=Runtime.getRuntime().exec( cmd ); 
						ret = p.waitFor(); 
						System.out.println( "ret=" + ret );
						
						
						/*	?do I even need to read this ???
						BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
						String line=reader.readLine(); 
	
						while( line != null ){ 
							System.out.println(line); 
							line=reader.readLine(); 
						} 
					 	*/	
					}
				} 
				catch(IOException e1) {} 
				//catch(InterruptedException e2) {} 
			
				System.out.println("Done uncompressing"); 
				newFname = true;

			}
		}

		
		switch ( doc.getDocTypeID()){
		
		case PDF:
			{
			System.out.println( "DocTypePDF" );
	        AMedia amedia = null;
	        
			try {
				amedia = new AMedia( new URL( "file", "localhost", fname ), "application/pdf", null );
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				alert( "File not found." );
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				alert( "Bad URL" );
			}
	        //set iframe content
	        docsIframe.setContent(amedia);
			}
			break;
			
		case TIFF:
			{
			System.out.println( "DocTypeTIFF" );
	        AMedia amedia = null;

			try {
				amedia = new AMedia( new URL( "file", "localhost", fname ), "application/tiff", null );
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				alert( "File not found." );
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				alert( "Bad URL" );
			}
	        //set iframe content
	        docsIframe.setContent(amedia);
			}
			break;
			
		case TEXT:

			System.out.println( "DocTypeTEXT" );
			//set TEXT file as iframe content
	        docsIframe.setSrc( fname );
			break;
			
		case HTML:

			System.out.println( "DocTypeHTML" );
	        //set HTML file as iframe content
	        docsIframe.setSrc( fname ); 
			break;
			
		default:
			System.out.println( "DocType is default" );
			;
			break;
		}
		
		
		
		
	}


}
