package palmed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class LabsWinController extends GenericForwardComposer {

	Window labsWin;					// autowired
	Window labsnWin;					// autowired
	private Listbox listbox;		// autowired - pars listbox
	private Listbox listboxn;		// autowired - pars listbox
	private Groupbox gbObs;         // autowired -Groupbox
	private Listheader Orddate;         // autowired -Groupbox
	
	private Rec	ptRec;		// patient record number


	
	final List<String[]> BatList = new ArrayList<String[]>();
	//final List<String[]> batstrList = new ArrayList<String[]>();
	final List<String[]> obsstrList = new ArrayList<String[]>();
	///Integer NumTsts,Flags = 0;
	final List<Integer> NumTsts = new ArrayList<Integer>();
	final List<Integer> Flags = new ArrayList<Integer>();
	

	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
			//System.out.println("here with component:"+component);
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
		if ( ptRec.getRec() < 2){
			System.out.println( "Error: LabsWinController() ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}

		refreshListn();
		
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshListn();
					return false;
				}}, ptRec, Notifier.Event.LAB );
		return;
	}
	
	
	public void refreshListn(){
		
		// clear listbox and Lists
		ZkTools.listboxClear( listboxn );
		Flags.clear();
		NumTsts.clear();
		BatList.clear();
		obsstrList.clear();
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		Reca reca = medPt.getLabResultReca();

		// finished if there are no entries to read
		if ( ! Reca.isValid( reca )) return;
		
		Integer NumTst = 0,Flag = 0, Dupat = 0, numobxs = 0;
		//String[] batlist = new String[] {"",""};
		
		for ( ; reca.getRec() != 0; ){
			
			LabResult lab = new LabResult( reca );
			boolean Duplicate  = false;
			++numobxs;
			// display this one?
			if ( lab.getStatus() == LabResult.Status.ACTIVE ){
			
				String strBat = "";
				Rec batRec = lab.getLabBatRec();
				if ( Rec.isValid( batRec )){
					LabBat bat = new LabBat( batRec );
					strBat = bat.getDesc();
					//System.out.println("strbat Alpha: "+ strBat);
				}else{
					//System.out.println("here cos not valid batRec");
					strBat = lab.getmiscAbbr();
					//System.out.println("Misc is now?: "+ strBat+","+ strBat.length());
				}
				
				
				if (strBat.length() < 1 || strBat == null ) { strBat = "Misc"; }	
				
				if ( BatList.size() == 0) {
						String[] battlist = new String[] {"","",};
						
						battlist[0] = lab.getDate().getPrintable();
						//System.out.println("strBat is: "+strBat);
						battlist[1] =(strBat);
						BatList.add(battlist);
						
				}else {						
						
						for ( int i = 0; i < BatList.size(); i++ ){
							
						if (  (lab.getDate().getPrintable().equals(BatList.get(i)[0])) && (strBat.equals(BatList.get(i)[1]))  ) {  
							
							//System.out.println("Bat i is: "+ BatList.get(i));
							Dupat = i ;
							Duplicate = true;
						}
							
						}
						
						//System.out.println("size of BL is: "+BatList.size());
						
						if (!Duplicate) {
							
						//System.out.println("new bat, NumTst, Flag: "+NumTst+","+Flag);
						NumTsts.add(1);
						Flags.add(1);
						
						String[] battlist = new String[] {"","",};
						
						//System.out.println("strBat 2: "+strBat);
						battlist[0] = lab.getDate().getPrintable();
						battlist[1] =(strBat);
						BatList.add(battlist);
						
						NumTst = 0; Flag = 0;
						
					}
					
				  }
				
				
				
				String[] obzlist = new String[] {"","","","","","","","",};
				
				String strObs = "";
				LabObsTbl obs = new LabObsTbl( lab.getLabObsRec());
				NumTst = NumTst + 1;
				strObs = obs.getDesc();
				//System.out.println("obs stuff:"+obs.getDesc()+","+ obs.getAbbr()+","+obs.getLOINC()+","+obs.getRec());
				obzlist[0] = strObs;
				String strResult = lab.getResult();
				
				String strUnits = lab.getUnitsText();
				
				//if (( strUnits == null ) || ( strUnits.length() < 1 )){ 
				//obs.getCodedUnits().getLabel();
				//strUnits = lab.getUnitsText();}
				
				//System.out.println("strunits are: "+ lab.getTime()+","+ lab.getUnitsText());
				
				obzlist[1] = strResult + " " + strUnits;
				//new Listcell( lab.getAbnormalFlag().getLabel()).setParent( i );
				obzlist[2] = lab.getAbnormalFlag().getLabel();
				//new Listcell( lab.getResultStatus().getLabel()).setParent( i );
				obzlist[3] = lab.getResultStatus().getLabel();
				if ( obzlist[3].contains("Final") ) { ++Flag; }
				//String strCondition = lab.getSpecimenCondition().getLabel();
				//if ( strCondition.length() < 1 ) strCondition = lab.getConditionText();
				//new Listcell( strCondition ).setParent( i );
				
				// store PAR reca in listitem's value
				//i.setValue( reca );
				obzlist[4] = reca.toString();
				obzlist[5] = strBat;
				obzlist[6] = lab.getDate().getPrintable();
				obzlist[7] = lab.getnormalRange();
				obsstrList.add(obzlist);
				//System.out.println("end of for loop");
				
				if ( Duplicate ){ 
					   //System.out.println("in duplicate");
					   NumTsts.set(Dupat, NumTsts.get(Dupat)+1);
					   Flags.set(Dupat, Flags.get(Dupat)+1);
				   }
				  else  if ( BatList.size() < 2 ) {	
						
						NumTsts.add(NumTst);
						Flags.add(Flag);
						NumTst = 0; Flag = 0;
					
				   }
				
				
			}
			
			//System.out.println("number of obx's were:"+ numobxs);
			// get next reca in list	
			reca = lab.getLLHdr().getLast();
		}
		
		
		for ( int j=0; j<BatList.size(); j++ ){
		//System.out.println("BatList 1 str bat "+ BatList.get(0)[1] );
		//System.out.println("BatList 15 str bat"+ BatList.get(14)[1] );
		
		// create new Listitem and add cells to it
		Listitem i;
		(i = new Listitem()).setParent( listboxn );
		Date Tdate = usrlib.Date.today();
		
		//new Listcell( Tdate.getPrintable()).setParent( i );
		
		new Listcell( BatList.get(j)[0] ).setParent( i );
		 
		new Listcell( BatList.get(j)[1] ).setParent( i );
		new Listcell( Integer.toString(NumTsts.get(j)) ).setParent( i );
		//new Listcell( Integer.toString(Flags.get(j)) + "/" + Integer.toString(NumTsts.get(j)) ).setParent( i );
		
		i.setValue(BatList.get(j)[1]);
		
		}
		Orddate.sort(false);
		
		//System.out.println("skip?");
		return;
	}
	

	public void onDoubleClick( Event ev ){  onClick$btnView(); }
		
	public void onClick$btnView() {
		
		if ( listboxn.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected. Please select A panel." );
			return;
		}
		//System.out.println("labsnWin is???: "+labsnWin+","+ gbObs.getChildren().toString());
		if (  gbObs.getChildren().size() >= 1  ){ 
			
			for ( int i=0; i<gbObs.getChildren().size(); i++ ) { 
				
				Object gbobschild;
				gbobschild = gbObs.getChildren().get(i);
				gbObs.removeChild((Component) gbobschild);
				
			}
			
		}	
		String BatStr = (String)listboxn.getSelectedItem().getValue();
		if (BatStr.length() >0 ) {
			//System.out.println("BatStr is: "+BatStr+","+labsnWin);
			
			
			//refreshList( BatStr );
			
			// pass parameters to new window
			Map<String, Object> myMap = new HashMap<String, Object>();
			myMap.put( "ptRec", (Rec)(ptRec ));
			myMap.put("obsstrList", (List<String[]>)(obsstrList));
			myMap.put("BatStr", (String)(BatStr));
			
			labsnWin =(Window) Executions.createComponents("labsn.zul", gbObs, myMap );
			labsnWin = null;
			//gbObs.setStyle("overflow:auto;position:relative");
		}
	 
		 
		 //DialogHelpers.Messagebox( "Please close the currently open Battery window." );
			//return;
		 
	 
		
	}
	
	public void onClick$btnAdd(){
		
		LabResEdit.add( ptRec, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		//refreshList();
		return;
	}
	
	public void onClick$btnEdit(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca labReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(labReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		LabResEdit.edit( ptRec, labReca, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		//refreshList();
		return;
	}
	
	
	
	
	
	
	
	public void onClick$btnRemove(){
		
		if ( listbox.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca labReca = (Reca) listbox.getSelectedItem().getValue();
		if ( ! Reca.isValid(labReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		LabResRemove.show( ptRec, labReca, labsWin );
		Notifier.notify( ptRec, Notifier.Event.LAB );
		//refreshList();
		return;
	}
	
	
	public void onClick$btnPrint(){
		
		//System.out.println( "onClick$btnPrint" );
		
		
		// Generate HTML report text
		
		StringBuilder sb = new StringBuilder( 1000 );
		
		sb.append( "<HTML>" );
		sb.append( "<HEAD>" );
		
		sb.append( "<style type='text/css'>" );
		//sb.append( "font-size:large" );
		//sb.append( "body{font-size:100%;}" );
		//sb.append( "h3 {font-size:200%;}" );
		//sb.append( "p {font-size:100%;}" );
		//sb.append( "*{font-size:40%;}" );

		sb.append( "</style>" );
		
		sb.append( "</HEAD>" );
		sb.append( "<BODY>" );
		sb.append( "<H2>PAL/MED<H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "<H3>" );
		sb.append( "<space>");
		
		DirPt dirpt = new DirPt( ptRec );
		
		sb.append( "<H3>Patient: " + dirpt.getName().getPrintableNameLFM());
		sb.append( "<H3>DOB: " + dirpt.getBirthdate().getPrintable(9));
		sb.append( "<space>" );
		sb.append( "<H3>PARs/Allergies<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Start Date</td><td>Medication</td><td>Severity</td><td>Domain</td><td>Reaction/Symptom</td></tr>" );
		
		for( int i = 0; i < listbox.getItemCount(); ++i ){
			Listitem li = listbox.getItemAtIndex( i );
			Reca parReca = (Reca) li.getValue();
			Par par = new Par( parReca );
			sb.append( "<tr><td>" + par.getDate().getPrintable(9) + "</td><td>" + par.getParDesc() + "</td><td>" + par.getSeverity().getLabel() + "</td><td>" +
					par.getDomain().getLabel() + "</td><td>" + par.getSymptomsText( true ) + "</td></tr>" );
		}
		
		sb.append( "</table>" );
		sb.append( "END REPORT" );

		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		sb.append( "</BODY>" );
		sb.append( "</HTML>" );
		
		String html = sb.toString();
		
		//System.out.println( html );

		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );
		if ( amedia == null ) System.out.println( "AMedia==null" );

		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( labsWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PAR_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}

}


/* vintage Refresh List 
public void refreshList(){

// clear listbox
ZkTools.listboxClear( listbox );

// populate list
DirPt dirPt = new DirPt( ptRec );
MedPt medPt = new MedPt( dirPt.getMedRec());
Reca reca = medPt.getLabResultReca();

// finished if there are no entries to read
if ( ! Reca.isValid( reca )) return;

for ( ; reca.getRec() != 0; ){
	
	LabResult lab = new LabResult( reca );
	
	// display this one?
	if ( lab.getStatus() == LabResult.Status.ACTIVE ){
	
		String strBat = "";
		Rec batRec = lab.getLabBatRec();
		if ( Rec.isValid( batRec )){
			LabBat bat = new LabBat( batRec );
			strBat = bat.getDesc();
		}
		
		String strObs = "";
		LabObsTbl obs = new LabObsTbl( lab.getLabObsRec());
		strObs = obs.getDesc();
		
		String strResult = lab.getResult();
		String strUnits = obs.getCodedUnits().getLabel();
		if (( strUnits == null ) || ( strUnits.length() < 1 )) strUnits = obs.getUnitsText();
		
		// create new Listitem and add cells to it
		Listitem i;
		(i = new Listitem()).setParent( listbox );
		new Listcell( lab.getDate().getPrintable(9)).setParent( i );
		
		new Listcell( strBat ).setParent( i );
		new Listcell( strObs ).setParent( i );
		new Listcell( strResult + " " + strUnits ).setParent( i );
		
		new Listcell( lab.getAbnormalFlag().getLabel()).setParent( i );
		new Listcell( lab.getResultStatus().getLabel()).setParent( i );
		
		String strCondition = lab.getSpecimenCondition().getLabel();
		if ( strCondition.length() < 1 ) strCondition = lab.getConditionText();
		new Listcell( strCondition ).setParent( i );
		
		// store PAR reca in listitem's value
		i.setValue( reca );
	}
	
	
	// get next reca in list	
	reca = lab.getLLHdr().getLast();
}


return;
}
*/