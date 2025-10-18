package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.impl.document.DoubleElementImpl;
import blue.endless.jankson.impl.document.LongElementImpl;
import blue.endless.pi.Assets;
import blue.endless.pi.Direction;
import blue.endless.pi.DoorType;
import blue.endless.pi.ItemType;
import blue.endless.pi.Wall;

public class ScreenInfo {
	private ObjectElement json;
	
	private static final int EMPTY_TILE = 0;

	public static final int TILE_WIDTH = 20;
	public static final int TILE_HEIGHT = 15;
	public static final int PIXEL_WIDTH = TILE_WIDTH * 16;
	public static final int PIXEL_HEIGHT = TILE_HEIGHT * 16;
	
	public ScreenInfo(ObjectElement json) {
		this.json = json;
	}
	
	public ObjectElement json() { return this.json; }
	
	public int x() {
		return json.getPrimitive("x").asInt().orElse(0);
	}
	
	public int y() {
		return json.getPrimitive("y").asInt().orElse(0);
	}
	
	public int area() {
		return json.getObject("MAP").getPrimitive("area").asInt().orElse(0);
	}
	
	public int fgTileAt(int x, int y) {
		ArrayElement tiles = json.getArray("TILES").getArray(2);
		return tileAt(tiles, x, y);
	}
	
	public int midTileAt(int x, int y) {
		ArrayElement tiles = json.getArray("TILES").getArray(1);
		int tile = tileAt(tiles, x, y);
		//if (tile > 0x1FF) System.out.println("SUCCESS: "+Integer.toHexString(tile));
		return tile;
		//return tileAt(tiles, x, y);
	}
	
	public int bgTileAt(int x, int y) {
		ArrayElement tiles = json.getArray("TILES").getArray(0);
		return tileAt(tiles, x, y);
	}
	
	public int liquidTileAt(int x, int y) {
		ArrayElement tiles = json.getArray("LIQUIDS");
		return tileAt(tiles, x, y);
	}
	
	public int enemyCount() {
		return json.getArray("ENEMIES").size();
	}
	
	public ObjectElement enemy(int i) {
		return json.getArray("ENEMIES").getObject(i);
	}
	
	public int objectCount() {
		return json.getArray("OBJECTS").size();
	}
	
	public ObjectElement object(int i) {
		return json.getArray("OBJECTS").getObject(i);
	}
	
	private static int tileAt(ArrayElement plane, int x, int y) {
		if (x<0 || y<0) return EMPTY_TILE;
		
		if (x >= plane.size()) return EMPTY_TILE;
		ArrayElement column = plane.getArray(x);
		
		if (y >= column.size()) return EMPTY_TILE;
		PrimitiveElement prim = column.getPrimitive(y);
		
		if (prim instanceof DoubleElementImpl d) {
			// Work around bad data - vigorously cast back to int so we can bit-manipulate
			return (int) d.asDouble().getAsDouble();
		} else if (prim instanceof LongElementImpl l) {
			return (int) l.asLong().getAsLong();
		} else {
			return EMPTY_TILE;
		}
	}
	
	public MinimapBaseShape mapShape() {
		int base = json.getObject("MAP").getPrimitive("base").asInt().orElse(0);
		return MinimapBaseShape.of(base);
	}
	
	public List<ObjectElement> doors() {
		ArrayList<ObjectElement> result = new ArrayList<>();
		ArrayElement arr = json.getArray("DOORS");
		for(ValueElement elem : arr) {
			if (elem instanceof ObjectElement obj) {
				result.add(obj);
			}
		}
		return result;
	}
	
	public void setPosition(int x, int y) {
		json.put("x", PrimitiveElement.of(x));
		json.put("y", PrimitiveElement.of(y));
	}
	
