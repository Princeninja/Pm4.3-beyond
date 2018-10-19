package palmed;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MedDosage {

	private static String dbPath = "/u/med/CodifiedSig.mdb";
	private static String tblName = "tblDosageNumberType";
	
	
	
	private Map row = null;
	
	private MedDosage(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from dosage form table.
	 * 
	 */
	public static MedDosage readID( int dosageID ){
		
		MedDosage md = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		//System.out.println( "Searching for >" + medID + "<" );		
		//BigDecimal bd = new BigDecimal( dosageID );
		
		try {
			db = Database.open( new java.io.File( dbPath ));
		} catch ( IOException e ){
			System.out.println( "Exception opening CodifiedSig database." );
			return null;
		}
		
		try {
			tbl = db.getTable( tblName );
		} catch (IOException e) {
			System.out.println( "Exception getting table." );
			return null;
		}
		

		//System.out.println(tbl.getColumns());
		
		
		
		try {
			//cursor.beforeFirst();
			row = Cursor.findRow(tbl, Collections.singletonMap("DosageNumberTypeID", (Object)( new Short( (short)dosageID ))));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		md = new MedDosage();
		md.row = row;
		
		try {
			db.close();
		} catch (IOException e) {
			System.out.println( "Exception closing database." );
		}
		
		return md;
	}
	
	
	
	
	
	public String getDesc(){
		if ( row == null ) return "";
		return (String) row.get( "DosageNumberDescription" );
	}

	public static String getDesc( int dosageCode ){
		System.out.println( "dosage=" + dosageCode );
		if ( dosageCode == 0 ) return "";					// suppress display of 'Select'
		MedDosage md = MedDosage.readID( dosageCode );
		if ( md == null ) return "";
		return md.getDesc();
	}
	
	public int getOrder(){		
		if ( row == null ) return 0;
		return (Short) row.get( "DisplayOrder" );
	}
	
	public BigDecimal getMultiplier(){		
		if ( row == null ) return new BigDecimal( 0 );
		return (BigDecimal) row.get( "Multiplier" );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void fillListbox( Listbox lb ){
		
		Database db = null;
		Table tbl = null;

		// Open database file
		try {
			db = Database.open( new java.io.File( dbPath ));
		} catch ( Exception e ){
			System.out.println( "Exception opening CodifiedSig database." );
		}


		// Select table
		try {
			tbl = db.getTable( tblName );
		} catch (IOException e) {
			System.out.println( "Exception getting table." );
		}
		
		// create cursor
		Cursor cursor = Cursor.createCursor( tbl );
		cursor.beforeFirst();
		
		// Create empty list
		
		class L implements Comparable<L>{
			int order;
			int id;
			String desc;
			public int compareTo(L o) {
				return this.order - o.order;
			}				
		}
		
		ArrayList<L> list = new ArrayList<L>();
		
/*		// insert blank for top of list
		L l0 = new L();
		l0.order = 0;
		l0.id = 0;
		l0.desc = "Select Dosage";
		list.add( l0 );
*/		
		
		try {
			while ( cursor.moveToNextRow()){
				
				Map<String, Object> row = cursor.getCurrentRow();
				L l = new L();
				l.order = (Short) row.get( "DisplayOrder" );
				l.id = (Short) row.get( "DosageNumberTypeID" );
				l.desc = (String) row.get( "DosageNumberDescription" );
				list.add( l );
			}
			
		} catch (IOException e) {
			System.out.println( "Exception in while loop." );
			e.printStackTrace();
		}
		
		// Sort entries
		Collections.sort( list );
		
		
		// copy entries to listbox
		for ( int i = 0; i < list.size(); ++i ){
			L l = list.get( i );
			Listitem li = new Listitem( l.desc );
			li.setValue( (Integer) l.id );
			li.setParent( lb );
		}
						
		return;
	}
	
	
	
	
	
	
	
	public static int parseFromOldDrugName( String drugName ){
		
		int	dosageCode = 0;
		
		if ( drugName.contains( "TAB" )){
			dosageCode = parse2( drugName, "TAB" );
		} else if ( drugName.contains( "CAP" )){
			dosageCode = parse2( drugName, "CAP" );
		}
		return dosageCode;
	}
			
			
	private static int parse2( String drugName, String form ){
		
		int	dosageCode = 0;
		String s;
		String tok[] = drugName.split( "[ ]+" );
		
		for ( int i = 0; i < tok.length; ++i ){
			if ( tok[i].contains( "TAB" )){
				if ( tok[i].startsWith( "TAB" )){
					s = tok[i-1];
				} else {
					s = tok[i].substring( 0, tok[i].indexOf( "TAB" )).trim();
				}
				
				if ( ! Character.isDigit(s.charAt(0))) return 0;
				
				if ( s.equals( "1" )){
					dosageCode = 2;
				} else if ( s.equals( "2" )){
					dosageCode = 6;
				} else if ( s.equals( "3" )){
					dosageCode = 7;
				} else if ( s.equals( "4" )){
					dosageCode = 8;
				} else if ( s.equals( "5" )){
					dosageCode = 9;
				} else if ( s.equals( "6" )){
					dosageCode = 18;
				} else if ( s.equals( "1-2" )){
					dosageCode = 3;
				} else if ( s.equals( "1-3" )){
					dosageCode = 4;
				} else if ( s.equals( "2-3" )){
					dosageCode = 17;
				} else if ( s.equals( "3-4" )){
					dosageCode = 14;			// Addl Sig
				} else if ( s.equals( "2-4" )){
					dosageCode = 14;			// Addl Sig
				} else if ( s.equals( "1/2" )){
					dosageCode = 1;
				} else if ( s.equals( "1/4" )){
					dosageCode = 14;			// Addl Sig
				} else if ( s.equals( "1.5" )
						|| s.equals( "1-1/2" )){
					dosageCode = 5;				// 1.5
				} else {
					dosageCode = 0;
				}
				
				
			}
		}
		
		return dosageCode;
	}
	
	
}
