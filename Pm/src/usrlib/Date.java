package usrlib;


/**
 * <p>Title: PAL/MED</p>
 *
 * <p>Description: A date class </p>
 *
 * <p>Copyright: Copyright (c) 1983 - 2005</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */

public class Date {
	
	// fields
    private int JulianDate; // days since 01-01-0000
    private int month = 0;
    private int day = 0;
    private int year = 0;
    private int dow = 0; // day of week

    
    public String Months[] = {"", "January", "February", "March", "April",
                             "May",
                             "June", "July", "August", "September",
                             "October", "November", "December"};

    public String Days[] = {"Sunday", "Monday", "Tuesday", "Wednesday",
                           "Thursday", "Friday", "Saturday"};

	

    
    // CONSTRUCTORS
    
	public Date(){
		;
	}
	
    public Date(int JulianDate) {
        this.fromJulian( JulianDate );
    }
    
    /**
     * Date - create from a string
     *
     * @param aString String
     */
    public Date(String strDate) {
        // call fromString to do the real work
        this.fromString(strDate);
    }

    /**
     * Date - create date from BCD byte[] data
     *
     * @param bcd byte[]
     * @param int offset
     */
    public Date(byte[] bcdDate, int offset ) {
        // call fromBCD() to do the real work
        this.fromBCD( bcdDate, offset );
    }
    /**
     * Date - create date from BCD byte[] data
     *
     * @param bcd byte[]
     */
    public Date(byte[] bcdDate) {
        // call fromBCD() to do the real work
        this.fromBCD(bcdDate, 0 );
    }


    /**
     * Date
     *
     * Construct date object from ints month, day, and year
     *
     * @param month int
     * @param day int
     * @param year int
     */
    public Date(int month, int day, int year) {
    	setDate( month, day, year );
    }




    
    
    /**
     * setDate()
     * 
     * Set a date given a month, day, and year.
     * 
     */
    public Date setDate(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.JulianDate = this.toJulian( month, day, year );
        this.dow = this.toDOW( this.JulianDate );
        return this;
    }
   
    
    
    

    /**
     * compare
     *
     * @return returns # of days difference between this and passed date
     *
     * @param aDate Date
     */
    public int compare(Date aDate) {
        return (this.JulianDate - aDate.JulianDate);
    }

    
    
    
    
    /**
     * equals() - Are two dates equal?
     * 
     * @param Date comparator
     * @return boolean
     */
    public boolean equals( Date aDate ){
    	return (( this.compare( aDate ) == 0 ) ? true: false );
    }
    
    

    /**
     * getPrintableDate
     */
    public String getPrintable() {
        return (this.getPrintable(9));
    }

    
    
    
    
    

    /**
     * getPrintableDate
     *
     * Get printable date according to format
     *
     * @param format int
     * @todo Fully implement this
     */
    public String getPrintable(int format) {

//	        PrintfFormat pf = new PrintfFormat( "%02d-%02d-%04d" );
//	        pf.sprintf( new Integer(month), new Integer( day) );
        switch (format) {

        case 1:
            return ( String.format("%02d%02d%02d", this.month, this.day, this.year % 100 ));
        case 2:
            return ( String.format("%02d/%02d/%02d", this.month, this.day, this.year % 100));
        case 3:
            return ( String.format("%02d-%02d-%02d", this.month, this.day, this.year % 100));
        case 4:
            return ( String.format("%3s %d, %d", this.Months[this.month], this.day, this.year));
        case 5:
            return ( String.format("%s %d, %d", this.Months[this.month], this.day, this.year));
        case 6:
            return ( String.format("%s %s %d, %d", this.Days[this.dow], this.Months[this.month], this.day, this.year));
        case 7:
            return ( String.format("%d %3s %d", this.day, this.Months[this.month], this.year));
        case 8:
            return ( String.format("%04d%02d%02d", this.year, this.month, this.day));
        case 9:
        default:
            return ( String.format("%02d-%02d-%04d", this.month, this.day, this.year));
        case 10:
            return ( String.format("%04d-%02d-%02d", this.year, this.month, this.day));
        case 11:
            return ( String.format("%02d/%02d/%04d", this.month, this.day, this.year));
        
        }
    }


    
    
    
    
