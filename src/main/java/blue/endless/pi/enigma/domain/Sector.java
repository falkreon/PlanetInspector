package blue.endless.pi.enigma.domain;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class Sector {
	public String name = "";
	public int x = 0;
	public int y = 0;
	public int width = 80;
	public int height = 70;
	
	public List<MapLocation> elevatorScreens = new ArrayList<>();
	public IntSet connections = new IntOpenHashSet(new int[] { 1 });
}
