package palmed;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;

public class TransferVitals {
	
	
	
	
	
	public static boolean transfer( Date startDate, Date endDate ){
		
		int startRec = 2; //7249
		
		
		
		System.out.println( "Transferring vitals...." );

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgHt = false;
			boolean flgWt = false;
			boolean flgBP = false;
			boolean flgVisit = false;
			
			Rec ptRec = dirPt.getRec();
			
			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			//Used just for testing
			//TEMP medpt.setVitalsReca( new Reca( 0, 0 ));
			//TEMP medpt.write();


			SoapNote soap = null;			
			Reca reca = null;
			Reca currentReca = null;
			
			// loop through progress notes to get to oldest
			for ( reca = medpt.getSoapReca(); Reca.isValid( reca ); reca = soap.getLLHdr().getLast()){		
				currentReca = reca;
				soap = new SoapNote( reca );				
			}
			
			
			
			// loop through progress notes from oldest (which we just found) to newest
			for ( reca = currentReca; Reca.isValid( reca ); reca = soap.getLLHdr().getNext()){				
				soap = new SoapNote( reca );				
				
				// within date range?
				if ( withinDateRange( soap.getDate(), startDate, endDate )){					
					
					// are there already corresponding vitals?
					if ( alreadyVitals( medpt, soap.getDate())) continue;
					

					// examine soap text for pertinant vitals data
					int p02 = 0;
					
					Date date = soap.getDate();
					String text = soap.getText().toLowerCase();
					boolean flgVitals = false;
					
					// get heart rate
					int hr = getHR( text );
					if ( hr > 0 ) flgVitals = true;

					int rr = getRR( text );
					if ( rr > 0 ) flgVitals = true;

					int sbp = 0;
					int dbp = 0;
					int[] bp = getBP( text );
					if ( bp.length >= 2 ){
						sbp = bp[0];
						dbp = bp[1];
					}
					
					if (( sbp > 0 ) && ( dbp > 0 )) flgVitals = true;

					double ht = getHt( text );
					if ( ht > 0 ){
						ht = UnitHelpers.getCentimeters( ht );		// convert to centimeters from inches
						flgVitals = true;
					}
					
					int iwt = 0;	// integer wt in grams
					double wt = getWt( text );
					if ( wt > 0 ){
						iwt = (int) UnitHelpers.getGramsFromPounds( wt );	// convert to grams from pounds
						flgVitals = true;
					}
					
					double hc = getHC( text );
					if ( hc > 0 ){
						// assume HC in cm
						flgVitals = true;
					}
					
					double temp = getTemp( text );
					if ( temp > 0 ){
						temp = UnitHelpers.getCelcius( temp );		// convert deg F to deg C
						flgVitals = true;
					}
					
					int pO2 = getPO2( text );
					if ( pO2 > 0 ) flgVitals = true;
					
				
					
					
					// save new vitals record if vitals were recorded
					if ( flgVitals ){
						
						Vitals.postNew( ptRec, date, temp, hr, rr, sbp, dbp, pO2, ht, iwt, hc );
					}
				}
			}			

			//break;
		}
		
		dirPt.close();
		

