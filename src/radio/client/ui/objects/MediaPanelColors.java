package radio.client.ui.objects;

import java.awt.Color;

import radio.client.ui.UIConfig;

public class MediaPanelColors
{
	private Color backgroundColor;
	private Color labelsColors;
	private Color progressBarColor;

	private Color waveColor1;
	private Color waveColor2;
	private Color waveColor3;

	public MediaPanelColors(Color backgroundColor, Color labelsColors, Color progressBarColor ) 
	{
		this.backgroundColor = backgroundColor;
		this.labelsColors = labelsColors;
		this.progressBarColor = progressBarColor;

		waveColor1 = labelsColors;
		
		int sum = waveColor1.getRed() + waveColor1.getGreen() + waveColor1.getBlue();
		
		if(sum < UIConfig.LIGHTEN_COLORS_SUM)
		{
		waveColor2 = new Color((int)Math.min(labelsColors.getRed() + UIConfig.LIGHTEN_1_BY * 255, 255) , 
				(int)Math.min(labelsColors.getGreen() + UIConfig.LIGHTEN_1_BY * 255, 255) ,
				(int)Math.min(labelsColors.getBlue() + UIConfig.LIGHTEN_1_BY * 255, 255));
		
		waveColor3 = new Color((int)Math.min(labelsColors.getRed() + UIConfig.LIGHTEN_2_BY * 255, 255) , 
				(int)Math.min(labelsColors.getGreen() + UIConfig.LIGHTEN_2_BY * 255, 255) ,
				(int)Math.min(labelsColors.getBlue() + UIConfig.LIGHTEN_2_BY * 255, 255));
		}
		
		else if (sum > UIConfig.LIGHTEN_COLORS_SUM && sum < UIConfig.LIGHTEN_AND_DARKEN_COLORS_SUM)
		{
			waveColor2 = new Color((int)Math.max(labelsColors.getRed() - UIConfig.DARKEN_2_BY * 255, 0) , 
					(int)Math.max(labelsColors.getGreen() - UIConfig.DARKEN_2_BY * 255, 0) ,
					(int)Math.max(labelsColors.getBlue() - UIConfig.DARKEN_2_BY * 255, 0));
			
			waveColor3 = new Color((int)Math.min(labelsColors.getRed() + UIConfig.LIGHTEN_2_BY * 255, 255) , 
					(int)Math.min(labelsColors.getGreen() + UIConfig.LIGHTEN_2_BY * 255, 255) ,
					(int)Math.min(labelsColors.getBlue() + UIConfig.LIGHTEN_2_BY * 255, 255));
		}
		
		else 
		{
			waveColor2 = new Color((int)Math.max(labelsColors.getRed() - UIConfig.DARKEN_1_BY * 255, 0) , 
					(int)Math.max(labelsColors.getGreen() - UIConfig.DARKEN_1_BY * 255, 0) ,
					(int)Math.max(labelsColors.getBlue() - UIConfig.DARKEN_1_BY * 255, 0));
			
			waveColor3 = new Color((int)Math.max(labelsColors.getRed() - UIConfig.DARKEN_2_BY * 255, 0) , 
					(int)Math.max(labelsColors.getGreen() - UIConfig.DARKEN_2_BY * 255, 0) ,
					(int)Math.max(labelsColors.getBlue() - UIConfig.DARKEN_2_BY * 255, 0));
		}
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getWaveColor1() {
		return waveColor1;
	}

	public void setWaveColor1(Color waveColor1) {
		this.waveColor1 = waveColor1;
	}

	public Color getWaveColor2() {
		return waveColor2;
	}

	public void setWaveColor2(Color waveColor2) {
		this.waveColor2 = waveColor2;
	}

	public Color getWaveColor3() {
		return waveColor3;
	}

	public void setWaveColor3(Color waveColor3) {
		this.waveColor3 = waveColor3;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the labelsColors
	 */
	public Color getLabelsColors() {
		return labelsColors;
	}

	/**
	 * @param labelsColors the labelsColors to set
	 */
	public void setLabelsColors(Color labelsColors) {
		this.labelsColors = labelsColors;
	}

	/**
	 * @return the progressBarColor
	 */
	public Color getProgressBarColor() {
		return progressBarColor;
	}

	/**
	 * @param progressBarColor the progressBarColor to set
	 */
	public void setProgressBarColor(Color progressBarColor) {
		this.progressBarColor = progressBarColor;
	}
}
