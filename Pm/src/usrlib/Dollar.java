package usrlib;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import palmed.SystemHelpers;

/**
 * <p>Title: PAL/MED </p>
 * 
 * <p>Description: The Dollar class </p>
 * 
 * <p>Copyright: Copyright (c) 2016 </p>
 * 
 * <p>Company: PAL/MED </p>
 * 
 * @author Prince A Antigha
 * @version TBD
 */



@SuppressWarnings("unused")

public class Dollar {


	private BigDecimal amount;  // = new BigDecimal("0.00");
	
	private Dollar dollar = null; // the default dollar amount set to null
	
	/**
	 * Set a Dollar
	 * @param dollar
	 */
	public void setDollar(Dollar dollar) {
		
		this.dollar = dollar;
	}

	/**
	 * Get a Dollar 
	 * @return
	 */
	public Dollar getDollar() {
		return dollar;
	}
	
	

	/**
	 * Dollar 
	 * 
	 * Sets String as BigDecimal 
	 * 
	 * @param dollar
	 */
	public Dollar(String dollar){
		setDollar(dollar);
	}
	
	
	
	
	/**
	 * Create a Dollar amount from a string
	 * 
	 * @param string dollar1
	 * @return BigDecimal
	 */
	
	public Dollar setDollar( String dollar1 ){
		
		this.amount =  this.amount(dollar1);
		
		return this;
		
	}
	
	
	
	/**
	 * Sets string as Big decimal
	 * 
	 * @param amount1
	 * @return BigDecimal
	 */
	public BigDecimal amount(String amount1){
		
		BigDecimal rawam; // raw value of dollar
		rawam = new BigDecimal(amount1.replaceAll("[^\\d|.|-]",""));
		
		amount = rawam.setScale(3, BigDecimal.ROUND_HALF_UP);
		
		return ( amount );
	}

	
	
	
	/**
	 * Dollar 
	 * 
	 * Set a dollar from a BCD byte[] data
	 * 
	 * @param bcdDollar
	 */
	public Dollar(byte[] bcdDollar) {
		this.fromBCD(bcdDollar);
	}
	
	
	
	
	/**
	 * Dollar 
	 * 
	 * Set a Dollar from a BCD byte[] and an integer offset
	 * 
	 * @param byte[] bcdDollar
	 * @param int offset
	 */
	public Dollar(byte[] bcdDollar, int offset ) {
		// call fromBCD() to carry this out 
		this.fromBCD(bcdDollar, offset);
		
	}

	
	
	
	/**
	 * getPrintable()
	 * 
	 * Get Printable Dollar with default setting 
	 * 
	 * @return
	 */
	
	public String getPrintable() {
		return (this.getPrintable(1));
		
	}
	
	
	