	public void paint(Graphics g, int x, int y, Color areaColor, boolean selected, boolean dragging, boolean valid) {
		Color bg = areaColor;
		if (dragging) {
			bg = Color.GRAY;
		} else if (selected) {
			bg = new Color(64, 128, 255);
		}
		
		Color fg = Color.WHITE;
		if (selected) fg = Color.LIGHT_GRAY;
		if (!valid) fg = Color.RED;
		
		// TODO: respect map base
		switch(mapShape()) {
			case SQUARE -> {
				g.setColor(bg);
				g.fillRect(x, y, PlanetView.CELL_SIZE, PlanetView.CELL_SIZE);
				// TODO: Check all walls and doors
				paintSouthWall(g, x, y, fg);
				paintNorthWall(g, x, y, fg);
				paintWestWall(g, x, y, fg);
				paintEastWall(g, x, y, fg);
			}
			case BLANK -> {
				g.setColor(new Color(255, 255, 255, 128));
				g.fillRect(x, y, PlanetView.CELL_SIZE, PlanetView.CELL_SIZE);
			}
			case SLOPE_NE -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x }, new int[] { y, y + PlanetView.CELL_SIZE, y + PlanetView.CELL_SIZE}, 3);
				paintSouthWall(g, x, y, fg);
				paintWestWall(g, x, y, fg);
				g.setColor(fg);
				g.drawLine(x, y, x + PlanetView.CELL_SIZE - 1, y + PlanetView.CELL_SIZE - 1);
			}
			case SLOPE_NW -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x + PlanetView.CELL_SIZE }, new int[] { y + PlanetView.CELL_SIZE, y, y + PlanetView.CELL_SIZE}, 3);
				paintSouthWall(g, x, y, fg);
				paintEastWall(g, x, y, fg);
				g.setColor(fg);
				g.drawLine(x, y + PlanetView.CELL_SIZE - 1, x + PlanetView.CELL_SIZE - 1, y);
			}
			case SLOPE_SE -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x }, new int[] { y, y, y + PlanetView.CELL_SIZE}, 3);
				paintNorthWall(g, x, y, fg);
				paintWestWall(g, x, y, fg);
				g.setColor(fg);
				g.drawLine(x, y + PlanetView.CELL_SIZE - 1, x + PlanetView.CELL_SIZE - 1, y);
			}
			case SLOPE_SW -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x + PlanetView.CELL_SIZE }, new int[] { y, y, y + PlanetView.CELL_SIZE}, 3);
				paintNorthWall(g, x, y, fg);
				paintEastWall(g, x, y, fg);
				g.setColor(fg);
				g.drawLine(x, y, x + PlanetView.CELL_SIZE - 1, y + PlanetView.CELL_SIZE - 1);
			}
			case TUBE_H -> {
				final int TUBE_NARROW = (PlanetView.CELL_SIZE / 2) + 2;
				final int TUBE_SIZE = PlanetView.CELL_SIZE - TUBE_NARROW;
				final int TUBE_OFFSET = TUBE_NARROW / 2;
				g.setColor(bg);
				g.fillRect(x, y+TUBE_OFFSET, PlanetView.CELL_SIZE, TUBE_SIZE);
				g.setColor(fg);
				g.fillRect(x, y+TUBE_OFFSET, PlanetView.CELL_SIZE, 1);
				g.fillRect(x, y+TUBE_OFFSET + TUBE_SIZE - 1, PlanetView.CELL_SIZE, 1);
			}
			case TUBE_V -> {
				final int TUBE_NARROW = (PlanetView.CELL_SIZE / 2) + 2;
				final int TUBE_SIZE = PlanetView.CELL_SIZE - TUBE_NARROW;
				final int TUBE_OFFSET = TUBE_NARROW / 2;
				g.setColor(bg);
				g.fillRect(x+TUBE_OFFSET, y, TUBE_SIZE, PlanetView.CELL_SIZE);
				g.setColor(fg);
				g.fillRect(x+TUBE_OFFSET, y, 1, PlanetView.CELL_SIZE);
				g.fillRect(x+TUBE_OFFSET + TUBE_SIZE - 1, y, 1, PlanetView.CELL_SIZE);
				
			}
		}
		/*
		for(ObjectElement obj : doors()) {
			Direction d = Direction.valueOf(obj.getPrimitive("pos").asInt().orElse(0));
			DoorType doorType = DoorType.of(obj.getPrimitive("type").asInt().orElse(1));
			g.setColor(doorType.color());
			switch(d) {
				case NORTH -> {
					//g.fillRect(x + PlanetView.DOOR_OFFSET , y, PlanetView.DOOR_SIZE, 2);
				}
				case EAST -> {
					//g.fillRect(x + PlanetView.CELL_SIZE - 2, y + PlanetView.DOOR_OFFSET, 2, PlanetView.DOOR_SIZE);
				}
				case SOUTH -> {
					//g.fillRect(x + PlanetView.DOOR_OFFSET, y + PlanetView.CELL_SIZE - 2, PlanetView.DOOR_SIZE, 2);
				}
				case WEST -> {
					//g.fillRect(x, y + PlanetView.DOOR_OFFSET, 2, PlanetView.DOOR_SIZE);
				}
				case INVALID -> {}
			}
		}*/
	}
	
	private void paintSouthWall(Graphics g, int x, int y, Color fg) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.SOUTH.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y + PlanetView.CELL_SIZE - 1, PlanetView.CELL_SIZE, 1);
			}
			case DOOR, SOLID -> {
				g.setColor(fg);
				g.fillRect(x, y + PlanetView.CELL_SIZE - 1, PlanetView.CELL_SIZE, 1);
			}
			default -> {}
		}
		
		for(ObjectElement obj : doors()) {
			Direction d = Direction.valueOf(obj.getPrimitive("pos").asInt().orElse(0));
			if (d == Direction.SOUTH) {
				DoorType doorType = DoorType.of(obj.getPrimitive("type").asInt().orElse(1));
				g.setColor(doorType.color());
				g.fillRect(x + PlanetView.DOOR_OFFSET, y + PlanetView.CELL_SIZE - 2, PlanetView.DOOR_SIZE, 2);
			}
		}
	}
	
	private void paintNorthWall(Graphics g, int x, int y, Color fg) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.NORTH.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y, PlanetView.CELL_SIZE, 1);
			}
			case DOOR, SOLID -> {
				g.setColor(fg);
				g.fillRect(x, y, PlanetView.CELL_SIZE, 1);
			}
			default -> {}
		}
		
		for(ObjectElement obj : doors()) {
			Direction d = Direction.valueOf(obj.getPrimitive("pos").asInt().orElse(0));
			if (d == Direction.NORTH) {
				DoorType doorType = DoorType.of(obj.getPrimitive("type").asInt().orElse(1));
				g.setColor(doorType.color());
				g.fillRect(x + PlanetView.DOOR_OFFSET, y, PlanetView.DOOR_SIZE, 2);
			}
		}
	}
	
	private void paintWestWall(Graphics g, int x, int y, Color fg) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.WEST.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y, 1, PlanetView.CELL_SIZE);
			}
			case DOOR, SOLID -> {
				g.setColor(fg);
				g.fillRect(x, y, 1, PlanetView.CELL_SIZE);
			}
			default -> {}
		}
		
		for(ObjectElement obj : doors()) {
			Direction d = Direction.valueOf(obj.getPrimitive("pos").asInt().orElse(0));
			if (d == Direction.WEST) {
				DoorType doorType = DoorType.of(obj.getPrimitive("type").asInt().orElse(1));
				g.setColor(doorType.color());
				g.fillRect(x, y + PlanetView.DOOR_OFFSET, 2, PlanetView.DOOR_SIZE);
			}
		}
	}
	
	private void paintEastWall(Graphics g, int x, int y, Color fg) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.EAST.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x + PlanetView.CELL_SIZE - 1, y, 1, PlanetView.CELL_SIZE);
			}
			case DOOR, SOLID -> {
				g.setColor(fg);
				g.fillRect(x + PlanetView.CELL_SIZE - 1, y, 1, PlanetView.CELL_SIZE);
			}
			default -> {}
		}
		
		for(ObjectElement obj : doors()) {
			Direction d = Direction.valueOf(obj.getPrimitive("pos").asInt().orElse(0));
			if (d == Direction.EAST) {
				DoorType doorType = DoorType.of(obj.getPrimitive("type").asInt().orElse(1));
				g.setColor(doorType.color());
				g.fillRect(x + PlanetView.CELL_SIZE - 2, y + PlanetView.DOOR_OFFSET, 2, PlanetView.DOOR_SIZE);
			}
		}
	}

	public BufferedImage createImage(RoomInfo room) {
		BufferedImage image = new BufferedImage(20 * 16, 15 * 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		
		g.setColor(Color.GRAY);
		for(int y=0; y<15; y++) {
			for(int x=0; x<20; x++) {
				int tile = bgTileAt(x, y);
				if (tile != 0) {
					Tileset.paintTile(g, x*16, y*16, tile, room);
				}
				
				tile = midTileAt(x, y);
				if (tile != 0) {
					Tileset.paintTile(g, x*16, y*16, tile, room);
				}
				
				tile = fgTileAt(x, y);
				if (tile != 0) {
					Tileset.paintTile(g, x*16, y*16, tile, room);
				}
			}
		}
		
		for(int i=0; i<enemyCount(); i++) {
			ObjectElement enemyObj = enemy(i);
			int x = enemyObj.getPrimitive("x").asInt().orElse(0);
			int y = enemyObj.getPrimitive("y").asInt().orElse(0);
			g.setColor(Color.RED);
			g.fillRect(x-2, y-2, 5, 5);
		}
		
		
		for(int i=0; i<objectCount(); i++) {
			ObjectElement objectObj = object(i);
			int x = objectObj.getPrimitive("x").asInt().orElse(0);
			int y = objectObj.getPrimitive("y").asInt().orElse(0);
			int type = objectObj.getPrimitive("type").asInt().orElse(0);
			if (type == 0) {
				/*
				Item item = Item.byId(objectObj.getPrimitive("item").asInt().orElse(0));
				Optional<BufferedImage> maybeImage = Assets.getCachedImage("items/"+item.spriteResource()+".png");
				if (maybeImage.isPresent()) {
					BufferedImage im = maybeImage.get();
					g.drawImage(maybeImage.get(), x - (im.getWidth()/2), y - (im.getHeight()/2), null);
				} else {
					g.setColor(Color.RED);
					g.fillRect(x-3, y-3, 7, 7);
					g.setColor(Color.GREEN);
					g.fillRect(x-2, y-2, 5, 5);
				}*/
			} else {
				g.setColor(Color.GREEN);
				g.fillRect(x-2, y-2, 5, 5);
			}
		}
		
		g.dispose();
		return image;
	}
	
	
}