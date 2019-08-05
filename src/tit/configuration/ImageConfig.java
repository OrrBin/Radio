package tit.configuration;

public class ImageConfig 
{
	//Alpha
	public final static int minAlpha = 50;
	public final static int maxAlpha = 255;
	
	//Offset for color groups (used originally in ImageUtilities.findRareColor)
	public final static int numberOfColors = 16;
	
	//Minimum difference between most common color to secondary color
	public final static int diff = 64;
	
	//Blur radius
	public final static int blurRadius = 15;
	
}