    /**
     * fromString
     *
     * Set this date object from a (user entered) string
     *
     * @param strDate String
     * @todo Make this use current year to determine default century (hardcoded to 10 )
     */
    public Date fromString(String strDate) {
        int month = 0;
        int day = 0;
        int year = 0;

        boolean digitsOnly;
        int i; // loop counter


        // write string to char array I can manipulate
        char buffer[] = strDate.toCharArray();

        // are there any seperator characters (- or /)? (is it all digits?)
        // change any seperator character to a '-'
        digitsOnly = true;
        for (i = 0; i < buffer.length; ++i) {
            if (!Character.isDigit(buffer[i])) {
                digitsOnly = false;
                buffer[i] = '-';
            }
        }

        // is it an even number of chars?
        if ((buffer.length % 2) != 0) digitsOnly = false;

        // handle digits only entered
        if (digitsOnly) {

            switch (buffer.length) {
            case 2: // yy
                year = Integer.parseInt(new String(buffer, 0, 2));
                break;
            case 4: // mmyy
                month = Integer.parseInt(new String(buffer, 0, 2));
                if ( month > 12 ){
                	month = 0;
                	year = Integer.parseInt(new String(buffer, 0, 4));
                } else {
                	year = Integer.parseInt(new String(buffer, 2, 2));
                }
                break;
            case 6: // mmddyy
                month = Integer.parseInt(new String(buffer, 0, 2));
                day = Integer.parseInt(new String(buffer, 2, 2));
                year = Integer.parseInt(new String(buffer, 4, 2));
                break;
            case 8: // either mmddyyyy or yyyymmdd
                month = Integer.parseInt(new String(buffer, 0, 2));
                if (month > 12) { // must be yyyymmdd
                    year = Integer.parseInt(new String(buffer, 0, 4));
                    month = Integer.parseInt(new String(buffer, 4, 2));
                    day = Integer.parseInt(new String(buffer, 6, 2));
                } else { // must be mmddyyyy
                    day = Integer.parseInt(new String(buffer, 2, 2));
                    year = Integer.parseInt(new String(buffer, 4, 4));
                }
                break;
            }

            // handle entries in form of mm-dd-yyyy or m-dd-yy, etc
        } else {

            String tmpDate = new String(buffer);

/*  OLD WAY BEFORE TRYING String.split()
            // break out tokens and set values in array
            String tmpString;
            int value[] = {0, 0, 0};
            int j;
            int start;
            int end;

            for (end = 0, j = 0; j < 3; ++j) {
                start = end;
                end = tmpDate.indexOf('-', start);
                if (end >= 0) {
                    tmpString = tmpDate.substring( start, end );
                    value[j] = (tmpString.length() > 0) ? Integer.parseInt(tmpString): 0;
                    ++end;
                } else {
                    tmpString = tmpDate.substring( start );
                    value[j] = (tmpString.length() > 0) ? Integer.parseInt(tmpString): 0;
                    break;
                }
            }

            // set values into date parts
            if (j == 2) {
                month = value[0];
                day = value[1];
                year = value[2];
            } else if (j == 1) {
                month = value[0];
                year = value[1];
            } else {
                year = value[0];
            }
*/
            String[] s = tmpDate.split( "-" );

            // mm-dd-yyyy or yyyy-mm-dd
            if ( s.length == 3 ){
            	month = EditHelpers.parseInt( s[0] );
            	if ( month > 12 ){
            		month = EditHelpers.parseInt( s[1] );
            		day = EditHelpers.parseInt( s[2] );
            		year = EditHelpers.parseInt( s[0] );
            	} else {          		
               		day = EditHelpers.parseInt( s[1] );
            		year = EditHelpers.parseInt( s[2] );
            	}
 
            // mm-yyyy
            } else if ( s.length == 2 ){
            	month = EditHelpers.parseInt( s[0] );
            	year = EditHelpers.parseInt( s[1] );

            // yyyy
            } else if ( s.length == 1 ){
            	year = EditHelpers.parseInt( s[0] );
            }
            
            
        }

        // handle century if 2 digit year entered
        //TODO - handle the default century better (have the 12 change with the year)
        if ((year != 0) && (year < 100)) {
            year += (year > 12) ? 1900 : 2000;
        }
        //System.out.println( "month=" + month + "  day=" + day + "   year=" + year );

        // set date objects parts
        setDate( month, day, year );
        return this;
    }

    
    
    
    
