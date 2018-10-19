package palmed;

import usrlib.Rec;

public class Pm {

	
	public static String getMedPath(){
		String str = (String) SystemHelpers.getSessionAttribute( "medPath" );
		return ( str == null ) ? "": str;
	}
	
	public static void setMedPath( String path ){
		SystemHelpers.setSessionAttribute( "medPath", path );
		return;
	}
	
	public static String getOvdPath(){
		String str = (String) SystemHelpers.getSessionAttribute( "ovdPath" );
		return ( str == null ) ? "": str;
	}

	public static void setOvdPath( String path ){
		SystemHelpers.setSessionAttribute( "ovdPath", path );
		return;
	}
	
	public static String getSchPath(){
		String str = (String) SystemHelpers.getSessionAttribute( "schPath" );
		return ( str == null ) ? "": str;
	}

	public static void setSchPath( String path ){
		SystemHelpers.setSessionAttribute( "schPath", path );
		return;
	}
	
	
	
	
	
	// get/set ERxDemo flag
	public static void setERxDemo(){
		SystemHelpers.setSessionAttribute( "ERxDemo", ERxConfig.isDemo() ? "true": "false" );
		return;
	}
	
	public static boolean getERxDemo(){
		String str = (String) SystemHelpers.getSessionAttribute( "ERxDemo" );
		//System.out.println( "getERxDemo() str=" + str );
		return ((( str != null ) && str.equalsIgnoreCase( "true")) ? true: false );
	}
	
	
	
	
	
	// get/set Logged In status flag
	public static void setLoggedIn( boolean status){
		SystemHelpers.setSessionAttribute( "loggedin", status ? "true": "false" );
		return;
	}
	
	public static boolean getLoggedIn(){
		String str = (String) SystemHelpers.getSessionAttribute( "loggedin" );
		//System.out.println( "getLoggedIn() str=" + str );
		return ((( str != null ) && str.equalsIgnoreCase( "true")) ? true: false );
	}
	
	
	
	
	// user info
	public static void setUserRec( Rec rec ){
		SystemHelpers.setSessionAttribute( "userrec", rec );
		return;
	}
	
	public static Rec getUserRec(){
		Rec rec = (Rec) SystemHelpers.getSessionAttribute( "userrec" );
		//System.out.println( "getUserRec() rec=" + rec.getRec());
		return ( rec );
	}
	
	public static void setUserLogin( String login ){
		SystemHelpers.setSessionAttribute( "username", login );
		return;
	}
	
	public static String getUserLogin(){
		String s = (String) SystemHelpers.getSessionAttribute( "username" );
		//System.out.println( "getUserLogin() s=" + s );
		return ( s );
	}
	
	
	
	
}
