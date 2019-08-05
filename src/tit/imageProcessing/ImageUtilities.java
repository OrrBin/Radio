package tit.imageProcessing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.LensBlurFilter;
import com.jhlabs.image.MotionBlurFilter;

import tit.configuration.ImageConfig;

/**
 * 
 *  
 *
 */
public class ImageUtilities 
{
	public static BufferedImage makeImageTransp(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[][] imageData = getPixelsData(image);
		Color pixelColor = null;
		Color newPixelColor = null;
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				pixelColor = new Color(imageData[col][row]);
				newPixelColor = new Color(pixelColor.getRed(), pixelColor.getGreen(),
						pixelColor.getBlue(), ImageConfig.minAlpha);
				output.setRGB(col, row, newPixelColor.getRGB());
			}
		}

		return output;
	}

	public static BufferedImage makeImagePartlyTransp(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.println("width = " + width + " height = " + height);
		int offset = (int) Math.ceil(Math.max(width/2, height/2) / (double)(ImageConfig.maxAlpha - ImageConfig.minAlpha));
		System.out.println("offset = " + offset);
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[][] imageData = getPixelsData(image);
		Color pixelColor = null;
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				int layer = Math.min(col, row);
				layer = Math.min(layer, width + 1 - col);
				layer = Math.min(layer, height + 1 - row);
				int alpha = ImageConfig.minAlpha + (layer / offset);
				//								System.out.println("col = " + col +" row = " +  row + " Alpha = " + alpha);
				pixelColor = new Color(imageData[col][row]);
				pixelColor = new Color(pixelColor.getRed(), pixelColor.getGreen(),
						pixelColor.getBlue(), alpha);
				output.setRGB(col, row, pixelColor.getRGB());
			}
		}

		return output;
	}
	
	public static BufferedImage makeImageBlurred(BufferedImage image)
	{		
//		int width = image.getWidth();
//		int height = image.getHeight();
//		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		

//		float[] matrix = {
//		        0.111f, 0.111f, 0.111f, 
//		        0.111f, 0.111f, 0.111f, 
//		        0.111f, 0.111f, 0.111f, 
//		    };
//
//		    BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
//			op.filter(image, destImage);
//			blurredImage = destImage;

		
//		int width = image.getWidth();
//		int height = image.getHeight();
//		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		
//		float[] matrix = {
//		        0.111f, 0.111f, 0.111f, 
//		        0.111f, 0.111f, 0.111f, 
//		        0.111f, 0.111f, 0.111f, 
//		    };
//
//		    BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
//			BufferedImage blurred = op.filter(image, output);
		
//		int width = image.getWidth();
//		int height = image.getHeight();
//		System.out.println("width = " + width + " height = " + height);
//		int offset = (int) Math.ceil(Math.max(width/2, height/2) / (double)(ImageConfig.maxAlpha - ImageConfig.minAlpha));
//		System.out.println("offset = " + offset);
//		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		int[][] imageData = getPixelsData(image);
//		Color pixelColor = null;
//		int sum;
//		for(int row = 0; row < height; row++)
//		{
//			for(int col = 0; col < width; col++)
//			{
//				sum = 0;
//				for(int i = 0; i < 3; i++)
//				{
//					for(int j = 0; j < 3; j++)
//					{
//						if(!(i == 1 && j == 1))
//							sum += imageData[col][row];
//							
//					}
//				}
//				
//				pixelColor = new Color(sum/8);
//				output.setRGB(col, row, pixelColor.getRGB());
//			}
//		}
		GaussianFilter gf = new GaussianFilter();
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);	
		gf.setRadius(ImageConfig.blurRadius);
		gf.filter(image, destImage);
		blurredImage = destImage;
		return blurredImage;
	}


	public static int calculateAvgRGB(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int sum = 0;
		int[][] imageData = getPixelsData(image);
		for (int row = 0; row < height; row++)
		{
			for (int col = 0; col < width; col++) 
			{
				sum += imageData[col][row];
			}
		}

		return sum /(width * height);

	}

	public static Color findMainColor(BufferedImage image)
	{
		int colorCellSize = 256/ImageConfig.numberOfColors;
		//Table that contains the frequency of every color range in the picture
		int [][][] freqTable = new int[ImageConfig.numberOfColors][ImageConfig.numberOfColors][ImageConfig.numberOfColors];

		int width = image.getWidth();
		int height = image.getHeight();
		Color pixelColor = null;

		int[][] imageData = getPixelsData(image);

		int red, green, blue;
		for (int row = 0; row < height; row++)
		{
			for (int col = 0; col < width; col++) 
			{
				pixelColor = new Color(imageData[col][row]);
				red = pixelColor.getRed();
				green = pixelColor.getGreen();
				blue = pixelColor.getBlue();
				//Add one to the specific color counter
				freqTable[red/colorCellSize][green/colorCellSize][blue/colorCellSize]++;
			}
		}

		//Holds the cell with the biggest number
		int [] cell = new int[3];
		int max = 0;
		for (int r = 0; r < ImageConfig.numberOfColors; r++)
		{
			for (int g = 0; g < ImageConfig.numberOfColors; g++) 
			{
				for (int b = 0; b < ImageConfig.numberOfColors; b++)
				{
					if(freqTable[r][g][b] > max)
					{
						max = freqTable[r][g][b];
						cell = new int[]{r,g,b};
					}
				}
			}
		}
		red = cell[0] * colorCellSize + (colorCellSize / 2);
		green = cell[1] * colorCellSize + (colorCellSize / 2);
		blue = cell[2] * colorCellSize + (colorCellSize / 2);
		Color c = new Color(red,green,blue);
		//		UIObjectsColors lc = new UIObjectsColors(c,c,c,c);

		return c;

	}

	public static Color findSecondryColor(BufferedImage image)
	{
		int colorCellSize = 256/ImageConfig.numberOfColors;
		//Table that contains the frequency of every color range in the picture
		int [][][] freqTable = new int[ImageConfig.numberOfColors][ImageConfig.numberOfColors][ImageConfig.numberOfColors];

		int width = image.getWidth();
		int height = image.getHeight();
		Color pixelColor = null;

		int[][] imageData = getPixelsData(image);

		int red, green, blue;
		for (int row = 0; row < height; row++)
		{
			for (int col = 0; col < width; col++) 
			{
				pixelColor = new Color(imageData[col][row]);
				red = pixelColor.getRed();
				green = pixelColor.getGreen();
				blue = pixelColor.getBlue();
				//Add one to the specific color counter
				freqTable[red/colorCellSize][green/colorCellSize][blue/colorCellSize]++;
			}
		}

		//Holds the cell with the biggest number
		int [] cell = new int[3];
		int max = 0;
		for (int r = 0; r < ImageConfig.numberOfColors; r++)
		{
			for (int g = 0; g < ImageConfig.numberOfColors; g++) 
			{
				for (int b = 0; b < ImageConfig.numberOfColors; b++)
				{
					if(freqTable[r][g][b] > max)
					{
						max = freqTable[r][g][b];
						cell = new int[]{r,g,b};
					}
				}
			}
		}
		int secondary = 0;
		int [] newCell = new int[3];
		for (int r = 0; r < ImageConfig.numberOfColors; r++)
		{
			for (int g = 0; g < ImageConfig.numberOfColors; g++) 
			{
				for (int b = 0; b < ImageConfig.numberOfColors; b++)
				{
					int rDiff = Math.abs(r * colorCellSize + (colorCellSize / 2) - cell[0] * colorCellSize + (colorCellSize / 2));
					int gDiff = Math.abs(g * colorCellSize + (colorCellSize / 2) - cell[1] * colorCellSize + (colorCellSize / 2));
					int bDiff = Math.abs(b * colorCellSize + (colorCellSize / 2) - cell[2] * colorCellSize + (colorCellSize / 2));
					if(freqTable[r][g][b] > secondary && (rDiff > ImageConfig.diff 
							|| gDiff > ImageConfig.diff ||  bDiff > ImageConfig.diff))
					{
						secondary = freqTable[r][g][b];
						newCell = new int[]{r,g,b};
					}
				}
			}
		}
		
		red = newCell[0] * colorCellSize + (colorCellSize / 2);
		green = newCell[1] * colorCellSize + (colorCellSize / 2);
		blue = newCell[2] * colorCellSize + (colorCellSize / 2);
		Color c = new Color(red,green,blue);


		return c;
	}

	private static int[][] getPixelsData(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[width][height];

		for (int row = 0; row < height; row++)
		{
			for (int col = 0; col < width; col++) 
			{
				try
				{
					result[col][row] = image.getRGB(col, row);
				}
				catch(Exception e)
				{
					System.out.println("width = "+ width +   "height = " + height
							+ " col = " + col + "   row = " + row);
					e.printStackTrace();
				}
			}
		}

		return result;
	}

}
