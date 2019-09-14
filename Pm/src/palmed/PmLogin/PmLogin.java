package palmed.PmLogin;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import usrlib.DialogHelpers;
import usrlib.Rec;





	



/**
 * This class creates a modal window as a dialog in which the user <br>
 * can input some text. By onClosing with <RETURN> or Button <send> this
 * InputConfirmBox can return the message as a String value if not empty. <br>
 * In this case the returnValue is the same as the inputValue.<br>
 *
 * @author rickpalen
 */
public class PmLogin /*extends Window*/ {

	private static final long serialVersionUID = 8109634704496621100L;
	//private static final Logger logger = Logger.getLogger(InputMessageTextBox.class);

	private String userName;
	
	private Component parent;		// parent Component (calling window)
	private Window pmLoginWin;		// login window (created window)
	private String Verify;
	private static int UserRec;
	private static int r;
	
	/**
	 * 
	 * Super methods for custom user verification
	 * 2018	
	 */
	
	public static int show(Component parent, String Verify) {
				new PmLogin(parent, Verify).getStatus();
				  r = getUserRec();
				return r;
	}
	
	/**
	 * The Call method.
	 *
	 * @param parent
	 *            The parent component
	 * @param anQuestion
	 *            The question that's to be confirmed.
	 * @return String from the input textbox.
	 */
	public static boolean show(Component parent) {
		return new PmLogin(parent, "").getStatus();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 *
	 * @param parent
	 * @param anQuestion
	 */
	private PmLogin(Component parent, String Verify) {
		super();
		this.parent = parent;
		if ( Verify.equals("true")){ this.Verify = "true";  }
//		textbox = new Textbox();

//		setParent(parent);

//		try {
//			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		userName = "RickP";

		createBox();
	}

	private void createBox() {
		
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put( "Verify", this.Verify );
		myMap.put("UserRec", UserRec);
		//System.out.println("Verfiy alpha: "+Verify );
		pmLoginWin = (Window) Executions.createComponents("login.zul", this.parent, myMap);
		//Library.setProperty("org.zkoss.theme.preferred", "sapphire");
		//Executions.sendRedirect(null);
		/*
		setWidth("350px");
		setHeight("150px");
		setTitle("TEXT BOX TITLE" );
		setId("confBox");
		setVisible(true);
		setClosable(true);
		*/
		
		pmLoginWin.addEventListener("onClose", new OnCloseListener());
		// Hbox hbox = new Hbox();
		// hbox.setWidth("100%");
		// hbox.setParent(this);
		// Checkbox cb = new Checkbox();
		// cb.setLabel(Labels.getLabel("common.All"));
		// cb.setChecked(true);

		/*
		Separator sp = new Separator();
		sp.setParent(this);

		textbox.setWidth("98%");
		textbox.setHeight("80px");
		textbox.setMultiline(true);
		textbox.setRows(5);
		textbox.setParent(this);

		Separator sp2 = new Separator();
		sp2.setBar(true);
		sp2.setParent(this);

		Button btnSend = new Button();
		btnSend.setLabel("common.Send");
		btnSend.setParent(this);
		btnSend.addEventListener("onClick", new EventListener() {

//			@Override
			public void onEvent(Event event) throws Exception {

				// Check if empty, than do not send
				if ( textbox.getText().trim().isEmpty()) {
					onClose();
					return;
				}

//				msg = msg + ZksampleDateFormat.getDateTimeLongFormater().format(new Date()) + " / " + Labels.getLabel("common.Message.From") + " " + userName + ":" + "\n";
				msg = msg + "{Date}" + " / " + "common.Message.From" + " " + userName + ":" + "\n";

				msg = msg + textbox.getText();
				msg = msg + "\n" + "_____________________________________________________" + "\n";

				onClose();
			}
		});
		*/


		try {
			pmLoginWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (InterruptedException e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}
		
		this.UserRec = (((Intbox) (pmLoginWin.getFellow( "uuserRec", false ))).getValue());

	}

	final class OnCloseListener implements EventListener {
//		@Override
		public void onEvent(Event event) throws Exception {
			//System.out.println( "createBox() - in ptSearchWin() onClose EventListener");
			UserRec = ((Intbox)(pmLoginWin.getFellow( "uuserRec" ))).getValue();
			pmLoginWin.onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	public boolean getStatus() {
		return true;
	}
	
	public static  int getUserRec() {
		return UserRec;
	}
}

	
	
