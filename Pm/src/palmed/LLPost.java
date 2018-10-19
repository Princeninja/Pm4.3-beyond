package palmed;

import org.zkoss.zul.XYModel;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;

public class LLPost {
	
	// method called to put vitals data into the chart
	interface LLPost_Helper {
		Reca getReca( MedPt medPt );
		void setReca( MedPt medPt, Reca reca );
		
	}	

	
	public static Reca post( Rec ptRec, LLItem item, LLPost_Helper llHelper ){
	
		Reca newReca = null;
		
		try {
	
			//TODO - lock file, assure atomic action, etc
			SystemHelpers.setLockStub();
	
			// read MedPt
			DirPt dirPt = new DirPt( ptRec );
			MedPt medPt = new MedPt( dirPt.getMedRec());
			
			// get old record pointer from medPt
			Reca oldReca = llHelper.getReca( medPt );
			
			// set LLHdr (linked-list header) with backward pointer and null next pointer in new vitals record
			item.setLLHdr( new LLHdr( new Reca().setNull(), oldReca ));
	
	
			// write new item record
			newReca = item.writeNew();
			System.out.println( "new item (" + item.getClass().getName() + ") created itemReca=" + newReca.toString());
			
			// set new item rec in MedPt and write MedPt
			llHelper.setReca( medPt, newReca );
			medPt.write();
			

			// set forward pointer in old item
			if ( Reca.isValid( oldReca )){
				LLItem oldItem = null;
				try { oldItem = item.getClass().newInstance();
				} catch (Exception e) { SystemHelpers.seriousError( e.getMessage()); }

				if ( oldItem != null ){
					oldItem.read( oldReca );
					LLHdr llhdr = oldItem.getLLHdr();
					llhdr.setNext( newReca );
					oldItem.setLLHdr( llhdr );
					oldItem.write( oldReca );
				}
			}
			
		} finally {
			
			//TODO - did the above execute properly??
			SystemHelpers.releaseLockStub();
		}

		return newReca;
	}

}
