package usrlib;

/**
 * <p>Title: PAL/MED Name Object</p>
 *
 * <p>Description: A Name object to implement palmed's Name Struct and
 *                 associated functions: nam_mkp(), namcmp(), nampack(),
 *                 namunpk(), and namsflgs().</p>
 *
 * <p>Copyright: Copyright (c) 1983 - 2005</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */
public class Name {
	
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String Suffix;
    
    private static int recordLength = 64;		// record length in data structs
    

	
	// CONSTRUCTORS
	
    public Name() {
        FirstName = "";
        MiddleName = "";
        LastName = "";
        Suffix = "";
    }
    
    public Name( byte[] data ) { fromData( data, 0 ); }
    public Name( byte[] data, int offset ) { fromData( data, offset ); }

    /**
     * Name
     *
     * @param firstName String
     * @param middleName String
     * @param lastName String
     * @param suffix String
     */
    public Name(String firstName, String middleName, String lastName, String suffix) {
        this.FirstName = firstName.trim().toUpperCase();
        this.MiddleName = middleName.trim().toUpperCase();
        this.LastName = lastName.trim().toUpperCase();
        this.Suffix = suffix.trim().toUpperCase();
    }


    

    
    
    
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName.trim().toUpperCase();
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setMiddleName(String MiddleName) {
        this.MiddleName = MiddleName.trim().toUpperCase();
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName.trim().toUpperCase();
    }

    public String getLastName() {
        return LastName;
    }

    public void setSuffix(String Suffix) {
        this.Suffix = Suffix.trim().toUpperCase();
    }

    public String getSuffix() {
        return Suffix;
    }

    
    
    
    /**
     * fromData() - Read name fields from data struct
     * 
     * @param byte[] data
     * @param int offset
     * @return Name
     */
    public Name fromData( byte[] data, int offset ) {
    	this.setLastName( new String( data, offset + 0, 20 ).trim());
    	this.setFirstName( new String( data, offset + 20, 16 ).trim());
    	this.setMiddleName( new String( data, offset + 36, 16 ).trim());
    	this.setSuffix( new String( data, offset + 52, 8 ).trim());
    	return this;
    }
    
    /**
     * toData() - Set Name into byte data struct
     * 
     * @param byte[] data
     * @param int offset
     * @return void
     */
    public void toData( byte[] data, int offset ){
    	StructHelpers.setStringPadded( getLastName(), data, offset + 0, 20 );
    	StructHelpers.setStringPadded( getFirstName(), data, offset + 20, 16 );
    	StructHelpers.setStringPadded( getMiddleName(), data, offset + 36, 16 );
    	StructHelpers.setStringPadded( getSuffix(), data, offset + 52, 8 );
    	return;
    }

    
    
    
    
    /**
     * getPrintableName
     *
     * @param format int
     */
    public String getPrintableName(int format) {

        switch ( format ){

        case 0:
        case 1:
            return ( FirstName + " " +  MiddleName + " " + LastName );

        case 2:
            return ( FirstName + " " + MiddleName.substring( 0, 1 ) + ". " + LastName );

        case 3:
            return ( FirstName + " " + LastName );

        case 4:
            return ( FirstName.substring( 0, 1 ) + ". " + MiddleName.substring( 0, 1 ) + ". " + LastName );

        case 5:
            return ( FirstName.substring( 0, 1 ) + "." + MiddleName.substring( 0, 1 ) + ". " + LastName );

        case 6:
            return ( FirstName.substring( 0, 1 ) + "." + MiddleName.substring( 0, 1 ) + "." + LastName.substring( 0, 1 ));

        case 7:
            return ( FirstName.substring( 0, 1 ) + MiddleName.substring( 0, 1 ) + LastName.substring( 0, 1 ));

        case 8:
            return ( FirstName.substring( 0, 1 ) + MiddleName.substring( 0, 1 ) + LastName.substring( 0, 6 ));

        case 9:
        case 10:
            if (( MiddleName.length() > 0 ) && ( MiddleName.charAt( 0 ) != '?' )){
                return ( LastName + ", " +  FirstName + MiddleName );
            } else {
                return ( LastName + ", " + FirstName );
            }

        case 11:
            if (( MiddleName.length() > 0 ) && ( MiddleName.charAt( 0 ) != '?' )){
                 return ( LastName + ", " +  FirstName + " " + MiddleName.substring( 0, 1 ));
            } else {
                 return ( LastName + ", " + FirstName );
            }


        case 12:

            return ( LastName + "," + FirstName + " " + MiddleName );
        }
        return "";
    }

