package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesTreatment extends Notes {

	public NotesTreatment(){
		super( NotesTreatment.class );
	}
	
	public NotesTreatment( Reca reca ){
		super( NotesTreatment.class, reca );
	}
	
	
    public Reca postNew( Rec ptRec ){
		Reca reca = LLPost.post( ptRec, (NotesTreatment) this, 
			new LLPost.LLPost_Helper () {
				public Reca getReca( MedPt medPt ){ return medPt.getTrtReca(); }
				public void setReca( MedPt medPt, Reca reca ){ medPt.setTrtReca(reca); }
			}
		);
		return reca;
    }

    
    
    public static Reca getMedPtReca( MedPt medpt ){ return medpt.getTrtReca(); }
	public static void setMedPtReca(MedPt medpt, Reca reca ){ medpt.setTrtReca( reca ); return; }
	
	public static MrLog.Types getMrLogTypes(){	return MrLog.Types.TREATMENT_NOTE; }	
	public static Notifier.Event getNotifierEvent(){ return Notifier.Event.TREATMENT_NOTES; }	
	public static AuditLog.Action getAuditLogActionNew(){ return AuditLog.Action.TREATMENT_ADD; }
	public static AuditLog.Action getAuditLogActionEdit(){ return AuditLog.Action.TREATMENT_EDIT; }
	public static AuditLog.Action getAuditLogActionDelete(){ return AuditLog.Action.TREATMENT_DELETE; }
	public static String getFnsub(){ return "trt"; }
	public static String getTitle(){ return "Treatment Notes"; }

	
	
	public static Window createWin( Rec ptRec, Component parent ){
	
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteClass", NotesTreatment.class );
		return (Window) Executions.createComponents( "notes.zul", parent, myMap );
	}

}
