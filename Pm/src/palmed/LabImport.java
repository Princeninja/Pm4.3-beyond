package palmed;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import palmed.LabResult.SpecimenSource;

import sun.management.counter.Units;
import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Validity;

public class LabImport {
	
	
	public static final String version = "2.3.1";
	
	
	class Patient {
		Name name = null;
		Date bdate = null;
		Sex sex = null;
		String ptID1 = "";
		String ptID2 = "";
		String ptID3 = "";
	
		Rec ptRec = null;
		
		void entry( Rec ptRec, Name name, Date bdate, Sex sex, String ptID1, String ptID2, String ptID3 ){
			this.ptRec = ptRec;
			this.name = name;
			this.bdate = bdate;
			this.sex = sex;
			this.ptID1 = ptID1;
			this.ptID2 = ptID2;
			this.ptID3 = ptID3;
		}
/*		void clear(){
			name = null;
			bdate = null;
			sex = null;
			ptID1 = "";
			ptID2 = "";
			ptID3 = "";
			return;
		}
*/	}
	
	class Visit {
		String type;
		String refdoc;
		String consdoc;
		String admitdoc;
		String attenddoc;
		
		void entry(){}
	}
	
	class Order {
		Date date;
		Rec provRec = null;
		Rec batRec = null;
		Rec facRec = null;
		LabResult.ResultStatus resultStatus;
		LabResult.SpecimenCondition condition;
		LabResult.SpecimenSource source;
		
		String orderID;
		String txtSource;
		String txtCondition;
		String reportType;
		String specimenID;
		String comment;
		
		int resultCnt;
		Vector<Result> results = null;
		
		void entry( Date date, Rec batRec, LabResult.ResultStatus resultStatus, String orderID, 
				String specID, LabResult.SpecimenSource source, String txtSource, LabResult.SpecimenCondition condition, String txtCondition,
				Rec provRec, Rec facRec ){
			this.date = date;
			this.batRec = batRec;
			this.resultStatus = resultStatus;
			this.orderID = orderID;
			this.specimenID = specID;
			this.source = source;
			this.txtSource = txtSource;
			this.txtCondition = txtCondition;
			this.condition = condition;
			this.provRec = provRec;
			this.facRec = facRec;
			return;
		}

	}
	
	class Result {
		Rec obsRec;
		LabResult.ResultStatus resultStatus;
		LabResult.Abnormal abnormal;
		LabObsTbl.Units units;
		String strUnits;
		String normalRange;
		
		String result;
		String range;
		String comment;

		void entry( Rec obsRec, LabResult.ResultStatus resultStatus, String result, LabObsTbl.Units units, String strUnits,
				String normalRange, LabResult.Abnormal abnormal ){
			this.obsRec = obsRec;
			this.resultStatus = resultStatus;
			this.result = result;
			this.units = units;
			this.strUnits = strUnits;
			this.normalRange = normalRange;
			this.abnormal = abnormal;
			return;
		}
}
	
	

	Patient patient = null;
	Visit visit = null;
	Order order = null;
	int posted = 0;
	
	
	
	private static String deleteachar(String str, int index){
		return str.substring(0,index)+ str.substring(index+1);		
	}
	
