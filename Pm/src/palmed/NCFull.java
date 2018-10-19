package palmed;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listheader;

import usrlib.EditHelpers;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexBuilder;
import com.healthmarketscience.jackcess.Table;

public class NCFull {
	
	private Map row = null;
	
	private NCFull(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from med table.
	 * 
	 */
	public static NCFull readMedID( String medID ){
		
		NCFull nc = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		//System.out.println( "Searching for >" + medID + "<" );		
		BigDecimal bd = new BigDecimal( EditHelpers.parseInt( medID ));	// I use EditHelpers to avoid NumberFormatExceptions
		
		try {
			db = Database.open( new java.io.File( "/u/med/NCFull-201104.mdb" ));
		} catch ( IOException e ){
			System.out.println( "Exception opening NCFull database." );
			return null;
		}
		
		try {
			tbl = db.getTable( "tblCompositeDrug" );
		} catch (IOException e) {
			System.out.println( "Exception getting table." );
			return null;
		}
		
		System.out.println( "NCFull indexes: " + tbl.getIndexes());

		Index index = null;
		try {
			index = tbl.getIndex( "ndxMEDID" );
		} catch (Exception e) {
			System.out.println( "Exception getting index." );
			//return null;
		}
		
		
		if ( index == null ){
			System.out.println( "building NCFull MEDID index" );
			IndexBuilder ib = new IndexBuilder( "ndxMEDID" );
			ib.addColumns( "MEDID" );
			ib.setPrimaryKey();
		}
		
		
		
		
		//Cursor cursor = Cursor.createCursor( tbl );
		//cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
		//Column col1 = tbl.getColumn( "MEDID" );
		//if ( col1 == null ) System.out.println( "col1==null" );

		
		try {
			//cursor.beforeFirst();
			row = Cursor.findRow(tbl, Collections.singletonMap("MEDID", (Object)bd ));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		nc = new NCFull();
		nc.row = row;
		
		try {
			db.close();
		} catch (IOException e) {
			System.out.println( "Exception closing database." );
		}
		
		return nc;
	}
	
	public String getMedName(){
		if ( row == null ) return null;
		return (String) row.get( "MED_NAME" );
	}

	public String getMedDesc(){
		if ( row == null ) return null;
		return (String) row.get( "MED_MEDID_DESC" );
	}

	public String getMedRoute(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_ROUTE_ABBR" );
	}
	
	public String getMedRouteDesc(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_ROUTE_DESC" );
	}
	
	public String getMedRouteID(){		
		if ( row == null ) return null;
		return row.get( "MED_ROUTE_ID" ).toString();
	}
	
	public String getMedForm(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_DOSAGE_FORM_ABBR" );
	}
	
	public String getMedFormDesc(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_DOSAGE_FORM_DESC" );
	}
	
	public String getMedGenericID(){		
		if ( row == null ) return null;
		return (String) row.get( "GENERIC_MEDID" );
	}
	
	public String getMedEtc(){		
		if ( row == null ) return null;
		return (String) row.get( "etc" );
	}
	
	public String getMedStrength(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_STRENGTH" );
	}
	
	public String getMedStrengthUnits(){		
		if ( row == null ) return null;
		return (String) row.get( "MED_STRENGTH_UOM" );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void search( Listbox lb, String src ){
		
		Database db = null;
		Table tbl = null;
		
		try {
			db = Database.open( new java.io.File( "/u/med/NCFull-201104.mdb" ));
		} catch ( Exception e ){
			System.out.println( "Exception opening NCFull database." );
		}
			//System.out.println( db.getTableNames( ));

			String colName = "MED_NAME";
			
			try {
				tbl = db.getTable( "tblCompositeDrug" );
			} catch (IOException e) {
				System.out.println( "Exception getting table." );
			}
			
			Column col1 = tbl.getColumn( "MED_NAME" );
			if ( col1 == null ) System.out.println( "col1==null" );
			Column col2 = tbl.getColumn( "GenericDrugName" );
			if ( col2 == null ) System.out.println( "col2==null" );
			//System.out.println( tbl.getColumns());
			
			
			Cursor cursor = Cursor.createCursor( tbl );
			cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
			
			System.out.println( "Searching for " + src + " in column " + colName + ".");
			
			cursor.beforeFirst();
			
			//Map<String, Object> srcMap = new HashMap<String, Object>();
			//srcMap.put( "MED_NAME", src);
			//srcMap.put( "MEDID", src );
			
			try {
				while ( cursor.moveToNextRow()){
				
				   if ( cursor.currentRowMatches( col1, src ) || cursor.currentRowMatches( col2, src ) ){
					//if ( cursor.currentRowMatches( col1, src ) ){
				    
						Map<String, Object> row = cursor.getCurrentRow();
						//System.out.println( "MED=" + row.get( "GenericDrugName" ));
						//System.out.println( "MED=" + row.get( "MED_MEDID_DESC" ));
						//System.out.println( row.get( "MEDID" ));
						lb.appendItem( (String)row.get( "MED_MEDID_DESC" ), row.get( "MEDID").toString());
						//System.out.println( "MEDID=" + (String) row.get( "MEDID"));
						//System.out.println( "DESC=" + row.get( "MED_MEDID_DESC" ) + "  MEDID=" + row.get( "MEDID"));
						
						//System.out.println( row.get( "MEDID" ).getClass().getName());
				   }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println( "Exception in while loop." );
				e.printStackTrace();
			}
			
			try {
				Listheader lh =((Listheader)(lb.getFellow( "name" )));
				lh.sort(true, true);
			} catch ( Exception e){
				;	// ignore
			}
			
	}

}
