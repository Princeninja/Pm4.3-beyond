package usrlib;

public class UnitHelpers {
	
	
	
	public enum Units {
		POUNDS(1, "pounds", "lbs" ),
		GRAMS( 2, "grams", "gm" ),
		KILOGRAMS( 3, "kilograms", "kg" ),
		OUNCES( 4, "ounces", "oz" ),
		INCHES( 5, "inches", "in" ),
		FEET( 6, "feet", "ft" ),
		METERS( 7, "meters", "m" ),
		CENTIMETERS( 8, "centimeters", "cm" ),
		FAHRENHEIT( 9, "Fahrenheit", "F" ),
		CELSIUS( 10, "Celsius", "C" ),
		BMI( 11, "kg/m2", "kg/m2" );
		
		private int code;
		private String label;
		private String abbr;
		
		Units ( int code, String label, String abbr ){
			this.code = code;
			this.label = label;
			this.abbr = abbr;
		}
		
		public int getCode(){ return this.code; }		
		public String getLabel(){ return this.label; }		
		public String getAbbr(){ return this.abbr; }		
	}

	
	
	// Temperature ******************************************************************************
	
	/**
	 * getCelcius() - Get celsius temp given a degrees fahrenheit.
	 * 
	 * @param f
	 * @return
	 */
	public static double getCelcius( double f ){
		
		return ((5.0 / 9.0) * (f - 32));		
	}

	
	/**
	 * getCelcius() - Get fahrenheit temp given a degrees celsius.
	 * 
	 * @param f
	 * @return
	 */
	public static double getFahrenheit( double c ){
		
		return (( c * 9.0 / 5.0) + 32 );		
	}



	
	
	
	
	
	
	// Weight **********************************************************************************
	
	/**
	 * getPoundsFromKilograms() - Get pounds given a weight in kilograms.
	 * 
	 * @param kg
	 * @return
	 */
	public static double getPoundsFromKilograms( double kg ){ return ( kg / 0.453 ); }

	
	/**
	 * getPoundsFromGrams() - Get pounds given a weight in grams.
	 * 
	 * @param grams
	 * @return
	 */
	public static double getPoundsFromGrams( double grams ){ return ( grams / 453 ); }

	
	
	
	/**
	 * getKilogramsFromPounds() - Get kilograms given a weight in pounds.
	 * 
	 * @param w
	 * @return
	 */
	public static double getKilogramsFromPounds( double w ){ return ( w * 0.453 ); }

	/**
	 * getKilogramsFromGrams() - Get kilograms given a weight in grams.
	 * 
	 * @param w
	 * @return
	 */
	public static double getKilogramsFromGrams( double w ){ return ( w / 1000 ); }

	
	
	/**
	 * getGramsFromPounds() - Get grams given a weight in pounds.
	 * 
	 * @param w
	 * @return
	 */
	public static double getGramsFromPounds( double w ){ return ( w * 453 ); }

	/**
	 * getGramsFromKilograms() - Get grams given a weight in kilograms.
	 * 
	 * @param w
	 * @return
	 */
	public static double getGramsFromKilograms( double w ){ return ( w * 1000 ); }



	
	
	
	
	
	
	// Length *******************************************************************************
	
	/**
	 * getInches() - Get inches given a length in centimeters.
	 * 
	 * @param cm
	 * @return
	 */
	public static double getInches( double cm ){
		
		return ( cm / 2.54 );		
	}

	
	/**
	 * getCentimeters() - Get centimeters given a length in inches.
	 * 
	 * @param in
	 * @return
	 */
	public static double getCentimeters( double in ){
		
		return ( in * 2.54 );		
	}




}
