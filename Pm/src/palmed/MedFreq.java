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

public class MedFreq {

	private static String dbPath = "/u/med/CodifiedSig.mdb";
	private static String tblName = "tblDosageFrequencyType";
	
	
	
	private Map row = null;
	
	private MedFreq(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from freq table.
	 * 
	 */
	public static MedFreq readID( int freqID ){
		
		MedFreq mf = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		//System.out.println( "Searching for >" + medID + "<" );		
		//BigDecimal bd = new BigDecimal( freqID );
		
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
			row = Cursor.findRow(tbl, Collections.singletonMap("DosageFrequencyTypeID", (Object)( new Short( (short)freqID ))));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		mf = new MedFreq();
		mf.row = row;
		
		try {
			db.close();
		} catch (IOException e) {
			System.out.println( "Exception closing database." );
		}
		
		return mf;
	}
	
	
	
	
	
	public String getDesc(){
		if ( row == null ) return null;
		return (String) row.get( "DosageFrequencyDescription" );
	}

	public static String getDesc( int freq ){
		System.out.println( "searching freq=" + freq );
		MedFreq mf = MedFreq.readID( freq );
		if ( mf == null ) {		System.out.println( "freq not found" ); return "";}
		return mf.getDesc();
	}

	public int getOrder(){		
		if ( row == null ) return 0;
		return (Short) row.get( "DisplayOrder" );
	}
	
	public String getLongDesc(){
		if ( row == null ) return null;
		return (String) row.get( "LongDescription" );
	}
	
	public int getMultiplier(){		
		if ( row == null ) return 0;
		return (Short) row.get( "Multiplier" );
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
		
		
		try {
			while ( cursor.moveToNextRow()){
				
				Map<String, Object> row = cursor.getCurrentRow();
				L l = new L();
				l.order = (Short) row.get( "DisplayOrder" );
				l.id = (Short) row.get( "DosageFrequencyTypeID" );
				l.desc = (String) row.get( "DosageFrequencyDescription" );
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
	
	
	
	
	
	// Translate MedFreq from some common text
	// Note - HARDCODE using NewCrop's CodifiedSig tables
	
	public static int fromText( String freq ){
		
		freq = freq.toUpperCase();
		
		int code = 0;
		
		if ( freq.startsWith( "QD" )  			// QD
				|| freq.startsWith( "DLY" )		// daily
				|| freq.startsWith( "Q24H" )	// Q24H
				){
			
			code = 2;	// daily
			
		} else if ( freq.startsWith( "QAM" )	// QAM
			){
				
			code = 4;	// in the AM
			
		} else if ( freq.startsWith( "BID" )	// BID
			){
			
			code = 5;	// BID
			
		} else if ( freq.startsWith( "TID")){	// TID
			
			code = 6;	// TID
			
		} else if ( freq.startsWith( "QID")){	// QID
			
			code =7;	// QID
			
		} else if ( freq.startsWith( "Q2H" )	// Q2H
				){
			
			code = 13;	// Q2H while awake
			
		} else if ( freq.startsWith( "Q4H" )	// Q4H
				){
			
			code = 8;	// Q4H
			
		} else if ( freq.startsWith( "Q6H" )	// Q6H
				){
			
			code = 9;	// Q6H
			
		} else if ( freq.startsWith( "Q8H" )		// Q8H
				){
			
			code = 10;	// Q8H
			
		} else if ( freq.startsWith( "Q12H")){	// Q12H
			
			code = 11;	// Q12H
			
		} else if ( freq.startsWith( "QOD" )	// QOD
				){
			
			code = 12;	// every other day
			
		} else if ( freq.startsWith( "Q1WK" )	// Q1WK
				|| freq.startsWith( "1XWK" )
				){
			
			code = 14;	// Q1week
			
		} else if ( freq.startsWith( "Q2WK" )	// Q2WK
		){
	
			code = 15;	// Q2weeks
	
		} else if ( freq.startsWith( "Q3WK" )	// Q3WK
		){
	
			code = 16;	// Q3weeks
	
		} else if ( freq.startsWith( "QPM" )	// QPM
			){
			
			code = 18;	// nightly
			
		} else if ( freq.startsWith( "QHS" )	// QHS
				|| freq.startsWith( "HS" )		// HS
				){
			
			code = 19;	// bedtime
			
		} else if ( freq.startsWith( "3XWK" )	// 3XWK
				){
			
			code = 20;	// three times weekly
			
		} else if ( freq.startsWith( "QMON" )	// monthly
				|| freq.startsWith( "1XMO" )
				){
			
			code = 22;	// once a month
			
		} else if ( freq.startsWith( "Q2MO" )	// Q2 months
		){
// TODO
			code = 1;	// as directed 
			
		} else if ( freq.startsWith( "Q3MO" )	// Q3 months
		){
// TODO
			code = 1;	// as directed 
			
		} else if ( freq.startsWith( "Q6MO" )	// Q6 months
		){
// TODO
			code = 1;	// as directed 
			
		} else if ( freq.startsWith( "Q72H" )	// Q72H
		){
// TODO
			code = 1;	// as directed 
			
		} else if ( freq.startsWith( "AC" )		// AC
		){
// TODO
			code = 1;	// as directed 
	
		} else if ( freq.startsWith( "ACHS" )	// ACHS
		){
// TODO
			code = 1;	// as directed 
	
		} else if ( freq.startsWith( "ACAM" )	// ACAM
		){
// TODO
			code = 1;	// as directed 
	
		} else if ( freq.startsWith( "ACPM" )	// ACPM
		){
// TODO
			code = 1;	// as directed 
	
		} else if ( freq.startsWith( "QYR" )	// QYR
				|| freq.startsWith( "YEAR" ) 	// YEARly
				|| freq.startsWith( "1XYR" ) 	// 1x YEAR
				|| freq.startsWith( "Q12M" ) 	// Q12 months
				|| freq.startsWith( "ANN" ) 	// ANNually
		){
// TODO
			code = 1;	// as directed 
	
		} else {
	
			code = 1;	// as directed	
		}
		
		System.out.println( "Freq:" + freq + " is code=" + code );
		return code;
	}
	

}
