package palmed;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;


import usrlib.Date;
import usrlib.RandomFile;
import usrlib.RandomFile.Mode;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.StructHelpers;
import usrlib.Time;






//Format
//
// struct {
// 0	Date date;
// 4	Time time;
// 6	short action;
// 8	Rec ptRec;
// 12	Rec userRec;
// 16	Reca reca;
// 20	char unused[4];
// 24	char desc[40];
// } Entry
// sizecheck( 64 );


	
	


public class AuditLog {

	public enum Action {
		
		PT_NEW( "Patient - New", 1 ),	
		PT_EDIT( "Patient - Edit", 2 ),
		PT_DELETE( "Patient - Delete", 3 ),
		PT_PRINT( "Patient - Print", 4 ),
		
		MED_NEWRX( "Meds - New RX", 10 ),
		MED_ADD( "Meds - Add", 11 ),
		MED_DC( "Meds - Discontinue", 12 ),
		MED_EDIT( "Meds - Edit", 13 ),
		MED_PRINT( "Meds - Print", 14 ),
		MED_PENDING( "Meds - Pending", 15 ),
		
		PAR_ADD( "PAR/Allergy - Add", 20 ),
		PAR_EDIT( "PAR/Allergy - Edit", 21 ),
		PAR_DELETE( "PAR/Allergy - Delete", 22 ),
		PAR_PRINT( "PAR/Allergy - Print", 23 ),
		
		SOAP_ADD( "SOAP - Add", 30 ),
		SOAP_EDIT( "SOAP - Edit", 31 ),
		SOAP_DELETE( "SOAP - Delete", 32 ),
		SOAP_PRINT( "SOAP - Print", 33 ),
		
		VITALS_ADD( "Vitals - Add", 40 ),
		VITALS_EDIT( "Vitals - Add", 41 ),
		VITALS_DELETE( "Vitals - Add", 42 ),
		VITALS_PRINT( "Vitals - Add", 43 ),
		
		USER_ADD( "User - Add", 50 ),
		USER_EDIT( "User - Edit", 51 ),
		USER_DELETE( "User - Delete", 52 ),
		USER_PRINT( "User - Print", 53 ),
		USER_LOGON_LOCAL( "User - Logon, local", 54 ),
		USER_LOGON_REMOTE( "User - Logon, remote", 55 ),
		USER_MANAGE( "User - Management Screen", 56 ),
	
		ER_PORTAL( "ER Portal - Access", 60 ),
		PT_PORTAL( "Patient Portal - Access", 70 ),
		PT_CHART( "PtChart - Access", 80 ),
		PT_SOAPSHT( "Soap Sheet - Access", 90 ),
		
		AUDIT_ACCESS( "Audit Log - Access", 100 ),
		AUDIT_PRINT( "Audit Log - Print", 101 ),
	
		ERX_ACCESS( "ERX - Access", 110 ),
		ERX_CONFIG( "ERX - Config", 111 ),
		
		PMIMPORT_NEW( "PmImport - New", 120 ),
		PMIMPORT_DELETE( "PmImport - Delete", 121 ),
		
		PROB_ADD( "Problem List - Add", 130 ),
		PROB_EDIT( "Problem List - Edit", 131 ),
		PROB_DELETE( "Problem List - Delete", 132 ),
		PROB_PRINT( "Problem List - Print", 133 ),
		PROB_RESOLVE( "Problem List - Resolve", 134 ),
		
		PROV_ADD( "Provider - Add", 140 ),
		PROV_EDIT( "Provider - Edit", 141 ),
		PROV_DELETE( "Provider - Delete", 142 ),
		PROV_PRINT( "Provider - Print", 143 ),
		
		LEDGER_ACCESS( "Ledger - Access", 150 ),
		
		ORDERS_ADD( "Orders - Add", 160 ),
		ORDERS_EDIT( "Orders - Edit", 161 ),
		ORDERS_DELETE( "Orders - Delete", 162 ),
		ORDERS_PRINT( "Orders - Print", 163 ),
		ORDERS_CLOSE( "Orders - Satisfy/Close", 164 ),

