package palmed;

import org.python.antlr.PythonParser.test_return;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.RandomFile;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.RandomFile.Mode;

public class CdmDashboard {

	
		
		public static boolean compile( Listbox lbox, int month, int year ){
			
			int startRec = 2; //7249
			
			//year = year % 100;
			
			
			System.out.format( "Compiling T$ data.  Month=%d, Year=%d  ....\n", month, year );
			
			
			

			DirPt dirPt = DirPt.open();
			dirPt.setRec( new Rec( startRec ));	
			
			while( dirPt.getNext()){

				
				Rec ptRec = dirPt.getRec();				
				MedPt medpt = new MedPt( dirPt.getMedRec());
				
				//Used just for testing
				//TEMP medpt.setVitalsReca( new Reca( 0, 0 ));
				//TEMP medpt.write();
				//System.out.format( "     ptrec=%d\n", ptRec.getRecInt());

				NotesNursing note = null;			
				Reca reca = null;
				Reca currentReca = null;
				
				int totalmin = 0;
				int occurance = 0;
				
				
				// loop through progress notes 
				for ( reca = medpt.getNurseReca(); Reca.isValid( reca ); reca = note.getLLHdr().getLast()){				
					note = new NotesNursing( reca );				
					
					// within date range?
					int noteMonth = note.getDate().getMonth();
					int noteYear = note.getDate().getYear();
					//System.out.format( "          Month=%d, Year=%d  ....\n", noteMonth, noteYear );
					
					if (( month == noteMonth ) && ( year == noteYear )){	
						//System.out.format( "DATE MATCH\n" );

						String text = note.getNoteText().toLowerCase();
						
						int min = getTdollar( text );
						if ( min > 0 ){
							totalmin += min;
							++occurance;
							
							System.out.format( "     %40s %d %40s\n", dirPt.getName().getPrintableNameLFM(), min, text );
						}
					}	
				}		
						
					
						
						
				// save new vitals record if vitals were recorded
				if ( occurance > 0 ){
					System.out.format( "%40s %d %d\n", dirPt.getName().getPrintableNameLFM(), totalmin, occurance );
					
					//Vitals.postNew( ptRec, date, temp, hr, rr, sbp, dbp, pO2, ht, iwt, hc );
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( lbox );
					i.setValue( ptRec );
					
					String s = (new DirPt( ptRec )).getName().getPrintableNameLFM();
					new Listcell( s ).setParent( i );
					
					new Listcell( String.valueOf( totalmin )).setParent( i );
					new Listcell( String.valueOf( occurance )).setParent( i );		

				}
	

				//break;
			}
			
			dirPt.close();
			

			return true;
		}
		
		
		
		
		


		
		

		static int getTdollar( String text ){
			
			int min = 0;
			
			java.util.regex.Pattern p = java.util.regex.Pattern.compile( "\\b[Tt][$][: ]*[ wais]*\\d+[ ]*[Ii]*[/]*[Rr]*" );		
			java.util.regex.Matcher m = p.matcher (text);
			
			if ( m.find()){
			
				System.out.println( m.group());
				min = getNum( m.group());
				
			}
			
			return min;
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
		
		
		
		
		
		static boolean withinDateRange( Date refDate, Date startDate, Date endDate ){
			return  (( startDate.compare( refDate ) <= 0 ) && ( endDate.compare( refDate ) >= 0 ));

		}
		


}
