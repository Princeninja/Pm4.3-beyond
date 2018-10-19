package usrlib;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.impl.MessageboxDlg;

public class MyOptionBoxController extends MessageboxDlg implements Composer {

	public void doAfterCompose(Component arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println( "in doAfterCompose()" );
		
		return;
	}

}
