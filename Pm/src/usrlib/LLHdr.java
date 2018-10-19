package usrlib;

public class LLHdr {
	
	private Reca nextReca;			// next Reca in list
	private Reca lastReca;			// last Reca in list
	
	
	public LLHdr(){
		this.nextReca.setNull();
		this.lastReca.setNull();
	}
	
	public LLHdr( Reca next, Reca last ){
		this.nextReca = next;
		this.lastReca = last;
	}
	
	public LLHdr( byte[] data ){
		LLHdr.fromLLHdr( data, 0 );
	}
	
	public LLHdr( byte[] data, int offset ){
		LLHdr.fromLLHdr( data, offset );
	}

	
	public static LLHdr fromLLHdr( byte[] data, int offset ){
		return new LLHdr( Reca.fromReca( data, offset+0 ), Reca.fromReca( data, offset+4 ));
	}
	
	public void toLLHdr( byte[] data, int offset ){
		this.nextReca.toReca( data, offset+0 );
		this.lastReca.toReca( data, offset+4 );
		return;
	}
	

	public Reca getNext(){ return this.nextReca; }
	
	public Reca getLast(){ return this.lastReca; }
	
	public void setNext( Reca reca ){ this.nextReca = reca; }
	
	public void setLast( Reca reca ){ this.lastReca = reca; }
	

}
