package blue.endless.pi.enigma.domain;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Screen {
	public int x = 0;
	public int y = 0;
	public boolean boss = false; // Not to be confused with ROOMS[*].STATS.boss or ROOMS[*].GENERAL.miniboss
	
	@SerializedName("BLOCKS")
	public ArrayElement blocks = new ArrayElement();
	@SerializedName("ELEVATORS")
	public ArrayElement elevators = new ArrayElement();
	@SerializedName("DOORS")
	public List<Door> doors = new ArrayList<>();
	@SerializedName("SCROLLS")
	public IntList scrolls = new IntArrayList();
	
	@SerializedName("TILES")
	public ArrayElement tiles = new ArrayElement();
	
	@SerializedName("LIQUIDS")
	public ArrayElement liquids = new ArrayElement();
	@SerializedName("ENEMIES")
	public ArrayElement enemies = new ArrayElement();
	@SerializedName("MAP")
	public ObjectElement map = new ObjectElement();
	@SerializedName("DECOR")
	public ArrayElement decor = new ArrayElement();
	@SerializedName("OBJECTS")
	public ArrayElement objects = new ArrayElement();
	@SerializedName("TUBES")
	public ArrayElement tubes = new ArrayElement();
	
	public static class Door {
		public int id = 1;
		public int x = 0; // px
		public int y = 0; // px
		public int type = 1;
		public int skin = 0;
		public int pos = 2; // Direction
		public boolean clearOnUse = true;
		public int destId = 0;
		public int destRm = 0;
	}
}