		HISTORY_NEW( "Pt History - New", 170 ),
		HISTORY_EDIT( "Pt History - Edit", 171 ),
		
		IMMUNIZATIONS_ADD( "Immunizations - Add", 180 ),
		IMMUNIZATIONS_EDIT( "Immunizations - Edit", 181 ),
		IMMUNIZATIONS_DELETE( "Immunizations - Delete", 182 ),
		IMMUNIZATIONS_PRINT( "Immunizations - Print", 183 ),
		IMMUNIZATIONS_EXPORT( "Immunizations - Export", 184 ),

		INTERVENTIONS_ADD( "Immunizations - Add", 190 ),
		INTERVENTIONS_EDIT( "Immunizations - Edit", 191 ),
		INTERVENTIONS_DELETE( "Immunizations - Delete", 192 ),
		INTERVENTIONS_PRINT( "Immunizations - Print", 193 ),
		INTERVENTIONS_EXPORT( "Immunizations - Export", 194 ),
		
		PT_EDUCATION_PRINT( "Patient Education - Print", 200 ),
		
		PROCEDURES_ADD( "Procedures - Add", 210 ),
		PROCEDURES_EDIT( "Procedures - Edit", 211 ),
		PROCEDURES_DELETE( "Procedures - Delete", 212 ),
		PROCEDURES_PRINT( "Procedures - Print", 213 ),
		
		NEWCROP_STATUS( "Newcrop - Status", 220 ),
		NEWCROP_COMPOSE( "Newcrop - Compose", 221 ),
		
		NURSING_ADD( "Nursing Notes - Add", 230 ),
		NURSING_EDIT( "Nursing Notes - Edit", 231 ),
		NURSING_DELETE( "Nursing Notes - Delete", 232 ),
		NURSING_PRINT( "Nursing Notes - Print", 233 ),
		
		TREATMENT_ADD( "Treatment Notes - Add", 240 ),
		TREATMENT_EDIT( "Treatment Notes - Edit", 241 ),
		TREATMENT_DELETE( "Treatment Notes - Delete", 242 ),
		TREATMENT_PRINT( "Treatment Notes - Print", 243 ),
		
		LABNOTES_ADD( "Lab Notes - Add", 250 ),
		LABNOTES_EDIT( "Lab Notes - Edit", 251 ),
		LABNOTES_DELETE( "Lab Notes - Delete", 252 ),
		LABNOTES_PRINT( "Lab Notes - Print", 253 ),
		
		XRAYNOTES_ADD( "Xray Notes - Add", 260 ),
		XRAYNOTES_EDIT( "Xray Notes - Edit", 261 ),
		XRAYNOTES_DELETE( "Xray Notes - Delete", 262 ),
		XRAYNOTES_PRINT( "Xray Notes - Print", 263 ),
		
		STDDICT_ADD( "Standard dictation - Add", 270 ),
		STDDICT_EDIT( "Standard dictation - Edit", 271 ),
		STDDICT_DELETE( "Standard dictation - Delete", 272 ),
		STDDICT_PRINT( "Standard dictation - Print", 273 ),
		
		DGNCODE_ADD( "Diagnosis code - Add", 280),
		DGNCODE_EDIT( "Diagnosis code - Edit", 281),
		DGNCODE_DELETE( "Diagnosis code - Delete", 282),
		DGNCODE_PRINT( "Diagnosis code - Print", 283),
		
		PPO_ADD( "Insurance code - Add", 290),
		PPO_EDIT("Insurance code - Edit",291),
		PPO_DELETE("Insurance code - Delete",292),
		PPO_RESTORE("Insurance code - Restore",293),
		PPO_PRINT("Inusrance code - Print",294),
		
		PHRDS_ADD( "Pharmarcy - Add", 300),
		PHRDS_EDIT( "Pharmarcy - Edit", 301),
		PHRDS_DELETE( "Pharmarcy - Delete", 302),
		PHRDS_PRINT( "Pharmarcy - Print", 303),
		
