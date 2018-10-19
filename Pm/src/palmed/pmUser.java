package palmed;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import usrlib.*;


/*
 * 0  char Username[10]
 * 10 char Password[10]
 * 20 long usracc
 * 24 DATE sgndte
 * 28 TIME sgntim
 * 30 ushort reserved
 * 32 ushort console
 * 34 unused[2]
 * 36 name[40]
 * 76 unused[51]
 * 127 byte valid
 * 
 */
/*  this defines the user map record structure  */

//typedef struct {
//
// 0	char    usrnam[10];             /*  user's login name  */
// 10	char    paswrd[10];             /*  user's password  */
//
// 20	long    usracc;                 /*  user's access map  */
//
// 24	DATE    sgndte;                 /*  user's last signon date  */
// 28	TIME    sgntim;                 /*  user's last signon time  */
//
// 30	USRT    reserved;               /*  unused field ( was time)  */
//
// 32	USRT    console;                /*  user's console number  */
// 34	char    unused1;					// unused
// 35	byte	role;					// user's role in palmed

// 36	Name    Name;					// user's name
//
// 100	char    unused[8];             // unused area
//
// 108	Rec		provRec;				// provider this user is
// 112	long	medPerms;				// medical permissions
// 116	long	finPerms;				// financial permissions
// 120	long	sysPerms;				// system permissions
//
// 124	char	unused[3];				// unused
// 127	byte    valid;                  /*  validity byte  */
//
//	} USRMAP;
//
///*      sizecheck( 128, sizeof( USRMAP ), "USRMAP" ); */


public class pmUser {
	
	private static final String fn_usermap = "usermap.ovd";
	private static final int recordLength = 128;
	
	private Rec userRec = null;
	private Rec maxrec = null;
	private RandomFile file = null;
	
	
    public byte[] dataStruct;
    
    enum Status {
    	ACTIVE, INACTIVE, REMOVED
    }
    
    enum Role {
    	
    	CLERICAL('C', "Clerical"),
    	MANAGER('M', "Manager"),
    	ADMINISTRATOR('A', "Administrator"),
    	NURSE('N', "Nurse"),
    	MIDLEVEL('L', "Midlevel"),
    	PHYSICIAN('P', "Physician"),
    	EMERGENCY('E', "Emergency"),
    	UNSPECIFIED('U', "Unspecified");
    	
    	private static final Map<Character, Role> lookup = new HashMap<Character,Role>();
    	
    	static {
    		for ( Role r : EnumSet.allOf(Role.class))
    			lookup.put(r.getCode(), r );
    	}

    	
    	private char	code;
    	private String	desc;
    	
       	Role( char code, String desc ){
    		this.code = code;
    		this.desc = desc;
    	}    
    	
    	public char getCode(){ return code; }
    	public String getDesc(){ return desc; }
    	public static Role get( char code ){ return lookup.get( code ); }
    }

    

	String userName;
	String password;
	String description;
	
	//Constructors
	//
	
	public pmUser(){
		super();
		allocateBuffer();
				this.userRec = new Rec( 0 );
	}
	
	public pmUser( String user, String password, Name name ){
		super();
		allocateBuffer();
		this.userRec = new Rec( 0 );
		setUser( user );
		setPassword( password );
		setName( name );
	}

	public pmUser( Rec userRec ){
		super();
		allocateBuffer();
		this.userRec = userRec;
		read( userRec );
	}
	
	
	
	
	
	
	
	
	
    private void allocateBuffer(){
        // allocate data structure space
        dataStruct = new byte[recordLength];
    }

    public void setDataStruct(byte[] dataStruct) {
        this.dataStruct = dataStruct;
    }

    public byte[] getDataStruct() {
        return dataStruct;
    }

    /**
     * getRecordLength
     */
    public static int getRecordLength() {
        return ( recordLength );
    }

    
    /**
     * getRec()
     * 
     * Get this entry's record number
     * 
     * @return Rec
     */
    public Rec getRec(){
    	// this needs to return a new object each time so that there are no
    	// data inconsistencies as we iterate this table
    	return new Rec( userRec.getRec());
    }
    
    
    
   
    
    
    /**
     * getUser()
     *
     * Gets the user's logon name.
     * 
     * @param none
     * @return String
     */
    public String getUser() {
        return new String( dataStruct, 0, 10 ).trim();
    }

    /**
     * setUser()
     *
     * Sets the user's logon name.
     * 
     * @param String
     * @return void
     */
    public void setUser( String s ) {
    	// enforce uppercase
        StructHelpers.setStringPadded( s.toUpperCase(), dataStruct, 0, 10 );
    }