	/**
	 * getPrintable()
	 * 
	 * get printable dollar based on the format type
	 * 
	 * @param format
	 * @return printable Dollar amount
	 */
	public String getPrintable(int format){
		
		switch (format) {
		
		case 1: 
		default:
			return (amount.setScale(2, BigDecimal.ROUND_HALF_UP).abs().toString());
		
		case 2:
			return(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		}
	}
	
	/**
	 * Deletes a character from a string .
	 * 
	 * @param str
	 * @param index
	 * @return
	 */	
	private static String deleteachar(String str, int index){
		return str.substring(0,index)+ str.substring(index+1);		
	}
	
		
	public final int DLRSIGN = 1;   // add a Dollar Sign
	public final int SIGN =    2;   // print trailing minus sign  
	public final int PSIGN =   4;   //  print trailing positive sign 
	public final int LD_0=     8;   //  print leading zeros  
	public final int LD_SPC =  16;  //  print leading spaces 
	public final int NODEC =   32;  //  leave off decimals 
	public final int NODP=     64;  //  leave out decimal point  
	public final int NOTRSPC=  128; //  leave off trailing spaces 
	
	
	/**
	 * The format String, formats the dollar amount based on the sent flags
	 * @param flags
	 * @return desired string format
	 */
	public  String format(int flags){
		
		String fancyDollar = this.getPrintable(2);
		
		boolean wasneg = false;
		
		if (fancyDollar.charAt(0) == '-'){ 
			wasneg = true;
			fancyDollar = deleteachar(fancyDollar, 0);			
		}
				
		if ((flags & LD_0) == LD_0) fancyDollar = ("000000.00"+fancyDollar).substring(fancyDollar.length());
			//DecimalFormat nf6 = new DecimalFormat("#000000.00");
			//fancyDollar = nf6.format(amount);
				
		if ((flags & LD_SPC) == LD_SPC) fancyDollar = String.format("%9s", fancyDollar);
		//if ((flags & LD_SPC) == LD_SPC) fancyDollar = ("      .   "+fancyDollar).substring(fancyDollar.length());
		
		if ((flags & NODEC) == NODEC) fancyDollar = fancyDollar.substring(0,(fancyDollar.length() -4));
		
		if ((flags & NODP) == NODP) fancyDollar = deleteachar(fancyDollar, (fancyDollar.length()-4));
		
		if ((flags & DLRSIGN) == DLRSIGN) fancyDollar = "$"+fancyDollar;
		
		if (((flags & SIGN) == SIGN) && wasneg ) fancyDollar = fancyDollar+"-";
		
		if (((flags & PSIGN) == PSIGN) && !wasneg) fancyDollar = fancyDollar+"+";
				
		if ((flags & NOTRSPC) == NOTRSPC) fancyDollar = fancyDollar.trim();
		
		return fancyDollar;
		
	}
	
	
	/**
	 * Dollar()
	 * 
	 * Construct dollar from integer amount 
	 * 
	 * @param amount
	 */
	public Dollar( int dollar){
		setDollar(dollar);
	}
	
	
	
	/**
	 * setDollar()
	 * 
	 * set Dollar as a BigDecimal
	 * 
	 * @param amount as integer
	 * @return Dollar
	 */
	public Dollar setDollar (int dollar){
	 	 		
	 this.amount = this.amount(dollar);
	 
	 return this;
	
	}
	
	
	
	/**
	 * 
	 * @param amount1
	 * @return BigDecimal
	 */
	public BigDecimal amount (int amount1) {
		
		BigDecimal rawam; //Actual amount 
		rawam = new BigDecimal((Integer.toString(amount1)));
		
		amount = rawam.setScale(3, BigDecimal.ROUND_CEILING);
		
		return ( amount );
	}


	
	
	/**
	 * byte2int
	 * 
	 * Converts byte(b) to integer (i)
	 * 
	 * @param byte
	 * @param integer
	 */
	static void byte2int (byte[] b, int i){
		i = ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16)
        | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);
		
