package blue.endless.pi.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;

public class Drawing {
	public static void textWithOutline(Graphics g, int x, int y, String text, Font font, Color fillColor, Color outlineColor, float outlineWidth) {
		BasicStroke outlineStroke = new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		
		if (g instanceof Graphics2D g2) {
			g2.translate(x, y);
			// remember original settings
			Color originalColor = g2.getColor();
			Stroke originalStroke = g2.getStroke();
			
			GlyphVector glyphVector = font.createGlyphVector(g2.getFontRenderContext(), text);
			Shape textShape = glyphVector.getOutline();
			
			g2.setColor(outlineColor);
			g2.setStroke(outlineStroke);
			g2.draw(textShape);
			
			g2.setColor(fillColor);
			g2.fill(textShape);
			
			// reset to original settings after painting
			g2.translate(-x, -y);
			g2.setColor(originalColor);
			g2.setStroke(originalStroke);
		}
	}
}
