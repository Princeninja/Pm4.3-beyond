package palmed;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import palmed.PmLogin.PmLoginController;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.StructHelpers;
import usrlib.ZkTools;

/**
 * STDSOAP - Soap Note Standard Dictations
 * 
 * 
 * C data structure file - c/palmed/include/stdsoap.h
 * 
 * typedef struct {
 * 
 * DATE modDate; // last modified date 4 char Desc[40]; // description 40 long
 * txtrec[4]; // text records 16 char unused[3]; // unused 3 char Status; //
 * status flag 1
 * 
 * } STDSOAP;
 * 
 * sizecheck( 64, sizeof( STDSOAP ), "STDSOAP" );
 * 
 */

public class StdDictSoap {


	private final static String fn_stdsoap = "stdsoap.med";
	private final static String fn_stdtxt = "stdtxt.med";

	private final static int recordLength = 64;

	private class Offsets {
		public final static int ModDate = 0;
		public final static int Desc = 4;
		public final static int TextRec = 44;
		public final static int Unused = 61;
		public final static int Status = 63;
	}

	public class Status {
		public final static char Current = 'C';
		public final static char Removed = 'R';
		public final static char Edited = 'E';
	}


	public enum DisplayRecords {
		Active, Inactive, All
	}

	// fields
	private byte[] dataStruct;

	public StdDictSoap() {
		allocateBuffer();
	}

	public StdDictSoap(Rec rec) {
		// allocate space
		allocateBuffer();

		// read record
		read(rec);

	}

	/**
	 * getRecordLength
	 */
	public static int getRecordLength() {
		return (recordLength);
	}

	private void allocateBuffer() {
		// allocate data structure space
		dataStruct = new byte[recordLength];
	}

	public void setDataStruct(byte[] dataStruct) {
		this.dataStruct = dataStruct;
	}

	public byte[] getDataStruct() {
		return dataStruct;
	}

	private boolean read(Rec rec) {

		if ((rec == null) || (rec.getRec() < 2))
			SystemHelpers.seriousError("StdSoapDict.read() bad rec");

		RandomFile.readRec(rec, dataStruct, Pm.getMedPath(), fn_stdsoap,
				getRecordLength());

		return true;
	}

	public usrlib.Date getDate() {
		return StructHelpers.getDate(dataStruct, 0);
	}

	public void setDate(usrlib.Date date) {
		StructHelpers.setDate(date, dataStruct, 0);
	}

	public String getAbbr() {
		return StructHelpers.getString(dataStruct, Offsets.Desc, 40);
	}
	public void setAbbr(String abbr) {
		StructHelpers.setString(abbr,dataStruct, Offsets.Desc, 40);

	}

	/**
	 * getText()
	 * 
	 * Get actual SOAP text from the soap text file.
	 * 
	 * @param num
	 * @return Rec
	 */
	public String getText(int num) {

		// make sure num is in range
		if ((num < 0) || (num > 3))
			num = 0;
		// read and return text
		return readText(getTextRec(num));
	}

	public Rec getTextRec(int num) {
		// make sure num is in range
		if ((num < 0) || (num > 3))
			num = 0;
		return Rec.fromInt(dataStruct, Offsets.TextRec + (num * 4));
	}
	public void setTextRec(Rec rec, int num) {
		// make sure num is in range
		if ((num < 0) || (num > 3))
			num = 0;
		rec.toInt(dataStruct, Offsets.TextRec  + (num * 4));
	}

	/**
	 * readText()
	 * 
	 * Read the actual StdSOAP text from the soap text file.
	 * 
	 * @param Rec
	 * @return String
	 */
	public String readText(Rec txtrec) {

		if ((txtrec == null) || (txtrec.getRec() < 2))
			return "";

		RandomFile file = RandomFile.open(Pm.getOvdPath(), fn_stdtxt, 1,
				RandomFile.Mode.READONLY);

		byte[] hdrData = new byte[16];
		file.readBytes(hdrData, txtrec.getRec(), 16);

		int txtlen = StructHelpers.getInt(hdrData, 4);

		// read text
		byte[] txtData = new byte[txtlen];
		file.readBytes(txtData, txtrec.getRec() + 16, txtlen);
		file.close();

		String text = new String(txtData, 0, txtlen);

		return text;
	}

