package palmed;


import usrlib.Address;
import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;

public class ImmExport {
	
	
	
	public static String exportOne( Rec ptRec, Reca immReca ){
		
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ImmExport() bad ptRec" );
		if ( ! Reca.isValid( immReca )) SystemHelpers.seriousError( "ImmExport() bad immReca" );

		StringBuilder sb = new StringBuilder( 4096 );

		Date date = Date.today();
		Time time = Time.now();
		String controlID = date.getPrintable(8) + time.getPrintable(1) + "PT" + ptRec.toString();

		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		
		sb.append( HL7Util.createMSH( date, time, controlID ));
		sb.append( HL7Util.createPID( ptRec, dirPt.getName(), dirPt.getBirthdate(), dirPt.getSex(), dirPt.getRace(), dirPt.getEthnicity(), dirPt.getAddress()));
		sb.append( HL7Util.createPD1( date ));
		//HL7Util.createNK1();
		//HL7Util.createPV1();

		makeORC( sb, ptRec, immReca, null );

		return sb.toString();
	}
	
	
	
	
	

		public static String exportAll( Rec ptRec ){
			
			if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "ImmExport() bad ptRec" );
			
			StringBuilder sb = new StringBuilder( 4096 );

			Date date = Date.today();
			Time time = Time.now();
			String controlID = date.getPrintable(8) + time.getPrintable(1) + "PT" + ptRec.toString();

			DirPt dirPt = new DirPt( ptRec );
			MedPt medPt = new MedPt( dirPt.getMedRec());
			
			
			sb.append( HL7Util.createMSH( date, time, controlID ));
			sb.append( HL7Util.createPID( ptRec, dirPt.getName(), dirPt.getBirthdate(), dirPt.getSex(), dirPt.getRace(), dirPt.getEthnicity(), dirPt.getAddress()));
			sb.append( HL7Util.createPD1( date ));
			//HL7Util.createNK1();
			//HL7Util.createPV1();
			
			Reca immReca = medPt.getImmuneReca();
			
			while ( Reca.isValid( immReca )){
				
				Immunizations imm = new Immunizations( immReca );
				makeORC( sb, ptRec, immReca, imm );
				
				// get next in list
				immReca = imm.getLLHdr().getLast();
			}

			return sb.toString();
		}
		
		
		
		public static void makeORC( StringBuilder sb, Rec ptRec, Reca immReca, Immunizations imm ){

			if ( imm == null ) imm = new Immunizations( immReca );

			Rec provRec = imm.getProvRec();
			if ( ! Rec.isValid( provRec )) provRec = new Rec( 2 );		//TODO - temp
			Prov prov = new Prov( provRec );
			Name provName = new Name( prov.getLastName(), prov.getFirstName(), prov.getMiddleName(), prov.getSuffix());
			
			String mvx = imm.getMVX();
			String cvx = imm.getCVX();

			sb.append( HL7Util.createORC( ptRec, immReca  ));
			sb.append( HL7Util.createRXA( imm.getDate(), imm.getTime(), ptRec, cvx, imm.getDesc(), mvx, VaccineMVX.getName( mvx ), 
					imm.getAmount(), imm.getUnits(), imm.getInfoSource(), imm.getLotNumber(), imm.getExpDate(), 
					provName, prov.getNPI(), prov.getSuffix(), prov.getOfficeAddress()));

			//HL7Util.createRXR();
			//HL7Util.createOBX();
			//HL7Util.createNTE();
			
		}

}
