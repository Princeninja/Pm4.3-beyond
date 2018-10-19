package palmed;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class StdDictEdit {

	private Window stdDictWin; 


	public enum Operation {
		Edit, New;
	}

	/**
	 * The Call method.
	 */
	public static boolean edit(Rec rec, boolean flgPerms, Component parent) {

		// if ptrec not valid, return
		if ((rec == null) || (rec.getRec() < 2)) {
			SystemHelpers.seriousError("StdDictEdit.edit() bad rec");
			return false;
		}

		new StdDictEdit(Operation.Edit, rec, flgPerms, parent);
		return true;
	}

	public static void add(boolean flgPerms, Component parent) {

		new StdDictEdit(Operation.New,null, flgPerms, parent);
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 */
	private StdDictEdit(Operation operation, Rec rec, boolean flgPerms,
			Component parent) {
		super();
		createBox(operation, rec, flgPerms, parent);
	}

	private void createBox(Operation operation, Rec rec,
			boolean flgPerms, Component parent) {

		// pass parameters to new window
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put("operation", operation);
		if (rec != null){
			myMap.put("rec", rec);
		}
		myMap.put("flgPerms", flgPerms);

		// create new window
		stdDictWin = (Window) Executions.createComponents("stddictedit.zul",
				parent, myMap);

		try {
			stdDictWin.doModal();
		} catch (SuspendNotAllowedException e) {

		} catch (Exception e) {
			System.out.println("Exception");

		}
	}

}
