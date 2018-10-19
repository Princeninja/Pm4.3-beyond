package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import usrlib.Rec;
import usrlib.Reca;

public class NotesXray extends Notes {

	public NotesXray(){
		super( NotesXray.class );
	}
	
	public NotesXray( Reca reca ){
		super( NotesXray.class, reca );
	}
	
	
    public Reca postNew( Rec ptRec ){
		Reca reca = LLPost.post( ptRec, (NotesXray) this, 
			new LLPost.LLPost_Helper () {
				public Reca getReca( MedPt medPt ){ return medPt.getXrayReca(); }
				public void setReca( MedPt medPt, Reca reca ){ medPt.setXrayReca(reca); }
			}
		);
		return reca;
    }
			
    public static Reca getMedPtReca( MedPt medpt ){ return medpt.getXrayReca(); }	
    public static void setMedPtReca( MedPt medpt, Reca reca ){ medpt.setXrayReca( reca ); return; }
	
	public static MrLog.Types getMrLogTypes(){	return MrLog.Types.XRAY_REPORT; }	
	public static Notifier.Event getNotifierEvent(){ return Notifier.Event.XRAY_NOTES; }	
	public static AuditLog.Action getAuditLogActionNew(){ return AuditLog.Action.XRAYNOTES_ADD; }
	public static AuditLog.Action getAuditLogActionEdit(){ return AuditLog.Action.XRAYNOTES_EDIT; }
	public static AuditLog.Action getAuditLogActionDelete(){ return AuditLog.Action.XRAYNOTES_DELETE; }
	public static String getFnsub(){ return "xry"; }
	public static String getTitle(){ return "Xray Notes"; }
	
	

	public static Window createWin( Rec ptRec, Component parent ){
	
		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "ptRec", (Rec)(ptRec ));
		myMap.put( "noteClass", NotesXray.class );
		return (Window) Executions.createComponents( "notes.zul", parent, myMap );
	}

}
