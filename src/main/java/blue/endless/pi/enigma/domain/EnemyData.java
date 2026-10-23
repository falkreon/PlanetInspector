package blue.endless.pi.enigma.domain;

import java.util.ArrayList;

public class EnemyData extends ArrayList<EnemyData.Level> {
	
	
	public static class Level {
		public int hp = 2;
		public int damage = 8;
		public int[][] paletteData = {
			{ 39, 60, 42 }
		};
	}
}