		SRVCODE_ADD( "Service code - Add", 310),
		SRVCODE_EDIT( "Service code - Edit", 311),
		SRVCODE_DELETE( "Service code - Delete", 312),
		SRVCODE_PRINT( "Service code - Print", 313),
		SRVCODE_RESTORE("Service code - Restore", 314),
		
		PHYEXAM_ADD( "Physical Exam - Add", 320),
		PHYEXAM_EDIT( "Physical Exam - Edit", 321),
		
		UNDEFINED( "Undefined", 32767 );
		
		
		private String label;
		private int code;
	
		private static final Map<Integer, Action> lookup = new HashMap<Integer,Action>();
		
		static {
			for ( Action r : EnumSet.allOf(Action.class))
				lookup.put(r.getCode(), r );
		}
	
	
		Action ( String label, int code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public int getCode(){ return this.code; }
		public static Action get( int code ){ return lookup.get( code ); }
	
	}

	private byte[] dataStruct;
	private static final int recLength = 64;	// record length
	private String path;
	private String fname;				// audit log file name
	private int month;
	private int year;
	
	
	
	
	
	
	
	
	private AuditLog( int month, int year ){
		
		dataStruct = new byte[recLength];
		
		this.month = month;
		this.year = year;

		// verify valid month and year
		if (( month < 1 ) || ( month > 12 )) month = 1;
		if (( year < 2011 ) || ( year > Date.today().getYear())) year = Date.today().getYear();
		
		path = Pm.getMedPath();
		fname = makeFilename( month, year );
		return;
	}

	
	
	
	
	
	
	

	public static String makeFilename( int month, int year ){
		return String.format( "audit%02d%02d.log", year % 100, month );
	}
	
	public static int getRecordLength(){
		return recLength;
	}
	
	
	
	
	
	
	

	
	public static AuditLog open( int month, int year ){

		AuditLog log = new AuditLog( month, year );		
		return log;
	}

	
	
	public static void fillListbox( Listbox lbox, int month, int year, AuditLog.Action action, Rec userRec, Rec ptRec ){
		
		AuditLog log = new AuditLog( month, year );
		
		RandomFile file = new RandomFile( log.path, log.fname, recLength, Mode.READONLY );
		int maxrec = file.getMaxRecord();

		for ( int rec = 2; rec <= maxrec; ++rec ){

			file.getRecord( log.dataStruct, rec );
			
			// is date in range?
			
			// is this action selected?
			if (( action != null ) && ( log.getAction() != action )) continue;
				
			// is this user selected?
			if ( Rec.isValid( userRec ) && ( ! userRec.equals( log.getUserRec()))) continue;
		
			// is this patient selected?
			if ( Rec.isValid( ptRec ) && ( ! ptRec.equals( log.getPtRec()))) continue;

			
			
			
			// create new Listitem and add cells to it
			Listitem i;
			(i = new Listitem()).setParent( lbox );
			i.setValue( new Rec( rec ));
			
			new Listcell( log.getDate().getPrintable(11)).setParent( i );
			new Listcell( log.getTime().getPrintable()).setParent( i );
			
			String strUser = "";
			Rec uRec = log.getUserRec();
			if ( Rec.isValid( uRec )) strUser = pmUser.getUser( uRec );
			new Listcell( strUser ).setParent( i );
			
			String s = Rec.isValid( log.getPtRec()) ? (new DirPt(log.getPtRec())).getName().getPrintableNameLFM(): "";
			new Listcell( s ).setParent( i );
			
			new Listcell( log.getAction().getLabel()).setParent( i );
			new Listcell( log.getNote()).setParent( i );		
		}

		return;
	}
	
	
	
	
	

	
	public Date getDate() {
		return new Date().fromBCD( dataStruct, 0 );
	}
	
	public void setDate( Date date ){
		if ( date != null ) date.toBCD( dataStruct, 0 );
	}

	
	
	
	public Time getTime() {
		return Time.fromBCD( dataStruct, 4, Time.BCDLEN.HHMM );
	}
	
