package blue.endless.pi.room;

public record PaletteEntry(
		/**
		 * each inner array is a triplet of light/medium/dark palette entries. Each element of the outer array
		 * is one step in the "palette animation"
		 */
		int[][] f,
		/**
		 * The delay between "frames" of the "palette animation" in this slot
		 */
		int s
		) {
	
}
