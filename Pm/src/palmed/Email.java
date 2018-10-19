package palmed;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import usrlib.DialogHelpers;





public class Email {

	
	
	public static boolean send( String toAddress, String subj, String text, String fname ){
		String[] sa = new String[1];
		sa[0] = toAddress;
		
		String[] fn = new String[1];
		fn[0] = fname;
		
		return send( sa, subj, text, fn );
		
	}
	
	
	public static boolean send( String[] toAddresses, String subj, String text, String[] fnames ){
		
		String host = "smtp.gmail.com";
		String from = "rickpalen@gmail.com";
		String password = "lfr996XX";
		
		Properties props = System.getProperties(); 
		props.put("mail.smtp.starttls.enable", "true"); 
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", "lfr996");
		props.put("mail.smtp.port", "587");	// 587 is the port number of yahoo mail 
		props.put("mail.smtp.auth", "true"); 
		
		
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		
		try {
			
			// set from address
			message.setFrom(new InternetAddress(from));

			// add recipients
			for ( int i = 0; i < toAddresses.length; ++i )
				message.addRecipient( Message.RecipientType.TO, new InternetAddress( toAddresses[i] ));
			
			message.setSubject( subj );
			message.setText( text );

    		// set multipart message content
    		Multipart multipart = new MimeMultipart();
			System.out.println( "here3" );

    		//add at least simple body
			MimeBodyPart body = new MimeBodyPart();
			body.setText( text ); 
    		multipart.addBodyPart( body );
			System.out.println( "here4" );

    		// attachment
			MimeBodyPart[] attachMents = new MimeBodyPart[fnames.length];
			
			for ( int i = 0; i < fnames.length; ++i ){
				
	    		attachMents[i] = new MimeBodyPart();
	    		FileDataSource dataSource = new FileDataSource(new File( fnames[i] ));
	    		attachMents[i].setDataHandler(new DataHandler(dataSource));
	    		attachMents[i].setFileName( fnames[i] );
	    		attachMents[i].setDisposition( MimeBodyPart.ATTACHMENT );
	    		multipart.addBodyPart( attachMents[i] );
			}

    		message.setContent( multipart );

			
			System.out.println( "here5" );
			
			Transport transport = session.getTransport("smtp");
			System.out.println( "here5a" );
			transport.connect(host, from, password);
			System.out.println( "here5b" );
			transport.sendMessage(message, message.getAllRecipients());
			System.out.println( "here5c" );
			transport.close(); 
			System.out.println( "here6" );

		} catch (AddressException e) {
			DialogHelpers.Messagebox( "One of these email addresses was invalid, msg="  + e.getMessage());
			return false;
		} catch (MessagingException e) {
			DialogHelpers.Messagebox( "Error: MessagingException, msg=" + e.getMessage());
			System.out.println( "MessagingException: msg=" + e.getMessage());
			return false;
		}
		System.out.println( "here7" );

		
		return true;
	}

	
	
	
	
	
	
	
/*
    	try {
    		Properties props = System.getProperties();
    		props.put("mail.smtp.starttls.enable", "true");
    		props.put("mail.smtp.host", "smtp.gmail.com");
    		props.put("mail.smtp.auth", "true");
    		props.put("mail.smtp.port", "465"); // smtp port
    		
    		Authenticator auth = new Authenticator() {
    		@Override
    		protected PasswordAuthentication getPasswordAuthentication() {
    			return new PasswordAuthentication("username-gmail", "password-gmail".toCharArray());
    			}
    		};
    		Session session = Session.getDefaultInstance(props, auth);
    		MimeMessage msg = new MimeMessage(session);
    		msg.setFrom(new InternetAddress("username-gmail@gmail.com"));
    		msg.setSubject("Try attachment gmail");
    		msg.setRecipient(RecipientType.TO, new InternetAddress("username-gmail@gmail.com"));
    		//add atleast simple body
    		MimeBodyPart body = new MimeBodyPart();
    		body.setText("Try attachment");
    		//do attachment
    		MimeBodyPart attachMent = new MimeBodyPart();
    		FileDataSource dataSource = new FileDataSource(new File("file-sent.txt"));
    		attachMent.setDataHandler(new DataHandler(dataSource));
    		attachMent.setFileName("file-sent.txt");
    		attachMent.setDisposition(MimeBodyPart.ATTACHMENT);
    		Multipart multipart = new MimeMultipart();
    		multipart.addBodyPart(body);
    		multipart.addBodyPart(attachMent);
    		msg.setContent(multipart);
    		Transport.send(msg);
    	} catch (AddressException ex) {
    		Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
    	} catch (MessagingException ex) {
    		Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
    	}
    } 
*/


}
