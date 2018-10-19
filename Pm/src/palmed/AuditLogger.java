package palmed;


import usrlib.Date;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;




public class AuditLogger {


	
/*
	class Entry {
		
		Date date;			// date item entered
		Time time;			// time item entered
		
		AuditLog.Action action;		// action performed
		
		Rec ptRec;			// patient record number
		Rec userRec;		// user record number
		Reca reca;
		
		String desc;		// description
		
		
		Entry( Date date, Time time, AuditLog.Action action, Rec ptRec, Rec userRec, Reca reca, String desc ){
			this.date = date;
			this.time = time;
			this.action = action;
			this.ptRec = ptRec;
			this.userRec = userRec;
			this.reca = reca;
			this.desc = desc;
		}
	}
*/
	
	
	static AuditLogger instance;		// one and only instance of this class
	static Date date;
	
	
	
	
	private AuditLogger(){
		System.out.println( "AuditLogger() object created, instance: " + instance.toString());
		return;
	}
	

	/**
	 * getInstance()
	 * 
	 * Instantiates the class if not already done.
	 * 
	 */
	public static AuditLogger getInstance(){
		
		//if ( ! ( instance instanceof AuditLogger )){
		if ( instance == null ){
			instance = new AuditLogger();			
		}
		
		return instance;
	}
	
	
	
	/**
	 * start()
	 * 
	 */
	public static void start(){
		
		if ( instance != null ) return;
		
		
		date = Date.today();
		
		System.out.println( "AuditLogger() - starting" );
		
		// create file if doesn't exist
		AuditLog.init( date.getMonth(), date.getYear());
	}
	
	/**
	 * restart()
	 * 
	 * Restart AuditLogger if new date.
	 * 
	 */
	public static void restart(){
		System.out.println( "AuditLogger() - restart" );

	}
	
	
	
	
	
	public static void recordEntry( AuditLog.Action action, Rec ptRec, Rec userRec, Object /*Rec or Reca*/ reca, String desc ){

		Date date = Date.today();
		Time time = Time.now();
		
		// is date still the same?
		if ( AuditLogger.date.compare( date ) != 0 ) restart();

		
		AuditLog.recordEntry( date, time, action, ptRec, userRec, reca, desc);
	}
	
		
		
/*
		
		// set data in data struct
		byte[] dataStruct = new byte[recLength];
		date.toBCD( dataStruct, 0 );
		time.toBCD2( dataStruct, 4 );
		StructHelpers.setUShort( action.getCode(), dataStruct, 6 );
		if ( Rec.isValid( ptRec )) ptRec.toInt( dataStruct, 8 );
		if ( Rec.isValid( userRec )) userRec.toInt( dataStruct, 12 );
		if ( Reca.isValid( reca )) reca.toReca( dataStruct, 16 );
		StructHelpers.setStringPadded(((desc == null) ? "": desc ), dataStruct, 24, 40 );

		
		if ( RandomFile.writeRec( null, dataStruct, path, fname, recLength ) == null )
			System.out.println( "AuditLogger() - recordEntry write error." );
		
		return;
	}
*/
	
	
}