		return true;
	}
	
	
	
	
	
	

	static int getHR( String text ){
		
		int hr = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Hh][Rr][: ]*[ wais]*\\d+[ ]*[Ii]*[/]*[Rr]*" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			hr = getNum( m.group());
			
			//TODO - regular/irregular
		}
		
		return hr;
	}
	
	
	
	
	
	static int getRR( String text ){
		
		int rr = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Rr][Rr][: ]*[ wais]*\\d+[ ]*[Ii]*[/]*[Rr]*" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			rr = getNum( m.group());
			
		} else {
						
			java.util.regex.Pattern p3 = java.util.regex.Pattern.compile( "\\b[Rr][Ee][Ss][Pp][: ]*[ wais]*\\d+" );		
			java.util.regex.Matcher m3 = p3.matcher (text);
			
			if ( m3.find()){				
				System.out.println( m3.group());
				rr = getNum( m3.group());
			}
		}
		
		return rr;
	}
	
	
	
	
	static int getPO2( String text ){
		
		int po2 = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Pp][Uu]*[Ll]*[Ss]*[Ee]*[ ]*[Oo][Xx]*[2]*[: ]*[ wais]*\\d+[ ]*[Ii]*[/]*[Rr]*" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			po2 = getNum( m.group());
			
			//TODO - regular/irregular
		}
		
		return po2;
	}
	
	
	
	
	

	
	
	
	
	public static int[] getBP( String text ){
		
		int sbp = 0;
		int dbp = 0;
		
		try {

		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Bb][/]*[Pp][: ]*[ wais]*\\d+/\\d+" );		
		java.util.regex.Matcher m = p.matcher (text);


		if ( m.find()){
		
			String s1 = m.group();
			System.out.println( s1 );
			
			java.util.regex.Pattern p2 = java.util.regex.Pattern.compile( "\\d+" );
			java.util.regex.Matcher m2 = p2.matcher( s1 );
			if ( m2.find()){
				String s2 = m2.group();
			
				System.out.println( s2);
				sbp = EditHelpers.parseInt( s2 );
				
				String s3 = s1.substring( m2.end() +1, s1.length());
				System.out.println( s3 );
				dbp = EditHelpers.parseInt( s3 );
			}
			
		} 
		
		} catch ( Exception e ){
			System.out.println( "not found exception 1");
		}

		
		if (( dbp == 0 ) && ( sbp == 0 )){
						
			try {

			java.util.regex.Pattern p3 = java.util.regex.Pattern.compile( "\\bblood pressure[: ]*[ wais]*\\d+/\\d+" );		
			java.util.regex.Matcher m3 = p3.matcher (text);

			if ( m3.find()){
				
				String s1 = m3.group();
				System.out.println( s1 );
	
				java.util.regex.Pattern p2 = java.util.regex.Pattern.compile( "\\d+" );
				java.util.regex.Matcher m2 = p2.matcher( s1 );
				if ( m2.find()){
					String s2 = m2.group();
				
					System.out.println( s2);
					sbp = EditHelpers.parseInt( s2 );
					
					String s3 = s1.substring( m2.end() +1, s1.length());
					System.out.println( s3 );
					dbp = EditHelpers.parseInt( s3 );
				}
			}
			
			} catch ( Exception e ){
				System.out.println( "not found exception 2");
			}
		}
		
		int[] i = { sbp, dbp };
		return i;
	}
	
	
	
	
	public static double getHt( String text ){
		
		double ht = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Hh][Ee]*[Ii]*[Gg]*[Hh]*[Tt][: ]*[ wais]*\\d+[.]*\\d+" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			ht = getDouble( m.group());
			
			//TODO - regular/irregular
		}
		
		return ht;
	}
	
	
	
	
	
	public static double getWt( String text ){
		
		double wt = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Ww][Ee]*[Ii]*[Gg]*[Hh]*[Tt][: ]*[ wais]*\\d+[.]*\\d+" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			wt = getDouble( m.group());
			
			//TODO - regular/irregular
		}
		
		return wt;
	}
	
	
	
	
	
	public static double getHC( String text ){
		
		double hc = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Hh][Ee]*[Aa]*[Dd]*[ ]*[Cc][irc]*[: ]*[ wais]*\\d+[.]*\\d+" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			hc = getDouble( m.group());
			
			//TODO - regular/irregular
		}
		
		return hc;
	}
	
	
	
	
	
	public static double getTemp( String text ){
		
		double temp = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Tt][Ee]*[Mm]*[Pp]*[: ]*[ wais]*\\d+[.]*\\d+" );		
		java.util.regex.Matcher m = p.matcher (text);
		
		if ( m.find()){
		
			System.out.println( m.group());
			temp = getDouble( m.group());
			
			//TODO - F/C
		}
		
		return temp;
	}
	
	
	
	
	



	
	static int getNum( String text ){
		int ret = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\d+" );
		java.util.regex.Matcher m = p.matcher( text );
		
		if ( m.find()){		
			System.out.println( m.group());
			ret = EditHelpers.parseInt( m.group());
		}
		
		return ret;
	}
	
	
	static double getDouble( String text ){
		double ret = 0;
		
		java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\d+[.]*\\d+" );
		java.util.regex.Matcher m = p.matcher( text );
		
		if ( m.find()){		
			System.out.println( m.group());
			ret = EditHelpers.parseDouble( m.group());
		}
		
		return ret;
	}
	
	
	
	// are there vitals for this date already?
	static boolean alreadyVitals( MedPt medpt, Date refDate ){
		
		Vitals v = null;
		
		for ( Reca reca = medpt.getVitalsReca(); Reca.isValid( reca ); reca = v.getLLHdr().getLast()){				
			v = new Vitals( reca );				
			if ( v.getDate().equals( refDate )){					
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	static boolean withinDateRange( Date refDate, Date startDate, Date endDate ){
		return  (( startDate.compare( refDate ) <= 0 ) && ( endDate.compare( refDate ) >= 0 ));

	}
	


}
