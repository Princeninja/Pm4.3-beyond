package usrlib;

import java.util.EnumSet;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import palmed.Orders.Status;

public class ZkTools {

	
	
	/**************************************************************************************************
	 * Functions to help with listboxes.
	 * 
	 * 
	 */
	
	
	/**
	 * listboxClear()
	 * 
	 * Remove all items from the given listbox.
	 */
	public static void listboxClear( Listbox lb ){
		
		for ( int i = lb.getItemCount(); i > 0; )
			lb.removeItemAt( --i );
	}
	
	
	
	
	public static boolean setListboxSelectionByValue( Listbox lb, Object v ){
		
		boolean found = false;
		
		//lb.setSelectedIndex(0);		// default
		lb.clearSelection();
		
		if ( v == null ) return found;
		
		for ( int i = 0; i < lb.getItemCount(); ++i ){
			
			Object w = lb.getItemAtIndex( i ).getValue();
			if ( w == null ) continue;
			
			if (( w != null ) && w.equals( v )){ 
				lb.setSelectedIndex( i );
				found = true;
			}
		}
		
		return found;
	}

	public static boolean setListboxSelectionByLabel( Listbox lb, String txt ){
		
		boolean found = false;
		
		//lb.setSelectedIndex(0);		// default
		lb.clearSelection();
		
		if ( txt == null ) return found;

		for ( int i = 0; i < lb.getItemCount(); ++i ){
			if ( lb.getItemAtIndex( i ).getLabel().equals( txt )){ 
				lb.setSelectedIndex( i );
				found = true;
			}
		}
		
		return found;
	}

	public static String getListboxSelectionLabel( Listbox lb ){
		
		// is item selected
		if ( lb.getSelectedCount() < 1 ) return null;
		
		// get selected item's String label
		return lb.getSelectedItem().getLabel();
	}
	
	public static Object getListboxSelectionValue( Listbox lb ){
		
		// is item selected
		if ( lb.getSelectedCount() < 1 ) return null;
		
		// get selected item's Object value
		Object s = lb.getSelectedItem().getValue();
		return s;
	}
	
	public static Listitem appendToListbox( Listbox lbox, String label, Object value ){
		Listitem i = new Listitem( label );
		i.setValue( value );
		i.setParent( lbox );
		return i;
	}

	
	
	
	
	
	public static int getListboxItemIndexByValue( Listbox lb, Object v ){
		
		boolean found = false;
		
		if ( v == null ) return -1;
		
		int i;
		for ( i = 0; i < lb.getItemCount(); ++i ){
			
			Object w = lb.getItemAtIndex( i ).getValue();
			if ( w == null ) continue;
			
			if (( w != null ) && w.equals( v )){ 
				found = true;
				break;
			}
		}
		
		return found ? i: -1;
	}

	
	public static void setTextboxIntIfNotZero( Textbox txtbox, int num ){
		txtbox.setValue(( num != 0 ) ? String.format( "%d", num ): "" );
		return;
	}

	


}