    /**
     * getPrintableNameFML
     */
    public String getPrintableNameFML() {
        if (( MiddleName.length() > 0 ) && ( MiddleName.charAt( 0 ) != '?' )){
            return ( FirstName + " " +  MiddleName + " " + LastName );
        } else {
            return (FirstName + " " + LastName);
        }
    }

    /**
     * getPrintableNameLFM
     */
    public String getPrintableNameLFM() {
        if (( MiddleName.length() > 0 ) && ( MiddleName.charAt( 0 ) != '?' )){
            return ( LastName + ", " +  FirstName + " " + MiddleName );
        } else {
            return ( LastName + ", " + FirstName );
        }
    }

    /**
     * compare
     *
     *  This function is a 'loose' comparison in that it does not compare name
     *  parts that are empty in the 'this' name struct.  It also does not
     *  compare the suffix.  This is useful for the PtSearch routines.
     *
     * @param aName Name     // the db name to compare against
     * @return Returns boolean true if match or false if no match
     */
    public boolean compare(Name aName) {

        // System.out.println( "comparing last name, length = " + LastName.length() );
        if ((( LastName.length() > 1 ) && ( LastName.compareTo( aName.getLastName()) != 0 ))
            || (( LastName.length() == 1 ) && ( aName.getLastName().startsWith( LastName ) == false ))){
            return ( false );
        }

        // System.out.println( "comparing first name, length = " + FirstName.length() );
        if ((( FirstName.length() > 1 ) && ( FirstName.compareTo( aName.getFirstName()) != 0 ))
            || (( FirstName.length() == 1 ) && ( aName.getFirstName().startsWith( FirstName ) == false ))){
            return ( false );
        }

        // System.out.println( "comparing middle name, length = " + MiddleName.length() );
        if ((( MiddleName.length() > 1 ) && ( MiddleName.compareTo( aName.getMiddleName()) != 0 ))
            || (( MiddleName.length() == 1 ) && ( aName.getMiddleName().startsWith( MiddleName ) == false ))){
             return ( false );
        }
        // System.out.println( "MATCH" );

        return ( true );
    }

    /**
     * compareStrict
     *
     *  Returns 0 if equal, returns otherwise  + or - depending on lex order
     *  This function is a 'strict' comparison in that it compares every
     *  field, including the suffix.
     *
     * @param aName Name          // the db name to compare against
     * @return boolean
     */
    public int compareStrict(Name aName) {

        int ret = 0;        // return value from compare

        if (( ret = LastName.compareTo( aName.getLastName())) != 0 ){
            return ( ret );
        }

        if (( ret = FirstName.compareTo( aName.getFirstName())) != 0 ){
            return ( ret );
        }

        if (( ret = MiddleName.compareTo( aName.getMiddleName())) != 0 ){
            return ( ret );
        }

        if (( ret = Suffix.compareTo( aName.getSuffix())) != 0 ){
            return ( ret );
        }

        return ( 0 );
    }

    /**
     * compareAnchoredSubstring
     *
     * @param aName Name
     *
     * @return boolean
     */
    public boolean compareAnchoredSubstring(Name aName) {

        if (( LastName.length() > 0 ) && ( aName.getLastName().startsWith( LastName ) == false )){
            return false;
        }

        if (( FirstName.length() > 0 ) && ( aName.getFirstName().startsWith( FirstName ) == false )){
            return false;
        }

        if (( MiddleName.length() > 0 ) && ( aName.getMiddleName().startsWith( MiddleName ) == false )){
            return false;
        }

        return true;
    }

