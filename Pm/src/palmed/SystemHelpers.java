package palmed;

//import java.io.IOException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import palmed.Perms;

public class SystemHelpers {
	
	
	
	public static void initApp(){
		


		// set OSType
		SystemHelpers.setSessionAttribute( "OSType", SystemHelpers.determineOSType());

		// set up session Perms (permissions) object
		SystemHelpers.setSessionAttribute( "Perms", new Perms());
			
		return;
	}

	
	
	
	
	
	
	
	
	
	// These methods deal with Operating System types
	
	private static String determineOSType(){
		
/*//THIS WAS MY BRUTE FORCE METHOD TILL I FOUND THE METHOD BELOW
		int ret = 0;
		String cmd = "/bin/uname";
				
		// Assumption - on a Unix style OS, /bin/uname is present and works.
		//    On windows, it is not present and will throw the IOException.
		
		try {
			Process p = Runtime.getRuntime().exec( cmd );
			ret = p.waitFor();
			
		} catch (IOException e1) {
			ret = -1;		// uname didn't work
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println( "isWinOS() ret=" + ret );

		return ( ret == 0 ) ? "Unix": "Windows";
*/
		
		String OSType = ( System.getProperty("os.name").indexOf( "Win" ) < 0 ) ? "Unix": "Windows";
		System.out.println( "SystemHelpers.determineOSType() = " + OSType );

		return OSType;
	}
	
	
	
	public static String getOSType(){
		return (String) Sessions.getCurrent().getAttribute( "OSType" );
	}
	
	public static boolean isWinOS(){
		return ((String)( Sessions.getCurrent().getAttribute( "OSType" ))).equals( "Windows" );
	}

	public static boolean isUnixOS(){
		return ((String)( Sessions.getCurrent().getAttribute( "OSType" ))).equals( "Unix" );
	}


	
	
	
	/**
	 * getRealPath()
	 * 
	 * Returns the true absolute path to the web page's apparent root.
	 * 
	 */
	public static String getRealPath(){
		String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/");
		return ( path );
	}
	
	
	
	
	
	/**
	 * setSessionAttribute() - Sets a session attribute
	 * 
	 * @param Object
	 * @return void
	 */
	public static void setSessionAttribute( String name, Object obj ){
		Sessions.getCurrent().setAttribute( name, obj );
	}
	
	/**
	 * getSessionAttribute() - Gets a session attribute
	 * 
	 * @param Object
	 * @return void
	 */
	public static Object getSessionAttribute( String name ){
//		return Sessions.getCurrent().getAttribute( name );
		return Executions.getCurrent().getSession().getAttribute( name );
	}
	
	
	
	
	
	/**
	 * seriousError() - Puts up a serious error message and logs it.
	 * 
	 * @param String
	 * @return void
	 */
	public static void seriousError( String s ){
		try {
			Messagebox.show( s );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try {
			Messagebox.show( s );
			// TODO log the error
			// TODO (recovery or fail?)
			// TODO show some of stack
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	
	
	/**
	 * seriousError() - Puts up a serious error message and logs it.
	 * 
	 * @param String
	 * @return void
	 */
	public static void msgAccessDenied( String s ){
		try {
			Messagebox.show( s );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try {
			Messagebox.show( s );
			// TODO log the error
			// TODO (recovery or fail?)
			// TODO show some of stack
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public static void msgAccessDenied(){ msgAccessDenied( "Access not authorized." ); }
	
	
	
	
	
	/**
	 * setLockStub() - TEMPORARY STUBBED VERSION PLACEHOLDER FOR SOME SORT OF ATOMIC ACTION / FILE LOCKING
	 * 
	 */
	public static void setLockStub(){
		return;
	}
	
	public static void releaseLockStub(){
		return;
	}
}
