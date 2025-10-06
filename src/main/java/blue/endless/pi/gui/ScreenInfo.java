package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Direction;
import blue.endless.pi.DoorType;
import blue.endless.pi.Wall;

public class ScreenInfo {
	private ObjectElement json;
	
	public ScreenInfo(ObjectElement json) {
		this.json = json;
	}
	
	public int x() {
		return json.getPrimitive("x").asInt().orElse(0);
	}
	
	public int y() {
		return json.getPrimitive("y").asInt().orElse(0);
	}
	
	public int area() {
		return json.getObject("MAP").getPrimitive("area").asInt().orElse(0);
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
	
	public void paint(Graphics g, int x, int y, Color areaColor, boolean selected, boolean dragging) {
		Color bg = areaColor;
		if (dragging) {
			bg = Color.GRAY;
		} else if (selected) {
			bg = new Color(64, 128, 255);
		}
		
		// TODO: respect map base
		switch(mapShape()) {
			case SQUARE -> {
				g.setColor(bg);
				g.fillRect(x, y, PlanetView.CELL_SIZE, PlanetView.CELL_SIZE);
				// TODO: Check all walls and doors
				paintSouthWall(g, x, y, selected);
				paintNorthWall(g, x, y, selected);
				paintWestWall(g, x, y, selected);
				paintEastWall(g, x, y, selected);
			}
			case BLANK -> {
				g.setColor(new Color(255, 255, 255, 128));
				g.fillRect(x, y, PlanetView.CELL_SIZE, PlanetView.CELL_SIZE);
			}
			case SLOPE_NE -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x }, new int[] { y, y + PlanetView.CELL_SIZE, y + PlanetView.CELL_SIZE}, 3);
				paintSouthWall(g, x, y, selected);
				paintWestWall(g, x, y, selected);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.drawLine(x, y, x + PlanetView.CELL_SIZE - 1, y + PlanetView.CELL_SIZE - 1);
			}
			case SLOPE_NW -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x + PlanetView.CELL_SIZE }, new int[] { y + PlanetView.CELL_SIZE, y, y + PlanetView.CELL_SIZE}, 3);
				paintSouthWall(g, x, y, selected);
				paintEastWall(g, x, y, selected);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.drawLine(x, y + PlanetView.CELL_SIZE - 1, x + PlanetView.CELL_SIZE - 1, y);
			}
			case SLOPE_SE -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x }, new int[] { y, y, y + PlanetView.CELL_SIZE}, 3);
				paintNorthWall(g, x, y, selected);
				paintWestWall(g, x, y, selected);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.drawLine(x, y + PlanetView.CELL_SIZE - 1, x + PlanetView.CELL_SIZE - 1, y);
			}
			case SLOPE_SW -> {
				g.setColor(bg);
				g.fillPolygon(new int[] { x, x + PlanetView.CELL_SIZE, x + PlanetView.CELL_SIZE }, new int[] { y, y, y + PlanetView.CELL_SIZE}, 3);
				paintNorthWall(g, x, y, selected);
				paintEastWall(g, x, y, selected);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.drawLine(x, y, x + PlanetView.CELL_SIZE - 1, y + PlanetView.CELL_SIZE - 1);
			}
			case TUBE_H -> {
				final int TUBE_NARROW = (PlanetView.CELL_SIZE / 2) + 2;
				final int TUBE_SIZE = PlanetView.CELL_SIZE - TUBE_NARROW;
				final int TUBE_OFFSET = TUBE_NARROW / 2;
				g.setColor(bg);
				g.fillRect(x, y+TUBE_OFFSET, PlanetView.CELL_SIZE, TUBE_SIZE);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.fillRect(x, y+TUBE_OFFSET, PlanetView.CELL_SIZE, 1);
				g.fillRect(x, y+TUBE_OFFSET + TUBE_SIZE - 1, PlanetView.CELL_SIZE, 1);
			}
			case TUBE_V -> {
				final int TUBE_NARROW = (PlanetView.CELL_SIZE / 2) + 2;
				final int TUBE_SIZE = PlanetView.CELL_SIZE - TUBE_NARROW;
				final int TUBE_OFFSET = TUBE_NARROW / 2;
				g.setColor(bg);
				g.fillRect(x+TUBE_OFFSET, y, TUBE_SIZE, PlanetView.CELL_SIZE);
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
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
	
	private void paintSouthWall(Graphics g, int x, int y, boolean selected) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.SOUTH.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y + PlanetView.CELL_SIZE - 1, PlanetView.CELL_SIZE, 1);
			}
			case DOOR, SOLID -> {
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
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
	
	private void paintNorthWall(Graphics g, int x, int y, boolean selected) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.NORTH.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y, PlanetView.CELL_SIZE, 1);
			}
			case DOOR, SOLID -> {
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
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
	
	private void paintWestWall(Graphics g, int x, int y, boolean selected) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.WEST.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x, y, 1, PlanetView.CELL_SIZE);
			}
			case DOOR, SOLID -> {
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
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
	
	private void paintEastWall(Graphics g, int x, int y, boolean selected) {
		int wallKind = json.getObject("MAP").getArray("walls").getPrimitive(Direction.EAST.value()).asInt().orElse(0);
		switch(Wall.valueOf(wallKind)) {
			case OPEN -> {} // Leave it
			case SECRET -> {
				g.setColor(new Color(255, 255, 255, 64));
				g.fillRect(x + PlanetView.CELL_SIZE - 1, y, 1, PlanetView.CELL_SIZE);
			}
			case DOOR, SOLID -> {
				if (selected) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
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
	
	
}