    /**
     * fromBCD
     *
     * Set this date object from a 4 byte BCD
     *
     * @param bcdDate byte[]
     * @return Date
     */
    public Date fromBCD(byte[] bcd) { return fromBCD( bcd, 0 ); }

    /**
     * fromBCD
     *
     * Set this date object from a 4 byte BCD
     *
     * @param bcdDate byte[]
     * @param int offset
     * @return Date
     */
    public Date fromBCD(byte[] bcd, int offset ){
    	
        int month;
        int day;
        int year;

        // break out date parts from BCD
        month = (bcd[offset+0] & 0xf0) / 16 * 10 + (bcd[offset+0] & 0x0f);
        day = (bcd[offset+1] & 0xf0) / 16 * 10 + (bcd[offset+1] & 0x0f);
        year = ((bcd[offset+2] & 0xf0) / 16 * 1000) + ((bcd[offset+2] & 0x0f) * 100) +
               ((bcd[offset+3] & 0xf0) / 16 * 10) + (bcd[offset+3] & 0x0f);

        setDate( month, day, year );
        return this;
    }
    
    

    
    
    
    /**
     * toBCD
     *
     * Set this date object into a 4 byte BCD
     *
     * @param bcdDate byte[]
     * @return void
     */
    public void toBCD(byte[] bcd) { this.toBCD( bcd, 0 ); }

    /**
     * toBCD
     *
     * Set this date object into a 4 byte BCD
     *
     * @param bcdDate byte[]
     * @param int offset
     * @return Date
     */
    public void toBCD( byte[] data, int offset ){

        String s = String.format( "%04d%02d%02d", this.month, this.day, this.year );
        Decoders.toBCD( s, data, 4, offset );
        return;
    }
    
    

    
    
    
    

    /**
     * fromJulian
     *
     * Set this date object from an int Julian date as defined as
     * the number of days since 01-01-0000
     *
     * Note: The algorithms used here were published in CACM 11(10):657
     * by Fliegel and Van Flandern, Oct 1968.  I took these from a FORTRAN
     * adaptation of the algorithm published on the web.
     *
     * @param julian int
     */
    public Date fromJulian(int julian) {

        // I use ints instead of longs because ints are 32 bit in Java

        int month;
        int day;
        int year;
        int l;
        int n;

        l = julian + 68569;
        n = 4 * l / 146097;
        l = l - ( 146097 * n + 3 ) / 4;
        year = 4000 * ( l + 1 ) / 1461001;
        l = l - 1461 * year / 4 + 31;
        month = 80 * l / 2447;
        day = l - 2447 * month / 80;
        l = month / 11;
        month = month + 2 - 12 * l;
        year = 100 * ( n - 49 ) + year + l;

        setDate( month, day, year );
        return this;
    }


    
    
    
    
    
    /**
     * toJulian
     *
     * Return a Julian date given a month, day, and year
     *
     * Note: The algorithms used here were published in CACM 11(10):657
     * by Fliegel and Van Flandern, Oct 1968.  I took these from a FORTRAN
     * adaptation of the algorithm published on the web.
     *
     * @param month int
     * @param day int
     * @param year int
     */
    public int toJulian(int month, int day, int year) {

        // I use ints instead of longs because ints are 32 bit in Java

        int julian;

        // make sure numbers are in bounds
        if ( month < 1) month = 1;
        if ( month > 12 ) month = 12;
        if ( day < 1 ) day = 1;
        if ( day > 31 ) day = 31;

        julian = day - 32075 + 1461*(year+4800+(month-14)/12)/4
                 + 367*(month-2-((month-14)/12)*12)/12
                 - 3*((year+4900+(month-14)/12)/100)/4;

        return ( julian );
    }



    /**
      * toDOW
      *
      * Return a day of week as 0 - 6 given a julian date
      *
      *
      * @param julian int
      */
     public int toDOW(int julian) {
         return ((julian+1) % 7);
     }



    /**
     * getMonth
     */
    public int getMonth() {
        return ( this.month );
    }

