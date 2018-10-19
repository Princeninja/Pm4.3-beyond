package usrlib;

import org.zkoss.zul.Listbox;

import palmed.DirPt;


/**
 * EditHelpers - A class of mostly static members to help with Edit pages.
 * 
 * 
 * @author Owner
 *
 */
public class EditHelpers {

	
	
	
	/**
	 * setListboxSelection() - Set listbox selection to Listitem with same String value v.
	 * 
	 * @param lb
	 * @param v
	 * @return boolean
	 */
/*	public static boolean setListboxSelection( Listbox lb, String v ){
		
		boolean found = false;
		
		lb.setSelectedIndex(0);		// default
		for ( int i = 0; i < lb.getItemCount(); ++i ){
			if ( lb.getItemAtIndex( i ).getValue().equals( v )){ 
				lb.setSelectedIndex( i );
				found = true;
			}
		}
		
		return found;
	}
*/
	public static boolean setListboxSelection( Listbox lb, Object v ){
		
		boolean found = false;
		
		lb.setSelectedIndex(0);		// default
		for ( int i = 0; i < lb.getItemCount(); ++i ){
			if ( lb.getItemAtIndex( i ).getValue().equals( v )){ 
				lb.setSelectedIndex( i );
				found = true;
			}
		}
		
		return found;
	}

/*	public static String getListboxSelection( Listbox lb ){
		
		String s;
		
		// no item selected
		if ( lb.getSelectedCount() < 1 ) return "";
		
		// get selected item's String value
		s = (String) lb.getSelectedItem().getValue();
		if ( s == null ) s = "";
		return s;
	}
*/
	public static Object getListboxSelection( Listbox lb ){
		
		Object s;
		
		// no item selected
		if ( lb.getSelectedCount() < 1 ) return null;
		
		// get selected item's String value
		s = lb.getSelectedItem().getValue();
		return s;
	}
	
	
	
	
	/**
	 * loadYNListbox() - load listbox with Yes/No values
	 * 
	 * @param Listbox
	 * @return void
	 */
	public static void loadYNListbox( Listbox lb ){
		
		lb.appendItem(" ", " ");
		lb.appendItem("Y", "Y");
		lb.appendItem("N", "N");
		return;
	}
	
	public static void loadSexListbox( Listbox lb ){
		
		lb.appendItem( " ", " " );
		lb.appendItem( "Male", "M" );
		lb.appendItem( "Female", "F" );
		return;
	}
	
	
	
	
	
	
	public static double parseDouble( String s ){
		
		double d = 0;
		
		if (( s == null ) || ( s.length() == 0 )) return 0;
		
		try {
			d = Double.parseDouble( s );
		} catch ( NumberFormatException e ){
			return 0;
		}
		
		return d;
	}
	
	
	public static int parseInt( String s ){
		
		int i = 0;
		
		if (( s == null ) || ( s.length() == 0 )) return 0;
		
		try {
			i = Integer.parseInt( s );
		} catch ( NumberFormatException e ){
			return 0;
		}
		
		return i;
	}
}
