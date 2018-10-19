package usrlib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum Validity {
	
	INVALID(1),
	VALID(2),
	HIDDEN(3);
	
	private static final Map<Integer, Validity> lookup = new HashMap<Integer,Validity>();
	
	static {
		for ( Validity s : EnumSet.allOf(Validity.class))
			lookup.put(s.getCode(), s );
	}
	
	private int code;
	
	private Validity( int code ){ this.code = code; }
	
	public int getCode() { return code; }
	
	public static Validity get( int code ){ return lookup.get( code ); }


}
