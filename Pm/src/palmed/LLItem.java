package palmed;

import usrlib.LLHdr;
import usrlib.Rec;
import usrlib.Reca;

public interface LLItem {
	
	public LLHdr getLLHdr();
	public void setLLHdr( LLHdr ll );
	public void read( Reca reca );
	public boolean write( Reca reca );
	public Reca writeNew();
	public Reca postNew( Rec ptRec );

}
