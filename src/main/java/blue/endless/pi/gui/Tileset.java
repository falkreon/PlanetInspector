package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Optional;

import blue.endless.pi.Assets;
import blue.endless.pi.enigma.wrapper.RoomInfo;

public class Tileset {
	private static BufferedImage tilesetImage;
	private static BufferedImage[] colorSeparated;
	
	public static void init() {
		Optional<BufferedImage> baseTilesetImage = Assets.readImage("tileset_atlas.png");
		if (baseTilesetImage.isEmpty()) {
			System.out.println("Couldn't find asset");
			System.exit(-1);
		}
		
		System.out.println("Asset found at "+baseTilesetImage.get().getWidth()+" x "+baseTilesetImage.get().getHeight());
		
		tilesetImage = new BufferedImage(16 * 16, 16 * 32, BufferedImage.TYPE_INT_ARGB);
		Graphics g = tilesetImage.createGraphics();
		for(int y=0; y<32; y++) {
			for(int x=0; x<16; x++) {
				int tileIndex = y * 16 + x;
				
				// Source atlas is 21 tiles wide, in 20x20 squares, reflecting a 2px mip moat around each tile
				int sourceTileY = (tileIndex / 21) * 20 + 2;
				int sourceTileX = (tileIndex % 21) * 20 + 2;
				
				g.drawImage(baseTilesetImage.get(), x*16, y*16, x*16+16, y*16+16, sourceTileX, sourceTileY, sourceTileX+16, sourceTileY+16, null);
			}
		}
		g.dispose();
		
		colorSeparated = new BufferedImage[3];
		for(int i=0; i<3; i++) {
			colorSeparated[i] = new BufferedImage(tilesetImage.getWidth(), tilesetImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		
		for(int y=0; y<tilesetImage.getHeight(); y++) {
			for(int x=0; x<tilesetImage.getWidth(); x++) {
				int baseColor = tilesetImage.getRGB(x, y);
				int red = (baseColor >> 16) & 0xFF;
				int green = (baseColor >> 8) & 0xFF;
				if (red != green || red >= 0xe0) {
					//Typically 0xe1
					colorSeparated[2].setRGB(x, y, 0xFF_FFFFFF);
				} else if (red > 0x90) {
					//Typically 0x96
					colorSeparated[1].setRGB(x, y, 0xFF_FFFFFF);
				} else if (red > 0x40) {
					//Typically 0x4b
					colorSeparated[0].setRGB(x, y, 0xFF_FFFFFF);
				} // Otherwise, the pixel will be transparent.
			}
		}
		
	}
	
	
	
	public static void paintTile(Graphics g, int x, int y, int tileImage) {
		int tx = tileImage % 16;
		int ty = tileImage / 16;
		g.drawImage(tilesetImage, x, y, x+16, y+16, tx*16, ty*16, tx*16+16, ty*16+16, null);
	}
	
	public static void paintTile(Graphics g, int x, int y, int tile, RoomInfo room) {
		int tileId = tile & 0x1FF;
		
		int palette = (tile >> 12) & 0x1F;
		
		boolean mirror = ((tile >> 28) & 0x1) != 0;
		boolean flip   = ((tile >> 29) & 0x1) != 0;
		boolean rotate = ((tile >> 30) & 0x1) != 0;
		//if (rotate) System.out.println("Rotate! "+(x/16)+","+(y/16));
		
		//int tx = tileId % 16;
		//int ty = tileId / 16;
		
		Color bg = room.getPaletteColor(palette, 0);
		Color mid = room.getPaletteColor(palette, 1);
		Color fg = room.getPaletteColor(palette, 2);
		//g.setColor(color);
		paintTintImage(g, x, y, tilesetImage, tileId, mirror, flip, rotate, bg, mid, fg);
		
		//color = room.getPaletteColor(palette, 1);
		//paintTintImage(g, x, y, 16, 16, tx*16, ty*16, colorSeparated[1], color, mirror, flip, rotate);
		//color = room.getPaletteColor(palette, 2);
		//paintTintImage(g, x, y, 16, 16, tx*16, ty*16, colorSeparated[2], color, mirror, flip, rotate);
	}
	
	
	/*
	private static void paintTintImage(Graphics g, int dx, int dy, int width, int height, int sx, int sy, BufferedImage buf, Color c) {
		g.setColor(c);
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				boolean pixel = buf.getRGB(sx + x, sy + y) != 0;
				if (pixel) g.fillRect(dx + x, dy + y, 1, 1);
			}
		}
	}*/
	
	private static void paintTintImage(Graphics g, int dx, int dy, BufferedImage buf, int tileId, boolean mirror, boolean flip, boolean rotate, Color bg, Color mid, Color fg) {
		int sx = tileId % 16;
		int sy = tileId / 16;
		
		//g.setColor(c);
		for(int y=0; y<16; y++) {
			for(int x=0; x<16; x++) {
				int ix = x;
				int iy = y;
				
				
				if (mirror) {
					ix = 15 - ix;
				}
				
				if (flip) {
					iy = 15 - iy;
				}
				
				if (rotate) {
					int xp = iy;
					int yp = 15-ix;
					
					ix = xp;
					iy = yp;
				}
				
				int color = buf.getRGB(sx*16 + ix, sy*16 + iy);
				if (color != 0) {
					int r = (color >> 16) & 0xFF;
					Color drawColor = Color.BLACK;
					if (r >= 0xe0) {
						drawColor = fg;
					} else if (r >= 0x96) {
						drawColor = mid;
					} else if (r >= 0x4b) {
						drawColor = bg;
					} else {
						continue;
					}
					g.setColor(drawColor);
					g.fillRect(dx + x, dy + y, 1, 1);
				}
				//boolean pixel = buf.getRGB(sx + x, sy + y) != 0;
				//if (pixel) g.fillRect(dx + x, dy + y, 1, 1);
			}
		}
	}
	
	
}
