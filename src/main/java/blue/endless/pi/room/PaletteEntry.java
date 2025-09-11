package blue.endless.pi.room;

public record PaletteEntry(
		/**
		 * each inner array is a triplet of light/medium/dark palette entries. Each element of the outer array
		 * is one step in the "palette animation"
		 */
		int[][] f,
		/**
		 * The speed of "frames" of the "palette animation" in this slot. More speed is faster.
		 * 
		 * <p>Time between animation "frames" appears to be about 1 / (2s + 1) seconds; so s=1 is a 1/3 second delay.
		 * The default s=3 is about 1/7 of a second, or 143msec
		 */
		int s
		) {
	
}
