package palmed;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class ProbUtils {

	
	
	public static void fillListbox( Listbox lbox, Rec ptRec ){
		
		boolean flgCurrent = false;

		//System.out.println( "ptchart - refreshing probs");
		ZkTools.listboxClear( lbox );
		
		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		
		// populate Problem list
		for ( Reca reca = medPt.getProbReca(); Reca.isValid( reca ); ){
			
			Prob prob = new Prob( reca );

			// is this a 'Current Problem'?
			if ( prob.getStatus() == 'C' /*Cmed.Status.CURRENT*/ ) flgCurrent = true;


			if ( prob.getStatus() == 'C' ){		// only active 'Current' problems
				Listitem i = ZkTools.appendToListbox( lbox, prob.getProbDesc(), reca );
				//i.setTooltiptext( par.getSymptomsText( true ));
			}
			reca = prob.getLLHdr().getLast();
		}
		
		if ( ! flgCurrent ){
			if ( medPt.getNoProbsFlag()){
				ZkTools.appendToListbox( lbox, "No Current Problems", null );
			}
		}
		
		return;
	}
	

	
	public static void setNoProbsFlag( Rec ptRec, boolean flg ){

		// get patient's medical record
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());
		// set flag and write record
		medPt.setNoProbsFlag( flg );
		medPt.write();
		return;
	}
	
	

}
