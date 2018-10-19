package palmed;


import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.io.*; 


public class EcsTransmitWinController extends GenericForwardComposer {

	private Window ecsTransmitWin;		// autowired
	private Button transmit;			// autowired
	private Button close;				// autowired
	private Listbox textarea;			// autowired
	
	
	
	

	
	public void onClick$transmit(){
		

		try { 
			Process p=Runtime.getRuntime().exec("cmd /c dir"); 
			//p.waitFor(); 
			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
			String line=reader.readLine(); 
			textarea.appendItem( line, null );

			
			while( line != null ){ 
				System.out.println(line); 
				textarea.appendItem( line, null );
				line=reader.readLine(); 
			} 
	
		} 
		catch(IOException e1) {} 
		//catch(InterruptedException e2) {} 
	
		System.out.println("Done"); 
	
	} 
	
	public void onClick$close(){
		
		ecsTransmitWin.detach();
	}


}
