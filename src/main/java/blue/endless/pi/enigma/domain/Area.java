package blue.endless.pi.enigma.domain;

import java.awt.Color;

public class Area {
	public String bgm = "bgm_Ambience";
	public String name = "UNKNOWN";
	public int color = 0x808080;
	
	public Color awtColor() {
		int r = color & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 16) & 0xFF;
		return new Color(r, g, b);
	}
	
	public void setColor(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		color = (b << 16) | (g << 8) | r;
	}
}
