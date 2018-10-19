package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesNursing extends Notes {

	public NotesNursing(){
		super( NotesNursing.class );
	}
	
	public NotesNursing( Reca reca ){
		super( NotesNursing.class, reca );
	}
	
	
    public Reca postNew( Rec ptRec ){
		Reca reca = LLPost.post( ptRec, (NotesNursing) this, 
			new LLPost.LLPost_Helper () {
				public Reca getReca( MedPt medPt ){ return medPt.getNurseReca(); }
				public void setReca( MedPt medPt, Reca reca ){ medPt.setNurseReca(reca); }
			}
		);
		return reca;
    }
			
    public static Reca getMedPtReca( MedPt medpt ){ return medpt.getNurseReca(); }	
    public static void setMedPtReca( MedPt medpt, Reca reca ){ medpt.setNurseReca( reca ); return; }
	
	public static MrLog.Types getMrLogTypes(){	return MrLog.Types.NURSING_NOTE; }	
	public static Notifier.Event getNotifierEvent(){ return Notifier.Event.NURSING_NOTES; }	
	public static AuditLog.Action getAuditLogActionNew(){ return AuditLog.Action.NURSING_ADD; }
	public static AuditLog.Action getAuditLogActionEdit(){ return AuditLog.Action.NURSING_EDIT; }
	public static AuditLog.Action getAuditLogActionDelete(){ return AuditLog.Action.NURSING_DELETE; }
	public static String getFnsub(){ return "nur"; }
	public static String getTitle(){ return "Nursing Notes"; }
	
	

	public static Window createWin( Rec ptRec, Component parent ){
	
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteClass", NotesNursing.class );
		return (Window) Executions.createComponents( "notes.zul", parent, myMap );
	}
	
}
