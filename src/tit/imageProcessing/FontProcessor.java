package tit.imageProcessing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import tit.configuration.UIConfig;

public class FontProcessor 
{
	private static int ShiftNorth(int p, int distance) {
		return (p - distance);
	}
	private static int ShiftSouth(int p, int distance) {
		return (p + distance);
	}
	private static int ShiftEast(int p, int distance) {
		return (p + distance);
	}
	private static int ShiftWest(int p, int distance) {
		return (p - distance);
	}

	public static void outLine(Graphics g, String str, Color outLineColor, Color stringColor, int x, int y, Font font)
	{
		g.setFont(font);
		g.setColor(outLineColor);
		g.drawString(str, ShiftWest(x, 2), ShiftNorth(y, 2));
		g.drawString(str, ShiftWest(x, 2), ShiftSouth(y, 2));
		g.drawString(str, ShiftEast(x, 2), ShiftNorth(y, 2));
		g.drawString(str, ShiftEast(x, 2), ShiftSouth(y, 2));
		g.setColor(stringColor);
		g.drawString(str, x, y);
	}
}
