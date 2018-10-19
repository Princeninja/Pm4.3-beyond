package usrlib;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;


public class ImageHelper {
	
	
	// Read Image from file and return BufferedImage
	
	public static java.awt.image.BufferedImage readImage( String filename ){
		
		java.awt.image.BufferedImage bi = null;
		
		// Read image from file
		try {
			bi = ImageIO.read( new URL( "file", "localhost", filename ));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bi;
	}
	
	
	// Read Image from byte[] data and return BufferedImage
	
	public java.awt.image.BufferedImage getImage( byte[] data ) throws Exception 
	{
	    BufferedImage bi = null;
	    
	    ByteArrayInputStream bais = new ByteArrayInputStream(data);
	    bi = ImageIO.read(bais);
	    return bi;
	 }
	 
	 


	public static java.awt.image.BufferedImage scaleToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc ){

		// get source image width and height
		int nHeight = imgSrc.getHeight();
		int nWidth = imgSrc.getWidth();

		// determine scale to maintain aspect ratio (fScale)
		double scaleX = (double)nMaxWidth / (double)nWidth;
		double scaleY = (double)nMaxHeight / (double)nHeight;
		double fScale = Math.min(scaleX, scaleY);
	  
		// Create new (blank) image of required (scaled) size
		BufferedImage si = new BufferedImage( nMaxWidth, nMaxHeight, BufferedImage.TYPE_INT_ARGB );
	
		// Paint scaled version of source image to new image
		Graphics2D graphics2D = si.createGraphics();
		graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		graphics2D.drawImage( imgSrc, 0, 0, (int)(fScale*nWidth), (int)(fScale*nHeight), null );

		// Clean up
		graphics2D.dispose();
		return si;
	}

/*	public static void showImage( org.zkoss.zul.Image zkImage, String imgName ){
		
		java.awt.image.BufferedImage bi = null;
		java.awt.Image si = null;
		
		System.out.println( "calling ImageIO.read");
		try {
			bi = ImageIO.read( new URL( "file", "localhost", imgName ));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		si = scaleToSize( 125, 150, bi );
		
		System.out.println( "calling zkImage.setContent");
		if (zkImage == null) System.out.println( "ptImage == null");
		if ( si != null ) zkImage.setContent( Images.encode(si));
		System.out.println( "returning");

	}
*/

	
	public static java.awt.image.BufferedImage showImage( String fileName, int targetWidth, int targetHeight ){
		
		java.awt.image.BufferedImage bi = null;
		java.awt.image.BufferedImage si = null;
		
		// Read image from file
		bi = ImageHelper.readImage( fileName );
		if ( bi == null ) return null;

		
		// Scale image to target size
		si = scaleToSize( targetWidth, targetHeight, bi );
		
		// return scaled image si
		return si;
	}


}
