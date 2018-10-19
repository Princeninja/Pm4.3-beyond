package palmed;

import java.util.List;
import java.util.Map;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;

public class TransferPars {

	
	public static boolean transfer( Date startDate, Date endDate ){
		
		int startRec = 2; //7249
		
		
		
		System.out.println( "Transferring PARs...." );

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgHt = false;
			boolean flgWt = false;
			boolean flgBP = false;
			boolean flgVisit = false;
			
			Rec ptRec = dirPt.getRec();			
			MedPt medpt = new MedPt( dirPt.getMedRec());
			

			if ( ! Rec.isValid( medpt.getHstRec())) continue;
				
			History hst = new History( medpt.getHstRec());
			List<String> allergies = hst.getAllergies();
			
			for ( String s: allergies ){
				
				if (( s.indexOf( "NKDA" ) >= 0 ) || s.equalsIgnoreCase( "NKA" ) || ( s.toLowerCase().indexOf( "no known") >= 0 )){
					//ParUtils.setNKDAFlag( ptRec, true);
					//System.out.println( s + "==>NKDA flag set" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "PCN" ) || s.equalsIgnoreCase( "Penicillin" ) || s.equalsIgnoreCase( "Penicillins" ) 
						|| s.equalsIgnoreCase( "Pencillin" )){
					//Par.postNew( Date.today(), ptRec, "Penicillins", 245, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Penicillins 245" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "Sulfa" ) || s.equalsIgnoreCase( "Sulfas" ) || s.equalsIgnoreCase( "Sulfonamides" )){
					//Par.postNew( Date.today(), ptRec, "Sulfa (sulfonamides)", 305, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Sulfonamides 305" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "ACE" ) || s.equalsIgnoreCase( "ACE inhibitors" ) || s.equalsIgnoreCase( "ACEs" )){
					//Par.postNew( Date.today(), ptRec, "Ace Inhibitors", 8, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Ace Inhibitors 8" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "ASA" ) || s.equalsIgnoreCase( "aspirin" )){
					//Par.postNew( Date.today(), ptRec, "Aspirin", 6554, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Aspirin 6554" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "codeine" ) || s.equalsIgnoreCase( "codiene" )){
					//Par.postNew( Date.today(), ptRec, "Codeine", 7370, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Codeine 7370" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "Statin" ) || s.equalsIgnoreCase( "Statins" )){
					//Par.postNew( Date.today(), ptRec, "Hmg-Coa Reductase Inhibitors", 9080, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Hmg-Coa Reductase Inhibitors 9080" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "IVP Dye" ) || s.equalsIgnoreCase( "IV Dye" ) || s.equalsIgnoreCase( "IV Contrast" )
						|| s.equalsIgnoreCase( "IVP" ) || s.equalsIgnoreCase( "Contrast Dye" ) || s.equalsIgnoreCase( "Dye" )){
					//Par.postNew( Date.today(), ptRec, "IVP Dye, Iodine Containing", 193, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>IVP Dye, Iodine Containing 193" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "tetanus" )){
					//Par.postNew( Date.today(), ptRec, "Tetanus Toxoid", 316, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Tetanus Toxoid 316" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "erythromycin" )){
					//Par.postNew( Date.today(), ptRec, "Erythromycin Base", 8015, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Erythromycin Base 8015" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "z-pack" ) || s.equalsIgnoreCase( "zpack" ) || s.equalsIgnoreCase( "z pack" )){
					//Par.postNew( Date.today(), ptRec, "Zithromax Z-Pak", 6040, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Zithromax Z-Pak 6040" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "darvocet" )  || s.equalsIgnoreCase( "darvocet-n" )){
					//Par.postNew( Date.today(), ptRec, "Darvocet-N 100", 1751, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Darvocet-N 100 1751" );
					continue;
				}
				
				if ( s.equalsIgnoreCase( "shell fish" )){
					//Par.postNew( Date.today(), ptRec, "Shellfish", 13184, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
					//System.out.println( s + "==>Shellfish 13184" );
					continue;
				}
				
//				if ( s.equalsIgnoreCase( "shell fish" )){
//					//Par.postNew( Date.today(), ptRec, "Shellfish", 13184, false, 0, Par.Severity.UNSPECIFIED, Par.Domain.UNSPECIFIED, null );
//					//System.out.println( s + "==>Shellfish 13184" );
//					continue;
//				}
				
				
				
				
				
				
				
				Par par = new Par();
				par.setDate( Date.today());
				par.setPtRec( ptRec );
				par.setStatus( Par.Status.CURRENT );
				par.setSeverity( Par.Severity.UNSPECIFIED );				
				par.setParDesc( s );
				
				
				
				
				Map<String,String> fnd = NCAllergies.search( s );
				
				//System.out.println( s + "==> " + fnd.size() + " found, first is: " + fnd.get( 0 ));

				boolean flgFnd = false;
				int id = 0;
				String desc = "";
				
				if ( fnd.size() >=  1 ){

					
/*					if ( fnd.size() == 1 ){
						id = EditHelpers.parseInt( fnd.get( 0 ));
						flgFnd = true;
						
					} else {
*/						
						
						for ( Map.Entry<String,String> entry: fnd.entrySet() ){
							
							if ( s.equalsIgnoreCase( entry.getKey())){
								
								desc = entry.getKey();
								id = EditHelpers.parseInt( entry.getValue());
								flgFnd = true;
								break;
							}
						}						
//					}
					
				} else {
//					par.setFlgMisc( true );
					//System.out.println( s + "==> NOT fnd" );
					
					if ( s.indexOf( '-' ) >= 1 ){
						
						String t = s.substring( 0, s.indexOf( '-' ));
						Map<String,String> fnd2 = NCAllergies.search( t );

						if ( fnd2.size() >= 1 ){
							
							for ( Map.Entry<String,String> entry: fnd2.entrySet() ){
								
								if ( t.equalsIgnoreCase( entry.getKey())){
									
									desc = entry.getKey();
									id = EditHelpers.parseInt( entry.getValue());
									flgFnd = true;
									break;
								}
							}						
						}
					}

				}
				
				if ( flgFnd ){
					// we have the ONE that we are going to post
					par.setCompositeID( id );
					par.setFlgMisc( false );
					
					par.setParDesc( desc );

					//par.setCode( code );
					//par.setDomain( domain );

					//System.out.println( s + "==> " + desc + ", " + id );
					
				} else {
					// post as Misc
					System.out.println( s + "==> posted as MISC" );
					par.setFlgMisc( true );

				}
			}
			
		}
		
		dirPt.close();
		

		return true;
	}
	
	
	
	
	
	



	
	
	static boolean withinDateRange( Date refDate, Date startDate, Date endDate ){
		return  (( startDate.compare( refDate ) <= 0 ) && ( endDate.compare( refDate ) >= 0 ));

	}
	


}
