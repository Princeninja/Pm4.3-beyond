package palmed;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

import usrlib.EditHelpers;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexBuilder;
import com.healthmarketscience.jackcess.Table;

public class NCAllergies {

	private Map row = null;
	private static String dbName = "/u/med/NCFull-201104.mdb";
	private static String tblName = "tblCompositeAllergy";
	
	
	private NCAllergies(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from med table.
	 * 
	 */
	public static NCAllergies readMedID( String medID ){
		
		NCAllergies nc = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		int id = EditHelpers.parseInt( medID );
		
		//System.out.println( "Searching for >" + medID + "<" );		
		//BigDecimal bd = new BigDecimal( EditHelpers.parseInt( medID ));	// I use EditHelpers to avoid NumberFormatExceptions
		
		try {
			db = Database.open( new java.io.File( dbName ));
		} catch ( IOException e ){
			System.out.println( "Exception opening NCFull database." );
			return null;
		}
		
		try {
			tbl = db.getTable( tblName );
		} catch (IOException e) {
			System.out.println( "Exception getting table." );
			return null;
		}
		
/*		System.out.println( "NCFull indexes: " + tbl.getIndexes());

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
*/		
		
		
		
		//Cursor cursor = Cursor.createCursor( tbl );
		//cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
		//Column col1 = tbl.getColumn( "MEDID" );
		//if ( col1 == null ) System.out.println( "col1==null" );

		
		try {
			//cursor.beforeFirst();
			row = Cursor.findRow(tbl, Collections.singletonMap("CompositeAllergyID", (Object)id ));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		nc = new NCAllergies();
		nc.row = row;
		
		try {
			db.close();
		} catch (IOException e) {
			System.out.println( "Exception closing database." );
		}
		
		return nc;
	}
	
	public String getDesc(){
		if ( row == null ) return null;
		return (String) row.get( "Description" );
	}

	public String getStatus(){		
		if ( row == null ) return null;
		return (String) row.get( "Status" );
	}
	
	public String getConceptID(){		
		if ( row == null ) return null;
		return (String) row.get( "ConceptID" );
	}
	
	public String getConceptType(){		
		if ( row == null ) return null;
		return row.get( "ConceptType" ).toString();
	}
	
	public String getCompositeAllergyID(){		
		if ( row == null ) return null;
		return (String) row.get( "CompositeAllergyID" );
	}
	
	public String getTouchDate(){		
		if ( row == null ) return null;
		return (String) row.get( "TouchDate" );
	}
	
	public String getSource(){		
		if ( row == null ) return null;
		return (String) row.get( "Source" );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void search( Listbox lb, String src ){
		
		Database db = null;
		Table tbl = null;
		
		try {
			db = Database.open( new java.io.File( dbName ));
		} catch ( Exception e ){
			System.out.println( "Exception opening NCFull database." );
		}
			//System.out.println( db.getTableNames( ));

			String colName = "Description";
			
			try {
				tbl = db.getTable( tblName );
			} catch (IOException e) {
				System.out.println( "Exception getting table." );
			}

			//System.out.println( tbl.getColumns());

			Column col1 = tbl.getColumn( colName );
			if ( col1 == null ) System.out.println( "col1==null" );
			
			
			Cursor cursor = Cursor.createCursor( tbl );
			cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
			
			System.out.println( "Searching for " + src + " in column " + colName + ".");
			
			cursor.beforeFirst();
			
			//Map<String, Object> srcMap = new HashMap<String, Object>();
			//srcMap.put( "MED_NAME", src);
			//srcMap.put( "MEDID", src );
			
			try {
				while ( cursor.moveToNextRow()){
				
				   if ( cursor.currentRowMatches( col1, src )){
				    
						Map<String, Object> row = cursor.getCurrentRow();
						//System.out.println( "MED=" + row.get( "GenericDrugName" ));
						//System.out.println( "MED=" + row.get( "MED_MEDID_DESC" ));
						//System.out.println( row.get( "MEDID" ));
						lb.appendItem( (String)row.get( "Description" ), row.get( "CompositeAllergyID").toString());
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
			} catch ( Exception e ){
				//ignore
			}
			
	}

	
	
	public static Map<String,String> search( String src ){
		
		Map<String,String> list = new HashMap<String,String>();
		
		Database db = null;
		Table tbl = null;
		
		try {
			db = Database.open( new java.io.File( dbName ));
		} catch ( Exception e ){
			System.out.println( "Exception opening NCFull database." );
		}
			//System.out.println( db.getTableNames( ));

			String colName = "Description";
			
			try {
				tbl = db.getTable( tblName );
			} catch (IOException e) {
				System.out.println( "Exception getting table." );
			}

			//System.out.println( tbl.getColumns());

			Column col1 = tbl.getColumn( colName );
			if ( col1 == null ) System.out.println( "col1==null" );
			
			
			Cursor cursor = Cursor.createCursor( tbl );
			cursor.setColumnMatcher( new usrlib.UnanchoredSubstringColumnMatcher() );
			
			//System.out.println( "Searching for " + src + " in column " + colName + ".");
			
			cursor.beforeFirst();
			
			//Map<String, Object> srcMap = new HashMap<String, Object>();
			//srcMap.put( "MED_NAME", src);
			//srcMap.put( "MEDID", src );
			
			try {
				while ( cursor.moveToNextRow()){
				
				   if ( cursor.currentRowMatches( col1, src )){
				    
						Map<String, Object> row = cursor.getCurrentRow();
						//System.out.println( "MED=" + row.get( "GenericDrugName" ));
						//System.out.println( "MED=" + row.get( "MED_MEDID_DESC" ));
						//System.out.println( row.get( "MEDID" ));
						list.put( (String)row.get( "Description" ), row.get( "CompositeAllergyID").toString());
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
			
			return list;			
	}

}
