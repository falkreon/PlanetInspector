package blue.endless.pi.room;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.pi.Direction;
import blue.endless.pi.Wall;
import blue.endless.pi.datastruct.IntGrid;
import blue.endless.pi.datastruct.Vec2;

public record Screen(
		// Haven't seen enough of the schema yet
		@SerializedName("DECOR")
		ArrayList<Decor> decor,
		@SerializedName("BLOCKS")
		ArrayList<Block> blocks,
		@SerializedName("ELEVATORS")
		ArrayList<Elevator> elevators,
		@SerializedName("TUBES")
		ArrayList<Tube> tubes,
		@SerializedName("OBJECTS")
		ArrayList<ScreenObject> objects,
		@SerializedName("LIQUIDS")
		long[][] liquids,
		
		
		// Seen enough schema, we kind of know what's going on here
		@SerializedName("MAP")
		MinimapCell map,
		@SerializedName("ENEMIES")
		ArrayList<Enemy> enemies,
		@SerializedName("DOORS")
		ArrayList<Door> doors,
		@SerializedName("SCROLLS")
		long[] scrolls,
		
		@SerializedName("TILES")
		long[][][] tiles,
		
		int x,
		int y
		) {
	
	public IntGrid makeBgGrid() {
		return IntGrid.unpackArray(20, 15, tiles[0]);
	}
	
	public IntGrid makeMidGrid() {
		return IntGrid.unpackArray(20, 15, tiles[1]);
	}
	
	public IntGrid makeFgGrid() {
		return IntGrid.unpackArray(20, 15, tiles[2]);
	}
	
	public IntGrid makeLiquidGrid() {
		return IntGrid.unpackArray(20, 15, liquids);
	}
	
	public BufferedImage createMapImage() {
		BufferedImage result = new BufferedImage(7, 7, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		
		g.setColor(new Color(0, 0, 255));
		g.fillRect(0, 0, 7, 7);
		
		g.setColor(new Color(255, 255, 255));
		for(Direction d : Direction.values()) {
			if (d == Direction.INVALID) continue; 
			Wall w = Wall.valueOf(map.walls()[d.value()]);
			Vec2 startLocation = new Vec2(0,0);
			Vec2 drawDirection = new Vec2(0,0);
			//int doorLocation = 0;
			//int doorSize = 0;
			//int wallSize = 0;
			switch(d) {
				case EAST  -> {
					startLocation = new Vec2(6,0);
					drawDirection = new Vec2(0,1);
					//doorLocation = 5;
					//doorSize = 3;
				}
				case NORTH -> {
					startLocation = new Vec2(0,0);
					drawDirection = new Vec2(1,0);
					//doorLocation = 8;
					//doorSize = 4;
				}
				case WEST  -> {
					startLocation = new Vec2(0,0);
					drawDirection = new Vec2(0,1);
					//doorLocation = 5;
					//doorSize = 3;
				}
				case SOUTH -> {
					startLocation = new Vec2(0,6);
					drawDirection = new Vec2(1,0);
					//doorLocation = 5;
					//doorSize = 4;
				}
				default -> {} //throw new IllegalArgumentException("Unexpected value: " + d);
			};
			
			int wallWidth = (drawDirection.x() * 6) + 1;
			int wallHeight = (drawDirection.y() * 6) + 1;
			
			System.out.println("Drawing wall of type "+w+" from "+startLocation+" in dir "+drawDirection+" with width "+wallWidth+" and height "+wallHeight);
			
			if (w != Wall.OPEN) {
				g.setColor(new Color(255,255,255));
				g.fillRect(startLocation.x(), startLocation.y(), wallWidth, wallHeight);
				if (w == Wall.DOOR) {
					startLocation = new Vec2(startLocation.x() + (drawDirection.x() * 2), startLocation.y() + (drawDirection.y() * 2));
					g.setColor(new Color(0, 0, 255));
					g.fillRect(startLocation.x(), startLocation.y(), 1 + drawDirection.x() * 2, 1 + drawDirection.y() * 2);
				}
			}
		}
		
		g.dispose();
		return result;
	}
	
	public BufferedImage createImage() {
		return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
	}
}
