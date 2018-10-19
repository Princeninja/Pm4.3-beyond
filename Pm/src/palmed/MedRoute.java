package palmed;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import usrlib.EditHelpers;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MedRoute {

	private static String dbPath = "/u/med/CodifiedSig.mdb";
	private static String tblName = "tblDosageRouteType";
	
	
	
	private Map row = null;
	
	private MedRoute(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from route table.
	 * 
	 */
	public static MedRoute readID( int routeID ){
		
		MedRoute mr = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		//System.out.println( "Searching for >" + medID + "<" );		
		//BigDecimal bd = new BigDecimal( routeID );
		
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
		
		
		
		//Cursor cursor = Cursor.createCursor( tbl );
		//cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
		//Column col1 = tbl.getColumn( "MEDID" );
		//if ( col1 == null ) System.out.println( "col1==null" );

		
		try {
			//cursor.beforeFirst();
			row = Cursor.findRow(tbl, Collections.singletonMap("DosageRouteTypeId", (Object)( new Byte( (byte) routeID ))));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		mr = new MedRoute();
		mr.row = row;
		
		try {
			db.close();
		} catch (IOException e) {
			System.out.println( "Exception closing database." );
		}
		
		return mr;
	}
	
	
	
	
	
	public String getDesc(){
		if ( row == null ) return null;
		return (String) row.get( "DosageRouteDescription" );
	}

	public static String getDesc( int route ){
		System.out.println( "route=" + route );
		MedRoute mr = MedRoute.readID( route );
		if ( mr == null ) return "";
		return mr.getDesc();
	}

	
	
	public String getDisplay(){
		if ( row == null ) return null;
		return (String) row.get( "DosageRouteDisplay" );
	}

	public int getOrder(){		
		if ( row == null ) return 0;
		return (Integer) row.get( "DisplayOrder" );
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
		
		// insert blank for top of list
		L l0 = new L();
		l0.order = 0;
		l0.id = 0;
		l0.desc = "Select Route";
		list.add( l0 );
		
		
		try {
			while ( cursor.moveToNextRow()){
				
				Map<String, Object> row = cursor.getCurrentRow();
				L l = new L();
				l.order = (Integer) row.get( "DisplayOrder" );
				l.id = (Byte) row.get( "DosageRouteTypeId" );
				l.desc = (String) row.get( "DosageRouteDisplay" );
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
	
	
	
	
	
	
	// Translate MedRoute from NCFull table
	// Note - HARDCODE using NewCrop's CodifiedSig tables
	
	public static int fromNCFull( String route ){
		
		int code = EditHelpers.parseInt( route );
		
		switch ( code ){
		
		case 24:		// oral
			
			code = 2;			// by mouth
			break;
			
		case 22:		// nasal
			
			code = 6;			// into the nose
			break;
			
		case 23:		// ophthalmic
			
			code = 5;			// into both eyes
			break;
			
		case 25:		// otic
			
			code = 4;			// into both ears
			break;
			
		case 1:			// buccal
			
			code = 30;			// under the lip
			break;
			
		case 26:			// perfusion
			
			code = 28;			// perfusion
			break;
			
		case 27:			// rectal
			
			code = 7;			// in the rectum
			break;
			
		case 29:			// sublingual
			
			code = 31;			// under the tongue
			break;
			
		case 30:			// subcutaneous
			
			code = 13;			// inject below the skin
			break;
			
		case 28:			// transdermal
		case 5:				// topical
			
			code = 25;			// apply to the skin
			break;
			
		case 32:			// urethral
			
			code = 8;			// in the urethra
			break;
			
		case 33:			// vaginal
			
			code = 9;			// into the vagina
			break;
			
		case 3:			// dental
			
			code = 26;			// apply to the teeth
			break;
			
		case 4:			// epidural
			
			code = 3;			// epidural
			break;
			
		case 20:		// intraarticular
			
			code = 17;			// intraarticular
			break;
			
		case 7:			// intracavernosal
			
			code = 14;			// inject into the penis
			break;
			
		case 8:			// intradermal
			
			code = 15;			// inject into the skin
			break;
			
		case 11:		// intramuscular
			
			code = 12;			// intramuscular inject
			break;
			
		case 12:		// inhalation
			
			code = 11;			// inhale
			break;
			
		case 9:			// injection
			
			code = 0;			// unknown??
			break;
			
		case 36:		// intrapleural
			
			code = 20;			// intrapleural
			break;
			
		case 34:		// in vitro
			
			code = 10;			// in vitro
			break;
			
		case 13:		// intraocular
			
			code = 18;			// intraocular
			break;
			
		case 14:		// intraperitoneal
			
			code = 19;			// intraperitoneal
			break;
			
		case 17:		// intrathecal
			
			code = 21;			// intrathecal
			break;
			
		case 18:		// intrauterine
			
			code = 22;			// intrauterine
			break;
			
		case 19:		// intravenous
			
			code = 23;			// intravenous
			break;
			
		case 16:		// intravesical
			
			code = 24;			// intravesical
			break;
			
		case 2:			// combination
		case 143:		// hemodialysis
		case 142:		// intratracheal
		case 15:		// irrigation
		case 149:		// lash
		case 150:		// local infiltration
		case 35:		// misc (non-drug)
		case 21:		// mucous membrane
		case 147:		// percutaneous
		case 146:		// periodontal
		case 145:		// subdermal
		default:
			
			code = 0;			// Addl sig
			break;	
		}
		
		return code;
	}
	
	// Translate MedForm from the old MediSpan route list
	// Note - HARDCODE using NewCrop's CodifiedSig tables
	
	public static int fromMediSpan( String route ){
		
		int code = 0;
		
		if ( route.startsWith( "OR" )  			// oral
				|| route.startsWith( "PO" )		// per os
				|| route.startsWith( "MT" ) 	// mouth / throat
				){
			
			code = 2;	// by mouth
			
		} else if ( route.startsWith( "IJ" )	// injection
			){
				
			code = 0;	// unknown
			
		} else if ( route.startsWith( "IM" )	// intramuscular
			){
			
			code = 12;	// intramuscular injection
			
		} else if ( route.startsWith( "BU")){	// buccal
			
			code = 30;	// under the lip
			
		} else if ( route.startsWith( "EX" )	// external
				){
			
			code = 1;	// as directed
			
		} else if ( route.startsWith( "IN" )	// inhalation
				){
			
			code = 11;	// inhaled
			
		} else if ( route.startsWith( "IR" )	// irrigation
				){
			
			code = 1;	// as directed
			
		} else if ( route.startsWith("IT" )		// intrathecal
				){
			
			code = 21;	// intrathecal
			
		} else if ( route.startsWith( "IV" )	// intravenous
				){
			
			code = 23;	// intravenous
			
		} else if ( route.startsWith( "NA" )	// nasal
				){
			
			code = 6;	// in the nose
			
		} else if ( route.startsWith( "OP" )	// ophthalmic
			){
			
			code = 5;	// in both eyes
			
		} else if ( route.startsWith( "IU" )	// intrauterine
				){
			
			code = 22;	// intrauterine
			
		} else if ( route.startsWith( "OT" )	// otic
				){
			
			code = 4;	// in both ears
			
		} else if ( route.startsWith( "RE" )	// rectal
				){
			
			code = 7;	// in the rectum
			
		} else if ( route.startsWith( "SC" )	// subcutaneous
				|| route.startsWith( "SQ" )	// subcutaneous
		){
	
			code = 13;	// inject below the skin
	
		} else if ( route.startsWith( "SL" )	// sublingual
		){
	
			code = 31;	// under the tongue
	
		} else if ( route.startsWith( "TD" )	// transdermal
		){
	
			code = 25;	// apply on the skin
	
		} else if ( route.startsWith( "UR" )	// urethral
		){
	
			code = 8;	//in the urethra
	
		} else if ( route.startsWith( "VA" )	// vaginal
		){
	
			code = 9;	// in the vagina
	
		} else if ( route.startsWith( "CO" )	// combination
				|| route.startsWith( "??" )		// unknown
				|| route.startsWith( "XX" )		// does not apply
		){

			code = 1;	// as directed
	
		}
		
		return code;
	}
	

	
	// for a given NewCrop Codified Sig route code - return the equivalent MediSpan route
	public static String toMediSpan( int route ){
		
		String[] tbl = {
				"??",	"??",	"OR",	"",		"OT",	"OP",	"NA",	"RE",
				"UR",	"VA",	"??",	"IN",	"IM",	"SC",	"??",	"??",
				"??",	"??",	"??",	"??",	"??",	"IT",	"IU",	"IV",
				"??",	"TD",	"??",	"??",	"??",	"??",	"BU",	"SL",
				"OT",	"OT",	"OP",	"OP",	"TD",	"TD",	"??",	"TD",
				"??"
				};
		
		if (( route < 0 ) || ( route > tbl.length )) route = 0;
		return tbl[route];
	}
}
