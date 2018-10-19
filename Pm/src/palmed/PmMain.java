package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;



public class PmMain {
	
	private String msg = "";
	private String userName;
	
	private Component parent;		// parent Component (calling window)
	private Window pmMainWin;		// login window (created window)
	

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
		return new PmMain( parent ).getStatus();
	}
	
	
	
	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 *
	 * @param parent
	 * @param anQuestion
	 */
	private PmMain( Component parent ) {
		
		super();
		this.parent = parent;
		
        System.out.println( "PmMain()" );


//		textbox = new Textbox();

//		setParent(parent);

//		try {
//			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		userName = "RickP";

		createBox();
        System.out.println( "PmMain() returning" );
	}

	private void createBox() {

        System.out.println( "PmMain.createBox()" );

        System.out.println( "PmMain.createBox() - calling Executions.createComponents()" );
		pmMainWin = (Window) Executions.createComponents("main.zul", this.parent, null);
        System.out.println( "PmMain.createBox() - back from Executions.createComponents()" );

        System.out.println( "calling createMainMenu()" );
    	createMainMenu( pmMainWin.getFellow( "divWest" ));

		
		/*
		setWidth("350px");
		setHeight("150px");
		setTitle("TEXT BOX TITLE" );
		setId("confBox");
		setVisible(true);
		setClosable(true);
		*/
		
//		pmLoginWin.addEventListener("onClose", new OnCloseListener());

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
				if ( textbox.getText().trim().length() < 1 ) {
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


/*		try {
			pmLoginWin.doModal();
		} catch (SuspendNotAllowedException e) {
//			logger.fatal("", e);
	        System.out.println( "SuspendNotAllowedException" );

		} catch (InterruptedException e) {
//			logger.fatal("", e);
	        System.out.println( "InterruptedException" );

		}
*/
	}
	
	
	/*
	 * This function creates the main menu tree
	 */
	private void createMainMenu( Component parent ){
		System.out.println( "createMainMenu()" );
		System.out.println( "parent=" + parent.toString());
		Executions.createComponents( "mainmenu.zul", parent, null );
		System.out.println( "createMainMenu() return" );
	}


	
	public boolean getStatus(){
        System.out.println( "PmMain.getStatus()" );
		return true;
	}
}
