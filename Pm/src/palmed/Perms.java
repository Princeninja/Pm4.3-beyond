package palmed;

public class Perms {
	
	
	
	enum Medical {
		VIEW( 0x0001 ),
		UPDATE( 0x0002),
		EDIT( 0x0004 ),
		PRESCRIPTIONS( 0x0008 ),
		TRANSCRIPTION( 0x0010 ),
		PATIENT_REPORTS( 0x0020 ),
		DATABASE_SEARCH( 0x0040 );
		
		private int code;
		
		Medical ( int code ){ this.code = code; }
		public int getCode(){ return this.code; }		
	}
	
	enum Financial {
		
		VIEW( 0x0001 ),
		POST( 0x0002),
		CORRECTIONS( 0x04 ),
		DAILY_REPORTS( 0x0008 ),
		PRACTICE_REPORTS( 0x0010 );

		private int code;
		
		Financial ( int code ){ this.code = code; }
		public int getCode(){ return this.code; }
	}

	enum Sys {
		
		USER_MANAGEMENT( 0x0001 ),
		PROBLEM_SOLVERS( 0x0002),
		MAINTENANCE( 0x04 ),
		BACKUP( 0x0008 ),
		REMOTE_ACCESS( 0x0010 );

		private int code;
		
		Sys ( int code ){ this.code = code; }
		public int getCode(){ return this.code; }
	}

	private int medPerms = 0;
	private int finPerms = 0;
	private int sysPerms = 0;
	private boolean loggedIn = false;
	
	
	public Perms(){
		
	}
	
	
	// set permissions in fields
	public void setFinPerms( int perms ){ this.finPerms = perms; }
	public void setMedPerms( int perms ){ this.medPerms = perms; }
	public void setSysPerms( int perms ){ this.sysPerms = perms; }
	
	// set logged in status
	public void setLoggedIn( boolean b ){ this.loggedIn = b; }
	
	// is permission granted?
	public boolean isPermitted( Perms.Financial request ){
		return (( finPerms & request.getCode() ) != 0 ) ? true: false;
	}
	public boolean isPermitted( Perms.Medical request ){
		//System.out.println( "medperms=" + medPerms + ",  req=" + request.getCode());
		return (( medPerms & request.getCode() ) != 0 ) ? true: false;
	}
	public boolean isPermitted( Perms.Sys request ){
		return (( sysPerms & request.getCode() ) != 0 ) ? true: false;
	}

	public boolean isLoggedIn(){
		return this.loggedIn;
	}
	
	
	
	
	/**
	 * Some STATIC methods to help out.
	 * 
	 */
	
	public static boolean isSessionLoggedIn(){
		Perms perms = (Perms)(SystemHelpers.getSessionAttribute("Perms"));
		if ( perms == null ) SystemHelpers.seriousError( "Perms not in Session" );
		return perms.isLoggedIn();
	}
	
	public static boolean isRequestPermitted( Perms.Financial req ){
		Perms perms = (Perms)(SystemHelpers.getSessionAttribute("Perms"));
		if ( perms == null ) SystemHelpers.seriousError( "Perms not in Session" );
		return perms.isPermitted( req );
	}
	
	public static boolean isRequestPermitted( Perms.Medical req ){
		Perms perms = (Perms)(SystemHelpers.getSessionAttribute("Perms"));
		if ( perms == null ) SystemHelpers.seriousError( "Perms not in Session" );
		return perms.isPermitted( req );
	}
	
	public static boolean isRequestPermitted( Perms.Sys req ){
		Perms perms = (Perms)(SystemHelpers.getSessionAttribute("Perms"));
		if ( perms == null ) SystemHelpers.seriousError( "Perms not in Session" );
		return perms.isPermitted( req );
	}
	
	
}