	public Rec writeText(String text) {

		if (text == null)
			text = "";

		RandomFile file = RandomFile.open(Pm.getOvdPath(), fn_stdtxt, 1,
				RandomFile.Mode.READWRITE);

		byte[] hdrData = StructHelpers.zeroBytes(new byte[16]);
		StructHelpers.setInt(text.length(), hdrData, 4);

		int txtrec = (int) file.seekToEnd();
		//log.debug("Writing header to location " + txtrec);
		
		// write header
		file.writeBytes(hdrData, txtrec, 16);

		int txtlen =text.length();
		// set text in text buffer
		byte[] txtData = new byte[txtlen];
		StructHelpers.setStringNoPad(text, txtData, 0, txtlen);

		// write text
		if (txtlen > 0){
			file.writeBytes(txtData, txtrec + 16, txtlen);
		}
		file.close();

		return new Rec(txtrec);
	}


	public Rec write( Rec rec ){
		
    	if (( rec == null ) || ( rec.getRec() < 2 )) 
    		SystemHelpers.seriousError( "pmUser.read() bad rec" );
					
		return RandomFile.writeRec(rec, dataStruct, Pm.getMedPath(), fn_stdsoap,
				getRecordLength());
	}
	
	
    public Rec newRec(){
    	
    	return RandomFile.writeRec(null, dataStruct, Pm.getMedPath(), fn_stdsoap,
				getRecordLength());
	}
    

    
	public char getStatus() {

		return (char) (StructHelpers.getByte(dataStruct, Offsets.Status) & 0xFF);
	}
	public void setStatusInactive() {

		setStatus(Status.Removed);
	}
	public void setStatusActive() {

		setStatus(Status.Current);
	}
	

	private void setStatus(char status) {

		StructHelpers.setByte( (byte)(status & 0xff), dataStruct, Offsets.Status );
	}

	public static boolean findAbbrRec(String abbr) {

		int rec; // record number
		int maxrec; // max record number

		StdDictSoap stdDict = new StdDictSoap();

		RandomFile file = RandomFile.open(Pm.getOvdPath(), fn_stdsoap,
				getRecordLength(), RandomFile.Mode.READONLY);
		maxrec = file.getMaxRecord();
		abbr = abbr.toLowerCase();
		for (rec = 2; rec <= maxrec; rec++) {
			file.getRecord(stdDict.dataStruct, rec);

			String recAbbr = stdDict.getAbbr().toLowerCase().trim();
			if(recAbbr.compareTo(abbr) == 0){
				//log.debug("Found duplicate abbreviation at rec " + rec);
				// found a record just like that, so bail out
				return true;
			}

		}
		//log.debug("No duplicate abbreviations found");
		return false;
	}

	public static int fillListbox(Listbox lb, DisplayRecords display,
			boolean showText) {

		int rec; // record number
		int maxrec; // max record number
		int fnd = 0; // number found

		StdDictSoap stdDict = new StdDictSoap();

		RandomFile file = RandomFile.open(Pm.getOvdPath(), fn_stdsoap,
				getRecordLength(), RandomFile.Mode.READONLY);
		maxrec = file.getMaxRecord();
		//log.debug("Found " + maxrec + " records");
		HashMap<String, Rec> map = new HashMap<String, Rec>();

		for (rec = 2; rec <= maxrec; rec++) {

			file.getRecord(stdDict.dataStruct, rec);

			char status = stdDict.getStatus();
			String abbr = stdDict.getAbbr();
			switch (display) {
			case All:
				if (status != Status.Edited) { // skip superseeded records 
					map.put(abbr, new Rec(rec));
					fnd++;
				}
				break;
			case Active:
				if (status == Status.Current) {
					map.put(abbr, new Rec(rec));
					fnd++;
				}
				break;
			case Inactive:
				if (status == Status.Removed) {
					map.put(abbr, new Rec(rec));
					fnd++;
				}
				break;
			}

		}
		// sort the values
		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		if (!showText) {
			for (String abbr : keys) {
				ZkTools.appendToListbox(lb, String.format("%-40.40s", abbr),
						map.get(abbr));
			}
		} else {

			for (String abbr : keys) {
				Listitem i;
				(i = new Listitem()).setParent(lb);
				Rec mapRecord = (Rec) map.get(abbr);
				i.setValue(mapRecord);
				new Listcell(abbr).setParent(i);
				if (file.getRecord(stdDict.dataStruct, mapRecord.getRec())) {
					for (int txtRec = 0; txtRec < 4; txtRec++) {
						String text = stdDict.getText(txtRec).trim();
						if (text.length() > 24) {
							text = text.substring(0, 24) + "...";
						}
						new Listcell(text).setParent(i);
					}
					if (display == StdDictSoap.DisplayRecords.All) {
						// show status
					char status = stdDict.getStatus();
					switch (status){
					case Status.Current:
								new Listcell("Current").setParent(i);
								break;
					case Status.Removed:
								new Listcell("Removed").setParent(i);
								break;
					default:
						break;
						
					}

				}

				}
			}
		}
		file.close();

		return (fnd);
	}

}
