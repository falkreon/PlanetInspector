package blue.endless.pi.enigma.domain;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorldGeneral {
	public Escape escape = new Escape();
	public boolean slim = false;
	public int startingSpawnPointId = 0;
	public int gunshipHints = 1;
	public IntSet gateBosses = new IntOpenHashSet();
	
	public static class Escape {
		public int time = 275; // 4.5 min = 270 sec
		public int halfwayPoint = 203;
		public boolean dynamicMusic = true;
	}
}
