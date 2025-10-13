package blue.endless.pi;

import java.awt.Color;
import java.awt.image.BufferedImage;

public final class Palette {
	private static final Color[] COLORS = {
		new Color(0x34, 0x34, 0x34), new Color(0x25, 0x18, 0x8c), new Color(0x00, 0x00, 0xaa), new Color(0x43, 0x00, 0x9d),
		new Color(0x8f, 0x00, 0x76), new Color(0xa8, 0x00, 0x11), new Color(0xa4, 0x00, 0x00), new Color(0x7d, 0x08, 0x00),
		new Color(0x3f, 0x2c, 0x01), new Color(0x01, 0x44, 0x01), new Color(0x01, 0x50, 0x01), new Color(0x00, 0x3d, 0x14),
		new Color(0x18, 0x3c, 0x5c), Color.BLACK,                 Color.BLACK,                 Color.BLACK,
		
		new Color(0x75, 0x75, 0x75), new Color(0x00, 0x74, 0xed), new Color(0x24, 0x3a, 0xf0), new Color(0x85, 0x00, 0xf2),
		new Color(0xbe, 0x01, 0xbf), new Color(0xe4, 0x00, 0x59), new Color(0xdb, 0x2c, 0x01), new Color(0xca, 0x50, 0x0f),
		new Color(0x87, 0x70, 0x00), new Color(0x01, 0x97, 0x02), new Color(0x00, 0xa9, 0x02), new Color(0x00, 0x93, 0x3b),
		new Color(0x00, 0x85, 0x89), Color.BLACK,                 Color.BLACK,                 Color.BLACK,
		
		new Color(0xbc, 0xbc, 0xbc), new Color(0x3f, 0xbf, 0xff), new Color(0x5e, 0x98, 0xfc), new Color(0xcd, 0x88, 0xfd),
		new Color(0xf5, 0x78, 0xfa), new Color(0xfc, 0x74, 0xb4), new Color(0xfb, 0x74, 0x61), new Color(0xfc, 0x98, 0x38),
		new Color(0xf2, 0xbf, 0x3f), new Color(0x80, 0xd0, 0x0f), new Color(0x4a, 0xdd, 0x47), new Color(0x58, 0xf8, 0x98),
		new Color(0x00, 0xe8, 0xda), Color.BLACK,                 Color.BLACK,                 Color.BLACK,
		
		new Color(0xff_ffffff), new Color(0xff_aae3ff), new Color(0xff_c5d5f9), new Color(0xff_d3c9fb),
		new Color(0xff_fec7dc), new Color(0xff_fac5d9), new Color(0xff_fbbeac), new Color(0xff_fad8aa),
		new Color(0xff_fce3a1), new Color(0xff_dffc9f), new Color(0xff_a8f1bc), new Color(0xff_b2fbcd),
		new Color(0xff_9cfcf0), Color.BLACK,         Color.BLACK,         Color.BLACK,
		
		
		new Color(0, 0, 0), new Color(0, 0, 0, 0)
	};
	
	public static final Color get(int id) {
		if (id < COLORS.length) {
			
			return COLORS[id];
		}
		System.out.println("Invalid ID: "+id);
		return Color.BLACK;
	}
	
	/**
	 * Colorizes this image in-place. This will ONLY use thresholds on the red value to decide on the color!
	 * @param image The image to colorize. Note: The image will be modified!
	 * @param palette The palette entry to use for colors.
	 */
	public static final void colorize(BufferedImage image, int[] palette) {
		for(int y=0; y<image.getHeight(); y++) {
			for(int x=0; x<image.getWidth(); x++) {
				int paletteIndex = palette[0];
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				if (r >= 0xe0) {
					paletteIndex = palette[2];
				} else if (r >= 0x96) {
					paletteIndex = palette[1];
				} else if (r >= 0x4b) {
					paletteIndex = palette[0];
				} else {
					paletteIndex = 65;
				}
				
				image.setRGB(x, y, COLORS[paletteIndex].getRGB());
			}
		}
	}
}
