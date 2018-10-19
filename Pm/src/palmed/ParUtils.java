package palmed;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class ParUtils {

	
	
	
	public static void fillListbox( Listbox lbox, Rec ptRec ){
		
		boolean flgCurrent = false;

		//System.out.println( "ptchart - refreshing pars");
		ZkTools.listboxClear( lbox );
		
		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		// populate PARs and allergies
		for ( Reca reca = medPt.getParsReca(); Reca.isValid( reca ); ){
			
			Par par = new Par( reca );

			// is this a valid allergy?
			if ( par.getStatus() == Par.Status.CURRENT ) flgCurrent = true;

			if ( par.getStatus() == Par.Status.CURRENT ){
				Listitem i = ZkTools.appendToListbox( lbox, par.getParDesc(), reca );
				i.setTooltiptext( par.getSymptomsText( true ));
			}
			reca = par.getLLHdr().getLast();
		}

		if ( ! flgCurrent ){
			if ( medPt.getNKDAFlag()){
				ZkTools.appendToListbox( lbox, "No Known Allergies", null );
			}
		}
		
		return;
	}
	
	
	
	
	
	public static void setNKDAFlag( Rec ptRec, boolean flg ){

		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		// set flag and write record
		medPt.setNKDAFlag( flg );
		medPt.write();
		return;
	}
	
	
	

}
