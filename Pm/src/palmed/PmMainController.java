/**
 * 
 */
package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;

/**
 * @author Owner
 *
 */
public class PmMainController extends GenericForwardComposer {
	
	Div	divWest;		// main menu
	
	
	@Override
	public void	doAfterCompose( Component comp ){
		System.out.println( "PmMainController.doAfterCompose()" );

		// call superclass to autowire variables
		try {
			super.doAfterCompose(comp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//create Main Menu

		System.out.println( "PmMainController.doAfterCompose() return" );

	}

}
