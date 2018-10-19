package usrlib;

public class Time {

	// fields
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
	private int hundreth = 0;
	
	public enum BCDLEN {
		HHMM, HHMMSS, HHMMSSDD
	}

    
    // CONSTRUCTORS
    
	private Time(){
		;
	}
	
   /**
     * Time - create from a string
     *
     * @param aString String
     */
/*    public Time(String strTime) {
        // call fromString to do the real work
        this.fromString(strTime);
    }
*/
    /**
     * Time - create time from BCD byte[] data
     *
     * @param bcd byte[]
     * @param int offset
     */
/*    public Time(byte[] bcdTime, int offset ) {
        // call fromBCD() to do the real work
        this.fromBCD( bcdTime, TIME.HHMM, offset );
    }
*/
    /**
     * Time - create time from BCD byte[] data
     *
     * @param bcd byte[]
     */
/*    public Time(byte[] bcdTime) {
        // call fromBCD() to do the real work
        this.fromBCD( bcdTime, TIME.HHMM, 0 );
    }
*/

    /**
     * Time
     *
     * Construct time object from ints hour, minute, and second
     *
     * @param hour int
     * @param minute int
     * @param second int
     */
/*    public Time(int hour, int minute, int second) {
    	setTime( hour, minute, second );
    }
*/



    
    
    /**
     * setTime()
     * 
     * Set a time given a hour, minute, and second.
     * 
     */
    public Time set(int hour, int minute, int second, int hundreth ){
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.hundreth = hundreth;
        return this;
    }
    public Time set( int hour, int minute, int second ){ return set( hour, minute, second, 0 ); }
    public Time set( int hour, int minute){ return set( hour, minute, 0, 0 ); }
    public Time set(){ return set( 0, 0, 0, 0 ); }

  
    public static Time setTime( int hour, int minute, int second, int hundreth ){
    	Time time = new Time().set(hour, minute, second, hundreth);
        return time;
    }
    public static Time setTime(){ return setTime( 0, 0, 0, 0 ); }
    public static Time setTime( int hour, int minute ){ return setTime( hour, minute, 0, 0 ); }
    public static Time setTime( int hour, int minute, int second ){ return setTime( hour, minute, second, 0 ); }
    
    

    /**
     * compare
     *
     * @return returns # of seconds difference between this and passed time
     *
     * @param aTime Time
     */
    public long compare(Time aTime) {
        return (this.toJulian() - aTime.toJulian());
    }

    
    
    
    
    /**
     * equals() - Are two times equal?
     * 
     * @param Time comparator
     * @return boolean
     */
    public boolean equals( Time aTime ){
    	return (( this.compare( aTime ) == 0 ) ? true: false );
    }
    
    

    /**
     * getPrintableTime
     */
    public String getPrintable() {
        return (this.getPrintable(5));
    }

    
    
    
    
    

    /**
     * getPrintableTime
     *
     * Get printable time according to format
     *
     * @param format int
     * @todo Fully implement this
     */
    public String getPrintable(int format) {

//	        PrintfFormat pf = new PrintfFormat( "%02d-%02d-%04d" );
//	        pf.sprintf( new Integer(hour), new Integer( minute) );
        switch (format) {

        case 1:
            return ( String.format("%02d%02d%02d", this.hour, this.minute, this.second ));
        case 2:
            return ( String.format("%02d:%02d:%02d", this.hour, this.minute, this.second ));
        case 3:
            return ( String.format("%02d%02d%02d%02d", this.hour, this.minute, this.second, 0 /*hundreth*/ ));
        case 4:
            return ( String.format("%02d:%02d:%02d.%02d", this.hour, this.minute, this.second, 0 /*hundreth*/ ));
        case 5:
        default:
            return ( String.format("%02d:%02d", this.hour, this.minute ));
        case 6:
            return ( String.format("%02d:%02d:%02d %s", this.hour % 12, this.minute, this.second, ( hour >= 12 ) ? "PM": "AM" ));
        case 7:
            return ( String.format("%02d:%02d %s", this.hour % 12, this.minute, ( hour >= 12 ) ? "PM": "AM" ));
       }
    }


    
    
    
    
