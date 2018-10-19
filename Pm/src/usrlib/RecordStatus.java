package usrlib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum RecordStatus {

		
	CURRENT( "Current", 'C' ),	
	REMOVED( "Removed", 'R' ),
	CHANGED( "Superceeded", 'E' );
	
	private String label;
	private int code;

	private static final Map<Integer, RecordStatus> lookup = new HashMap<Integer,RecordStatus>();
	
	static {
		for ( RecordStatus r : EnumSet.allOf(RecordStatus.class))
			lookup.put(r.getCode(), r );
	}


	RecordStatus ( String label, int code ){
		this.label = label;
		this.code = code & 0xff;
	}
	
	public String getLabel(){ return this.label; }
	public int getCode(){ return this.code; }
	public static RecordStatus get( int code ){ return lookup.get( code & 0xff ); }



		
}
