package palmed;

import java.util.ArrayList;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import usrlib.Rec;

public class Notifier {
	
	public enum Event {
		
		PTINFO,	
		MED,
		PAR,
		SOAP,
		DOC,
		LAB,
		PROB,
		VITALS,
		CARELOG,
		ORDERS,
		IMMUNIZATIONS,
		INTERVENTION,
		NC_STATUS,			// Newcropt ERx Status
		NURSING_NOTES,
		TREATMENT_NOTES,
		LAB_NOTES,
		XRAY_NOTES,
		
		MISC;
		
	}


	
	private class WatchList {
		
		Rec ptRec;
		Notifier.Event event;
		NotifierCallback callbackObj;
		
		public WatchList( NotifierCallback callbackObj, Rec ptRec, Notifier.Event event ){
			this.callbackObj = callbackObj;
			this.ptRec = ptRec;
			this.event = event;
		}
		
		public boolean matches( Rec ptRec, Notifier.Event event ){
			boolean ret = false;
			if ( event == this.event ){
				if ( ptRec == null ){
					ret = true;
				} else if ( this.ptRec.equals( ptRec )){
					ret = true;
				}
			}
			
			return ret;
		}
	}

	
	static Notifier instance = null;		// instance of this class
	static EventQueue eventQueue = null;	// instance of event queue - application wide
	//static ArrayList<WatchList> list = new ArrayList<WatchList>();
	
	

	
	/**
	 * Notifier() - private constructor
	 * 
	 * Trivial.
	 */
	private Notifier(){
		System.out.println( "Notifier() object created" );
		return;
	}
	
	
	
	/**
	 * getInstance()
	 * 
	 * Instantiates the class if not already done.
	 * 
	 */
	public static Notifier getInstance(){
		
		//if ( ! ( instance instanceof Notifier )){
		if ( instance == null ){
			instance = new Notifier();
			//System.out.println( "Notifier() new instance created: " + instance.toString() + ", list=" + list.toString());
			
			// create ZK event queue
			eventQueue = EventQueues.lookup("palmedNotifier", EventQueues.APPLICATION, true ); 
		}
		
		return instance;
	}
	
	
	/**
	 * registerCallback()
	 * 
	 * Register callback.
	 * 
	 * @param callbackObj
	 * @param ptRec
	 * @param event
	 * @return
	 */
	public static boolean registerCallback( final NotifierCallback callbackObj, final Rec ptRec, final Notifier.Event event ){
		
		//list.add( new WatchList( callbackObj, ptRec, event ));
		//System.out.println( "Notifier.registerCallback() instance=" + instance.toString() + ", list=" + list.toString() + ", ptRec=" + ptRec + ", event=" + event.toString());
		
		// Subscribe to event
		eventQueue.subscribe( new EventListener(){

			public void onEvent(org.zkoss.zk.ui.event.Event ev )
					throws Exception {
				WatchList w = (WatchList) ev.getData();
				if ( w.matches( ptRec, event ))
					callbackObj.callback(ptRec, event);
			}
			
		});
		
		return true;
	}
	
	

	/**
	 * removeCallback()
	 * 
	 * @param callbackObj
	 * @param ptRec
	 * @param event
	 * @return
	 */
	public static boolean removeCallback( NotifierCallback callbackObj, Rec ptRec, Notifier.Event event ){
		
		//System.out.println( "Notifier.removeCallback() instance=" + instance.toString() + ", list=" + list.toString() + ", ptRec=" + ptRec + ", event=" + event.toString());
		boolean found = false;
		
/*		for ( WatchList w: list ){
			if ( w.matches( ptRec, event ) && ( w.callbackObj.equals( callbackObj ))){
				System.out.println( "removing callback ptRec=" + ptRec + ", event=" + event.toString());
				//list.remove( w );
				found = true;
			}
		}
*/		return found;
	}
	
	
	
	/**
	 * notify()
	 * 
	 * Notify this Notifier class of event - and handle calling the callbacks.
	 * (Eventually need to make this multithreaded and work across sessions.
	 * 
	 * @param ptRec
	 * @param event
	 */
	public static boolean notify( Rec ptRec, Notifier.Event event ){
		
		//System.out.println( "Notifier.notify() instance=" + instance.toString() + ", list=" + list.toString() + ", ptRec=" + ptRec + ", event=" + event.toString());
		boolean found = false;
		
/*		for ( NotifierWatchlist w: list ){
			if ( w.matches( ptRec, event )){
				if ( w.callbackObj instanceof NotifierCallback ){
					System.out.println( "Initiating callback entry=" + w.toString() + ", ptRec=" + ptRec + ", event=" + event.toString());
					w.callbackObj.callback( ptRec, event );
					found = true;
				} else {
					// clean up list
					list.remove( w );
					System.out.println( "Old callback entry removed from list, ptRec=" + ptRec + ", event=" + event.toString());
				}
			}
		}
*/
		
		eventQueue.publish( new org.zkoss.zk.ui.event.Event( "onPalmedNotifier", null, instance.new WatchList( null, ptRec, event ) ));
		return found;
	}

}
