package palmed;

import java.util.Vector;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.StringHelpers;
import usrlib.DialogHelpers;
import usrlib.Rec;

public class LkupController extends GenericForwardComposer{

	//private Window lkupwin;
	private Textbox Code; //dgn code
	private Textbox Code2; //srv code
	private Textbox Code3; //Insurance code
	private Textbox Code4; //Pharmacy code
	private Listbox Codebox;  //dgn
	private Listbox Codebox2; //srv 
	private Listbox Codebox3;  //Insurance
	private Listbox Codebox4;  //Pharmacy
	private Tabbox	tabbox;
	//private Groupbox multFndGroupbox;
	
	public LkupController() {
		// TODO Auto-generated constructor stub
		
	}

	public LkupController(char separator) {
		super(separator);
		// TODO Auto-generated constructor stub
	}

	public LkupController(char separator, boolean ignoreZScript,
			boolean ignoreXel) {
		super(separator, ignoreZScript, ignoreXel);
		// TODO Auto-generated constructor stub
	}

	
	public void doAfterCompose (Component comp){
		try {
			super.doAfterCompose(comp);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Code.setFocus(true);
		
		
		
		return;
	}
	
	/**public void onClick$exitBtn (Event event){
		
		lkupwin.detach();
	}
	*/
	
	
	/**
	 * Method to add strings based on their existence
	 */
	static void join (StringBuilder SB, String str){
		if ((str != null) && (!(str.length() == 0))){
			if (SB.length() > 0 ){
				SB.append('~');
			}
			SB.append(str);
		}
	}
	
	/**
	 * For Diagnosis Codes
	 */
	
	public void refreshList(){ onClick$searchBtn( null ); }
	
	public void refreshList( String searchString ){
	
		if ( Codebox == null ) return;
		
		// remove all items
		for ( int i = Codebox.getItemCount(); i > 0; --i ){
			Codebox.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s = searchString;
		if ( s != null ) s = s.toUpperCase();
		
		
		// populate list

		Dgn dgn = Dgn.open();
		
		while ( dgn.getNext()){
			
			int fnd = 1;
			
			// get dgn code  validity byte
			Dgn.Status status = dgn.isValid() ? Dgn.Status.ACTIVE: Dgn.Status.INACTIVE;
			// is this type selected?
			if (( status == Dgn.Status.ACTIVE ) /*|| ( status == Dgn.Status.INACTIVE )*/){
			
				if (( searchString != null )
					&& (( dgn.getAbbr().toUpperCase().indexOf( s ) < 0 )
					&& ( dgn.getDesc().toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(1).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(2).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getCode(3).toUpperCase().indexOf( s ) < 0 )
					&& (dgn.getrv().getPrintable().toUpperCase().indexOf(s)<0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new List item and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( Codebox );
					i.setValue( dgn.getRec());
					
					Listcell Desc = new Listcell( dgn.getDesc());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(i);
					
					Listcell Abbr = new Listcell( dgn.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent(i);
					
					Listcell C1 = new Listcell(dgn.getCode(1));
					C1.setStyle("border-right:2px dotted black;");
					C1.setParent(i);
					
					Listcell C2 = new Listcell(dgn.getCode(2));
					C2.setStyle("border-right:2px dotted black;");
					C2.setParent(i);
					
					Listcell C3 = new Listcell(dgn.getCode(3));
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(i);
					
					Listcell rv = new Listcell(dgn.getrv().getPrintable());
					rv.setStyle("border-right:2px dotted black;");
					rv.setParent(i);
					
								    
				}
			}	
		}
		
		
		dgn.close();
		

		return;
	}

/* public void onClick$CheckBtn (Event ev){
	 
		
	int searchType=0;   //search type: 0- Diagnosis codes , 1- service codes , 2- Insurance Carriers,  3- Pharmacies
		
	searchType = tabbox.getSelectedIndex();	
	
	switch ( searchType ){
	case 0:				// search for Diagnosis Codes 
	default:
		
	if ( Codebox.getSelectedCount() < 1 ){
		try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ /*} 
	/*	return;
		}
	
	System.out.println(Codebox.getSelectedItem().getValue());
	return;
	
	case 1:
	if ( Codebox2.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ /*}
	/*		return;
			}
		
	System.out.println(Codebox2.getSelectedItem().getValue());
		return;
	
	case 2:
		if ( Codebox3.getSelectedCount() < 1 ){
				try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ /*}
	/*			return;
				}
			
		System.out.println(Codebox3.getSelectedItem().getValue());
		return;	
	}
}
*/
	
	public void onOK$Code( Event ev ){ onClick$searchBtn( ev ); }
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$searchBtn( Event ev){

		int searchType=0;   //search type: 0- Diagnosis codes , 1- service codes , 2- Insurance Carriers,  3- Pharmacies
		
		searchType = tabbox.getSelectedIndex();
		//System.out.println( "searchType=" + searchType );
	switch ( searchType ){
		
	case 0:			// search for Diagnosis Codes 
	default:
			String s = Code.getValue().trim();
			if ( s.length() < 3 ){
				DialogHelpers.Messagebox( "Please enter at least three letters in the search field for diagnosis codes." );
				return;
			}
			refreshList(( s.length() < 1 ) ? null: s );
			return;
	case 1:	         // search for Service Codes
			String s1 = Code2.getValue().trim();
			if ( s1.length() < 3 ){
				DialogHelpers.Messagebox( "Please enter at least three letters in the search field for service codes." );
				return;
			}
			refreshList2(( s1.length() < 1 ) ? null: s1 );
			return;
		
	case 2:	         // search for Insurance Carriers
		String s2 = Code3.getValue().trim();
		if ( s2.length() < 3 ){
			DialogHelpers.Messagebox( "Please enter at least three letters in the search field for Insurance carriers." );
			return;
		}
		refreshList3(( s2.length() < 1 ) ? null: s2 );
		return;
	
	
	case 3:	         // search for Pharmacies
	String s3 = Code4.getValue().trim();
	if ( s3.length() < 3 ){
		DialogHelpers.Messagebox( "Please enter at least three letters in the search field for Pharmacies.");
		return;
	  }
	refreshList4(( s3.length() < 1 ) ? null: s3 );
	return;
	}	
} 
	
	
	public void onClick$selectBtn ( Event event) {
		int num = Codebox.getItemCount();
		if (num < 1) return;
		
		if (Codebox.getSelectedCount() < 1) return;
					
	}
	
	public void onOK (Event ev){
		if (Codebox.getSelectedCount() > 0){
			onClick$selectBtn( ev );
		} else {
			onClick$searchBtn( ev );
		}
		return;
	}
	
	/*public void onClear ( Event ev){
		onClick$exitBtn( ev );
		return;
		
	}*/
	
	/**
	 * For Service codes
	 * */
	
	public void refreshList2(){ onClick$searchBtn( null ); }
	
	public void refreshList2( String searchString ){

				
		if ( Codebox2 == null ) return;
		
		// remove all items
		for ( int i = Codebox2.getItemCount(); i > 0; --i ){
			Codebox2.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s1 = searchString;
		if ( s1 != null ) s1 = s1.toUpperCase();
		
		
		// populate list

		Srv srv = Srv.open();
		
		while ( srv.getNext()){
			
			int fnd = 1;
			
				// get srv code  validity byte
				Srv.Status status = srv.isValid() ? Srv.Status.ACTIVE: Srv.Status.INACTIVE;
				// is this type selected?
				if (( status == Srv.Status.ACTIVE ) /*|| ( status == Srv.Status.INACTIVE )*/){
			
				if (( searchString != null )
					&& (( srv.getAbbr().toUpperCase().indexOf( s1 ) < 0 )
					&& ( srv.getDesc().toUpperCase().indexOf( s1 ) < 0 )
					&& (srv.getCode(1).toUpperCase().indexOf( s1) <0 )
					&& (srv.getCode(2).toUpperCase().indexOf( s1) <0 )
					&& (srv.getCode(3).toUpperCase().indexOf( s1) <0 )
					&& (srv.getrv().getPrintable().indexOf(s1) <0 )
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( Codebox2 );
					i.setValue( srv.getRec());
					
					Listcell Desc = new Listcell(srv.getDesc());
					Desc.setStyle("border-right:2px dotted black;");
					Desc.setParent(i);
					
					Listcell Abbr = new Listcell(srv.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent(i);
					
					Listcell C1 = new Listcell(srv.getCode(1));
					C1.setStyle("border-right:2px dotted black;");
					C1.setParent(i);
					
					Listcell C2 = new Listcell(srv.getCode(2));
					C2.setStyle("border-right:2px dotted black;");
					C2.setParent(i);
					
					Listcell C3 = new Listcell(srv.getCode(3));
					C3.setStyle("border-right:2px dotted black;");
					C3.setParent(i);
					
					Listcell rv = new Listcell(srv.getrv().getPrintable());
					rv.setStyle("border-right:2px dotted black;");
					rv.setParent(i);
					
				}
			
			}
		}
		
		
		srv.close();
		

		return;
	}

	/**
	 * For Insurance Carriers
	 * @return 
	 * */
	

	
	
	public void refreshList3(){ onClick$searchBtn( null ); }
	
	public void refreshList3( String searchString ){

				
		if ( Codebox3 == null ) return;
		
		// remove all items
		for ( int i = Codebox3.getItemCount(); i > 0; --i ){
			Codebox3.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s2 = searchString;
		if ( s2 != null ) s2 = s2.toUpperCase();
		
		
		// populate list

		Ppo ppo = Ppo.open();
		
		while ( ppo.getNext()){
			String A2,A3,A12,A13,Contacts2,Notes2,Phones2;
			A2 = ppo.getAddress().getPrintableAddress(2);
			A3 = ppo.getAddress().getPrintableAddress(3);
			A12 = ppo.getAddress().getPrintableAddress(1)+","+ A2;
			A13 = ppo.getAddress().getPrintableAddress(1)+","+ A3;
			String C1,C2,C3,C4,C11,C22,C33,C44;
			C1= ppo.getContact(1);
			C2= ppo.getContact(2);
			C3= ppo.getContact(3);
			C4= ppo.getContact(4);
			if (C1.trim().length() == 0){
				C11="";
			}
			else {
				C11=C1;
			}
			if (C2.trim().length() == 0){
				C22="";
			}
			else {
				C22=C2;
			}
			if (C3.trim().length() == 0){
				C33="";
			}
			else {
				C33=C3;
			}
			if (C4.trim().length() == 0){
				C44="";
			}
			else {
				C44=C4;
			}
			
			StringBuilder Contacts = new StringBuilder(C11);
			join(Contacts, C22);
			join(Contacts, C33);
			join(Contacts, C44);
			
			Contacts2 = Contacts.toString();
			
			String N1,N2,N3,N4,N11,N22,N33,N44;
			N1= ppo.getnote(1);
			N2= ppo.getnote(2);
			N3= ppo.getnote(3);
			N4= ppo.getnote(4);
			if (N1.trim().length() == 0){
				N11="";
			}
			else {
				N11=N1;
			}
			if (N2.trim().length() == 0){
				N22="";
			}
			else {
				N22=N2;
			}
			if (N3.trim().length() == 0){
				N33="";
			}
			else {
				N33=N3;
			}
			if (N4.trim().length() == 0){
				N44="";
			}
			else {
				N44=N4;
			}
			
			StringBuilder Notes = new StringBuilder(N11);
			join(Notes, N22);
			join(Notes, N33);
			join(Notes, N44);
		
			Notes2 = Notes.toString();
			
			String P1,P2,P3,P4,P5,P6,P11,P22,P33,P44,P55,P66;
			P1= ppo.getphone(1);
			P2= ppo.getphone(2);
			P3= ppo.getphone(3);
			P4= ppo.getphone(4);
			P5= ppo.getphone(5);
			P6= ppo.getphone(6);
			if (P1.trim().length() == 0){
				P11="";
			}
			else {
				P11=P1;
			}
			if (P2.trim().length() == 0){
				P22="";
			}
			else {
				P22=P2;
			}
			if (P3.trim().length() == 0){
				P33="";
			}
			else {
				P33=P3;
			}
			if (P4.trim().length() == 0){
				P44="";
			}
			else {
				P44=P4;
			}
			if (P5.trim().length() == 0){
				P55="";
			}
			else {
				P55=P5;
			}
			if (P6.trim().length() == 0){
				P66="";
			}
			else {
				P66=P6;
			}
			
			StringBuilder Phones = new StringBuilder(P11);
			join(Phones, P22);
			join(Phones, P33);
			join(Phones, P44);
			join(Phones, P55);
			join(Phones, P66);
			
			Phones2 = Phones.toString();
			
			int fnd = 1;
			
			// get insurance carrier  validity byte
			Ppo.Status status = ppo.isValid() ? Ppo.Status.ACTIVE: Ppo.Status.INACTIVE;
			// is this type selected?
			if (( status == Ppo.Status.ACTIVE ) /*|| ( status == Ppo.Status.INACTIVE )*/){
			
			
				if (( searchString != null )
					&& (( ppo.getAbbr().toUpperCase().indexOf( s2 ) < 0 )
					&& ( ppo.getname().toUpperCase().indexOf( s2 ) < 0 )
					&& (ppo.getContact(1).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(2).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(3).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getContact(4).toUpperCase().indexOf( s2) <0 )
					&& (ppo.getnote(1).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(2).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(3).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getnote(4).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(1).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(2).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(3).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(4).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(5).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getphone(6).toUpperCase().indexOf(s2) < 0)
					&& (ppo.getAddress().getPrintableAddress(1).toUpperCase().indexOf(s2) < 0)
					&& (A2.toUpperCase().indexOf(s2) < 0)
					&& (A3.toUpperCase().indexOf(s2) < 0)
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( Codebox3 );
					i.setValue( ppo.getRec());
					//i.setStyle("background-color:yellow;");
					//i.setStyle("border-left:1px solid black;border-bottom:1px solid black;border-right:1px solid black; border-top:1px solid black");
					//i.setHflex("min");
					//int ccs = 0,ccs2,ccs3,ccs4;
					
					
					
					//System.out.println(ccs);
					
					Listcell Desc = new Listcell( ppo.getname());
					Desc.setStyle("border-left:2px solid dotted; border-right:2px dotted black;");
					Desc.setParent( i );
					
					Listcell Abbr = new Listcell( ppo.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent( i );
					
					Listcell C = new Listcell(Contacts2);
					/*if (C.getHeight() == null ){
						C.setHeight("50%");
					}
					else {
						System.out.println("yolo");
					}
					//C.setWidth("auto");
					//C.setHeight("auto");
					System.out.println("Height is:"+ C.getHeight());
					System.out.println("Width is:"+ C.getWidth());*/
					C.setStyle("border-right:2px dotted black;"); // solid
					//C.setStyle("text-align:left");
					C.setParent(i);
					
					Listcell N = new Listcell (Notes2);
					N.setStyle("border-right:2px dotted black;");
					N.setParent(i);
					
					Listcell P = new Listcell (Phones2);
					P.setStyle("border-right:2px dotted black;");
					P.setParent(i);
					
					if(ppo.getAddress().getPrintableAddress(1).trim().length()== 0 && A2.trim().length() == 0)
					{
						Listcell Blank =  new Listcell ("");
						Blank.setStyle("border-right:2px dotted black;");
						Blank.setParent(i);
						
						Listcell Blank2 = new Listcell ("");
						Blank2.setStyle("border-right:2px dotted black;");
						Blank2.setParent(i);
					}
					
					else if (A2.trim().length() == 0){ 
						Listcell Addr =  new Listcell (ppo.getAddress().getPrintableAddress(1));
						Addr.setStyle("border-right:2px dotted black;");
						Addr.setParent(i);
						
						Listcell CSZ = new Listcell (A3);
						CSZ.setStyle("border-right:2px dotted black;");
						CSZ.setParent(i);
							}
					
					else {
						Listcell Addr = new Listcell (A12);
						Addr.setStyle("border-right:2px dotted black;");
						Addr.setParent(i);
						
						
						Listcell CSZ = new Listcell (A3);
						CSZ.setStyle("border-right:2px dotted black;");
						CSZ.setParent(i);
						
						}
				    
				}
			}
		}
		
		
		ppo.close();
		

		return;
	}
	
	/**
	 * For Pharmacies
	 * */
	
	public void refreshList4(){ onClick$searchBtn( null ); }
	
	public void refreshList4( String searchString ){

				
		if ( Codebox4 == null ) return;
		
		// remove all items
		for ( int i = Codebox4.getItemCount(); i > 0; --i ){
			Codebox4.removeItemAt( 0 );
		}
		
		
		// search string passed?
		String s3 = searchString;
		if ( s3 != null ) s3 = s3.toUpperCase();
		
		
		// populate list

		Phrds phrds = Phrds.open();
		
		while ( phrds.getNext()){
			String Pa2,Pa3,P12,P13;
			Pa2= phrds.getAddress().getPrintableAddress(2);
			Pa3= phrds.getAddress().getPrintableAddress(3);
			P12= phrds.getAddress().getPrintableAddress(1)+"," + Pa2;
			P13 = phrds.getAddress().getPrintableAddress(1)+"," + Pa3;
			
			int fnd = 1;
			
			// get pharmacy  validity byte
			Phrds.Status status = phrds.isValid() ? Phrds.Status.ACTIVE: Phrds.Status.INACTIVE;
			// is this type selected?
			if (( status == Phrds.Status.ACTIVE ) /*|| ( status == Phrds.Status.INACTIVE )*/){
			
				if (( searchString != null )
					&& (( phrds.getAbbr().toUpperCase().indexOf( s3 ) < 0 )
					&& ( phrds.getName().toUpperCase().indexOf( s3 ) < 0 )
					&& (phrds.getAddress().getPrintableAddress(1).toUpperCase().indexOf(s3)< 0)
					&& (Pa2.toUpperCase().indexOf(s3)<0)
					&& (Pa3.toUpperCase().indexOf(s3)< 0)
					&& (phrds.getContact().toUpperCase().indexOf(s3) < 0)
					
					)){					
					// this one doesn't match
					fnd = 0;
				}
		
				if ( fnd > 0 ){
					
					// create new Listitem and add cells to it
					Listitem i;
					(i = new Listitem()).setParent( Codebox4 );
					i.setValue( phrds.getRec());
					
					Listcell Name = new Listcell( phrds.getName());
					Name.setStyle("border-right:2px dotted black;");
					Name.setParent( i );
					
					Listcell Abbr = new Listcell( phrds.getAbbr());
					Abbr.setStyle("border-right:2px dotted black;");
					Abbr.setParent( i );
					
					Listcell Contact = new Listcell(phrds.getContact());
					Contact.setStyle("border-right:2px dotted black;");
					Contact.setParent(i);
					
					if (phrds.getAddress().getPrintableAddress(1).trim().length() == 0 && Pa2.trim().length()== 0){
						Listcell Blank = new Listcell ("");
						Blank.setStyle("border-right:2px dotted black;");
						Blank.setParent(i);
						
						Listcell Blank2 = new Listcell ("");
						Blank2.setStyle("border-right:2px dotted black;");
						Blank2.setParent(i);
					
					}
										
					else if (Pa2.trim().length() ==0){
						Listcell A1 = new Listcell (phrds.getAddress().getPrintableAddress(1));
						A1.setStyle("border-right:2px dotted black;");
						A1.setParent( i );
						
						Listcell CSZ = new Listcell (Pa3);
						CSZ.setStyle("border-right:2px dotted black;");
						CSZ.setParent(i);
					}
				
					else{
						Listcell A12 = new Listcell (P12);
						A12.setStyle("border-right:2px dotted black;");
						A12.setParent(i);
						
					    Listcell CSZ = new Listcell (Pa3);
					    CSZ.setStyle("border-right:2px dotted black;");
					    CSZ.setParent(i);
					}
		    
				}
			}
		}
		
		
		phrds.close();
		

		return;
	}
	public void onOK$Code2( Event ev ){ onClick$searchBtn( ev ); }
	
	
	// Open dialog to enter new vitals data
	
	/*public void onClick$searchBtn2( Event ev){

		String s = Code2.getValue().trim();
		if ( s.length() < 3 ){
			DialogHelpers.Messagebox( "Please enter at least three letters in search field." );
			return;
		}
		refreshList(( s.length() < 1 ) ? null: s );
		return;
			
	} */
	
	public void onClick$selectBtn2 ( Event event) {
		int num = Codebox2.getItemCount();
		if (num < 1) return;
		
		if (Codebox2.getSelectedCount() < 1) return;
					
	}
	
	public void onOK2 (Event ev){
		if (Codebox2.getSelectedCount() > 0){
			onClick$selectBtn( ev );
		} else {
			onClick$searchBtn( ev );
		}
		return;
	}
}