    /**
     * getUser()
     *
     * Static version reads user's record and gets the user's abbreviation or logon name.
     * 
     * @param rec
     * @return String
     */
    public static String getUser( Rec rec ) {
    	String str = "";
    	if (( rec != null ) && ( rec.getRec() > 1 ))
    		str = (new pmUser( rec )).getUser();
        return str;
    }

   
    
    

	
	
    /**
     * getPassword()
     *
     * Gets the user's Password.
     * 
     * @param none
     * @return String
     */
    public String getPassword() {
        return new String( dataStruct, 10, 10 ).trim();
    }

    /**
     * setPassword()
     *
     * Sets the user's Password.
     * 
     * @param String
     * @return void
     */
    public void setPassword( String s ) {
        StructHelpers.setStringPadded( s, dataStruct, 10, 10 );
    }

    
    

	
	
	
    /**
     * getName()
     *
     * Gets the user's Name object.
     * 
     * @param none
     * @return Name
     */
    public Name getName() {
        return new Name( dataStruct, 36 );
    }

    /**
     * setName()
     *
     * Sets the user's Name object.
     * 
     * @param Name
     * @return void
     */
    public void setName( Name n ) {
    	n.toData(dataStruct, 36 );
        //StructHelpers.setStringPadded( s, dataStruct, 36, 40 );
    }

    /**
     * getName()
     *
     * Static version reads user's record and gets the user's Name object.
     * 
     * @param rec
     * @return Name
     */
    public static Name getName( Rec rec ) {
        return new pmUser( rec ).getName();
    }

    
    
    
    // DATE sgndte;			// last signon date, offset 24    
    
    /**
     * getSignonDate()
     * 
     * Get last signon date from dataStruct.
     * 
     * @param none
     * @return usrlib.Date
     */
    public usrlib.Date getSignonDate(){
       	return StructHelpers.getDate( dataStruct, 24 );
    }

    /**
     * setSignonDate()
     * 
     * Set last signon date in dataStruct.
     * 
     * @param usrlib.Date		last signon date
     * @return void
     */
    public void setSignonDate( usrlib.Date date ){
        StructHelpers.setDate( date, dataStruct, 24 );
        return;
    }

    /**
     * setSignonDate()
     * 
     * Set today as last signon date in dataStruct.
     * 
     * @param none
     * @return void
     */
    public void setSignonDate(){
        setSignonDate( usrlib.Date.today());
        return;
    }


    
    
    


    // TIME sgntim;			// last signon time, offset 28    
    
    /**
     * getSignonTime()
     * 
     * Get last signon time from dataStruct.
     * 
     * @param none
     * @return usrlib.Time
     */
    public usrlib.Time getSignonTime(){
       	return Time.fromBCD( dataStruct, 28, Time.BCDLEN.HHMM );
    }

    /**
     * setSignonTime()
     * 
     * Set last signon time in dataStruct.
     * 
     * @param usrlib.Time		last signon time
     * @return void
     */
    public void setSignonTime( usrlib.Time time ){
        time.toBCD( dataStruct, 28, Time.BCDLEN.HHMM );
        return;
    }

    /**
     * setSignonTime()
     * 
     * Set today as last signon time in dataStruct.
     * 
     * @param none
     * @return void
     */
    public void setSignonTime(){
        setSignonTime( usrlib.Time.now());
        return;
    }


    
    
    


    // long medPerms;			// financial perms bitmask, offset 76    
    
    /**
     * getMedPerms()
     * 
     * Get user's medical permissions from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getMedPerms(){
       	
       	return StructHelpers.getInt( dataStruct, 112 );
    }

    /**
     * setMedPerms()
     * 
     * Set user's medical permissions in dataStruct.
     * 
     * @param int perms		user's permissions
     * @return void
     */
    public void setMedPerms( int perms ){
    	StructHelpers.setInt( perms, dataStruct, 112 );
        return;
    }


    
    // long finPerms;			// financial perms bitmask, offset 80    
    
    /**
     * getFinPerms()
     * 
     * Get user's financial permissions from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getFinPerms(){
       	
       	return StructHelpers.getInt( dataStruct, 116 );
    }

    /**
     * setFinPerms()
     * 
     * Set user's financial permissions in dataStruct.
     * 
     * @param int perms
     * @return void
     */
    public void setFinPerms( int perms ){
    	StructHelpers.setInt( perms, dataStruct, 116 );
        return;
    }


    
    

    // long sysPerms;			// system perms bitmask, offset 84    
    