    /**
     * compareUnAnchoredSubstring
     *
     * @param aName Name
     *
     * @return boolean
     */
    public boolean compareUnAnchoredSubstring(Name aName) {

        if (( LastName.length() > 0 ) && ( aName.getLastName().indexOf( LastName ) < 0 )){
            return false;
        }

        if (( FirstName.length() > 0 ) && ( aName.getFirstName().indexOf( FirstName ) < 0 )){
            return false;
        }

        if (( MiddleName.length() > 0 ) && ( aName.getMiddleName().indexOf( MiddleName ) < 0 )){
            return false;
        }

        return true;
    }


    
	/*  Soundslike Ver 1.1.
    /*  Borrowed from anomaly!~/archives/soundx.tar.Z on 04-28-92. */
    


    /**
     * soundsLike
     *
     * @param aName Name
     *
     * @return  Returns 0 for match, else -1
     * @todo Need to implement sounds like
     */
    public boolean compareSoundsLike( Name aName ) {
    	
    	if ((( this.LastName.length() == 1 ) && ( this.LastName.charAt( 0 ) != aName.LastName.charAt( 0 )))
    		|| ( this.LastName.length() > 1 ) && ! soundsLikeEncode( this.LastName).equals( soundsLikeEncode( aName.LastName))){
    		return false;
    	}

    	if ((( this.FirstName.length() == 1 ) && ( this.FirstName.charAt( 0 ) != aName.FirstName.charAt( 0 )))
    		|| ( this.FirstName.length() > 1 ) && ! soundsLikeEncode( this.FirstName).equals( soundsLikeEncode( aName.FirstName))){
    		return false;
    	}

    	if ((( this.MiddleName.length() == 1 ) && ( this.MiddleName.charAt( 0 ) != aName.MiddleName.charAt( 0 )))
    		|| ( this.MiddleName.length() > 1 ) && ! soundsLikeEncode( this.MiddleName).equals( soundsLikeEncode( aName.MiddleName))){
    		return false;
    	}

        return true;
    }
    
    
    
    /**
     * soundsLikeEncode
     *
     * @param String s - string to encode
     *
     * @return  String - encoded
     */
    public String soundsLikeEncode( String s ){
    	
    	
    	if (( s == null ) || ( s.length() == 0 )) return s;
    	String sav = s;

    	s = s.trim().toUpperCase();
    	
    	// names that start with 'TS', 'KN', or 'PF'
    	//    (my improvement)
    	if ( s.startsWith( "TS" )) s = s.replaceFirst( "TS", "S" );
    	if ( s.startsWith( "KN" )) s = s.replaceFirst( "KN", "N" );
    	if ( s.startsWith( "PF" )) s = s.replaceFirst( "PF", "F" );
   	
    	// names with 'PH' or 'GHT'
    	//    (my improvement)
    	if ( s.indexOf( "PH" ) >= 0 ) s = s.replaceAll( "PH", "F" );
    	if ( s.indexOf( "GHT" ) >= 0 ) s = s.replaceAll( "GHT", "T" );

    	// allocate char array
    	int j = 0;
    	char[] out = new char[s.length()];
    	
    	out[0] = s.charAt(0);
    	
    	for ( int i = 1; i < s.length(); ++i ){
    		
    		// skip double letters
    		if (s.charAt(i) == out[j]) continue;
    		
    		switch ( s.charAt(i)){
    		
    		case 'B':
    		case 'F':
    		case 'P':
    		case 'V':

    			out[++j] = '1';
    			break;

    		case 'C':
    		case 'G':
    		case 'J':
    		case 'K':
    		case 'Q':
    		case 'S':
    		case 'X':
    		case 'Z':

    			out[++j] = '2';
    			break;

    		case 'D':
    		case 'T':

    			out[++j] = '3';
    			break;

    		case 'L':

    			out[++j] = '4';
    			break;

    		case 'M':
    		case 'N':

    			out[++j] = '5';
    			break;

    		case 'R':

    			out[++j] = '6';
    			break;

    		default:

    			break;
    		}    		
    		
    		// limit encoded string to 5 chars
        	//    (my improvement)
    		if ( j >= 4 ) break;
    	}
    	
    	s =  StringHelpers.pad( new String( out, 0, ++j ), 5, '0' );
    	System.out.println( sav + "==>" + s );
		return ( s );
    }

}
