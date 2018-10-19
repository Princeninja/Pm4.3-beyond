package palmed;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class MedUtils {

	
	
	
	
	
	public static void fillListbox( Listbox lbox, Rec ptRec ){

		boolean flgCurrent = false;

		//System.out.println( "ptchart - refreshing meds");
		ZkTools.listboxClear( lbox );
		
		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		// populate Med list
		for ( Reca reca = medPt.getMedsReca(); Reca.isValid( reca ); ){
			
			Cmed cmed = new Cmed( reca );
			
			// is this a 'Current Medication'?
			if ( cmed.getStatus() == Cmed.Status.CURRENT ) flgCurrent = true;

			if (( cmed.getStatus() == Cmed.Status.CURRENT ) && ! cmed.getStopDate().isValid()){
				String s = cmed.getDrugName();
				if ( cmed.getDosage() > 1 ) s = s + " " + MedDosage.getDesc( cmed.getDosage());
				s = s + " " + MedFreq.getDesc( cmed.getSched());
				if ( cmed.getPRN()) s = s + " PRN";
				Listitem i = ZkTools.appendToListbox( lbox, s, reca );

				// set tooltip text
				s = cmed.getDrugCode();
				if (( s != null ) && ( s.length() > 0 )){
					NCFull nc =  NCFull.readMedID( s );
					if ( nc != null ) i.setTooltiptext( nc.getMedEtc());
				}			
			}
			reca = cmed.getLLHdr().getLast();
		}
		
		if ( ! flgCurrent ){
			if ( medPt.getNoMedsFlag()){
				ZkTools.appendToListbox( lbox, "No Current Medications", null );
			}
		}
		
		return;
	}
	
	
	
	
	public static void setNoMedsFlag( Rec ptRec, boolean flg ){

		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		// set flag and write record
		medPt.setNoMedsFlag( flg );
		medPt.write();
		return;
	}
	
	


}
