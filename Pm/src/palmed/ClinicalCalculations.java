package palmed;

public class ClinicalCalculations {
	
	
	/**]
	 * computeBMI() - Compute a BMI given a height and weight
	 * 
	 * @param wt - weight in grams
	 * @param ht - height in centimeters
	 * @return double
	 */
	public static double computeBMI( double wt, double ht ){
		
    	if (( wt == 0 ) || ( ht == 0 )) return 0;
    	ht = ht / 100;		// convert height from centimeters to meters
    	wt = wt / 1000;		// convert weight from grams to kilograms
    	double bmi = wt / ( ht * ht );
    	return bmi;
	}

}