    /**
     * getSysPerms()
     * 
     * Get user's system permissions from dataStruct.
     * 
     * @param none
     * @return int
     */
    public int getSysPerms(){
       	
       	return StructHelpers.getInt( dataStruct, 120 );
    }

    /**
     * setSysPerms()
     * 
     * Set user's system permissions in dataStruct.
     * 
     * @param int perms		user's permissions
     * @return void
     */
    public void setSysPerms( int perms ){
    	StructHelpers.setInt( perms, dataStruct, 120 );
        return;
    }


    
    

    // char role;			// user role, offset 35   
    
    /**
     * getRole()
     * 
     * Get user's role from dataStruct.
     * 
     * @param none
     * @return usrlib.Time
     */
    public pmUser.Role getRole(){
       	pmUser.Role role = pmUser.Role.get( (char) (dataStruct[35] & 0xff) );
       	return ( role == null ) ? pmUser.Role.UNSPECIFIED: role;
    }

    /**
     * setRole()
     * 
     * Set user role code in dataStruct.
     * 
     * @param pmUser.Role		user's role
     * @return void
     */
    public void setRole( pmUser.Role role ){
        dataStruct[35] = (byte) role.getCode();
		System.out.println( "pmUser.setRole() role=" + role.getDesc() + ", code=" + role.getCode() + ".  Datastruct[35]=" + dataStruct[35] + "." );

        return;
    }


    
    
    
   // Rec provRec;			// provider record, offset 108   
    
    /**
     * getProvRec()
     * 
     * Get provider record from dataStruct.
     * 
     * @param none
     * @return Rec
     */
    public Rec getProvRec(){
       	return Rec.fromInt( dataStruct, 108 );
    }

    /**
     * setProvRec()
     * 
     * Set provider record in dataStruct.
     * 
     * @param Rec	
     * @return void
     */
    public void setProvRec( Rec rec ){
        if ( Rec.isValid( rec )) rec.toInt( dataStruct, 108 );
        return;
    }






    /**
     * isValid
     */
    public boolean isValid() { return ( Validity.get( (int) dataStruct[127] ) == Validity.VALID ); }
    
    /**
     * setValid()
     * 
     */
    public void setValid(){ dataStruct[127] = (byte)( Validity.VALID.getCode() & 0xff ); }
    
    /**
     * setHidden()
     * 
     */
    public void setHidden(){ dataStruct[127] = (byte)( Validity.HIDDEN.getCode() & 0xff ); }
    
    /**
     * isHidden()
     */
    public boolean isHidden() { return ( Validity.get( (int) dataStruct[127] ) == Validity.HIDDEN ); }
    
    /**
     * getValidityByte()
     * 
     */
    public Validity getValidity(){ return Validity.get( (int) dataStruct[127] ); }
    
    /**
     * setValidityByte()
     * 
     */
    public void setValidity( Validity valid ){ dataStruct[127] = (byte)( valid.getCode() & 0xff ); }
    
    
    

    

    
	
	// Validate a password.  Returns 'true' if password matches.
	//
	
	public boolean validatePassword( String testPassword ){

		return ( testPassword.trim().equals(this.getPassword()) ? true: false );
	}
	

	
	
	
	
	// Read info from disk file given a userRec
	//
	
	public void read( Rec rec ){

    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "pmUser.read() bad rec" );

		//READ INFO FROM USERMAP.OVD
				
