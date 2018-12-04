package palmed;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.io.FileUtils;
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
import usrlib.XMLElement;
import usrlib.XMLParseException;

public class LabImport {
	
	
	public static final String version = "2.3.1";
	private final static String fn_config1 = "Compendium0.xml";
	private static StringBuilder errpt = null;
	private static String LabName = null;
	
	
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
		String batdesc;
		LabResult.ResultStatus resultStatus;
		LabResult.SpecimenCondition condition;
		LabResult.SpecimenSource source;
		
		String orderID;
		String miscabbr;
		String txtSource;
		String txtCondition;
		String reportType;
		String specimenID;
		String comment;
		
		int resultCnt;
		Vector<Result> results = null;
		
		void entry( Date date, Rec batRec, String miscabbr, LabResult.ResultStatus resultStatus, String orderID, 
				String specID, LabResult.SpecimenSource source, String txtSource, LabResult.SpecimenCondition condition, String txtCondition,
				Rec provRec, Rec facRec ){
			this.date = date;
			this.batRec = batRec;
			this.miscabbr = miscabbr;
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
				if( iif ){ 
					media = null;
					LabImport.show(parent, ptab); } 
				
			 
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
		
			
		LabName = media.getName();
		
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
		
		if ( !( br == null )){
			
			
		LabImport lab = new LabImport();
		
	    errpt = new StringBuilder( "<h1>Lab Report</h1>" );
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<table width=\"100%\" >");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<colgroup>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<col>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<col>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("</colgroup>");
		errpt.append(System.getProperty("line.separator"));
		Date Tdate = usrlib.Date.today();
		errpt.append("<tr><td><b>Date:</b>"+ Tdate.getPrintable() + "</td>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<td><b>Practice:</b> RIVERSIDE FAMILY MEDICINE, P. C. </td></tr>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("</table> ");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<hr width=100% style=\"height:5px;border:none;color:#333;background-color:#333;\" > ");
		errpt.append(System.getProperty("line.separator"));
		
		
		
		lab.read( br, errpt );
		
		DialogHelpers.Messagebox( "Lab Import done. " + lab.posted + " results posted." );
		
		File errrpt = new File( Pm.getOvdPath() + File.separator + "LabErr");
		
		File[] matchingfiles = errrpt.listFiles(new FilenameFilter() { 
			
			public boolean accept(File visits, String name){
				return name.startsWith("lab")&& name.endsWith(".html");
			}
			
		});
		
		int mflength = matchingfiles.length;
		System.out.println("number of files : " + mflength  );
		
		if ( mflength > 0 ) { 
			
			String errr = matchingfiles[ mflength-1 ].getPath();
			String putData = "";
			
			File errfile = new File(errr);
			
			try {
				 putData = FileUtils.readFileToString( errfile );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			errpt.append(System.getProperty("line.separator"));
			errpt.append("<p>ZCC</p>");
			putData = putData.replace("ZCC", errpt.toString());
			
			//System.out.println(errpt.toString());
			
			try {
				FileUtils.writeStringToFile(errfile, putData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 
		
		else {
			
			String putData = "";
			errrpt = new File ( Pm.getOvdPath() + File.separator +"laberrrpt.html");
			
			try {
				 putData = FileUtils.readFileToString( errrpt );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			errpt.append(System.getProperty("line.separator"));
			StringBuilder sb = new StringBuilder();
			sb.append( "<style type='text/css'>" );
			sb.append("@page"+ System.getProperty("line.separator") );
			sb.append("{"+ System.getProperty("line.separator") );
			sb.append("    size: auto;"+ System.getProperty("line.separator") );
			sb.append("    margin: 0mm;"+ System.getProperty("line.separator") );
			sb.append("}"+ System.getProperty("line.separator") );
			sb.append(" body { margin: 0.875cm; }"+ System.getProperty("line.separator") );
			sb.append(" div { line-height: normal; }"+ System.getProperty("line.separator") );
			sb.append( "</style>" );
			sb.append( System.getProperty("line.separator") );
			putData = putData.replace("<p>XCX23</p>", sb.toString());			
			errpt.append("<p>ZCC</p>");
			//System.out.println(errpt.toString());
			
			putData = putData.replace("ZCC", errpt.toString());	
			//System.out.println(putData);
			
			String labpath = Pm.getOvdPath() + File.separator + "LabErr"+File.separator+ "lab"+Tdate.getPrintable()+".html";
			File Laberrf = new File(labpath);
			
			try {
				FileUtils.writeStringToFile(Laberrf, putData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		try {
			r.close();
			br.close();
			parent.detach();
			ptab.detach();
		} catch (IOException e) {
			// ignore
		}
	   }
		
		
		

			
		//String absPath = SystemHelpers.getRealPath(); // + "temp.xml";


			    
		
		return;
	}
	int numchck = 1; 
	int notesnum = 0;
    List<String> NotesList = new ArrayList<String>();
	
	StringBuilder Notess = new StringBuilder();
	
	
	public void read( BufferedReader br, StringBuilder errpt ){
		String line;
		int level = 0;

		System.out.println( "reading text" );

		try {
			while (( line = br.readLine()) != null ){
				
				//obr and obx counter
				int obrc = 0, obxc = 0;
				
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
					++obrc;
					parseOBR( line );
					level = 2;
					
				} else if ( line.startsWith( "OBX" ) || line.startsWith( "OBS" )){
					//System.out.println("pre OBX level: "+level);
					
					++obxc;
					parseOBX( line );
					level = 3;
					
				} else if ( line.startsWith( "NTE")){
					
					//System.out.println("pre NTE level: "+level);				
					parseNTE( line, obxc );
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
		
		errpt.append("<h2>Lab Name:"+LabName+"</h2>");
		errpt.append(System.getProperty("line.separator"));
		
		errpt.append("<table width=\"100%\" >");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<colgroup>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<col>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<col>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("</colgroup>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("<tr><td><b>Patient:</b> "+ ptName.getPrintableNameLFM() + " </td>");
		errpt.append(System.getProperty("line.separator"));
		

	}

	

	public void parsePV1( String line ){
		
		if ( ! line.startsWith( "PV1")) return;
		System.out.println( "in parsePV1()" );
		
		
		return;
	}
	

	
	public void parseOBR( String line ){
		
		boolean BATstat = false;
		
		if ( ! line.startsWith( "OBR")) return;
		System.out.println( "in parseOBR()" );
		
		String token[] = line.split( "\\|", 26 );
		if ( token.length < 26 ) System.out.println( "short line in OBR, tokens=" + token.length );
		
		Rec batRec = null;
		String miscabbr = "";
		Rec obsRec = null;
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
		String strprovider = token[16];																				//16 - provider 
																						//17 -
																						//18 -
																						//19 -
		String strFacility = token[20];													//20 - lab facility
																						//21 -
																						//22 -
																						//23 -
																						//24 - CH??
		String strStatus = token[25];													//25 - F??  final?
		
		int prh = strprovider.length() - strprovider.replaceAll("^","").length();
		String name[] = strprovider.split( "\\^", prh-1 );
		
		errpt.append(System.getProperty("line.separator"));
		String namee ="";
		if ( name[3].length() == 0 || name[3] == null ){
			namee = name[2]+","+name[1];			
		}else { namee = name[2]+","+name[3]+","+name[1]; }		
		errpt.append("<td><b>Provider:</b> "+ namee +" </td></tr>");
		errpt.append(System.getProperty("line.separator"));
		errpt.append("</table> ");
		errpt.append(System.getProperty("line.separator"));
		
		
		
		
		int pc = strBattery.length() - strBattery.replaceAll("^","").length();
		
		token = strBattery.split( "\\^", pc-1 );
		String type = "";
		if ( !(token.length < 3) ) { System.out.println( "too few battery fields found" );
			type = token[2]; }
		
		if ( !(type=="") ) { System.out.println( "too few battery fields found" );}
		
		String Abbr = token[0]	;
		String batDesc = token[1];
		
		errpt.append("<h4>OBR: </h4> ");
		//errpt.append(System.getProperty("line.separator"));
		
		if ( ! type.equals( "LN" )) System.out.println( "not loinc code?" );
		
		
		
		XMLElement xml = new XMLElement();
		FileReader reader = null;
			
		try {
			reader = new FileReader( Pm.getOvdPath() + File.separator + fn_config1 );
	    } catch (FileNotFoundException e) {
	    	System.out.println( "the.." + File.separator + fn_config1 + "file was not found:" );
	    	
	    }
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e ) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		final XMLElement e;
				
		 String Name = xml.getName();
		 //System.out.println(Name+" Name is config1");
		 final int NosChildren = xml.countChildren();
		 
		 e = xml.getElementByPathName(Name);
		
		for ( int i = 0; i < NosChildren; i++){
		
		  if ( e.getChildByNumber(i).getChildByNumber(0).getContent().equalsIgnoreCase(Abbr) ){
			  
		 if ( e.getChildByNumber(i).getChildByName("IS_PANEL").getContent().equalsIgnoreCase("TRUE") ){
			 
			 BATstat = true;
		 }}}
		
		if (BATstat) { 
			
		// look up lab batch by loinc code
		if ( Abbr.length() > 1 ){			
			batRec = LabBat.searchAbbr( Abbr); 
			//TODO create Bat on the go or find using other method?
	
			if ( batRec == null ){ System.out.println( "Bat Abbr " + Abbr + " not found." );
			
			LabBat p = new LabBat();
			
			p.setAbbr(Abbr);
			p.setDesc(batDesc);
			
			batRec = p.writeNew();
			
			obsRec = LabObsTbl.searchAbbr( Abbr );
			
			if ( !(obsRec == null) ){
			LabObsTbl labobstbl =  new LabObsTbl( obsRec );
			labobstbl.setHidden();
			
			System.out.println("The new Battery was deleted in LabObsTbl.");
		
			labobstbl.write(obsRec); 			
			
		   }
		  }
		 }	
		} else {
			
			miscabbr = Abbr;
			
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
			
			errpt.append("<p> Lab facility not found</p> ");
			errpt.append(System.getProperty("line.separator"));
		}
		
		//TODO - result status
		LabResult.ResultStatus resultStatus = LabResult.ResultStatus.NONE;
		if ( strStatus.startsWith( "F" )) resultStatus = LabResult.ResultStatus.FINAL;
		if ( strStatus.startsWith( "P" )) resultStatus = LabResult.ResultStatus.PENDING;
		if ( strStatus.startsWith( "I" )) resultStatus = LabResult.ResultStatus.IN_PROGRESS;
		
		
		order = new Order();
		order.entry( date1, batRec, miscabbr, resultStatus, orderID, specID, source, txtSource, condition, txtCondition, provRec, facRec );
		System.out.println("batrec, miscabbr: "+batRec+","+miscabbr);
		MrLog.postNew( patient.ptRec, Date.today(), "", MrLog.Types.LAB_NOTE, null );
		
	}
	

	
	public void parseNTE( String line, int obxc ){
		
		if ( !(obxc == numchck) ){ 
			
			NotesList.add(Notess.toString());
			Notess.setLength(0);
			
		}
		++notesnum;
				
		if ( ! line.startsWith( "NTE")) return;
		System.out.println( "in parseNTE()" );
		
		String token[] = line.split( "\\|", 4);
		if ( token.length < 3 ){ System.out.println( "short NTE segment, "+ token.length );
		errpt.append("short NTE segment @: "+ notesnum + "length is: "+ token.length );
		errpt.append(System.getProperty("line.separator")); }
		
																				//0- hdr
																				//1- deq#
																				//2- L
		String nteStr = token [3];  	                                   		//3- Text
		String nteStrt = deleteachar (nteStr, nteStr.length()-1);
		if ( nteStr.length() > 0 ) { Notess.append(nteStrt);
		
									Notess.append(System.getProperty("line.separator"));}
		
		numchck = obxc ;
	}
	
	
	
	public void parseOBX( String line ){
		
		if ( ! line.startsWith( "OBX")) return;
		System.out.println( "in parseOBX()" );
		errpt.append("<h4>OBX: </h4> ");
		String token[] = line.split( "\\|", 14 );
		if ( token.length < 14 ) { System.out.println( "short line in OBX, tokens=" + token.length );
		//errpt.append(System.getProperty("line.separator"));
		errpt.append("<p> Short line in Obx </p> ");
		//errpt.append(System.getProperty("line.separator")); 
		}
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
			errpt.append("<p> obs loinc code " + loinc + " not found. </p> ");
			errpt.append(System.getProperty("line.separator"));
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

		
		//TODO units - check whats wrong 
		String txtUnits = "";
		LabObsTbl.Units units = LabObsTbl.Units.get( strUnits.trim() );
		//System.out.println("units 1 is: "+units);
		if (( units == LabObsTbl.Units.NONE ) || ( units == LabObsTbl.Units.UNSPECIFIED )){
			units = LabObsTbl.Units.NONE;
			txtUnits = strUnits;
		}
		
		System.out.println("units2, txtunits: "+ units + ", "+ txtUnits);
		
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
		errpt.append("<h4>NTE: </h4> ");
		errpt.append(notesnum +" NTE lines (attempted)parsed. ");
		errpt.append(System.getProperty("line.separator"));		
		errpt.append("<h4>process Order: </h4> ");
		//errpt.append(System.getProperty("line.separator"));
		
	
		// make sure data is valid
		if ( ! Rec.isValid( patient.ptRec )){			
			System.out.println( "Invalid ptRec." );
			errpt.append("<p> Invalid ptRec </p> ");
			//errpt.append(System.getProperty("line.separator"));
			
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
				errpt.append("<p> Invalid obs rec </p> ");
				errpt.append(System.getProperty("line.separator"));
				continue;
			}
			
			
			LabResult lab = new LabResult();
			
			lab.setPtRec( patient.ptRec );
			
			lab.setDate( order.date );
			if ( Rec.isValid( order.batRec )) { lab.setLabBatRec( order.batRec );} else { lab.setmiscAbbr( order.miscabbr ); }
			
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
				note.setNoteText( NotesList.get(i));
				
				lab.setResultNoteReca(note.postNew(patient.ptRec));
				
				// post to MrLog
			    //MrLog.postNew( patient.ptRec, Date.today(), "", Notes.getMrLogTypes( noteClass ), lab.getResultNoteReca() );
				// log the action
				AuditLogger.recordEntry( Notes.getAuditLogActionNew( noteClass ), patient.ptRec, Pm.getUserRec(), lab.getResultNoteReca(), null );
				 
				//lab.setResultNoteText(Notes.toString()); 
				System.out.println(NotesList.get(i));}
			
			lab.setStatus( LabResult.Status.ACTIVE );
			lab.setValid( Validity.VALID );
			
			lab.postNew( patient.ptRec );
			++posted;
			System.out.println( "Posted one, " + posted );
			errpt.append("<p> Succesful post(s):"+posted+"</p> ");
			//errpt.append(System.getProperty("line.separator"));
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
