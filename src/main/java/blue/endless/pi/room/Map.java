package blue.endless.pi.room;

public record Map(
		int base,
		int[] doors,
		int[] icons,
		int area,
		int elevator,
		int[] walls
		) {
}
