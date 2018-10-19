package usrlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;


import java.net.URL;
//import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
//import javax.security.cert.CertificateException;
//import javax.security.cert.X509Certificate;

public class SOAPClient4XG {

    public static String send(String SOAPUrl, String SOAPAction, String xmltext) throws Exception {

    	
    	
    	
    	System.out.println( "SOAPClient4XG: \n     URL=" + SOAPUrl + "\n     Action=" + SOAPAction );
    	
    	X509TrustManager tm = new X509TrustManager() {
    		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    		return null;
    		}
    		
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
				
				// trust everyone
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub		
				
				// trust everyone
			}
    	};


    	
    	
    	SSLContext ctx = SSLContext.getInstance("TLS");
    	ctx.init(null, new TrustManager[] { tm }, null);
    	
    	HttpsURLConnection httpConn = (HttpsURLConnection) new URL( SOAPUrl ).openConnection();
    	//HttpsURLConnection httpConn = new HttpsURLConnection( new URL( SOAPUrl ));
    	
    	httpConn.setSSLSocketFactory(ctx.getSocketFactory());
    	httpConn.setHostnameVerifier(new HostnameVerifier() {

		public boolean verify(String paramString, SSLSession paramSSLSession) {
			// TODO Auto-generated method stub
			return false;
		}
    	});
    	
    	//httpConn.connect();


    	
    	
		
        // Create the connection where we're going to send the file.
/* REPLACED WITH CODE ABOVE FOR SECURE HTTPS SSL (to get around security certificate stuff)
        URL url = new URL(SOAPUrl);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
*/
        // Copy input XML into byte array
        byte[] b = xmltext.getBytes( "UTF-8" );
        
        //System.out.println( "==================");
        //System.out.println( xmltext );
        //System.out.println( "==================");
        //System.out.println( xmltext.length());
        


        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty( "Content-Length", String.valueOf( b.length) );
        httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction",SOAPAction);
        httpConn.setRequestMethod( "POST" );
        //httpConn.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
        //httpConn.setRequestProperty( "Content-Length", String.valueOf( b.length) );
		//httpConn.setRequestProperty("SOAPAction",SOAPAction);
        //httpConn.setRequestMethod( "POST" );
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        
        httpConn.setDoOutput(true);		// was connection.set....
        httpConn.setDoInput(true);		// was connection.set....
        
   

        // Everything's set up; send the XML that was read in to b.
        OutputStream out = httpConn.getOutputStream();
        //OutputStreamWriter out = new OutputStreamWriter( connection.getOutputStream());

        out.write( b /*xmltext*/ );    
        out.close();


        //System.out.println( "\n\n\n\n\n\n\n");
        //System.out.println( "**************************");
        //System.out.println( new String( b, "UTF-8" ) );
        //System.out.println( "**************************");

        // Read the response and write it to standard out.

        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());	// was connection.get.....
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        StringBuilder sb = new StringBuilder();

        while ((inputLine = in.readLine()) != null){
        	sb.append( inputLine );       	
            //System.out.println(inputLine);
        }
        
        in.close();
        
        
        
        
    	httpConn.disconnect();
    	
        return sb.toString();
    }

  // copy method from From E.R. Harold's book "Java I/O"
  public static void copy(InputStream in, OutputStream out) 
   throws IOException {

    // do not allow other threads to read from the
    // input or write to the output while copying is
    // taking place

    synchronized (in) {
      synchronized (out) {

        byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = in.read(buffer);
          if (bytesRead == -1) break;
          out.write(buffer, 0, bytesRead);
        }
      }
    }
  } 
}
