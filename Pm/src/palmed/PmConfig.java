package palmed;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

//import org.zkoss.zk.ui.Executions;

import usrlib.XMLElement;
import usrlib.XMLParseException;

public class PmConfig {
	
	private static String fn_config = "palmed.xml";
	
	
	private PmConfig(){
		return;
	}

	
	public static XMLElement read(){
		
		XMLElement xml = new XMLElement();
		FileReader reader;
		
		String path = SystemHelpers.getRealPath();
		System.out.println( "absolute path=" + path );
		
		try {
			reader = new FileReader( path + fn_config );
		} catch (FileNotFoundException e) {
			System.out.println( "file not found:" + path + fn_config );
			return null;
		}
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e) {
			System.out.println( "poorly formed XML:" + fn_config );
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println( "root element=" + xml.getName());
		System.out.println( "num children=" + xml.countChildren());
		Enumeration<usrlib.XMLElement> children = xml.enumerateChildren();
		
		while ( children.hasMoreElements()){
			XMLElement e2 = (XMLElement) children.nextElement();
			System.out.println( e2.getName());
		}

		return xml;		
	}
	
	
	
	
	
	public static String getElement( String name ){
		
		String str = "";
		XMLElement xml = read();
		if ( xml == null ) return str;
		
		XMLElement e = xml.getElementByPathName( name );
		if ( e != null ) str = e.getContent();
		System.out.println( name + "-->" + str + "." );
		return str;
	}
	
	
	public static String getMedPath(){		
		return getElement( "/palmed/practice/paths/medPath" );
	}
	
	public static String getOvdPath(){
		return getElement( "/palmed/practice/paths/ovdPath" );
	}

	public static String getSchPath(){
		return getElement( "/palmed/practice/paths/schPath" );
	}
}
