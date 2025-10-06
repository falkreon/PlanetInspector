package blue.endless.pi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.datastruct.IntGrid;
import blue.endless.pi.datastruct.Vec2;
import blue.endless.pi.gui.PlanetView;
import blue.endless.pi.room.Block;

public class PlanetsScreen {
	/*
	 * Keys: DECOR, BLOCKS, ELEVATORS, TUBES, OBJECTS, LIQUIDS
	 */
	//private ArrayList<Block> blocks = new ArrayList<>();
	//private ArrayList<Elevator> elevators = new ArrayList<>();
	
	private Vec2 position;
	
	private IntGrid liquid = new IntGrid(20, 15);
	private IntGrid fg = new IntGrid(20, 15);
	private IntGrid mid = new IntGrid(20, 15);
	private IntGrid bg = new IntGrid(20, 15);
	
	private ObjectElement rawData;
	
	
	public PlanetsScreen() {
		rawData = new ObjectElement();
		
		// Unknown-schema keys that are typically empty
		rawData.put("DECOR", new ArrayElement());
		rawData.put("TUBES", new ArrayElement());
		
		rawData.put("BLOCKS", new ArrayElement());
		rawData.put("ELEVATORS", new ArrayElement());
		rawData.put("OBJECTS", new ArrayElement());
		
		ArrayElement tilesArray = new ArrayElement();
		tilesArray.add(new ArrayElement());
		tilesArray.add(new ArrayElement());
		tilesArray.add(new ArrayElement());
		rawData.put("TILES", new ArrayElement());
		
		rawData.put("LIQUIDS", new ArrayElement());
		
		ArrayElement scrolls = new ArrayElement();
		scrolls.add(PrimitiveElement.of(0));
		scrolls.add(PrimitiveElement.of(0));
		scrolls.add(PrimitiveElement.of(0));
		scrolls.add(PrimitiveElement.of(0));
		scrolls.add(PrimitiveElement.of(0));
		rawData.put("SCROLLS", scrolls);
		
		ObjectElement map = new ObjectElement(); // TODO: Convert from map representation instead! 
	}
	
	public PlanetsScreen(ValueElement value) throws SyntaxError {
		if (value instanceof ObjectElement obj) {
			for(ValueElement val : obj.getArray("BLOCKS")) {
				if (val instanceof ObjectElement block) {
					
				}
			}
		} else {
			throw new SyntaxError("Screens must be json objects. Found: "+value.getClass().getSimpleName());
		}
	}
	
	
	public Vec2 position() { return position; }
	
	public IntGrid liquid() { return liquid; }
	public IntGrid fg()  { return fg; }
	public IntGrid mid() { return mid; }
	public IntGrid bg()  { return bg; }
	
	public void setPosition(Vec2 value) {
		this.position = value;
	}
	
}
