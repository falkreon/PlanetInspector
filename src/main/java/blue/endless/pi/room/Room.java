package blue.endless.pi.room;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.JFrame;

import blue.endless.jankson.api.annotation.SerializedName;

public record Room(
		@SerializedName("GENERAL")
		General general,
		@SerializedName("HAZARD")
		Hazard hazard,
		@SerializedName("META")
		Meta meta,
		@SerializedName("PATHING")
		ArrayList<PathNode> pathing,
		@SerializedName("PALETTES")
		ArrayList<PaletteEntry> palettes,
		@SerializedName("SCREENS")
		ArrayList<Screen> screens,
		
		@SerializedName("EVENTS")
		ArrayList<Event> events
		) {
	public BufferedImage createImage() {
		// Figure out screensWide and screensHigh
		// Paint each screen into the bufferedImage
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}
	
	public BufferedImage createMapImage() {
		if (screens.size() == 0) return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for(Screen s : screens) {
			minX = Math.min(minX, s.x());
			maxX = Math.max(maxX, s.x());
			minY = Math.min(minY, s.y());
			maxY = Math.max(maxY, s.y());
		}
		
		int screensWide = maxX - minX + 1;
		int screensHigh = maxY - minY + 1;
		
		if (screensWide <= 0 || screensHigh <= 0) return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		System.out.println("Screens: "+screensWide+"x"+screensHigh);
		
		
		
		BufferedImage result = new BufferedImage(screensWide * 6 + 1, screensHigh * 6 + 1, BufferedImage.TYPE_INT_ARGB);
		System.out.println("Created.");
		
		Graphics g = result.createGraphics();
		
		for(Screen s : screens) {
			
			//BufferedImage im = s.createMapImage();
			int dx = (s.x() - minX) * 6;
			int dy = (s.y() - minY) * 6;
			int dx2 = dx + 7;
			int dy2 = dy + 7;
			//g.drawImage(im, dx, dy, dx2, dy2, 0, 0, 7, 7, null);
			
			s.map().paint(g, dx, dy, new ArrayList<>(), new ArrayList<>());
		}
		
		g.dispose();
		
		return result;
	}
}