		return;
	}
	
	
	
	/**
	 * abs()
	 * 
	 * Get the absolute Dollar amount
	 * 
	 * @param amount
	 * @return abs value
	 */
	
	public BigDecimal absolute(){
	
	return(this.amount.abs());
		
	}
	
	
	
	
	/**
	 * add()
	 * 
	 * Get the sum of adding two dollar amounts 
	 * 
	 * @param dollar1
	 * @return sum 
	 */
	public BigDecimal add(Dollar dollar1){
						
		BigDecimal sum,amount1,amount2; //sum of the two  dollar amounts
		amount1 = this.amount;
		amount2 = dollar1.amount;
		
		sum = amount1.add(amount2);
		
		return ( sum );
		
	}
	
	
	
	
	/**
	 * sub()
	 * 
	 * Finds the difference from two Dollar amounts 
	 * 
	 * @param dollar1
	 * @return difference
	 */
	public BigDecimal sub(Dollar dollar1 ){
		
		BigDecimal difference; //the difference of dollar amounts
		
		difference = (this.amount).subtract(dollar1.amount);
				
		return ( difference );
	}

	
	
	
	/**
	 * comp()
	 * 
	 * Get the absolute difference in Dollar's between two amounts
	 *  
	 * @param dollar1
	 * 
	 * @return absolute difference
	 */
	public BigDecimal comp(Dollar dollar1){
		
				
		BigDecimal dif,comp; //the difference between the two dollar amounts, the absolute value
		
		dif = (this.amount).subtract(dollar1.amount);
		
		comp = dif.abs();
		
		return( comp );
	}
	
	
	
	
	/**
	 * div()
	 * 
	 * Get the quotient Dollar when the dividend (Dollar) is divided by the divisor (Dollar)
	 * 
	 * @param divisor
	 * @return quotient
	 */
	public BigDecimal div(Dollar divisor){
		
		
		BigDecimal quotient, quo3; //the value of the quotient, the quotient to 3 decimal places
		
		quotient = (this.amount).divide(divisor.amount, 5, RoundingMode.CEILING);
		
		quo3 = quotient.setScale(3, BigDecimal.ROUND_CEILING);
		
		return( quo3 );
	}
	
	
	
	
	/**
	 *  integer
	 * 
	 * Converts a Dollar amount to an integer
	 * 
	 * @param dollar
	 * @return integer
	 */
	public int integer(){
						
		return (amount.intValue()) ; 
		
	}
	
	
	
	
	
	/**
	 * mult()
	 * 
	 * Gives a product from multiplying 2 dollar amounts 
	 * @param factor1
	 * @return product
	 */
	public BigDecimal mult(Dollar factor){
					
		BigDecimal product,pro3; //the product of the two dollar amounts, the product to 3 decimal places.
		
		product = (this.amount).multiply(factor.amount );
		
		pro3 = product.setScale(3, BigDecimal.ROUND_CEILING);
		
		return( pro3 ) ;
	}

	
	
	
	
	/**
	 * negative()
	 * 
	 * This gives the negative amount of the dollar
	 * 
	 * @return negative value
	 */
	public BigDecimal negative() {
		
		return (new BigDecimal("-"+this.amount.toString()));
	}
	
	
	
	
	
	/**
	 * zero()
	 * 
	 * This zero's the amount of the dollar 
	 * 
	 * @return zero
	 */
	public BigDecimal zero(){
		
		BigDecimal zero = new BigDecimal("0");
		
		this.amount = zero;
		
		return( zero );
	}
	
	
	
	
	
	/**
	 * toBCD
	 * 
	 * Set the Dollar to 4 byte BCD
	 * 
	 * @param bcd byte[]
	 */
	public void toBCD(byte[] bcd){ this.toBCD(bcd, 0);	}

	
	
	
	/**
	 *toBCD
	 *
	 * Set the Dollar amount(BigDecimal) to a 4 byte BCD
	 * 	
	 * @param byte[] data
	 * @param int offset
	 */
	public void toBCD ( byte[] data, int offset ){
		
		BigDecimal da; // the dollar amount as it will be sent to bcd
		da = this.amount.setScale(2, BigDecimal.ROUND_CEILING);
		
		
		String d = da.toString(); // dollar amount as a string
		//System.out.println("The string before it becomes bcd:"+ d);
		
				
		boolean isNegative = false;
		
		if (d.charAt(0) == '-'){ isNegative = true; }
		
		d = ("000000.00"+d).substring(d.length());
		
		// exception for negative numbers > 5 numbers or +ve number greater than 6 figures.
		if (d.length()> 9) { SystemHelpers.seriousError( "This dollar amount is too large to be currently stored in bcd format!");} 
		
		else if ((d.length() < 9) || (d.length() == 9)){ Decoders.toBCD(d, data, 4, offset );}
		
		 byte minusSign = (byte) 0xf0 ; //Minus sign (-)
		                                     
		if (isNegative){ (data[0]) = (byte) (data[0] | minusSign) ; }
		
		//System.out.println("byte string 'tobcd' [0]:"+Integer.toHexString((int) data[0]).toUpperCase());
		
		return;
		
	}
	
	
	
	
	/**
	 * fromBCD
	 * 
	 * Set a Dollar amount from a 4 byte BCD
	 * 
	 * @param byte[] bcd
	 * @return Dollar
	 */
	public Dollar fromBCD (byte[] bcd) { return fromBCD( bcd, 0 ); }
	
	
	
	
	/**
	 * fromBCD
	 * 
	 * Set the Dollar amount from a 4 byte BCD 
	 * 
	 * @param data byte[]
	 * @param  int offset
	 * @return Dollar
	 */
	public Dollar fromBCD ( byte[] data, int offset ){
		
		byte[] datanew = new byte[4];
		
		/*System.out.println("byte string [0]:"+Integer.toHexString((int) data[0]).toUpperCase());
		System.out.println("byte string [1]:"+Integer.toHexString((int) data[1]).toUpperCase());
		System.out.println("byte string [2]:"+Integer.toHexString((int) data[2]).toUpperCase());
		System.out.println("byte string [3]:"+Integer.toHexString((int) data[3]).toUpperCase());*/
		
		boolean wasNegative = false; //previous Dollar amount negative status
		
		//if(Integer.toHexString((int) data[0]).toUpperCase().contains("F")){
		if(data[0] == (0xf0 | data[0]))	{
			wasNegative = true;
			//data[0] = (byte) ((0x00 + data[0]<<4 )); //<<4
			data[0] = (byte) (data[0] & 0x0f);
			datanew = data;
		}
		
		else { datanew = data; }
		
		String out = Decoders.fromBCD(datanew, 4, offset);
		// out is the string that is returned in Decoders when converted form bcd 
		String negout = null;
		
		out = new StringBuilder(out).insert((out.length()-2), ".").toString();
		
		if (wasNegative){
				
		negout = "-"+out;
		
		//System.out.println("negout: "+negout);
		
		/*if (negout.charAt(2) == '0' && negout.length() > 7){
		StringBuilder	negout1 = new StringBuilder(negout);
		negout1.deleteCharAt(2);
		negout = negout1.toString();
		
		}*/		
	  }
		
		else {
			
		negout = out; 
		
	  }
		
		setDollar(negout);
		
		return this;
		
	}
	
	
	
}