		this.userRec = rec;		
    	RandomFile.readRec( userRec, dataStruct, Pm.getOvdPath(), fn_usermap, getRecordLength());
	}


	
	
	// Write info to disk file given a userRec
	//
	
	public void write( Rec rec ){
		
    	if (( rec == null ) || ( rec.getRec() < 2 )) SystemHelpers.seriousError( "pmUser.read() bad rec" );
		// Write INFO to USERMAP.OVD
					
    	this.userRec = RandomFile.writeRec( rec, dataStruct, Pm.getOvdPath(), fn_usermap, getRecordLength());
	}
	
	
	
    public Rec newUser(){
    	
    	Rec rec;		// new user rec
    	
    	rec = RandomFile.writeRec( null, dataStruct, Pm.getOvdPath(), fn_usermap, getRecordLength());
    	return rec;
    }
    
    
    
    
	// open
    public static pmUser open(){
    	
    	// create new pmUser object
    	pmUser p = new pmUser();

    	// open file, read maxrec, and set current rec to null
		p.file = RandomFile.open( Pm.getOvdPath(), fn_usermap, getRecordLength(), RandomFile.Mode.READONLY );
		p.maxrec = new Rec( p.file.getMaxRecord());
		p.userRec = null;
		return p;
    }
    
    
    
    // getNext()
    public boolean getNext(){
        	
        // is file open??
        if ( file == null ) SystemHelpers.seriousError( "pmUser.getNext() file not open" );

        // set initial record number
    	if ( userRec == null ){
    		userRec = new Rec( 2 );
    	} else if ( userRec.getRec() < 2 ){
    		userRec.setRec( 2 );
    		
    	// else, increment record number
    	} else {
    		userRec.increment();
    	}
    	
    	// is this past maxrec
    	if ( userRec.getRec() > maxrec.getRec()) return false;
 
    	// read record
		file.getRecord( dataStruct, userRec );
		return true;
    }	
    
        
        
    // close()
    public boolean close(){
    	if ( file != null ) file.close();
    	file = null;
    	userRec = null;
    	maxrec = null;
    	return true;
    }

    
    
    



	


	
	
	/**
	 * search() - Find a user given a username
	 * 
	 * returns null Rec if not found
	 * 
	 * @param String username
	 * @return Rec
	 */
	public static Rec search( String username ){
				
		int	rec;					// record number
		int	maxrec;					// max record number
		boolean flgFound = false;	// flag that username has been found
		
		
		// Make sure username is all caps and trimmed
		username = username.trim().toUpperCase();

		// Create new user object
		pmUser user = new pmUser();
		
		// Open usermap.ovd file
		RandomFile file = new RandomFile( Pm.getOvdPath(), fn_usermap, pmUser.getRecordLength(), RandomFile.Mode.READONLY );
		
        // get max record
        maxrec = file.getMaxRecord();
        System.out.println( "maxrec = " + maxrec );
        System.out.println( "searching for " + username );

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( user.dataStruct, rec );
            if ( ! user.isValid()) continue;
            
            if ( username.equals( user.getUser())){
            	
                System.out.println( "found " + user.getUser() + " rec=" + rec );
        		flgFound = true;
        		break;
            }
        }
                
        System.out.println( "loop finished, flgFound = " + flgFound );

        // close file
        file.close();
		
		return flgFound ? new Rec( rec ): null;
	}
	



}


/**
 * pmUserStruct class
 * 
 * This class holds the field formatting for usermap.ovd
 * 
 * @author Owner
 *
 */

class pmUserStruct extends Struct {
	
	/*
	 * struct USRMAP, len=128, filename="usermap.ovd"
	 * **********************************************
	 * 
	 * 0	char	usrnam[10];
	 * 10	char	paswrd[10];
	 * 20	long	usracc;				// user's access map
	 * 24	DATE	sgndte;				// user's last signon date
	 * 28	TIME	sgntim;				// user's last signon time
	 * 32	USRT	console;			// user's console number
	 * 34	char	unused2[2];
	 * 36	char	Name[40];			// user's name
	 * 76	char	unused[51];
	 * 127	byte	valid;				// validity byte
	 */
	
	
	private static int reclen = 128;
	private static String filename = "usermap.ovd";
	
byte data1[] = new byte[128];

	// Constructor  (allocates data)
	public pmUserStruct() {
		super();
	}
	
	
	// Get record length
	// Overrides stub class method
	public int getRecLen(){
		return pmUserStruct.reclen;
	}

	
	// Get file name
	// Overrides stub class method
	public String getFileName(){
		return pmUserStruct.filename;
	}
	
	
	
	
	// Get/Set User's logon name
	public String getLogon(){
		return ( this.getString( 0, 10));
	}

	public void setLogon( String logon ){
		this.setStringPadded( logon, 0, 10 );
	}
	
	// Get/Set User's password
	public String getPassword(){
		return ( this.getString( 10, 10));
	}

	public void setPassword( String password ){
		this.setStringPadded( password, 10, 10 );
	}
	
	// Get/Set User's access map
	public int getAccessMap(){
		return ( this.getInt( 20 ));
	}

	public void setAccessMap( int accessMap ){
		this.setInt( accessMap, 20 );
	}
	
	// Get/Set User's logon name
	public String getUserName(){
		return ( this.getString( 36, 40));
	}

	public void setUserName( String userName ){
		this.setStringPadded( userName, 36, 40 );
	}
	

	
	
	
	// Get/Set User's validity byte
	public boolean isValid(){
		return ( this.isValid( 127 ));
	}
	public boolean isHidden(){
		return ( this.isHidden( 127 ));
	}
	public void setValid(){
		this.setValid( 127 );
	}
	public Validity getValidity(){
		return ( this.getValidity( 127 ));
	}
	public void setValidity( Validity valid ){
		this.setValidity( valid, 127 );
	}
	

	
}