    /**
     * fromString
     *
     * Set this time object from a (user entered) string
     *
     * @param strTime String
     */
    public static Time fromString(String strTime) {
    	
        int hour = 0;
        int minute = 0;
        int second = 0;
        int hundreth = 0;

        boolean digitsOnly;
        int i; // loop counter


        // write string to char array I can manipulate
        char buffer[] = strTime.trim().toCharArray();

        // are there any seperator characters (- or /)? (is it all digits?)
        // change any seperator character to a ':'
        digitsOnly = true;
        for (i = 0; i < buffer.length; ++i) {
            if (!Character.isDigit(buffer[i])) {
                digitsOnly = false;
                buffer[i] = ':';
            }
        }

        // is it an even number of chars?
        if ((buffer.length % 2) != 0) digitsOnly = false;

        // handle digits only entered
        if (digitsOnly) {

            switch (buffer.length) {
            case 2: // hh
                hour = Integer.parseInt(new String(buffer, 0, 2));
                break;
            case 4: // hhmm
                hour = Integer.parseInt(new String(buffer, 0, 2));
                minute = Integer.parseInt(new String(buffer, 2, 2));
                break;
            case 6: // hhmmss
                hour = Integer.parseInt(new String(buffer, 0, 2));
                minute = Integer.parseInt(new String(buffer, 2, 2));
                second = Integer.parseInt(new String(buffer, 4, 2));
                break;
            case 8: // either hhmmsshh
                hour = Integer.parseInt(new String(buffer, 0, 2));
                minute = Integer.parseInt(new String(buffer, 2, 2));
                second = Integer.parseInt(new String(buffer, 4, 2));
                hundreth = Integer.parseInt(new String(buffer, 6, 2));
                break;
            }

            // handle entries in form of hh:mm:ss, etc
        } else {

        	String[] token = null;
            int value[] = {0, 0, 0, 0};
            int j;
            int start;
            int end;
            int cnt;

            String tmpTime = new String(buffer);
            String tmpString;
            
            token = tmpTime.split( ":", 4 );

            // break out tokens and set values in array
/*            for ( start = 0, cnt = 0; cnt < 4; ++cnt ){
            	end = tmpTime.indexOf( ':', start );
            	if ( end > 0 ){
            		value[cnt] = Integer.parseInt( tmpTime.substring(start, end));
            		start = end + 1;
            	} else {
            		value[cnt] = Integer.parseInt( tmpTime.substring( start, tmpTime.length()-1));
            		break;
            	}
            }
*/            
            
 /*           for (end = 0, j = 0; j < 3; ++j) {
                start = end;
                end = tmpTime.indexOf(':', start);
                if (end >= 0) {
                    tmpString = tmpTime.substring( start, end );
                    value[j] = (tmpString.length() > 0) ? Integer.parseInt(tmpString): 0;
                    ++end;
                } else {
                    tmpString = tmpTime.substring( start );
                    value[j] = (tmpString.length() > 0) ? Integer.parseInt(tmpString): 0;
                    break;
                }
            }
*/
            // set values into time parts
            if ( token.length > 0 ) hour = EditHelpers.parseInt( token[0] );
            if ( token.length > 1 ) minute = EditHelpers.parseInt( token[1] );
            if ( token.length > 2 ) second = EditHelpers.parseInt( token[2] );
            if ( token.length > 3 ) hundreth = EditHelpers.parseInt( token[3] );
            
        }

        System.out.println( "hour=" + hour + "  minute=" + minute + "   second=" + second );

        // set time objects parts
        return new Time().set( hour, minute, second, 0 );
    }

    
    
    
    
    
    
    
    
    
    /*  get length of time type  */
    
    public static int getBCDLen( BCDLEN tl ){    	
		int len = 2;
		if ( tl == BCDLEN.HHMMSS ) len = 3;
		if ( tl == BCDLEN.HHMMSSDD ) len = 4;
		return ( len );
    }
    
    
    /**
     * fromBCD
     *
     * Set this time object from a 4 byte BCD
     *
     * @param bcdTime byte[]
     * @param BCDLEN
     * @return Time
     */
    public static Time fromBCD( byte[] data, BCDLEN bcd ) { return fromBCD( data, 0, bcd ); }

    /**
     * fromBCD
     *
     * Static function creates and sets a new time object from a 4 byte BCD
     *
     * @param bcdTime byte[]
     * @param int offset
     * @param BCDLEN
     * @return Time
     */
    public static Time fromBCD(byte[] data, int offset, BCDLEN bcd ){
    	
    	int len = getBCDLen( bcd );
        int value[] = { 0, 0, 0, 0 };
               
        for ( int cnt = 0; cnt < len; ++ cnt ){        	
        	value[cnt] = ((data[offset+cnt] & 0xf0) / 16 * 10 ) + ( data[offset+cnt] & 0x0f );
        }
        
        return new Time().set( value[0], value[1], value[2], 0 );
    }
/*    
    public static Time fromBCD2( byte[] bcd, int offset ){ return fromBCD( bcd, offset, 2 ); }
    public static Time fromBCD2( byte[] bcd ){ return fromBCD( bcd, 0, 2 ); }
    public static Time fromBCD3( byte[] bcd, int offset ){ return fromBCD( bcd, offset, 3 ); }
    public static Time fromBCD3( byte[] bcd ){ return fromBCD( bcd, 0, 3 ); }
    public static Time fromBCD4( byte[] bcd, int offset ){ return fromBCD( bcd, offset, 4 ); }
    public static Time fromBCD4( byte[] bcd ){ return fromBCD( bcd, 0, 4 ); }
*/    

    
    
    
    /**
     * toBCD
     *
     * Set this time object into a 4 byte BCD
     *
     * @param bcdTime byte[]
     * @param BCDLEN
     * @return void
     */
    public void toBCD(byte[] data, BCDLEN bcd ) { this.toBCD( data, 0, bcd ); }