    /**
     * getDay
     */
    public int getDay() {
        return ( this.day );
    }

    /**
     * getYear
     */
    public int getYear() {
        return ( this.year );
    }

    /**
     * getDOW
     */
    public int getDOW() {
        return ( this.dow );
    }

    /**
     * getJulian
     */
    public int getJulian() {
        return (this.JulianDate);
    }

    /**
     * isValid
     */
    public boolean isValid() {
        if (( month < 1 ) || ( month > 12 )
            || ( day < 1 ) || ( day > 31 )
            || ( year < 1800 ) || ( year > 2199 ))

            return false;
        return true;
    }
    
    
    
    
    /**
     * getJavaDate()
     * 
     */
    public java.util.Date getJavaDate(){
  		final java.util.Calendar calendar = java.util.Calendar.getInstance();
    	calendar.set( this.year, this.month-1, this.day );
    	final java.util.Date result = calendar.getTime();
    	return result;
    }
    

    /**
     * getSystemDate()
     * 
     * This class sets the Date object according to the system date.
     * 
     */
    public usrlib.Date getSystemDate(){
  		final java.util.Calendar calendar = java.util.Calendar.getInstance();
  		java.util.Date jdate = calendar.getTime();
  		this.setDate( calendar.get( calendar.MONTH ) + 1, calendar.get( calendar.DAY_OF_MONTH ), calendar.get( calendar.YEAR ));
    	return this;
    }
    
    
    /**
     * getSystemDate()
     * 
     * This static class returns a Date object initialized to the system date.
     * 
     */
    public static usrlib.Date today(){
    	usrlib.Date date = new usrlib.Date().getSystemDate();
    	return date;
    }
    
    
    
    
    
    //**************************************************************************************
    //**************************************************************************************
    //**************************************************************************************
    
    public static int getAgeYears( Date bdate ){
    	return getAgeYears( bdate, Date.today());
    }
    
    public static int getAgeYears( Date bdate, Date refDate ){
    	
    	// is date valid?
    	if (( ! bdate.isValid() ) || ( ! refDate.isValid())) return -1;
    	
    	int age = refDate.getYear() - bdate.getYear() - 1;
    	if (( refDate.getMonth() > bdate.getMonth()) 
    		|| (( refDate.getMonth() == bdate.getMonth()) && ( refDate.getDay() >= bdate.getDay()))) 
    			++age;
    	return age;
    }

    
    
    
    
    
    public static int getAgeMonths( Date b ){
        return getAgeMonths( b, Date.today() );
    }
        	     
    public static int getAgeMonths( Date b, Date r ){
    	
    	// is date valid?
    	if (( ! b.isValid() ) || ( ! r.isValid())) return -1;
    	
    	int months = r.getYear() * 12 - b.getYear() * 12;
  
    	months += (r.getMonth() - b.getMonth());
    	
    	//if ( r.getMonth() > b.getMonth()) months += (r.getMonth() - b.getMonth());
    	//if ( r.getMonth() < b.getMonth()) months -= (b.getMonth() - r.getMonth());
    	
    	if ( r.getDay() < b.getDay()) --months;
    	
    	return months;
    }
    
    
    public static int getAgeDays( Date b ){
        return getAgeDays( b, Date.today() );
    }
        	     
    public static int getAgeDays( Date b, Date r ){
    	
    	// is date valid?
    	if (( ! b.isValid() ) || ( ! r.isValid())) return -1;
    	
    	int age = r.getJulian() - b.getJulian();
    	return age;
    }
    
    
    
    
    public static String getPrintableAge( Date b ){
        return getPrintableAge( b, Date.today() );
    }

    public static String getPrintableAge( Date bdate, Date refDate ){
    	
    	int age = getAgeYears( bdate, refDate );
    	if ( age < 0 ) return "??";

    	if ( age > 2 ) return age + " yrs";

    	age = getAgeMonths( bdate, refDate );    	
    	if ( age >= 3 ) return age + " months";
    	
    	age = getAgeDays( bdate, refDate );
    	if ( age > 13 ) return ( age / 7 ) + " weeks";
    	
    	return age + " days";
    }

    
    
    public String toString(){
    	return this.getPrintable();
    }
}


/**/
