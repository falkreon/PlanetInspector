package blue.endless.pi.enigma.domain;

import blue.endless.pi.enigma.util.MinimapBaseShape;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class MinimapCell {
	public IntList walls = new IntArrayList(new int[] { 0, 1, 1, 1, 1}); // All solid walls
	public IntList doors = new IntArrayList(new int[] { 0, 0, 0, 0, 0}); // No doors
	public IntSet icons = new IntOpenHashSet();
	public MinimapBaseShape base = MinimapBaseShape.SQUARE;
	public int area = 0;
	public int elevator = 0; // no elevator
}
