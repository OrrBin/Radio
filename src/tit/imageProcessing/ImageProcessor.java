package tit.imageProcessing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import tit.dataManagment.DataManagerException;
import tit.dataManagment.DataManagmentUtilities;
import tit.objects.MediaPanelColors;
import tit.objects.SongPanelColors;

/**
 * 
 *  
 *
 */
public class ImageProcessor 
{
	public static BufferedImage processImage(File imageFile) throws IOException
	{
		BufferedImage image = ImageIO.read(imageFile);
//		BufferedImage processed = ImageUtilities.makeImageBlurred(image);
		BufferedImage processed = image;
		processed = ImageUtilities.makeImagePartlyTransp(processed);

		//TODO
//		//Write transparent image to file
//		try
//		{
//			DataManagmentUtilities.writeImageToFile(processed, imageFile);
//		}
//		catch(DataManagerException e)
//		{
//			e.printStackTrace(); //TODO : Remove
//		}
		return processed;
	}
	
	public static SongPanelColors getSongPanelColors(File imageFile) throws IOException
	{
		BufferedImage image = ImageIO.read(imageFile);

		SongPanelColors spc = null;
		Color c = ImageUtilities.findSecondryColor(image);
		Color bg = ImageUtilities.findMainColor(image);
		spc = new SongPanelColors(c, c, c, bg);
		
		return spc;
	}
	
	public static MediaPanelColors getMediaPanelColors(File imageFile) throws IOException
	{
		BufferedImage image = ImageIO.read(imageFile);

		MediaPanelColors mpc = null;
		Color sec = ImageUtilities.findSecondryColor(image);
		Color main = ImageUtilities.findMainColor(image);
		mpc = new MediaPanelColors(main, sec, sec);
		
		return mpc;
	}
}