	public void setTime( Time time ){
		if ( time != null ) time.toBCD( dataStruct, 4, Time.BCDLEN.HHMM );
	}

	
	
	
	public Rec getUserRec(){
		return Rec.fromInt( dataStruct, 12 );
	}
	
	public void setUserRec( Rec rec ){
		if ( Rec.isValid( rec )) rec.toInt( dataStruct, 12 );
	}

	
	
	
	public Rec getPtRec(){
		return Rec.fromInt( dataStruct, 8 );
	}
	
	public void setPtRec( Rec rec ){
		if ( Rec.isValid( rec )) rec.toInt( dataStruct, 8 );
	}
	
	
	
	

	public Reca getReca(){
		return Reca.fromReca( dataStruct, 16 );
	}
	
	public void setReca( Reca reca ){
		if ( Reca.isValid( reca )) reca.toReca( dataStruct, 16 );
	}
	
	
	// This writes to the same spot as get/setReca() above.  Done to allow me to save a Rec or Reca here depending on the need
	public Rec getRec(){
		return Rec.fromInt( dataStruct, 16 );
	}
	
	public void setRec( Rec rec ){
		if ( Rec.isValid( rec )) rec.toInt( dataStruct, 16 );
	}
	
	
	
	
	
	public String getNote(){
		return StructHelpers.getString( dataStruct, 24, 40 );
	}
	
	public void setNote( String s ){
		StructHelpers.setStringPadded(( s == null ) ? "": s, dataStruct, 24, 40 );
	}
	
	
	
	

	public AuditLog.Action getAction() {
		AuditLog.Action action = AuditLog.Action.get( StructHelpers.getShort( dataStruct, 6 ));
		return ( action == null ) ? AuditLog.Action.UNDEFINED: action;
	}
	
	public void setAction( AuditLog.Action action ){
		StructHelpers.setShort(( action == null ) ? AuditLog.Action.UNDEFINED.getCode(): action.getCode(), dataStruct, 6 );
	}
	
	
	
	

	public static AuditLog read( int month, int year, Rec logRec ){
			
		AuditLog log = new AuditLog( month, year );
		boolean flg = RandomFile.readRec( logRec, log.dataStruct, log.path, log.fname, recLength );
		return flg ? log: null;
	}
	
	
	
	

	public static void recordEntry( Date date, Time time, Action action, Rec ptRec, Rec userRec, Object /*Rec or Reca*/ reca, String desc ){
		
		int month = date.getMonth();
		int year = date.getYear();
		
		AuditLog log = new AuditLog( month, year );

		log.setDate(date);
		log.setTime(time);
		log.setAction(action);
		log.setUserRec(userRec);
		log.setPtRec(ptRec);
		log.setNote(desc);
		if ( reca instanceof Reca ) log.setReca((Reca) reca);
		if ( reca instanceof Rec ) log.setRec((Rec) reca);
		
		log.writeNew();
		return;
	}
	
	
	
		
		


	public boolean write( Rec rec ){
		
		if ( RandomFile.writeRec( rec, dataStruct, path, fname, recLength ) == null )
			System.out.println( "AuditLog() - write() write error." );
		return true;
	}
	
	
	public boolean writeNew(){
		
		if ( RandomFile.writeRec( null, dataStruct, path, fname, recLength ) == null )
			System.out.println( "AuditLog() - writeNew write error." );
		return true;
	}

	public static boolean init( int month, int year ){
		
		boolean ret = false;
		AuditLog log = new AuditLog( month, year );
		
		if ( ! ( new File( log.path + '/' + log.fname )).exists()){
			RandomFile file = RandomFile.open( log.path, log.fname, recLength, Mode.CREATE );
			if ( file == null ) SystemHelpers.seriousError( "cannot create file" );
			file.init();
			file.close();
			System.out.println( "AuditLog() - new log file created: " + log.fname );
			ret = true;
		}
		
		return ret;
	}
	


}
