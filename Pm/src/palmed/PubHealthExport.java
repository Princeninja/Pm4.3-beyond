package palmed;

import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.Time;

public class PubHealthExport {

	public static String exportProb( Rec ptRec, Reca probReca ){
		
		if ( ! Rec.isValid( ptRec )) SystemHelpers.seriousError( "PubHealthExport() bad ptRec" );
		if ( ! Reca.isValid( probReca )) SystemHelpers.seriousError( "PubHealthExport() bad probReca" );

		StringBuilder sb = new StringBuilder( 4096 );

		Date date = Date.today();
		Time time = Time.now();
		String controlID = date.getPrintable(8) + time.getPrintable(1) + "PT" + ptRec.toString();
		
		String facilityName = "RFP";
		String facilityNPI = "npi";

		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		sb.append( HL7UtilPH.createMSH( date, time, controlID, facilityNPI ));
		sb.append( HL7UtilPH.createEVN( date, time, facilityName, facilityNPI ));
		sb.append( HL7UtilPH.createPID( ptRec, dirPt.getName(), dirPt.getBirthdate(), dirPt.getSex(), dirPt.getRace(), dirPt.getEthnicity(), dirPt.getAddress()));

		sb.append( HL7UtilPH.createPV1( date, time, ptRec, "O" ));
		sb.append( HL7UtilPH.createPV2( date, time, ptRec ));
		
		//sb.append( HL7UtilPH.createOBX( 1, ptRec, date, time ));

		String code = "";
		String desc = "";
		
		Prob prob = new Prob( probReca );		
		Rec pRec = prob.getProbTblRec();
		if ( Rec.isValid( pRec )){
			ProbTbl p = new ProbTbl( pRec );
			code = p.getSNOMED();
			desc = p.getDesc();
		} else {
			desc = prob.getProbDesc();
			code = "";
		}
		sb.append( HL7UtilPH.createDG1( 1, ptRec, prob.getStartDate(), Time.setTime( 0, 0 ), code, desc, "SCT" )); // SNOMED code set

		//PR1
		//IN1
		
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
