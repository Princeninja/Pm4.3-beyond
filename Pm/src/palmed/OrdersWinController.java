package palmed;

import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.DialogHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class OrdersWinController extends GenericForwardComposer {

	Window ordersWin;					// autowired
	private Listbox lboxOrders;		// autowired - orders listbox
	private Radio rbActive;
	private Radio rbAll;
	
	
	private Rec	ptRec;		// patient record number


	
	

	public OrdersWinController(){
		super();
	}

	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if ( ! Rec.isValid( ptRec )){
			System.out.println( "Error: OrdersWinController() ptRec=" + ptRec.getRec());
			ptRec = new Rec( 2 );		// default for testing
		}
		
		// set initial display to all
		rbActive.setChecked( true );

		refreshList();
		
		// register callback with Notifier
		Notifier.registerCallback( new NotifierCallback (){
				public boolean callback( Rec ptRec, Notifier.Event event ){
					refreshList();
					return false;
				}}, ptRec, Notifier.Event.ORDERS );
		return;
	}
	
	
	
	
	
	
	
	public void refreshList(){
		
		// clear listbox
		ZkTools.listboxClear( lboxOrders );
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		MedPt medPt = new MedPt( dirPt.getMedRec());

		boolean flgAll = rbAll.isChecked();
		
		for ( Reca reca = medPt.getOrdersReca(); Reca.isValid( reca ); ){
			
			Orders order = new Orders( reca );
			
			// display this one?
			if ( flgAll || ( order.getStatus() != Orders.Status.CLOSED )){
			
				// create new Listitem and add cells to it
				Listitem i;
				(i = new Listitem()).setParent( lboxOrders );
				new Listcell( order.getDate().getPrintable(9)).setParent( i );
				new Listcell( order.getDesc()).setParent( i );
				new Listcell( Prov.getAbbr( order.getPcnRec())).setParent( i );
				new Listcell( order.getPriority().getLabel()).setParent( i );
				new Listcell( order.getStatus().getLabel()).setParent( i );
				new Listcell( order.getIndicationsText()).setParent( i );
				
				String s = "";
				String t = order.getNoteTxt();
				if ( t.length() > 0 ) s = "Note/Instructions: " + t;
				
				if ( order.getStatus() == Orders.Status.CLOSED ){
					t = "Closed: " + order.getReason().getLabel() + " " + order.getDateSatisfied().getPrintable(9);
					if ( s.length() > 0 ) s = s + ", ";
					s = s + t;
				}
				
				if ( s.length() > 0 ) i.setTooltiptext( s );
				//new Listcell( order.getNoteTxt()).setParent( i );
				
				// store PAR reca in listitem's value
				i.setValue( reca );
			}
			
			
			// get next reca in list	
			reca = order.getLLHdr().getLast();
		}


		return;
	}
	
	public void onCheck$rbAll(){
		refreshList();
	}
	
	public void onCheck$rbActive(){
		refreshList();
	}
	
	
	
	public void onClick$btnAdd(){
		
		OrdersAdd.add( ptRec, ordersWin );
		Notifier.notify( ptRec, Notifier.Event.ORDERS );
		//refreshList();
		return;
	}
	
	public void onClick$btnEdit(){
		
		if ( lboxOrders.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca ordersReca = (Reca) lboxOrders.getSelectedItem().getValue();
		if ( ! Reca.isValid(ordersReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		OrdersAdd.edit( ptRec, ordersReca, ordersWin );
		Notifier.notify( ptRec, Notifier.Event.ORDERS );
		//refreshList();
		return;
	}
	
	
	
	
	
	
	
	public void onClick$btnRemove(){
		
		if ( lboxOrders.getSelectedCount() < 1 ){
			DialogHelpers.Messagebox( "No item selected." );
			return;
		}
		
		Reca parReca = (Reca) lboxOrders.getSelectedItem().getValue();
		if ( ! Reca.isValid(parReca)){
			DialogHelpers.Messagebox( "Invalid item selected." );
			return;
		}
		
		OrdersClose.show( ptRec, parReca, ordersWin );
		Notifier.notify( ptRec, Notifier.Event.ORDERS );
		//refreshList();
		return;
	}
	
	
	public void onClick$btnPrint(){
		
		//System.out.println( "onClick$btnPrint" );
		
		
		// Generate HTML report text
		
		StringBuilder sb = new StringBuilder( 1000 );
		
		sb.append( "<HTML>" );
		sb.append( "<HEAD>" );
		
		sb.append( "<style type='text/css'>" );
		//sb.append( "font-size:large" );
		//sb.append( "body{font-size:100%;}" );
		//sb.append( "h3 {font-size:200%;}" );
		//sb.append( "p {font-size:100%;}" );
		//sb.append( "*{font-size:40%;}" );

		sb.append( "</style>" );
		
		sb.append( "</HEAD>" );
		sb.append( "<BODY>" );
		sb.append( "<H2>PAL/MED<H2>" );
		sb.append( "<H3>Date: " + Date.today().getPrintable( 9 ) + "<H3>" );
		sb.append( "<space>");
		
		DirPt dirpt = new DirPt( ptRec );
		
		sb.append( "<H3>Patient: " + dirpt.getName().getPrintableNameLFM());
		sb.append( "<H3>DOB: " + dirpt.getBirthdate().getPrintable(9));
		sb.append( "<space>" );
		sb.append( "<H3>Orders<H3>" );
		sb.append( "<space>" );
		
		sb.append( "<table>" );
		sb.append( "<tr><td>Date</td><td>Order</td><td>Priority</td><td>Status</td><td>Provider</td><td>Instructions</td></tr>" );
		
		for( int i = 0; i < lboxOrders.getItemCount(); ++i ){
			Listitem li = lboxOrders.getItemAtIndex( i );
			Reca orderReca = (Reca) li.getValue();
			Orders order = new Orders( orderReca );
			sb.append( "<tr><td>" + order.getDate().getPrintable(9) + "</td>"
					+ "<td>" + order.getDesc() + "</td>"
					+ "<td>" + order.getPriority().getLabel() + "</td>"
					+ "<td>" + order.getStatus().getLabel() + "</td>"
					+ "<td>" + Prov.getAbbr( order.getPcnRec()) + "</td>"
					+ "<td>" + order.getNoteTxt() + "</td></tr>" );
		}
		
		sb.append( "</table>" );
		sb.append( "END REPORT" );

		// javascript to print the report
		sb.append( "<script type='text/javascript' defer='true' >" );
		sb.append( "window.focus();window.print();window.close();" );
		sb.append( "</script>" );

		sb.append( "</BODY>" );
		sb.append( "</HTML>" );
		
		String html = sb.toString();
		
		//System.out.println( html );

		
		// Wrap HTML in AMedia object
		AMedia amedia = new AMedia( "print.html", "html", "text/html;charset=UTF-8", html );
		if ( amedia == null ) System.out.println( "AMedia==null" );

		
		// Create Iframe and pass it amedia object (HTML report)
		Iframe iframe = new Iframe();
		iframe.setHeight( "1px" );
		iframe.setWidth( "1px" );
		iframe.setContent( amedia );
		iframe.setParent( ordersWin );

		//TODO - check this out // Note: - the script in the HTML code should close the iframe after printing......
		
		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.ORDERS_PRINT, ptRec, Pm.getUserRec(), null, null );
		
		
		return;
	}

}
