package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesLab extends Notes {

	public NotesLab(){
		super( NotesLab.class );
	}
	
	public NotesLab( Reca reca ){
		super( NotesLab.class, reca );
	}
	
	
    public Reca postNew( Rec ptRec ){
		Reca reca = LLPost.post( ptRec, (NotesLab) this, 
			new LLPost.LLPost_Helper () {
				public Reca getReca( MedPt medPt ){ return medPt.getLabReca(); }
				public void setReca( MedPt medPt, Reca reca ){ medPt.setLabReca(reca); }
			}
		);
		return reca;
    }
			
    public static Reca getMedPtReca( MedPt medpt ){ return medpt.getLabReca(); }	
    public static void setMedPtReca( MedPt medpt, Reca reca ){ medpt.setLabReca( reca ); return; }
	
	public static MrLog.Types getMrLogTypes(){	return MrLog.Types.LAB_NOTE; }	
	public static Notifier.Event getNotifierEvent(){ return Notifier.Event.LAB_NOTES; }	
	public static AuditLog.Action getAuditLogActionNew(){ return AuditLog.Action.LABNOTES_ADD; }
	public static AuditLog.Action getAuditLogActionEdit(){ return AuditLog.Action.LABNOTES_EDIT; }
	public static AuditLog.Action getAuditLogActionDelete(){ return AuditLog.Action.LABNOTES_DELETE; }
	public static String getFnsub(){ return "lab"; }
	public static String getTitle(){ return "Lab Notes"; }
	
	

	public static Window createWin( Rec ptRec, Component parent ){
	
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteClass", NotesLab.class );
		return (Window) Executions.createComponents( "notes.zul", parent, myMap );
	}

}