	public static void show( Component parent, Component ptab ){
		
		String title = null;
		String htmlStr = null;
		Media media = null;
		
		
		// upload the CCR file to view
        try {
			media = Fileupload.get( "Select the lab HL7 file to upload.", "Upload HL7 Lab" );
		} catch ( Exception e) {
			DialogHelpers.Messagebox( "Error loading lab file." );
			parent.detach();
			ptab.detach();
			return;
		}
		
		
		// make sure we got something
		if ( media == null ){ 
			DialogHelpers.Messagebox( "Error loading lab file." );
			parent.detach();
			ptab.detach();
			return;
		}
		
		System.out.println("content type: "+ media.getContentType());
		System.out.println("name is : "+ media.getName());
		if ( ! media.getContentType().equalsIgnoreCase( "text/plain" )){
			
			//DialogHelpers.Messagebox( "Lab HL7 file wrong content type: " + media.getContentType());
			//return;
			
		
			boolean iif = false;
			try {
				iif = (Messagebox.show( "Is this file actually a text file?", "Import lab as text?", Messagebox.YES | Messagebox.NO , Messagebox.QUESTION ) != Messagebox.YES );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				if( iif ){LabImport.show(parent, ptab); } 
				
			 
				else{
			
		
		//File Labfile = new File ( media.getName() );
		
		String newlabstr= "";
		newlabstr = media.getName();
		
		for (int i = 0; i < 3; i++){
			
			newlabstr = deleteachar( newlabstr, newlabstr.length()-1 );
			
		}
		/*System.out.println("newlabstr: "+ newlabstr);
		Media newlabfile = (Media) new File ( newlabstr+"txt" );
		Labfile.renameTo((File) newlabfile);
			
		media =  newlabfile;
		System.out.println("new media name is: "+ media.getName());*/
		
	    DialogHelpers.Messagebox( "Change the file extension to: " + newlabstr+"txt" );
		LabImport.show(parent, ptab);
	    return;
			
		}}
		
			
		
		
		// in memory vs string
		BufferedReader br = null;
		Reader r = null;
		
		if ( media.inMemory()){
		
			// convert to string data
			String str = media.getStringData();
			r = new StringReader( str );
			br = new BufferedReader( r );
			
		} else {
			System.out.println("else read");
			r = media.getReaderData();
			br = new BufferedReader( r );
		}
		
		
		LabImport lab = new LabImport();
		lab.read( br );
		
		DialogHelpers.Messagebox( "Lab Import done. " + lab.posted + " results posted." );
		
		try {
			r.close();
			br.close();
			parent.detach();
			ptab.detach();
		} catch (IOException e) {
			// ignore
		}

		
		
		

			
		//String absPath = SystemHelpers.getRealPath(); // + "temp.xml";


			    
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	StringBuilder Notess = new StringBuilder();
	
	
	public void read( BufferedReader br ){
		String line;
		int level = 0;

		System.out.println( "reading text" );

		try {
			while (( line = br.readLine()) != null ){

				// skip blank lines
				if ( line.trim().length() < 1 ) continue;

				
				// parse message headers
				if ( line.startsWith( "MSH" )){
					
					if ( level > 1 ) processOrder();
					
					//clearHeader();
					level = 0;
					parseMSH( line );
			
				} else if ( line.startsWith( "PID" )){

					if ( level > 1 ) processOrder();

					//clearPatient();
					patient = new Patient();
					
					parsePID( line );
					level = 1;
					
				} else if ( line.startsWith( "PV1" )){
					
					parsePV1( line );
					
				} else if ( line.startsWith( "OBR" )){
					//System.out.println("pre OBR level: "+level);
					if ( level > 1 ) processOrder();
					
					order = new Order();
					parseOBR( line );
					level = 2;
					
				} else if ( line.startsWith( "OBX" ) || line.startsWith( "OBS" )){
					//System.out.println("pre OBX level: "+level);
					parseOBX( line );
					level = 3;
					
				} else if ( line.startsWith( "NTE")){
					
					//System.out.println("pre NTE level: "+level);				
					parseNTE( line );
					level = 4;
				
			    } else {
					System.out.println( "segment type " + line.substring( 0, 4 ) + " ignored." );
				}
			}
			
			System.out.println( "end of file reached..." );
			if ( level > 1 ) processOrder();

			
			
		} catch (IOException e) {
			DialogHelpers.Messagebox( "Error reading input text." );
			return;
		}
		
	
		return;
	}
	
	
	
	public void parseMSH( String line ){
		
		if ( ! line.startsWith( "MSH")) return;
		System.out.println( "in parseMSH()" );
		
		
		return;
	}
	
	
	
	
	public void parsePID( String line ){
		
		if ( ! line.startsWith( "PID")) return;
		System.out.println( "in parsePID()" );

		String token[] = line.split( "\\|", 23 );
		if ( token.length < 22 ) System.out.println( "short line in PID, tokens=" + token.length );
		
		
																						//0 - hdr
																						//1 - seq#
		String ptID1 = token[2];														//2 - ptID1
		String ptID2 = token[3];														//3 - patient ID list
		String ptID3 = token[4];														//4 - alternate patient ID
		Name ptName = parseName( token[5] );											//5 - patient name
																						//6 - mother's maiden name?
		Date bdte = parseDate( token[7] );												//7 - birthdate
		palmed.Sex sex = Sex.get( token[8] );											//8 - sex
																						//9 - alias
		String strRace = token[10];														//10 - race
		String strAddress = token[11];													//11 - address
																						//12 - county
		String strPhone = token[13];													//13 - home phone
																						//14 -
																						//15 - language
																						//16 - marital status
																						//17 - religion
		//String ptSSN = token[18];														//18 - patient account number
																						//19 - SSN
																						//20 - driver's license
																						//21 - mother's id
		//String strEthnic = token[22];													//22 - ethnicity
		
		
		
		boolean match = false;
		
		if (( ptName.getFirstName().length() > 0 ) && ( ptName.getLastName().length() > 0 )){
			
			PtFinder ptFinder = new PtFinder();
			int fnd = ptFinder.doSearchName( ptName, 0 );

			
			
				// patient not found
				
			if ( fnd > 0 ){
			
				// verify matching birthdate
				if ( bdte.isValid()){
					for ( int i = 0; i < fnd; ++i ){
						Vector<PtFoundList> list = ptFinder.getPtList();
						PtFoundList pt = list.elementAt( i );
						if ( bdte.equals( pt.getBirthdate())){
							// match!
							patient.entry( pt.getPtRec(), pt.getName(), pt.getBirthdate(), sex, ptID1, ptID2, ptID3);
							match = true;
							System.out.println( "matching name and birthdate" );
							break;
						}
					}
					
				} else if ( false /* pt numbers entered */ ){
					// verify some number matches
					
					
				} else if (( fnd == 1 ) && ( ptName.getFirstName().length() > 1 ) && ( ptName.getMiddleName().length() > 0 ) && ( ptName.getLastName().length() > 1 )){
					// otherwise, if full name entered and matches, we'll accept that
					PtFoundList pt = ptFinder.getPtList().elementAt( 0 );
					patient.entry( pt.getPtRec(), pt.getName(), pt.getBirthdate(), sex, ptID1, ptID2, ptID3);
					match = true;
					System.out.println( "enough name parts" );
				}
			}
			
		}
		
		if ( ! match && ( ptID1.length() > 0 )){
			
			PtFinder ptFinder = new PtFinder();
			int fnd = ptFinder.doSearchNum( ptID1, 0 );

			// try to match by ID number			

			// TODO
			
			if ( match ){

				PtFoundList pt = ptFinder.getPtList().elementAt( 0 );
				patient.entry( pt.getPtRec(), pt.getName(), pt.getBirthdate(), sex, ptID1, ptID2, ptID3);
				match = true;
				System.out.println( "ID number" );
			}
			
		}
		
		if ( ! match ){
			// report no match
			System.out.println( "match not found: " + ptName.getPrintableNameLFM() + ", bdate:" + bdte.getPrintable(9) + ". ptID=" + ptID1 );
		}
		
		
		

	}

	

	public void parsePV1( String line ){
		
		if ( ! line.startsWith( "PV1")) return;
		System.out.println( "in parsePV1()" );
		
		
		return;
	}
	

	
	public void parseOBR( String line ){
		
		if ( ! line.startsWith( "OBR")) return;
		System.out.println( "in parseOBR()" );
		
		String token[] = line.split( "\\|", 26 );
		if ( token.length < 26 ) System.out.println( "short line in OBR, tokens=" + token.length );
		
		Rec batRec = null;
		Rec provRec = null;
		Rec facRec = null;
		
		
																						//0 - hdr
		String seqNum = token[1];														//1 - seq#
		String specID = token[2];														//2 - ??specID
		String orderID = token[3];														//3 - ??orderID
		String strBattery = token[4];													//4 - battery name & id
																						//5 - 
		Date date1 = parseDate( token[6] );												//6 - date
		Date date2 = parseDate( token[7] );												//7 - date
		Date date3 = parseDate( token[8] );												//8 - date
																						//9 - 
																						//10 -
																						//11 - 
																						//12 - 
		String strCondition = token[13];												//13 - specimen condition
																						//14 -
		String strSource = token[15];													//15 - specimen source
																						//16 - provider 
																						//17 -
																						//18 -
																						//19 -
		String strFacility = token[20];													//20 - lab facility
																						//21 -
																						//22 -
																						//23 -
																						//24 - CH??
		String strStatus = token[25];													//25 - F??  final?
		
		int pc = strBattery.length() - strBattery.replaceAll("^","").length();
		
		token = strBattery.split( "\\^", pc-1 );
		String type = "";
		if ( !(token.length < 3) ) { System.out.println( "too few battery fields found" );
			type = token[2]; }
		
		if ( !(type=="") ) { System.out.println( "too few battery fields found" );}
		
		String loinc = token[0]	;
		String batDesc = token[1];

		if ( ! type.equals( "LN" )) System.out.println( "not loinc code?" );
		
		
		// look up lab batch by loinc code
		if ( loinc.length() > 1 ){			
			batRec = LabBat.searchLoinc( loinc );
			if ( batRec == null ) System.out.println( "loinc code " + loinc + " not found." );
		}
		
		//TODO - specimen ID
		
		//TODO - specimen souce
		LabResult.SpecimenSource source = null;
		strSource = "";
		String txtSource = "";
		token = strSource.split( "\\&", 3 );
		if ( token.length > 1 ){
		//if ( token.length < 3 ) System.out.println( "too few source fields found" );
		strSource = token[1];
		source = LabResult.SpecimenSource.get( strSource );
		}
		if (( source == null ) || ( source == LabResult.SpecimenSource.UNSPECIFIED )){
			source = LabResult.SpecimenSource.NONE;
			txtSource = strSource;
		}
		
		
		//TODO - specimen condition
		String txtCondition = "";
		LabResult.SpecimenCondition condition = LabResult.SpecimenCondition.get( strCondition );
		if (( condition == null ) || ( condition == LabResult.SpecimenCondition.UNSPECIFIED )){
			condition = LabResult.SpecimenCondition.NONE;
			txtCondition = strCondition;
		}
		
		
		//TODO - look up lab facility (do the first 15 chars match?
		if ( strFacility.length() > 0 ){
			facRec = LabFacility.search( strFacility.substring( 0, ( strFacility.length() < 15 ) ? strFacility.length(): 15 ));
			if ( facRec == null ) System.out.println( "Lab facility not found: " + strFacility );
		}
		
		//TODO - result status
		LabResult.ResultStatus resultStatus = LabResult.ResultStatus.NONE;
		if ( strStatus.startsWith( "F" )) resultStatus = LabResult.ResultStatus.FINAL;
		if ( strStatus.startsWith( "P" )) resultStatus = LabResult.ResultStatus.PENDING;
		if ( strStatus.startsWith( "I" )) resultStatus = LabResult.ResultStatus.IN_PROGRESS;
		
		
		order = new Order();
		order.entry( date1, batRec, resultStatus, orderID, specID, source, txtSource, condition, txtCondition, provRec, facRec );
		
	}
	
	
	
	public void parseNTE( String line ){
		
		if ( ! line.startsWith( "NTE")) return;
		System.out.println( "in parseNTE()" );
		
		String token[] = line.split( "\\|", 4);
		if ( token.length < 3 ) System.out.println( "short NTE segment, "+ token.length );
		
																				//0- hdr
																				//1- deq#
																				//2- L
		String nteStr = token [3];  	                                   		//3- Text
		String nteStrt = deleteachar (nteStr, nteStr.length()-1);
		if ( nteStr.length() > 0 ) { Notess.append(nteStrt);
		
									Notess.append(System.getProperty("line.separator"));}
		
	}
	
	
	
	public void parseOBX( String line ){
		
		if ( ! line.startsWith( "OBX")) return;
		System.out.println( "in parseOBX()" );
		
		String token[] = line.split( "\\|", 14 );
		if ( token.length < 14 ) System.out.println( "short line in OBX, tokens=" + token.length );
		
		Rec obsRec = null;
		
		
																						//0 - hdr
		String seqNum = token[1];														//1 - seq#
		String specID = token[2];														//2 - 
		String strObs = token[3];														//3 - test name & id
																						//4 - 
		String strResult = token[5];													//5 - result
		String strUnits = token[6];														//6 - result units
		String normalRange = token[7];													//7 - normal range
		String strAbnormal = token[8];													//8 - normal/abnormal???
																						//9 - 
																						//10 -
		String strStatus = token[11];													//11 - F ????
																						//12 - 
		String strCondition = token[13];												//13 - 
																						//14 - date
		
		
		token = strObs.split( "\\^", 3 );
		if ( token.length < 3 ) System.out.println( "too few obs fields found" );
		String loinc = token[0]	;
		String obsDesc = token[1];
		String type = token[2];
		if ( ! type.equals( "LN" )) System.out.println( "not obs loinc code?" );
		
		
		// look up lab obs by loinc code
		if ( loinc.length() > 1 ){			
			obsRec = LabObsTbl.searchLoinc( loinc );
			if ( obsRec == null ) System.out.println( "obs loinc code " + loinc + " not found." );
		}
		
		
		//TODO abnormal flag
		LabResult.Abnormal abnormal = LabResult.Abnormal.NONE;
		if ( strAbnormal.startsWith( "A" )) abnormal = LabResult.Abnormal.ABNORMAL;
		if ( strAbnormal.startsWith( "N" )) abnormal = LabResult.Abnormal.NORMAL;
		
		//TODO - result status
		LabResult.ResultStatus resultStatus = LabResult.ResultStatus.NONE;
		if ( strStatus.startsWith( "F" )) resultStatus = LabResult.ResultStatus.FINAL;
		if ( strStatus.startsWith( "P" )) resultStatus = LabResult.ResultStatus.PENDING;
		if ( strStatus.startsWith( "I" )) resultStatus = LabResult.ResultStatus.IN_PROGRESS;

		
		//TODO units
		String txtUnits = "";
		LabObsTbl.Units units = LabObsTbl.Units.get( strUnits );
		if (( units == LabObsTbl.Units.NONE ) || ( units == LabObsTbl.Units.UNSPECIFIED )){
			units = LabObsTbl.Units.NONE;
			txtUnits = strUnits;
		}
			
		//TODO normal range
		
		//System.out.println("order is: "+order.toString());
		Result result = new Result();
		result.entry( obsRec, resultStatus, strResult, units, txtUnits, normalRange, abnormal );
		
		System.out.println("results, Obsrec: "+ obsRec );
		if ( order.results == null ) order.results = new Vector<Result>();
		order.results.add( result );
		return;
	}
	

	
	public void processOrder(){
		
		System.out.println( "in processOrder()" );
		
		// make sure data is valid
		if ( ! Rec.isValid( patient.ptRec )){			
			System.out.println( "Invalid ptRec." );
			return;
		}
		
		if ( ! order.date.isValid()){
			System.out.println( "Invalid date." );
			return;
		}
		
		
			
		for ( int i = 0; i < order.results.size(); ++i ){
			
			Result res = order.results.get( i );
			
			if ( ! Rec.isValid( res.obsRec )){
				System.out.println( "Invalid obs rec." );
				continue;
			}
			
			
			LabResult lab = new LabResult();
			
			lab.setPtRec( patient.ptRec );
			
			lab.setDate( order.date );
			if ( Rec.isValid( order.batRec )) lab.setLabBatRec( order.batRec );
			lab.setSpecimenSource( order.source );
			lab.setSourceText( order.txtSource );
			lab.setSpecimenCondition( order.condition );
			lab.setConditionText( order.txtCondition );
			if ( Rec.isValid( order.facRec )) lab.setLabFacilityRec( order.facRec );
			
			lab.setLabObsRec( res.obsRec );
			System.out.println("result is: "+( res.result ));
			lab.setResult( res.result );
			lab.setAbnormalFlag( res.abnormal );
			lab.setResultStatus( res.resultStatus );
			lab.setUnitsText( res.strUnits );
			
			if(Notess.toString().length() >1 ) {
				
				Class<? extends Notes> noteClass = NotesLab.class;
				
				Notes note = Notes.newInstance( noteClass );
				
				note = Notes.newInstance( noteClass );
				//try { note = noteClass.newInstance(); } catch ( Exception e ){ e.printStackTrace(); }
				
				note.setPtRec( patient.ptRec );
				note.setUserRec( Pm.getUserRec());
				note.setStatus( Notes.Status.CURRENT );
				note.setNumEdits( 0 );
				
				note.setDate( Date.today() );
				note.setNoteText( Notess.toString().trim());
				
				lab.setResultNoteReca(note.postNew(patient.ptRec));
				
				// post to MrLog
			    MrLog.postNew( patient.ptRec, Date.today(), "", Notes.getMrLogTypes( noteClass ), lab.getResultNoteReca() );
				// log the action
				AuditLogger.recordEntry( Notes.getAuditLogActionNew( noteClass ), patient.ptRec, Pm.getUserRec(), lab.getResultNoteReca(), null );
				
				//lab.setResultNoteText(Notes.toString()); 
				System.out.println(Notess.toString());}
			
			lab.setStatus( LabResult.Status.ACTIVE );
			lab.setValid( Validity.VALID );
			
			lab.postNew( patient.ptRec );
			++posted;
			System.out.println( "Posted one, " + posted );
		}
		
		
		return;
	}
	
	
	
	
	public static Name parseName( String strName ){
		
		String last = "";
		String first = "";
		String middle = "";
		String suffix = "";
		
		String[] token = strName.split( "\\^" );
		if ( token.length < 2 ) System.out.println( "too few name tokens (" + token.length + ") in: " + strName );
		last = token[0];
		first = token[1];
		if ( token.length > 2 ) middle = token[2];
		if ( token.length > 3 ) suffix = token[3];
		return new Name( first, middle, last, suffix );		
	}
	
	public static Date parseDate( String strDate ){
		return new Date( strDate.substring( 0, 8 ));
	}
	
	
	
	
	
	
	
}
