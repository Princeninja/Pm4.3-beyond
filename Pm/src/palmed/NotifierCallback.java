package palmed;

import usrlib.Rec;

public interface NotifierCallback {
	
	public boolean callback( Rec ptRec, Notifier.Event event );
	

}
