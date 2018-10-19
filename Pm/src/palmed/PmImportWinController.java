package palmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.WildCardFilenameFilter;



import org.apache.commons.io.FileUtils;  





public class PmImportWinController extends GenericForwardComposer {

	Listbox importListbox;		// autowired
	Iframe importIframe;		// autowired
	Label ptname;				// autowired
	Label ptdob;				// autowired
	Label ptssn;				// autowired
	Textbox txtDate;
	Textbox txtDesc;
	
	
	Rec ptRec = null;
	String ptName = "";
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Components.wireVariables( phyexHtmlWin, this );
//		Components.addForwards( phyexHtmlWin, this );

		
		
		// populate list
		refreshList();

		// set default date
		txtDate.setValue( Date.today().getPrintable());

		return;
	}
	
	
	
	
	public void refreshList(){

		// clear list
		for ( int i = importListbox.getItemCount(); i > 0; --i )
			importListbox.removeItemAt( i - 1 );
		
		// build file name filter
		String filter = "*.pdf"; //String.format( "rp*%02d_*.ecs.Z", year % 100 );
		System.out.println( "filename filter=" + filter );
		
		// populate list
		
		File file = new File( /*"c:/Windows" ); */  Pm.getMedPath() + "/import" );
		File list[] = file.listFiles( new WildCardFilenameFilter( filter ));
		
		if ( list != null ){
			
			for ( int cnt = 0; cnt < list.length; ++cnt ){
				
				//String s = soap.toString();
				System.out.println( list[cnt] );
				
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( importListbox );
				new Listcell( list[cnt].getName() ).setParent( i );
				new Listcell( "" + list[cnt].length() ).setParent( i );
				i.setValue( list[cnt] );		// set list[] (File) into listitem for later retrieval
				
				
			}
		}

		return;
	}
	
	
	
	
	public void onSelect$importListbox( Event e ) throws InterruptedException{
		
		int	ret;				// process return value
		
		
		File file = (File) importListbox.getSelectedItem().getValue();
		String fname = "/u/med/import/" + file.getName();
		
		System.out.println( "fname=" + fname );

		
		// Does file exist?
		//   If not, is it .Z compressed?
		//      If so, uncompress it.
		if ( ! new File( "", fname ).exists()){
			System.out.println( "fname=" + fname + " not found." );
		}

		
//		switch ( DocTypes.DocTypeIDs.get( doc.getDocTypeID())){
		
//		case PDF:
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
	        importIframe.setContent(amedia);
			}
/*			break;
			
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
*/		
	}

	
	
	public void onClick$btnRefresh( Event e ){
		
		refreshList();
	}

	public void onClick$btnDelete( Event e ){
		
		// make sure an item is selected
		if ( importListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No document selected." );
			return;			
		}
		
		// try to delete the file
		System.out.println( "deleting file....." );
		File file = (File) importListbox.getSelectedItem().getValue();
		boolean success = file.delete();
		
		// if move not successful, copy file and delete 
		if ( ! success ){			
			System.out.println( "unable to delete file....." );
			return;
		}

		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PMIMPORT_DELETE, ptRec, Pm.getUserRec(), null, null );
		


		// remove this entry from list
		int index = importListbox.getSelectedIndex();
		importListbox.removeItemAt( index );

		// select next item in list
		if ( importListbox.getItemCount() > 0 ){
			if ( index >= importListbox.getItemCount()) --index;
			importListbox.setSelectedIndex( index );
			try {
				onSelect$importListbox( null );
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		return;
		}

	}

	public void onClick$btnSelectPt( Event e ){
		
		int rec;
		
		ptRec = null;
		ptName = "";
		
		importIframe.setVisible(false);
		
		if (( rec = PtSearch.show()) == 0 ){
			// No patient found
			return;
		}			
		
		importIframe.setVisible(true);

		ptRec = new Rec( rec );

		DirPt dirpt = new DirPt( ptRec );
		ptname.setValue( dirpt.getName().getPrintableNameLFM());
		ptdob.setValue( dirpt.getBirthdate().getPrintable());
		ptssn.setValue( dirpt.getSSN());

		return;
	}

	
	
	
	
	public void onClick$btnStore( Event e ){
		
		// make sure an item is selected
		if ( importListbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No document selected." );
			return;			
		}
		
		// make sure patient selected
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			DialogHelpers.Messagebox( "No patient selected." );
			return;
		}
		
		// make sure valid date entered
		String pdate = txtDate.getValue();
		if ( pdate.length() < 6 ){
			DialogHelpers.Messagebox( "Enter a valid date." );
			return;
		}
		Date date = new Date( pdate );
		if (( date == null ) || ( ! date.isValid())){
			DialogHelpers.Messagebox( "Invalid date: " + pdate + "." );
			return;			
		}
		

		// make sure valid desc entered
		String desc = txtDesc.getValue();
		if ( desc.length() < 3 ){
			DialogHelpers.Messagebox( "Enter a valid description." );
			return;
		}
		
		
		
		
		// valid info
		// set up document
		
		Docs doc = new Docs();
		doc.setPtRec(ptRec);
		doc.setDate( date );
		doc.setDesc( desc );
		//TODO
		doc.setDocTypeID( DocTypes.PDF );
		// TODO
		//doc.setDocumentID(null);
		// set as 'Current' document 
		doc.setStatus( 'C' );
		
		
		// post new doc record
		Reca docReca = doc.postNew( ptRec );
		
		
		// post to MrLog
	    MrLog.postNew( ptRec, date, desc, MrLog.Types.DOCUMENT, docReca );

	    
		// save document
		
		// get document type
		// make target document filename
		String targetFname = String.format( "%s/docs/%02d/d%07d.%s", Pm.getMedPath(), docReca.getYear(), docReca.getRec(), doc.getDocTypeID().getExt());
		System.out.println( "fname=" + targetFname );

		
		String cmd;
		
		
		//4 String orig ="file.xml";    
		//6 File fOrig = new File(orig);  
		
		
		
		// try to move the file
		System.out.println( "moving file....." );
		File file = (File) importListbox.getSelectedItem().getValue();
		boolean success = file.renameTo( new File( targetFname ));
		
		// if move not successful, copy file and delete 
		if ( ! success ){
			
			System.out.println( "move not successful, copying file....." );
			try {
				FileUtils.copyFile( file, new File( targetFname ));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println( "IO error copying file....." );
				e1.printStackTrace();
			} 

			System.out.println( "deleting file....." );
			file.delete();
		}

		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PMIMPORT_NEW, ptRec, Pm.getUserRec(), docReca, null );
		


		// remove this entry from list
		int index = importListbox.getSelectedIndex();
		importListbox.removeItemAt( index );

		// select next item in list
		if ( importListbox.getItemCount() > 0 ){
			if ( index >= importListbox.getItemCount()) --index;
			importListbox.setSelectedIndex( index );
			try {
				onSelect$importListbox( null );
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		}

		
	    // Notify listeners 
		Notifier.notify( ptRec, Notifier.Event.CARELOG );
		Notifier.notify( ptRec, Notifier.Event.DOC );

	    

		return;
	}

}