    /**
     * toBCD
     *
     * Set this time object into a 4 byte BCD
     *
     * @param bcdTime byte[]
     * @param int offset
     * @param BCDLEN
     * @return Time
     */
    public void toBCD( byte[] data, int offset, BCDLEN bcd ){

    	String s = null;
    	if ( bcd == BCDLEN.HHMM ) s = String.format( "%02d%02d", hour, minute );
    	if ( bcd == BCDLEN.HHMMSS ) s = String.format( "%02d%02d%02d", hour, minute, second );
    	if ( bcd == BCDLEN.HHMMSSDD ) s = String.format( "%02d%02d%02d%02d", hour, minute, second, hundreth );
        Decoders.toBCD( s, data, getBCDLen( bcd ), offset );
        return;
    }
    
/*    public void toBCD2( byte[] bcd, int offset ){  toBCD( bcd, offset, 2 ); }
    public void toBCD2( byte[] bcd ){  toBCD( bcd, 0, 2 ); }
    public void toBCD3( byte[] bcd, int offset ){  toBCD( bcd, offset, 3 ); }
    public void toBCD3( byte[] bcd ){  toBCD( bcd, 0, 3 ); }
    public void toBCD4( byte[] bcd, int offset ){  toBCD( bcd, offset, 4 ); }
    public void toBCD4( byte[] bcd ){  toBCD( bcd, 0, 4 ); }
*/

    
    
    
    

    /**
     * fromJulian
     *
     * Set this time object from an int Julian time as defined as
     * the number of seconds since 00:00:00 am (midnight)
     *
     * @param julian int
     */
    public static Time fromJulian(int julian) {

        // I use ints instead of longs because ints are 32 bit in Java

        int hour;
        int minute;
        int second;

        hour = julian / 3600;
        minute = ( julian % 3600 ) / 60;
        second = julian % 216000;
        setTime( hour, minute, second );
        System.out.println( "julian=" + julian + ", hour=" + hour + ", min=" + minute + ", sec=" + second );
        return Time.setTime( hour, minute, second, 0 );
    }


    
    
    
    
    
    /**
     * toJulian
     *
     * Return a Julian time given a hour, minute, and second
     *
     *
     * @param hour int
     * @param minute int
     * @param second int
     */
    public static int toJulian( int hour, int minute, int second ){

        // I use ints instead of longs because ints are 32 bit in Java

        int julian;

        // make sure numbers are in bounds
        if ( hour < 0 ) hour = 0;
        if ( hour > 23 ) hour = 0;
        if ( minute < 0 ) minute = 0;
        if ( minute > 59 ) minute = 0;
        if ( second < 0 ) second = 0;
        if ( second > 59 ) second = 0;
        
        julian = ( hour * 3600 ) + ( minute * 60 ) + second;
        System.out.println( "julian=" + julian + ", hour=" + hour + ", min=" + minute + ", sec=" + second );
        return ( julian );
    }
    
    public int toJulian(){
    	return toJulian( hour, minute, second );
    }



    /**
     * getMonth
     */
    public int getMonth() {
        return ( this.hour );
    }

    /**
     * getDay
     */
    public int getHour() {
        return ( this.minute );
    }

    /**
     * getYear
     */
    public int getSecond() {
        return ( this.second );
    }


    /**
     * isValid
     */
    public boolean isValid() {
        if (( hour < 0 ) || ( hour > 23 )
            || ( minute < 0 ) || ( minute > 59 )
            || ( second < 0 ) || ( second > 59 ))

            return false;
        return true;
    }
    
    
    
    
    /**
     * getJavaTime()
     * 
     */
/*    public java.util.Time getJavaTime(){
  		final java.util.Calendar calendar = java.util.Calendar.getInstance();
    	calendar.set( this.second, this.hour-1, this.minute );
    	final java.util.Time result = calendar.getTime();
    	return result;
    }
*/    

    /**
     * getSystemTime()
     * 
     * This class sets the Time object according to the system time.
     * 
     */
    public static usrlib.Time getSystemTime(){
  		final java.util.Calendar calendar = java.util.Calendar.getInstance();
  		java.util.Date jdate = calendar.getTime();
  		return Time.setTime( calendar.get( calendar.HOUR_OF_DAY ), calendar.get( calendar.MINUTE ), calendar.get( calendar.SECOND )); // why +1 for HOUR_OF_DAY ????
    }
    
    
    /**
     * now()
     * 
     * This static class returns a Time object initialized to the system time.
     * 
     */
    public static usrlib.Time now(){
    	return Time.getSystemTime();
    }

}
