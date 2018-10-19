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

public class MedForm {

	private static String dbPath = "/u/med/CodifiedSig.mdb";
	private static String tblName = "tblDosageFormType";
	
	
	
	private Map row = null;
	
	private MedForm(){
		
	}
	

	
	
	
	
	
	/********************************************************************************
	 * 
	 * Read a row from dosage form table.
	 * 
	 */
	public static MedForm readID( int formID ){
		
		MedForm mf = null;
		Database db = null;
		Table tbl = null;
		Map<String, Object> row = null;
		
		//System.out.println( "Searching for >" + medID + "<" );		
		//BigDecimal bd = new BigDecimal( formID );
		
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
			row = Cursor.findRow(tbl, Collections.singletonMap("DosageFormTypeId", (Object)( new Short( (short)formID ))));
			//if ( cursor.findRow( Collections.singletonMap("MEDID", (Object)bd ))){
			//	row = cursor.getCurrentRow();
			//}
		} catch (IOException e) {
			System.out.println( "Exception reading row." );
			return null;
		}
		
		if ( row == null ) return null;
		
		mf = new MedForm();
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
		return (String) row.get( "DosageFormDescription" );
	}

	public static String getDesc( int formID ){
		System.out.println( "searching formID=" + formID );
		MedForm mf = MedForm.readID( formID );
		if ( mf == null ) {		System.out.println( "form not found" ); return "";}
		return mf.getDesc();
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
		l0.desc = "Select Form";
		list.add( l0 );
		
		
		try {
			while ( cursor.moveToNextRow()){
				
				Map<String, Object> row = cursor.getCurrentRow();
				L l = new L();
				l.order =  0xff & (Byte) row.get( "DisplayOrder" );
				l.id = (Short) row.get( "DosageFormTypeId" );
				l.desc = (String) row.get( "DosageFormDescription" );
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
	
	
	
	
	// Translate MedForm from NCFull table
	// Note - HARDCODE using NewCrop's CodifiedSig tables
	
	public static int fromNCFull( String form ){
		
		int code = 0;
		
		if ( form.startsWith( "Tab" )  
				|| form.startsWith( "TM12" ) || form.startsWith( "TM24" ) 
				|| form.startsWith( "TO12" ) || form.startsWith( "TO24" ) 
				|| form.startsWith( "TG24" )
				|| form.startsWith( "TbTQ" )		// tablet, ER Particles/Crystals
				|| form.startsWith( "TbSQ" )		// tablet, Sequential
				|| form.startsWith( "TbSO" )		// tablet, Soluble
				|| form.startsWith( "TbPt" )		// tablet, ER Particles/Crystals
				|| form.startsWith( "TbOm" )		// tablet, ER Osmotic Push
				|| form.startsWith( "TbMP" )		// tablet, ER Multiphase
				|| form.startsWith( "TbLD" )		// tablet, rapid dissolve
				|| form.startsWith( "TbER" )		// tablet, ER (extended release)
				|| form.startsWith( "TbEF" )		// tablet, effervescent
				|| form.startsWith( "TbEC" )		// tablet, delayed release (E.C.)
				|| form.startsWith( "TbDP" )		// tablet, dispersible
				|| form.startsWith( "TbDL" )		// tablet, rapid dissolve
				|| form.startsWith( "Tb8H" )		// tablet, extended release 8 hour
				|| form.startsWith( "Tb24" )		// tablet, ER 24 hour
				|| form.startsWith( "Tb12" )		// tablet, ER 12 hour
				|| form.startsWith( "T12S" )		// tablet, ER 12 hour sequential
				|| form.startsWith( "Subl" )		// tablet, sublingual
				|| form.startsWith( "Chew" )		// tablet, chewable
				|| form.startsWith( "3MPk" )		// tablets, 3 month pack
				){
			
			code = 12;	// tablet
			
		} else if ( form.startsWith( "Cap" )
			|| form.startsWith( "MP30" )		// capsule, ER multiphase 30-70
			|| form.startsWith( "MP50" )		// capsule, ER multiphase 50-50
			|| form.startsWith( "Cs24" )		// capsule, ER 24 hr
			|| form.startsWith( "CpSQ" )		// capsule, sequential
			|| form.startsWith( "CpSP" )		// capsule, sprinkle
			|| form.startsWith( "CpMR" )		// capsule, ER multiphase
			|| form.startsWith( "CpER" )		// capsule, ER
			|| form.startsWith( "CpEP" )		// capsule, delayed release particles
			|| form.startsWith( "CpDR" )		// capsule, delayed release
			|| form.startsWith( "CpDM" )		// capsule, delayed release, multiphasic
			|| form.startsWith( "CpCT" )		// capsule, 24 hr ER pellet
			|| form.startsWith( "Cp24" )		// capsule, ER 24 hr
			|| form.startsWith( "Cp12" )		// capsule, ER 12 hr
			|| form.startsWith( "CM24" )		// capsule, ER multiphase 24 hr
			|| form.startsWith( "CM12" )		// capsule, ER multiphase 12 hr
			|| form.startsWith( "CERP" )		// capsule, ER pellets
			|| form.startsWith( "CDER" )		// capsule, ER degradable
			|| form.startsWith( "C24P" )		// capsule, ER pellets 24 hr
			|| form.startsWith( "C12P" )		// capsule, ER pellets 12 hr
			){
				
			code = 2;	// capsule
			
		} else if ( form.startsWith( "Drop" )	// drops
			|| form.startsWith( "DrpS" )		// drops, suspension
			|| form.startsWith( "DrpV" )		// drops, viscous
			|| form.startsWith( "DrpD" )		// drops, daily
			){
			
			code = 3;	// drop
			
		} else if ( form.startsWith( "TdSy")){
			
			code = 7;	// patch
		} else if ( form.startsWith( "SyrR" )		// syringe, reusable
				|| form.startsWith( "Syrg" )		// syringe
				){
			
			code = 15;	// syringe
			
		} else if ( form.startsWith( "Syrp" )		// syrup
				|| form.startsWith( "SusR" )		// suspension for reconstitution
				|| form.startsWith( "Susp" )		// suspension
				|| form.startsWith( "SuDR" )		// suspension, delayed release for recon
				|| form.startsWith( "SuMC" )		// suspension, microcapsule recon
				|| form.startsWith( "Su24" )		// suspension, ER 24 hr
				|| form.startsWith( "Su12" )		// suspension, ER 12 hr
				|| form.startsWith( "SoIR" )		// recon soln
				|| form.startsWith( "SoIP" )		// parenteral solution
				|| form.startsWith( "Soln" )		// solution
				|| form.startsWith( "SERR" )		// suspension, ER recon
				|| form.startsWith( "Liqd" )		// liquid
				|| form.startsWith( "Inj" )			// injectable
				|| form.startsWith( "Emul" )		// emulsion
				|| form.startsWith( "Elix" )		// elixir
				|| form.startsWith( "Conc" )		// concentrate
				){
			code = 6;	// ml
			
		} else if ( form.startsWith( "Supp" )		// suppository
				){
			
			code = 11;	// suppository
			
		} else if ( form.startsWith("Spry" )		// spray, non-aerosol
				|| form.startsWith( "SprA" )		// spray, aerosol
				|| form.startsWith( "Inha" )		// inhaler
				|| form.startsWith( "HFAA" )		// HFA aerosol inhaler
				|| form.startsWith( "AerP" )		// aerosol powder
				|| form.startsWith( "Aero" )		// aerosol
				|| form.startsWith( "AerB" )		// aerosol, breath activated
				|| form.startsWith( "AePB" )		// aerosol, powder breath activated
				){
			
			code = 9;	// puff
			
		} else if ( form.startsWith( "Soap" )		// soap
				|| form.startsWith( "ShCr" )		// shaving cream
				|| form.startsWith( "Sham" )		// shampoo
				|| form.startsWith( "Ring" )		// ring
				|| form.startsWith( "Lubr" )		// lubricant
				|| form.startsWith( "Lotn" )		// lotion
				|| form.startsWith( "LoER" )		// lotion, ER
				|| form.startsWith( "Lnmt" )		// liniment
				|| form.startsWith( "Gel" )		// gel
				|| form.startsWith( "Foam" )		// foam
				|| form.startsWith( "Clsr" )		// cleanser
				){
			
			code = 1;	// application
			
		} else if ( form.startsWith( "PwPk" )		// powder in packet
				|| form.startsWith( "PwEP" )		// powder effervescent in packet
				|| form.startsWith( "Pste" )		// paste
				|| form.startsWith( "Pack" )		// packet
				|| form.startsWith( "LiPk" )		// liquid in a packet
				|| form.startsWith( "GrPk" )		// granules in packet
				|| form.startsWith( "GlPk" )		// gel in packet
				|| form.startsWith( "Crea" )		// cream
				){
			
			code = 16;	// package
			
		} else if ( form.startsWith( "PTWK" )		// patch weekly
				|| form.startsWith( "PTSW" )		// patch semiweekly
				|| form.startsWith( "PtPc" )		// patch, transdermal PCA
				|| form.startsWith( "PtMH" )		// patch, medicated, self-heating
				|| form.startsWith( "PtMd" )		// adhesive patch, medicated
				|| form.startsWith( "PtIo" )		// patch, iontophoretic
				|| form.startsWith( "PTDS" )		// patch, daily sequential
				|| form.startsWith( "Ptch" )		// patch
				|| form.startsWith( "PT72" )		// patch 72 hr
				|| form.startsWith( "PT24" )		// patch 24 hr
			){
			
			code = 7;	// patch
			
		} else if ( form.startsWith( "Lozg" )		// lozenge
				|| form.startsWith( "LozH" )		// lozenge on a handle
				|| form.startsWith( "LozX" )		// lozenge, ER
				){
			
			code = 5;	// lozenge
			
		} else if ( form.startsWith( "AeMS" )		// aerosol, metered spray
				){
			
			code = 10;	// squirt
			
		} else if ( form.startsWith( "Powd" )		// powder
				|| form.startsWith( "Oint" )		// ointment
				|| form.startsWith( "Mist" )		// mist
				|| form.startsWith( "Misc" )		// misc
				|| form.startsWith( "Kit" )			// kit
				|| form.startsWith( "InPn" )		// insulin pen
				|| form.startsWith( "Enem" )		// enema
				|| form.startsWith( "DsPk" )		// tablets, dose pack
				|| form.startsWith( "Devi" )		// device
				|| form.startsWith( "Cmpk" )		// combo pack
				|| form.startsWith( "Bndg" )		// bandage
				|| form.startsWith( "AerS" )		// aerosol solution
				){
			
			code = 0;	// Additional Sig
		}
		
		return code;
	}
	
	
}
